<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form>
 <h2><ufrn:subSistema /> > Lista de Calend�rios da Aplica��o da Auto Avalia��o ${ calendarioAplicacaoAutoAvaliacaoMBean.nivelDescricao }</h2>
	<div class="descricaoOperacao">
		<p><b>Caro usu�rio,</b></p>
		<p>Abaixo est�o listadas os calend�rios de aplica��o da Auto Avalia��o atualmente cadastrados. </p>
	</div>
			
	<div class="infoAltRem" style="width: 80%">
		<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: <h:commandLink action="#{calendarioAplicacaoAutoAvaliacaoMBean.preCadastrar}" value="Cadastrar Novo Calend�rio"/>
		<h:graphicImage value="/img/estagio/relatorio_final.png" style="overflow: visible;" />: Relat�rio de Preenchimento
		<h:graphicImage value="/img/nota.png" style="overflow: visible;" />: Visualizar / Analisar Respostas
		<br/>
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Formul�rio
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Atualizar Calend�rio
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Calend�rio
	</div>
	<table class="formulario" width="80%">
		<caption>Lista de Calend�rios de Aplica��o da Auto Avalia��o</caption>
		<thead>
			<tr>
				<th>Formul�rio</th>
				<th style="text-align: center">Per�odo de Aplica��o</th>
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
							<h:graphicImage value="/img/estagio/relatorio_final.png" style="overflow: visible;" title="Relat�rio de Prenchimento"/>
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
							<h:graphicImage value="/img/view.gif" style="overflow: visible;" title="Visualizar Formul�rio"/>
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
					</td>
					<td width="2%">
						<h:commandLink action="#{calendarioAplicacaoAutoAvaliacaoMBean.atualizar}" id="atualizar">
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Atualizar Calend�rio" />
							<f:param name="id" value="#{item.id}" />
						</h:commandLink>
					</td>
					<td width="2%">
						<h:commandLink action="#{calendarioAplicacaoAutoAvaliacaoMBean.inativar}" id="remover" onclick="#{ confirmDelete }">
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover Calend�rio" />
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