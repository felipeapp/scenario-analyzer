<%-- Pagina que mostra os emprestimos renovaveis do usuario --%>

<%@include file="/mobile/commons/cabecalho.jsp"%>

	<f:view>
	
		<h:form>
			
			<h:commandButton value="Menu Principal" action="#{operacoesBibliotecaMobileMBean.telaMenuPrincipalBibliotecaMobile}"/> <br/><br/>
	
			<c:if test="${fn:length(operacoesBibliotecaMobileMBean.emprestimosAtivosRenovaveis) > 0}">
				
				
				<table class="listagemMobile">
						<caption>Empréstimos Renováveis</caption>
						
						<tbody>
			
							<c:forEach var="emprestimo" items="#{operacoesBibliotecaMobileMBean.emprestimosAtivosRenovaveis }" varStatus="status">
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
										<th>Prazo:</th>
										<td colspan="4">
											<h:outputText value="#{emprestimo.prazo}" converter="convertData">
												<f:convertDateTime pattern="dd/MM/yy HH:mm"/>
											</h:outputText> 
										</td>
										
										<td colspan="1" style="padding:0; margin:0;">
											<input type="checkbox" name="emprestimosSelecionados" value="${status.index}"/>
										</td>
									</tr>
				
									<%-- informacoes so item do emprestimos --%>
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
										<th colspan="3">Cód. Barras:</th>
										<td colspan="3">${emprestimo.material.codigoBarras}</td>
									</tr>
										
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
										<th colspan="6"><small>Título:</small></th>
									</tr>
			
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
										<td colspan="6">
											<c:out value="${operacoesBibliotecaMobileMBean.titulosResumidos[status.index].titulo}"></c:out>
										</td>
									</tr>				
			
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
										<th colspan="6"><small>Autor:</small></th>
									</tr>
			
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
										<td colspan="6">
											<c:out value="${operacoesBibliotecaMobileMBean.titulosResumidos[status.index].autor}"></c:out>
										</td>
									</tr>
			
									<tr>
										<td colspan="6">
											<hr style="color:#111155"/>
										</td>	
									</tr>
	
							</c:forEach>
				
						</tbody>
			
						<tfoot>
							<tr>
								<td align="center" colspan="6">
								<h:commandButton value="Renovar" action="#{operacoesBibliotecaMobileMBean.renovarEmprestimos}" style="background-color: #C4D2EB"/>
								</td>
							</tr>
						</tfoot>
						
				</table>
				
			</c:if>
			
		</h:form>
		
	</f:view>

<%@include file="/mobile/commons/rodape.jsp" %>