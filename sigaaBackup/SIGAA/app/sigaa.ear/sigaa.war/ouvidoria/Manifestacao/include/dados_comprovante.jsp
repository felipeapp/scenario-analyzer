<!-- Dados Pessoais de um InteressadoNaoAutenticado -->
<c:if test="${interessadoNaoAutenticado != null }">
	<table class="visualizacao" style="width: 90%">
		<tr>
			<th colspan="4" style="text-align: center;"><big>Dados Pessoais</big></th>
		</tr>
		<tr>
			<td style="font-weight: bold; text-align: right;" width="17%">Nome: </td>
			<td colspan="3"><h:outputText value="#{interessadoNaoAutenticado.nome == null ? '-' : interessadoNaoAutenticado.nome }" /></td>
		</tr>
		<tr>
			<td style="font-weight: bold; text-align: right;" width="17%">E-Mail: </td>
			<td colspan="3"><h:outputText value="#{interessadoNaoAutenticado.email == null ? '-' : interessadoNaoAutenticado.email }" /></td>
		</tr>
		<tr>
			<td style="font-weight: bold; text-align: right;" width="17%">Telefone: </td>
			<td colspan=3><h:outputText value="#{interessadoNaoAutenticado.telefoneString }" /></td>
		</tr>
		<c:if test="${interessadoNaoAutenticado.enderecoCadastrado }">
			<tr>
				<td style="font-weight: bold; text-align: center;" width="17%" colspan="4"><u>Endereço</u></td>
			</tr>
			<tr>
				<td style="font-weight: bold; text-align: right;" width="17%">CEP: </td>
				<td colspan="3"><h:outputText value="#{interessadoNaoAutenticado.endereco.cep == null ? '-' : interessadoNaoAutenticado.endereco.cep }" /></td>
			</tr>
			<tr>
				<td style="font-weight: bold; text-align: right;" width="17%">Logradouro: </td>
				<td><h:outputText value="#{interessadoNaoAutenticado.logradouroString }" /></td>
				<td style="font-weight: bold; text-align: right;" width="17%">Número: </td>
				<td><h:outputText value="#{interessadoNaoAutenticado.endereco.numero == null ? '-' : interessadoNaoAutenticado.endereco.numero }" /></td>
			</tr>
			<tr>
				<td style="font-weight: bold; text-align: right;" width="17%">Bairro: </td>
				<td><h:outputText value="#{interessadoNaoAutenticado.endereco.bairro == null ? '-' : interessadoNaoAutenticado.endereco.bairro }" /></td>
				<td style="font-weight: bold; text-align: right;" width="17%">Complemento: </td>
				<td><h:outputText value="#{interessadoNaoAutenticado.endereco.complemento == null ? '-' : interessadoNaoAutenticado.endereco.complemento }" /></td>
			</tr>
			<tr>
				<td style="font-weight: bold; text-align: right;" width="17%">UF: </td>
				<td><h:outputText value="#{interessadoNaoAutenticado.endereco.unidadeFederativa == null ? '-' : interessadoNaoAutenticado.endereco.unidadeFederativa.descricao }" /></td>
				<td style="font-weight: bold; text-align: right;" width="17%">Município: </td>
				<td><h:outputText value="#{interessadoNaoAutenticado.endereco.municipio == null ? '-' : interessadoNaoAutenticado.endereco.municipio.nome }" /></td>
			</tr>
		</c:if>
	</table>
</c:if>

<!-- Dados Pessoais de um Usuario do sistema -->
<c:if test="${interessado != null }">
	<table class="visualizacao" style="width: 90%">
		<tr>
			<th colspan="4" style="text-align: center;"><big>Dados Pessoais</big></th>
		</tr>
		<tr>
			<td style="font-weight: bold; text-align: right;" width="17%">Nome: </td>
			<td colspan="3"><h:outputText value="#{interessado.nome == null ? '-' : interessado.nome }" /></td>
		</tr>
		<c:if test="${discenteInteressado != null }">
			<tr>
				<th width="17%" style="font-weight: bold; text-align: right;">Matrícula:</th>
				<td style="text-align: left;">
						${discenteInteressado.matricula }
				</td>
			</tr>
			<tr>
				<th width="17%" style="font-weight: bold; text-align: right;">Curso:</th>
				<td style="text-align: left;">
						${discenteInteressado.curso.descricao }
				</td>
			</tr>
		</c:if>
		<c:if test="${servidorInteressado != null }">
			<tr>
				<th width="17%" style="font-weight: bold; text-align: right;">SIAPE:</th>
				<td style="text-align: left;">
						${servidorInteressado.siape }
				</td>
			</tr>
			<tr>
				<th width="17%" style="font-weight: bold; text-align: right;">Lotação:</th>
				<td style="text-align: left;">
						${servidorInteressado.unidade }
				</td>
			</tr>
		</c:if>
	</table>
</c:if>

<br />

<!-- Dados da Manifestação -->
<table class="visualizacao" style="width: 90%; table-layout: fixed; word-wrap:break-word;">
	<col width="17%">
	<col width="80%">
	<col width="3%">
	<tr>
		<th colspan="3" style="text-align: center;"><big>Dados da Manifestação</big></th>
	</tr>
	<tr>
		<td style="font-weight: bold; text-align: right;">Categoria do Assunto: </td>
		<td colspan="2"><h:outputText value="#{manifestacao.assuntoManifestacao.categoriaAssuntoManifestacao == null ? '-' : manifestacao.assuntoManifestacao.categoriaAssuntoManifestacao.descricao }" /></td>
	</tr>
	<tr>
		<td style="font-weight: bold; text-align: right;">Assunto: </td>
		<td colspan="2"><h:outputText value="#{manifestacao.assuntoManifestacao == null ? '-' : manifestacao.assuntoManifestacao.descricao }" /></td>
	</tr>
	<tr>
		<td style="font-weight: bold; text-align: right;">Tipo da Manifestação: </td>
		<td colspan="2"><h:outputText value="#{manifestacao.tipoManifestacao == null ? '-' : manifestacao.tipoManifestacao.descricao }" /></td>
	</tr>
	<tr>
		<td style="font-weight: bold; text-align: right;">Título: </td>
		<td colspan="2"><h:outputText value="#{manifestacao.titulo == null ? '-' : manifestacao.titulo }" /></td>
	</tr>
	<tr>
		<td style="font-weight: bold; text-align: right;">Arquivo: </td>
		<td colspan="2"><h:outputText value="#{arquivo.name == null ? '-' : arquivo.name }" /></td>
	</tr>
	<tr>
		<td style="font-weight: bold; text-align: right;">Sigilo Solicitado: </td>
		<td colspan="2"><ufrn:format type="simnao" valor="${manifestacao.anonima }" /></td>
	</tr>
	<tr>
		<td style="font-weight: bold; text-align: right; vertical-align: top;">Texto: </td>
		<td style="text-align: justify;""><h:outputText value="#{manifestacao.mensagem == null ? '-' : manifestacao.mensagem }" /></td>
		<td></td>
	</tr>
</table>