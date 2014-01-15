<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<%@page import="br.ufrn.sigaa.pesquisa.dominio.TipoStatusPlanoTrabalho"%>
<h2 class="tituloPagina">
	<ufrn:subSistema></ufrn:subSistema> >
	Meus Planos de Trabalho
</h2>

<style>
	table.listagem tr.cota td {
		background-color: #C4D2EB;
		padding: 8px 10px 2px;
		border-bottom: 1px solid #BBB;
		font-variant: small-caps;

		font-style: italic;
	}
</style>

	<div class="infoAltRem">
		<html:img page="/img/pesquisa/info_discente.gif" style="overflow: visible;"/>
		 : Consultar dados do bolsista
		<html:img page="/img/email_go.png" style="overflow: visible;"/>
	 	 : Enviar Mensagem ao bolsista
		<html:img page="/img/pesquisa/report_go.png" style="overflow: visible;"/>
		 : Resubmeter Plano de Trabalho
		 <html:img page="/img/pesquisa/finalizar_projeto.png" style="overflow: visible;"/>
		 : Finalizar Plano de Trabalho
		 <br/>
		<html:img page="/img/pesquisa/view.gif" style="overflow: visible;"/>
		 : Visualizar Plano de Trabalho
		<html:img page="/img/alterar.gif" style="overflow: visible;"/>
		 : Alterar Plano de Trabalho
		<html:img page="/img/delete.gif" style="overflow: visible;"/>
		 : Remover Plano de Trabalho
		<%--<html:img page="/img/alterar_old.gif" style="overflow: visible;"/>
		 : Mudar a modalidade da bolsa--%>
	</div>

<table class="listagem">
	<caption>Planos de Trabalho</caption>
	<thead>
		<tr>
			<th> Projeto </th>
			<th> Discente </th>
			<th> Tipo de Bolsa </th>
			<th> Período </th>
			<th> Status </th>
			<th> </th>
		</tr>
	</thead>
	<tbody>
		<c:set var="ano"/>

		<c:forEach var="plano" items="${ planos }" varStatus="loop">

		<c:if test="${plano.projetoPesquisa.codigo.ano != ano}">
			<c:set var="ano" value="${ plano.projetoPesquisa.codigo.ano }" />
			<tr class="cota">
				<td colspan="6">
					Projetos de ${ ano }
				</td>
			</tr>
		</c:if>

		<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
			<td colspan="5">
				<i>
				<ufrn:format name="plano" property="titulo" type="texto" length="150"/>
				</i>
			</td>
			<td align="center" rowspan="2">
				<c:if test="${ plano.aguardandoResubmissao }">
					<html:link action="/pesquisa/planoTrabalho/wizard?dispatch=edit&id=${plano.id}">
						<img src="${ctx}/img/pesquisa/report_go.png" alt="Resubmeter Plano de Trabalho" title="Resubmeter Plano de Trabalho"/>
					</html:link>
				</c:if>

				<c:if test="${ not empty plano.membroProjetoDiscente and not plano.membroProjetoDiscente.inativo}">
					<html:link action="/pesquisa/buscarMembroProjetoDiscente?dispatch=view&idMembroDiscente=${plano.membroProjetoDiscente.id}" >
						<img src="${ctx}/img/pesquisa/info_discente.gif" alt="Consultar Dados do Discente" title="Consultar dados do discente" />
					</html:link>
				</c:if>
				
				<html:link action="/pesquisa/planoTrabalho/wizard?dispatch=enviarMensagemBolsista&idDiscente=${plano.membroProjetoDiscente.discente.id}">
						<img src="${ctx}/img/email_go.png" alt="Enviar Mensagem ao bolsista" title="Enviar Mensagem ao bolsista"/>
				</html:link>
				
				<c:if test="${ plano.finalizarPlanoSemCota }">
					<html:link action="/pesquisa/finalizarPlanosTrabalho?dispatch=finalizar&obj.id=${plano.id}" onclick="confirm();">
						<img src="${ctx}/img/pesquisa/finalizar_projeto.png" alt="Finalizar Plano de Trabalho" title="Finalizar Plano de Trabalho"/>
					</html:link>
				</c:if>

				<html:link action="/pesquisa/planoTrabalho/wizard?dispatch=view&obj.id=${plano.id}">
					<img src="${ctx}/img/pesquisa/view.gif" alt="Visualizar Plano de Trabalho" title="Visualizar Plano de Trabalho"/>
				</html:link>
				
				<c:if test="${ (plano.cadastrado and periodoSubmissao) or not plano.tipoBolsa.vinculadoCota }">
					<html:link action="/pesquisa/planoTrabalho/wizard?dispatch=edit&id=${plano.id}">
						<img src="${ctx}/img/alterar.gif" alt="Alterar Plano de Trabalho" title="Alterar Plano de Trabalho"/>
					</html:link>
				
					<html:link action="/pesquisa/planoTrabalho/wizard?dispatch=remove&id=${plano.id}">
						<img src="${ctx}/img/delete.gif" alt="Remover Plano de Trabalho" title="Remover Plano de Trabalho"/>
					</html:link>
				</c:if>
<%-- retirando link temporariamente até obter uma definição da PROPESQ
	de como vai ficar a mudança de uma bolsa SEM cota para uma bolsa COM cota
	já que os períodos podem ser diferentes e isso implicaria em uma mudança no
	cronograma do plano de trabalho  				
				<c:if test="${ plano.passivelMudarTipoBolsa }">
					<html:link action="/pesquisa/mudarTipoBolsa?dispatch=popular&obj.id=${plano.id}">
						<img src="${ctx}/img/alterar_old.gif" alt="Mudar a modalidade da bolsa" title="Mudar a modalidade da bolsa"/>
					</html:link>
				</c:if>
--%>
			</td>
		</tr>

		<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar"  }">
			<td>
				<html:link action="/pesquisa/projetoPesquisa/buscarProjetos?dispatch=view&id=${plano.projetoPesquisa.id}">
				${ plano.projetoPesquisa.codigo }
				</html:link>
			</td>
			<td>
				<c:choose>
					<c:when test="${ not empty plano.membroProjetoDiscente and not plano.membroProjetoDiscente.inativo }">
						${ plano.membroProjetoDiscente.discente.pessoa.nome }
					</c:when>
					<c:otherwise>
						<em> não definido </em>
					</c:otherwise>
				</c:choose>
			</td>
			<td> ${ plano.tipoBolsaString } </td>
			<td>
				<ufrn:format type="data" name="plano" property="dataInicio" /> a
				<ufrn:format type="data" name="plano" property="dataFim" />
			</td>
			<td> ${ plano.statusString } </td>
		</tr>
		<c:if test="${ plano.aguardandoResubmissao }">
			<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar"  }">
				<td colspan="6">
					<h5 style="text-align: center"> Parecer do Avaliador <em>(emitido em <ufrn:format type="data" name="plano" property="dataAvaliacao" />)</em> </h5>
					<p style="padding: 5px 10px; text-indent: 3em;">
						${plano.parecerConsultor}
					</p>
				</td>
			</tr>
		</c:if>
		</c:forEach>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="6" style="text-align: center; font-weight: bold;	">
				${ fn:length(planos) } planos de trabalho encontrados
			</td>
		</tr>
	</tfoot>
</table>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>