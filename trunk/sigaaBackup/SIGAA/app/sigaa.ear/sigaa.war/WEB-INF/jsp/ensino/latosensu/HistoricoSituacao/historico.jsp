<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/ensino/latosensu/cadastroHistoricoSituacao.do?dispatch=cancelar">
		<ufrn:subSistema semLink="true"/>
	</html:link> &gt;
	Histórico de Situações
</h2>

<table class="formulario" width="80%">
<caption>Proposta do Curso: ${curso.descricao}</caption>
<tbody>
<c:if test="${ empty historicoSituacoes }">
	<tr>
		<td>
		<div style="font-style: italic; text-align:center">
			Nenhum registro a ser exibido
		</div>
		</td>
	</tr>
</c:if>
<c:forEach items="${historicoSituacoes}" var="historico">
	<tr>
	<td>
		<table class="subformulario" width="100%">
		<caption>
		<fmt:formatDate value="${historico.dataCadastro}" pattern="dd/MM/yyyy"/> - ${historico.situacao.descricao}
		</caption>
		<tr>
			<td>Despacho:</td>
		</tr>
		<tr>
			<td>${historico.observacoes}</td>
		</tr>
		</table>
	</td>
	</tr>
</c:forEach>
</tbody>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>