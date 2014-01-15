<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> Relat�rio de Participa��o de Docentes em Projetos de Pesquisa </h2>

<html:form action="/pesquisa/relatorioParticipacaoDocentes" method="post">

<table class="formulario" style="width: 75%">
	<caption> Informe os crit�rios de consulta </caption>
	<tbody>
	<tr>
		<th> Ano dos Projetos: </th>
		<td> <html:text property="ano" size="4"/> </td>
	</tr>
	<tr>
		<th> Situa��o dos Projetos: </th>
		<td>
			<html:select property="situacao.id" style="width: 380px;">
				<html:option value="-1">-- QUALQUER SITUA��O --</html:option>
				<html:options collection="situacoesProjeto" property="id" labelProperty="descricao"/>
			</html:select>
		</td>
	</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<html:button dispatch="gerar" value="Gerar Relat�rio"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>

</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>