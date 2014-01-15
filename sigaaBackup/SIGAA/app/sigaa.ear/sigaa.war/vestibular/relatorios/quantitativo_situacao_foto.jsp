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

<table class="tabelaRelatorioBorda" align="center" width="70%">
		<thead>
			<tr>
				<th>Situação da Foto</th>				
				<th style="text-align: right;">Total</th>
			</tr>
		</thead>
	<c:set value="0" var="total"/>
	<c:forEach items="#{relatoriosVestibular.statusFoto}" var="item" varStatus="indice">
			<tr>
				<td>${item.statusFoto.descricao}</td>
				<td style="text-align: right;">${item.total}</td>
				<c:set var="total" value="${item.total + total}"/>
			</tr>
	</c:forEach>
			<tr>
				<td><b>Total Geral</b></td>
				<td style="text-align: right;"><b>${total}</b></td>
			</tr>
</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>