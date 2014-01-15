<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<f:view>
	<h:form id="formAtividade">
	
		<h2><ufrn:subSistema /> &gt; A��es de Extens�o Removidas</h2>
		
		<!-- A��es -->
		<c:if test="${not empty recuperarAcoes.atividades}">
			<div class="infoAltRem">
				<h:graphicImage value="/img/refresh.png" style="overflow: visible;"/>: Recuperar A��o de Extens�o
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar	    			    
			</div>
			<br />
			<c:set value="#{recuperarAcoes.totalAt}" scope="page" var="_total" />
			<table class="listagem">
				<caption class="listagem">Lista das A��es de Extens�o (${_total}) </caption>
				<thead>
					<tr>
						<th>C�digo</th>
						<th>T�tulo</th>
						<th>Tipo A��o</th>								
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
				
				<c:forEach items="#{recuperarAcoes.atividades}" var="atividade" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						 
						<td> ${atividade.codigo} </td>
						<td> 
							${atividade.titulo} 
							<br />
							COORDENADOR: ${atividade.projeto.coordenador.pessoa.nome} 
						</td>			
						<td> ${atividade.tipoAtividadeExtensao.descricao} </td>
						<td> 
							<h:commandLink id="recuperaAcao" title="Recuperar A��o"
					 			onclick="return confirm('Tem certeza que deseja Recuperar esta A��o de Extens�o?');"
					 			action="#{recuperarAcoes.recuperarAcao}" >
									<f:param name="id" value="#{atividade.id}" />
				    				<h:graphicImage value="/img/refresh.png" style="overflow: visible;"/>
							</h:commandLink> 
						</td>
						<td>
							<h:commandLink id="visualizaAcao" title="Visualizar A��o de Extens�o" action="#{ atividadeExtensao.view }" immediate="true">
								        <f:param name="id" value="#{atividade.id}"/>
							    		<h:graphicImage url="/img/view.gif" />
							</h:commandLink>
						</td>
				</c:forEach>
			</tbody>
		</table>
		
		<br/>
		<br/>
		</c:if>
		<!-- FIM DAS A��es -->
		
		
			
			
		<c:if test="${empty recuperarAcoes.atividades}">
			<center><font color='red'>Nenhuma a��o de extens�o encontrada com os dados passados.</font></center>
		</c:if>
		
		
			
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>