<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Projetos Pendentes (Consultoria Especial) </h2>

<f:view>

	<h:form id="formProjeto" >
    	<c:choose>
				<c:when test="${ not empty portalConsultorMBean.avaliacoesProjetoEspeciaisPendetes }">

				<center>
					<div class="infoAltRem">
				        <img src="${ctx}/img/pesquisa/avaliar.gif" alt="Avaliar Projeto" title="Avaliar Projeto"/> Avaliar Projeto de Pesquisa
					</div>
				</center>

					<table class="listagem">
						<caption> Projetos Pendentes de Avaliação </caption>
						<thead>
							<tr>
								<th> Código </th>
								<th> Título </th>
								<th style="text-align: center"> Avaliação </th>
								<th> Avaliar </th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td colspan="4" class="subFormulario"> Projetos Pendentes de Avaliação (Consultoria Especial) </td>
							</tr>
							<c:forEach var="projeto" items="${ portalConsultorMBean.avaliacoesProjetoEspeciaisPendetes }" varStatus="loop">
							<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<td> ${projeto.codigo } </td>
								<td> ${projeto.titulo } </td>
									<td class="avaliacao">
									<c:set var="media" value="0"/>
									<c:set var="total" value="0"/>
									<c:forEach var="aval" items="${projeto.avaliacoesProjeto}">
										<c:if test="${ not empty aval.dataAvaliacao && empty aval.justificativa }">
											<c:set var="media" value="${media + aval.media}" />
											<c:set var="total" value="${total + 1}"/>
										</c:if>
									</c:forEach>
		
									<c:choose>
				 						<c:when test="${total > 0}">
											<c:set var="media" value="${ media / total }"/>
											<span class="nota ${ media > 5 ? "recomendado" : "n-recomendado" }">
												<ufrn:format type="decimal" name="media"/>
											</span>
											<span class="data">
												(${total} avaliaç${ total > 1 ? "ões" : "ão" })
											</span>
										</c:when> 
										<c:otherwise>
											PENDENTE
										</c:otherwise>
									</c:choose>
								</td>
								<td align="center">
									<ufrn:link action="pesquisa/avaliarProjetoPesquisa" param="idProjeto=${projeto.id}&dispatch=popularProjeto">
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
							Não foram destinadas avaliações de projetos de pesquisa a você.
						</p>
					</c:otherwise>
		</c:choose>

	</h:form>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>