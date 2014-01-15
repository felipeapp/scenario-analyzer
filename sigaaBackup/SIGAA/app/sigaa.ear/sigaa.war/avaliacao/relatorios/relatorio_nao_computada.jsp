<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib tagdir="/WEB-INF/tags" prefix="aval" %>
<f:view>
	<h2>Resultado Quantitativo de Avaliações Institucionais Não Computadas</h2>
	
<div id="parametrosRelatorio">
<table>
	<tr>
		<th>Ano-Período:</th>
		<td><h:outputText value="#{relatorioAvaliacaoMBean.ano}"/>.<h:outputText value="#{relatorioAvaliacaoMBean.periodo}"/></td>
	</tr>
	<c:if test="${relatorioAvaliacaoMBean.idUnidade > 0}">
		<tr>
			<th>Unidade:</th>
			<td><h:outputText value="#{relatorioAvaliacaoMBean.unidade.codigoNome}"/></td>
		</tr>
	</c:if>
</table>
</div>
<br/>

<c:set var="totalDepartamento" value="0" />
<c:set var="totalCentro" value="0" />

<c:forEach items="#{relatorioAvaliacaoMBean.dadosRelatorioNaoComputado}" var="centro">
	<table class="tabelaRelatorioBorda" width="100%">
		<caption>${centro.key}</caption>
		<tbody>
			<c:set var="totalCentro" value="0" />
			<c:forEach items="#{centro.value}" var="departamento">
				<tr class="caixaCinza" style="font-size: 11px">
					<th colspan="2">${departamento.key }</th>
					<c:set var="totalDepartamento" value="0" />
				</tr>
				<tr class="caixaCinza">
					<th width="95%">Descrição</th>
					<th width="5%" style="text-align: right;">Quantidade</th>
				</tr>
				<c:forEach items="#{departamento.value}" var="item">
					<tr>
						<th width="95%">${item.key}</th>
						<td width="5%" style="text-align: right;">${item.value}</td>
						<c:set var="totalDepartamento" value="${totalDepartamento + item.value}" />
						<c:set var="totalCentro" value="${totalCentro + item.value}" />
					</tr>
				</c:forEach>
				<tr>
					<th width="95%" style="text-align: right;">Total do Departamento:</th>
					<th width="5%" style="text-align: right;">${totalDepartamento }</th>
				</tr>
				<tr>
					<td colspan="2">&nbsp;</td>
				</tr>
			</c:forEach>
			<tr>
				<th width="95%" style="text-align: right;">Total do Centro:</th>
				<th width="5%" style="text-align: right;">${totalCentro }</th>
			</tr>
		</tbody>
	</table>
	<br/>
</c:forEach>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>