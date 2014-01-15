<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.SAE } %>">
	<c:redirect url="menu.jsf" />
</ufrn:checkRole>

