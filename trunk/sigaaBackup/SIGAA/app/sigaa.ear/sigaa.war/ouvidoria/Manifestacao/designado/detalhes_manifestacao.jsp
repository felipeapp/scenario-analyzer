<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="analiseManifestacaoDesignado" />

<f:view>
	<h:form id="form">
	<h2>
		<ufrn:subSistema /><h:outputText value=" > " rendered="#{!analiseManifestacaoOuvidoria.operacaoVisualizarBusca }" /><h:commandLink value="Manifestações Pendentes" action="#{analiseManifestacaoDesignado.listarPendentes }" rendered="#{!analiseManifestacaoDesignado.operacaoVisualizarBusca }" /> &gt; Detalhes da Manifestação
	</h2>
	
	<c:if test="${analiseManifestacaoDesignado.operacaoResponder }">
		<table class="formulario" width="100%">
			<caption>Responder Manifestação</caption>
			<tbody>
				<tr>
					<th valign="top" class="required">Resposta: </td>
					<td width="85%">
						<h:inputTextarea value="#{analiseManifestacaoDesignado.historico.respostaUnidade }" style="width: 95%;" rows="5" />
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="Enviar Resposta à Unidade" action="#{analiseManifestacaoDesignado.enviarResposta }" />
					<h:commandButton value="<< Voltar" action="#{analiseManifestacaoDesignado.listarPendentes }" />
					<h:commandButton value="Cancelar" action="#{analiseManifestacaoDesignado.cancelar }" onclick="#{confirm }" />
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
		<c:if test="${!analiseManifestacaoDesignado.obj.anonima }">
			<table class="subFormulario" style="width: 100%">
				<caption>Dados do Interessado</caption>
				<c:if test="${!analiseManifestacaoDesignado.comunidadeInterna }">
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Nome: </td>
						<td colspan="3"><h:outputText value="#{analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.nome == null ? '-' : analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.nome }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">E-Mail: </td>
						<td colspan="3"><h:outputText value="#{analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.email == null ? '-' : analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.email }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Telefone: </td>
						<td colspan=3><h:outputText value="#{analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.telefoneString }" /></td>
					</tr>
					<c:if test="${analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.enderecoCadastrado }">
						<tr>
							<td style="font-weight: bold; text-align: center;" width="17%" colspan="4"><u>Endereço</u></td>
						</tr>
						<tr>
							<td style="font-weight: bold; text-align: right;" width="17%">CEP: </td>
							<td colspan="3"><h:outputText value="#{analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.cep == null ? '-' : analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.cep }" /></td>
						</tr>
						<tr>
							<td style="font-weight: bold; text-align: right;" width="17%">Logradouro: </td>
							<td><h:outputText value="#{analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.logradouro == null ? '-' : analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.logradouro }" /></td>
							<td style="font-weight: bold; text-align: right;" width="17%">Número: </td>
							<td><h:outputText value="#{analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.numero == null ? '-' : analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.numero }" /></td>
						</tr>
						<tr>
							<td style="font-weight: bold; text-align: right;" width="17%">Bairro: </td>
							<td><h:outputText value="#{analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.bairro == null ? '-' : analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.bairro }" /></td>
							<td style="font-weight: bold; text-align: right;" width="17%">Complemento: </td>
							<td><h:outputText value="#{analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.complemento == null ? '-' : analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.complemento }" /></td>
						</tr>
						<tr>
							<td style="font-weight: bold; text-align: right;" width="17%">UF: </td>
							<td><h:outputText value="#{analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.unidadeFederativa == null ? '-' : analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.unidadeFederativa.descricao }" /></td>
							<td style="font-weight: bold; text-align: right;" width="17%">Município: </td>
							<td><h:outputText value="#{analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.municipio == null ? '-' : analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.interessadoNaoAutenticado.endereco.municipio.nome }" /></td>
						</tr>
					</c:if>
				</c:if>
				<c:if test="${analiseManifestacaoDesignado.comunidadeInterna }">
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Categoria: </td>
						<td colspan="3"><h:outputText value="#{analiseManifestacaoDesignado.obj.interessadoManifestacao.categoriaSolicitante == null ? '-' : analiseManifestacaoDesignado.obj.interessadoManifestacao.categoriaSolicitante.descricao }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Nome: </td>
						<td colspan="3"><h:outputText value="#{analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.pessoa.nome == null ? '-' : analiseManifestacaoDesignado.obj.interessadoManifestacao.dadosInteressadoManifestacao.pessoa.nome }" /></td>
					</tr>
				</c:if>
			</table>
		</c:if>
		
		<c:set var="manifestacao" value="#{analiseManifestacaoDesignado.obj }" scope="page" />
		<c:set var="historicos" value="#{analiseManifestacaoDesignado.historicos }" scope="page" />
		<c:set var="copias" value="#{analiseManifestacaoDesignado.copias }" scope="page" />
		<c:set var="delegacoes" value="#{analiseManifestacaoDesignado.delegacoes }" scope="page" />
		<%@ include file="/ouvidoria/Manifestacao/include/dados_detalhes_manifestacao.jsp" %>
		
		</td></tr>
		</tbody>
		<c:if test="${analiseManifestacaoDesignado.operacaoVisualizarPendente }">
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="Responder Manifestação" action="#{analiseManifestacaoDesignado.responderManifestacaoCarregada }" />
						<h:commandButton value="<< Voltar" action="#{analiseManifestacaoDesignado.listarPendentes }" />
					</td>
				</tr>
			</tfoot>
		</c:if>
		<c:if test="${analiseManifestacaoDesignado.operacaoVisualizarBusca }">
			<tfoot>
				<tr>
					<td>
						<h:commandButton value="<< Voltar" action="#{analiseManifestacaoDesignado.buscarManifestacao }" />
					</td>
				</tr>
			</tfoot>
		</c:if>
	</table>
	
	<c:if test="${!analiseManifestacaoDesignado.operacaoVisualizarBusca }">
		<br>
		<center>
			<html:img page="/img/required.gif" style="vertical-align: top;" /> 
			<span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br> <br>
		</center>
	</c:if>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>