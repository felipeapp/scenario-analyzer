<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<html:link action="/verMenuPesquisa">Pesquisa</html:link> &gt;
	Finalização de Planos de Trabalho
</h2>

<html:form action="/pesquisa/finalizarPlanosTrabalho" method="post">
<html:hidden property="obj.cota.id"/>

	<table class="listagem"">
		<caption>Cota ${formPlanoTrabalho.obj.cota.descricaoCompleta}</caption>
		<thead>
			<tr>
				<th> Orientador </th>
				<th> Aluno </th>
				<th> Modalidade </th>
			</tr>
		</thead>
		<tbody>
			<c:forEach var="plano" items="${planos}" varStatus="loop">
			<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
				<td> ${ plano.orientador.pessoa.nome }</td>
				<td>
					${ plano.membroProjetoDiscente.discente.pessoa.nome }
					<c:if test="${empty plano.membroProjetoDiscente.discente.pessoa.nome }">
						<em> indefindo </em>
					</c:if>
				</td>
				<td> ${ plano.tipoBolsaString }</td>
			</tr>
			</c:forEach>
		</tbody>

		<tfoot>
			<tr>
				<td colspan="3" align="center">
					<i><b> ${fn:length(planos)} planos de trabalho em andamento </b></i>
				</td>
			</tr>
			<tr>
				<td colspan="3" align="center">
					<html:button dispatch="confirmar" value="Confirmar Finalização" />
					<html:button dispatch="iniciar" value="<< Selecionar Outra Cota" />
					<html:button dispatch="cancelar" value="Cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>
</html:form>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>