<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>

#ranking {
	width: 100%;
}

#ranking thead tr td {
	border: 1px solid #999;
	border-width: 1px 0;
	background: #EEE;
}

#ranking tbody tr td {
	border-bottom: 1px dashed #AAA;
	padding-top: 5px;
}

#ranking tbody tr.divisao td {
	border-bottom: 1px solid #555;
	padding-left: 10px;
	padding-bottom: 10px;
}

#ranking tbody tr.centro td {
	font-size: 1.3em;
	padding: 5px 5px 3px;
	border-bottom: 1px solid #555;
	font-weight: bold;
	letter-spacing: 1px;
	background: #EEE;
}

.objetivos {
	display: none;
}
</style>

<script>

 function switchTexto(index) {
	 
	var elem = $('objs'+index);
	var dpy = elem.style.display;
	if (dpy == "none" || dpy == "") {
		elem.style.display = "block";
		$('botao'+index).src = "/sigaa/img/pesquisa/botao_menos.png";
	} else {
		elem.style.display = "none";
		$('botao'+index).src = "/sigaa/img/pesquisa/botao_mais.png";
	}
 }

</script>

<h2>Relatório de Projetos Financiados</h2>
<div id="parametrosRelatorio">
<table>
	<tr>
		<th>Ano dos Projetos:</th>
		<td>${projetoPesquisaForm.obj.codigo.ano}</td>
	</tr>
	<tr>
		<th>Entidade Financiadora:</th>
		<td>${projetoPesquisaForm.financiamentoProjetoPesq.entidadeFinanciadora.nome}</td>
	</tr>
	<c:if test="${projetoPesquisaForm.financiamentoProjetoPesq.entidadeFinanciadora.classificacaoFinanciadora != null}">
	<tr>
		<th>Classificação do Financiamento:</th>
		<td>${projetoPesquisaForm.financiamentoProjetoPesq.entidadeFinanciadora.classificacaoFinanciadora.descricao}</td>
	</tr>
	</c:if>
</table>
</div>

<br />
<table id="ranking" class="tabelaRelatorio" >

	<c:set var="centro" />
	<c:forEach items="${ lista }" var="projeto" varStatus="loop" begin="1">

		<c:if test="${ centro != projeto.centro.nome }">
			<c:set var="centro" value="${ projeto.centro.nome }" />
			<tr class="centro">
				<td colspan="2">${ projeto.centro.nome }</td>
			</tr>
		</c:if>

		<tr>
			<td><b>Projeto:</b></td>
			<td> 
				<ufrn:format type="texto"  valor="${ projeto.codigoTitulo }"/>
			</td>
		</tr>

		<tr>
			<td><b>Coordenador:</b></td>
			<td>${ projeto.coordenador.pessoa.nome } (${
			projeto.coordenador.unidade.sigla })</td>
		</tr>

		<tr>
			<td colspan="2"><b>Objetivos:</b>&nbsp;<img
				id="botao${loop.count}" src="/sigaa/img/pesquisa/botao_mais.png"
				onclick="javascript:switchTexto(${loop.count})" /></td>
		</tr>

		<tr class="divisao">
			<td colspan="2">
			<div id="objs${loop.count}" class="objetivos"><ufrn:format
				type="texto" name="projeto" property="objetivos" /></div>
			</td>
		</tr>
	</c:forEach>

</table>
		

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
