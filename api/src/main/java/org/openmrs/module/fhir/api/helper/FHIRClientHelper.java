package org.openmrs.module.fhir.api.helper;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hl7.fhir.dstu3.model.AllergyIntolerance;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Group;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.MedicationRequest;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Person;
import org.hl7.fhir.dstu3.model.Practitioner;
import org.hl7.fhir.dstu3.model.ProcedureRequest;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.openmrs.module.fhir.api.client.BasicAuthInterceptor;
import org.openmrs.module.fhir.api.client.BasicHttpRequestInterceptor;
import org.openmrs.module.fhir.api.client.ClientHttpEntity;
import org.openmrs.module.fhir.api.client.ClientHttpRequestInterceptor;
import org.openmrs.module.fhir.api.client.FHIRHttpMessageConverter;
import org.openmrs.module.fhir.api.exceptions.FHIRException;
import org.openmrs.module.fhir.api.util.ContextUtil;
import org.openmrs.module.fhir.api.util.ErrorUtil;
import org.openmrs.module.fhir.api.util.FHIRAllergyIntoleranceUtil;
import org.openmrs.module.fhir.api.util.FHIREncounterUtil;
import org.openmrs.module.fhir.api.util.FHIRGroupUtil;
import org.openmrs.module.fhir.api.util.FHIRLocationUtil;
import org.openmrs.module.fhir.api.util.FHIRMedicationRequestUtil;
import org.openmrs.module.fhir.api.util.FHIRObsUtil;
import org.openmrs.module.fhir.api.util.FHIRPatientUtil;
import org.openmrs.module.fhir.api.util.FHIRPersonUtil;
import org.openmrs.module.fhir.api.util.FHIRProcedureRequestUtil;
import org.openmrs.module.fhir.api.util.FHIRVisitUtil;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

import javax.transaction.NotSupportedException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.openmrs.module.fhir.api.util.FHIRConstants.CATEGORY_ALLERGY;
import static org.openmrs.module.fhir.api.util.FHIRConstants.CATEGORY_COHORT;
import static org.openmrs.module.fhir.api.util.FHIRConstants.CATEGORY_CONDITION;
import static org.openmrs.module.fhir.api.util.FHIRConstants.CATEGORY_DRUG_ORDER;
import static org.openmrs.module.fhir.api.util.FHIRConstants.CATEGORY_ENCOUNTER;
import static org.openmrs.module.fhir.api.util.FHIRConstants.CATEGORY_LOCATION;
import static org.openmrs.module.fhir.api.util.FHIRConstants.CATEGORY_OBSERVATION;
import static org.openmrs.module.fhir.api.util.FHIRConstants.CATEGORY_PATIENT;
import static org.openmrs.module.fhir.api.util.FHIRConstants.CATEGORY_PERSON;
import static org.openmrs.module.fhir.api.util.FHIRConstants.CATEGORY_PRACTITIONER;
import static org.openmrs.module.fhir.api.util.FHIRConstants.CATEGORY_PROVIDER;
import static org.openmrs.module.fhir.api.util.FHIRConstants.CATEGORY_TEST_ORDER;
import static org.openmrs.module.fhir.api.util.FHIRConstants.CATEGORY_VISIT;

public class FHIRClientHelper implements ClientHelper {

	private static final Map<String, Class> CATEGORY_MAP;

	static {
		CATEGORY_MAP = new HashMap<>();
		CATEGORY_MAP.put(CATEGORY_PATIENT, Patient.class);
		CATEGORY_MAP.put(CATEGORY_VISIT, Encounter.class);
		CATEGORY_MAP.put(CATEGORY_ENCOUNTER, Encounter.class);
		CATEGORY_MAP.put(CATEGORY_OBSERVATION, Observation.class);
		CATEGORY_MAP.put(CATEGORY_LOCATION, Location.class);
		CATEGORY_MAP.put(CATEGORY_PRACTITIONER, Practitioner.class);
		CATEGORY_MAP.put(CATEGORY_PROVIDER, Practitioner.class);
		CATEGORY_MAP.put(CATEGORY_ALLERGY, AllergyIntolerance.class);
		CATEGORY_MAP.put(CATEGORY_PERSON, Person.class);
		CATEGORY_MAP.put(CATEGORY_COHORT, Group.class);
		CATEGORY_MAP.put(CATEGORY_DRUG_ORDER, MedicationRequest.class);
		CATEGORY_MAP.put(CATEGORY_TEST_ORDER, ProcedureRequest.class);
		CATEGORY_MAP.put(CATEGORY_CONDITION, Condition.class);
	}

	private final IParser parser;

	protected final Log log = LogFactory.getLog(this.getClass());

	public FHIRClientHelper() {
		parser = FhirContext.forDstu3().newJsonParser();
	}

	@Override
	public ClientHttpEntity retrieveRequest(String url) throws URISyntaxException {
		return new ClientHttpEntity(HttpMethod.GET, new URI(url));
	}

	@Override
	public ClientHttpEntity createRequest(String url, Object object) throws URISyntaxException {
		url = createUrl(url, (IBaseResource) object);
		return new ClientHttpEntity<String>(parser.encodeResourceToString((IBaseResource) object), HttpMethod.PUT, new URI(url));
	}

