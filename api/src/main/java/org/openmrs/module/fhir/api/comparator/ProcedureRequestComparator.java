package org.openmrs.module.fhir.api.comparator;

import org.hl7.fhir.dstu3.model.ProcedureRequest;

import java.util.ArrayList;
import java.util.List;

public class ProcedureRequestComparator extends AbstractPropertyComparator<ProcedureRequest> {
    @Override
    protected boolean areCustomPropsEquals(ProcedureRequest pr1, ProcedureRequest pr2) {
        List<Boolean> result = new ArrayList<>();

        result.add(pr1.getId().equals(pr2.getId()));
        result.add(pr1.getStatus().equals(pr2.getStatus()));
        result.add(pr1.getPriority().equals(pr2.getPriority()));
        result.add(areBasePropsEquals(pr1.getSubject(), pr2.getSubject()));
        result.add(areBasePropsEquals(pr1.getContext(), pr2.getContext()));
        result.add(areBasePropsEquals(pr1.getRequester(), pr2.getRequester()));
        result.add(areEquals(pr1.getSpecimen(), pr2.getSpecimen()));
        result.add(pr1.getIntent() != null ? 
                pr1.getIntent().equals(pr2.getIntent()) 
                : areBothNull(pr1.getIntent(), pr2.getIntent()));
        result.add(pr1.getPriority() != null ?
                pr1.getPriority().equals(pr2.getPriority())
                : areBothNull(pr1.getPriority(), pr2.getPriority()));
        result.add(areBasePropsEquals(pr1.getContext(), pr2.getContext()));

        return areValuesOnlyTrue(result);
    }
}
