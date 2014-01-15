<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<div  style="text-align: center">
		<h2>RELATÓRIO DE OCUPAÇÃO DE VAGAS DE TURMAS</h2>
	</div>
	
	<div id="parametrosRelatorio">
		<table width="100%">
				<tr>
					<th style="width: 20%;">Ano-Período:</th>
					<td>
						<h:outputText value="#{relatorioTurma.ano}" />.<h:outputText value="#{relatorioTurma.periodo}" />
					</td>
				</tr>
		
				<tr>
					<c:if test="${relatorioTurma.portalCoordenadorStricto}">
						<th>Programa:</th>
					</c:if>
					<c:if test="${relatorioTurma.portalCoordenadorGraduacao || acesso.graduacao}">
						<th>Departamento:</th>
					</c:if>
					<c:if test="${relatorioTurma.portalDocente}">
						<th>Unidade:</th>
					</c:if>
					<td>
						<h:outputText value="#{relatorioTurma.departamento.nome}" rendered="#{not empty relatorioTurma.departamento && relatorioTurma.departamento.id > 0}" />
						<h:outputText value="TODOS" rendered="#{empty relatorioTurma.departamento || relatorioTurma.departamento.id == 0}" />
					</td>
				</tr>
	
				<c:if test="${not empty relatorioTurma.curso && relatorioTurma.curso.id > 0}">	
					<tr>
						<th align="left">Curso (vagas reservadas):</th>
						<td>
							<h:outputText value="#{relatorioTurma.curso.descricao}" />
						</td>
					</tr>
				</c:if>
	
				<tr>
					<th>Situação:</th>
					<td>
						<c:if test="${relatorioTurma.situacaoTurma.id == 0 }">
							TODOS
						</c:if>
						<c:if test="${relatorioTurma.situacaoTurma.id > 0 }">
							<h:outputText value="#{relatorioTurma.situacaoTurma.descricao}" />						
						</c:if>
					</td>
				</tr>				
		</table>
	</div>
	<c:set var="fechaTabela" value="${false}" />
	<c:set var="departamento_loop" value=""/>
	<br/>
	<c:forEach items="${relatorioTurma.turmas}" var="turmaLoop">
		<c:set var="departamento_atual" value="${turmaLoop.disciplina.unidade.nome}"/>
		<c:if test="${departamento_loop != departamento_atual}">
			<c:set var="departamento_loop" value="${departamento_atual}"/>
			<c:if test="${fechaTabela}">
				</tbody>
				</table>
				<br/>
			</c:if>
			<c:set var="fechaTabela" value="${true}" />
			<table class="tabelaRelatorioBorda" width="100%">
			<caption>${departamento_atual}</caption>
			<thead>			
				<tr>
					<th style="text-align: center; width: 10%;">Cod. Comp.</th>
					<th style="text-align: left;   width: 35%;">Nome Componente</th>
					<th style="text-align: right;  width:  5%;">Turma</th>
					<th style="text-align: right;  width: 15%;">Horário</th>
					<c:if test="${relatorioTurma.situacaoTurma.id == 0 }">
						<th style="text-align: left;   width: 15%;">Situação</th>
					</c:if>
					<th style="text-align: right;  width:  5%;">Cap</th>
					<th style="text-align: right;  width:  5%;">Mat</th>
					<th style="text-align: right;  width:  5%;">Sol</th>
					<th style="text-align: right;  width:  5%;">Déficit</th>
				</tr>
			</thead>
			<tbody>
		</c:if>
		<tr>
			<td align="center">${turmaLoop.disciplina.codigo}</td>
			<td align="left">${turmaLoop.disciplina.nome}</td>
			<td align="center">${turmaLoop.codigo}</td>
			<td align="left">${turmaLoop.descricaoHorario}</td>
			<c:if test="${relatorioTurma.situacaoTurma.id == 0 }">
				<td align="left">${turmaLoop.situacaoTurma.descricao}</td>
			</c:if>
			<td align="right">${turmaLoop.capacidadeAluno}</td>
			<td align="right">${turmaLoop.qtdMatriculados}</td>
			<td align="right">${turmaLoop.qtdEspera}</td>
			<td align="right">
				<c:set var="deficit" value="${ (1) * (turmaLoop.capacidadeAluno - (turmaLoop.qtdMatriculados + turmaLoop.qtdEspera)) }"/>
				<c:if test="${deficit < 0}">
					<b><font color="red"> ${deficit}</font></b>
				</c:if>
				<c:if test="${deficit >= 0}">
					<font color="blue">${deficit}</font>
				</c:if>
			</td>
		</tr>
	</c:forEach>
		</tbody>
		</table>
	<br/>
	<b>Total de Registros: ${fn:length(relatorioTurma.turmas)}</b>
	<br/>
	<br/>
	<b>LEGENDA:</b> <br/>
	<hr/>
	<div style="text-align: left">
		<b>Cap</b>: Capacidade da Turma <br/>
		<b>Mat</b>: Total de alunos matriculados <br/>
		<b>Sol</b>: Total de solicitações de matrícula pendentes de processamento<br/>
		<b>Déficit</b>: Capacidade - (Mat + Sol)<br/>
	</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
