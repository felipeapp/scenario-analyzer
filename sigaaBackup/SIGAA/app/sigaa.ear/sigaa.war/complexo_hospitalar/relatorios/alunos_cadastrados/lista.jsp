<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<style>
	<!--
	.linkValor a {
	  text-decoration: underline;
	  color: #000000;
	  font-size: 11px;
	  font-weight: normal;
	}
	-->
</style>
<f:view>
	<a4j:keepAlive beanName="relatorioAlunosCadastradosResidenciaMBean"/>
	<h:form>
	    <h2 class="tituloTabela"><b>Total de Alunos Cadastrados por Programa</b></h2>

	   	<br/>
		<table class="tabelaRelatorioBorda" align="center" style="width: 100%">	
			<tr class="linhaCinza">
				<th>Programa</th>
				<th style="text-align: right;width: 70px;">Total</th>										
			</tr>				
			<c:set var="total" value="0"/>
			<tbody>
				<c:forEach items="#{relatorioAlunosCadastradosResidenciaMBean.listagem}" varStatus="loop" var="linha" >   
				
					<c:set var="total" value="${total + linha.total}"/>

					<tr>
						<td style="text-align: left;">
							${linha.unidade.nome}
						</td>						
						<td style="text-align: right;" class="linkValor">
							<c:if test="${linha.total > 0 }">
								<h:commandLink action="#{relatorioAlunosCadastradosResidenciaMBean.detalhar}">
									<ufrn:format type="valorint" valor="${ linha.total}"/>
									<f:param name="idUnidade" value="#{linha.unidade.id}"/>
								</h:commandLink>
							</c:if>
							<c:if test="${linha.total == 0 }">
								<h:outputText value="0"/>
							</c:if>
						</td>	
					</tr>
				</c:forEach>	  
			</tbody>	
			<tr class="linhaCinza">
				<th style="text-align: right;">TOTAL GERAL:</th>
				<td style="text-align: right;" class="linkValor">
					<c:if test="${total > 0 }">
						<h:commandLink action="#{relatorioAlunosCadastradosResidenciaMBean.detalhar}">
							<ufrn:format type="valorint" valor="${ total }"/>
							<f:param name="idUnidade" value="0"/>
						</h:commandLink>
					</c:if>
					<c:if test="${total == 0 }">
						<h:outputText value="0"/>
					</c:if>					
				</td>			
			</tr>
		</table>	
		<br/>
		
	</h:form>		
			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>