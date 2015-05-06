package com.rha.control;

import com.rha.entity.Periodical;
import com.rha.entity.Step;
import com.rha.entity.StepPeriod;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author alacambra
 * @param <T>
 */
public class CalendarGenerator<T extends Periodical> {

    LocalDate startDate;
    LocalDate endDate;
    Step step;
    LocalDate last;
    List<T> finalObjects = new ArrayList<>();
    List<T> originalObjects;
    int currentPosition = 0;

    public CalendarGenerator(LocalDate startDate, 
            LocalDate endDate, Step step, 
            List<T> originalObjects, Class<T> clazz) {

        try {
            clazz.getConstructor(Void.class);
        } catch (SecurityException | NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
        
        this.startDate = LocalDate.of(startDate.getYear(), startDate.getMonth(), 1);
        this.endDate = LocalDate.of(endDate.getYear(), endDate.getMonth(), endDate.lengthOfMonth());
        this.step = step;
        last = this.startDate;
        this.originalObjects = originalObjects;

    }

    void initCurrentPosition() {
        if (step == Step.DAY) {
            currentPosition = startDate.getDayOfYear();
        } else if (step == Step.WEEK) {
            WeekFields weekFields = WeekFields.of(Locale.getDefault()); 
            currentPosition = startDate.get(weekFields.weekOfWeekBasedYear());
        } else if (step == Step.MONTH) {
            currentPosition = startDate.getYear();
        }
    }

    public List<T> getFinalObjects() {
        return finalObjects;
    }
    
    public List<StepPeriod> getEntries() {

        List<StepPeriod> periods = new ArrayList<>();

        while (last.compareTo(endDate) <= 0) {
            periods.add(supplyPeriod());
        }

        return periods;
    }

    private StepPeriod supplyPeriod() {

        StepPeriod period = new StepPeriod();
        LocalDate current = last;

        if (step == Step.DAY) {
            last = current;

        } else if (step == Step.WEEK) {
            last = current.plusWeeks(1);

        } else if (step == Step.MONTH) {
            last = current.plusMonths(1).minusDays(1);
        }

        period.setStartLocalDate(current);
        period.setEndLocalDate(last);
        period.setStepType(step);

        last = last.plusDays(1);

        currentPosition++;
        return period;
    }

}
