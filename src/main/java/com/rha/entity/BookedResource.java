package com.rha.entity;

import com.rha.control.LocalDateConverter;
import java.io.Serializable;
import java.time.LocalDate;
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

@Entity
@Table(name = "BOOKEDRESOURCE")
@NamedQueries({
    @NamedQuery(name = BookedResource.totalByServiceInPeriod,
            query = "SELECT new com.rha.entity.PeriodTotal(br.startDate, br.endDate, sum(br.booked)) "
            + "FROM BookedResource br "
            + "WHERE ((br.startDate>=:startDate AND br.startDate<=:endDate)"
            + " OR (br.endDate>=:startDate AND br.endDate<=:endDate) "
            + " OR (br.startDate<=:startDate AND br.endDate>=:endDate)) "
            + "group by br.service, br.startDate, br.endDate order by br.startDate"),

    @NamedQuery(name = BookedResource.forService,
            query = "SELECT br FROM BookedResource br JOIN br.service s WHERE s=:service"),

    @NamedQuery(name = BookedResource.forServiceInPeriod,
            query = "SELECT br FROM BookedResource br JOIN br.service s "
            + "WHERE ((br.startDate>=:startDate AND br.startDate<=:endDate)"
            + " OR (br.endDate>=:startDate AND br.endDate<=:endDate) "
            + " OR (br.startDate<=:startDate AND br.endDate>=:endDate)) "
            + "AND s=:service order by br.startDate"),

    @NamedQuery(name = BookedResource.totalForServiceInPeriod,
            query = "SELECT new com.rha.entity.PeriodTotal(br.startDate, br.endDate, sum(br.booked))"
            + " FROM BookedResource br JOIN br.service s "
            + "WHERE ((br.startDate>=:startDate AND br.startDate<=:endDate)"
            + " OR (br.endDate>=:startDate AND br.endDate<=:endDate) "
            + " OR (br.startDate<=:startDate AND br.endDate>=:endDate)) "
            + "AND s=:service "
            + "group by br.startDate, br.endDate order by br.startDate"),
 
    @NamedQuery(name = BookedResource.bookedInPeriod,
            query = "SELECT br FROM BookedResource br "
            + " WHERE ((br.startDate>=:startDate AND br.startDate<=:endDate)"
            + " OR (br.endDate>=:startDate AND br.endDate<=:endDate) "
            + " OR (br.startDate<=:startDate AND br.endDate>=:endDate)) "
            + " ORDER BY br.startDate")
})
public class BookedResource implements Serializable, Comparable<BookedResource>, PeriodWithValue {

    private static final String prefix = "com.rha.entity.BookedResource.";
    public static final String bookedInPeriod = prefix + "bookedInPeriod";
    public static final String totalByServiceInPeriod = prefix + "totalByServiceInPeriod";
    public static final String totalForServiceInPeriod = prefix + "totalForServiceForPeriod";
    public static final String forService = prefix + "forService";
    public static final String forServiceInPeriod = prefix + "forServiceForPeriod";

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
            return LocalDateConverter.toLocalDate(startDate);
        } else {
            return null;
        }
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = LocalDateConverter.toDate(startDate);
    }

    public LocalDate getEndDate() {
        if (endDate != null) {
            return LocalDateConverter.toLocalDate(endDate);
        } else {
            return null;
        }
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = LocalDateConverter.toDate(endDate);
    }
    
    public Date getStartDateAsDate(){
        return new Date(startDate.getTime());
    }
    
    public Date getEndDateAsDate(){
        return new Date(endDate.getTime());
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

    @Override
    public Long getValue() {
        return booked;
    }

}
