package org.openmrs.module.fhir.api.strategies.medicationrequest;

import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.openmrs.CareSetting;
import org.openmrs.Drug;
import org.openmrs.DrugOrder;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.util.ErrorUtil;
import org.openmrs.module.fhir.api.util.FHIRConstants;
import org.openmrs.module.fhir.api.util.FHIRMedicationRequestUtil;
import org.openmrs.module.fhir.api.util.FHIRUtils;
import org.openmrs.module.fhir.api.util.StrategyUtil;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("DefaultMedicationRequestStrategy")
public class MedicationRequestStrategy implements GenericMedicationRequestStrategy {

	@Override
	public MedicationRequest getMedicationRequestById(String uuid) {
		DrugOrder drugOrder = (DrugOrder) Context.getOrderService().getOrderByUuid(uuid);
		if (drugOrder == null || drugOrder.isVoided()) {
			return null;
		}
		return FHIRMedicationRequestUtil.generateMedicationRequest(drugOrder);
	}

	@Override
	public List<MedicationRequest> searchMedicationRequestById(String uuid) {
		DrugOrder drugOrder = (DrugOrder) Context.getOrderService().getOrderByUuid(uuid);
		List<MedicationRequest> medicationRequests = new ArrayList<>();
		if (drugOrder != null) {
			medicationRequests.add(FHIRMedicationRequestUtil.generateMedicationRequest(drugOrder));
		}
		return medicationRequests;
	}

	@Override
	public List<MedicationRequest> searchMedicationRequestByPatientId(String patientUuid) {
		Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		List<MedicationRequest> medicationRequests = new ArrayList<>();
		if (patient != null) {
			List<Order> orders = Context.getOrderService().getAllOrdersByPatient(patient);
			for (Order order : orders) {
				if (order instanceof DrugOrder) {
					DrugOrder drugOrder = (DrugOrder) order;
					medicationRequests.add(FHIRMedicationRequestUtil.generateMedicationRequest(drugOrder));
				}
			}
		}
		return medicationRequests;
	}

	@Override
	public void deleteMedicationRequest(String uuid) {
		Order drugOrder = Context.getOrderService().getOrderByUuid(uuid);
		Context.getOrderService().voidOrder(drugOrder, FHIRConstants.FHIR_VOIDED_MESSAGE);
	}

	@Override
	public MedicationRequest createFHIRMedicationRequest(MedicationRequest medicationRequest) {
		List<String> errors = new ArrayList<>();
		DrugOrder drugOrder = FHIRMedicationRequestUtil.generateDrugOrder(medicationRequest, errors);
		FHIRUtils.checkGeneratorErrorList(errors);
		CareSetting careSetting = Context.getOrderService().getCareSetting(2);
		drugOrder.setCareSetting(careSetting);

		if (!StringUtils.isEmpty(drugOrder.getDrug().getUuid())) {
			Drug drug = Context.getConceptService().getDrugByUuid(drugOrder.getDrug().getUuid());
			if (drug == null) {
				Context.getConceptService().saveDrug(drugOrder.getDrug());
			}
		}

		drugOrder = (DrugOrder) Context.getOrderService().saveOrder(drugOrder, null);
		return FHIRMedicationRequestUtil.generateMedicationRequest(drugOrder);
	}

	@Override
	public MedicationRequest updateFHIRMedicationRequest(MedicationRequest medicationRequest, String uuid) {
		List<String> errors = new ArrayList<>();
		DrugOrder incomingDrugOrder = FHIRMedicationRequestUtil.generateDrugOrder(medicationRequest, errors);
		DrugOrder generatedDrugOrder = (DrugOrder) Context.getOrderService().getOrderByUuid(uuid);
		FHIRMedicationRequestUtil.copyObsAttributes(incomingDrugOrder, generatedDrugOrder, errors);
		if (generatedDrugOrder != null) { //medication request update
			if (!errors.isEmpty()) {
				String errorMessage = ErrorUtil.generateErrorMessage(errors, FHIRConstants.REQUEST_ISSUE_LIST);
				throw new UnprocessableEntityException(errorMessage);
			}

			incomingDrugOrder = (DrugOrder) Context.getOrderService().saveOrder(generatedDrugOrder, null);
			return FHIRMedicationRequestUtil.generateMedicationRequest(incomingDrugOrder);
		} else {
			StrategyUtil.setIdIfNeeded(medicationRequest, uuid);
			return createFHIRMedicationRequest(medicationRequest);
		}
	}
}
