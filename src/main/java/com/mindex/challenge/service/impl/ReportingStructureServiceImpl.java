package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure read(String employeeId) {
        LOG.debug("Generating reporting structure for employee with ID [{}]", employeeId);

        //The bootstrapped data in the DB is inserted literally from the JSON. As such, employee records fetched from
        //the repo are just stubs, meaning they are Employee instances with ONLY the employeeId set. We need
        //replace all these stubs with the full records from the repo for the full structure display.
        Employee employee = employeeRepository.findByEmployeeId(employeeId);

        //Follow the lead of EmployeeServiceImpl regarding nonexistent IDs.
        if(null == employee) {
            throw new RuntimeException("Invalid employeeID: " + employeeId);
        }

        fillOutDirectReports(employee);

        //With any nested employee records all filled out, we're all set to generate the reporting structure
        ReportingStructure reportingStructure = new ReportingStructure(employee);
        reportingStructure.calculateStructure();

        return reportingStructure;
    }

    /**
     * Ensures that all {@link Employee} instances in directReports are filled out, for the parent employee as well as
     * any of their own nested direct reports.
     *
     * @param employee -- the employee with the given employeeId.
     */
    private void fillOutDirectReports(Employee employee) {
        for(Employee directReport : employee.getDirectReports()) {

            //Only go on to fill out stub records...
            if(directReport.isOnlyEmployeeIdSet()) {
                int index = employee.getDirectReports().indexOf(directReport);

                //Update the directReport record in the parent employee
                employee.getDirectReports().set(index,
                        employeeRepository.findByEmployeeId(directReport.getEmployeeId()));

                //refresh the newly updated directReport
                directReport = employee.getDirectReports().get(index);

                if(!directReport.getDirectReports().isEmpty()) {
                    fillOutDirectReports(directReport);
                }
            }
        }
    }

}
