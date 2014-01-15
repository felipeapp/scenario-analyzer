<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Projetos Pendentes </h2>

<f:view>

	<h:form id="formProjeto" >
    	<c:choose>
				<c:when test="${ not empty portalConsultorMBean.avaliacoesProjetoPendetes }">

				<center>
					<div class="infoAltRem">
				        <img src="${ctx}/img/pesquisa/avaliar.gif" alt="Avaliar Projeto" title="Avaliar Projeto"/> Avaliar Projeto de Pesquisa
					</div>
				</center>
					<table class="listagem">
						<caption> Projetos Pendentes de Avalia��o </caption>
						<thead>
							<tr>
								<th> C�digo </th>
								<th> T�tulo </th>
								<th style="text-align: center"> Avalia��o </th>
								<th> Avaliar </th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="avaliacao" items="${ portalConsultorMBean.avaliacoesProjetoPendetes }" varStatus="loop">
							<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<td> ${avaliacao.projetoPesquisa.codigo } </td>
								<td> ${avaliacao.projetoPesquisa.titulo } </td>
								<td class="avaliacao"> PENDENTE </td>
								<td align="center">
									<ufrn:link action="pesquisa/avaliarProjetoPesquisa" param="obj.id=${avaliacao.id}&dispatch=popular" aba="lista-projetos">
										<img src="${ctx}/img/pesquisa/avaliar.gif" alt="Avaliar Projeto" title="Avaliar Projeto"/>
									</ufrn:link>
								</td>
							</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					<p class="vazio">
						N�o foram destinadas avalia��es de projetos de pesquisa a voc�.
					</p>
				</c:otherwise>
		</c:choose>

	</h:form>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>