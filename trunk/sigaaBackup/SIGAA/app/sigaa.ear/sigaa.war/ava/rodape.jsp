			</div>
		</div>
		
		<%@page import="br.ufrn.arq.util.AmbienteUtils"%>
		
		<div id="rodape" class="ui-layout-south">
			<p>	${ configSistema['siglaSigaa']} | ${ configSistema['nomeResponsavelInformatica']} - ${ configSistema['telefoneHelpDesk'] } | Copyright &copy; <%= br.ufrn.arq.util.UFRNUtils.getCopyright(2006) %> - ${configSistema['siglaInstituicao']} - <%= br.ufrn.arq.util.AmbienteUtils.getNomeServidorComInstancia() %>
			<c:if test="${acesso.acessibilidade}">
				<img src="${ctx}//img/modoAcessibilidade.png" width="15px;">
			</c:if>
			<%  if (AmbienteUtils.dadosVersao("versao_sigaa") != null) { %> 
				 - <a href="#" style="color:#FFF;" onclick="javascript:versao();return false;">v<%=AmbienteUtils.dadosVersao("versao_sigaa").get("sistema") %></a>
			<%} %>		
			</p>
		</div>
		
		<c:if test="${ sessionScope.alertErro != null }">
			<script type="text/javascript">
				alert('${ sessionScope.alertErro }');
			</script>
			<c:remove var="alertErro" scope="session"/>
		</c:if>
		
		<script>
			J(document).ready(function (){
				esconderLoading();
			});
	
			if (YAHOO.util.Event == null)
				YAHOO.util.Event = YahooEvent;
		</script>
		
		<div id="mensagem">
			<div class="x-window-header"></div>
			<div class="x-window-body"></div>
		</div>
		</div> <%-- Base Layout --%>
		
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
</body>
</html>



<script type="application/javascript" src="/sigaa/ava/javascript/iscroll.js"></script>
<script type="text/javascript">
var ua = navigator.userAgent.toLowerCase();
if (ua.indexOf('android') != -1 || ua.indexOf('ipad') != -1 || ua.indexOf('ipod') != -1 || ua.indexOf('iphone') != -1) { // identifica a requisição através de um dispositivo móvel (smartphone ou tablet)
	var myScroll;
	function loaded() {
		setTimeout(function () {
			myScroll = new iScroll('conteudo', { zoom: true });
		}, 100);
	}
	window.addEventListener('load', loaded, false);
}
</script>