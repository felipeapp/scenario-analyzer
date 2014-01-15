<%@include file="../include/cabecalho.jsp"%>

<style>
	.calendario tr td,.calendario tr th {
		font-size: 20pt;
		text-align: center;
	}
</style>

<f:view>
	<a4j:keepAlive beanName="frequenciaAluno" />
	<div data-role="page" id="page-lancar-frequencia" data-theme="b">
   		<h:form id="formLancarFrequencia">
			<div data-role="header" data-position="inline" data-theme="b">
				<h1 style="white-space: normal;">${ configSistema['siglaInstituicao']} - ${configSistema['siglaSigaa'] } Mobile</h1>
			</div>
			
			<div data-role="navbar" data-theme="b" data-iconpos="left">
				<ul>
					<li><h:commandLink id="lnkVoltar" value="Voltar" action="#{ portalDocenteTouch.sairCalendarios }" /></li>
					<li><h:commandLink value="Início" action="#{ portalDocenteTouch.acessarPortal }" id="lnkInicio"/></li>
					<li><h:commandLink value="Sair" action="#{ loginMobileTouch.logoff }" id="lnkSair" onclick="#{logout }" /></li>
				</ul>
			</div>
	
			<div data-role="content">
				<p align="center"><small><strong><h:outputText value="#{ portalDocenteTouch.turma.disciplina.codigoNome} (#{portalDocenteTouch.turma.anoPeriodo})" escape="false" /></strong></small></p>
    			<p align="center"><strong><h:outputText value="Lançar Frequência" /></strong></p>
    			
    			<%@include file="/mobile/touch/include/mensagens.jsp"%>
    			<c:set var="contMes" value="#{0 }" />
    			
    			<div data-role="collapsible-set" data-content-theme="b">
    			<c:forEach var="c" varStatus="status" items="#{frequenciaAluno.calendarios }">
    				<c:if test="${frequenciaAluno.calendarios[status.index-1].nomeMes != c.nomeMes}">
						<c:set var="calendario" value="#{c.nomeMes }" />
						<c:set var="contMes" value="#{contMes+1 }" />
						
						<h:outputText value="</table></div></div>" escape="false" rendered="#{contMes > 1 }" />
						<h:outputText value="<div data-role='collapsible' data-theme='b' data-content-theme='a' data-collapsed='true'><h3>#{ c.nomeMes }</h3><div style='text-align:center;'><table class='calendario' align='center'><tr><th style='color:#FF0000;'>D</th><th>S</th><th>T</th><th>Q</th><th>Q</th><th>S</th><th>S</th></tr>" escape="false" />
    				</c:if>
					
					<h:outputText value="<tr>" escape="false" rendered="#{c.diaDaSemana == 0 }" />
					
					<td style="padding:4px;
						<h:outputText value="background:#FFFF99;" rendered="#{c.semAula }" />
						<h:outputText value="color:#FF0000;" rendered="#{c.feriado }" />
						<h:outputText value="background:#6CDF46;" rendered="#{c.frequenciaLancada == true }" />">
						
						<h:outputText value="#{c.dia }" rendered="#{c.dia > 0 && !c.exibirLink }" />
						<h:commandLink action="#{portalDocenteTouch.lancarFrequencia }" value="#{c.dia }" rendered="#{c.dia > 0 && c.exibirLink }">
							<f:param name="dia" value="#{c.dia }" />
							<f:param name="mes" value="#{c.mes }" />
							<f:param name="ano" value="#{c.ano }" />
						</h:commandLink>
					</td>
					
					<h:outputText value="</tr>" escape="false" rendered="#{c.diaDaSemana == 6 }" />
				</c:forEach>
				
				<h:outputText value="</table></div></div>" escape="false" />
				</div>
				
				<script>
					$("#formLancarFrequencia\\:lnkVoltar").attr("data-icon", "back");
					$("#formLancarFrequencia\\:lnkInicio").attr("data-icon", "home");
					$("#formLancarFrequencia\\:lnkSair").attr("data-icon", "sair");
				</script>
			</div>
			<%@include file="/mobile/touch/include/rodape_copyright.jsp"%>
  		</h:form>
  		<%@include file="../include/modo_classico.jsp"%>
	</div>
</f:view>
<%@include file="/mobile/touch/include/rodape.jsp"%>
