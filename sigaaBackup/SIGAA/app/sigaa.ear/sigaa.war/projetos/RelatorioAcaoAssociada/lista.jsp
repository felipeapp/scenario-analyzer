<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Submissão de Relatórios de Ações Integradas</h2>
		
	<div class="infoAltRem">
	    <h:graphicImage value="/img/extensao/document_add.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relatório Parcial	    
	    <h:graphicImage value="/img/extensao/document_new.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relatório Final<br/>
	    <h:graphicImage value="/img/extensao/document_edit.png"style="overflow: visible;" styleClass="noborder"/>: Editar/Enviar Relatório
	    <h:graphicImage value="/img/extensao/document_delete.png"style="overflow: visible;" styleClass="noborder"/>: Remover Relatório
	    <h:graphicImage value="/img/extensao/document_view.png"style="overflow: visible;" styleClass="noborder"/>: Visualizar Relatório	    
	</div>
		
		
<h:form id="form">
	<table class="listagem">
					<caption class="listagem"> Lista de relatórios de ações integradas coordenadas pelo usuário atual</caption>
			<thead>
			<tr>
					<th>Tipo Relatório</th>
					<th style="text-align: center">Data de Envio</th>
<!--					<th>Validação Depto.</th>-->
<!--					<th width="20%">Justificativa Depto.</th>-->
					<th>Validação Comitê.</th>
					<th width="20%">Justificativa Comitê.</th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>
					<th></th>					
			</tr>
			</thead>
			<tbody>
			
					<c:set value="#{relatorioAcaoAssociada.projetosCoordenador}" var="projetos"/>			
					<c:forEach items="#{projetos}" var="projeto" varStatus="status">						
						
						<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
							<td colspan="7">${ projeto.anoTitulo }</td>
							<td width="2%">
							
								<h:commandLink action="#{relatorioAcaoAssociada.preAdicionarRelatorio}" style="border: 0;"	id="relParcial">
							       <f:param name="id" value="#{projeto.id}"/>
							       <f:param name="relatorioFinal" value="false"/>
					               <h:graphicImage url="/img/extensao/document_add.png" title="Cadastrar Relatório Parcial" />
								</h:commandLink>							
								
							</td>	
							<td width="2%">
								<h:commandLink action="#{relatorioAcaoAssociada.preAdicionarRelatorio}" style="border: 0;"	id="relFinal">
							       <f:param name="id" value="#{projeto.id}"/>
							       <f:param name="relatorioFinal" value="true"/>
					               <h:graphicImage url="/img/extensao/document_new.png" title="Cadastrar Relatório Final" />
								</h:commandLink>
							</td>																		
							
						</tr>
						
						<c:forEach items="#{projeto.relatorios}" var="item" varStatus="status">
							<c:if test="${item.ativo == true}">
				               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				               			<td>${item.tipoRelatorio.descricao}</td>
										<td style="text-align: center"><fmt:formatDate value="${item.dataEnvio}" pattern="dd/MM/yyyy" /><h:outputText value="CADASTRO EM ANDAMENTO" rendered="#{empty item.dataEnvio}"/></td>
<!--										<td>${item.tipoParecerDepartamento.descricao}</td>-->
<!--										<td>${item.parecerDepartamento}</td>-->
										<td>${item.tipoParecerComite.descricao}</td>
										<td>${item.parecerComite}</td>
										<td></td>
										<td></td>										
										<td width="2%">
											<h:commandLink action="#{relatorioAcaoAssociada.preAlterarRelatorio}" style="border: 0;" 
											id="alterarRelatorio" rendered="#{(item.ativo) && (item.editavel)}">
										       <f:param name="idRelatorio" value="#{item.id}"/>
								               <h:graphicImage url="/img/extensao/document_edit.png" title="Editar/Enviar Relatório" />
											</h:commandLink>
										</td>																		
										
										<td width="2%">
											<h:commandLink action="#{relatorioAcaoAssociada.preRemoverRelatorio}" style="border: 0;" 
											id="removerRelatorio" rendered="#{(item.ativo) && (item.editavel)}">
										       <f:param name="idRelatorio" value="#{item.id}"/>
								               <h:graphicImage url="/img/extensao/document_delete.png" title="Remover Relatório"/>
											</h:commandLink>
										</td>
																			
										<td width="2%">								               
											<h:commandLink action="#{relatorioAcaoAssociada.view}" style="border: 0;" id="verRelatorio">
											   <f:param name="id" value="#{item.id}"/>
									           <h:graphicImage url="/img/extensao/document_view.png" title="Visualizar Relatório" />
											</h:commandLink>
										</td>								
										
								</tr>
							</c:if>
						</c:forEach>
						
						<c:if test="${empty projeto.relatorios}" >
			 		   		<tr><td colspan="6" align="center"><font color="red">Não há relatórios cadastrados para esta ação.</font></td></tr>
			 		    </c:if>
						
					</c:forEach>
			 		   
			 		<c:if test="${empty projetos}" >
			 		 		<tr><td colspan="6" align="center"><font color="red">Não há ações ativas coordenadas pelo usuário atual.</font></td></tr>
			 		</c:if>
			 		   
			</tbody>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>