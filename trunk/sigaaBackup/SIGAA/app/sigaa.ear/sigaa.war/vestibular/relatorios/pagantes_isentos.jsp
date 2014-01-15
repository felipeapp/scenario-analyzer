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
	<c:forEach items="${relatoriosVestibular.isencaoVestibular}" var="item" >
		<tr>
			<td><b>${item.key}</b></td>
			<td style="text-align: right;">${item.value}</td>
		</tr>
	</c:forEach>	
</table>
<br />

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>