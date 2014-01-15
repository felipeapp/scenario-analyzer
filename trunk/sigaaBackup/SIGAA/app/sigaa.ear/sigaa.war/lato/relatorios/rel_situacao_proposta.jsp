<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.curso td {padding: 20px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000;}
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
</style>

<h2>RELATÓRIO DE SITUAÇÕES DAS PROPOSTAS DE CURSO DE LATO SENSU</h2>

<f:view>
<table width="100%" style="font-size: 10px;">


	<c:set var="_situacao" />	
	<c:forEach items="#{relatoriosLato.cursosLato}" var="linha">

		<c:set var="situacaoAtual" value="${linha.propostaCurso.situacaoProposta.id}"/>
		<c:if test="${_situacao != situacaoAtual}">
				<tr>
					<th style="text-align: center; font-weight: bold;" >Situação Proposta - ${ linha.propostaCurso.situacaoProposta.descricao }</th>
				<tr>
				<tr class="header">
					<td>Curso</td> 	
				</tr>
		</c:if>

		<tr class="componentes">
			<td> ${ linha.nome } </td>
		</tr>

	<c:set var="_situacao" value="${ situacaoAtual }"/>

	</c:forEach>
	
</table>
</f:view>
<br />

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>