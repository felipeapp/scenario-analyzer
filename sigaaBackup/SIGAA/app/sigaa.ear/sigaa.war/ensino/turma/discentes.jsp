<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> Lista de Alunos da Turma</h2>

	<table class="visualizacao" >
		<tr>
			<th width="20%"> Componente: </th>
			<td> ${buscaTurmaBean.turmaEscolhida.disciplina.descricao}</td>
		</tr>
		<tr>
			<th>Turma:</th>
			<td>${buscaTurmaBean.turmaEscolhida.codigo}</td> 
		</tr>
		<tr>
			<th> Docente(s): </th>
			<td> ${buscaTurmaBean.turmaEscolhida.docentesNomes} </td>
		</tr>
		<tr>
			<th> Horário: </th>
			<td> ${buscaTurmaBean.turmaEscolhida.descricaoHorario} </td>
		</tr>
	</table>
	
	<br />
	<table class="listagem" id="lista-turmas" style="width: 90%;">
		<caption>${fn:length(buscaTurmaBean.matriculados) } discentes foram matriculados nessa turma</caption>

		<c:if test="${empty buscaTurmaBean.matriculados}">
			<tr><td>Nenhum aluno está matriculado nessa turma</td></tr>
		</c:if>
		<c:if test="${not empty buscaTurmaBean.matriculados}">
			<thead>
				<tr>
					<td width="10%" style="text-align: center;">Matrícula</td>
					<td style="text-align: center;">${ sessionScope.nivel == 'F' ? 'CPF' : ''}</td>
					<td style="text-align: left;">Nome</td>
					<td style="text-align: left;">Curso</td>
					<td  style="text-align: left;" width="13%">Situação</td>
				</tr>
			</thead>
		<c:forEach items="${buscaTurmaBean.matriculados}" var="mat" varStatus="s">
			<tbody>
				<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td style="text-align: center;"> ${mat.discente.matricula } </td>
					<td style="text-align: center;">
						<c:if test="${ sessionScope.nivel == 'F' }">
							<ufrn:format type="cpf" valor="${mat.discente.pessoa.cpf_cnpj}" />
						</c:if> 
					</td>
					<td style="text-align: left;"> ${mat.discente.nome} </td>
					<td  style="text-align: left;">
					<c:choose>
						<c:when test="${ mat.discente.tecnico }">
							${mat.discente.curso.nome}
							<c:if test="${ not empty mat.discente.turmaEntradaTecnico.especializacao.descricao }">
							- ${mat.discente.turmaEntradaTecnico.especializacao.descricao }
							</c:if> 
						</c:when>
						<c:when test="${ mat.discente.formacaoComplementar }">
							${mat.discente.curso.nome}
						</c:when>
						<c:when test="${ mat.discente.stricto }">
							${ mat.discente.nivelDesc }
						</c:when>
						<c:when test="${ mat.discente.lato }">
							${ mat.discente.curso.nome }
						</c:when>
						<c:otherwise>
						${mat.discente.curriculo.matriz.descricao }
						</c:otherwise>
					</c:choose>
					</td>
					<td style="text-align: left;"> ${mat.situacaoMatricula.descricao} </td>
				</tr>
			</tbody>
		</c:forEach>
		</c:if>
	</table>
	
	<br>
	<table class="listagem" id="lista-turmas" style="width: 90%;">
		<caption>${fn:length(buscaTurmaBean.solicitantes) } discentes solicitaram matrícula nessa turma</caption>

		<c:if test="${empty buscaTurmaBean.solicitantes}">
			<tr><td>Nenhum aluno solicitou matrícula para esta turma</td></tr>
		</c:if>
		
		<c:if test="${not empty buscaTurmaBean.solicitantes}">
			<thead>
				<tr>
					<td width="10%">Matrícula</td>
					<td>Nome</td>
					<td></td>
					<td>Rematrícula</td>
					<td width="13%">Status da Orientação</td>
				</tr>
			</thead>
			
		<tbody>
		<c:set var="resultado" />
		<c:forEach items="${gruposSolicitacoes}" var="grupo" varStatus="s">
			<tr>
				<td colspan="5" class="subFormulario"> ${grupo.key} (${fn:length(grupo.value)})</td>
			</tr>
			<c:forEach items="${grupo.value}" var="sol" varStatus="s">
				<tr class="${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
					<td> ${sol.discente.matricula } </td>
					<td> ${sol.discente.nome} </td>
					<td>
						<c:if test="${sol.discente.graduacao}">
							${sol.discente.curriculo.matriz.descricao}
						</c:if>
					</td>
					<td style="text-align:center;	">
						<c:if test="${sol.rematricula}">
							SIM
						</c:if>
						<c:if test="${not sol.rematricula}">
							NÃO
						</c:if>
					</td>
					<td> ${sol.statusDescricao} </td>
				</tr>
			</c:forEach>
		</c:forEach>
		</tbody>
		</c:if>
	</table>
	<br>
	<center>
		<h:form>
			<input type="button" value="<< Selecionar Outra Turma"  onclick="javascript: history.back();" id="voltar" />
		</h:form>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
