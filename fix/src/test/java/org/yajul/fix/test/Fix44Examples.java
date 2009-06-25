package org.yajul.fix.test;

/**
 * Example FIX 4.4 messages.
 * <br>
 * User: josh
 * Date: Jun 25, 2009
 * Time: 4:58:44 PM
 */
public interface Fix44Examples
{
    String HEARTBEAT = "8=FIX.4.4\u00019=87\u000135=0\u000134=39\u000149=PEPTESTING\u000152=20090625-20:41:47.165\u000156=TESTCOMPANY1\u000157=testtrader1-salut\u000110=026\u0001";
    String EXECUTION_REPORT = "8=FIX.4.4\u00019=315\u000135=8\u000134=8123\u000149=PEPTESTING\u000152=20090625-20:41:43.143\u000156=TESTCOMPANY1\u00011=testtrader1\u00016=0\u000111=testtrader1_421666508\u000114=0\u000117=104030+202\u000137=104030\u000138=100\u000139=4\u000144=0.54\u000154=1\u000155=NG SDO\u000158=Removed, Killed by user\u000159=0\u000160=20090625-20:41:39.092\u0001150=4\u0001151=100\u0001202=7.8\u0001461=OPEFCS\u0001541=20090625\u0001454=1\u0001455=NG SDO N9 7.80 Put\u0001456=PEP\u000110=150\u0001";
    String NEW_ORDER_SINGLE = "8=FIX.4.4\u00019=208\u000135=D\u000134=3\u000149=TESTCOMPANY1\u000150=testtrader1-salut\u000152=20090625-19:07:21.173\u000156=PEPTESTING\u00011=testtrader1\u000111=1245956841126\u000138=200\u000140=2\u000144=0.05\u000154=1\u000155=WT SDO\u000160=20090625-19:07:21.157\u0001202=69\u0001461=OPEFCS\u0001541=20090625\u000110=017\u0001";
    String QUOTE_REQUEST = "8=FIX.4.4\u00019=295\u000135=R\u000134=26\u000149=PEPTESTING\u000152=20090625-19:12:19.525\u000156=TESTCOMPANY1\u000157=testtrader1-salut\u0001131=5425\u0001146=1\u000155=WTI APO\u0001454=1\u0001455=WTI APO X9-H10 66.00 Call vs 74.55 30d\u0001456=PEP\u0001555=2\u0001600=WTI APO\u0001608=OCEFCS\u0001610=200911\u0001612=66\u0001623=1\u00017540=5\u0001600=WTI SWAP CAL\u0001608=FFXXXX\u0001610=200911\u0001612=74.55\u0001623=-0.3\u00017540=5\u000110=046\u0001";
    String REJECT = "8=FIX.4.4\u00019=137\u000135=3\u000134=28\u000149=PEPTESTING\u000152=20090625-19:12:40.931\u000156=TESTCOMPANY1\u000157=testtrader1-salut\u000145=19\u000158=Required tag missing\u0001371=295\u0001372=i\u0001373=1\u000110=130\u0001";
    String QUOTE_STATUS_REQUEST = " 8=FIX.4.4\u00019=95\u000135=a\u000134=20\u000149=TESTCOMPANY1\u000150=testtrader1-salut\u000152=20090625-19:13:00.931\u000156=PEPTESTING\u0001649=QR1\u000110=235\u0001";
    String MASS_QUOTE_ACK = "8=FIX.4.4\u00019=135\u000135=b\u000134=56\u000149=PEPTESTING\u000152=20090625-19:20:30.724\u000156=TESTCOMPANY1\u000157=testtrader1-salut\u00011=testtrader1\u0001117=qid\u0001297=0\u0001296=1\u0001302=QS1\u0001304=0\u000110=055\u0001";
}
