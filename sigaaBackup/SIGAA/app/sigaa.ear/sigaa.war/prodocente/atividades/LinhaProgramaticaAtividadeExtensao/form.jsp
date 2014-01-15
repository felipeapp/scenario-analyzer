<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Linha Progmática de Atividade de Extensão</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de
			Linha Progmática de Atividade de Extensão</caption>
			<h:inputHidden
				value="#{linhaProgramaticaAtividadeExtensao.confirmButton}" />
			<h:inputHidden value="#{linhaProgramaticaAtividadeExtensao.obj.id}" />
			<tr>
				<th class="required">Denominação:</th>
				<td><h:inputText
					value="#{linhaProgramaticaAtividadeExtensao.obj.denominacao}"
					size="60" maxlength="255"
					readonly="#{linhaProgramaticaAtividadeExtensao.readOnly}"
					id="denominacao" /></td>
			</tr>
			<tr>
				<th class="required">Definição:</th>
				<td><h:inputText
					value="#{linhaProgramaticaAtividadeExtensao.obj.definicao}"
					size="60" maxlength="255"
					readonly="#{linhaProgramaticaAtividadeExtensao.readOnly}"
					id="definicao" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{linhaProgramaticaAtividadeExtensao.confirmButton}"
						action="#{linhaProgramaticaAtividadeExtensao.cadastrar}" /> <h:commandButton
						value="Cancelar"
						action="#{linhaProgramaticaAtividadeExtensao.cancelar}" /></td>
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
