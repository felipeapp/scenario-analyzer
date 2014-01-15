
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


	<h:form id="formAtividade">	
		<!-- Ações -->
		<c:if test="${not empty recuperarAcoesAssociadas.projetos}">
			<div class="infoAltRem">
				<h:graphicImage value="/img/refresh.png" style="overflow: visible;"/>: Recuperar Ação
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Ação	    			    
			</div>
			<br />
			<c:set value="#{recuperarAcoesAssociadas.totalAt}" scope="page" var="_total" />
			<table class="listagem">
				<caption class="listagem">Lista das Ações Integradas (${_total}) </caption>
				<thead>
					<tr>
						<th width="8%">Nº Inst.</th>
						<th width="80%">Título</th>						
						<th>Unidade</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
				
				<c:forEach items="#{recuperarAcoesAssociadas.projetos}" var="acao" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						 
						<td> <fmt:formatNumber value="${acao.numeroInstitucional}"  pattern="0000"/>/${acao.ano}</td>
						<td> 
							${acao.titulo} 
							<br />
							<i>COORDENADOR: ${acao.coordenador.pessoa.nome}</i> 
						</td>			
						<td>${acao.unidade.sigla} </td>
						<td> 
							<h:commandLink id="recuperaAcao" title="Recuperar Ação"
					 			onclick="return confirm('Tem certeza que deseja Recuperar esta Ação Integrada?');"
					 			action="#{recuperarAcoesAssociadas.recuperarAcao}" >
									<f:param name="id" value="#{acao.id}" />
				    				<h:graphicImage value="/img/refresh.png" style="overflow: visible;"/>
							</h:commandLink> 
						</td>
						<td>
							<h:commandLink id="visualizaAcao" title="Visualizar Ação" action="#{ projetoBase.view }" immediate="true">
								        <f:param name="id" value="#{acao.id}"/>
							    		<h:graphicImage url="/img/view.gif" />
							</h:commandLink>
						</td>
				</c:forEach>
			</tbody>
		</table>
		
		<br/>
		<br/>
		</c:if>
		<!-- FIM DAS Ações -->
		
		
			
			
		<c:if test="${empty recuperarAcoesAssociadas.projetos}">
			<center><font color='red'>Nenhuma ação integrada encontrada com os dados passados.</font></center>
		</c:if>
		
		
			
</h:form>

