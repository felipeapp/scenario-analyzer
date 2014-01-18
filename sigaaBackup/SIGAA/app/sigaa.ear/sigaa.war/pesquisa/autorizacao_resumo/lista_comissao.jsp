<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2><ufrn:subSistema /> &gt; Autorisar Resumos Pendentes</h2>
	<div class="infoAltRem">
		<h:graphicImage value="/img/monitoria/document_ok.png" style="overflow: visible;" />: Analisar Resumo
	</div>
	<h:form>
		<table class="listagem">
			<caption>Lista de Resumos enviados para autorização</caption>
				<c:set value="0" var="_status" />
				<c:forEach var="ava" items="#{autorizacaoResumo.avaliacoes}" varStatus="count">
					<c:if test="${ _status != ava.resumo.status }">
						<thead>
							<td class="subFormulario" colspan="4" style="padding-top: 5px;">
								${ava.resumo.statusString}
							</td>
							<tr>
								<th>Código</th>
								<th>Título</th>
								<th style="text-align:center">Data de Envio</th>
								<th width="5%"></th>
							</tr>
						</thead>
					</c:if>
					
					<tr class="${count.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<td>${ava.resumo.codigo}</td>
						<td>${ava.resumo.titulo}</td>
						<td style="text-align:center">
							<h:outputText value="#{ava.resumo.dataEnvio}">
								<f:convertDateTime pattern="dd/MM/yyyy" />
							</h:outputText>
						</td>
						<td align="center">
							<h:commandLink title="Analisar Resumo" action="#{autorizacaoResumo.preAutorizarResumoComissao}" id="autorizarResumo"
							 immediate="true" rendered="#{ava.resumo.passivelAvaliacao}">
								<f:param name="idAvaliacao" value="#{ava.id}" />
								<h:graphicImage url="/img/monitoria/document_ok.png" />
							</h:commandLink>
						</td>
					</tr>
					
					<c:set value="${ava.resumo.status}" var="_status"/>
				</c:forEach>
		</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>