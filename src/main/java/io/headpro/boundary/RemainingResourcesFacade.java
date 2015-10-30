package io.headpro.boundary;

import io.headpro.entity.AvailableResource;
import io.headpro.entity.PeriodWithValue;
import io.headpro.entity.RequiredResource;
import io.headpro.entity.Service;

import javax.inject.Inject;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.*;

/**
 *
 * @author alacambra
 */
public class RemainingResourcesFacade implements Serializable{

    AvailableResourceFacade availableResourceFacade;
    RequiredResourceFacade bookedResourceFacade;

    @Inject
    public RemainingResourcesFacade(AvailableResourceFacade availableResourceFacade, RequiredResourceFacade bookedResourceFacade) {
        this.availableResourceFacade = availableResourceFacade;
        this.bookedResourceFacade = bookedResourceFacade;
    }

    public Map<Service, List<PeriodWithValue>> getWeighedRemainingResourcesByService(LocalDate startDate, LocalDate endDate) {

        List<AvailableResource> available = availableResourceFacade.getAvailableResourcesInPeriod(startDate, endDate);
        List<RequiredResource> booked = bookedResourceFacade.getBookedResourcesInPeriod(startDate, endDate);

        Map<Service, List<PeriodWithValue>> brs = booked.stream().map(br -> {

            br.setBooked((-1 * br.getBooked()));
            return br;
        }).collect(groupingBy(RequiredResource::getService, mapping(r -> (PeriodWithValue) r, toList())));

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

    public Map<Service, Map<LocalDate, Float>> getWeighedRemainingResourcesByServiceAndDate(LocalDate startDate, LocalDate endDate) {

        List<AvailableResource> available = availableResourceFacade.getAvailableResourcesInPeriod(startDate, endDate);
        List<RequiredResource> booked = bookedResourceFacade.getBookedResourcesInPeriod(startDate, endDate).stream()
                .map(br -> {
                    br.setBooked((-1 * br.getBooked()));
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

    private Service getService(PeriodWithValue resource){
        if(resource instanceof AvailableResource){
            return ((AvailableResource)resource).getService();
        }

        if(resource instanceof RequiredResource){
            return ((RequiredResource)resource).getService();
        }

        throw new RuntimeException("Invalid instance");
    }

    public Map<LocalDate, Map<Service, Float>> getWeighedRemainingResourcesByDateAndService(LocalDate startDate, LocalDate endDate) {

        List<AvailableResource> available = availableResourceFacade.getAvailableResourcesInPeriod(startDate, endDate);
        List<RequiredResource> booked = bookedResourceFacade.getBookedResourcesInPeriod(startDate, endDate).stream()
                .map(br -> {
                    br.setBooked((-1 * br.getBooked()));
                    return br;
                }).collect(toList());

        List<PeriodWithValue> resources = new ArrayList<PeriodWithValue>() {
            {
                addAll(available);
                addAll(booked);
            }
        };

        Map<LocalDate, Map<Service, Float>> allResources = resources.stream().collect(
                groupingBy(
                        PeriodWithValue::getStartDate,
                        groupingBy(this::getService,
                                mapping(r -> r.getValue(),
                                        reducing(0f, Float::sum))
                        )
                ));

        return allResources;
    }

}
