<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:subSistema /> >
	<c:out value="Relat�rios Anuais de Projetos de Pesquisa"/>
</h2>

<c:set var="projetos" value="${ formRelatorioProjeto.referenceData.projetos }" />

<div class="infoAltRem">
	<html:img page="/img/seta.gif" style="overflow: visible;"/>: Enviar Relat�rio
</div>

<table class="listagem">
	<caption>Relat�rio Anual de Projetos de Pesquisa</caption>
	<thead>
		<tr>
			<th> C�digo </th>
			<th> T�tulo do Projeto </th>
			<th style="text-align: center;"> Enviado </th>
			<th></th>
		</tr>
	</thead>

	<tbody>
		<c:forEach var="projeto" items="${ projetos }" varStatus="loop">
		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td> ${ projeto.codigo } </td>
			<td> ${ projeto.titulo } </td>
			<td style="text-align: center;"> ${ projeto.relatorioProjeto.enviado ? 'Sim' : 'N�o' } </td>		
			<td><html:link action="/pesquisa/relatorioProjeto?idProjeto=${projeto.id}&dispatch=popularEnvio"> <img src="/sigaa/img/seta.gif"  title="Enviar Relat�rio"/> </html:link> </td>			
		</tr>
		</c:forEach>
	</tbody>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>