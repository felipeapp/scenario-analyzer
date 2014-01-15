<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="aval" %>
<f:view>
	<h2>Estatísticas do Preenchimento da Avaliação Atual</h2>
	<br/>
	<table class="tabelaRelatorioBorda" width="100%">
		<c:if test="${not empty relatorioAvaliacaoMBean.estatisticas}">
			<thead>
				<tr>
					<th>Descrição</th>
					<th>Qtd</th>
				</tr>
			</thead>
			<tbody>
				<c:set var="formulario" value="" />
				<c:forEach items="#{relatorioAvaliacaoMBean.estatisticas}" var="linha" varStatus="status">
					<c:set var="loop" value="${ linha.formulario.titulo } (${ linha.ano }.${ linha.periodo })"/>
					<c:if test="${ formulario != loop }">
						<tr class="caixaCinza">
							<td colspan="3" style="text-align: left;font-weight: bold;">
								${ loop }
							</td>
						</tr>
						<c:set var="formulario" value="${ loop }"/>
					</c:if>
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td style="text-align: left;">${linha.descricao }</td>
						<td style="text-align: right;">
							<h:outputText value="#{linha.valor}"/>
							<h:outputText value=" (#{linha.percentual}%)" rendered="#{not empty linha.percentual}"/>
						</td>
					</tr>
				</c:forEach>
			</c:if>
			<c:if test="${empty relatorioAvaliacaoMBean.estatisticas}">
				<tr>
					<td colspan="3" style="text-align: center;">Não há dados estatísticos sobre Avaliação Atual</td>
				</tr>
			</c:if>
		</tbody>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>