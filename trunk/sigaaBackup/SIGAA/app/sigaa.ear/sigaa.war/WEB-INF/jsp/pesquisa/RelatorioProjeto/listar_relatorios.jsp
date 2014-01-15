<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:subSistema /> >
	<c:out value="Relatórios Anuais de Projetos de Pesquisa"/>
</h2>

<c:set var="projetos" value="${ formRelatorioProjeto.referenceData.projetos }" />

<div class="infoAltRem">
	<html:img page="/img/seta.gif" style="overflow: visible;"/>: Enviar Relatório
</div>

<table class="listagem">
	<caption>Relatório Anual de Projetos de Pesquisa</caption>
	<thead>
		<tr>
			<th> Código </th>
			<th> Título do Projeto </th>
			<th style="text-align: center;"> Enviado </th>
			<th></th>
		</tr>
	</thead>

	<tbody>
		<c:forEach var="projeto" items="${ projetos }" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td> ${ projeto.codigo } </td>
			<td> ${ projeto.titulo } </td>
			<td style="text-align: center;"> ${ projeto.relatorioProjeto.enviado ? 'Sim' : 'Não' } </td>		
			<td><html:link action="/pesquisa/relatorioProjeto?idProjeto=${projeto.id}&dispatch=popularEnvio"> <img src="/sigaa/img/seta.gif"  title="Enviar Relatório"/> </html:link> </td>			
		</tr>
		</c:forEach>
	</tbody>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>