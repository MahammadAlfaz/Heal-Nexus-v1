package com.healnexus.exception;

public class ResourceNotFoundException extends RuntimeException {
    String resourceName ;
    String field;
    Long fieldId;
    String fieldName;

    public ResourceNotFoundException(){};

    public ResourceNotFoundException(String resourceName, String field, String fieldName){

        super(String.format("Resource %s not found for %s and %s", resourceName, field, fieldName));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }

    public ResourceNotFoundException(String resourceName, String field, Long fieldId){

        super(String.format("Resource %s not found for %s and %d", resourceName, field, fieldId));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldId = fieldId;
    }
    public ResourceNotFoundException(String message){
        super(message);
    }


}
