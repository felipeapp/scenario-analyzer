<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<ufrn:subSistema></ufrn:subSistema> &gt;
	<c:out value="Parecer de Relatórios Parciais de Discentes"/>
</h2>

<table class="listagem">
	<caption>Relatórios Parciais dos Planos de Trabalho em Andamento</caption>
	<thead>
		<tr>
			<th> Discente </th>
			<th> Projeto de Pesquisa</th>
			<th> Data de Envio </th>
			<th> Parecer Emitido? </th>
			<th> </th>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="_relatorio" items="${relatorios}" varStatus="status">
		<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
			<td>
				${_relatorio.membroDiscente.discente.matricula} <br/>
				${_relatorio.membroDiscente.discente.pessoa.nome}
			</td>
			<td> ${_relatorio.planoTrabalho.projetoPesquisa.codigo} </td>
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
				<ufrn:link action="/pesquisa/relatorioBolsaParcial" param="idRelatorio=${_relatorio.id}&dispatch=selecionarBolsista">
					<img src="${ctx}/img/pesquisa/avaliar.gif" alt="Emitir Parecer" title="Emitir Parecer"/>
				</ufrn:link>
			</td>
		</tr>
		</c:forEach>
	</tbody>
</table>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>