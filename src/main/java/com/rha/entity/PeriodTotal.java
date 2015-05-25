/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.entity;

import com.rha.control.LocalDateConverter;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 *
 * @author alacambra
 */
public class PeriodTotal implements PeriodWithValue, Comparable<PeriodTotal>{
    LocalDate startDate;
    LocalDate endDate;
    Long total;
    int position = -1;

    public PeriodTotal(Date startDate, Date endDate, Long total) {
        this.startDate = LocalDateConverter.toLocalDate(startDate);
        this.endDate = LocalDateConverter.toLocalDate(endDate);
        this.total = total;
    }
    
    public PeriodTotal(Date startDate, Long total) {
        this.startDate = LocalDateConverter.toLocalDate(startDate);
        this.total = total;
    }
    
    public PeriodTotal() {
        this.total = 0L;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    @Override
    public Integer getPosition() {
        return position;
    }

    @Override
    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public void setValue(Long o) {
        setTotal(o);
    }

    @Override
    public void setPeriod(LocalDate[] period) {
        setStartDate(period[0]);
        setEndDate(period[1]);
    }
    
    @Override
    public boolean equals(Object obj) {
        
        if(obj == this) return true;
        
        if (obj instanceof PeriodTotal) {
            final PeriodTotal other = (PeriodTotal) obj;
                 return new EqualsBuilder()
                         .append(getTotal(), other.getTotal())
                         .append(getStartDate(), other.getStartDate())
                         .append(getEndDate(), other.getEndDate())
                         .isEquals();

        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.startDate);
        hash = 97 * hash + Objects.hashCode(this.endDate);
        hash = 97 * hash + (int) (this.total ^ (this.total >>> 32));
        return hash;
    }

    @Override
    public int compareTo(PeriodTotal o) {

        if (o.getStartDate() == null) {
            return 1;
        }

        if (getStartDate().isAfter(o.getStartDate())) {
            return 1;
        } else if (getStartDate().isEqual(o.getStartDate())) {
            return 0;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        
        return new StringBuilder(startDate.toString()).append(":").append(endDate).append(" = ").append(total).toString();
    }
    
    
}
