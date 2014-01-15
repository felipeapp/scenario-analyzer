<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="cadastramentoDiscenteTecnico"></a4j:keepAlive>
<h2><ufrn:subSistema /> &gt; Gerenciar Cadastramento de Discentes &gt; Confirmação</h2>

<h:form>

<table class="visualizacao">
	<caption>Confirmação do Cadastramento</caption>
	<tr>
		<th>Processo Seletivo Vestibular:</th>
		<td><h:outputText id="psVest" value="#{cadastramentoDiscenteTecnico.processoSeletivo.nome}"/></td>
	</tr>
	<tr>
		<td colspan="2">
			<table class="subFormulario" width="100%">
				<caption>Confirmações (${fn:length(cadastramentoDiscenteTecnico.cadastrados)})</caption>
				<thead>
					<tr>
						<th style="text-align: center; width: 10%;">Ingresso</th>
						<th style="text-align: center; width: 15%;">CPF</th>
						<th style="text-align: left;">Nome</th>
						<th style="text-align: left; width: 15%;">Novo Status</th>
					</tr>
				</thead>
				<c:choose>
					<c:when test="${not empty cadastramentoDiscenteTecnico.cadastrados}">
						<c:forEach items="#{cadastramentoDiscenteTecnico.cadastrados}" var="discente" varStatus="loop">
							<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td style="text-align: center;"><h:outputText value="#{discente.anoPeriodoIngresso}" /></td>
								<td style="text-align: center;"><h:outputText value="#{discente.pessoa.cpf_cnpjString}" /></td>
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
				<caption>Cancelamentos (${fn:length(cadastramentoDiscenteTecnico.cancelados)})</caption>
				<thead>
					<tr>
						<th style="text-align: center; width: 10%;">Ingresso</th>
						<th style="text-align: center; width: 15%;">CPF</th>
						<th style="text-align: left;">Nome</th>
						<th style="text-align: left; width: 15%;">Novo Status</th>
					</tr>
				</thead>
				<c:choose>
					<c:when test="${not empty cadastramentoDiscenteTecnico.cancelados}">
						<c:forEach items="#{cadastramentoDiscenteTecnico.cancelados}" var="discente" varStatus="loop">
							<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td style="text-align: center;"><h:outputText value="#{discente.anoPeriodoIngresso}" /></td>
								<td style="text-align: center;"><h:outputText value="#{discente.pessoa.cpf_cnpjString}" /></td>
								<td><h:outputText value="#{discente.pessoa.nome}"/></td>
								<td><h:outputText value="#{discente.statusString}"/></td>
							</tr>
						</c:forEach>
						
						<a4j:outputPanel rendered="#{ !cadastramentoDiscenteTecnico.preCadastro }">
							<tr>
								<th>Motivo do Indeferimento: <span class="obrigatorio">&nbsp;</span></th>
								<td colspan="3"><h:inputTextarea style="width:600px;height:100px;" id="motivoIndeferimento" value="#{ cadastramentoDiscenteTecnico.motivoIndeferimento }" /></td>
							</tr>
						</a4j:outputPanel>
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
				<h:commandButton value="Confirmar" action="#{ cadastramentoDiscenteTecnico.confirmarPre }" id="btnConfirmarPre" rendered="#{ cadastramentoDiscenteTecnico.preCadastro }" />
				<h:commandButton value="Confirmar" action="#{ cadastramentoDiscenteTecnico.confirmar }" id="btnConfirmar" rendered="#{ !cadastramentoDiscenteTecnico.preCadastro }" />
				<h:commandButton value="<< Voltar" action="#{ cadastramentoDiscenteTecnico.telaFormulario }" id="btnVoltar"/>
			</td>
		</tr>
	</tfoot>
</table>


</h:form>


</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>