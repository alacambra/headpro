/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 * @author alacambra
 */
@Entity
public class BookedResource implements Serializable, Comparable<BookedResource> {

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
    
    Integer position;

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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BookedResource) {
            final BookedResource other = (BookedResource) obj;
            return new EqualsBuilder()
                    .append(id, other.getId())
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(BookedResource o) {
        
        if(o.getPosition() == null){
            return 1;
        }
        
        if(position > o.getPosition()){
            return 1;
        }else if(position.equals(o.getPosition())){
            return 0;
        }else{
            return -1;
        }
    }
    
}
