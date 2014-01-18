<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Relat�rio de A��es de Extens�o por Edital</h2>
	<h:outputText value="#{relatoriosAtividades.create}" />
	
<c:set var="confirmm"
		value="return confirm('Deseja cancelar a opera��o? ');"
		scope="application" />
	

	<c:if test="${acesso.extensao or acesso.planejamento }">
		<h:form id="busca">
		
			<h:inputHidden value="#{relatoriosAtividades.nomeRelatorio}" />
		
			<table class="formulario">
				<caption>Dados do Relat�rio</caption>
				<tbody>
					<tr>
						<th class="obrigatorio">Edital de Extens�o: </th>
						<td>
							<h:selectOneMenu value="#{relatoriosAtividades.edital.id}">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM EDITAL --" />
							<f:selectItems value="#{editalExtensao.allCombo}" />
							</h:selectOneMenu> 
						</td>
					</tr>

					<tr>
						<th class="obrigatorio">Data In�cio: </th>
						<td>
						<%--<t:inputCalendar id="inicio" value="#{relatoriosAtividades.dataInicio}" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data Inicial">
     <f:convertDateTime pattern="dd/MM/yyyy"/>
    </t:inputCalendar>--%>
							<t:inputCalendar value="#{relatoriosAtividades.dataInicio}" renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" id="inicio"
								size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" maxlength="10" popupTodayString="Hoje �">
								<f:converter converterId="convertData"/>
							</t:inputCalendar> 
							
						</td>
					</tr>

					<tr>
						<th class="obrigatorio">Data Fim: </th>
						<td>
						<%--<t:inputCalendar id="fim" value="#{relatoriosAtividades.dataFim}" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data Final">
     <f:convertDateTime pattern="dd/MM/yyyy"/>
    </t:inputCalendar>--%> 
							<t:inputCalendar value="#{relatoriosAtividades.dataFim}" renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" id="fim"
								size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" maxlength="10" popupTodayString="Hoje �">
								<f:converter converterId="convertData"/>
							</t:inputCalendar> 
						</td>
					</tr>
				</tbody>
				
				<tfoot>
					<tr>
						<td colspan="2">
							<h:commandButton value="Gerar Relat�rio" action="#{relatoriosAtividades.relatorioByEdital}" /> 
							<h:commandButton value="Cancelar" onclick="#{confirmm}" action="#{relatoriosAtividades.cancelar}" />
						</td>
					</tr>
				</tfoot>
			</table>
		</h:form>
		<br />
		<div class="obrigatorio"> Campos de preenchimento obrigat�rio. </div>
	</c:if>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>