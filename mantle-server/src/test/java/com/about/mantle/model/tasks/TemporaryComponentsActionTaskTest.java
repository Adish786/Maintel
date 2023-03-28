package com.about.mantle.model.tasks;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import org.junit.Before;
import org.junit.Test;

import com.about.mantle.definition.action.TemporaryComponentsActionTasks;

public class TemporaryComponentsActionTaskTest {
	
	private TemporaryComponentsActionTasks task;
	
	@Before
	public void setUp() {
		task = new TemporaryComponentsActionTasks();
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void testTemporaryComponent_timeSince_before() {
		task.timeSince(tomorrow(), ChronoUnit.DAYS);
	}
	
	@Test(expected = DateTimeParseException.class)
	public void testTemporaryComponent_timeSince_DateTimeParseException() {
		long actualTime = task.timeSince("Incorrect DateTime", ChronoUnit.DAYS);
		assertEquals(1, actualTime);
	}
	
	@Test
	public void testTemporaryComponent_timeSince_after_days() {
		long actualTime = task.timeSince(yesterday(), ChronoUnit.DAYS);
		assertEquals(1, actualTime);
	}
	
	@Test
	public void testTemporaryComponent_timeSince_after_hours() {
		long actualTime = task.timeSince(yesterday(), ChronoUnit.HOURS);
		assertEquals(24, actualTime);
	}
	
	@Test
	public void testTemporaryComponent_timeSince_after_minutes() {
		long actualTime = task.timeSince(yesterday(), ChronoUnit.MINUTES);
		assertEquals(1440, actualTime);
	}
	
	private String tomorrow() {
		return LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
	
	private String yesterday() {
		return LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
	}
}
