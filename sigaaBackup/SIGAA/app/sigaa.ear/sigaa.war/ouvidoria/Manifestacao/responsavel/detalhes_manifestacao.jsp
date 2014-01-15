<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="analiseManifestacaoResponsavel" />

<f:view>
	<h:form id="form">
	<h2>
		<ufrn:subSistema /><h:outputText value=" > " rendered="#{!analiseManifestacaoResponsavel.operacaoVisualizarBusca }" /><h:commandLink value="Manifestações Pendentes" action="#{analiseManifestacaoResponsavel.listarPendentes }" rendered="#{!analiseManifestacaoResponsavel.operacaoVisualizarBusca }" /> &gt; Detalhes da Manifestação
	</h2>
	
	<c:if test="${analiseManifestacaoResponsavel.operacaoResponder }">
		<table class="formulario" width="100%">
			<caption>Responder Manifestação</caption>
			<tbody>
				<tr>
					<th valign="top" class="required">Resposta: </td>
					<td width="85%">
						<h:inputTextarea value="#{analiseManifestacaoResponsavel.historico.resposta }" style="width: 95%;" rows="5" />
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="Enviar Resposta ao Interessado" action="#{analiseManifestacaoResponsavel.enviarRespostaInteressado }" />
					<h:commandButton value="Encaminhar à Ouvidoria" action="#{analiseManifestacaoResponsavel.enviarRespostaOuvidoria }" />
					<h:commandButton value="<< Voltar" action="#{analiseManifestacaoResponsavel.listarPendentes }" />
					<h:commandButton value="Cancelar" action="#{analiseManifestacaoResponsavel.cancelar }" onclick="#{confirm }" />
				</td></tr>
			</tfoot>
		</table>
		<br /><br />
	</c:if>
	
	<c:if test="${analiseManifestacaoResponsavel.operacaoEncaminhar }">
		<table class="formulario" width="100%">
			<caption>Encaminhar Manifestação</caption>
			<tbody>
				<tr>
					<th valign="top" class="required">Pessoa: </th>
					<td width="82%">
						<h:inputText id="nomePessoa" value="#{analiseManifestacaoResponsavel.delegacao.pessoa.nome }" size="55" />
						
						<rich:suggestionbox for="nomePessoa" width="450" height="100" minChars="3" nothingLabel="Nenhum Usuário Encontrado com o Nome Informado."
											suggestionAction="#{designadoAutoComplemeMBean.autocompleteDesignado }" var="_pessoa" fetchValue="#{ _pessoa.nome }">
							<h:column>
								   <h:outputText value="#{_pessoa.nome}" /> 
							</h:column>
							<f:param name="apenasAtivos" value="true" />
							<f:param name="idUnidade" value="#{analiseManifestacaoResponsavel.historicoPendenteResposta.unidadeResponsavel.id }" />
							<a4j:support event="onselect">
								<f:param name="apenasAtivos" value="true" />
								<f:param name="idUnidade" value="#{analiseManifestacaoResponsavel.historicoPendenteResposta.unidadeResponsavel.id }" />
								<f:setPropertyActionListener value="#{_pessoa.id }" target="#{analiseManifestacaoResponsavel.delegacao.pessoa.id }" />
							</a4j:support>
						</rich:suggestionbox>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="Encaminhar Manifestacao" action="#{analiseManifestacaoResponsavel.cadastrarEncaminhamento }" />
					<h:commandButton value="<< Voltar" action="#{analiseManifestacaoResponsavel.listarPendentes }" rendered="#{!analiseManifestacaoOuvidoria.operacaoVisualizarBusca }" />
					<h:commandButton value="Cancelar" action="#{analiseManifestacaoResponsavel.cancelar }" onclick="#{confirm }" />
				</td></tr>
			</tfoot>
		</table>
		<br /><br />
	</c:if>
	
	<c:if test="${analiseManifestacaoResponsavel.operacaoReencaminhar }">
		<table class="formulario" width="100%">
			<caption>Encaminhar Manifestação</caption>
			<tbody>
				<tr>
					<td colspan="2">
						<table class="subFormulario" width="100%">
							<caption>Dados da Designação Ativa</caption>
							<tbody>
								<tr>
									<th style="font-weight: bold;">Pessoa: </th>
									<td width="85%">${analiseManifestacaoResponsavel.delegacaoAntiga.pessoa.nome }</td>
								</tr>
								<tr>
									<th style="font-weight: bold;">Data da delegação: </th>
									<td><ufrn:format type="data" valor="${analiseManifestacaoResponsavel.delegacaoAntiga.dataCadastro }" /></td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<table class="subFormulario" width="100%">
							<caption>Dados da Nova Designação</caption>
							<tbody>
								<th valign="top" class="required" style="font-weight: bold;">Pessoa: </th>
								<td width="85%">
									<h:inputText id="nomePessoa" value="#{analiseManifestacaoResponsavel.delegacao.pessoa.nome }" size="55" />
									
									<rich:suggestionbox for="nomePessoa" width="450" height="100" minChars="3" nothingLabel="Nenhum Usuário Encontrado com o Nome Informado."
														suggestionAction="#{designadoAutoComplemeMBean.autocompleteDesignado }" var="_pessoa" fetchValue="#{ _pessoa.nome }">
										<h:column>
											   <h:outputText value="#{_pessoa.nome}" /> 
										</h:column>
										<f:param name="apenasAtivos" value="true" />
										<f:param name="idUnidade" value="#{analiseManifestacaoResponsavel.historicoPendenteResposta.unidadeResponsavel.id }" />
										<a4j:support event="onselect">
											<f:param name="apenasAtivos" value="true" />
											<f:param name="idUnidade" value="#{analiseManifestacaoResponsavel.historicoPendenteResposta.unidadeResponsavel.id }" />
											<f:setPropertyActionListener value="#{_pessoa.id }" target="#{analiseManifestacaoResponsavel.delegacao.pessoa.id }" />
										</a4j:support>
									</rich:suggestionbox>
								</td>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="Encaminhar Manifestacao" action="#{analiseManifestacaoResponsavel.cadastrarReencaminhamento }" />
					<h:commandButton value="<< Voltar" action="#{analiseManifestacaoResponsavel.listarPendentes }" rendered="#{!analiseManifestacaoOuvidoria.operacaoVisualizarBusca }" />
					<h:commandButton value="Cancelar" action="#{analiseManifestacaoResponsavel.cancelar }" onclick="#{confirm }" />
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
		<c:if test="${!analiseManifestacaoResponsavel.obj.anonima }">
			<table class="subFormulario" style="width: 100%">
				<caption>Dados do Interessado</caption>
				<c:if test="${!analiseManifestacaoResponsavel.comunidadeInterna }">
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Nome: </td>
						<td colspan="3"><h:outputText value="#{analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.nome == null ? '-' : analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.nome }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">E-Mail: </td>
						<td colspan="3"><h:outputText value="#{analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.email == null ? '-' : analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.email }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Telefone: </td>
						<td colspan=3><h:outputText value="#{analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.telefoneString }" /></td>
					</tr>
					<c:if test="${analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.enderecoCadastrado }">
						<tr>
							<td style="font-weight: bold; text-align: center;" width="17%" colspan="4"><u>Endereço</u></td>
						</tr>
						<tr>
							<td style="font-weight: bold; text-align: right;" width="17%">CEP: </td>
							<td colspan="3"><h:outputText value="#{analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.cep == null ? '-' : analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.cep }" /></td>
						</tr>
						<tr>
							<td style="font-weight: bold; text-align: right;" width="17%">Logradouro: </td>
							<td><h:outputText value="#{analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.logradouro == null ? '-' : analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.logradouro }" /></td>
							<td style="font-weight: bold; text-align: right;" width="17%">Número: </td>
							<td><h:outputText value="#{analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.numero == null ? '-' : analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.numero }" /></td>
						</tr>
						<tr>
							<td style="font-weight: bold; text-align: right;" width="17%">Bairro: </td>
							<td><h:outputText value="#{analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.bairro == null ? '-' : analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.bairro }" /></td>
							<td style="font-weight: bold; text-align: right;" width="17%">Complemento: </td>
							<td><h:outputText value="#{analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.complemento == null ? '-' : analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.complemento }" /></td>
						</tr>
						<tr>
							<td style="font-weight: bold; text-align: right;" width="17%">UF: </td>
							<td><h:outputText value="#{analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.unidadeFederativa == null ? '-' : analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.unidadeFederativa.descricao }" /></td>
							<td style="font-weight: bold; text-align: right;" width="17%">Município: </td>
							<td><h:outputText value="#{analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.municipio == null ? '-' : analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.municipio.nome }" /></td>
						</tr>
					</c:if>
				</c:if>
				<c:if test="${analiseManifestacaoResponsavel.comunidadeInterna }">
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Categoria: </td>
						<td colspan="3"><h:outputText value="#{analiseManifestacaoResponsavel.obj.interessadoManifestacao.categoriaSolicitante == null ? '-' : analiseManifestacaoResponsavel.obj.interessadoManifestacao.categoriaSolicitante.descricao }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Nome: </td>
						<td colspan="3"><h:outputText value="#{analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.pessoa.nome == null ? '-' : analiseManifestacaoResponsavel.obj.interessadoManifestacao.dadosInteressadoManifestacao.pessoa.nome }" /></td>
					</tr>
				</c:if>
			</table>
		</c:if>
		
		<c:set var="manifestacao" value="#{analiseManifestacaoResponsavel.obj }" scope="page" />
		<c:set var="historicos" value="#{analiseManifestacaoResponsavel.historicos }" scope="page" />
		<c:set var="copias" value="#{analiseManifestacaoResponsavel.copias }" scope="page" />
		<c:set var="delegacoes" value="#{analiseManifestacaoResponsavel.delegacoes }" scope="page" />
		<%@ include file="/ouvidoria/Manifestacao/include/dados_detalhes_manifestacao.jsp" %>
		
		</td></tr>
		</tbody>
		<c:if test="${analiseManifestacaoResponsavel.operacaoVisualizarPendente }">
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="Responder Manifestação" action="#{analiseManifestacaoResponsavel.responderManifestacaoCarregada }" rendered="#{!analiseManifestacaoResponsavel.obj.designada }" />
						<h:commandButton value="Encaminhar Manifestação" action="#{analiseManifestacaoResponsavel.encaminharManifestacaoCarregada }" rendered="#{analiseManifestacaoResponsavel.obj.passivelDesignacao }" />
						<h:commandButton value="<< Voltar" action="#{analiseManifestacaoResponsavel.listarPendentes }" />
					</td>
				</tr>
			</tfoot>
		</c:if>
		<c:if test="${analiseManifestacaoResponsavel.operacaoVisualizarBusca }">
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="<< Voltar" action="#{analiseManifestacaoResponsavel.buscarManifestacao }" />
					</td>
				</tr>
			</tfoot>
		</c:if>
	</table>
	
	<c:if test="${!analiseManifestacaoResponsavel.operacaoVisualizacao }">
		<br>
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" /> 
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br> <br>
		</center>
	</c:if>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>