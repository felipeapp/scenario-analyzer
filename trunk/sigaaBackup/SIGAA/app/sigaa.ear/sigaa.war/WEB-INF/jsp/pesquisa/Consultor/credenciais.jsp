<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	table.formulario th { font-weight: bold;}
</style>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt; Visualização das Credenciais dos Consultores
</h2>

	<table class="formulario" width="85%">
        <caption>Dados do Consultor</caption>
        <tbody>
	    	<tr>
				<th>Nome:</th>
				<td> ${ formConsultor.obj.nome } </td>
	        </tr>
	        <tr>
	            <th>E-mail:</th>
	            <td> ${ formConsultor.obj.email } </td>
	        </tr>
	        <tr>
	            <th>Área de Conhecimento:</th>
	            <td> ${ formConsultor.obj.areaConhecimentoCnpq.nome } </td>
	        </tr>
	        <tr>
	            <th>URL:</th>
	            <td> ${ formConsultor.obj.urlAcesso }</td>
	        </tr>
	        <tr>
	            <th>Senha:</th>
	            <td> ${ formConsultor.obj.senha } </td>
	        </tr>
        </tbody>
	</table>

<br />
<center>
	<a href="javascript:history.go(-1)">  << Voltar </a>
</center>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>