<%@ include file="/WEB-INF/template/include.jsp"%>
<%@ include file="/WEB-INF/template/header.jsp"%>

<script type="text/javascript">
	var $ = jQuery; // required because the legacy UI uses jQuery.noConflict() and Swagger requires the $ variable
</script>
 
<link rel="icon" type="image/png" href="<openmrs:contextPath/>/moduleResources/fhir/js/swagger-ui/dist/images/favicon-32x32.png" sizes="32x32" />
<link rel="icon" type="image/png" href="<openmrs:contextPath/>/moduleResources/fhir/js/swagger-ui/dist/images/favicon-16x16.png" sizes="16x16" />

<link href="<openmrs:contextPath/>/moduleResources/fhir/js/swagger-ui/dist/css/reset.css" media="screen" rel="stylesheet" type="text/css"/>
<link href="<openmrs:contextPath/>/moduleResources/fhir/js/swagger-ui/dist/css/screen.css" media="screen" rel="stylesheet" type="text/css"/>

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

<openmrs:require privilege="Manage FHIR" otherwise="/login.htm" redirect="/module/fhir/settings.form" />
  <script type="text/javascript">
			jQuery(document).ready(function() {
				jQuery("#content").addClass("swagger-section ");
				var swaggerUi = new SwaggerUi({
					  url:"${pageContext.request.contextPath}/module/fhir/rest/swagger.json",
					  dom_id:"swaggerDocumentation",
					  docExpansion: "none",
                      apisSorter: "alpha",
                      onFailure: function(data) {
                          console.log(data);
                          jQuery("#swaggerError").innerHTML = "Error Loading Swagger UI";
                        },
                        validatorUrl: null
					});
					swaggerUi.load();

				});

</script>

<div id="swaggerDocumentation" class="swagger-ui-wrap">
	 <img src="<openmrs:contextPath/>/moduleResources/fhir/js/swagger-ui/dist/images/inprogress.gif" style="display: block; margin-left: auto; margin-right: auto;"/>
<div id="swaggerError" ></div>
