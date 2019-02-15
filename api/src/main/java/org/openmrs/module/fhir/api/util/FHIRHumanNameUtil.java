package org.openmrs.module.fhir.api.util;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.HumanName;
import org.hl7.fhir.dstu3.model.StringType;
import org.openmrs.PersonName;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class FHIRHumanNameUtil {

	private static final String FAMILY_PREFIX = "FAMILY_PREFIX";

	private static final String PREFIX = "PREFIX";

	public static Set<PersonName> buildOpenmrsNames(List<HumanName> humanNames) {
		Set<PersonName> names = new TreeSet<PersonName>();
		for (HumanName humanNameDt : humanNames) {
			PersonName name = FHIRHumanNameUtil.buildOpenmrsPersonName(humanNameDt);
			names.add(name);
		}
		return names;
	}

	public static List<HumanName> buildHumanNames(Set<PersonName> omrsNames) {
		List<HumanName> humanNames = new ArrayList<>();
		for (PersonName name : omrsNames) {
			humanNames.add(FHIRHumanNameUtil.buildHumanName(name));
		}
		return humanNames;
	}

	public static PersonName buildOpenmrsPersonName(HumanName humanNameDt) {
		PersonName personName = new PersonName();
		if(StringUtils.isNotBlank(humanNameDt.getId())) {
			personName.setUuid(humanNameDt.getId());
		}
		setOpenmrsNames(humanNameDt, personName);
		buildOpenmrsNamePrefixes(humanNameDt, personName);
		buildOpenmrsSuffix(humanNameDt, personName);
		personName.setPreferred(determineIfPreferredName(humanNameDt));

		return personName;
	}

	public static HumanName buildHumanName(org.openmrs.PersonName personName) {
		HumanName fhirName = new HumanName();
		fhirName.setId(personName.getUuid());
		setHumanNames(personName, fhirName);
		fhirName.setPrefix(buildHumanNamePrefixes(personName));
		fhirName.setSuffix(buildHumanNameSuffix(personName));
		fhirName.setUse(buildHumanNameUse(personName));

		return fhirName;
	}

	public static void setOpenmrsNames(HumanName humanNameDt, PersonName personName) {
		String familyName = humanNameDt.getFamily();
		if (!StringUtils.isEmpty(familyName)) {
			personName.setFamilyName(familyName);
		}

		List<StringType> names = humanNameDt.getGiven();
		if (names.size() > 0) {
			personName.setGivenName(String.valueOf(names.get(0)));
		}
		if (names.size() > 1) {
			personName.setMiddleName(String.valueOf(names.get(1)));
		}
	}

	public static void setHumanNames(PersonName personName, HumanName fhirName) {
		fhirName.setFamily(personName.getFamilyName());
		List<StringType> givenNames = new ArrayList<StringType>();

		StringType givenName = new StringType();
		givenName.setValue(personName.getGivenName());
		givenNames.add(givenName);

		if (StringUtils.isNotBlank(personName.getMiddleName())) {
			StringType middleName = new StringType();
			middleName.setValue(personName.getMiddleName());
			givenNames.add(middleName);
		}
		fhirName.setGiven(givenNames);
	}

	public static void buildOpenmrsNamePrefixes(HumanName humanNameDt, PersonName personName) {
		if (humanNameDt.getPrefix() != null) {
			List<StringType> prefixes = humanNameDt.getPrefix();
			for(StringType prefix : prefixes) {
				if (prefix.getId().equalsIgnoreCase(PREFIX)) {
					personName.setPrefix(String.valueOf(prefix));
				} else if (prefix.getId().equalsIgnoreCase(FAMILY_PREFIX)) {
					personName.setFamilyNamePrefix(String.valueOf(prefix));
				}
			}
		}
	}

	private static List<StringType> buildHumanNamePrefixes(PersonName personName) {
		List<StringType> prefixes = new ArrayList<StringType>();
		if (StringUtils.isNotBlank(personName.getPrefix())) {
			StringType prefix = new StringType();
			prefix.setId(PREFIX);
			prefix.setValue(personName.getPrefix());
			prefixes.add(prefix);
		}

		if (StringUtils.isNotBlank(personName.getFamilyNamePrefix())) {
			StringType prefix = new StringType();
			prefix.setId(FAMILY_PREFIX);
			prefix.setValue(personName.getPrefix());
			prefixes.add(prefix);
		}
		return prefixes;
	}

	public static void buildOpenmrsSuffix(HumanName humanNameDt, PersonName personName) {
		if (humanNameDt.getSuffix() != null) {
			List<StringType> suffixes = humanNameDt.getSuffix();
			if (suffixes.size() > 0) {
				StringType suffix = suffixes.get(0);
				personName.setFamilyNameSuffix(String.valueOf(suffix));
			}
		}
	}

	private static List<StringType> buildHumanNameSuffix(PersonName personName) {
		List<StringType> suffixes = new ArrayList<StringType>();
		if (StringUtils.isNotBlank(personName.getFamilyNameSuffix())) {
			StringType suffix = new StringType();
			suffix.setValue(personName.getFamilyNameSuffix());
			suffixes.add(suffix);
		}
		return suffixes;
	}

	public static boolean determineIfPreferredName(HumanName humanNameDt) {
		boolean preferred = false;
		if (humanNameDt.getUse() != null) {
			String getUse = humanNameDt.getUse().toCode();
			if (String.valueOf(HumanName.NameUse.OFFICIAL).equalsIgnoreCase(getUse)
					|| String.valueOf(HumanName.NameUse.USUAL).equalsIgnoreCase(getUse)) {
				preferred = true;
			}
			if (String.valueOf(HumanName.NameUse.OLD).equalsIgnoreCase(getUse)) {
				preferred = false;
			}
		}
		return preferred;
	}

	private static HumanName.NameUse buildHumanNameUse(PersonName personName) {
		if (personName.isPreferred()) {
			return HumanName.NameUse.USUAL;
		} else {
			return HumanName.NameUse.OLD;
		}
	}

	public static PersonName updatePersonName(PersonName oldName, PersonName newName) {
		oldName.setPreferred(newName.getPreferred());
		oldName.setPrefix(newName.getPrefix());
		oldName.setFamilyNameSuffix(newName.getFamilyNameSuffix());
		oldName.setGivenName(newName.getGivenName());
		oldName.setFamilyName(newName.getFamilyName());
		return oldName;
	}

	public static boolean validateOpenmrsNames(Set<PersonName> names) {
		boolean valid = false;
		for (PersonName name : names) {
			if (org.apache.commons.lang.StringUtils.isNotBlank(name.getGivenName())
					&& org.apache.commons.lang.StringUtils.isNotBlank(name.getFamilyName())) {
				valid = true;
				break;
			}
		}
		return valid;
	}

	private FHIRHumanNameUtil() { }
}
