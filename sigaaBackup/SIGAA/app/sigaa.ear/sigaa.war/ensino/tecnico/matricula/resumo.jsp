<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema></ufrn:subSistema> &gt; Resumo da Matrícula</h2>
<br/>

<f:view>

<h:outputText value="#{ matriculaTecnico.create }"/>
	<c:set value="#{matriculaTecnico.discente}" var="discente" />
		<%@ include file="/graduacao/info_discente.jsp"%>
<br/>

<%-- Disciplinas da Matrícula --%>
<table class="listagem" width="100%">
<caption>Disciplinas Matriculadas</caption>
<thead>
<tr><th>Código</th><th>Nome</th><th>Turma</th><th>Horário</th></tr>
</thead>
<tbody>
<c:forEach var="item" items="${ matriculaTecnico.turmas }">
<tr><td>${ item.disciplina.codigo }</td><td>${ item.nomeDisciplinaEspecializacao }</td><td>${ item.codigo }</td><td>${ item.descricaoHorario }</td>
</tr>
</c:forEach>
</tbody>
</table>

<h:form>
<h:messages showDetail="true"/>
<br/>
<p align="center">
<h:commandButton value="Gerar Atestado" action="#{ matriculaTecnico.verAtestado  }"/>	
<h:commandButton value="Nova Matrícula" action="#{ matriculaTecnico.novaMatricula }"/>
</p>
<br/>
<ufrn:horarios turmas="${ matriculaTecnico.turmas }"></ufrn:horarios>

</h:form>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>