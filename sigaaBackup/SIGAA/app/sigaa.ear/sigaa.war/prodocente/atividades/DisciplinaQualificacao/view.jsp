<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Disciplina de Qualificação</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Disciplina de Qualificação</caption>
			<h:inputHidden value="#{disciplinaQualificacao.confirmButton}" />
			<h:inputHidden value="#{disciplinaQualificacao.obj.id}" />
			<tr>
				<th class="required">Disciplina:</th>
				<td><h:inputText
					value="#{disciplinaQualificacao.obj.disciplina}" size="60"
					maxlength="255" readonly="#{disciplinaQualificacao.readOnly}"
					id="disciplina" /></td>
			</tr>
			<tr>
				<th class="required">Conceito:</th>
				<td><h:inputText value="#{disciplinaQualificacao.obj.conceito}"
					size="60" maxlength="255"
					readonly="#{disciplinaQualificacao.readOnly}" id="conceito" /></td>
			</tr>
			<tr>
				<th class="required">Qualificação:</th>

				<td><h:selectOneMenu
					value="#{disciplinaQualificacao.obj.qualificacaoDocente.id}"
					disabled="#{disciplinaQualificacao.readOnly}"
					disabledClass="#{disciplinaQualificacao.disableClass}"
					id="qualificacao">
					<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--" />
					<f:selectItems value="#{qualificacaoDocente.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{disciplinaQualificacao.confirmButton}"
						action="#{disciplinaQualificacao.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{disciplinaQualificacao.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
