<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h2><ufrn:subSistema /> > Relatórios de Projetos de Monitoria > Avaliações do Relatório</h2>

	<div class="infoAltRem">
		<c:if test="${acesso.coordenadorMonitoria}">		    
		    <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Avaliações do Relatório
	    </c:if>
	</div>


<h:form>
	<table class="listagem">
		<thead>
			<tr>
				<th>Avaliador</th>												
				<th>Status</th>
				<th>Data da Avaliação</th>
				<th></th>				
			
			</tr>
		</thead>
				
		<c:forEach items="#{avalRelatorioProjetoMonitoria.avaliacoes}" var="avaliacao" varStatus="status">
		    <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		    	
		    	<c:if test="${avaliacao.dataAvaliacao != null}">  	
		    	
		    		<td>[Visualização não autorizada]</td>
		    	
		    		<td> ${avaliacao.statusAvaliacao.descricao} </td>
		    	
		    		<td><fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${avaliacao.dataAvaliacao}"/></td>				            
				
					<td width="1%">															
						<c:if test="${avaliacao.dataAvaliacao != null}">
							<h:commandLink title="Visualizar Avaliação" action="#{avalRelatorioProjetoMonitoria.view}" style="border: 0;">
								<f:param name="idAvaliacao" value="#{avaliacao.id}"/>
								<h:graphicImage url="/img/view.gif" id="view"/>
							</h:commandLink>	
						</c:if>														
					</td>
				
				</c:if>
								
								
			</tr>
		</c:forEach>
		
		<tfoot>
			<tr align="center">
				<td colspan="4">
					<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)"/>
				</td>
			</tr>
		</tfoot>
		
	</table>
	
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>