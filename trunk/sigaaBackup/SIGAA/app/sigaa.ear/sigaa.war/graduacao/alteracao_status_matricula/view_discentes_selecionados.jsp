<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="buscaTurmaBean"/>
<f:view>
	<h2><ufrn:subSistema /> &gt; Alterar Status de Matrícula por Turma</h2>
	
	<h:form id="form">
	
	<c:set value="#{alteracaoStatusMatricula.turma}" var="turma"/>
	<%@include file="/graduacao/alteracao_status_matricula/dados_turma.jsp"%>
	<br/>
	
	<table class="formulario" style="width: 90%">
		<caption>Discentes que terão o Status da Matrícula Alterado</caption>
		<thead>
			<tr>
				<th style="width:15%; text-align: center;">Matrícula</th>
				<th>Discente</th>
				<th>Situação</th>
			</tr>
		</thead>		
		<tbody>
			<c:forEach items="#{alteracaoStatusMatricula.matriculasEscolhidas}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td style="text-align: center;">${item.discente.matricula}</td>
					<td>${item.discente.nome}</td>
					<td>${item.novaSituacaoMatricula.descricao}</td>
				</tr>			
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="4">
					<h:commandButton value="Confirmar" action="#{alteracaoStatusMatricula.efetuarAlteracaoStatusGeral}" id="confirmar"/>
					<h:commandButton value="<< Voltar" action="#{alteracaoStatusMatricula.selecionarAlterarSituacaoMatricula}" id="voltar">
						<f:setPropertyActionListener value="#{turma}" target="#{alteracaoStatusMatricula.turma}"/>			
					</h:commandButton>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{alteracaoStatusMatricula.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>