package com.rha.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;

/**
 * Created by alacambra on 27/04/15.
 */
@Entity
public class Project implements Serializable {

    @Id
    Integer id;

    String name;

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
}