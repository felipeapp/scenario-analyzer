<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form>
	<a4j:keepAlive beanName="respostasAutoAvaliacaoMBean" />
	<a4j:keepAlive beanName="calendarioAplicacaoAutoAvaliacaoMBean" />
 	<h2><ufrn:subSistema /> > Lista de Respostas da Auto Avaliação ${ respostasAutoAvaliacaoMBean.nivelDescricao }</h2>
	<div class="descricaoOperacao">
		<p><b>Caro usuário,</b></p>
		<p>Abaixo estão listadas as respostas da Auto Avaliação atualmente cadastrados. </p>
	</div>
			
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Respostas
		<h:graphicImage value="/img/pdf.png" style="overflow: visible;" />: Exportar em PDF
		<h:graphicImage value="/img/edit.png" style="overflow: visible;" />: Analisar Auto Avaliação
	</div>
	<table class="formulario" width="100%">
		<caption>Lista de Respostas da Auto Avaliação</caption>
		<tbody>
		<tr>
			<th class="rotulo" width="15%">Formulário:</th>
			<td>${ respostasAutoAvaliacaoMBean.calendarioAutoAvaliacao.questionario.titulo }</td>
		</tr>
		<tr>
			<th class="rotulo">Período de Aplicação:</th>
			<td>
				de <ufrn:format type="data" valor="${respostasAutoAvaliacaoMBean.calendarioAutoAvaliacao.dataInicio}" /> 
				a 
				<ufrn:format type="data" valor="${respostasAutoAvaliacaoMBean.calendarioAutoAvaliacao.dataFim}"/>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario">Respostas da Auto Avaliação</td>
		</tr>
		<tr>
			<td colspan="2">
				<table class="listagem">
				<thead>
					<tr>
						<th>
							<h:outputText value="Programa" rendered="#{ respostasAutoAvaliacaoMBean.portalPpg }" />
							<h:outputText value="Curso" rendered="#{ respostasAutoAvaliacaoMBean.portalLatoSensu }" />
						</th>
						<th>Respondido Em</th>
						<th>Atualizado Em</th>
						<th>Preenchido Por</th>
						<th>Situação</th>
						<th width="2%"></th>
						<th width="2%"></th>
						<th width="2%"></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{respostasAutoAvaliacaoMBean.resultadosBusca}" var="item" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td>
								${ item.unidade.nome }
								${ item.curso.nome }
							</td>
							<td><ufrn:format type="datahora" valor="${ item.cadastradoEm }"/></td>
							<td><ufrn:format type="datahora" valor="${ item.atualizadoEm }"/></td>
							<td>${ item.preenchidoPor.usuario.pessoa.nomeAbreviado}</td>
							<td>${ item.situacao}</td>
							<td width="2%">
								<h:commandLink action="#{respostasAutoAvaliacaoMBean.verRespostasPppg}" id="Visualizar">
									<h:graphicImage value="/img/view.gif" style="overflow: visible;" title="Visualizar Respostas"/>
									<f:param name="id" value="#{item.id}" />
								</h:commandLink>
							</td>
							<td width="2%">
								<h:commandLink action="#{respostasAutoAvaliacaoMBean.exportarRespostasPDF}" id="Exportar">
									<h:graphicImage value="/img/pdf.png" style="overflow: visible;" title="Exportar em PDF"/>
									<f:param name="id" value="#{item.id}" />
								</h:commandLink>
							</td>
							<td width="2%">
								<h:commandLink action="#{respostasAutoAvaliacaoMBean.analisarAutoAvaliacao}" id="Analisar" rendered="#{ item.submetido }">
									<h:graphicImage value="/img/edit.png" style="overflow: visible;" title="Analisar Auto Avaliação"/>
									<f:param name="id" value="#{item.id}" />
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</tbody>
				</table>
			</td>
		</tr>
		</tbody>
		<tfoot>
			<tr style="text-align: center">
				<td colspan="2">
					<h:commandButton action="#{calendarioAplicacaoAutoAvaliacaoMBean.listar}" value="<< Voltar" id="voltar" />
					<h:commandButton action="#{calendarioAplicacaoAutoAvaliacaoMBean.cancelar}" value="Cancelar" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>