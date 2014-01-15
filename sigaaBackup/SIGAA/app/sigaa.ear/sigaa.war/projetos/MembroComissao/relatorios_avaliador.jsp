<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>

<f:view>
	<h:outputText value="#{membroComissao.create}"/>
	<h:outputText value="#{avalProjetoMonitoria.create}"/>
	
	<h:messages showDetail="true" showSummary="true"/>
	<h2><ufrn:subSistema /> > Consultar Projetos do Membro da Comissão</h2>
	<br>
	<h:form id="form">
	
		<table class="formulario" width="100%">
			<caption>Busca por Membros da Comissão</caption>
			
				<tr>
					<td>Selecione a Comissão:</td>
					<td>
						<h:selectOneMenu value="#{membroComissao.comissaoBusca}" style="width: 250px">
							<f:selectItems value="#{membroComissao.tiposMembro}" />
						</h:selectOneMenu>
						&nbsp;
						<h:commandButton action="#{membroComissao.buscar}" value="Buscar" />
	
					</td>
				</tr>
		</table>
	
		<br/>
			
		<center>
			<table class="formulario" width="100%">
				<caption class="listagem">Lista de Membros da Comissão Selecionada</caption>
				<tr>			
					<td>
					 	<h:selectOneListbox value="#{membroComissao.idMembroSelecionado}"
							valueChangeListener="#{membroComissao.changeMembroComissaoRelatorio}" onchange="submit()" 
							size="6" style="width: 100%" immediate="true">	
							<f:selectItems value="#{membroComissao.membrosCombo}"/>
						</h:selectOneListbox>
					</td>
				</tr>
			</table>
		</center>
		
		<br/>
		
		<c:set var="avaliacoes" value="#{membroComissao.avaliacoesRelatoriosMembroAtual}" />
		
		<c:if test="${not empty avaliacoes }">
	
			<div class="infoAltRem">
		   	    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Avaliação
		   	    <h:graphicImage value="/img/monitoria/form_blue.png" style="overflow: visible;"/>: Visualizar Relatório
			</div>
		
			<table class="formulario" width="100%">
				<caption class="listagem">Lista de projetos do Membro da Comissão Selecionado</caption>
					<thead id="headLista">
			  		   	<tr>
			  		   		<td>Ano</td>				  		   		
			  		   		<td>Projeto</td>		  		   		
			  		   		<td>Situação</td>
			  		   		<td></td>
			  		   		<td></td>
					  	</tr>
					 </thead>
					 <tbody>
							<c:set var="AVALIACAO_CANCELADA" value="<%= String.valueOf(StatusAvaliacao.AVALIACAO_CANCELADA) %>" scope="application"/>
							<c:set var="AVALIADO" value="<%= String.valueOf(StatusAvaliacao.AVALIADO) %>" scope="application"/>						
															
							<c:forEach items="#{ avaliacoes }" var="avaliacao" varStatus="status">
									<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">
										<td width="1%"><c:out value="${avaliacao.relatorioProjetoMonitoria.projetoEnsino.ano}" /></td>															
										<td width="70%"><c:out value="${avaliacao.relatorioProjetoMonitoria.projetoEnsino.titulo}"/></td>
										<td>
											<c:set var="cor" value="${(avaliacao.statusAvaliacao.id == AVALIACAO_CANCELADA) ? 'red' : (avaliacao.statusAvaliacao.id == AVALIADO) ? 'blue' : ((avaliacao.statusAvaliacao.id != AVALIADO) and (avaliacao.statusAvaliacao.id != AVALIACAO_CANCELADA)) ? 'black' : ''}" />	
											<font color="${cor}">	${avaliacao.statusAvaliacao.descricao} </font>																							
										</td>
										
										<td width="1%">															
										   <c:if test="${avaliacao.dataAvaliacao != null}">
												<h:commandLink title="Visualizar Avaliação" action="#{avalRelatorioProjetoMonitoria.view}" style="border: 0;">
												   	<f:param name="idAvaliacao" value="#{avaliacao.id}"/>
												     	<h:graphicImage url="/img/view.gif" id="view"/>
												</h:commandLink>	
											</c:if>														
										</td>
										
										<td width="1%">															
												<h:commandLink title="Visualizar Relatório" action="#{relatorioProjetoMonitoria.view}" style="border: 0;">
												   	<f:param name="id" value="#{avaliacao.relatorioProjetoMonitoria.id}"/>
												     	<h:graphicImage url="/img/monitoria/form_blue.png" id="view_"/>
												</h:commandLink>	
										</td>				
									</tr>
									
							</c:forEach>
													
					</tbody>
			</table>
		
		</c:if>
		
		<c:if test="${(empty avaliacoes) and (not empty membroComissao.membrosCombo)}">
							<table align="center">
								<tr>
									<td align="center" width="100%"><font color="red">Atualmente, este membro de comissão não está avaliando projetos!</font></td>
								</tr>
							</table>									
		</c:if>
		
		<c:if test="${empty membroComissao.membrosCombo}">
						<table align="center">
							<tr>
								<td align="center" width="100%"><font color="red">Nenhum Membro de Comissão selecionado!</font></td>
							</tr>
						</table>									
		</c:if>
		
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>