<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:steps /></h2>

<html:form action="/pesquisa/avaliarProjetoPesquisa" method="post" styleId="formAvaliacaoProjeto">

<table class="visualizacao" width="80%">
    <caption>Dados do Projeto de Pesquisa</caption>
	<tbody>
		<c:set var="projeto" value="${ formAvaliacaoProjeto.obj.projetoPesquisa }" />
    	<%@include file="/WEB-INF/jsp/pesquisa/ProjetoPesquisa/include/resumo_projeto.jsp"%>
</table>

<table class="visualizacao" style="width: 100%">
	<tr> <td colspan="2" style="margin:0; padding: 0;">
    <table class="subFormulario" width="100%">
	<caption>Planos de trabalho vinculados ao projeto</caption>
        <thead>
        	<tr>
			    <th style="text-align: left"> Título </th>
			    <th style="text-align: center"> Tipo de Bolsa </th>
			    <th style="text-align: center"> Status </th>
			    <th style="text-align: left">  </th>
	       </tr>
        </thead>
        <tbody>
		<c:forEach var="plano" items="${projetoPesquisaForm.obj.planosTrabalho}" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td>${plano.titulo}</td>
				<td>${plano.tipoBolsaString}</td>
				<td>${plano.statusString}</td>
				<td width="5%">
					<c:choose>
						<c:when test="${plano.pendenteAvaliacao}">
							<ufrn:link action="/pesquisa/avaliarPlanoTrabalho" param="obj.id=${plano.id}&dispatch=edit&fromAvaliacaoProjeto=${formAvaliacaoProjeto.obj.id}">
								<img src="${ctx}/img/pesquisa/avaliar.gif" alt="Avaliar Plano de Trabalho" title="Avaliar Plano de Trabalho"/>
							</ufrn:link>
						</c:when>
						<c:otherwise>
							<html:link action="/pesquisa/planoTrabalho/wizard?dispatch=view&obj.id=${plano.id}">
								<img src="${ctx}/img/pesquisa/view.gif" alt="Visualizar Plano de Trabalho" title="Visualizar Plano de Trabalho"/>
							</html:link>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</td></tr>
</table>

<br />
<table class="formulario" style="width:80%">
	<caption> Formulário de Avaliação </caption>
	<thead>
		<tr>
			<th align="left"> Item </th>
			<th align="right"> Pontuação </th>
		</tr>
	</thead>
	<c:forEach items="${formAvaliacaoProjeto.referenceData.itens}" var="item" varStatus="status">
	<tbody>
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
			<td> ${item.descricao} </td>
			<td align="right">
				<html:text property="notas" size="5" maxlength="4" value="${formAvaliacaoProjeto.notas[status.index]}" 
					onkeydown="return(formataValor(this, event, 1))"/>
			</td>
		</tr>
	</c:forEach>
	<tfoot>
		<tr>
			<td colspan="2" style="text-align: center;">
				<html:button dispatch="cancelar">Cancelar</html:button>
				<html:button dispatch="avaliar">Avançar >></html:button>
			</td>
		</tr>
	</tfoot>
</table>

<style>
	table.escala {
		width: 98%;
		margin: 5px auto;
		background: #FDFDFD;
		border: 1px solid #EEE;
	}

	table.escala td {
		padding: 3px;
		border-bottom: 1px solid #EEE;
	}

	table.escala td {
		text-align: center;
	}

	table.escala .n-recomendado {
		color: #922;
	}

	table.escala .recomendado {
		color: #292;
	}
</style>

<div class="descricaoOperacao" style="width: 70%">
	<h4> Interpretação da escala de pontuação </h4>
	<table class="escala">
		<tr>
			<td> 0,0 a 2,9 </td>
			<td class="intepretacao"> Ruim </td>
			<td class="n-recomendado"> não recomendado</td>
		</tr>
		<tr>
			<td> 3,0 a 4,9 </td>
			<td class="intepretacao"> Regular </td>
			<td class="n-recomendado"> não recomendado</td>
		</tr>
		<tr>
			<td> 5,0 a 6,9 </td>
			<td class="intepretacao"> Bom </td>
			<td class="recomendado"> recomendado</td>
		</tr>
		<tr>
			<td> 7,0 a 7,9 </td>
			<td class="intepretacao"> Ótimo </td>
			<td class="recomendado"> recomendado</td>
		</tr>
		<tr>
			<td> 8,0 a 10,0 </td>
			<td class="intepretacao">  Excelente </td>
			<td class="recomendado"> recomendado</td>
		</tr>
	</table>
	<p>
		Obs.: Se para qualquer item acima a pontuação atribuída for inferior a 3,0 o projeto é
	 	automaticamente considerado como <strong> não recomendado </strong>.
	 </p>
</div>

</html:form>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>