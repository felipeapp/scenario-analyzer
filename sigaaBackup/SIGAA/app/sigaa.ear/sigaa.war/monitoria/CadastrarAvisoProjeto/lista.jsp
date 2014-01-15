<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h:outputText  value="#{projetoMonitoria.create}"/>
	<h:outputText  value="#{avisoProjeto.create}"/>

	<h2><ufrn:subSistema /> > Meus Projetos de Ensino</h2>

<%-- 
	<div class="infoAltRem">
		    <h:graphicImage value="/img/monitoria/document_new.png"style="overflow: visible;" />: Novo Aviso
		    <h:graphicImage value="/img/alterar.gif"style="overflow: visible;" />: Alterar Aviso
		    <h:graphicImage value="/img/delete.gif"style="overflow: visible;" />: Apagar Aviso
	</div>
--%>

	<div class="infoAltRem">
		 <h:graphicImage url="/img/view.gif" />: Visualizar
	</div>

	<h:form>
		<table class="listagem">
			<caption class="listagem">Lista de Avisos do projeto</caption>
			<thead>
				<tr>
					<th width="80%">Assunto</th>
					<th>Data Envio</th>
					<th></th>
					<th></th>
				</tr>
			</thead>
	
			<c:set var="projeto" value=""/>	 	
			<c:forEach items="#{avisoProjeto.avisosProjeto}" var="aviso" varStatus="status">
	            
	            
	            	<c:if test="${ projeto != aviso.projeto.id }">
						<c:set var="projeto" value="${ aviso.projeto.id }"/>
						<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
								<td colspan="3">
									${ aviso.projeto.anoTitulo }
								</td>
								
								<td width="2%">
									<h:commandLink action="#{projetoMonitoria.view}" title="Visualizar Projeto" style="border: 0;">
									      <f:param name="id" value="#{aviso.projeto.id}"/>
									      <h:graphicImage url="/img/view.gif" />
									</h:commandLink>
								</td>
						</tr>					
					</c:if>
	            
	            <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	            
					<td> ${fn:substring(aviso.titulo, 0, 60)}... </td>
					<td> <fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${aviso.dataCadastro}"/></td>
					<td></td>
					<td width="2%">
						<h:commandLink action="#{avisoProjeto.visualizarAviso}" title="Visualizar Aviso" style="border: 0;">
						      <f:param name="id" value="#{aviso.id}"/>
						      <h:graphicImage url="/img/view.gif" />
						</h:commandLink>
					</td>
	
<%--
					<td width="2%">
							<h:commandLink  title="Alterar" action="#{avisoProjeto.atualizar}" style="border: 0;">
							      <f:param name="id" value="#{aviso.id}"/>
							      <h:graphicImage url="/img/alterar.gif" />
							</h:commandLink>
					</td>		
					<td width="2%">
							<h:commandLink  title="Remover" action="#{avisoProjeto.preRemover}" style="border: 0;">
							      <f:param name="id" value="#{aviso.id}"/>
							      <h:graphicImage url="/img/delete.gif" />
							</h:commandLink>
					</td>
--%>
					
				</tr>
			</c:forEach>
	
			<c:if test="${empty avisoProjeto.avisosProjeto}">
				<tr>
					<td colspan="5"><center><font color="red">Não há avisos cadastrados para este projeto!<br/></font> </center></td>
				</tr>
			</c:if>
			
			
			<tfoot>
				<tr>
					<td colspan="6" align="center">
						<h:commandButton title="Novo Aviso" action="#{avisoProjeto.novoAviso}" value="Novo Aviso...">
								<f:setPropertyActionListener target="#{avisoProjeto.idProjeto}" value="#{avisoProjeto.obj.projeto.id}"/>								        							        
						</h:commandButton>								
					</td>
				</tr>
			</tfoot>

		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>