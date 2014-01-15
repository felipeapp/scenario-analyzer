<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/discente/menu_discente.jsp"%>

	<h2><ufrn:subSistema /> > Relat�rio de Discente de Extens�o</h2>
	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/extensao/document_add.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relat�rio Parcial	    
	    <h:graphicImage value="/img/extensao/document_new.png"style="overflow: visible;" styleClass="noborder"/>: Cadastrar Relat�rio Final<br/>
	    <h:graphicImage value="/img/extensao/document_edit.png"style="overflow: visible;" styleClass="noborder"/>: Editar/Enviar Relat�rio
	    <h:graphicImage value="/img/extensao/document_delete.png"style="overflow: visible;" styleClass="noborder"/>: Cancelar Relat�rio
	    <h:graphicImage value="/img/extensao/document_view.png"style="overflow: visible;" styleClass="noborder"/>: Ver Relat�rio
	</div>
		
<h:form>
	<table class="listagem">
			<caption class="listagem"> Lista de Relat�rios de Discentes de Extens�o</caption>
			<thead>
			<tr>
					<th>A��o de Extens�o</th>
					<th>Data Envio</th>
					<th>Parecer</th>
					<th></th>
					<th></th>
					<th></th>
			</tr>
			</thead>
			<tbody>
			
			<c:forEach items="#{relatorioBolsistaExtensao.discentesExtensaoUsuarioLogado}" var="discenteExtensao" varStatus="status">
			
			
						<tr style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
							<td colspan="4" >${ discenteExtensao.atividade.codigoTitulo }</td>
							<td width="2%">							
								<h:commandLink action="#{relatorioBolsistaExtensao.preAdicionarRelatorio}" style="border: 0;"	id="relParcial">
							       <f:param name="idDiscente" value="#{discenteExtensao.id}"/>
							       <f:param name="relatorioFinal" value="false"/>
					               <h:graphicImage url="/img/extensao/document_add.png" title="Cadastrar Relat�rio Parcial" />
								</h:commandLink>
							</td>	
							<td width="2%">
								<h:commandLink action="#{relatorioBolsistaExtensao.preAdicionarRelatorio}" style="border: 0;"	id="relFinal">
							       <f:param name="idDiscente" value="#{discenteExtensao.id}"/>
							       <f:param name="relatorioFinal" value="true"/>
					               <h:graphicImage url="/img/extensao/document_new.png" title="Cadastrar Relat�rio Final" />
								</h:commandLink>
							</td>																		
							
						</tr>
			
			
						<c:forEach items="#{discenteExtensao.relatorios}" var="item" varStatus="status">
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			               			<td>${item.tipoRelatorio.descricao}</td>
									<td><fmt:formatDate value="${item.dataEnvio}" pattern="dd/MM/yyyy HH:mm:ss" /><h:outputText value="N�O ENVIADO" rendered="#{empty item.dataEnvio}"/></td>
									<td><fmt:formatDate value="${item.dataParecer}" pattern="dd/MM/yyyy HH:mm:ss" /><h:outputText value="N�O ANALISADO" rendered="#{empty item.dataParecer}"/></td>
									<td width="2%">
										<h:commandLink action="#{relatorioBolsistaExtensao.atualizar}" style="border: 0;" 
										id="alterarRelatorio" rendered="#{(item.ativo) && (item.editavel)}">
									       <f:param name="id" value="#{item.id}"/>
							               <h:graphicImage url="/img/extensao/document_edit.png" title="Editar Relat�rio" />
										</h:commandLink>
									</td>																		
									
									<td width="2%">
										<h:commandLink action="#{relatorioBolsistaExtensao.preRemover}" style="border: 0;" 
										id="removerRelatorio" rendered="#{item.ativo && (item.editavel)}">
									       <f:param name="id" value="#{item.id}"/>
							               <h:graphicImage url="/img/extensao/document_delete.png" title="Cancelar Relat�rio"/>
										</h:commandLink>
									</td>
																		
									<td width="2%">								               
										<h:commandLink action="#{relatorioBolsistaExtensao.view}" style="border: 0;" id="verRelatorio">
										   <f:param name="id" value="#{item.id}"/>
								           <h:graphicImage url="/img/extensao/document_view.png" />
										</h:commandLink>
									</td>
							</tr>
						</c:forEach>
						
						<c:if test="${empty discenteExtensao.relatorios}" >
			 		   		<tr><td colspan="6" align="center"><font color="red">N�o h� relat�rios cadastrados para esta a��o.</font></td></tr>
			 		    </c:if>
						
					</c:forEach>
			 		   
			 		<c:if test="${empty relatorioBolsistaExtensao.discentesExtensaoUsuarioLogado}" >
			 		   		<tr><td colspan="6" align="center"><font color="red">N�o h� a��es de extens�o ativas onde o usu�rio atual � bolsista ativo.</font></td></tr>
			 		</c:if>
			
			</tbody>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>