<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<f:view>

	<a4j:keepAlive beanName="levantamentoInfraMBean" />
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Levantamento de Infra-Estrutura</h2>
	
	<h:messages showDetail="true" />
	
	<div class="descricaoOperacao">
		<p>Nesta página são listadas as solicitações de Levantamento de Infra-Estrutura. Utilize
		os filtros para ver somente as solicitações referente a uma única biblioteca e para visualizar
		as solicitações já atendidas.</p>
	</div>
	
	<h:form id="form">
	
		<table class="formulario" style="width: 300px;">
			<caption>Filtros</caption>
			
			<tbody>
				<tr>
					<th>Biblioteca:</th>
					<td>
						<h:selectOneMenu id="biblioteca" value="#{ levantamentoInfraMBean.filtroBiblioteca }">
							<ufrn:checkRole papeis="<%= new int[] {SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL} %>">
								<f:selectItem itemLabel="Todas" itemValue="#{ levantamentoInfraMBean.todasBibliotecas }" />
							</ufrn:checkRole>
							<f:selectItems value="#{ levantamentoInfraMBean.bibliotecasDoBibliotecarioCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Situação:</th>
					<td>
						<h:selectOneMenu id="situacao" value="#{ levantamentoInfraMBean.filtroSituacao }">
							<f:selectItem itemLabel="Todas" itemValue="#{ levantamentoInfraMBean.todasSituacoes }" />
							<f:selectItems value="#{ levantamentoInfraMBean.situacoesCombo }"/>
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			
			<tfoot><tr><td colspan="2">
				<h:commandButton value="Filtrar" id="filtrar" action="#{ levantamentoInfraMBean.filtrar }"/>
				<h:commandButton value="<< Voltar" id="voltar" action="#{ levantamentoInfraMBean.cancelar }"/>
			</td></tr></tfoot>
		</table>
		
		<div class="infoAltRem" style="margin-top: 15px;">
			<h:graphicImage value="/img/seta.gif"/> : Realizar levantamento
			<h:graphicImage value="/img/view.gif"/> : Visualizar Levantamento
		</div>
		
		<table class="listagem" style="margin-top: 15px;" >
		
			<caption>Solicitações de Levantamento de Infra-estrutura</caption>
			
			<thead>
				<tr>
					<th style="text-align: right;">Número</th>
					<th style="text-align: center;">Data da Solicitação</th>
					<th style="text-align: left;">Biblioteca</th>
					<th style="text-align: center;">Situação</th>
					<th style="text-align: left;">Ultima alteração</th>
					<th style="text-align: center;">Ações</th>
				</tr>
			</thead>
		
			<tbody>
			
				<c:choose>
					
					<c:when test="${ empty levantamentoInfraMBean.lista }"><tr><td colspan="4" style="text-align: center;">
					
						<em>Não há levantamentos que obedeçam aos valores escolhidos para os filtros.</em>
					
					</td></tr></c:when>
					
					<c:otherwise>
					
						<c:forEach var="lv" items="#{ levantamentoInfraMBean.lista }">
							<tr>
								<td style="text-align: right;"><h:outputText value="#{ lv.numeroLevantamentoInfra }" /> </td>
								<td style="text-align: center;"><h:outputText value="#{ lv.dataSolicitacao }" /> </td>
								<td style="text-align: left;"><h:outputText value="#{ lv.biblioteca.descricao }" /> </td>
								<td style="text-align: center;"><h:outputText value="#{ lv.descricaoSituacao }" /> </td>
								<td style="text-align: left;">
									<c:choose>
										<c:when test="${ not empty lv.dataAtualizacao }">
											<h:outputText value="#{ lv.dataAtualizacao }" />
											por
											<h:outputText value="#{ lv.registroEntradaAtualizacao.usuario.nome }" />
										</c:when>
										<c:otherwise>
											-
										</c:otherwise>
									</c:choose>
								</td>
								<td style="text-align: center;">
									<c:choose>
										<c:when test="${ lv.situacao == lv.solicitado }">
											<h:commandLink action="#{ levantamentoInfraMBean.visualizarParaBibliotecario }" >
												<f:param name="idLevantamentoInfra" value="#{ lv.id }" />
												<h:graphicImage value="/img/seta.gif" alt="Atender" title="Realizar Levantamento" />
											</h:commandLink>
										</c:when>
										<c:otherwise>
											<h:commandLink action="#{ levantamentoInfraMBean.visualizarParaBibliotecario }" >
												<f:param name="idLevantamentoInfra" value="#{ lv.id }" />
												<h:graphicImage value="/img/view.gif" alt="Visualizar" title="Visualizar Levantamento" />
											</h:commandLink>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
						</c:forEach>
						
					</c:otherwise>
					
				</c:choose>
			
			</tbody>
			
		</table>
	
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>