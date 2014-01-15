
<%@include file="/mobile/commons/cabecalho.jsp"%>

	<f:view>
	
		<h:form>
			
			<h:commandButton value="Menu Principal" action="#{operacoesBibliotecaMobileMBean.telaMenuPrincipalBibliotecaMobile}"/> <br/><br/>
			<h:commandButton value="<< Voltar Tela Consulta" action="#{operacoesBibliotecaMobileMBean.telaConsultaMeusEmprestimosMobile}"/> <br/><br/>

			<c:if test="${fn:length(operacoesBibliotecaMobileMBean.emprestimos) > 0}">
				
				<table class="parametroMobile">
					<tr>
						<th> <c:out value="De: " /></th>
						<td> 
							<h:outputText value="#{operacoesBibliotecaMobileMBean.dataInicial}"><f:convertDateTime pattern="dd/MM/yyyy" /> </h:outputText> 
						</td>
					</tr>
					<tr>	
						<th> <c:out value="Até: " /></th>
						<td>
							<h:outputText value="#{operacoesBibliotecaMobileMBean.dataFinal}"><f:convertDateTime pattern="dd/MM/yyyy"/> </h:outputText>
						</td>
					</tr>
				</table>
				
				<table class="listagemMobile">
						
						<caption> ${fn:length(operacoesBibliotecaMobileMBean.emprestimos)} Empréstimo(s)</caption>
						
						<tbody>
			
							<c:forEach var="emprestimo" items="#{operacoesBibliotecaMobileMBean.emprestimos }" varStatus="status">
								 <tr>
									<th colspan="6">Empréstimo ${status.index+1} </th>
								</tr>
								 <tr>
								 	<th colspan="6">Data Empréstimo: </th>
								 </tr>
								 <tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<td colspan="6">
										<h:outputText value="#{emprestimo.dataEmprestimo}" converter="convertData">
											<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
										</h:outputText> 
									</td>
										
								</tr>
								<tr>
								 	<th colspan="6">Data Renovação: </th>
								 </tr>
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<td colspan="6">
										<h:outputText value="#{emprestimo.dataRenovacao}" converter="convertData">
											<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
										</h:outputText> 
									</td>
										
								</tr>
								
								<tr>
								 	<th colspan="6">Prazo Devolução: </th>
								</tr>
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<td colspan="6">
										<h:outputText value="#{emprestimo.prazo}" converter="convertData">
											<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
										</h:outputText> 
									</td>
										
								</tr>
				
								<tr>
								 	<th colspan="6">Data Devolução: </th>
								</tr>
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<td colspan="6">
										<h:outputText value="#{emprestimo.dataDevolucao}" converter="convertData">
											<f:convertDateTime pattern="dd/MM/yyyy HH:mm"/>
										</h:outputText> 
									</td>
									
								</tr>
				
								<%-- informacoes so item do emprestimos --%>
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<th colspan="3">Código de Barras:</th>
									<td colspan="3">${emprestimo.material.codigoBarras}</td>
								</tr>
									
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
									<th colspan="6">Título:</th>
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