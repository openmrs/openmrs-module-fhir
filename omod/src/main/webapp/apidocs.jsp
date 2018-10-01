<%@ include file="/WEB-INF/template/include.jsp" %>
<%@ include file="/WEB-INF/template/header.jsp" %>

<script type="text/javascript">
	var $ = jQuery; // required because the legacy UI uses jQuery.noConflict() and Swagger requires the $ variable
</script>

<link rel="icon" type="image/png"
      href="<openmrs:contextPath/>/moduleResources/fhir/js/swagger-ui/dist/images/favicon-32x32.png" sizes="32x32"/>
<link rel="icon" type="image/png"
      href="<openmrs:contextPath/>/moduleResources/fhir/js/swagger-ui/dist/images/favicon-16x16.png" sizes="16x16"/>

<link href="<openmrs:contextPath/>/moduleResources/fhir/js/swagger-ui/dist/css/reset.css" media="screen" rel="stylesheet"
      type="text/css"/>
<link href="<openmrs:contextPath/>/moduleResources/fhir/js/swagger-ui/dist/css/screen.css" media="screen" rel="stylesheet"
      type="text/css"/>

<openmrs:htmlInclude file="/moduleResources/fhir/js/swagger-ui/dist/lib/jquery.slideto.min.js"/>
<openmrs:htmlInclude file="/moduleResources/fhir/js/swagger-ui/dist/lib/jquery.wiggle.min.js"/>
<openmrs:htmlInclude file="/moduleResources/fhir/js/swagger-ui/dist/lib/jquery.ba-bbq.min.js"/>
<openmrs:htmlInclude file="/moduleResources/fhir/js/swagger-ui/dist/lib/handlebars-2.0.0.js"/>
<openmrs:htmlInclude file="/moduleResources/fhir/js/swagger-ui/dist/lib/js-yaml.min.js"/>
<openmrs:htmlInclude file="/moduleResources/fhir/js/swagger-ui/dist/lib/lodash.min.js"/>
<openmrs:htmlInclude file="/moduleResources/fhir/js/swagger-ui/dist/lib/backbone-min.js"/>
<openmrs:htmlInclude file="/moduleResources/fhir/js/swagger-ui/dist/swagger-ui.min.js"/>
<openmrs:htmlInclude file="/moduleResources/fhir/js/swagger-ui/dist/lib/highlight.9.1.0.pack.js"/>
<openmrs:htmlInclude file="/moduleResources/fhir/js/swagger-ui/dist/lib/highlight.9.1.0.pack_extended.js"/>
<openmrs:htmlInclude file="/moduleResources/fhir/js/swagger-ui/dist/lib/jsoneditor.min.js"/>
<openmrs:htmlInclude file="/moduleResources/fhir/js/swagger-ui/dist/lib/marked.js"/>
<openmrs:htmlInclude file="/moduleResources/fhir/js/swagger-ui/dist/lib/swagger-oauth.js"/>

<openmrs:require privilege="Manage FHIR" otherwise="/login.htm" redirect="/module/fhir/settings.form"/>
<script type="text/javascript">
	jQuery(document).ready(function () {
		jQuery("#content").addClass("swagger-section ");
		var swaggerUi = new SwaggerUi({
			url: "${pageContext.request.contextPath}/module/fhir/rest/swagger.json",
			dom_id: "swaggerDocumentation",
			docExpansion: "none",
			apisSorter: "alpha",
			onFailure: function (data) {
				console.log(data);
				jQuery("#swaggerError").innerHTML = '<openmrs:message code="fhir.swagger.error"/>';
			},
			validatorUrl: null
		});
		swaggerUi.load();

		$j("#sdkGenerationModal").dialog({
			autoOpen: false,
			resizable: false,
			width: 'auto',
			height: 'auto',
			modal: true
		});

	});

	function generateSDK() {
		var selectedLanguage = $('#languagelist :selected').text();
		var url = "${pageContext.request.contextPath}/module/fhir/rest/swaggercodegen?language=" + selectedLanguage;
		$j.ajax({
			url: url,
			method: 'GET',
			processData: false,
			success: function (data) {
				window.location = url;
				alert(' <openmrs:message code="fhir.generate.sdk.success"/> ' + selectedLanguage);
				$j('#sdkGenerationModal').dialog('close')
			},
			error: function (xhr) {
				alert(' <openmrs:message code="fhir.generate.sdk.error"/> : ' + JSON.stringify(xhr));
			}
		});
	}
