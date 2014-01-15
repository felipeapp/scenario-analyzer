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
	
				Para cada ação acadêmica listada abaixo você pode cadastrar uma solicitação de reconsideração caso 
				discorde da avaliação realizada pelos membros do Comitê integrado de Ensino, Pesquisa e Extensão (CIEPE). 
				Se o ícone para realização da solicitação não estiver presente em seu projeto, verifique se ele possui as seguintes características:<br/>
				
				<ul>
				    <li>Está sob sua coordenação.</li>
				    <li>Possui status igual a APROVADO COM RECURSO, APROVADO SEM RECURSO ou REPROVADO.</li>
				    <li>O prazo para solicitação não expirou.</li>
				</ul>
			</div>



			<div class="infoAltRem">
			    <h:graphicImage value="/img/arrow_undo.png"style="overflow: visible;"/>: Solicitar Reconsideração
			    <h:graphicImage value="/img/extensao/financiamento_faex.png" width="20px" height="20px" style="overflow: visible;"/>: Orçamento Aprovado
			    <h:graphicImage value="/img/view2.gif"style="overflow: visible;"/>: Listar Avaliações
			    <h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>: Listar Solicitações
			</div>
			<br>
			
			<h:form>

			<table class="listagem">
			    <caption>Lista de ações passíveis de solicitação de reconsideração</caption>

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

				<c:set var="lista" value="#{solicitacaoReconsideracao.projetosReconsideraveis}" />

			       <tbody>
				<c:if test="${empty lista}">
	                    <tr> <td colspan="5" align="center"> <font color="red">Não há ações acadêmicas para reconsideração <br/> ou o usuário atual não é Coordenador de ações ativas</font> </td></tr>
				</c:if>

				        <c:forEach items="#{lista}" var="projeto" varStatus="status">

					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

											<td style="text-align:right;" width="4%"> ${projeto.ano} </td>
						                    <td width="50%"> ${projeto.titulo} </td>
						                    <td width="20%"> ${projeto.situacaoProjeto.descricao} </td>
						                    <td style="text-align:center;"> 
						                    	<fmt:formatDate value="${projeto.edital.dataFimReconsideracao}" pattern="dd/MM/yyyy"/>
						                    	<h:outputText value="--" rendered="#{empty projeto.edital.dataFimReconsideracao}" /> 
						                    </td>
											<td width="1%">
												<h:commandLink title="Solicitar Reconsideração" 
													action="#{solicitacaoReconsideracao.solicitarReconsideracaoProjeto}"
													rendered="#{projeto.permitidoSolicitarReconsideracao}" 
													style="border: 0;">
												   	<f:param name="id" value="#{projeto.id}"/>				    	
													<h:graphicImage url="/img/arrow_undo.png"  />
												</h:commandLink>													
											</td>											
											<td width="1%">
												<h:commandLink title="Orçamento Aprovado" action="#{ projetoBase.viewOrcamento }" immediate="true">
											        <f:param name="id" value="#{projeto.id}"/>
													<h:graphicImage url="/img/projeto/orcamento.png" width="16" height="16" />
												</h:commandLink>
											</td>
											<td>
												<h:commandLink title="Listar Avaliações" action="#{ avaliacaoProjetoBean.listarAvaliacoesProjeto }" immediate="true" rendered="#{projeto.permitidoVisualizarAvaliacao}">
													<f:param name="id" value="#{projeto.id}"/>
										    		<h:graphicImage url="/img/view2.gif"/>
												</h:commandLink>											
											</td>											
											<td width="1%">
													<h:commandLink title="Listar Solicitações" action="#{solicitacaoReconsideracao.visualizarSolicitacoesProjeto}"
														rendered="#{not empty projeto.solicitacoesReconsideracao}" 
														style="border: 0;">
													   	<f:param name="id" value="#{projeto.id}"/>				    	
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