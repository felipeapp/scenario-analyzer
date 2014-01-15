<%-- Pagina que mostra os emprestimos ativos do usu�rio --%>

<%@include file="/mobile/commons/cabecalho.jsp"%>

	<f:view>
	
		<h:form>
			
			<h:commandButton value="Menu Principal" action="#{operacoesBibliotecaMobileMBean.telaMenuPrincipalBibliotecaMobile}"/> <br/><br/>
	
			<c:if test="${fn:length(operacoesBibliotecaMobileMBean.emprestimosAtivos) > 0}">
				
				
				<table class="listagemMobile">
					<caption> ${fn:length(operacoesBibliotecaMobileMBean.emprestimosAtivos)} Empr�stimo(s) Ativo(s) </caption>
						
					<tbody>
		
						<c:forEach var="emprestimo" items="#{operacoesBibliotecaMobileMBean.emprestimosAtivos }" varStatus="status">
								<tr>
									<th colspan="6">Empr�stimo ${status.index+1} </th>
								</tr>
								
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<th colspan="6">Data Empr�stimo: </th>
								</tr>
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">	
									<td colspan="6">
										<h:outputText value="#{emprestimo.dataEmprestimo}" converter="convertData">
											<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
										</h:outputText>
									</td>
								</tr>
								
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">	
									<th colspan="6">Data Renova��o: </th>
								</tr>
								
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									
									<td colspan="6">
										<h:outputText value="#{emprestimo.dataRenovacao}" converter="convertData">
											<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
										</h:outputText>
									</td>
								</tr>
								
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<th colspan="6">Prazo Devolu��o: </th>
								</tr>
								
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">	
									<td colspan="6">
										<h:outputText value="#{emprestimo.prazo}" converter="convertData">
											<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
										</h:outputText>
									</td>
								</tr>
			
								<%-- informacoes so item do emprestimos --%>
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<th colspan="6">C�digo de Barras:</th>
								</tr>
								
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<td colspan="6">${emprestimo.material.codigoBarras}</td>
								</tr>
									
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<th colspan="6"><small>T�tulo:</small></th>
								</tr>
		
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<td colspan="6">
										<c:out value="${operacoesBibliotecaMobileMBean.titulosResumidos[status.index].titulo}"></c:out>
									</td>
								</tr>				
		
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<th colspan="6">Autor:</th>
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
						
				</table>
				
			</c:if>
			
		</h:form>
		
	</f:view>

<%@include file="/mobile/commons/rodape.jsp" %>