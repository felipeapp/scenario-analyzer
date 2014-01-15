<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>
<%@page import="br.ufrn.sigaa.monitoria.dominio.TipoAvaliacaoMonitoria"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>	
	
	<h:outputText value="#{projetoMonitoria.create}"/>
	<h:outputText value="#{avalProjetoMonitoria.create}"/>
	
	<h:messages/>
	<h2><ufrn:subSistema /> > Consultar Avaliadores do Projeto</h2>


	<div class="infoAltRem">
   	    <h:graphicImage value="/img/monitoria/document_view.png" style="overflow: visible;"/>: Visualizar Detalhes da Avaliação
	</div>
	
	<h:form>
	<table class="formulario" width="100%" id="avalidoresDoProjeto">
			<caption>Lista de Avaliadores do Projeto</caption>

				<tr>
					<td class="subFormulario">${projetoMonitoria.obj.anoTitulo}</td>
				</tr>

				<tr>
					<td>
						<table width="100%" class="subFormulario">

								<thead id="headLista">
						  		   	<tr>
						  		   		<td>Tipo de Avaliação</td>
						  		   		<td>Nota</td>				  		   				  		   		
						  		   		<td>Situação</td>
						  		   		<td></td>		  		   		
								  	</tr>
								</thead>
				
								<tbody>
									<c:set var="avaliacoes" value="#{projetoMonitoria.obj.avaliacoes}" />
														
									<c:if test="${not empty avaliacoes}">
									
										<c:set var="AVALIACAO_CANCELADA" value="<%= String.valueOf(StatusAvaliacao.AVALIACAO_CANCELADA) %>" scope="application"/>
										<c:set var="AVALIADO" value="<%= String.valueOf(StatusAvaliacao.AVALIADO) %>" scope="application"/>												
										<c:set var="AVALIACAO_PROJETO_ENSINO" value="<%= String.valueOf(TipoAvaliacaoMonitoria.AVALIACAO_PROJETO_ENSINO) %>" scope="application"/>												
										
										<c:forEach items="#{avaliacoes}" var="avaliacao" varStatus="status">
										
														<c:if test="${(avaliacao.statusAvaliacao.id != AVALIACAO_CANCELADA)}">
														
															<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">
															
																<td><c:out value="${avaliacao.tipoAvaliacao.descricao}" /></td>															
																<td>
																	<c:if test="${avaliacao.tipoAvaliacao.id == AVALIACAO_PROJETO_ENSINO}">
																		<fmt:formatNumber pattern="#0.00" value="${avaliacao.notaAvaliacao}" />
																	</c:if>																		
																	<c:if test="${avaliacao.tipoAvaliacao.id != AVALIACAO_PROJETO_ENSINO}">--</c:if>																																			
																</td>																											
																
																<td>
																	<font color="<c:out value="${(avaliacao.statusAvaliacao.id == AVALIADO) ?'blue':'black'}" escapeXml="false" />" >	
																	<c:out value="${((avaliacao.statusAvaliacao.id != AVALIADO) and (avaliacao.statusAvaliacao.id != AVALIACAO_CANCELADA)) ?'<font color=black>':''}" escapeXml="false" />	
																		<c:out value="${avaliacao.statusAvaliacao.descricao}" />
																	</font>
																</td>
																<td>															
																   <c:if test="${not empty avaliacao.dataAvaliacao}">																			
																			<h:commandLink  title="Ver Avaliacao" action="#{avalProjetoMonitoria.view}" style="border: 0;">
																				   	<f:param name="idAvaliacao" value="#{avaliacao.id}"/>				    	
																					<h:graphicImage url="/img/monitoria/document_view.png" />
																			</h:commandLink>
																	</c:if>				
																</td>
				
															</tr>
															
														</c:if>
												
										</c:forEach>
									</c:if>
								</tbody>
						   </table>
					</td>
				</tr>
				<tfoot>
					<tr>
						<td width="20%" class="voltar"><input type="button" onclick="javascript:history.back();" value="<< Voltar"></td>
					</tr>				
				</tfoot>
				
		</table>
</h:form>

	   <c:if test="${not empty projetoMonitoria.obj.parecerAvaliacaoFinal}">
			<table class="formulario" width="100%">
						<caption>Avaliação Final da PROGRAD</caption>
						<tr><td width="10%"><b>Média:</b></td><td><h:outputText value="#{ projetoMonitoria.obj.mediaAnalise }"><f:convertNumber pattern="#0.00"/></h:outputText></td></tr>
						<tr>
						<td width="10%"><b>Parecer:</b></td>
							<td>
								<h:outputText value="#{projetoMonitoria.obj.parecerAvaliacaoFinal}" /></td>		
							</td>
						</tr>
			</table>							
		</c:if>				


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>