<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<ufrn:subSistema /> > Relatórios Parciais de Iniciação Científica
</h2>

<table class="listagem">
	<caption>Meus Relatórios</caption>
	<thead>
		<tr>
			<th> Cota </th>
			<th> Orientador </th>
			<th> Submetido em </th>
			<th> Parecer Emitido? </th>
			<th> </th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="_relatorio" items="${relatorios}" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
			<td>
				${ _relatorio.planoTrabalho.cota.descricao }
			</td>
			<td> ${_relatorio.planoTrabalho.orientador.pessoa.nome} </td>
			<td nowrap="nowrap"> <ufrn:format type="dataHora" name="_relatorio" property="dataEnvio"/></td>
			<td align="center">
				<c:choose>
					<c:when test="${_relatorio.parecerOrientador != null}">
						Sim
					</c:when>
					<c:otherwise>Não</c:otherwise>
				</c:choose>
			</td>
			<td>
				<ufrn:link action="/pesquisa/relatorioBolsaParcial" param="idRelatorio=${_relatorio.id}&dispatch=view">
					<img src="${ctx}/img/pesquisa/view.gif" />
				</ufrn:link>
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>