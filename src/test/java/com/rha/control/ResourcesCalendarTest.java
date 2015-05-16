/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.control;

import com.rha.entity.BookedResource;
import com.rha.entity.Step;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alacambra
 */
public class ResourcesCalendarTest {

    ResourcesCalendar cut;

    @Before
    public void setUp() {
        cut = new ResourcesCalendar();

    }

    @After
    public void tearDown() {
    }
    
    @Test
    public void testDailyCalendarEntriesGeneration(){
        cut.setStartDate(LocalDate.of(2015, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31));
        
        cut.setStep(Step.DAY);
        
        assertThat(cut.getCalendarEntries().size(), Is.is(cut.getStartDate().lengthOfYear()));
        
        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31));
        
        cut.setStep(Step.DAY);
        
        assertThat(cut.getCalendarEntries().size(), Is.is(2 * cut.getStartDate().lengthOfYear()));
        
        setUp();
        
        int expected = IntStream.rangeClosed(2012, 2016).boxed()
                    .map(year -> LocalDate.ofYearDay(year, 1).lengthOfYear())
                    .reduce(Integer::sum).get();
        
        cut.setStartDate(LocalDate.of(2012, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2016, Month.DECEMBER, 31));
        
        cut.setStep(Step.DAY);
        
        assertThat(cut.getCalendarEntries().size(), Is.is(expected));
        
        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2014, Month.JANUARY, 1));
        
        cut.setStep(Step.DAY);
        
        assertThat(cut.getCalendarEntries().size(), Is.is(1));
    }
    
    @Test
    public void testWeeklyCalendarEntriesGeneration(){
        cut.setStartDate(LocalDate.of(2015, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31));
        
        cut.setStep(Step.WEEK);
        
        assertThat(cut.getCalendarEntries().size(), Is.is(53));
        
        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31));
        
        cut.setStep(Step.WEEK);
        
        assertThat(cut.getCalendarEntries().size(), Is.is(2 * 53));
        
        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2014, Month.JANUARY, 1));
        
        cut.setStep(Step.WEEK);
        
        assertThat(cut.getCalendarEntries().size(), Is.is(1));
        
        setUp();
        cut.setStartDate(LocalDate.of(2015, Month.MARCH, 1))
                .setEndDate(LocalDate.of(2015, Month.JUNE, 30));
        
        cut.setStep(Step.WEEK);
        
        assertThat(cut.getCalendarEntries().size(), Is.is(19));
    }
    
    @Test
    public void testMonthlyCalendarEntriesGeneration(){
        cut.setStartDate(LocalDate.of(2015, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31));
        
        cut.setStep(Step.MONTH);
        
        assertThat(cut.getCalendarEntries().size(), Is.is(12));
        
        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31));
        
        cut.setStep(Step.MONTH);
        
        assertThat(cut.getCalendarEntries().size(), Is.is(2 * 12));
        
        setUp();
        cut.setStartDate(LocalDate.of(2014, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2014, Month.JANUARY, 1));
        
        cut.setStep(Step.MONTH);
        
        assertThat(cut.getCalendarEntries().size(), Is.is(1));
        
        setUp();
        cut.setStartDate(LocalDate.of(2015, Month.MARCH, 1))
                .setEndDate(LocalDate.of(2015, Month.JUNE, 30));
        
        cut.setStep(Step.MONTH);
        
        assertThat(cut.getCalendarEntries().size(), Is.is(4));
    }

    @Test
    public void testMergeCalenderEntriesMonth() {

        List<BookedResource> existentResources = new ArrayList<>();

        existentResources.add(new BookedResource()
                .setStartDate(LocalDate.of(2015, Month.JANUARY, 1)));
        
        existentResources.add(new BookedResource()
                .setStartDate(LocalDate.of(2015, Month.JUNE, 1)));

        cut.setStartDate(LocalDate.of(2015, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31))
                .setStep(Step.MONTH)
                .setExistentResources(existentResources);

        List<BookedResource> result = cut.getCalendarEntries();
        assertEquals(12, result.size());
        assertEquals(existentResources.get(0), result.get(0));
        assertEquals(existentResources.get(1), result.get(5));
    }
    
    @Test
    public void testMergeCalendarEntriesHalfYeyar() {

        List<BookedResource> existentResources = new ArrayList<>();

        existentResources.add(new BookedResource()
                .setStartDate(LocalDate.of(2015, Month.JUNE, 1)));

        cut.setStartDate(LocalDate.of(2015, Month.FEBRUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.AUGUST, 31))
                .setStep(Step.MONTH)
                .setExistentResources(existentResources);

        List<BookedResource> result = cut.getCalendarEntries();
        assertEquals(7, result.size());
        assertTrue(result.contains(existentResources.get(0)));
        assertEquals(existentResources.get(0), result.get(4));
    }
    
    @Test
    public void testMergeCalendarEntriesWeek() {

        List<BookedResource> existentResources = new ArrayList<>();

        existentResources.add(new BookedResource()
                .setStartDate(LocalDate.of(2015, Month.FEBRUARY, 1)));
        
        existentResources.add(new BookedResource()
                .setStartDate(LocalDate.of(2015, Month.JUNE, 1)));

        cut.setStartDate(LocalDate.of(2015, Month.JANUARY, 1))
                .setEndDate(LocalDate.of(2015, Month.DECEMBER, 31))
                .setStep(Step.MONTH)
                .setExistentResources(existentResources);

        List<BookedResource> result = cut.getCalendarEntries();
        assertEquals(12, result.size());
        assertEquals(existentResources.get(0), result.get(0));
        assertEquals(existentResources.get(1), result.get(5));
    }
    
    @Test
    public void testWeeksMethods(){
        LocalDate date = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.GERMANY);
        
        System.out.println("results:");
        System.out.println(date.get(weekFields.weekBasedYear()));
        System.out.println(date.get(weekFields.weekOfYear()));
        System.out.println(date.get(weekFields.weekOfWeekBasedYear()));
        System.out.println(date.with(TemporalAdjusters.lastDayOfYear()).get(weekFields.weekOfYear()));
        
    }
}
