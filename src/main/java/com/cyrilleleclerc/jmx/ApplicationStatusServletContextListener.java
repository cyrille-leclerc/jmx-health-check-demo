/*
 * Copyright (c) 2010-2013 the original author or authors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package com.cyrilleleclerc.jmx;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

/**
 * @author <a href="mailto:cleclerc@cloudbees.com">Cyrille Le Clerc</a>
 */
public class ApplicationStatusServletContextListener implements ServletContextListener {

    private MBeanServer mbeanServer;
    private ObjectName objectName;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        List<MBeanServer> mbeanServers = MBeanServerFactory.findMBeanServer(null);
        if (mbeanServers.isEmpty()) {
            servletContext.log("Unable to retrieve the MBean server, skip ApplicationStatus MBean registration");
            return;
        }
        mbeanServer = mbeanServers.get(0);

        ApplicationStatus applicationStatus = new ApplicationStatus();

        ObjectName on = null;
        try {
            on = new ObjectName("mycompany:type=ApplicationStatus,name=ApplicationStatus");
            objectName = mbeanServer.registerMBean(applicationStatus, on).getObjectName();
            servletContext.log("Successful registration of ApplicationStatusMBean '" + objectName + "'");
        } catch (Exception e) {
            servletContext.log("Failure to register ApplicationStatusMBean '" + on + "'", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        if (mbeanServer == null || objectName == null) {
            return;
        }
        try {
            mbeanServer.unregisterMBean(objectName);
            servletContext.log("Successful un-registration of ApplicationStatusMBean '" + objectName + "'");
        } catch (Exception e) {
            servletContext.log("Failure to register ApplicationStatusMBean '" + objectName + "'", e);
        }
    }
}
