/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.headpro.presentation;

import io.headpro.entity.Service;
import io.headpro.entity.Step;
import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author alacambra
 */
public class PeriodChangedEvent {
    
    LocalDate startDate;
    LocalDate endDate;
    Service activeSerivice;
    List<LocalDate[]> periods;
    Step step;

    public LocalDate getStartDate() {
        return startDate;
    }

    public PeriodChangedEvent setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public PeriodChangedEvent setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public Service getActiveSerivice() {
        return activeSerivice;
    }

    public PeriodChangedEvent setActiveSerivice(Service activeSerivice) {
        this.activeSerivice = activeSerivice;
        return this;
    }

    public List<LocalDate[]> getPeriods() {
        return periods;
    }

    public PeriodChangedEvent setPeriods(List<LocalDate[]> periods) {
        this.periods = periods;
        return this;
    }

    public Step getStep() {
        return step;
    }

    public PeriodChangedEvent setStep(Step step) {
        this.step = step;
        return this;
    }
}
