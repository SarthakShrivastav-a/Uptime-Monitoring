package com.product.uptime.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorCondition {
    private String triggerOn; // we can store this in an enum STATUS_NOT, RESPONSE_CONTAINS, TIMEOUT
    private List<Integer> value; // expected http  status codes

    public String getTriggerOn() {
        return triggerOn;
    }

    public void setTriggerOn(String triggerOn) {
        this.triggerOn = triggerOn;
    }

    public List<Integer> getValue() {
        return value;
    }

    public void setValue(List<Integer> value) {
        this.value = value;
    }
}
