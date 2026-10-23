package com.talentbridge.backend.preparation.dto;

import java.util.List;

public class RoundInfoDTO {
    private int roundNumber;
    private String roundTitle;
    private String format; // e.g. "60-90 min Online Assessment", "45 min 1:1 Live Coding"
    private String evaluationFocus;
    private List<String> tipsForSuccess;

    public RoundInfoDTO() {}

    public RoundInfoDTO(int roundNumber, String roundTitle, String format, String evaluationFocus, List<String> tipsForSuccess) {
        this.roundNumber = roundNumber;
        this.roundTitle = roundTitle;
        this.format = format;
        this.evaluationFocus = evaluationFocus;
        this.tipsForSuccess = tipsForSuccess;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public String getRoundTitle() {
        return roundTitle;
    }

    public void setRoundTitle(String roundTitle) {
        this.roundTitle = roundTitle;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getEvaluationFocus() {
        return evaluationFocus;
    }

    public void setEvaluationFocus(String evaluationFocus) {
        this.evaluationFocus = evaluationFocus;
    }

    public List<String> getTipsForSuccess() {
        return tipsForSuccess;
    }

    public void setTipsForSuccess(List<String> tipsForSuccess) {
        this.tipsForSuccess = tipsForSuccess;
    }
}
