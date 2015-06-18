package org.openmrs.module.fhir.api.diagnosticreport.handler;

import javax.naming.InvalidNameException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.fhir.api.util.FHIRDiagnosticReportUtil;

public abstract class AbstractHandler {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private String id = null;
	
	public AbstractHandler() {
		try {
			this.id = FHIRDiagnosticReportUtil.getServiceCode(getClass().getSimpleName());
		}
		catch (InvalidNameException e) {
			log.error("Unable to find a valid code.", e);
		}
	}
	
	public String getId() {
		// Refer: http://hl7.org/fhir/v2/0074
		return this.id;
	}
}
