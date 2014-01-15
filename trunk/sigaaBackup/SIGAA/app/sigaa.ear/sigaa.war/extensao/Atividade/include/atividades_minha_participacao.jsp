		<!-- Ações QUE PARTICIPO -->
		<c:if test="${not empty atividadeExtensao.atividadesMembroParticipa}">
		
				<div class="infoAltRem">
				    <h:graphicImage value="/img/biblioteca/emprestimos_ativos.png" style="overflow: visible;" />: Visualizar Menu
				</div>
				
				<table class="listagem" id="lista-turmas">
					<caption>Lista das Ações de Extensão das quais Participo (${ fn:length(atividadeExtensao.atividadesMembroParticipa) })</caption>
					<thead>
						<tr>
							<th>Código</th>
							<th>Título</th>
							<th>Tipo</th>
							<th>Situação</th>
							<th></th>
						</tr>
					</thead>
					<c:forEach var="atividade" items="#{ atividadeExtensao.atividadesMembroParticipa }" varStatus="s">
						
						<tr class="${s.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td><h:outputText value="#{atividade.codigo}" /></td>
							<td>
								<h:outputText value="#{atividade.titulo}" /> <br />
								Coordenador: <h:outputText value="#{atividade.projeto.coordenador.pessoa.nome}" />
							</td>
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
									
									<li id="visualizar">
										<h:commandLink id="visualizarAcaoExtensaoParticipante" title="Visualizar" action="#{ atividadeExtensao.view }" immediate="true">
											<f:param name="id" value="#{atividade.id}"/>
								    		Visualizar
										</h:commandLink>
									</li>
									
									<li id="visualizarImpressao">
										<h:commandLink id="imprimirVersaoParticipante" title="Versão para impressão" action="#{ atividadeExtensao.view }" immediate="true">
											<f:param name="id" value="#{atividade.id}"/>
											<f:param name="print" value="true"/>
				    						Versão para impressão
										</h:commandLink>
									</li>
									
									<%-- CADASTRO_EM_ANDAMENTO, EM EXECUÇÃO, NÃO APROVADO,REGISTRO APROVADO, REGISTRO NÃO APROVADO, CONCLUIDO, --%>
									<a4j:region rendered="#{(atividade.situacaoProjeto.id == 101) || (atividade.projeto.aprovadoComSemRecurso) || 
										(atividade.aprovadoEmExecucao) || (atividade.situacaoProjeto.id == 104) || (atividade.situacaoProjeto.id == 105) || 
										(atividade.situacaoProjeto.id == 110) || (atividade.situacaoProjeto.id == 111)}">
										<li id="visualizarAvaliacoes">
											<h:commandLink id="visualizarAvaliacoesParticipante" title="Avaliação do Comitê" 
												action="#{ atividadeExtensao.iniciarVisualizarAvaliacaoAtividade }" immediate="true">
													<f:param name="id" value="#{atividade.id}"/>
				    								Avaliação do Comitê
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
												<h:commandLink id="cmdLinkalterarSubAtividadeParticipacao" title="Alterar" action="#{subAtividadeExtensaoMBean.preAtualizar }"> 
 								        			<f:param name="idSubAtividade" value="#{subAtividade.id}" />
 								        			<f:setPropertyActionListener target="#{subAtividadeExtensaoMBean.paginaRetorno}" value="/extensao/Atividade/lista_minhas_atividades.jsp" />
							    					Alterar
 												</h:commandLink>
											</li>
												
											<li id="removerMiniAtividade">
												<h:commandLink id="cmdLinkRemoverSubAtividadeParticipacao" title="Remover" action="#{subAtividadeExtensaoMBean.preRemoverSubAtividade}">
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