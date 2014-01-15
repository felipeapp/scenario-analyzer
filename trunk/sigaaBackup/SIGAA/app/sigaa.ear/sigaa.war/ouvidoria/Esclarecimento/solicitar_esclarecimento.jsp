<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="esclarecimentoOuvidoria" />

<f:view>
	<h:form id="form">
	<h2>
		<ufrn:subSistema />
		<h:outputText value=" > " />
		<h:commandLink value="Acompanhar Manifestações" action="#{esclarecimentoOuvidoria.acompanhar }" />	
		&gt; Solicitar Esclarecimento
	</h2>
	
		<div class="descricaoOperacao">
			<b>Caro Ouvidor,</b> 
			<br/><br/>
			É possível enviar um pedido de esclarecimento para o usuário interessado na manifestação. Caso o pedido de esclarecimento seja enviado o interessado receberá
			um e-mail com as suas dúvidas e a manifestação ficará suspensa esperando resposta. Caso o usuário demore muito para responder ainda será possível enviar um novo pedido de esclarecimento que sobrescreverá o antigo.  
		</div>				
	
		<table class="formulario" width="100%">
			<caption>Solicitar Esclarecimento</caption>
			<tbody>
				<tr>
					<td>
						<table class="subFormulario" width="100%">
							<tbody>
								<tr>
									<th valign="top" class="required" width="15%">Esclarecimento: </th>
									<td>
										<h:inputTextarea value="#{esclarecimentoOuvidoria.historico.resposta }" style="width: 95%;" rows="5" disabled="#{esclarecimentoOuvidoria.obj.respondida }" />
									</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="Enviar Solicitação" action="#{esclarecimentoOuvidoria.enviarEmailEsclarecimento }" />
					<h:commandButton value="<< Voltar" action="#{esclarecimentoOuvidoria.acompanhar }" rendered="#{ !esclarecimentoOuvidoria.forwardPendentes }"/>
					<h:commandButton value="<< Voltar" action="#{esclarecimentoOuvidoria.retornarPendentes }" rendered="#{ esclarecimentoOuvidoria.forwardPendentes }"/>
					<h:commandButton value="Cancelar" action="#{esclarecimentoOuvidoria.cancelar }" onclick="#{confirm }" />
				</td></tr>
			</tfoot>
		</table>
		<br /><br />
	
	<table class="formulario" width="100%">
		<caption>Detalhes da Manifestação</caption>
	<tbody>
	<tr><td>
		<table class="subFormulario" style="width: 100%">
			<caption>Dados do Interessado</caption>
			<c:if test="${!esclarecimentoOuvidoria.comunidadeInterna }">
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">Nome: </td>
					<td colspan="3"><h:outputText value="#{esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.nome == null ? '-' : esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.nome }" /></td>
				</tr>
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">E-Mail: </td>
					<td colspan="3"><h:outputText value="#{esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.email == null ? '-' : esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.email }" /></td>
				</tr>
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">Telefone: </td>
					<td colspan=3><h:outputText value="#{esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.telefoneString }" /></td>
				</tr>
				<c:if test="${esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.enderecoCadastrado }">
					<tr>
						<td style="font-weight: bold; text-align: center;" width="17%" colspan="4"><u>Endereço</u></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">CEP: </td>
						<td colspan="3"><h:outputText value="#{esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.cep == null ? '-' : esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.cep }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Logradouro: </td>
						<td><h:outputText value="#{esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.logradouroString }" /></td>
						<td style="font-weight: bold; text-align: right;" width="17%">Número: </td>
						<td><h:outputText value="#{esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.numero == null ? '-' : esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.numero }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Bairro: </td>
						<td><h:outputText value="#{esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.bairro == null ? '-' : esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.bairro }" /></td>
						<td style="font-weight: bold; text-align: right;" width="17%">Complemento: </td>
						<td><h:outputText value="#{esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.complemento == null ? '-' : esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.complemento }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">UF: </td>
						<td><h:outputText value="#{esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.unidadeFederativa == null ? '-' : esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.unidadeFederativa.descricao }" /></td>
						<td style="font-weight: bold; text-align: right;" width="17%">Município: </td>
						<td><h:outputText value="#{esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.municipio == null ? '-' : esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.municipio.nome }" /></td>
					</tr>
				</c:if>
			</c:if>
			<c:if test="${esclarecimentoOuvidoria.comunidadeInterna }">
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">Categoria: </td>
					<td colspan="3"><h:outputText value="#{esclarecimentoOuvidoria.obj.interessadoManifestacao.categoriaSolicitante == null ? '-' : esclarecimentoOuvidoria.obj.interessadoManifestacao.categoriaSolicitante.descricao }" /></td>
				</tr>
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">Nome: </td>
					<td colspan="3"><h:outputText value="#{esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.pessoa.nome == null ? '-' : esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.pessoa.nome }" /></td>
				</tr>
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">E-Mail: </td>
					<td colspan="3"><h:outputText value="#{esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.pessoa.email == null ? '-' : esclarecimentoOuvidoria.obj.interessadoManifestacao.dadosInteressadoManifestacao.pessoa.email }" /></td>
				</tr>
			</c:if>
		</table>
		
		<c:set var="manifestacao" value="#{esclarecimentoOuvidoria.obj }" scope="page" />
		<c:set var="historicos" value="#{esclarecimentoOuvidoria.historicos }" scope="page" />
		<c:set var="copias" value="#{esclarecimentoOuvidoria.copias }" scope="page" />
		<c:set var="delegacoes" value="#{esclarecimentoOuvidoria.delegacoes }" scope="page" />
		<%@ include file="/ouvidoria/Manifestacao/include/dados_detalhes_manifestacao.jsp" %>
		
		</td></tr>
		</tbody>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>