<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 20px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000;}
</style>

<f:view>
	<h2>Discente sem Orientação Cadastrada</h2>
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	
	    <c:set var="_curso" />
		<c:forEach items="${ relatoriosLato.discentes }" var="linha">
	
			<c:set var="cursoAtual" value="${ linha.curso.nome }"/>
			<c:if test="${_curso != cursoAtual}">
				<tr class="curso">
					<td colspan="10"><b> ${ linha.curso.nome } </b></td>
				</tr>
				
				<tr class="header">
					<td style="text-align: left;"> Matrícula </td>
					<td style="text-align: left;"> Discente </td>
				</tr>
					<c:set var="_curso" value="${cursoAtual}"/>
			</c:if>
				

				<tr class="componentes">
					<td style="text-align: left;"> ${ linha.matricula } </td>
					<td style="text-align: left;"> ${ linha.pessoa.nome } </td>
				</tr>
			
		</c:forEach>
	
	</table>
	
	<center><b>Total de Registros: ${ fn:length( relatoriosLato.discentes) } </b></center>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>