package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.BooleanType;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Extension;
import org.hl7.fhir.dstu3.model.PrimitiveType;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;
import org.openmrs.User;
import org.openmrs.api.context.Context;

import java.util.Date;

public final class BaseOpenMRSDataUtil {

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

    private BaseOpenMRSDataUtil() { }

    public static void setBaseExtensionFields(DomainResource fhirResource, BaseOpenmrsData openmrsData) {
        fhirResource.addExtension(createDateCreatedExtension(openmrsData.getDateCreated()));
        fhirResource.addExtension(createCreatorExtension(openmrsData.getCreator()));

        if (openmrsData.getDateChanged() != null) {
            fhirResource.addExtension(createDateChangedExtension(openmrsData.getDateChanged()));
            fhirResource.addExtension(createChangedByExtension(openmrsData.getChangedBy()));
        }
        if (openmrsData.getVoided()) {
            fhirResource.addExtension(createVoidedExtension(openmrsData.getVoided()));
            fhirResource.addExtension(createDateVoidedExtension(openmrsData.getDateVoided()));
            fhirResource.addExtension(createVoidedByExtension(openmrsData.getVoidedBy()));
            fhirResource.addExtension(createVoidReasonExtension(openmrsData.getVoidReason()));
        }
    }
    
    public static void setBaseExtensionFields(DomainResource fhirResource, BaseOpenmrsMetadata openmrsMetadata) {
        fhirResource.addExtension(createDateCreatedExtension(openmrsMetadata.getDateCreated()));
        fhirResource.addExtension(createCreatorExtension(openmrsMetadata.getCreator()));

        if (openmrsMetadata.getDateChanged() != null) {
            fhirResource.addExtension(createDateChangedExtension(openmrsMetadata.getDateChanged()));
            fhirResource.addExtension(createChangedByExtension(openmrsMetadata.getChangedBy()));
        }
        if (openmrsMetadata.getRetired()) {
            fhirResource.addExtension(createRetiredExtension(openmrsMetadata.getRetired()));
            fhirResource.addExtension(createDateRetiredExtension(openmrsMetadata.getDateRetired()));
            fhirResource.addExtension(createRetiredByExtension(openmrsMetadata.getRetiredBy()));
            fhirResource.addExtension(createRetireReasonExtension(openmrsMetadata.getRetireReason()));
        }
    }

    public static void readBaseExtensionFields(BaseOpenmrsData openmrsData, DomainResource fhirResource) {
        for (Extension extension : fhirResource.getExtension()) {
            setBaseOpenMRSData(openmrsData, extension);
        }
    }

    public static void readBaseExtensionFields(BaseOpenmrsMetadata openmrsMetadata, DomainResource fhirResource) {
        for (Extension extension : fhirResource.getExtension()) {
            setBaseOpenMRSMetadata(openmrsMetadata, extension);
        }
    }

    //region read extensions

    private static void setBaseOpenMRSData(BaseOpenmrsData openMRSData, Extension extension) {
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

    private static void setBaseOpenMRSMetadata(BaseOpenmrsMetadata openmrsMetadata, Extension extension) {
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

    private static Date getDateValueFromExtension(Extension extension) {
        if (extension.getValue() instanceof DateTimeType) {
            DateTimeType dateTimeValue = (DateTimeType) extension.getValue();
            return dateTimeValue.getValue();
        }
        return null;
    }

    private static String getStringFromExtension(Extension extension) {
        if (extension.getValue() instanceof StringType) {
            StringType string = (StringType) extension.getValue();
            return string.getValue();
        }
        return null;
    }

    private static boolean getBooleanFromExtension(Extension extension) {
        if (extension.getValue() instanceof BooleanType) {
            BooleanType booleanType = (BooleanType) extension.getValue();
            return booleanType.booleanValue();
        }
        return false;
    }

    private static User getUserFromExtension(Extension extension) {
        String userName = getStringFromExtension(extension);
        if (userName != null && !userName.isEmpty()) {
            return Context.getUserService().getUserByUsername(userName);
        }
        return null;
    }
    //endregion

    //region create extensions

    private static Extension createExtension(String url, PrimitiveType data) {
        Extension extension = new Extension();

        extension.setUrl(url);
        extension.setValue(data);

        return extension;
    }

    private static Extension createCreatorExtension(User creator) {
        if (creator == null) {
            return null;
        }
        return createExtension(CREATOR_URL, new StringType(creator.getUsername()));
    }

    private static Extension createDateCreatedExtension(Date dateCreated) {
        return createExtension(DATE_CREATED_URL, new DateTimeType(dateCreated));
    }

    private static Extension createDateChangedExtension(Date dateChanged) {
        return createExtension(DATE_CHANGED_URL, new DateTimeType(dateChanged));
    }

    private static Extension createChangedByExtension(User user) {
        if (user == null) {
            return null;
        }
        return createExtension(CHANGED_BY_URL, new StringType(user.getUsername()));
    }

    private static Extension createVoidedExtension(boolean voided) {
        return createExtension(VOIDED_URL, new BooleanType(voided));
    }

    private static Extension createVoidedByExtension(User user) {
        if (user == null) {
            return null;
        }
        return createExtension(VOIDED_BY_URL, new StringType(user.getUsername()));
    }

    private static Extension createDateVoidedExtension(Date dateVoided) {
        return createExtension(DATE_VOIDED_URL, new DateTimeType(dateVoided));
    }

    private static Extension createVoidReasonExtension(String reason) {
        return createExtension(VOID_REASON_URL, new StringType(reason));
    }

    private static Extension createRetiredExtension(boolean retired) {
        return createExtension(RETIRED_URL, new BooleanType(retired));
    }

    private static Extension createRetiredByExtension(User user) {
        if (user == null) {
            return null;
        }
        return createExtension(RETIRED_BY_URL, new StringType(user.getUsername()));
    }

    private static Extension createDateRetiredExtension(Date dateRetired) {
        return createExtension(DATE_RETIRED_URL, new DateTimeType(dateRetired));
    }

    private static Extension createRetireReasonExtension(String reason) {
        return createExtension(RETIRE_REASON_URL, new StringType(reason));
    }
    //endregion
}
