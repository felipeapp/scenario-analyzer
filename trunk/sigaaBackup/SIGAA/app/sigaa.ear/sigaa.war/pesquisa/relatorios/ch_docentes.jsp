<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	table.relatorio thead tr th, td {
		text-align: center;
		padding: 2px;
	}

	table.relatorio thead tr th {
		border-bottom: 1px solid #666;
	}

	table.relatorio thead th.label, td.label {
		text-align: left;
	}
	table.relatorio thead th.ch, td.ch {
		text-align: right;
	}

	table.relatorio tbody td {
		border-bottom: 1px dashed #DDD;
	}
	
	table.relatorio tbody td.centro {
		border-bottom: 1px solid #AAA;
		background: #EEE;
		text-align: center;
		font-weight: bold;
		padding: 4px;
		margin-top: 5px;
	}
	table.relatorio tbody td.unidade {
		border-bottom: 1px solid #AAA;
		text-align: left;
		font-weight: bold;
		padding: 8px 2px 2px;
	}

	table.relatorio tfoot tr td {
		border-top: 1px solid #666;
		font-weight: bold;
	}
</style>

<h2> Relatório de Carga Horária de Docentes dedicada a Projetos de Pesquisa </h2>

<table class="relatorio" style="margin: 0 auto;">

	<thead>
		<tr>
			<th rowspan="2"> Docente </th>
			<th colspan="3"> Carga Horária Dedicada a Projetos </th>
		</tr> 
		<tr>
			<th> Internos </th>
			<th> Externos </th>
			<th> Total </th>
		</tr>
	</thead>

	<tbody>
	<c:set var="centroLoop" />
	<c:set var="unidadeLoop" />
	
	<c:forEach var="linha" items="${ relatorio }" varStatus="status">
		
		<c:if test="${ centroLoop != linha.centro}" >
			<c:set var="centroLoop" value="${linha.centro}"/>
			<tr>
				<td class="centro" colspan="4"> ${linha.centro} </td>
			</tr>
		</c:if>
		
		<c:if test="${ unidadeLoop != linha.unidade and linha.unidade != centroLoop}" >
			<c:set var="unidadeLoop" value="${linha.unidade}"/>
			<tr>
				<td class="unidade" colspan="4"> ${linha.unidade} </td>
			</tr>
		</c:if>
	
		<tr>
			<td class="label"> ${linha.nome} </td>
			<td class="ch"> ${linha.chinternos}</td>
			<td class="ch"> ${linha.chexternos}</td>
			<td class="ch"> <b>${linha.chtotal}</b> </td>
		</tr>
	</c:forEach>
	</tbody>
</table>

<br />
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>