<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
</style>

<f:view>
	<h2>Quantitativo de Discentes Regulares por Gênero e Curso</h2>

<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Campus: </th>
			<td> ${relatorioDiscente.campus.nome}</td>
		</tr>
	</table>
</div>

<br />
    
    <c:set var="totalHomens" value="0" />
    <c:set var="totalMulheres" value="0"/>
    
	<table cellspacing="1" width="100%" style="font-size: 10px;" class="tabelaRelatorioBorda" >
		<tr class="header">
			<td align="left"> Curso</td>
			<td align="right"> Quantidade de Homens </td>
			<td align="right"> Quantidade de Mulheres </td>
		</tr>
	
		<c:forEach items="#{relatorioDiscente.listaDiscente}" var="linha" varStatus="indice">
			<tr>
				<td align="left"> ${linha.nome} - ${linha.cidade }</td>
				<td align="right"> ${linha.homens}</td>
				<c:set var="totalHomens" value="${totalHomens + linha.homens}"/>
				<td align="right"> ${linha.mulheres}</td>
				<c:set var="totalMulheres" value="${totalMulheres + linha.mulheres}"/>
			</tr>
		</c:forEach>
			<tr class="header">
				<td style="text-align: right;">Total</td>
				<td style="text-align: right;">${totalHomens}</td>
				<td style="text-align: right;">${totalMulheres}</td>
			</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>