package com.rha.entity;

import javax.persistence.*;
import java.util.List;

/**
 * Created by alacambra on 27/04/15.
 */
@Entity
public class Project {

    @Id
    Integer id;

    @ManyToOne
    Company company;

    @OneToMany
    List<Division> divisions;

    @ManyToMany
    List<Resource> resources;

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
