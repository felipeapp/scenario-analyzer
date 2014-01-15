<table class="subFormulario" style="width: 100%; table-layout: fixed; word-wrap:break-word;">
	<col width="17%">
	<col width="80%">
	<col width="3%">
	<tbody>
		<caption>Dados da Manifestação</caption>
		<tr>
			<td style="font-weight: bold; text-align: right;">Número/Ano: </td>
			<td colspan="2"><h:outputText value="#{manifestacao.numeroAno == null ? '-' : manifestacao.numeroAno }" /></td>
		</tr>
		<tr>
			<td style="font-weight: bold; text-align: right;">Origem da Manifestação: </td>
			<td colspan="2"><h:outputText value="#{manifestacao.origemManifestacao == null ? '-' : manifestacao.origemManifestacao.descricao }" /></td>
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
		<c:if test="${manifestacao.idAnexo != null }">
			<tr>
				<td style="font-weight: bold; text-align: right;">Arquivo: </td>
				<td colspan="2">
					
						<a target="_blank" href="${ configSistema['linkSigaa'] }/sigaa/verProducao?idProducao=${manifestacao.idAnexo}&key=${ sf:generateArquivoKey(manifestacao.idAnexo) }">
							<h:graphicImage url="/img/porta_arquivos/icones/desconhecido.png" style="border:none" title="Clique aqui para o arquivo anexado a manifestação." /> Visualizar
						</a>
				</td>
			</tr>
		</c:if>	
		<tr>
		<td style="font-weight: bold; text-align: right;">Manifestação Sigilosa: </td>
			<td colspan="2"><ufrn:format type="simnao" valor="${manifestacao.anonima }" /></td>
		</tr>
		<tr>
			<td style="font-weight: bold; text-align: right; vertical-align: top;">Texto: </td>
			<td style="text-align: justify;""><h:outputText value="#{manifestacao.mensagem == null ? '-' : manifestacao.mensagem }" /></td>
			<td></td>
		</tr>
	</tbody>
</table>
<c:if test="${historicos != null && not empty historicos }">
	<table class="subFormulario" width="100%">
	<caption>Histórico da Manifestação</caption>
	<tr><td>
		<c:forEach items="#{historicos }" var="historico" varStatus="loop">
			<table class="subFormulario" style="width: 100%; table-layout: fixed; word-wrap:break-word;">
				<col width="17%">
				<col width="80%">
				<col width="3%">
				<caption style="background-color: #F9FBFD;">${historico.tipoHistoricoManifestacao.descricao } (<ufrn:format  valor="${historico.dataCadastro }" type="datahora"/>)</caption>
				<tbody>
					<c:if test="${historico.ouvidoriaResponsavel }">
						<tr>
							<th style="font-weight: bold;" valign="top">Unidade: </th>
							<td>${historico.unidadeResponsavel.nome }</td>
						</tr>
						<tr>
							<th style="font-weight: bold;" valign="top">Responsável: </th>
							<td>${historico.pessoaResponsavel.nome }</td>
						</tr>
						<tr>
							<th style="font-weight: bold;" valign="top">Solicitação: </th>
							<td>${historico.solicitacao }</td>
						</tr>
						<tr>
							<th style="font-weight: bold;">Prazo de Resposta: </th>
							<td><ufrn:format  valor="${historico.prazoResposta }" type="data"/></td>
						</tr>
						<c:if test="${delegacoes != null && not empty delegacoes }">
							<c:forEach items="#{delegacoes }" var="delegacao" varStatus="loop">
								</tbody></table>
								<table class="subFormulario" style="width: 100%; table-layout: fixed; word-wrap:break-word;">
								<col width="17%">
								<col width="80%">
								<col width="3%">
								<caption style="background-color: #F9FBFD;">Responsável/Designado (<ufrn:format  valor="${delegacao.dataCadastro }" type="datahora"/>)</caption>
								<tbody>
								<tr>
									<th style="font-weight: bold;" valign="top">Designado: </th>
									<td>${delegacao.pessoa.nome }</td>
								</tr>
							</c:forEach>
							<c:if test="${historico.respostaUnidade != null }">
								</tbody></table>
								<table class="subFormulario" style="width: 100%; table-layout: fixed; word-wrap:break-word;">
								<col width="17%">
								<col width="80%">
								<col width="3%">
								<caption style="background-color: #F9FBFD;">Designado/Responsável (<ufrn:format  valor="${historico.dataRespostaUnidade }" type="datahora"/>)
								</caption>
								<tbody>
								<tr>
									<th style="font-weight: bold;" valign="top">Resposta à Unidade: </th>
									<td>${historico.respostaUnidade }</td>
								</tr>
								</tr></td>
							</c:if>
						</c:if>
						<c:if test="${historico.resposta != null && historico.dataResposta != null }">
							</tbody></table>
							<table class="subFormulario" style="width: 100%; table-layout: fixed; word-wrap:break-word;">
							<col width="17%">
							<col width="80%">
							<col width="3%">
							<caption style="background-color: #F9FBFD;">Responsável/Ouvidoria (<ufrn:format  valor="${historico.dataResposta }" type="datahora"/>)
							</caption>
							<tbody>
							<tr>
								<th style="font-weight: bold;" valign="top">Resposta à Ouvidoria: </th>
								<td>${historico.resposta }</td>
							</tr>
						</c:if>
					</c:if>
					<c:if test="${historico.respostaUsuario }">
						<tr>
							<th style="font-weight: bold;" valign="top">Resposta: </th>
							<td>${historico.resposta }</td>
						</tr>
						<tr>
							<th style="font-weight: bold;">Data de Resposta: </th>
							<td><ufrn:format  valor="${historico.dataResposta }" type="data"/></td>
						</tr>
					</c:if>
					<c:if test="${historico.solicitacaoEsclarecimento}">
						<tr>
							<th style="font-weight: bold;" valign="top">Solicitação de Esclarecimento: </th>
							<td>${historico.resposta }</td>
						</tr>
					</c:if>
					<c:if test="${historico.respostaEsclarecimento}">
						<tr>
							<th style="font-weight: bold;" valign="top">Resposta do Esclarecimento: </th>
							<td>${historico.resposta }</td>
						</tr>
						<tr>
							<th style="font-weight: bold;">Data da Resposta: </th>
							<td><ufrn:format  valor="${historico.dataResposta }" type="data"/></td>
						</tr>
					</c:if>
				</tbody>
			</table>
		</c:forEach>
	</td></tr>
	</table>
</c:if>
<c:if test="${copias != null && not empty copias }">
	<table class="subFormulario" width="100%">
	<caption>Cópias da Manifestação</caption>
	<tr><td>
		<table class="subFormulario" style="width: 100%; table-layout: fixed; word-wrap:break-word;">
			<col width="40%">
			<col width="60%">
			<thead>
				<th style="font-weight: bold; text-align: left;">Pessoa </th>
				<th style="font-weight: bold; text-align: left;">Unidade Responsável</th>
			</thead>
			<tbody>
				<c:forEach items="#{copias }" var="copia" varStatus="loop">
					<tr>
						<td>${copia.pessoa.nome }</td>
						<c:if test="${copia.unidadeResponsabilidade != null}">
							<td>${copia.unidadeResponsabilidade }</td>
						</c:if>
						<c:if test="${copia.unidadeResponsabilidade == null}">
							<td>-</td>
						</c:if>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</td></tr>
	</table>
</c:if>