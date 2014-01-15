<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="analiseManifestacaoOuvidoria" />

<style>
#listagem thead>tr>th {
	border: none;
}
</style>

<script type="text/javascript">var J = jQuery.noConflict();</script>
<script type="text/javascript">
	JAWR.loader.script('/javascript/jquery.tablesorter.min.js');
</script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<f:view>
	<h2>
		<ufrn:subSistema /> &gt; Manifestações Pendentes
	</h2>
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Detalhes da Manifestação
		<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Editar Manifestação<br />
		<h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;" />: Responder Manifestação
		<h:graphicImage value="/img/addUnd.gif" style="overflow: visible;" />: Encaminhar Manifestação
		<h:graphicImage value="/img/notificar.png" style="overflow: visible;" />: Solicitar Esclarecimentos
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Manifestação 
	</div>

	<br />

	<h:form id="form">
		<table class="listagem tablesorter" id="listagem" width="100%" cellpadding="3px">
			<caption>Manifestações Pendentes(${ fn:length(analiseManifestacaoOuvidoria.manifestacoes) })</caption>
				<thead>
					<tr>
						<th style="text-align: center; padding-right: 15px;">Data de Cadastro</th>
						<th style="padding-left: 10px; padding-right: 15px;text-align: right;">Ano/Número</th>
						<th style="padding-left: 10px; padding-right: 15px;" width="11%">Categoria do Solicitante</th>
						<th style="padding-left: 10px; padding-right: 15px;">Nome</th>
						<th style="padding-left: 10px; padding-right: 15px;">Origem</th>
						<th style="padding-left: 10px; padding-right: 15px;" width="11%">Categoria do Assunto</th>
						<th style="padding-left: 10px; padding-right: 15px;">Assunto</th>
						<th style="padding-left: 10px; padding-right: 15px;">Título</th>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{analiseManifestacaoOuvidoria.manifestacoes }" var="manifestacao" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<td style="text-align: center;">
								<ufrn:format valor="${manifestacao.dataCadastro }" type="datahora"/>
							</td>
							<td style="text-align: right; padding-left: 17px;">${manifestacao.numeroAno }</td>
							<td style="text-align: left;">${manifestacao.interessadoManifestacao.categoriaSolicitante.descricao }</td>
							<td style="text-align: left;">${manifestacao.interessadoManifestacao.dadosInteressadoManifestacao.nome }</td>
							<td style="text-align: left;padding-left: 10px;">${manifestacao.origemManifestacao.descricao }</td>
							<td style="text-align: left;">${manifestacao.assuntoManifestacao.categoriaAssuntoManifestacao.descricao }</td>
							<td style="text-align: left;">${manifestacao.assuntoManifestacao.descricao }</td>
							<td style="text-align: left;">${manifestacao.titulo }</td>
							<td>
								<h:commandButton image="/img/view.gif" title="Visualizar Detalhes da Manifestação" actionListener="#{analiseManifestacaoOuvidoria.detalharManifestacaoPendente }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</td>
							<td>
								<h:commandButton image="/img/alterar.gif" title="Editar Manifestação" actionListener="#{analiseManifestacaoOuvidoria.editarManifestacao }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</td>
							<td>
								<h:commandButton image="/img/arrow_undo.png" title="Responder Manifestação" actionListener="#{analiseManifestacaoOuvidoria.responderManifestacaoPendente }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</td>
							<td>
								<h:commandButton image="/img/addUnd.gif" title="Encaminhar Manifestação" actionListener="#{analiseManifestacaoOuvidoria.encaminharManifestacao }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</td>
							<td>
								<h:commandLink title="Solicitar Esclarecimentos" action="#{esclarecimentoOuvidoria.solicitarEsclarecimentos }" rendered="#{!manifestacao.finalizada && analiseManifestacaoOuvidoria.usuarioOuvidor}">
									<h:graphicImage value="/img/notificar.png"/>
									<f:param name="idManifestacao" value="#{manifestacao.id}" />
									<f:param name="pendentes" value="true" />
								</h:commandLink>
							</td>
							<td>
								<h:commandLink title="Remover Manifestação" action="#{analiseManifestacaoOuvidoria.removerManifestacao }" onclick="return confirm('Tem certeza que deseja remover essa manifestação?')">
									<h:graphicImage value="/img/delete.gif"/>
									<f:param name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandLink>
							</td>
						</tr>
					</c:forEach>
				</tbody>
		</table>
		<rich:jQuery selector="#listagem" query="tablesorter( {dateFormat: 'uk', headers: {8: { sorter: false },9: { sorter: false },10: { sorter: false },11: { sorter: false },12: { sorter: false } } });" timing="onload" />
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>