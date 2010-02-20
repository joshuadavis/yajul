import groovy.sql.Sql

@Grapes([
  @Grab('mysql:mysql-connector-java:5.1.10'),
  @GrabConfig(systemClassLoader=true)
])

// select p.page_title,r.rev_text_id,t.old_text
// from page p join revision r on r.rev_id = p.page_latest
// join text t on t.old_id = r.rev_text_id;
def username = 'sa', password = 'sa', database = 'wikidb', server = 'localhost'

def driverClass = 'com.mysql.jdbc.Driver'
println "instantiating ${driverClass} ..."
Class.forName(driverClass).newInstance()

println "query..."
// Create connection to MySQL with classic JDBC DriverManager.
def db = Sql.newInstance("jdbc:mysql://$server/$database", username, password, driverClass)

db.eachRow("select p.page_title from page p") {
  println "${it.page_title}"
}