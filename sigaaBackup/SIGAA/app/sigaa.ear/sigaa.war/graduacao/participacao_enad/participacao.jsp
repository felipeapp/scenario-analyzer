<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form id="form">
	<a4j:keepAlive beanName="participacaoDiscenteEnade"></a4j:keepAlive>
	<h2> <ufrn:subSistema /> > Cadastro de Participação no ENADE </h2>

	<c:set var="discente" value="#{participacaoDiscenteEnade.obj}"/>
	<%@include file="/graduacao/info_discente.jsp"%>

	<table class="formulario" width="90%">
		<caption> Participação no ENADE </caption>
		<tr>
			<th class="${participacaoDiscenteEnade.obj.dataProvaEnadeIngressante != null ? 'obrigatorio' : '' }"> Participação no ENADE Ingressante: </th>
			<td>
				<h:selectOneMenu value="#{participacaoDiscenteEnade.participacaoEnadeIngressante.id}" id="participacaoIngressante">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{participacaoDiscenteEnade.tiposParticipacaoIngressante}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th> Data da prova no ENADE Ingressante: </th>
			<td>
				<t:inputCalendar id="dataProvaIngressante" renderAsPopup="true"
					renderPopupButtonAsImage="true" size="10" maxlength="10"
					onkeypress="return formataData(this,event)"
					readonly="#{participacaoDiscenteEnade.readOnly}"
					disabled="#{readOnly}" popupDateFormat="dd/MM/yyyy"
					value="#{participacaoDiscenteEnade.obj.dataProvaEnadeIngressante}" >
					 <f:converter converterId="convertData"/>
					 <a4j:support event="onchange" reRender="form"/>
				 </t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th class="${participacaoDiscenteEnade.obj.dataProvaEnadeConcluinte != null ? 'obrigatorio' : '' }"> Participação no ENADE Concluinte: </th>
			<td>
				<h:selectOneMenu value="#{participacaoDiscenteEnade.participacaoEnadeConcluinte.id}" id="participacaoConcluinte">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{participacaoDiscenteEnade.tiposParticipacaoConcluinte}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th> Data da prova no ENADE Concluinte: </th>
			<td>
				<t:inputCalendar id="dataProvaConcluinte" renderAsPopup="true"
					renderPopupButtonAsImage="true" size="10" maxlength="10"
					onkeypress="return formataData(this,event)"
					readonly="#{participacaoDiscenteEnade.readOnly}"
					disabled="#{readOnly}" popupDateFormat="dd/MM/yyyy"
					value="#{participacaoDiscenteEnade.obj.dataProvaEnadeConcluinte}" >
					 <f:converter converterId="convertData"/>
					 <a4j:support event="onchange" reRender="form"/>
				 </t:inputCalendar>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Confirmar" action="#{participacaoDiscenteEnade.cadastrar}" id="btnCadastrar"/>
					<h:commandButton value="<< Selecionar outro Discente" action="#{participacaoDiscenteEnade.buscarDiscente}" id="btnBuscar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{participacaoDiscenteEnade.cancelar}" id="btnCancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>