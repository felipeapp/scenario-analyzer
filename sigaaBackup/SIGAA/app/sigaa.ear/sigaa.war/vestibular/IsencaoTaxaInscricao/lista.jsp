<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Listar/Remover Isentos da Taxa de Inscrição</h2>
	<h:form id="formBusca">
	<a4j:keepAlive beanName="isencaoTaxaInscricao"></a4j:keepAlive>
	<div class="descricaoOperacao">Este formulário permite
	pesquisar por CPFs cadastrados como isentos da taxa de inscrição do Vestibular.
	 Para pesquisar, selecione um processo seletivo.
	 Você poderá informar uma lista de CPFs a buscar.</div>

	<table class="formulario" width="80%">
		<caption>Informe os Parametros para Realizar a Busca:</caption>
		<tr>
			<th>Processo Seletivo:</th>
			<td>
				<h:selectOneMenu id="processoSeletivo"
					value="#{isencaoTaxaInscricao.obj.processoSeletivoVestibular.id}">
					<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
					<f:selectItems value="#{processoSeletivoVestibular.allAtivoCombo}" />
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th>CPF:</th>
			<td>
				<h:inputText value="#{isencaoTaxaInscricao.cpf}" size="16" maxlength="14" id="txtCPF" onkeypress="return formataCPF(this, event, null)" >
					<f:converter converterId="convertCpf"/>
					<f:param name="type" value="cpf"/>
				</h:inputText>
			</td>
		</tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Buscar" action="#{isencaoTaxaInscricao.buscar}" id="buscarCPFs"/> 
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{isencaoTaxaInscricao.cancelar}" id="cancela" />
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
	<br>
	<c:if test="${not empty isencaoTaxaInscricao.resultadosBusca}">
		<h:form id="formResultado">	
			<center>
			<div class="infoAltRem">
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover CPF
			</div>
			</center>
			<table class="listagem" width="100%">
				<caption>Lista de Isentos Encontrados (${fn:length(isencaoTaxaInscricao.resultadosBusca)})</caption>
				<thead>
					<tr>
						<th style="text-align: right;">Ordem</th>
						<th style="text-align: center;">CPF</th>
						<th style="text-align: left;">Nome</th>
						<th style="text-align: left;">Processo Seletivo</th>
						<th style="text-align: left;">Tipo de Isento</th>
						<th style="text-align: right;">Valor a Pagar</th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{isencaoTaxaInscricao.resultadosBusca}" var="item" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td style="text-align: right;">${status.index + 1}</td>
							<td style="text-align: center;"><h:outputText value="#{item.cpfFormatado}"/></td>
							<td style="text-align: left;">
								<c:if test="${not empty item.pessoa}">
									<h:outputText value="#{item.pessoa.nome}"/>
								</c:if>
								<h:outputText value="Não há cadastro deste CPF no módulo Vestibular" rendered="#{empty item.pessoa}" />
							</td>
							<td style="text-align: left;">
								<h:outputText value="#{item.processoSeletivoVestibular.nome}" />
							</td>
							<td style="text-align: left;">
								<h:outputText value="Estudante" rendered="#{item.estudante}"/>
								<h:outputText value="Funcionário" rendered="#{item.funcionario}"/>
							</td>
							<td style="text-align: right;">
								<h:outputText value="Isento Total" rendered="#{item.isentoTotal}"/>
								<c:if test="${not item.isentoTotal}">
									<ufrn:format type="moeda" valor="${item.valor}" />
								</c:if>
							</td>
							<td  style="text-align: right;">
								<h:commandLink title="Remover" action="#{isencaoTaxaInscricao.remover}"
									onclick="#{confirmDelete}" style="border: 0;" id="remover">
									<f:param name="id" value="#{item.id}" />
									<h:graphicImage url="/img/delete.gif" />
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</h:form>
		<h:form id="formPaginacao">
			<br/>
			<div style="text-align: center;"> 
				<h:commandButton image="/img/voltar.gif" actionListener="#{paginacao.previousPage}" rendered="#{paginacao.paginaAtual > 0 }" style="vertical-align:middle" id="paginacaoVoltar"/>
				<h:selectOneMenu value="#{paginacao.paginaAtual}" valueChangeListener="#{paginacao.changePage}" onchange="submit()" immediate="true" id="mudaPagina">
					<f:selectItems id="paramPagina" value="#{paginacao.listaPaginas}"/>
				</h:selectOneMenu>
				<h:commandButton image="/img/avancar.gif" actionListener="#{paginacao.nextPage}"  rendered="#{paginacao.paginaAtual < (paginacao.totalPaginas - 1)}" style="vertical-align:middle" id="paginacaoAvancar"/>
				<br/><br/>
   				<em><h:outputText value="#{paginacao.totalRegistros }"/> Registro(s) Encontrado(s)</em>
 			</div>
		</h:form>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>