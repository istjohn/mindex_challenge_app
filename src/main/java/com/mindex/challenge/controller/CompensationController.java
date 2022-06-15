package com.mindex.challenge.controller;

import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
public class CompensationController {
    private static final Logger LOG = LoggerFactory.getLogger(CompensationController.class);

    @Autowired
    private CompensationService compensationService;

    @PostMapping("/compensation")
    public Compensation create(@RequestBody Compensation compensation) {
        LOG.debug("Received creation request for compensation [{}]", compensation);

        //Local dates are a TOTAL pain to type in JSON and may be left blank to avoid the headache. As such, today's
        //date is quite the sensible default for a record being created.
        if(compensation.getEffectiveDate()==null) {
            compensation.setEffectiveDate(LocalDate.now());
        }

        return compensationService.create(compensation);
    }

    @GetMapping("/compensation/{employeeId}")
    public Compensation read(@PathVariable String employeeId) {
        LOG.debug("Received read request for compensation for employee with ID [{}]", employeeId);

        return compensationService.read(employeeId);
    }
}
