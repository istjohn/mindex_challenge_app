package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.data.builder.EmployeeBuilder;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.mindex.challenge.service.impl.EmployeeServiceImplTest.assertEmployeeEquivalence;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureServiceImplTest {

    private String reportingStructureUrl;
    private String reportingStructureEmployeeIdUrl;

    @Autowired
    private ReportingStructureService reportingStructureService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Before
    public void setup() {
        reportingStructureUrl = "http://localhost:"+port+"/reportingStructure";
        reportingStructureEmployeeIdUrl = "http://localhost:"+port+"/reportingStructure/{employeeId}";
    }

    /**
     * Tests the service read of a simple reporting structure, where there are no sub-direct reports.
     */
    @Test
    public void test_read_flat() {
        Employee employee = new EmployeeBuilder("Jackie Pitts")
                .employeeId("888888888")
                .position("Lead Snark Ambassador")
                .department("Pit of Sadness")
                .build();

        Employee directRep1 = new EmployeeBuilder("Hector Hamburg").employeeId("555444").build();
        Employee directRep2 = new EmployeeBuilder("Ingrid Igloo").employeeId("444333").build();

        employeeRepository.insert(List.of(directRep1, directRep2));
        employee.addDirectReport(directRep1);
        employee.addDirectReport(directRep2);
        employeeRepository.insert(employee);

        ReportingStructure readStructure = restTemplate.getForEntity(reportingStructureEmployeeIdUrl,
                ReportingStructure.class, employee.getEmployeeId()).getBody();

        assertNotNull(readStructure);
        assertEmployeeEquivalence(employee, readStructure.getEmployee());
        assertEquals(2, readStructure.getNumberOfReports());
        assertEquals("+Jackie Pitts\n|--Hector Hamburg\n|--Ingrid Igloo\n",
                readStructure.getTreeLikeDisplay());
    }

    /**
     * Tests a service read of a more complex reporting structure with some sub-direct reports.
     */
    @Test
    public void test_read_nested() {
        Employee employee = new EmployeeBuilder("Nancy Oodles")
                .employeeId("123456789")
                .position("Cat Tamer")
                .department("Fuzzy Zone")
                .build();

        Employee directRep1 = new EmployeeBuilder("Manuel Bunches").employeeId("987").build();
        Employee directRep2 = new EmployeeBuilder("Liam Bundles").employeeId("789").build();

        Employee subDirectRep1 = new EmployeeBuilder("Ella Batches").employeeId("456").build();
        Employee subDirectRep2 = new EmployeeBuilder("Jillian Clumps").employeeId("123").build();

        employeeRepository.insert(List.of(subDirectRep1, subDirectRep2));
        directRep1.addDirectReport(subDirectRep1);
        employeeRepository.insert(directRep1);

        directRep2.addDirectReport(subDirectRep2);
        employeeRepository.insert(directRep2);

        employee.addDirectReport(directRep1);
        employee.addDirectReport(directRep2);
        employeeRepository.insert(employee);

        ReportingStructure readStructure = restTemplate.getForEntity(reportingStructureEmployeeIdUrl,
                ReportingStructure.class, employee.getEmployeeId()).getBody();

        assertNotNull(readStructure);
        assertEmployeeEquivalence(employee, readStructure.getEmployee());
        assertEquals(4, readStructure.getNumberOfReports());
        assertEquals("+Nancy Oodles\n" +
                        "|--Manuel Bunches\n" +
                        "   |---Ella Batches\n" +
                        "|--Liam Bundles\n" +
                        "   |---Jillian Clumps\n",
                readStructure.getTreeLikeDisplay());
    }

    /**
     * Nearly identical test to the above, {@link #test_read_nested()}, but using the default springbooted project data.
     */
    @Test
    public void test_read_nested_alt() {
        ReportingStructure readStructure = restTemplate.getForEntity(reportingStructureEmployeeIdUrl,
                ReportingStructure.class, "16a596ae-edd3-4847-99fe-c4518e82c86f").getBody();

        assertNotNull(readStructure);
        assertEquals(4, readStructure.getNumberOfReports());
        assertEquals("+John Lennon\n" +
                        "|--Paul McCartney\n" +
                        "|--Ringo Starr\n" +
                        "   |---Pete Best\n" +
                        "   |---George Harrison\n",
                readStructure.getTreeLikeDisplay());
    }
}
