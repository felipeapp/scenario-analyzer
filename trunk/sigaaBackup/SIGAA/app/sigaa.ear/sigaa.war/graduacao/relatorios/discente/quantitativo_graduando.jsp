<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@include file="/graduacao/relatorios/cabecalho_curso.jsp"%>

	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: ${ fn:length(resultado) }</b></caption>
	<c:set var="cursoLoop"/>
	<c:set var="turnoLoop"/>
	<c:set var="grauAcademicoLoop"/>
    <c:set var="totalQuantidade"  value="0"/>
    <c:set var="totalGraduando" value="0"/>
    <c:set var="totalGraduandoGrupo" value="0"/>
    <c:set var="totalGraduandoCurso" value="0"/>
	<c:forEach items="${resultado}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="municipioAtual" value="${linha.id_municipio}"/>
			<c:set var="turnoAtual" value="${linha.id_turno}"/>
			<c:set var="grauAcademicoAtual" value="${linha.id_grau_academico}"/>
		<c:if test="${cursoLoop != cursoAtual || turnoLoop != turnoAtual || grauAcademicoLoop != grauAcademicoAtual}">
			<c:if test="${not empty cursoLoop}">
				<tr>
					<td colspan="3" align="center"><hr></td>
				</tr>
				<tr>
					<td colspan="2" align="right">Total:</td><td> ${totalGraduandoGrupo}</td>
				</tr>

				<c:if test="${cursoLoop != cursoAtual}">
					<tr>
						<td colspan="3" align="center"><hr></td>
					</tr>
					<tr>
						<td colspan="3" align="center"><b>Total de Graduandos do Curso-Cidade: ${totalGraduandoCurso} </b></td>
					</tr>
					<tr>
						<td colspan="3" align="center"><br></td>
					</tr>
					<c:set var="totalGraduandoCurso" value="0"/>
				</c:if>

				<c:set var="totalGraduandoGrupo" value="0"/>
			</c:if>
			<c:set var="cursoLoop" value="${cursoAtual}"/>
			<c:set var="municipioLoop" value="${municipioAtual}"/>
			<c:set var="turnoLoop" value="${turnoAtual}"/>
			<c:set var="grauAcademicoLoop" value="${grauAcademicoAtual}"/>
			<tr>
				<td colspan="3">
					<br>
					<b>${linha.centro}	- ${linha.curso_nome} - ${linha.municipio_curso} </b><br>
					<b><i>${linha.turno_curso}	- ${linha.modalidade_curso}</i></b><br>
					<hr>
				</td>
			</tr>
			<tr>
				<td><b>Habilitacao</b></td>
				<td><b>Ult. matrícula</b></td>
				<td><b>Quantidade</b></td>
			</tr>
		</c:if>
		<c:set var="totalGraduando" value="${ totalGraduando + linha.qtd_discente }"/>
		<c:set var="totalGraduandoGrupo" value="${ totalGraduandoGrupo + linha.qtd_discente }"/>
		<c:set var="totalGraduandoCurso" value="${ totalGraduandoCurso + linha.qtd_discente }"/>
		<tr>
			<td>
				${empty linha.habilitacao_curso ? 'MODALIDADE SEM HABILITACÃO': linha.habilitacao_curso}
			</td>
			<td>
				${linha.ano_semestre_maximo}
			</td>
			<td>
				${linha.qtd_discente}
			</td>
		</tr>
	</c:forEach>
		<tr>
			<td colspan="3" align="center"><hr></td>
		</tr>
		<tr>
			<td colspan="2" align="right">Total:</td><td> ${totalGraduandoGrupo}</td>
		</tr>
		<tr>
			<td colspan="3" align="center"><hr></td>
		</tr>
		<tr>
			<td colspan="3" align="center"><b>Total de Graduandos do Curso-Cidade: ${totalGraduandoCurso} </b></td>
		</tr>
		<tr>
			<td colspan="3" align="center"><br></td>
		</tr>
		<tr>
			<td colspan="3" align="center"><hr><br><hr></td>
		</tr>
		<tr>
			<td colspan="3" align="center"><b>Total de Graduandos: ${totalGraduando} </b></td>
		</tr>
		<tr>
			<td colspan="3" align="center"><br></td>
		</tr>

	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
