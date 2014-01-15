<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
<h2>Relatório de Turmas por Departamento</h2>

<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Departamento:</th>
			<td><h:outputText value="#{relatoriosDepartamentoCpdi.departamento.nome}"/></td>
		</tr>
		<tr>
			<th>Ano-Período:</th>
			<td>
				<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}">
					TODOS
					<c:set var="divisorMedia" value="2" />
				</c:if>
				<c:if test="${relatoriosDepartamentoCpdi.periodo != -1}">
					<h:outputText value="#{relatoriosDepartamentoCpdi.ano}"/>.<h:outputText value="#{relatoriosDepartamentoCpdi.periodo}"/>
					<c:set var="divisorMedia" value="1" />
				</c:if>
			</td>
		</tr>
	</table>
</div>
<br/>
	<c:set var="_docenteGrupo"/>
	<c:set var="_nivelGrupo"/>
	<c:set var="_nivelAnterior"/>
	<c:set var="_semestreGrupo" value="1"/>
	<c:set var="fechaTabela" value="false"/>
	
	<c:set var="stChTotalSemestre" value="0" />
	<c:set var="stChDedicadaSemestre" value="0" />
	<c:set var="stDiscentesSemestre" value="0" />
	<c:set var="qtdTurmasSemestre" value="0" />
	
	<c:set var="totalChTotalDocente" value="0" />
	<c:set var="totalChDedicadaDocente" value="0" />
	<c:set var="totalDiscentesDocente" value="0" />
	<c:set var="totalTurmasDocente" value="0" />
	
	<c:forEach items="${relatoriosDepartamentoCpdi.lista}" var="linha">
		<c:set var="_docenteAtual" value="${linha.siape}"/>
		<c:set var="_nivelAtual" value="${linha.nivel}"/>
		<c:set var="_semestreAtual" value="${linha.periodo}"/>
		
		<c:set var="_separadorEad" value="${linha.ano}.${periodo}_${codComponente}_${siape}" />
		
		<c:if test="${_semestreAtual != _semestreGrupo}">
			<c:set var="_semestreGrupo" value="${_semestreAtual}" />
		</c:if>	
		<!--  fecha a tabela de grupo de turma por docente, coloca o total no final e abre nova tabela para o próximo docente. -->
		<c:if test="${_docenteAtual != _docenteGrupo}">
			<c:set var="_docenteGrupo" value="${_docenteAtual}" />
			<c:set var="_separadorEadAtual" value="" />
			<c:if test="${fechaTabela}">
				<c:if test="${qtdTurmasSemestre != 0}">
					<tr class="caixaCinzaMedio" style="text-align: right; font-weight: bold; font-size: 10px">
						<td colspan="4">
							Subtotal ${ _nivelAnterior == 'G' ? 'Graduação' 
									: _nivelAnterior == 'T' ? 'Técnico' 
									: _nivelAnterior == 'B' ? ' Básico'
									: _nivelAnterior == 'M' ? 'Médio'
									: _nivelAnterior == 'I' ? 'Infantil' 
									: 'Pós-Graduação' }
							<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (média)</c:if>: 
						</td>
						<td>${qtdTurmasSemestre}</td>
						<td>
							${stChTotalSemestre} 
							<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${stChTotalSemestre / qtdTurmasSemestre}" />)</c:if>
						</td>
						<td>
							${stChDedicadaSemestre} 
							<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${stChDedicadaSemestre / qtdTurmasSemestre}"/>)</c:if>
						</td>
						<td>
							${stDiscentesSemestre} 
							<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${stDiscentesSemestre / qtdTurmasSemestre}"/>)</c:if>
						</td>
					</tr>
				</c:if>
				<c:if test="${totalTurmasDocente != 0}">
					<tr class="caixaCinzaMedio" style="text-align: right; font-weight: bold; font-size: 10px">
						<td colspan="4">
							Total Geral
							<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (média no ano)</c:if>: 
						</td>
						<td>${totalTurmasDocente}</td>
						<td>
							${totalChTotalDocente} 
							<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${totalChTotalDocente / qtdTurmasSemestre}" />)</c:if>
						</td>
						<td>
							${totalChDedicadaDocente} 
							<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${totalChDedicadaDocente / qtdTurmasSemestre}"/>)</c:if>
						</td>
						<td>
							${totalDiscentesDocente} 
							<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${totalDiscentesDocente / qtdTurmasSemestre}"/>)</c:if>
						</td>
					</tr>
				</c:if>
				</tbody>
				</table>
				<c:set var="stChTotalSemestre" value="0" />
				<c:set var="stChDedicadaSemestre" value="0" />
				<c:set var="stDiscentesSemestre" value="0" />
				<c:set var="qtdTurmasSemestre" value="0" />
				<c:set var="totalChTotalDocente" value="0" />
				<c:set var="totalChDedicadaDocente" value="0" />
				<c:set var="totalDiscentesDocente" value="0" />
				<c:set var="totalTurmasDocente" value="0" />
				<c:set var="_nivelGrupo"/>
				<br />
				<c:set var="fechaTabela" value="false"/>
			</c:if>
			<!-- Tabela agrupadora de turmas por docente -->
			<table class="tabelaRelatorioBorda" width="100%">
				<c:set var="_nivelGrupo"/>
				<c:set var="fechaTabela" value="true"/>
				<caption>
						Docente: ${linha.docente_nome}<br/>
						SIAPE: ${linha.siape} / 
						Regime: 
						<c:if test="${linha.dedicacao_exclusiva}"> DE </c:if>
						<c:if test="${not linha.dedicacao_exclusiva}"> ${linha.regime_trabalho}h</c:if> / 
						Classe Funcional: ${linha.denominacao_classe_funcional}
				</caption>
				<thead>
					<tr>
						<th style="text-align: center">Ano-Período</th>
						<th>Tipo</th>
						<th>Código</th>
						<th>Nome</th>
						<th style="text-align: right;" width="15px">Turma</th>
						<th style="text-align: right;" width="65px">Ch. Total</th>
						<th style="text-align: right;" width="65px">Ch. Dedicada</th>
						<th style="text-align: right;" width="65px">Qtd. Alunos</th>
					<tr>
				</thead>
				<tbody>
		</c:if>
		<!-- Linha dividindo as turmas por nível de ensino. -->
		<c:if test="${_nivelAtual != _nivelGrupo}">
			<c:set var="_nivelGrupo" value="${_nivelAtual}" />
			<c:if test="${qtdTurmasSemestre != 0}">
				<tr class="caixaCinzaMedio" style="text-align: right; font-weight: bold; font-size: 10px">
					<td colspan="4">
						Subtotal ${ _nivelAnterior == 'G' ? 'Graduação' 
								: _nivelAnterior == 'T' ? 'Técnico' 
								: _nivelAnterior == 'B' ? ' Básico'
								: _nivelAnterior == 'M' ? 'Médio'
								: _nivelAnterior == 'I' ? 'Infantil' 
								: 'Pós-Graduação' }
						<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (média)</c:if>: 
					</td>
					<td>${qtdTurmasSemestre}</td>
					<td>
						${stChTotalSemestre} 
						<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${stChTotalSemestre / qtdTurmasSemestre}" />)</c:if>
					</td>
					<td>
						${stChDedicadaSemestre} 
						<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${stChDedicadaSemestre / qtdTurmasSemestre}"/>)</c:if>
					</td>
					<td>
						${stDiscentesSemestre} 
						<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${stDiscentesSemestre / qtdTurmasSemestre}"/>)</c:if>
					</td>
				</tr>
			</c:if>
			<c:set var="stChTotalSemestre" value="0" />
			<c:set var="stChDedicadaSemestre" value="0" />
			<c:set var="stDiscentesSemestre" value="0" />
			<c:set var="qtdTurmasSemestre" value="0" />
			<tr>
				<td colspan="8" class="caixaCinzaMedio"><b>Turmas de ${ linha.nivel == 'G' ? 'Graduação' 
									: linha.nivel == 'T' ? 'Técnico' 
									: linha.nivel == 'B' ? ' Básico'
									: linha.nivel == 'M' ? 'Médio'
									: linha.nivel == 'I' ? 'Infantil' 
									: 'Pós-Graduação' }</b></td>
			</tr>
		</c:if>
		<!-- Linha dividindo a turma de EAD em pólos -->
		<c:if test="${not empty linha.id_polo and linha.id_polo != -1 and _separadorEadAtual != _separadorEad}">
			<c:set var="_separadorEadAtual" value="${_separadorEad}" />
			<tr>
				<td style="text-align: center">
					${linha.ano}.${linha.periodo}
				</td>
				<td>
					${ linha.tipo == 1 or empty linha.tipo ? 'Regular' : (linha.tipo == 2 ? 'Férias' : 'Individual') }
				</td>
				<td>
					${linha.componente_codigo}
				</td>
				<td>
					${linha.componente_nome}
				</td>
				<td style="text-align: right;">
					EAD
				</td>
				<td style="text-align: right;">
					${linha.ch_total}
					<c:set var="stChTotalSemestre" value="${stChTotalSemestre + linha.ch_total}" />
					<c:set var="totalChTotalDocente" value="${totalChTotalDocente + linha.ch_total}" />
				</td>
				<td style="text-align: right;">
					${linha.ch_dedicada_periodo}
					<c:set var="stChDedicadaSemestre" value="${stChDedicadaSemestre + linha.ch_dedicada_periodo}" />
					<c:set var="totalChDedicadaDocente" value="${totalChDedicadaDocente + linha.ch_dedicada_periodo}" />
				</td>
				<td style="text-align: right;">
					- 
				</td>
			</tr>
		</c:if>
		<!-- Dados das turmas -->
		<tr>
			<td style="text-align: center">
				<c:choose>
					<c:when test="${not empty linha.id_polo and linha.id_polo != -1}">
					</c:when>
					<c:otherwise>
						${linha.ano}.${linha.periodo}
					</c:otherwise>
				</c:choose>
			</td>
			<td>
				<c:choose>
					<c:when test="${not empty linha.id_polo and linha.id_polo != -1}">
					</c:when>
					<c:otherwise>
						${ linha.tipo == 1 or empty linha.tipo ? 'Regular' : (linha.tipo == 2 ? 'Férias' : 'Individual') }
					</c:otherwise>
				</c:choose>
			</td>
			<td>
				<c:if test="${empty linha.id_polo or linha.id_polo == -1}">
					${linha.componente_codigo}
				</c:if>
			</td>
			<td>
				<c:if test="${empty linha.id_polo or linha.id_polo == -1}">
					${linha.componente_nome}
				</c:if>
				<c:if test="${not empty linha.id_polo and linha.id_polo != -1}">
					PÓLO ${linha.nome_municipio}
				</c:if>
			</td>
			<td style="text-align: right;">
				<c:if test="${empty linha.id_polo}">
					${linha.codigo_turma}
				</c:if>
				<c:if test="${not empty linha.id_polo and linha.id_polo == -1}">
					EAD
				</c:if>
				<c:set var="qtdTurmasSemestre" value="${qtdTurmasSemestre + 1}" />
				<c:set var="totalTurmasDocente" value="${totalTurmasDocente + 1}" />
			</td>
			<td style="text-align: right;">
				<c:choose>
					<c:when test="${not empty linha.id_polo and linha.id_polo != -1}">
						-
					</c:when>
					<c:otherwise>
						${linha.ch_total}
						<c:set var="stChTotalSemestre" value="${stChTotalSemestre + linha.ch_total}" />
						<c:set var="totalChTotalDocente" value="${totalChTotalDocente + linha.ch_total}" />
					</c:otherwise>
				</c:choose>
			</td>
			<td style="text-align: right;">
				<c:choose>
					<c:when test="${not empty linha.id_polo and linha.id_polo != -1}">
						-
					</c:when>
					<c:otherwise>
						${linha.ch_dedicada_periodo}
						<c:set var="stChDedicadaSemestre" value="${stChDedicadaSemestre + linha.ch_dedicada_periodo}" />
						<c:set var="totalChDedicadaDocente" value="${totalChDedicadaDocente + linha.ch_dedicada_periodo}" />
					</c:otherwise>
				</c:choose>
			</td>
			<td style="text-align: right;">
				<c:choose>
					<c:when test="${not empty linha.id_polo and linha.id_polo == -1}">
						- 
					</c:when>
					<c:otherwise>
						${linha.qtd_alunos}
						<c:set var="stDiscentesSemestre" value="${stDiscentesSemestre + linha.qtd_alunos}" />
						<c:set var="totalDiscentesDocente" value="${totalDiscentesDocente + linha.qtd_alunos}" />
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		<c:set var="_nivelAnterior" value="${linha.nivel}"/>
	</c:forEach>
	<c:if test="${fechaTabela}">
		<c:if test="${qtdTurmasSemestre != 0}">
			<tr class="caixaCinzaMedio" style="text-align: right; font-weight: bold; font-size: 10px">
				<td colspan="4">
					Subtotal ${ _nivelAnterior == 'G' ? 'Graduação' 
							: _nivelAnterior == 'T' ? 'Técnico' 
							: _nivelAnterior == 'B' ? ' Básico'
							: _nivelAnterior == 'M' ? 'Médio'
							: _nivelAnterior == 'I' ? 'Infantil' 
							: 'Pós-Graduação' }
					<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (média semestre)</c:if>: 
				</td>
				<td>${qtdTurmasSemestre}</td>
				<td>
					${stChTotalSemestre} 
					<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${stChTotalSemestre / qtdTurmasSemestre}" />)</c:if>
				</td>
				<td>
					${stChDedicadaSemestre} 
					<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${stChDedicadaSemestre / qtdTurmasSemestre}"/>)</c:if>
				</td>
				<td>
					${stDiscentesSemestre} 
					<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${stDiscentesSemestre / qtdTurmasSemestre}"/>)</c:if>
				</td>
			</tr>
		</c:if>
		<c:if test="${totalTurmasDocente != 0}">
			<tr class="caixaCinzaMedio" style="text-align: right; font-weight: bold; font-size: 10px">
				<td colspan="4">
					Total Geral
					<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (média ano)</c:if>: 
				</td>
				<td>${totalTurmasDocente}</td>
				<td>
					${totalChTotalDocente} 
					<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${totalChTotalDocente / qtdTurmasSemestre}" />)</c:if>
				</td>
				<td>
					${totalChDedicadaDocente} 
					<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${totalChDedicadaDocente / qtdTurmasSemestre}"/>)</c:if>
				</td>
				<td>
					${totalDiscentesDocente} 
					<c:if test="${relatoriosDepartamentoCpdi.periodo == -1}"> (<ufrn:format type="valor1" valor="${totalDiscentesDocente / qtdTurmasSemestre}"/>)</c:if>
				</td>
			</tr>
		</c:if>
		</tbody>
		</table>
		<br />
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
