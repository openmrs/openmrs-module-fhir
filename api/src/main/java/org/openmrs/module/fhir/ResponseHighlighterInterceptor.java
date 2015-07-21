package org.openmrs.module.fhir;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.hl7.fhir.instance.model.IBaseResource;

import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.RequestTypeEnum;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.server.Constants;
import ca.uhn.fhir.rest.server.EncodingEnum;
import ca.uhn.fhir.rest.server.RestfulServer.NarrativeModeEnum;
import ca.uhn.fhir.rest.server.RestfulServerUtils;
import ca.uhn.fhir.rest.server.exceptions.AuthenticationException;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import ca.uhn.fhir.rest.server.interceptor.InterceptorAdapter;

public class ResponseHighlighterInterceptor extends InterceptorAdapter {
	
	private String format(String theResultBody, EncodingEnum theEncodingEnum) {
		String str = StringEscapeUtils.escapeHtml4(theResultBody);
		if (str == null || theEncodingEnum == null) {
			return str;
		}
		
		StringBuilder b = new StringBuilder();
		
		if (theEncodingEnum == EncodingEnum.JSON) {
			
			boolean inValue = false;
			boolean inQuote = false;
			for (int i = 0; i < str.length(); i++) {
				char prevChar = (i > 0) ? str.charAt(i - 1) : ' ';
				char nextChar = str.charAt(i);
				char nextChar2 = (i + 1) < str.length() ? str.charAt(i + 1) : ' ';
				char nextChar3 = (i + 2) < str.length() ? str.charAt(i + 2) : ' ';
				char nextChar4 = (i + 3) < str.length() ? str.charAt(i + 3) : ' ';
				char nextChar5 = (i + 4) < str.length() ? str.charAt(i + 4) : ' ';
				char nextChar6 = (i + 5) < str.length() ? str.charAt(i + 5) : ' ';
				if (inQuote) {
					b.append(nextChar);
					if (prevChar != '\\' && nextChar == '&' && nextChar2 == 'q' && nextChar3 == 'u' && nextChar4 == 'o'
					        && nextChar5 == 't' && nextChar6 == ';') {
						b.append("quot;</span>");
						i += 5;
						inQuote = false;
					} else if (nextChar == '\\' && nextChar2 == '"') {
						b.append("quot;</span>");
						i += 5;
						inQuote = false;
					}
				} else {
					if (nextChar == ':') {
						inValue = true;
						b.append(nextChar);
					} else if (nextChar == '[' || nextChar == '{') {
						b.append("<span class='hlControl'>");
						b.append(nextChar);
						b.append("</span>");
						inValue = false;
					} else if (nextChar == '}' || nextChar == '}' || nextChar == ',') {
						b.append("<span class='hlControl'>");
						b.append(nextChar);
						b.append("</span>");
						inValue = false;
					} else if (nextChar == '&' && nextChar2 == 'q' && nextChar3 == 'u' && nextChar4 == 'o'
					        && nextChar5 == 't' && nextChar6 == ';') {
						if (inValue) {
							b.append("<span class='hlQuot'>&quot;");
						} else {
							b.append("<span class='hlTagName'>&quot;");
						}
						inQuote = true;
						i += 5;
					} else if (nextChar == ':') {
						b.append("<span class='hlControl'>");
						b.append(nextChar);
						b.append("</span>");
						inValue = true;
					} else {
						b.append(nextChar);
					}
				}
			}
			
		} else {
			boolean inQuote = false;
			boolean inTag = false;
			for (int i = 0; i < str.length(); i++) {
				char nextChar = str.charAt(i);
				char nextChar2 = (i + 1) < str.length() ? str.charAt(i + 1) : ' ';
				char nextChar3 = (i + 2) < str.length() ? str.charAt(i + 2) : ' ';
				char nextChar4 = (i + 3) < str.length() ? str.charAt(i + 3) : ' ';
				char nextChar5 = (i + 4) < str.length() ? str.charAt(i + 4) : ' ';
				char nextChar6 = (i + 5) < str.length() ? str.charAt(i + 5) : ' ';
				if (inQuote) {
					b.append(nextChar);
					if (nextChar == '&' && nextChar2 == 'q' && nextChar3 == 'u' && nextChar4 == 'o' && nextChar5 == 't'
					        && nextChar6 == ';') {
						b.append("quot;</span>");
						i += 5;
						inQuote = false;
					}
				} else if (inTag) {
					if (nextChar == '&' && nextChar2 == 'g' && nextChar3 == 't' && nextChar4 == ';') {
						b.append("</span><span class='hlControl'>&gt;</span>");
						inTag = false;
						i += 3;
					} else if (nextChar == ' ') {
						b.append("</span><span class='hlAttr'>");
						b.append(nextChar);
					} else if (nextChar == '&' && nextChar2 == 'q' && nextChar3 == 'u' && nextChar4 == 'o'
					        && nextChar5 == 't' && nextChar6 == ';') {
						b.append("<span class='hlQuot'>&quot;");
						inQuote = true;
						i += 5;
					} else {
						b.append(nextChar);
					}
				} else {
					if (nextChar == '&' && nextChar2 == 'l' && nextChar3 == 't' && nextChar4 == ';') {
						b.append("<span class='hlControl'>&lt;</span><span class='hlTagName'>");
						inTag = true;
						i += 3;
					} else {
						b.append(nextChar);
					}
				}
			}
		}
		
		return b.toString();
	}
	
