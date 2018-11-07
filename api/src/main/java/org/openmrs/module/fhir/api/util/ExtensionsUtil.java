package org.openmrs.module.fhir.api.util;

import org.apache.commons.lang.StringUtils;
import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.IntegerType;
import org.hl7.fhir.dstu3.model.PrimitiveType;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.Concept;
import org.openmrs.OrderFrequency;
import org.openmrs.TestOrder;
import org.openmrs.User;
import org.openmrs.api.context.Context;
import org.openmrs.module.fhir.api.constants.ExtensionURL;

import java.util.Date;

public final class ExtensionsUtil {

	private ExtensionsUtil() { }

	public static void setBaseOpenMRSData(BaseOpenmrsData openMRSData, Extension extension) {
		switch (extension.getUrl()) {
			case ExtensionURL.DATE_CREATED_URL:
				openMRSData.setDateCreated(getDateValueFromExtension(extension));
				break;
			case ExtensionURL.CREATOR_URL:
				openMRSData.setCreator(getUserFromExtension(extension));
				break;
			case ExtensionURL.CHANGED_BY_URL:
				openMRSData.setChangedBy(getUserFromExtension(extension));
				break;
			case ExtensionURL.DATE_CHANGED_URL:
				openMRSData.setDateChanged(getDateValueFromExtension(extension));
				break;
			case ExtensionURL.VOIDED_URL:
				openMRSData.setVoided(getBooleanFromExtension(extension));
				break;
			case ExtensionURL.DATE_VOIDED_URL:
				openMRSData.setDateVoided(getDateValueFromExtension(extension));
				break;
			case ExtensionURL.VOIDED_BY_URL:
				openMRSData.setVoidedBy(getUserFromExtension(extension));
				break;
			case ExtensionURL.VOID_REASON_URL:
				openMRSData.setVoidReason(getStringFromExtension(extension));
				break;
			default:
				break;
		}
	}

	public static void setBaseOpenMRSMetadata(BaseOpenmrsMetadata openmrsMetadata, Extension extension) {
		switch (extension.getUrl()) {
			case ExtensionURL.DATE_CREATED_URL:
				openmrsMetadata.setDateCreated(getDateValueFromExtension(extension));
				break;
			case ExtensionURL.CREATOR_URL:
				openmrsMetadata.setCreator(getUserFromExtension(extension));
				break;
			case ExtensionURL.CHANGED_BY_URL:
				openmrsMetadata.setChangedBy(getUserFromExtension(extension));
				break;
			case ExtensionURL.DATE_CHANGED_URL:
				openmrsMetadata.setDateChanged(getDateValueFromExtension(extension));
				break;
			case ExtensionURL.RETIRED_URL:
				openmrsMetadata.setRetired(getBooleanFromExtension(extension));
				break;
			case ExtensionURL.DATE_RETIRED_URL:
				openmrsMetadata.setDateRetired(getDateValueFromExtension(extension));
				break;
			case ExtensionURL.RETIRED_BY_URL:
				openmrsMetadata.setRetiredBy(getUserFromExtension(extension));
				break;
			case ExtensionURL.RETIRE_REASON_URL:
				openmrsMetadata.setRetireReason(getStringFromExtension(extension));
				break;
			default:
				break;
		}
	}

	//region create extensions

	public static Extension createCreatorExtension(User creator) {
		if (creator == null) {
			return null;
		}
		return createExtension(ExtensionURL.CREATOR_URL, new StringType(creator.getUsername()));
	}

	public static Extension createDateCreatedExtension(Date dateCreated) {
		return createExtension(ExtensionURL.DATE_CREATED_URL, new DateTimeType(dateCreated));
	}

	public static Extension createDateChangedExtension(Date dateChanged) {
		return createExtension(ExtensionURL.DATE_CHANGED_URL, new DateTimeType(dateChanged));
	}

	public static Extension createChangedByExtension(User user) {
		if (user == null) {
			return null;
		}
		return createExtension(ExtensionURL.CHANGED_BY_URL, new StringType(user.getUsername()));
	}

	public static Extension createVoidedExtension(boolean voided) {
		return createExtension(ExtensionURL.VOIDED_URL, new BooleanType(voided));
	}

	public static Extension createVoidedByExtension(User user) {
		if (user == null) {
			return null;
		}
		return createExtension(ExtensionURL.VOIDED_BY_URL, new StringType(user.getUsername()));
	}

