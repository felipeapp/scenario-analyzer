<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp" %>

<style>
tr.relatorioHeader td {padding: 3px; }
tr.relatorioBody td {padding: 2px ; border-bottom: 1px solid #888}
tr.relatorioBodyProdutividade td {padding: 2px ; border-bottom: 1px solid #888; background-color: #BEBEBE;}
</style>

<f:view>
	<h:outputText value="#{ relatorioResumoCotaMBean.create }" />
	<c:set var="relatorio_" value="${ relatorioResumoCotaMBean.lista }"/>

	<center>
		<h2>Relatório Quantitativo de Cotas de Bolsas</h2>
		<table cellspacing="1" width="80%" style="font-size: 10px;" border="1">
			<thead>
			<tr class="relatorioHeader">
				<th style="text-align: left;"> Docente </th>
				<th style="text-align: right;"> Solicitações </th>
				<c:if test="${relatorioResumoCotaMBean.param.propesqIC}">
					<th style="text-align: right;"> PROPESQ(IC) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.propesqIT}">
					<th style="text-align: right;"> PROPESQ(IT) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.propesqInducaoIC}">
					<th style="text-align: right;"> PROPESQ-INDUÇÃO(IC) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.pibic}">
					<th style="text-align: right;"> PIBIC(IC) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.pibicAf}">
					<th style="text-align: right;"> PIBIC AF(IC)</th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.balcao}">
					<th style="text-align: right;"> Balcão(IC)</th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.pibit}">
					<th style="text-align: right;"> PIBIT(IT)</th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.prhAnp}">
					<th style="text-align: right;"> PRH-ANP(IC)</th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.dinterIC}">
					<th style="text-align: right;"> DINTER(IC) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.dinterIT}">
					<th style="text-align: right;"> DINTER(IT) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.reuniIC}">
					<th style="text-align: right;"> REUNI(IC) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.reuniIT}">
					<th style="text-align: right;"> REUNI(IT) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.reuniDinterIT}">
					<th style="text-align: right;"> REUNI-DINTER(IT) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.reuniNuplamIT}">
					<th style="text-align: right;"> REUNI-NUPLAM(IT) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.pnaesIC}">
					<th style="text-align: right;"> PNAES(IC) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.pnaesIT}">
					<th style="text-align: right;"> PNAES(IT) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.picme}">
					<th style="text-align: right;"> PICME(IC/IT) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.pet}">
					<th style="text-align: right;"> Pesquisa/PET(IC)</th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.voluntarioIC}">
					<th style="text-align: right;"> Voluntário(IC) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.voluntarioIT}">
					<th style="text-align: right;"> Voluntário(IT) </th>
				</c:if>
				<c:if test="${relatorioResumoCotaMBean.param.outros}">
					<th style="text-align: right;"> Outros(IC)</th>
				</c:if>
			</tr>	
			</thead>
			
			<c:forEach items="${ relatorio_ }" var="linha">
				<tr class="${ linha.value.docenteProdutividade  ? "relatorioBodyProdutividade" : "relatorioBody"}">
						
					<td> ${ linha.value.docente } </td>
					<td align="right"> ${ linha.value.solicitacoes } </td>
					<c:if test="${relatorioResumoCotaMBean.param.propesqIC}">
						<td style="text-align: right;"> ${ linha.value.contPropesqIC } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.propesqIT}">
						<td style="text-align: right;"> ${ linha.value.contPropesqIT } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.propesqInducaoIC}">
						<td style="text-align: right;"> ${ linha.value.contPropesqInducaoIC } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.pibic}">
						<td style="text-align: right;"> ${ linha.value.contPibic } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.pibicAf}">
						<td style="text-align: right;"> ${ linha.value.contPibicAf } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.balcao}">
						<td style="text-align: right;"> ${ linha.value.contBalcao } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.pibit}">
						<td style="text-align: right;"> ${ linha.value.contPibit } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.prhAnp}">
						<td style="text-align: right;"> ${ linha.value.contPrhAnp } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.dinterIC}">
						<td style="text-align: right;"> ${ linha.value.contDinterIC } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.dinterIT}">
						<td style="text-align: right;"> ${ linha.value.contDinterIT } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.reuniIC}">
						<td style="text-align: right;"> ${ linha.value.contReuniIC } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.reuniIT}">
						<td style="text-align: right;"> ${ linha.value.contReuniIT } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.reuniDinterIT}">
						<td style="text-align: right;"> ${ linha.value.contReuniDinterIT } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.reuniNuplamIT}">
						<td style="text-align: right;"> ${ linha.value.contReuniNuplamIT } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.pnaesIC}">
						<td style="text-align: right;"> ${ linha.value.contPnaesIC } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.pnaesIT}">
						<td style="text-align: right;"> ${ linha.value.contPnaesIT } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.picme}">
						<td style="text-align: right;"> ${ linha.value.contPicme } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.pet}">
						<td style="text-align: right;"> ${ linha.value.contPet } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.voluntarioIC}">
						<td style="text-align: right;"> ${ linha.value.contVoluntarioIC } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.voluntarioIT}">
						<td style="text-align: right;"> ${ linha.value.contVoluntarioIT } </td>
					</c:if>
					<c:if test="${relatorioResumoCotaMBean.param.outros}">
						<td style="text-align: right;"> ${ linha.value.contOutros } </td>
					</c:if>
				</tr>
			</c:forEach>
		</table>
	</center>
	
	<br />
	
	<center>
		<table cellspacing="1" width="80%" style="font-size: 10px;">
			<tr>
				<td width="5%;" style="background-color: #BEBEBE;"></td>
				<td width="95%;"> &nbsp; Bolsistas de Produtividade do CNPq.</td>
			</tr>
		</table>

		<br />
		<b>Total de Registros: <h:outputText value="#{relatorioResumoCotaMBean.registrosEncontrados}"/></b>

	</center>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp" %>