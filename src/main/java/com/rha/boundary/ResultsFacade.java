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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.*;
import javax.inject.Inject;

/**
 *
 * @author alacambra
 */
public class ResultsFacade {

    AvailableResourceFacade availableResourceFacade;
    BookedResourceFacade bookedResourceFacade;

    @Inject
    public ResultsFacade(AvailableResourceFacade availableResourceFacade, BookedResourceFacade bookedResourceFacade) {
        this.availableResourceFacade = availableResourceFacade;
        this.bookedResourceFacade = bookedResourceFacade;
    }

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

    public Map<Service, List<PeriodWithValue>> getWeighedRemainingResourcesByService(LocalDate startDate, LocalDate endDate) {

        List<AvailableResource> available = availableResourceFacade.getAvailableResourcesInPeriod(startDate, endDate);
        List<BookedResource> booked = bookedResourceFacade.getBookedResourcesInPeriod(startDate, endDate);

        Map<Service, List<PeriodWithValue>> brs = booked.stream().map(br -> {

            int probability = br.getProject().getProbability() / 100;
            probability = 1;
            br.setBooked((-1 * br.getBooked() * probability));
            return br;
        }).collect(groupingBy(BookedResource::getService, mapping(r -> (PeriodWithValue) r, toList())));

        Map<Service, List<PeriodWithValue>> ars = available.stream()
                .collect(groupingBy(AvailableResource::getService, mapping(r -> (PeriodWithValue) r, toList())));

        Map<Service, List<PeriodWithValue>> result = new HashMap<>();

        brs.entrySet().stream().forEach(entry -> result.put(entry.getKey(), entry.getValue()));

        ars.entrySet().stream().forEach(entry -> {
            if (result.containsKey(entry.getKey())) {
                List<PeriodWithValue> value = new ArrayList<>();
                value.addAll(result.get(entry.getKey()));
                value.addAll(entry.getValue());
                result.put(entry.getKey(), value);
            } else {
                result.put(entry.getKey(), entry.getValue());
            }
        });

        return result;
    }
    
    private Service getService(PeriodWithValue resource){
        if(resource instanceof AvailableResource){
            return ((AvailableResource)resource).getService();
        }
        
        if(resource instanceof BookedResource){
            return ((BookedResource)resource).getService();
        }
        
        throw new RuntimeException("Invalid instance");
    }

    public Map<Service, Map<LocalDate, Float>> getWeighedRemainingResourcesByService2(LocalDate startDate, LocalDate endDate) {

        List<AvailableResource> available = availableResourceFacade.getAvailableResourcesInPeriod(startDate, endDate);
        List<BookedResource> booked = bookedResourceFacade.getBookedResourcesInPeriod(startDate, endDate).stream()
                .map(br -> {
                    int probability = br.getProject().getProbability() / 100;
                    probability = 1;
                    br.setBooked((-1 * br.getBooked() * probability));
                    return br;
                }).collect(toList());

        List<PeriodWithValue> resources = new ArrayList<PeriodWithValue>() {
            {
                addAll(available);
                addAll(booked);
            }
        };

        Map<Service, Map<LocalDate, Float>> allResources = resources.stream().collect(
                groupingBy(
                        this::getService,
                        groupingBy(PeriodWithValue::getStartDate,
                                mapping(r -> r.getValue(),
                                        reducing(0f, Float::sum))
                        )
                ));


        return allResources;

    }

}
