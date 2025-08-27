package com.triageflow.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 建议结果类
public class ExamSuggestion {
    private int patientId;
    private int examId;
    private int deviceId;
    private double estimatedWaitTime; // 分钟
    private Date estimatedStartTime;
}
