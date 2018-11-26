package org.openmrs.module.fhir.api.util;

import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.PersonAddress;

import java.util.List;

import static java.lang.String.valueOf;

public class FHIRAddressUtil {

	public static PersonAddress generatePersonAddress(Address fhirAddress) {
		PersonAddress address = new PersonAddress();
		address.setUuid(fhirAddress.getId());
		address.setCityVillage(fhirAddress.getCity());
		address.setCountry(fhirAddress.getCountry());
		address.setStateProvince(fhirAddress.getState());
		address.setPostalCode(fhirAddress.getPostalCode());
		List<StringType> addressStrings = fhirAddress.getLine();

		if (addressStrings != null) {
			for (int i = 0; i < addressStrings.size(); i++) {
				if (i == 0) {
					address.setAddress1(valueOf(addressStrings.get(0)));
				} else if (i == 1) {
					address.setAddress2(valueOf(addressStrings.get(1)));
				} else if (i == 2) {
					address.setAddress3(valueOf(addressStrings.get(2)));
				} else if (i == 3) {
					address.setAddress4(valueOf(addressStrings.get(3)));
				} else if (i == 4) {
					address.setAddress5(valueOf(addressStrings.get(4)));
				}
			}
		}

		if (String.valueOf(Address.AddressUse.HOME.toCode()).equalsIgnoreCase(fhirAddress.getUse().toCode())) {
			address.setPreferred(true);
		}
		if (String.valueOf(Address.AddressUse.OLD.toCode()).equalsIgnoreCase(fhirAddress.getUse().toCode())) {
			address.setPreferred(false);
		}
		return address;
	}

	public static PersonAddress updatePersonAddress(PersonAddress oldAddress, PersonAddress newAddress) {
		oldAddress.setCityVillage(newAddress.getCityVillage());
		oldAddress.setCountry(newAddress.getCountry());
		oldAddress.setStateProvince(newAddress.getStateProvince());
		oldAddress.setPostalCode(newAddress.getPostalCode());
		oldAddress.setAddress1(newAddress.getAddress1());
		oldAddress.setAddress2(newAddress.getAddress2());
		oldAddress.setAddress3(newAddress.getAddress3());
		oldAddress.setAddress4(newAddress.getAddress4());
		oldAddress.setAddress5(newAddress.getAddress5());
		oldAddress.setPreferred(newAddress.getPreferred());
		return oldAddress;
	}

	private FHIRAddressUtil() { }
}
