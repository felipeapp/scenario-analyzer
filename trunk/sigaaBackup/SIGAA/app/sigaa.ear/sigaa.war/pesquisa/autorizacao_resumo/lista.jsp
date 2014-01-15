<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> &gt; Autorizar Resumos Pendentes</h2>
	<div class="infoAltRem">
		<h:graphicImage value="/img/monitoria/document_ok.png" style="overflow: visible;" />: Analisar Resumo
	</div>
	<h:form>
		<table class="listagem">
			<caption>Lista de Resumos enviados para autorização</caption>
			<thead>
				<tr>
					<th>Código</th>
					<th>Título</th>
					<th style="text-align:center">Data de Envio</th>
					<th>Autor</th>
					<th width="5%"></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="resumo" items="#{autorizacaoResumo.resumosPendentes}" varStatus="count">
					<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<td>${resumo.codigo}</td>
						<td>${resumo.titulo}</td>
						<td style="text-align:center">
							<h:outputText value="#{resumo.dataEnvio}">
								<f:convertDateTime pattern="dd/MM/yyyy" />
							</h:outputText> 
						</td>
						<td>${resumo.autor.nome}</td>
						<td align="center">
							<h:commandLink title="Analisar Resumo" action="#{autorizacaoResumo.preAutorizarResumo}" id="autorizarResumo"
							 immediate="true">
								<f:param name="idResumo" value="#{resumo.id}" />
								<h:graphicImage url="/img/monitoria/document_ok.png" />
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</tbody>		
		</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>