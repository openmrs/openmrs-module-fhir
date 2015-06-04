package org.openmrs.module.fhir.api.diagnosticreport;

import java.util.Date;

public class DiagnosticReportTemplate {
	
	private String status = null;
	private Date issued = null;
	
    public String getStatus() {
    	return status;
    }
	
    public void setStatus(String status) {
    	this.status = status;
    }
	
    public Date getIssued() {
    	return issued;
    }
	
    public void setIssued(Date issued) {
    	this.issued = issued;
    }
}
