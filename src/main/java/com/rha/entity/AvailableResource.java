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

/**
 *
 * @author alacambra
 */
@Entity
public class AvailableResource implements Serializable{
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Integer id;
    
    @ManyToOne
    Division division;
    
    Date startDate;
    Date endDate;
    
    Integer available;
    
    
    
    
}
