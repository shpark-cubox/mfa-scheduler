package cubox.aero.cmfa.scheduler.quartz.jobs;

import cubox.aero.cmfa.scheduler.config.BeanUtils;
import cubox.aero.cmfa.scheduler.model.CustomerServDtl;
import cubox.aero.cmfa.scheduler.model.Mail;
import cubox.aero.cmfa.scheduler.repository.CalcRepository;
import cubox.aero.cmfa.scheduler.utils.MailService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
public class StopPolicyJob implements Job {

    private final CalcRepository calcRepository = (CalcRepository) BeanUtils.getBean("calcRepository");
    private final MailService mailService = (MailService) BeanUtils.getBean("mailService");

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            List<CustomerServDtl> policyList =
                    this.calcRepository.srvDtlList(CustomerServDtl.builder().useYn("Y").build());

            for (CustomerServDtl o : policyList) {
                String startDate = o.getStartDate();
                if (!emptyString(startDate)) {
                    LocalDateTime expiredDateTime =
                            LocalDateTime.parse(startDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                                    .plusMonths(o.getServExp());

                    // 만료일자가 지난 경우
                    if (expiredDateTime.isBefore(LocalDateTime.now())) {
                        o.setUseYn("N");
                        this.calcRepository.updateCstSrvDtl(o);
                    }
                }
            }
        } catch (Exception e) {
            this.mailService.sendEmail(new Mail(e.toString()));
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }

    private boolean emptyString(String s) {
        return (s == null || s.isEmpty() || s.trim().isEmpty());
    }
}