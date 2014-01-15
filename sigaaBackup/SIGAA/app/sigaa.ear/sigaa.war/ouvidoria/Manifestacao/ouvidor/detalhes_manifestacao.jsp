<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="analiseManifestacaoOuvidoria" />

<f:view>
	<h:form id="form">
	<h2>
		<ufrn:subSistema />
		<h:outputText value=" > " rendered="#{analiseManifestacaoOuvidoria.operacaoPendente || analiseManifestacaoOuvidoria.operacaoEncaminhada }" />
		<h:commandLink value="Manifestações Pendentes" action="#{analiseManifestacaoOuvidoria.listarPendentes }" rendered="#{analiseManifestacaoOuvidoria.operacaoPendente }" />
		<h:commandLink value="Manifestações Encaminhadas" action="#{analiseManifestacaoOuvidoria.listarEncaminhadas }" rendered="#{analiseManifestacaoOuvidoria.operacaoEncaminhada }" /> 
		
		&gt; Detalhes da Manifestação
	</h2>
	
	<c:if test="${analiseManifestacaoOuvidoria.operacaoEditar }">
		<table class="formulario" width="100%">
			<caption>Editar Manifestação</caption>
			<tbody>
				<tr>
					<th class="required">Categoria do Assunto:</th>
					<td width="85%">
						<h:selectOneMenu id="categoria" value="#{analiseManifestacaoOuvidoria.obj.assuntoManifestacao.categoriaAssuntoManifestacao.id }" onchange="submit()" style="width: 95%">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{categoriaAssuntoManifestacao.allCategoriasAtivasCombo }" />
							<a4j:support event="onselect" reRender="assunto" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th class="required">Assunto:</th>
					<td>
						<h:selectOneMenu id="assunto" value="#{analiseManifestacaoOuvidoria.obj.assuntoManifestacao.id }" style="width: 95%">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
							<f:selectItems value="#{analiseManifestacaoOuvidoria.allAssuntosByCategoriaCombo }" />
						</h:selectOneMenu>
					</td>
				</tr>
				<tr>
					<th>Sigilo:</th>
					<td>
						<h:selectBooleanCheckbox value="#{analiseManifestacaoOuvidoria.obj.anonima}" styleClass="noborder" id="anonima" />
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="Editar Manifestação" action="#{analiseManifestacaoOuvidoria.alterarDadosManifestacao }" />
					<h:commandButton value="<< Voltar" action="#{analiseManifestacaoOuvidoria.listarPendentes }" rendered="#{analiseManifestacaoOuvidoria.operacaoPendente }" />
					<h:commandButton value="<< Voltar" action="#{analiseManifestacaoOuvidoria.listarEncaminhadas }" rendered="#{analiseManifestacaoOuvidoria.operacaoEncaminhada }" />
					<h:commandButton value="Cancelar" action="#{analiseManifestacaoOuvidoria.cancelar }" onclick="#{confirm }" />
				</td></tr>
			</tfoot>
		</table>
		<br /><br />
	</c:if>
	
	<c:if test="${analiseManifestacaoOuvidoria.operacaoResponder }">
		<table class="formulario" width="100%">
			<caption>Responder Manifestação</caption>
			<tbody>
				<tr>
					<th valign="top" class="required">Resposta: </th>
					<td width="85%">
						<h:inputTextarea value="#{analiseManifestacaoOuvidoria.historico.resposta }" style="width: 95%;" rows="5" />
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="Enviar Resposta" action="#{analiseManifestacaoOuvidoria.enviarResposta }" />
					<h:commandButton value="<< Voltar" action="#{analiseManifestacaoOuvidoria.listarPendentes }" rendered="#{analiseManifestacaoOuvidoria.operacaoPendente }" />
					<h:commandButton value="<< Voltar" action="#{analiseManifestacaoOuvidoria.listarEncaminhadas }" rendered="#{analiseManifestacaoOuvidoria.operacaoEncaminhada }" />
					<h:commandButton value="Cancelar" action="#{analiseManifestacaoOuvidoria.cancelar }" onclick="#{confirm }" />
				</td></tr>
			</tfoot>
		</table>
		<br /><br />
	</c:if>
	
	<c:if test="${analiseManifestacaoOuvidoria.operacaoEncaminhar }">
		<table class="formulario" width="100%">
			<caption>Encaminhar Manifestação</caption>
			<tbody>
				<tr><td colspan="2" class="subFormulario" style="text-align: center; font-size: small;">Detalhes do Encaminhamento</td></tr>
				<tr>
					<th valign="top" class="required">Mensagem: </th>
					<td width="85%">
						<h:inputTextarea value="#{analiseManifestacaoOuvidoria.historico.solicitacao }" style="width: 95%;" rows="5" />
					</td>
				</tr>
				<tr>
					<th valign="top" class="required">Prazo de Resposta: </th>
					<td width="85%">
						<t:inputCalendar value="#{analiseManifestacaoOuvidoria.historico.prazoResposta }" renderAsPopup="true" renderPopupButtonAsImage="true" 
									popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" id="Data_Inicial" size="10" maxlength="10" onkeypress="return formataData(this,event)" />
					</td>
				</tr>
				<tr>
					<td style="text-align: right; vertical-align: top;">
						<h:selectBooleanCheckbox value="#{analiseManifestacaoOuvidoria.manterSigilo}" styleClass="noborder" id="anonima" disabled="#{analiseManifestacaoOuvidoria.obj.anonima }" />
					</td>
					<td>
						<c:if test="${!analiseManifestacaoOuvidoria.obj.anonima }">
							<label for="anonima" onclick="$('form:anonima').checked = !$('form:anonima').checked;">
						</c:if>
							Manter sigilo ao encaminhar.
						<c:if test="${!analiseManifestacaoOuvidoria.obj.anonima }">
							</label>
						</c:if>
						<ufrn:help img="/img/ajuda.gif">Essa opção estará habilitada para modificação apenas para as manifestações que o sigilo não tenha sido solicitado.</ufrn:help>
					</td>
				</tr>
				<tr>
					<th align="left" class="required">Unidade de Destino:</th>
					<td width="85%" align="left">
					 	<h:inputText id="nomeUnidadeDestino" value="#{ analiseManifestacaoOuvidoria.historico.unidadeResponsavel.nome }" size="55"/>
			
						<rich:suggestionbox for="nomeUnidadeDestino" width="450" height="100" minChars="3"  
											suggestionAction="#{ unidade.autocompleteUnidades }" var="_unidade" fetchValue="#{ _unidade.nome }">
							<h:column>
								   <h:outputText value="#{_unidade.nome}" /> 
							</h:column>
							<a4j:support event="onselect" actionListener="#{analiseManifestacaoOuvidoria.selecionarUnidade}" reRender="_responsavelUnidade">
								<f:setPropertyActionListener value="#{_unidade.id}" target="#{analiseManifestacaoOuvidoria.historico.unidadeResponsavel.id}" />
								<f:attribute name="idUnidadeDestino" value="#{_unidade.id}" />
							</a4j:support>
						</rich:suggestionbox>
					</td>
				</tr>
				<tr id="trResponsavel">
					<td colspan="2">
						<table class="formulario" width="100%">
							<caption>Servidor responsável pela unidade selecionada:</caption>
							<tr>
								<td>
									<h:outputText id="_responsavelUnidade" value="#{analiseManifestacaoOuvidoria.responsavelUnidade.servidor.nomeSiape}" />
								</td>
							</tr>
						</table>
					</td>
				</tr>
				<tr><td colspan="2" class="subFormulario" style="text-align: center; font-size: small;">Enviar Cópia da Manifestação</td></tr>
				<tr>
					<td colspan="2">
						<a4j:region>
							<h:selectOneRadio value="#{analiseManifestacaoOuvidoria.copiaPessoa }">
								<f:selectItem itemLabel="Adicionar Pessoas Para Cópia" itemValue="true"/>
								<f:selectItem itemLabel="Adicionar Unidades Para Cópia" itemValue="false"/>
								<a4j:support event="onchange" reRender="form" />
							</h:selectOneRadio>
						</a4j:region>
					</td>
				</tr>
				<c:if test="${!analiseManifestacaoOuvidoria.copiaPessoa }">
					<tr>
						<th align="left">Unidade para Cópia:</th>
						<td width="85%" align="left">
						 	<h:inputText id="nomeUnidadeCopia" value="#{ analiseManifestacaoOuvidoria.unidadeSelecionadaCopia.nome }" size="55"/>
				
							<rich:suggestionbox for="nomeUnidadeCopia" width="450" height="100" minChars="3"  
												suggestionAction="#{ unidade.autocompleteUnidades }" var="_unidade" fetchValue="#{ _unidade.nome }">
								<h:column>
									   <h:outputText value="#{_unidade.nome}" /> 
								</h:column>
								<a4j:support event="onselect" actionListener="#{analiseManifestacaoOuvidoria.selecionarUnidadeCopia}" reRender="form">
									<f:setPropertyActionListener value="#{_unidade.id}" target="#{analiseManifestacaoOuvidoria.unidadeSelecionadaCopia.id}" />
									<f:attribute name="idUnidadeDestino" value="#{_unidade.id}" />
								</a4j:support>
							</rich:suggestionbox>
							<h:commandLink actionListener="#{analiseManifestacaoOuvidoria.adicionarUnidadeCopia }" title="Adicionar Unidade Para Cópia">
								<h:graphicImage url="/img/adicionar.gif"></h:graphicImage>
							</h:commandLink>
						</td>
					</tr>
					<tr id="trResponsavel">
						<td colspan="2">
							<table class="formulario" width="100%">
								<caption>Servidor responsável pela unidade selecionada para cópia:</caption>
								<tr>
									<td>
										<c:if test="${analiseManifestacaoOuvidoria.responsavelUnidadeCopia.nomeServidor != null }">
											<h:outputText value="#{analiseManifestacaoOuvidoria.responsavelUnidadeCopia.servidor.nomeSiape}" />
										</c:if>
									</td>
								</tr>
							</table>
						</td>
					</tr>
				</c:if>
				<c:if test="${analiseManifestacaoOuvidoria.copiaPessoa }">
					<tr>
						<th align="left">Pessoa para Cópia:</th>
						<td width="85%" align="left">
							<h:inputText value="#{analiseManifestacaoOuvidoria.responsavelUnidadeCopia.servidor.pessoa.nome}" id="nomePessoa" size="59"/>
							<rich:suggestionbox for="nomePessoa" width="450" height="100" minChars="3" id="suggestionNomePessoa" 
								suggestionAction="#{pessoaAutoCompleteMBean.autocompleteNomePessoaFisica}" var="_pessoa" 
								fetchValue="#{_pessoa.nome}">
								<h:column>
									<h:outputText value="#{_pessoa.nome}" />
								</h:column>
								<a4j:support event="onselect">
									<f:setPropertyActionListener value="#{_pessoa.id}" target="#{analiseManifestacaoOuvidoria.responsavelUnidadeCopia.servidor.pessoa.id}" />
								</a4j:support>
							</rich:suggestionbox>
							<h:commandLink actionListener="#{analiseManifestacaoOuvidoria.adicionarPessoaCopia }" title="Adicionar Pessoa Para Cópia">
								<h:graphicImage url="/img/adicionar.gif"></h:graphicImage>
							</h:commandLink>
						</td>
					</tr>
				</c:if>
				<tr id="trResponsavel">
					<td colspan="2">
						<table class="formulario" width="100%">
							<caption>Pessoas Adicionadas para Cópia</caption>
							<tr>
								<td>
									<h:panelGroup id="pessoasAdicionadas">
										<c:if test="${empty analiseManifestacaoOuvidoria.responsaveisUnidadesCopia }">
											<span class="vazio">Nenhuma pessoa adicionada para cópia. </span>
										</c:if>
										<c:if test="${not empty analiseManifestacaoOuvidoria.responsaveisUnidadesCopia }">
											<table width="100%">
												<thead>
													<th>Pessoa</th>
													<th>Unidade de Responsabilidade</th>
													<th></th>
												</thead>
												<tbody>
													<c:forEach items="#{analiseManifestacaoOuvidoria.responsaveisUnidadesCopia }" var="r">
														<tr>
															<td>${r.servidor.pessoa.nome }</td>
															<td>${r.unidade != null ? r.unidade : "-" }</td>
															<td>
																<h:commandLink actionListener="#{analiseManifestacaoOuvidoria.removerPessoaCopia }" title="Remover Pessoa da Listagem">
																	<h:graphicImage url="/img/delete.png" />
																	<f:attribute name="idPessoaRemocao" value="#{r.servidor.pessoa.id }" />
																</h:commandLink>
															</td>
														</tr>
													</c:forEach>
												</tbody>
											</table>
										</c:if>
									</h:panelGroup>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="Encaminhar Manifestação" action="#{analiseManifestacaoOuvidoria.cadastrarEncaminhamento }" />
					<h:commandButton value="<< Voltar" action="#{analiseManifestacaoOuvidoria.listarPendentes }" rendered="#{analiseManifestacaoOuvidoria.operacaoPendente }" />
					<h:commandButton value="<< Voltar" action="#{analiseManifestacaoOuvidoria.listarEncaminhadas }" rendered="#{analiseManifestacaoOuvidoria.operacaoEncaminhada }" />
					<h:commandButton value="Cancelar" action="#{analiseManifestacaoOuvidoria.cancelar }" onclick="#{confirm }" />
				</td></tr>
			</tfoot>
		</table>
		<br /><br />
	</c:if>
	
	<c:if test="${analiseManifestacaoOuvidoria.operacaoAlterarPrazo }">
		<table class="formulario" width="100%">
			<caption>Alterar Prazo de Resposta da Manifestação</caption>
			<tbody>
				<tr>
					<td>
						<table class="subFormulario" width="100%">
							<caption>Dados do Encaminhamento Cadastrado</caption>
							<tbody>
								<tr>
									<th valign="top" style="font-weight: bold;">Mensagem: </th>
									<td width="85%">
										<h:outputText value="#{analiseManifestacaoOuvidoria.historico.solicitacao }" style="width: 95%;"/>
									</td>
								</tr>
								<tr>
									<th valign="top" style="font-weight: bold;">Prazo de Resposta: </th>
									<td><ufrn:format type="data" valor="${analiseManifestacaoOuvidoria.prazoResposta }" />
									</td>
								</tr>
								<tr>
									<th style="font-weight: bold;">Unidade de Destino:</th>
									<td><h:outputText value="#{analiseManifestacaoOuvidoria.historico.unidadeResponsavel}" /></td>
								</tr>
								<tr>
									<th style="font-weight: bold;">Servidor responsável:</th>
									<td><h:outputText value="#{analiseManifestacaoOuvidoria.responsavelUnidade.servidor.nomeSiape}" /></td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
				<tr>
					<td>
						<table class="subFormulario" width="100%">
							<caption>Alterar Prazo de Resposta</caption>
							<tbody>
								<tr>
									<th valign="top" class="required" style="font-weight: bold;" width="15%">Novo Prazo de Resposta: </th>
									<td>
										<t:inputCalendar value="#{analiseManifestacaoOuvidoria.historico.prazoResposta }" renderAsPopup="true" renderPopupButtonAsImage="true" 
													popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é" id="Data_Inicial" size="10" maxlength="10" onkeypress="return formataData(this,event)" />
									</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="Alterar Prazo de Resposta" action="#{analiseManifestacaoOuvidoria.alterarPrazoRespostaManifestacao }" />
					<h:commandButton value="<< Voltar" action="#{analiseManifestacaoOuvidoria.listarPendentes }" rendered="#{analiseManifestacaoOuvidoria.operacaoPendente }" />
					<h:commandButton value="<< Voltar" action="#{analiseManifestacaoOuvidoria.listarEncaminhadas }" rendered="#{analiseManifestacaoOuvidoria.operacaoEncaminhada }" />
					<h:commandButton value="Cancelar" action="#{analiseManifestacaoOuvidoria.cancelar }" onclick="#{confirm }" />
				</td></tr>
			</tfoot>
		</table>
		<br /><br />
	</c:if>
	
	<c:if test="${analiseManifestacaoOuvidoria.operacaoFinalizarManifestacao && !analiseManifestacaoOuvidoria.obj.respondida }">
		<c:if test="${analiseManifestacaoOuvidoria.usuarioOuvidor }">
			<div class="descricaoOperacao">
				<b>Caro Ouvidor,</b> 
				<br/><br/>
				É possível finalizar a manifestação sem resposta. Neste caso o usuário receberá um e-mail sugerindo que ele procure mais informações junto a ouvidoria.
			</div>				
		</c:if>
	
		<table class="formulario" width="100%">
			<caption>Finalizar Manifestação</caption>
			<tbody>
				<tr>
					<td>
						<table class="subFormulario" width="100%">
							<tbody>
								<tr>
									<c:if test="${analiseManifestacaoOuvidoria.usuarioOuvidor }">
										<th valign="top" width="15%">Resposta: </th>
									</c:if>
									<c:if test="${!analiseManifestacaoOuvidoria.usuarioOuvidor }">
										<th valign="top" class="required" style="font-weight: bold;" width="15%">Resposta: </th>
									</c:if>
									<td>
										<h:inputTextarea value="#{analiseManifestacaoOuvidoria.historico.resposta }" style="width: 95%;" rows="5" disabled="#{analiseManifestacaoOuvidoria.obj.respondida }" />
									</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="Finalizar Manifestação" action="#{analiseManifestacaoOuvidoria.registrarFinalizacaoManifestacao }" />
					<h:commandButton value="<< Voltar" action="#{analiseManifestacaoOuvidoria.listarRespondidas }" />
					<h:commandButton value="Cancelar" action="#{analiseManifestacaoOuvidoria.cancelar }" onclick="#{confirm }" />
				</td></tr>
			</tfoot>
		</table>
		<br /><br />
	</c:if>
	
	<table class="formulario" width="100%">
	<c:if test="${analiseManifestacaoOuvidoria.operacaoVisualizacao }">
		<caption>Detalhes da Manifestação</caption>
	</c:if>
	<tbody>
	<c:if test="${!analiseManifestacaoOuvidoria.operacaoVisualizacao }">
		<tr><td class="subFormulario" style="text-align: center; font-size: small;">Detalhes da Manifestação</td></tr>
	</c:if>
	<tr><td>
		<table class="subFormulario" style="width: 100%">
			<caption>Dados do Interessado</caption>
			<c:if test="${!analiseManifestacaoOuvidoria.comunidadeInterna }">
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">Nome: </td>
					<td colspan="3"><h:outputText value="#{analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.nome == null ? '-' : analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.nome }" /></td>
				</tr>
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">E-Mail: </td>
					<td colspan="3"><h:outputText value="#{analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.email == null ? '-' : analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.email }" /></td>
				</tr>
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">Telefone: </td>
					<td colspan=3><h:outputText value="#{analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.telefoneString }" /></td>
				</tr>
				<c:if test="${analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.enderecoCadastrado }">
					<tr>
						<td style="font-weight: bold; text-align: center;" width="17%" colspan="4"><u>Endereço</u></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">CEP: </td>
						<td colspan="3"><h:outputText value="#{analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.cep == null ? '-' : analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.cep }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Logradouro: </td>
						<td><h:outputText value="#{analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.logradouroString }" /></td>
						<td style="font-weight: bold; text-align: right;" width="17%">Número: </td>
						<td><h:outputText value="#{analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.numero == null ? '-' : analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.numero }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Bairro: </td>
						<td><h:outputText value="#{analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.bairro == null ? '-' : analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.bairro }" /></td>
						<td style="font-weight: bold; text-align: right;" width="17%">Complemento: </td>
						<td><h:outputText value="#{analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.complemento == null ? '-' : analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.complemento }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">UF: </td>
						<td><h:outputText value="#{analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.unidadeFederativa == null ? '-' : analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.unidadeFederativa.descricao }" /></td>
						<td style="font-weight: bold; text-align: right;" width="17%">Município: </td>
						<td><h:outputText value="#{analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.municipio == null ? '-' : analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.municipio.nome }" /></td>
					</tr>
				</c:if>
			</c:if>
			<c:if test="${analiseManifestacaoOuvidoria.comunidadeInterna }">
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">Categoria: </td>
					<td colspan="3"><h:outputText value="#{analiseManifestacaoOuvidoria.obj.interessadoManifestacao.categoriaSolicitante == null ? '-' : analiseManifestacaoOuvidoria.obj.interessadoManifestacao.categoriaSolicitante.descricao }" /></td>
				</tr>
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">Nome: </td>
					<td colspan="3"><h:outputText value="#{analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.pessoa.nome == null ? '-' : analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.pessoa.nome }" /></td>
				</tr>
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">E-Mail: </td>
					<td colspan="3"><h:outputText value="#{analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.pessoa.email == null ? '-' : analiseManifestacaoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.pessoa.email }" /></td>
				</tr>
			</c:if>
		</table>
		
		<c:set var="manifestacao" value="#{analiseManifestacaoOuvidoria.obj }" scope="page" />
		<c:set var="historicos" value="#{analiseManifestacaoOuvidoria.historicos }" scope="page" />
		<c:set var="copias" value="#{analiseManifestacaoOuvidoria.copias }" scope="page" />
		<c:set var="delegacoes" value="#{analiseManifestacaoOuvidoria.delegacoes }" scope="page" />
		<%@ include file="/ouvidoria/Manifestacao/include/dados_detalhes_manifestacao.jsp" %>
		
		</td></tr>
		</tbody>
		<c:if test="${analiseManifestacaoOuvidoria.operacaoVisualizarPendente }">
			<tfoot>
				<tr>
					<td>
						<input type="hidden" name="idManifestacao" value="${ manifestacao.id }"/>
						<input type="hidden" name="pendentes" value="true"/>
					
						<h:commandButton value="Editar Manifestação" action="#{analiseManifestacaoOuvidoria.editarManifestacaoCarregada }" />
						<h:commandButton value="Responder Manifestação" action="#{analiseManifestacaoOuvidoria.responderManifestacaoPendenteCarregada }" />
						<h:commandButton value="Encaminhar Manifestação" action="#{analiseManifestacaoOuvidoria.encaminharManifestacaoCarregada }" />
						<h:commandButton value="Solicitar Esclarecimento" action="#{esclarecimentoOuvidoria.solicitarEsclarecimentos}"/>
						<h:commandButton value="<< Voltar" action="#{analiseManifestacaoOuvidoria.listarPendentes }" />
					</td>
				</tr>
			</tfoot>
		</c:if>
		<c:if test="${analiseManifestacaoOuvidoria.operacaoVisualizarEncaminhada }">
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="Responder Manifestação" action="#{analiseManifestacaoOuvidoria.responderManifestacaoEncaminhadaCarregada }" rendered="#{analiseManifestacaoOuvidoria.obj.statusManifestacao.parecerCadastrado }" />
						<h:commandButton value="Prorrogar Prazo de Resposta" action="#{analiseManifestacaoOuvidoria.alterarPrazoManifestacaoCarregada }" rendered="#{!analiseManifestacaoOuvidoria.obj.statusManifestacao.parecerCadastrado }" />
						<h:commandButton value="<< Voltar" action="#{analiseManifestacaoOuvidoria.listarEncaminhadas }" />
					</td>
				</tr>
			</tfoot>
		</c:if>
		<c:if test="${(analiseManifestacaoOuvidoria.operacaoVisualizarRespondida || analiseManifestacaoOuvidoria.operacaoFinalizarManifestacao) && analiseManifestacaoOuvidoria.obj.respondida }">
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="Finalizar Manifestação" action="#{analiseManifestacaoOuvidoria.registrarFinalizacaoManifestacao }" />
					<h:commandButton value="<< Voltar" action="#{analiseManifestacaoOuvidoria.listarRespondidas }" />
					<h:commandButton value="Cancelar" action="#{analiseManifestacaoOuvidoria.cancelar }" onclick="#{confirm }" />
				</td></tr>
			</tfoot>
		</c:if>
		<c:if test="${analiseManifestacaoOuvidoria.operacaoVisualizarRespondida && !analiseManifestacaoOuvidoria.obj.respondida }">
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="Finalizar Manifestação" action="#{analiseManifestacaoOuvidoria.finalizarManifestacaoCarregada }" />
						<h:commandButton value="<< Voltar" action="#{analiseManifestacaoOuvidoria.listarRespondidas }" />
					</td>
				</tr>
			</tfoot>
		</c:if>
		<c:if test="${analiseManifestacaoOuvidoria.operacaoVisualizarBusca }">
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="<< Voltar" action="#{analiseManifestacaoOuvidoria.acompanharManifestacao }" />
					</td>
				</tr>
			</tfoot>
		</c:if>
	</table>
	
	<c:if test="${!analiseManifestacaoOuvidoria.operacaoVisualizacao }">
		<br>
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" /> 
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br> <br>
		</center>
	</c:if>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>