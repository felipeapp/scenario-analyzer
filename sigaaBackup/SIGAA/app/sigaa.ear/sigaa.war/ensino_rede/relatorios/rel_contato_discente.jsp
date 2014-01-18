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
				<h2><b>Relatório dos Contatos dos Discente</b></h2>
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
						<td align="left" colspan="5">Campus: ${ linha.dadosCurso.campus.sigla }</td>
					</tr>
					<tr class="componentes">
						<td align="center"> Nome </td>
						<td align="left"> CPF </td>
						<td align="center"> Tel. Fixo </td>
						<td align="center"> Tel. Celular </td>
						<td align="center"> Email </td>
					</tr>
					
					  <c:set var="_unidade" value="${unidadeAtual}"/>
					  <c:set var="_campus" value="${campusAtual}"/>
			  </c:if>
			  
			  <c:if test="${ _campus != campusAtual }">
					<tr class="componentes">
						<td align="left" colspan="5">Campus: ${ linha.dadosCurso.campus.sigla }</td>
					</tr>
					<tr class="componentes">
						<td align="center"> Nome </td>
						<td align="left"> CPF </td>
						<td align="center"> Tel. Fixo </td>
						<td align="center"> Tel. Celular </td>
						<td align="center"> Email </td>
					</tr>
					
					  <c:set var="_campus" value="${campusAtual}"/>
			  </c:if>
			  
			  <tr class="valores">
			  	<td> ${ linha.pessoa.nome } </td>
			  	<td> ${ linha.pessoa.cpfCnpjFormatado } </td>
			  	<td> ${ linha.pessoa.telefone }</td>
			  	<td> ${ linha.pessoa.celular }</td>
			  	<td> ${ linha.pessoa.email } </td>
			  </tr>
			  
		</c:forEach>
	</table>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>