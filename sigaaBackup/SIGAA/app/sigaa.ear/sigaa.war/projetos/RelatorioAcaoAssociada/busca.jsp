<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> > Busca de Relatórios </h2> 
		
	<%@include file="/projetos/RelatorioAcaoAssociada/_busca.jsp"%>
	
	<c:set value="#{buscaRelatoriosProjetosBean.relatorios}" var="relatorios"/>
	
	<c:if test="${not empty relatorios}" >
			
		<div class="infoAltRem">
		    <h:graphicImage value="/img/extensao/form_green.png"style="overflow: visible;" styleClass="noborder"/>: Visualizar Relatório
		</div>
				
		<h:form id="form">
			<table class="listagem">
							<caption class="listagem"> Lista de relatórios cadastrados (${ fn:length(buscaRelatoriosProjetosBean.relatorios) })</caption>
					<thead>
						<tr>
								<th width="2%" >Ano</th>
								<th>Título</th>
								<th width="15%">Tipo de Relatório</th>
								<th width="10%">Enviado em</th>
								<th width="12%">Situação</th>
								<th></th>
								<th></th>
						</tr>
					</thead>
					<tbody>
							<c:forEach items="#{relatorios}" var="item" varStatus="status">
					               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
					               			<td>${item.projeto.ano}</td>
											<td>${item.projeto.titulo}</td>
											<td>${item.tipoRelatorio.descricao }</td>
											<td><fmt:formatDate value="${item.dataEnvio}" pattern="dd/MM/yyyy"/></td>
											<td>${item.tipoParecerComite != null ? item.tipoParecerComite.descricao  :  'NÃO ANALISADO'  }</td>
											
											<td width="2%">								               
												<h:commandLink action="#{relatorioAcaoAssociada.view}" style="border: 0;" id="btnViewRelatorio_" title="Visualizar Relatório">
												   <f:param name="id" value="#{item.id}"/>
										           <h:graphicImage url="/img/extensao/form_green.png"/>
												</h:commandLink>
											</td>
									</tr>
							</c:forEach>					
					</tbody>
			</table>		
		</h:form>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>