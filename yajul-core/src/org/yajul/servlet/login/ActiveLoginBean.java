package org.yajul.servlet.login;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * A session-scope bean that is used to store login information
 * when the application wants to force a login from an unprotected page.
 * Manages the state of the programmatic 'active' login.  This is intended to be called from
 * a form based login page using a scriplet such as:
 * <pre>
 * <%
 *    // If an 'active login' was requested, don't show this page.  Just
 *    // allow the ActiveLoginBean to redirect the browser.
 *    if (ActiveLoginBean.redirectToSecurityCheck(request,response))
 *        return;
 * %>
 *
 * </pre>
 * User: josh
 * Date: Sep 23, 2003
 * Time: 6:40:51 AM
 */
public class ActiveLoginBean
{
    /** The bean has been created, but not initialized. **/
    private static final int STATE_UNINITIALIZED = 0;
    /** The bean has been initialized. **/
    private static final int STATE_INITIALIZED = 1;
    /** The bean has been passed through the login process. **/
    private static final int STATE_AFTER_LOGIN = 2;

    /** A logger for this class. **/
    private static Logger log = Logger.getLogger(ActiveLoginBean.class);

    // -- Constants --
    private static final String ATTRIBUTE_NAME = "loginBean";
    private static final String SECURITY_CHECK_URI = "j_security_check";
    private static final String USERNAME_PARAM = "j_username";
    private static final String PASSWORD_PARAM = "j_password";

    // -- Instance variables --
    private String username;
    private String password;
    private String originatorURL;
    private int state;
    private static final int MAX_REDIRECT_ATTEMTPS = 3;

    /**
     * Logs the user in by sending a redirect to the browser.  When invoked from a
     * JSP or a servlet, the calling code must return from the service() method
     * before any output is written.
     * @param request The servlet request.
     * @param response The servlet reponse (used for redirect).
     * @param username The user name to log in as.
     * @param password The user's password.
     * @param originatorURL The URL to go to when the login is complete.
     * @throws java.io.IOException if there was a problem sending the redirect.
     */
    public static void login(HttpServletRequest request, HttpServletResponse response, String username, String password, String originatorURL)
            throws IOException
    {
        // Prepare for active login
        prepareForActiveLogin(request, username, password, originatorURL);
        /**
         NOTE:
         1) Forwarding directly to j_security_check doesn't work with Jetty because the dispatcher
         copies the request and response in to new DispatcherRequest and DispatcherResponse objects.
         This confuses the j_security_check processors, which expects the request and response
         to be ServletHttpRequest and ServletHttpResponse.  Even if the class cast problem did
         not happen, the forward would not navigate to the correct url afterwards.
         2) Forwarding to the ActiveLogin servlet also doesn't work.  Since the container may
         or may not (Jetty doesn't) interpret the request as requiring login.  This is because the
         current page is unprotected.
         **/
        // Redirect to the ActiveLogin servlet.
        // Since this is protected, /login/login.jsp will be invoked before
        // the servlet.  For redirect (as opposed to forwarding), the application context path
        // must be added to the beginning of the url.
        String uri = ActiveLogin.getDeployedURI();
        sendRedirect(response, request, uri);
        return;
    }

    /**
     * Prepares the ActiveLogin bean.  After this, the caller should issue a redirect to
     * a protected URI.
     * @param request The servlet request.
     * @param username The user name to log in as.
     * @param password The user's password.
     * @param originatorURL The URL to go to when the login is complete.
     */
    public static void prepareForActiveLogin(HttpServletRequest request,
                                             String username, String password, String originatorURL)
    {
        // If there is a session, invalidate it.
        if (request.getSession(false) != null)
        {
            HttpSession session = request.getSession(false);
            // Invalidate the session.
            session.invalidate();
            if (log.isDebugEnabled())
                log.debug("Session " + session.getId() + " invalidated due to active login.");
        }

        ActiveLoginBean bean = getInstance(request, true);
        bean.initialize(username, password, originatorURL);
    }

    /**
     * Finds the instance of ActiveLoginBean in the session, creating it if needed.
     * @param request The servlet request.
     * @param create If true, the session and bean will be created if they don't exist.
     * @return The ActiveLoginBean instance, or null if there was no session and 'create' was false.
     */
    public static ActiveLoginBean getInstance(HttpServletRequest request, boolean create)
    {
        HttpSession session = request.getSession(create);
        if (session == null)
        {
            log.warn("login() : No session!");
            return null;
        }
        ActiveLoginBean loginBean = (ActiveLoginBean) session.getAttribute(ATTRIBUTE_NAME);
        if (loginBean == null && create)
        {
            log.info("login() : Creating new ActiveLoginBean.");
            loginBean = new ActiveLoginBean();
            session.setAttribute(ATTRIBUTE_NAME, loginBean);
        }
        return loginBean;
    }

    /**
     * Redirects to the web container's security check processor if there is an
     * active login request in the session.<br>
     * NOTE: This method should be used from the login form of the application
     * before any output is comitted to the browser.  Otherwise, the redirect
     * will fail.
     * @param request The request.
     * @param response The response.
     * @return boolean - True if the active login was performed, false if not.
     * @throws java.io.IOException if there was a problem sending a redirect.
     */
    public static boolean redirectToSecurityCheck(
            HttpServletRequest request,
            HttpServletResponse response)
            throws IOException
    {
        if (log.isDebugEnabled())
            log.debug("redirectToSecurityCheck() : ENTER");
        // Get the bean from the session, but don't create it if it doesn't exist.
        ActiveLoginBean bean = getInstance(request, false);
        // If there is no active login request, do not perform the redirect.
        if (bean == null)
        {
            if (log.isDebugEnabled())
                log.debug("redirectToSecurityCheck() : LEAVE - ActiveLoginBean not found, active login was not requested.");
            return false;
        }
        bean.doSecurityCheck(response);
        if (log.isDebugEnabled())
            log.debug("redirectToSecurityCheck() : LEAVE - Redirected.");
        return true;
    }

