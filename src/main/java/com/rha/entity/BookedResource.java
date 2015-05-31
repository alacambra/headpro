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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author alacambra
 */
@Entity
@Table(uniqueConstraints={
    @UniqueConstraint(columnNames = {"startdate", "project"})
})
@NamedQueries({
    @NamedQuery(name = BookedResource.byProjectAndService,
            query = "SELECT br FROM BookedResource br LEFT JOIN br.project p LEFT JOIN br.service d "
            + "WHERE p.id=:pid and d.id=:did"),

    @NamedQuery(name = BookedResource.totalByService,
            query = "SELECT sum(br.booked) FROM BookedResource br JOIN br.service s "
            + "WHERE s=:service group by br.startDate order by br.startDate"),

    @NamedQuery(name = BookedResource.byService,
            query = "SELECT br FROM BookedResource br JOIN br.service s WHERE s=:service"),

    @NamedQuery(name = BookedResource.byServiceForPeriod,
            query = "SELECT br FROM BookedResource br JOIN br.service s WHERE "
            + "br.startDate>=:startDate AND br.endDate<=:endDate AND s=:service order by br.startDate"),

    @NamedQuery(name = BookedResource.totalByServiceForPeriod,
            query = "SELECT new com.rha.entity.PeriodTotal(br.startDate, br.endDate, sum(br.booked))"
            + " FROM BookedResource br JOIN br.service s "
            + "WHERE "
            + "s=:service AND br.startDate>=:startDate AND br.endDate<=:endDate "
            + "group by br.startDate, br.endDate order by br.startDate"),

    @NamedQuery(name = "back-up",
            query = "SELECT br FROM BookedResource br JOIN br.service d WHERE d.id=:did "
            + "AND br.startDate>=:startDate AND br.endDate<=:endDate")
})
public class BookedResource implements Serializable, Comparable<BookedResource>, PeriodWithValue {

    private static final String prefix = "com.rha.entity.BookedResource.";
    public static final String byProjectAndService = prefix + "byProjectAndService";
    public static final String totalByService = prefix + "totalByService";
    public static final String totalByServiceForPeriod = prefix + "totalByServiceForPeriod";
    public static final String byService = prefix + "byService";
    public static final String byServiceForPeriod = prefix + "byServiceForPeriod";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    @ManyToOne
    Service service;

    @ManyToOne
    Project project;

    @Temporal(TemporalType.DATE)
    Date startDate;

    @Temporal(TemporalType.DATE)
    Date endDate;

    Long booked = 0L;

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

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
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

    public Long getBooked() {
        return booked;
    }

    public void setBooked(Long booked) {
        this.booked = booked;

    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;

    }

    @Override
    public int hashCode() {
        int hash = 7;

        hash = 29 * hash + Objects.hashCode(this.startDate);
        hash = 29 * hash + Objects.hashCode(this.endDate);

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
        if (!Objects.equals(this.startDate, other.startDate)) {
            return false;
        }
        if (!Objects.equals(this.endDate, other.endDate)) {
            return false;
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
    public void setValue(Long o) {
        setBooked(o);
    }

    @Override
    public void setPeriod(LocalDate[] period) {
        setStartDate(period[0]);
        setEndDate(period[1]);
    }

}
