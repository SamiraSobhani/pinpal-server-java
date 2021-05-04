package com.pinpal.pinpalproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ReCaptchaResponse {
    private Boolean success;
    private Double score;
    private String action;
    @JsonProperty("challenge_ts")
    private Date timestamp;
    private String hostName;
    @JsonProperty("error-codes")
    private List<String> errorCodes;
}
