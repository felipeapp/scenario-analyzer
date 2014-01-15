<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Quantitativo de Docentes por Departamento</b></caption>
			<tr >
				<th width="15%">Centro:</th>
				<td><b><h:outputText
					value="#{relatorioDocente.departamento.gestora.nome }" /></b></td>
			</tr>
			<tr>
				<th>Departamento:</th>
				<td><b><h:outputText
					value="#{relatorioDocente.departamento.nome }" /></b></td>
			</tr>
	</table>
	<hr>
	<table width="100%">
	<thead>
		<tr>
			<td>Departamento</td><td>Quantidade</td>
		<tr>
	</thead>
	<c:forEach items="${relatorioDocente.listaDocentes}" var="linha">
		<tr>
			<td>
				${linha.depto}
			</td>
			<td>
				${linha.qtd}
			</td>
		</tr>
	</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
