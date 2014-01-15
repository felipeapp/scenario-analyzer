<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<c:if test="${acesso.discente}">
	<%@include file="/portais/discente/menu_discente.jsp" %>
</c:if>
<c:if test="${acesso.docente}">
	<%@include file="/portais/docente/menu_docente.jsp" %>
</c:if>
	<h2 class="title">Detalhes do Curso</h2>

	<h:form id="resumo">
		<table class="visualizacao">
			<caption class="listagem">Dados do Curso</caption>
			<tbody>
			<tr>
				<th>Código do Curso:</th>
				<td><h:outputText value="#{curso.obj.codigo}" /></td>
			</tr>
			<tr>
				<th>Nome:</th>
				<td><h:outputText value="#{curso.obj.descricao}" /></td>
			</tr>
			<tr>
				<th>Centro:</th>
				<td><h:outputText value="#{curso.obj.unidade.gestora.nome}" /></td>
			</tr>
			</tbody>
		</table>
	</h:form>
	<br>
	<center>
	<a href="javascript: history.go(-1);"> << Voltar </a>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
