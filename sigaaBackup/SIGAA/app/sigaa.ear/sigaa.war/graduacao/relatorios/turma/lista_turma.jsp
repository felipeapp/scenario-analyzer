<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	#relatorio h3 {text-align: center; font-size: 11px;}
	#relatorio tr.itemRel td {padding: 1px 0 0 ;}
	#relatorio tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555}
	#relatorio tr.header td {padding: 3px ; background-color: #eee;}
	#relatorio tr.subheader td {padding: 1px ; background-color: #eee;}
	#relatorio tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	#relatorio tr.componentes td {padding: 2px ; border-bottom: 1px dashed #888}
	#relatorio tr.componentescur td {padding: 1px ; font-size: 11px; vertical-align: top;}
	#relatorio tr.componentescur2 td {padding: 2px ; font-size: 9px; vertical-align: top;}
	
	#percentuais tr td {padding: 0 3px;}
	#percentuais tr td span {font-weight: bold;}
	
	table.totais {padding: 1; width: 50%; font-size: 10px; align: center;}
	table.totais caption {text-align: center;font-weight: bold; border-bottom: 1px solid #555;background-color: #eee;}
	table.totais tr th {text-align: left;font-weight: bold;}
	table.totais tr td {text-align: right;}
	
	.bold {font-weight: bold;}
</style>
<f:view>
	<h2>Relatório de Turmas</h2>
<div id="parametrosRelatorio">
	<table>
		<tr class="itemRel">
			<th>Ano-Período:</th>
			<td>
				<h:outputText rendered="#{!relatorioTurma.filtroAnoPeriodo}" value="TODOS"/>
				<h:outputText rendered="#{relatorioTurma.filtroAnoPeriodo}" value="#{relatorioTurma.ano}.#{relatorioTurma.periodo}"/>
			</td>
		</tr>
		<c:if test="${relatorioTurma.filtroCentro}">
			<tr class="itemRel">
				<th>Centro:</th>
				<td>
					<h:outputText value="#{relatorioTurma.centro.nome}"/>
				</td>
			</tr>
		</c:if>
		<c:if test="${relatorioTurma.filtroDepartamento}">
			<tr class="itemRel">
				<th>Unidade:</th>
				<td>
					<h:outputText value="#{relatorioTurma.departamento.nome}"/>
				</td>
			</tr>
		</c:if>
		<c:if test="${not relatorioTurma.filtroCentro}">
			<tr class="itemRel">
				<th>Centro:</th>
				<td>
					TODOS
				</td>
			</tr>
		</c:if>
		<c:if test="${not relatorioTurma.filtroDepartamento}">
			<tr class="itemRel">
				<th>Unidade:</th>
				<td>
					TODAS
				</td>
			</tr>
		</c:if>
<tr class="itemRel">
			<th>Situação da Turma:</th>
			<td>
				<h:outputText value="#{relatorioTurma.filtroSituacaoTurma ? relatorioTurma.situacaoTurma.descricao : 'TODAS'}"/>
			</td>
		</tr>
		<tr class="itemRel">
		

		<tr class="itemRel">
			<th>Reserva de Curso:</th>
			<td>
				<h:outputText value="#{relatorioTurma.filtroReservaCurso ? relatorioTurma.curso.descricao : 'TODAS TURMAS COM/SEM RESERVA'}"/>
			</td>
		</tr>
</table>
</div>
<br>
<table width="100%" style="font-size: 10px">
	<caption><b>Legenda</b></caption>
		<tr>
			<td><b>CT:</b><i> Código da Turma</i> </td>
			<td><b>AP:</b> <i>Nº de alunos aprovados</i></td>
		</tr>
		<tr>
			<td><b>RP:</b> <i>Nº de alunos reprovados</i></td>
			<td><b>RF:</b> <i>Nº de alunos reprovados por falta</i></td>
		</tr>
		<tr>
			<td><b>TR:</b> <i>Nº de alunos trancados</i></td>
			<td><b>CA:</b> <i>Nº de alunos cancelados</i></td>
		</tr>
		<tr>
			<td><b>MA:</b> <i>Nº de alunos matriculados</i></td>
			<td><b>ES:</b> <i>Nº de alunos em espera</i></td>
		</tr>
</table>
	<hr>

	<c:set var="anoSemestre"/>
	<c:set var="departamento_loop"/>
	<c:set var="turma_loop"/>
	<c:set var="docente_loop"/>
	<c:set var="reserva"/>
	
	<c:set var="totalComponentes" value="0"/>
	<c:set var="totalAprovados" value="0"/>
	<c:set var="totalReprovadosNota" value="0"/>
	<c:set var="totalReprovadosFalta" value="0"/>
	<c:set var="totalTrancados" value="0"/>
	<c:set var="totalMatriculados" value="0"/>
	<c:set var="totalEspera" value="0"/>
	<c:set var="totalCancelados" value="0"/>
	
	<c:forEach items="${relatorioTurma.listaTurma}" var="linha">
			<c:set var="totalLinha" value="0"/>
			<c:set var="departamentoAtual" value="${linha.id_departamento}"/>
			<c:set var="turmaAtual" value="${linha.id_turma}"/>
			<c:set var="docenteAtual" value="${linha.siape}"/>
			<c:set var="reservaAtual" value="${linha.id_curso}"/>
			<c:set var="anoSemestreAtual" value="${linha.ano}.${linha.periodo}"/>
		<c:if test="${anoSemestre != anoSemestreAtual  || departamento_loop != departamentoAtual}">
			<c:set var="departamento_loop" value="${departamentoAtual}"/>
			<c:set var="anoSemestre" value="${anoSemestreAtual}"/>
			<table cellspacing="1" width="100%" style="font-size: 10px;">
				<tr class="curso">
					<td colspan="10">
						<b>${anoSemestre} - ${linha.centro_depto} - ${linha.departamento}</b>
					</td>
				</tr>
			</table>
	    </c:if>
	    	<table cellspacing="1" width="100%" style="font-size: 10px;" >
		    <c:choose>
			    <c:when test="${turma_loop != turmaAtual}">
    					<tr class="header">
							<td width="50%" class="bold">Componente Curricular</td>
							<td class="bold">CT</td>
							<td class="bold">Situação</td>
							<td class="bold">AP</td>
							<td class="bold">RP</td>
							<td class="bold">RF</td>
							<td class="bold">TR</td>
							<td class="bold">MA</td>
							<td class="bold">ES</td>
							<td class="bold">CA</td>
							<td class="bold">Total</td>
						</tr>
						<tr class="subheader">
							<td><b>Docente(s)</b></td>
							<td colspan="11"><b>Reserva(s)</b></td>
						</tr>
				    	<c:if test="${not empty turma_loop}">
				    		<tr class="componentes">
				    			<td colspan="11" > </td>
				    		</tr>
				    	</c:if>
				    	<c:set var="turma_loop" value="${turmaAtual}"/>
						<tr class="componentescur">
							<td>
								${linha.componente_codigo } - ${linha.componente_nome } - ${linha.ch_total }h
								<c:set var="totalComponentes" value="${totalComponentes + 1}"/>
							</td>
							<td align="center">
								T${linha.turma_codigo }
							</td>
							<td>
								${linha.situacao_turma_desc }
							</td>
							<td align="center">
								<fmt:formatNumber value="${linha.aprovados}" type="number"/>
								<c:set var="totalAprovados" value="${totalAprovados + linha.aprovados}"/>
							</td>
							<td align="center">
								<fmt:formatNumber value="${linha.reprovados_nota}" type="number"/>	
								<c:set var="totalReprovadosNota" value="${totalReprovadosNota + linha.reprovados_nota}"/>
							</td>
							<td align="center">
								<fmt:formatNumber value="${linha.reprovados_falta}" type="number"/>
								<c:set var="totalReprovadosFalta" value="${totalReprovadosFalta + linha.reprovados_falta}"/>
							</td>
							<td align="center">
								<fmt:formatNumber value="${linha.trancados}" type="number"/>
								<c:set var="totalTrancados" value="${totalTrancados + linha.trancados}"/>
							</td>
							<td align="center">
								<fmt:formatNumber value="${linha.matriculados}" type="number"/>
								<c:set var="totalMatriculados" value="${totalMatriculados + linha.matriculados}"/>
							</td>
							<td align="center">
								<fmt:formatNumber value="${linha.espera}" type="number"/>
								<c:set var="totalEspera" value="${totalEspera + linha.espera}"/>
							</td>
							<td align="center">
								<fmt:formatNumber value="${linha.cancelados}" type="number"/>
								<c:set var="totalCancelados" value="${totalCancelados + linha.cancelados}"/>
							</td>
							<td align="center">
								<c:set var="totalLinha" value="${linha.aprovados + linha.reprovados_nota + linha.reprovados_falta + linha.trancados + linha.matriculados + linha.espera + linha.cancelados}"/>
								<fmt:formatNumber value="${totalLinha}" type="number"/>
							</td>							
						</tr>
					
						<c:set var="nomeDocente" value="${linha.docentes_turma}"/>
						<c:set var="nomeCurso" value="${linha.unidade_curso}  - ${linha.curso_reserva } / ${linha.numero_vagas} vagas "/>
						<tr>
							<td>
								<i>${fn:replace((not empty linha.docentes_turma ? nomeDocente : '<i>Sem Docente Definido</i>'),',','<br>')}</i>
							</td>
							<td colspan="11">
								<i>${not empty linha.unidade_curso ? nomeCurso : '<i>Sem Reserva</i>'}</i>
							</td>
						</tr>
						<tr class="subheader">
							<td colspan="12" class="bold">Percentuais:</td>
						</tr>
						<tr>
							<td colspan="12">
								<table id="percentuais">
									<tr>
										<td><span>AP</span> = <fmt:formatNumber value="${linha.aprovados / totalLinha}" type="percent" minFractionDigits="2" maxFractionDigits="2" /></td>
										<td><span>RP</span> = <fmt:formatNumber value="${linha.reprovados_nota / totalLinha}" type="percent" minFractionDigits="2" maxFractionDigits="2" /></td>
										<td><span>RF</span> = <fmt:formatNumber value="${linha.reprovados_falta / totalLinha}" type="percent" minFractionDigits="2" maxFractionDigits="2" /></td>
										<td><span>TR</span> = <fmt:formatNumber value="${linha.trancados / totalLinha}" type="percent" minFractionDigits="2" maxFractionDigits="2" /></td>
										<td><span>MA</span> = <fmt:formatNumber value="${linha.matriculados / totalLinha}" type="percent" minFractionDigits="2" maxFractionDigits="2" /></td>
										<td><span>ES</span> = <fmt:formatNumber value="${linha.espera / totalLinha}" type="percent" minFractionDigits="2" maxFractionDigits="2" /></td>
										<td><span>CA</span> = <fmt:formatNumber value="${linha.cancelados / totalLinha}" type="percent" minFractionDigits="2" maxFractionDigits="2" /></td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td colspan="12"><br/><br/></td>
						</tr>
				</c:when>
				<c:otherwise>
				    <c:if test="${ not empty linha.docente_nome or not empty linha.curso_reserva }">
					    <c:set var="nomeDocente2" value="${linha.docentes_turma}"/>
					    <c:set var="nomeCurso2" value="${linha.unidade_curso}  - ${linha.curso_reserva } / ${linha.numero_vagas} vagas "/>
					    <tr>
							<td>
								<i>${nomeDocente ne nomeDocente2 ? nomeDocente2 : ''} </i>
							</td>
							<td colspan="11">
								<i>${nomeCurso ne nomeCurso2 ? nomeCurso2 : ''}</i>
							</td>
						</tr>
					</c:if>
				</c:otherwise>
			</c:choose>
		</table>
	</c:forEach>
	<div style="clear: both;">
		<br><br>
		<c:choose>
			<c:when test="${relatorioTurma.numeroRegistosEncontrados ne 0 }">
				<center>
				<table class="totais">
				    <caption>Totais</caption>
					<tr>
						<th>Componentes</th>
						<td><fmt:formatNumber value="${totalComponentes}" type="number"/></td>
					</tr>
					<tr>
						<th>Aprovados</th>
						<td><fmt:formatNumber value="${totalAprovados}" type="number"/></td>
					</tr>
					<tr>
						<th>Reprovados por Nota</th>
						<td><fmt:formatNumber value="${totalReprovadosNota}" type="number"/></td>
					</tr>
					<tr>
						<th>Reprovados por Falta</th>
						<td><fmt:formatNumber value="${totalReprovadosFalta}" type="number"/></td>
					</tr>
					<tr>
						<th>Trancados</th>
						<td><fmt:formatNumber value="${totalTrancados}" type="number"/></td>
					</tr>
					<tr>
						<th>Matriculados</th>
						<td><fmt:formatNumber value="${totalMatriculados}" type="number"/></td>
					</tr>
					<tr>
						<th>Em Espera</th>
						<td><fmt:formatNumber value="${totalEspera}" type="number"/></td>
					</tr>
					<tr>
						<th>Cancelados</th>
						<td><fmt:formatNumber value="${totalCancelados}" type="number"/></td>
					</tr>
				</table>
				</center>
			</c:when>
			<c:otherwise>
			    <h3>Nenhum Registro Encontrado</h3>
			</c:otherwise>
		</c:choose>
	</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
