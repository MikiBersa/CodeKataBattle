package BersaniChiappiniFraschini.AuthenticationService.respository;

import BersaniChiappiniFraschini.AuthenticationService.persistence.PairKeyValue;
import BersaniChiappiniFraschini.AuthenticationService.persistence.PairKeyValueRepository;
import lombok.AllArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.NONE)
public class RepositoryTest {

    private final PairKeyValueRepository pairKeyValueRepository;

    @Autowired
    public RepositoryTest(PairKeyValueRepository pairKeyValueRepository) {
        this.pairKeyValueRepository = pairKeyValueRepository;
    }

    @Test
    public void PairKeyValueRepository_Save_ReturnSavedPair(){
        PairKeyValue p = new PairKeyValue("prova", "pass");

        PairKeyValue pp = pairKeyValueRepository.save(p);

        Assertions.assertThat(pp).isNotNull();
        Assertions.assertThat(pp.getId()).isGreaterThan(0);
    }

    @Test
    public void PairKeyValueRepository_GetAll_ReturnMoreThenOnePair() {
        PairKeyValue p1 = new PairKeyValue("prova", "pass");
        PairKeyValue p2 = new PairKeyValue("altro", "pass");

        pairKeyValueRepository.save(p1);
        pairKeyValueRepository.save(p2);

        List<PairKeyValue> pairList = pairKeyValueRepository.findAll();

        Assertions.assertThat(pairList).isNotNull();
        Assertions.assertThat(pairList.size()).isEqualTo(2);
    }

    @Test
    public void PairKeyValueRepository_GetByKey_ReturnOnePair() {
        PairKeyValue p1 = new PairKeyValue("prova", "pass");
        PairKeyValue p2 = new PairKeyValue("altro", "pass");

        pairKeyValueRepository.save(p1);
        pairKeyValueRepository.save(p2);

        Optional<PairKeyValue> pairList = pairKeyValueRepository.findPairKeyValueByKey("prova");

        Assertions.assertThat(pairList).isNotNull();
        Assertions.assertThat(pairList).isNotEmpty();
        Assertions.assertThat(pairList.get()).isNotNull();
    }
}
