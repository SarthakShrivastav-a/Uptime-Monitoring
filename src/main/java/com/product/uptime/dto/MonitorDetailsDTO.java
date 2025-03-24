package com.product.uptime.dto;

import com.product.uptime.entity.DomainInfo;
import com.product.uptime.entity.ErrorCondition;
import com.product.uptime.entity.MonitorCheckHistory;
import com.product.uptime.entity.SSLInfo;

import java.time.Instant;
import java.util.List;

public class MonitorDetailsDTO {
    private String monitorId;
    private String userId;
    private String url;
    private ErrorCondition errorCondition;
    private SSLInfo sslInfo;
    private DomainInfo domainInfo;
    private Instant createdAt;

    private String currentStatus;
    private double uptimePercentage;
    private int totalChecks;
    private int downChecks;
    private long cumulativeDowntime;
    private long consecutiveDowntimeCount;
    private long cumulativeResponse;
    private double averageResponseTime;
    private Instant lastChecked;
    private int upChecks;

    private List<MonitorCheckHistory> checkHistory;

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ErrorCondition getErrorCondition() {
        return errorCondition;
    }

    public void setErrorCondition(ErrorCondition errorCondition) {
        this.errorCondition = errorCondition;
    }

    public SSLInfo getSslInfo() {
        return sslInfo;
    }

    public void setSslInfo(SSLInfo sslInfo) {
        this.sslInfo = sslInfo;
    }

    public DomainInfo getDomainInfo() {
        return domainInfo;
    }

    public void setDomainInfo(DomainInfo domainInfo) {
        this.domainInfo = domainInfo;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public double getUptimePercentage() {
        return uptimePercentage;
    }

    public void setUptimePercentage(double uptimePercentage) {
        this.uptimePercentage = uptimePercentage;
    }

    public int getTotalChecks() {
        return totalChecks;
    }

    public void setTotalChecks(int totalChecks) {
        this.totalChecks = totalChecks;
    }

    public int getDownChecks() {
        return downChecks;
    }

    public void setDownChecks(int downChecks) {
        this.downChecks = downChecks;
    }

    public long getCumulativeDowntime() {
        return cumulativeDowntime;
    }

    public void setCumulativeDowntime(long cumulativeDowntime) {
        this.cumulativeDowntime = cumulativeDowntime;
    }

    public long getConsecutiveDowntimeCount() {
        return consecutiveDowntimeCount;
    }

    public void setConsecutiveDowntimeCount(long consecutiveDowntimeCount) {
        this.consecutiveDowntimeCount = consecutiveDowntimeCount;
    }

    public long getCumulativeResponse() {
        return cumulativeResponse;
    }

    public void setCumulativeResponse(long cumulativeResponse) {
        this.cumulativeResponse = cumulativeResponse;
    }

    public double getAverageResponseTime() {
        return averageResponseTime;
    }

    public void setAverageResponseTime(double averageResponseTime) {
        this.averageResponseTime = averageResponseTime;
    }

    public Instant getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(Instant lastChecked) {
        this.lastChecked = lastChecked;
    }

    public int getUpChecks() {
        return upChecks;
    }

    public void setUpChecks(int upChecks) {
        this.upChecks = upChecks;
    }

    public List<MonitorCheckHistory> getCheckHistory() {
        return checkHistory;
    }

    public void setCheckHistory(List<MonitorCheckHistory> checkHistory) {
        this.checkHistory = checkHistory;
    }
}

