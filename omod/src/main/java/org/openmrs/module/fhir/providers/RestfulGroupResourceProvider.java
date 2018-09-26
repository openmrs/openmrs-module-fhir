package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.annotation.Update;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.IResourceProvider;
import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Coding;
import org.hl7.fhir.dstu3.model.Group;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.openmrs.module.fhir.resources.FHIRGroupResource;

import java.util.List;

public class RestfulGroupResourceProvider implements IResourceProvider {

    private static final String GROUP_CREATE_SUCCESS = "Group successfully created with id %s";
    private static final String GROUP_UPDATE_SUCCESS = "Group successfully updated with id %s";

    private FHIRGroupResource groupResource;

    public RestfulGroupResourceProvider() {
        groupResource = new FHIRGroupResource();
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Group.class;
    }

    @Create
    public MethodOutcome createGroup(@ResourceParam Group group) {
        Group createdGroup = groupResource.createGroup(group);
        return createMethodOutcome(createdGroup.getId(), GROUP_CREATE_SUCCESS);
    }

    @Update()
    public MethodOutcome updateGroup(@ResourceParam Group group, @IdParam IdType id) {
        Group updatedGroup = groupResource.updateGroup(group, id.getIdPart());
        return createMethodOutcome(updatedGroup.getId(), GROUP_UPDATE_SUCCESS);
    }

    @Read()
    public Group getResourceById(@IdParam IdType id) {
        return groupResource.getByUniqueId(id);
    }

    @Search()
    public List<Group> searchGroupsById(
            @RequiredParam(name = Group.SP_RES_ID)TokenParam id) {
        return groupResource.searchGroupById(id);
    }

    @Search()
    public List<Group> searchGroupsByName(
            @RequiredParam(name = "name") StringParam name) {
        return groupResource.searchGroupByName(name);
    }

    private MethodOutcome createMethodOutcome(String resourceId, String messagePattern) {
        MethodOutcome retVal = new MethodOutcome();
        retVal.setId(new IdType(Group.class.getSimpleName(), resourceId));
        OperationOutcome outcome = new OperationOutcome();
        CodeableConcept concept = new CodeableConcept();
        Coding coding = concept.addCoding();
        coding.setDisplay(String.format(messagePattern, resourceId));
        outcome.addIssue().setDetails(concept);
        retVal.setOperationOutcome(outcome);
        return retVal;
    }
}
