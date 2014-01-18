<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="cronogramaExecucao"/>
<h2> <ufrn:subSistema /> > Cronograma de Execução </h2>
<h:form>

<table class="formulario">
	  <caption>Resumo</caption>
		
		<tbody>
				<tr colspan="4">
					<th width="25%">Módulo:</th>
					<td><h:outputText value="#{cronogramaExecucao.cronograma.modulo.descricao}" /></td>
				</tr>
				<tr colspan="4">
					<th width="25%">Carga Horária:</th>
					<td><h:outputText value="#{cronogramaExecucao.cronograma.modulo.cargaHoraria}" /></td>
				</tr>
				<tr colspan="4">
					<th width="25%">Ano - Período:</th>
					<td><h:outputText value="#{cronogramaExecucao.cronograma.ano}" />
					 - 
					<h:outputText value="#{cronogramaExecucao.cronograma.periodo}" /></td>
				</tr>
				<tr colspan="4">
					<th width="25%">Período Letivo:</th>
					<td><h:outputText value="#{cronogramaExecucao.cronograma.dataInicio}" />
					 - 
					<h:outputText value="#{cronogramaExecucao.cronograma.dataFim}" /></td>
				</tr>
		 
		</tbody>
	</table>

	<table class="formulario" style="width: 100%">
	  <caption>Turmas Selecionadas</caption>
		<thead>
			<tr>
				<th style="text-align: left;">Descrição</th>
				<th style="text-align: left;">Especialização</th>
				<th style="text-align: center;">Ano</th>
				<th style="text-align: center;">Período</th>
				<th style="text-align: center;">Horário</th>
				<th style="text-align: center;">Polo/Grupo</th>
				<th colspan="2"></th>
			</tr>
		</thead>
		<tbody>
		   <c:forEach var="linha" items="#{cronogramaExecucao.turmasSelecionadas}" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					<td width="40%" style="text-align: left;">${linha.turmaEntrada.cursoTecnico.descricao}</td>
					<td style="text-align: left;">${linha.turmaEntrada.especializacao.descricao}</td>
					<td style="text-align: center;">${linha.turmaEntrada.anoReferencia}</td>
					<td style="text-align: center;">${linha.turmaEntrada.periodoReferencia}</td>
					<td style="text-align: center;">${linha.descricaoHorario}</td>
					<td style="text-align: center;">${linha.opcaoPoloGrupo}</td>
					<td width="3%" align="right">
						<h:commandLink action="#{cronogramaExecucao.removerTurma}">
							<h:graphicImage value="/img/delete.gif"  style="overflow: visible;" title="Remover Turma" />  
							<f:param name="idTurmaSelecionada" value="#{linha.turmaEntrada.id}"/>
						</h:commandLink>
					</td>
				</tr>
		   </c:forEach>
		</tbody>
	</table>
	
	<table class="formulario" style="width: 100%">
	  <caption>Turmas Não Selecionadas</caption>
		<thead>
			<tr>
				<th style="text-align: left;">Descrição</th>
				<th style="text-align: left;">Especialização</th>
				<th style="text-align: center;">Ano</th>
				<th style="text-align: center;">Período</th>
				<th style="text-align: center;">Horário</th>
				<th style="text-align: center;">Polo/Grupo</th>
				<th colspan="2"></th>
			</tr>
		</thead>
		<tbody>
		   <c:if test="${not empty  cronogramaExecucao.turmasNaoSelecionadas}">
			   <c:forEach var="linha" items="#{cronogramaExecucao.turmasNaoSelecionadas}" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td width="40%" style="text-align: left;">${linha.turmaEntrada.cursoTecnico.descricao}</td>
						<td style="text-align: left;">${linha.turmaEntrada.especializacao.descricao}</td>
						<td style="text-align: center;">${linha.turmaEntrada.anoReferencia}</td>
						<td style="text-align: center;">${linha.turmaEntrada.periodoReferencia}</td>
					<td style="text-align: center;">${linha.descricaoHorario}</td>
					<td style="text-align: center;">${linha.opcaoPoloGrupo}</td>
						<td width="3%" align="right">
							<h:commandLink action="#{cronogramaExecucao.adicionarTurma}">
								<h:graphicImage value="/img/adicionar.gif" title="Adicionar Turma" />  
								<f:param name="idTurmaNaoSelecionada" value="#{linha.turmaEntrada.id}"/>
							</h:commandLink>
						</td>
					</tr>
			   </c:forEach>
		   </c:if>
		</tbody>
		<tfoot>   
		   <tr>
				<td colspan="6">
					<h:commandButton value="<< Voltar" action="#{cronogramaExecucao.voltarFormCargaHoraria}" id="voltar" style="overflow: visible;"/>
					<h:commandButton value="Cancelar" action="#{cronogramaExecucao.cancelar}" onclick="#{confirm}" id="cancelar" style="overflow: visible;"/>
					<h:commandButton value="Finalizar" action="#{cronogramaExecucao.salvar}" id="finalizar" style="overflow: visible;"/>
				</td>
		   </tr>
		</tfoot>
	</table>
	
</h:form>			
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>