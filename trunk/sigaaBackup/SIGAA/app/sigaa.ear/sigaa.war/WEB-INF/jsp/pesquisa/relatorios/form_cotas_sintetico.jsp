<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> &gt; Relatório de Pedido de Cotas</h2>

<html:form action="/pesquisa/editalSintetico" method="post">
	<table class="formulario" style="width: 40%">
		<caption>Dados para emissão do relatório</caption>
		<tbody>
			<tr>
				<th class="required">Cota:</th>
				<td><html:select property="idEdital" style="width: 450px;">
					<html:option value="0">-- SELECIONE --</html:option>
					<html:options collection="cotas" property="id"
						labelProperty="descricao" />
				</html:select></td>
			</tr>
			<tr>
				<td> <label for="centro">Centro:</label> </td>
				<td>
					<html:select property="centro.id" style="width: 450px;">
						<html:option value="-1">  -- TODOS OS CENTROS --  </html:option>
						<html:options collection="centros" property="unidade.id" labelProperty="unidade.codigoNome" />
					</html:select>
				</td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<html:checkbox property="agruparPorDepartamento" styleId="checkAgrupar"/>
					<label for="agruparPorDepartamento" onclick="$('checkAgrupar').checked = !$('checkAgrupar').checked;">Agrupar docentes por departamento</label>
				</td>
			</tr>
		</tbody>

		<tfoot>
			<tr>
				<td colspan="3">
					<html:button dispatch="buscarCotas"
					value="Buscar" />
					<html:button dispatch="cancelar" value="Cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
	<br/>
	<div class="obrigatorio"> Campo de preenchimento obrigatório. </div>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
