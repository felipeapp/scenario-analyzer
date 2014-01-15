<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<ufrn:subSistema /> &gt; Relatórios Finais de Iniciação Científica
</h2>

<table class="listagem">
	<caption>Meus Relatórios Finais de Iniciação Científica</caption>
	<thead>
		<tr>
			<th> Cota </th>
			<th> Orientador </th>
			<th> Última modificação em </th>
			<th> Submetido? </th>
			<th> Parecer Emitido? </th>
			<th> </th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="relatorio" items="${relatorios}" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
			<td>
				${ relatorio.planoTrabalho.cota.descricao }
			</td>
			<td> ${relatorio.planoTrabalho.orientador.pessoa.nome} </td>
			<td nowrap="nowrap"> <ufrn:format type="dataHora" name="relatorio" property="dataEnvio"/></td>
			<td align="center">
				<ufrn:format type="simnao" valor="${relatorio.enviado}"></ufrn:format>
			</td>
			<td align="center">
				<c:choose>
					<c:when test="${relatorio.parecerOrientador != null}">
						Sim
					</c:when>
					<c:otherwise>Não</c:otherwise>
				</c:choose>
			</td>
			<td>
				<ufrn:link action="/pesquisa/relatorioBolsaFinal" param="idRelatorio=${relatorio.id}&dispatch=view">
					<img src="${ctx}/img/pesquisa/view.gif" />
				</ufrn:link>
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>