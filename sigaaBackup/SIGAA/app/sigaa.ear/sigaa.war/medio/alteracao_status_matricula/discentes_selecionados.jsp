<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="alteracaoStatusMatriculaMedioMBean"/>
<f:view>
	<h2><ufrn:subSistema /> &gt; Alterar Status de Matrícula por Série</h2>
	
	<h:form id="form">
	
	<table class="visualizacao" style="width: 60%">
		<tr>
			<th>Ano:</th>
			<td>${alteracaoStatusMatriculaMedioMBean.turmaSerie.ano}</td>
		</tr>
		<tr>
			<th>Série:</th>
			<td>${alteracaoStatusMatriculaMedioMBean.turmaSerie.serie.descricaoCompleta}</td>
		</tr>
		<tr>
			<th>Turma:</th>
			<td>${alteracaoStatusMatriculaMedioMBean.turmaSerie.nome}</td>
		</tr>
	</table>
	<br/>
	<br/>
	
	<table class="formulario" style="width: 90%">
		<caption>Discentes que terão o Status da Matrícula Alterado</caption>
		<thead>
			<tr>
				<th style="width:15%; text-align: center;">Matrícula</th>
				<th>Discente</th>
				<th>Situação Atual</th>
				<th>Nova Situação</th>
			</tr>
		</thead>		
		<tbody>
			<c:forEach items="#{alteracaoStatusMatriculaMedioMBean.matriculasEscolhidas}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td style="text-align: center;">${item.discenteMedio.matricula}</td>
					<td>${item.discenteMedio.nome}</td>
					<td>${item.situacaoMatriculaSerie.descricao}</td>
					<td>${item.novaSituacaoMatricula.descricao}</td>
				</tr>			
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4">
					<h:commandButton value="Confirmar" action="#{alteracaoStatusMatriculaMedioMBean.efetuarAlteracaoStatusGeral}" id="confirmar"/>
					<h:commandButton value="<< Voltar" action="#{alteracaoStatusMatriculaMedioMBean.telaSelecaoDiscentes}" id="voltar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{alteracaoStatusMatriculaMedioMBean.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>