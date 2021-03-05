package com.springboot.httpInterface.services.impl;

import com.springboot.httpInterface.dao.JobAndTriggerMapper;
import com.springboot.httpInterface.entity.JobAndTrigger;
import com.springboot.httpInterface.services.IJobAndTriggerService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class JobAndTriggerImpl implements IJobAndTriggerService{

	@Autowired
	private JobAndTriggerMapper jobAndTriggerMapper;

	@Override
	public PageInfo<JobAndTrigger> getJobAndTriggerDetails(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<JobAndTrigger> list = jobAndTriggerMapper.getJobAndTriggerDetails();
		PageInfo<JobAndTrigger> page = new PageInfo<JobAndTrigger>(list);
		return page;
	}

}