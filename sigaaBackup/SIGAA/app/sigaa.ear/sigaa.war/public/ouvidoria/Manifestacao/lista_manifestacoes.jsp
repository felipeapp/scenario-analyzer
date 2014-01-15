<%@include file="/public/include/cabecalho.jsp"%>
<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>

<script>
	var marcar = function(idCheck) {
		$(idCheck).checked = true;
	}
</script>
<style>
	table.listagem  tr.agrupador {background: #C8D5EC;font-weight: bold;padding-left: 20px;}
</style>




<f:view>
	<a4j:keepAlive beanName="esclarecimentoOuvidoria" />
	<h:form>

	<h2 class="title">Ouvidoria > Acompanhar Manifestações</h2>
	
	
	<%-- Descrição e orientações para a consulta --%>
	<div class="descricaoOperacao">
	Caro usuário,
	<p>esta página permite visualizar as manifestações pendentes de esclarecimentos.</p>
	</div>
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Detalhes da Manifestação
		<img src="/sigaa/img/email_go.png">: Responder Pedido de Esclarecimento
	</div>
	
	<div>
		<c:if test="${not empty esclarecimentoOuvidoria.manifestacoes }">
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
			<c:forEach items="#{esclarecimentoOuvidoria.manifestacoes }" var="manifestacao" varStatus="status">
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
						<h:commandLink title="Visualizar Detalhes da Manifestação" actionListener="#{esclarecimentoOuvidoria.detalharManifestacao}">
							<f:param name="idManifestacao" value="#{manifestacao.id}" />
							<h:graphicImage value="/img/view.gif"/>
						</h:commandLink>
					</th>
					<th>
						<c:if test="${manifestacao.esperandoEsclarecimento }">
							<h:commandLink title="Responder Pedido de Esclarecimento" actionListener="#{esclarecimentoOuvidoria.iniciarResponderEsclarecimentoPublico }">
								<f:attribute name="idManifestacao" value="#{manifestacao.id}" />
								<h:graphicImage value="/img/email_go.png"/>
							</h:commandLink>
						</c:if>
					</th>
					<th></th>
				</tr>
			</c:forEach>
		</tbody>
		</table>
	</c:if>
	
	<c:if test="${empty esclarecimentoOuvidoria.manifestacoes }">
		<br/>
		<div style="text-align:center;color:red;font-weight:bold;">Nenhuma manfestação encontrada.</div>
	</c:if>
	</div>
	
	</h:form>
	<%@include file="/public/include/voltar.jsp"%>
</f:view>
<%@include file="/public/include/rodape.jsp"%>