<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2>
		<ufrn:subSistema /> &gt; Acompanhar Manifesta��es
	</h2>
	
	<div class="descricaoOperacao">
		<p>Caro servidor,</p>
		<p>Aqui voc� pode acompanhar todas as suas manifesta��es cadastradas para a ouvidoria. � poss�vel, ainda, visualizar os detalhes da manifesta��o e sua resposta clicando em Visualizar Detalhes da Manifesta��o.</p>
	</div>

	<h:form id="formBusca">
			<br />
			<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Detalhes da Manifesta��o
			</div>
	
			<br />
			
			<table class="listagem" width="100%" cellpadding="3px">
				<caption>Manifesta��es Encontradas(${analiseManifestacaoServidor.totalManifestacoes })</caption>
				<c:if test="${not empty analiseManifestacaoServidor.manifestacoesCopiadas }">
					<tr><td colspan="7" class="subFormulario" style="text-align: center;">Minhas Manifesta��es(${ fn:length(analiseManifestacaoServidor.manifestacoes) })</td></tr>
				</c:if>
				<c:if test="${empty analiseManifestacaoServidor.manifestacoes }">
					<tr class="vazio" style="text-align: center;">
						<td>
							N�o foi encontrada nenhuma manifesta��o cadastrada para seu usu�rio.
						</td>
					</tr>
				</c:if>
				<c:if test="${not empty analiseManifestacaoServidor.manifestacoes }">
					<tr><td>
					<table class="listagem" width="100%" cellpadding="3px">
					<thead>
						<tr>
							<th style="text-align: center;">Data de Cadastro</th>
							<th style="padding-right: 10%; text-align: right;">N�mero/Ano</th>
							<th>Status</th>
							<th>Categoria do Solicitante</th>
							<th>T�tulo</th>
							<th></th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<c:forEach items="#{analiseManifestacaoServidor.manifestacoes }" var="manifestacao" varStatus="status">
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
									<h:commandButton image="/img/view.gif" title="Visualizar Detalhes da Manifesta��o" actionListener="#{analiseManifestacaoServidor.detalharManifestacaoBuscada }">
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
				<c:if test="${not empty analiseManifestacaoServidor.manifestacoesCopiadas }">
					<tr><td colspan="7" class="subFormulario" style="text-align: center;">Manifesta��es Copiadas Para Mim(${ fn:length(analiseManifestacaoServidor.manifestacoesCopiadas) })</td></tr>
					<tr><td>
						<table class="listagem" width="100%" cellpadding="3px">
							<thead>
								<tr>
									<th style="text-align: center;">Data de Cadastro</th>
									<th style="padding-right: 10%; text-align: right;">N�mero/Ano</th>
									<th>Status</th>
									<th>Categoria do Solicitante</th>
									<th>T�tulo</th>
									<th></th>
									<th></th>
								</tr>
							</thead>
							<tbody>
								<c:forEach items="#{analiseManifestacaoServidor.manifestacoesCopiadas }" var="manifestacao" varStatus="status">
									<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
										<th style="text-align: center;"><ufrn:format  valor="${manifestacao.dataCadastro }" type="datahora"/></th>
										<th style="padding-right: 10%; text-align: right;">${manifestacao.numeroAno }</th>
										<th style="text-align: left;">${manifestacao.statusManifestacao.descricao }</th>
										<th style="text-align: left;">
											<c:if test="${manifestacao.anonima }">
												<i>Manifesta��o An�nima</i>
											</c:if>
											<c:if test="${!manifestacao.anonima }">
												${manifestacao.interessadoManifestacao.categoriaSolicitante.descricao }
											</c:if>
										</th>
										<th style="text-align: left;">${manifestacao.titulo }</th>
										<th>
											<h:commandButton image="/img/view.gif" title="Visualizar Detalhes da Manifesta��o" actionListener="#{analiseManifestacaoServidor.detalharManifestacaoBuscada }">
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