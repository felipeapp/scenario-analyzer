<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2> <ufrn:subSistema/> > Total de P�blico Atingido com Base nos Relat�rios Submetidos</h2>
	
	<div class="descricaoOperacao">
	   <b>Atent��o:</b><br/>
	       O relat�rio gerado apresenta o total de p�blico atingido com base nos relat�rios submetidos de todas 
	       as A��es de Extens�o realizadas no per�odo informado. 
	</div>
	
	<h:form id="formConsulta">
		<table class="formulario" width="40%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				<tr>
				<th class="obrigatorio">Data In�cio:</th>
				<td>
					<t:inputCalendar id="Data_Inicial"
						value="#{ relatorioPublicoAtingido.dataInicio }"
						renderAsPopup="true"
						readonly="#{relatorioPublicoAtingido.readOnly}"
						disabled="#{relatorioPublicoAtingido.readOnly}"
						renderPopupButtonAsImage="true"
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �"
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
						popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �"
						onkeypress="return(formataData(this,event))" size="10"
						maxlength="10">
						
					</t:inputCalendar>
				</td>
			</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Gerar Relat�rio" action="#{relatorioPublicoAtingido.gerarRelatorio}" /> 
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