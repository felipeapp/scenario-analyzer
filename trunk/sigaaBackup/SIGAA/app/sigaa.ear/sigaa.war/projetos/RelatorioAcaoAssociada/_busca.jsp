<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<a4j:keepAlive beanName="buscaRelatoriosProjetosBean" />
<h:form id="formBusca">

	<table class="formulario" width="90%">
		<caption>Busca por Relatórios</caption> 
		<tbody>
			<tr>
				<td><h:selectBooleanCheckbox
					value="#{buscaRelatoriosProjetosBean.checkBuscaTitulo}"
					id="selectBuscaTitulo" styleClass="noborder" /></td>
				<td><label for="nomeProjeto"> Título da Ação: </label></td>
				<td><h:inputText id="buscaTitulo" maxlength="50"
					value="#{buscaRelatoriosProjetosBean.buscaTitulo}" size="80"
					onfocus="javascript:$('formBusca:selectBuscaTitulo').checked = true;" /></td>
			</tr>

			<tr>
				<td><h:selectBooleanCheckbox
					value="#{buscaRelatoriosProjetosBean.checkBuscaAno}" id="selectBuscaAno"
					styleClass="noborder" /></td>
				<td><label for="ano"> Ano da Ação:</label></td>
				<td><h:inputText id="buscaAno" maxlength="4" size="4"
					value="#{buscaRelatoriosProjetosBean.buscaAno}" converter="#{ intConverter }" onkeyup="return formatarInteiro(this)"
					onfocus="javascript:$('formBusca:selectBuscaAno').checked = true;"/>
				</td>			
			</tr>

			<tr>
				<td><h:selectBooleanCheckbox
					value="#{buscaRelatoriosProjetosBean.checkBuscaPeriodo}"
					id="selectBuscaPeriodo" styleClass="noborder" /></td>
				<td><label for="periodo"> Período de Envio: </label></td>
				<td><t:inputCalendar id="dataInicio"
									 value="#{buscaRelatoriosProjetosBean.buscaInicio}"
									 renderAsPopup="true"
									 popupDateFormat="dd/MM/yyyy"
									 renderPopupButtonAsImage="true" size="10"
									 onkeypress="return(formataData(this,event))"
									 onchange="$('formBusca:selectBuscaPeriodo').checked = true;"									 
									 maxlength="10">
									 <f:converter converterId="convertData"/>
					</t:inputCalendar>
					a 
					<t:inputCalendar id="dataFim"
									 value="#{buscaRelatoriosProjetosBean.buscaFim}"
									 renderAsPopup="true"
									 renderPopupButtonAsImage="true"
									 popupDateFormat="dd/MM/yyyy"
									 size="10"
									 onkeypress="return(formataData(this,event))"
									 onchange="$('formBusca:selectBuscaPeriodo').checked = true;"
									 maxlength="10">
									 <f:converter converterId="convertData"/>
					</t:inputCalendar>
					<ufrn:help img="/img/ajuda.gif">Período de envio do relatório.</ufrn:help>
				</td>
			</tr>
			
			<tr>
				<td><h:selectBooleanCheckbox
					value="#{buscaRelatoriosProjetosBean.checkBuscaPeriodoConclusao}"
					id="selectBuscaPeriodoConclusao" styleClass="noborder" /></td>
				<td><label for="periodoCon"> Período de Conclusão: </label></td>
				<td><t:inputCalendar id="dataInicioConclusao"
									 value="#{buscaRelatoriosProjetosBean.buscaInicioConclusao}"
									 renderAsPopup="true"
									 popupDateFormat="dd/MM/yyyy"
									 renderPopupButtonAsImage="true" size="10"
									 onkeypress="return(formataData(this,event))"
									 onchange="$('formBusca:selectBuscaPeriodoConclusao').checked = true;"									 
									 maxlength="10">
									 <f:converter converterId="convertData"/>
					</t:inputCalendar>
					a 
					<t:inputCalendar id="dataFimConclusao"
									 value="#{buscaRelatoriosProjetosBean.buscaFimConclusao}"
									 renderAsPopup="true"
									 renderPopupButtonAsImage="true"
									 popupDateFormat="dd/MM/yyyy"
									 size="10"
									 onkeypress="return(formataData(this,event))"
									 onchange="$('formBusca:selectBuscaPeriodoConclusao').checked = true;"
									 maxlength="10">
									 <f:converter converterId="convertData"/>
					</t:inputCalendar>
					<ufrn:help img="/img/ajuda.gif">Período de conclusão da Ação Acadêmica Associada.</ufrn:help>
				</td>
			</tr>

			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{buscaRelatoriosProjetosBean.checkBuscaTipoRelatorio}" id="selectBuscaTipoRelatorio" styleClass="noborder" />
				</td>
					
				<td><label for="tipoRelatorio"> Tipo de Relatório: </label></td>
				
				<td><h:selectOneMenu id="buscaTipoRelatorio"
						value="#{buscaRelatoriosProjetosBean.buscaTipoRelatorio}"
						onfocus="javascript:$('formBusca:selectBuscaTipoRelatorio').checked = true;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItems value="#{buscaRelatoriosProjetosBean.tiposRelatorioCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td> <h:selectBooleanCheckbox value="#{buscaRelatoriosProjetosBean.checkBuscaEdital}" id="selectBuscaEdital" styleClass="noborder"/> </td>
	    		<td> <label for="edital"> Edital: </label> </td>
	    		<td> 
	    			<h:selectOneMenu id="buscaEdital" value="#{buscaRelatoriosProjetosBean.buscaEdital}" onfocus="javascript:$('formBusca:selectBuscaEdital').checked = true;">
	    				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
	    	 			<f:selectItems value="#{editalMBean.editaisCombo}" />
	    	 	 	</h:selectOneMenu>	    	 
		    	</td>
	    	</tr>
	    	
	    	<tr>
	    	
				<td> 
					<h:selectBooleanCheckbox value="#{buscaRelatoriosProjetosBean.checkBuscaServidor}" id="selectBuscaServidor" styleClass="noborder"/>  
				</td>
			
				<td><label>Servidor:</label></td>
			
				<td>
				
					<h:inputHidden id="buscaServidor" value="#{buscaRelatoriosProjetosBean.membroProjeto.servidor.id}"></h:inputHidden>
					
					<h:inputText id="buscaNome"
						value="#{buscaRelatoriosProjetosBean.membroProjeto.servidor.pessoa.nome}" size="80" onfocus="javascript:$('formBusca:selectBuscaServidor').checked = true;"/> 
						<ajax:autocomplete
							source="formBusca:buscaNome" target="formBusca:buscaServidor"
							baseUrl="/sigaa/ajaxServidor" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
							parser="new ResponseXmlToHtmlListParser()" /> 
						<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>
				</td>
			</tr>

		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="Buscar"   action="#{ buscaRelatoriosProjetosBean.buscar }" id="btBuscar" /> 
					<h:commandButton value="Cancelar" action="#{ buscaRelatoriosProjetosBean.cancelar }"
					id="btCancelar" />
				</td>
			</tr>
		</tfoot>
	</table>

</h:form>

<br />
<br />
