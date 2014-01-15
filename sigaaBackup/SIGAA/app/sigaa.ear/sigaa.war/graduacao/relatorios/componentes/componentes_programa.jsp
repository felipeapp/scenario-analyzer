<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	<h2> Relatório de Componentes Curriculares com programas cadastrados !</h2>

<div id="parametrosRelatorio">
	<table>
		<tr>
			<th> Unidade: </th>
			<td> ${relatorioPorDepartamento.unidade} </td>
		</tr>
		<tr>
			<th> Ano-Período: </th>
			<td> ${relatorioPorDepartamento.ano}.${relatorioPorDepartamento.periodo} </td>
		</tr>
	</table>
</div>
	<br/>
	<table class="tabelaRelatorio" width="100%">
		<thead>
			<tr>
				<th style="text-align: center;"> Código </th>
				<th> Nome </th>
				<th> Tipo </th>
			</tr>
		</thead>
		
		<tbody>
		<c:forEach items="${componentes}" var="componente" varStatus="loop">
			<tr>
				<td style="text-align: center;"> ${componente.codigo} </td>
				<td> ${componente.nome} </td>
				<td> ${componente.tipoComponente} </td>
			</tr>
		</c:forEach>
		</tbody>
	</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>