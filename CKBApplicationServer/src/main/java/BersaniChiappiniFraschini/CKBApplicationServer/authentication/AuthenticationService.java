package BersaniChiappiniFraschini.CKBApplicationServer.authentication;

import BersaniChiappiniFraschini.CKBApplicationServer.config.JwtService;
import BersaniChiappiniFraschini.CKBApplicationServer.user.AccountType;
import BersaniChiappiniFraschini.CKBApplicationServer.user.User;
import BersaniChiappiniFraschini.CKBApplicationServer.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service that performs authentication actions (Login and Registration)
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final Environment environment;

    // ================================= REGISTRATION =================================
    public ResponseEntity<AuthenticationResponse> register(RegisterRequest request) {
        AccountType account_type;

        //Check if valid account type
        try{
            account_type = AccountType.valueOf(request.getAccount_type());
        }catch (Exception ignored){
            var body = AuthenticationResponse.builder().error_msg("Invalid account type").build();
            return ResponseEntity.badRequest().body(body);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .accountType(account_type)
                .invites(List.of())
                .build();

        // Checks for unique values

        if(repository.existsUserByEmail(user.getEmail())){
            var body = AuthenticationResponse.builder().error_msg("Email already used").build();
            return ResponseEntity.badRequest().body(body);
        }

        if(repository.existsUserByUsername(user.getUsername())){
            var body = AuthenticationResponse.builder().error_msg("Username already taken").build();
            return ResponseEntity.badRequest().body(body);
        }


        try {
            storePasswordInMicroservice(user.getUsername(), user.getEmail(), request.getPassword());
            repository.insert(user);
            String jwt = jwtService.generateJWT(user);
            return ResponseEntity.ok(AuthenticationResponse.builder().token(jwt).build());

        } catch (Exception e){
            return ResponseEntity.badRequest().body(AuthenticationResponse.builder().error_msg(e.getMessage()).build());
        }
    }

    // ================================= LOGIN =================================
    public ResponseEntity<AuthenticationResponse> login(LoginRequest request) {
        String key = request.getEmail_or_username();
        String value = request.getPassword();


        try {
            if (!authenticate(key, value)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(AuthenticationResponse.builder()
                                .error_msg("Login failed")
                                .build());
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AuthenticationResponse.builder()
                            .error_msg(e.getMessage())
                            .build());
        }



        var user = userDetailsService.loadUserByUsername(request.getEmail_or_username());
        String jwt = jwtService.generateJWT(user);
        return ResponseEntity.ok(AuthenticationResponse.builder().token(jwt).build());
    }

    // ==================== GENERATION TOKEN FOR GROUP ===========================
    public String generateToken(String idGroup){
        try {
            HttpResponse<ReturnMessage> response = generateTokenRequest(idGroup);
            if (response != null && response.getBody().code == 200) {
                return response.getBody().message;
            } else {
                return "No Token";
            }
        }catch (Exception e){
            return "No Token";
        }
    }

    private HttpResponse<ReturnMessage> generateTokenRequest(String idGroup) throws Exception {
        return sendPostRequest("/generateAuthToken", new RequestToken(idGroup));
    }

    private void storePasswordInMicroservice(String username, String email, String password) throws Exception {
        sendPostRequest("/registerNewAccount", new StorePasswordRequest(username, email, password));
    }


    private boolean authenticate(String username_or_email, String password) throws Exception {
        HttpResponse<ReturnMessage> response = sendPostRequest("/auth",
                new AuthRequest(username_or_email, password));

        return response.getBody().message.equals("OK");
    }


    // Microservice communication
    private HttpResponse<ReturnMessage> sendPostRequest(String method, Object requestBody) throws Exception {
        String microservice_url = environment.getProperty("auth.microservice.url");
        try {
            Unirest.setObjectMapper(new com.mashape.unirest.http.ObjectMapper() {
                final ObjectMapper mapper = new ObjectMapper();
                @SneakyThrows
                public String writeValue(Object value) {
                    return mapper.writeValueAsString(value);
                }
                @SneakyThrows
                public <T> T readValue(String value, Class<T> valueType) {
                    return mapper.readValue(value, valueType);
                }
            });

            return Unirest.post(microservice_url+method)
                    .header("Content-Type", "application/json")
                    .body(requestBody).asObject(ReturnMessage.class);
        } catch (UnirestException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            // in case of no connection with the authentication server
            throw new Exception("Not able to connect to the authentication server");
        }
    }

    private record ReturnMessage(
            int code,
            String message
    ){}
    private record StorePasswordRequest(
            String username,
            String email,
            String password
    ){}

    private record AuthRequest(
            String key,
            String value
    ){}

    private record RequestToken(
            String id
    ){}
}
