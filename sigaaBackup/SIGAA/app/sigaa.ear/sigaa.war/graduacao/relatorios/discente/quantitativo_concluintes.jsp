<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@include file="/graduacao/relatorios/cabecalho_curso.jsp"%>

	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: ${ fn:length(resultado) }</b></caption>

	<c:set var="cursoLoop"/>
	<c:set var="municipioLoop"/>
	<c:set var="turnoLoop"/>
    <c:set var="totalQuantidade"  value="0"/>
    <c:set var="totalGraduando" value="0"/>
    <c:set var="totalGraduandoGrupo" value="0"/>
    <c:set var="totalConcluidoGrupo" value="0"/>
    <c:set var="totalConcluido" value="0"/>
	<c:forEach items="${resultado}" var="linha">
		<c:set var="cursoAtual" value="${linha.id_curso}"/>
		<c:set var="municipioAtual" value="${linha.id_municipio}"/>
		<c:set var="turnoAtual" value="${linha.turno}"/>
		
		<c:if test="${cursoLoop != cursoAtual || municipioLoop != municipioAtual || turnoLoop != turnoAtual}">
			<c:if test="${not empty cursoLoop}">
				<tr>
					<td colspan="3" align="center"><hr></td>
				</tr>
				<tr>
					<td colspan="3" align="right"><b>Total de Graduandos: ${totalGraduandoGrupo} / Total de Concluídos: ${totalConcluidoGrupo }</b></td>
				</tr>
				<tr>
					<td colspan="3" align="center"><br></td>
				</tr>
				<c:set var="totalGraduandoGrupo" value="0"/>
    			<c:set var="totalConcluidoGrupo" value="0"/>
			</c:if>
			
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="municipioLoop" value="${municipioAtual}"/>
			<c:set var="turnoLoop" value="${turnoAtual}"/>
			<tr>
				<td colspan="3">
					<br>
					<b>${linha.centro}	- ${linha.curso_nome} - ${linha.municipio_nome} - ${linha.turno} </b><br>
					<hr>
				</td>
			</tr>
			<tr>
				<td><b>Ingresso do Discente</b></td>
				<td><b>Status</b></td>
				<td><b>Quantidade</b></td>
			</tr>
		</c:if>
		<c:set var="totalQuantidade" value="${totalQuantidade + linha.qtd}"/>

		<c:if test="${linha.status == 3}">
			<c:set var="totalConcluido" value="${ totalConcluido + linha.qtd }"/>
			<c:set var="totalConcluidoGrupo" value="${ totalConcluidoGrupo + linha.qtd }"/>
		</c:if>
		<c:if test="${linha.status == 9}">
			<c:set var="totalGraduando" value="${ totalGraduando + linha.qtd }"/>
			<c:set var="totalGraduandoGrupo" value="${ totalGraduandoGrupo + linha.qtd }"/>
		</c:if>

		<tr>
			<td>
				${linha.ano_ingresso}
				-
				${linha.periodo_ingresso}
			</td>
			<td>
				${linha.status_aluno}
			</td>
			<td>
				${linha.qtd}
			</td>
		</tr>
	</c:forEach>
		<tfoot>
			<tr>
				<td colspan="3" align="center"><hr></td>
			</tr>
			<tr>
				<td colspan="3" align="right"><b>Total de Graduandos: ${totalGraduandoGrupo} / Total de Concluídos: ${totalConcluidoGrupo }</b></td>
			</tr>
			<tr>
				<td colspan="3" align="center"><br></td>
			</tr>		
			<tr>
				<th>
					Quantitativo Concluído:
				</th>
				<td>
					<b>${totalConcluido}</b>
				</td>
			</tr>
			<tr>
				<th>
					Quantitativo Graduando:
				</th>
				<td>
					<b>${totalGraduando}</b>
				</td>
			</tr>
			<tr>
				<th>
					Quantitativo Total:
				</th>
				<td>
					<b>${totalQuantidade}</b>
				</td>
			</tr>
		</tfoot>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
