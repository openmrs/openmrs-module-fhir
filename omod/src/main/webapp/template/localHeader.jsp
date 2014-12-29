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

<spring:htmlEscape defaultHtmlEscape="true"/>
<ul id="menu">
	<li class="first"><a
			href="${pageContext.request.contextPath}/admin"><spring:message
			code="admin.title.short"/></a></li>

	<li
	<c:if test='<%= request.getRequestURI().contains("/manage") %>'>class="active"</c:if>>
	<a
			href="${pageContext.request.contextPath}/module/fhir/manage.form"><spring:message
			code="fhir.manage"/></a>
	</li>

	<li
	<c:if test='<%= request.getRequestURI().contains("/settings") %>'>class="active"</c:if>>
	<a
			href="${pageContext.request.contextPath}/module/fhir/settings.form"><spring:message
			code="fhir.settings"/></a>
	</li>

	                                                                                     <!-- Add further links here -->
</ul>
<h2>
	<spring:message code="fhir.title"/>
</h2>
