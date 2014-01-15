<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp" %>

<h2> Alunos com mais de um vínculo a serem cancelados</h2>
<c:set var="total" value="0"/>
<table class="tabelaRelatorioBorda">
<tr><th>Nome</th><th>Matr. Anterior</th><th>Ano/Período</th><th>Curso Anterior</th><th>Matr. Atual</th><th>Ano/Período</th><th>Curso Atual</th></tr>
<c:forEach var="d" items="${ discentes }">
<tr><td nowrap="nowrap">${ d[0].pessoa.nome }</td><td>${ d[0].matricula }</td><td>${ d[0].anoPeriodoIngresso }</td><td nowrap="nowrap">${ d[0].curso.nome }</td><td>${ d[1].matricula }</td><td>${ d[1].anoPeriodoIngresso }</td><td nowrap="nowrap">${ d[1].curso.nome }</td></tr>
<c:set var="total" value="${ total + 1 }"/>
</c:forEach>
<tfoot>
<tr>
<td>Total: </td>
<td colspan="6">${ total }</td>
</tr>
</tfoot>
</table>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp" %>