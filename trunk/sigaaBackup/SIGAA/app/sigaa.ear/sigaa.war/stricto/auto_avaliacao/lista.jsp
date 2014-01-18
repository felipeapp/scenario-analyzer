<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form>
 <h2><ufrn:subSistema /> > Lista de Calendários da Aplicação da Auto Avaliação ${ calendarioAplicacaoAutoAvaliacaoMBean.nivelDescricao }</h2>
	<div class="descricaoOperacao">
		<p><b>Caro usuário,</b></p>
		<p>Abaixo estão listadas os calendários de aplicação da Auto Avaliação atualmente cadastrados. </p>
	</div>
			
	<div class="infoAltRem" style="width: 80%">
		<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: <h:commandLink action="#{calendarioAplicacaoAutoAvaliacaoMBean.preCadastrar}" value="Cadastrar Novo Calendário"/>
		<h:graphicImage value="/img/estagio/relatorio_final.png" style="overflow: visible;" />: Relatório de Preenchimento
		<h:graphicImage value="/img/nota.png" style="overflow: visible;" />: Visualizar / Analisar Respostas
		<br/>
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Formulário
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Atualizar Calendário
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Calendário
	</div>
	<table class="formulario" width="80%">
		<caption>Lista de Calendários de Aplicação da Auto Avaliação</caption>
		<thead>
			<tr>
				<th>Formulário</th>
				<th style="text-align: center">Período de Aplicação</th>
				<th style="text-align: right;">Qtd. Respostas</th>
				<th width="2%"></th>
				<th width="2%"></th>
				<th width="2%"></th>
				<th width="2%"></th>
				<th width="2%"></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{calendarioAplicacaoAutoAvaliacaoMBean.all}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td style="text-align: left">${item.questionario.titulo}</td>
					<td style="text-align: center"><ufrn:format type="data" valor="${item.dataInicio}" /> a <ufrn:format type="data" valor="${item.dataFim}"/></td>
					<td style="text-align: right;">${ item.qtdRespostas }</td>
					<td width="2%">
						<h:commandLink action="#{calendarioAplicacaoAutoAvaliacaoMBean.relatorioPreenchido}" id="relatorioPreenchido" rendered="#{ item.qtdRespostas > 0 }">
							<h:graphicImage value="/img/estagio/relatorio_final.png" style="overflow: visible;" title="Relatório de Prenchimento"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
					</td>
					<td width="2%">
						<h:commandLink action="#{respostasAutoAvaliacaoMBean.listarRespostas}" id="listarRespostas" rendered="#{ item.qtdRespostas > 0 }">
							<h:graphicImage value="/img/nota.png" style="overflow: visible;" title="Visualizar / Analisar Respostas"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
					</td>
					<td width="2%">
						<h:commandLink action="#{calendarioAplicacaoAutoAvaliacaoMBean.view}" id="VisualizarFormulario">
							<h:graphicImage value="/img/view.gif" style="overflow: visible;" title="Visualizar Formulário"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
					</td>
					<td width="2%">
						<h:commandLink action="#{calendarioAplicacaoAutoAvaliacaoMBean.atualizar}" id="atualizar">
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Atualizar Calendário" />
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
					</td>
					<td width="2%">
						<h:commandLink action="#{calendarioAplicacaoAutoAvaliacaoMBean.inativar}" id="remover" onclick="#{ confirmDelete }">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Calendário" />
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="8">
					<h:commandButton action="#{calendarioAplicacaoAutoAvaliacaoMBean.cancelar}" value="Cancelar"  />
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>