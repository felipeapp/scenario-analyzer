<%@page import="br.ufrn.arq.util.AmbienteUtils"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>  

<br>
<center>
	<a href="javascript:window.close();"><html:img page="/img/fechar.jpg" width="85" height="16" alt="Fechar" border="0"/></a>
</center>
<br>
<table width="100%" class="rodape" align="center" cellspacing="0" cellpadding="0">
	<tr>
		<td width="400" align="center" class="azulMedio">	
		${ configSistema['siglaSigaa']} | ${ configSistema['nomeResponsavelInformatica']} - ${ configSistema['telefoneHelpDesk'] } | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['siglaInstituicao']} - <%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %> 
		</td>
		<td width="30" height="20" class="divisaoRodape"></td>
		<td width="220" class="cinzaCabecalho"></td>
	</tr>
</table>
