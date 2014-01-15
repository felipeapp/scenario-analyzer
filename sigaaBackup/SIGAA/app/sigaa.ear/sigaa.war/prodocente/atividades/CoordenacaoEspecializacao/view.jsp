<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Coordenação de Especialização</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Coordenação de
			Especialização</caption>
			<h:inputHidden value="#{coordenacao.confirmButton}" />
			<h:inputHidden value="#{coordenacao.obj.id}" />
			<tr>
				<th>Docente:</th>

				<td><h:selectOneMenu
					value="#{coordenacao.obj.servidor.id}"
					disabled="#{coordenacao.readOnly}"
					disabledClass="#{coordenacao.disableClass}" id="servidor">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{coordenacao.docentesUnidade}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th>Nome do Curso:</th>
				<td><h:inputText value="#{coordenacao.obj.nomeCurso}"
					size="60" maxlength="255" readonly="#{coordenacao.readOnly}"
					id="nomeCurso" /></td>
			</tr>
			<tr>
				<th>Pago:</th>
				<td><h:selectBooleanCheckbox
							value="#{coordenacao.obj.pago}" id="pago"
							disabled="#{coordenacao.readOnly}" /></td>
			</tr>
			<tr>
				<th>Período Início:</th>
				<td><t:inputCalendar
							value="#{coordenacao.obj.periodoInicio}" size="10"
							maxlength="10" readonly="#{coordenacao.readOnly}" id="periodoInicio"
							renderAsPopup="true" renderPopupButtonAsImage="true" /></td>
			</tr>
			<tr>
				<th>Período Fim:</th>
				<td><t:inputCalendar
							value="#{coordenacao.obj.periodoFim}" size="10"
							maxlength="10" readonly="#{coordenacao.readOnly}" id="periodoFim"
							renderAsPopup="true" renderPopupButtonAsImage="true" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{coordenacao.confirmButton}"
						action="#{coordenacao.cadastrarEspecializacao}" /> <h:commandButton
						value="Cancelar" action="#{coordenacao.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
