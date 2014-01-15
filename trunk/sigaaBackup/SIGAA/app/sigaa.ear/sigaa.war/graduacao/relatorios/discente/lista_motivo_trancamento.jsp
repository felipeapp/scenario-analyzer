<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h2>Relatório de Aluno por Solicitação de Trancamento</h2>
	<div id="parametrosRelatorio">
		<table>
				<tr>
					<th>Ano-Semestre: </th>
					<td>
						<c:choose>
							<c:when test="${relatorioDiscente.filtroAnoPeriodo}">
								${relatorioDiscente.ano}.${relatorioDiscente.periodo} 
							</c:when>
							<c:otherwise>
						 		TODOS
							</c:otherwise>
						</c:choose>
					</td>
				</tr>
				<tr>
					<th>Motivo do Trancamento: </tg>
					<td>
						<h:outputText value="#{relatorioDiscente.filtroMotivoTrancamento ? relatorioDiscente.motivoTrancamento.descricao : 'TODOS'}"/>
					</td>
				</tr>
				<tr>
					<th>Centro:</th>
					<td>
						<h:outputText value="#{relatorioDiscente.filtroCentro ? relatorioDiscente.centro.nome : 'TODOS'}"/>
					</td>
				</tr>
				<tr>
					<th>Curso:</th>
					<td>
						<h:outputText value="#{relatorioDiscente.filtroCurso ? relatorioDiscente.curso.descricao : 'TODOS'}"/>
					</td>
				</tr>
				<tr>
					<th>Matriz Curricular:</th>
					<td>
						<h:outputText value="#{relatorioDiscente.filtroMatriz ? relatorioDiscente.matrizCurricular.descricaoMin : 'TODAS'}"/>
					</td>
				</tr>
	
		</table>
	</div>
	<br/>
	<c:set var="_curso"/>
	<c:set var="_matriz"/>
	<c:set var="_anoPeriodo"/>
	<c:set var="_componenteCurricular"/>
	<c:set var="_fechaTabela" value="false"/>
	<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha">
		<c:set var="_cursoAtual" value="${linha.id_curso}"/>
		<c:set var="_matrizAtual" value="${linha.id_matriz_curricular}"/>
		<c:set var="_anoPeriodoAtual" value="${(linha.ano * 10) + linha.periodo}"/>
		<c:set var="_componenteCurricularAtual" value="${linha.componente_codigo}"/>
		<c:if test="${ _anoPeriodo != _anoPeriodoAtual}">
			<c:set var="_anoPeriodo" value="${_anoPeriodoAtual}"/>
			<c:if test="${fechaTabela}">
					</tbody>
				</table>
				<br/>
				<c:set var="fechaTabela" value="false"/>
			</c:if>
			<h3>Trancamentos em ${linha.ano}.${linha.periodo}</h3>
			<br/>
		</c:if>
		<c:if test="${ _componenteCurricular != _componenteCurricularAtual}">
			<c:if test="${fechaTabela}">
					</tbody>
				</table>
				<br/>
				<c:set var="fechaTabela" value="false"/>
			</c:if>
			<c:set var="_componenteCurricular" value="${_componenteCurricularAtual}"/>
			<c:set var="fechaTabela" value="true"/>
			<table class="tabelaRelatorio" width="100%">
				<caption>${linha.componente_codigo} - ${linha.componente_nome }</caption>
				<thead>
					<tr>
						<th style="text-align: right;" width="5%">Matrícula</th>
						<th style="text-align: left;" width="40%">Nome</th>
						<th style="text-align: left;" width="55%">Motivo Trancamento</th>
						<th style="text-align: left;" width="10%">Situação Atual</th>
					</tr>
				</thead>
				<tbody>
		</c:if>
		<c:if test="${_curso != _cursoAtual || _matriz != _matrizAtual}">
		<c:set var="_curso" value="${_cursoAtual}"/>
		<c:set var="_matriz" value="${_matrizAtual}"/>
			<tr>
				<td colspan="4" class="caixaCinzaMedio">
					<b>
					${linha.centro_sigla}	- ${linha.curso_nome} - ${linha.cidade_curso} - 
					<i>${linha.turno_descricao} - ${linha.modalidade_nome} - ${ empty linha.habilitacao? "<i>MODALIDADE SEM HABILITAÇÃO</i>": linha.habilitacao }</i>
					</b>
				</td>
			</tr>
		</c:if>
		<tr>
			<td valign="top" style="text-align: right;">
				${linha.matricula}
			</td>
			<td valign="top" style="text-align: left;">
				 ${linha.aluno_nome }
				 <c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
			</td>
			<td valign="top" style="text-align: left;">
				<c:set var="componenteCurricularDesc" value="${linha.motivo_descricao} - ${linha.justificativa}"/>
				${linha.id_motivo_trancamento == 6 ? componenteCurricularDesc : linha.motivo_descricao  }
			</td>
			<td valign="top" style="text-align: left;">
				${linha.situacao_trancamento }
			</td>
		</tr>
	</c:forEach>
	<c:if test="${fechaTabela}">
			</tbody>
		</table>
		<c:set var="fechaTabela" value="false"/>
	</c:if>
	<br/>
	<c:choose>
		<c:when test="${relatorioDiscente.numeroRegistosEncontrados != 0 }">
			<div align="center">Total de Registros: <h:outputText value="#{relatorioDiscente.numeroRegistosEncontrados}"/></div>
		</c:when>
		<c:otherwise>
			<div align="center"><span class="semRegistro">Nenhum registro encontrado</span></div>
		</c:otherwise>
	</c:choose>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
