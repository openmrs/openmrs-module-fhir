package org.openmrs.module.fhir.api.diagnosticreport.handler;

import org.openmrs.Obs;
import org.openmrs.module.fhir.api.diagnosticreport.DiagnosticReportHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RadiologyHandler extends AbstractHandler implements DiagnosticReportHandler {
	
	public RadiologyHandler() {
		super();
	}

	@Override
	public Set<Obs> saveObs(List<Obs> result) {
		return new HashSet<Obs>();
	}

	@Override
	public Set<Obs> getObs(List<Obs> result) {
		return new HashSet<Obs>();
	}

	@Override
	public Set<Obs> purgeObs(List<Obs> result) {
		return new HashSet<Obs>();
	}
}
