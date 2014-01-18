<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="t" value="${pageScope.turma}" />

<table class="visualizacao" style="width:90%">
	<tr>
		<th width="20%">Componente:</th>
		<td colspan="3">${t.componente.descricaoResumida}
		</td>
	</tr>
	<c:if test="${not empty t.dadosCurso}">
	<tr>
		<th>Campus:</th>
		<td colspan="3">${t.dadosCurso.campus.descricao }</td>
	</tr>
	</c:if>
	<tr>
		<th>Ano-Período:</th>
		<td width="10%">${t.anoPeriodo }</td>
		<c:if test="${not empty t.codigo}">
			<td width="20%" style="font-weight: bold; text-align: right;">Código da Turma:</td>
			<td>${t.codigo}</td>
		</c:if>
		<c:if test="${empty t.codigo}">
			<td colspan="2"></td>
		</c:if>
	</tr>
	<tr>
		<th>Período Letivo:</th>
		<td colspan="3">de <ufrn:format type="data" valor="${t.dataInicio}" /> até <ufrn:format type="data" valor="${t.dataFim}"/></td>
	</tr>
	<c:if test="${not empty t.docentesTurmas}">
	<tr>
		<th>Docente(s):</th>
		<td colspan="3">${t.docentesNomes }</td>
	</tr>
	</c:if>
</table>
<br />