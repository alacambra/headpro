/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import com.rha.entity.BookedResource;
import com.rha.entity.Service;
import com.rha.entity.Project;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 * @author alacambra
 */
@SessionScoped
public class BookingRow implements Serializable{

    Project project;
    Service division;
    List<BookedResource> resources;
    
    @Inject
    Logger logger;

    public BookingRow(Project project, List<BookedResource> resources, Service division) {
        this.project = project;
        this.resources = resources;
        this.division = division;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<BookedResource> getResources() {
        return resources;
    }

    public void setResources(List<BookedResource> resources) {
        this.resources = resources;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BookingRow) {
            final BookingRow other = (BookingRow) obj;
            return new EqualsBuilder()
                    .append(project.getId(), other.getProject().getId())
                    .isEquals();
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(project.getId()).append(resources).toHashCode();
    }
}
