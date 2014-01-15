<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Consolidar Atividade Acadêmica </h2>

	<center>
		<div class="infoAltRem">
		    <h:graphicImage value="/img/graduacao/coordenador/validar.png" style="overflow: visible;"/>: Consolidar
			<h:graphicImage value="/img/email_go.png" style="overflow: visible;"/>: Enviar Mensagem
			<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar<br/> 
		</div>
	</center>
	
	<h:outputText value="#{registroAtividade.create}" />
	<table class="listagem">
		<caption> Discentes </caption>
		<thead>
			<tr>
				<th> Discente </th>
				<th> Atividade</th>
				<th width="10%"></th>
			</tr>
		</thead>
		<tbody>
			<c:set var="atividades" value="${registroAtividade.atividadesOrientandos}"/>
		
			<c:if test="${not empty atividades}">
				<c:forEach var="atividade" items="${atividades}" varStatus="loop">
				<tr class="${ loop.index % 2 == 0 ? 'linhaPar': 'linhaImpar' }">
					<td> ${atividade.matricula.discente} </td>
					<td> ${atividade.matricula.componente} </td>
					<td>
						<div style="float: left; margin : 0 5px 0 5px;">
							<c:if test="${atividade.matricula.componente.necessitaMediaFinal}">
							<h:form>
								<input type="hidden" value="${atividade.matricula.id}" name="id"/>
								<h:commandButton image="/img/graduacao/coordenador/validar.png" id="consolidar" title="Consolidar" action="#{registroAtividade.iniciarConsolidacaoOrientador}" style="border: 0;"/>
							</h:form>
							</c:if>
						</div>
						<div style="float: left; margin : 0 5px 0 5px;">
							<c:if test="${not empty atividade.matricula.discente.usuario}">
								<a href="javascript://nop/" onclick="mensagem.show(<%=""+br.ufrn.arq.caixa_postal.Mensagem.MENSAGEM%>, null, '${atividade.matricula.discente.usuario.login}' );">
									<img src="/sigaa/img/email_go.png" alt="Enviar Mensagem" title="Enviar Mensagem"/>
								</a>
							</c:if>
							<c:if test="${empty atividade.matricula.discente.usuario}">
								<img src="/sigaa/img/email_disabled.png" alt="Sem Caixa Postal" title="Usuário sem Caixa Postal"/>
							</c:if>	
						</div>
						<div style="float: left; margin : 0 5px 0 5px;">
							<c:if test="${!atividade.matricula.componente.necessitaMediaFinal}">
							<h:form>
								<input type="hidden" value="${atividade.matricula.id}" name="id"/>
								<h:commandButton image="/img/view.gif" id="visualizar" title="Visualizar" action="#{registroAtividade.viewAtividadeOrientando}" style="border: 0;"/>
							</h:form>
							</c:if>
						</div>
					</td>				
				</tr>
				</c:forEach>
			</c:if>
			
			<c:if test="${empty atividades}">
			<tr>
				<td colspan="3">
				<br><div style="font-style: italic; text-align:center">Não há nenhuma atividade de orientandos seus para consolidar</div>
				</td>
			</tr>
			</c:if>
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
