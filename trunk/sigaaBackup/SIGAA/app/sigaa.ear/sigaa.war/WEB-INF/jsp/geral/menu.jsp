<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 
 


<h2>
	Menu de Cadastros Gerais
</h2>	
<hr>
<div class="menuEsquerda">
	<%@include file="/WEB-INF/jsp/geral/menu/cadastro.jsp"%>
</div>


<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
