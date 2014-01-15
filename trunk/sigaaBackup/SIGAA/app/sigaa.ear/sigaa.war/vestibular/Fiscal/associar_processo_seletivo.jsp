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
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Alocar Fiscais aos Locais de Aplicação de Prova</h2>

	<h:form id="formBusca">
		<table class="formulario" width="80%">
			<caption>Escolha o Processo Seletivo e o Local de Aplicação</caption>
			<tbody>
				<tr>
					<th width="30%">Processo Seletivo:</th>
					<td width="70%"><h:selectOneMenu id="processoSeletivo"
						immediate="true"
						value="#{associacaoFiscalLocalAplicacao.idProcessoSeletivo}"
						valueChangeListener="#{associacaoFiscalLocalAplicacao.processoSeletivoListener}"
						onchange="submit();">
							<f:selectItem itemValue="0"	itemLabel="-- SELECIONE --" />
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
						<f:selectItem itemValue="0"	itemLabel="-- SELECIONE --" />
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
				<tr>
					<th>Local de Preferência:</th>
					<td width="70%"><h:selectOneMenu id="localAplicacaoOrigem"
						immediate="true"
						value="#{associacaoFiscalLocalAplicacao.idLocalAplicacaoOrigem}"
						valueChangeListener="#{associacaoFiscalLocalAplicacao.localAplicacaoOrigemListener}"
						onchange="submit();">
						<f:selectItem itemValue="0"	itemLabel="-- SELECIONE --" />
						<f:selectItems
							value="#{associacaoFiscalLocalAplicacao.locaisAplicacao}" />
					</h:selectOneMenu></td>
				</tr>
				<tr>
					<th>Ordem de Preferência:</th>
					<td width="70%"><h:selectOneMenu id="ordemPreferenciaLocal"
						immediate="true"
						value="#{associacaoFiscalLocalAplicacao.ordemPreferencial}"
						valueChangeListener="#{associacaoFiscalLocalAplicacao.ordemPreferencialListener}"
						onchange="submit();">
						<f:selectItem itemValue="0"
							itemLabel="Qualquer ordem de preferência" />
						<f:selectItem itemValue="1" itemLabel="Primeira opção" />
						<f:selectItem itemValue="2" itemLabel="Segunda opção" />
						<f:selectItem itemValue="3" itemLabel="Terceira opção" />
						<f:selectItem itemValue="4" itemLabel="Quarta opção" />
						<f:selectItem itemValue="5" itemLabel="Quinta opção" />
					</h:selectOneMenu></td>
				</tr>
			</tbody>
		</table>
		<br>
		<div align="center">Total de fiscais: ${fn:length(associacaoFiscalLocalAplicacao.listaFiscais)}
		<c:if test="${not empty associacaoFiscalLocalAplicacao.listaFiscais}">
			<table class="listagem">
				<thead>
					<tr>
						<td style="text-align:center; width:  5%">Associar</td>
						<td style="text-align:center; width: 70%">Nome</td>
						<td style="text-align:center; width:  5%">Titularidade</td>
						<td style="text-align:center; width:  5%">Experiência</td>
						<td style="text-align:center; width:  5%">Curso/Unidade</td>
						<td style="text-align:center; width:  5%">IRA/Média</td>
						<td style="text-align:center; width:  5%">Disponibilidade para viajar</td>
					</tr>
				</thead>
				<tbody>
				<c:set var="indiceSelecao" value="<%=ParametroHelper.getInstance().getParametroInt(ParametrosVestibular.INDICE_ACADEMICO_SELECAO_FISCAL_GRADUACAO) %>" />
				<c:forEach items="#{associacaoFiscalLocalAplicacao.listaFiscais}" 
					var="item" varStatus="status" >
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
						<td align="center" width="5%">
							<h:selectBooleanCheckbox id="selecionar" value="#{item.selecionado}"  rendered="#{empty item.objeto.localAplicacaoProva}"/>
							<h:outputText id="selecionado" value="Alocado"  rendered="#{not empty item.objeto.localAplicacaoProva}"/>
						</td>
						<td align="left"   width="70%"><h:outputText value="#{item.objeto.pessoa.nome}" /></td>
						<td align="center" width="5%"><h:outputText value="Reserva" rendered="#{item.objeto.reserva}" /><h:outputText value="Titular" rendered="#{not item.objeto.reserva}" /></td>
						<td align="center" width="5%"><h:outputText value="Novato" rendered="#{item.objeto.novato}" /><h:outputText value="Experiente" rendered="#{not item.objeto.novato}" /></td>
						<td align="center" width="5%">
							<h:outputText value="#{item.objeto.discente.curso.descricao}" rendered="#{not empty item.objeto.discente}"/>
							<h:outputText value="#{item.objeto.servidor.unidade.nome}" rendered="#{not empty item.objeto.servidor}"/>
						</td>
						<td align="center" width="5%">${ira}</td>
						<td align="center" width="5%"><h:outputText value="#{item.objeto.inscricaoFiscal.disponibilidadeOutrasCidades}" ><f:converter converterId="convertSimNao"/></h:outputText></td>
					</tr>
				</c:forEach>
				</tbody>
				<tfoot>
					<tr>
						<td align="center" colspan="7"><h:commandButton value="Alocar Fiscais Selecionados" action="#{associacaoFiscalLocalAplicacao.cadastrar}" />
						<h:commandButton value="Cancelar" action="#{associacaoFiscalLocalAplicacao.cancelar}" onclick="#{confirm}" immediate="true" />
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if></div>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>