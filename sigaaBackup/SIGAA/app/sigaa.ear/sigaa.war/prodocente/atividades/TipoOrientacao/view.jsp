<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Orientação</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Tipo de Orientação</caption>
			<h:inputHidden value="#{tipoOrientacao.confirmButton}" />
			<h:inputHidden value="#{tipoOrientacao.obj.id}" />
			<tr>
				<th  class="required">Descrição:</th>
				<td><h:inputText value="#{tipoOrientacao.obj.descricao}"
					size="60" maxlength="255" readonly="#{tipoOrientacao.readOnly}"
					id="descricao" /></td>
			</tr>
			<tr>
				<th  class="required">Contexto:</th>
				<td><h:inputText value="#{tipoOrientacao.obj.contexto}"
					size="60" maxlength="255" readonly="#{tipoOrientacao.readOnly}"
					id="contexto" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoOrientacao.confirmButton}"
						action="#{tipoOrientacao.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{tipoOrientacao.cancelar}" /></td>
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
