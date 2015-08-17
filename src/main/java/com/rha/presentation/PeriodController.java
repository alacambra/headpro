/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import com.rha.control.CalendarPeriodsGenerator;
import com.rha.control.LocalDateConverter;
import com.rha.entity.Service;
import com.rha.entity.Step;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Event;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

/**
 *
 * @author alacambra
 */
@SessionScoped
@Named
public class PeriodController implements Serializable {

    LocalDate startDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
    LocalDate endDate = LocalDate.now().plusMonths(3).with(TemporalAdjusters.lastDayOfMonth());
    Service activeSerivice;
    List<LocalDate[]> periods;
    Step step = Step.BIWEEK;
    
    @Inject
    Event<PeriodChangedEvent> event;
    
    @Inject
    CalendarPeriodsGenerator calendarPeriodsGenerator;
    
    private void loadPeriods() {
        periods = calendarPeriodsGenerator
                .setStartDate(startDate)
                .setEndDate(endDate)
                .setStep(step)
                .generatePeriods();
    }

    public List<LocalDate[]> getPeriods() {
        if(periods == null){
            loadPeriods();
        }
        return periods;
    }

    public LocalDate getLocalStartDate() {
        return startDate;
    }

    public LocalDate getLocalEndDate() {
        return endDate;
    }
    
    public Date getStartDate() {
        return LocalDateConverter.toDate(startDate);
    }

    public void setStartDate(@NotNull Date startDate) {
        this.startDate = LocalDateConverter.toLocalDate(startDate);
    }

    public Date getEndDate() {
        return LocalDateConverter.toDate(endDate);
    }

    public void setEndDate(@NotNull Date endDate) {
        this.endDate = LocalDateConverter.toLocalDate(endDate);
    }

    public Service getActiveSerivice() {
        return activeSerivice;
    }

    public void setActiveSerivice(@NotNull Service activeSerivice) {
        this.activeSerivice = activeSerivice;
    }

    public void setStartDate(@NotNull LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(@NotNull LocalDate endDate) {
        this.endDate = endDate;
    }

    public Step getStep() {
        return step;
    }

    public void setStep(@NotNull Step step) {
        this.step = step;
    }

    public List<Step> getSteps() {
        return Arrays.asList(Step.BIWEEK);
    }

    public void submitChanges() {
        
        loadPeriods();
        PeriodChangedEvent periodChangedEvent = 
                new PeriodChangedEvent()
                .setActiveSerivice(activeSerivice)
                .setEndDate(endDate)
                .setStartDate(startDate)
                .setPeriods(periods)
                .setStep(step);
        
        event.fire(periodChangedEvent);
        
//        try {
//            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
//            ec.redirect(((HttpServletRequest) ec.getRequest()).getRequestURI());
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
    }
}
