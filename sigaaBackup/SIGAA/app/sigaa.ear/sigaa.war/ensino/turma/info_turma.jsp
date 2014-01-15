<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="t" value="${pageScope.turma}" />

<table class="visualizacao">
	<tr>
		<th width="20%">Componente:</th>
		<td colspan="3">${t.disciplina.descricao}
			<c:if test="${not t.lato}"> (${t.disciplina.crTotal} cr�ditos)</c:if>
		</td>
	</tr>
	<c:if test="${ (t.polo == null || t.polo.id == 0) && not empty t.descricaoHorario }">
	<tr>
		<th>Hor�rio:</th>
		<td colspan="3">${t.descricaoHorario}</td>
	</tr>
	</c:if>
	<tr>
		<th>Ano-Per�odo:</th>
		<td width="10%">${t.anoPeriodo }</td>
		<c:if test="${not empty t.codigo}">
			<td width="20%" style="font-weight: bold; text-align: right;">C�digo da Turma:</td>
			<td>${t.codigo}</td>
		</c:if>
		<c:if test="${empty t.codigo}">
			<td colspan="2"></td>
		</c:if>
	</tr>
	<tr>
		<th>Per�odo Letivo:</th>
		<td colspan="3">de <ufrn:format type="data" valor="${t.dataInicio}" /> at� <ufrn:format type="data" valor="${t.dataFim}"/></td>
	</tr>
	<tr>
		<th>Capacidade:</th>
		<c:if test="${ !(t.polo != null && t.polo.id > 0 || t.distancia != null && t.distancia)}">
			<td>${t.capacidadeAluno } aluno(s)</td>
		</c:if>
		<c:if test="${ t.polo != null && t.polo.id > 0 || t.distancia != null && t.distancia}">
			<td nowrap="nowrap">N�o se aplica</td>
		</c:if>
		<th>Tipo:</th>
		<td>${t.tipoString}</td>
	</tr>
		<c:if test="${ (not empty t.local) && (t.polo == null || t.polo.id == 0) }">
			<tr>
				<th>Local:</th>
				<td colspan="3">${t.local}</td>
			</tr>
		</c:if>
		<c:if test="${ t.polo != null && t.polo.id > 0 }">
			<tr>
				<th>P�lo:</th>
				<td colspan="3">${t.polo.cidade.nomeUF}</td>
			</tr>
		</c:if>
		<c:if test="${ !t.graduacao }">
			<tr>
				<th>In�cio - Fim:</th>
				<td colspan="3"><ufrn:format type="data" valor="${t.dataInicio}" /> - <ufrn:format type="data" valor="${t.dataFim}" /></td>
			</tr>
		</c:if>
	<tr>
		<th>Docente(s):</th>
		<td colspan="3">${t.docentesNomes}</td>
	</tr>
</table>
<br />