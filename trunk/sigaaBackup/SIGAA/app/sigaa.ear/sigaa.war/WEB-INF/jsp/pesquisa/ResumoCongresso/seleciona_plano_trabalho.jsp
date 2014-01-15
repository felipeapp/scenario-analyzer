<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2 class="tituloPagina">
	<ufrn:subSistema /> >
	Submiss�o de Resumo para Congresso de Inicia��o Cient�fica
</h2>

<style>
	table.listagem tr.orientador td {
		background-color: #C4D2EB;
		padding: 8px 10px 2px;
		border-bottom: 1px solid #BBB;
		font-variant: small-caps;

		font-style: italic;
	}
</style>

<div class="descricaoOperacao">
	<h3 style="text-align: center; margin-bottom: 15px;"> ${ congresso_.edicao } Congresso de Inicia��o Cient�fica </h3>
	<p>
		<b>Per�odo do Congresso:</b>
		<ufrn:format type="data" name="congresso_" property="inicio" /> a
		<ufrn:format type="data" name="congresso_" property="fim" />
	</p>
	<p>
	Selecione um plano de trabalho a partir do qual voc� deseja popular seu resumo para o CIC  
	ou, se preferir, clique no bot�o ao final da p�gina para submeter um resumo independente.
	</p>
	<p>
	Lembrando que voc� s� pode enviar <strong>UM</strong> �nico resumo como autor por congresso de inicia��o cient�fica.
	</p>
	<p>
	Se o seu plano de trabalho n�o est� listado abaixo, verifique se ele atende a uma das seguintes restri��es:
	</p>
	<ul>
		<c:forEach var="r" items="${ congresso_.restricoes }">
			<li>${ r.descricao }</li>
		</c:forEach>
	</ul>
</div>

<div class="infoAltRem">
	<html:img page="/img/pesquisa/view.gif" style="overflow: visible;"/>
	 : Visualizar Plano de Trabalho
	<html:img page="/img/seta.gif" style="overflow: visible;"/>
	 : Enviar Resumo
</div>

<table class="listagem">
	<caption>Planos de Trabalho</caption>
	<thead>
		<tr>
			<th> Projeto de Pesquisa </th>
			<th> Discente Ativo</th>
			<th> Modalidade da Bolsa </th>
			<th> Status </th>
			<th> </th>
			<th> </th>
		</tr>
	</thead>
	<tbody>
		<c:set var="orientador"/>

		<c:forEach var="plano" items="${ planos }" varStatus="loop">

		<c:if test="${plano.orientador.nome != orientador}">
			<c:set var="orientador" value="${ plano.orientador.nome }" />
			<tr class="orientador">
				<td colspan="6">
					<b>Orientador:</b> ${orientador}
				</td>
			</tr>
		</c:if>

		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar"  }">
			<td>
				<html:link action="/pesquisa/projetoPesquisa/criarProjetoPesquisa?&dispatch=view&id=${ plano.projetoPesquisa.id }">
				${ plano.projetoPesquisa.codigo }
				</html:link>
			</td>
			<td>
				<c:choose>
					<c:when test="${ not empty plano.membroProjetoDiscente }">
						${ plano.membroProjetoDiscente.discente.pessoa.nome }
					</c:when>
					<c:otherwise>
						<em> n�o definido </em>
					</c:otherwise>
				</c:choose>
			</td>
			<td> ${ plano.tipoBolsaString } </td>
			<td> ${ plano.statusString } </td>
			<td nowrap="nowrap">
				<html:link action="/pesquisa/planoTrabalho/wizard?dispatch=view&obj.id=${plano.id}">
					<img src="${ctx}/img/pesquisa/view.gif" alt="Visualizar Plano de Trabalho" title="Visualizar Plano de Trabalho"/>
				</html:link>
			</td>
			<td nowrap="nowrap">
				<html:link action="/pesquisa/resumoCongresso.do?dispatch=iniciarEnvio&idPlanoTrabalho=${plano.id}">
					<img src="${ctx}/img/seta.gif" alt="Enviar Resumo" title="Enviar Resumo"/>
				</html:link>
			</td>
		</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="6" style="text-align: center; font-weight: bold;">
				<c:if test="${usuario.discenteAtivo.graduacao}">
					<html:form action="/pesquisa/resumoCongresso.do?dispatch=iniciarEnvio">
						<html:submit>Submeter Resumo Independente</html:submit>
					</html:form>
				</c:if>
			</td>
		</tr>
	</tfoot>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>