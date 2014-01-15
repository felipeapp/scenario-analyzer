<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<a4j:keepAlive beanName="relatorioUnidadesAcademicasMBean"/>
<style>
	<!--
	.linkValor a {
	  text-decoration: underline;
	  color: #000000;
	  font-size: 11px;
	  font-weight: normal;
	}
	
	.totalGeral a {
		font-weight: bold;
	}
	-->
</style>
<f:view>
	<h:form>
	    <h2 class="tituloTabela"><b>Detalhamento das Unidades Acadêmicas</b></h2>
	    
	    <div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Tipo Unidade Acadêmica:</th>
					<td>
						${relatorioUnidadesAcademicasMBean.tipoUnidade}
					</td>
				</tr>	      
			</table>
		</div>
		
		<br/>	    
	   	
		<table class="tabelaRelatorioBorda" align="center" style="width: 100%">	
			<thead>
				<tr>
					<th>Nome</th>
					<th>Sigla</th>					
				</tr>				
			</thead>
			<tbody>
				<c:set var="idGestora" value="0"/>
				<c:set var="total" value="0"/>
				<c:set var="totalGeral" value="0"/>
				<c:forEach items="#{relatorioUnidadesAcademicasMBean.detalhamento}" varStatus="loop" var="linha" >
					<c:if test="${linha.gestora.id != idGestora}">
						
						<c:if test="${!loop.first}">
							<tr class="linhaCinza">
								<td colspan="2" style="text-align: center;">
									Total: ${total}
								</td>
							</tr>			
							<tr><td colspan="2">&nbsp;</td></tr>				
						</c:if>					
					
						<tr class="linhaCinza">
							<td colspan="2" style="text-align: center;">
								${linha.gestora.nome} - ${linha.gestora.sigla}
							</td>
						</tr>
						<c:set var="idGestora" value="${linha.gestora.id}"/>	
						<c:set var="total" value="0"/>				
					</c:if>
					
					<c:set var="total" value="${total + 1}"/>
					<c:set var="totalGeral" value="${totalGeral + 1}"/>
					   
					<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: left;">
							${linha.nome}
						</td>
						<td style="text-align: left;">
							${linha.sigla}
						</td>																					
					</tr>
				</c:forEach>	
				<tr class="linhaCinza">
					<td colspan="2" style="text-align: center;">
						Total: ${total}
					</td>
				</tr>	 
				<tr><td colspan="2">&nbsp;</td></tr>
				<tr class="linhaCinza">
					<td colspan="2" style="text-align: center;">
						Total Geral: ${totalGeral}
					</td>
				</tr>					
			</tbody>			
		</table>	
	</h:form>		
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>