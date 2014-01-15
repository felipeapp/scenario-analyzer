<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Relatórios Finais </h2>

<f:view>

	<h:form id="formRelatorioFinal">
	  <c:choose>
			<c:when test="${ not empty portalConsultorMBean.relatoriosEspecial }">
	
				<center>
					<div class="infoAltRem">
				        <img src="${ctx}/img/view.gif" alt="Avaliar Projeto" title="Avaliar Projeto"/> Visualizar Relatório
					</div>
				</center>
	
				<table class="listagem">
					<thead>
						<tr>
							<th> Título do Plano de Trabalho </th>
							<th> Tipo de Bolsa </th>
							<th> Parecer Emitido? </th>
							<th> </th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td colspan="4" class="subFormulario"> Lista de Relatórios Finais de IC para Consulta </td>
						</tr>
						<c:forEach var="relatorioFinal" items="${ portalConsultorMBean.relatoriosEspecial }" varStatus="loop">
							<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
								<td> ${ relatorioFinal.planoTrabalho.titulo } </td>
								<td> ${ relatorioFinal.planoTrabalho.tipoBolsa.descricao } </td>
								<td> ${ relatorioFinal.parecerEmitidoString } </td>
								<td align="center">
									<ufrn:link action="/pesquisa/relatorioBolsaFinal" param="idRelatorio=${relatorioFinal.id}&dispatch=view">
										<img src="${ctx}/img/view.gif" alt="Visualizar Relatório" title="Visualizar Relatório"/>
									</ufrn:link>
								</td>
							</tr>
						</c:forEach>
					</tbody>
				</table>
			</c:when>						
			<c:otherwise>
				<p class="vazio">
					Não há relatórios finais de bolsa para consulta
				</p>
			</c:otherwise>
		</c:choose>
	</h:form>
  
  </f:view>
  
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>