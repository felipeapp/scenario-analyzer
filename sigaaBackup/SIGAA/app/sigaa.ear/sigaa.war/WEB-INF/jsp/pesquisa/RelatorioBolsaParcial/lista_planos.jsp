<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2>
	<ufrn:subSistema /> &gt; Envio de Relatório Parcial de Iniciação à Pesquisa
</h2>

<div class="descricaoOperacao">
	<p>
		<strong>Bem-vindo ao envio de relatórios parciais.</strong>
	</p>
	<br />
	<p>Abaixo estão listados os seus planos de trabalho em andamento.
		Clique na seta para acessar o formulário de envio do relatório parcial
		para o plano de trabalho escolhido.</p>
</div>

  <center>
	<div class="infoAltRem">
		<img src="${ctx}/img/seta.gif"/>: Enviar Relatório Parcial
	</div>
  </center>
	
<table class="listagem">
	<caption>Meus Planos de Trabalho</caption>
	<thead>
		<tr>
			<th> Título </th>
			<th> Orientador </th>
			<th> Cota </th>
			<th> Modalidade </th>
			<th style="text-align: center;"> Relatório submetido? </th>
			<th> </th>
		</tr>
	</thead>
	<tbody>

		<c:forEach var="plano" items="${ planos }" varStatus="loop">

		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar"  }">
			<td colspan="4">
				<em>
				<c:choose>
					<c:when test="${ not empty plano.titulo }">
						${ plano.titulo }
					</c:when>
					<c:otherwise> Título não informado  </c:otherwise>
				</c:choose>
				</em>
			</td>
			<td rowspan="2" style="text-align: center;">			
				<c:choose>
					<c:when test="${plano.relatorioBolsaParcial.enviado}">
						Sim
					</c:when>
					<c:otherwise>Não</c:otherwise>
				</c:choose>
			</td>
			<td nowrap="nowrap" rowspan="2">
				<html:link action="/pesquisa/relatorioBolsaParcial?dispatch=popularEnvio&obj.planoTrabalho.id=${plano.id}">
					<img src="${ctx}/img/seta.gif" alt="Enviar Relatório" title="Enviar Relatório"/>
				</html:link>
			</td>
		</tr>
		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar"  }">
			<td style="font-variant: small-caps;">
				Projeto
				<html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=view&id=${plano.projetoPesquisa.id}">
				${ plano.projetoPesquisa.codigo }
				</html:link>
			</td>
			<td>
				${ plano.orientador.pessoa.nome }
			</td>
			<td> ${ plano.cota.descricao } </td>
			<td> ${ plano.tipoBolsa.descricaoResumida } </td>

		</tr>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="6" style="text-align: center; font-weight: bold;	">
				${ fn:length(planos) } plano(s) de trabalho encontrado(s)
			</td>
		</tr>
	</tfoot>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>