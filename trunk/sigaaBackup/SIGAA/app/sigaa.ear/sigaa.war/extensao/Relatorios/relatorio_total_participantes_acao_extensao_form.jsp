<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<a4j:keepAlive beanName="relatoriosAtividades" />
<f:view>


<h2><ufrn:subSistema /> > Relatório de Ação de Extensão</h2>

<h:form id="form">
	<table class="formulario" width="100%">
		<caption>Relatório de participação em Ações de Extensão</caption>
		<tbody>
			
			<tr>
				<th style="font-weight: bold;">Tipo de Relatório:</th>
				<td>
					<h:outputText style="text-align: right;" value="#{relatoriosAtividades.nomeRelatorio}" />
				</td>
			</tr>
			
			<tr>
				<th class="obrigatorio">Período:</th>
				<td>
					
					<t:inputCalendar id="dataInicio"
						value="#{relatoriosAtividades.dataInicio}"
						renderAsPopup="true" renderPopupButtonAsImage="true"
						popupDateFormat="dd/MM/yyyy" size="10"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))"
						maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" />
					</t:inputCalendar>
				
					a
					<t:inputCalendar id="dataFim"
						value="#{relatoriosAtividades.dataFim}" 
						renderAsPopup="true" renderPopupButtonAsImage="true"
						popupDateFormat="dd/MM/yyyy" size="10"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))"
						maxlength="10" popupTodayString="Hoje é">
						<f:converter converterId="convertData" /> 					
					</t:inputCalendar>
				</td>
			</tr>
			
			<tr>
				<th>Unidade Acadêmica:</th>
				<td>
					<h:selectOneMenu id="buscaUnidade" value="#{relatoriosAtividades.unidade.id}">
						<f:selectItem itemLabel="--SELECIONE--" itemValue="0" />
						<f:selectItems value="#{unidade.allCentrosEscolasCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
		</tbody>
		<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="btnGerarRel" value="Gerar Relatório" 
					action="#{relatoriosAtividades.gerarRelatorioTotalParticipantesAcaoExtensao}"/>
				<h:commandButton id="btnCancelar" value="Cancelar" action="#{relatoriosAtividades.cancelar}" onclick="#{confirm}" />
			</td>
		</tr>
	</tfoot>
	</table>
	<br />
	<center>
		<div class="required-items">
			<span class="required"></span>Campos de Preenchimento Obrigatório
		</div>
	</center>

</h:form>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>