package com.product.uptime;

import com.product.uptime.entity.ErrorCondition;
import com.product.uptime.entity.Incident;
import com.product.uptime.entity.IncidentUpdate;
import com.product.uptime.entity.StatusPage;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UptimeApplicationTests {

    @Test
    void errorConditionKeepsTriggerAndValues() {
        ErrorCondition condition = new ErrorCondition();
        condition.setTriggerOn("STATUS_NOT");
        condition.setValue(List.of(200, 204));

        assertEquals("STATUS_NOT", condition.getTriggerOn());
        assertEquals(List.of(200, 204), condition.getValue());
    }

    @Test
    void statusPageDefaultsToPublished() {
        StatusPage statusPage = new StatusPage();

        assertTrue(statusPage.isPublished());
        assertTrue(statusPage.getComponents().isEmpty());
    }

    @Test
    void incidentCanRecordTimelineUpdates() {
        Incident incident = new Incident();
        incident.setTitle("API down");
        incident.getUpdates().add(new IncidentUpdate("INVESTIGATING", "Initial report", Instant.now()));

        assertEquals("API down", incident.getTitle());
        assertEquals(1, incident.getUpdates().size());
        assertEquals("INVESTIGATING", incident.getUpdates().get(0).getState());
    }
}
