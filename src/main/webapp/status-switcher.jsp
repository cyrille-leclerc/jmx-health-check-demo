<%@ page import="javax.management.MBeanServerFactory" %>
<%@ page import="javax.management.MBeanServer" %>
<%@ page import="javax.management.ObjectName" %>
<%@ page import="java.util.Set" %>
<%!
    MBeanServer mbeanServer = MBeanServerFactory.findMBeanServer(null).get(0);
%>
<%

    String statusCode = request.getParameter("statusCode");
    String statusReasonPhrase = request.getParameter("statusReasonPhrase");

    if(statusCode == null || statusCode.isEmpty()) {
        out.println("Ignore missing or empty 'statusCode'");
    } else {

    }

    Set<ObjectName> objectNames = mbeanServer.queryNames(new ObjectName("mycompany:type=ApplicationStatus,name=ApplicationStatus,*"), null);
    String msg;
    if (objectNames.isEmpty()) {
        // Application status has not bee overridden, just return 200 OK
        msg = "Application status has not been overridden";
    } else {
        ObjectName objectName = objectNames.iterator().next();
        Object statusCode = mbeanServer.getAttribute(objectName, "StatusCode");
        Object statusReasonPhrase = mbeanServer.getAttribute(objectName, "StatusReasonPhrase");

        if (statusCode == null) {
            application.log("Ignore unexpected null 'StatusCode' attribute on " + objectName);
            msg = "Ignore unexpected null 'StatusCode' attribute on " + objectName;

        } else if (statusCode instanceof Number) {
            int statusCodeAsInt = ((Number) statusCode).intValue();
            if (statusCodeAsInt == HttpServletResponse.SC_OK) {
                response.setStatus(statusCodeAsInt);
                msg = "Overridden " + statusCodeAsInt;

            } else if (statusReasonPhrase == null) {
                response.sendError(statusCodeAsInt);
                msg = "Overridden " + statusCodeAsInt;

            } else {
                response.sendError(statusCodeAsInt, statusReasonPhrase.toString());
                msg = "Overridden " + statusCodeAsInt +  ":" + statusReasonPhrase;

            }
        } else {
            application.log("Ignore unexpected 'StatusCode' attribute " + statusCode + "(type: " + statusCode.getClass() + ") on " + objectName);
            msg = "Ignore unexpected 'StatusCode' attribute " + statusCode + "(type: " + statusCode.getClass() + ") on " + objectName;
        }
    }

    out.println(msg);
%>
<html>

</html>
