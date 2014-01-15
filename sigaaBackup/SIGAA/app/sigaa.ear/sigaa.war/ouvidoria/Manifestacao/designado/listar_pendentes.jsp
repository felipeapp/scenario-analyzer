<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="analiseManifestacaoDesignado" />

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
		<h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;" />: Responder Manifestação
	</div>

	<br />

	<h:form id="form">
		<table class="listagem tablesorter" id="listagem" width="100%" cellpadding="3px">
			<caption>Manifestações Pendentes(${ fn:length(analiseManifestacaoDesignado.manifestacoes) })</caption>
				<thead>
					<tr>
						<th style="text-align: center;padding-right: 15px">Data de Cadastro</th>
						<th style="padding-left: 15px; padding-right: 15px;text-align: right;">Número/Ano</th>
						<th>Categoria do Solicitante</th>
						<th style="padding-right: 15px;">Nome</th>
						<th style="padding-right: 15px;">Origem</th>
						<th>Categoria do Assunto</th>
						<th style="padding-right: 15px;">Assunto</th>
						<th style="padding-right: 15px;">Título</th>
						<th style="padding-right: 15px;">Lida</th>
						<th></th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{analiseManifestacaoDesignado.manifestacoes }" var="manifestacao" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<th style="text-align: center;"><ufrn:format  valor="${manifestacao.dataCadastro }" type="datahora"/></th>
							<th style="text-align: right; padding-left: 15px;">${manifestacao.numeroAno }</th>
							<th style="text-align: left;">
								<c:if test="${manifestacao.anonima }">
									<i>Manifestação Anônima</i>
								</c:if>
								<c:if test="${!manifestacao.anonima }">
									${manifestacao.interessadoManifestacao.categoriaSolicitante.descricao }
								</c:if>
							</th>
							<th style="text-align: left;">
								<c:if test="${manifestacao.anonima }">
									<i>Manifestação Anônima</i>
								</c:if>
								<c:if test="${!manifestacao.anonima }">
									${manifestacao.interessadoManifestacao.dadosInteressadoManifestacao.nome }
								</c:if>
							</th>
							<th style="text-align: left;">${manifestacao.origemManifestacao.descricao }</th>
							<th style="text-align: left;">${manifestacao.assuntoManifestacao.categoriaAssuntoManifestacao.descricao }</th>
							<th style="text-align: left;">${manifestacao.assuntoManifestacao.descricao }</th>
							<th style="text-align: left;">${manifestacao.titulo }</th>
							<th style="text-align: left;"><ufrn:format  valor="${manifestacao.lida }" type="simnao"/></th>
							<th>
								<h:commandButton image="/img/view.gif" title="Visualizar Detalhes da Manifestação" actionListener="#{analiseManifestacaoDesignado.detalharManifestacaoPendente }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</th>
							<th>
								<h:commandButton image="/img/arrow_undo.png" title="Responder Manifestação" actionListener="#{analiseManifestacaoDesignado.responderManifestacao }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</th>
							<th></th>
						</tr>
					</c:forEach>
				</tbody>
		</table>
		<rich:jQuery selector="#listagem" query="tablesorter( {dateFormat: 'uk', headers: {9: { sorter: false },10: { sorter: false } } });" timing="onload" />
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>