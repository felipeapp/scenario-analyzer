<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="matriculaDiscenteSerieMBean"/>
<f:view>
	<h2><ufrn:subSistema /> &gt; Sinalizar Alunos para Dependência</h2>
	
	<h:form id="form">
	
	<table class="visualizacao" style="width: 60%">
		<tr>
			<th>Curso:</th>
			<td>${matriculaDiscenteSerieMBean.serie.cursoMedio.nomeCompleto}</td>
		</tr>
		<tr>
			<th>Série:</th>
			<td>${matriculaDiscenteSerieMBean.serie.descricaoCompleta}</td>
		</tr>
		<tr>
			<th>Ano:</th>
			<td>${matriculaDiscenteSerieMBean.ano}</td>
		</tr>
		
	</table>
	<br/>
	<br/>
	
	<table class="formulario" style="width: 90%">
		<caption>Discentes que terão o Status da Matrícula Alterado</caption>
		<thead>
			<tr>
				<th>Turma</th>
				<th style="width:15%; text-align: center;">Matrícula</th>
				<th>Discente</th>
				<th>Situação Atual</th>
				<th>Nova Situação</th>
			</tr>
		</thead>		
		<tbody>
			<c:forEach items="#{matriculaDiscenteSerieMBean.matriculasEscolhidas}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td>${item.turmaSerie.nome} </td>
					<td style="text-align: center;">${item.discenteMedio.matricula}</td>
					<td>${item.discenteMedio.nome}</td>
					<td>${item.situacaoMatriculaSerie.descricao}</td>
					<td>${item.novaSituacaoMatricula.descricao}</td>
				</tr>			
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="5">
					<h:commandButton value="Confirmar" action="#{matriculaDiscenteSerieMBean.efetuarAlteracaoStatusGeral}" id="confirmar"/>
					<h:commandButton value="<< Voltar" action="#{matriculaDiscenteSerieMBean.telaSelecaoDiscentes}" id="voltar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{matriculaDiscenteSerieMBean.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>