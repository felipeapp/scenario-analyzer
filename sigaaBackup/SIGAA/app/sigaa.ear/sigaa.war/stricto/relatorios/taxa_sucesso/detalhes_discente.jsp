<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@taglib uri="/tags/a4j" prefix="a4j"%>
	<a4j:keepAlive beanName="relatorioTaxaSucessoStricto"/>
    <h2 class="tituloTabela"><b>Relatório da Taxa de Sucesso (Analítico)</b></h2>
    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Curso:</th>
				<td>
					${relatorioTaxaSucessoStricto.curso.id == 0 ? 'TODOS' : relatorioTaxaSucessoStricto.curso.nome}
				</td>
			</tr>	 	
			<tr>
				<th>Nível:</th>
				<td>
					${relatorioTaxaSucessoStricto.descricaoNivel} 			
				</td>
			</tr>	 								 
			<tr>
				<th>Ano do Ingresso:</th>
				<td>
					${relatorioTaxaSucessoStricto.anoIngresso} 			
				</td>
			</tr>	   
			<tr>
				<th>Ano de Defesa:</th>
				<td>
					${relatorioTaxaSucessoStricto.anoDefesa} 			
				</td>
			</tr>	 					
		</table>
	</div>
		
	<br/>
	<h:form>
	<style>
		.aluno a {
		  text-decoration: underline;
		  color: #000000;
		  font-size: 11px;
		  font-weight: normal;
		}	
	</style>
		<c:set var="idCurso" value="0"/>
		
		<table class="tabelaRelatorioBorda" style="border-right: 1px solid silver;" align="center" width="100%">
			<thead>
					<tr>
						<th style="width:100px; text-align: center;">
							Matrícula 
						</th>
						<th style="width:400px;">
							Nome 
						</th>
						<th style="width:80px; text-align: center;">
							Mês/Ano Entrada
						</th>								
						<th style="width:80px; text-align: center;">
							Data da Defesa 
						</th>			
						<th style="width:80px; text-align: right;">
							Total de Meses 
						</th>
					</tr>
			</thead>		
		
			<c:set var="total" value="0"/>
			<c:set var="totalPrograma" value="0"/>
			<c:set var="QTD" value="0"/>
			<c:set var="QTDGeral" value="0"/>
			<tbody>
			<c:forEach items="#{relatorioTaxaSucessoStricto.detalhes}" var="linha" varStatus="loop">
				<c:if test="${idCurso != linha.id_curso}">
					<c:if test="${!loop.first}">
						<tr>
							<th colspan="6" style="text-align: center;">Quantidade de Discentes (${QTD})</th>
						</tr>																	
						<tr>
							<th style="text-align: right;" colspan="4">Média do Programa:</th>
							<td style="text-align: right; font-weight: bold;"> 								
								<fmt:formatNumber pattern="0.00" value="${totalPrograma / QTD}"/>
								<c:set var="totalPrograma" value="0"/>
								<c:set var="QTD" value="0"/>									
							</td>
						</tr>						
						<tr><td colspan="5" style="border-bottom: 1px solid black;">&nbsp;</td></tr>
					</c:if>
					<tr class="linhacinza">
						<th colspan="5" style="text-align: center;">${linha.curso} - ${linha.nivel}</th>
					</tr>	
				</c:if>
				<c:set var="idCurso" value="${linha.id_curso}"/>
				<c:set var="total" value="${total + linha.meses}"/>
				<c:set var="totalPrograma" value="${totalPrograma + linha.meses}"/>
				<c:set var="QTD" value="${QTD + 1}"/>
				<c:set var="QTDGeral" value="${QTDGeral + 1}"/>
					<tr>
						<td style="text-align: center;">
							${linha.matricula}
						</td>
						<td class="aluno">
						    <h:commandLink action="#{relatoriosPlanejamento.gerarHistorico}" title="Clique para Visualizar o Histórico">
						    	<h:outputText value="#{linha.nome}"/>
						    	<f:param name="id" value="#{linha.id_discente}"/>
						    </h:commandLink>						
						</td>
						<td style="text-align: center;">
							${linha.mes_entrada}/${linha.ano_ingresso}
						</td>
						<td style="text-align: center;">
							<c:if test="${linha.data != null}">
								<ufrn:format type="data" valor="${linha.data}"/>
							</c:if>
							<c:if test="${linha.data == null}">
								--
							</c:if>
						</td>
						<td style="text-align: right;">
							<c:if test="${linha.data != null}">
								${linha.meses}
							</c:if>
							<c:if test="${linha.data == null}">
								--
							</c:if>						
						</td>																
					</tr>
			</c:forEach>
			<tr>
				<th colspan="6" style="text-align: center;">Quantidade de Discentes (${QTD})</th>
			</tr>																				
			<tr>
				<th style="text-align: right;" colspan="4">Média do Programa:</th>
				<td style="text-align: right; font-weight: bold;"> 
					<fmt:formatNumber pattern="0.00" value="${totalPrograma / QTD}"/>
				</td>
			</tr>	
			<c:if test="${relatorioTaxaSucessoStricto.curso.id == 0}">	
				<tr><td colspan="5" style="border-bottom: 1px solid black;">&nbsp;</td></tr>
				<tr>
					<th style="text-align: right;" colspan="4">MÉDIA GERAL:</th>
					<td style="text-align: right; font-weight: bold;"> 
						<fmt:formatNumber pattern="0.00" value="${total / fn:length(relatorioTaxaSucessoStricto.detalhes)}"/>					
					</td>
				</tr>						
				<tr>
					<th colspan="6" style="text-align: center;">Quantidade Geral de Discentes (${QTDGeral})</th>
				</tr>																	
			</c:if>		
			</tbody>					
		</table>
			
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
