<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2>
		<ufrn:subSistema /> &gt; Acompanhar Manifestações
	</h2>
	
	<div class="descricaoOperacao">
		<p>Caro docente,</p>
		<p>Aqui você pode acompanhar todas as suas manifestações cadastradas para a ouvidoria. É possível, ainda, visualizar os detalhes da manifestação e sua resposta clicando em Visualizar Detalhes da Manifestação.</p>
	</div>

	<h:form id="formBusca">
			<br />
			<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Detalhes da Manifestação
				<img src="/sigaa/img/email_go.png">: Responder Pedido de Esclarecimento
			</div>
	
			<br />
			
			<table class="listagem" width="100%" cellpadding="3px">
				<caption>Manifestações Encontradas(${analiseManifestacaoDocente.totalManifestacoes })</caption>
				<c:if test="${not empty analiseManifestacaoDocente.manifestacoesCopiadas }">
					<tr><td colspan="7" class="subFormulario" style="text-align: center;">Minhas Manifestações(${ fn:length(analiseManifestacaoDocente.manifestacoes) })</td></tr>
				</c:if>
				<c:if test="${empty analiseManifestacaoDocente.manifestacoes }">
					<tr class="vazio" style="text-align: center;">
						<td>
							Não foi encontrada nenhuma manifestação cadastrada para seu usuário.
						</td>
					</tr>
				</c:if>
				<c:if test="${not empty analiseManifestacaoDocente.manifestacoes }">
					<tr><td>
					<table class="listagem" width="100%" cellpadding="3px">
					<thead>
						<tr>
							<th style="text-align: center;">Data de Cadastro</th>
							<th style="padding-right: 10%; text-align: right;">Número/Ano</th>
							<th>Status</th>
							<th>Categoria do Solicitante</th>
							<th>Título</th>
							<th></th>
							<th></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="#{analiseManifestacaoDocente.manifestacoes }" var="manifestacao" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<th style="text-align: center;"><ufrn:format  valor="${manifestacao.dataCadastro }" type="datahora"/></th>
								<th style="padding-right: 10%; text-align: right;">${manifestacao.numeroAno }</th>
								<th style="text-align: left;">
									<c:if test="${manifestacao.respondida }">
										<p style='color:#00AA00; font-weight: bold;'>
									</c:if>
									${manifestacao.statusManifestacao.descricao }
								</th>
								<th style="text-align: left;">${manifestacao.interessadoManifestacao.categoriaSolicitante.descricao }</th>
								<th style="text-align: left;">${manifestacao.titulo }</th>
								<th>
									<h:commandButton image="/img/view.gif" title="Visualizar Detalhes da Manifestação" actionListener="#{analiseManifestacaoDocente.detalharManifestacaoBuscada }">
										<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
									</h:commandButton>
								</th>
								<th>
									<c:if test="${manifestacao.esperandoEsclarecimento }">
										<h:commandLink title="Responder Pedido de Esclarecimento" action="#{esclarecimentoOuvidoria.iniciarResponderEsclarecimento }">
											<f:param name="idManifestacao" value="#{manifestacao.id}" />
											<h:graphicImage value="/img/email_go.png"/>
										</h:commandLink>
									</c:if>
								</th>
								<th></th>
							</tr>
						</c:forEach>
					</tbody>
					</table>
					</td></tr>
				</c:if>
				<c:if test="${not empty analiseManifestacaoDocente.manifestacoesCopiadas }">
					<tr><td colspan="7" class="subFormulario" style="text-align: center;">Manifestações Copiadas Para Mim(${ fn:length(analiseManifestacaoDocente.manifestacoesCopiadas) })</td></tr>
					<tr><td>
						<table class="listagem" width="100%" cellpadding="3px">
							<thead>
								<tr>
									<th style="text-align: center;">Data de Cadastro</th>
									<th style="padding-right: 10%; text-align: right;">Número/Ano</th>
									<th>Status</th>
									<th>Categoria do Solicitante</th>
									<th>Título</th>
									<th></th>
									<th></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="#{analiseManifestacaoDocente.manifestacoesCopiadas }" var="manifestacao" varStatus="status">
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
										<th style="text-align: center;"><ufrn:format  valor="${manifestacao.dataCadastro }" type="datahora"/></th>
										<th  style="padding-right: 10%; text-align: right;">${manifestacao.numeroAno }</th>
										<th style="text-align: left;">${manifestacao.statusManifestacao.descricao }</th>
										<th style="text-align: left;">
											<c:if test="${manifestacao.anonima }">
												<i>Manifestação Anônima</i>
											</c:if>
											<c:if test="${!manifestacao.anonima }">
												${manifestacao.interessadoManifestacao.categoriaSolicitante.descricao }
											</c:if>
										</th>
										<th style="text-align: left;">${manifestacao.titulo }</th>
										<th>
											<h:commandButton image="/img/view.gif" title="Visualizar Detalhes da Manifestação" actionListener="#{analiseManifestacaoDocente.detalharManifestacaoBuscada }">
												<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
											</h:commandButton>
										</th>
										<th></th>
									</tr>
								</c:forEach>
							</tbody>
						</table>
					</td></tr>
				</c:if>
			</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>