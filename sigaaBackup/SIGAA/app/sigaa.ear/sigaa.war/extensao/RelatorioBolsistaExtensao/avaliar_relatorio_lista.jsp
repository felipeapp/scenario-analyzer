<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h2><ufrn:subSistema /> > Avaliar Relatórios de Discentes de Extensão</h2>
	
	<h:form id="form">
	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif" style="overflow: visible;" styleClass="noborder"/>: Visualizar Relatório	    
	    <h:graphicImage value="/img/seta.gif" style="overflow: visible;" styleClass="noborder"/>: Avaliar Relatório	    
	</div>
		
		
	<table class="listagem">
			<caption class="listagem"> Lista de relatórios de discentes de ações coordenadas pelo usuário atual</caption>
			<thead>
			<tr>				
				<th>Matrícula</th>
				<th>Discente</th>					
				<th>Vínculo</th>
				<th>Tipo de Relatório</th>
				<th>Avaliado em</th>
				<th></th>
				<th></th>					
			</tr>
			</thead>
			<tbody>
			<c:if test="${empty relatorioBolsistaExtensao.relatoriosCoordenadorAvaliar}">
				<tr>
						<td colspan="7" align="center"><font color="red">Não há relatórios de discentes cadastrados.</font></td>
				</tr>
			</c:if>
			
			<c:set var="idProjeto" value="0"/>
			<c:forEach items="#{relatorioBolsistaExtensao.relatoriosCoordenadorAvaliar}" var="item" varStatus="status">
 			  <c:if test="${idProjeto != item.discenteExtensao.atividade.projeto.id }">
				  <tr>
				  	<c:set var="idProjeto" value="${ item.discenteExtensao.atividade.projeto.id }"/>
				  	<td colspan="7" class="subFormulario" >${item.discenteExtensao.atividade.codigoTitulo}</td>
				  </tr>
			  </c:if>			
			  
               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td>${item.discenteExtensao.discente.matricula}</td>
						<td>${item.discenteExtensao.discente.nome}</td>
						<td>${item.discenteExtensao.tipoVinculo.descricao}</td>
						<td>${item.tipoRelatorio.descricao}</td>						
						<td><fmt:formatDate value="${item.dataParecer}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
						<td width="2%">
								<h:commandLink title="Visualizar Relatório" action="#{relatorioBolsistaExtensao.view}" 
								style="border: 0;" id="ver_relatorio">
							       <f:param name="id" value="#{item.id}"/>
					               <h:graphicImage url="/img/view.gif" />
								</h:commandLink>
						</td>						
						<td width="2%">		
								<h:commandLink title="Analisar Relatório" action="#{relatorioBolsistaExtensao.iniciarAvaliacao}" 
									style="border: 0;" id="analisar_relatorio" rendered="#{empty item.dataParecer}">
							       <f:param name="id" value="#{item.id}"/>
					               <h:graphicImage url="/img/seta.gif" />
								</h:commandLink>
						</td>
				</tr>
			</c:forEach>
			</tbody>
	</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>