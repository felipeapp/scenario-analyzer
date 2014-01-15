<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
<!--

/* Paineis de opçoes */
.titulo {
	background: #EFF3FA;
}

-->
</style>


<f:view>
	<h2><ufrn:subSistema /> > Visualização da Bolsa de Apoio Técnico</h2>

<h:form>
	<table class="visualizacao" width="90%">
		<caption>DADOS DO ANÚNCIO DE BOLSA</caption>
		<tbody>
			<tr>
				<th>Data Anúncio:</th>
				<td><fmt:formatDate value="${agregadorBolsas.apoioTecnico.dataAnuncio }" pattern="dd/MM/yyyy"/></td>
				<th>Responsável:</th>
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
				<td class="subFormulario" colspan="4">Descrição:</td>
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