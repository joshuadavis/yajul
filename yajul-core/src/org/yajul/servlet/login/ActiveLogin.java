package org.yajul.servlet.login;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * A protected resource that provides login from unprotected URIs.  This
 * Servlet collaborates with /login/login.jsp and ActiveLoginBean to force
 * the web container to do some authentication, and then to return to the
 * 'originator' URL.
 * <br>
 * NOTE: This servlet must be deployed to a URI that is protected by security
 * constraints, the ActiveLoginBean will be redirecting to it in order to
 * force the login form to be displayed.
 * <br>User: josh
 * Date: Jun 7, 2003
 * Time: 5:42:02 PM
 */
public class ActiveLogin extends HttpServlet
{
    private static Logger log = Logger.getLogger(ActiveLogin.class);


    /** The deployment URI of this servlet. **/
    private static String deployedURI = null;

    public ActiveLogin()
    {
        log.info("<ctor>");
    }

    /**
     * Returns the URI where the ActiveLogin Servlet is deployed.
     * @return String - The URI for the ActiveLogin servlet.
     */
    public static String getDeployedURI()
    {
        synchronized (ActiveLogin.class)
        {
            if (deployedURI == null || deployedURI.length() == 0)
                throw new IllegalStateException("The ActiveLogin servlet has not been initialized.  Please check the web deployment descriptor.");
            return deployedURI;
        }
    }

    /**
     * Initializes the active login servlet.
     * @param servletConfig The servlet configuration.
     * @throws javax.servlet.ServletException if something goes wrong with the initialization.
     */
    public void init(ServletConfig servletConfig) throws ServletException
    {
        super.init(servletConfig);
        if (log.isDebugEnabled())
            log.debug("init() : ENTER");
        // The URI that will be used to access this servlet.
        synchronized (ActiveLogin.class)
        {
            deployedURI = servletConfig.getInitParameter("deployedURI");
            log.info("init() : deployedURI = " + deployedURI);
        }
        if (log.isDebugEnabled())
            log.debug("init() : LEAVE");
    }

    /**
     * Services the request.
     * @param request The request.
     * @param response The response.
     * @throws javax.servlet.ServletException If the servlet cannot service the request.
     * @throws java.io.IOException If there was an unexpected IO problem.
     */
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        if (log.isDebugEnabled())
            log.debug("service() : ENTER");
        // AUTHENTICATION
        ActiveLoginBean loginBean = ActiveLoginBean.getInstance(request, false);
        String url = "/index.jsp";
        if (loginBean == null)
            log.warn("ActiveLogin servlet was invoked, but there is no ActiveLoginBean!");
        else if (loginBean != null)
        {
            loginBean.forwardAfterLogin(request, response);
            return;
        }
        // Forward the request to the desired URL.
        request.getRequestDispatcher(url).forward(request, response);

        if (log.isDebugEnabled())
            log.debug("service() : LEAVE");
    }
}
