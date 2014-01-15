<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
</style>
<f:view>

<h2>${relatoriosDepartamentoCpdi.titulo}</h2>

<div id="parametrosRelatorio">
	<table>
		<tr>
			<th>Centro:</th>
			<td>
				${usuario.vinculoAtivo.unidade.unidadeGestora.codigoNome}
			</td>
		</tr>
		<tr>
			<th width="100px">Ano.Período:</th>
			<td>
				${relatoriosDepartamentoCpdi.ano}.${relatoriosDepartamentoCpdi.periodo}
			</td>
		</tr>
</table>
</div>
<br/>
<table class="tabelaRelatorioBorda" align="center" width="100%">
	<thead>
		<tr>
			<th>Item</th>
			<th style="text-align: right;" width="80px">Quantidade</th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="linha" items="#{qtdDiscentesGraduacao}">
			<tr>
				<td>Discentes de Graduação Ativos</td>
				<td style="text-align: right;">${linha.ativos}</td>
			</tr>
			<tr>
				<td>Discentes de Graduação Matriculados</td>
				<td style="text-align: right;">${linha.matriculados}</td>
			</tr>
		</c:forEach>
		<c:forEach var="linha" items="#{qtdCursoDepartamento}">
			<tr>
				<td>Cursos de Graduação</td>
				<td style="text-align: right;">${linha.cursos}</td>
			</tr>
			<tr>
				<td>Departamentos Acadêmicos</td>
				<td style="text-align: right;">${linha.departamentos}</td>
			</tr>
		</c:forEach>
		<c:forEach var="linha" items="#{qtdDiscentesPos}">
			<tr>
				<td>Discentes de Pós-Graduação Ativos</td>
				<td style="text-align: right;">${linha.ativos}</td>
			</tr>
			<tr>
				<td>Discentes de Pós-Graduação Matriculados</td>
				<td style="text-align: right;">${linha.matriculados}</td>
			</tr>
		</c:forEach>
		<c:forEach var="linha" items="#{qtdCursosPos}">
			<tr>
				<td>Cursos de Especialização</td>
				<td style="text-align: right;">${linha.cursos_especializacao}</td>
			</tr>
			<tr>
				<td>Cursos de Mestrado</td>
				<td style="text-align: right;">${linha.cursos_mestrado}</td>
			</tr>
			<tr>
				<td>Cursos de Doutorado</td>
				<td style="text-align: right;">${linha.cursos_doutorado}</td>
			</tr>
		</c:forEach>
		<c:forEach var="linha" items="#{qtdTesesDissertacoes}">
			<tr>
				<td>Teses</td>
				<td style="text-align: right;">${linha.teses}</td>
			</tr>
			<tr>
				<td>Dissertações</td>
				<td style="text-align: right;">${linha.dissertacoes}</td>
			</tr>
		</c:forEach>
		<c:forEach var="linha" items="#{qtdBasesPesquisa}">
			<tr>
				<td>Bases de Pesquisa</td>
				<td style="text-align: right;">${linha.pesquisas}</td>
			</tr>
		</c:forEach>
		<c:forEach var="linha" items="#{qtdProjetosExtensao}">
			<tr>
				<td>Projetos de Extensão</td>
				<td style="text-align: right;">${linha.projetos}</td>
			</tr>
		</c:forEach>
		<tr>
			<td colspan="2"><b>Comunidade Universitária do Centro</b></td>
		</tr>
		<c:forEach var="linha" items="#{qtdServidor}">
			<c:forEach var="mapa" items="#{linha}">
				<tr>
					<td>${mapa.key}</td>
					<td style="text-align: right;">${mapa.value}</td>
				</tr>
			</c:forEach>
		</c:forEach>
		<tr>
			<td colspan="2"><b>Docentes do Centro por Titulação</b></td>
		</tr>
		<c:forEach var="linha" items="#{qtdDocentesCentro}">
			<tr>
				<td>Graduação</td>
				<td style="text-align: right;">${linha.graduacao}</td>
			</tr>
			<tr>
				<td>Especialista</td>
				<td style="text-align: right;">${linha.especialista}</td>
			</tr>
			<tr>
				<td>Mestrado</td>
				<td style="text-align: right;">${linha.mestrado}</td>
			</tr>
			<tr>
				<td>Doutorado</td>
				<td style="text-align: right;">${linha.doutorado}</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<br/>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>