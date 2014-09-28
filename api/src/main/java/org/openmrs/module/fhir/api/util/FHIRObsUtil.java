package org.openmrs.module.fhir.api.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Bundle;
import ca.uhn.fhir.model.api.BundleEntry;
import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu.composite.*;
import ca.uhn.fhir.model.dstu.resource.Observation;
import ca.uhn.fhir.model.dstu.valueset.ObservationReliabilityEnum;
import ca.uhn.fhir.model.dstu.valueset.ObservationStatusEnum;
import ca.uhn.fhir.model.primitive.*;
import ca.uhn.fhir.narrative.DefaultThymeleafNarrativeGenerator;
import ca.uhn.fhir.parser.IParser;
import org.openmrs.*;
import org.openmrs.api.context.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class FHIRObsUtil {

    public static Observation generateObs(Obs obs) {

        Observation observation = new Observation();
        observation.setId(obs.getUuid());

        InstantDt instant = new InstantDt();
        instant.setValue(obs.getDateCreated());

        observation.setIssued(instant);

        observation.setComments(obs.getComment());

        ResourceReferenceDt patientReference = new ResourceReferenceDt();

        PersonName name = Context.getPatientService().getPatientByUuid(obs.getPerson().getUuid()).getPersonName();
        System.out.println(name.getGivenName());
        String nameDisplay = name.getGivenName() + " " + name.getFamilyName();
        nameDisplay += "(" + Context.getPatientService().getPatientByUuid(obs.getPerson().getUuid()).getPatientIdentifier().getIdentifier() + ")";

        patientReference.setDisplay(nameDisplay);
        String patientUri = Context.getAdministrationService().getGlobalProperty("webservices.rest.uriPrefix")+ "/ws/rest/v1/fhirpatient/" + obs.getPerson().getUuid();

        IdDt patientRef = new IdDt();
        patientRef.setValue(patientUri);
        patientReference.setReference(patientRef);

        observation.setSubject(patientReference);



        List<ResourceReferenceDt> performers = new ArrayList<ResourceReferenceDt>();

        if(obs.getEncounter() != null) {

            for (EncounterProvider provider : obs.getEncounter().getEncounterProviders()) {
                ResourceReferenceDt providerReference = new ResourceReferenceDt();
                providerReference.setDisplay(provider.getProvider().getName() + "(" + provider.getProvider().getProviderId() + ")");
                IdDt providerRef = new IdDt();
                String providerUri = Context.getAdministrationService().getSystemVariables().get("OPENMRS_HOSTNAME") + "/ws/rest/v1/fhirprovider/" + provider.getUuid();

                providerRef.setValue(providerUri);
                providerReference.setReference(providerRef);

                performers.add(providerReference);
            }
        }

        observation.setPerformer(performers);

        Collection<ConceptMap> mappings = obs.getConcept().getConceptMappings();
        CodeableConceptDt dt = observation.getName();
        List<CodingDt> dts = new ArrayList<CodingDt>();

        for (ConceptMap map : mappings) {

            String display = map.getConceptReferenceTerm().getName();
            if(display == null)
                display = map.getConceptReferenceTerm().getUuid();

            System.out.println(map.getSource());
            System.out.println(map.getSource().getName());
            if (map.getSource().getName().equals("LOINC"))
                dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(Constants.loinc));
            if (map.getSource().getName().equals("SNOMED"))
                dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(Constants.snomed));
            if (map.getSource().getName().equals("CIEL"))
                dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(Constants.ciel));

            dt.setCoding(dts);
        }

        if (obs.getConcept().isNumeric()) {
            ConceptNumeric cn = Context.getConceptService().getConceptNumeric(obs.getConcept().getId());

            QuantityDt q = new QuantityDt();

            q.setValue(obs.getValueNumeric());
            q.setSystem("http://unitsofmeasure.org");
            q.setUnits(cn.getUnits());
            q.setCode(cn.getUnits());


            observation.setValue(q);

        }

        if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("ST")) {
            StringDt value = new StringDt();
            value.setValue(obs.getValueAsString(Context.getLocale()));
            observation.setValue(value);

        }

        if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("BIT")) {
            CodeableConceptDt codeableConceptDt = new CodeableConceptDt();

            List<CodingDt> codingDts = new ArrayList<CodingDt>();
            CodingDt codingDt = new CodingDt();

            codingDts.add(codingDt);

            codeableConceptDt.setCoding(codingDts);
            observation.setValue(codeableConceptDt);


        }

        if(obs.getConcept().getDatatype().getHl7Abbreviation().equals("TS")){
            PeriodDt datetime = new PeriodDt();

            DateTimeDt startDate = new DateTimeDt();
            startDate.setValue(obs.getValueDatetime());
            DateTimeDt endDate = new DateTimeDt();
            endDate.setValue(obs.getValueDatetime());

            datetime.setStart(startDate);
            datetime.setEnd(endDate);
            observation.setValue(datetime);
        }

        if(obs.getConcept().getDatatype().getHl7Abbreviation().equals("DT")){
            PeriodDt datetime = new PeriodDt();

            DateTimeDt startDate = new DateTimeDt();
            startDate.setValue(obs.getValueDate());
            DateTimeDt endDate = new DateTimeDt();
            endDate.setValue(obs.getValueDate());

            datetime.setStart(startDate);
            datetime.setEnd(endDate);
            observation.setValue(datetime);

        }

        if (obs.getConcept().getDatatype().getHl7Abbreviation().equals("CWE")) {

            Collection<ConceptMap> valueMappings = obs.getValueCoded().getConceptMappings();

            List<CodingDt> values = new ArrayList<CodingDt>();

            for (ConceptMap map : valueMappings) {

                String display = map.getConceptReferenceTerm().getName();
                if(display == null)
                    display = map.getConceptReferenceTerm().toString();

                System.out.println(map.getSource().getName());
                if (map.getSource().getName().equals("LOINC"))
                    values.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(Constants.loinc));
                if (map.getSource().getName().equals("SNOMED"))
                    values.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(Constants.snomed));
                if (map.getSource().getName().equals("CIEL"))
                    values.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(Constants.ciel));
                else{
                    String uri = Context.getAdministrationService().getGlobalProperty("webservices.rest.uriPrefix") + "/ws/rest/v1/fhirconceptsource/" + map.getSource().getUuid();
                    dts.add(new CodingDt().setCode(map.getConceptReferenceTerm().getCode()).setDisplay(display).setSystem(uri));

                }

            }

            CodeableConceptDt codeableConceptDt = new CodeableConceptDt();
            codeableConceptDt.setCoding(values);
            observation.setValue(codeableConceptDt);

        }

        observation.setStatus(ObservationStatusEnum.FINAL);
        observation.setReliability(ObservationReliabilityEnum.OK);

        DateTimeDt dateApplies = new DateTimeDt();
        dateApplies.setValue(obs.getObsDatetime());
        observation.setApplies(dateApplies);

        return observation;



    }

    public static String parseObservation(Observation observation, String contentType) {

        FhirContext ctx = new FhirContext();
        ctx.setNarrativeGenerator(new DefaultThymeleafNarrativeGenerator());

        IParser jsonParser = ctx.newJsonParser();
        IParser xmlParser = ctx.newXmlParser();

        String encoded = null;

        if(contentType != null) {

            if (contentType.equals("application/xml+fhir")) {

                xmlParser.setPrettyPrint(true);
                encoded = xmlParser.encodeResourceToString(observation);
                System.out.println(encoded);
            } else {

                jsonParser.setPrettyPrint(true);
                encoded = jsonParser.encodeResourceToString(observation);
                System.out.println(encoded);
            }
        }

        else {

            jsonParser.setPrettyPrint(true);
            encoded = jsonParser.encodeResourceToString(observation);
            System.out.println(encoded);
        }

        return encoded;
    }


    public static String generateBundle(List<Obs> obsList) {

        Bundle bundle = new Bundle();
        StringDt title = bundle.getTitle();
        title.setValue("Search result");

        IdDt id = new IdDt();
        id.setValue("the request uri");
        bundle.setId(id);

        for(Obs obs : obsList){
            BundleEntry bundleEntry = new BundleEntry();

            IdDt entryId = new IdDt();
            entryId.setValue(Context.getAdministrationService().getGlobalProperty("webservices.rest.uriPrefix") + "/ws/fhir/Observation/" + obs.getUuid());

            bundleEntry.setId(entryId);

            StringDt entryTitle = bundleEntry.getTitle();
            entryTitle.setValue("Observation'/" +obs.getUuid());

            IResource resource = new Observation();
            resource = generateObs(obs);

            bundleEntry.setResource(resource);
            InstantDt dt = new InstantDt();
            if (obs.getDateChanged() != null)
                dt.setValue(obs.getDateChanged());
            else
                dt.setValue(obs.getDateCreated());
            bundleEntry.setUpdated(dt);

            bundle.addEntry(bundleEntry);
        }

        FhirContext ctx = new FhirContext();
        IParser jsonParser = ctx.newJsonParser();

        jsonParser.setPrettyPrint(true);
        String encoded = jsonParser.encodeBundleToString(bundle);
        System.out.println(encoded);

        return encoded;




    }
}
