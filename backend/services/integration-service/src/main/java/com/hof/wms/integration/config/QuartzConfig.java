package com.hof.wms.integration.config;

import com.hof.wms.integration.entity.SyncTask;
import com.hof.wms.integration.job.AdCampaignImportJob;
import com.hof.wms.integration.job.PortfolioImportJob;
import com.hof.wms.integration.job.ShopInfoImportJob;
import com.hof.wms.integration.service.SfImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import java.util.List;

/**
 * Quartz调度配置
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class QuartzConfig {

    private final Scheduler scheduler;
    private final SfImportService sfImportService;
    private final ApplicationContext applicationContext;

    @Bean
    public SpringBeanJobFactory autowiringSpringBeanJobFactory() {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        try {
            scheduleImportTasks();
        } catch (Exception e) {
            log.error("初始化Quartz调度任务失败: {}", e.getMessage(), e);
        }
    }

    public void scheduleImportTasks() throws SchedulerException {
        List<SyncTask> tasks = sfImportService.getAllTaskConfigs();
        log.info("读取到 {} 个导入任务配置", tasks.size());

        for (SyncTask task : tasks) {
            if (!Boolean.TRUE.equals(task.getEnabled())) {
                log.info("任务 {} 已禁用，跳过", task.getTaskName());
                continue;
            }

            Class<? extends Job> jobClass = getJobClass(task.getSyncType());
            if (jobClass == null) {
                continue;
            }
            scheduleTask(task);
        }
    }

    public void scheduleTask(SyncTask taskConfig) throws SchedulerException {
        Class<? extends Job> jobClass = getJobClass(taskConfig.getSyncType());
        if (jobClass == null) {
            log.warn("未知的任务类型: {}", taskConfig.getSyncType());
            return;
        }

        String jobName = taskConfig.getTaskName() + "_job";
        String triggerName = taskConfig.getTaskName() + "_trigger";
        String groupName = "import_tasks";

        JobKey jobKey = JobKey.jobKey(jobName, groupName);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            log.info("删除已存在的Job: {}", jobKey);
        }

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, groupName)
                .usingJobData("taskConfigId", taskConfig.getId())
                .usingJobData("taskName", taskConfig.getTaskName())
                .usingJobData("params", taskConfig.getParams() != null ? taskConfig.getParams() : "{}")
                .usingJobData("paramsClass", taskConfig.getParamsClass() != null ? taskConfig.getParamsClass() : "")
                .storeDurably()
                .build();

        CronTrigger trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerName, groupName)
                .withSchedule(CronScheduleBuilder.cronSchedule(taskConfig.getCronExpr())
                        .withMisfireHandlingInstructionFireAndProceed())
                .forJob(jobDetail)
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("已调度任务: {} (类型: {}, Cron: {})", taskConfig.getTaskName(),
                taskConfig.getSyncType(), taskConfig.getCronExpr());
    }

    public void unscheduleTask(String taskName) throws SchedulerException {
        String groupName = "import_tasks";
        JobKey jobKey = JobKey.jobKey(taskName + "_job", groupName);
        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
            log.info("已取消调度任务: {}", jobKey);
        }
    }

    private Class<? extends Job> getJobClass(String syncType) {
        if (syncType == null) return null;
        return switch (syncType) {
            case SyncTask.TYPE_SHOP_INFO -> ShopInfoImportJob.class;
            case SyncTask.TYPE_AD_CAMPAIGN -> AdCampaignImportJob.class;
            case SyncTask.TYPE_PORTFOLIO -> PortfolioImportJob.class;
            default -> null;
        };
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public static class AutowiringSpringBeanJobFactory extends SpringBeanJobFactory {

        private ApplicationContext applicationContext;

        public void setApplicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            Object job = super.createJobInstance(bundle);
            if (applicationContext != null) {
                applicationContext.getAutowireCapableBeanFactory().autowireBean(job);
            }
            return job;
        }
    }
}
