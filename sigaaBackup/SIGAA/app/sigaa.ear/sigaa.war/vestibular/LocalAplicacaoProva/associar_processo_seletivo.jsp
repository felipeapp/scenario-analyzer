<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/prototype-1.6.0.3.js" ></script>
<style>
<!--
.leftCol {
	text-align: left;
	border-spacing: 3px;
}

.centerCol {
	text-align: center;
	border-spacing: 3px;
}

.buttonCol {
	text-align: center;
	border-spacing: 3px;
	width: 10%;
}
-->
</style>
<f:view>
	<h2><ufrn:subSistema /> > Associar Local de Aplicação de Prova a
	um Processo Seletivo</h2>

	<div class="descricaoOperacao" style="width: 75%;">Selecione um
	Processo Seletivo e um município. A lista de locais já associados
	será exibida logo abaixo do formulário.<br />
	Para associar um novo Local de Aplicação de Prova ao Processo Seletivo,
	escolha-o e, opcionalmente, informe o nome do coordenador do prédio,
	local e horário da reunião.<br />
	Para desassociar ou editar o local da reunião ou o nome do coordenador,
	utilize os ícones relacionados na lista.</div>

	<h:form id="form">
		<table class="formulario" width="80%">
			<caption class="listagem">Escolha o Processo Seletivo e o
			Local de Aplicação de Prova</caption>
			<tr>
				<th class="required">Processo Seletivo:</th>
				<td><h:selectOneMenu id="processoSeletivo"
					value="#{localAplicacaoProcessoSeletivo.obj.processoSeletivoVestibular.id}"
					onchange="submit()"
					valueChangeListener="#{localAplicacaoProcessoSeletivo.carregaListaMunicipio}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Município:</th>
				<td><h:selectOneMenu id="municipio"
					rendered="#{not empty localAplicacaoProcessoSeletivo.municipiosCombo}"
					value="#{localAplicacaoProcessoSeletivo.obj.localAplicacaoProva.endereco.municipio.id}"
					onchange="submit()"
					valueChangeListener="#{localAplicacaoProcessoSeletivo.carregaLocalProva}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems
						value="#{localAplicacaoProcessoSeletivo.municipiosCombo}" />
				</h:selectOneMenu> <h:outputText
					rendered="#{empty localAplicacaoProcessoSeletivo.municipiosCombo}"
					value="Selecione um Processo Seletivo" /></td>
			</tr>
			<tr>
				<th class="required">Local de Prova:</th>
				<td><c:if
					test="${not empty localAplicacaoProcessoSeletivo.locaisProvaCombo}">
					<h:selectOneMenu id="localProva"
						value="#{localAplicacaoProcessoSeletivo.obj.localAplicacaoProva.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems
							value="#{localAplicacaoProcessoSeletivo.locaisProvaCombo}" />
					</h:selectOneMenu>
				</c:if> <c:if
					test="${empty localAplicacaoProcessoSeletivo.locaisProvaCombo}">
					<h:outputText
						value="Não há locais de aplicação de prova para associar" />
				</c:if></td>
			</tr>
			<tr>
				<th>Coordenador:</th>
				<td>
					<h:inputText value="#{localAplicacaoProcessoSeletivo.obj.nomeCoordenador}" id="nomeCoordenador" size="60" maxlength="160"/>
					<rich:suggestionbox for="nomeCoordenador" height="100"	width="450" minChars="4"
						suggestionAction="#{localAplicacaoProcessoSeletivo.autocompleteCoordenador}"
						 var="_servidor" fetchValue="#{_servidor.nome}">
						<h:column>
							<h:outputText value="#{_servidor.siape}"/>
						</h:column>
						<h:column>
							<h:outputText value="#{_servidor.nome}"/>
						</h:column>
						<a4j:support event="onselect" actionListener="#{localAplicacaoProcessoSeletivo.atualizaServidor}" reRender="isServidor">
							<f:attribute name="idServidor" value="#{_servidor.id}"/>
						</a4j:support>
					</rich:suggestionbox> 
				</td>
			</tr>
			<tr>
				<th>Matricula SIAPE:</th>
				<td><h:panelGrid id="isServidor">
						<h:outputText value="#{localAplicacaoProcessoSeletivo.obj.coordenador.siape}" rendered="#{not empty localAplicacaoProcessoSeletivo.obj.coordenador}"/>
						<h:outputText value="Não é Servidor da Instituição" rendered="#{empty localAplicacaoProcessoSeletivo.obj.coordenador}"/>
					</h:panelGrid>
				</td>
			</tr>
			<tr>
				<th>Local/hora da reunião:</th>
				<td><h:inputTextarea rows="3" cols="60" id="reuniao"
					value="#{localAplicacaoProcessoSeletivo.obj.localReuniao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton
						value="#{localAplicacaoProcessoSeletivo.confirmButton}"
						action="#{localAplicacaoProcessoSeletivo.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}"
						action="#{localAplicacaoProcessoSeletivo.cancelar}"
						immediate="true" /></td>
				</tr>
			</tfoot>
		</table>
		<br />
		<center><html:img page="/img/required.gif"
			style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigatório. </span> <br />
		<br />
		<div class="infoAltRem" style="width: 85%"><h:graphicImage
			value="/img/alterar.gif" style="overflow: visible;" />: Alterar <h:graphicImage
			value="/img/delete.gif" style="overflow: visible;" />: Remover</div>
		<br />
		<h:dataTable id="lista"
			value="#{localAplicacaoProcessoSeletivo.localProvaDataModel}"
			rendered="#{not empty localAplicacaoProcessoSeletivo.localProvaDataModel}"
			var="item" style="width:85%"
			columnClasses="leftCol,centerCol,buttonCol"
			rowClasses="linhaPar,linhaImpar">
			<h:column>
				<f:facet name="header">
					<h:outputText value="Local de aplicação / Coordenador / Reunião" />
				</f:facet>
				<b>Local:</b>
				<h:outputText value="#{item.localAplicacaoProva.nome}" />
				<br />
				<b>Coordenador:</b>
				<h:outputText value="#{item.nomeCoordenador}" /> <h:outputText value=" (SIAPE: #{item.coordenador.siape})" rendered="#{not empty item.coordenador}" />
				<br />
				<b>Reunião:</b>
				<h:outputText value="#{item.localReuniao}" />
				<br />
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="Capacidade Ideal" />
				</f:facet>
				<h:outputText
					value="#{item.localAplicacaoProva.capacidadeIdealTotal}" />
			</h:column>
			<h:column>
				<f:facet name="header">
					<h:outputText value="Operação" />
				</f:facet>
				<h:commandLink title="Alterar"
					action="#{localAplicacaoProcessoSeletivo.atualizar}"
					style="border: 0;">
					<f:param name="id" value="#{item.id}" />
					<h:graphicImage url="/img/alterar.gif" />
				</h:commandLink>
				<h:commandLink title="Remover"
					action="#{localAplicacaoProcessoSeletivo.preRemover}"
					style="border: 0;">
					<f:param name="id" value="#{item.id}" />
					<h:graphicImage url="/img/delete.gif" />
				</h:commandLink>
			</h:column>
		</h:dataTable>
		<br />
		<h:outputText
			value="Capacidade total: #{localAplicacaoProcessoSeletivo.capacidadeTotalAssociada}" />
		</center>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>