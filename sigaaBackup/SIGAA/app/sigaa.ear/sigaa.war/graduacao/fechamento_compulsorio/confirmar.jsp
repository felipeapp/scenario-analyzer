<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2><ufrn:subSistema /> > Fechamento Compulsório de Atividades</h2>

<f:view>
<a4j:keepAlive beanName="fechamentoCompulsorioAtividades" />
<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

	<div class="descricaoOperacao">
			<b> Caro(a) usuário, </b>
			<br/><br/>
			Nesta tela é possível cancelar a matrícula de todos os discentes listados.
			<br/><br/>
	</div>

	<h:form id="form">

		<c:if test="${not empty fechamentoCompulsorioAtividades.matriculas}">
			<table class="listagem" style="width:100%">
			<caption class="listagem">Lista de Matrículas para Fechar (${fn:length(fechamentoCompulsorioAtividades.matriculasEscolhidas)})</caption>
			
			<thead>
			<tr>
				<th width="10%">Matrícula</th>
				<th>Discente</th>
				<th style="text-align:center;width:10%;">Status do Discente</th>
				<th>Atividade</th>
				<th width="7%">Período</th>
			</tr>
			</thead>
					<c:set var="idCurso" value="0" />		
					<c:forEach items="#{fechamentoCompulsorioAtividades.matriculasEscolhidas}" var="m" varStatus="status">
	
						<c:if test="${idCurso != m.discente.curso.id}">
						<tr><td colspan="5" style="background-color: #C8D5EC;font-weight:bold;"><h:outputText value="#{m.discente.curso.nome} - #{m.discente.curso.municipio.nome}"/></td></tr>
						</c:if>
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td><h:outputText value="#{m.discente.matricula}"/></td>
							<td><h:outputText value="#{m.discente.nome}"/></td>
							<td align="center"><h:outputText value="#{m.discente.statusString}"/></td>
							<td><h:outputText value="#{m.componente.nome}"/></td>	
							<td><h:outputText value="#{m.anoPeriodo}"/></td>						
						</tr>
						<c:set var="idCurso" value="${m.discente.curso.id}" />	
					</c:forEach>
					<tr>
						<c:set var="exibirApenasSenha" value="true" scope="request"/>
						<td colspan="5" align="center">
							<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
						</td>
					</tr>
					<tfoot>
						<tr>
							<td colspan="5" align="center">
								<h:commandButton action="#{fechamentoCompulsorioAtividades.confirmar}" onclick="return(confirm('Deseja realmente fechar as matrículas destes discentes?'));" value="Confirmar"/>
								<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{fechamentoCompulsorioAtividades.cancelar}" /> 
							</td>
						</tr>
					</tfoot>	
			</table>
			
		</c:if>
		<c:if test="${empty fechamentoCompulsorioAtividades.matriculas}">
			<div style="text-align: center;font-weight:bold;color:red;margin:40px"> 
			Nenhum discente encontrado.
			</div>
		</c:if>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
