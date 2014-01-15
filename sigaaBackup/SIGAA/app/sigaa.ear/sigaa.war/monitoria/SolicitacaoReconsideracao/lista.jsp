<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>


	<jsp:useBean id="hoje" class="java.util.Date"/>
	<c:set var="hoje" scope="request">
		<fmt:formatDate value="${hoje}" type="DATE" pattern="yyyy-MM-dd"/>
	</c:set>
	
	<h2><ufrn:subSistema /> > Solicitação de Reconsideração da Avaliação</h2>

			<div class="descricaoOperacao">
				<b>Bem-vindo ao cadastro de solicitações de reconsideração.</b> <br/><br/>
	
				Para cada Projeto de Monitoria listado abaixo você pode cadastrar uma solicitação de reconsideração caso 
				discorde da avaliação realizada pelos membros do Comitê de Monitoria. 
				Se o ícone para realização da solicitação não estiver presente em seu projeto, verifique se ele possui as seguintes características:<br/>
				
				<ul>
				    <li>Está sob sua coordenação.</li>
				    <li>Possui status igual a RECOMENDADO ou NÃO RECOMENDADO.</li>
				    <li>O prazo para solicitação não expirou.</li>
				</ul>
			</div>



			<div class="infoAltRem">
			    <h:graphicImage value="/img/arrow_undo.png"style="overflow: visible;"/>: Solicitar Reconsideração
			    <h:graphicImage value="/img/view2.gif"style="overflow: visible;"/>: Listar Avaliações
			    <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>: Listar Solicitações
			</div>
			<br>
			
			<h:form>

			<table class="listagem">
			    <caption>Lista de projetos passíveis de solicitação de reconsideração</caption>

			      <thead>
			      	<tr>
			      		<th style="text-align:right;">Ano</th>
			        	<th>Título</th>
			        	<th>Situação</th>
			        	<th style="text-align:center;">Prazo para Solicitação</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>
			     		<th>&nbsp;</th>
			     		<th>&nbsp;</th>
			        </tr>
			      </thead> 

				<c:set var="lista" value="#{solicitacaoReconsideracao.projetosMonitoriaReconsideraveis}" />

			       <tbody>
						<c:if test="${empty lista}">
	    	                <tr> <td colspan="5" align="center"> <font color="red">Não há projetos para reconsideração <br/> ou o usuário atual não é Coordenador de Projetos de Monitoria ativos.</font> </td></tr>
						</c:if>

				        <c:forEach items="#{lista}" var="pm" varStatus="status">

					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

											<td style="text-align:right;" width="4%"> ${pm.ano} </td>
						                    <td width="50%"> ${pm.titulo} </td>
						                    <td width="20%"> ${pm.situacaoProjeto.descricao} </td>
						                    <td style="text-align:center;"> 
						                    	<fmt:formatDate value="${pm.editalMonitoria.edital.dataFimReconsideracao}" pattern="dd/MM/yyyy"/>
						                    	<h:outputText value="--" rendered="#{empty pm.editalMonitoria.edital.dataFimReconsideracao}" /> 
						                    </td>
											<td width="1%">
												<h:commandLink title="Solicitar Reconsideração" 
													action="#{solicitacaoReconsideracao.solicitarReconsideracaoMonitoria}"
													rendered="#{pm.projeto.permitidoSolicitarReconsideracao}" style="border: 0;">
												   	<f:param name="id" value="#{pm.id}"/>				    	
													<h:graphicImage url="/img/arrow_undo.png"  />
												</h:commandLink>													
											</td>											
											<td>
												<h:commandLink title="Listar Avaliações" action="#{ projetoMonitoria.viewAvaliacoes }" immediate="true" rendered="#{pm.projeto.permitidoVisualizarAvaliacao}">
													<f:param name="id" value="#{pm.id}"/>
										    		<h:graphicImage url="/img/view2.gif"/>
												</h:commandLink>											
											</td>											
											<td width="1%">
													<h:commandLink title="Listar Solicitações" action="#{solicitacaoReconsideracao.visualizarSolicitacoesMonitoria}"
														rendered="#{not empty pm.projeto.solicitacoesReconsideracao}" 
														style="border: 0;">
													   	<f:param name="id" value="#{pm.id}"/>				    	
														<h:graphicImage url="/img/listar.gif"  />
													</h:commandLink>													
											</td>
					              </tr>

				        </c:forEach>
				</tbody>

		</table>
		</h:form>

<br/>
<br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>