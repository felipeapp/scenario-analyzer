<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
	.naoLida { color: red; }
</style>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp"%>
	<h2 class="title"><ufrn:subSistema /> > Solicitações de Apoio de NEE</h2>

	<c:set value="#{solicitacaoApoioNee.obj.discente}" var="discente" />

	<%@include file="/graduacao/info_discente.jsp"%>
	
	<h:form id="lista">
		
		<c:if test="${ solicitacaoApoioNee.infomativoCoordenador != null }">
			<div class="descricaoOperacao">
				<p>${ solicitacaoApoioNee.infomativoCoordenador }</p>
			</div>
		</c:if>
		
		<div class="infoAltRem">
		    <img src="${ctx}/img/alterar.gif"/>: Alterar
			<img src="${ctx}/img/view.gif"/>: Visualizar
		</div>
		
		<table class="listagem">
			<caption>Solicitações de Apoio Encontradas (${fn:length(solicitacaoApoioNee.solicitacoesApoioDiscente)})</caption>
			<thead>
				<tr>
					<th>Discente</th>
					<th>Situação</th>
					<th style="text-align: center;">Data</th>
					<th></th>
					<th></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{solicitacaoApoioNee.solicitacoesApoioDiscente}" var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td valign="top">
							<h:outputText value="#{item.discente.matriculaNome}" />
						</td>
						<td valign="top">
							<h:outputText value="#{item.statusAtendimento.denominacao}" rendered="#{item.lida || solicitacaoApoioNee.solicitacaoCoordenador}" />
							<h:outputText styleClass="naoLida" value="Não Lida" rendered="#{!item.lida && !solicitacaoApoioNee.solicitacaoCoordenador}"/>
						</td>
						<td valign="top" style="text-align: center;">
							<h:outputText value="#{item.dataCadastro}">
								<f:convertDateTime pattern="dd/MM/yyyy"/>
							</h:outputText>
						</td>
						<td align="right" width="20" valign="top">
							<h:commandLink action="#{solicitacaoApoioNee.atualizar}" id="linkVisualizarSolicitacao"> 
								<h:graphicImage url="/img/alterar.gif" alt="Alterar" title="Alterar"/>
								<f:param name="id" value="#{item.id}"/>
							</h:commandLink>
						</td>
						<td align="right" width="20" valign="top">
							<h:commandLink action="#{solicitacaoApoioNee.visualizar}" title="Visualizar" id="botaoAlterarSolicitacao">
								<h:graphicImage value="/img/view.gif" title="Visualizar" />
								<f:param name="id" value="#{item.id}"/>
							</h:commandLink>
						</td>	
					</tr>
				</c:forEach>
				<c:if test="${fn:length(solicitacaoApoioNee.solicitacoesApoioDiscente) == 0}">
					<tr><td align="center"> <h:outputText value="Não há solicitações de Apoio a CAENE para este Aluno." /> </td></tr>
				</c:if>
				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="5" align="center">
						<h:commandButton value="<< Voltar" action="#{solicitacaoApoioNee.voltar}" id="btnVoltar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>			
	
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>