package org.yajul.servlet.login;

import org.apache.log4j.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.security.Principal;

/**
 * Unprotected servlet that processes login commands.
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Sep 23, 2003
 * Time: 7:32:44 AM
 */
public class Login extends HttpServlet
{
    private static Logger log = Logger.getLogger(Login.class);

    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        if (log.isDebugEnabled())
            log.debug("service() : ENTER");
        if (log.isDebugEnabled())
            log.debug("service()");

        String command = request.getParameter("command");
        if (log.isDebugEnabled())
            log.debug("command = " + command);


        HttpSession session = request.getSession(false);
        if (session == null)
        {
            String message = "login".equals(command) ?
                                "Please log in." :
                                "There is no logged in user (no session)";
            if (log.isDebugEnabled())
                log.debug(message);
            forward("/webui", request, response);
            return;
        }

        // NOTE: The principal will not be set in the request if the web application descriptor does not
        // declare a security role for the URI that accesses this servlet.
        // So, we must rely on LoginFilter to put the current principal into the session.
        // Get the principal from the session before the session gets invalidated.
        Principal userPrincipal = (Principal) session.getAttribute("userPrincipal");

        if (log.isDebugEnabled())
            log.debug("Invalidating session " + session.getId() + " ...");

        // Invalidate the session.
        session.invalidate();

        if (log.isDebugEnabled())
            log.debug("Session " + session.getId() + " invalidated.");

        // Forward to the login page.
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        forward("login".equals(command) ? "/webui" : "/", request, response);
        return;
    }

    private void forward(String uri, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException, IOException
    {
        httpServletRequest.getRequestDispatcher(uri).forward(
                httpServletRequest,httpServletResponse);
    }


}