	@Override
	public ClientHttpEntity deleteRequest(String url, String uuid) throws URISyntaxException {
		url = url + "/" + uuid;
		return new ClientHttpEntity<String>(uuid, HttpMethod.DELETE, new URI(url));
	}

	@Override
	public ClientHttpEntity updateRequest(String url, Object object) throws URISyntaxException {
		url = createUrl(url, (IBaseResource) object);
		return new ClientHttpEntity<String>(parser.encodeResourceToString((IBaseResource) object), HttpMethod.PUT, new URI(url));
	}

	@Override
	public Class resolveClassByCategory(String category) {
		if (CATEGORY_MAP.containsKey(category)) {
			return CATEGORY_MAP.get(category);
		}
		throw new FHIRException(String.format("Category %s not recognized", category));
	}

	@Override
	public List<ClientHttpRequestInterceptor> getCustomInterceptors(String username, String password) {
		return Arrays.asList(new BasicAuthInterceptor(username, password),
				new BasicHttpRequestInterceptor("Accept", "application/json"));
	}

	@Override
	public List<HttpMessageConverter<?>> getCustomMessageConverter() {
		return Arrays.asList(new HttpMessageConverter<?>[]
				{ new FHIRHttpMessageConverter(), new StringHttpMessageConverter() });
	}

	@Override
	public boolean compareResourceObjects(String category, Object from, Object dest) {
		boolean result;
		switch (category) {
			case CATEGORY_PATIENT:
				result = FHIRPatientUtil.compareCurrentPatients(dest, from);
				break;
			case CATEGORY_ENCOUNTER:
				result = FHIREncounterUtil.compareCurrentEncounters(dest, from);
				break;
			case CATEGORY_VISIT:
				result = FHIREncounterUtil.compareCurrentEncounters(dest, from);
				break;
			case CATEGORY_OBSERVATION:
				result = FHIRObsUtil.compareCurrentObs(dest, from);
				break;
			case CATEGORY_ALLERGY:
				result = FHIRAllergyIntoleranceUtil.areAllergiesEquals(dest, from);
				break;
			case CATEGORY_PERSON:
				result = FHIRPersonUtil.arePersonsEquals(dest, from);
				break;
			case CATEGORY_COHORT:
				result = FHIRGroupUtil.areGroupsEquals(dest, from);
				break;
			case CATEGORY_DRUG_ORDER:
				result = FHIRMedicationRequestUtil.areMedicationRequestsEquals(dest, from);
				break;
			case CATEGORY_TEST_ORDER:
				result = FHIRProcedureRequestUtil.areProcedureRequestsEqual(dest, from);
				break;
			default:
				result = dest.equals(from);
		}
		return result;
	}

	@Override
	public Object convertToObject(String formattedData, Class<?> clazz) {
		return parser.parseResource(formattedData);
	}

	@Override
	public String convertToFormattedData(Object object) {
		return parser.encodeResourceToString((IBaseResource) object);
	}

	@Override
	public Object convertToOpenMrsObject(Object object, String category) throws NotSupportedException {
		List<String> errors = new ArrayList<>();
		Object result;
		switch (category) {
			case CATEGORY_LOCATION:
				result = FHIRLocationUtil.generateOpenMRSLocation((Location) object, errors);
				break;
			case CATEGORY_OBSERVATION:
				result = FHIRObsUtil.generateOpenMRSObs((Observation) object, errors);
				break;
			case CATEGORY_ENCOUNTER:
				result = FHIREncounterUtil.generateOMRSEncounter((Encounter) object, errors);
				break;
			case CATEGORY_VISIT:
				result = FHIRVisitUtil.generateOMRSVisit((Encounter) object, errors);
				break;
			case CATEGORY_DRUG_ORDER:
				result = FHIRMedicationRequestUtil.generateDrugOrder((MedicationRequest) object,
						errors);
				break;
			case CATEGORY_TEST_ORDER:
				result = FHIRProcedureRequestUtil.generateTestOrder((ProcedureRequest) object,
						errors);
				break;
			case CATEGORY_PERSON:
				result = FHIRPersonUtil.generateOpenMRSPerson((Person) object, errors);
				break;
			case CATEGORY_PATIENT:
				result = FHIRPatientUtil.generateOmrsPatient((Patient) object, errors);
				break;
			case CATEGORY_COHORT:
				result = FHIRGroupUtil.generateCohort((Group) object);
				break;
			case CATEGORY_ALLERGY:
				result = ContextUtil.getAllergyHelper().generateAllergy(object);
				break;
			case CATEGORY_CONDITION:
				result = ContextUtil.getConditionHelper().generateOpenMrsCondition((Condition) object);
				break;
			default:
				throw new NotSupportedException(String.format("Category %s not supported.", category));
		}
		ErrorUtil.checkErrors(errors);
		return result;
	}

	private String createUrl(String url, IBaseResource object) {
		return url + "/" + object.getIdElement().getIdPart();
	}
}
