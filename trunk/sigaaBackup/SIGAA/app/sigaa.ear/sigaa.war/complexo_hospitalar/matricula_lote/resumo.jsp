<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema></ufrn:subSistema> &gt; Matricular Alunos em Lote</h2>
<br/>

<f:view>
	<%-- Disciplinas da Matrícula --%>
	<table class="listagem" width="100%">
		<caption>Disciplina</caption>
			<thead>
				<tr>
					<th> Turma </th>
					<th> Docente(s) </th>
					<th> Tipo </th>
					<th> Horário </th>
					<th> Local </th>
					<th> Capacidade </th>
				</tr>
			</thead>
		<tbody>
			<tr>
				<td width="8%">${matriculaResidenciaMedica.turmaEscolhida.codigo}</td>
				<td>${matriculaResidenciaMedica.turmaEscolhida.docentesNomes}</td>
				<td width="10%">${matriculaResidenciaMedica.turmaEscolhida.tipoString}</td>
				<td width="10%">${matriculaResidenciaMedica.turmaEscolhida.descricaoHorario}</td>
				<td width="10%">${matriculaResidenciaMedica.turmaEscolhida.local}</td>
				<td width="10%">${matriculaResidenciaMedica.turmaEscolhida.capacidadeAluno} alunos</td>
			</tr>
		</tbody>
	</table>
	
	<br/>
	
	<%-- Alunos que estão sendo matriculados --%>
	<table class="listagem" width="100%">
		<caption>Alunos</caption>
			<thead>
				<tr>	
					<th style="text-align: center;">Matrícula</th>
					<th>Nome</th>
					<th>Curso</th>
				</tr>
			</thead>
		<tbody>
			<c:forEach var="item" items="${ matriculaResidenciaMedica.discentes }">
				<tr>
					<td style="text-align: center;">${ item.matricula }</td>
					<td>${ item.pessoa.nome }</td>
					<td>${ item.curso.descricao }</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<h:form>
		<br/>
			<p align="center">
				<h:commandButton value="Confirmar Matrículas" action="#{ matriculaResidenciaMedica.confirmarMatriculas }"/>
				<h:commandButton value="<< Selecionar Componente" action="#{ matriculaResidenciaMedica.forwardSelecionarComponente }" />
				<h:commandButton value="<< Selecionar Discentes" action="#{matriculaResidenciaMedica.voltarSelecaoDiscentes }" />  
				<h:commandButton value="Cancelar" action="#{ matriculaResidenciaMedica.cancelar }" onclick="#{confirm}"/>
			</p>
		<br/>
		<ufrn:horarios turmas="${ matriculaTecnico.turmas }"></ufrn:horarios>
	</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>