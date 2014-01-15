<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="solicitacaoBolsasReuniBean" />

	<h2> <ufrn:subSistema /> &gt; Solicitações de Bolsas REUNI de Assistência ao Ensino </h2>

	<div class="infoAltRem" style="width:90%">
        <h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar solicitação
        <c:if test="${solicitacaoBolsasReuniBean.portalPpg}">
        	<h:graphicImage value="/img/check.png"/>/<h:graphicImage value="/img/check_cinza.png" style="margin-left:0"/>: Liberar/Submeter Solicitação
        </c:if>
	</div>

	<h:form id="form">
	<table class="listagem" style="width:90%">
		<caption> Solicitações cadastradas </caption>
		<thead>
		<tr>
			<th> Programa </th>
			<th style="text-align: right;"> N&ordm; de Bolsas Solicitadas </th>
			<th> Status </th>
			<th> </th>
			<c:if test="${solicitacaoBolsasReuniBean.portalPpg}">
				<th></th>
			</c:if>
		</tr>
		</thead>
		<tbody>
		<c:forEach items="#{solicitacaoBolsasReuniBean.all}" var="_solicitacao">
			<tr>
				<td> ${_solicitacao.programa.codigoNome} </td>
				<td style="text-align: right;"> ${ fn:length(_solicitacao.planos) } </td>
				<td> ${ _solicitacao.descricaoStatus } </td>
				<td> 
					<h:commandButton image="/img/view.gif" title="Visualizar solicitação"  id="ver"
						action="#{solicitacaoBolsasReuniBean.view}" styleClass="noborder">
						<f:setPropertyActionListener value="#{_solicitacao}" target="#{solicitacaoBolsasReuniBean.obj}"/>
					</h:commandButton>
				</td>
				<c:if test="${solicitacaoBolsasReuniBean.portalPpg}">
					<td style="text-align: right;"> 
						<h:commandButton image="/img/check#{not _solicitacao.submetida ? '_cinza' : ''}.png" title="#{_solicitacao.submetida ? 'Liberar' : 'Submeter'} Solicitação"
							action="#{solicitacaoBolsasReuniBean.liberarSubmeter}" id="liberar" styleClass="noborder">
							<f:setPropertyActionListener value="#{_solicitacao}" target="#{solicitacaoBolsasReuniBean.obj}"/>
						</h:commandButton>
					</td>			
				</c:if>					
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>	