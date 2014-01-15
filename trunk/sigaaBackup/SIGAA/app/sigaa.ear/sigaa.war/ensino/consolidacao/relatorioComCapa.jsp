<% request.setAttribute("res1024","true"); %>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<%@include file="/WEB-INF/jsp/include/capa.jsp"%>

<style>
	div.opcoes { margin: 4px 0; }
	div.opcoes a { font-size: 0.5em; }
	tr.alunoSelecionado { background: #FF8888; }
</style>

<f:view>
	<c:set var="defaultCancel" value="${ ctx }/ensino/consolidacao/detalhesTurma.jsf?avaliacao=true" scope="session"/>
	<h:outputText value="#{ consolidarTurma.create }"/>
	<h:outputText value="#{ relatorioConsolidacao.create }"/>
	<c:set var="totalNotas" value="0"/>
	<c:set var="somaNotas" value="0"/>	

	<c:set var="bean" value="${ consolidarTurma }"/>
	<c:if test="${ consolidarTurma.turma.id == 0 }">
	<c:set var="bean" value="${ relatorioConsolidacao }"/>
	</c:if>

	<c:if test="${ param['avaliacao'] != null }">
		<h:outputText value="#{ consolidarTurma.atualizarTurma }"/>
	</c:if>

		<div class="notas" style="clear: both;">

			<table class="tabelaRelatorio" width="100%">
			<h3> ${ bean.turma.descricaoSemDocente } - Consolidada </h3>			
			<thead>
				<tr>
					<th></th>
					<th></th>
					
					<th colspan="4" style="border-left: 2px solid #888; border-bottom: 2px solid #888; text-align: center"> Unid 1</th>
					<th colspan="4" style="border-bottom: 2px solid #888; border-right: 2px solid #888; border-left: 2px solid #888; text-align: center"> Unid 2</th>
					<th></th>
					<th></th>
					<th></th>

				</tr>
					<tr>
					<th style="border-bottom: 2px solid #888">Matrícula</th>
					<th style="border-bottom: 2px solid #888">Nome</th>
				
					<c:forEach var="unid" items="${ bean.notas }">
					
						<c:if test="${ bean.ead }">
							<th style="text-align: center; border-left: 2px solid #888; border-bottom: 2px solid #888">Prof.</th>
						</c:if>
						
						<c:if test="${ !bean.ead }">
							<th style="text-align: center; border-bottom: 2px solid #888" colspan="${ unid.numeroAvaliacoes }">Unid.</th>
						</c:if>
						
						<c:if test="${ bean.ead }">
							<th style="border-bottom: 2px solid #888; text-align: center">Prof. ${ bean.pesoProfessor }%</th>
							<th style="border-bottom: 2px solid #888; text-align: center">Tutor </th>
							<th style="border-bottom: 2px solid #888; border-right: 2px solid #888; text-align: center">Tutor ${ bean.pesoTutor }%</th>
						</c:if>
					
					</c:forEach>
					
					<c:if test="${ bean.nota && !bean.lato && (!bean.ead || (bean.ead && bean.duasNotas)) }">
					<th width="5%" style="text-align: center; border-bottom: 2px solid #888">Recuperação</th>
					</c:if>
					<th width="5%" style="text-align: center; border-bottom: 2px solid #888">Resultado</th>
					<c:if test="${ !bean.ead }">
					<th width="5%" style="text-align: center; border-bottom: 2px solid #888">Faltas</th>
					</c:if>
					<th width="5%" style="text-align: center; border-bottom: 2px solid #888">Sit.</th>
				</tr>

				<tr bgcolor="#C4D2EB">
					
					
				</tr>

		</thead>
			<tbody> 
			<c:forEach var="matricula" items="${ bean.turma.matriculasDisciplina }" varStatus="i">
				<c:if test="${ acesso.docente or acesso.ead or obj.mostrarTodasAsNotas or (!obj.mostrarTodasAsNotas and usuario.discente.id == matricula.discente.id ) }">
				<tr class="${ i.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td nowrap="nowrap" style="text-align: center">${ matricula.discente.matricula } </td>
					<td nowrap="nowrap">${ matricula.discente.pessoa.nome }</td>
	
				
				<c:forEach var="unid" items="${ matricula.notas }" varStatus="loop">
					<%--  Avaliações --%>
					<c:if test="${ bean.avaliacao }">
						<c:forEach var="aval" items="${ unid.avaliacoes }">
							<td style="text-align: center;">
							<fmt:formatNumber pattern="#0.0" value="${ aval.nota }"/>
							</td>
						</c:forEach>
					</c:if>
					<td style="text-align: center;">
					<fmt:formatNumber pattern="#0.0" value="${ unid.nota }"/>
					</td>
					
					<c:if test="${ bean.ead }">
					<td style="text-align: center;"><label><fmt:formatNumber pattern="#0.0" value="${ unid.nota * bean.pesoProfessor / 100.0 }"/></label></td>
					<c:if test="${ unid.unidade == 1 }">
					<td style="text-align: center;"><label><fmt:formatNumber pattern="#0.0" value="${ matricula.notaTutor }"/></label></td>
					<td style="text-align: center;"><fmt:formatNumber pattern="#0.0" value="${ matricula.notaTutor * bean.pesoTutor / 100.0 }"/></td>
					</c:if>
					<c:if test="${ unid.unidade == 2 }">
					<td style="text-align: center;"><label><fmt:formatNumber pattern="#0.0" value="${ matricula.notaTutor2 }"/></label></td>
					<td style="text-align: center;"><fmt:formatNumber pattern="#0.0" value="${ matricula.notaTutor2 * bean.pesoTutor / 100.0 }"/></td>
					</c:if>
					</c:if>
					
				</c:forEach>
			
				<c:if test="${ bean.nota && !bean.lato && (!bean.ead || (bean.ead && bean.duasNotas)) }">
				<td style="text-align: center;">
					<fmt:formatNumber pattern="#0.0" value="${ empty matricula.recuperacao ? '' : matricula.recuperacao }"/>
				</td>
				</c:if>
	
				<td style="text-align:center">
					<c:if test="${ bean.nota }">
					<c:set var="somaNotas" value="${ somaNotas + matricula.media }"/>
					<c:set var="totalNotas" value="${ totalNotas + 1 }"/>
					<c:if test="${ matricula.consolidada }">
					<label><fmt:formatNumber pattern="#0.0" value="${ matricula.mediaFinal }"/></label>
					</c:if>
					<c:if test="${ !matricula.consolidada }">
					<label><fmt:formatNumber pattern="#0.0" value="${ matricula.media }"/></label>
					</c:if>
					</c:if>
					<c:if test="${ bean.conceito }">
						${ sf:descricaoConceito(matricula.conceito) }
					</c:if>
					<c:if test="${ bean.competencia }">
						${ matricula.apto ? 'Apto': 'Não Apto'}
					</c:if>
				</td>
				<c:if test="${ !bean.ead }">
				<td style="text-align: center;">
				${ matricula.numeroFaltas }
				</td>
				</c:if>
				<td style="text-align:center"><label>${ matricula.situacaoAbrev }</label></td>
				</tr>
				</c:if>
			</c:forEach>					
			</tbody>
			</table>
		</div>
		<br/>
		<c:if test="${ bean.nota and obj.mostrarMediaDaTurma }">
		<h3 style="text-align: center">Média da Turma: <fmt:formatNumber value="${ somaNotas/ totalNotas }" pattern="#0.0"/></h3>
		</c:if>
		
		<c:if test="${ not empty relatorioConsolidacao.listagemItensProgramaPorDisciplina}">
				<table class="listagem">
				<tr>
					<td>Aula</td>
					<td>Conteúdo</td>
				</tr>
				</table>
				<table class="listagem">
						<c:forEach items="${relatorioConsolidacao.listagemItensProgramaPorDisciplina}" var="itensProgram">
						
							<tr>
								<td>
									${itensProgram.aula}
								</td>
								<td>
									${itensProgram.conteudo}
								</td>
							</tr>
	
						</c:forEach>
				</table>
		</c:if>
		
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
</f:view>