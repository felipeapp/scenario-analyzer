<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> Relatório de Submissão de Projetos por Edital </h2>

<html:form action="/pesquisa/editalSintetico" method="post">

<center>
<h5>
Este relatório recupera todos os projetos EXTERNOS do ano atual e os internos vinculados ao edital escolhido abaixo.
</h5>
</center>

<br>

<table class="formulario" style="width: 40%">
	<caption> Informe o Edital </caption>
	<tbody>
	<tr>
		<th> Edital: </th>
		<td>
			<html:select property="idEdital" style="width: 380px;">
				<html:option value="0">Escolha uma opção</html:option>
				<html:options collection="editais" property="id" labelProperty="descricao"/>
			</html:select>
		</td>
	</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<html:button dispatch="buscarProjetos" value="Buscar"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>