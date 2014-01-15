<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<a4j:keepAlive beanName="cadastramentoDiscente"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Gerenciar Cadastramento de Discentes &gt; Resumo</h2>

<h:form>

<table class="visualizacao">
	<caption>Resumo do Cadastramento</caption>
	<tr>
		<th>Processo Seletivo Vestibular:</th>
		<td><h:outputText id="psVest" value="#{cadastramentoDiscente.processoSeletivo.nome}"/></td>
	</tr>
	<tr>
		<th>Matriz Curricular:</th>
		<td><h:outputText id="matriz" value="#{cadastramentoDiscente.matriz.descricao}"/></td>
	</tr>
	<tr>
		<td colspan="2">
			<table class="subFormulario" width="100%">
				<caption>Confirma��es (${fn:length(cadastramentoDiscente.cadastrados)})</caption>
				<thead>
					<tr>
						<th style="text-align: center; width: 10%;">Ingresso</th>
						<th style="text-align: center; width: 15%;">Matr�cula</th>
						<th style="text-align: left;">Nome</th>
						<th style="text-align: left; width: 15%;">Novo Status</th>
					</tr>
				</thead>
				<c:choose>
					<c:when test="${not empty cadastramentoDiscente.cadastrados}">
						<c:forEach items="#{cadastramentoDiscente.cadastrados}" var="discente" varStatus="loop">
							<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td style="text-align: center;"><h:outputText value="#{discente.anoPeriodoIngresso}" /></td>
								<td style="text-align: center;"><h:outputText value="#{discente.matricula}" /></td>
								<td><h:outputText value="#{discente.pessoa.nome}"/></td>
								<td><h:outputText value="#{discente.statusString}"/></td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<td colspan="4" style="text-align: center; color: red;">Nenhum discente nesta categoria.</td>
					</c:otherwise>
				</c:choose>
			</table>
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<table class="subFormulario" width="100%">
				<caption>Cancelamentos (${fn:length(cadastramentoDiscente.cancelamentos)})</caption>
				<thead>
					<tr>
						<th style="text-align: center; width: 10%;">Ingresso</th>
						<th style="text-align: center; width: 15%;">Matr�cula</th>
						<th style="text-align: left;">Nome</th>
						<th style="text-align: left; ">Motivo do Cancelamento</th>
						<th style="text-align: left; width: 15%;">Novo Status</th>
					</tr>
				</thead>
				<c:choose>
					<c:when test="${not empty cadastramentoDiscente.cancelamentos}">
						<c:forEach items="#{cadastramentoDiscente.cancelamentos}" var="c" varStatus="loop">
							<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td style="text-align: center;"><h:outputText value="#{c.convocacao.discente.anoPeriodoIngresso}" /></td>
								<td style="text-align: center;"><h:outputText value="#{c.convocacao.discente.matricula}" /></td>
								<td><h:outputText value="#{c.convocacao.discente.pessoa.nome}"/></td>
								<td><h:outputText value="#{c.motivo.descricao}"/></td>
								<td><h:outputText value="#{c.convocacao.discente.statusString}"/></td>
							</tr>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<td colspan="4" style="text-align: center; color: red;">Nenhum discente nesta categoria.</td>
					</c:otherwise>
				</c:choose>
			</table>
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="2" style="text-align: center;">
				<h:commandButton value="Confirmar Cadastramento" action="#{ cadastramentoDiscente.confirmar }" id="btnConfirmar" 
					rendered="#{ not (empty cadastramentoDiscente.cancelamentos && empty cadastramentoDiscente.cadastrados) }"/>
				<h:commandButton value="<< Voltar" action="#{ cadastramentoDiscente.telaFormulario }" id="btnVoltar"/>
			</td>
		</tr>
	</tfoot>
</table>


</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>