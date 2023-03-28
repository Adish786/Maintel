package com.about.mantle.definition.action;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.about.globe.core.task.annotation.Task;
import com.about.globe.core.task.annotation.TaskParameter;
import com.about.globe.core.task.annotation.Tasks;

/**
 * Used for components that should be shown only for a set period of time (eg birthday banners, holidays, etc)
 * or that have temporal logic involved in implementation
 */
@Tasks
public class TemporaryComponentsActionTasks {

    /**
     * Decides if the current local date of the server falls between two dates.  Returns true if so, null if not.
     * currentDateOverride is intended to be used only for testing
     */
    @Task(name="isLocalDateBetween")
    public Boolean isLocalDateBetween(@TaskParameter(name = "start") String start,
                                      @TaskParameter(name = "stop") String stop,
                                      @TaskParameter(name = "currentDateOverride") String currentDateOverride) {

        Boolean answer = null;

        LocalDateTime startDate = LocalDateTime.parse(start, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endDate = LocalDateTime.parse(stop, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        LocalDateTime now = isBlank(currentDateOverride) ?
                LocalDateTime.now() :
                LocalDateTime.parse(currentDateOverride, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        answer = (now.isAfter(startDate) && now.isBefore(endDate)) ? true : null;
        return answer;

    }

    /**
     * Returns true if the first date comes after the second date.
     */
    @Task(name = "isDateAfter")
    public Boolean isDateAfter(@TaskParameter(name = "first") String first,
                               @TaskParameter(name = "second") String second) {
        LocalDateTime firstDate = LocalDateTime.parse(first, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime secondDate = LocalDateTime.parse(second, DateTimeFormatter.ISO_LOCAL_DATE_TIME);

        return firstDate.isAfter(secondDate);
    }
    
    /**
     * Calculates the amount of time since a given start date. Returns the amount of time in the temporalUnit provided.
     */
    @Task(name="timeSince")
    public Long timeSince(@TaskParameter(name = "start") String start, @TaskParameter(name = "temporalUnit") ChronoUnit temporalUnit) {

        if (start == null || temporalUnit == null ) return null;

    	LocalDateTime startTime = LocalDateTime.parse(start, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    	LocalDateTime now = LocalDateTime.now();
    	
    	if (now.isBefore(startTime)) throw new UnsupportedOperationException("Only times in the past are supported.");
    	
    	return startTime.until(now, temporalUnit);

    }
}
