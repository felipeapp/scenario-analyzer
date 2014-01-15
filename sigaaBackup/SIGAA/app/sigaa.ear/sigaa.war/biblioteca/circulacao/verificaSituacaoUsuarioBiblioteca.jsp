<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2> <ufrn:subSistema /> &gt; Verificar a Situação do Usuário </h2>

	<div class="descricaoOperacao">
		<c:if test="${! verificaSituacaoUsuarioBibliotecaMBean.verificandoMinhaSituacao}">
			<p>Prezado operador,</p>
			<p>Nesta página, é possível verificar a situação do usuário selecionado em relação aos empréstimos nas bibliotecas do sistema e emitir o documento de quitação.</p>
			<br/>
			<p><strong>ATENÇÃO:</strong> Caso seja emitido o documento de quitação, o respectivo vínculo será finalizado, <strong>o usuário não poderá mais realizar empréstimo com ele</strong>. </p>
			<br/>
			<p><strong>Observação:</strong> Se o usuário possuir outro vínculo com a instituição, será necessário que ele se recadastre para começar a realizar empréstimos com o novo vínculo. </p>
		</c:if>
		<c:if test="${verificaSituacaoUsuarioBibliotecaMBean.verificandoMinhaSituacao}">
			<p>Prezado(a) usuário(a),</p>
			<p>Nesta página, é possível verificar sua situação em relação aos empréstimos nas bibliotecas do sistema e emitir o documento de quitação.</p>
			<p><strong>ATENÇÃO:</strong> Caso seja emitido o documento de quitação, o respectivo vínculo será finalizado, <strong>você não poderá mais realizar empréstimo com ele</strong>. </p>
			<br/>
			<p><strong>Observação:</strong> Caso possua outro vínculo com a instituição, para realizar empréstimos com esse outro vínculo, será necessário se recadastrar. </p>
		</c:if>
	</div>

	<%-- Mantém as informações do usuário e a situação entre as requisições --%>
	<a4j:keepAlive beanName="verificaSituacaoUsuarioBibliotecaMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />

	<h:form id="formVerificaSituacaoUsario">

				
			<%-- Mostra as informações do usuário --%>

			<%-- DIV com a foto --%>

			<table class="visualizacao" style="width:100%;">
				
				<caption>Situação do Usuário nas Bibliotecas</caption>
			
			
				<%-- INFORMAÇÕES PESSOAIS DO USUÁRIO  --%>
				<tr>
					<td td colspan="2">
						<c:set var="_infoUsuarioCirculacao" value="${verificaSituacaoUsuarioBibliotecaMBean.informacoesUsuarioBiblioteca}" scope="request" />
						<c:set var="_transparente" value="true" scope="request" />
						<c:set var="_mostrarVinculo" value="false" scope="request" />
						<%@include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%>
					</td>
				</tr>
				
		
				<tr>
					<td colspan="2">
				
						<%-- INFORMAÇÕES DA SITUAÇÃO DO USUÁRIO  --%>
		
		
						
						<div style="font-weight:bold;text-align:center;">
						
							<c:forEach items="#{verificaSituacaoUsuarioBibliotecaMBean.situacoes}" var="situacao">
								<c:if test="${verificaSituacaoUsuarioBibliotecaMBean.situacaoSemPendencias}">  <%-- Se situação está OK --%>
									<h:outputText value="#{situacao.descricaoCompleta}" style="color:green;" /> <br/>
								</c:if>
				
								<c:if test="${ not verificaSituacaoUsuarioBibliotecaMBean.situacaoSemPendencias}"> <%-- Se situação NÃO está OK --%>
									<h:outputText value="#{situacao.descricaoCompleta}" style="color:red;" /> <br/>
								</c:if>
							</c:forEach>
							
						</div>
					</td>
				</tr>
					
				<%-- EMPRÉSTIMOS DO USUÁRIO POR VÍNCULO --%>
				
				<c:if test="${! verificaSituacaoUsuarioBibliotecaMBean.usuarioNaoPossuiNenhumVinculo}">
				
					<tr>
						<td colspan="2">	
								
								<table class="subFormulario" style="width:100%; margin-bottom: 30px;">
									<caption>Vínculos Ativos do Usuário</caption>
									
									<c:if test="${fn:length(verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoAtivos) > 0}">
									<c:forEach items="#{verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoAtivos}"  var="infoEmprestimosVinculo">
										
										<tr style="background-color:#DEDFE3; font-weight: bold;">
											<td colspan="5" style=" ${infoEmprestimosVinculo.usuarioBiblioteca.quitado ? 'color: gray' : ( infoEmprestimosVinculo.vinculoAtualmenteUsado ? 'color: blue' : '' )}; " >
													${infoEmprestimosVinculo.usuarioBiblioteca.vinculo.descricao}  
													${infoEmprestimosVinculo.vinculoNuncaUsado ? "[Vínculo Nunca Utilizado]" : " "} 
													${infoEmprestimosVinculo.vinculoAtualmenteUsado ? "[ VÍNCULO ATUAL ]" : ""}  
													${infoEmprestimosVinculo.usuarioBiblioteca.quitado ? " [ VÍNCULO QUITADO ]" : ""} 
											</td>
											
											
											<td colspan="2" style="text-align: right; "  >
												
												<h:commandButton value="Emitir Quitação" id="cmdEmiteQuitacaoComConfirmacaoEncerramento"
													actionListener="#{verificaSituacaoUsuarioBibliotecaMBean.configuraVinculoEscolhido}" 
													action="#{verificaSituacaoUsuarioBibliotecaMBean.emitirDocumentoQuitacao}"
													rendered="#{infoEmprestimosVinculo.quantidadeEmprestimosAtivos == 0 && verificaSituacaoUsuarioBibliotecaMBean.podeEmitirDocumentoQuitacao && ! infoEmprestimosVinculo.usuarioBiblioteca.quitado }"
													onclick="return confirm('Confirma o Encerramento deste Vínculo ? Não será mais possível realizar empréstimos com ele ! ');" >
													
													<f:attribute name="idUsuarioBibliotecaSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.id}" />
													<f:attribute name="vinculoSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.vinculo}" />
													<f:attribute name="identificacaoVinculoSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.identificacaoVinculo}" />
														
												</h:commandButton> 
												
												<h:commandButton value="Emitir Quitação" id="cmdEmiteQuitacaoSemConfirmacaoEncerramento"
													actionListener="#{verificaSituacaoUsuarioBibliotecaMBean.configuraVinculoEscolhido}" 
													action="#{verificaSituacaoUsuarioBibliotecaMBean.emitirDocumentoQuitacao}"
													rendered="#{infoEmprestimosVinculo.quantidadeEmprestimosAtivos == 0 && verificaSituacaoUsuarioBibliotecaMBean.podeEmitirDocumentoQuitacao && infoEmprestimosVinculo.usuarioBiblioteca.quitado }">
													
													<f:attribute name="idUsuarioBibliotecaSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.id}" />
													<f:attribute name="vinculoSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.vinculo}" />
													<f:attribute name="identificacaoVinculoSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.identificacaoVinculo}" />
														
												</h:commandButton> 
												
											</td>
											
										</tr>
										
										<tr style="background-color:#DEDFE3; font-weight: bold;">
											<td colspan="7" style='${infoEmprestimosVinculo.usuarioBiblioteca.quitado ? "color: gray" : ""};'>( ${infoEmprestimosVinculo.descricaoDetalhadaVinculo} )  </td>
										</tr>
										
										<c:if test="${infoEmprestimosVinculo.quantidadeEmprestimosAtivos > 0}">
											<tr style="background-color:#EEEEEE;">
												<th style="text-align: center;">Cód. Barras</th>
												<th style="text-align: center;">Data de Empréstimo</th>
												<th style="text-align: center;">Data de Renovação</th>
												<th style="text-align: center;">Prazo para Devolução</th>
												<th style="text-align:   left;">Biblioteca</th>
												<th style="text-align: center;">Renovável</th>
											</tr>
										</c:if>
										
										<c:if test="${infoEmprestimosVinculo.quantidadeEmprestimosAtivos == 0}">
											<tr>
												<td colspan="7" style="color:green; text-align: center;">Usuário não possui empréstimos ativos com esse vínculo</td>
											</tr>
										</c:if>
										
										<c:if test="${infoEmprestimosVinculo.quantidadeEmprestimosAtivos > 0}">
											<c:forEach items="#{infoEmprestimosVinculo.emprestimosAtivos}"  var="e">
												<tr class='${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}'>
													<td style="text-align: center;">${e.material.codigoBarras}</td>
													<td style="text-align: center;"><ufrn:format type="dataHora" valor="${e.dataEmprestimo}" /></td>
													<td style="text-align: center;">
														<c:if test="${e.dataRenovacao == null}">-</c:if>
														<c:if test="${e.dataRenovacao != null}">
															<ufrn:format type="dataHora" valor="${e.dataRenovacao}" />
														</c:if>
													</td>
													<td style='text-align: center;${e.atrasado ? "color:#FF0000;" : ""}'><ufrn:format type="dataHora" valor="${e.prazo}" /></td>
													<td style="text-align:   left;">${e.material.biblioteca.descricao}</td>
													<td style="text-align: center;">${e.podeRenovar && ! e.atrasado ? 'SIM' : 'NÃO'}</td>
												</tr>
											</c:forEach> 
										</c:if>
										
									</c:forEach>
									</c:if>
								</table>
							
						</td>
					</tr>
					
					<tr>
						<td colspan="2">	
								
								<table class="subFormulario" style="width:100%; margin-bottom: 30px;">
									<caption>Vínculos Inativos do Usuário</caption>
									
									<c:if test="${fn:length(verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoInativos) > 0}">
									<c:forEach items="#{verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoInativos}"  var="infoEmprestimosVinculo">
										
										<tr style="background-color:#DEDFE3; font-weight: bold;">
											<td colspan="5" style=" ${infoEmprestimosVinculo.usuarioBiblioteca.quitado ? 'color: gray' : ( infoEmprestimosVinculo.vinculoAtualmenteUsado ? 'color: blue' : '' )}; " >
													${infoEmprestimosVinculo.usuarioBiblioteca.vinculo.descricao}  
													${infoEmprestimosVinculo.vinculoNuncaUsado ? "[Vínculo Nunca Utilizado]" : " "} 
													${infoEmprestimosVinculo.vinculoAtualmenteUsado ? "[ VÍNCULO ATUAL ]" : ""}  
													${infoEmprestimosVinculo.usuarioBiblioteca.quitado ? " [ VÍNCULO QUITADO ]" : ""} 
											</td>
											
											
											<td colspan="2" style="text-align: right; "  >
												
												<h:commandButton value="Emitir Quitação" id="cmdEmiteQuitacaoComConfirmacaoEncerramentoVinculoInativo"
													actionListener="#{verificaSituacaoUsuarioBibliotecaMBean.configuraVinculoEscolhido}" 
													action="#{verificaSituacaoUsuarioBibliotecaMBean.emitirDocumentoQuitacao}"
													rendered="#{infoEmprestimosVinculo.quantidadeEmprestimosAtivos == 0 && verificaSituacaoUsuarioBibliotecaMBean.podeEmitirDocumentoQuitacao && ! infoEmprestimosVinculo.usuarioBiblioteca.quitado }"
													onclick="return confirm('Confirma o Encerramento deste Vínculo ? Não será mais possível realizar empréstimos com ele ! ');" >
													
													<f:attribute name="idUsuarioBibliotecaSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.id}" />
													<f:attribute name="vinculoSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.vinculo}" />
													<f:attribute name="identificacaoVinculoSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.identificacaoVinculo}" />
														
												</h:commandButton> 
												
												<h:commandButton value="Emitir Quitação" id="cmdEmiteQuitacaoSemConfirmacaoEncerramentoVinculoInativo"
													actionListener="#{verificaSituacaoUsuarioBibliotecaMBean.configuraVinculoEscolhido}" 
													action="#{verificaSituacaoUsuarioBibliotecaMBean.emitirDocumentoQuitacao}"
													rendered="#{infoEmprestimosVinculo.quantidadeEmprestimosAtivos == 0 && verificaSituacaoUsuarioBibliotecaMBean.podeEmitirDocumentoQuitacao && infoEmprestimosVinculo.usuarioBiblioteca.quitado }">
													
													<f:attribute name="idUsuarioBibliotecaSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.id}" />
													<f:attribute name="vinculoSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.vinculo}" />
													<f:attribute name="identificacaoVinculoSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.identificacaoVinculo}" />
														
												</h:commandButton> 
												
											</td>
											
										</tr>
										
										<tr style="background-color:#DEDFE3; font-weight: bold;">
											<td colspan="7" style='${infoEmprestimosVinculo.usuarioBiblioteca.quitado ? "color: gray" : ""};'>( ${infoEmprestimosVinculo.descricaoDetalhadaVinculo} )  </td>
										</tr>
										
										<c:if test="${infoEmprestimosVinculo.quantidadeEmprestimosAtivos > 0}">
											<tr style="background-color:#EEEEEE;">
												<th style="text-align: center;">Cód. Barras</th>
												<th style="text-align: center;">Data de Empréstimo</th>
												<th style="text-align: center;">Data de Renovação</th>
												<th style="text-align: center;">Prazo para Devolução</th>
												<th style="text-align:   left;">Biblioteca</th>
												<th style="text-align: center;">Renovável</th>
											</tr>
										</c:if>
										
										<c:if test="${infoEmprestimosVinculo.quantidadeEmprestimosAtivos == 0}">
											<tr>
												<td colspan="7" style="color:green; text-align: center;">Usuário não possui empréstimos ativos com esse vínculo</td>
											</tr>
										</c:if>
										
										<c:if test="${infoEmprestimosVinculo.quantidadeEmprestimosAtivos > 0}">
											<c:forEach items="#{infoEmprestimosVinculo.emprestimosAtivos}"  var="e">
												<tr class='${s.index % 2 == 0 ? "linhaPar" : "linhaImpar"}'>
													<td style="text-align: center;">${e.material.codigoBarras}</td>
													<td style="text-align: center;"><ufrn:format type="dataHora" valor="${e.dataEmprestimo}" /></td>
													<td style="text-align: center;">
														<c:if test="${e.dataRenovacao == null}">-</c:if>
														<c:if test="${e.dataRenovacao != null}">
															<ufrn:format type="dataHora" valor="${e.dataRenovacao}" />
														</c:if>
													</td>
													<td style='text-align: center;${e.atrasado ? "color:#FF0000;" : ""}'><ufrn:format type="dataHora" valor="${e.prazo}" /></td>
													<td style="text-align:   left;">${e.material.biblioteca.descricao}</td>
													<td style="text-align: center;">${e.podeRenovar && ! e.atrasado ? 'SIM' : 'NÃO'}</td>
												</tr>
											</c:forEach> 
										</c:if>
										
									</c:forEach>
									</c:if>
								</table>
							
						</td>
					</tr>
					
				</c:if>
				
				
				<c:if test="${verificaSituacaoUsuarioBibliotecaMBean.usuarioNaoPossuiNenhumVinculo}">
					<tr>
						<td colspan="2" style="color:green; text-align: center;">Usuário nunca utilizou, nem  possui vínculos para utilizar a biblioteca</td>
					</tr>
				</c:if> 
				
				
				<tfoot>
					<tr>
						<td colspan="2" style="text-align:center;">
						
						<h:commandButton value="Emitir Quitação" action="#{verificaSituacaoUsuarioBibliotecaMBean.emitirDocumentoQuitacao}" rendered="#{verificaSituacaoUsuarioBibliotecaMBean.usuarioNaoPossuiNenhumVinculo}" />
						<h:commandButton value="<< Voltar" action="#{verificaSituacaoUsuarioBibliotecaMBean.voltaTelaBusca}" rendered="#{! verificaSituacaoUsuarioBibliotecaMBean.verificandoMinhaSituacao}"/>
						<h:commandButton value="<< Voltar" action="#{verificaSituacaoUsuarioBibliotecaMBean.cancelar}" rendered="#{verificaSituacaoUsuarioBibliotecaMBean.verificandoMinhaSituacao} }"/>
						
						</td>
					</tr>
				</tfoot> 
			</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>