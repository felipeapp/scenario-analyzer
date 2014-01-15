<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Afastamento</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Afastamento</caption>
			<h:inputHidden value="#{afastamento.confirmButton}" />
			<h:inputHidden value="#{afastamento.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText value="#{afastamento.obj.descricao}" size="60"
					maxlength="255" readonly="#{afastamento.readOnly}" id="descricao" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{afastamento.confirmButton}"
						action="#{afastamento.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{afastamento.cancelar}" /></td>
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
