<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<table class="tabelaRelatorio" width="90%">
		<caption>Dados da Turma</caption>
		<tbody>
			<tr>
				<th width="30%">Componente Curricular:</th>
				<td>
					<h:outputText value="#{buscaTurmaBean.obj.disciplina.codigo} - #{buscaTurmaBean.obj.disciplina.nome}"/>
				</td>
			</tr>
			<tr>
				<th> Tipo do Componente: </th>
				<td> ${buscaTurmaBean.obj.disciplina.tipoComponente.descricao } </td>
			</tr>
			<tr>
				<th>CH / Créditos:</th>
				<td>
				<h:outputText value="#{buscaTurmaBean.obj.disciplina.chTotal} h / #{buscaTurmaBean.obj.disciplina.crTotal} crs"/>
				</td>
			</tr>
			<c:if test="${buscaTurmaBean.obj.curso.id > 0}">
				<tr>
					<th>Curso</th>
					<td>${buscaTurmaBean.obj.curso.descricao}</td>
				</tr>
			</c:if>
			<c:if test="${ not empty buscaTurmaBean.obj.turmasSolicitacaoTurmas}">
				<c:forEach var="item" items="${buscaTurmaBean.obj.turmasSolicitacaoTurmas}">
					<tr>
						<th> Tipo da Turma: </th>
						<td> ${item.turma.tipoString} </td>
					</tr>
					<c:if test="${item.turma.turmaFerias}">
						<tr>
							<th>Motivo da Solicitação:</th>
							<td>
								<h:outputText id="motivo" value="#{ item.solicitacao.motivo }"/>
							</td>
						</tr>
					</c:if>
				</c:forEach>
			</c:if>
			<tr>
				<th>Docente(s):</th>
				<td>${buscaTurmaBean.obj.docentesNomes }</td>
			</tr>
			<tr>
				<th>Código da Turma:</th>
				<td>
					<h:outputText value="#{ buscaTurmaBean.obj.codigo }"/>
				</td>
			</tr>
			<c:if test="${ !buscaTurmaBean.obj.distancia }">
			<tr>
				<th>Local:</th>
				<td>
					<h:outputText value="#{ buscaTurmaBean.obj.local }"/>
				</td>
			</tr>
			<tr>
				<th>Horário:</th>
				<td>
					<h:outputText value="#{ buscaTurmaBean.obj.descricaoHorario }"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<th>Ano-Período:</th>
				<td>
					<h:outputText value="#{ buscaTurmaBean.obj.anoPeriodo }"/>
				</td>
			</tr>
			<tr>
				<th>Período de Aulas:</th>
				<td>
				<h:outputText value="#{ buscaTurmaBean.obj.dataInicio}"/> - <h:outputText value="#{ buscaTurmaBean.obj.dataFim}"/>
				</td>
			</tr>
			<tr>
				<th>Modalidade:</th>
				<td>
					<c:choose>
						<c:when test="${ !buscaTurmaBean.obj.distancia }">Presencial</c:when>
						<c:otherwise>A Distância</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<c:if test="${ !buscaTurmaBean.obj.distancia }">
			<tr>
				<th>Capacidade de Alunos:</th>
				<td>
				<h:outputText value="#{ buscaTurmaBean.obj.capacidadeAluno }"/>
				</td>
			</tr>
			</c:if>
			<c:if test="${buscaTurmaBean.obj.totalMatriculados > 0}">
				<tr>
					<th>Total de Matriculados:</th>
					<td>${buscaTurmaBean.obj.totalMatriculados }</td>
				</tr>
				<tr>
					<th>Discentes Matriculados:</th>
					<td>
						<c:forEach var="matricula" items="${buscaTurmaBean.obj.matriculasDisciplina}">
							${matricula.discente.matricula} - $(matricula.discente.nome} (${matricula.situacaoMatricula.descricao})<br/> 
						</c:forEach>
					</td>	
				</tr>
			</c:if>
			<c:if test="${acesso.administradorDAE or acesso.dae}">
			<tr>
				<th>Data de Cadastro:</th>
				<td>
					<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${buscaTurmaBean.obj.dataCadastro}"/>
				</td>
			</tr>
			<tr>
				<th>Usuário de Cadastro:</th>
				<td>
					${buscaTurmaBean.obj.registroCadastro.usuario.login} - ${buscaTurmaBean.obj.registroCadastro.usuario.pessoa.nome}
				</td>
			</tr>
			</c:if>
		</tbody>
	</table>
</f:view>
<br/>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
