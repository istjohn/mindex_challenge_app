package com.mindex.challenge;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.data.builder.EmployeeBuilder;
import com.mindex.challenge.ex.MissingEmployeeException;
import com.mindex.challenge.service.EmployeeService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static com.mindex.challenge.service.impl.EmployeeServiceImplTest.assertEmployeeEquivalence;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ChallengeApplicationTests {

	private ReportingStructure reportingStructure;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@Autowired
	private EmployeeRepository employeeRepository;

	/**
	 * Used for creating new test employees.
	 */
	@Autowired
	private EmployeeService employeeService;

	@Test
	public void contextLoads() {
	}

//	private Employee setupEmployee(String first, String last, @Nullable String department, @Nullable String position) {
//		Employee e = new Employee();
//		e.setFirstName(first);
//		e.setLastName(last);
//
//	}

	private void setup() {

	}

	/**
	 * Tests appropriate exception when attempting to generate a reporting structure
	 * without a specified employee
	 */
	@Test
	public void test_reportingStructure_noEmployee() {
		reportingStructure = new ReportingStructure();

		expectedException.expect(MissingEmployeeException.class);
		expectedException.expectMessage("Exception occurred attempting to generate a ReportingStructure " +
				"without specifying an Employee! Unable to generate structure without one.");

		reportingStructure.calculateStructure();
	}

	/**
	 * Tests smooth generation of reporting structure for an employee with no direct reports.
	 */
	@Test
	public void test_reportingStructure_noReports() {
		reportingStructure = new ReportingStructure();

		Employee testEmployee = new EmployeeBuilder("Nathan Person").employeeId("12345").build();

		reportingStructure = new ReportingStructure(testEmployee);
		reportingStructure.calculateStructure();

		assertNotNull(reportingStructure.getEmployee());
		assertEmployeeEquivalence(testEmployee, reportingStructure.getEmployee());
		assertEquals(0, reportingStructure.getNumberOfReports());
		assertEquals("+Nathan Person\n", reportingStructure.getTreeLikeDisplay());
	}

	/**
	 * Tests correct generation of reporting structure for an employee with only direct reports,
	 * meaning no nesting (i.e. no reports under those direct reports)
	 */
	@Test
	public void test_reportingStructure_onlyDirectReports() {
		Employee rootEmployee = new EmployeeBuilder()
				.employeeId("54321")
				.firstName("Kathleen")
				.lastName("Rupert")
				.position("Supervisor")
				.department("Supervising")
				.build();

		Employee directRep1 = new EmployeeBuilder("Stevie Employments").employeeId("11111").build();
		Employee directRep2 = new EmployeeBuilder("Stevie Nickles").employeeId("2222").build();

		rootEmployee.addDirectReport(directRep1);
		rootEmployee.addDirectReport(directRep2);

		reportingStructure = new ReportingStructure(rootEmployee);
		reportingStructure.calculateStructure();

		assertEmployeeEquivalence(rootEmployee, reportingStructure.getEmployee());
		assertEquals(2, reportingStructure.getNumberOfReports());
		assertEquals("+Kathleen Rupert\n|--Stevie Employments\n|--Stevie Nickles\n",
				reportingStructure.getTreeLikeDisplay());
	}

	/**
	 * Tests that reporting structure generation correctly disregards duplicate sub-direct reports.
	 */
	@Test
	public void test_reportingStructure_duplicateHandling() {
		Employee root = new EmployeeBuilder("Yankee Doodle").build();

		//no args supplied -- all default data...
		Employee subDirectRep = new EmployeeBuilder().build();

		Employee directRep1 = new EmployeeBuilder("Yancy Schmancy").employeeId("159").build();
		Employee directRep2 = new EmployeeBuilder("Florence Pigeon").employeeId("951").build();

		//Add the default sub-direct to each of the direct reps
		directRep1.addDirectReport(subDirectRep);
		directRep2.addDirectReport(subDirectRep);

		//Add the direct reps to the root
		root.addDirectReport(directRep1);
		root.addDirectReport(directRep2);

		reportingStructure = new ReportingStructure(root);
		reportingStructure.calculateStructure();

		assertEmployeeEquivalence(root, reportingStructure.getEmployee());

		//count should only be 3 - the 2 direct reps and the 1st occurrence of the default sub-direct.
		assertEquals(3, reportingStructure.getNumberOfReports());

		assertEquals("+Yankee Doodle\n|--Yancy Schmancy\n   |---Jane Doe\n|--Florence Pigeon\n",
				reportingStructure.getTreeLikeDisplay());
	}

	/**
	 * Tests reporting structure calculation for the full basic example provided in the README.
	 */
	@Test
	public void test_reportingStructure_fullBasicExample() {
		Employee employee = employeeRepository.findByEmployeeId("16a596ae-edd3-4847-99fe-c4518e82c86f");

		//the above only retrieves the directReports as mostly empty Employee instance, with only the ID filled in. Let's fix that
		employee.getDirectReports().set(0,
				employeeRepository.findByEmployeeId(employee.getDirectReports().get(0).getEmployeeId()));
		employee.getDirectReports().set(1,
				employeeRepository.findByEmployeeId(employee.getDirectReports().get(1).getEmployeeId()));

		//Now fill out the direct rep with their own...
		Employee directRepWithReps = employee.getDirectReports().get(1);
		directRepWithReps.getDirectReports().set(0,
				employeeRepository.findByEmployeeId(directRepWithReps.getDirectReports().get(0).getEmployeeId()));
		directRepWithReps.getDirectReports().set(1,
				employeeRepository.findByEmployeeId(directRepWithReps.getDirectReports().get(1).getEmployeeId()));

		reportingStructure = new ReportingStructure(employee);
		reportingStructure.calculateStructure();

		assertEquals(4, reportingStructure.getNumberOfReports());
		assertEquals("+John Lennon\n|--Paul McCartney\n|--Ringo Starr\n   |---Pete Best\n   |---George Harrison\n",
				reportingStructure.getTreeLikeDisplay());
	}

}
