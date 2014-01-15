<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Relatório de Substituição de Bolsistas
</h2>

<div class="infoAltRem">
	<html:img page="/img/pesquisa/view.gif" style="overflow: visible;"/>
		 : Visualizar Detalhes da Substituição
</div>

<table class="listagem">
	<caption>
		Substituições Encontradas de
		${formSubstituicoesBolsistas.inicio} a
		${formSubstituicoesBolsistas.fim}
	</caption>
	<thead>
	<tr>
		<c:if test="${acesso.pesquisa}">
			<th> CPF </th>
		</c:if>
		<th> Bolsista </th>
		<th> Data de Indicação </th>
		<th> Data de Finalização </th>
		<th> Orientador </th>
		<th> Status </th>
		<th> </th>
	</tr>
	</thead>

	<tbody>
		<c:set var="_total" value="0"/>
		<c:set var="_tipoBolsa" value="0"/>

		<c:forEach items="${relatorio}" var="membro" varStatus="loop">

		<c:if test="${ _tipoBolsa != membro.tipoBolsa.id }">
			<c:set var="_tipoBolsa" value="${membro.tipoBolsa.id}"/>

			<c:if test="${_total != 0}">
				<tr>
					<td colspan="7" style="text-align: right;">
						Total: ${_total}
					</td>
				</tr>
				<c:set var="_total" value="0"/>
			</c:if>

			<tr>
				<td colspan="7" style="background: #C8D5EC; font-weight: bold; padding: 4px 0 4px 10px;">
					Bolsistas ${membro.tipoBolsaString}
				</td>
			</tr>
		</c:if>
		<tr class="${ loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
			<c:if test="${acesso.pesquisa}">
				<td> <ufrn:format type="cpf_cnpj" valor="${membro.discente.pessoa.cpf_cnpj}" /> </td>
			</c:if>
			<td> ${membro.discente.pessoa.nome} </td>
			<td> <ufrn:format type="datahorasec" valor="${membro.dataIndicacao}"/> </td>
			<td> <ufrn:format type="datahorasec" valor="${membro.dataFinalizacao}"/> </td>
			<td> ${membro.planoTrabalho.orientador.pessoa.nome} </td>
			<td>
				<c:choose>
					<c:when test="${membro.dataFinalizacao == null}">ATIVO</c:when>
					<c:otherwise>INATIVO</c:otherwise>
				</c:choose>
			</td>
			<td>
				<c:if test="${membro.dataFinalizacao == null}">
				<html:link action="/pesquisa/indicarBolsista?dispatch=resumo&obj.id=${membro.planoTrabalho.id}">
					<img src="${ctx}/img/pesquisa/view.gif"
						alt="Resumo da Substituição"
						title="Resumo da Substituição"/>
				</html:link>
				</c:if>
			</td>
			<c:set var="_total" value="${ _total + 1 }"/>
		</tr>
		</c:forEach>
		<tr>
			<td colspan="7" style="text-align: right;">
				Total: ${_total}
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="7" style="text-align: right;">
				Total de Substituições: ${ fn:length(relatorio) }
			</td>
		</tr>
	</tfoot>
</table>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>