<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<%@page import="br.ufrn.sigaa.monitoria.dominio.StatusAvaliacao"%>

<f:view>

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
							valueChangeListener="#{membroComissao.changeMembroComissao}" onchange="submit()" 
							size="6" style="width: 100%">	
							<f:selectItems value="#{membroComissao.membrosCombo}"/>
						</h:selectOneListbox>
					</td>
				</tr>
			</table>
		</center>
		
		<br/>
	
		<div class="infoAltRem">
	   	    <h:graphicImage value="/img/view.gif" style="overflow: visible;" id="img_view"/>: Visualizar Avaliação
		</div>
	
	 	<c:set var="avaliacoes" value="#{membroComissao.avaliacoesDoMembroAtual}" />
	 	
		<table class="listagem tablesorter" id="listagem" width="100%">
			<caption class="listagem">Lista de projetos do Membro da Comissão Selecionado (${ fn:length(avaliacoes) })</caption>
				<thead>
		  		   	<tr>
		  		   		<th width="5%">Ano</th>				  		   		
		  		   		<th>Projeto</th>
		  		   		<th>Avaliado em</th>
		  		   		<th>Situação</th>
		  		   		<th></th>
				  	</tr>
				 </thead>
				 <tbody>
				 
					<c:if test="${not empty avaliacoes }">						
							
						<c:set var="AVALIACAO_CANCELADA" value="<%= String.valueOf(StatusAvaliacao.AVALIACAO_CANCELADA) %>" scope="application"/>
						<c:set var="AVALIADO" value="<%= String.valueOf(StatusAvaliacao.AVALIADO) %>" scope="application"/>						
														
						<c:forEach items="#{ avaliacoes }" var="aval" varStatus="status">
						
								<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">
									<td width="1%"><c:out value="${aval.projetoEnsino.ano}" /></td>															
									<td width="70%"><c:out value="${aval.projetoEnsino.titulo}"/></td>
									<td><h:outputText value="#{aval.dataAvaliacao}"/></td>
									<td>
										<c:set var="cor" value="${(aval.statusAvaliacao.id == AVALIACAO_CANCELADA) ? 'red' : (aval.statusAvaliacao.id == AVALIADO) ? 'blue' : ((aval.statusAvaliacao.id != AVALIADO) and (aval.statusAvaliacao.id != AVALIACAO_CANCELADA)) ? 'black' : ''}" />	
										<font color="${cor}">	${aval.statusAvaliacao.descricao} </font>																							
									</td>
									<td width="1%">															
									   <c:if test="${not empty aval.dataAvaliacao}">
												<h:commandLink title="Ver Avaliacao" action="#{avalProjetoMonitoria.view}" style="border: 0;">
											    	<f:param name="idAvaliacao" value="#{aval.id}"/>
											      	<h:graphicImage url="/img/view.gif" id="view_"/>
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
		<rich:jQuery selector="#listagem" query="tablesorter( {dateFormat: 'uk', headers: {2: { sorter: 'shortDate' }, 4: { sorter: false } } });" timing="onload" />
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>