package org.yajul.scannermodule;

import com.google.inject.name.Named;
import com.google.inject.name.Names;

import java.io.Serializable;

/**
 * Example implementation.
 * <br>
 * User: Josh
 * Date: 3/27/11
 * Time: 3:38 PM
 */
@Bind
@Type(type = ExampleInterface.class, annotatedWith = Two.class)
public class ExampleImplTwo implements ExampleInterface, Serializable {

}
