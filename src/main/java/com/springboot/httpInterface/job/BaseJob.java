package com.springboot.httpInterface.job;

import org.quartz.*;

@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public interface BaseJob extends Job{
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException;
}

