<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form>
 <h2><ufrn:subSistema /> > Lista de Calend�rios da Aplica��o da Auto Avalia��o ${ respostasAutoAvaliacaoMBean.nivelDescricao }</h2>
	<div class="descricaoOperacao">
		<p><b>Caro usu�rio,</b></p>
		<p>Abaixo est�o listadas os calend�rios de aplica��o da Auto Avalia��o atualmente cadastrados. </p>
	</div>
			
	<div class="infoAltRem" style="width: 80%">
		<h:graphicImage value="/img/nota.png" style="overflow: visible;" />: Visualizar Respostas
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Preencher a Auto Avalia��o
	</div>
	<table class="formulario" width="80%">
		<caption>Lista de Formul�rios de Aplica��o da Auto Avalia��o</caption>
		<thead>
			<tr>
				<th>Formul�rio</th>
				<th>Situa��o</th>
				<th style="text-align: center">Per�odo de Aplica��o</th>
				<th width="2%"></th>
				<th width="2%"></th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{respostasAutoAvaliacaoMBean.all}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td style="text-align: left">${item.calendario.questionario.titulo}</td>
					<td style="text-align: left;">
						<h:outputText value="#{ item.situacao }" rendered="#{ not empty item.situacao }" />
						<h:outputText value="N�O RESPONDIDO" rendered="#{ empty item.situacao }" />
					</td>
					<td style="text-align: center"><ufrn:format type="data" valor="${item.calendario.dataInicio}" />
						 a <ufrn:format type="data" valor="${item.calendario.dataFim}"/></td>
					<td width="2%">
						<h:commandLink action="#{respostasAutoAvaliacaoMBean.verRespostas}" id="VisualizarRespostas">
							<h:graphicImage value="/img/nota.png" style="overflow: visible;" title="Visualizar Respostas"/>
							<f:param name="id" value="#{item.calendario.id}" />
						</h:commandLink>
					</td>
					<td width="2%">
						<h:commandLink action="#{respostasAutoAvaliacaoMBean.responderFormulario}" 
							rendered="#{item.calendario.periodoPreenchimento && (item.salvo || item.retornado || item.id == 0)}" id="responder">
							<h:graphicImage value="/img/seta.gif" style="overflow: visible;" title="Preencher a Auto Avalia��o"/>
							<f:param name="id" value="#{item.calendario.id}" />
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="5">
					<h:commandButton action="#{calendarioAplicacaoAutoAvaliacaoMBean.cancelar}" value="Cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>