<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<a4j:keepAlive beanName="analiseManifestacaoResponsavel" />

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
	
		<div class="descricaoOperacao" style="text-align: justify">
			<b>Caro usuário(a),</b> 
			<br/><br/>
			Nesta tela serão listadas as manifestações pendentes de respostas. Aqui será possível visualizar os detalhes da manifestação,
			designá-las para outro usuário, ou respondê-las para ouvidoria ou para o interessado.
			<br/><br/>
			Caso a manifestação seja designada para outro usuário, ele terá que respondê-la ao senhor(a), de modo que, o senhor(a) ainda terá de enviar uma resposta para a
			ouvidoria ou para o interessado.
			<br/><br/>
			Caso o tempo de resposta para a manifestação esteja próximo de acabar ou já tenha acabado, será enviado um e-mail de notificação. Para responder a
			manifestação é necessário clicar no botão abaixo "Responder Manifestação" e encaminhar a resposta para a ouvidoria ou para o interessado. Uma vez que a manifestação
			seja respondida os e-mails cessarão.
			<br/><br/>
			Caso sua preferência seja responder a manifestação fisicamente sem a utilização do SIGAA, os e-mails cessarão assim que a ouvidoria der baixa nas manifestações. 
			<br/><br/>
		</div>
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Detalhes da Manifestação
		<h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;" />: Responder Manifestação <br />
		<h:graphicImage value="/img/addUnd.gif" style="overflow: visible;" />: Designar Pessoa para Responder Manifestação
		<h:graphicImage value="/img/trocar.gif" style="overflow: visible;" />: Designar Outra Pessoa para Responder Manifestação
	</div>

	<br />

	<h:form id="form">
		<table class="listagem tablesorter" id="listagem" width="100%" cellpadding="3px">
			<caption>Manifestações Pendentes(${ fn:length(analiseManifestacaoResponsavel.manifestacoes) })</caption>
				<thead>
					<tr>
						<th style="text-align: center;padding-right: 15px;">Data de Cadastro</th>
						<th style="padding-right: 15px;text-align: right;">Ano/ Número</th>
						<th>Categoria do Solicitante</th>
						<th style="padding-right: 15px;">Nome</th>
						<th style="padding-right: 15px;">Origem</th>
						<th>Categoria do Assunto</th>
						<th style="padding-right: 15px;">Assunto</th>
						<th style="padding-right: 15px;">Título</th>
						<th style="padding-right: 15px;">Lida</th>
						<th style="padding-right: 15px;">Designada</th>
						<th></th>
						<th></th>
						<th></th>
						
					</tr>
				</thead>
				<tbody>
					<c:forEach items="#{analiseManifestacaoResponsavel.manifestacoes }" var="manifestacao" varStatus="status">
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
							<th style="text-align: center;"><ufrn:format  valor="${manifestacao.dataCadastro }" type="datahora"/></th>
							<th style="text-align: right;">${manifestacao.numeroAno }</th>
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
							<th style="text-align: center;"><ufrn:format  valor="${manifestacao.lida }" type="simnao"/></th>
							<th style="text-align: center;"><ufrn:format  valor="${manifestacao.designada || manifestacao.aguardandoParecer }" type="simnao"/></th>
							<th>
								<h:commandButton image="/img/view.gif" title="Visualizar Detalhes da Manifestação" actionListener="#{analiseManifestacaoResponsavel.detalharManifestacaoPendente }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</th>
							<th>
								<h:commandButton image="/img/arrow_undo.png" title="Responder Manifestação" actionListener="#{analiseManifestacaoResponsavel.responderManifestacao }" rendered="#{!manifestacao.designada }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</th>
							<th>
								<h:commandButton image="/img/addUnd.gif" title="Designar Pessoa para Responder Manifestação" actionListener="#{analiseManifestacaoResponsavel.encaminharManifestacao }" rendered="#{manifestacao.passivelDesignacao }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
								<h:commandButton image="/img/trocar.gif" title="Designar Outra Pessoa para Responder Manifestação" actionListener="#{analiseManifestacaoResponsavel.encaminharManifestacao }" rendered="#{manifestacao.designada }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</th>
							
						</tr>
					</c:forEach>
				</tbody>
		</table>
		<rich:jQuery selector="#listagem" query="tablesorter( {dateFormat: 'uk', headers: {10: { sorter: false },11: { sorter: false },12: { sorter: false },13: { sorter: false } } });" timing="onload" />
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>