<%@page import="br.ufrn.arq.util.AmbienteUtils"%>
<%@page import="br.ufrn.sigaa.dominio.Usuario" %>
<div class="clear"> </div>
<c:if test="${param.ajaxRequest == null}">
</div> <%-- Fim do div 'conteudo' --%>
	<br />

	<c:if test="${not empty sessionScope.usuario}">
	<c:if test="${hideSubsistema == null}">
	<div style="width: 80%; text-align: center; margin: 0 auto;">
		<ufrn:subSistema/>
	</div>
	</c:if>
	</c:if>

	<div id="rodape">
		<p>	${ configSistema['siglaSigaa']} | ${ configSistema['nomeResponsavelInformatica']} - ${ configSistema['telefoneHelpDesk'] } | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['siglaInstituicao']} - <%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %>
		<c:if test="${acesso.acessibilidade}">
			<img src="${ctx}//img/modoAcessibilidade.png" width="15px;">
		</c:if>
		<%  if (AmbienteUtils.dadosVersao("versao_sigaa") != null) { %> 
			 - <a onclick="javascript:versao();">v<%=AmbienteUtils.dadosVersao("versao_sigaa").get("sistema") %></a>
		<%} %>		
		</p>
	</div>


</div>  <%-- Fim do div 'container' --%>

<div id="painel-mensagem-envio"> </div>

</c:if>

</body>

</html>
<%  if (AmbienteUtils.dadosVersao("versao_sigaa") != null) { %> 
	<script type="text/javascript" charset="UTF-8">
		function versao(){
			var msg='';
			msg+='${ configSistema['siglaSigaa'] } <%=AmbienteUtils.dadosVersao("versao_sigaa").get("sistema") %>,  publicado em: <%=AmbienteUtils.dadosVersao("versao_sigaa").get("dataPublicacao") %>\n\n';
			msg+='Depend\u00eancias:\n';
			msg+='Arquitetura <%=AmbienteUtils.dadosVersao("versao_sigaa").get("arquitetura") %>\n';
			msg+='Entidades Comuns <%=AmbienteUtils.dadosVersao("versao_sigaa").get("entidadesComum") %>\n';
			msg+='Servicos Integrados <%=AmbienteUtils.dadosVersao("versao_sigaa").get("servicosIntegrados") %>\n\n';
			msg+='Copyright SINFO/UFRN';
			alert(msg);
		}	
	</script>
<%} %>
<c:if test="${ sessionScope.alertErro != null }">
<script type="text/javascript">
	alert('${ sessionScope.alertErro }');
</script>
<c:remove var="alertErro" scope="session"/>
</c:if>
<script language="javascript">
Relogio.init(<%= session.getMaxInactiveInterval() / 60 %>);
</script>
<%
	String uagent = request.getHeader("User-Agent").toLowerCase();
	Usuario usuario = (Usuario) request.getSession().getAttribute("usuario");
	boolean multiplosVinculos;
	
	try {
		multiplosVinculos = usuario.getVinculosPrioritarios().size() > 1;
	} catch (Exception e){
		multiplosVinculos = false;
	}
	
	if (uagent != null && AmbienteUtils.isMobileUserAgent(uagent)) { %>
		<br/>
		<p align="center">
			<% if(usuario == null) { %>
				<a href="/sigaa/mobile/touch/login.jsf" >Modo Mobile</a> | Modo Clássico
			<%} else {%>
				<% if(multiplosVinculos) { %>
					<a href="/sigaa/mobile/touch/vinculos.jsf" >Modo Mobile</a> | Modo Clássico
				<%} else {%> 	
					<a href="/sigaa/mobile/touch/menu.jsf" >Modo Mobile</a> | Modo Clássico
				<%}%>
			<%}%> 
		</p>	
		<br/>	
	<%}%>