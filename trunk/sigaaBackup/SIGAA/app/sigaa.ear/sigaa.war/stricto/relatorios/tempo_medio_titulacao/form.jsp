<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> &gt; ${relatorioTempoMedioTitulacaoMBean.descricaoTipoRelatorio}</h2>

<a4j:keepAlive beanName="relatorioTempoMedioTitulacaoMBean"/>
<h:form id="form">
<div class="descricaoOperacao">
	<p>
		Este relatório mostra a quantidade de meses que os Discentes levaram para concluir seu programa. 
	</p>
	<p>			
		Para prosseguir, ${acesso.ppg ? 'selecione abaixo o Programa e ' : 'informe '} o período de defesa dos discentes e clique no botão <b>Gerar Relatório.</b>	
	</p>
</div>

<table class="formulario" style="width: 70%">
<caption> Informe os Critérios para a Emissão do Relatório </caption>
	<tbody>
	<tr>
		<th>
			<c:choose>
				<c:when test="${!acesso.ppg}">
					<b>Programa:</b> 
				</c:when>
				<c:otherwise>
					Programa: 
				</c:otherwise>
			</c:choose>
		</th>
		<td>
		<c:if test="${acesso.ppg}">
			<h:selectOneMenu id="programa" value="#{relatorioTempoMedioTitulacaoMBean.unidade.id}">
				<f:selectItem itemLabel="-- TODOS --" itemValue="0"/>
				<f:selectItems value="#{unidade.allProgramaPosCombo}"/>
			</h:selectOneMenu>
		</c:if>
		<c:if test="${!acesso.ppg}">
			${relatorioTempoMedioTitulacaoMBean.programaStricto}
		</c:if>
		</td>
	</tr>
	<tr>
		<th class="obrigatorio">Data de Início:</th>
		<td>
			<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
						maxlength="10" onkeypress="return formataData(this,event)" value="#{relatorioTempoMedioTitulacaoMBean.dataInicio}" id="dataInicio" /> 			

			<h:outputText value=" Data de Fim: " styleClass="obrigatorio"/>
			<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
						maxlength="10" onkeypress="return formataData(this,event)" value="#{relatorioTempoMedioTitulacaoMBean.dataFim}" id="datafim" />
		</td>
	</tr>		
	</tbody>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton id="btEmitir" action="#{relatorioTempoMedioTitulacaoMBean.gerarRelatorio}" value="Gerar Relatório"/>		
			<h:commandButton id="btCancelar" action="#{relatorioPrazoMaximoPosBean.cancelar}" value="Cancelar" onclick="#{confirm}"/>
		</td>
	</tr>
	</tfoot>
</table>
</h:form>
<br />
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>