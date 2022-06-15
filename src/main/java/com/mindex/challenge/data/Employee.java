package com.mindex.challenge.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    private String employeeId;
    private String firstName;
    private String lastName;
    private String position;
    private String department;
    private List<Employee> directReports;

    public Employee() {
        //Added this initialization of the reports to an empty list, as it's good practice to avoid those pesky NPEs on
        //simple dumb empty lists
        directReports = new ArrayList<>();
    }

    /**
     * Gets the first name and last name of this employee instance.
     * @return String -- as, "firstName lastName"
     */
    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    /**
     * @return String -- first and last name of this employee in the form: "Last, First"
     */
    public String getLastFirst() {
        return String.format("%s, %s", lastName, firstName);
    }

    /**
     * Convenience adder for a directReport employee record.
     */
    public void addDirectReport(Employee employee) {
        directReports.add(employee);
    }

    /**
     * @return boolean -- true if employeeId is only piece of string data set, else false.
     */
    @JsonIgnore
    public boolean isOnlyEmployeeIdSet() {
        return !employeeId.isEmpty()
                && firstName == null
                && lastName == null
                && position == null
                && department == null;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public List<Employee> getDirectReports() {
        return directReports;
    }

    public void setDirectReports(List<Employee> directReports) {
        this.directReports = directReports;
    }
}
