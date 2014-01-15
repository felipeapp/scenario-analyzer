<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--

/* Paineis de op�oes */
.titulo {
	background: #EFF3FA;
}

-->
</style>


<f:view>
	<h2><ufrn:subSistema /> > Visualiza��o da Bolsa de Apoio T�cnico</h2>

<h:form>
	<table class="visualizacao" width="90%">
		<caption>DADOS DO AN�NCIO DE BOLSA</caption>
		<tbody>
			<tr>
				<th>Data An�ncio:</th>
				<td><fmt:formatDate value="${agregadorBolsas.apoioTecnico.dataAnuncio }" pattern="dd/MM/yyyy"/></td>
				<th>Respons�vel:</th>
				<td>${agregadorBolsas.apoioTecnico.responsavelProjeto.pessoa.nome }</td>
			</tr>
			<tr>
				<th>Status:</th>
				<td>${agregadorBolsas.apoioTecnico.statusAtualBolsa }</td>
				<th>Tipo da Bolsa:</th>
				<td>${ agregadorBolsas.apoioTecnico.tipoBolsa }</td>
			</tr>
			<tr>
				<th>Email:</th>
				<td colspan="3">${agregadorBolsas.apoioTecnico.emailContato }</td>
			</tr>
			<tr>
				<td class="subFormulario" colspan="4">Descri��o:</td>
			</tr>
			<tr>
				<td colspan="4">${agregadorBolsas.apoioTecnico.descricao }</td>
			</tr>
		</tbody>
		<tfoot>
			<tr align="center">
				<td colspan="4">
					<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)" id="btnVoltar"/>
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>