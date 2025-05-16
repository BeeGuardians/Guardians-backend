package com.guardians.service.job;

import com.guardians.dto.job.req.ReqCreateJobDto;
import com.guardians.dto.job.req.ReqUpdateJobDto;
import com.guardians.dto.job.res.ResJobDto;
import com.guardians.dto.job.res.ResJobListDto;

import java.util.List;

public interface JobService {

    void createJob(ReqCreateJobDto dto);

    void updateJob(Long jobId, ReqUpdateJobDto dto);

    void deleteJob(Long jobId);

    ResJobDto getJobDetail(Long jobId);

    List<ResJobListDto> getJobList();
}
