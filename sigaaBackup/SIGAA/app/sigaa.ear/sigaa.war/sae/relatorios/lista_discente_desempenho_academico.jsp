<%@taglib uri="/tags/a4j" prefix="a4j"%>
<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	.alinharDireita{ 
		text-align:right !important;
	}
	.alinharEsquerda{ 
		text-align:left !important;
	} 
	.alinharCentro{ 
		text-align:center !important;
	}
	.destacado{
		color: red;
	}
	tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
</style>

<f:view>
<h:form>
	<h2>Relatório de Desempenho de Bolsistas</h2>

		<div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Ano-Período:</th>
					<td> 
						${ relatoriosSaeMBean.ano }.${ relatoriosSaeMBean.periodo }
					</td>
				</tr>
			</table>
		</div>
		<BR><BR>
		<table class="tabelaRelatorioBorda" width="100%" id="tabelaDesempenho">	
			<thead>
				<tr>
					<th rowspan="2" class="alinharEsquerda">Tipo Bolsa</th>
					<th rowspan="2" class="alinharDireita">Total Bolsista</th>
					<th rowspan="2" class="alinharDireita">Total Prioritários</th>
					<th colspan="2" class="alinharCentro">Total de Reprovados</th>				
					<th colspan="2" class="alinharCentro">% com Trancamento</th>
					<th colspan="2" class="alinharCentro">% sem Reprovações e/ou Trancamentos</th>
				</tr>
				<tr>
					
					<th class="alinharDireita" width="10%">P</th>
					<th class="alinharDireita" width="10%">NP</th>
					<th class="alinharDireita" width="10%">P</th>
					<th class="alinharDireita" width="10%">NP</th>
					<th class="alinharDireita" width="10%">P</th>
					<th class="alinharDireita" width="10%">NP</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="item" items="#{ relatoriosSaeMBean.desempenhoDiscentes }" varStatus="status">
					<tr>

						<td> ${ item.bolsa } </td>
						<td style="text-align: right;"> 
							<c:choose>
								<c:when test="${ item.totalBolsistas == 0 }">
									<ufrn:format type="valorint" valor="${ item.totalBolsistas }"/>
								</c:when>
								<c:otherwise>
									<h:commandLink action="#{relatoriosSaeMBean.viewTotalBolsistas}" >
										<ufrn:format type="valorint" valor="${ item.totalBolsistas }"/>
										<f:param name="idLinha" value="#{ item.linha }"/>
										<f:param name="idColuna" value="0"/>
									</h:commandLink>
								</c:otherwise>
							</c:choose>
 					    </td>
						<td style="text-align: right;">
							<c:choose>
								<c:when test="${ item.totalPrioritarios == 0 }">
									<ufrn:format type="valorint" valor="${ item.totalPrioritarios }"/>
								</c:when>
								<c:otherwise>
									<h:commandLink action="#{relatoriosSaeMBean.viewTotalBolsistas}" >
										<ufrn:format type="valorint" valor="${ item.totalPrioritarios }"/>
										<f:param name="idColuna" value="1"/>
										<f:param name="idLinha" value="#{ item.linha }"/>
									</h:commandLink>
								</c:otherwise>
							</c:choose>
 					    </td>
						<td style="text-align: right;">
							<c:choose>
								<c:when test="${ item.totalReprovadosP == 0 }">
									<ufrn:format type="valorint" valor="${ item.totalReprovadosP }"/>%
								</c:when>
								<c:otherwise>
									<h:commandLink action="#{relatoriosSaeMBean.viewTotalBolsistas}" >
										<ufrn:format type="valor1" valor="${ item.totalReprovadosP }"/>%
										<f:param name="idColuna" value="2"/>
										<f:param name="idLinha" value="#{ item.linha }"/>
									</h:commandLink>
								</c:otherwise>
							</c:choose>
 					    </td>
						<td style="text-align: right;">
							<c:choose>
								<c:when test="${ item.totalReprovadosNP == 0 }">
									<ufrn:format type="valorint" valor="${ item.totalReprovadosNP }"/>%
								</c:when>
								<c:otherwise>
									<h:commandLink action="#{relatoriosSaeMBean.viewTotalBolsistas}" >
										<ufrn:format type="valor1" valor="${ item.totalReprovadosNP }"/>%
										<f:param name="idColuna" value="3"/>
										<f:param name="idLinha" value="#{ item.linha }"/>
									</h:commandLink>
								</c:otherwise>
							</c:choose>
 					    </td>
						<td style="text-align: right;">
							<c:choose>
								<c:when test="${ item.totalTrancadosP == 0 }">
									<ufrn:format type="valorint" valor="${ item.totalTrancadosP }"/>%
								</c:when>
								<c:otherwise>
									<h:commandLink action="#{relatoriosSaeMBean.viewTotalBolsistas}" >
										<ufrn:format type="valor1" valor="${ item.totalTrancadosP }"/>%
										<f:param name="idColuna" value="4"/>
										<f:param name="idLinha" value="#{ item.linha }"/>
									</h:commandLink>
								</c:otherwise>
							</c:choose>
 					    </td>
						<td style="text-align: right;">
							<c:choose>
								<c:when test="${ item.totalTrancadosNP == 0 }">
									<ufrn:format type="valorint" valor="${ item.totalTrancadosNP }"/>%
								</c:when>
								<c:otherwise>
									<h:commandLink action="#{relatoriosSaeMBean.viewTotalBolsistas}" >
										<ufrn:format type="valor1" valor="${ item.totalTrancadosNP }"/>%
										<f:param name="idColuna" value="5"/>
										<f:param name="idLinha" value="#{ item.linha }"/>
									</h:commandLink>
								</c:otherwise>
							</c:choose>
 					    </td>
						<td style="text-align: right;">
							<c:choose>
								<c:when test="${ item.totalTrancadosReprovadosP == 0 }">
									<ufrn:format type="valorint" valor="${ item.totalTrancadosReprovadosP }"/>%
								</c:when>
								<c:otherwise>
									<h:commandLink action="#{relatoriosSaeMBean.viewTotalBolsistas}" >
										<ufrn:format type="valor1" valor="${ item.totalTrancadosReprovadosP }"/>%
										<f:param name="idColuna" value="6"/>
										<f:param name="idLinha" value="#{ item.linha }"/>
									</h:commandLink>
								</c:otherwise>
							</c:choose>
 					    </td>
						<td style="text-align: right;">
							<c:choose>
								<c:when test="${ item.totalTrancadosReprovadosNP == 0 }">
									<ufrn:format type="valorint" valor="${ item.totalTrancadosReprovadosNP }"/>%
								</c:when>
								<c:otherwise>
									<h:commandLink action="#{relatoriosSaeMBean.viewTotalBolsistas}" >
										<ufrn:format type="valor1" valor="${ item.totalTrancadosReprovadosNP }"/>%
										<f:param name="idColuna" value="7"/>
										<f:param name="idLinha" value="#{ item.linha }"/>
									</h:commandLink>
								</c:otherwise>
							</c:choose>
 					    </td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>