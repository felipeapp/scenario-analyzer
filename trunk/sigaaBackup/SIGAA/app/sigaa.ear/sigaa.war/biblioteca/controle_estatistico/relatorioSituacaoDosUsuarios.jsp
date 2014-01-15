<%@ include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<f:view>
	<h:form>
	
		<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>

		<table class="tabelaRelatorioBorda" style="width:100%;margin-bottom:30px; border-top:none;">
		
			<c:set var="totalUsuariosGeral" value="${0}" />
			<c:set var="totalItensGeral" value="${0}" />
			
			<c:if test="${ !_abstractRelatorioBiblioteca.relatorioUsuariosEmAtraso 
					|| ( _abstractRelatorioBiblioteca.relatorioUsuariosEmAtraso  && !_abstractRelatorioBiblioteca.consultaServidor ) }">

				<c:forEach var="catg" items="#{_abstractRelatorioBiblioteca.resultados}" >
				
					<c:set var="totalUsuariosPorCategoria" value="${0}" />
					<c:set var="totalItensPorCategoria" value="${0}" />
					
					<tr><td style="border: none;">&nbsp;</td></tr>
					<tr style="border:3px solid #666666; background: #DDDDDD"><th>Categoria: <h:outputText value="#{catg.key}" /></th></tr>
					
					<c:forEach var="bibl" items="#{catg.value}" >
						
						<tr><td style="border: none;">&nbsp;</td></tr>
						<tr style="border:2px solid #666666; background: #EEEEEE"><th>Biblioteca: <h:outputText value="#{bibl.key}" /></th></tr>
						
						<c:set var="totalItensPorBiblioteca" value="${0}" />
						<c:set var="totalUsuariosPorBiblioteca" value="${0}"/>
						
						<c:forEach var="linha" items="#{bibl.value}" >
	
							<%-- Exibe as informa��es do usu�rio --%>
							<c:if test="${idUsuario != linha.id}">
								<c:set var="totalUsuariosPorBiblioteca" value="${totalUsuariosPorBiblioteca + 1}"/>
								
								<tr><td style="border:none;">&nbsp;</td></tr>
								<c:set var="idUsuario" value="${linha.id}"/>
								<tr>
									<td style="border:1px solid #666666;background:#F5F5F5;">
										<strong>Nome:</strong> ${linha.nome}
										
										<c:if test="${not empty linha.matricula}">
											<strong>Matr�cula:</strong> ${linha.matricula}
										</c:if>
										<c:if test="${not empty linha.siape}">
											<strong>SIAPE:</strong> ${linha.siape}
										</c:if>
										<c:if test="${empty linha.matricula and empty linha.siape and not empty linha.cpfCnpj}">
											<strong>CPF:</strong> ${linha.cpfCnpj}
										</c:if>
										
										<c:if test='${linha.usuarioExterno}'>
											- <strong>Usu�rio Externo</strong>
										</c:if>
										
									</td>
								</tr>
							</c:if>
	
							<c:if test="${idEmprestimo != linha.idEmprestimo}">
								<c:set var="idEmprestimo" value="${linha.idEmprestimo}"/>
								
								<%-- Informa��es do empr�stimo com multa ou suspens�o (dependendo do relat�rio escolhido)--%>
								<c:if test="${not empty linha.dataEmprestimo}">
								
									<c:set var="totalItensPorBiblioteca" value="${totalItensPorBiblioteca + 1}"/>
										
									<tr>
										<td>
											<strong>C�d. Barras:</strong> ${linha.codigoBarras }
											<br/>
											<strong>Material: </strong> 
											${linha.titulo}
											<c:if test='${linha.autor != "null"}'>
												- ${linha.autor}
											</c:if>
											
											<br/>
											<strong>Data do Empr�stimo:</strong> <ufrn:format type="data" valor="${linha.dataEmprestimo}" />
											<strong>Prazo:</strong> <ufrn:format type="dataHora" valor="${linha.prazo}" />
											<c:if test="${not empty linha.dataDevolucao}">
												<strong>Data de devolu��o: </strong> <ufrn:format type="dataHora" valor="${linha.dataDevolucao}" />
											</c:if>
											
											
											<c:if test="${linha.punicaoPorSuspensao}">
												<c:if test="${ not empty linha.inicioSuspensao }">
													<br/> <strong>Suspens�o: </strong>
														de <ufrn:format type="data" valor="${linha.inicioSuspensao}" /> at�
														<ufrn:format type="data" valor="${linha.prazoSuspensao}" />
												</c:if>
											</c:if>
											
											<c:if test="${! linha.punicaoPorSuspensao}">
												<c:if test="${ not empty linha.valorMulta }">
													<br/><strong>Valor da Multa: </strong> <ufrn:format type="moeda" valor="${linha.valorMulta}"></ufrn:format> 
												</c:if>
											</c:if>
											
										</td>
									</tr>
								</c:if>							
							</c:if>
							
							<%-- Informa��es da suspens�o ou multa manual (dependendo do relat�rio escolhido) --%>
							<c:if test="${ linha.idEmprestimo == 0 }">
								<tr>
									<td>
										<c:if test="${linha.punicaoPorSuspensao}">
											<strong>SUSPENS�O MANUAL. </strong>
											<c:if test="${ not empty linha.suspensaoManualCadastradaPor }">
												<strong>Cadastrada por: </strong> <h:outputText value="#{linha.suspensaoManualCadastradaPor}" /> <br/>
											</c:if>
											<strong>Suspens�o: </strong>
												de <ufrn:format type="data" valor="${linha.inicioSuspensao}" /> at�
												<ufrn:format type="data" valor="${linha.prazoSuspensao}" />
											<c:if test="${ not empty linha.motivoPunicao }">
												<br/> <strong>Motivo: </strong> <h:outputText value="#{linha.motivoPunicao}" />
											</c:if>
										</c:if>
										
										<c:if test="${! linha.punicaoPorSuspensao}">
											<strong>MULTA MANUAL. </strong>
											<c:if test="${ not empty linha.suspensaoManualCadastradaPor }">
												<strong>Cadastrada por: </strong> <h:outputText value="#{linha.suspensaoManualCadastradaPor}" /> <br/>
											</c:if>
											<strong>Valor da Multa: </strong> <ufrn:format type="moeda" valor="${linha.valorMulta}" /> 
											<c:if test="${ not empty linha.motivoPunicao }">
												<br/> <strong>Motivo: </strong> <h:outputText value="#{linha.motivoPunicao}" />
											</c:if>
										</c:if>
										
									</td>
								</tr>
							</c:if>
							
						</c:forEach>
						
						<%-- Total por biblioteca --%>
						<tr><td style="border: none;">&nbsp;</td></tr>
						<tr style="border:2px solid #666666; background: #FAFAFA; font-style: italic;">
							<th>
								Total (<h:outputText value="#{catg.key}" />, <h:outputText value="#{bibl.key}" />):
									${totalUsuariosPorBiblioteca} usu�rio${ totalUsuariosPorBiblioteca > 1 ? 's' : '' } /
									${totalItensPorBiblioteca} empr�stimo${ totalItensPorBiblioteca == 1 ? '' : 's' }
							</th>
						</tr>
						
						<c:set var="totalUsuariosPorCategoria" value="${totalUsuariosPorCategoria + totalUsuariosPorBiblioteca}" />
						<c:set var="totalItensPorCategoria" value="${totalItensPorCategoria + totalItensPorBiblioteca}" />
						
					</c:forEach>
					
					<%-- Total por categoria --%>
					<tr><td style="border: none;">&nbsp;</td></tr>
					<tr style="border:3px solid #666666; background: #EEEEEE; font-style: italic;">
						<th>
							Total (<h:outputText value="#{catg.key}" />):
							${totalUsuariosPorCategoria} usu�rio${ totalUsuariosPorCategoria > 1 ? 's' : '' } /
							${totalItensPorCategoria} empr�stimo${ totalItensPorCategoria == 1 ? '' : 's' }
						</th>
					</tr>
					
					<c:set var="totalUsuariosGeral" value="${totalUsuariosGeral + totalUsuariosPorCategoria}" />
					<c:set var="totalItensGeral" value="${totalItensGeral + totalItensPorCategoria}" />
					
				</c:forEach>

			</c:if>
						
			<c:if test="${ _abstractRelatorioBiblioteca.relatorioUsuariosEmAtraso && _abstractRelatorioBiblioteca.consultaServidor}">
				<c:forEach var="unid" items="#{_abstractRelatorioBiblioteca.resultadosServidores}" >
					
					<tr><td style="border: none;">&nbsp;</td></tr>
					<tr style="border:3px solid #666666; background: #BBBBBB"><th>Unidade: <h:outputText value="#{unid.key}" /></th></tr>
			
					<c:set var="totalUsuariosPorUnidade" value="${0}" />
					<c:set var="totalItensPorUnidade" value="${0}" />
					
					<c:forEach var="catg" items="#{unid.value}" >
					
						<c:set var="totalUsuariosPorCategoria" value="${0}" />
						<c:set var="totalItensPorCategoria" value="${0}" />
						
						<tr><td style="border: none;">&nbsp;</td></tr>
						<tr style="border:3px solid #666666; background: #DDDDDD"><th>Categoria: <h:outputText value="#{catg.key}" /></th></tr>
						
						<c:forEach var="bibl" items="#{catg.value}" >
							
							<tr><td style="border: none;">&nbsp;</td></tr>
							<tr style="border:2px solid #666666; background: #EEEEEE"><th>Biblioteca: <h:outputText value="#{bibl.key}" /></th></tr>
							
							<c:set var="totalItensPorBiblioteca" value="${0}" />
							<c:set var="totalUsuariosPorBiblioteca" value="${0}"/>
							
							<c:forEach var="linha" items="#{bibl.value}" >
		
								<%-- Exibe as informa��es do usu�rio --%>
								<c:if test="${idUsuario != linha.id}">
									<c:set var="totalUsuariosPorBiblioteca" value="${totalUsuariosPorBiblioteca + 1}"/>
									
									<tr><td style="border:none;">&nbsp;</td></tr>
									<c:set var="idUsuario" value="${linha.id}"/>
									<tr>
										<td style="border:1px solid #666666;background:#F5F5F5;">
											<strong>Nome:</strong> ${linha.nome}
											
											<c:if test="${not empty linha.matricula}">
												<strong>Matr�cula:</strong> ${linha.matricula}
											</c:if>
											<c:if test="${not empty linha.siape}">
												<strong>SIAPE:</strong> ${linha.siape}
											</c:if>
											<c:if test="${empty linha.matricula and empty linha.siape and not empty linha.cpfCnpj}">
												<strong>CPF:</strong> ${linha.cpfCnpj}
											</c:if>
											
											<c:if test='${linha.usuarioExterno}'>
												- <strong>Usu�rio Externo</strong>
											</c:if>
											
										</td>
									</tr>
								</c:if>
		
								<c:if test="${idEmprestimo != linha.idEmprestimo}">
									<c:set var="idEmprestimo" value="${linha.idEmprestimo}"/>
									
									<%-- Informa��es do empr�stimo com multa ou suspens�o (dependendo do relat�rio escolhido)--%>
									<c:if test="${not empty linha.dataEmprestimo}">
									
										<c:set var="totalItensPorBiblioteca" value="${totalItensPorBiblioteca + 1}"/>
											
										<tr>
											<td>
												<strong>C�d. Barras:</strong> ${linha.codigoBarras }
												<br/>
												<strong>Material: </strong> 
												${linha.titulo}
												<c:if test='${linha.autor != "null"}'>
													- ${linha.autor}
												</c:if>
												
												<br/>
												<strong>Data do Empr�stimo:</strong> <ufrn:format type="data" valor="${linha.dataEmprestimo}" />
												<strong>Prazo:</strong> <ufrn:format type="dataHora" valor="${linha.prazo}" />
												<c:if test="${not empty linha.dataDevolucao}">
													<strong>Data de devolu��o: </strong> <ufrn:format type="dataHora" valor="${linha.dataDevolucao}" />
												</c:if>
												
												
												<c:if test="${linha.punicaoPorSuspensao}">
													<c:if test="${ not empty linha.inicioSuspensao }">
														<br/> <strong>Suspens�o: </strong>
															de <ufrn:format type="data" valor="${linha.inicioSuspensao}" /> at�
															<ufrn:format type="data" valor="${linha.prazoSuspensao}" />
													</c:if>
												</c:if>
												
												<c:if test="${! linha.punicaoPorSuspensao}">
													<c:if test="${ not empty linha.valorMulta }">
														<br/><strong>Valor da Multa: </strong> <ufrn:format type="moeda" valor="${linha.valorMulta}"></ufrn:format> 
													</c:if>
												</c:if>
												
											</td>
										</tr>
									</c:if>							
								</c:if>
								
								<%-- Informa��es da suspens�o ou multa manual (dependendo do relat�rio escolhido) --%>
								<c:if test="${ linha.idEmprestimo == 0 }">
									<tr>
										<td>
											<c:if test="${linha.punicaoPorSuspensao}">
												<strong>SUSPENS�O MANUAL. </strong>
												<c:if test="${ not empty linha.suspensaoManualCadastradaPor }">
													<strong>Cadastrada por: </strong> <h:outputText value="#{linha.suspensaoManualCadastradaPor}" /> <br/>
												</c:if>
												<strong>Suspens�o: </strong>
													de <ufrn:format type="data" valor="${linha.inicioSuspensao}" /> at�
													<ufrn:format type="data" valor="${linha.prazoSuspensao}" />
												<c:if test="${ not empty linha.motivoPunicao }">
													<br/> <strong>Motivo: </strong> <h:outputText value="#{linha.motivoPunicao}" />
												</c:if>
											</c:if>
											
											<c:if test="${! linha.punicaoPorSuspensao}">
												<strong>MULTA MANUAL. </strong>
												<c:if test="${ not empty linha.suspensaoManualCadastradaPor }">
													<strong>Cadastrada por: </strong> <h:outputText value="#{linha.suspensaoManualCadastradaPor}" /> <br/>
												</c:if>
												<strong>Valor da Multa: </strong> <ufrn:format type="moeda" valor="${linha.valorMulta}" /> 
												<c:if test="${ not empty linha.motivoPunicao }">
													<br/> <strong>Motivo: </strong> <h:outputText value="#{linha.motivoPunicao}" />
												</c:if>
											</c:if>
											
										</td>
									</tr>
								</c:if>
								
							</c:forEach>
							
							<%-- Total por biblioteca --%>
							<tr><td style="border: none;">&nbsp;</td></tr>
							<tr style="border:2px solid #666666; background: #FAFAFA; font-style: italic;">
								<th>
									Total (<h:outputText value="#{unid.key}" />, <h:outputText value="#{catg.key}" />, <h:outputText value="#{bibl.key}" />):
										${totalUsuariosPorBiblioteca} usu�rio${ totalUsuariosPorBiblioteca > 1 ? 's' : '' } /
										${totalItensPorBiblioteca} empr�stimo${ totalItensPorBiblioteca == 1 ? '' : 's' }
								</th>
							</tr>
							
							<c:set var="totalUsuariosPorCategoria" value="${totalUsuariosPorCategoria + totalUsuariosPorBiblioteca}" />
							<c:set var="totalItensPorCategoria" value="${totalItensPorCategoria + totalItensPorBiblioteca}" />
							
						</c:forEach>
						
						<%-- Total por categoria --%>
						<tr><td style="border: none;">&nbsp;</td></tr>
						<tr style="border:3px solid #666666; background: #EEEEEE; font-style: italic;">
							<th>
								Total (<h:outputText value="#{unid.key}" />, <h:outputText value="#{catg.key}" />):
								${totalUsuariosPorCategoria} usu�rio${ totalUsuariosPorCategoria > 1 ? 's' : '' } /
								${totalItensPorCategoria} empr�stimo${ totalItensPorCategoria == 1 ? '' : 's' }
							</th>
						</tr>
						
						<c:set var="totalUsuariosPorUnidade" value="${totalUsuariosPorUnidade + totalUsuariosPorCategoria}" />
						<c:set var="totalItensPorUnidade" value="${totalItensPorUnidade + totalItensPorCategoria}" />
						
					</c:forEach>
					
					<%-- Total por unidade --%>
					<tr><td style="border: none;">&nbsp;</td></tr>
					<tr style="border:3px solid #666666; background: #DADADA; font-style: italic;">
						<th>
							Total (<h:outputText value="#{unid.key}" />):
							${totalUsuariosPorUnidade} usu�rio${ totalUsuariosPorUnidade > 1 ? 's' : '' } /
							${totalItensPorUnidade} empr�stimo${ totalItensPorUnidade == 1 ? '' : 's' }
						</th>
					</tr>
						
					<c:set var="totalUsuariosGeral" value="${totalUsuariosGeral + totalUsuariosPorUnidade}" />
					<c:set var="totalItensGeral" value="${totalItensGeral + totalItensPorUnidade}" />
				
				</c:forEach>
			</c:if>
			
			<%-- Total geral --%>
			<tr><td style="border: none;">&nbsp;</td></tr>
			<tr style="border:4px solid #666666; background: #DDDDDD; font-style: italic;">
				<th>
					Total Geral:
					${totalUsuariosGeral} usu�rio${ totalUsuariosGeral > 1 ? 's' : '' } /
					${totalItensGeral} empr�stimo${ totalItensGeral == 1 ? '' : 's' }
				</th>
			</tr>
			
		</table>

	</h:form>	
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>