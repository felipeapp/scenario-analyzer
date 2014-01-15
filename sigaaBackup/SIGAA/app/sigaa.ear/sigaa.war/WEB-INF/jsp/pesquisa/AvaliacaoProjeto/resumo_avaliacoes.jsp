<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> Pesquisa > Resumo de Avaliações de Projeto de Pesquisa</h2>

<style>
	table.formulario td.media {
		font-size: 1.2em;
		font-weight: bold;
	}
</style>

<table class="visualizacao" width="80%">
	<caption> Avaliação de Projeto de Pesquisa </caption>
	<tbody>
		<tr>
			<th width="20%"> Código: </th>
			<td>
				<html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=view&id=${projeto.id}">
				${projeto.codigo}
				</html:link>
			</td>
		</tr>
		<tr>
			<th> Título: </th>
			<td> ${projeto.titulo} </td>
		</tr>
		<tr>
			<th> Coordenador: </th>
			<td> ${projeto.coordenador.nome} </td>
		</tr>
		<tr>
			<th> Média: </h>
			<td class="media" style="${ projeto.media < 5.0 ? "color: #922" : "color: #292" }; font-size: 1.2em;">
				<strong><ufrn:format type="valor1" name="projeto" property="media"/></strong>
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario" style="text-align: center"> Avaliações </td>
		</tr>
		<tr>
			<td colspan="2">
				<table class="subFormulario listagem" style="width:100%; border: 0">
				<c:forEach items="${projeto.avaliacoesProjeto}" var="avaliacao" varStatus="loop">
				<tbody>
					<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td>
							<b>c${avaliacao.consultor.codigo}</b> <br />
							<small>${avaliacao.tipoDistribuicaoString}</small>
						</td>
						<td>
							${avaliacao.statusAvaliacao} <br />
							<c:choose>
								<c:when test="${ avaliacao.justificada }">
									<em>${avaliacao.justificativa }</em>
								</c:when>
								<c:when test="${ avaliacao.realizada }">
									<em>${avaliacao.observacoes }</em>
								</c:when>
							</c:choose>
						</td>

						<td align="right" style="${avaliacao.media < 5 ? "color: #922" : "color: #292" }">
						<c:if test="${ avaliacao.realizada }">
							<big><strong><ufrn:format type="valor1" name="avaliacao" property="media" /></strong></big>
						</c:if>
						</td>

						<td align="center">
							<ufrn:link action="/pesquisa/avaliarProjetoPesquisa" param="obj.id=${avaliacao.id}&dispatch=view">
								<img src="${ctx}/img/pesquisa/view.gif"
									alt="Visualizar Avaliação"
									title="Visualizar Avaliação" />
							</ufrn:link>
						</td>
					</tr>
				</c:forEach>
				</tfoot>
				</table>
			</td>
		</tr>
	</tbody>
</table>

<br />
<div class="voltar" style="text-align: center;">
	<a href="javascript: history.go(-1);"> Voltar </a>
</div>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>