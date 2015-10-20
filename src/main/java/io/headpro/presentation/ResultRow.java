/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.headpro.presentation;

import io.headpro.entity.PeriodWithValue;
import io.headpro.entity.Service;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Inject;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 *
 * @author alacambra
 */
//TODO why not just ResourcesRow?
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
        if (obj instanceof ResourcesRow) {
            final ResourcesRow other = (ResourcesRow) obj;
            return service.equals(other.getKey());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(service).append(resources).toHashCode();
    }
}
