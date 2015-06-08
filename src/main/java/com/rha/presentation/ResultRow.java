/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rha.presentation;

import com.rha.entity.PeriodWithValue;
import com.rha.entity.Service;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 * @author alacambra
 */
@SessionScoped
public class ResultRow implements Serializable {

    Service service;
    List<PeriodWithValue> resources;

    @Inject
    transient Logger logger;

    public ResultRow(List<PeriodWithValue> resources, Service service) {
        this.resources = resources;
        this.service = service;
    }

    public List<PeriodWithValue> getResources() {
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
