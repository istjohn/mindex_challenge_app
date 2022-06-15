package com.mindex.challenge.data;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Compensation {

    private String employeeId;
    private Employee employee;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_FLOAT)
    private BigDecimal salary;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate effectiveDate;

    public Compensation() {}
    public Compensation(Employee employee) {
        this.employee = employee;
        employeeId = employee.getEmployeeId();
    }

    public void setEmployeeId(String employeeId) {this.employeeId = employeeId;}
    public String getEmployeeId() {return employeeId;}

    public void setEmployee(Employee employee) {this.employee = employee;}
    public Employee getEmployee() {return employee;}

    public void setSalary(BigDecimal salary) {this.salary = salary;}
    public BigDecimal getSalary() {return salary;}

    public void setEffectiveDate(LocalDate effectiveDate) {this.effectiveDate = effectiveDate;}
    public LocalDate getEffectiveDate() {return effectiveDate;}
}
