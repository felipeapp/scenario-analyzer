<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>


<f:view>
	<c:set var="material" value=""/>
	<c:if test="${relatorioAcessoTurmaVirtualMBean.video}">
		<c:set var="material" value="vídeo"/>
		<center><h3 class="fonte">RELATÓRIO DE VÍDEOS ACESSADOS</h3></center>
	</c:if>
	<c:if test="${!relatorioAcessoTurmaVirtualMBean.video}">
		<c:set var="material" value="arquivo"/>
		<center><h3 class="fonte">RELATÓRIO DE ARQUIVOS ACESSADOS</h3></center>
	</c:if>
	
	<br/>
	
	<div class="fonte">
	
		<c:set var="items" value="#{relatorioAcessoTurmaVirtualMBean.logLeituraTurmaVirtual}"></c:set>
	
		<c:if test="${ not empty items }">
			<div style="border-bottom:1px solid #000000;padding-bottom:10px;">
				<p>Discentes que acessaram o ${material}:</p>
				<p><strong><h:outputText value="#{relatorioAcessoTurmaVirtualMBean.nomeArquivoVisualizado}" /></strong></p>
			</div>
		</c:if>
			
		<table class="tabelaRelatorio" width="100%"">
			<tr>
				<th style="width:300px;">Discente</th>
				<th style="text-align: left;">Acessos</th>
			</tr>

			<c:set var="items" value="#{relatorioAcessoTurmaVirtualMBean.logLeituraTurmaVirtual}"></c:set>
			<c:if test="${ empty items }">
				<tr>
					<td style="padding-left: 200px;">
						Nenhum discente acessou esse ${material} ainda.
					</td>
				</tr>
			</c:if>
			
			<c:if test="${ not empty items }">
				<c:forEach var="item" items="#{items}">
					<tr>
						<td colspan="2" style="background:#EEE;"><h:outputText value="#{item.nomeDiscente} (#{ fn:length(item.detalhes) })" /></td>
					</tr>
						<c:forEach var="detalhe" items="#{item.detalhes}" >
							<tr>
								<td></td>
								<td style="text-align: left;"> <h:outputText value="#{ detalhe.dataExtenso }"/> </td>
							</tr>
						</c:forEach>
					<tr><td style="height:10px;"></td></tr>
				</c:forEach>
			</c:if>
		</table>
	</div>
	
	<c:if test="${ not empty items }">
		<table align="center">
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center; font-weight: bold;">
						<h:outputText value="#{fn:length(relatorioAcessoTurmaVirtualMBean.logLeituraTurmaVirtual)} de #{ fn:length(turmaVirtual.discentesTurma) } discentes acessaram o arquivo totalizando " rendered="#{!relatorioAcessoTurmaVirtualMBean.video}"/>
						<h:outputText value="#{fn:length(relatorioAcessoTurmaVirtualMBean.logLeituraTurmaVirtual)} de #{ fn:length(turmaVirtual.discentesTurma) } discentes acessaram o vídeo totalizando " rendered="#{relatorioAcessoTurmaVirtualMBean.video}" />
						<h:outputText value="#{(fn:length(relatorioAcessoTurmaVirtualMBean.logLeituraTurmaVirtual) / fn:length(turmaVirtual.discentesTurma)) * 100 }"><f:convertNumber maxFractionDigits="2" groupingUsed="true" maxIntegerDigits="3" type="currency" currencySymbol="" /></h:outputText>%  
					</td>
				</tr>
			</tfoot>
		</table>
	</c:if>
	
	<div style="font-size:7.5pt;font-weight:bold;text-align:center;margin-top:20px;">OBS: Os dados de acessos podem demorar alguns minutos até serem contabilizados.</div>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>