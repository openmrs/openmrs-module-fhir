<%@ include file="/WEB-INF/template/include.jsp" %>

<%--
  The contents of this file are subject to the OpenMRS Public License
  Version 1.0 (the "License"); you may not use this file except in
  compliance with the License. You may obtain a copy of the License at
  http://license.openmrs.org

  Software distributed under the License is distributed on an "AS IS"
  basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
  License for the specific language governing rights and limitations
  under the License.

  Copyright (C) OpenMRS, LLC.  All Rights Reserved.
  --%>

<openmrs:require privilege="Manage Global Properties" otherwise="/login.htm"
                 redirect="/module/fhir/settings.form"/>

<%@ include file="/WEB-INF/template/header.jsp" %>
<%@ include file="template/localHeader.jsp" %>

<h2><spring:message code="GlobalProperty.manage"/></h2>

<openmrs:portlet url="globalProperties" parameters="title=${title}|propertyPrefix=fhir.|hidePrefix=true|readOnly=false"/>

<%@ include file="/WEB-INF/template/footer.jsp" %>
