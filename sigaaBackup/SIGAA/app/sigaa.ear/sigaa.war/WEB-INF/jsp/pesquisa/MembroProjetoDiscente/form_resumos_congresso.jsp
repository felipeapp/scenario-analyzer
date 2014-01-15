<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Resumos para Congresso de Iniciação Científica
</h2>

<html:form action="/pesquisa/buscarMembroProjetoDiscente" method="post">

<table class="formulario" width="35%">
    <caption>Informe o ano do Congresso</caption>
    <tbody>
	   <tr>
	   		<th> Ano: </th>
       		<td> <html:text property="obj.planoTrabalho.projetoPesquisa.ano" size="5"/></td>
       </tr>
	<tfoot>
		<tr>
			<td colspan="2">
				<html:button dispatch="resumosCongresso" value="Buscar"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>