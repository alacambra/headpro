/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.boundary;

import com.rha.control.PeriodTotalsMerger;
import com.rha.entity.PeriodTotal;
import java.time.LocalDate;
import java.util.List;
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
    
    public List<PeriodTotal> getRemainingResources(LocalDate startDate, LocalDate endDate){
        
        List<PeriodTotal> available = availableResourceFacade.getTotalAvailableResourcesInPeriod(startDate, endDate);
        List<PeriodTotal> booked = bookedResourceFacade.getTotalBookedResourcesByServiceInPeriod(startDate, endDate);
        
        return PeriodTotalsMerger.reduce(available, booked); 
    } 
   
}
