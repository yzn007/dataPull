package com.springboot.httpInterface.job;

import org.quartz.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public interface BaseJob extends Job{
	public void execute(JobExecutionContext context) throws JobExecutionException;
}

