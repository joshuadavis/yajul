package org.yajul.jdbc

import groovy.sql.Sql

/**
 * JDBC connection info
 * <br>
 * User: josh
 * Date: Feb 21, 2010
 * Time: 6:40:51 PM
 */
class ConnectionInfo
{
  String url
  String username
  String password
  String driverClassName

  def Sql connect() {
    Sql.newInstance(url, username, password, driverClassName)
  }
}
