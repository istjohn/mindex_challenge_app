package com.mindex.challenge.ex;

/**
 * An exception for when attempting to generate an {@link com.mindex.challenge.data.ReportingStructure} without
 * providing an employee to which the structure would pertain.
 */
public class MissingEmployeeException extends RuntimeException {

    private static final String EXCEPTION_MESSAGE = "Exception occurred attempting to generate a ReportingStructure " +
            "without specifying an Employee! Unable to generate structure without one.";

    public MissingEmployeeException() {
        super(EXCEPTION_MESSAGE);
    }
}
