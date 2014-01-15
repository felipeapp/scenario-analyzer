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

	<h2>Relatório de Desempenho de Bolsistas</h2>

		<div id="parametrosRelatorio">
			<table>
				<c:if test="${ not empty relatoriosSaeMBean.nivel }">
				<tr>
					<th>Nível de Ensino: </th>
					<td>${ relatoriosSaeMBean.nivelDescricao }</td>
				</tr>
				</c:if>
				<c:if test="${ not empty relatoriosSaeMBean.curso }">
				<tr>
					<th>Curso:</th>
					<td> 
						${ relatoriosSaeMBean.curso.descricao }
					</td>
				</tr>
				</c:if>
				<tr>
					<th>Tipo de Bolsa:</th>
					<td>${ relatoriosSaeMBean.tipoBolsaAuxilio.denominacao}</td>
				</tr>
				<tr>
					<th>Ano-Período sem a bolsa:</th>
					<td> 
						${ relatoriosSaeMBean.anoSem }.${ relatoriosSaeMBean.periodoSem }
					</td>
				</tr>
				<tr>
					<th>Ano-Período com a bolsa: </th>
					<td>${relatoriosSaeMBean.ano}.${relatoriosSaeMBean.periodo}</td>
				</tr>
				<c:if test="${not empty relatoriosSaeMBean.matricula}">
					<tr>
						<th>Matrícula do Discente: </th>
						<td>${relatoriosSaeMBean.matricula}</td>
					</tr>
				</c:if>
				<c:if test="${not empty relatoriosSaeMBean.nome}">
					<tr>
						<th>Nome do Discente: </th>
						<td>${fn:toUpperCase(relatoriosSaeMBean.nome)} </td>
					</tr>
				</c:if>
			</table>
		</div>
		<BR><BR>
		<c:set var="totalAprovadoSem" value="0" />
		<c:set var="totalTrancadoSem" value="0"/>
		<c:set var="totalReprovadoSem" value="0"/>
		<c:set var="totalAprovadoCom" value="0"/>
		<c:set var="totalTrancadoCom" value="0"/>
		<c:set var="totalReprovadoCom" value="0"/>
		<c:set var="totalSem" value="0"/>
		<c:set var="totalCom" value="0"/>
		<c:set var="disciplinasSem" value="0"/>
		<c:set var="disciplinasCom" value="0"/>
		<table class="tabelaRelatorioBorda" width="100%" id="tabelaDesempenho">	
			<thead>
				<tr>
					<th rowspan="2" class="alinharCentro">Matrícula</th>
					<th rowspan="2" class="alinharEsquerda">Discente</th>
					<th colspan="3" class="alinharCentro">Ano-Período Sem Bolsa</th>				
					<th colspan ="3" class="alinharCentro">Ano-Período Com Bolsa</th>
				</tr>
				<tr>
					
					<th class="alinharDireita" width="10%">Aprovado</th>
					<th class="alinharDireita" width="10%" >Trancado</th>
					<th class="alinharDireita" width="10%">Reprovado</th>
					<th class="alinharDireita" width="10%" >Aprovado</th>
					<th class="alinharDireita" width="10%">Trancado</th>
					<th class="alinharDireita" width="10%">Reprovado</th>
				</tr>
			</thead>
		<tbody>
		<c:forEach var="item" items="#{relatoriosSaeMBean.listaDesempenhoDiscentes}">
			<tr>
				<c:set var="disciplinasSem" value="#{item.aprovadoSem + item.trancadoSem + item.reprovadoSem}"/>
				<c:set var="disciplinasCom" value="#{item.aprovadoCom + item.trancadoCom + item.reprovadoCom}"/>
					
					<td class="alinharCentro" width="5%"> ${item.matricula} </td>
					<td class="alinharEsquerda" width="25%"> ${item.discente} </td>										
					
					<td class="alinharDireita" width="10%"> ${item.aprovadoSem} 
						<c:if test="${disciplinasSem == 0 }">(0,00%)</c:if>			
						<c:if test="${disciplinasSem > 0 }">
							(<ufrn:format type="valor" valor="${(item.aprovadoSem/disciplinasSem)*100}"/>%)
						</c:if>
					</td>
					<td class="alinharDireita" width="10%"> ${item.trancadoSem}
					<c:if test="${disciplinasSem == 0 }">(0,00%)</c:if>			
						<c:if test="${disciplinasSem > 0 }">
							(<ufrn:format type="valor" valor="${(item.trancadoSem/disciplinasSem)*100}"/>%)
						</c:if>
					</td>
					<td class="alinharDireita" width="10%"> ${item.reprovadoSem} 
					<c:if test="${disciplinasSem == 0 }">(0,00%)</c:if>			
						<c:if test="${disciplinasSem > 0 }">
							(<ufrn:format type="valor" valor="${(item.reprovadoSem/disciplinasSem)*100}"/>%)
						</c:if>
					</td>
					<td class="alinharDireita" width="10%"> ${item.aprovadoCom}
						<c:if test="${disciplinasCom == 0 }">(0,00%)</c:if>			
						<c:if test="${disciplinasCom > 0 }">
							(<ufrn:format type="valor" valor="${(item.aprovadoCom/disciplinasCom)*100}"/>%)
						</c:if>
					</td>
					<td class="alinharDireita" width="10%"> ${item.trancadoCom} 
					<c:if test="${disciplinasCom == 0 }">(0,00%)</c:if>			
						<c:if test="${disciplinasCom > 0 }">
							(<ufrn:format type="valor" valor="${(item.trancadoCom/disciplinasCom)*100}"/>%)
						</c:if>
					</td>
					<td class="alinharDireita" width="10%"> ${item.reprovadoCom}
					<c:if test="${disciplinasCom == 0 }">(0,00%)</c:if>			
						<c:if test="${disciplinasCom > 0 }">
							(<ufrn:format type="valor" valor="${(item.reprovadoCom/disciplinasCom)*100}"/>%)
						</c:if>
					</td>
											
					<c:set var="totalAprovadoSem" value="#{totalAprovadoSem + item.aprovadoSem }" />
					<c:set var="totalTrancadoSem" value="#{totalTrancadoSem + item.trancadoSem  }" />
					<c:set var="totalReprovadoSem" value="#{totalReprovadoSem + item.reprovadoSem  }" />
					
					<c:set var="totalAprovadoCom" value="#{totalAprovadoCom + item.aprovadoCom }" />
					<c:set var="totalTrancadoCom" value="#{totalTrancadoCom + item.trancadoCom }" />
					<c:set var="totalReprovadoCom" value="#{totalReprovadoCom + item.reprovadoCom}" />

				</tr>
			</c:forEach>
		</tbody>
		</table>
		<br>
		<table align="center">
			<tr>
				<td colspan="9">
					 <b>Discente(s) encontrado(s): ${fn:length(relatoriosSaeMBean.listaDesempenhoDiscentes)}</b> 
				</td>
			</tr>
		</table>
		<br><br><br>
		<c:set var="totalCom" value="#{totalReprovadoCom + totalTrancadoCom + totalAprovadoCom  }" />
		<c:set var="totalSem" value="#{totalReprovadoSem + totalTrancadoSem + totalAprovadoSem  }" />
		<table class="tabelaRelatorioBorda" width="100%" id="totaisGerais">	
			<thead>
				<tr><th colspan="6" class="alinharCentro">Totais Gerais</th></tr>
				<tr>
					<th colspan="3" class="alinharCentro">Total Sem Bolsa</th>				
					<th colspan ="3" class="alinharCentro">Total Com Bolsa</th>
				</tr>
				<tr>
					<th class="alinharDireita">Aprovado</th>
					<th class="alinharDireita">Trancado</th>
					<th class="alinharDireita">Reprovado</th>
					<th class="alinharDireita">Aprovado</th>
					<th class="alinharDireita">Trancado</th>
					<th class="alinharDireita">Reprovado</th>
				</tr>
			</thead>
			<tbody>
					<tr>
						<td class="alinharDireita"> ${totalAprovadoSem} 
						<c:if test="${totalSem == 0 }">(0,00%)</c:if>	
						<c:if test="${totalSem > 0 }"> 
							(<ufrn:format type="valor" valor="${(totalAprovadoSem/totalSem)*100}"/>%)
						</c:if>
						</td>
						<td class="alinharDireita"> ${totalTrancadoSem}
						<c:if test="${totalSem == 0 }">(0,00%)</c:if>	
						<c:if test="${totalSem > 0 }"> 
							(<ufrn:format type="valor" valor="${(totalTrancadoSem/totalSem)*100}"/>%)
						</c:if>
						</td>
						<td class="alinharDireita"> ${totalReprovadoSem}
						<c:if test="${totalSem == 0 }">(0,00%)</c:if>	
						<c:if test="${totalSem > 0 }"> 
								(<ufrn:format type="valor" valor="${(totalReprovadoSem/totalSem)*100}"/>%)
						</c:if>
						</td>
						<td class="alinharDireita"> ${totalAprovadoCom}
							<c:if test="${totalCom == 0 }">(0,00%)</c:if>	
								<c:if test="${totalCom > 0 }"> 
									(<ufrn:format type="valor" valor="${(totalAprovadoCom/totalCom)*100}"/>%)
								</c:if>
						</td>
						<td class="alinharDireita"> ${totalTrancadoCom}
						<c:if test="${totalCom == 0 }">(0,00%)</c:if>	
							<c:if test="${totalCom > 0 }"> 
								(<ufrn:format type="valor" valor="${(totalTrancadoCom/totalCom)*100}"/>%)
							</c:if>
						</td>
						<td class="alinharDireita"> ${totalReprovadoCom} 
							<c:if test="${totalCom == 0 }">(0,00%)</c:if>	
							<c:if test="${totalCom > 0 }">
								(<ufrn:format type="valor" valor="${(totalReprovadoCom/totalCom)*100}"/>%)
							</c:if>
						</td>
					</tr>
			</tbody>
		</table>	
		<br><br> 
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>