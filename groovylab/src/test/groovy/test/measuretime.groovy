package test

/**
 * TODO: Class level comments!
 * <br>
 * User: Josh
 * Date: Feb 27, 2010
 * Time: 3:04:25 PM
 */

long start = System.currentTimeMillis()
long millis = 5000
for (i in 0..100) {
  long now = System.currentTimeMillis()
  println "elapsed=${now - start}, remaining=${(start + millis) - now}"
  Thread.sleep(100)
}