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
	
				Para cada ação de extensão listada abaixo você pode cadastrar uma solicitação de reconsideração caso 
				discorde da avaliação realizada pelos membros do comitê de extensão. 
				Se o icone para realização da solicitação não estiver presente para sua ação de extensão, verifique se ela possui as seguintes características:<br/>
				
				<ul>
				    <li>Se está sob sua coordenação.</li>
				    <li>Se possui status igual a APROVADO COM RECURSOS, APROVADO SEM RECURSOS ou NÃO APROVADA.</li>
				    <li>Se o prazo para solicitação não expirou.</li>
				</ul>
			</div>

			<div class="infoAltRem">
			    <h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;"/>: Solicitar Reconsideração
			    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Ação de Extensão
			    <h:graphicImage value="/img/extensao/financiamento_faex.png" width="16px" height="16px" style="overflow: visible;"/>: Visualizar Orçamento Aprovado
			    <h:graphicImage url="/img/table_go.png" style="overflow: visible;"/>: Listar Avaliações
			    <h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>: Listar Solicitações
			</div>
			<br>
			
			<h:form>

			<table class="listagem">
			    <caption>Lista de ações passíveis de solicitação de reconsideração</caption>

			      <thead>
			      	<tr>
			      		<th width="8%">Código</th>
			        	<th>Título da ação</th>
			        	<th>Situação</th>
			        	<th>Prazo para Solicitação</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>
			        </tr>
			        

				<c:set var="lista" value="#{solicitacaoReconsideracao.acoesExtensaoReconsideraveis}" />

			        <tbody>
				<c:if test="${empty lista}">
	                    <tr> <td colspan="6" align="center"> <font color="red">Não há ações de extensão passivas de reconsideração <br/> ou o usuário atual não é coordenador de ações ativas</font> </td></tr>
				</c:if>

				        <c:forEach items="#{lista}" var="atividade" varStatus="status">

					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

											<td width="5%"> ${atividade.codigo} </td>
						                    <td width="60%"> ${atividade.titulo} </td>
						                    <td  width="20%"> ${atividade.situacaoProjeto.descricao} </td>
						                    <td> 
						                    	<fmt:formatDate value="${atividade.editalExtensao.edital.dataFimReconsideracao}" pattern="dd/MM/yyyy"/>
						                    	<h:outputText value="--" rendered="#{empty atividade.editalExtensao.edital.dataFimReconsideracao}" /> 
						                    </td>
											<td width="2%">
													<h:commandLink title="Solicitar Reconsideração" action="#{solicitacaoReconsideracao.solicitarReconsideracaoExtensao}"
														rendered="#{atividade.projeto.permitidoSolicitarReconsideracao}" 
														style="border: 0;">
													   	<f:param name="id" value="#{atividade.id}"/>				    	
														<h:graphicImage url="/img/arrow_undo.png"  />
													</h:commandLink>													
											</td>
											<td>
													<h:commandLink id="visualizaAcao" title="Visualizar Ação de Extensão" action="#{ atividadeExtensao.view }" immediate="true">
														<f:param name="id" value="#{atividade.id}" />
													    <h:graphicImage url="/img/view.gif" />
													</h:commandLink>
											</td>
											<td width="2%">		
													<h:commandLink title="Visualizar Orçamento Aprovado" action="#{atividadeExtensao.view}" immediate="true">
														<f:param name="id" value="#{atividade.id}"/>
						   								<f:param name="orcamentoAprovado" value="true"/>
													    <h:graphicImage url="/img/extensao/financiamento_faex.png" width="16px" height="16px" />
													</h:commandLink>
											</td>
											<td width="2%">
													<h:commandLink id="visualizarAvaliacoes" title="Listar Avaliações" action="#{ atividadeExtensao.iniciarVisualizarAvaliacaoAtividade }" immediate="true" rendered="#{atividade.projeto.permitidoVisualizarAvaliacao}">
												        <f:param name="id" value="#{atividade.id}"/>
											    		<h:graphicImage url="/img/table_go.png" />
													</h:commandLink>
											</td>
											<td width="2%">
													<h:commandLink title="Listar Solicitações" action="#{solicitacaoReconsideracao.visualizarSolicitacoesExtensao}"
														rendered="#{not empty atividade.solicitacoesReconsideracao}" 
														style="border: 0;" immediate="true">
													   	<f:param name="id" value="#{atividade.id}"/>				    	
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