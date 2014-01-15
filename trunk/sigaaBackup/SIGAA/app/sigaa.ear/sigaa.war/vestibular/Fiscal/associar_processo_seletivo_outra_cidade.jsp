<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.parametrizacao.ParametroHelper"%>
<%@page import="br.ufrn.sigaa.parametros.dominio.ParametrosVestibular"%>

<style>
<!--
.center {
	text-align: center;
	border-spacing: 3px;
}

.left {
	text-align: left;
	border-spacing: 3px;
}
-->
</style>

<f:view>
	<h:messages></h:messages>
	<h2><ufrn:subSistema /> > Alocação de Fiscais com Disponibilidade para Viajar</h2>

	<div class="descricaoOperacao">Selecione um processo seletivo  para ter a lista de fiscais.
		A seguir, selecione o local de aplicação de prova de destino e marque quais
		fiscais deverão ser alocados.<br>
	</div>
	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Escolha o Processo Seletivo e o Local de Aplicação</caption>
			<tbody>
				<tr>
					<th width="30%">Processo Seletivo:</th>
					<td width="70%"><h:selectOneMenu id="processoSeletivo"
						immediate="true"
						value="#{associacaoFiscalLocalAplicacao.idProcessoSeletivo}"
						valueChangeListener="#{associacaoFiscalLocalAplicacao.processoSeletivoOutraCidade}"
						onchange="submit();">
						<f:selectItem itemValue="0"	itemLabel="--> SELECIONE <--" />
						<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th>Local de Aplicação:</th>
					<td width="70%"><h:selectOneMenu id="idlocalAplicacaoDestino"
						immediate="true"
						value="#{associacaoFiscalLocalAplicacao.idLocalAplicacaoDestino}"
						valueChangeListener="#{associacaoFiscalLocalAplicacao.localAplicacaoDestinoListener}"
						onchange="submit()">
						<f:selectItem itemValue="0"	itemLabel="--> SELECIONE <--" />
						<f:selectItems
							value="#{associacaoFiscalLocalAplicacao.locaisAplicacao}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th>Quantidade alocados:</th>
					<td width="70%">
					${associacaoFiscalLocalAplicacao.totalTitularAlocado} Titulares /
					${associacaoFiscalLocalAplicacao.totalReservaAlocado} Reservas</td>
				</tr>
			</tbody>
		</table>
		<br>
		<div align="center">Total de fiscais: ${fn:length(associacaoFiscalLocalAplicacao.listaFiscais)}
		<c:if test="${not empty associacaoFiscalLocalAplicacao.listaFiscais}">
			<table class="listagem">
				<thead>
					<tr>
						<td align="center">Associar</td>
						<td align="left">Nome<br>(Local de Aplicação de Prova)</td>
						<td align="center">Titularidade</td>
						<td align="center">Experiência</td>
						<td align="center">Curso/Unidade</td>
						<td align="center" width="10%">IRA/Média</td>
					</tr>
				</thead>
				<tbody>
				<c:set var="indiceSelecao" value="<%=ParametroHelper.getInstance().getParametroInt(ParametrosVestibular.INDICE_ACADEMICO_SELECAO_FISCAL_GRADUACAO) %>" />
				<c:forEach items="#{associacaoFiscalLocalAplicacao.listaFiscais}"
					var="item" varStatus="status">
					<c:if test="${item.objeto.discente.graduacao}">
						<c:forEach var="indice" items="#{item.objeto.discente.indices}">
							<c:if test="${indice.indice.id == indiceSelecao}">
								<c:set var="ira" value="${indice.valor}" />
							</c:if>
						</c:forEach>
					</c:if>
					<c:if test="${item.objeto.discente.stricto}">
						<c:set var="ira" value="Pós-graduação" />
					</c:if>
					<c:if test="${not empty item.objeto.servidor}">
						<c:set var="ira" value="Servidor" />
					</c:if>
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td align="center">
							<h:selectBooleanCheckbox value="#{item.selecionado}" rendered="#{ empty item.objeto.localAplicacaoProva.id }"/>
							<h:outputText value="Alocado" rendered="#{ not empty item.objeto.localAplicacaoProva.id }"/>
						</td>
						<td align="left">
							<h:outputText value="#{item.objeto.pessoa.nome}" />
						  	<c:if test="${not empty item.objeto.localAplicacaoProva}">
								<br>(<h:outputText value="#{item.objeto.localAplicacaoProva.nome}" />)
							</c:if>
						</td>
						<td align="center"><h:outputText value="Reserva" rendered="#{item.objeto.reserva}" /><h:outputText value="Titular" rendered="#{not item.objeto.reserva}" /></td>
						<td align="center"><h:outputText value="Novato" rendered="#{item.objeto.novato}" /><h:outputText value="Experiente" rendered="#{not item.objeto.novato}" /></td>
						<td align="left">
							<h:outputText value="#{item.objeto.discente.curso.descricao}" rendered="#{not empty item.objeto.discente}"/>
							<h:outputText value="#{item.objeto.servidor.unidade.nome}" rendered="#{not empty item.objeto.servidor}"/>
						</td>
						<td align="right">${ira}</td>
					</tr>
				</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td align="center" colspan="6"><h:commandButton value="Alocar Fiscais Selecionados" action="#{associacaoFiscalLocalAplicacao.cadastrar}" />
						<h:commandButton value="Cancelar" action="#{associacaoFiscalLocalAplicacao.cancelar}" onclick="#{confirm}" immediate="true" />
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if></div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>