/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.headpro.entity;

import java.time.LocalDate;

/**
 *
 * @author alacambra
 */
public class RemainingResource implements PeriodWithValue {

    private LocalDate startDate;
    private LocalDate endDate;
    private Float total;
    private Service service;

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public void setValue(Float o) {
        this.total = o;
    }

    @Override
    public void setPeriod(LocalDate[] period) {
        startDate = period[0];
        endDate = period[1];
    }

    public void setService(Service service) {
        this.service = service;
    }

    @Override
    public Float getValue() {
        return this.total;
    }

}
