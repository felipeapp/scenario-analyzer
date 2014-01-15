<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@include file="/graduacao/relatorios/cabecalho_curso.jsp"%>

	<table cellspacing="1" width="100%" style="font-size: 10px;" class="tabelaRelatorio">
		<caption><b>Total de Registros: ${ fn:length(resultado) }</b></caption>
	<c:set var="curso2"/>
	<c:set var="turno2"/>
    <c:set var="cidade2"/>
	<c:forEach items="${resultado}" var="linha" varStatus="loop">

		
		<c:if test="${curso2 != cursoAtual || turno2 != turnoAtual}">
			<c:set var="curso2" value="${cursoAtual}"/>
			<c:set var="turno2" value="${turnoAtual}"/>

			<tr>
				<td>
					<br>
					<b>${linha.centro}	- ${linha.curso_nome} </b>
					<hr>
				</td>
			</tr>
		</c:if>

		<c:if test="${linha.cidade != cidadeAtual}">
			<tr>
				<th class="tituloRelatorio">
					<br/>
					${linha.cidade}
				</th>
			</tr>
			<tr class="destaque">
				<th>Discente</th>
			<tr>
		</c:if>
		<tr>
			<td class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
				${linha.matricula} - ${linha.nome_aluno}
				<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
		</tr>
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="turnoAtual" value="${linha.id_turno}"/>
			<c:set var="cidadeAtual" value="${linha.cidade}"/>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
