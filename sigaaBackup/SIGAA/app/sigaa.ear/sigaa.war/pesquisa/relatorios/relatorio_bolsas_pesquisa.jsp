<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp" %>

<style>
tr.relatorioHeader td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
tr.relatorioBody td {padding: 2px ; border-bottom: 1px solid #888}
</style>

<f:view>
	<h:outputText value="#{ relatorioBolsasPesquisaMBean.create }" />
	<c:set var="relatorio_" value="${ relatorioBolsasPesquisaMBean.lista }"/>

	<center>
		<h2>Relatório Quantitativo de Bolsas de Pesquisa Ativas</h2>
		<table cellspacing="1" width="80%" style="font-size: 10px;" border="1">
			<thead>
			<tr class="relatorioHeader">
				<th style="text-align: left;"> Docente </th>
				<th style="text-align: left;"> Departamento </th>
				<th style="text-align: left;"> Centro </th>
				<c:if test="${relatorioBolsasPesquisaMBean.param.propesqIC}">
					<th style="text-align: right;"> PROPESQ(IC) </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.propesqIT}">
					<th style="text-align: right;"> PROPESQ(IT) </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.propesqInducaoIC}">
					<th style="text-align: right;"> PROPESQ-INDUÇÃO(IC) </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.pibic}">
					<th style="text-align: right;"> PIBIC </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.balcao}">
					<th style="text-align: right;"> Balcão </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.pibit}">
					<th style="text-align: right;"> PIBIT </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.prhAnp}">
					<th style="text-align: right;"> PRH-ANP </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.dinterIC}">
					<th style="text-align: right;"> DINTER(IC) </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.dinterIT}">
					<th style="text-align: right;"> DINTER(IT) </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.reuniIC}">
					<th style="text-align: right;"> REUNI(IC) </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.reuniIT}">
					<th style="text-align: right;"> REUNI(IT) </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.reuniDinterIT}">
					<th style="text-align: right;"> REUNI-DINTER(IT) </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.pnaesIC}">
					<th style="text-align: right;"> PNAES(IC) </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.pnaesIT}">
					<th style="text-align: right;"> PNAES(IT) </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.picme}">
					<th style="text-align: right;"> PICME(IC/IT) </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.pet}">
					<th style="text-align: right;"> Pesquisa/PET </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.voluntarioIC}">
					<th style="text-align: right;"> Voluntário(IC) </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.voluntarioIT}">
					<th style="text-align: right;"> Voluntário(IT) </th>
				</c:if>
				<c:if test="${relatorioBolsasPesquisaMBean.param.outros}">
					<th style="text-align: right;"> Outros </th>
				</c:if>
			</tr>	
			</thead>
			
			<c:forEach items="${ relatorio_ }" var="linha">
				<tr class="relatorioBody">		
					<td> ${ linha.value.docente } </td>
					<td> ${ linha.value.departamento } </td>
					<td> ${ linha.value.centro } </td>
					<c:if test="${relatorioBolsasPesquisaMBean.param.propesqIC}">
						<td style="text-align: right;"> ${ linha.value.contPropesqIC } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.propesqIT}">
						<td style="text-align: right;"> ${ linha.value.contPropesqIT } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.propesqInducaoIC}">
						<td style="text-align: right;"> ${ linha.value.contPropesqInducaoIC } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.pibic}">
						<td style="text-align: right;"> ${ linha.value.contPibic } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.balcao}">
						<td style="text-align: right;"> ${ linha.value.contBalcao } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.pibit}">
						<td style="text-align: right;"> ${ linha.value.contPibit } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.prhAnp}">
						<td style="text-align: right;"> ${ linha.value.contPrhAnp } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.dinterIC}">
						<td style="text-align: right;"> ${ linha.value.contDinterIC } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.dinterIT}">
						<td style="text-align: right;"> ${ linha.value.contDinterIT } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.reuniIC}">
						<td style="text-align: right;"> ${ linha.value.contReuniIC } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.reuniIT}">
						<td style="text-align: right;"> ${ linha.value.contReuniIT } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.reuniDinterIT}">
						<td style="text-align: right;"> ${ linha.value.contReuniDinterIT } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.pnaesIC}">
						<td style="text-align: right;"> ${ linha.value.contPnaesIC } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.pnaesIT}">
						<td style="text-align: right;"> ${ linha.value.contPnaesIT } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.picme}">
						<td style="text-align: right;"> ${ linha.value.contPicme } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.pet}">
						<td style="text-align: right;"> ${ linha.value.contPet } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.voluntarioIC}">
						<td style="text-align: right;"> ${ linha.value.contVoluntarioIC } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.voluntarioIT}">
						<td style="text-align: right;"> ${ linha.value.contVoluntarioIT } </td>
					</c:if>
					<c:if test="${relatorioBolsasPesquisaMBean.param.outros}">
						<td style="text-align: right;"> ${ linha.value.contOutros } </td>
					</c:if>
				</tr>
			</c:forEach>
		</table>
		<b>Total de Registros: <h:outputText value="#{relatorioBolsasPesquisaMBean.registrosEncontrados}"/></b>
	</center>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp" %>
