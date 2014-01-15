<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> &gt; Relatório Quantitativo de Solicitações de Cotas de Bolsas</h2>

<html:form action="/pesquisa/relatorios/quantitativoSolicitacoesCotas" method="post">
	<table class="formulario" style="width: 30%">
		<caption>Informe a Cota</caption>
		<tbody>
			<tr>
				<th>Cota:</th>
				<td><html:select property="obj.id">
					<html:options collection="cotas" property="id"
						labelProperty="descricao" />
				</html:select></td>
			</tr>
		</tbody>

		<tfoot>
			<tr>
				<td colspan="3">
					<html:button dispatch="gerar" value="Buscar" />
					<html:button dispatch="cancelar" value="Cancelar" cancelar="true"/>
				</td>
			</tr>
		</tfoot>
	</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>