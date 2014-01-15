<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>

<f:view>

	<h:outputText value="#{atividadeExtensao.create}"/>
	<h:outputText value="#{avaliacaoAtividade.create}"/>
	
	<h2><ufrn:subSistema /> > Consultar Avaliadores da Ação de Extensão</h2>
	<br>


	<%@include file="/extensao/form_busca_atividade.jsp"%>

<br/>

	<c:set var="atividades" value="#{atividadeExtensao.atividadesLocalizadas}"/>

	<center>
	<h:form id="formAtividadesCombo">
		<table class="formulario" width="100%">
			<caption>Ações de extensão localizadas (${ fn:length(atividades) })</caption>
	
			<tr>			
				<td>
				 	<h:selectOneListbox id="comboAtividades" value="#{atividadeExtensao.obj.id}"
						valueChangeListener="#{atividadeExtensao.changeAtividadeExtensao}" onchange="submit()" 
						size="6" style="width: 740px">	
						<f:selectItems value="#{atividadeExtensao.atividadesLocalizadasCombo}"/>
					</h:selectOneListbox>
				</td>
			</tr>
			
		</table>
	</h:form>
	</center>
	<br>
	
	
	<div class="infoAltRem">
   	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Avaliação
	</div>
	
	<h:form id="formAtividadesLocalizadas">
	<table class="subFormulario" width="100%" id="avalidoresDoProjeto">
			<caption>Lista de Avaliadores da Ação</caption>
				<thead id="headLista">
		  		   	<tr>
		  		   		<td>Avaliador(a)</td>
		  		   		<td>Depto.</td>				  		   		
		  		   		<td>Nota</td>				  		   				  		   		
						<td>Aprovado</td>		  		   		
		  		   		<td>Situação</td>
		  		   		<td></td>		  		   		
				  	</tr>
				  </thead>

					<c:set var="avaliacoes" value="#{atividadeExtensao.obj.avaliacoes}" />
					
					<c:if test="${not empty avaliacoes}">
					
						<c:set var="AVALIACAO_CANCELADA" value="<%= String.valueOf(StatusAvaliacao.AVALIACAO_CANCELADA) %>" scope="application"/>
						<c:set var="AVALIADO" value="<%= String.valueOf(StatusAvaliacao.AVALIADO) %>" scope="application"/>												
						
						
						<c:if test="${(not empty atividadeExtensao.atividadesLocalizadasCombo)}">
						<c:forEach items="#{avaliacoes}" var="avaliacao" varStatus="status">
						
								<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">
																				
									<td><c:out value="${avaliacao.avaliador.nome}"/></td>
									<td><c:out value="${avaliacao.avaliador.unidade.sigla}"/></td>															
									<td><c:out value="${avaliacao.nota}" /></td>
									<td><c:out value="${avaliacao.parecer.descricao}"/></td>
																																				
									<td>
											<c:out value="${(avaliacao.statusAvaliacao.id == AVALIACAO_CANCELADA) ?'<font color=red>':''}" escapeXml="false" />	
											<c:out value="${(avaliacao.statusAvaliacao.id == AVALIADO) ?'<font color=blue>':''}" escapeXml="false" />	
											<c:out value="${((avaliacao.statusAvaliacao.id != AVALIADO) and (avaliacao.statusAvaliacao.id != AVALIACAO_CANCELADA)) ?'<font color=black>':''}" escapeXml="false" />	
												<c:out value="${avaliacao.statusAvaliacao.descricao}" />
											</font>
									</td>
									<td>															
											<h:commandLink id="visualizarAvaliacao"  title="Ver Avaliacao" action="#{avaliacaoAtividade.view}" style="border: 0;"
											 rendered="#{not empty avaliacao.dataAvaliacao}">
											   	<f:param name="idAvaliacao" value="#{avaliacao.id}"/>				    	
												<h:graphicImage url="/img/view.gif"/>
											</h:commandLink>
									</td>
								</tr>
								
						</c:forEach>
						</c:if>
					</c:if>
					<c:if test="${(not empty atividadeExtensao.atividadesLocalizadasCombo) and (empty atividadeExtensao.obj.avaliacoes)}">
							<tr>
								<td colspan="6" align="center"><font color="red">Atualmente, não há avaliadores para esta ação de extensão.</font></td>
							</tr>									
					</c:if>
								
					<c:if test="${empty atividadeExtensao.atividadesLocalizadasCombo}">
						<tr>
							<td colspan="6" align="center"><font color="red">Nenhuma ação selecionada!</font></td>
						</tr>									
					</c:if>
		</table>
		
</h:form>
	</tbody>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>