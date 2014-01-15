<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<table class="tabelaRelatorio" width="90%">
		<caption>Dados da Disciplina</caption>
		<tbody>
			<tr>
				<th width="30%">Componente Curricular:</th>
				<td>
					<h:outputText value="#{buscaDisciplinaMedio.obj.disciplina.nome}"/>
				</td>
			</tr>
			<tr>
				<th> Tipo do Componente: </th>
				<td> ${buscaDisciplinaMedio.obj.disciplina.tipoComponente.descricao } </td>
			</tr>
			<tr>
				<th>CH:</th>
				<td>
				<h:outputText value="#{buscaDisciplinaMedio.obj.disciplina.chTotal} h / #{buscaDisciplinaMedio.obj.disciplina.crTotal} crs"/>
				</td>
			</tr>
			<c:if test="${buscaDisciplinaMedio.obj.curso.id > 0}">
				<tr>
					<th>Curso</th>
					<td>${buscaDisciplinaMedio.obj.curso.descricao}</td>
				</tr>
			</c:if>
			<c:if test="${ not empty buscaDisciplinaMedio.obj.turmasSolicitacaoTurmas}">
				<c:forEach var="item" items="${buscaDisciplinaMedio.obj.turmasSolicitacaoTurmas}">
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
				<td>${buscaDisciplinaMedio.obj.docentesNomes }</td>
			</tr>
			<tr>
				<th>Turma:</th>
				<td>
					<h:outputText value="#{ buscaDisciplinaMedio.obj.codigo }"/>
				</td>
			</tr>
			<c:if test="${ !buscaDisciplinaMedio.obj.distancia }">
			<tr>
				<th>Local:</th>
				<td>
					<h:outputText value="#{ buscaDisciplinaMedio.obj.local }"/>
				</td>
			</tr>
			<tr>
				<th>Horário:</th>
				<td>
					<h:outputText value="#{ buscaDisciplinaMedio.obj.descricaoHorario }"/>
				</td>
			</tr>
			</c:if>
			<tr>
				<th>Ano:</th>
				<td>
					<h:outputText value="#{ buscaDisciplinaMedio.obj.ano }"/>
				</td>
			</tr>
			<tr>
				<th>Período de Aulas:</th>
				<td>
				<h:outputText value="#{ buscaDisciplinaMedio.obj.dataInicio}"/> - <h:outputText value="#{ buscaDisciplinaMedio.obj.dataFim}"/>
				</td>
			</tr>
			<tr>
				<th>Modalidade:</th>
				<td>
					<c:choose>
						<c:when test="${ !buscaDisciplinaMedio.obj.distancia }">Presencial</c:when>
						<c:otherwise>A Distância</c:otherwise>
					</c:choose>
				</td>
			</tr>
			<c:if test="${ !buscaDisciplinaMedio.obj.distancia }">
			<tr>
				<th>Capacidade de Alunos:</th>
				<td>
				<h:outputText value="#{ buscaDisciplinaMedio.obj.capacidadeAluno }"/>
				</td>
			</tr>
			</c:if>
			<c:if test="${buscaDisciplinaMedio.obj.totalMatriculados > 0}">
				<tr>
					<th>Total de Matriculados:</th>
					<td>${buscaDisciplinaMedio.obj.totalMatriculados }</td>
				</tr>
				<tr>
					<th>Discentes Matriculados:</th>
					<td>
						<c:forEach var="matricula" items="${buscaDisciplinaMedio.obj.matriculasDisciplina}">
							${matricula.discente.matricula} - ${matricula.discente.nome} (${matricula.situacaoMatricula.descricao})<br/> 
						</c:forEach>
					</td>	
				</tr>
			</c:if>
			<c:if test="${acesso.administradorDAE or acesso.dae}">
			<tr>
				<th>Data de Cadastro:</th>
				<td>
					<fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${buscaDisciplinaMedio.obj.dataCadastro}"/>
				</td>
			</tr>
			<tr>
				<th>Usuário de Cadastro:</th>
				<td>
					${buscaDisciplinaMedio.obj.registroCadastro.usuario.login} - ${buscaDisciplinaMedio.obj.registroCadastro.usuario.pessoa.nome}
				</td>
			</tr>
			</c:if>
		</tbody>
	</table>
</f:view>
<br/>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
