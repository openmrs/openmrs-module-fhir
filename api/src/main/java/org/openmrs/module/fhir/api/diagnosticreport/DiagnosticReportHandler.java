package org.openmrs.module.fhir.api.diagnosticreport;

import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import org.openmrs.Obs;

import java.util.List;
import java.util.Set;

public interface DiagnosticReportHandler {

	public String getId();

	public Set<Obs> saveObs(List<Obs> result);

	public Set<Obs> getObs(List<Obs> result);

	public Set<Obs> purgeObs(List<Obs> result);

}
