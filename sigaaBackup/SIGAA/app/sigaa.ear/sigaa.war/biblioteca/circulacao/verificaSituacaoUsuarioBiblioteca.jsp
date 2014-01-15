<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2> <ufrn:subSistema /> &gt; Verificar a Situa��o do Usu�rio </h2>

	<div class="descricaoOperacao">
		<c:if test="${! verificaSituacaoUsuarioBibliotecaMBean.verificandoMinhaSituacao}">
			<p>Prezado operador,</p>
			<p>Nesta p�gina, � poss�vel verificar a situa��o do usu�rio selecionado em rela��o aos empr�stimos nas bibliotecas do sistema e emitir o documento de quita��o.</p>
			<br/>
			<p><strong>ATEN��O:</strong> Caso seja emitido o documento de quita��o, o respectivo v�nculo ser� finalizado, <strong>o usu�rio n�o poder� mais realizar empr�stimo com ele</strong>. </p>
			<br/>
			<p><strong>Observa��o:</strong> Se o usu�rio possuir outro v�nculo com a institui��o, ser� necess�rio que ele se recadastre para come�ar a realizar empr�stimos com o novo v�nculo. </p>
		</c:if>
		<c:if test="${verificaSituacaoUsuarioBibliotecaMBean.verificandoMinhaSituacao}">
			<p>Prezado(a) usu�rio(a),</p>
			<p>Nesta p�gina, � poss�vel verificar sua situa��o em rela��o aos empr�stimos nas bibliotecas do sistema e emitir o documento de quita��o.</p>
			<p><strong>ATEN��O:</strong> Caso seja emitido o documento de quita��o, o respectivo v�nculo ser� finalizado, <strong>voc� n�o poder� mais realizar empr�stimo com ele</strong>. </p>
			<br/>
			<p><strong>Observa��o:</strong> Caso possua outro v�nculo com a institui��o, para realizar empr�stimos com esse outro v�nculo, ser� necess�rio se recadastrar. </p>
		</c:if>
	</div>

	<%-- Mant�m as informa��es do usu�rio e a situa��o entre as requisi��es --%>
	<a4j:keepAlive beanName="verificaSituacaoUsuarioBibliotecaMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />

	<h:form id="formVerificaSituacaoUsario">

				
			<%-- Mostra as informa��es do usu�rio --%>

			<%-- DIV com a foto --%>

			<table class="visualizacao" style="width:100%;">
				
				<caption>Situa��o do Usu�rio nas Bibliotecas</caption>
			
			
				<%-- INFORMA��ES PESSOAIS DO USU�RIO  --%>
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
				
						<%-- INFORMA��ES DA SITUA��O DO USU�RIO  --%>
		
		
						
						<div style="font-weight:bold;text-align:center;">
						
							<c:forEach items="#{verificaSituacaoUsuarioBibliotecaMBean.situacoes}" var="situacao">
								<c:if test="${verificaSituacaoUsuarioBibliotecaMBean.situacaoSemPendencias}">  <%-- Se situa��o est� OK --%>
									<h:outputText value="#{situacao.descricaoCompleta}" style="color:green;" /> <br/>
								</c:if>
				
								<c:if test="${ not verificaSituacaoUsuarioBibliotecaMBean.situacaoSemPendencias}"> <%-- Se situa��o N�O est� OK --%>
									<h:outputText value="#{situacao.descricaoCompleta}" style="color:red;" /> <br/>
								</c:if>
							</c:forEach>
							
						</div>
					</td>
				</tr>
					
				<%-- EMPR�STIMOS DO USU�RIO POR V�NCULO --%>
				
				<c:if test="${! verificaSituacaoUsuarioBibliotecaMBean.usuarioNaoPossuiNenhumVinculo}">
				
					<tr>
						<td colspan="2">	
								
								<table class="subFormulario" style="width:100%; margin-bottom: 30px;">
									<caption>V�nculos Ativos do Usu�rio</caption>
									
									<c:if test="${fn:length(verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoAtivos) > 0}">
									<c:forEach items="#{verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoAtivos}"  var="infoEmprestimosVinculo">
										
										<tr style="background-color:#DEDFE3; font-weight: bold;">
											<td colspan="5" style=" ${infoEmprestimosVinculo.usuarioBiblioteca.quitado ? 'color: gray' : ( infoEmprestimosVinculo.vinculoAtualmenteUsado ? 'color: blue' : '' )}; " >
													${infoEmprestimosVinculo.usuarioBiblioteca.vinculo.descricao}  
													${infoEmprestimosVinculo.vinculoNuncaUsado ? "[V�nculo Nunca Utilizado]" : " "} 
													${infoEmprestimosVinculo.vinculoAtualmenteUsado ? "[ V�NCULO ATUAL ]" : ""}  
													${infoEmprestimosVinculo.usuarioBiblioteca.quitado ? " [ V�NCULO QUITADO ]" : ""} 
											</td>
											
											
											<td colspan="2" style="text-align: right; "  >
												
												<h:commandButton value="Emitir Quita��o" id="cmdEmiteQuitacaoComConfirmacaoEncerramento"
													actionListener="#{verificaSituacaoUsuarioBibliotecaMBean.configuraVinculoEscolhido}" 
													action="#{verificaSituacaoUsuarioBibliotecaMBean.emitirDocumentoQuitacao}"
													rendered="#{infoEmprestimosVinculo.quantidadeEmprestimosAtivos == 0 && verificaSituacaoUsuarioBibliotecaMBean.podeEmitirDocumentoQuitacao && ! infoEmprestimosVinculo.usuarioBiblioteca.quitado }"
													onclick="return confirm('Confirma o Encerramento deste V�nculo ? N�o ser� mais poss�vel realizar empr�stimos com ele ! ');" >
													
													<f:attribute name="idUsuarioBibliotecaSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.id}" />
													<f:attribute name="vinculoSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.vinculo}" />
													<f:attribute name="identificacaoVinculoSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.identificacaoVinculo}" />
														
												</h:commandButton> 
												
												<h:commandButton value="Emitir Quita��o" id="cmdEmiteQuitacaoSemConfirmacaoEncerramento"
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
												<th style="text-align: center;">C�d. Barras</th>
												<th style="text-align: center;">Data de Empr�stimo</th>
												<th style="text-align: center;">Data de Renova��o</th>
												<th style="text-align: center;">Prazo para Devolu��o</th>
												<th style="text-align:   left;">Biblioteca</th>
												<th style="text-align: center;">Renov�vel</th>
											</tr>
										</c:if>
										
										<c:if test="${infoEmprestimosVinculo.quantidadeEmprestimosAtivos == 0}">
											<tr>
												<td colspan="7" style="color:green; text-align: center;">Usu�rio n�o possui empr�stimos ativos com esse v�nculo</td>
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
													<td style="text-align: center;">${e.podeRenovar && ! e.atrasado ? 'SIM' : 'N�O'}</td>
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
									<caption>V�nculos Inativos do Usu�rio</caption>
									
									<c:if test="${fn:length(verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoInativos) > 0}">
									<c:forEach items="#{verificaSituacaoUsuarioBibliotecaMBean.informacoesEmprestimosPorVinculoInativos}"  var="infoEmprestimosVinculo">
										
										<tr style="background-color:#DEDFE3; font-weight: bold;">
											<td colspan="5" style=" ${infoEmprestimosVinculo.usuarioBiblioteca.quitado ? 'color: gray' : ( infoEmprestimosVinculo.vinculoAtualmenteUsado ? 'color: blue' : '' )}; " >
													${infoEmprestimosVinculo.usuarioBiblioteca.vinculo.descricao}  
													${infoEmprestimosVinculo.vinculoNuncaUsado ? "[V�nculo Nunca Utilizado]" : " "} 
													${infoEmprestimosVinculo.vinculoAtualmenteUsado ? "[ V�NCULO ATUAL ]" : ""}  
													${infoEmprestimosVinculo.usuarioBiblioteca.quitado ? " [ V�NCULO QUITADO ]" : ""} 
											</td>
											
											
											<td colspan="2" style="text-align: right; "  >
												
												<h:commandButton value="Emitir Quita��o" id="cmdEmiteQuitacaoComConfirmacaoEncerramentoVinculoInativo"
													actionListener="#{verificaSituacaoUsuarioBibliotecaMBean.configuraVinculoEscolhido}" 
													action="#{verificaSituacaoUsuarioBibliotecaMBean.emitirDocumentoQuitacao}"
													rendered="#{infoEmprestimosVinculo.quantidadeEmprestimosAtivos == 0 && verificaSituacaoUsuarioBibliotecaMBean.podeEmitirDocumentoQuitacao && ! infoEmprestimosVinculo.usuarioBiblioteca.quitado }"
													onclick="return confirm('Confirma o Encerramento deste V�nculo ? N�o ser� mais poss�vel realizar empr�stimos com ele ! ');" >
													
													<f:attribute name="idUsuarioBibliotecaSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.id}" />
													<f:attribute name="vinculoSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.vinculo}" />
													<f:attribute name="identificacaoVinculoSelecionado" value="#{infoEmprestimosVinculo.usuarioBiblioteca.identificacaoVinculo}" />
														
												</h:commandButton> 
												
												<h:commandButton value="Emitir Quita��o" id="cmdEmiteQuitacaoSemConfirmacaoEncerramentoVinculoInativo"
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
												<th style="text-align: center;">C�d. Barras</th>
												<th style="text-align: center;">Data de Empr�stimo</th>
												<th style="text-align: center;">Data de Renova��o</th>
												<th style="text-align: center;">Prazo para Devolu��o</th>
												<th style="text-align:   left;">Biblioteca</th>
												<th style="text-align: center;">Renov�vel</th>
											</tr>
										</c:if>
										
										<c:if test="${infoEmprestimosVinculo.quantidadeEmprestimosAtivos == 0}">
											<tr>
												<td colspan="7" style="color:green; text-align: center;">Usu�rio n�o possui empr�stimos ativos com esse v�nculo</td>
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
													<td style="text-align: center;">${e.podeRenovar && ! e.atrasado ? 'SIM' : 'N�O'}</td>
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
						<td colspan="2" style="color:green; text-align: center;">Usu�rio nunca utilizou, nem  possui v�nculos para utilizar a biblioteca</td>
					</tr>
				</c:if> 
				
				
				<tfoot>
					<tr>
						<td colspan="2" style="text-align:center;">
						
						<h:commandButton value="Emitir Quita��o" action="#{verificaSituacaoUsuarioBibliotecaMBean.emitirDocumentoQuitacao}" rendered="#{verificaSituacaoUsuarioBibliotecaMBean.usuarioNaoPossuiNenhumVinculo}" />
						<h:commandButton value="<< Voltar" action="#{verificaSituacaoUsuarioBibliotecaMBean.voltaTelaBusca}" rendered="#{! verificaSituacaoUsuarioBibliotecaMBean.verificandoMinhaSituacao}"/>
						<h:commandButton value="<< Voltar" action="#{verificaSituacaoUsuarioBibliotecaMBean.cancelar}" rendered="#{verificaSituacaoUsuarioBibliotecaMBean.verificandoMinhaSituacao} }"/>
						
						</td>
					</tr>
				</tfoot> 
			</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>