	public static Extension createDateVoidedExtension(Date dateVoided) {
		return createExtension(ExtensionURL.DATE_VOIDED_URL, new DateTimeType(dateVoided));
	}

	public static Extension createVoidReasonExtension(String reason) {
		return createExtension(ExtensionURL.VOID_REASON_URL, new StringType(reason));
	}

	public static Extension createRetiredExtension(boolean retired) {
		return createExtension(ExtensionURL.RETIRED_URL, new BooleanType(retired));
	}

	public static Extension createRetiredByExtension(User user) {
		if (user == null) {
			return null;
		}
		return createExtension(ExtensionURL.RETIRED_BY_URL, new StringType(user.getUsername()));
	}

	public static Extension createDateRetiredExtension(Date dateRetired) {
		return createExtension(ExtensionURL.DATE_RETIRED_URL, new DateTimeType(dateRetired));
	}

	public static Extension createRetireReasonExtension(String reason) {
		return createExtension(ExtensionURL.RETIRE_REASON_URL, new StringType(reason));
	}

	public static Extension createDescriptionExtension(String description) {
		return createExtension(ExtensionURL.DESCRIPTION_URL, new StringType(description));
	}

	public static Extension createAsNeededConditionExtension(String value) {
		return createExtension(ExtensionURL.AS_NEEDED_CONDITION, new StringType(value));
	}

	public static Extension createDosingTypeExtension(String value) {
		return createExtension(ExtensionURL.DOSING_TYPE, new StringType(value));
	}

	public static Extension createNumRefillsExtension(Integer value) {
		return createExtension(ExtensionURL.NUM_REFILLS, new IntegerType(value));
	}

	public static Extension createBrandNameExtension(String value) {
		return createExtension(ExtensionURL.BRAND_NAME, new StringType(value));
	}

	public static Extension createDispenseAsWrittenExtension(Boolean value) {
		return createExtension(ExtensionURL.DISPENSE_AS_WRITTEN, new BooleanType(value));
	}

	public static Extension createDrugNonCodedExtension(String value) {
		return createExtension(ExtensionURL.DRUG_NON_CODED, new StringType(value));
	}

	public static Extension createCareSettingExtension(String value) {
		return createExtension(ExtensionURL.CARE_SETTING, new StringType(value));
	}

	public static Extension createOrderConceptExtension(Concept concept) {
		return createExtension(ExtensionURL.ORDER_CONCEPT_URL, new StringType(concept.getUuid()));
	}

	public static Extension createLateralityExtension(TestOrder.Laterality laterality) {
		return createExtension(ExtensionURL.LATERALITY_URL, new StringType(laterality.toString()));
	}

	public static Extension createClinicalHistoryExtension(String clinicalHistory) {
		return createExtension(ExtensionURL.CLINICAL_HISTORY_URL, new StringType(clinicalHistory));
	}

	public static Extension createOrderFrequencyExtension(OrderFrequency frequency) {
		return createExtension(ExtensionURL.ORDER_FREQUENCY_URL, new StringType(frequency.getUuid()));
	}

	//endregion

	private static Extension createExtension(String url, PrimitiveType data) {
		Extension extension = new Extension();

		extension.setUrl(url);
		extension.setValue(data);

		return extension;
	}

	//region read extensions

	public static Date getDateValueFromExtension(Extension extension) {
		if (extension.getValue() instanceof DateTimeType) {
			DateTimeType dateTimeValue = (DateTimeType) extension.getValue();
			return dateTimeValue.getValue();
		}
		return null;
	}

	public static String getStringFromExtension(Extension extension) {
		if (extension.getValue() instanceof StringType) {
			StringType string = (StringType) extension.getValue();
			return string.getValue();
		}
		return null;
	}

	public static boolean getBooleanFromExtension(Extension extension) {
		if (extension.getValue() instanceof BooleanType) {
			BooleanType booleanType = (BooleanType) extension.getValue();
			return booleanType.booleanValue();
		}
		return false;
	}

	public static Integer getIntegerFromExtension(Extension extension) {
		if (extension.getValue() instanceof IntegerType) {
			return ((IntegerType) extension.getValue()).getValue();
		}
		return null;
	}

	public static User getUserFromExtension(Extension extension) {
		String userName = getStringFromExtension(extension);
		if (StringUtils.isNotEmpty(userName)) {
			return Context.getUserService().getUserByUsername(userName);
		}
		return null;
	}

	//endregion
}
