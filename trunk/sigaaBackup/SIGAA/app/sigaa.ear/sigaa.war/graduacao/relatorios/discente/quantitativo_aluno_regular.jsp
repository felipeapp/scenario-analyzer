<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.curso td {padding: 20px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000;}
</style>
<f:view>

	<h2>Quantitativo de Discentes Regulares por Gênero e Status</h2>

<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>Campus: </th>
			<td> ${relatorioDiscente.campus.nome}</td>
		</tr>
		
		</table>
	</div>

    <c:set var="status" />
    <c:set var="total" value="0"/>
    
	<table cellspacing="1" width="60%" style="font-size: 10px;" align="center">
		<c:forEach items="#{relatorioDiscente.listaDiscente}" var="linha" varStatus="indice">
			
			<c:set var="statusAtual" value="${linha.descricao}"/>
			  <c:if test="${status != statusAtual}">
					<tr class="curso">
						<td colspan="10" width="64%"><b>Status: ${linha.descricao}</b></td>
					</tr>
					<tr class="header">
						<td align="left"width="64%"> Sexo</td>
						<td align="right"> Total</td>
					</tr>
						<c:set var="status" value="${statusAtual}"/>
				</c:if>
					<tr class="componentes">
						<td align="left"><b> ${linha.sexo == 'F' ? "Feminino" : "Masculino"}</b></td>
						<td align="right"> ${linha.count}</td>
						<c:set var="total" value="${linha.count + total}"/>
					</tr>
		</c:forEach>
	</table>
	<br />
		<table cellspacing="1" width="60%" style="font-size: 10px;" align="center">
			<tr class="componentes">
				<td width="64%"><b>Total</b></td>
				<td align="right">${total}</td>
			</tr>
		</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>