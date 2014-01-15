<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>


<style>
	#ranking thead tr td {
		border: 1px solid #999;
		border-width: 1px 0;
		background: #EEE;
	}

	#ranking tbody tr td {
		border-bottom: 1px dashed #AAA;
	}

	#ranking tbody tr.divisao td {
		border-bottom: 1px solid #AAA;
	}

	#ranking tbody tr.centro td {
		font-size: 1.3em;
		padding: 10px 10px 3px;
		border-bottom: 1px solid #555;
		font-weight: bold;
		letter-spacing: 1px;
	}
	
	#ranking tbody tr.departamento td {
		font-size: 1em;
		color: #444;
		padding: 10px 20px 3px;
		border-bottom: 1px solid #888;
		font-weight: bold;
		letter-spacing: 1px;
	}
	.colMC{text-align: right !important;width:10%;}
</style>
<br><br>
<body>
<f:view>
	<table width="100%">
		<caption><b>RELATÓRIO DE ALUNOS MATRICULADOS EM ATIVIDADES</b></caption>
	</table>
	<br><br>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<td align="left">
					<b>Ano-Período:</b>
					<h:outputText value="#{relatorioDiscente.ano}" />.<h:outputText value="#{relatorioDiscente.periodo}" />
				</td>
			</tr>
			<tr>
				<td align="left">
					<b>Atividade:</b>
					<c:if test="${ not empty relatorioDiscente.disciplina.codigo}">
						<h:outputText value="#{relatorioDiscente.disciplina.codigo}" /> 
						-
					</c:if> 
					<h:outputText value="#{relatorioDiscente.disciplina.detalhes.nome}" />
				</td>
			</tr>
		</table>
	</div>
	<br>
	<div>
		<c:set var="cursoLoop"/>
		<c:set var="turnoLoop"/>
		<c:set var="grauAcademicoLoop"/>
		<c:set var="habilitacaoLoop"/>
	    <c:set var="componenteLoop"/>		
		<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			<c:set var="turnoAtual" value="${linha.id_turno}"/>
			<c:set var="habilitacaoAtual" value= "${linha.id_habilitacao}"/>
			<c:set var="grauAcademicoAtual" value="${linha.id_grau_academico}"/>
			<c:set var="componenteAtual" value="${linha.disciplina_codigo}"/>
			<c:if test="${componenteLoop != componenteAtual}">
			<br />
				<table width="100%" class="tabelaRelatorioBorda">
				<c:set var="turnoLoop"/>
				<c:set var="cursoLoop"/>
			    <c:set var="componenteLoop" value="${componenteAtual}"/>
				<caption>${linha.disciplina_codigo} - ${linha.disciplina_nome}</caption>
				<tr>
					<td align="center" width="10%"><b>Ingresso</b></td>
					<td width="50%"><b>Discente</b></td>
					<c:if test="${relatorioDiscente.exibirOrientador}">	<td><b>Orientador</b></td></c:if>
					<c:if test="${!acesso.ppg && !relatorioDiscente.portalCoordenadorStricto}"><td class="colMC"><b>MC</b></td></c:if>
				</tr>
			</c:if>	
				<c:if test="${ !acesso.algumUsuarioStricto && (cursoLoop != cursoAtual || turnoLoop != turnoAtual || grauAcademicoLoop != grauAcademicoAtual ||  habilitacaoLoop != habilitacaoAtual)}">
				<c:set var="cursoLoop" value="${cursoAtual}"/>
				<c:set var="turnoLoop" value="${turnoAtual}"/>
				<c:set var="grauAcademicoLoop" value="${grauAcademicoAtual}"/>
				<c:set var="habilitacaoLoop" value="${habilitacaoAtual}"/>			
				<tr  class="departamento">
					<td colspan="3">		
						<b>${linha.centro}	- ${linha.curso_nome} - ${linha.grau_academico_aluno } 
							- <c:if test="${ not empty linha.habilitacao_aluno }"> ${linha.habilitacao_aluno }  /</c:if>
							
							${linha.turno_sigla} </b>
					</td>
				</tr>
				
			</c:if>
			
			<c:if test="${ acesso.algumUsuarioStricto && (cursoLoop != cursoAtual || componenteLoop != componenteAtual )}">
		
				<c:set var="cursoLoop" value="${cursoAtual}"/>
				<c:set var="turnoLoop" value="${turnoAtual}"/>
				<c:set var="componenteLoop" value="${componenteAtual}"/>
				<c:set var="grauAcademicoLoop" value="${grauAcademicoAtual}"/>
				<c:set var="habilitacaoLoop" value="${habilitacaoAtual}"/>
				<tr class="departamento">
				<c:if test="${!acesso.ppg}">
					<td colspan="3">
						<b>${linha.centro}	- ${linha.curso_nome} - ${linha.nivel_curso eq 'E' ? 'MESTRADO' : 'DOUTORADO' }</b>
					</td>
				</c:if>
				<c:if test="${acesso.ppg}">
					<td colspan="3">
						<b>${linha.centro}	- ${linha.curso_nome} - ${linha.nivel_curso eq 'E' ? 'MESTRADO' : 'DOUTORADO' }</b>
					</td>
					
				</c:if>	
				</tr>
			</c:if>
			<tr>
				<td align="center" width="10%">
					${linha.ano_ingresso}-${linha.periodo_ingresso}
				</td>
				<td>
					${linha.matricula} - ${linha.nome_aluno}
					<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
				</td>
				<c:if test="${relatorioDiscente.exibirOrientador}">
					<c:if test="${empty linha.nome_docente}">
						<td align="center"> -- </td>
					</c:if>
					
					<c:if test="${not empty linha.nome_docente}">
					<td>
						${linha.nome_docente} 
					</td>
					</c:if>
				</c:if>
				<c:if test="${!acesso.ppg && !relatorioDiscente.portalCoordenadorStricto}">
					<td class="colMC">
						<ufrn:format type="valor" valor="${linha.mc}"/>
					</td>
				</c:if>	
			</tr>
			
		</c:forEach>
		<br />
		</table>
	</div>	
	<br>
	<table width="100%" class="tabelaRelatorioBorda">
		<tr>
			<td align="center">
				<b>Total de Registros:</b>
				<h:outputText value="#{relatorioDiscente.numeroRegistosEncontrados}"/>
			</td>
		</tr>
	</table>
	<br><br>
	
</f:view>

</body>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
