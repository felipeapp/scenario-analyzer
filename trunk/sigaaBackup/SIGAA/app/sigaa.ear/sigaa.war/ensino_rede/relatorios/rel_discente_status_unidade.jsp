<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.header td {padding: 0px ; background-color: #fffff;  font-weight: bold; }
	tr.componente td {padding: 0px 0px 0px; font-weight: bold; color: red; }
	tr.componentes td {padding: 0px 0px 0px; font-weight: bold; background-color: #eee; border-width: thin; border-style: solid; }
	tr.valores td { border-width: thin; border-style: solid; }
</style>

<f:view>
	<table width="100%">
  	  <tr>		
		<td class="listagem">
			<td align="center">
				<h2><b>Relatório dos Discentes por Unidade/Status</b></h2>
			</td>
	  </tr>
	</table>

<br />

    <c:set var="_unidade" />
    <c:set var="_campus" />
	<table cellspacing="1" width="100%" style="font-size: 11px;" align="center">
		<c:forEach items="#{ relatoriosEnsinoRedeMBean.resultadosBusca }" var="linha" varStatus="indice">
			<c:set var="unidadeAtual" value="${ linha.dadosCurso.campus.instituicao.sigla }"/>
			<c:set var="campusAtual" value="${ linha.dadosCurso.campus.sigla }"/>
			  
			  <c:if test="${ _unidade != unidadeAtual }">
					<tr class="header">
						<td colspan="10" align="center" style="font-size: 12px; padding-top: 40px; border: none;"><b>${ linha.dadosCurso.campus.instituicao.sigla }</b></td>
					</tr>
					<tr class="componentes">
						<td align="left" colspan="4">Campus: ${ linha.dadosCurso.campus.sigla }</td>
					</tr>
					<tr class="componentes">
						<td align="center"> Ingresso </td>
						<td align="left"> Discente </td>
						<td align="center"> CPF </td>
						<td align="center"> Situação </td>
					</tr>
					
					  <c:set var="_unidade" value="${unidadeAtual}"/>
					  <c:set var="_campus" value="${campusAtual}"/>
			  </c:if>
			  
			  <c:if test="${ _campus != campusAtual }">
					<tr class="componentes">
						<td align="left" colspan="4">Campus: ${ linha.dadosCurso.campus.sigla }</td>
					</tr>
					<tr class="componentes">
						<td align="center"> Ingresso </td>
						<td align="left"> Discente </td>
						<td align="center"> CPF </td>
						<td align="center"> Situação </td>
					</tr>
					
					  <c:set var="_campus" value="${campusAtual}"/>
			  </c:if>
			  
			  <tr class="valores">
			  	<td align="center"> ${ linha.anoIngresso }.${ linha.periodoIngresso } </td>
			  	<td> ${ linha.nome } </td>
			  	<td align="center"> ${ linha.pessoa.cpfCnpjFormatado } </td>
			  	<td align="center"> ${ linha.status.descricao } </td>
			  </tr>
			  
		</c:forEach>
	</table>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>