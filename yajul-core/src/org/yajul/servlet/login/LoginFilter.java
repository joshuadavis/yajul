package org.yajul.servlet.login;

import org.apache.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;

/**
 * Adds the logged in Principal to the HttpSession, if it is available.  This way, unsecured URIs can
 * have access to the logged in user.
 * Created by IntelliJ IDEA.
 * User: josh
 * Date: Jun 28, 2003
 * Time: 12:21:03 PM
 */
public class LoginFilter implements Filter
{
    private static Logger log = Logger.getLogger(LoginFilter.class);

    public void init(FilterConfig filterConfig) throws ServletException
    {
        if (log.isDebugEnabled())
            log.debug("init()");
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException
    {
        if (servletRequest instanceof HttpServletRequest)
        {
            HttpServletRequest request = (HttpServletRequest)servletRequest;
            HttpSession session = request.getSession(false);

            // Requests for unsecured web resources will not have a 'user principal' associated
            // with them.   If any unsecured web resources want to know if the user is logged in,
            // then this will have to be tracked in the session.
            Principal userPrincipal = request.getUserPrincipal();
            if (session != null && userPrincipal != null)
            {
                Principal sessionUserPrincipal = (Principal) session.getAttribute("userPrincipal");
                if (sessionUserPrincipal == null)
                {
                    if (log.isDebugEnabled())
                        log.debug("doFilter() : User principal registered into the session.");
                    session.setAttribute("userPrincipal",userPrincipal);
                }
                else
                {
                    if (!sessionUserPrincipal.getName().equals(userPrincipal.getName()))
                    {
                        throw new ServletException("ERROR: Session principal does not match request principal!");
                    }
                }
            }

            if (log.isDebugEnabled())
                log.debug("doFilter() : URI = " + request.getRequestURI() + " context path = " + request.getContextPath());
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    public void destroy()
    {
        if (log.isDebugEnabled())
            log.debug("destroy()");
    }
}
