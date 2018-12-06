package org.openmrs.module.fhir.api.helper;

import org.springframework.http.RequestEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.HttpMessageConverter;

import javax.transaction.NotSupportedException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * <h1>ClientHelper</h1>
 * Describes required methods for alternative node-to-node communication clients.
 *
 * @see <a href="https://issues.openmrs.org/browse/SYNCT-274">SYNCT-274</a>
 * @since 1.14.0
 */
public interface ClientHelper {

	/**
	 * <p>Returns new 'retrieve' request - in means of HTTP protocol it refers to GET request.</p>
	 *
	 * @param url represents URL of resource to be retrieved
	 * @return returns new RequestEntity with 'retrieve' request
	 * @throws URISyntaxException
	 */
	RequestEntity retrieveRequest(String url) throws URISyntaxException;

	/**
	 * <p>Returns new 'create' request - in means of HTTP protocol it refers to POST request.</p>
	 *
	 * @param url represents URL of resource category, where an object will be created
	 * @param object represents an object that will be sent
	 * @return returns new RequestEntity with 'create' request
	 * @throws URISyntaxException
	 */
	RequestEntity createRequest(String url, Object object) throws URISyntaxException;

	/**
	 * <p>Returns new 'delete' request - in means of HTTP protocol it refers to DELETE request.</p>
	 *
	 * @param url represents URL of resource category, from where an object will be deleted
	 * @param uuid represents UUID of the object, that will be deleted
	 * @return returns new RequestEntity with 'delete' request
	 * @throws URISyntaxException
	 */
	RequestEntity deleteRequest(String url, String uuid) throws URISyntaxException;

	/**
	 * <p>Returns new 'update' request - in means of HTTP protocol it refers to PUT request.</p>
	 *
	 * @param url represents URL of resource, that will be updated
	 * @param object represents an updated object
	 * @return returns new RequestEntity with 'update' request
	 * @throws URISyntaxException
	 */
	RequestEntity updateRequest(String url, Object object) throws URISyntaxException;

	/**
	 * <p>Returns Class object corresponding to category name.</p>
	 *
	 * @param category represents name of category
	 * @return returns Class object
	 */
	Class resolveClassByCategory(String category);

	/**
	 * <p>Returns a list of HTTP interceptors used in communication between nodes, including authentication of a user.</p>
	 *
	 * @param username represents username of a user
	 * @param password represents password of a user
	 * @return return a list of custom interceptors
	 */
	List<ClientHttpRequestInterceptor> getCustomInterceptors(String username, String password);

	/**
	 * <p>Returns a list of custom converters, that will be used to convert messages between formats.</p>
	 *
	 * @return returns a list of custom converters
	 */
	List<HttpMessageConverter<?>> getCustomMessageConverter();

	/**
	 * <p>Compares two objects of the same category.</p>
	 *
	 * @param category represents category of two compared objects
	 * @param from represents incoming object
	 * @param dest represents destination object
	 * @return returns true if objects are equal, false otherwise
	 */
	boolean compareResourceObjects(String category, Object from, Object dest);

	/**
	 * <p>Converts a String of formatted data to an object of a specified class.</p>
	 *
	 * @param formattedData represents data as a String
	 * @param clazz represents desired class of returned object
	 * @return returns an object of specified class
	 */
	Object convertToObject(String formattedData, Class<?> clazz);

	/**
	 * <p>Converts an object to a String of formatted data.</p>
	 *
	 * @param object represents an object that will be converted to a String of formatted data
	 * @return returns String of formatted data
	 */
	String convertToFormattedData(Object object);

	/**
	 * <p>Converts an object to OpenMRS object of specified category.</p>
	 *
	 * @param object represents an object that will be converted to OpenMRS object
	 * @param category represents category of OpenMRS object as a String
	 * @return returns OpenMRS object of specified category
	 * @throws NotSupportedException
	 */
	Object convertToOpenMrsObject(Object object, String category) throws NotSupportedException;
}
