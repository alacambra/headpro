/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.entity;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 * @author alacambra
 */
@Entity
@NamedQueries({
    @NamedQuery(name = BookedResource.byProjectAndDivision,
            query = "SELECT br FROM BookedResource br LEFT JOIN br.project p LEFT JOIN br.division d "
            + "WHERE p.id=:pid and d.id=:did"),

    @NamedQuery(name = BookedResource.totalByDivision,
            query = "SELECT sum(br.booked) FROM BookedResource br JOIN br.division d "
            + "WHERE d.id=:did group by br.startDate order by br.startDate"),

    @NamedQuery(name = BookedResource.byDivision,
            query = "SELECT br FROM BookedResource br JOIN br.division d WHERE d.id=:did"),
    
    @NamedQuery(name = BookedResource.byDivisionForPeriod,
            query = "SELECT br FROM BookedResource br JOIN br.division d WHERE d.id=:did "
                    + "AND br.startDate>=:startDate AND br.endDate<=:endDate")
})
public class BookedResource implements Serializable, Comparable<BookedResource> {

    private static final String prefix = "com.rha.entity.BookedResource.";
    public static final String byProjectAndDivision = prefix + "byProjectAndDivision";
    public static final String totalByDivision = prefix + "totalByDivision";
    public static final String byDivision = prefix + "byDivision";
    public static final String byDivisionForPeriod = prefix + "byDivisionForPeriod";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @ManyToOne
    Division division;

    @ManyToOne
    Project project;

    @Temporal(TemporalType.DATE)
    Date startDate;

    @Temporal(TemporalType.DATE)
    Date endDate;

    Integer booked;

    @Transient
    Integer position = 0;

    @Transient
    private boolean persisted = true;

    public boolean isPersisted() {
        return persisted;
    }

    public BookedResource setPersisted(boolean persisted) {
        this.persisted = persisted;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public BookedResource setId(Integer id) {
        this.id = id;
        return this;
    }

    public Division getDivision() {
        return division;
    }

    public BookedResource setDivision(Division division) {
        this.division = division;
        return this;
    }

    public Project getProject() {
        return project;
    }

    public BookedResource setProject(Project project) {
        this.project = project;
        return this;
    }

    public LocalDate getStartDate() {
        if(startDate != null)
            return startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        else return null;
    }
    
    

    public BookedResource setStartDate(LocalDate startDate) {
        Instant instant = startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        this.startDate = Date.from(instant);
        return this;
    }

    public LocalDate getEndDate() {
        if(endDate != null)
            return endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        else return null;
    }

    public BookedResource setEndDate(LocalDate endDate) {
        Instant instant = endDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        this.endDate = Date.from(instant);
        return this;
    }

    public Integer getBooked() {
        return booked;
    }

    public BookedResource setBooked(Integer booked) {
        this.booked = booked;
        return this;
    }

    public Integer getPosition() {
        return position;
    }

    public BookedResource setPosition(Integer position) {
        this.position = position;
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if (obj instanceof BookedResource) {
            final BookedResource other = (BookedResource) obj;
            if (null != id && id != -1) {

                return new EqualsBuilder()
                        .append(id, other.getId())
                        .isEquals();
            } else {
                 return new EqualsBuilder()
                         .append(getStartDate(), other.getStartDate())
                         .append(getEndDate(), other.getEndDate())
                         .isEquals();
            }

        } else {
            return false;
        }
    }

    @Override
    public int compareTo(BookedResource o) {

        if (o.getPosition() == null) {
            return 1;
        }

        if (position > o.getPosition()) {
            return 1;
        } else if (position.equals(o.getPosition())) {
            return 0;
        } else {
            return -1;
        }
    }

}
