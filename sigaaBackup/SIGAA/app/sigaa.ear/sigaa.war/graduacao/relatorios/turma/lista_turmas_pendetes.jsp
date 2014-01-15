<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>


<%@page import="br.ufrn.academico.dominio.NivelEnsino"%>
<%@page import="br.ufrn.sigaa.ensino.dominio.SituacaoTurma"%>

<c:set var="NIVEL_LATO" value="<%= String.valueOf(NivelEnsino.LATO) %>" />
<c:set var="ABERTO" value="<%= String.valueOf(SituacaoTurma.ABERTA) %>" />
<c:set var="A_DEFINIR_DOCENTE" value="<%= String.valueOf(SituacaoTurma.A_DEFINIR_DOCENTE) %>" />

<f:view>
	
	<h2>Relatório de Turmas Pendentes</h2>
	
	<div id="parametrosRelatorio">
		<table >
			<tr>
				<th>Nível:</th>
				<td><h:outputText value="#{relatorioTurmasPendentes.descricao}" /></td>
			</tr>
		</table>
	</div>

	<br />
	<c:set var="unidadeAtual" />
	<c:set var="contador" value="0" />
	<table class="tabelaRelatorioBorda" width="100%">
		<thead>
			<tr>
				<th>Ano-Período</th>
				<th>Código da Turma</th>
				<th>Código da Disciplina</th>
				<th>Nome da Disciplina</th>
				<th>Carga Horária</th>
				<c:if test="${relatorioTurmasPendentes.nivel == NIVEL_LATO}">
					<th>Curso</th>
				</c:if>
				<th>Descrição</th>
			</tr>	
		</thead>
		<tbody>
			<c:if test="${not empty relatorioTurmasPendentes.turmasPendentes}">
				<c:forEach items="#{relatorioTurmasPendentes.turmasPendentes}" var="turma" varStatus="status">
					
					<c:if test="${(unidadeAtual != turma.disciplina.unidade.nome && contador != 0) }">
						<tr>
							<td colspan="${relatorioTurmasPendentes.nivel == NIVEL_LATO ? '7' : '6'}"><strong>Turmas Neste Departamento:</strong> ${ contador }</td>
						</tr>
						<tr>
							<td colspan="${relatorioTurmasPendentes.nivel == NIVEL_LATO ? '7' : '6'}">&nbsp</td>
						</tr>
					</c:if>				
					<c:if test="${unidadeAtual != turma.disciplina.unidade.nome}">
						<tr>
							<td colspan="${relatorioTurmasPendentes.nivel == NIVEL_LATO ? '7' : '6'}"><strong>${ turma.disciplina.unidade.nome }</strong></td>
							<c:set var="unidadeAtual" value="${ turma.disciplina.unidade.nome }" />
							<c:set var="contador" value="0" />						
						</tr>				
					</c:if>
					<tr>
						<td>${ turma.ano }.${ turma.periodo }</td>
						<td>${ turma.codigo }</td>
						<td>${ turma.disciplina.codigo }</td>
						<td>${ turma.disciplina.nome }</td>
						<td>${ turma.disciplina.detalhes.chTotal }h</td>
						<c:if test="${relatorioTurmasPendentes.nivel == NIVEL_LATO}">
							<td>${ turma.curso.descricao }</td>
						</c:if>
						<td>${ turma.situacaoTurma.id == ABERTO ? 'NÃO CONSOLIDADA' : 'SEM DOCENTE' }</td>
					</tr>
					<c:if test="${unidadeAtual == turma.disciplina.unidade.nome}">
						<c:set var="contador" value="${contador + 1}" />			
					</c:if>					
				</c:forEach>
						<tr>
							<td colspan="${relatorioTurmasPendentes.nivel == NIVEL_LATO ? '7' : '6'}"><strong>Turmas Neste Departamento:</strong> ${ contador }</td>
						</tr>
						<tr>
							<td colspan="${relatorioTurmasPendentes.nivel == NIVEL_LATO ? '7' : '6'}">&nbsp</td>
						</tr>
				
			</c:if>
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
