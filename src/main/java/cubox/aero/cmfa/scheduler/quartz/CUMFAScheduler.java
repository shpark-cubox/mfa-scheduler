package cubox.aero.cmfa.scheduler.quartz;

import cubox.aero.cmfa.scheduler.quartz.jobs.CalcJob;
import cubox.aero.cmfa.scheduler.quartz.jobs.StopPolicyJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static org.quartz.CronScheduleBuilder.dailyAtHourAndMinute;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class CUMFAScheduler {

    SchedulerFactory schedulerFactory;
    Scheduler scheduler;

    @PostConstruct
    public void scheduleSet() throws Exception {
        try {
            this.schedulerFactory = new StdSchedulerFactory();
            this.scheduler = this.schedulerFactory.getScheduler();
            this.scheduler.start();

            JobDetail calc = newJob(CalcJob.class)
                    .withIdentity("calc", "calcGroup")
                    .build();

            Trigger calcTrigger = newTrigger()
                    .withIdentity("calcTrigger", "calcTriggerGroup")
                    .startNow()
//                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0/30 * * * ?")) // 30분 마다
                    .withSchedule(dailyAtHourAndMinute(1, 10)) // 1시 1분 시작
                    .build();

            JobDetail stopPolicy = newJob(StopPolicyJob.class)
                    .withIdentity("stopPolicy", "stopPolicyJobGroup")
                    .build();

            Trigger stopPolicyTrigger = newTrigger()
                    .withIdentity("stopPolicyTrigger", "stopPolicyTriggerGroup")
                    .withSchedule(dailyAtHourAndMinute(1, 20))
                    .build();

            this.scheduler.scheduleJob(calc, calcTrigger);
            this.scheduler.scheduleJob(stopPolicy, stopPolicyTrigger);
        } catch (Exception e) {
            this.scheduler.shutdown();
            throw new RuntimeException(e);
        }
    }
}