<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
tr.curso td {
	padding: 15px 0 0;
	border-bottom: 1px solid #555
}

tr.header td {
	padding: 3px;
	border-bottom: 1px solid #555;
	background-color: #eee;
}

tr.foot td {
	padding: 3px;
	border-bottom: 1px solid #555;
	background-color: #eee;
	font-weight: bold;
	font-size: 13px;
}

tr.discente td {
	border-bottom: 1px solid #888;
	font-weight: bold;
	padding-top: 7px;
}

tr.componentes td {
	padding: 4px 2px 2px;
	border-bottom: 1px dashed #888;
}

tr.componentes td.assinatura {
	padding: 2px;
	border-bottom: 1px solid #888;
	width: 40%;
}
</style>
<f:view>
	<hr>
	<h2 class="tituloTabela"><b>Lista de locais de aplicação de
	prova<br>
	${relatoriosVestibular.obj.nome}</h2>
	<c:set var="total" value="0" />
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<tr class="header">
			<td align="left"><b>Município</b></td>
			<td align="left"><b>Nome</b></td>
			<td align="left"><b>Endereço</b></td>
			<td align="left"><b>Bairro</b></td>
		</tr>
		<c:forEach items="#{lista}" var="linha">
			<tr class="componentes">
				<td align="left">${linha.endereco.municipio.nome }</td>
				<td align="left">${linha.nome }</td>
				<td align="left">${linha.endereco.logradouro},
				${linha.endereco.numero}</td>
				<td align="left">${linha.endereco.bairro}</td>
			</tr>
			<c:set var="total" value="${total+1}" />
		</c:forEach>
		<tr class="foot">
			<td colspan="4">Total: ${total}</td>
		<tr>
	</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
