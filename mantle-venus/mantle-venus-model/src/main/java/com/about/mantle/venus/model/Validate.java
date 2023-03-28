package com.about.mantle.venus.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author aditiursekar
 * Used to define which methods of Venus components will be used for 
 * default validation for pattern library tests
 **/

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD) 
public @interface Validate {
	
}