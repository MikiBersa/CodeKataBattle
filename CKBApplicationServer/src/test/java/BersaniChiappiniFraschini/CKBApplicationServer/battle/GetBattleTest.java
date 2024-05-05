package BersaniChiappiniFraschini.CKBApplicationServer.battle;

import BersaniChiappiniFraschini.CKBApplicationServer.authentication.AuthenticationService;
import BersaniChiappiniFraschini.CKBApplicationServer.event.EventService;
import BersaniChiappiniFraschini.CKBApplicationServer.genericResponses.PostResponse;
import BersaniChiappiniFraschini.CKBApplicationServer.githubManager.GitHubManagerService;
import BersaniChiappiniFraschini.CKBApplicationServer.group.Group;
import BersaniChiappiniFraschini.CKBApplicationServer.group.GroupMember;
import BersaniChiappiniFraschini.CKBApplicationServer.invite.InviteService;
import BersaniChiappiniFraschini.CKBApplicationServer.notification.NotificationService;
import BersaniChiappiniFraschini.CKBApplicationServer.tournament.Tournament;
import BersaniChiappiniFraschini.CKBApplicationServer.tournament.TournamentManager;
import BersaniChiappiniFraschini.CKBApplicationServer.tournament.TournamentRepository;
import BersaniChiappiniFraschini.CKBApplicationServer.tournament.TournamentService;
import BersaniChiappiniFraschini.CKBApplicationServer.user.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mongodb.assertions.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest(includeFilters = @ComponentScan.Filter(Service.class))
class GetBattleTest {
    @MockBean
    private AuthenticationService authenticationService;
    @MockBean
    private TournamentService tournamentService;
    @MockBean
    private NotificationService notificationService;
    @MockBean
    private UserDetailsService userDetailsService;
    @MockBean
    private GitHubManagerService gitHubManagerService;
    @MockBean
    private InviteService inviteService;
    @MockBean
    private EventService eventService;
    @Autowired
    private TournamentRepository tournamentRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private BattleService battleService;

    @BeforeEach
    void setup(){
        var leader = new GroupMember(User.builder()
                .username("Leader name")
                .build());

        var sender = new GroupMember(User.builder()
                .username("I'm a student")
                .build());

        Tournament testingTournament = Tournament.builder()
                .id("FFFFFF0123451989BBBBBB99")
                .title("Tournament title")
                .educators(List.of(new TournamentManager(User.builder()
                        .username("I'm a manager!")
                        .build())))
                .battles(List.of(Battle.builder()
                        .id("BBBBBB012345678999999999")
                        .title("Battle title")
                        .min_group_size(1)
                        .max_group_size(3)
                        .groups(List.of(Group.builder()
                                .id("ABCDEF0123456789ABBEDD01")
                                .leader(leader)
                                .members(List.of(leader,sender))
                                .build()))
                        .build(),
                        Battle.builder()
                                .id("CCCCCC333333333999999999")
                                .title("Other battle")
                                .min_group_size(1)
                                .max_group_size(1)
                                .build()))
                .build();

        mongoTemplate.remove(new Query(), "tournament");
        mongoTemplate.insert(testingTournament, "tournament");
    }

    @Test
    @WithMockUser(username = "I'm a student", authorities = {"STUDENT"})
    public void shouldFindBattleAsStudent(){
        var response = battleService.getBattleInfo("Tournament title", "Battle title");
        var info = response.getBody();
        assertAll(
                ()->assertEquals(HttpStatus.OK, response.getStatusCode()),
                ()->assertTrue(info instanceof BattleInfo)
        );
    }


    @Test
    @WithMockUser(username = "I'm a manager!", authorities = {"EDUCATOR"})
    public void shouldFindBattleAsEducator(){
        var response = battleService.getBattleInfo("Tournament title", "Battle title");
        var info = response.getBody();
        assertAll(
                ()->assertEquals(HttpStatus.OK, response.getStatusCode()),
                ()->assertTrue(info instanceof BattleInfo)
        );
    }

    @Test
    @WithMockUser(username = "I'm a student", authorities = {"STUDENT"})
    public void shouldNotFindBattle(){
        var response = battleService.getBattleInfo("Tournament title", "Nonexistent battle");
        PostResponse info = (PostResponse) response.getBody();
        assertAll(
                ()->assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()),
                ()-> {
                    assert info != null;
                    assertEquals("Battle not found",info.getError_msg());
                }
        );
    }

}