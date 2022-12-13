package cubox.aero.cmfa.scheduler.quartz.jobs;

import cubox.aero.cmfa.scheduler.model.CustomerServDtl;
import cubox.aero.cmfa.scheduler.repository.CalcRepository;
import cubox.aero.cmfa.scheduler.utils.MailService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class StopPolicyJobTest {

    @Autowired
    CalcRepository calcRepository;

    @Autowired
    MailService mailService;

    int expiredCnt = 0;
    List<CustomerServDtl> lists = new ArrayList<>();

    // 정책 종료일에 해당하는 정산 객체 초기화
    @BeforeEach
    void setUp() {
        // 정책 사용 기간이 만료되어 사용 여부가 'N' 으로 변경될 테스트 데이터 1 ~ 6개 랜덤 생성
        expiredCnt = new Random().nextInt(7) + 1;

        for (int i = 0; i < 10; ++i) {
            // Month 1 ~ 12, 아래 사용된 6, 7은 시작일을 기준으로 만료 6개월을 넘겨 'Y' 가능하도록
            int month = ((i + 1) > expiredCnt) ?
                    new Random().nextInt(12 - 6 + 1) + 6 :
                    new Random().nextInt(5 - 1 + 1) + 1;

            // 현재일자 기준 - 6개월을 시작일자로 지정
            // expiredCnt 수 만큼 사용기간이 지나도록 설정
            String startDate = LocalDateTime.now()
                    .minusMonths(6)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE); //yyyy-mm-dd

            CustomerServDtl csd = CustomerServDtl.builder()
                    .idCustomer(i)
                    .idServ(i)
                    .servExp(month)
                    .useYn("Y")
                    .startDate(startDate)
                    .build();

            lists.add(csd);
        }
    }

    @AfterEach
    void tearDown() {
        expiredCnt = 0;
        lists.clear();
    }

    @Test
    @DisplayName("만료일자(시작일자 + 사용일자)에 대한 사용여부 처리 개수 확인")
    void expiredDateCheck() {
        //given
        int realExpiredCnt = 0;

        //when
        for (CustomerServDtl o : lists) {
            LocalDateTime expiredDateTime = LocalDateTime.parse(o.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .plusMonths(o.getServExp());

            realExpiredCnt = (expiredDateTime.isBefore(LocalDateTime.now())) ? realExpiredCnt + 1 : realExpiredCnt;
        }

        //then
        assertEquals(expiredCnt, realExpiredCnt);
    }
}