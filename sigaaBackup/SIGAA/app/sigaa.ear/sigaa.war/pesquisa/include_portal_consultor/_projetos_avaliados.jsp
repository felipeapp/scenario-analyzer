<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Projetos Avaliados </h2>

<f:view>

	<h:form id="formProjetoAvaliado" >
    	<c:choose>
				<c:when test="${ not empty portalConsultorMBean.avaliacoesProjetoAvaliado }">

					<center>
						<div class="infoAltRem">
					        <img src="${ctx}/img/pesquisa/avaliar.gif" alt="Avaliar Projeto" title="Avaliar Projeto"/> Avaliar Projeto de Pesquisa
						</div>
					</center>

					<table class="listagem">
						<thead>
							<tr>
								<th> Código </th>
								<th> Título </th>
								<th style="text-align: center"> Avaliação </th>
								<th> Avaliar </th>
							</tr>
						</thead>
						<tbody>
						<c:if test="${not empty projetos}">
							<tr>
								<td colspan="4" class="subFormulario"> Projetos Pendentes de Avaliação e Projetos Avaliados </td>
							</tr>
						</c:if>
							<c:forEach var="avaliacao" items="${ portalConsultorMBean.avaliacoesProjetoAvaliado }" varStatus="loop">
							<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<td> ${avaliacao.projetoPesquisa.codigo } </td>
								<td> ${avaliacao.projetoPesquisa.titulo } </td>
								<td class="avaliacao">
									<span class="nota ${ avaliacao.media > 5 ? "recomendado" : "n-recomendado" }">
										<ufrn:format type="decimal" name="avaliacao" property="media"/>
									</span>
								<span class="data"> (em <ufrn:format type="data" name="avaliacao" property="dataAvaliacao" /> ) </span>
								
								</td>
								<td align="center">
									<ufrn:link action="pesquisa/avaliarProjetoPesquisa" param="obj.id=${avaliacao.id}&dispatch=popular" aba="lista-projetos">
										<img src="${ctx}/img/pesquisa/avaliar.gif" alt="Avaliar Projeto" title="Avaliar Projeto de Pesquisa"/>
									</ufrn:link>
								</td>
							</tr>
							</c:forEach>
						</tbody>
					</table>
					</c:when>
					<c:otherwise>
						<p class="vazio">
							Não foram destinadas avaliações de projetos de pesquisa a você
						</p>
					</c:otherwise>
		</c:choose>
	</h:form>
  
  </f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>