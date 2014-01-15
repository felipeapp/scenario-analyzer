<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<h:form id="form">
    <h2 class="tituloTabela"><b>Detalhamento da Taxa de Conclusão</b></h2>
    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Ano:</th>
				<td>
					${relatorioTaxaConclusao.anoBase} 			
				</td>
			</tr>	   
		</table>
	</div>
	
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

	<div class="naoImprimir" align="center">
		<div class="descricaoOperacao" style="width: 50%">Clique sobre os números para ver o detalhamento.</div>
	</div>	
	<table class="tabelaRelatorioBorda" align="center" width="100%">
		<c:set var="idUnidade" value="0"/>
		<c:set var="siglaAtual" value=""/>	
		<c:set var="TConclusaoCentro" value="0"/>
		<c:set var="TIngressosCentro" value="0"/>
		<c:set var="TConclusao" value="0"/>
		<c:set var="TIngressos" value="0"/>

		<c:forEach items="#{relatorioTaxaConclusao.taxaConclusaoDetalhada}" varStatus="loop" var="linha" >
			<c:if test="${idUnidade != linha.id_unidade}">
		 		<c:if test="${!loop.first}">
					<tr>
						<td style="text-align: right; font-weight: bold;">TOTAL ${siglaAtual}:</td>
						<td style="text-align: right;" class="linkValor">
							<h:commandLink action="#{ relatorioTaxaConclusao.exibirDiscentes }">
								<h:outputText>${TConclusaoCentro}</h:outputText>
								<f:param name="ano" value="#{relatorioTaxaConclusao.anoBase}" />
								<f:param name="idUnidade" value="#{idUnidade}" />
								<f:param name="tipo" value="C" />
							</h:commandLink>							
						</td>
						<td style="text-align: right;" class="linkValor">
							<h:outputText>${TIngressosCentro}</h:outputText>
						</td>
						<td style="text-align: right;"><fmt:formatNumber pattern="#0.00" value="${(TConclusaoCentro/TIngressosCentro)*100}"/>%</td>
					</tr>		
					<c:set var="TConclusaoCentro" value="0"/>
					<c:set var="TIngressosCentro" value="0"/>					 		
		 		</c:if>			
				<thead>
					<tr>
						<th colspan="4" style="text-align: center;">${linha.sigla}</th>
					</tr>
					<tr>
						<th style="text-align: center;width: 200px;">Curso</th>
						<th style="text-align: right;width: 80px;">Concluintes</th>
						<th style="text-align: right;width: 80px;">Ingressantes</th>
						<th style="text-align: right;width: 80px;">Taxa de Conclusão (%)</th>
					</tr>
				</thead>	
				
				<c:set var="siglaAtual" value="${linha.sigla}"/>	
				<c:set var="TConclusaoCentro" value="0"/>
				<c:set var="TIngressosCentro" value="0"/>													
			</c:if>
			
			<tr>
				<td>${linha.nome}</td>
				<td style="text-align: right;" class="linkValor">
					<h:commandLink action="#{ relatorioTaxaConclusao.exibirDiscentes }">
						<h:outputText>${linha.concluintes}</h:outputText>
						<f:param name="ano" value="#{relatorioTaxaConclusao.anoBase}" />
						<f:param name="idCurso" value="#{linha.id_curso}" />
						<f:param name="idGrau" value="#{linha.id_grau_academico}" />
						<f:param name="idTurno" value="#{linha.id_turno}" />						
						<f:param name="tipo" value="C" />
					</h:commandLink>				
				</td>
				<td style="text-align: right;" class="linkValor">
					<h:outputText>${linha.ingressos}</h:outputText>
				</td>
				<td style="text-align: right;">
					<fmt:formatNumber pattern="#0.00" value="${linha.taxa*100}"/>%
				</td>
			</tr>
			
			<c:set var="TConclusaoCentro" value="${TConclusaoCentro + linha.concluintes}"/>
			<c:set var="TIngressosCentro" value="${TIngressosCentro + linha.ingressos}"/>				
			
			<c:set var="TConclusao" value="${TConclusao + linha.concluintes}"/>
			<c:set var="TIngressos" value="${TIngressos + linha.ingressos}"/>
			
			<c:set var="idUnidade" value="#{linha.id_unidade}"/>			
		</c:forEach>
		<tr>
			<td style="text-align: right; font-weight: bold;">TOTAL ${siglaAtual}:</td>
			<td style="text-align: right;" class="linkValor">
				<h:commandLink action="#{ relatorioTaxaConclusao.exibirDiscentes }">
					<h:outputText>${TConclusaoCentro}</h:outputText>
					<f:param name="ano" value="#{relatorioTaxaConclusao.anoBase}" />
					<f:param name="idUnidade" value="#{idUnidade}" />
					<f:param name="tipo" value="C" />
				</h:commandLink>							
			</td>
			<td style="text-align: right;" class="linkValor">
				<h:outputText>${TIngressosCentro}</h:outputText>
			</td>
			<td style="text-align: right;"><fmt:formatNumber pattern="#0.00" value="${(TConclusaoCentro/TIngressosCentro)*100}"/>%</td>
		</tr>		
		<tr>
			<td style="text-align: right; font-weight: bold;">TOTAL GERAL:</td>
			<td style="text-align: right;" class="linkValor totalGeral">
				<h:commandLink action="#{ relatorioTaxaConclusao.exibirDiscentes }">
					<h:outputText>${TConclusao}</h:outputText>
					<f:param name="ano" value="#{relatorioTaxaConclusao.anoBase}" />
					<f:param name="tipo" value="C" />
				</h:commandLink>							
			</td>
			<td style="text-align: right;" class="linkValor totalGeral">
				<h:outputText><b>${TIngressos}</b></h:outputText>					
			</td>
			<td style="text-align: right; font-weight: bold;"><fmt:formatNumber pattern="#0.00" value="${(TConclusao/TIngressos)*100}"/>%</td>			
		</tr>			
	</table>
				
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
	