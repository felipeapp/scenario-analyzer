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
		<ufrn:subSistema /> &gt; Manifesta��es Pendentes
	</h2>
	
		<div class="descricaoOperacao" style="text-align: justify">
			<b>Caro usu�rio(a),</b> 
			<br/><br/>
			Nesta tela ser�o listadas as manifesta��es pendentes de respostas. Aqui ser� poss�vel visualizar os detalhes da manifesta��o,
			design�-las para outro usu�rio, ou respond�-las para ouvidoria ou para o interessado.
			<br/><br/>
			Caso a manifesta��o seja designada para outro usu�rio, ele ter� que respond�-la ao senhor(a), de modo que, o senhor(a) ainda ter� de enviar uma resposta para a
			ouvidoria ou para o interessado.
			<br/><br/>
			Caso o tempo de resposta para a manifesta��o esteja pr�ximo de acabar ou j� tenha acabado, ser� enviado um e-mail de notifica��o. Para responder a
			manifesta��o � necess�rio clicar no bot�o abaixo "Responder Manifesta��o" e encaminhar a resposta para a ouvidoria ou para o interessado. Uma vez que a manifesta��o
			seja respondida os e-mails cessar�o.
			<br/><br/>
			Caso sua prefer�ncia seja responder a manifesta��o fisicamente sem a utiliza��o do SIGAA, os e-mails cessar�o assim que a ouvidoria der baixa nas manifesta��es. 
			<br/><br/>
		</div>
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Detalhes da Manifesta��o
		<h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;" />: Responder Manifesta��o <br />
		<h:graphicImage value="/img/addUnd.gif" style="overflow: visible;" />: Designar Pessoa para Responder Manifesta��o
		<h:graphicImage value="/img/trocar.gif" style="overflow: visible;" />: Designar Outra Pessoa para Responder Manifesta��o
	</div>

	<br />

	<h:form id="form">
		<table class="listagem tablesorter" id="listagem" width="100%" cellpadding="3px">
			<caption>Manifesta��es Pendentes(${ fn:length(analiseManifestacaoResponsavel.manifestacoes) })</caption>
				<thead>
					<tr>
						<th style="text-align: center;padding-right: 15px;">Data de Cadastro</th>
						<th style="padding-right: 15px;text-align: right;">Ano/ N�mero</th>
						<th>Categoria do Solicitante</th>
						<th style="padding-right: 15px;">Nome</th>
						<th style="padding-right: 15px;">Origem</th>
						<th>Categoria do Assunto</th>
						<th style="padding-right: 15px;">Assunto</th>
						<th style="padding-right: 15px;">T�tulo</th>
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
									<i>Manifesta��o An�nima</i>
								</c:if>
								<c:if test="${!manifestacao.anonima }">
									${manifestacao.interessadoManifestacao.categoriaSolicitante.descricao }
								</c:if>
							</th>
							<th style="text-align: left;">
								<c:if test="${manifestacao.anonima }">
									<i>Manifesta��o An�nima</i>
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
								<h:commandButton image="/img/view.gif" title="Visualizar Detalhes da Manifesta��o" actionListener="#{analiseManifestacaoResponsavel.detalharManifestacaoPendente }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</th>
							<th>
								<h:commandButton image="/img/arrow_undo.png" title="Responder Manifesta��o" actionListener="#{analiseManifestacaoResponsavel.responderManifestacao }" rendered="#{!manifestacao.designada }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
							</th>
							<th>
								<h:commandButton image="/img/addUnd.gif" title="Designar Pessoa para Responder Manifesta��o" actionListener="#{analiseManifestacaoResponsavel.encaminharManifestacao }" rendered="#{manifestacao.passivelDesignacao }">
									<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								</h:commandButton>
								<h:commandButton image="/img/trocar.gif" title="Designar Outra Pessoa para Responder Manifesta��o" actionListener="#{analiseManifestacaoResponsavel.encaminharManifestacao }" rendered="#{manifestacao.designada }">
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