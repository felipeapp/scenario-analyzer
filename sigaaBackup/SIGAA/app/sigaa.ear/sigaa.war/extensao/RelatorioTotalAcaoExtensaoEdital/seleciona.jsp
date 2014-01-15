<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2> <ufrn:subSistema/> > Total de Ações de Extensão que Concorreram a Editais Públicos</h2>
	<h:form id="formConsulta">
		<table class="formulario" width="75%">
			<caption class="formulario">Dados da Busca</caption>
			<tbody>
				
				<tr>
					<th>
						<h:selectBooleanCheckbox value="#{relatorioTotalAcaoEdital.filtroAreaTematica}" id="checkArea" styleClass="noborder"/>
					</th>
					<td>
						<label for="checkArea" onclick="$('formConsulta:checkArea').checked = !$('formConsulta:checkArea').checked;">Área Temática:</label>
					</td>
					<td>
						<h:selectOneMenu id="areaTematica" value="#{relatorioTotalAcaoEdital.area.id}" onfocus="$('formConsulta:checkArea').checked = true;">							
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{areaTematica.allCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<th><h:selectBooleanCheckbox value="#{relatorioTotalAcaoEdital.filtroSituacaoAcao}" id="checkSituacao" styleClass="noborder" />  </th>
			    	<td>
						<label for="checkSituacao" onclick="$('formConsulta:checkSituacao').checked = !$('formConsulta:checkSituacao').checked;">Situação da Ação:</label>
					</td>
			    	<td>
			    		<h:selectOneMenu id="buscaSituacao" value="#{relatorioTotalAcaoEdital.situacao.id}" 
			    			onfocus="$('formConsulta:checkSituacao').checked = true;" >
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
			    	 		<f:selectItems value="#{atividadeExtensao.tipoSituacaoAtividadeCombo}" />
		 			 	</h:selectOneMenu>
			    	 </td>
			    </tr>
			    
				<tr>
					<th><h:selectBooleanCheckbox value="#{relatorioTotalAcaoEdital.filtroPeriodo}" id="checkPeriodo" styleClass="noborder" />  </th>
				    <td>
						<label for="checkPeriodo" onclick="$('formConsulta:checkPeriodo').checked = !$('formConsulta:checkPeriodo').checked;">Período da Excução da Ação:</label>
					</td>
					<td>
						<t:inputCalendar id="Data_Inicial"
							value="#{ relatorioTotalAcaoEdital.dataInicio }"
							renderAsPopup="true"
							readonly="#{relatorioTotalAcaoEdital.readOnly}"
							disabled="#{relatorioTotalAcaoEdital.readOnly}"
							renderPopupButtonAsImage="true"
							onchange="$('formConsulta:checkPeriodo').checked = true;"
							popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje é"
							onkeypress="return(formataData(this,event))" size="10"
							maxlength="10">
							
						</t:inputCalendar>
					
					&nbsp; a &nbsp;					
						<t:inputCalendar id="Data_Final"
							value="#{ relatorioTotalAcaoEdital.dataFim }"
							renderAsPopup="true"
							readonly="#{relatorioTotalAcaoEdital.readOnly}"
							disabled="#{relatorioTotalAcaoEdital.readOnly}"
							renderPopupButtonAsImage="true"
							onchange="$('formConsulta:checkPeriodo').checked = true;"
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
						<h:commandButton value="Gerar Relatório" action="#{relatorioTotalAcaoEdital.gerarRelatorio}" /> 
						<h:commandButton value="Cancelar" action="#{relatorioTotalAcaoEdital.cancelar}" id="cancelar" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br />
	<br />
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>