/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import com.rha.entity.AvailableResource;
import com.rha.entity.Service;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@SessionScoped
public class ResultsController implements Serializable{
    
    Service service;
    List<AvailableResource> resources;
    
    @Inject
    transient Logger logger;

    public ResultsController(List<AvailableResource> resources, Service service) {
        this.resources = resources;
        this.service = service;
    }

    public List<AvailableResource> getResources() {
        return resources;
    }

    public Service getService() {
        return service;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AvailableResourceRow) {
            final AvailableResourceRow other = (AvailableResourceRow) obj;
            return service.equals(other.getService());
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(service).append(resources).toHashCode();
    }
}
