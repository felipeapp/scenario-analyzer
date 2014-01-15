<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Alterar Planos de Trabalho</h2>

	
<h:form>

	<div class="infoAltRem">
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Plano de Trabalho		    		    
		    <h:graphicImage value="/img/pesquisa/indicar_bolsista.gif" style="overflow: visible;"/>: Indicar/Substituir Discente
	</div>

		<table class=listagem>
				<caption class="listagem"> Lista de Planos de Trabalho de Ações Integradas Coordenadas pelo Usuário Atual</caption>
				<thead>
						<tr>
							<th>Discente</th>
							<th>Vínculo</th>
							<th>Situação</th>
							<th style="text-align: center; ">Período</th>
							<th></th>							
							<th></th>							
							<th></th>
						</tr>
					</thead>
					<tbody>
						
						<c:set var="projeto" value=""/>
						<c:forEach items="#{planoTrabalhoProjeto.planosCoordenadorLogado}" var="item" varStatus="status">						
						
							<c:if test="${ projeto != item.projeto.id }">
								<c:set var="projeto" value="${ item.projeto.id }"/>
								<tr>
									<td colspan="8" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
										<h:outputText value="#{item.projeto.anoTitulo}" /> 
									</td>
								</tr>
							</c:if>		
						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>
									 	<h:outputText value="#{item.discenteProjeto.discente.nome}" />
									</td>
									<td>
										<h:outputText value="#{item.discenteProjeto.tipoVinculo.descricao}" />
									</td>
									<td>
										<h:outputText value="#{item.discenteProjeto.situacaoDiscenteProjeto.descricao}" />
									</td>
									<td style="text-align: center;">
										<h:outputText value="#{item.dataInicio}">
											<f:convertDateTime type="date" dateStyle="medium"/>
										</h:outputText> a 
										<h:outputText value="#{item.dataFim}">
											<f:convertDateTime type="date" dateStyle="medium"/>
										</h:outputText>
									</td>
									<td  width="2%">								               
										<h:commandLink action="#{planoTrabalhoProjeto.view}" style="border: 0;">
										   <f:param name="id" value="#{item.discenteProjeto.id}"/>
										   <f:param name="idPlano" value="#{item.id}"/>
								           <h:graphicImage url="/img/view.gif" title="Visualizar Plano de Trabalho" />
										</h:commandLink>
									</td>

									<td width="2%">
										<h:commandLink action="#{planoTrabalhoProjeto.iniciarIndicarDiscente}" style="border: 0;" id="indicar" >
									       <f:param name="id" value="#{item.id}"/>
							               <h:graphicImage url="/img/pesquisa/indicar_bolsista.gif" title="Indicar/Substituir Discente" />
										</h:commandLink>
									</td>		
																
							</tr>
			 		   </c:forEach>
			 		   
			 		   <c:if test="${empty planoTrabalhoProjeto.planosCoordenadorLogado}" >
			 		   		<tr><td colspan="5" align="center"><font color="red">Não há planos de trabalhos cadastrados!</font></td></tr>
			 		   </c:if>
			 		   
					</tbody>	
					
		</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>