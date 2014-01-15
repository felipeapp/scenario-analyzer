<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> Relatório Sintético de Finaciamentos de Projetos de Pesquisa </h2>

<html:form action="/pesquisa/relatoriosPesquisa" method="post">
<table class="formulario" style="width: 40%">
	<caption> Informe os critérios de busca </caption>
	<tbody>
	<tr>
		<th> Ano inicial: </th>
		<td>
			<html:text property="anoInicio" size="5" maxlength="5"/>
		</td>
	</tr>
	<tr>
		<th> Ano final: </th>
		<td>
			<html:text property="anoFim" size="5" maxlength="5"/>
		</td>
	</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<html:button dispatch="financiamentosSintetico" value="Buscar"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>