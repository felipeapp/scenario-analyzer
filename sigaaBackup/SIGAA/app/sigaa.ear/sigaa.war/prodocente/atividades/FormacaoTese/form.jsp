<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Tese de Formação</h2>
	<hr>
		<h:form>
		<div style="text-align:right">
		<h:commandLink action="#{formacaoTese.listar}"
			value="Listar Tese de Formação Cadastradas"/>
		</div>
	</h:form>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Tese de Formação</caption>
			<h:inputHidden value="#{formacaoTese.confirmButton}" />
			<h:inputHidden value="#{formacaoTese.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText value="#{formacaoTese.obj.descricao}"
					size="60" maxlength="255" readonly="#{formacaoTese.readOnly}"
					id="descricao" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{formacaoTese.confirmButton}"
						action="#{formacaoTese.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{formacaoTese.cancelar}" /></td>
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
