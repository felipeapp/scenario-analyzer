<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:outputText value="#{aproveitamentoCredito.create}" />
	<h2 class="title"><ufrn:subSistema /> > Remover Aproveitamento Crédito</h2>

	<c:set var="discente" value="#{aproveitamentoCredito.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<h:messages showDetail="true"></h:messages>
		
	<c:if test="${not empty aproveitamentoCredito.historicoAproveitamento}">
		<h:form>
			<div class="infoAltRem">
		    	<h:graphicImage value="/img/cross.png" style="overflow: visible;"/>: Remover Crédito		
			</div>	
			<table class="subFormulario" style="width: 100%">
				<caption>Histórico de Aproveitamento de Créditos do Discente</caption>
				<thead>
					<td>Total de Créditos</td>
					<td>Observação</td>
					<td>Usuário</td>
					<td width="15%">Data</td>
					<td>Status</td>
					<td></td>
				</thead>
				<tbody>
					<c:forEach items="#{aproveitamentoCredito.historicoAproveitamento}" var="aprov" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td>${aprov.creditos}</td>
							<td><i>${aprov.observacao}</i></td>
							<td>${aprov.registroEntrada.usuario.pessoa.nome}</td>
							<td><ufrn:format type="dataHora" valor="${aprov.dataCadastro}" /></td>
							<td><i>${aprov.ativo ? "Ativo" : "Anulado" }</i></td>
							<td>
								<h:commandLink 
										action="#{aproveitamentoCredito.remover}" 
										rendered="#{aprov.ativo}" 
										onclick="return confirm('Deseja continuar a Operação?');" 
										title="Remover Crédito"
										id="botaoRemoverAproveitamentoCredito" 
										>
									<h:graphicImage url="/img/cross.png"/>
									<f:param name="idAprov" value="#{aprov.id}" />
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
					<%-- 
					<tr id="senhaRow">
						<td colspan="6">
							<c:set var="exibirApenasSenha" value="true" scope="request"/>
							<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
						</td>
					</tr>
					--%>
				</tbody>
			</table>
		</h:form>
	</c:if>
	<br><br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>