<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Submiss�o de Relat�rios de Cursos e Eventos de Extens�o</h2>
		<h:outputText value="#{relatorioCursoEvento.create}"/>
		
	<div class="infoAltRem">
	    <h:graphicImage value="/img/extensao/document_add.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relat�rio Parcial	    
	    <h:graphicImage value="/img/extensao/document_new.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relat�rio Final<br/>
	    <h:graphicImage value="/img/extensao/document_edit.png"style="overflow: visible;" styleClass="noborder"/>: Editar Relat�rio
	    <h:graphicImage value="/img/extensao/document_delete.png"style="overflow: visible;" styleClass="noborder"/>: Cancelar Relat�rio
	    <h:graphicImage value="/img/extensao/document_view.png"style="overflow: visible;" styleClass="noborder"/>: Ver Relat�rio
	</div>
		
		
<h:form id="form">
	<table class="listagem">
					<caption class="listagem"> Lista de relat�rios cadastrados de cursos e eventos coordenados pelo usu�rio atual</caption>
			<thead>
			<tr>
					<th width="15%">Tipo Relat�rio</th>
					<th>Data de Cadastro</th>
					<th>Validado Depto.</th>					
					<th>Validado PROEx</th>
					<th></th>
					<th></th>
					<th></th>
			</tr>
			</thead>
			<tbody>
			
					<c:set value="#{relatorioCursoEvento.cursosEventosCoordenador}" var="cursosEventos"/>
			
					<c:forEach items="#{cursosEventos}" var="cursoEvento" varStatus="status">						
						
							<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
								<td colspan="5" >${ cursoEvento.codigoTitulo }</td>
								<td width="2%">
								
									<h:commandLink action="#{relatorioCursoEvento.preAdicionarRelatorio}" style="border: 0;"	id="relParcial">
								       <f:param name="id" value="#{cursoEvento.id}"/>
								       <f:param name="relatorioFinal" value="false"/>
						               <h:graphicImage url="/img/extensao/document_add.png" title="Cadastrar Relat�rio Parcial" />
									</h:commandLink>							
									
								</td>	
								<td width="2%">
									<h:commandLink action="#{relatorioCursoEvento.preAdicionarRelatorio}" style="border: 0;"	id="relFinal">
								       <f:param name="id" value="#{cursoEvento.id}"/>
								       <f:param name="relatorioFinal" value="true"/>
						               <h:graphicImage url="/img/extensao/document_new.png" title="Cadastrar Relat�rio Final" />
									</h:commandLink>
								</td>																		
								
							</tr>
							
							<c:forEach items="#{cursoEvento.relatoriosCursosEventos}" var="item" varStatus="status">
				               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				               			<td>${item.tipoRelatorio.descricao}</td>
										<td><fmt:formatDate value="${item.dataCadastro}" pattern="dd/MM/yyyy" /></td>
										<td>${item.tipoParecerDepartamento != null ? item.tipoParecerDepartamento.descricao : 'N�O ANALISADO'}</td>
										<td>${item.tipoParecerProex != null ? item.tipoParecerProex.descricao : 'N�O ANALISADO'}</td>
										
										<td width="2%">
											<h:commandLink action="#{relatorioCursoEvento.preAlterarRelatorio}" style="border: 0;" 
											id="alterar" rendered="#{item.editavel}">
										       <f:param name="idRelatorio" value="#{item.id}"/>
								               <h:graphicImage url="/img/extensao/document_edit.png" title="Editar Relat�rio Final" />
											</h:commandLink>
										</td>																		
										
										<td width="2%">
											<h:commandLink action="#{relatorioCursoEvento.preRemoverRelatorio}" style="border: 0;" 
											id="remover" rendered="#{item.editavel}">
										       <f:param name="idRelatorio" value="#{item.id}"/>
								               <h:graphicImage url="/img/extensao/document_delete.png" title="Cancelar Relat�rio"/>
											</h:commandLink>
										</td>
																			
										<td width="2%">								               
											<h:commandLink id="view" action="#{relatorioCursoEvento.view}" style="border: 0;">
											   <f:param name="idRelatorio" value="#{item.id}"/>
									           <h:graphicImage url="/img/extensao/document_view.png" />
											</h:commandLink>
										</td>
								</tr>
							</c:forEach>
						
							<c:if test="${empty cursoEvento.relatoriosCursosEventos}" >
				 		   		<tr><td colspan="8" align="center"><font color="red">N�o h� relat�rios cadastrados para esta a��o.</font></td></tr>
				 		    </c:if>
						
					</c:forEach>
			 		   
			 		   <c:if test="${empty cursosEventos}" >
			 		   		<tr><td colspan="6" align="center"><font color="red">N�o h� cursos ou eventos de extens�o ativos coordenados pelo usu�rio atual.</font></td></tr>
			 		   </c:if>
			</tbody>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>