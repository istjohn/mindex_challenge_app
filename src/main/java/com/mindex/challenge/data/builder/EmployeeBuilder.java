package com.mindex.challenge.data.builder;

import com.mindex.challenge.data.Employee;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Really just kinda added this for the tests, because I loooooove the builder pattern, prefer it strongly over
 * all them setters and getters.
 */
public class EmployeeBuilder {
    private String employeeId;
    private String first;
    private String last;
    private String position;
    private String department;
    private List<Employee> directReports;

    public EmployeeBuilder() {
         directReports = new ArrayList<>();
    }

    /**
     * Convenience constructor meant to take a first and last name separated by a single space.
     * Ex: given, EmployeeBuilder("Bob Dob") returns instance where first = "Bob" and last = "Dob"
     */
    public EmployeeBuilder(String firstLast) {
        this();
        String[] split = firstLast.split(" ");
        first = split[0];
        last = split[1];
    }

    /**
     * Constructs and returns the final employee with data from this builder instance, setting defaults on unset string
     * fields.
     */
    public Employee build() {

        //some nice defaulting in those lazy cases
        if(null == employeeId) {
            employeeId = UUID.randomUUID().toString();
        }
        if(null == first) {
            first = "Jane";
        }
        if(null == last) {
            last = "Doe";
        }
        if(null == position) {
            position = "Faceless Henchman";
        }
        if(null == department) {
            department = "Nefarious Henchman-ing";
        }

        Employee employee = new Employee();
        employee.setEmployeeId(employeeId);
        employee.setFirstName(first);
        employee.setLastName(last);
        employee.setPosition(position);
        employee.setDepartment(department);
        employee.setDirectReports(directReports);
        return employee;
    }

    public EmployeeBuilder employeeId(String arg) {employeeId = arg; return this;}
    public EmployeeBuilder firstName(String arg) {first = arg; return this;}
    public EmployeeBuilder lastName(String arg) {last = arg; return this;}
    public EmployeeBuilder position(String arg) {position = arg; return this;}
    public EmployeeBuilder department(String arg) {department = arg; return this;}
    public EmployeeBuilder directReport(Employee arg) {directReports.add(arg); return this;}

}
