<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	
	<table class="tabelaRelatorioBorda" style="width:100%;margin-bottom:30px; border-top:none;">
	
		<tbody>
				<tr>
					<td>
						<table style="border: none; width: 100%;">
						
							<thead>
								<tr>
									<th style="text-align: center; width: 10%; ">Tipo</th>
									<th style="text-align: center; width: 10%; ">Data de Início</th>
									<th style="text-align: center; width: 10%; ">Data Final</th>
									<th style="text-align: center; width: 50%;">Usuário que Realizou o Estorno</th>
									<th style="text-align: center; width: 20%;">Data do Estorno</th>
								</tr>
							</thead>
							
							<c:set var="idFiltroUsuarioBiblioteca" value="-1" scope="request"/>
							
							<c:forEach var="resul" items="#{ _abstractRelatorioBiblioteca.resultado }" >
								
								<c:if test="${ idFiltroUsuarioBiblioteca != resul[2]}">
									<c:set var="idFiltroUsuarioBiblioteca" value="${resul[2]}" scope="request"/>
									
									<tr>
										 <%-- Usuário que teve sua suspensao Estornada  --%>
										<td colspan="5" style="font-weight: bold; background-color: #DDDDDD; ">
										
											<%-- Se suspensão de Pessoa --%>
											<c:if test="${ resul[0]}">
												
											
									 			<c:if test="${ ! resul[8]}">
													${resul[9]} -  <%-- CPF --%>
												</c:if>
												<c:if test="${ resul[8]}">
													${resul[10]} - <%-- Passaporte --%>
												</c:if>
												${resul[11]}
											</c:if>
											
											<%-- Se suspensão de Biblioteca --%>
											<c:if test="${! resul[0]}">
									 			${ resul[8]} - ${ resul[9]} 
											</c:if>
											
										</td>
									</tr>
								</c:if>						
								
								
								<tr> 
								
									<%-- Se suspensão de Pessoa --%>
									<c:if test="${ resul[0]}">
										<td> ${ resul[1] ? 'Manual' : 'Automática' } </td>
										<td> <ufrn:format type="data" valor="${ resul[3]}" />  </td>
										<td> <ufrn:format type="data" valor="${ resul[4]}" /> </td>
										
										                                  
										
										<td>${ resul[5]}</td>                                       <%-- Usuário Estornou --%>
										<td> <ufrn:format type="data" valor="${ resul[6]}" /> </td> <%-- Data Estorno --%>
									</c:if>
								
									<%-- Se suspensão de Biblioteca --%>
									<c:if test="${ ! resul[0]}">
										<td> ${ resul[1] ? 'Manual' : 'Automática' } </td>
										<td> <ufrn:format type="data" valor="${ resul[3]}" /> </td>
										<td> <ufrn:format type="data" valor="${ resul[4]}" /> </td>
										
										
										
										<td>${ resul[5]}</td>                                       <%-- Usuário Estornou --%>
										<td> <ufrn:format type="data" valor="${ resul[6]}" /> </td> <%-- Data Estorno --%>
										
										
									</c:if>
								
								
								</tr>
								
								<tr>
									<td style="font-style: italic;" colspan="5"> <strong>Motivo do Estorno:</strong> ${ resul[7]} </td>
								</tr>
								
								
							</c:forEach>
								
								<%-- Se suspensão de Biblioteca 
								<c:if test="${ ! resul[0]}">
								
								</c:if>
								
								<tr> 
									<td>
										${ resul[6]}
									</td>
									<td>
										${ resul[7]}
									</td>
								
									
								
									<td style="text-align: right;">
										<c:if test="${ resul[4]}"> ${ resul[3]} </c:if> <c:if test="${ ! resul[4]}"> ${ resul[5]} </c:if> ${ resul[2]}
									</td> 
									
									
									<td style=" text-align: center;">
										${ resul[8]}
									</td>
									
									<td style=" text-align: center;">
										${ resul[9]}
									</td>
									
								</tr>
								<tr> 
									<td style="font-style: italic;" colspan="5">
										Motivo Estorno: ${ resul[10]}
									</td>
								</tr>--%>
						
										
							
						</table>
					</td>
				</tr>
		</tbody>
		
	</table>
	
</f:view>


<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>