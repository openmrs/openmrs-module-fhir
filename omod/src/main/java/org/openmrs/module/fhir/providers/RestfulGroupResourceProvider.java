package org.openmrs.module.fhir.providers;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.Delete;
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
import org.hl7.fhir.dstu3.model.Group;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.openmrs.module.fhir.resources.FHIRGroupResource;
import org.openmrs.module.fhir.util.MethodOutcomeBuilder;

import java.util.List;

public class RestfulGroupResourceProvider implements IResourceProvider {

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
		return MethodOutcomeBuilder.buildCreate(groupResource.createGroup(group));
	}

	@Update
	public MethodOutcome updateGroup(@ResourceParam Group group, @IdParam IdType id) {
		return MethodOutcomeBuilder.buildUpdate(groupResource.updateGroup(group, id.getIdPart()));
	}

	@Delete
	public void deleteGroup(@IdParam IdType id) {
		groupResource.deleteGroup(id);
	}

	@Read
	public Group getResourceById(@IdParam IdType id) {
		return groupResource.getByUniqueId(id);
	}

	@Search
	public List<Group> findGroupsById(
			@RequiredParam(name = Group.SP_RES_ID) TokenParam id) {
		return groupResource.searchGroupById(id);
	}

	@Search
	public List<Group> findGroupsByName(
			@RequiredParam(name = "name") StringParam name) {
		return groupResource.searchGroupByName(name);
	}
}
