/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
  *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.jbossas.embedded.examples.slsb;

import javax.ejb.Local;
import javax.ejb.Stateless;

import org.jboss.ejb3.annotation.LocalBinding;

/**
 * Stateless Session implementation of {@link EchoLocalBusiness}, 
 * an EJB which echos messages from the caller
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
@Stateless
@Local(EchoLocalBusiness.class)
@LocalBinding(jndiBinding = EchoLocalBusiness.JNDI_NAME)
public class EchoBean implements EchoLocalBusiness
{
   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.jbossas.embedded.examples.slsb.EchoLocalBusiness#echo(java.lang.String)
    */
   public String echo(final String message)
   {
      // Return
      return message;
   }

}
