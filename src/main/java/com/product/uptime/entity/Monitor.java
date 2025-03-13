package com.product.uptime.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "monitors")
public class Monitor {
    @Id
    private String id;
    private String userId;
    private String url;
    private ErrorCondition errorCondition;
    private SSLInfo sslInfo;
    private DomainInfo domainInfo;
    private Instant createdAt = Instant.now();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
