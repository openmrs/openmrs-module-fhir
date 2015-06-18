package org.openmrs.module.fhir.api.diagnosticreport.handler;

import javax.naming.InvalidNameException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.fhir.api.util.FHIRDiagnosticReportUtil;

public abstract class AbstractHandler {
	
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private String id = null;
	
	public AbstractHandler() {
		//Leave blank for first draft
	}

}
