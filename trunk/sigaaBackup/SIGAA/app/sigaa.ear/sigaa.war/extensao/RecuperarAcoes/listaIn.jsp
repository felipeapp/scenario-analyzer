
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


	<h:form id="formAtividade">	
		<!-- Ações -->
		<c:if test="${not empty recuperarAcoes.atividades}">
			<div class="infoAltRem">
				<h:graphicImage value="/img/refresh.png" style="overflow: visible;"/>: Recuperar Ação
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar	    			    
			</div>
			<br />
			<c:set value="#{recuperarAcoes.totalAt}" scope="page" var="_total" />
			<table class="listagem">
				<caption class="listagem">Lista das Ações de Extensão (${_total}) </caption>
				<thead>
					<tr>
						<th width="10%">Código</th>
						<th width="70%">Título</th>
						<th>Tipo Ação</th>								
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
				
				<c:forEach items="#{recuperarAcoes.atividades}" var="atv" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						 
						<td> ${atv.codigo} </td>
						<td> 
							${atv.titulo} 
							<br />
							<i>COORDENADOR: ${atv.projeto.coordenador.pessoa.nome}</i> 
						</td>			
						<td> ${atv.tipoAtividadeExtensao.descricao} </td>
						<td> 
							<h:commandLink id="recuperaAcao" title="Recuperar Ação"
					 			onclick="return confirm('Tem certeza que deseja Recuperar esta Ação de Extensão?');"
					 			action="#{recuperarAcoes.recuperarAcao}" >
									<f:param name="id" value="#{atv.id}" />
				    				<h:graphicImage value="/img/refresh.png" style="overflow: visible;"/>
							</h:commandLink> 
						</td>
						<td>
							<h:commandLink id="visualizaAcao" title="Visualizar Ação de Extensão" action="#{ atividadeExtensao.view }" immediate="true">
								        <f:param name="id" value="#{atv.id}"/>
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
		
		
			
			
		<c:if test="${empty recuperarAcoes.atividades}">
			<center><font color='red'>Nenhuma ação de extensão encontrada com os dados passados.</font></center>
		</c:if>
		
		
			
</h:form>

