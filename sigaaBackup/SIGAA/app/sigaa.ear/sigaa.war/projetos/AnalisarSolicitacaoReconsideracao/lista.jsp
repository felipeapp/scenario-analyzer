<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	
	<h2><ufrn:subSistema /> > Analisar Solicitações de Reconsideração</h2>
	<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;" id="gifview"/>: Visualizar Ação
   		    <h:graphicImage url="/img/projeto/orcamento.png" width="16" height="16" style="overflow: visible;" /> <h:outputText value=": Visualizar Orçamento" />
		    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" id="gifseta"/>: Analisar Solicitação
	</div>
	<br/>
	<c:set var="solicitacoes" value="#{solicitacaoReconsideracao.solicitacoesPendentes}"/>

			<h:form>
				 <table class="listagem">
				    <caption>Solicitações pendentes de validação (${ fn:length(solicitacoes) })</caption>
			
				      <thead>
				      	<tr>
				      		<th>Nº Inst.</th>
				        	<th width="70%">Título</th>
				        	<th>Unidade</th>
				        	<th style="text-align:center;"  width="20%">Data Submissão</th>
				        	<th>&nbsp;</th>
				        	<th>&nbsp;</th>
				        	<th>&nbsp;</th>				        	
				        </tr>
				 	</thead>
				 	<tbody>				 	
						<c:if test="${not empty solicitacoes}">
						 	 <c:forEach items="#{solicitacoes}" var="sol" varStatus="status">
					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					
										<td> <fmt:formatNumber value="${sol.projeto.numeroInstitucional}"  pattern="0000"/>/${sol.projeto.ano} </td>
					                    <td> ${sol.projeto.titulo} </td>
					                    <td> ${sol.projeto.unidade.sigla} </td>
										<td align="center"> <fmt:formatDate value="${sol.dataSolicitacao}" pattern="dd/MM/yyyy HH:mm:ss" /> </td>
										<td>					
												<h:commandLink title="Visualizar Ação"  action="#{ projetoBase.view }" style="border: 0;">
											       <f:param name="id" value="#{sol.projeto.id}"/>
									               <h:graphicImage url="/img/view.gif" />
												</h:commandLink>
										</td>
										<td>
												<h:commandLink title="Visualizar Orçamento" action="#{ projetoBase.viewOrcamento }" immediate="true">
												   <f:param name="id" value="#{sol.projeto.id}"/>
											       <h:graphicImage url="/img/projeto/orcamento.png" width="16" height="16" />
												</h:commandLink>
										</td>
										<td>
												<h:commandLink title="Analisar Solicitação" action="#{ solicitacaoReconsideracao.iniciarAnalisarSolicitacaoProjeto }" style="border: 0;" rendered="#{acesso.comissaoIntegrada}">
											       <f:param name="id" value="#{sol.id}"/>
									               <h:graphicImage url="/img/seta.gif" />
												</h:commandLink>
										</td>
					              </tr>
					          </c:forEach>
						</c:if>
			          	<c:if test="${empty solicitacoes}">
			          		<tr>
								<td colspan="5"><center><i> Nenhuma Solicitação pendendente de validação </i></center></td>
							</tr>
						</c:if>
				 	</tbody>
				 </table>
			</h:form>
		
			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>