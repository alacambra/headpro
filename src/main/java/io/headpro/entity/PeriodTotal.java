/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.headpro.entity;

import io.headpro.control.LocalDateConverter;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 *
 * @author alacambra
 */
public class PeriodTotal implements PeriodWithValue, Comparable<PeriodTotal> {

    LocalDate startDate;
    LocalDate endDate;
    Float total;
    int position = -1;

    public PeriodTotal(Date startDate, Date endDate, Float total) {
        if (startDate != null) {
            this.startDate = LocalDateConverter.toLocalDate(startDate);
        }

        if (endDate != null) {
            this.endDate = LocalDateConverter.toLocalDate(endDate);
        }
        this.total = total;
    }
    
     public PeriodTotal(Date startDate, Date endDate, Double total) {
        this(startDate, endDate, new Float(total));
    }

    public PeriodTotal(Date startDate, Float total) {
        this.startDate = LocalDateConverter.toLocalDate(startDate);
        this.total = total;
    }

    public PeriodTotal() {
        this.total = 0f;
    }

    @Override
    public LocalDate getStartDate() {
        return startDate;
    }

    public PeriodTotal setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    @Override
    public LocalDate getEndDate() {
        return endDate;
    }

    public PeriodTotal setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }
    
    public Date getStartDateAsDate(){
        return LocalDateConverter.toDate(startDate);
    }
    
    public Date getEndDateAsDate(){
        return LocalDateConverter.toDate(endDate);
    }

    public Float getTotal() {
        return total;
    }

    public PeriodTotal setTotal(Float total) {
        this.total = total;
        return this;
    }

    @Override
    public void setValue(Float o) {
        setTotal(o);
    }

    @Override
    public void setPeriod(LocalDate[] period) {
        setStartDate(period[0]);
        setEndDate(period[1]);
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

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
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.startDate);
        hash = 53 * hash + Objects.hashCode(this.endDate);
        hash = 53 * hash + Objects.hashCode(this.total);
        hash = 53 * hash + this.position;
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

    @Override
    public Float getValue() {
        return this.total;
    }

}
