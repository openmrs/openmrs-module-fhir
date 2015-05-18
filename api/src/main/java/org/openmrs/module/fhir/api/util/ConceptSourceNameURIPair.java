/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.fhir.api.util;

public class ConceptSourceNameURIPair {

	private String conceptSourceName;
	private String conceptSourceURI;

	public ConceptSourceNameURIPair(String conceptSourceName, String conceptSourceURI) {
		this.conceptSourceName = conceptSourceName;
		this.conceptSourceURI = conceptSourceURI;
	}

	public String getConceptSourceName() {
		return conceptSourceName;
	}

	public void setConceptSourceName(String conceptSourceName) {
		this.conceptSourceName = conceptSourceName;
	}

	public String getConceptSourceURI() {
		return conceptSourceURI;
	}

	public void setConceptSourceURI(String conceptSourceURI) {
		this.conceptSourceURI = conceptSourceURI;
	}
}
