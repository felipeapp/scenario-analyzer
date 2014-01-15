<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	#relatorio table tr td.espaco{
		height: 20px;
	}

	#relatorio table tr td.espaco_departamentos{
		height: 10px;
	}

	#relatorio table tr.titulo th {
		background: #DDD;
		text-align: center;
		font-weight: bold;
		padding: 4px;
	}

	#relatorio table tr.centro td{
		font-weight: bold;
		padding: 1px 5px;
		border: 1px solid #444;
		border-width: 1px 0px;
		font-size: 1.2em;
		background: #EEE;
	}

	#relatorio table tr.campos td{
		font-weight: bold;
		padding: 3px;
	}

	#relatorio table tr.departamento td{
		font-weight: bold;
		padding: 1px 5px;
		border-bottom: 1px solid #444;
		font-size: 1em;
	}

	#relatorio table tr td.nome, #relatorio table tr th.nome{
		border-right: 1px solid #888;
	}

	#relatorio table tr.docente td{
		padding: 2px 0 2px 15px;
		border-bottom: 1px dashed #444;
	}

	#relatorio table tr.total td{
		padding: 3px;
		border-top: 1px solid #222;
		font-weight: bold;
	}

	#relatorio table td.total {
		text-align: right;
		background: #EEE;
	}

	#relatorio table tfoot tr.total td {
		text-align: center;
		font-variant: small-caps;
		font-size: 1.1em;
	}

</style>

<h2> Relatório de Participação de Docentes em Projetos de Pesquisa </h2>

<table width="100%">
	<c:set var="centro" />
	<c:set var="departamento" />
	<c:set var="totalDepartamento" value="0"/>
	<c:set var="totalCentro" value="0"/>

	<tbody>
	<c:forEach var="participacao" items="${ participacoes }" varStatus="status">

		<c:if test="${ departamento != participacao.docente.unidade.nome && totalDepartamento != 0 }">
			<tr class="total">
				<td> Total de Docentes no Departamento: ${totalDepartamento } </td>
			</tr>
		</c:if>

		<c:if test="${ centro != participacao.docente.unidade.gestora.nome }">
			<c:set var="centro" value="${ participacao.docente.unidade.gestora.nome }"/>

			<tr><td class="espaco"> </td> </tr>
			<tr class="centro">
				<td colspan="5">
					${ centro }
				</td>
			</tr>
			<c:set var="totalCentro" value="0"/>
		</c:if>

		<c:if test="${ departamento != participacao.docente.unidade.nome }">
			<c:set var="departamento" value="${ participacao.docente.unidade.nome }"/>

			<tr><td class="espaco_departamentos"> </td> </tr>
			<tr class="departamento">
				<td colspan="5">
					${ departamento }
				</td>
			</tr>
			<tr class="titulo">
				<th class="nome"> Docente </th>
				<th> Coordenações </th>
				<th> Internos </th>
				<th> Externos </th>
				<th> Total </th>
			</tr>
			<c:set var="totalDepartamento" value="0"/>
		</c:if>

		<tr class="docente">
			<td class="nome"> ${ participacao.docente.pessoa.nome } </td>
			<td class="total"> ${ participacao.totalCoordenador } </td>
			<td class="total"> ${ participacao.totalInternos } </td>
			<td class="total"> ${ participacao.totalExternos } </td>
			<td class="total"> ${ participacao.totalInternos + participacao.totalExternos } </td>
		</tr>

		<c:set var="totalDepartamento" value="${totalDepartamento + 1}"/>
		<c:set var="totalCentro" value="${totalCentro + 1}"/>
	</c:forEach>

		<tr class="total">
			<td> Total de Docentes no Departamento: ${totalDepartamento } </td>
		</tr>

	</tbody>

	<tfoot>
		<tr><td class="espaco" colspan="5"> </td> </tr>
		<tr class="total">
			<td colspan="5"> Total de Docentes encontrados: ${ fn:length(participacoes) }</td>
		</tr>
	</tfoot>
</table>

<br />
<div class="naoImprimir" style="text-align: center;">
	<a href="javascript: history.go(-1);"> Voltar </a>
</div>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>