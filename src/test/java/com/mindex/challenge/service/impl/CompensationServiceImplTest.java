package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.CompensationRepository;
import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.builder.EmployeeBuilder;
import com.mindex.challenge.service.CompensationService;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.mindex.challenge.service.impl.EmployeeServiceImplTest.assertEmployeeEquivalence;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CompensationServiceImplTest {

    private String compensationUrl;
    private String compensationEmployeeIdUrl;

    @Autowired
    private CompensationService compensationService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CompensationRepository compensationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Before
    public void setup() {
        compensationUrl = "http://localhost:"+port+"/compensation";
        compensationEmployeeIdUrl = "http://localhost:"+port+"/compensation/{employeeId}";
    }

    @Test
    public void test_createAndRead() {
        Employee employee = new EmployeeBuilder("Stacy Haberdasher")
                .employeeId("887766")
                .position("Executive Brooding Chancellor")
                .department("Research and Development")
                .build();
        employeeRepository.insert(employee);

        Compensation ogCompensation = new Compensation(employee);
        ogCompensation.setSalary(new BigDecimal("50000.00"));
        ogCompensation.setEffectiveDate(LocalDate.parse("2022-01-15"));

        Compensation createdComp = restTemplate.postForEntity(compensationUrl, ogCompensation, Compensation.class).getBody();
        
        assertNotNull(createdComp);
        assertCompensationEquals(ogCompensation, createdComp);

        Compensation readComp = restTemplate.getForEntity(compensationEmployeeIdUrl, Compensation.class,
                createdComp.getEmployeeId()).getBody();
        assertNotNull(readComp);
        assertCompensationEquals(ogCompensation, readComp);
    }

    private void assertCompensationEquals(Compensation expected, Compensation actual) {
        assertEmployeeEquivalence(expected.getEmployee(), actual.getEmployee());
        assertEquals(expected.getSalary(), actual.getSalary());
        assertEquals(expected.getEffectiveDate(), actual.getEffectiveDate());
    }

}
