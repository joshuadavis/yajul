package test

import groovy.grape.Grape
import groovy.sql.Sql
import org.yajul.jdbc.DbSchema

/**
 * TODO: Add class level comments!
 * <br>
 * User: josh
 * Date: Feb 24, 2010
 * Time: 11:17:02 PM
 */

//Grape.grab(group:'org.apache.derby', module:'derby', version:'10.5.3.0')
// org.apache.derby.jdbc.EmbeddedDriver
// jdbc:derby:firstdb;create=true

/*
System.properties.keySet().asList().sort().each {
  println "$it = ${System.properties[it]}"
}
*/
Sql sql = Sql.newInstance("jdbc:derby:data/derbytest;create=true",null,null,"org.apache.derby.jdbc.EmbeddedDriver")
DbSchema schema = new DbSchema(sql)
println "${schema}"
