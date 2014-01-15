<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.header td {background-color: #eee; font-weight: bold; border: none;}
</style>

<f:view>
	<h2>${relatoriosVestibular.nomeRelatorio }</h2>
<div id="parametrosRelatorio">
<table>
	<tr>
		<th>Processo Seletivo:</th>
		<td>${relatoriosVestibular.obj.nome}</td>
	</tr>
	<tr>
		<th>Demanda Final:</th>
		<td><ufrn:format type="simnao" valor="${relatoriosVestibular.demandaFinal}" /></td>
	</tr>
</table>
</div>
<br/>

<table class="tabelaRelatorioBorda" align="center" width="50%">
	<c:forEach items="${relatoriosVestibular.isencaoVestibular}" var="item" >
		<tr>
			<td><b>${item.key}</b></td>
			<td style="text-align: right;">${item.value }</td>
		</tr>
	</c:forEach>	
</table>
<br />

<table class="tabelaRelatorioBorda" align="center" width="100%">
	<c:set var="_area" />
	<c:set var="_municipio" />
	<c:forEach items="#{relatoriosVestibular.demanda}" var="item" varStatus="index">
		<c:set var="municipioAtual" value="${item.centro}"/>
		<c:if test="${_municipio != municipioAtual}">
			<thead>
				<tr>
					<td style="text-align: center; padding-top: 20px; border: none;" colspan="5">${item.centro}</td>
					<c:set var="_area" />
				</tr>
				<tr>
					<th>Curso</th>
					<th style="text-align: right;">Vagas</th>
					<th style="text-align: right;">Candidatos</th>
					<th style="text-align: right;">Demanda</th>
				</tr>
			</thead>
		</c:if>

		<c:set var="areaAtual" value="${item.area}"/>
			<c:if test="${_area != areaAtual}">
					<tr class="header">
						<td colspan="10">${item.area}</td>
					</tr>
			</c:if>
		<tr>
			<td>
				${item.nome} 
				<h:outputText value=" - " rendered="#{ not empty item.habilitacao }" /> ${ item.habilitacao } 
				<h:outputText value=" - " rendered="#{ not empty item.enfase }" /> ${ item.enfase }
				(${item.grau}) (${item.sigla}) </td>
			<c:set var="vagas" value="${item.vagas_periodo_1 + item.vagas_periodo_2}" />
			<td style="text-align: right;"><ufrn:format type="valorint" valor="${vagas}"/></td>
			<td style="text-align: right;"><ufrn:format type="valorint" valor="${item.totalcandidatosdistintos}"/></td>
			<c:set var="total" value="${total + item.totalcandidatosdistintos}"/>
			<td style="text-align: right;"><ufrn:format type="valor" valor="${item.totalcandidatosdistintos / vagas}"/></td>
		</tr>
			<c:set var="_area" value="${areaAtual}"/>
			<c:set var="_municipio" value="${item.centro}"/>
	</c:forEach>
</table>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>