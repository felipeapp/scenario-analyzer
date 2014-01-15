<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@taglib uri="/tags/a4j" prefix="a4j"%>
	<a4j:keepAlive beanName="relatorioTempoMedioTitulacaoMBean"/>
    <h2 class="tituloTabela"><b>${relatorioTempoMedioTitulacaoMBean.descricaoTipoRelatorio}</b></h2>
    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Programa:</th>
				<td>
					${relatorioTempoMedioTitulacaoMBean.unidade.id == 0 ? 'TODOS' : relatorioTempoMedioTitulacaoMBean.unidade.nome}
				</td>
			</tr>	   
			<tr>
				<th>Data de Início: </th>
				<td>
				    <ufrn:format type="data" valor="${relatorioTempoMedioTitulacaoMBean.dataInicio}"/>
				</td>		
			</tr>	
			<tr>
				<th>Data de Fim: </th>
				<td>
					<ufrn:format type="data" valor="${relatorioTempoMedioTitulacaoMBean.dataFim}"/>
				</td>		
			</tr>				
		</table>
	</div>
		
	<br/>
	<h:form>
	
		<c:set var="idCurso" value="0"/>
		
		<table class="tabelaRelatorioBorda" align="center" width="100%">
			<thead>
					<tr>
						<th style="width:400px;">
							Orientador 
						</th>
						<th style="width:80px; text-align: right;">
							Total de Orientandos 
						</th>			
						<th style="width:80px; text-align: right;">
							Tempo Médio (meses) 
						</th>
					</tr>
			</thead>		
		
			<c:set var="total" value="0"/>
			<c:set var="totalOrientandos" value="0"/>
			<c:set var="totalGeralOrientandos" value="0"/>
			<c:set var="totalPrograma" value="0"/>
			<c:set var="mediaGeral" value="0"/>
			
			<c:set var="totalNivel" value="0"/>
			<c:set var="QTDNivel" value="0"/>
			<c:set var="varNivel" value=""/>			
			<c:forEach items="#{relatorioTempoMedioTitulacaoMBean.listagem}" var="linha" varStatus="loop">
				<c:if test="${idCurso != linha.id_curso}">
					<c:if test="${!loop.first}">
						<tr>
							<th style="text-align: right;">Totais do Programa:</th>
							<td style="text-align: right; font-weight: bold;">
								${totalOrientandos}
							</td>
							<td style="text-align: right; font-weight: bold;"> 
								<fmt:formatNumber pattern="0.00" value="${mediaGeral}"/>
								<c:set var="totalPrograma" value="0"/>
								<c:set var="totalOrientandos" value="0"/>
							</td>
						</tr>						
						<tr><td colspan="3" style="border-bottom: 1px solid black;">&nbsp;</td></tr>
					</c:if>
					<c:if test="${varNivel != linha.nivel}">
						<c:if test="${!loop.first}">
							<tr>
								<th style="text-align: right;" colspan="2">Média ${varNivel}:</th>
								<td style="text-align: right; font-weight: bold;"> 
									<fmt:formatNumber pattern="0.00" value="${totalNivel / QTDNivel}"/>
									<c:set var="QTDNivel" value="0"/>	
									<c:set var="totalNivel" value="0"/>	
								</td>
							</tr>						
							<tr><td colspan="3" style="border-bottom: 1px solid black;">&nbsp;</td></tr>					
						</c:if>
						<tr class="linhaCinza">
							<th colspan="3" style="text-align: center;">${linha.nivel}</th>
						</tr>
					</c:if>							
					<tr class="linhaCinza">
						<th colspan="3" style="text-align: center;">${linha.curso} - ${linha.nivel}</th>
					</tr>						
				</c:if>
				<c:set var="idCurso" value="${linha.id_curso}"/>
				<c:set var="total" value="${total + linha.meses}"/>
				<c:set var="totalPrograma" value="${totalPrograma + linha.meses}"/>
				<c:set var="totalOrientandos" value="${totalOrientandos + linha.totalAlunos}"/>
				<c:set var="totalGeralOrientandos" value="${totalGeralOrientandos + linha.totalAlunos}"/>
				<c:set var="mediaGeral" value="${linha.totalgeral}"/>	
				
				<c:set var="varNivel" value="${linha.nivel}"/>
				<c:set var="QTDNivel" value="${QTDNivel + 1}"/>	
				<c:set var="totalNivel" value="${totalNivel + linha.meses}"/>		
				<tbody>
					<tr>
						<td>
							${linha.orientador}
						</td>
						<td style="text-align: right;">
							${linha.totalAlunos}
						</td>
						<td style="text-align: right;">
							${linha.meses}
						</td>																
					</tr>
				</tbody>					
			</c:forEach>
			<tr>
				<th style="text-align: right;">Totais do Programa:</th>
				<td style="text-align: right; font-weight: bold;">
					${totalOrientandos}
				</td>
				<td style="text-align: right; font-weight: bold;"> 
					<fmt:formatNumber pattern="0.00" value="${mediaGeral}"/>
					<c:set var="totalOrientandos" value="0"/>
				</td>
			</tr>		
			<tr><td colspan="3" style="border-bottom: 1px solid black;">&nbsp;</td></tr>
			<tr>
				<th style="text-align: right;" colspan="2">Média ${varNivel}:</th>
				<td style="text-align: right; font-weight: bold;"> 
					<fmt:formatNumber pattern="0.00" value="${totalNivel / QTDNivel}"/>
				</td>
			</tr>						
		</table>
			
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
