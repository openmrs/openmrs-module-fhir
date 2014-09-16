package org.openmrs.module.fhir.web.controller;

import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.module.webservices.rest.web.resource.api.Converter;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

public class Result implements PageableResult {

    public SimpleObject toSimpleObject(){
       return new SimpleObject();

    }


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    String result;


    public String toString(){
        return this.result;
    }

    @Override
    public SimpleObject toSimpleObject(Converter<?> converter) throws ResponseException {
        return null;
    }
}
