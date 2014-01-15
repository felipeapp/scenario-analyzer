<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
	#distribuicao {
		width: 80%;
	}

	#distribuicao .area{
		text-align: left;
		padding-left: 15px;
	}

	#distribuicao .grandeArea td{
		font-weight: bold;
		background: #EEE;
		padding: 2px 5px;
		border: 1px solid #DDD;
		border-width: 1px 0;
	}

	#distribuicao .total{
		text-align: right;
	}

	#distribuicao tfoot tr td {
		text-align: center;
	}

	#distribuicao .consultores {
		padding: 2px;
		font-size: 1.3em;
		text-align: center;
		color: #292;
	}

</style>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Distribuir Projeto de Pesquisa para Consultores Especiais
</h2>

<div class="descricaoOperacao">
	<p>
		Caro gestor, através desta operação serão distribuídos os projetos marcados como INTERNOS e que aguardam avaliação 
		juntamente com os projetos que não obtiveram avaliações suficientes para os consultores especiais cadastrados na base de dados.
	</p>
</div>

	<html:form action="/pesquisa/distribuirProjetoPesquisa" method="post" focus="consultoresPorProjeto">

<table class="formulario">
<caption>Filtrar projetos por edital</caption>
	<tr>
		<th>Edital:</th>
		<td>
		<html:select property="idEdital" style="width: 95%;">
			<html:options collection="editais" property="id" labelProperty="descricao"/>
		</html:select>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2"> <html:button dispatch="popularAutomaticaEspeciais" value="Filtrar Projetos"/></td>
	</tr>
</table>
<br />

<c:if test="${not empty areas}">
	<table id="distribuicao" class="listagem">
		<caption>Distribuição de Projetos de Pesquisa para Avaliação</caption>
		<thead>
			<tr>
		       	<th class="area">Área de Conhecimento</th>
		       	<th class="total">N° de Projetos</th>
		       	<th class="total">N° de Consultores</th>
			</tr>
		</thead>
		<tbody>

		    <c:set var="grandeArea" value=""/>
		    <c:set var="totalProjetos" value="0"/>
		    <c:forEach items="${areas}" var="area" varStatus="status">
		    	<c:set var="totalProjetos" value="${totalProjetos + area.qtdProjetos}" />

		    	<c:if test="${grandeArea != area.grandeArea.nome }" >
		    	  <c:set var="grandeArea" value="${ area.grandeArea.nome }"/>
		    		<tr class="grandeArea">
		    			<td colspan="3"> ${grandeArea } </td>
		    		</tr>
		    	</c:if>

				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td class="area"> ${area.nome} </td>
					<td class="total"> ${area.qtdProjetos} </td>
					<td class="total"> ${area.qtdConsultores} </td>
				</tr>
			</c:forEach>
		</tbody>

		<tfoot>
			<tr>
				<td colspan="3">
					<strong>  ${totalProjetos} projetos disponíveis </strong>
				</td>
			</tr>
			<tr>
		    	<td colspan="3">
		    		<label for="consultoresProProjeto">Defina o número de Consultores por Projeto </label>
		    		<input type="text" name="consultoresPorProjeto" size="2" maxlength="1" class="consultores"/>
		    	</td>
		    </tr>
			<tr>
				<td colspan="3">
					<html:button dispatch="automaticamenteEspeciais" value="Realizar Distribuição"/>
					<html:button dispatch="cancelar" value="Cancelar"/>
		    	</td>
		    </tr>
		</tfoot>
	</table>
</c:if>

	</html:form>



<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>