<c:if test="${fn:length(estagios) > 0}">
<table class="listagem" style="width: 100%">
	<caption class="listagem">${nomeCaption} (${fn:length(estagios)})</caption>
	<thead>
		<tr>
			<th>Discente</th>
			<c:if test="${!pendentes}">
				<th>Orientador</th>
				<th style="text-align: center;">Período do<br/>Estágio</th>
				<th>Tipo do Estágio</th>
			</c:if>
			<th style="text-align: center;">Data do Cadastro</th>
			<th>Status</th>
			<th colspan="3"></th>
		</tr>
	</thead>
	<c:set var="idConcedente" value="0"/> 
	<c:forEach items="#{estagios}" var="estagio" varStatus="loop">	
		<c:if test="${idConcedente != estagio.concedente.pessoa.id}">
			<tr>
				<td colspan="8" class="subFormulario">Concedente: ${estagio.concedente.pessoa.nome}</td>
			</tr>
			<c:set var="idConcedente" value="${estagio.concedente.pessoa.id}"/> 		
		</c:if>
		
		<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">				
			<td>${estagio.discente.matricula} - ${estagio.discente.nome}</td>
			<c:if test="${!estagio.pendente}">
				<td>${estagio.orientador.pessoa.nome}</td>
				<td style="text-align: center;">
					<ufrn:format type="data" valor="${estagio.dataInicio}"></ufrn:format> 
					<h:outputText value="a" rendered="#{ not empty estagio.dataInicio }" /> 
					<ufrn:format type="data" valor="${estagio.dataFim}"></ufrn:format> 
				</td>
				<td>${estagio.tipoEstagio.descricao}</td>
			</c:if>
			<td style="text-align: center;"><ufrn:format type="dataHora" valor="${estagio.dataCadastro}"></ufrn:format></td>
			<td>${estagio.status.descricao}</td>
			<td>
				<img src="${ctx}/img/biblioteca/emprestimos_ativos.png" onclick="exibirOpcoes(${estagio.id});" style="cursor: pointer" title="Visualizar Menu"/>
			</td>																							
			<c:if test="${estagio.pendente && estagioMBean.permiteAnalisarEstagio }">		
				<td>
					<h:commandLink action="#{estagioMBean.efetivarEstagio}" title="Analisar Estagiário">
						<h:graphicImage value="/img/seta.gif"/>
						<f:setPropertyActionListener value="#{estagio}" target="#{estagioMBean.obj}"/>
					</h:commandLink>				
				</td>
			</c:if>
		</tr>		
		<tr class="${s.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style="display: none" id="trOpcoes${estagio.id}">
			<td colspan="8">
				<ul class="listaOpcoes">	
					<a4j:outputPanel rendered="#{estagioMBean.permiteAnalisarEstagio && !estagio.pendente && !estagio.cancelado && !estagio.naoCompativel && !estagio.concluido}">							
						<li id="btnAlterarEstagio">
							<h:commandLink action="#{estagioMBean.alterar}" title="Alterar Estágio">
								Alterar Estágio
								<f:setPropertyActionListener value="#{estagio}" target="#{estagioMBean.obj}"/>
							</h:commandLink>
						</li>
					</a4j:outputPanel>
					<li id="btnVisualizaEstagio">
						<h:commandLink styleClass="noborder" title="Visualizar Estágio" action="#{estagioMBean.view}">
							Visualizar Estágio
							<f:setPropertyActionListener value="#{estagio}" target="#{estagioMBean.obj}"/>
						</h:commandLink>
					</li>	
					<a4j:outputPanel rendered="#{estagio.aprovado || estagio.cancelado || estagio.solicitadoCancelamento}">
						<li id="btnTermo">				
							<h:commandLink action="#{estagioMBean.exibirTermoCompromisso}" target="_blank" title="Termo de Compromisso">
								Termo de Compromisso
								<f:setPropertyActionListener value="#{estagio}" target="#{estagioMBean.obj}"/>
							</h:commandLink>
						</li>	
					</a4j:outputPanel>	
					<a4j:outputPanel rendered="#{estagio.aprovado || estagio.cancelado || estagio.solicitadoCancelamento}">
						<li id="btnRelatorios">
							<h:commandLink action="#{relatorioEstagioMBean.view}" title="Visualizar Relatórios">
								Visualizar Relatórios
								<f:setPropertyActionListener value="#{estagio}" target="#{relatorioEstagioMBean.estagio}"/>
							</h:commandLink>
						</li>	
					</a4j:outputPanel>		
					<a4j:outputPanel rendered="#{estagioMBean.permiteAnalisarEstagio && estagio.aprovado}">						
						<li id="btnRenovarEstagio">
							<h:commandLink action="#{renovacaoEstagioMBean.iniciar}" title="Renovar Estágio">
								 Renovar Estágio
								<f:setPropertyActionListener value="#{estagio}" target="#{renovacaoEstagioMBean.estagio}"/>
							</h:commandLink>
						</li>			
					</a4j:outputPanel>	
					<a4j:outputPanel rendered="#{(estagio.permitePreencherRelatorioPeriodico) &&
					 (buscaEstagioMBean.portalDiscente || buscaEstagioMBean.portalDocente || buscaEstagioMBean.portalConcedenteEstagio)}">
						<li id="btnPreencherRelatorio">
							<h:commandLink action="#{relatorioEstagioMBean.iniciarRelatorioPeriodico}" title="Preencher Relatório Periódico">
								Preencher Relatório Periódico
								<f:setPropertyActionListener value="#{estagio}" target="#{relatorioEstagioMBean.estagio}"/>
							</h:commandLink>
						</li>	
					</a4j:outputPanel>
					<a4j:outputPanel rendered="#{estagio.permitePreencherRelatorioFinal && (buscaEstagioMBean.portalDiscente || 
					      buscaEstagioMBean.portalDocente || buscaEstagioMBean.portalConcedenteEstagio)}">
						<li id="btnRelatorioFinal">
							<h:commandLink action="#{relatorioEstagioMBean.iniciarRelatorioFinal}" title="Preencher Relatório Final"
							rendered="#{(estagio.aprovado || estagio.cancelado || estagio.solicitadoCancelamento) && (buscaEstagioMBean.portalDiscente || buscaEstagioMBean.portalDocente || buscaEstagioMBean.portalConcedenteEstagio)}">
								Preencher Relatório Final
								<f:setPropertyActionListener value="#{estagio}" target="#{relatorioEstagioMBean.estagio}"/>
							</h:commandLink>
						</li>	
					</a4j:outputPanel>	
					<a4j:outputPanel rendered="#{(buscaEstagioMBean.portalDiscente || 
					      buscaEstagioMBean.portalDocente || buscaEstagioMBean.portalConcedenteEstagio)}">
						<li id="btnRelatorioRenovacao">
							<h:commandLink action="#{relatorioEstagioMBean.iniciarRelatorioRenovacao}" title="Preencher Relatório de Renovação"
							rendered="#{(estagio.aprovado || estagio.cancelado || estagio.solicitadoCancelamento) && (buscaEstagioMBean.portalDiscente || buscaEstagioMBean.portalDocente || buscaEstagioMBean.portalConcedenteEstagio)}">
								Preencher Rel. de Renovação
								<f:setPropertyActionListener value="#{estagio}" target="#{relatorioEstagioMBean.estagio}"/>
							</h:commandLink>
						</li>	
					</a4j:outputPanel>							
					<a4j:outputPanel rendered="#{((buscaEstagioMBean.portalDiscente || buscaEstagioMBean.portalConcedenteEstagio) && (estagio.aprovado)) || 
					  ((buscaEstagioMBean.portalCoordenadorGraduacao || convenioEstagioMBean.permiteAnalisarConvenio) && (estagio.aprovado || estagio.solicitadoCancelamento)) }">		
						<li id="btnCancelarEstagio">
							<h:commandLink action="#{estagioMBean.cancelarEstagio}" title="Cancelar Estágio">
								Cancelar Estágio
								<f:setPropertyActionListener value="#{estagio}" target="#{estagioMBean.obj}"/>
							</h:commandLink>
						</li>	
					</a4j:outputPanel>
					<a4j:outputPanel rendered="#{estagio.cancelado}">		
						<li id="btnRescisaoTermo">				
							<h:commandLink action="#{estagioMBean.exibirRescisaoEstagio}" target="_blank" title="Rescisão do Termo">
								Rescisão do Termo
								<f:setPropertyActionListener value="#{estagio}" target="#{estagioMBean.obj}"/>
							</h:commandLink>
						</li>						
					</a4j:outputPanel>
					<li style="clear: both; float: none; background-image: none;"></li>				
				</ul>
			</td>
		</tr>			
	</c:forEach>
</table>	

<style>

	#btnAlterarEstagio a { background-image: url(/sigaa/img/alterar.gif); }
	#btnVisualizaEstagio a { background-image: url(/sigaa/img/view.gif); }
	#btnTermo a { background-image: url(/sigaa/img/report.png); }
	
	#btnRelatorios a { background-image: url(/sigaa/img/estagio/view_relatorio.png); }	
	#btnRenovarEstagio a { background-image: url(/sigaa/img/estagio/renovar.png); }	
	#btnPreencherRelatorio a { background-image: url(/sigaa/img/estagio/responder_relatorio.png); }	
	#btnRelatorioFinal a { background-image: url(/sigaa/img/estagio/relatorio_final.png); }	
	#btnCancelarEstagio a { background-image: url(/sigaa/img/estagio/cancelar_estagio.png); }	
	#btnRescisaoTermo a { background-image: url(/sigaa/img/estagio/rescisao.png); }
	#btnRelatorioRenovacao a { background-image: url(/sigaa/img/estagio/responder_relatorio.png); }
	
	
</style>

<script type="text/javascript">
	function exibirOpcoes(id){
		var linha = 'trOpcoes'+ id;
		$(linha).toggle();
	}
</script>
</c:if>