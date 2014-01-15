<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Finalização de Planos de Trabalho
</h2>

<html:form action="/pesquisa/finalizarPlanosTrabalho" method="post">
	<table class="formulario" style="width: 50%">
		<caption>Selecione a Cota desejada</caption>
		<tbody>
			<tr>
				<th>Cota:</th>
				<td><html:select property="obj.cota.id">
					<html:option value="0">Selecione uma cota</html:option>
					<html:options collection="cotas" property="id" labelProperty="descricaoCompleta" />
				</html:select></td>
			</tr>
		</tbody>

		<tfoot>
			<tr>
				<td colspan="3">
					<html:button dispatch="buscarPlanos" value="Buscar Planos de Trabalho" />
					<html:button dispatch="cancelar" value="Cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>