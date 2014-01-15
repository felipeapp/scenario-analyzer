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
		<ufrn:subSistema /> &gt; Manifestações Respondidas
	</h2>
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Detalhes da Manifestação
		<h:graphicImage value="/img/check.png" style="overflow: visible;" />: Finalizar Manifestação
	</div>

	<br />

	<h:form id="form">
		<table class="listagem tablesorter" id="listagem" width="100%" cellpadding="5px">
			<caption>Manifestações Respondidas(${ fn:length(analiseManifestacaoOuvidoria.manifestacoes) })</caption>
				<thead>
					<tr>
						<th style="text-align: center;">Data de Cadastro</th>
						<th style="padding-right: 15px; text-align: right;">Número/Ano</th>
						<th>Categoria do Solicitante</th>
						<th style="padding-right: 15px;" width="10">Origem</th>
						<th>Categoria do Assunto</th>
						<th>Assunto</th>
						<th>Unidade Responsável</th>
						<th>Status</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{analiseManifestacaoOuvidoria.manifestacoes }" var="manifestacao" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<th style="text-align: center;"><ufrn:format  valor="${manifestacao.dataCadastro }" type="datahora"/></th>
							<th style="text-align: left; padding-left: 15px; padding-right: 15px; text-align: right;">${manifestacao.numeroAno }</th>
							<th style="text-align: left;">${manifestacao.interessadoManifestacao.categoriaSolicitante.descricao }</th>
							<th style="text-align: left;">${manifestacao.origemManifestacao.descricao }</th>
							<th style="text-align: left;">${manifestacao.assuntoManifestacao.categoriaAssuntoManifestacao.descricao }</th>
							<th style="text-align: left;">${manifestacao.assuntoManifestacao.descricao }</th>
							<th style="text-align: left;">${manifestacao.unidadeResponsavel }</th>
							<th style="text-align: left;">${manifestacao.statusManifestacao.descricao }</th>
							<th>
								<h:commandButton image="/img/view.gif" title="Visualizar Detalhes da Manifestação" actionListener="#{analiseManifestacaoOuvidoria.detalharManifestacaoRespondida }" rendered="#{!manifestacao.respondida }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
								<h:commandButton image="/img/view.gif" title="Visualizar Detalhes da Manifestação" actionListener="#{analiseManifestacaoOuvidoria.finalizarManifestacaoRespondida }" rendered="#{manifestacao.respondida }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</th>
							<th>
								<h:commandButton image="/img/check.png" title="Finalizar Manifestação" actionListener="#{analiseManifestacaoOuvidoria.finalizarManifestacaoRespondida }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</th>
						</tr>
					</c:forEach>
				</tbody>
		</table>
		<rich:jQuery selector="#listagem" query="tablesorter( {dateFormat: 'uk', headers: {8: { sorter: false },9: { sorter: false },10: { sorter: false },11: { sorter: false } } });" timing="onload" />
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>