<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> &gt; Relatório de Acompanhamento de Cotas de Bolsas</h2>

<html:form action="/pesquisa/relatorios/acompanhamentoCotas" method="post">
	<table class="formulario" style="width: 50%">
		<caption>Selecione uma das cotas cadastradas</caption>
		<tbody>
			<tr>
				<th>Cota:</th>
				<td>
					<html:select property="obj.id">
						<html:options collection="cotas" property="id" labelProperty="descricao" />
					</html:select>
				</td>
			</tr>
		</tbody>

		<tfoot>
			<tr>
				<td colspan="2">
					<html:button dispatch="gerar"	value="Consultar" />
					<html:button dispatch="cancelar" value="Cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
