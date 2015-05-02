/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.control;

import com.rha.entity.Step;
import com.rha.entity.StepPeriod;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alacambra
 */
public class CalendarGeneratorTest {
    
    public CalendarGeneratorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getEntries method, of class CalendarGenerator.
     */
    @Test
    public void testGetEntriesStepMonth() {
        LocalDate startDate = LocalDate.of(2000, Month.MARCH, 1);
        LocalDate endDate = LocalDate.of(2001, Month.FEBRUARY, 1);
        Step step = Step.MONTH;
        CalendarGenerator instance = new CalendarGenerator(startDate, endDate, step);
        List<StepPeriod> result = instance.getEntries();
//        result.stream().forEach(System.out::println);
        assertEquals(12, result.size());
    }
    
    @Test
    public void testGetEntriesStepDay() {
        LocalDate startDate = LocalDate.of(2000, Month.MARCH, 1);
        LocalDate endDate = LocalDate.of(2001, Month.FEBRUARY, 1);
        Step step = Step.DAY;
        CalendarGenerator instance = new CalendarGenerator(startDate, endDate, step);
        List<StepPeriod> result = instance.getEntries();
//        result.stream().forEach(System.out::println);
        assertEquals(365, result.size());
    }
    
    @Test
    public void testGetEntriesStepWeek() {
        LocalDate startDate = LocalDate.of(2000, Month.MARCH, 1);
        LocalDate endDate = LocalDate.of(2000, Month.MARCH, 30);
        Step step = Step.WEEK;
        CalendarGenerator instance = new CalendarGenerator(startDate, endDate, step);
        List<StepPeriod> result = instance.getEntries();
//        result.stream().forEach(System.out::println);
        assertEquals(4, result.size());
    }
    
}
