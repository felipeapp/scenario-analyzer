<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<%@taglib uri="/tags/a4j" prefix="a4j"%>
	<a4j:keepAlive beanName="relatorioTaxaSucessoStricto"/>
	<h:form id="form">
    <h2 class="tituloTabela"><b>Relatório da Taxa de Sucesso</b></h2>
    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Curso:</th>
				<td>
					${relatorioTaxaSucessoStricto.unidade.id == 0 ? 'TODOS' : relatorioTaxaSucessoStricto.unidade.nome}
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
	<br/>
	<table class="tabelaRelatorioBorda" align="center" width="100%">
		<c:set var="TConclusao" value="0"/>
		<c:set var="TIngressos" value="0"/>
		
		<thead>
		<tr>
			<th style="text-align: left; width: 80px;">Programa</th>
			<th style="text-align: right; width: 80px;">Ingressos</th>
			<th style="text-align: right; width: 80px;">Defesas</th>
			<th style="text-align: right; width: 140px;">Taxa de Sucesso (%)</th>
		</tr>
		</thead>

		<c:forEach items="#{relatorioTaxaSucessoStricto.listagem}" varStatus="loop" var="linha" >
			<tr>
				<td>${linha.nome}</td>
				<td style="text-align: right;" class="linkValor">
					<c:if test="${linha.ingressos > 0}">
						<h:commandLink action="#{ relatorioTaxaSucessoStricto.exibirDetalhes }">
							<h:outputText>${linha.ingressos}</h:outputText>
							<f:param name="idCurso" value="#{linha.id_curso}" />
							<f:param name="tipo" value="I" />
						</h:commandLink>				
					</c:if>
					<c:if test="${linha.ingressos == 0}">
						${linha.ingressos}
					</c:if>
				</td>
				<td style="text-align: right;" class="linkValor">
					<c:if test="${linha.defesas > 0}">
						<h:commandLink action="#{ relatorioTaxaSucessoStricto.exibirDetalhes }">
							<h:outputText>${linha.defesas}</h:outputText>
							<f:param name="idCurso" value="#{linha.id_curso}" />
							<f:param name="tipo" value="D"/>
						</h:commandLink>
					</c:if>	
					<c:if test="${linha.defesas == 0}">
						${linha.defesas}
					</c:if>										
				</td>
				<td style="text-align: right;">
					${linha.taxa_sucesso}%
				</td>
			</tr>			
			<c:set var="TConclusao" value="${TConclusao + linha.defesas}"/>
			<c:set var="TIngressos" value="${TIngressos + linha.ingressos}"/>		
		</c:forEach>	
		<c:if test="${fn:length(relatorioTaxaSucessoStricto.listagem) > 1}">			
			<tr>
				<td style="text-align: right; font-weight: bold;">TOTAL GERAL:</td>
				<td style="text-align: right;" class="linkValor totalGeral">
					<c:if test="${TIngressos > 0}">
						<h:commandLink action="#{ relatorioTaxaSucessoStricto.exibirDetalhes }">
							<h:outputText>${TIngressos}</h:outputText>
							<f:param name="tipo" value="I" />
						</h:commandLink>		
					</c:if>				
					<c:if test="${TIngressos == 0}">
						${TIngressos}
					</c:if>	
				</td>
				<td style="text-align: right;" class="linkValor totalGeral">
					<c:if test="${TConclusao > 0}">
						<h:commandLink action="#{ relatorioTaxaSucessoStricto.exibirDetalhes }">
							<h:outputText>${TConclusao}</h:outputText>					
							<f:param name="tipo" value="D" />
						</h:commandLink>
					</c:if>	
					<c:if test="${TConclusao == 0}">
						${TConclusao}
					</c:if>											
				</td>
				<td style="text-align: right; font-weight: bold;"><fmt:formatNumber pattern="#0.00" value="${(TConclusao/TIngressos)*100}"/>%</td>			
			</tr>	
		</c:if>		
	</table>
				
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
	