package BersaniChiappiniFraschini.AuthenticationService.ServiceTest;


import BersaniChiappiniFraschini.AuthenticationService.SHA256;
import BersaniChiappiniFraschini.AuthenticationService.persistence.PairKeyValue;
import BersaniChiappiniFraschini.AuthenticationService.persistence.PairKeyValueRepository;
import BersaniChiappiniFraschini.AuthenticationService.returnMessage.MessageReturn;
import BersaniChiappiniFraschini.AuthenticationService.returnMessage.ReturnCode;
import BersaniChiappiniFraschini.AuthenticationService.service.AuthenticationService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceTest {

    @Mock
    private PairKeyValueRepository pairKeyValueRepository;

    @Mock
    private SHA256 sha256;

    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    public void testInsertNewAccount_Success() {
        when(pairKeyValueRepository.save(any())).thenReturn(new PairKeyValue());

        MessageReturn result = authenticationService.insertNewAccount("testUser", "test@example.com", "password123");

        assertEquals("OK", result.getMessage());
        verify(pairKeyValueRepository, times(2)).save(any());
    }

    @Test
    public void testInsertNewAccount_UsernameExists() {
        // Configure the mock to restore an existing user when trying to recover an existing user
        when(pairKeyValueRepository.findPairKeyValueByKey(eq("existingUsername")))
                .thenReturn(Optional.of(new PairKeyValue("existingUsername", "hashedPassword")));

        // Run the authentication service method with an existing username
        MessageReturn result = authenticationService.insertNewAccount("existingUsername", "newEmail@example.com", "password123");


        // Verify that the result is an error message
        assertEquals("Value already exists", result.getMessage());


        // Check that the findPairKeyValueByKey method has been called once (per username)
        verify(pairKeyValueRepository, times(1)).findPairKeyValueByKey(eq("existingUsername"));

        // Verify that the save method has never been called
        verify(pairKeyValueRepository, never()).save(any());
    }

    @Test
    public void testInsertNewAccount_EmailExists() {
        // Configure the mock to return an existing user when trying to retrieve an existing user
        when(pairKeyValueRepository.findPairKeyValueByKey(eq("newUsername")))
                .thenReturn(Optional.empty());
        when(pairKeyValueRepository.findPairKeyValueByKey(eq("existingEmail@example.com")))
                .thenReturn(Optional.of(new PairKeyValue("existingUsername", "hashedPassword")));

        // Run the authentication service method with an existing email
        MessageReturn result = authenticationService.insertNewAccount("newUsername", "existingEmail@example.com", "password123");

        // Verify that the result is an error message
        assertEquals("Value already exists", result.getMessage());

        // Check that the findPairKeyValueByKey method has been called twice (once for username, once for email)
        verify(pairKeyValueRepository, times(2)).findPairKeyValueByKey(anyString());


        // Verify that the save method has never been called
        verify(pairKeyValueRepository, never()).save(any());
    }

    @Test
    public void testInsertNewAccount_AllExists() {

        // Configure the mock to return an existing user when trying to retrieve an existing user
        when(pairKeyValueRepository.findPairKeyValueByKey(eq("existUsername")))
                .thenReturn(Optional.of(new PairKeyValue("existingUsername", "hashedPassword")));

        // Run the authentication service method with an existing email
        MessageReturn result = authenticationService.insertNewAccount("existUsername", "existingEmail@example.com", "password123");

        // Verify that the result is an error message
        assertEquals("Value already exists", result.getMessage());

        // Check that the findPairKeyValueByKey method has been called twice (once for username, once for email)
        verify(pairKeyValueRepository, times(1)).findPairKeyValueByKey(anyString());

        // Verify that the save method has never been called
        verify(pairKeyValueRepository, never()).save(any());
    }

    // controllo login
    @Test
    public void testAuthenticationSuccess() {
        String hashValue = SHA256.hashSHA256("Password");
        when(pairKeyValueRepository.findPairKeyValueByKey(eq("existUsername")))
                .thenReturn(Optional.of(new PairKeyValue("existingUsername", hashValue)));

        MessageReturn result = authenticationService.authentication("existUsername", "Password");

        // Verifica che il risultato sia un messaggio di errore
        assertEquals("OK", result.getMessage());

        // Verifica che il metodo findPairKeyValueByKey sia stato chiamato due volte (una per username, una per email)
        verify(pairKeyValueRepository, times(1)).findPairKeyValueByKey(anyString());
        verify(pairKeyValueRepository, never()).save(any());
    }

    @Test
    public void testAuthenticationFail() {
        String hashValue = SHA256.hashSHA256("Password");
        when(pairKeyValueRepository.findPairKeyValueByKey(eq("existUsername")))
                .thenReturn(Optional.empty());

        MessageReturn result = authenticationService.authentication("existUsername", "Password");

        // Verifica che il risultato sia un messaggio di errore
        assertEquals("KO", result.getMessage());

        // Verifica che il metodo findPairKeyValueByKey sia stato chiamato due volte (una per username, una per email)
        verify(pairKeyValueRepository, times(1)).findPairKeyValueByKey(anyString());
        verify(pairKeyValueRepository, never()).save(any());
    }

    @Test
    public void testApiTokenGenerationSuccess() {
        when(pairKeyValueRepository.findPairKeyValueByKey(eq("id")))
                .thenReturn(Optional.empty());

        MessageReturn result = authenticationService.createAPIAuthToken("id");

        // Verifica che il risultato sia un messaggio di errore
        assertEquals(200, result.getCode());

        // Verifica che il metodo findPairKeyValueByKey sia stato chiamato due volte (una per username, una per email)
        verify(pairKeyValueRepository, times(1)).findPairKeyValueByKey(anyString());
        verify(pairKeyValueRepository, times(1)).save(any());

    }

    @Test
    public void testApiTokenGenerationFail() {
        when(pairKeyValueRepository.findPairKeyValueByKey(eq("id")))
                .thenReturn(Optional.of(new PairKeyValue("id", "hashValue")));

        MessageReturn result = authenticationService.createAPIAuthToken("id");

        // Verifica che il risultato sia un messaggio di errore
        assertEquals("token already exists", result.getMessage());

        // Verifica che il metodo findPairKeyValueByKey sia stato chiamato due volte (una per username, una per email)
        verify(pairKeyValueRepository, times(1)).findPairKeyValueByKey(anyString());
        verify(pairKeyValueRepository, never()).save(any());

    }
}
