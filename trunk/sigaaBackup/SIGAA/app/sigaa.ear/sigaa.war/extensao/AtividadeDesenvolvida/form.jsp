<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:form>
		<table class="formulario">
			<caption class="listagem">Cadastro de atividadeDesenvolvida</caption>
			<h:inputHidden value="#{atividadeDesenvolvida.confirmButton}" />
			<h:inputHidden value="#{atividadeDesenvolvida.obj.id}" />
			<tr>
				<th>Denominacao:</th>
				<td><h:inputText value="#{atividadeDesenvolvida.obj.denominacao}"
					readonly="#{atividadeDesenvolvida.readOnly}" id="denominacao" /></td>
			</tr>
			<tr>
				<th>ResultadosQuantitativos:</th>
				<td><h:inputText
					value="#{atividadeDesenvolvida.obj.resultadosQuantitativos}"
					readonly="#{atividadeDesenvolvida.readOnly}"
					id="resultadosQuantitativos" /></td>
			</tr>
			<tr>
				<th>ResultadosQualitativos:</th>
				<td><h:inputText
					value="#{atividadeDesenvolvida.obj.resultadosQualitativos}"
					readonly="#{atividadeDesenvolvida.readOnly}"
					id="resultadosQualitativos" /></td>
			</tr>
			<tr>
				<th>Dificuldades:</th>
				<td><h:inputText value="#{atividadeDesenvolvida.obj.dificuldades}"
					readonly="#{atividadeDesenvolvida.readOnly}" id="dificuldades" /></td>
			</tr>

			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{atividadeDesenvolvida.confirmButton}"
						action="#{atividadeDesenvolvida.cadastrar}" /> <h:commandButton
						value="Cancelar" action="#{atividadeDesenvolvida.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
