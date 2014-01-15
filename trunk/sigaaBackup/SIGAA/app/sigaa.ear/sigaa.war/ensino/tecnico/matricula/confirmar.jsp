<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema></ufrn:subSistema> &gt; Matricular</h2>
<br/>

<f:view>

<h:outputText value="#{ matriculaTecnico.create }"/>

<%-- Alunos que estão sendo matriculados --%>
<table class="listagem" width="100%">
	<caption>Alunos</caption>
		<thead>
			<tr>	
				<th style="text-align: center;">Matrícula</th>
				<th>Nome</th>
			</tr>
		</thead>
	<tbody>
		<c:forEach var="item" items="${ matriculaTecnico.discentes }">
			<tr>
				<td style="text-align: center;">${ item.matricula }</td>
				<td>${ item.pessoa.nome }</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<br/>

<%-- Disciplinas da Matrícula --%>
<table class="listagem" width="100%">
	<caption>Disciplinas</caption>
		<thead>
			<tr>
				<th style="text-align: center;">Código</th>
				<th>Nome</th>
				<th style="text-align: right;">Turma</th>
				<th>Horário</th>
			</tr>
		</thead>
	<tbody>
		<c:forEach var="item" items="${ matriculaTecnico.turmas }">
			<tr>
				<td style="text-align: center;">${ item.disciplina.codigo }</td>
				<td>${ item.nomeDisciplinaEspecializacao }</td>
				<td style="text-align: right;">${ item.codigo }</td>
				<td>${ item.descricaoHorario }</td>
			</tr>
		</c:forEach>
	</tbody>
</table>

<h:form>
<h:messages showDetail="true"/>
<br/>
	<p align="center">
		<h:commandButton value="Confirmar" action="#{ matriculaTecnico.confirmar }"/>
		<h:commandButton value="<< Voltar" action="#{matriculaTecnico.escolheTipo}" />  
		<h:commandButton value="Cancelar" action="#{ matriculaTecnico.cancelar }" onclick="#{confirm}"/>
	</p>
<br/>
<ufrn:horarios turmas="${ matriculaTecnico.turmas }"></ufrn:horarios>
</h:form>
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>