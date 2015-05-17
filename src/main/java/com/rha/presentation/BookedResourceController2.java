/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import com.rha.control.ResourcesCalendar;
import com.rha.boundary.BookedResourceFacade;
import com.rha.boundary.DivisionFacade;
import com.rha.entity.BookedResource;
import com.rha.entity.Project;
import com.rha.entity.Step;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
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

    Map<LocalDate, List<LocalDate[]>> periods;
    List<BookingRow> bookingRow;

    @Inject
    BookedResourceFacade bookedResourceFacade;

    @Inject
    DivisionFacade divisionFacade;

    ResourcesCalendar resourcesCalendar = new ResourcesCalendar();

    public void loadBookedResourcesForPeriod() {
        
        LocalDate startDate = LocalDate.of(2014, Month.JANUARY, 1);
        LocalDate endDate = LocalDate.of(2016, Month.JANUARY, 1);

        List<BookedResource> bookedResources = bookedResourceFacade.getBookedResourcesForDivision(
                1, startDate, endDate);

        final Map<Project, List<BookedResource>> resourcesByProject
                = bookedResources.stream().collect(groupingBy(br -> br.getProject()));

        bookingRow = new ArrayList<>();

        resourcesByProject.keySet().stream().forEach(project -> {
            bookingRow.add(new BookingRow(
                    project,
                    resourcesCalendar
                    .setStartDate(startDate)
                    .setEndDate(endDate)
                            .setExistentResources(resourcesByProject.get(project))
                            .setStep(Step.BIWEEK)
                            .getCalendarEntries(),
                    divisionFacade.find(1)));
        });

//        for (int i = 0; i < 20; i++) {
//            bookingRow.add(new BookingRow(
//                    new Project().setName("someName: " + i).setId(-1),
//                    resourcesCalendar
//                    .setStartDate(LocalDate.of(2012, Month.JANUARY, 1))
//                    .setEndDate(LocalDate.of(2015, Month.JANUARY, 1)).setStep(Step.WEEK).getCalendarEntries(),
//                    new Division().setName("java").setId(-1)));
//        }
    }

    public List<BookingRow> getBookingRow() {
//        if (bookingRow == null) {
            loadBookedResourcesForPeriod();
//        }

        return bookingRow;
    }

    public List<LocalDate> getPeriods() {

//        if (periods == null) {
            loadBookedResourcesForPeriod();
            periods = bookingRow.get(0).getResources().stream()
                    .map(br -> new LocalDate[]{br.getStartDate(), br.getEndDate()})
                    .collect(groupingBy(period -> period[0]));
//        }

        return periods.keySet().stream().sorted().collect(toList());
    }

    private LocalDate getDate(BookedResource br) {
        return br.getStartDate();
    }

}
