/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.headpro.presentation;

import io.headpro.entity.RequiredResource;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author alacambra
 */
public class RepresentedPeriod {
    
    LocalDate startDate;
    LocalDate endDate;
    
    List<RequiredResource> bookedResources;

    public RepresentedPeriod(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<RequiredResource> getBookedResources() {
        return bookedResources;
    }

    public void setBookedResources(List<RequiredResource> bookedResources) {
        this.bookedResources = bookedResources;
    }
    
    
}
