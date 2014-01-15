<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	
	<h2><ufrn:subSistema /> > Analisar Solicita��es de Reconsidera��o</h2>
	<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;" id="gifview"/>: Visualizar A��o
   		    <h:graphicImage url="/img/projeto/orcamento.png" width="16" height="16" style="overflow: visible;" /> <h:outputText value=": Visualizar Or�amento" />
		    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" id="gifseta"/>: Analisar Solicita��o
	</div>
	<br/>
	<c:set var="solicitacoes" value="#{solicitacaoReconsideracao.solicitacoesPendentes}"/>

			<h:form>
				 <table class="listagem">
				    <caption>Solicita��es pendentes de valida��o (${ fn:length(solicitacoes) })</caption>
			
				      <thead>
				      	<tr>
				      		<th>N� Inst.</th>
				        	<th width="70%">T�tulo</th>
				        	<th>Unidade</th>
				        	<th style="text-align:center;"  width="20%">Data Submiss�o</th>
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
												<h:commandLink title="Visualizar A��o"  action="#{ projetoBase.view }" style="border: 0;">
											       <f:param name="id" value="#{sol.projeto.id}"/>
									               <h:graphicImage url="/img/view.gif" />
												</h:commandLink>
										</td>
										<td>
												<h:commandLink title="Visualizar Or�amento" action="#{ projetoBase.viewOrcamento }" immediate="true">
												   <f:param name="id" value="#{sol.projeto.id}"/>
											       <h:graphicImage url="/img/projeto/orcamento.png" width="16" height="16" />
												</h:commandLink>
										</td>
										<td>
												<h:commandLink title="Analisar Solicita��o" action="#{ solicitacaoReconsideracao.iniciarAnalisarSolicitacaoProjeto }" style="border: 0;" rendered="#{acesso.comissaoIntegrada}">
											       <f:param name="id" value="#{sol.id}"/>
									               <h:graphicImage url="/img/seta.gif" />
												</h:commandLink>
										</td>
					              </tr>
					          </c:forEach>
						</c:if>
			          	<c:if test="${empty solicitacoes}">
			          		<tr>
								<td colspan="5"><center><i> Nenhuma Solicita��o pendendente de valida��o </i></center></td>
							</tr>
						</c:if>
				 	</tbody>
				 </table>
			</h:form>
		
			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>