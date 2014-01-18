<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<f:view>
	<a4j:keepAlive beanName="alterarSituacaoMatriculaRede"/>
	<h2><ufrn:subSistema /> &gt; 
		<c:if test="${!turmaRedeMBean.alterar}">	
			Criar Turma
		</c:if>
		<c:if test="${turmaRedeMBean.alterar}">	
			Alterar Turma
		</c:if>
	</h2>
	
	<h:form id="form" prependId="false">
	
	<div class="descricaoOperacao">
		<b>Caro usuário,</b> 
		<br/><br/>
		<c:if test="${!turmaRedeMBean.alterar}">	
			Esta é a tela final para o cadastro de turma. Após clicar no botão "confirmar", a turma será criada, os discentes selecionados serão matriculados e os docentes serão associados. 
		</c:if>
		<c:if test="${turmaRedeMBean.alterar}">	
			Esta é a tela final para a alteração de turma. Após clicar no botão "confirmar", a turma será alterada conforme os dados escolhidos. 
		</c:if>
	</div>	
	
	<br/>
			
		<table class="formulario" width="90%">
		<caption>Dados da Turma</caption>
		<tr><td>
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
		<tr><td>		
			<table class="subFormulario" style="width: 100%">
			<caption>Discentes</caption>
			<c:if test="${ not empty turmaRedeMBean.discentesEscolhidos}">			
				<thead>
					<tr>
						<th width="70%">Discente</th>
						<th style="text-align:center">Período de Ingresso</th>
						<th style="text-align:center">Situação</th>				
					</tr>
				</thead>		
				<tbody>
					<c:forEach items="#{turmaRedeMBean.discentesEscolhidos}" var="d" varStatus="status">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td>${d.nome}</td>
							<td style="text-align:center">${d.anoIngresso}.${d.periodoIngresso}</td>
							<td style="text-align:center">${d.status.descricao}</td>
						</tr>			
					</c:forEach>
				</tbody>
			</c:if>
			<c:if test="${ empty turmaRedeMBean.discentesEscolhidos }">
				<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td colspan="3" align="center" width="1%"> 
						<div style="color:red;font-weight:bold;">Nenhum discente foi escolhido.</div>
					</td> 
				</tr>			
			</c:if>
			</table>
		</td></tr>
		<tr><td>		
			<table class="subFormulario" style="width: 100%">
			<caption>Docentes</caption>
			<c:if test="${ not empty turmaRedeMBean.docentesEscolhidos}">
				<thead>
					<tr>
						<th width="35%">Docentes</th>
						<th>Tipo</th>				
					</tr>
				</thead>		
				<tbody>
					<c:forEach items="#{turmaRedeMBean.docentesEscolhidos}" var="d" varStatus="status">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td width="15%">${d.nome}</td>
							<td>${d.tipo.descricao}</td>
						</tr>			
					</c:forEach>
				</tbody>
			</c:if>
			<c:if test="${ empty turmaRedeMBean.docentesEscolhidos}">
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
				<td colspan="1">
					<h:commandButton value="Confirmar" action="#{turmaRedeMBean.confirmarAlterar}" rendered="#{turmaRedeMBean.alterar}" id="confirmarAlterar"/>
					<h:commandButton value="Confirmar" action="#{turmaRedeMBean.confirmar}" rendered="#{!turmaRedeMBean.alterar}" id="confirmar"/>
					<h:commandButton value="<< Selecionar Campus" action="#{turmaRedeMBean.telaSelecaoCampus}" rendered="#{!turmaRedeMBean.alterar}" id="voltarTelaCampus"/>
					<h:commandButton value="<< Selecionar Componente" action="#{turmaRedeMBean.telaSelecaoComponentes}" rendered="#{!turmaRedeMBean.alterar}" id="voltarTelaComponente"/>
					<h:commandButton value="<< Selecionar Turma" action="#{turmaRedeMBean.telaBuscarTurmas}" id="turmas" rendered="#{turmaRedeMBean.alterar}"/>
					<h:commandButton value="<< Selecionar Dados Gerais" action="#{turmaRedeMBean.telaDadosGerais}" id="dadosGerais"/>
					<h:commandButton value="<< Selecionar Discentes" action="#{turmaRedeMBean.telaSelecaoDiscentes}" id="voltarTelaDiscentes"/>
					<h:commandButton value="<< Selecionar Docentes" action="#{turmaRedeMBean.telaSelecaoDocentes}" id="voltarTelaDocentes"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{turmaRedeMBean.cancelar}" id="cancelar" />
				</td>
			</tr>
		</tfoot>

		
	</table>
	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>