package cubox.aero.cmfa.scheduler.quartz.jobs;


import cubox.aero.cmfa.scheduler.config.BeanUtils;
import cubox.aero.cmfa.scheduler.model.Calc;
import cubox.aero.cmfa.scheduler.model.CustomerServDtl;
import cubox.aero.cmfa.scheduler.model.CustomerTransaction;
import cubox.aero.cmfa.scheduler.model.Mail;
import cubox.aero.cmfa.scheduler.repository.CalcRepository;
import cubox.aero.cmfa.scheduler.types.CalcStatus;
import cubox.aero.cmfa.scheduler.utils.MailService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 정산 Job 클래스로 시작 일자를 기준으로 고객이 지정한 정산 주기에 해당하는 경우
 * 트랜잭션 성공 수 * 정책 비용(트랜잭션 1회 비용, 변경 및 추가될 수 있음) = 최종 금액, 사용 기간
 * 위 두 가지 정보를 포함한 객체를 정산테이블에 등록합니다.
 */
@Slf4j
@Component
public class CalcJob implements Job {

    private final CalcRepository calcRepository = (CalcRepository) BeanUtils.getBean("calcRepository");
    private final MailService mailService = (MailService) BeanUtils.getBean("mailService");

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            List<CustomerServDtl> usePolicyList =
                    this.calcRepository.srvDtlList(CustomerServDtl.builder().useYn("Y").build());

            for (CustomerServDtl o : usePolicyList) {
                LocalDate startDateTime = LocalDate.parse(o.getStartDate(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                LocalDate startDate = LocalDate.parse(startDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
                LocalDate today = LocalDate.parse(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE));

                // 정책 생성 당일 무시 & 시작일의 일자를 기준으로 매 달 정산 여부 체크
                if (!startDate.isEqual(today) && startDate.getDayOfMonth() == today.getDayOfMonth()) {
                    int month = getMonthOfCalcCycle(o, startDate, today);
                    Calc calc = getCalcAddedTotalPrice(o, today, month);
                    if (calc != null) this.calcRepository.insertCalc(calc);
                }
            }
        } catch (Exception e) {
            this.mailService.sendEmail(new Mail(e.toString()));
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    private int getMonthOfCalcCycle(CustomerServDtl csd, LocalDate startDate, LocalDate today) {
        int calcCycleValue;
        CalcStatus CALC_TYPE = CalcStatus.of(csd.getCalcCycle());
        switch (CALC_TYPE) {
            case MONTH:
                calcCycleValue = CalcStatus.MONTH.getCalcValue();
                break;
            case QUARTER:
                calcCycleValue = (today.getMonth().getValue() % 3 == 0) ? CalcStatus.QUARTER.getCalcValue() : 0;
                break;
            case YEAR:
                calcCycleValue = (today.getMonth().equals(startDate.plusMonths(12).getMonth())) ?
                        CalcStatus.YEAR.getCalcValue() : 0;
                break;
            default:
                log.error("customer: " + csd.getIdCustomer() + ", serv: " + csd.getIdServ() + " CalcType NULL");
                throw new IllegalStateException("정산 타입의 값이 비어있을 수 없습니다.");
        }
        return calcCycleValue;
    }

    private Calc getCalcAddedTotalPrice(CustomerServDtl csd, LocalDate today, int month) {
        String startDate = today.minusMonths(month).toString();
        String endDate = today.toString();
        CustomerTransaction dto = CustomerTransaction.builder()
                .idCustomer(csd.getIdCustomer())
                .idServ(csd.getIdServ())
                .transactionStartDate(startDate)
                .transactionEndDate(endDate)
                .build();

        int numberSuccesses = this.calcRepository.getSuccessTransaction(dto);
        int priceOfPayType = csd.getPayValue();
        int usePrice = numberSuccesses * priceOfPayType;
        return (usePrice == 0) ? null :
                Calc.builder()
                .idCustomer(csd.getIdCustomer())
                .idServ(csd.getIdServ())
                .usePrice(usePrice)
                .useDate(startDate + " ~ " + endDate)
                .build();
    }
}
