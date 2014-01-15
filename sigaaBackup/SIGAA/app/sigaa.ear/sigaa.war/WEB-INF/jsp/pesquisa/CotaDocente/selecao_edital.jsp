<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> &gt; Efetuar Ajustes na Distribuição de Cotas </h2>

<html:form action="/pesquisa/distribuirCotasDocentes" method="post">
<c:set var="operacao" value="Buscar" />
<c:set var="editais" value="${ formCotaDocente.referenceData.editais }"/>
<c:set var="classificacoes" value="${ formCotaDocente.referenceData.classificacoes }"/>

<div class="descricaoOperacao">
	<p>
		Caro Gestor,
	</p>
	<p align="justify">
		Essa operação permite ao usuário efetuar ajustes na distribuição de cotas para os docentes que foi realizada. 
		Dentre os critérios abaixo, selecione o edital para o qual irá distribuir cotas, 
		o ranking de classificação dos docentes desejados e o critério de ordenação desejado.
	</p>
	<p align="justify">
		Após selecionar os critérios e clicar no botão <strong>Buscar Distribuição</strong> será exibido a distribuição de cotas
		gerada para que você pode realizar os ajustes necessários. 
	</p>
</div>

<table class="formulario" style="width: 100%">
	<caption> Critérios de Busca </caption>
	<tr>
		<th style="width: 16%;"> Edital: </th>
		<td>
			<html:select property="obj.edital.id" style="width: 100%; ">
				<html:options collection="editais" property="id" labelProperty="descricao"/>
			</html:select>
		</td>
	</tr>

	<c:if test="${ formCotaDocente.geracao}">
		<c:set var="operacao" value="Gerar" />
		<tr>
			<th> Ranking de Produtividade: </th>
			<td>
				<html:select property="classificacao.id" style="width: 820px;">
					<html:options collection="classificacoes" property="id" labelProperty="descricao"/>
				</html:select>
			</td>
		</tr>
	</c:if>

	<c:if test="${ not formCotaDocente.geracao}">
		<c:set var="centros" value="${formCotaDocente.referenceData.centros}" />
		<tr>
			<th> Centro/Unidade: </th>
			<td>
				<html:select property="centro.id" style="width: 50%;">
					<html:option value="-1">TODOS OS CENTROS</html:option>
					<html:options collection="centros" property="unidade.id" labelProperty="unidade.codigoNome"/>
				</html:select>
			</td>
		</tr>

		<tr>
			<th> Ordenar por: </th>
			<td>
				<html:radio property="ordenacao" value="ifc" styleId="ordenarIfc"/>
				<label for="ordenarIfc">IFC</label>
				<html:radio property="ordenacao" value="nome" styleId="ordenarNome"/>
				<label for="ordenarNome">Nome do Docente</label>
			</td>
		</tr>
	</c:if>

	<tfoot>
		<tr>
			<td colspan="2">
				<html:button dispatch="selecionarEdital" value="${operacao} Distribuição"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>