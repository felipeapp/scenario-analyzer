<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<f:view>
	<a4j:keepAlive beanName="alterarSituacaoMatriculaRede"/>
	<h2><ufrn:subSistema /> &gt; Consultar Turma</h2>
	
	<h:form id="form" prependId="false">
		
	<br/>
			
		<table class="formulario" width="90%">
		<caption>Dados da Turma</caption>
		<tr><td colspan="2">
			<table class="subFormulario" width="100%" style="">
			<caption>Dados Básicos</caption>
				<tr>
					<th width="20%" style="font-weight:bold">Componente Curricular:</th>
					<td>
						<h:outputText value="#{turmaRedeMBean.obj.componente.descricaoResumida}"/>
					</td>
				</tr>
				<tr>
					<th style="font-weight:bold"><b>Campus:</b></th>
					<td>
						<h:outputText value="#{turmaRedeMBean.obj.dadosCurso.campus.descricao}" /> 
					</td>
				</tr>
				<tr>
					<th style="font-weight:bold">Ano-Período:</th>
					<td>
						<h:outputText value="#{ turmaRedeMBean.obj.anoPeriodo }"/>
					</td>
				</tr>
				<tr>
					<th style="font-weight:bold">Período de Aulas:</th>
					<td>
					<h:outputText value="#{ turmaRedeMBean.obj.dataInicio}"/> - <h:outputText value="#{ turmaRedeMBean.obj.dataFim}"/>
					</td>
				</tr>				
			</table>		
		</td></tr>
		<tr><td colspan="2">		
			<table class="subFormulario" style="width: 100%">
			<caption>Discentes</caption>
				<thead>
					<tr>
						<th width="70%">Discente</th>
						<th style="text-align:center">Situação</th>	
						<th style="text-align:center">Período de Ingresso</th>
						<th style="text-align:center">Status</th>				
					</tr>
				</thead>		
				<tbody>
					<c:forEach items="#{turmaRedeMBean.matriculas}" var="m" varStatus="status">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>${m.discente.nome}</td>
							<td style="text-align:center">${m.situacao.descricao}</td>
							<td style="text-align:center">${m.discente.anoIngresso}.${m.discente.periodoIngresso}</td>
							<td style="text-align:center">${m.discente.status.descricao}</td>
						</tr>			
					</c:forEach>
				</tbody>
			</table>
		</td></tr>
		<tr><td colspan="2">		
			<table class="subFormulario" style="width: 100%">
			<caption>Docentes</caption>
			<c:if test="${ not empty turmaRedeMBean.docentesAssociados}">
				<thead>
					<tr>
						<th width="35%">Docentes</th>
						<th>Tipo</th>				
					</tr>
				</thead>		
				<tbody>
					<c:forEach items="#{turmaRedeMBean.docentesAssociados}" var="d" varStatus="status">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td width="15%">${d.nome}</td>
							<td>${d.tipo.descricao}</td>
						</tr>			
					</c:forEach>
				</tbody>
			</c:if>
			<c:if test="${ empty turmaRedeMBean.docentesAssociados}">
				<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td colspan="3" align="center" width="1%"> 
						<div style="color:red;font-weight:bold;">Nenhum docente foi escolhido.</div>
					</td> 
				</tr>			
			</c:if>
			</table>
		</td></tr>		
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="<< Voltar" action="#{turmaRedeMBean.telaBuscarTurmas}" id="turmas" />	
					<h:commandButton value="Cancelar" action="#{turmaRedeMBean.cancelar}" id="cancelar" />	
				</td>
			</tr>
		</tfoot>

		
	</table>
	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>