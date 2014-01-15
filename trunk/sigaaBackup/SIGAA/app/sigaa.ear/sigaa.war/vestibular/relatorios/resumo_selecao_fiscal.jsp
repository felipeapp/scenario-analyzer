<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h2>Resumo do Processamento da Seleção de Fiscais</h2>
<div id="parametrosRelatorio">
<table>
	<tr>
		<th>Processo Seletivo:</th>
		<td>${relatoriosVestibular.obj.nome}</td>
	</tr>
</table>
</div>
<br/>

<c:set var="_grupoAtual"/>
<c:set var="totalTitular" value="0" />
<c:set var="totalReserva" value="0" />
<c:set var="totalInscritos" value="0" />

<c:forEach items="${lista}" var="resumo" >
	<c:set var="_grupoLoop" value="${resumo.grupoSelecao}" />
	<c:if test="${_grupoLoop != _grupoAtual}">
		<c:set var="_grupoAtual" value="${_grupoLoop}"/>
		<c:if test="${fechaTabela}">
			<c:if test="${totalTitular != 0 || totalReserva != 0 || totalInscritos != 0}">
				<tr class="caixaCinzaMedio">
					<td style="text-align: right;font-weight: bold;">Total:</td>
					<td style="text-align: right;font-weight: bold;">${totalInscritos}</td>
					<td style="text-align: right;font-weight: bold;">${totalTitular}</td>
					<td style="text-align: right;font-weight: bold;">${totalReserva}</td>
					<td></td>
					<td></td>
				</tr>
				<c:set var="index" value="0" />
				<c:set var="totalTitular" value="0" />
				<c:set var="totalReserva" value="0" />
				<c:set var="totalInscritos" value="0" />
			</c:if>
			</tbody>
			</table>
			<br/>
			<c:set var="fechaTabela" value="false" />
		</c:if>
		<table class="tabelaRelatorioBorda" width="100%">
			<c:set var="fechaTabela" value="true" />
			<c:set var="_centroGrupo" />
			<caption>${_grupoAtual}</caption>
			<thead>
			<tr>
					<th style="font-weight: bold;">Grupo de Seleção</th>
					<th width="5%" style="text-align: right;font-weight: bold;">Inscritos</th>
					<th width="15%" style="text-align: right;font-weight: bold;">Nº de Titulares</th>
					<th width="15%" style="text-align: right;font-weight: bold;">Nº de Reservas</th>
					<th width="5%" style="text-align: right;font-weight: bold;">${ indiceAcademico.sigla } Máximo</th>
					<th width="5%" style="text-align: right;font-weight: bold;">${ indiceAcademico.sigla } Mínimo</th>
				</tr>
			</thead>
			<tbody>
	</c:if>
	<tr>
		<td>${resumo.subgrupoSelecao}</td>
		<td style="text-align: right;">${resumo.inscritos}</td>
		<td style="text-align: right;">${resumo.titulares}
			(<ufrn:format type="valor" valor="${resumo.titulares / resumo.inscritos * 100}" />%)</td>
		<td style="text-align: right;">${resumo.reservas}
			(<ufrn:format type="valor" valor="${resumo.reservas / resumo.inscritos * 100}" />%)</td>
		<td style="text-align: right;"><ufrn:format type="valor4" valor="${resumo.iraMaximo}" /></td>
		<td style="text-align: right;"><ufrn:format type="valor4" valor="${resumo.iraMinimo}" /></td>
		<c:set var="totalInscritos" value="${totalInscritos + resumo.inscritos}" />
		<c:set var="totalTitular" value="${totalTitular + resumo.titulares}" />
		<c:set var="totalReserva" value="${totalReserva + resumo.reservas}" />
	</tr>
</c:forEach>
<c:if test="${fechaTabela}">
	<c:if test="${totalTitular != 0 || totalReserva != 0 || totalInscritos != 0}">
		<tr class="caixaCinzaMedio">
			<td style="text-align: right;font-weight: bold;">Total:</td>
			<td style="text-align: right;font-weight: bold;">${totalInscritos}</td>
			<td style="text-align: right;font-weight: bold;">${totalTitular}</td>
			<td style="text-align: right;font-weight: bold;">${totalReserva}</td>
			<td></td>
			<td></td>
		</tr>
		<c:set var="index" value="0" />
		<c:set var="totalTitular" value="0" />
		<c:set var="totalReserva" value="0" />
		<c:set var="totalInscritos" value="0" />
	</c:if>
	</tbody>
	</table>
	<br/>
	<ul><b>Legenda:</b>
		<li><b>${ indiceAcademico.sigla }</b> - ${ indiceAcademico.nome }</li>
	</ul>
</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
