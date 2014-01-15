<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
<a4j:keepAlive beanName="planoDocenciaAssistidaMBean" />

<c:if test="${not planoDocenciaAssistidaMBean.relatorioSemestral}">
	<h2>Plano de Docência Assistida</h2>
</c:if>
<c:if test="${planoDocenciaAssistidaMBean.relatorioSemestral}">
	<h2>Relatório Semestral </h2>
</c:if>	

<table class="listagem, tabelaRelatorio" width="100%">
	<tbody>
	
	<tr class="subFormulario">
		<td colspan="2" class="subFormulario">
			<h3 class="tituloTabelaRelatorio">Dados do Aluno de Pós-Graduação</h3>
		</td>
	</tr>
	
	<tr>
		<th style="width: 30%">
			Nome:
		</th>
		<td>
		    ${planoDocenciaAssistidaMBean.obj.discente.matricula} - ${planoDocenciaAssistidaMBean.obj.discente.pessoa.nome}
		</td>		
	</tr>
	<tr>
		<th>
			Programa:
		</th>
		<td>
		    ${planoDocenciaAssistidaMBean.obj.discente.unidade.nome}
		</td>		
	</tr>	
	<tr>
		<th>
			Orientador:
		</th>
		<td>
			<c:if test="${planoDocenciaAssistidaMBean.orientacao != null}">
		    	${planoDocenciaAssistidaMBean.orientacao.descricaoOrientador}
		    </c:if>
		    <c:if test="${planoDocenciaAssistidaMBean.orientacao.descricaoOrientador == null}">
		    	<span style="color: red;">Orientador não informado.</span>
		    </c:if>
		</td>		
	</tr>		
	<tr>
		<th>
			Nível:
		</th>
		<td>
		    ${planoDocenciaAssistidaMBean.obj.discente.nivelDesc}
		</td>		
	</tr>	
	<c:if test="${not empty planoDocenciaAssistidaMBean.obj.periodoIndicacaoBolsa}">
		<tr>
			<th>
				Período da Indicação:
			</th>
			<td>
			    ${planoDocenciaAssistidaMBean.obj.periodoIndicacaoBolsa.anoPeriodoFormatado}
			</td>		
		</tr>			
	</c:if>	
	<c:if test="${empty planoDocenciaAssistidaMBean.obj.periodoIndicacaoBolsa}">
		<tr>
			<th>
				Ano/Período de Referência:
			</th>
			<td>
			    ${planoDocenciaAssistidaMBean.obj.ano}.${planoDocenciaAssistidaMBean.obj.periodo}
			</td>		
		</tr>			
	</c:if>		
	<tr>
		<th>
			Situação do Plano:
		</th>
		<c:choose>
			<c:when test="${not planoDocenciaAssistidaMBean.obj.ativo}">
				<td style="color:red; font-weight: bold;">INATIVO</td>
			</c:when>		
			<c:when test="${planoDocenciaAssistidaMBean.obj.reprovado or planoDocenciaAssistidaMBean.obj.solicitadoAlteracao}">
				<td style="color:red;">
					<b>${planoDocenciaAssistidaMBean.obj.descricaoStatus}</b>
					<c:if test="${not empty planoDocenciaAssistidaMBean.obj.observacao}">
						 <br/>Motivo: ${planoDocenciaAssistidaMBean.obj.observacao}
					</c:if>				
				</td>
			</c:when>
			<c:when test="${planoDocenciaAssistidaMBean.obj.aprovado}">
				<td style="color:green; font-weight: bold;">${planoDocenciaAssistidaMBean.obj.descricaoStatus}</td>
			</c:when>
			<c:when test="${planoDocenciaAssistidaMBean.obj.concluido}">
				<td style="color:#4169E1; font-weight: bold;">${planoDocenciaAssistidaMBean.obj.descricaoStatus}</td>
			</c:when>			
			<c:otherwise>
				<td>${planoDocenciaAssistidaMBean.obj.descricaoStatus}</td>
			</c:otherwise>						
		</c:choose>				
	</tr>				
	<tr>
		<th>
			Modalidade de Bolsa:
		</th>	
		<td>
			<c:choose>
				<c:when test="${planoDocenciaAssistidaMBean.obj.bolsista}">

					<c:choose>
						<c:when test="${planoDocenciaAssistidaMBean.obj.modalidadeBolsa != null && planoDocenciaAssistidaMBean.obj.modalidadeBolsa.id > 0}">
							${planoDocenciaAssistidaMBean.obj.modalidadeBolsa.descricao}
						</c:when>
						<c:otherwise>
							${planoDocenciaAssistidaMBean.obj.outraModalidade}
							<c:if test="${empty planoDocenciaAssistidaMBean.obj.outraModalidade}">
								<span style="color: red;">Modalidade de Bolsa não informada.</span>
							</c:if>
						</c:otherwise>	
					</c:choose>
						
				</c:when>
				<c:otherwise>
					Não possui bolsa.
				</c:otherwise>	
			</c:choose>
		</td>		
	</tr>	
	
	<tr class="subFormulario">
		<td colspan="2" class="subFormulario">
			<h3 class="tituloTabelaRelatorio">Dados do Componente Curricular</h3>
		</td>
	</tr>		
	<tr>
		<th>Curso: </th>
		<td>
			<c:if test="${not empty planoDocenciaAssistidaMBean.obj.curso.descricao}">
				${planoDocenciaAssistidaMBean.obj.curso.descricao}
			</c:if>
			<c:if test="${empty planoDocenciaAssistidaMBean.obj.curso.descricao}">
				<span style="color: red;">Curso não informado.</span>
			</c:if>
		</td>
	</tr>		
	<tr>
		<th>Componente Curricular: </th>
		<td>
			<c:if test="${not empty planoDocenciaAssistidaMBean.obj.componenteCurricular.nome}">
				${planoDocenciaAssistidaMBean.obj.componenteCurricular.nome}
			</c:if>
			<c:if test="${empty planoDocenciaAssistidaMBean.obj.componenteCurricular.nome}">
				<span style="color: red;">Componente Curricular não informado.</span>
			</c:if>
		</td>
	</tr>	
	<tr>
		<th>Departamento: </th>
		<td colspan="2">
			<c:if test="${not empty planoDocenciaAssistidaMBean.obj.componenteCurricular.unidade.nome}">
				<h:outputText value="#{planoDocenciaAssistidaMBean.obj.componenteCurricular.unidade.nome}"/>					
			</c:if>
			<c:if test="${empty planoDocenciaAssistidaMBean.obj.componenteCurricular.unidade.nome}">
				<span style="color: red;">Departamento não encontrado.</span>
			</c:if>		
		</td>
	</tr>			
	<c:if test="${planoDocenciaAssistidaMBean.obj.componenteCurricular.atividade}">
		<tr>
			<th>Docente:</th>
			<td>
				<c:if test="${not empty planoDocenciaAssistidaMBean.obj.servidor.pessoa.nome}">
					${planoDocenciaAssistidaMBean.obj.servidor.pessoa.nome}
				</c:if>
				<c:if test="${empty planoDocenciaAssistidaMBean.obj.servidor.pessoa.nome}">
					<span style="color: red;">Curso não informado.</span>				
				</c:if>					
			</td>
		</tr>				
	</c:if>
	<tr>
		<td colspan="2">
			<c:if test="${not empty planoDocenciaAssistidaMBean.obj.turmaDocenciaAssistida}">
				<table class="listagem" width="100%">
					<thead>
						<tr>
							<td>Turma(s)</td>
							<td>Docente(s)</td>
							<td style="text-align: center;">Início</td>
							<td style="text-align: center;">Fim</td>
						</tr>
					</thead>
					<c:forEach items="#{planoDocenciaAssistidaMBean.obj.turmaDocenciaAssistida}" var="item" varStatus="loop">
						<tr class="${loop.index % 2 == 0? 'linhaPar': 'linhaImpar' }">
							<td>
								<h:outputText value="#{item.turma.codigo} - #{item.turma.disciplina.nome} (#{item.turma.anoPeriodo})"/>												
							</td>
							<td>${item.turma.docentesNomes}</td>
							<td style="text-align: center;">
								<h:outputText value="#{item.dataInicio}">
									<f:convertDateTime pattern="dd/MM/yyyy"  />
								</h:outputText> 						
							</td>
							<td style="text-align: center;">
								<h:outputText value="#{item.dataFim}">
									<f:convertDateTime pattern="dd/MM/yyyy"  />
								</h:outputText> 						
							</td>				
						</tr>			
						<c:if test="${planoDocenciaAssistidaMBean.relatorioSemestral}">
							<tr>
								<td colspan="4">
									<table class="listagem" style="width: 75%" align="center">
										<tr>
											<th style="text-align: right; width: 80px;">Matriculados</th>
											<th style="text-align: right; width: 80px;">Aprovados</th>
											<th style="text-align: right; width: 80px;">Reprovados por Nota</th>
											<th style="text-align: right; width: 80px;">Reprovados por Falta</th>
											<th style="text-align: right; width: 80px;">Trancamentos</th>
										</tr>
										<tr>
											<td style="text-align: right;">${item.turma.qtdMatriculados}</td>
											<td style="text-align: right;">${item.turma.qtdAprovados}</td>
											<td style="text-align: right;">${item.turma.qtdReprovados}</td>
											<td style="text-align: right;">${item.turma.qtdReprovadosFalta}</td>
											<td style="text-align: right;">${item.turma.qtdTrancados}</td>
										</tr>
									</table>
								</td>
							</tr>
						</c:if>					
					</c:forEach>
				</table>	
			</c:if>
			<c:if test="${empty planoDocenciaAssistidaMBean.obj.turmaDocenciaAssistida && !planoDocenciaAssistidaMBean.obj.componenteCurricular.atividade}">
				<span style="color: red;">Nenhuma turma informada.</span>
			</c:if>
		</td>
	</tr>
	<c:if test="${not empty planoDocenciaAssistidaMBean.obj.parecerDocenciaAssistida}">
		<tr class="subFormulario">
			<td colspan="2" class="subFormulario">
				<h3 class="tituloTabelaRelatorio">Parecer dos Docentes:</h3>
			</td>
		</tr>	
		
		<tr>
			<td colspan="2">		
				<table class="listagem">
					<thead>
						<tr>
							<td width="80px;">Docente</td>
							<td width="30px;" style="text-align: center;">Data</td>
							<td>Parecer</td>
						</tr>
					</thead>
					<c:forEach items="#{planoDocenciaAssistidaMBean.obj.parecerDocenciaAssistida}" var="item" varStatus="loop">
						<tr class="${loop.index % 2 == 0? 'linhaPar': 'linhaImpar' }">
							<td>
								${item.registroCadastro.usuario.nome}											
							</td>
							<td style="text-align: center;">
								<h:outputText value="#{item.data}">
									<f:convertDateTime pattern="dd/MM/yyyy hh:mm"  />
								</h:outputText> 						
							</td>								
							<td>${item.observacao}</td>			
						</tr>													
					</c:forEach>
				</table>		
			</td>
		</tr>
	</c:if>	
	<c:if test="${not planoDocenciaAssistidaMBean.relatorioSemestral}">
		<tr class="subFormulario">
			<td colspan="2" class="subFormulario">
				<h3 class="tituloTabelaRelatorio">Justificativa para escolha do componente curricular:</h3>
			</td>
		</tr>	
	
		<tr>
			<td colspan="2" align="justify"> 
				<c:if test="${not empty planoDocenciaAssistidaMBean.obj.justificativa}">
					<p> ${ planoDocenciaAssistidaMBean.obj.justificativa } </p>			
				</c:if>
				<c:if test="${empty planoDocenciaAssistidaMBean.obj.justificativa}">
					<span style="color: red;">Justificativa não informada.</span>
				</c:if>			
			</td>
		</tr>
		
		<tr class="subFormulario">
			<td colspan="2" class="subFormulario">
				<h3 class="tituloTabelaRelatorio">Objetivos:</h3>
			</td>
		</tr>		
		
		<tr>
			<td colspan="2" align="justify">
				<c:if test="${not empty planoDocenciaAssistidaMBean.obj.objetivos}">
					<p> ${ planoDocenciaAssistidaMBean.obj.objetivos } </p>			
				</c:if>
				<c:if test="${empty planoDocenciaAssistidaMBean.obj.objetivos}">
					<span style="color: red;">Objetivos não informado.</span>
				</c:if>
			</td>
		</tr>
		
		<tr class="subFormulario">
			<td colspan="2" class="subFormulario">
				<h3 class="tituloTabelaRelatorio">Atividade(s):</h3>
			</td>
		</tr>	
		
		<tr>
			<td colspan="2">		
				<c:if test="${not empty planoDocenciaAssistidaMBean.obj.atividadeDocenciaAssistida}">
					<table class="listagem">
						<thead>
							<tr>
								<td>Atividade</td>
								<td style="text-align: center;">Data de Início</td>
								<td style="text-align: center;">Data de Fim</td>
								<td style="text-align: center;">Carga Horária</td>
								<td>Frequência</td>
								<td></td>
							</tr>
						</thead>
						<c:forEach items="#{planoDocenciaAssistidaMBean.obj.atividadeDocenciaAssistida}" var="item" varStatus="loop">
							<tr class="${loop.index % 2 == 0? 'linhaPar': 'linhaImpar' }">
								<td>
									<c:if test="${not empty item.outraAtividade}">
										<b>${item.outraAtividade}</b>							
									</c:if>
									<c:if test="${empty item.outraAtividade}">
										<b>${item.formaAtuacao.descricao}</b>							
									</c:if>													
								</td>
								<td style="text-align: center;">
									<h:outputText value="#{item.dataInicio}">
										<f:convertDateTime pattern="dd/MM/yyyy"  />
									</h:outputText> 						
								</td>
								<td style="text-align: center;">
									<h:outputText value="#{item.dataFim}">
										<f:convertDateTime pattern="dd/MM/yyyy"  />
									</h:outputText> 						
								</td>
								<td style="text-align: center;">${item.ch}</td>
								<td>${item.frequenciaAtividade.descricao}</td>			
								<td style="text-align: left;">
									<c:if test="${!item.prevista}"><b><i>Não Prevista</i></b></c:if>
								</td>												
							</tr>	
							<tr>
								<td colspan="5" style="text-indent: 15px;"><i>Metodologias:</i><br/>
									<p style="text-align: justify;">${item.comoOrganizar}</p>
								</td>
							</tr>
							<tr>
								<td colspan="5" style="text-indent: 15px;"><i>Como Avaliar:</i><br/>
									<p style="text-align: justify;">${item.procedimentos}</p>
								</td>
							</tr>													
						</c:forEach>
					</table>		
				</c:if>
				<c:if test="${empty planoDocenciaAssistidaMBean.obj.atividadeDocenciaAssistida}">
					<span style="color: red;">Nenhuma atividade cadastrada.</span>
				</c:if>
			</td>
		</tr>	
	</c:if>
	<c:if test="${planoDocenciaAssistidaMBean.relatorioSemestral}">
		<tr>
			<td colspan="2" class="subFormulario"> Atividades Realizadas:</td>
		</tr>
		<tr>
			<td colspan="2">	
				<table class="listagem">
					<thead>
						<tr>
							<td>Atividades Propostas no Plano de Atuação</td>
							<td></td>
							<td style="text-align: right;">Realização (%)</td>
						</tr>
					</thead>			
					<c:forEach items="#{planoDocenciaAssistidaMBean.obj.atividadeDocenciaAssistida}" var="item" varStatus="loop">
						<tr class="${loop.index % 2 == 0? 'linhaPar': 'linhaImpar' }">
							<td>
								<c:if test="${not empty item.outraAtividade}">
									<b>${item.outraAtividade}</b>							
								</c:if>
								<c:if test="${empty item.outraAtividade}">
									<b>${item.formaAtuacao.descricao}</b>							
								</c:if>													
							</td>
							<td style="text-align: left;">
								<c:if test="${!item.prevista}"><b><i>Não Prevista</i></b></c:if>
							</td>							
							<td style="text-align: right;">
								${item.percentualRealizado}
							</td>
						</tr>
						<tr>
							<td style="text-align: left;" colspan="2">
								&emsp;&emsp;<i>Resultados Obtidos:</i><br/>
								<p style="text-align: justify; margin: 10px;">&emsp;&emsp;<h:outputText value="#{ item.resultadosObtidos }"/></p>
							</td>
						</tr>
						<tr>
							<td style="text-align: left;" colspan="2">
								&emsp;&emsp;<i>Dificuldades Encontradas:</i><br/>
								<p style="text-align: justify; margin: 10px;">&emsp;&emsp;<h:outputText value="#{ item.dificuldades }"/></p>
							</td>
						</tr>						
					</c:forEach>
				</table>		
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario"> Análise da Contribuição para Formação Docente:</td>
		</tr>
		<tr>
			<td colspan="2">
				<p style="text-align: justify; margin: 10px;">&emsp;&emsp;<h:outputText value="#{ planoDocenciaAssistidaMBean.obj.analise }"/></p>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario"> Sugestões:</td>
		</tr>
		<tr>
			<td colspan="2">
				<p style="text-align: justify; margin: 10px;">&emsp;&emsp;<h:outputText value="#{ planoDocenciaAssistidaMBean.obj.sugestoes }"/></p>
			</td>
		</tr>		
	</c:if>
	</tbody>
</table>	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>