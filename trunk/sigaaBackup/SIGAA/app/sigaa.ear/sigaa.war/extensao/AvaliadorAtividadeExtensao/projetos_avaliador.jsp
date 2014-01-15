<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>

<f:view>
	<h:outputText value="#{membroComissao.create}"/>
	<h:outputText value="#{avalProjetoMonitoria.create}"/>
	
	<h:messages showDetail="true" showSummary="true"/>
	<h2><ufrn:subSistema /> > Consultar Projetos do Membro da Comissão</h2>
	<br>
	<h:form>
	<table class="formulario" width="100%">
		<caption>Busca por Membros da Comissão</caption>
		
			<tr>
				<td>
				Selecione a Comissão:</td>
				<td>
					<:selectOneMenu value="#{membroComissao.comissaoBusca.id}" style="width: 250px">
						<f:selectItems value="#{membroComissao.tiposMembro}" />
					</h:selectOneMenu>
					<h:commandButton action="#{membroComissao.buscar}" value="Buscar" />
				</td>
			</tr>
			
	</table>
	</h:form>
	
	<br/>	
	
	<center>
	<h:form>
		<table class="formulario" width="100%">
			<caption class="listagem">Lista de Membros da Comissão Selecionada</caption>
	
			<tr>			
				<td>
				 	<h:selectOneListbox value="#{membroComissao.idMembroSelecionado}"
						valueChangeListener="#{membroComissao.changeMembroComissao}" onchange="submit()" 
						size="6" style="width: 740px" immediate="true">	
						<f:selectItems value="#{membroComissao.membrosCombo}"/>
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
	
	<h:form>
	<table class="formulario" width="100%">
		<caption class="listagem">Lista de projetos do Membro da Comissão Selecionado</caption>
	

			<thead id="headLista">
	  		   	<tr>
	  		   		<td>Projeto</td>
	  		   		<td>Ano</td>				  		   		
	  		   		<td>Situação</td>
	  		   		<td></td>
			  	</tr>
			 </thead>
			 <tbody>
			 
			 <c:set var="avaliacoes" value="#{membroComissao.avaliacoesDoMembroAtual}" />

					<c:if test="${not empty avaliacoes }">						
							
						<c:set var="AVALIACAO_CANCELADA" value="<%= String.valueOf(StatusAvaliacao.AVALIACAO_CANCELADA) %>" scope="application"/>
						<c:set var="AVALIADO" value="<%= String.valueOf(StatusAvaliacao.AVALIADO) %>" scope="application"/>						
														
						<c:forEach items="#{ avaliacoes }" var="avaliacao" varStatus="status">
						
											<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">
												<td><c:out value="${avaliacao.projetoEnsino.titulo}"/></td>
												<td><c:out value="${avaliacao.projetoEnsino.ano}" /></td>															
												<td>
													<c:set var="cor" value="${(avaliacao.statusAvaliacao.id == AVALIACAO_CANCELADA) ? 'red' : (avaliacao.statusAvaliacao.id == AVALIADO) ? 'blue' : ((avaliacao.statusAvaliacao.id != AVALIADO) and (avaliacao.statusAvaliacao.id != AVALIACAO_CANCELADA)) ? 'black' : ''}" />	
													<font color="${cor}">	${avaliacao.statusAvaliacao.descricao} </font>																									
												</td>
												<td>															
												   <c:if test="${not empty avaliacao.dataAvaliacao}">
															<h:commandLink title="Ver Avaliacao" action="#{avalProjetoMonitoria.view}" style="border: 0;">
														    	<f:param name="idAvaliacao" value="#{avaliacao.id}"/>
														      	<h:graphicImage url="/img/view.gif" />
															</h:commandLink>															
													</c:if>				
												</td>
																								
											</tr>
								
						</c:forEach>
					</c:if>
					<c:if test="${(empty avaliacoes) and (not empty membroComissao.membrosCombo)}">
							<tr>
								<td colspan="4" align="center"><font color="red">Atualmente, este membro de comissão não está avaliando projetos!</font></td>
							</tr>									
					</c:if>
					

					<c:if test="${empty membroComissao.membrosCombo}">
						<tr>
							<td colspan="4" align="center"><font color="red">Nenhum Membro de Comissão selecionado!</font></td>
						</tr>									
					</c:if>

			</tbody>
	</table>
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>