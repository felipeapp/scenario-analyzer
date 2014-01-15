<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@include file="/graduacao/relatorios/cabecalho_curso.jsp"%>
<br/>

<c:if test="${not empty resultado}">

	<c:forEach items="${resultado}" var="linha" end="1">
		<c:set var="_statusDiscente" value="${linha.status_aluno}" />
	</c:forEach>


	<h2>DISCENTES ${ _statusDiscente } NO PERÍODO ATUAL</h2>
	
	<c:set var="cursoLoop"/>
	<c:set var="turnoLoop"/>
	<c:set var="cidadeLoop"/>
    <c:set var="grauAcademicoLoop"/>
    <c:set var="fechaTabela" value="false" />
	<c:forEach items="${resultado}" var="linha">
		<c:set var="cursoAtualLoop" value="${linha.id_curso}"/>
		<c:set var="turnoAtual" value="${linha.id_turno}"/>
		<c:set var="cidadeAtual" value="${linha.id_municipio}"/>
		<c:set var="grauAcademicoAtual" value="${linha.id_grau_academico}"/>
		<c:if test="${cursoLoop != cursoAtualLoop || turnoLoop != turnoAtual || cidadeLoop != cidadeAtual || grauAcademicoLoop != grauAcademicoAtual}">
			<c:if test="${fechaTabela}">
					</tbody>
				</table>
				<br/>
			</c:if>
			<c:set var="fechaTabela" value="true" />
			<table class="tabelaRelatorio" width="100%">
				<c:set var="cursoLoop" value="${cursoAtualLoop}"/> 
				<c:set var="turnoLoop" value="${turnoAtual}"/>
				<c:set var="cidadeLoop" value="${cidadeAtual}"/>
				<c:set var="grauAcademicoLoop" value="${grauAcademicoAtual}"/>
				<thead>
					<tr>
						<th colspan="7">
							${linha.unidade_sigla}- ${linha.curso_nome} - ${linha.modalidade_nome} - ${linha.turno_sigla} - ${linha.municipio}
						</th>
					</tr>
					<tr>
						<th style="text-align: center;">Ingresso</th>
						<th style="text-align: right; padding-right: 5px;">Matricula</th>
						<th style="text-align: left;">Nome</th>
						<th style="text-align: right;">IRA</th>
						<th style="text-align: right;">MC</th>
						<th style="text-align: right;">MCN</th>
						<th style="text-align: left;">Status</th>
					</tr>
				</thead>
				<tbody>
		</c:if>
		<tr>
			<td style="text-align: center;"> ${linha.ano_ingresso}.${linha.periodo_ingresso} </td>
			<td style="text-align: right; padding-right: 5px;"> ${linha.matricula}</td>
			<td style="text-align: left;"> ${linha.aluno_nome}</td>
			<td style="text-align: right;"> ${linha.ira} </td>
			<td style="text-align: right;"> ${linha.mc} </td>
			<td style="text-align: right;"> ${linha.mcn} </td>
			<td style="text-align: left;"> ${linha.status_aluno} </td>
		</tr>
	</c:forEach>
	<c:if test="${fechaTabela}">
			</tbody>
		</table>
	</c:if>
	<BR/>
	<div align="center">TOTAL DE DISCENTES ${ _statusDiscente } NO PERÍODO ATUAL: ${ fn:length(resultado) }</div>
</c:if>
<br/>
<c:if test="${not empty outrosGraduandos}">
	<c:forEach items="${outrosGraduandos}" var="linha" end="1">
		<c:set var="_statusDiscente" value="${linha.status_aluno}" />
	</c:forEach>
	
	<h2>DISCENTES ${ _statusDiscente } EM OUTROS PERÍODOS</h2>
	
	<c:set var="cursoLoop"/>
	<c:set var="turnoLoop"/>
	<c:set var="cidadeLoop"/>
    <c:set var="grauAcademicoLoop"/>
    <c:set var="fechaTabela" value="false" />
	<c:forEach items="${outrosGraduandos}" var="linha">
		<c:set var="cursoAtualLoop" value="${linha.id_curso}"/>
		<c:set var="turnoAtual" value="${linha.id_turno}"/>
		<c:set var="cidadeAtual" value="${linha.id_municipio}"/>
		<c:set var="grauAcademicoAtual" value="${linha.id_grau_academico}"/>
		<c:if test="${cursoLoop != cursoAtualLoop || turnoLoop != turnoAtual || cidadeLoop != cidadeAtual || grauAcademicoLoop != grauAcademicoAtual}">
			<c:if test="${fechaTabela}">
					</tbody>
				</table>
				<br/>
			</c:if>
			<c:set var="fechaTabela" value="true" />
			<table class="tabelaRelatorio" width="100%">
				<c:set var="cursoLoop" value="${cursoAtualLoop}"/> 
				<c:set var="turnoLoop" value="${turnoAtual}"/>
				<c:set var="cidadeLoop" value="${cidadeAtual}"/>
				<c:set var="grauAcademicoLoop" value="${grauAcademicoAtual}"/>
				<thead>
					<tr>
						<th colspan="7">
							${linha.unidade_sigla}- ${linha.curso_nome} - ${linha.modalidade_nome} - ${linha.turno_sigla} - ${linha.municipio}
						</th>
					</tr>
					<tr>
						<th style="text-align: center;">Ingresso</th>
						<th style="text-align: right; padding-right: 5px;">Matricula</th>
						<th style="text-align: left;">Nome</th>
						<th style="text-align: right;">IRA</th>
						<th style="text-align: right;">MC</th>
						<th style="text-align: right;">MCN</th>
						<th style="text-align: center;">Ano-Período<br/>${ _statusDiscente }</th>
					</tr>
				</thead>
				<tbody>
		</c:if>
		<tr>
			<td style="text-align: center;"> ${linha.ano_ingresso}.${linha.periodo_ingresso} </td>
			<td style="text-align: right; padding-right: 5px;"> ${linha.matricula}</td>
			<td style="text-align: left;">
				 ${linha.aluno_nome}
				 <c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<td style="text-align: right;"> ${linha.ira} </td>
			<td style="text-align: right;"> ${linha.mc} </td>
			<td style="text-align: right;"> ${linha.mcn} </td>
			<td style="text-align: center;"> ${linha.ano_graduando}.${linha.periodo_graduando} </td>
		</tr>
	</c:forEach>
	<c:if test="${fechaTabela}">
			</tbody>
		</table>
	</c:if>
	<BR/>
	<div align="center">TOTAL DE DISCENTES ${ _statusDiscente } EM OUTROS PERÍODOS: ${ fn:length(outrosGraduandos) }</div>
</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
