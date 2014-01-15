<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Tipo de Avaliação de Organização de Evento</h2>
	<hr>
	<h:messages showDetail="true"></h:messages>
	<h:form id="form">
		<table class=formulario width="90%">
			<caption class="listagem">Cadastro de
			Tipo de Avaliação de Organização de Evento</caption>
			<h:inputHidden
				value="#{tipoAvaliacaoOrganizacaoEvento.confirmButton}" />
			<h:inputHidden value="#{tipoAvaliacaoOrganizacaoEvento.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText
					value="#{tipoAvaliacaoOrganizacaoEvento.obj.descricao}" size="60"
					maxlength="255"
					readonly="#{tipoAvaliacaoOrganizacaoEvento.readOnly}"
					id="descricao" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoAvaliacaoOrganizacaoEvento.confirmButton}"
						action="#{tipoAvaliacaoOrganizacaoEvento.cadastrar}" /> <h:commandButton
						value="Cancelar"
						action="#{tipoAvaliacaoOrganizacaoEvento.cancelar}" /></td>
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
