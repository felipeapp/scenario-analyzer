<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	
	<h2 class="title">Excluir Discente</h2>

	<c:set value="#{excluirDiscente.obj}" var="discente" />
	<%@ include file="/graduacao/info_discente.jsp"%>

	<h:form id="form">

		<table class="formulario">
			<caption class="formulario">Excluir Discente</caption>
			<tr>
				<th>Justificativa:</th>
				<td>
					<span class="required">&nbsp;</span>
					<h:inputTextarea id="justificativa" value="#{excluirDiscente.observacao}" rows="4" cols="80"/>
				</td>
			</tr>

			<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Excluir Discente" action="#{excluirDiscente.cadastrar}" id="cadastrar" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{excluirDiscente.cancelar}" id="cancelar" />
				</td>
			</tr>
			</tfoot>
		</table>

		<c:set var="exibirApenasSenha" value="true" scope="request"/>
		<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>

	</h:form>
	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
		<br><br>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
