/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.boundary;

import com.rha.control.PeriodTotalsMerger;
import com.rha.entity.AvailableResource;
import com.rha.entity.BookedResource;
import com.rha.entity.PeriodTotal;
import com.rha.entity.PeriodWithValue;
import com.rha.entity.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;
import javax.inject.Inject;

/**
 *
 * @author alacambra
 */
public class ResultsFacade {

    @Inject
    AvailableResourceFacade availableResourceFacade;

    @Inject
    BookedResourceFacade bookedResourceFacade;

    /**
     * Subtract TotalAvailableResources - TotalBookedResources per period
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public List<PeriodTotal> getNetoRemainingResources(LocalDate startDate, LocalDate endDate) {

        List<PeriodTotal> available = availableResourceFacade.getTotalAvailableResourcesInPeriod(startDate, endDate);
        List<PeriodTotal> booked = bookedResourceFacade.getTotalBookedResourcesByServiceInPeriod(startDate, endDate);

        return PeriodTotalsMerger.reduce(available, booked);
    }

    public Map<LocalDate ,Service> getWeighedRemainingResourcesByService(LocalDate startDate, LocalDate endDate) {

        List<AvailableResource> available = availableResourceFacade.getAvailableResourcesInPeriod(startDate, endDate);
        List<BookedResource> booked = bookedResourceFacade.getBookedResourcesInPeriod(startDate, endDate);

        Map<LocalDate, List<PeriodWithValue>> brs = booked.stream().map(br -> {
            br.setBooked(br.getBooked() * br.getProject().getProbability() / (1 - br.getProject().getAbscence()));
            return br;
        }).collect(groupingBy(BookedResource::getStartDate, mapping(r -> (PeriodWithValue) r, toList())));

        Map<LocalDate, List<PeriodWithValue>> ars = available.stream()
                .collect(groupingBy(AvailableResource::getStartDate, mapping(r -> (PeriodWithValue) r, toList())));
        
        Set<LocalDate> dates = new HashSet<>();
        dates.addAll(brs.keySet());
        dates.addAll(ars.keySet());
        
//        dates.stream().map(date -> {
//            brs.
//        });
        
        return null;
    }

}
