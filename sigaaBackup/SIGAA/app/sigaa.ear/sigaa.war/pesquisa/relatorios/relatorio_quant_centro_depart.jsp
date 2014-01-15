<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>
<style>
	tr.centro td {padding: 20px 0 0; font-weight: bold; page-break-after: always;}
	tr.impar td { font-weight: bolder; color: blue;}
	.folha { page-break-after: always }; 
</style>

<f:view>
	
	<table width="100%">
		<tr>
			<td colspan="11" style="font-size: 12px;" >
				<h2><b>RELATÓRIO QUANTITATIVO DE BOLSAS POR CENTRO/DEPARTAMENTO </b></h2>			
			</td>
		</tr>
	</table>

	<c:set var="_centro" />
	<c:set var="_departamento" />
	<c:forEach items="${ relatoriosPesquisaMBean.relatorioQuantCentroDeparta }" var="centro" varStatus="status">
		<table width="100%">
			
			<c:if test="${ not empty _centro }">
				<div class="folha"> 
					Quebra Página
				</div>
			</c:if>
					  
			<tr>		
				<c:set var="centroAtual" value="${centro.key}"/>
					<c:if test="${_centro != centroAtual}">
						<tr class="centro">
							<td colspan="14" style="text-align: center;">
								<hr >${centro.key} <hr> 
							</td>
						</tr>
					</c:if>
				<c:set var="_centro" value="${centroAtual}"/>
				
				<c:forEach items="${ centro.value }" var="linha" varStatus="status">
					<c:set var="departamentoAtual" value="${linha.departamento}"/>	
					<c:if test="${_departamento != departamentoAtual}">
						<tr class="centro" style="padding-top: -10%;">
							<td colspan="14"><hr><b>${linha.departamento}</b><hr></td>
						</tr>
						
						<tr class="impar">
							<th> Voluntário: </th>
							<td style="text-align: center;"> ${ linha.voluntario }</td>
							<th> PIBIC: &nbsp; </th>
							<td> ${ linha.pibic }</td>
							<th> PIBIT: &nbsp; </th>
							<td> ${ linha.pibit } </td>
							<th> PIBIC AA: &nbsp; </th>
							<td> ${ linha.pibicAA  } </td>
							<th> PIBIC EM: &nbsp; </th>
							<td> ${ linha.pibicEM } </td>
							<th> BALCÃO: &nbsp; </th>
							<td> ${ linha.balcao } </td>
							<th> PROPESQ IC: &nbsp; </th>
							<td> ${ linha.propesqIC } </td>
						</tr>
						
						<tr class="impar">
							<th> PROPESQ IT: </th>
							<td style="text-align: center;"> ${ linha.propesqIT } </td>
							<th> PROPESQ INDUÇÃO: &nbsp; </th>
							<td> ${ linha.propesqInd } </td>
							<th> REUNI IC: &nbsp; </th>
							<td> ${ linha.reuniIC } </td>
							<th> REUNI IT: &nbsp; </th>
							<td> ${ linha.reuniIT } </td>
							<th> REUNI NUPLAM: &nbsp; </th>
							<td> ${ linha.reuniNuplam } </td>
							<th> REUNI DINTER: &nbsp; </th>
							<td> ${ linha.reuniDinter } </td>
							<th> DINTER IC: &nbsp; </th>
							<td> ${ linha.dinterIC } </td>
						</tr>
						
						<tr class="impar">
							<th> DINTER IT: </th>
							<td style="text-align: center;"> ${ linha.dinterIT } </td>
							<th> FAPERN IC: &nbsp; </th>
							<td> ${ linha.fapernIC } </td>
							<th> PRH ANP: &nbsp; </th>
							<td> ${ linha.prhAnp } </td>
							<th> PICME: &nbsp; </th>
							<td> ${ linha.picme } </td>
							<th> PNAES IC: &nbsp; </th>
							<td> ${ linha.pnaesIC } </td>
							<th> PNAES IT: &nbsp; </th>
							<td> ${ linha.pnaesIT } </td>
							<th> PESQUISA PET: &nbsp; </th>
							<td> ${ linha.pesquisaPet } </td>
						</tr>
						
					</c:if>
					<c:set var="_departamento" value="${departamentoAtual}"/>
					
				</c:forEach>
				
			</tr>
		</table>
	</c:forEach>

	<br /><br />
	
	<table width="100%">
		<tr>
			<td style="font-weight: bold" align="center">Total de bolsistas: ${ relatoriosPesquisaMBean.total } </td>
		</tr>
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>