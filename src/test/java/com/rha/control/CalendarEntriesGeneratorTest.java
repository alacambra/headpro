/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.control;

import com.rha.entity.PeriodTotal;
import com.rha.entity.Step;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alacambra
 */
public class CalendarEntriesGeneratorTest {

    CalendarEntriesGenerator cut;

    public CalendarEntriesGeneratorTest() {
    }

    @Before
    public void setUp() {
        cut = new CalendarEntriesGenerator();
    }

    @Test
    public void testGetWeekCalendarEntries() {
        getWeekCalendarEntries(Step.DAY, 365);
        getWeekCalendarEntries(Step.WEEK, 53);
        getWeekCalendarEntries(Step.BIWEEK, 24);
        getWeekCalendarEntries(Step.MONTH, 12);
    }
    
    private void getWeekCalendarEntries(Step step, int totalPeriods){
         CalendarPeriodsGenerator periodsGenerator = new CalendarPeriodsGenerator()
                .setStartDate(LocalDate.of(2015, 1, 1))
                .setEndDate(LocalDate.of(2015, 12, 31))
                .setStep(step);

        List<PeriodTotal> periodicEntities = new ArrayList<>();
        List<LocalDate[]> periods = periodsGenerator.generatePeriods();
        Supplier<PeriodTotal> supplier = PeriodTotal::new;
        
        periodicEntities.add(new PeriodTotal(
                LocalDateConverter.toDate(periods.get(0)[0]), 
                LocalDateConverter.toDate(periods.get(0)[1]), 
                10L));
        
        periodicEntities.add(new PeriodTotal(
                LocalDateConverter.toDate(periods.get(4)[0]), 
                LocalDateConverter.toDate(periods.get(4)[1]), 
                20L));
        
        periodicEntities.add(new PeriodTotal(
                LocalDateConverter.toDate(periods.get(10)[0]), 
                LocalDateConverter.toDate(periods.get(10)[1]), 
                30L));
        
        periodicEntities.add(new PeriodTotal(
                LocalDateConverter.toDate(periods.get(11)[0]), 
                LocalDateConverter.toDate(periods.get(11)[1]), 
                40L));
        
        
        List<PeriodTotal> totals = cut.getCalendarEntries(periodicEntities, periods, supplier);
        assertThat(totals.size(), Is.is(totalPeriods));
        
        assertThat(totals.get(0).getTotal(), Is.is(10L));
        assertThat(totals.get(4).getTotal(), Is.is(20L));
        assertThat(totals.get(10).getTotal(), Is.is(30L));
        assertThat(totals.get(11).getTotal(), Is.is(40L));
        
        totals.stream()
                .filter(t -> t != totals.get(0))
                .filter(t -> t != totals.get(4))
                .filter(t -> t != totals.get(10))
                .filter(t -> t != totals.get(11))
                .forEach(t -> assertThat(t.getTotal(), Is.is(0L)));
    }

}
