<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="trancamentoPrograma" />
<f:view>
	<h:form id="form">
	<h2> <ufrn:subSistema /> &gt; Solicitações de Trancamento de Programa </h2>
	
	<c:if test="${acesso.dae or acesso.administradorDAE or trancamentoPrograma.portalCoordenadorStricto}">
		<div class="descricaoOperacao">
			<p>Caro Usuário,</p>
			<p>Através desta tela será possível analisar as solicitações de Trancamento de Programa do discente selecionado, 
			assim podendo <b>Submeter</b> ou <b>Indeferir</b> a solicitação.</p>
		</div>
	</c:if>
	
	<c:set value="#{trancamentoPrograma.discente}" var="discente"></c:set>
	<%@include file="/graduacao/info_discente.jsp"%>			
	
	<c:if test="${empty trancamentoPrograma.solicitacoes}">
			<div align="center" style="font-style:italic; ">Nenhuma Solicitação de Trancamento de Programa Cadastrada.</div>
	</c:if>
	
	<c:if test="${not empty trancamentoPrograma.solicitacoes}">
	
		<c:if test="${not trancamentoPrograma.obj.solicitado}">
			<div class="infoAltRem" style="width:80%">
		        <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Solicitação
		        <c:if test="${trancamentoPrograma.portalDiscente}">
		        	<h:graphicImage value="/img/graduacao/cancelar16.png" style="overflow: visible;"/>: Cancelar Solicitação	
		        </c:if>
		        <c:if test="${acesso.dae or acesso.administradorDAE or trancamentoPrograma.portalCoordenadorStricto}">
		        	<h:graphicImage value="/img/check.png"/>/<h:graphicImage value="/img/check_cinza.png" style="margin-left:0;overflow: visible;"/>: Submetido/Submeter Solicitação
		        	<h:graphicImage value="/img/cross.png" style="overflow: visible;"/>: Indeferir Solicitação
		        </c:if>
			</div>
		</c:if>
		
			<c:if test="${ trancamentoPrograma.obj.solicitado && trancamentoPrograma.portalDiscente}">
				<br>
				<div align="center">
					<h:commandButton image="/img/printer_ok.png"
					title="Gerar Comprovante da Solicitação" action="#{trancamentoPrograma.view}"/> <br>
					<h:commandLink action="#{trancamentoPrograma.view}" value="Imprimir Comprovante da Solicitação"/>
				</div>
				<br>
			</c:if>		
		
			<table class="listagem" style="width:80%">
				<caption> Solicitações Cadastradas </caption>
				<thead>
				<tr>
					<th style="text-align: center;">Data de Cadastro</th>
					<c:if test="${trancamentoPrograma.discente.stricto}">
						<th style="text-align: center;"> Início do Trancamento </th>
						<th style="text-align: right;"> Meses </th>
					</c:if>
					<th style="text-align: center;"> Ano-Período </th>
					<th> Status </th>
					<th colspan="4"></th>
				</tr>
				</thead>
				<tbody>
		
				
					<c:forEach items="#{trancamentoPrograma.solicitacoes}" var="_solicitacao" varStatus="status">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td style="text-align: center;"> <ufrn:format type="data" valor="${_solicitacao.dataCadastro}"/> </td>
							<c:if test="${trancamentoPrograma.discente.stricto}">
								<td style="text-align: center;"> <ufrn:format type="data" valor="${_solicitacao.inicioTrancamento}"/> </td>
								<td style="text-align: right;"> ${_solicitacao.numeroMeses} </td>
							</c:if>					
							<td style="text-align: center;"> ${ _solicitacao.ano }.${ _solicitacao.periodo } </td>
							<td> ${ _solicitacao.situacaoString } </td>
								<td style="width: 1px;"> 
									<c:if test="${(not _solicitacao.cancelado && not trancamentoPrograma.obj.solicitado) || not trancamentoPrograma.portalDiscente}">
										<h:commandButton image="/img/view.gif" title="Visualizar Solicitação" 
											action="#{trancamentoPrograma.view}" styleClass="noborder">
											<f:setPropertyActionListener value="#{_solicitacao}" target="#{trancamentoPrograma.obj}"/>
										</h:commandButton>
									</c:if>
								</td>
								<td style="text-align: right; width: 1px;"> 
									<c:if test="${trancamentoPrograma.portalDiscente && _solicitacao.solicitado && not trancamentoPrograma.obj.solicitado}">
										<h:commandButton onclick="return confirm('Desejar cancelar a solicitação de trancamento de programa?')"  image="/img/graduacao/cancelar16.png" title="Cancelar Solicitação"  
											action="#{trancamentoPrograma.cancelarSolicitacao}" styleClass="noborder">
											<f:setPropertyActionListener value="#{_solicitacao}" target="#{trancamentoPrograma.obj}"/>
										</h:commandButton>
									</c:if>						
								</td>			
								<td style="text-align: right; width: 1px;"> 
									<c:if test="${(acesso.dae or acesso.administradorDAE or trancamentoPrograma.portalCoordenadorStricto) && (!_solicitacao.cancelado && !_solicitacao.indeferido)}">
										<h:commandButton image="/img/check#{not _solicitacao.trancado ? '_cinza' : ''}.png" title="#{_solicitacao.trancado ? 'Submetido' : 'Submeter Solicitação'}" 
											action="#{trancamentoPrograma.preSubmeter}" styleClass="noborder" disabled="#{_solicitacao.trancado ? 'true' : 'false'}">
											<f:setPropertyActionListener value="#{_solicitacao}" target="#{trancamentoPrograma.obj}"/>
										</h:commandButton>
									</c:if>					
								</td>			
								<td style="text-align: right; width: 1px;"> 
									<c:if test="${(acesso.dae or acesso.administradorDAE or trancamentoPrograma.portalCoordenadorStricto) && (_solicitacao.solicitado)}">
										<h:commandButton image="/img/cross.png" title="Indeferir Solicitação" 
											action="#{trancamentoPrograma.indeferirSolicitacao}" styleClass="noborder">
											<f:setPropertyActionListener value="#{_solicitacao}" target="#{trancamentoPrograma.obj}"/>
										</h:commandButton>
									</c:if>									
								</td>			
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</c:if>
		<br/><br/>
		
		<c:if test="${not empty movimentacaoAluno.historicoMovimentacoes}">
		<table class="subFormulario" style="width: 80%">
			<caption>Histórico de Movimentações do Discente</caption>
			<thead>
				<tr>
					
					<td>Tipo</td>
					<c:if test="${acesso.dae or acesso.administradorDAE}"> 
					<td>Usuário</td>
					</c:if>
					<td style="text-align: center;">Ano-Período</td>
					<td width="15%" style="text-align: center;" >Data</td>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="${movimentacaoAluno.historicoMovimentacoes}" var="mov" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td>${mov.tipoMovimentacaoAluno.descricao}</td>
					<c:if test="${acesso.dae or acesso.administradorDAE}">
						<td>${mov.usuarioCadastro.pessoa.nome}</td>
					</c:if>
					<td style="text-align: center;">${mov.anoPeriodoReferencia }</td>
					<td><ufrn:format type="dataHora" valor="${mov.dataOcorrencia}" /></td>
				</tr>
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td colspan="4"><i>${mov.observacao}</i></td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
		</c:if>
		
		<c:if test="${ trancamentoPrograma.obj.solicitado }">
			<br>
			<center>
				<h:commandButton value="Voltar ao Menu Principal" action="#{trancamentoPrograma.cancelar}" />
			</center>
		</c:if>		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
