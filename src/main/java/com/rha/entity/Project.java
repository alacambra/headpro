package com.rha.entity;

import java.io.Serializable;
import java.util.Calendar;
import javax.persistence.*;
import java.util.List;

/**
 * Created by alacambra on 27/04/15.
 */
@Entity
public class Project implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;

    String name;
    
    Integer probability;
    
    @Temporal(TemporalType.DATE)
    Calendar startDate;
    
    @Temporal(TemporalType.DATE)
    Calendar endDate;
    
    Integer abscence;

    @ManyToOne
    Company company;

    @OneToMany
    List<Division> divisions;

    @ManyToMany
    List<Resource> resources;
    
    Step step;

    public Step getStep() {
        return step;
    }

    public void setStep(Step step) {
        this.step = step;
    }

    public Integer getProbability() {
        return probability;
    }

    public void setProbability(Integer probability) {
        this.probability = probability;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<Division> getDivisions() {
        return divisions;
    }

    public void setDivisions(List<Division> divisions) {
        this.divisions = divisions;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    public void setEndDate(Calendar end) {
        this.endDate = end;
    }

    public Integer getAbscence() {
        return abscence;
    }

    public void setAbscence(Integer abscence) {
        this.abscence = abscence;
    }
    
    
    
}