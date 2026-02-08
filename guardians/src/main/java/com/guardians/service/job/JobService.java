package com.guardians.service.job;

import com.guardians.dto.job.res.ResJobDto;
import com.guardians.dto.job.res.ResJobListDto;

import java.time.LocalDate;
import java.util.List;

public interface JobService {

    void createJob(String companyName, String title, String description, String location, String employmentType, String careerLevel, String salary, LocalDate deadline, String sourceUrl);

    void updateJob(Long jobId, String title, String description, String salary, LocalDate deadline, Boolean isActive);

    void deleteJob(Long jobId);

    ResJobDto getJobDetail(Long jobId);

    List<ResJobListDto> getJobList();
}
