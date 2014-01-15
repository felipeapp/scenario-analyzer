

		<%-- Página padrão para ser incluída em todas as página que mostram as informações do usuário na circulação   --%>
		<%-- _completo  = true mostra as informação do usuário em 100% da página --%>
		<%-- _transparente  = true mostra as informação do usuário emm uma tabela transparente --%>
		<%-- _inativo  = true mostra as informação do usuário em vermelho --%>
		<%-- _mostrarFoto  = false NÃO  mostra a foto do usuario --%>
		<%-- _observacaoUsuario  = alguma informão extra que se queira mostrar do usuário. --%>
		<%-- _situacoesUsuario  = as situção que o usuário se encontra na biblioteca, com suspensão, com multas, bloqueado. --%>
		<%-- _mostrarVinculo  = mostra as informações do vínculo, se false, mostra apenas os dados pessoas do usuário --%>

		


		<c:if test="${_infoUsuarioCirculacao == null }">
				<div style="width:100%; text-align:center; margin-top:20px; margin-bottom:10px;">
					<h:outputText value=" Usuario Não Encontrado" style="font-weight:bold; color:red; "/>
				</div>
		</c:if>


		<c:if test="${_infoUsuarioCirculacao != null}">


			<%--   Mostra as informacoes do usario --%>

			<table class="listagem tabelaRelatorio" style="width:${_completo == true ? "100%" : "80%"};${_transparente == true ? "border:none; background:none;" : " " } ; margin-bottom: 20px; ">
				
				<tbody style="${_transparente == true ? "border:none; background:none;" : " " } "> 
				
				<tr>
					<c:if test="${empty _mostrarFoto }">
						<td style="width: 15%; height: 150px; text-align: right; ">
							<c:if test="${_infoUsuarioCirculacao.idFoto != null}">
			                	<img width='100' height='120' src="${ configSistema['linkSigaa'] }/sigaa/verFoto?idFoto=${_infoUsuarioCirculacao.idFoto}&key=${ sf:generateArquivoKey(_infoUsuarioCirculacao.idFoto) }" />
							</c:if>							
			
							<c:if test="${_infoUsuarioCirculacao.idFoto == null}">
								<img width='100' height='120' src='${ configSistema['linkSigaa'] }/sigaa/img/no_picture.png' />
							</c:if>
						</td>
					</c:if>
					
					<td width="50%">
					
						<table style="width:100%; ${_inativo == true ? "color:red;" : " "} ">
						
						
							<tr>
								<c:if test="${_infoUsuarioCirculacao.vinculo.vinculoServidor}">
									<th style="width: 25%; font-weight: bold;">SIAPE:</th>
									<td>
										<h:outputText value="#{_infoUsuarioCirculacao.siape}"/>	
									</td>
								</c:if>
								
								<c:if test="${_infoUsuarioCirculacao.vinculo.vinculoAluno}">
									<th style="width: 25%; font-weight: bold;">MATRÍCULA:</th>
									<td>
										<h:outputText value="#{_infoUsuarioCirculacao.matricula}"/>	
									</td>
								</c:if>
								
								<c:if test="${_infoUsuarioCirculacao.vinculo.vinculoBiblioteca}">
									<th style="width: 25%; font-weight: bold;">Código:</th>
									<td>
										<h:outputText value="#{_infoUsuarioCirculacao.codigoBiblioteca}"/>	
									</td>
								</c:if>
								
								
								<c:if test="${! _infoUsuarioCirculacao.vinculo.vinculoServidor && ! _infoUsuarioCirculacao.vinculo.vinculoAluno && ! _infoUsuarioCirculacao.vinculo.vinculoBiblioteca }">
									
									<c:if test="${  _infoUsuarioCirculacao.contemCPF && not empty _infoUsuarioCirculacao.CPF}" >
										<th style="width: 25%; font-weight: bold;">CPF:</th>
										<td>
											<h:outputText value="#{_infoUsuarioCirculacao.CPF}"/>	
										</td>
									</c:if>
									
									<c:if test="${ _infoUsuarioCirculacao.contemPassaporte && not empty _infoUsuarioCirculacao.passaporte}" >
										<th style="width: 25%; font-weight: bold;">Passaporte:</th>
										<td>
											<h:outputText value="#{_infoUsuarioCirculacao.passaporte}"/>	
										</td>
									</c:if>
									
									<c:if test="${ _infoUsuarioCirculacao.contemCodigoBiblioteca && not empty _infoUsuarioCirculacao.codigoBiblioteca}" >
										<th style="width: 25%; font-weight: bold;">Código de Identificação:</th>
										<td>
											<h:outputText value="#{_infoUsuarioCirculacao.codigoBiblioteca}"/>	
										</td>
									</c:if>
									
									
								</c:if>
								
							</tr>
						
							<tr>
								<th style="width: 25%; font-weight: bold;">Nome:</th>
								<td>
									<h:outputText value="#{_infoUsuarioCirculacao.nomeUsuario}"/>	
								</td>
							</tr>
		
							<c:if test="${_mostrarVinculo == null || _mostrarVinculo == true}">
									<tr>
										<th style="font-weight: bold;">Vínculo do Usuário:</th>
										<td>
											<h:outputText value="#{_infoUsuarioCirculacao.vinculo.descricao}"/>	
											
											<c:if test="${_infoUsuarioCirculacao.mobilidadeEstudantil}">
												<spam style="font-style: italic;"> <h:outputText value=" ( Mobilidade Estudantil ) " /> <spam>
											</c:if>
											
											<c:if test="${_infoUsuarioCirculacao.iniciacaoCientifica}">
												<spam style="font-style: italic;"> <h:outputText value="( Iniciação Científica ) " /> <spam>
											</c:if>
											
										</td>
									</tr>
				
									<%-- Dados do servidor em geral --%>
				
									<c:if test="${_infoUsuarioCirculacao.vinculo.vinculoServidor}">
				
										<tr>
											<th style="font-weight: bold;">Cargo:</th>
											<td>
												<h:outputText value="#{_infoUsuarioCirculacao.cargo}" />
											</td>
										</tr>
										<tr>
											<th style="font-weight: bold;">Lotação:</th>
											<td>
												<h:outputText value="#{_infoUsuarioCirculacao.lotacao}" />	
											</td>
										</tr>
				
									</c:if>
				
				
									<%-- Dados do aluno em geral  --%>
				
									<c:if test="${_infoUsuarioCirculacao.vinculo.vinculoAluno}">
										
										<c:if test="${_infoUsuarioCirculacao.curso != null && _infoUsuarioCirculacao.curso != 'null' }">
											<tr>
												<th style="font-weight: bold;">Curso:</th>
												<td>
													<h:outputText value="#{_infoUsuarioCirculacao.curso}" />
												</td>
											</tr>
										</c:if>
				
										<c:if test="${_infoUsuarioCirculacao.centro != null && _infoUsuarioCirculacao.centro != 'null' }">
											<tr>
												<th style="font-weight: bold;">Centro:</th>
												<td>
													<h:outputText value="#{_infoUsuarioCirculacao.centro}" />
												</td>
											</tr>
										</c:if>
									</c:if>
				
				
									<c:if test="${_infoUsuarioCirculacao.vinculoCancelado}">
										<tr>
											<th style="font-weight: bold; color:red;">Vinculo Cancelado:</th>
											<td style="color:red;">
												<h:outputText  value="#{_infoUsuarioCirculacao.motivoCancelamentoVinculo}" />
											</td>
										</tr>
									</c:if>
							</c:if>
		
		
							<c:if test="${_infoUsuarioCirculacao.telefone != null && _infoUsuarioCirculacao.telefone != 'null' }">
								<tr>
									<th style="font-weight: bold;">Telefone:</th>
									<td>
										<h:outputText value="#{_infoUsuarioCirculacao.telefone}"/>
									</td>
								</tr>
							</c:if>
							
							<c:if test="${_infoUsuarioCirculacao.email != null && _infoUsuarioCirculacao.email != 'null' }">
								<tr>
									<th style="font-weight: bold;">Email:</th>
									<td>
										<h:outputText value="#{_infoUsuarioCirculacao.email}"/>
									</td>
								</tr>
							</c:if>
		
		
							<c:if test="${_infoUsuarioCirculacao.endereco != null }">
								<tr>
									<th style="font-weight: bold;">Endereço:</th>
									<td>
										<h:outputText value="#{_infoUsuarioCirculacao.endereco}"/>
									</td>
								</tr>
							</c:if>
		
							<c:if test="${_observacaoUsuario != null}">
								<tr>
									<th class="rotulo">Observação:</th>
									<td>${_observacaoUsuario}</td>
								</tr>
							</c:if>
		
							<c:if test="${_situacoesUsuario != null}">
								
								<tr>
									<th>
									</th>
									<td style="padding-top: 20px;">
									</td>
								</tr>
								
								<c:forEach items="#{_situacoesUsuario}" var="situacao">
									<tr>
										<th>
										</th>
										<td style="font-weight: bold;">
											<c:if test="${not situacao.situacaoImpedeEmprestimos}">
												<h:outputText value="#{situacao.descricaoCompleta}" style="color:green;" /> <br/>
											</c:if>
							
											<c:if test="${ situacao.situacaoImpedeEmprestimos}"> 
												<h:outputText value="#{situacao.descricaoCompleta}" style="color:red;" /> <br/>
											</c:if>
										</td>
									</tr>
								</c:forEach>
								
							</c:if>
		
							
		
						</table>
					</td>
				</tr>
				
				</tbody>
				
			</table>

		</c:if>
