import groovy.sql.Sql
import org.wikimodel.wem.mediawiki.MediaWikiParser
import org.wikimodel.wem.IWemListener
import org.wikimodel.wem.WikiParameters
import org.wikimodel.wem.WikiFormat
import javax.xml.parsers.DocumentBuilderFactory
import groovy.xml.XmlUtil
import groovy.grape.Grape


Grape.grab(group: 'mysql', module: 'mysql-connector-java', version: '5.1.10', classLoader: this.class.classLoader.rootLoader)

/*
@Grapes([
  @Grab('mysql:mysql-connector-java:5.1.10'),
  @GrabConfig(systemClassLoader=true)
])
*/

class DomCreator implements IWemListener {
  private builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
  private document
  private page
  private elementStack

  DomCreator() {
  }

  void beginDocument() {
    document = builder.newDocument()
    page = document.createElement('page')
    elementStack = [ page ]
  }

  void endDocument() {
  }

  void beginHeader(int level, WikiParameters params) {
    println "beginHeader ${level}"
    def header = document.createElement("h${level}")
    elementStack[elementStack.size - 1].addChild(header)
    elementStack.add(header)
  }

  void endHeader(int level, WikiParameters params) {
    println "endHeader ${level}"
    def h = elementStack.removeLast()
    println "h=$h";
  }


  void beginDefinitionList(WikiParameters params) {
    println "beginDefinitionList"    
    params.toList().each { println "> ${it}"}
  }

  void endDefinitionList(WikiParameters params) {
    println "endDefinitionList"
    params.toList().each { println "> ${it}"}
  }


  void beginDefinitionDescription() {
    // TODO: Implement this!

  }

  void beginDefinitionTerm() {
    // TODO: Implement this!

  }

  void beginFormat(WikiFormat format) {
    // TODO: Implement this!

  }

  void beginInfoBlock(char infoType, WikiParameters params) {
    // TODO: Implement this!

  }

  void beginList(WikiParameters params, boolean ordered) {
    // TODO: Implement this!

  }

  void beginListItem() {
    // TODO: Implement this!

  }

  void beginParagraph(WikiParameters params) {
    // TODO: Implement this!

  }

  void beginPropertyBlock(String propertyUri, boolean doc) {
    // TODO: Implement this!

  }

  void beginPropertyInline(String str) {
    // TODO: Implement this!

  }

  void beginQuotation(WikiParameters params) {
    // TODO: Implement this!

  }

  void beginQuotationLine() {
    // TODO: Implement this!

  }

  void beginTable(WikiParameters params) {
    // TODO: Implement this!

  }

  void beginTableCell(boolean tableHead, WikiParameters params) {
    // TODO: Implement this!

  }

  void beginTableRow(WikiParameters params) {
    // TODO: Implement this!

  }

  void endDefinitionDescription() {
    // TODO: Implement this!

  }

  void endDefinitionTerm() {
    // TODO: Implement this!

  }

  void endFormat(WikiFormat format) {
    // TODO: Implement this!

  }

  void endInfoBlock(char infoType, WikiParameters params) {
    // TODO: Implement this!

  }

  void endList(WikiParameters params, boolean ordered) {
    // TODO: Implement this!

  }

  void endListItem() {
    // TODO: Implement this!

  }

  void endParagraph(WikiParameters params) {
    // TODO: Implement this!

  }

  void endPropertyBlock(String propertyUri, boolean doc) {
    // TODO: Implement this!

  }

  void endPropertyInline(String inlineProperty) {
    // TODO: Implement this!

  }

  void endQuotation(WikiParameters params) {
    // TODO: Implement this!

  }

  void endQuotationLine() {
    // TODO: Implement this!

  }

  void endTable(WikiParameters params) {
    // TODO: Implement this!

  }

  void endTableCell(boolean tableHead, WikiParameters params) {
    // TODO: Implement this!

  }

  void endTableRow(WikiParameters params) {
    // TODO: Implement this!

  }

  void onEmptyLines(int count) {
    // TODO: Implement this!

  }

  void onEscape(String str) {
    // TODO: Implement this!

  }

  void onExtensionBlock(String extensionName, WikiParameters params) {
    // TODO: Implement this!

  }

  void onExtensionInline(String extensionName, WikiParameters params) {
    // TODO: Implement this!

  }

  void onHorizontalLine() {
    // TODO: Implement this!

  }

  void onLineBreak() {
    // TODO: Implement this!

  }

  void onMacroBlock(String macroName, WikiParameters params, String content) {
    // TODO: Implement this!

  }

  void onMacroInline(String macroName, WikiParameters params, String content) {
    // TODO: Implement this!

  }

  void onNewLine() {
    // TODO: Implement this!

  }

  void onReference(String ref, boolean explicitLink) {
    println "onReference: ${ref} ${explicitLink}"
  }

  void onSpace(String str) {
    println "onSpace: ${str}"

  }

  void onSpecialSymbol(String str) {
    println "onSpecialSymbol: ${str}"
  }

  void onTableCaption(String str) {
    // TODO: Implement this!

  }

  void onVerbatimBlock(String str) {
    println "onVerbatimBlock: ${str}"

  }

  void onVerbatimInline(String str) {
    println "onVerbatimInline: ${str}"
  }

  void onWord(String str) {
    println "word: ${str}"
  }

}
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
def parser = new MediaWikiParser()
def domCreator = new DomCreator()
db.eachRow("""select p.page_title, p.page_namespace, t.old_text
  from page p
  join revision r on r.rev_id = p.page_latest
  join text t on t.old_id = r.rev_text_id
  where length(t.old_text) > 0
  and p.page_is_redirect = 0
  and p.page_namespace = 0
  order by p.page_title asc
  limit 1""") {
  def text = new String(it.old_text)
  println "*** ${it.page_title}, ${it.page_namespace} ***"
  println text
  println "***"
  def reader = new StringReader(text)
  parser.parse(reader, domCreator)
  println XmlUtil.serialize(domCreator.page)
}