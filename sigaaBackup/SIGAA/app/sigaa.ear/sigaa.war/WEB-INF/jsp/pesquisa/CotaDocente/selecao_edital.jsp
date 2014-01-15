<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> &gt; Efetuar Ajustes na Distribui��o de Cotas </h2>

<html:form action="/pesquisa/distribuirCotasDocentes" method="post">
<c:set var="operacao" value="Buscar" />
<c:set var="editais" value="${ formCotaDocente.referenceData.editais }"/>
<c:set var="classificacoes" value="${ formCotaDocente.referenceData.classificacoes }"/>

<div class="descricaoOperacao">
	<p>
		Caro Gestor,
	</p>
	<p align="justify">
		Essa opera��o permite ao usu�rio efetuar ajustes na distribui��o de cotas para os docentes que foi realizada. 
		Dentre os crit�rios abaixo, selecione o edital para o qual ir� distribuir cotas, 
		o ranking de classifica��o dos docentes desejados e o crit�rio de ordena��o desejado.
	</p>
	<p align="justify">
		Ap�s selecionar os crit�rios e clicar no bot�o <strong>Buscar Distribui��o</strong> ser� exibido a distribui��o de cotas
		gerada para que voc� pode realizar os ajustes necess�rios. 
	</p>
</div>

<table class="formulario" style="width: 100%">
	<caption> Crit�rios de Busca </caption>
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
				<html:button dispatch="selecionarEdital" value="${operacao} Distribui��o"/>
				<html:button dispatch="cancelar" value="Cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>