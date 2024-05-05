package BersaniChiappiniFraschini.AuthenticationService.ControllerTest;


import BersaniChiappiniFraschini.AuthenticationService.AuthenticationRouter;
import BersaniChiappiniFraschini.AuthenticationService.persistence.PairKeyValue;
import BersaniChiappiniFraschini.AuthenticationService.requestMessage.RequestNewAccount;
import BersaniChiappiniFraschini.AuthenticationService.requestMessage.RequestToken;
import BersaniChiappiniFraschini.AuthenticationService.returnMessage.MessageReturn;
import BersaniChiappiniFraschini.AuthenticationService.returnMessage.ReturnCode;
import BersaniChiappiniFraschini.AuthenticationService.service.AuthenticationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(controllers = AuthenticationRouter.class)
@AutoConfigureMockMvc(addFilters = false)
@ExtendWith(MockitoExtension.class)
public class ControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    private  MessageReturn messageSuccess;
    private  MessageReturn messageFail;
    @BeforeEach
    public void setup(){
        messageSuccess  = new MessageReturn(ReturnCode.SUCCESS.getDefaultMessage(), "OK");
        messageFail  = new MessageReturn(ReturnCode.ALREADY_EXISTS.getDefaultMessage(), "Value already exists");
    }
    @Test
    public void insertNewAccount_Success() throws Exception {
        given(authenticationService.insertNewAccount(anyString(),anyString(), anyString())).willAnswer((invocation) -> messageSuccess);
        RequestNewAccount requestNewAccount = RequestNewAccount.build("nuovo", "email2","pass");

        ResultActions response = mockMvc.perform(post("/api/v1/registerNewAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestNewAccount)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(messageSuccess.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is(messageSuccess.getMessage())))
                .andDo(MockMvcResultHandlers.print());
    }


    @Test
    public void insertNewAccount_Fail() throws Exception {
        given(authenticationService.insertNewAccount(anyString(),anyString(), anyString())).willAnswer((invocation) -> messageFail);
        RequestNewAccount requestNewAccount = RequestNewAccount.build("nuovo", "email2","pass");

        ResultActions response = mockMvc.perform(post("/api/v1/registerNewAccount")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestNewAccount)));

        response.andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code", CoreMatchers.is(messageFail.getCode())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is(messageFail.getMessage())))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void authentication() throws Exception {
        given(authenticationService.authentication(ArgumentMatchers.eq("nuovo"),anyString())).willAnswer((invocation) -> messageSuccess);
        RequestNewAccount requestNewAccount = RequestNewAccount.build("nuovo", "email2","pass");

        ResultActions response = mockMvc.perform(post("/api/v1/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestNewAccount)));

        response.andExpect(MockMvcResultMatchers.status().isAccepted())
                .andDo(MockMvcResultHandlers.print());

        messageFail  = new MessageReturn(ReturnCode.FAILED.getDefaultMessage(), "KO");

        given(authenticationService.authentication(ArgumentMatchers.eq("altro"),anyString())).willAnswer((invocation) -> messageFail);
        RequestNewAccount requestNewAccount2 = RequestNewAccount.build("altro", "email2","pass");

        ResultActions response2 = mockMvc.perform(post("/api/v1/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestNewAccount2)));

        response2.andExpect(MockMvcResultMatchers.status().isAccepted())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void apiTokenCreation() throws Exception {
        messageSuccess  = new MessageReturn(ReturnCode.SUCCESS.getDefaultMessage(), "hashToken");
        given(authenticationService.authentication(ArgumentMatchers.eq("id"),anyString())).willAnswer((invocation) -> messageSuccess);
        RequestToken requestToken = new RequestToken();
        requestToken.setId("id");

        ResultActions response = mockMvc.perform(post("/api/v1/generateAuthToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestToken)));

        response.andExpect(MockMvcResultMatchers.status().isAccepted())
                .andDo(MockMvcResultHandlers.print());

        messageFail = new MessageReturn(ReturnCode.ALREADY_EXISTS.getDefaultMessage(), "token already exists");

        given(authenticationService.authentication(ArgumentMatchers.eq("id"),anyString())).willAnswer((invocation) -> messageFail);

        ResultActions response2 = mockMvc.perform(post("/api/v1/generateAuthToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestToken)));

        response2.andExpect(MockMvcResultMatchers.status().isAccepted())
                .andDo(MockMvcResultHandlers.print());
    }



}
