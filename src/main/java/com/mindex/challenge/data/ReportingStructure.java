package com.mindex.challenge.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mindex.challenge.ex.MissingEmployeeException;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a given {@link Employee} and their <em>distinct</em> {@link Employee#directReports}. So, this
 * class represents everyone reporting under a given employee, disregarding duplicates.
 *
 * Note that this class is always computed on demand, and will never be persisted.
 */
public class ReportingStructure {

    //Some basic string constants for the tree-like representation of the structure.
    private static final String EMPLOYEE_ROOT_SYMBOL = "+";
    private static final String DIRECT_REPORT_SYMBOL = "|--";
    private static final String SUB_DIRECT_REPORT_SYMBOL = "|---";

    /**
     * The employee to which this reporting structure pertains.
     */
    private Employee employee;

    /**
     * The number of employees directly reporting to the {@link #employee} and the number of distinct reports under each
     * one of them.
     */
    private int numberOfReports;

    /**
     * Simple descriptive title for this reporting structure.
     */
    private String title;

    /**
     * A basic tree-like rendering of a fully filled out reporting structure where...
     * "+" denotes the root employee for which the structure applies,
     *  "|--" denotes a direct report under that root, and
     *  "|---" denotes a direct report's distinct report.
     *
     *  Given all that, an example of a filled out report could look like...
     *  +John Lennon
     *  |--Paul McCartney
     *  |--Ringo Starr
     *     |---Pete Best
     *     |---George Harrison
     *
     */
    private String treeLikeDisplay;

    public ReportingStructure() {}

    public ReportingStructure(Employee employee) {this.employee = employee;}

    /**
     * Performs one bit of light validation, then fills out the reporting structure if it's satisfied, based on the
     * given, {@link #employee}.
     *
     * The {@link #employee} MUST be set in order to calculate a reporting structure - there can be no structure for
     * reporting without one. Doth maketh no sense...eth.
     *
     * Note that because of the way {@link com.mindex.challenge.service.impl.ReportingStructureServiceImpl#read(String)}
     * handles fetching the employee with the given ID, we're sure that we have full employee records when we get here.
     */
    public void calculateStructure() {
        //If there isn't an employee specified, throw custom exception for it because there is no applicable structure.
        if(null == employee) {
            throw new MissingEmployeeException();
        }

        title = "Reporting structure prepared for: " + employee.getLastFirst();

        //If there is an employee, then we're all set, let's build out their structure. Filling out the direct reports
        //is a straightforward read off of an Employee instance. However, under each of those
        //direct reports we need DISTINCT reports. So, we should keep track of all the sub-direct reports we've
        //processed, just in case of duplicates in the structure. We use employee IDs, since there could technically be
        //duplicate names.
        List<String> knownReportingIds = new ArrayList<>();

        //We'll also build the tree-like representation as we go. Mark the root as our employee
        StringBuilder sb = new StringBuilder(EMPLOYEE_ROOT_SYMBOL).append(employee.getFullName()).append("\n");

        //Let's get started by running through the directReports
        for(Employee directReport : employee.getDirectReports()) {

            //First, increment the number of reports
            numberOfReports++;

            //Next, build the directReport's node in the tree-like string for display
            sb.append(DIRECT_REPORT_SYMBOL).append(directReport.getFullName()).append("\n");

            //Add their ID to the known list, as they could potentially appear in other directReport reports
            knownReportingIds.add(directReport.getEmployeeId());

            //Now, let's process distinct sub-direct reports, if there are any.
            for(Employee subDirectReport : directReport.getDirectReports()) {

                //Only process if we know this isn't a duplicate
                if(!knownReportingIds.contains(subDirectReport.getEmployeeId())) {
                    //Increment our counter
                    numberOfReports++;

                    //Add to string representation, taking correct indentation into account. We know that the "|---"
                    //marker should account for indentation, meaning it should align with the start of the parent's
                    //name. Pad the string with spaces up to appropriate index
                    sb.append(" ".repeat(DIRECT_REPORT_SYMBOL.length()))
                            .append(SUB_DIRECT_REPORT_SYMBOL)
                            .append(subDirectReport.getFullName())
                            .append("\n");

                    //Make sure to add the employee ID to our known list so we can detect later dupes
                    knownReportingIds.add(subDirectReport.getEmployeeId());
                }
            }
        }

        //At this point, we should have incremented for everyone in the structure. Just finally build the detail string
        treeLikeDisplay = sb.toString();
    }

    /**
     * @return String describing the fully filled out reporting structure for given employee.
     */
    @Override
    public String toString() {
        return String.format("%s (%d).%n%n%s", title, numberOfReports, treeLikeDisplay);
    }

    //STANDARD GETTERS/SETTERS AT THE BOTTOM TO DECREASE THE CLUTTER... logic stuff is all above.

    public void setEmployee(Employee employee) {this.employee = employee;}
    public Employee getEmployee() {return employee;}

    /**
     * The field, {@link #numberOfReports} is calculated on demand every time a given reporting structure is requested,
     * so public accessor is suppressed, as the field should always be determined internally.
     */
    private void setNumberOfReports(int numberOfReports) {this.numberOfReports = numberOfReports;}
    public int getNumberOfReports() {return numberOfReports;}

    /**
     * Suppressing any potential public setter, this string should only be set in the calc method.
     */
    private void setTreeLikeDisplay(String treeLikeDisplay) {this.treeLikeDisplay = treeLikeDisplay;}
    public String getTreeLikeDisplay() {return treeLikeDisplay;}

    /**
     * Same as above, suppressing a public setter.
     */
    private void setTitle(String title) {this.title = title;}
    public String getTitle() {return title;}

}
