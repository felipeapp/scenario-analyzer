<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2> <ufrn:subSistema/> > Total de Público Atingido com Base nos Relatórios Submetidos</h2>
	
	<div class="descricaoOperacao">
	   <b>Atentção:</b><br/>
	       O relatório gerado apresenta o total de público atingido com base nos relatórios submetidos de todas 
	       as Ações de Extensão realizadas no período informado. 
	</div>
	
	<h:form id="formConsulta">
		<table class="formulario" width="40%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
				<th class="obrigatorio">Data Início:</th>
				<td>
					<t:inputCalendar id="Data_Inicial"
						value="#{ relatorioPublicoAtingido.dataInicio }"
						renderAsPopup="true"
						readonly="#{relatorioPublicoAtingido.readOnly}"
						disabled="#{relatorioPublicoAtingido.readOnly}"
						renderPopupButtonAsImage="true"
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é"
						onkeypress="return(formataData(this,event))" size="10"
						maxlength="10">
						
					</t:inputCalendar>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio"> Data Fim: </th>
					<td>
					<t:inputCalendar
						id="Data_Final"
						value="#{ relatorioPublicoAtingido.dataFim }"
						renderAsPopup="true"
						readonly="#{relatorioPublicoAtingido.readOnly}"
						disabled="#{relatorioPublicoAtingido.readOnly}"
						renderPopupButtonAsImage="true"
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é"
						onkeypress="return(formataData(this,event))" size="10"
						maxlength="10">
						
					</t:inputCalendar>
				</td>
			</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Relatório" action="#{relatorioPublicoAtingido.gerarRelatorio}" /> 
						<h:commandButton value="Cancelar" action="#{relatorioPublicoAtingido.cancelar}" id="cancelar" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
  <br />
  <br />
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>