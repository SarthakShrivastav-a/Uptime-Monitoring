package com.product.uptime;

import com.product.uptime.dto.MonitorStatusUpdate;
import com.product.uptime.entity.MonitorCheckHistory;
import com.product.uptime.entity.MonitorStatus;
import com.product.uptime.repository.MonitorCheckHistoryRepository;
import com.product.uptime.repository.MonitorStatusRepository;
import com.product.uptime.service.MonitorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UptimeApplicationTests {

	@Test
	void contextLoads() {
	}

		@Mock
		private MonitorStatusRepository monitorStatusRepository;

		@Mock
		private MonitorCheckHistoryRepository monitorCheckHistoryRepository;

		@InjectMocks
		private MonitorService monitorStatusService;

		private MonitorStatus existingMonitor;

		@BeforeEach
		public void setup() {
			existingMonitor = new MonitorStatus();
			existingMonitor.setMonitorId("1");
			existingMonitor.setTotalChecks(5);
			existingMonitor.setUpChecks(4);
			existingMonitor.setCumulativeResponse(500);
			existingMonitor.setStatus("UP");
			existingMonitor.setConsecutiveDowntimeCount(0);
		}

		@Test
		public void testUpdateMonitorStatus_ExistingMonitor_UpToDown() {
			MonitorStatusUpdate update = new MonitorStatusUpdate("1", "DOWN", 100, "Timeout", Instant.now());

			when(monitorStatusRepository.findById("1")).thenReturn(Optional.of(existingMonitor));

			monitorStatusService.updateMonitorStatus(update);

			assertEquals("DOWN", existingMonitor.getStatus());
			assertEquals(6, existingMonitor.getTotalChecks());
			assertEquals(4, existingMonitor.getUpChecks());
			assertEquals(600, existingMonitor.getCumulativeResponse());
			assertEquals(66.67, existingMonitor.getUptimePercentage(), 0.01);
			assertEquals(100.0, existingMonitor.getAverageResponseTime(), 0.01);
			assertEquals(1, existingMonitor.getConsecutiveDowntimeCount());

			verify(monitorCheckHistoryRepository, times(1)).save(any(MonitorCheckHistory.class));
			verify(monitorStatusRepository, times(1)).save(existingMonitor);
		}

		@Test
		public void testUpdateMonitorStatus_ExistingMonitor_DownToUp() {
			existingMonitor.setStatus("DOWN");
			MonitorStatusUpdate update = new MonitorStatusUpdate("1", "UP", 200, null, Instant.now());

			when(monitorStatusRepository.findById("1")).thenReturn(Optional.of(existingMonitor));

			monitorStatusService.updateMonitorStatus(update);

			assertEquals("UP", existingMonitor.getStatus());
			assertEquals(6, existingMonitor.getTotalChecks());
			assertEquals(5, existingMonitor.getUpChecks());
			assertEquals(700, existingMonitor.getCumulativeResponse());
			assertEquals(83.33, existingMonitor.getUptimePercentage(), 0.01);
			assertEquals(116.67, existingMonitor.getAverageResponseTime(), 0.01);
			assertEquals(0, existingMonitor.getConsecutiveDowntimeCount());

			verify(monitorCheckHistoryRepository, never()).save(any());
			verify(monitorStatusRepository, times(1)).save(existingMonitor);
		}

		@Test
		public void testUpdateMonitorStatus_NewMonitor() {
			MonitorStatusUpdate update = new MonitorStatusUpdate("2", "UP", 150, null, Instant.now());

			when(monitorStatusRepository.findById("2")).thenReturn(Optional.empty());

			monitorStatusService.updateMonitorStatus(update);

			verify(monitorStatusRepository, times(1)).save(any(MonitorStatus.class));
		}

		@Test
		public void testUpdateMonitorStatus_ConsecutiveDowntime() {
			existingMonitor.setStatus("DOWN");
			existingMonitor.setConsecutiveDowntimeCount(2);
			MonitorStatusUpdate update = new MonitorStatusUpdate("1", "DOWN", 120, "Server Error", Instant.now());

			when(monitorStatusRepository.findById("1")).thenReturn(Optional.of(existingMonitor));

			monitorStatusService.updateMonitorStatus(update);

			assertEquals("DOWN", existingMonitor.getStatus());
			assertEquals(6, existingMonitor.getTotalChecks());
			assertEquals(4, existingMonitor.getUpChecks());
			assertEquals(620, existingMonitor.getCumulativeResponse());
			assertEquals(66.67, existingMonitor.getUptimePercentage(), 0.01);
			assertEquals(103.33, existingMonitor.getAverageResponseTime(), 0.01);
			assertEquals(3, existingMonitor.getConsecutiveDowntimeCount());

			verify(monitorCheckHistoryRepository, never()).save(any());
			verify(monitorStatusRepository, times(1)).save(existingMonitor);
		}
	}
