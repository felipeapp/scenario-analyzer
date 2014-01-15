<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> > Analisar Solicita��es de Reconsidera��o</h2>
	<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;" id="gifview"/>: Visualizar A��o
		    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" id="gifseta"/>: Analisar Solicita��o		    		    
	</div>
	<br/>
	<c:set var="solicitacoes" value="#{solicitacaoReconsideracao.solicitacoesPendentes}"/>

		<h:form id="form">
				 <table class="listagem">
				    <caption>Solicita��es pendentes de valida��o (${ fn:length(solicitacoes) })</caption>
			
				      <thead>
				      	<tr>
				        	<th width="50%">Ano - T�tulo</th>
				        	<th>Tipo</th>
				        	<th>Unidade</th>
				        	<th>Data da submiss�o</th>
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
											<h:commandLink title="Visualizar A��o"  action="#{ atividadeExtensao.view }" style="border: 0;" id="LnkViewAcao">
										       <f:param name="id" value="#{sol.atividade.id}"/>
								               <h:graphicImage url="/img/view.gif" />
											</h:commandLink>
										</td>
										<td>
											<h:commandLink title="Analisar Solicita��o" 
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
					<center><i> Nenhuma solicita��o pendendente de valida��o </i></center>
				</c:if>				 
		</h:form>
		
			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>