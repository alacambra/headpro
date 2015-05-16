/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import com.rha.entity.BookedResource;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author alacambra
 */
public class RepresentedPeriod {
    
    LocalDate startDate;
    LocalDate endDate;
    
    List<BookedResource> bookedResources;

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

    public List<BookedResource> getBookedResources() {
        return bookedResources;
    }

    public void setBookedResources(List<BookedResource> bookedResources) {
        this.bookedResources = bookedResources;
    }
    
    
}
