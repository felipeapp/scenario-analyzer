<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>

<f:view>
		${matriculaGraduacao.carregarTurmasPorExpressao}
		<div style="background-color: white">
		<br>
		<center style="">
		<c:if test="${empty resultadoTurmasBuscadas}">
			<c:choose>
				<c:when test="${not empty expressaoFormatada}">
					Não foram encontradas turmas abertas nessa expressão.<br />
					${expressaoFormatada}
				</c:when>
				<c:otherwise>
					Você deve reiniciar esta operação para ter acesso às informações das turmas.
				</c:otherwise>
			</c:choose>
		</c:if>
		<c:if test="${not empty resultadoTurmasBuscadas}">
			<b>${expressaoFormatada}</b>
		</c:if>
		</center>
		<br>
		<c:if test="${not empty resultadoTurmasBuscadas}">
			<table class="listagem" id="lista-turmas-extra">
			<c:set var="disciplinaAtual" value="0" />
			<c:forEach items="#{resultadoTurmasBuscadas}" var="turma" varStatus="s">

				<%-- Componente Curricular --%>
				<c:if test="${ disciplinaAtual != turma.disciplina.id}">
					<c:set var="disciplinaAtual" value="${turma.disciplina.id}" />
					<tr class="disciplina" >
					<td colspan="6" style="font-variant: small-caps;">
						${turma.disciplina.codigo} - ${turma.disciplina.detalhes.nome}
					</td></tr>
				</c:if>
				<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small" id="turma_${turma.id}TR">
					<td width="8%" nowrap="nowrap">Turma ${turma.codigo}</td>
					<td>${turma.docentesNomes}</td>
					<td width="10%">${turma.descricaoHorario}</td>
					<td width="10%">${turma.local}</td>
				</tr>
			</c:forEach>
		</table>
		</c:if>
		</div>

</f:view>

