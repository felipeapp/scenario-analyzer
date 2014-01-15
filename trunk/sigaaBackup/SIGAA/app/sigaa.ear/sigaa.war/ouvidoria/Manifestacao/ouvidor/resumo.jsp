<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="manifestacaoOuvidoria" />

<f:view>
	<h2>
		<ufrn:subSistema /> &gt; Cadastro de Manifestação
	</h2>

	<div id="ajuda" class="descricaoOperacao">
		<p>Caro usuário, por favor confirme se os dados mostrados abaixo conferem com os dados desejados para a Manifestação.</p>
		<p>Caso exista algum dado incorreto, é possível voltar às telas anteriores e editar os dados informados.</p>
	</div>

	<h:form id="form">
	<table class="formulario" width="100%">
	<caption>Confirmação</caption>
	<tr><td>
		<table class="subFormulario" style="width: 100%">
			<caption>Dados do Interessado</caption>
			<c:if test="${!manifestacaoOuvidoria.comunidadeInterna }">
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">Nome: </td>
					<td colspan="3"><h:outputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.nome == null ? '-' : manifestacaoOuvidoria.interessadoNaoAutenticado.nome }" /></td>
				</tr>
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">E-Mail: </td>
					<td colspan="3"><h:outputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.email == null ? '-' : manifestacaoOuvidoria.interessadoNaoAutenticado.email }" /></td>
				</tr>
				<tr>
					<td style="font-weight: bold; text-align: right;" width="17%">Telefone: </td>
					<td colspan=3><h:outputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.telefoneString }" /></td>
				</tr>
				<c:if test="${manifestacaoOuvidoria.interessadoNaoAutenticado.enderecoCadastrado }">
					<tr>
						<td style="font-weight: bold; text-align: center;" width="17%" colspan="4"><u>Endereço</u></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">CEP: </td>
						<td colspan="3"><h:outputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.cep == null ? '-' : manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.cep }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Logradouro: </td>
						<td><h:outputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.logradouroString }" /></td>
						<td style="font-weight: bold; text-align: right;" width="17%">Número: </td>
						<td><h:outputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.numero == null ? '-' : manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.numero }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Bairro: </td>
						<td><h:outputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.bairro == null ? '-' : manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.bairro }" /></td>
						<td style="font-weight: bold; text-align: right;" width="17%">Complemento: </td>
						<td><h:outputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.complemento == null ? '-' : manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.complemento }" /></td>
					</tr>
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">UF: </td>
						<td><h:outputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.unidadeFederativa == null ? '-' : manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.unidadeFederativa.descricao }" /></td>
						<td style="font-weight: bold; text-align: right;" width="17%">Município: </td>
						<td><h:outputText value="#{manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.municipio == null ? '-' : manifestacaoOuvidoria.interessadoNaoAutenticado.endereco.municipio.nome }" /></td>
					</tr>
				</c:if>
			</c:if>
			<c:if test="${manifestacaoOuvidoria.comunidadeInterna }">
				<tr>
					<th width="17%" style="font-weight: bold; text-align: right;">Categoria do Solicitante:</th>
					<td colspan="3" style="text-align: left;">${manifestacaoOuvidoria.obj.interessadoManifestacao.categoriaSolicitante.descricao }</td>
				</tr>
				<c:if test="${manifestacaoOuvidoria.categoriaDiscente }">
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Nome: </td>
						<td colspan="3"><h:outputText value="#{manifestacaoOuvidoria.discente.pessoa.nome == null ? '-' : manifestacaoOuvidoria.discente.pessoa.nome }" /></td>
					</tr>
				</c:if>
				<c:if test="${!manifestacaoOuvidoria.categoriaDiscente }">
					<tr>
						<td style="font-weight: bold; text-align: right;" width="17%">Nome: </td>
						<td colspan="3"><h:outputText value="#{manifestacaoOuvidoria.servidor.pessoa.nome == null ? '-' : manifestacaoOuvidoria.servidor.pessoa.nome }" /></td>
					</tr>
				</c:if>
				<c:if test="${manifestacaoOuvidoria.categoriaDiscente }">
					<tr>
						<th width="17%" style="font-weight: bold; text-align: right;">Matrícula:</th>
						<td style="text-align: left;">
								${manifestacaoOuvidoria.discente.matricula }
						</td>
					</tr>
					<tr>
						<th width="17%" style="font-weight: bold; text-align: right;">Curso:</th>
						<td style="text-align: left;">
								${manifestacaoOuvidoria.discente.curso.descricao }
						</td>
					</tr>
				</c:if>
				<c:if test="${!manifestacaoOuvidoria.categoriaDiscente }">
					<tr>
						<th width="17%" style="font-weight: bold; text-align: right;">SIAPE:</th>
						<td style="text-align: left;">
								${manifestacaoOuvidoria.servidor.siape }
						</td>
					</tr>
					<tr>
						<th width="17%" style="font-weight: bold; text-align: right;">Lotação:</th>
						<td style="text-align: left;">
								${manifestacaoOuvidoria.servidor.unidade }
						</td>
					</tr>
				</c:if>
			</c:if>
		</table>
		
		<table class="subFormulario" style="width: 100%; table-layout: fixed; word-wrap:break-word;">
			<col width="17%">
			<col width="80%">
			<col width="3%">
			<caption>Dados da Manifestação</caption>
			<tbody>
				<tr>
					<td style="font-weight: bold; text-align: right;">Categoria do Assunto: </td>
					<td colspan="2"><h:outputText value="#{manifestacaoOuvidoria.obj.assuntoManifestacao.categoriaAssuntoManifestacao == null ? '-' : manifestacaoOuvidoria.obj.assuntoManifestacao.categoriaAssuntoManifestacao.descricao }" /></td>
				</tr>
				<tr>
					<td style="font-weight: bold; text-align: right;">Assunto: </td>
					<td colspan="2"><h:outputText value="#{manifestacaoOuvidoria.obj.assuntoManifestacao == null ? '-' : manifestacaoOuvidoria.obj.assuntoManifestacao.descricao }" /></td>
				</tr>
				<tr>
					<td style="font-weight: bold; text-align: right;">Tipo da Manifestação: </td>
					<td colspan="2"><h:outputText value="#{manifestacaoOuvidoria.obj.tipoManifestacao == null ? '-' : manifestacaoOuvidoria.obj.tipoManifestacao.descricao }" /></td>
				</tr>
				<tr>
					<td style="font-weight: bold; text-align: right;">Título: </td>
					<td colspan="2"><h:outputText value="#{manifestacaoOuvidoria.obj.titulo == null ? '-' : manifestacaoOuvidoria.obj.titulo }" /></td>
				<tr>
					<td style="font-weight: bold; text-align: right;">Arquivo: </td>
					<td colspan="2"><h:outputText value="#{manifestacaoOuvidoria.arquivo.name == null ? '-' : manifestacaoOuvidoria.arquivo.name }" /></td>
				</tr>
				
				<tr>
					<td style="font-weight: bold; text-align: right;">Sigilo Solicitado: </td>
					<td colspan="2"><ufrn:format type="simnao" valor="${manifestacaoOuvidoria.obj.anonima }" /></td>
				</tr>
				<tr>
					<td style="font-weight: bold; text-align: right; vertical-align: top;">Texto: </td>
					<td style="text-align: justify;""><h:outputText value="#{manifestacaoOuvidoria.obj.mensagem == null ? '-' : manifestacaoOuvidoria.obj.mensagem }" /></td>
					<td></td>
				</tr>
			</tbody>
		</table>
		</td></tr>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton id="btn_confirmar" value="Confirmar" action="#{manifestacaoOuvidoria.cadastrar }" />
					<h:commandButton id="btn_tipo_solicitante" value="<< Tipo Solicitante" action="#{manifestacaoOuvidoria.paginaTipoSolicitante }" />
					<h:commandButton id="btn_dados_interessado" value="<< Dados do Interessado" action="#{manifestacaoOuvidoria.paginaDadosInteressado }" rendered="#{!manifestacaoOuvidoria.comunidadeInterna }" />
					<h:commandButton id="btn_selecionar_usuario" value="<< Selecionar Usuário" action="#{manifestacaoOuvidoria.paginaSelecaoPessoa }" rendered="#{manifestacaoOuvidoria.comunidadeInterna }" />
					<h:commandButton id="btn_dados_manifestacao" value="<< Dados da Manifestação" action="#{manifestacaoOuvidoria.paginaDadosManifestacao }" />
					<h:commandButton id="btn_cancelar" value="Cancelar"  action="#{manifestacaoOuvidoria.cancelar }" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>