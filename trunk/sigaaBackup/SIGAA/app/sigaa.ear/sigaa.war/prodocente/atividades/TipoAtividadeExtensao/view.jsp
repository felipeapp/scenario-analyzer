<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Tipo de Atividade de Extens�o</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Tipo de Atividade de Extens�o</caption>
			<h:inputHidden value="#{tipoAtividadeExtensao.confirmButton}" />
			<h:inputHidden value="#{tipoAtividadeExtensao.obj.id}" />
			<tr>
				<th class="required">Descri��o:</th>
				<td><h:inputText value="#{tipoAtividadeExtensao.obj.descricao}"
					size="60" maxlength="255"
					readonly="#{tipoAtividadeExtensao.readOnly}" id="descricao" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoAtividadeExtensao.confirmButton}"
						action="#{tipoAtividadeExtensao.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{tipoAtividadeExtensao.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>

	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>
	</center>
	<br>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