	@Override
	public boolean outgoingResponse(RequestDetails theRequestDetails, IBaseResource theResponseObject,
	                                HttpServletRequest theServletRequest, HttpServletResponse theServletResponse)
	    throws AuthenticationException {
		
		/*
		 * It's not a browser...
		 */
		String accept = theServletRequest.getHeader(Constants.HEADER_ACCEPT);
		if (accept == null || !accept.toLowerCase().contains("html")) {
			return super.outgoingResponse(theRequestDetails, theResponseObject, theServletRequest, theServletResponse);
		}
		
		/*
		 * It's an AJAX request, so no HTML 
		 */
		String requestedWith = theServletRequest.getHeader("X-Requested-With");
		if (requestedWith != null) {
			return super.outgoingResponse(theRequestDetails, theResponseObject, theServletRequest, theServletResponse);
		}
		
		/*
		 * Not a GET
		 */
		if (theRequestDetails.getRequestType() != RequestTypeEnum.GET) {
			return super.outgoingResponse(theRequestDetails, theResponseObject, theServletRequest, theServletResponse);
		}
		// Pretty print
		boolean prettyPrint = true;//RestfulServerUtils.prettyPrintResponse(theRequestDetails.getServer(), theRequestDetails);
		
		// Narrative mode
		NarrativeModeEnum narrativeMode = RestfulServerUtils.determineNarrativeMode(theRequestDetails);
		
		// Determine response encoding
		EncodingEnum responseEncoding = null;
		if (theRequestDetails.getParameters().containsKey(Constants.PARAM_FORMAT)) {
			// Browsers often state that they accept XML but we won't take that as being the user's preference
			// unless they explicitly request it
			responseEncoding = RestfulServerUtils.determineResponseEncodingNoDefault(theServletRequest);
		}
		if (responseEncoding == null) {
			responseEncoding = theRequestDetails.getServer().getDefaultResponseEncoding();
		}
		
		IParser p = responseEncoding.newParser(theRequestDetails.getServer().getFhirContext());
		p.setPrettyPrint(prettyPrint);
		
		String encoded = p.encodeResourceToString(theResponseObject);
		
		theServletResponse.setContentType("text/html; charset=UTF-8");///(Constants.CT_HTML_WITH_UTF8);//"text/html; charset=UTF-8"
		
		//@formatter:on
		String out = "<html lang=\"en\">\n" + "	<head>\n" + "		<meta charset=\"utf-8\" />\n" + "       <style>\n"
		        + ".hlQuot {\n" + "	color: #88F;\n" + "}\n" + ".hlAttr {\n" + "	color: #888;\n" + "}\n" + ".hlTagName {\n"
		        + "	color: #006699;\n" + "}\n" + ".hlControl {\n" + "	color: #660000;\n" + "}\n" + ".hlText {\n"
		        + "	color: #000000;\n" + "}\n" + ".hlUrlBase {\n" + "}" + "       </style>\n" + "	</head>\n" + "\n"
		        + "	<body>" + "<pre>" + format(encoded, responseEncoding) + "</pre>" + "   </body>" + "</html>";
		//@formatter:off
		
		try {
			theServletResponse.getWriter().append(out);
			theServletResponse.getWriter().close();
		} catch (IOException e) {
			throw new InternalErrorException(e);
		}
		
		return false;
	}
	
}