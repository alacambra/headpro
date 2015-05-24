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
import java.util.Objects;
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
            query = "SELECT br FROM BookedResource br WHERE "
            + "br.startDate>=:startDate AND br.endDate<=:endDate"),

    @NamedQuery(name = BookedResource.totalByDivisionForPeriod,
            query = "SELECT new com.rha.entity.PeriodTotal(br.startDate, br.endDate, sum(br.booked))"
            + " FROM BookedResource br "
            + "WHERE "
            + "br.startDate>=:startDate AND br.endDate<=:endDate "
            + "group by br.startDate, br.endDate order by br.startDate"),

    @NamedQuery(name = "back-up",
            query = "SELECT br FROM BookedResource br JOIN br.division d WHERE d.id=:did "
            + "AND br.startDate>=:startDate AND br.endDate<=:endDate")
})
public class BookedResource implements Serializable, Comparable<BookedResource>, PeriodWithValue {

    private static final String prefix = "com.rha.entity.BookedResource.";
    public static final String byProjectAndDivision = prefix + "byProjectAndDivision";
    public static final String totalByDivision = prefix + "totalByDivision";
    public static final String totalByDivisionForPeriod = prefix + "totalByDivisionForPeriod";
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

    Integer booked = 0;

    @Transient
    Integer position = 0;

    @Transient
    private boolean persisted = true;

    public boolean isPersisted() {
        return persisted;
    }

    public void setPersisted(boolean persisted) {
        this.persisted = persisted;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;

    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;

    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;

    }

    public LocalDate getStartDate() {
        if (startDate != null) {
            return startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            return null;
        }
    }

    public void setStartDate(LocalDate startDate) {
        Instant instant = startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        this.startDate = Date.from(instant);

    }

    public LocalDate getEndDate() {
        if (endDate != null) {
            return endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } else {
            return null;
        }
    }

    public void setEndDate(LocalDate endDate) {
        Instant instant = endDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        this.endDate = Date.from(instant);

    }

    public Integer getBooked() {
        return booked;
    }

    public void setBooked(Integer booked) {
        this.booked = booked;

    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;

    }

//    @Override
//    public boolean equals(Object obj) {
//
//        if (obj == this) {
//            return true;
//        }
//
//        if (obj instanceof BookedResource) {
//            final BookedResource other = (BookedResource) obj;
//            if (null != id && id != -1) {
//
//                return new EqualsBuilder()
//                        .append(id, other.getId())
//                        .isEquals();
//            } else {
//                return new EqualsBuilder()
//                        .append(getStartDate(), other.getStartDate())
//                        .append(getEndDate(), other.getEndDate())
//                        .isEquals();
//            }
//
//        } else {
//            return false;
//        }
//    }
    @Override
    public int hashCode() {
        int hash = 7;

        if (null != id && id != -1) {
            hash = 29 * hash + Objects.hashCode(this.id);
        } else {
            hash = 29 * hash + Objects.hashCode(this.startDate);
            hash = 29 * hash + Objects.hashCode(this.endDate);
        }

        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BookedResource other = (BookedResource) obj;
        if (null != id && id != -1) {
            if (!Objects.equals(this.id, other.id)) {
                return false;
            }
        } else {
            if (!Objects.equals(this.startDate, other.startDate)) {
                return false;
            }
            if (!Objects.equals(this.endDate, other.endDate)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(BookedResource o) {

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
    public void setValue(Object o) {
        setBooked((Integer) o);
    }

    @Override
    public void setPeriod(LocalDate[] period) {
        setStartDate(period[0]);
        setEndDate(period[1]);
    }

}
