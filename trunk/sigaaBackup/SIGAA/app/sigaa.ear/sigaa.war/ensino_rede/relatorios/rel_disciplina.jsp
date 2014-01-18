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
				<h2><b>Relatório Desempenho por Disciplina</b></h2>
			</td>
	  </tr>
	</table>

<br />

    <c:set var="_unidade" />
	<table cellspacing="1" width="100%" style="font-size: 11px;" align="center">
		<c:forEach items="#{ relatoriosEnsinoRedeMBean.resultadosBusca }" var="linha" varStatus="indice">
			<c:set var="unidadeAtual" value="${ linha.turma.dadosCurso.campus.sigla }"/>
			  
			  <c:if test="${ _unidade != unidadeAtual }">
					<tr class="header">
						<td colspan="10" align="center" style="font-size: 12px; padding-top: 40px; border: none;"><b>${ linha.turma.dadosCurso.campus.instituicao.sigla }</b></td>
					</tr>
					<tr class="componentes">
						<td> Campus </td>
						<td> Componente </td>
						<td> % </td>
					</tr>
					
					  <c:set var="_unidade" value="${unidadeAtual}"/>
			  </c:if>
			  
			  <tr class="valores">
			  	<td> ${ linha.turma.dadosCurso.campus.sigla } </td>
			  	<td> ${ linha.turma.componente.codigo } - ${ linha.turma.componente.nome } </td>
			  	<td> ${ linha.total  } </td>

			  </tr>
			  
		</c:forEach>
	</table>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>