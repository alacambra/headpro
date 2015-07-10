/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import com.rha.entity.Project;
import java.time.LocalDate;
import java.time.Month;
import org.hamcrest.core.Is;
import static org.hamcrest.core.Is.is;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author alacambra
 */
public class BookedResourceControllerTest {
    
    BookedResourceController cut;
    
    public BookedResourceControllerTest() {
    }
    
    @Before
    public void setUp() {
        cut = new BookedResourceController();
    }

    @Test
    public void testInit() {
    }
    
    @Test
    public void testProjectIsActive(){
        
        cut.startDate = LocalDate.now();
        cut.endDate = LocalDate.now();
        
        Project project = new Project();
        project.setStartLocalDate(LocalDate.of(2015, Month.JULY, 1));
        project.setEndLocalDate(LocalDate.of(2015, Month.JULY, 24));
        
        assertThat(cut.projectIsActive(project), is(true));
    }

    @Test
    public void testLoadBookedResourcesForPeriod() {
    }

    @Test
    public void testGetBookingRow() {
    }

    @Test
    public void testGetPeriods() {
    }

    @Test
    public void testOnCellEdit() {
    }

    @Test
    public void testGetAreaModel() {
    }

    @Test
    public void testGetTotalBooking() {
    }

    @Test
    public void testGetStartDate() {
    }

    @Test
    public void testSetStartDate() {
    }

    @Test
    public void testGetEndDate() {
    }

    @Test
    public void testSetEndDate() {
    }

    @Test
    public void testDateChanged() {
    }

    @Test
    public void testGetStep() {
    }

    @Test
    public void testSetStep() {
    }

    @Test
    public void testGetSteps() {
    }

    @Test
    public void testGetCurrentService() {
    }

    @Test
    public void testSetCurrentService() {
    }

    @Test
    public void testSomethingToShow() {
    }
    
}
