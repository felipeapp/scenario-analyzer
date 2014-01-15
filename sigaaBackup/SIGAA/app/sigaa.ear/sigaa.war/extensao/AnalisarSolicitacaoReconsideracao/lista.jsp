<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> > Analisar Solicitações de Reconsideração</h2>
	<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;" id="gifview"/>: Visualizar Ação
		    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" id="gifseta"/>: Analisar Solicitação		    		    
	</div>
	<br/>
	<c:set var="solicitacoes" value="#{solicitacaoReconsideracao.solicitacoesPendentes}"/>

		<h:form id="form">
				 <table class="listagem">
				    <caption>Solicitações pendentes de validação (${ fn:length(solicitacoes) })</caption>
			
				      <thead>
				      	<tr>
				        	<th width="50%">Ano - Título</th>
				        	<th>Tipo</th>
				        	<th>Unidade</th>
				        	<th>Data da submissão</th>
				        	<th width="2%">&nbsp;</th>
				        	<th width="2%">&nbsp;</th>
				        </tr>
				 	</thead>
				 	<tbody>				 	
						<c:if test="${not empty solicitacoes}">
						 	 <c:forEach items="#{solicitacoes}" var="sol" varStatus="status">
					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">					
					                    <td> ${sol.atividade.anoTitulo}</td>
					                    <td> ${sol.atividade.tipoAtividadeExtensao.descricao} </td>
					                    <td> ${sol.atividade.unidade.sigla} </td>
										<td> <fmt:formatDate value="${sol.dataSolicitacao}" pattern="dd/MM/yyyy HH:mm:ss" /> </td>
										<td>					
											<h:commandLink title="Visualizar Ação"  action="#{ atividadeExtensao.view }" style="border: 0;" id="LnkViewAcao">
										       <f:param name="id" value="#{sol.atividade.id}"/>
								               <h:graphicImage url="/img/view.gif" />
											</h:commandLink>
										</td>
										<td>
											<h:commandLink title="Analisar Solicitação" 
												action="#{ solicitacaoReconsideracao.iniciarAnalisarSolicitacaoExtensao }" 
												style="border: 0;" rendered="#{acesso.extensao}" id="BtnAnalisarSolicitacao">
										       <f:param name="id" value="#{sol.id}"/>
								               <h:graphicImage url="/img/seta.gif" />
											</h:commandLink>
										</td>										
					              </tr>
					          </c:forEach>
						</c:if>
				 	</tbody>
				 </table>
				 
			 	<c:if test="${empty solicitacoes}">
					<center><i> Nenhuma solicitação pendendente de validação </i></center>
				</c:if>				 
		</h:form>
		
			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>