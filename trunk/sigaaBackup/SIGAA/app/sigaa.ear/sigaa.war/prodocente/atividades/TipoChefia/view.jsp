<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Chefia</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
	<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Tipo de Chefia</caption>
			<h:inputHidden value="#{tipoChefia.confirmButton}" />
			<h:inputHidden value="#{tipoChefia.obj.id}" />
			<tr>
				<th class="required">Descri��o:</th>
				<td><h:inputText value="#{tipoChefia.obj.descricao}" size="60"
					maxlength="255" readonly="#{tipoChefia.readOnly}" id="descricao" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoChefia.confirmButton}"
						action="#{tipoChefia.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{tipoChefia.cancelar}" /></td>
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
