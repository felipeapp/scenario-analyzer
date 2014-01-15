<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="ofertaVagasTecnico"></a4j:keepAlive>
	<h2><ufrn:subSistema /> > Cadastro de Oferta de Vagas</h2>
	<h:form id="form" enctype="multipart/form-data">
		<c:set var="readOnly" value="#{ofertaVagasTecnico.readOnly}" />
		<h:inputHidden value="#{ofertaVagasTecnico.obj.id}" />
		<table class=formulario width="100%">
			<caption class="listagem">Dados da Oferta de Vagas</caption>
			<tbody>
				<tr>
					<th class="obrigatorio">Nome:</th>
					<td><h:inputText id="nome"
						value="#{ofertaVagasTecnico.obj.nome}"
						size="60" readonly="#{ofertaVagasTecnico.readOnly}"
						maxlength="160" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Polo:</th>
					<td>
						<h:selectOneMenu id="polo" value="#{ofertaVagasTecnico.obj.polo.id}" readonly="#{ofertaVagasTecnico.readOnly}">
							<f:selectItem itemLabel=" == SELECIONE ==" itemValue="0" />
							<f:selectItems value="#{ ofertaVagasTecnico.polosCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Grupo:</th>
					<td>
						<h:selectOneMenu id="grupo" value="#{ofertaVagasTecnico.obj.grupo}" readonly="#{ofertaVagasTecnico.readOnly}">
							<f:selectItem itemLabel=" == SELECIONE ==" itemValue="0" />
							<f:selectItem itemLabel="De 15 a 20 anos" itemValue="1" />
							<f:selectItem itemLabel="21 anos ou mais" itemValue="2" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Ano:</th>
					<td>
						<h:inputText id="ano" value="#{ofertaVagasTecnico.obj.ano}" size="60" readonly="#{ofertaVagasTecnico.readOnly}" maxlength="10" />
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Quantidade de vagas:</th>
					<td>
						<h:inputText id="quantidade" value="#{ofertaVagasTecnico.obj.quantidadeVagas}" size="60" readonly="#{ofertaVagasTecnico.readOnly}" maxlength="10" />
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Reserva de Vagas:</th>
					<td>
						<h:selectOneMenu id="reserva" value="#{ofertaVagasTecnico.obj.reservaVagas}" readonly="#{ofertaVagasTecnico.readOnly}">
							<f:selectItem itemLabel="Sim" itemValue="true" />
							<f:selectItem itemLabel="Não" itemValue="false" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{ofertaVagasTecnico.confirmButton}" action="#{ofertaVagasTecnico.cadastrar}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ofertaVagasTecnico.cancelar}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
		<br>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>