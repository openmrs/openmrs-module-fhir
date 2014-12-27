package org.openmrs.module.fhir.api.util;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu.composite.AddressDt;
import ca.uhn.fhir.model.dstu.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu.resource.Location;
import ca.uhn.fhir.model.dstu.valueset.AddressUseEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationFailureException;
import ca.uhn.fhir.model.dstu.resource.Location.Position;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class FHIRLocationUtil {

	public static Location generateLocation(org.openmrs.Location omrsLocation) {
		Location location = new Location();

		IdDt uuid = new IdDt();

		uuid.setValue(omrsLocation.getUuid());
        location.setId(uuid);

        location.setName(omrsLocation.getName());
        location.setDescription(omrsLocation.getDescription());

        AddressDt address = new AddressDt();
        address.setCity(omrsLocation.getCityVillage());
        address.setCountry(omrsLocation.getCountry());

        List<StringDt> addressStrings = new ArrayList<StringDt>();

        addressStrings.add(new StringDt(omrsLocation.getAddress1()));
        addressStrings.add(new StringDt(omrsLocation.getAddress2()));
        addressStrings.add(new StringDt(omrsLocation.getAddress3()));
        addressStrings.add(new StringDt(omrsLocation.getAddress4()));
        addressStrings.add(new StringDt(omrsLocation.getAddress5()));

        address.setLine(addressStrings);
        address.setUse(AddressUseEnum.WORK);

        Position position = location.getPosition();

        if(omrsLocation.getLongitude() != null) {
            BigDecimal longitude = new BigDecimal(omrsLocation.getLongitude());
            position.setLongitude(longitude);
        }

        if(omrsLocation.getLatitude() != null) {
            BigDecimal latitude = new BigDecimal(omrsLocation.getLatitude());
            position.setLatitude(latitude);
        }

        validate(location);

		return location;
	}

	public static void validate(Location location) {
		FhirContext ctx = new FhirContext();

		// Request a validator and apply it
		FhirValidator val = ctx.newValidator();
		try {
			val.validate(location);
		} catch (ValidationFailureException e) {
			// We failed validation!
			String results = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(e.getOperationOutcome());
		}

	}
}
