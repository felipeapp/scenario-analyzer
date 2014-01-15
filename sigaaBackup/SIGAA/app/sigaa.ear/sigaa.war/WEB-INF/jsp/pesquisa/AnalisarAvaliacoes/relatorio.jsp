<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	table.tabelaRelatorio tr.centro td {
		font-size: 1.3em;
		font-weight: bold;
		padding: 4px 30px 2px;
		letter-spacing: 1px;
		background: #EEE;
		border-bottom: 1px solid #999;		
	}
	
	table.tabelaRelatorio tr.projeto td {
		border-bottom: 1px dashed #999;		
	}
	table.tabelaRelatorio tfoot td {
		padding: 3px;
		background: #DDD;
		border: 1px solid #555;		
		border-width: 1px 0;
		font-size: 1.3em;
	}
</style>

<h2> Relatório de Projetos de Pesquisa em Avaliação </h2>

<table class="tabelaRelatorio" align="center" style="width:100%">
	<thead>
	<tr>
		<th> Projeto </th>
		<th width="18%" style="text-align: center">Avaliações Distribuídas</th>
		<th width="18%" style="text-align: center">Avaliações Efetuadas</th>
		<th width="10%" style="text-align: right;">Média</th>
		<th width="25%" style="text-align: center">Sugestão da Análise</th>
	</tr>
	</thead>

	<tbody>
	<c:set var="centro" />

	<c:forEach items="${formAnalisarAvaliacoes.analise}" var="analise" varStatus="status">

	<c:set var="projeto" value="${ analise.key }" />

	<c:if test="${ centro != projeto.centro.sigla }">
		<c:set var="centro" value="${projeto.centro.sigla}"/>
		<tr class="centro">
			<td colspan="9"> ${centro} </td>
		</tr>
	</c:if>

	<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" } projeto">

		<td> ${projeto.codigo} </td>
		<td align="center">${projeto.qtdAvaliacoesSubmetidas}</td>
		<td align="center">${projeto.qtdAvaliacoesRealizadas}</td>
		<td align="right">
			<span style="${projeto.media < 5.0 ? "color: #922" : "color: #292" }; font-weight: bold; font-size: 1.1em;">
			<ufrn:format type="valor1" name="projeto" property="media"/>
			</span>
			</td>
		<td align="center"> 
			<c:choose>
				<c:when test="${analise.value == 1}">
					APROVADO		
				</c:when>
				<c:when test="${analise.value == 2}">
					REPROVADO
				</c:when>
				<c:otherwise>
					INDEFINIDO
				</c:otherwise>
			</c:choose>		
		</td>
	</tr>
	</c:forEach>
	</tbody>

<tfoot>
	<tr>
		<td colspan="5" align="center">
	   		${fn:length(formAnalisarAvaliacoes.analise)} projetos encontrados
		</td>
	</tr>
</tfoot>

</table>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>