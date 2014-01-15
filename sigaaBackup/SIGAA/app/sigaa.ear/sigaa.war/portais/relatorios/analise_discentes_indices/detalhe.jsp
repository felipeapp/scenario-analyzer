<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<f:view>
	<a4j:keepAlive beanName="relatorioAnaliseDiscentesPorIndiceMBean"/>
	<h:form>
	    <h2 class="tituloTabela"><b>Análise de Discentes por Índice Acadêmico (Detalhe)</b></h2>

    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Ano-Período:</th>
				<td>
					${relatorioAnaliseDiscentesPorIndiceMBean.ano}.${relatorioAnaliseDiscentesPorIndiceMBean.periodo}			
				</td>
			</tr>	 
			
			<c:if test="${relatorioAnaliseDiscentesPorIndiceMBean.nivel > 0}">
				<tr>
					<th>Nível: </th>
					<td>
						${relatorioAnaliseDiscentesPorIndiceMBean.nivel}			
					</td>		
				</tr>
			</c:if>		
					
			<c:if test="${relatorioAnaliseDiscentesPorIndiceMBean.curso.id > 0}">
				<tr>
					<th>Curso: </th>
					<td>
						${relatorioAnaliseDiscentesPorIndiceMBean.curso.descricao}			
					</td>		
				</tr>
			</c:if>				  

			<tr>
				<th>Índice Acadêmico: </th>
				<td>
					${relatorioAnaliseDiscentesPorIndiceMBean.indice.nome}			
				</td>		
			</tr>	
			
			<c:if test="${relatorioAnaliseDiscentesPorIndiceMBean.faixa != null}">
				<tr>
					<th>Faixa: </th>
					<td>
						${relatorioAnaliseDiscentesPorIndiceMBean.faixa}			
					</td>		
				</tr>				
			</c:if>
	
		</table>
	</div>	
	
	   	<br/>
		<table class="tabelaRelatorioBorda" align="center" style="width: 100%">	
			<tr class="linhaCinza">
				<th style="width: 500px;">Discente</th>
				<th style="width: 150px;">Situação</th>
				<th style="width: 80px; text-align: right;">Índice</th>										
			</tr>				
			<tbody>
				<c:set var="idUnidade" value="0"/>
				<c:set var="quant" value="0"/>
				<c:set var="programa" value=""/>
				<c:forEach items="#{relatorioAnaliseDiscentesPorIndiceMBean.listaDiscente}" varStatus="loop" var="linha" >
					
					<c:if test="${idUnidade != linha.discente.curso.id}">				
						
						<c:if test="${!loop.first}">		
							<script>document.getElementById("programa${idUnidade}").innerHTML = '${programa} ('+${quant}+')';</script>
						</c:if>
						
						<c:set var="idUnidade" value="${linha.discente.curso.id}"/>
						<c:set var="programa" value="${linha.discente.curso.descricao}"/>								
									    						
						<tr class="linhaCinza">
							<td style="text-align: center;" id="programa${idUnidade}" colspan="3"></td>
						</tr>
						
						<c:set var="quant" value="0"/>	
					</c:if>
					
					<c:set var="quant" value="${quant + 1}"/>
					   
					<tr>
						<td>
						    <h:commandLink action="#{relatorioAnaliseDiscentesPorIndiceMBean.gerarHistorico}" title="Clique para Visualizar o Histórico">
						    	<h:outputText value="#{linha.discente.matriculaNome}"/>
						    	<f:param name="id" value="#{linha.discente.id}"/>
						    </h:commandLink>						
						</td>
						<td style="width: 100px;">
							${linha.discente.statusString}
						</td>					
						<td style="text-align: right;">
							${linha.valor}
						</td>	
					</tr>
				</c:forEach>	  
				<script>document.getElementById("programa${idUnidade}").innerHTML = '${programa} ('+${quant}+')';</script>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" style="text-align: center;">
						${fn:length(relatorioAnaliseDiscentesPorIndiceMBean.listaDiscente)}
						Registro(s) Encontrado(s)					
					</td>
				</tr>
			</tfoot>		
		</table>	
		<br/>
		
	</h:form>		
			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>