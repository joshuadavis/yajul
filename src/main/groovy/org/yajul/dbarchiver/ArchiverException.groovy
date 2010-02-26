package org.yajul.dbarchiver

/**
 * Thrown when there is an error.
 * <br>
 * User: josh
 * Date: Feb 22, 2010
 * Time: 5:21:37 PM
 */
class ArchiverException extends RuntimeException {

  def ArchiverException() {
    super();
  }

  def ArchiverException(String s) {
    super(s);
  }

  def ArchiverException(String s, Throwable throwable) {
    super(s, throwable);
  }

  def ArchiverException(Throwable throwable) {
    super(throwable);
  }

}
