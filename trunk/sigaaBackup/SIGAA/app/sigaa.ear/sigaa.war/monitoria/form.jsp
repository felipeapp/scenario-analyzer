<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Tipo de Situação do Projeto</h2>
	<br>
	<h:form>

	<h:inputHidden value="#{tipoSituacaoProjeto.confirmButton}"/>
	<h:inputHidden value="#{tipoSituacaoProjeto.obj.id}"/>

	<table class="formulario" width="100%">
	<caption class="listagem"> Solicitar Cadastro de Projeto de Monitoria </caption>


	<tr>
		<th width="20%"> Descrição da Situação: </th>
		<td> <h:inputText value="#{tipoSituacaoProjeto.obj.denominacao}" size="50" readonly="#{tipoSituacaoProjeto.readOnly}"/> </td>
	</tr>

	<tfoot>
		<tr>
			<td colspan="2">

				<c:if test="${tipoSituacaoProjeto.confirmButton != 'Remover'}">
					<h:commandButton value="#{tipoSituacaoProjeto.confirmButton}" action="#{tipoSituacaoProjeto.cadastrar}"/>
				</c:if>

				<c:if test="${tipoSituacaoProjeto.confirmButton == 'Remover'}">
					<h:commandButton value="#{tipoSituacaoProjeto.confirmButton}" action="#{tipoSituacaoProjeto.remover}"/>
				</c:if>

				<h:commandButton value="Cancelar" action="#{tipoSituacaoProjeto.cancelar}"/>
			</td>
		</tr>
	</tfoot>
	</h:form>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
