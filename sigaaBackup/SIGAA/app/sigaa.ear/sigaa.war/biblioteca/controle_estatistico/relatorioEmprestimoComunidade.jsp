<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 20px 0 0; border-bottom: 1px solid #555}
	tr.header td {padding: 0px ; background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #000;}
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
</style>
<f:view>
	

	<%@include file="/biblioteca/controle_estatistico/paginaParametrosPadraorelatoriosBiblioteca.jsp"%>
	

    <c:set var="totalInterna" value="0" scope="request"/>
    <c:set var="totalExterna" value="0" scope="request"/>
 
	<table class="tabelaRelatorioBorda" cellspacing="1" width="100%" style="font-size: 10px;" >
		
		<tr class="header">
			<td align="left">Biblioteca</td>
			<td align="right">Empr�stimo para comunidade Interna</td>
			<td align="right">Empr�stimo para comunidade Externa</td>
			<td align="right">Total de Empr�stimos</td>
		</tr>
		
		<c:forEach items="#{_abstractRelatorioBiblioteca.lista}" var="linha" varStatus="indice">
			<tr class="componentes">
				<td align="left"> ${linha[0]}</td>
				<td align="right"> ${linha[1]}</td>
				<td align="right"> ${linha[2]}</td>
				<td align="right"> ${linha[1] + linha[2]}</td>
				
				<c:set scope="request" var="totalInterna" value="${totalInterna  + linha[1]}"/>
				<c:set scope="request" var="totalExterna" value="${totalExterna  +  linha[2]}"/>
			</tr>
		</c:forEach>
			<tr class="header">
				<td align="right">Total</td>
				<td align="right">${totalInterna}</td>
				<td align="right">${totalExterna}</td>
				<td align="right">* ${totalInterna + totalExterna}</td>
			</tr>
	</table>
	
<div style="margin-top:20px;">
		<hr  />
		<h4>Observa��es:</h4>
		<br/>
		<p> 1 - As quantidades mostradas se referem as quantidades de empr�stimos + renova��es. </p>
		<p> 2 - As quantidades de empr�stimos nesse relat�rio <strong>n�o</strong> incluem empr�stimos antigos que n�o se tem a informa��o do usu�rio que realizou o empr�stimo, com isso a quantidade 
		mostrada poder�, para empr�stimos antigos e migrados, ser menor que a quantidade de empr�stimos realizados.</p>
</div>	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>