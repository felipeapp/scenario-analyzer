<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> > Projetos Pendentes </h2>

<f:view>

	<h:form id="formRelatorio">
	  <c:choose>
				<c:when test="${ not empty portalConsultorMBean.relatoriosProjeto }">
	
					<center>
						<div class="infoAltRem">
					        <img src="${ctx}/img/pesquisa/avaliar.gif" alt="Avaliar Projeto" title="Avaliar Projeto"/> Avaliar Relat�rio
						</div>
					</center>
	
					<table class="listagem">
						<thead>
							<tr>
								<th> C�digo do Projeto </th>
								<th> Nome do Projeto </th>
								<th> Data de Envio </th>
								<th> Status da Avalia��o </th>
								<th> </th>
							</tr>
						</thead>
						<tbody>
							<tr>
								<td colspan="6" class="subFormulario"> Lista de Relat�rios para Avalia��o </td>
							</tr>
							<c:forEach var="_relatorio" items="${ portalConsultorMBean.relatoriosProjeto }" varStatus="loop">
								<tr class="${ loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td> ${ _relatorio.projetoPesquisa.codigo } </td>
									<td> ${ _relatorio.projetoPesquisa.titulo } </td>
									<td> ${ _relatorio.dataEnvio } </td>
									<td> ${ _relatorio.statusString } </td>
									<td align="center">
										<ufrn:link action="/pesquisa/avaliarRelatorioProjeto" param="obj.id=${_relatorio.id}&dispatch=edit">
											<img src="${ctx}/img/pesquisa/avaliar.gif" alt="Avaliar Relat�rio" title="Avaliar Relat�rio"/>
										</ufrn:link>
									</td>
								</tr>
							</c:forEach>
						</tbody>
					</table>
				</c:when>
				<c:otherwise>
					<p class="vazio">
						N�o foram destinadas avalia��es de relat�rios de projeto a voc�
					</p>
				</c:otherwise>
	  </c:choose>						
	
	</h:form>
  
  </f:view>
  
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>  