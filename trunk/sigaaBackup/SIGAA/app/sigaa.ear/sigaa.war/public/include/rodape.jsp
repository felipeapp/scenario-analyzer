	<%@page import="br.ufrn.arq.util.AmbienteUtils"%>
	<div class="clear"></div>
	</div>

	<div id="rodape">
		<p>	
			${ configSistema['siglaSigaa']} | ${ configSistema['nomeResponsavelInformatica']} - ${ configSistema['telefoneHelpDesk'] } | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['siglaInstituicao']} - <%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %>
			<%  if (AmbienteUtils.dadosVersao("versao_sigaa") != null) { %> 
				<a onclick="javascript:versao();">v<%=AmbienteUtils.dadosVersao("versao_sigaa").get("sistema") %></a>
			<%} %>
		</p>
	</div>

	<!-- Fim dos containers -->
	</div>
	</div>
</body>

</html>
<c:if test="${ sessionScope.alertErro != null }">
<script type="text/javascript">
	alert('${ sessionScope.alertErro }');
</script>
<c:remove var="alertErro" scope="session"/>
</c:if>
<%  if (AmbienteUtils.dadosVersao("versao_sigaa") != null) { %> 
	<script type="text/javascript" charset="UTF-8">
		function versao(){
			var msg='';
			msg+='SIGAA <%=AmbienteUtils.dadosVersao("versao_sigaa").get("sistema") %>,  publicado em: <%=AmbienteUtils.dadosVersao("versao_sigaa").get("dataPublicacao") %>\n\n';
			msg+='Depend\u00eancias:\n';
			msg+='Arquitetura <%=AmbienteUtils.dadosVersao("versao_sigaa").get("arquitetura") %>\n';
			msg+='Entidades Comuns <%=AmbienteUtils.dadosVersao("versao_sigaa").get("entidadesComum") %>\n';
			msg+='Servicos Integrados <%=AmbienteUtils.dadosVersao("versao_sigaa").get("servicosIntegrados") %>\n\n';
			msg+='Copyright SINFO/UFRN';
			alert(msg);
		}	
	</script>
<%} %>
<style>
a.linkMobile:link {
	color: #003395;
	font-weight: bold;
	font-size: inherit;
	text-decoration: none;
}

a.linkMobile:visited {
	color: #003390;
	text-decoration: none;
	font-weight: bold;
}

a.linkMobile:active {
	color: #444444;
	text-decoration: none;
	font-weight: bold;
}
</style>

<%
	String uagent = request.getHeader("User-Agent").toLowerCase();
	String modo = request.getParameter("modo");
	if (modo == "mobile" || (uagent != null && AmbienteUtils.isMobileUserAgent(uagent))) { %>
		<br/>
		<p align="center">
			<a class="linkMobile" href="/sigaa/mobile/touch/public/principal.jsf">Modo Mobile</a> |
			Modo Clássico 
		</p>
		<br/>
	<%}%>