    /**
     * Empty constructor, creates an uninitialized ActiveLoginBean.
     */
    public ActiveLoginBean()
    {
        state = STATE_UNINITIALIZED;
    }

    /**
     * Creates a new ActiveLoginBean
     * @param username The user name to log in as.
     * @param password The password.
     * @param originatorURL The URL that will be navigated to when the login completes.
     */
    public ActiveLoginBean(String username, String password, String originatorURL)
    {
        this();
        initialize(username, password, originatorURL);
    }

    /**
     * Returns the originator URL (where to navigate to once login is complete).
     * @return String - The originator URL.
     */
    public String getOriginatorURL()
    {
        return originatorURL;
    }

    /**
     * Returns the current state of the bean (STATE_xxx values).
     * @return int - The STATE_xxx value corresponding to the current state of the bean.
     */
    public int getState()
    {
        return state;
    }

    /**
     * Invoked when the programmatic login is complete, from the ActiveLogin servlet.
     * @param request The servlet request.
     * @param response The servlet response.
     * @throws java.io.IOException if anything goes wrong with the forwarding.
     * @throws javax.servlet.ServletException if anything goes wrong in the forwarded page.
     */
    public void forwardAfterLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
    {
        HttpSession session = request.getSession(false);
        if (session == null)
        {
            log.warn("forwardAfterLogin() : Unable to remove loginBean because there is no session!");
        }
        else
        {
            session.setAttribute(ATTRIBUTE_NAME, null);
            if (log.isDebugEnabled())
                log.debug("forwardAfterLogin() : ActiveLoginBean removed from session.");
        }

        if (state != STATE_AFTER_LOGIN)
        {
            IllegalStateException ise = new IllegalStateException(this.getClass().getName()
                    + ".forwardAfterLogin() invoked when the object was in an unexpected state.");
            log.error("Unexpected state! Throwing: " + ise.getMessage(), ise);
            throw ise;
        }

        String url = originatorURL;     // Remember the url.
        reset();                        // Reset this instance, just in case.
        // Do the forwarding.
        request.getRequestDispatcher(url).forward(request, response);
    }

    /**
     * Initializes the login bean.
     * @param username The user name to log in as.
     * @param password The password.
     * @param originatorURL The URL that will be navigated to when the login completes.
     */
    private void initialize(String username, String password, String originatorURL)
    {
        this.username = username;
        this.password = password;
        this.originatorURL = originatorURL;
        this.state = STATE_INITIALIZED;
        if (log.isDebugEnabled())
            log.debug("initialize() : username='" + username + "' originatorURL='" + originatorURL + "'");
    }

    /**
     * Returns the username.
     * @return String - The username.
     */
    private String getUsername()
    {
        return username;
    }

    /**
     * Returns the password.
     * @return String - The password.
     */
    private String getPassword()
    {
        return password;
    }

    private void reset()
    {
        this.username = null;
        this.password = null;
        this.originatorURL = null;
        this.state = STATE_UNINITIALIZED;
        if (log.isDebugEnabled())
            log.debug("reset()");
    }


    /**
     * Performs the web container login, using the information in the
     * ActiveLoginBean.  Used by the login page to perform the programmatic login.
     * @param response The servlet response.
     * @throws java.io.IOException if there was a problem sending the redirect.
     */
    private void doSecurityCheck(HttpServletResponse response) throws IOException
    {
        if (log.isDebugEnabled())
            log.debug("doSecurityCheck() : ENTER");

        if (state != STATE_INITIALIZED)
        {
            IllegalStateException ise = new IllegalStateException(this.getClass().getName()
                    + ".doSecurityCheck() invoked when the object was in an unexpected state.");
            log.error("Unexpected state!  Throwing: " + ise.getMessage(), ise);
            throw ise;
        }

        // Transition to the 'after login' state.
        state = STATE_AFTER_LOGIN;
        String redirectURL = SECURITY_CHECK_URI
                + "?" + USERNAME_PARAM + "=" + getUsername()
                + "&" + PASSWORD_PARAM + "=" + getPassword();
        try
        {
            if (log.isDebugEnabled())
                log.debug("doSecurityCheck() : Redirecting to " + SECURITY_CHECK_URI);
            response.sendRedirect(response.encodeRedirectURL(redirectURL));
        }
        catch (IOException e)
        {
            log.error("Unexpected exception: " + e.getMessage(), e);
            throw e;
        }

        if (log.isDebugEnabled())
            log.debug("doSecurityCheck() : LEAVE");
    }

    private static void sendRedirect(HttpServletResponse response, HttpServletRequest request, String uri) throws IOException
    {
        boolean loop = false;
        int attempts = 0;
        do
        {
            try
            {
                attempts++;
                response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + uri));
                loop = false;   // If the redirect worked, don't loop.
            }
            catch (IOException e)
            {
                // If an IOException is thrown, this can be because the user caused the browser
                // to do something while the server was trying to send the redirect.
                if (attempts < MAX_REDIRECT_ATTEMTPS)
                {
                    log.error("Unexpected exception: " + e.getMessage() + " retrying redirect...");
                    loop = true;
                }
                else
                {
                    log.error("Unexpected exception: " + e.getMessage(), e);
                    loop = false;   // Yeah, this is a little redundant but what the heck.
                    throw e;
                }
            } // catch
        } // do/while
        while (loop);
    }

}
