package org.openmrs.module.fhir.web.controller;


public class Result extends SimpleObject {

    public Object toSimpleObject(){
       return new Object();

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

}
