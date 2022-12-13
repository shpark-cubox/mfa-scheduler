package cubox.aero.cmfa.scheduler.quartz.jobs;

import cubox.aero.cmfa.scheduler.model.CustomerServDtl;
import cubox.aero.cmfa.scheduler.repository.CalcRepository;
import cubox.aero.cmfa.scheduler.types.CalcStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class CalcJobTest {

    @Autowired
    CalcRepository calcRepository;

    List<CustomerServDtl> lists = new ArrayList<>();

    int year;
    int month;
    int day;

    int numberRandomCycleMonth;
    int numberRandomCycleQuarter;
    int numberRandomCycleYear;

    @BeforeEach
    void setUp() {
        // month: Max 12의 3의 배수 랜덤, day: 당일
        for (int i = 1; i <= 30; ++i) {
            int numberRandom30 = new Random().nextInt(30) + 1;
            String calcCycle;
            if (numberRandom30 <= 10) {
                ++numberRandomCycleMonth;
                calcCycle = "M";
            } else if (numberRandom30 <= 20) {
                ++numberRandomCycleQuarter;
                calcCycle = "Q";
            } else {
                ++numberRandomCycleYear;
                calcCycle = "Y";
            }

            LocalDate date = LocalDate.now();
            year = date.getYear() - 1;
            month = date.getMonth().getValue();
            String month10Under = (month < 10) ? "0" + month : String.valueOf(month);
            day = LocalDate.now().getDayOfMonth();
            CustomerServDtl csd = CustomerServDtl.builder()
                    .idCustomer(i)
                    .idServ(i)
                    .calcCycle(calcCycle)
                    .useYn("Y")
                    .startDate(year + "-" + month10Under + "-" + day)
                    .build();

            lists.add(csd);
        }
    }

    @AfterEach
    void tearDown() {
        lists.clear();
    }

    @Test
    @DisplayName("30개의 객체 중 정산주기(Month)에 해당되는 객체 수")
    void monthCalcCycle() {
        //given
        int numberCycleMonth = 0;
        LocalDate today = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

        //when
        for (CustomerServDtl o : this.lists) {
            CalcStatus calcStatus = CalcStatus.of(o.getCalcCycle());

            LocalDate startDate = LocalDate.parse(o.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            if (startDate.getDayOfMonth() == today.getDayOfMonth() && CalcStatus.MONTH.equals(calcStatus)) {
                ++numberCycleMonth;
            }
        }
        
        //then
        assertEquals(numberRandomCycleMonth, numberCycleMonth);
    }

    @Test
    @DisplayName("30개의 객체 중 정산주기(Quarter)에 해당되는 객체 수")
    void quarterCalcCycle() {
        //given
        int numberCycleQuarter = 0;

        LocalDate today = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
        while (today.getMonth().getValue() % 3 != 0) {
            today = today.plusMonths(1);
        }

        //when
        for (CustomerServDtl o : this.lists) {
            CalcStatus calcStatus = CalcStatus.of(o.getCalcCycle());

            LocalDate startDate = LocalDate.parse(o.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            if (startDate.getDayOfMonth() == today.getDayOfMonth() && CalcStatus.QUARTER.equals(calcStatus)) {
                ++numberCycleQuarter;
            }
        }

        //then
        assertEquals(numberRandomCycleQuarter, numberCycleQuarter);
    }

    @Test
    @DisplayName("30개의 객체 중 정산주기(Year)에 해당되는 객체 수")
    void yearCalcCycle() {
        //given
        int numberCycleYear = 0;

        LocalDate today = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

        //when
        for (CustomerServDtl o : this.lists) {
            CalcStatus calcStatus = CalcStatus.of(o.getCalcCycle());

            LocalDate date = LocalDate.parse(o.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE);
            if (today.isEqual(date.plusMonths(12)) && CalcStatus.YEAR.equals(calcStatus)) {
                ++numberCycleYear;
            }
        }

        //then
        assertEquals(numberRandomCycleYear, numberCycleYear);
    }




    @Test
    @DisplayName("application.yml 파일 변경 적용되는가")
    void abc() throws Exception {
        CustomerServDtl csd = CustomerServDtl.builder().useYn("Y").build();
        List<CustomerServDtl> customerServDtls = this.calcRepository.srvDtlList(csd);

        for (CustomerServDtl customerServDtl : customerServDtls) {
            System.out.println(customerServDtl.getIdCustomer());
        }
    }

    @Test
    @DisplayName("startDate to LocalDate")
    void changeDateTime() {
        String dateTime = "2022-08-28T01:32:40";
        LocalDate daa = LocalDate.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDate today = LocalDate.parse(daa.format(DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println(today.toString());
    }

    @Test
    @DisplayName("isBeforeVSisAfter")
    void beforeVSafter() {
        int 사용기간 = 2;
        String dateTime = "2022-08-28T01:32:40";
        LocalDateTime daa = LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME).plusMonths(사용기간);
        if (daa.isBefore(LocalDateTime.now())) {
            System.out.println("드루와");
        } else {
            System.out.println("나가");
        }
    }
}