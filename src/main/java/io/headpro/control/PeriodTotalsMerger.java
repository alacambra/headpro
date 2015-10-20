/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.headpro.control;

import io.headpro.entity.RequiredResource;
import io.headpro.entity.PeriodTotal;
import java.util.List;
import java.util.function.BinaryOperator;
import static java.util.stream.Collectors.*;
import java.util.stream.Stream;

/**
 *
 * @author alacambra
 */
public class PeriodTotalsMerger {

    public static List<PeriodTotal> reduce(
            List<PeriodTotal> p1,
            List<PeriodTotal> p2) {

        BinaryOperator<PeriodTotal> reducer = (o1, o2) -> new PeriodTotal()
                .setStartDate(o1.getStartDate())
                .setEndDate(o1.getEndDate())
                .setTotal(o1.getTotal() + o2.getTotal());

        List<PeriodTotal> periods = Stream.concat(p1.stream(), p2.stream()).sorted()
                .collect(
                        groupingBy(PeriodTotal::getStartDate, reducing(reducer))
                ).values().stream().map(v -> v.get()).sorted().collect(toList());

        return periods;
    }

    public static List<RequiredResource> factorsPonderation(List<RequiredResource> bookedResources) {

        List<RequiredResource> r = bookedResources.stream().map(br -> {
            br.setBooked(br.getBooked() * br.getProject().getProbability() / (1 - br.getProject().getAbscence()));
            return br;
        }).collect(toList());
    
        return r;
    }

}
