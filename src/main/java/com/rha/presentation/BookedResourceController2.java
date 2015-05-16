/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import com.rha.control.ResourcesCalendar;
import com.rha.boundary.BookedResourceFacade;
import com.rha.entity.BookedResource;
import com.rha.entity.Division;
import com.rha.entity.Project;
import com.rha.entity.Step;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.*;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author alacambra
 */
@SessionScoped
@Named("brc")
public class BookedResourceController2 implements Serializable {

    List<BookedResource> bookedResources;
    Map<LocalDate, List<LocalDate[]>> periods;
    BookingRow bookingRow;

    @Inject
    BookedResourceFacade bookedResourceFacade;

    ResourcesCalendar resourcesCalendar = new ResourcesCalendar();

    public void loadBookedResourcesForPeriod() {

//        if (bookedResources == null) {
//            bookedResourceFacade.getBookedResourcesForDivision(
//                    1, LocalDate.of(2012, Month.JANUARY, 1), LocalDate.now());
//        }
        
        bookedResources = resourcesCalendar
                .setStartDate(LocalDate.of(2012, Month.JANUARY, 1))
                .setEndDate(LocalDate.now()).setStep(Step.MONTH).getCalendarEntries();
        
        bookingRow = new BookingRow(
                new Project().setName("someName").setId(-1), 
                bookedResources, 
                new Division().setName("java").setId(-1));
    }

    public List<BookingRow> getBookingRow() {
        if(bookingRow == null)
            loadBookedResourcesForPeriod();
        
        return Arrays.asList(bookingRow);
    }
    

    public List<LocalDate> getPeriods() {
        
        if(periods == null){
            loadBookedResourcesForPeriod();
            periods = bookedResources.stream()
                .map(br -> new LocalDate[]{br.getStartDate(), br.getEndDate()})
                .collect(groupingBy(period -> period[0]));
        }
        
        return periods.keySet().stream().sorted().collect(toList());
    }
    
    private LocalDate getDate(BookedResource br){
        return br.getStartDate();
    }
    
    
}
