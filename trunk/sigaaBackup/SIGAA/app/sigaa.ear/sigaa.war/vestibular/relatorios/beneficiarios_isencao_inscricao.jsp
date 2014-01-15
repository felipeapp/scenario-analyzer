<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h2>${relatoriosVestibular.nomeRelatorio }</h2>
<div id="parametrosRelatorio">
<table>
	<tr>
		<th>Processo Seletivo:</th>
		<td>${relatoriosVestibular.obj.nome}</td>
	</tr>
</table>
</div>
<br/>

<table class="tabelaRelatorioBorda" align="center" width="100%">
		<thead>
			<tr>
				<th>${relatoriosVestibular.obj.nome}</th>
				<th>Estudantes com isenção total</th>
				<th>Funcionários com isenção total</th>
				<th>Total</th>
			</tr>
		</thead>
	<c:set var="cont" value="0"/>
	<c:forEach items="#{relatoriosVestibular.isencaoVestibular}" var="item" varStatus="indice">
		<c:choose>
			<c:when test="${cont == 0}">
				<tr>
					<td>Beneficiário (B)</td>
					<td style="text-align: right;">${item.value}</td>
					<c:set var="cont" value="${cont + 1}" />
			</c:when>	
			<c:when test="${cont == 3}">
				</tr>
				<tr>
					<td>Inscritos no ${relatoriosVestibular.obj.nome} (A)</td>
					<td style="text-align: right;">${item.value}</td>
					<c:set var="cont" value="${cont + 1}" />
			</c:when>	
		<c:otherwise>
				<td style="text-align: right;">${item.value}</td>
				<c:set var="cont" value="${cont + 1}" />
			</c:otherwise>
		</c:choose>
		<c:if test="${item.key == 0}"><c:set var="EstInsc" value="${item.value}"/></c:if>
		<c:if test="${item.key == 1}"><c:set var="FuncInsc" value="${item.value}"/></c:if>
		<c:if test="${item.key == 2}"><c:set var="TotalInsc" value="${item.value}"/></c:if>
		<c:if test="${item.key == 3}"><c:set var="EstBenef" value="${item.value}"/></c:if>
		<c:if test="${item.key == 4}"><c:set var="FuncBenef" value="${item.value}"/></c:if>
		<c:if test="${item.key == 5}"><c:set var="TotalBenef" value="${item.value}"/></c:if>
	</c:forEach>
			</tr>
				<tr>
					<td>Relação A/B (%)</td>
					<td style="text-align: right;"><ufrn:format type="valor" valor="${(EstBenef/EstInsc) * 100}" /> %</td>
					<td style="text-align: right;"><ufrn:format type="valor" valor="${(FuncBenef/FuncInsc) * 100}"/> %</td>
					<td style="text-align: right;"><ufrn:format type="valor" valor="${(TotalBenef/TotalInsc) * 100}"/> %</td>
				</tr>					
</table>
<table width="100%" align="center">
<tr>
	<td colspan="4"><b>Observação:</b> os valores percentuais acima estão apenas truncados, ou seja, não foi aplicada qualquer regra de arredondamento.</td>
</tr>
</table>
		

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>