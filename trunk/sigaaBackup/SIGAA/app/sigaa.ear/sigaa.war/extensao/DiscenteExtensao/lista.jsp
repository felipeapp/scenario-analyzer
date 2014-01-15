<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.arq.seguranca.sigaa.SipacPapeis"%>


<f:view>

	<c:if test="${!acesso.extensao}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	
	<h2><ufrn:subSistema /> > Finalizar Discentes de Extensão</h2>
	
	<%@include file="/extensao/DiscenteExtensao/_busca.jsp" %>
	
	<c:if test="${not empty discentes}">
		<div class="infoAltRem">
		    <h:graphicImage value="/img/extensao/user1_view.png" style="overflow: visible;"/>: Visualizar Dados do Discente
		    <h:graphicImage value="/img/report.png" style="overflow: visible;"/>: Visualizar Plano de Trabalho
		    <ufrn:checkRole papeis="<%= new int[] {	SipacPapeis.GESTOR_BOLSAS_LOCAL	} %>">
		    	<h:graphicImage value="/img/extensao/user1_delete.png" style="overflow: visible;"/>: Finalizar Discente
		    </ufrn:checkRole>
		</div>	
	
		<br/>
	</c:if>
	


	<c:if test="${not empty discentes}">

		<h:form id="formLista">

			 <table class="listagem">
			    <caption>Discentes Encontrados (${ fn:length(discentes) })</caption>
		
			      <thead>
			      	<tr>
			        	<th>Discente</th>
			        	<th>Curso</th>
			        	<th>Vínculo</th>
			        	<th>Situação</th>		        	
			        	<th>Início</th>
			        	<th>Fim</th>
			        	<th>&nbsp;</th>		        
			        	<th>&nbsp;</th>
			        	<th>&nbsp;</th>
			        </tr>
			 	</thead>
			 	<tbody>
	
			 	<c:set var="atividade" value=""/>
		       	<c:forEach items="#{discentes}" var="de" varStatus="status">
		
			       			<c:if test="${ atividade != de.planoTrabalhoExtensao.atividade.id }">
								<c:set var="atividade" value="${ de.planoTrabalhoExtensao.atividade.id }"/>
								<tr>
										<td colspan="10" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
											${de.planoTrabalhoExtensao.atividade.codigoTitulo}
										</td>
								</tr>						
							</c:if>
		
		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		                    
		                    <td> ${de.discente.matriculaNome}</td>
		                    <td> <c:if test="${not empty discenteExtensao.obj.discente.curso}">${de.discente.curso.nomeCompleto}</c:if> </td>
							<td> ${de.tipoVinculo.descricao} </td>
							<td> ${de.situacaoDiscenteExtensao.descricao} </td>
							<td> <fmt:formatDate value="${de.dataInicio}" pattern="dd/MM/yyyy"/> </td>
							<td> <fmt:formatDate value="${de.dataFim}" pattern="dd/MM/yyyy"/> </td>
							
							<td width="2%">
								<h:commandLink title="Visualizar Discente" action="#{ discenteExtensao.view }" id="ver_discente">
								      <f:param name="idDiscenteExtensao" value="#{de.id}"/>
								      <h:graphicImage url="/img/extensao/user1_view.png" />
								</h:commandLink>
							</td>
							<td  width="2%">
									<h:commandLink action="#{planoTrabalhoExtensao.view}" id="ver_plano"
									title="Visualizar Plano de Trabalho" style="border: 0;" rendered="#{not empty de.planoTrabalhoExtensao}">
								       <f:param name="id" value="#{de.planoTrabalhoExtensao.id}"/>
						               <h:graphicImage url="/img/report.png" />
									</h:commandLink>
							</td>
							<td width="2%">
								<ufrn:checkRole papeis="<%= new int[] {	SipacPapeis.GESTOR_BOLSAS_LOCAL	} %>">			
									<h:commandLink action="#{planoTrabalhoExtensao.iniciarFinalizarDiscente}" id="finalizar_discente" rendered="#{!de.finalizado && not empty de.discente.nome && de.planoTrabalhoExtensao.enviado}">
									      <f:param name="idDiscenteExtensao" value="#{de.id}"/>
									      <f:param name="origemAcesso" value="/extensao/DiscenteExtensao/lista.jsp"/>
							              <t:graphicImage value="/img/extensao/user1_delete.png" title="Finalizar Discente" style="border: 0;"/>
									</h:commandLink>
								</ufrn:checkRole>							
							</td>
		              </tr>		              
		          </c:forEach>
			 	</tbody>
			 </table>

		</h:form>
	
	</c:if>
	

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>