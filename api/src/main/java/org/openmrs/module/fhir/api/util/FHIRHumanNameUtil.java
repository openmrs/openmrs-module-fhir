package org.openmrs.module.fhir.api.util;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.PersonName;

import java.util.List;

public class FHIRHumanNameUtil {

	public static PersonName generatePersonName(HumanName humanNameDt) {
		PersonName personName = new PersonName();
		personName.setUuid(humanNameDt.getId());
		if (humanNameDt.getUse() != null) {
			String getUse = humanNameDt.getUse().toCode();
			if (String.valueOf(HumanName.NameUse.OFFICIAL).equalsIgnoreCase(getUse)
					|| String.valueOf(HumanName.NameUse.USUAL).equalsIgnoreCase(getUse)) {
				personName.setPreferred(true);
			}
			if (String.valueOf(HumanName.NameUse.OLD).equalsIgnoreCase(getUse)) {
				personName.setPreferred(false);
			}
		}
		if (humanNameDt.getSuffix() != null) {
			List<StringType> prefixes = humanNameDt.getSuffix();
			if (prefixes.size() > 0) {
				StringType prefix = prefixes.get(0);
				personName.setPrefix(String.valueOf(prefix));
			}
		}
		if (humanNameDt.getSuffix() != null) {
			List<StringType> suffixes = humanNameDt.getSuffix();
			if (suffixes.size() > 0) {
				StringType suffix = suffixes.get(0);
				personName.setFamilyNameSuffix(String.valueOf(suffix));
			}
		}

		List<StringType> givenNames = humanNameDt.getGiven();
		if (givenNames != null) {
			StringType givenName = givenNames.get(0);
			personName.setGivenName(String.valueOf(givenName));
		}
		String familyName = humanNameDt.getFamily();
		if (!StringUtils.isEmpty(familyName)) {
			personName.setFamilyName(familyName);
		}
		return personName;
	}

	public static PersonName updatePersonName(PersonName oldName, PersonName newName) {
		oldName.setPreferred(newName.getPreferred());
		oldName.setPrefix(newName.getPrefix());
		oldName.setFamilyNameSuffix(newName.getFamilyNameSuffix());
		oldName.setGivenName(newName.getGivenName());
		oldName.setFamilyName(newName.getFamilyName());
		return oldName;
	}

	private FHIRHumanNameUtil() { }
}
