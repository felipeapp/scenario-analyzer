<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Cadastro de Área de Conhecimento</h2>

	<h:form id="form">
		<c:set var="readOnly" value="#{areaConhecimentoVestibular.readOnly}" />
		<table class=formulario width="100%">
			<caption class="listagem">Dados da Área de Conhecimento</caption>
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText
					value="#{areaConhecimentoVestibular.obj.descricao}" size="60"
					readonly="#{areaConhecimentoVestibular.readOnly}"
					disabled="#{areaConhecimentoVestibular.readOnly}" maxlength="80" /></td>
			</tr>
			<tr>
				<th>Ativo:</th>
				<td><h:selectBooleanCheckbox
					value="#{areaConhecimentoVestibular.obj.ativo}"
					readonly="#{areaConhecimentoVestibular.readOnly}"
					disabled="#{areaConhecimentoVestibular.readOnly}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{areaConhecimentoVestibular.confirmButton}"
						action="#{areaConhecimentoVestibular.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{areaConhecimentoVestibular.cancelar}"
						onclick="#{confirm}" immediate="true" /></td>
				</tr>
			</tfoot>
		</table>
		<br>
		<center><html:img page="/img/required.gif"
			style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigatório. </span> <br>
		<br>
		</center>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>