<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
 
 <h2> <ufrn:subSistema /> > Planos Pendentes </h2>
 
 <f:view>
 	<h:form id="formPlanos">
	 	<c:choose>
				<c:when test="${ not empty portalConsultorMBean.planoTrabalhoPendentes }">
	
					<center>
						<div class="infoAltRem">
					        <img src="${ctx}/img/pesquisa/avaliar.gif" alt="Avaliar Projeto" title="Avaliar Projeto"/> Avaliar Plano de Trabalho
						</div>
					</center>
	
					<table class="listagem">
						<thead>
							<tr>
								<th> Título </th>
								<th style="text-align: center"> Avaliação </th>
								<th style="text-align: center"> Avaliar </th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td colspan="3" class="subFormulario"> Planos de Trabalho Pendentes de Avaliação </td>
							</tr>
							<c:forEach var="plano" items="${ portalConsultorMBean.planoTrabalhoPendentes }" varStatus="loop">
							<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<td> ${ plano.titulo } </td>
								<td align="center">
								    <c:choose>
								    	<c:when test="${plano.status == 3}"> <span class="recomendado"> </c:when>
								    	<c:when test="${plano.status == 4}"> <span class="n-recomendado"> </c:when>
								    	<c:otherwise> <span> </c:otherwise>
								    </c:choose>
									${ plano.statusString }
									</span>
								</td>
								<td align="center">
									<ufrn:link action="/pesquisa/avaliarPlanoTrabalho" param="obj.id=${plano.id}&dispatch=edit" aba="lista-planos">
										<img src="${ctx}/img/pesquisa/avaliar.gif" alt="Avaliar Plano de Trabalho" title="Avaliar Plano de Trabalho"/>
									</ufrn:link>
								</td>
							</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
			<c:otherwise>
				<p class="vazio">
					Não foram destinadas avaliações de planos de trabalho a você.
				</p>
			</c:otherwise>
		</c:choose>
 	</h:form>
  </f:view>
  
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>