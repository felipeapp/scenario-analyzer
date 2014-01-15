		<!-- Ações QUE COORDENO -->
		<c:if test="${not empty atividadeExtensao.atividadesMembroCoordena}">
		
				<div class="infoAltRem">
				    <h:graphicImage value="/img/biblioteca/emprestimos_ativos.png" style="overflow: visible;" />: Visualizar Menu
				</div>
				
				
				<table class="listagem" id="lista-turmas">
					<caption>Lista das Ações de Extensão das quais Coordeno (${ fn:length(atividadeExtensao.atividadesMembroCoordena) })</caption>
					<thead>
						<tr>
							<th>Código</th>
							<th>Título</th>
							<th>Tipo</th>
							<th>Situação</th>
							<th></th>
						</tr>
					</thead>
					<c:forEach var="atividade" items="#{ atividadeExtensao.atividadesMembroCoordena }" varStatus="s">
						
						<tr class="${s.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td><h:outputText value="#{atividade.codigo}" /></td>
							<td><h:outputText value="#{atividade.titulo}" /></td>
							<td><h:outputText value="#{atividade.tipoAtividadeExtensao.descricao}" /></td>
							<td><h:outputText value="#{atividade.situacaoProjeto.descricao}" /></td>
							<td style="text-align: right; width: 5%">
								<img src="${ctx}/img/biblioteca/emprestimos_ativos.png" 
									onclick="exibirOpcoes(${atividade.id});" style="cursor: pointer" title="Visualizar Menu"/>
							</td>
						</tr> 

						<tr id="trOpcoes${ atividade.id }" class="${s.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style="display: none">
							<td colspan="5">
								<ul class="listaOpcoes">
									
									<a4j:region rendered="#{ atividade.projeto.aprovadoComSemRecurso }">
										<li id="executarAcao">
				                            <h:commandLink id="executarAcao" title="Executar Ação" action="#{ atividadeExtensao.confirmarExecucao }">
		                                        <f:param name="id" value="#{atividade.id}"/>
		                                        Executar Ação
				                            </h:commandLink>
										</li>
									</a4j:region>
									
									<%-- CADASTRO EM ANDAMENTO ou DEVOLVIDA PARA COORDENADOR REEDITAR--%>
									<a4j:region rendered="#{ atividade.passivelEdicao }">
										<li id="alterarCadastro">
											<h:commandLink id="alterarCadastro" title="Alterar" action="#{ atividadeExtensao.preAtualizar }" immediate="true"> 
												<f:param name="id" value="#{atividade.id}" />
												Alterar
											</h:commandLink>
										</li>
									</a4j:region>
									
									<%-- CADASTRO EM ANDAMENTO ou DEVOLVIDA PARA COORDENADOR REEDITAR--%>
									<a4j:region rendered="#{ atividade.passivelEdicao || atividade.projeto.passivelRemocao }">
										<li id="removerAcao">
											<h:commandLink id="removerCadastroAcao" title="Remover" action="#{ atividadeExtensao.preRemover }" immediate="true"> 
												<f:param name="id" value="#{atividade.id}"/>
												Remover
											</h:commandLink>
										</li>
									</a4j:region>
									
									<%-- CADASTRO EM ANDAMENTO ou REPROVADO--%>
									<a4j:region rendered="#{ (atividade.situacaoProjeto.id != 101) || (atividade.situacaoProjeto.id != 104) }">
										<li id="anexarFotos">
											<h:commandLink id="anexarFotos" title="Anexar Fotos" action="#{ fotoProjeto.iniciarAnexarFoto }" immediate="true"> 
												<f:param name="idProjeto" value="#{atividade.projeto.id}"/>
												Anexar Fotos
											</h:commandLink>
										</li>
									</a4j:region>

									<li id="visualizar">
										<h:commandLink id="visualizarAcaoExtensao" title="Visualizar" action="#{ atividadeExtensao.view }" immediate="true">
											<f:param name="id" value="#{atividade.id}"/>
								    		Visualizar
										</h:commandLink>
									</li>
									
									<li id="visualizarImpressao">
										<h:commandLink id="imprimirVersao" title="Versão para impressão" action="#{ atividadeExtensao.view }" immediate="true">
											<f:param name="id" value="#{atividade.id}"/>
											<f:param name="print" value="true"/>
				    						Versão para impressão
										</h:commandLink>
									</li>
									
									<%-- EM EXECUÇÃO, NÃO APROVADO,REGISTRO APROVADO, REGISTRO NÃO APROVADO, CONCLUIDO--%>
									<a4j:region rendered="#{ (atividade.projeto.aprovadoComSemRecurso) || (atividade.aprovadoEmExecucao) || 
											(atividade.situacaoProjeto.id == 104) || (atividade.situacaoProjeto.id == 105) || 
											(atividade.situacaoProjeto.id == 110) || (atividade.situacaoProjeto.id == 111) }">
										<li id="visualizarOrcamentoAprovado">
											<h:commandLink id="visualizarOrcamentoAprovado" title="Orcamento Aprovado" action="#{ atividadeExtensao.view }" immediate="true"> 
												<f:param name="id" value="#{atividade.id}"/>
												<f:param name="orcamentoAprovado" value="true"/>
									    		Orçamento Aprovado
											</h:commandLink>
										</li>
									</a4j:region>
									
									<%-- CADASTRO_EM_ANDAMENTO, EM EXECUÇÃO, NÃO APROVADO,REGISTRO APROVADO, REGISTRO NÃO APROVADO, CONCLUIDO, --%>
									<a4j:region rendered="#{(atividade.situacaoProjeto.id == 101) || (atividade.projeto.aprovadoComSemRecurso) || 
										(atividade.aprovadoEmExecucao) || (atividade.situacaoProjeto.id == 104) || (atividade.situacaoProjeto.id == 105) || 
										(atividade.situacaoProjeto.id == 110) || (atividade.situacaoProjeto.id == 111)}">
										<li id="visualizarAvaliacoes">
											<h:commandLink id="visualizarAvaliacoes" title="Avaliação do Comitê" 
												action="#{ atividadeExtensao.iniciarVisualizarAvaliacaoAtividade }" immediate="true">
													<f:param name="id" value="#{atividade.id}"/>
				    								Avaliação do Comitê
											</h:commandLink>
										</li>
									</a4j:region>
									
									<a4j:region rendered="#{ atividade.valida }">
										<li id="criarComunidadeVirtual">
											<h:commandLink id="criarComunidadeVirtual" title="Criar Comunidade Virtual com participantes do projeto" 
													action="#{ criarComunidadeVirtualProjetoMBean.criarComunidadeVirtualExtensao }"
													onclick="if (!confirm(\"Tem certeza que deseja criar uma Comunidade Virtual com os participantes desse projeto?\")) return false;">
														<f:param name="id" value="#{atividade.id}"/>
				    									Criar Comunidade Virtual
											</h:commandLink>
										</li>
									</a4j:region>
									
									<a4j:region rendered="#{ atividade.aprovadoEmExecucao }">
										<li id="configuracoes">
											<h:commandLink id="configuracoes" title="Designar Função a Membro" 
												action="#{ designacaoFuncaoProjetoMBean.listar }">
													<f:param name="id" value="#{atividade.id}"/>
			    									Designar Função a Membro
											</h:commandLink>
										</li>
									</a4j:region>
									
									<li style="clear: both; float: none; background-image: none;"></li>
									
								</ul>
							</td>
						</tr>
						
						<c:if test="${ not empty atividade.subAtividadesExtensao }">
							<c:forEach var="subAtividade" items="#{ atividade.subAtividadesExtensao }">
								<tr style="font-style: italic; color:green;">
									<td></td>
									<td> <h:outputText value="#{subAtividade.titulo}"/> </td>
									<td> <h:outputText value="#{subAtividade.tipoSubAtividadeExtensao.descricao}" /> </td>
									<td> </td>
									<td style="text-align: right; width: 5%">
										<img src="${ctx}/img/biblioteca/emprestimos_ativos.png" 
											onclick="exibirOpcoes(${subAtividade.id});" style="cursor: pointer" title="Visualizar Menu"/>
									</td>
								</tr>
								<tr id="trOpcoes${ subAtividade.id }" style="width: 100%; display: none;">
									<td colspan="5">
										<ul class="listaOpcoes">
											<li id="alterarMiniAtividade">
												<h:commandLink id="cmdLinkalterarSubAtividadeCoordenacao" title="Alterar" action="#{subAtividadeExtensaoMBean.preAtualizar }"> 
 								        			<f:param name="idSubAtividade" value="#{subAtividade.id}" />
 								        			<f:setPropertyActionListener target="#{subAtividadeExtensaoMBean.paginaRetorno}" value="/extensao/Atividade/lista_minhas_atividades.jsp" />
							    					Alterar
 												</h:commandLink>
											</li>
												
											<li id="removerMiniAtividade">
												<h:commandLink id="cmdLinkRemoverSubAtividadeCoordenacao" title="Remover" action="#{subAtividadeExtensaoMBean.preRemoverSubAtividade}">
 								        			<f:param name="idSubAtividade" value="#{subAtividade.id}" />
 								        			<f:setPropertyActionListener target="#{subAtividadeExtensaoMBean.paginaRetorno}"  value="/extensao/Atividade/lista_minhas_atividades.jsp" />
 							    					Remover
 												</h:commandLink>
											</li>
		
											<li style="clear: both; float: none; background-image: none;"></li>
											
										</ul>
									</td>
								</tr>
															
							</c:forEach> 
						</c:if>
						
					</c:forEach>
				</table>

			</c:if>
			<!-- FIM DAS Ações QUE PARTICIPO -->