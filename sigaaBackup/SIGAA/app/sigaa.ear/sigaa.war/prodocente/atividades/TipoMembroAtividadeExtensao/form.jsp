<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Tipo de Membro de Atividade de Extensão</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de
			Tipo de Membro de Atividade de Extensão</caption>
			<h:inputHidden value="#{tipoMembroAtivividadeExtensao.confirmButton}" />
			<h:inputHidden value="#{tipoMembroAtivividadeExtensao.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText
					value="#{tipoMembroAtivividadeExtensao.obj.descricao}" size="60"
					maxlength="255"
					readonly="#{tipoMembroAtivividadeExtensao.readOnly}" id="descricao" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoMembroAtivividadeExtensao.confirmButton}"
						action="#{tipoMembroAtivividadeExtensao.cadastrar}" /> <h:commandButton
						value="Cancelar"
						action="#{tipoMembroAtivividadeExtensao.cancelar}" /></td>
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
