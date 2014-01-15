<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Submiss�o de Relat�rios de A��es Integradas</h2>
		
	<div class="infoAltRem">
	    <h:graphicImage value="/img/extensao/document_add.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relat�rio Parcial	    
	    <h:graphicImage value="/img/extensao/document_new.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relat�rio Final<br/>
	    <h:graphicImage value="/img/extensao/document_edit.png"style="overflow: visible;" styleClass="noborder"/>: Editar/Enviar Relat�rio
	    <h:graphicImage value="/img/extensao/document_delete.png"style="overflow: visible;" styleClass="noborder"/>: Remover Relat�rio
	    <h:graphicImage value="/img/extensao/document_view.png"style="overflow: visible;" styleClass="noborder"/>: Visualizar Relat�rio	    
	</div>
		
		
<h:form id="form">
	<table class="listagem">
					<caption class="listagem"> Lista de relat�rios de a��es integradas coordenadas pelo usu�rio atual</caption>
			<thead>
			<tr>
					<th>Tipo Relat�rio</th>
					<th style="text-align: center">Data de Envio</th>
<!--					<th>Valida��o Depto.</th>-->
<!--					<th width="20%">Justificativa Depto.</th>-->
					<th>Valida��o Comit�.</th>
					<th width="20%">Justificativa Comit�.</th>
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
					               <h:graphicImage url="/img/extensao/document_add.png" title="Cadastrar Relat�rio Parcial" />
								</h:commandLink>							
								
							</td>	
							<td width="2%">
								<h:commandLink action="#{relatorioAcaoAssociada.preAdicionarRelatorio}" style="border: 0;"	id="relFinal">
							       <f:param name="id" value="#{projeto.id}"/>
							       <f:param name="relatorioFinal" value="true"/>
					               <h:graphicImage url="/img/extensao/document_new.png" title="Cadastrar Relat�rio Final" />
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
								               <h:graphicImage url="/img/extensao/document_edit.png" title="Editar/Enviar Relat�rio" />
											</h:commandLink>
										</td>																		
										
										<td width="2%">
											<h:commandLink action="#{relatorioAcaoAssociada.preRemoverRelatorio}" style="border: 0;" 
											id="removerRelatorio" rendered="#{(item.ativo) && (item.editavel)}">
										       <f:param name="idRelatorio" value="#{item.id}"/>
								               <h:graphicImage url="/img/extensao/document_delete.png" title="Remover Relat�rio"/>
											</h:commandLink>
										</td>
																			
										<td width="2%">								               
											<h:commandLink action="#{relatorioAcaoAssociada.view}" style="border: 0;" id="verRelatorio">
											   <f:param name="id" value="#{item.id}"/>
									           <h:graphicImage url="/img/extensao/document_view.png" title="Visualizar Relat�rio" />
											</h:commandLink>
										</td>								
										
								</tr>
							</c:if>
						</c:forEach>
						
						<c:if test="${empty projeto.relatorios}" >
			 		   		<tr><td colspan="6" align="center"><font color="red">N�o h� relat�rios cadastrados para esta a��o.</font></td></tr>
			 		    </c:if>
						
					</c:forEach>
			 		   
			 		<c:if test="${empty projetos}" >
			 		 		<tr><td colspan="6" align="center"><font color="red">N�o h� a��es ativas coordenadas pelo usu�rio atual.</font></td></tr>
			 		</c:if>
			 		   
			</tbody>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>