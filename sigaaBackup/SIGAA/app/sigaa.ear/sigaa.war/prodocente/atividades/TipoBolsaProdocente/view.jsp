<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<<f:view>
	<h2><ufrn:subSistema /> > Tipo de Bolsa</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de Tipo de Bolsa</caption>
			<h:inputHidden value="#{tipoBolsaProdocente.confirmButton}" />
			<h:inputHidden value="#{tipoBolsaProdocente.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText value="#{tipoBolsaProdocente.obj.descricao}" size="60"
					maxlength="255" readonly="#{tipoBolsaProdocente.readOnly}" id="descricao" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoBolsaProdocente.confirmButton}" action="#{tipoBolsaProdocente.cadastrar}" />
					<h:commandButton value="Cancelar" action="#{tipoBolsaProdocente.cancelar}" /></td>
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
