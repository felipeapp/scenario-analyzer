<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	table tr td.espaco{
		height: 20px;
	}

	table tr td.espaco_docentes{
		height: 8px;
	}

	#relatorio table tr.centro td{
		font-weight: bold;
		padding: 3px 8px;
		border: 1px solid #444;
		border-width: 1px 0px;
		font-size: 1.2em;
		background: #EEE;
	}

	table tr.departamento td{
		font-style: italic;
		padding: 2px 5px;
		border-bottom: 1px solid #888;
		font-size: 1em;
		text-align: center;
		background: #F5F5F5;
	}

	table tr.campos td{
		font-weight: bold;
		padding: 3px;
	}

	table tr.docente td{
		font-weight: bold;
		padding: 1px 5px;
		border-bottom: 1px solid #444;
		font-size: 1.2em;
	}

	table tr.projeto td{
		padding: 2px 0 2px 15px;
		border-bottom: 1px dashed #444;
	}

	table tr.total td{
		padding: 3px;
		border-top: 1px solid #222;
		font-weight: bold;
	}

	table tr.totalDepartamento td{
		padding: 2px;
		border-top: 1px solid #888;
		font-style: italic;
		background: #F5F5F5;
	}

	span.departamento {
		font-size: 0.9em;
		font-weight: normal;
	}

</style>

<h2> Relatório de Cotas Solicitadas para ${cota.descricao} </h2>

<div id="parametrosRelatorio">
	<table>
		<tr>
			<th>Cota:</th>
			<td>${cota}</td>
		</tr>
		<tr>
			<th>Centro:</th>
			<td>${centro_}</td>
		</tr>
	</table>
</div>
<br/>
<table width="100%">
	<c:set var="centro" />
	<c:set var="departamento" />
	<c:set var="siape" />

	<thead>
		<tr>
			<th colspan="2" style="text-align: left"> Docente ${ agruparPorDepartamento ? '' : '(Departamento)' }</th>
		</tr>
		<tr>
			<th style="text-align: left"> &nbsp;&nbsp;&nbsp; Projeto(s)</th>
			<th> Cotas </th>
		</tr>
	</thead>

	<tbody>
		<c:set var="totalCotasCentro" value="0"/>
		<c:set var="totalCotasDepartamento" value="0"/>
		<c:forEach var="linha" items="${ relatorio_ }" varStatus="status">
	
			<%-- Departamento --%>
			<c:if test="${agruparPorDepartamento && linha.departamento != departamento && totalCotasDepartamento != 0}">
				<tr>
					<td class="espaco_docentes"></td>
				</tr>
				<tr class="totalDepartamento">
						<td align="center"> Total do Departamento </td>
						<td align="right"> ${totalCotasDepartamento} </td>
				</tr>
			</c:if>
	
			<%-- Centro --%>
			<c:if test="${ linha.sigla != centro }">
				<c:if test="${totalCotasCentro != 0 }">
					<tr>
						<td class="espaco_docentes"></td>
					</tr>
					<tr class="total">
							<td align="center"> Total de Solicitações ${centro} </td>
							<td align="right"> ${totalCotasCentro} </td>
					</tr>
				</c:if>
	
				<c:set var="centro" value="${ linha.sigla }" />
				<c:set var="totalCotasCentro" value="0"/>
	
				<c:set var="totalCentros" value="1"/>
	
				<c:if test="${status.index != 0 }">
					<tr>
						<td class="espaco"></td>
					</tr>
				</c:if>
	
				<tr class="centro">
					<td colspan="2"> ${centro} </td>
				</tr>
			</c:if>
	
			<%-- Departamento --%>
			<c:if test="${agruparPorDepartamento && linha.departamento != departamento}">
				<c:set var="totalCotasDepartamento" value="0"/>
				<c:set var="departamento" value="${ linha.departamento }" />
	
				<tr>
					<td class="espaco"></td>
				</tr>
				<tr class="departamento">
					<td colspan="2"> ${departamento} </td>
				</tr>
			</c:if>
	
			<%-- Docente --%>
			<c:if test="${ linha.siape != siape }">
				<c:set var="siape" value="${ linha.siape }" />
	
				<tr>
					<td class="espaco_docentes"></td>
				</tr>
				<tr class="docente">
					<td colspan="2">
						${ linha.nome }
						<c:if test="${!agruparPorDepartamento}">
							<span class="departamento">(${linha.departamento })</span>
						</c:if>
					</td>
				</tr>
			</c:if>
	
			<%-- Projeto --%>
			<tr class="projeto">
				<td> ${linha.prefixo}${linha.numero}-${linha.ano} : ${ linha.titulo } </td>
				<td align="right" width="5%"> ${ linha.cotas } </td>
			</tr>
	
			<c:set var="total" value="${total + linha.cotas}"/>
			<c:set var="totalCotasCentro" value="${totalCotasCentro + linha.cotas}"/>
			<c:set var="totalCotasDepartamento" value="${totalCotasDepartamento + linha.cotas}"/>
		</c:forEach>
	
		<c:if test="${totalCotasDepartamento != 0 }">
			<tr>
				<td class="espaco_docentes"></td>
			</tr>
			<tr class="totalDepartamento">
				<td align="center"> Total do Departamento </td>
				<td align="right"> ${totalCotasDepartamento} </td>
			</tr>
		</c:if>
	
		<c:if test="${totalCotasCentro != 0 }">
			<tr>
				<td class="espaco_docentes"></td>
			</tr>
			<tr class="total">
				<td align="center"> Total de Solicitações ${centro} </td>
				<td align="right"> ${totalCotasCentro} </td>
			</tr>
		</c:if>
	
		<c:if test="${ not filtroCentro }">
			<tr>
				<td class="espaco"></td>
			</tr>
			<tr class="total">
				<td align="center"> TOTAL GERAL DE COTAS SOLICITADAS </td>
				<td align="right"> ${total} </td>
			</tr>
		</c:if>
	<tbody>
</table>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>