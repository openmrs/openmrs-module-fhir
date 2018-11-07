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
import org.openmrs.User;
import org.openmrs.api.context.Context;

import java.util.Date;

public final class ExtensionsUtil {

	/** https://simplifier.net/SRxProject/resource-date-created/~json */
	private static final String DATE_CREATED_URL = "http://fhir-es.transcendinsights.com/stu3/StructureDefinition/resource-date-created";

	/** https://simplifier.net/eLabTest/Creator-crew-version1/~json */
	private static final String CREATOR_URL = "https://purl.org/elab/fhir/StructureDefinition/Creator-crew-version1";

	// Local extensions
	private static final String CHANGED_BY_URL = "changedBy";
	private static final String DATE_CHANGED_URL = "dateChanged";
	private static final String VOIDED_URL = "voided";
	private static final String DATE_VOIDED_URL = "dateVoided";
	private static final String VOIDED_BY_URL = "voidedBy";
	private static final String VOID_REASON_URL = "voidReason";
	private static final String RETIRED_URL = "retired";
	private static final String DATE_RETIRED_URL = "dateRetired";
	private static final String RETIRED_BY_URL = "retiredBy";
	private static final String RETIRE_REASON_URL = "retireReason";

	public static final String DESCRIPTION_URL = "description";

	public static final String AS_NEEDED_CONDITION = "asNeededCondition";
	public static final String DOSING_TYPE = "dosingType";
	public static final String NUM_REFILLS = "numRefills";
	public static final String BRAND_NAME = "brandName";
	public static final String DISPENSE_AS_WRITTEN = "dispenseAsWritten";
	public static final String DRUG_NON_CODED = "drugNonCoded";
	public static final String CARE_SETTING = "careSetting";

	private ExtensionsUtil() { }

	public static void setBaseOpenMRSData(BaseOpenmrsData openMRSData, Extension extension) {
		switch (extension.getUrl()) {
			case DATE_CREATED_URL:
				openMRSData.setDateCreated(getDateValueFromExtension(extension));
				break;
			case CREATOR_URL:
				openMRSData.setCreator(getUserFromExtension(extension));
				break;
			case CHANGED_BY_URL:
				openMRSData.setChangedBy(getUserFromExtension(extension));
				break;
			case DATE_CHANGED_URL:
				openMRSData.setDateChanged(getDateValueFromExtension(extension));
				break;
			case VOIDED_URL:
				openMRSData.setVoided(getBooleanFromExtension(extension));
				break;
			case DATE_VOIDED_URL:
				openMRSData.setDateVoided(getDateValueFromExtension(extension));
				break;
			case VOIDED_BY_URL:
				openMRSData.setVoidedBy(getUserFromExtension(extension));
				break;
			case VOID_REASON_URL:
				openMRSData.setVoidReason(getStringFromExtension(extension));
				break;
			default:
				break;
		}
	}

	public static void setBaseOpenMRSMetadata(BaseOpenmrsMetadata openmrsMetadata, Extension extension) {
		switch (extension.getUrl()) {
			case DATE_CREATED_URL:
				openmrsMetadata.setDateCreated(getDateValueFromExtension(extension));
				break;
			case CREATOR_URL:
				openmrsMetadata.setCreator(getUserFromExtension(extension));
				break;
			case CHANGED_BY_URL:
				openmrsMetadata.setChangedBy(getUserFromExtension(extension));
				break;
			case DATE_CHANGED_URL:
				openmrsMetadata.setDateChanged(getDateValueFromExtension(extension));
				break;
			case RETIRED_URL:
				openmrsMetadata.setRetired(getBooleanFromExtension(extension));
				break;
			case DATE_RETIRED_URL:
				openmrsMetadata.setDateRetired(getDateValueFromExtension(extension));
				break;
			case RETIRED_BY_URL:
				openmrsMetadata.setRetiredBy(getUserFromExtension(extension));
				break;
			case RETIRE_REASON_URL:
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
		return createExtension(CREATOR_URL, new StringType(creator.getUsername()));
	}

	public static Extension createDateCreatedExtension(Date dateCreated) {
		return createExtension(DATE_CREATED_URL, new DateTimeType(dateCreated));
	}

	public static Extension createDateChangedExtension(Date dateChanged) {
		return createExtension(DATE_CHANGED_URL, new DateTimeType(dateChanged));
	}

	public static Extension createChangedByExtension(User user) {
		if (user == null) {
			return null;
		}
		return createExtension(CHANGED_BY_URL, new StringType(user.getUsername()));
	}

	public static Extension createVoidedExtension(boolean voided) {
		return createExtension(VOIDED_URL, new BooleanType(voided));
	}

	public static Extension createVoidedByExtension(User user) {
		if (user == null) {
			return null;
		}
		return createExtension(VOIDED_BY_URL, new StringType(user.getUsername()));
	}

	public static Extension createDateVoidedExtension(Date dateVoided) {
		return createExtension(DATE_VOIDED_URL, new DateTimeType(dateVoided));
	}

	public static Extension createVoidReasonExtension(String reason) {
		return createExtension(VOID_REASON_URL, new StringType(reason));
	}

	public static Extension createRetiredExtension(boolean retired) {
		return createExtension(RETIRED_URL, new BooleanType(retired));
	}

	public static Extension createRetiredByExtension(User user) {
		if (user == null) {
			return null;
		}
		return createExtension(RETIRED_BY_URL, new StringType(user.getUsername()));
	}

	public static Extension createDateRetiredExtension(Date dateRetired) {
		return createExtension(DATE_RETIRED_URL, new DateTimeType(dateRetired));
	}

	public static Extension createRetireReasonExtension(String reason) {
		return createExtension(RETIRE_REASON_URL, new StringType(reason));
	}

	public static Extension createDescriptionExtension(String description) {
		return createExtension(DESCRIPTION_URL, new StringType(description));
	}

	public static Extension createAsNeededConditionExtension(String value) {
		return createExtension(AS_NEEDED_CONDITION, new StringType(value));
	}

	public static Extension createDosingTypeExtension(String value) {
		return createExtension(DOSING_TYPE, new StringType(value));
	}

	public static Extension createNumRefillsExtension(Integer value) {
		return createExtension(NUM_REFILLS, new IntegerType(value));
	}

	public static Extension createBrandNameExtension(String value) {
		return createExtension(BRAND_NAME, new StringType(value));
	}

	public static Extension createDispenseAsWrittenExtension(Boolean value) {
		return createExtension(DISPENSE_AS_WRITTEN, new BooleanType(value));
	}

	public static Extension createDrugNonCodedExtension(String value) {
		return createExtension(DRUG_NON_CODED, new StringType(value));
	}

	public static Extension createCareSettingExtension(String value) {
		return createExtension(CARE_SETTING, new StringType(value));
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
