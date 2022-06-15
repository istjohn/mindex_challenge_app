package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.service.CompensationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompensationServiceImpl implements CompensationService {

    private static final Logger LOG = LoggerFactory.getLogger(CompensationServiceImpl.class);

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public Compensation create(Compensation compensation) {
        LOG.debug("Creating compensation [{}]", compensation);

        if(compensation.getEmployee().isOnlyEmployeeIdSet()) {
            compensation.setEmployee(employeeRepository.findByEmployeeId(compensation.getEmployee().getEmployeeId()));
        }

        return compensationRepository.insert(compensation);
    }

    @Override
    public Compensation read(String employeeId) {
        LOG.debug("Attempting to read compensation for employee with ID [{}]", employeeId);

        Compensation compensation = compensationRepository.findByEmployeeId(employeeId);

        if(compensation.getEmployee().isOnlyEmployeeIdSet()) {
            compensation.setEmployee(employeeRepository.findByEmployeeId(compensation.getEmployee().getEmployeeId()));
        }

        return compensation;
    }
}
