package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.DomainResource;
import org.hl7.fhir.dstu3.model.Element;
import org.hl7.fhir.dstu3.model.Extension;
import org.openmrs.BaseOpenmrsData;
import org.openmrs.BaseOpenmrsMetadata;

public final class BaseOpenMRSDataUtil {

    private BaseOpenMRSDataUtil() { }

    public static void setBaseExtensionFields(DomainResource fhirResource, BaseOpenmrsData openmrsData) {
        fhirResource.addExtension(ExtensionsUtil.createDateCreatedExtension(openmrsData.getDateCreated()));
        fhirResource.addExtension(ExtensionsUtil.createCreatorExtension(openmrsData.getCreator()));

        if (openmrsData.getDateChanged() != null) {
            fhirResource.addExtension(ExtensionsUtil.createDateChangedExtension(openmrsData.getDateChanged()));
            fhirResource.addExtension(ExtensionsUtil.createChangedByExtension(openmrsData.getChangedBy()));
        }
        if (openmrsData.getVoided()) {
            fhirResource.addExtension(ExtensionsUtil.createVoidedExtension(openmrsData.getVoided()));
            fhirResource.addExtension(ExtensionsUtil.createDateVoidedExtension(openmrsData.getDateVoided()));
            fhirResource.addExtension(ExtensionsUtil.createVoidedByExtension(openmrsData.getVoidedBy()));
            fhirResource.addExtension(ExtensionsUtil.createVoidReasonExtension(openmrsData.getVoidReason()));
        }
    }

    public static void setBaseExtensionFields(Element element, BaseOpenmrsData openmrsData) {
        element.addExtension(ExtensionsUtil.createDateCreatedExtension(openmrsData.getDateCreated()));
        element.addExtension(ExtensionsUtil.createCreatorExtension(openmrsData.getCreator()));

        if (openmrsData.getDateChanged() != null) {
            element.addExtension(ExtensionsUtil.createDateChangedExtension(openmrsData.getDateChanged()));
            element.addExtension(ExtensionsUtil.createChangedByExtension(openmrsData.getChangedBy()));
        }
        if (openmrsData.getVoided()) {
            element.addExtension(ExtensionsUtil.createVoidedExtension(openmrsData.getVoided()));
            element.addExtension(ExtensionsUtil.createDateVoidedExtension(openmrsData.getDateVoided()));
            element.addExtension(ExtensionsUtil.createVoidedByExtension(openmrsData.getVoidedBy()));
            element.addExtension(ExtensionsUtil.createVoidReasonExtension(openmrsData.getVoidReason()));
        }
    }
    
    public static void setBaseExtensionFields(DomainResource fhirResource, BaseOpenmrsMetadata openmrsMetadata) {
        fhirResource.addExtension(ExtensionsUtil.createDateCreatedExtension(openmrsMetadata.getDateCreated()));
        fhirResource.addExtension(ExtensionsUtil.createCreatorExtension(openmrsMetadata.getCreator()));

        if (openmrsMetadata.getDateChanged() != null) {
            fhirResource.addExtension(ExtensionsUtil.createDateChangedExtension(openmrsMetadata.getDateChanged()));
            fhirResource.addExtension(ExtensionsUtil.createChangedByExtension(openmrsMetadata.getChangedBy()));
        }
        if (openmrsMetadata.getRetired()) {
            fhirResource.addExtension(ExtensionsUtil.createRetiredExtension(openmrsMetadata.getRetired()));
            fhirResource.addExtension(ExtensionsUtil.createDateRetiredExtension(openmrsMetadata.getDateRetired()));
            fhirResource.addExtension(ExtensionsUtil.createRetiredByExtension(openmrsMetadata.getRetiredBy()));
            fhirResource.addExtension(ExtensionsUtil.createRetireReasonExtension(openmrsMetadata.getRetireReason()));
        }
    }

    public static void readBaseExtensionFields(BaseOpenmrsData openmrsData, DomainResource fhirResource) {
        for (Extension extension : fhirResource.getExtension()) {
            ExtensionsUtil.setBaseOpenMRSData(openmrsData, extension);
        }
    }

    public static void readBaseExtensionFields(BaseOpenmrsData openmrsData, Element fhirResource) {
        for (Extension extension : fhirResource.getExtension()) {
            ExtensionsUtil.setBaseOpenMRSData(openmrsData, extension);
        }
    }

    public static void readBaseExtensionFields(BaseOpenmrsMetadata openmrsMetadata, DomainResource fhirResource) {
        for (Extension extension : fhirResource.getExtension()) {
            ExtensionsUtil.setBaseOpenMRSMetadata(openmrsMetadata, extension);
        }
    }
}
