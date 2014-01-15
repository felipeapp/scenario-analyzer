<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
#cepIndicator {
	padding: 0 25px;
	color: #999;
}

span.info {
	font-size: 0.9em;
	color: #888;
}
.centro{
	text-align: center !important;
}
</style>

<f:view>
	<%@include file="/stricto/menu_coordenador.jsp"%>

	<h2 class="tituloPagina"><ufrn:subSistema /> &gt; 
	Consulta de Solicitação de Revalidação de Diplomas</h2>
	<h:outputText value="#{solRevalidacaoDiploma.create}" />

	<h:form id="form">
		<table align="center" class="formulario" width="50%">

			<caption class="listagem">Dados da Solicitação</caption>
			<tr>
				<td></td>
				<th align="right" class="required">Edital:</th>
				<td align="left">
					<h:selectOneMenu value="#{solRevalidacaoDiploma.filtroAgenda}" id="edital">
						<f:selectItem itemLabel="-- Selecione --" itemValue="" />
						<f:selectItems value="#{editalRevalidacaoDiploma.allCombo}" />
						<a4j:support event="onchange" reRender="data">
							<f:setPropertyActionListener value="#{solRevalidacaoDiploma.filtroAgenda}"
							 target="#{agendaRevalidacaoDiploma.editalRevalidacaoDiploma.id}"/>
						</a4j:support>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td></td>
				<th align="right">Fila de Espera:</th>
				<td align="left">
					<h:selectOneRadio id="filtroEspera" value="#{solRevalidacaoDiploma.filtroEspera}">
						<f:selectItem itemLabel="Sim" itemValue="true" />
						<f:selectItem itemLabel="Não" itemValue="false" />
						<a4j:support event="onchange" reRender="data,horario" />
					</h:selectOneRadio>
				</td>
			</tr>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{solRevalidacaoDiploma.buscaNome}" styleClass="noborder"
					id="checkNome" />
				</td>
				<th style="text-align:left !important">Nome:</th>
				<td align="left">
					<h:inputText id="nomeInscrito"
					value="#{solRevalidacaoDiploma.filtroNome}" size="35"
					onfocus="getEl('form:checkNome').dom.checked = true;" />
				</td>
			</tr>
			<tr>
				<td>
					<h:selectBooleanCheckbox
					value="#{solRevalidacaoDiploma.buscaData}" styleClass="noborder" id="checkData" />
				</td>
				
				<th style="text-align:left !important">Data-Horário:</th>
				
				<td align="left">
				<a4j:region>
					<h:selectOneMenu value="#{solRevalidacaoDiploma.filtroData}"
						id="data"
						valueChangeListener="#{agendaRevalidacaoDiploma.carregarHorariosData}"
						disabled="#{solRevalidacaoDiploma.filtroEspera}"
						onfocus="getEl('form:checkData').dom.checked = true;">
						<f:selectItem itemLabel="-- Selecione --" itemValue="" />
						<f:selectItems value="#{agendaRevalidacaoDiploma.allDatas}" />
						<a4j:support event="onchange" reRender="horario" />
					</h:selectOneMenu>
					<a4j:status>
						<f:facet name="start">
							<h:graphicImage value="/img/indicator.gif" />
						</f:facet>
					</a4j:status>&nbsp;
					<h:selectOneMenu
						value="#{solRevalidacaoDiploma.filtroHorario}" id="horario"
						disabled="#{solRevalidacaoDiploma.filtroEspera}">
						<f:selectItem itemLabel="Selecione" itemValue="" />
						<f:selectItems value="#{agendaRevalidacaoDiploma.horariosData}" />
					</h:selectOneMenu>
				</a4j:region>
				</td>
				
			</tr>
			<tfoot>
				<tr>
					<td colspan="3" align="center"><h:commandButton
						id="buscarSolicitacao" value="Buscar"
						action="#{solRevalidacaoDiploma.buscarSolicitacoes}" /> <h:commandButton
						value="Cancelar" action="#{solRevalidacaoDiploma.cancelar}"
						id="cancelar" onclick="#{confirm}"/></td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<c:if test="${empty solRevalidacaoDiploma.solicitacoesRevalidacaoDiploma}">
		<br>
		<div style="font-style: italic; text-align: center">Nenhum
		registro encontrado de acordo com os critérios de busca informados.</div>
	</c:if>
	
	<c:if test="${not empty solRevalidacaoDiploma.solicitacoesRevalidacaoDiploma}">
		
		<br>
		<center>
			<div class="infoAltRem">
				<h:form id="formLegendaBusca">
					<h:graphicImage url="/img/adicionar.gif" style="overflow: visible;"/>
					<h:commandLink id="cadastrarSolcitacaoRevalidacao" action="#{solRevalidacaoDiploma.preCadastrar}" value="Cadastrar" />
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Dados da Solicitação
					<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Solicitação
				</h:form>
			</div>
		</center>
		
		<table class="listagem">
			<caption class="listagem">Lista de Solicitações Encontradas</caption>
			<thead>
				<tr>
					<td width="55%">Nome</td>
					<td width="15%" class="centro">Passaporte</td>
					<td width="15%" class="centro">CPF</td>
					<td width="5%" class="centro">Data</td>
					<td width="5%" class="centro">Horário</td>
					<td width="2%"></td>
					<td width="2%"></td>
					<td width="2%"></td>
				</tr>
			</thead>

			<h:form id="formListaSolicitacoes">
				<c:forEach
					items="#{solRevalidacaoDiploma.solicitacoesRevalidacaoDiploma}"
					var="item" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" >
						<td>${item.nome}</td>
						<td class="centro">${item.passaporte}</td>
						<td class="centro"><h:outputText converter="convertCpf" value="#{item.cpf}"/></td>
						<td class="centro"><ufrn:format type="data" valor="${item.agendaRevalidacaoDiploma.data}"/></td>
						<td class="centro">${item.agendaRevalidacaoDiploma.horario}</td>
						<td width="2%">
							<h:commandLink id="atualizar"
								action="#{solRevalidacaoDiploma.atualizar}">
								<h:graphicImage url="/img/alterar.gif" alt="Alterar"
									title="Alterar" />
								<f:param name="id" value="#{item.id}" />
							</h:commandLink>
						</td>
						<td width="2%">
							<h:commandLink id="remover"
								action="#{solRevalidacaoDiploma.remover}" onclick="#{confirm}">
								<h:graphicImage url="/img/delete.gif" alt="Remover"
									title="Remover" />
								<f:param name="id" value="#{item.id}" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</h:form>
		</table>
		
	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>