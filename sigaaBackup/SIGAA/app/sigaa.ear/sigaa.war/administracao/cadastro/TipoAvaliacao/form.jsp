<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2>Tipo de Avaliação</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: left; width: 100%">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoAvaliacao.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="100%">
		<h:form>
			<caption class="listagem">Cadastro de Tipo de Avaliação</caption>
			<h:inputHidden value="#{tipoAvaliacao.confirmButton}" />
			<h:inputHidden value="#{tipoAvaliacao.obj.id}" />
			<tr>
				<th>Descrição:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{tipoAvaliacao.readOnly}" value="#{tipoAvaliacao.obj.denominacao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton value="#{tipoAvaliacao.confirmButton}"
						action="#{tipoAvaliacao.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{tipoAvaliacao.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>