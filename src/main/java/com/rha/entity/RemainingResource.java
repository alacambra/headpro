/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.entity;

import java.time.LocalDate;

/**
 *
 * @author alacambra
 */
public class RemainingResource implements PeriodWithValue {

    private Integer position;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long total;
    private Service service;

    @Override
    public Integer getPosition() {
        return position;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void setValue(Long o) {
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
    public Long getValue() {
        return this.total;
    }

}