</script>
<div id="content">
	<div id="swaggerDocumentation" class="swagger-ui-wrap">
		<img src="<openmrs:contextPath/>/moduleResources/fhir/js/swagger-ui/dist/images/inprogress.gif"
		     style="display: block; margin-left: auto; margin-right: auto;"/>
		<div id="swaggerError"></div>
	</div>
	<div id="swagger-sdk" style="margin-top: 10px;" class="info swagger-ui-wrap">
		<button type="button" onclick="javascript:$j('#sdkGenerationModal').dialog('open')" style="float: right;">
			<openmrs:message code="fhir.generate.sdk"/></button>
	</div>
	<div id="sdkGenerationModal" style="display:none;">
		<form id="sdkGenerationForm">
			<table cellpadding="3" cellspacing="3" align="center">
				<tr>
					<th><openmrs:message code="fhir.generate.sdk.language"/></th>
					<td>
						<select id="languagelist">
							<option>java
							<option>
							<option>android
							<option>
							<option>csharp
							<option>
							<option>cpp
							<option>
							<option>dart
							<option>
							<option>flash
							<option>
							<option>go
							<option>
							<option>groovy
							<option>
							<option>javascript
							<option>
							<option>jmeter
							<option>
							<option>nodejs
							<option>
							<option>perl
							<option>
							<option>php
							<option>
							<option>python
							<option>
							<option>ruby
							<option>
							<option>scala
							<option>
							<option>swift
							<option>
							<option>clojure
							<option>
							<option>aspNet5
							<option>
							<option>asyncScala
							<option>
							<option>spring
							<option>
							<option>csharpDotNet2
							<option>
							<option>haskell
							<option>
							<option>ada
							<option>
							<option>ada-server
							<option>
							<option>akka-scala
							<option>
							<option>apache2-config
							<option>
							<option>apex
							<option>
							<option>aspnet-server
							<option>
							<option>bash
							<option>
							<option>confluence-wiki
							<option>
							<option>cpp-rest-client
							<option>
							<option>charp-dot-net2
							<option>
							<option>eiffel
							<option>
							<option>elm
							<option>
							<option>erlang
							<option>
							<option>erlang-server
							<option>
							<option>finch-server
							<option>
							<option>flask-connexion
							<option>
							<option>go-server
							<option>
							<option>haskel-http
							<option>
							<option>haskel-servant
							<option>
							<option>java-cxf
							<option>
							<option>java-cxf-server
							<option>
							<option>java-inflector-server
							<option>
							<option>java-jaxrs-cxfcdi-server
							<option>
							<option>java-jaxrs-spec-server
							<option>
							<option>java-jersey-server
							<option>
							<option>java-msf4j-server
							<option>
							<option>java-pkmst-server
							<option>
							<option>java-play-framework
							<option>
							<option>java-resteasy-eap-server
							<option>
							<option>java-resteasy-server
							<option>
							<option>java-vertx-server
							<option>
							<option>javascript-closure-angular
							<option>
							<option>kotlin
							<option>
							<option>kotlin-server
							<option>
							<option>lua
							<option>
							<option>lumen-server
							<option>
							<option>nancy-fx-server
							<option>
							<option>nodejs-server
							<option>
							<option>objc
							<option>
							<option>pistache-server
							<option>
							<option>powershell
							<option>
							<option>qt5cpp
							<option>
							<option>rclient
							<option>
							<option>rails5-server
							<option>
							<option>restbed
							<option>
							<option>rust
							<option>
							<option>rust-server
							<option>
							<option>scala-gatling
							<option>
							<option>scala-lagom-server
							<option>
							<option>scala-tra-server
							<option>
							<option>scalaz
							<option>
							<option>silex-server
							<option>
							<option>sinatra-server
							<option>
							<option>slim-framework-server
							<option>
							<option>staticdoc
							<option>
							<option>static-html2
							<option>
							<option>static-html
							<option>
							<option>swagger
							<option>
							<option>swagger-yaml
							<option>
							<option>swift3
							<option>
							<option>swift4
							<option>
							<option>swift
							<option>
							<option>symfony-server
							<option>
							<option>tizen
							<option>
							<option>typescript-angular
							<option>
							<option>typescript-angular-js
							<option>
							<option>typescript-aureliac
							<option>
							<option>typescript-fetch
							<option>
							<option>typescript-inversify
							<option>
							<option>typescript-jquery
							<option>
							<option>typescript-node
							<option>
							<option>undertow
							<option>
							<option>zend-expressive-path-handler
							<option>
						</select>
					</td>
				</tr>
				<tr height="20"></tr>
				<tr>
					<td colspan="2" style="text-align: center">
						<input type="button" value="Generate" onclick="generateSDK()"/>
						<input id="close-delete-dialog" type="button" value="<openmrs:message code="general.cancel"/>"/>
					</td>
				</tr>
			</table>
		</form>
	</div>
</div>
