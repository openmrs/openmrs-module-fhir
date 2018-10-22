package org.openmrs.module.fhir.reference.helper;

public class PatientGenerator {
	
	public static TestPatient generateTestPatient() {
		TestPatient p = new TestPatient();
		p.givenName = randomArrayEntry(PATIENT_GIVEN_NAMES);
		p.familyName = randomArrayEntry(PATIENT_FAMILY_NAMES);
		p.gender = randomArrayEntry(PATIENT_GENDER);
		p.birthDay = randomArrayEntry(PATIENT_BIRTH_DAY);
		p.birthMonth = randomArrayEntry(PATIENT_BIRTH_MONTH);
		p.birthYear = randomArrayEntry(PATIENT_BIRTH_YEAR);
		String suffix = randomSuffix();
		p.address1 = "Address1" + suffix;
		p.address2 = "Address2" + suffix;
		p.city = "City" + suffix;
		p.state = "State" + suffix;
		p.country = "Country" + suffix;
		p.phone = randomSuffix(9);
		p.postalCode = "345234";
		p.latitude = "12";
		p.longitude = "47";
		p.startDate = "01-01-2000";
		p.endDate = "01-01-2010";
		return p;
	}
	
	private static final String[] PATIENT_GIVEN_NAMES = { "Anonymous1", "Anonymous2", "Anonymous3", "User1", "User2", "User3", "User4" };
	
	private static final String[] PATIENT_FAMILY_NAMES = { "Name1", "Name2", "Name3", "Name4", "Username1", "Username3", "Username4" };
	
	private static final String[] PATIENT_BIRTH_MONTH = { "January", "February", "March", "April", "May", "June", "July",
	        "August", "September", "October", "November", "December" };
	
	private static final String[] PATIENT_BIRTH_DAY = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
	        "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28" };
	
	private static final String[] PATIENT_BIRTH_YEAR = { "1980", "1981", "1982", "1983", "1990", "1991", "1995" };
	
	private static final String[] PATIENT_GENDER = { "Male", "Female" };
	
	static String randomArrayEntry(String[] array) {
		return array[(int) (Math.random() * array.length)];
	}
	
	static String randomSuffix() {
		int digits = 6; // First n digits of the current time
		return randomSuffix(digits);
	}
	
	static String randomSuffix(int digits) {
		// First n digits of the current time.
		return String.valueOf(System.currentTimeMillis()).substring(0, digits - 1);
	}
}
