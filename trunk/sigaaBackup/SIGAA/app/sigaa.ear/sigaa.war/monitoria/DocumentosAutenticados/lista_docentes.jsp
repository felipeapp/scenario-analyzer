<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Documentos autenticados de Monitoria</h2>

<a4j:keepAlive beanName="documentosAutenticadosMonitoria" />		
	
<h:form>
	<div class="infoAltRem">		
		<h:graphicImage value="/img/comprovante.png" height="19" width="19" style="overflow: visible;"/>: Emitir Declaração					    		    
	</div>
	
	
		<table class=listagem>
				<caption class="listagem">Lista de participações do docente em Projetos de Monitoria</caption>
				<thead>
						<tr>
							<th width="60%">Projeto de Monitoria</th>							
							<th style="text-align: center;">Data Entrada no Projeto</th>
							<th style="text-align: center;">Data Saída do Projeto</th>
							<th>Situação Projeto</th>
							<th> </th>		
														
						</tr>
				</thead>
				<tbody>
						
					<c:forEach items="#{documentosAutenticadosMonitoria.docentesMonitoria}" var="item" varStatus="status">						
			               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td>${ item.projetoEnsino.anoTitulo }</td>							
									<td style="text-align: center;"><fmt:formatDate value="${item.dataEntradaProjeto}" pattern="dd/MM/yyyy"/></td>
									<td style="text-align: center;"><fmt:formatDate value="${item.dataSaidaProjeto}" pattern="dd/MM/yyyy"/></td>				
									<td> ${ item.projetoEnsino.projeto.situacaoProjeto.descricao } </td>
									
									<td width="2%">
										<h:commandLink title="Emitir Declaração" action="#{declaracaoDocenteMonitoria.emitirDeclaracao}" immediate="true" id="btEmitirDeclaracao"	rendered="#{ true }">
											<f:param name="id" value="#{item.id}"/>
					                   		<h:graphicImage url="/img/comprovante.png" height="19" width="19"/>
										</h:commandLink>
									</td>								
															
							</tr>
			 		   </c:forEach>
			 		   
			 		   <c:if test="${empty documentosAutenticadosMonitoria.docentesMonitoria}" >
			 		   		<tr><td colspan="6" align="center"><font color="red">Usuário atual não possui Declarações/Certificados pendentes de impressão.</font></td></tr>
			 		   </c:if>
			 		   
					</tbody>	
		</table>
		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>