<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<h:form id="formBusca">

	<h:outputText value="#{relatorioAcaoExtensao.create}" />

	<table class="formulario" width="90%">
		<caption>Busca por Relatórios</caption> 
		<tbody>
			<tr>
				<td><h:selectBooleanCheckbox
					value="#{relatorioAcaoExtensao.checkBuscaTitulo}"
					id="selectBuscaTitulo" styleClass="noborder" /></td>
				<td><label for="nomeProjeto"> Título da Ação: </label></td>
				<td><h:inputText id="buscaTitulo" maxlength="50"
					value="#{relatorioAcaoExtensao.buscaTitulo}" size="50"
					onfocus="javascript:$('formBusca:selectBuscaTitulo').checked = true;" /></td>
			</tr>

			<tr>
				<td><h:selectBooleanCheckbox
					value="#{relatorioAcaoExtensao.checkBuscaTipoAcao}" id="selectBuscaTipoAcao"
					styleClass="noborder" /></td>
				<td><label for="ano"> Tipo de Ação:</label></td>
				<td><h:selectBooleanCheckbox
					value="#{relatorioAcaoExtensao.buscaTipoAcaoProjeto}" id="selectBuscaTipoAcaoProjeto"
					styleClass="noborder" onfocus="javascript:$('formBusca:selectBuscaTipoAcao').checked = true;" />Projetos:
					<h:selectBooleanCheckbox
					value="#{relatorioAcaoExtensao.buscaTipoAcaoCurso}" id="selectBuscaTipoAcaoCurso"
					styleClass="noborder" onfocus="javascript:$('formBusca:selectBuscaTipoAcao').checked = true;" /> Cursos
					<h:selectBooleanCheckbox
					value="#{relatorioAcaoExtensao.buscaTipoAcaoEvento}" id="selectBuscaTipoAcaoEvento"
					styleClass="noborder" onfocus="javascript:$('formBusca:selectBuscaTipoAcao').checked = true;" /> Eventos
					<h:selectBooleanCheckbox
					value="#{relatorioAcaoExtensao.buscaTipoAcaoPrograma}" id="selectBuscaTipoAcaoPrograma"
					styleClass="noborder" onfocus="javascript:$('formBusca:selectBuscaTipoAcao').checked = true;" /> Programas					
					<h:selectBooleanCheckbox
					value="#{relatorioAcaoExtensao.buscaTipoAcaoProduto}" id="selectBuscaTipoAcaoProduto"
					styleClass="noborder" onfocus="javascript:$('formBusca:selectBuscaTipoAcao').checked = true;" /> Produto				
				</td>
			</tr>


			<tr>
				<td><h:selectBooleanCheckbox
					value="#{relatorioAcaoExtensao.checkBuscaAno}" id="selectBuscaAno"
					styleClass="noborder" /></td>
				<td><label for="ano"> Ano da Ação:</label></td>
				<td><h:inputText id="buscaAno" maxlength="4" size="4"
					value="#{relatorioAcaoExtensao.buscaAno}" converter="#{ intConverter }" onkeyup="return formatarInteiro(this)"
					onfocus="javascript:$('formBusca:selectBuscaAno').checked = true;"/>
				</td>			
			</tr>

			<tr>
				<td><h:selectBooleanCheckbox
					value="#{relatorioAcaoExtensao.checkBuscaPeriodo}"
					id="selectBuscaPeriodo" styleClass="noborder" /></td>
				<td><label for="periodo"> Período de Envio: </label></td>
				<td><t:inputCalendar id="dataInicio"
									 value="#{relatorioAcaoExtensao.buscaInicio}"
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
									 value="#{relatorioAcaoExtensao.buscaFim}"
									 renderAsPopup="true"
									 renderPopupButtonAsImage="true"
									 popupDateFormat="dd/MM/yyyy"
									 size="10"
									 onkeypress="return(formataData(this,event))"
									 onchange="$('formBusca:selectBuscaPeriodo').checked = true;"
									 maxlength="10">
									 <f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>
			
			<tr>
				<td><h:selectBooleanCheckbox
					value="#{relatorioAcaoExtensao.checkBuscaPeriodoConclusao}"
					id="selectBuscaPeriodoConclusao" styleClass="noborder" /></td>
				<td><label for="periodoCon"> Período de Conclusão: </label></td>
				<td><t:inputCalendar id="dataInicioConclusao"
									 value="#{relatorioAcaoExtensao.buscaInicioConclusao}"
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
									 value="#{relatorioAcaoExtensao.buscaFimConclusao}"
									 renderAsPopup="true"
									 renderPopupButtonAsImage="true"
									 popupDateFormat="dd/MM/yyyy"
									 size="10"
									 onkeypress="return(formataData(this,event))"
									 onchange="$('formBusca:selectBuscaPeriodoConclusao').checked = true;"
									 maxlength="10">
									 <f:converter converterId="convertData"/>
					</t:inputCalendar>
				</td>
			</tr>

			<tr>
				<td><h:selectBooleanCheckbox value="#{relatorioAcaoExtensao.checkBuscaTipoRelatorio}"
											 id="selectBuscaTipoRelatorio" styleClass="noborder" />
				</td>
					
				<td><label for="tipoRelatorio"> Tipo de Relatório: </label></td>
				
				<td><h:selectOneMenu id="buscaTipoRelatorio"
					value="#{relatorioAcaoExtensao.buscaTipoRelatorio}"
					onfocus="javascript:$('formBusca:selectBuscaTipoRelatorio').checked = true;">
					<f:selectItem itemLabel="-- SELECIONE --"
						itemValue="0" />
					<f:selectItems value="#{relatorioAcaoExtensao.tiposRelatorioCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td> <h:selectBooleanCheckbox value="#{relatorioAcaoExtensao.checkBuscaEdital}" id="selectBuscaEdital" styleClass="noborder"/> </td>
	    		<td> <label for="edital"> Edital: </label> </td>
	    		<td> 
	    			<h:selectOneMenu id="buscaEdital" value="#{relatorioAcaoExtensao.buscaEdital}" onfocus="javascript:$('formBusca:selectBuscaEdital').checked = true;">
	    				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
	    	 			<f:selectItems value="#{editalExtensao.allCombo}" />
	    	 	 	</h:selectOneMenu>	    	 
		    	</td>
	    	</tr>
	    	
	    	<tr>
	    	
				<td> 
					<h:selectBooleanCheckbox value="#{relatorioAcaoExtensao.checkBuscaServidor}" id="selectBuscaServidor" styleClass="noborder"/>  
				</td>
			
				<td><label>Servidor:</label></td>
			
				<td>
				
					<h:inputHidden id="buscaServidor" value="#{relatorioAcaoExtensao.membroEquipe.servidor.id}"></h:inputHidden>
					
					<h:inputText id="buscaNome"
						value="#{relatorioAcaoExtensao.membroEquipe.servidor.pessoa.nome}" size="50" onfocus="javascript:$('formBusca:selectBuscaServidor').checked = true;"/> 
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
				<td colspan="3"><h:commandButton value="Buscar"
					action="#{ relatorioAcaoExtensao.buscar }" id="btBuscar" /> <h:commandButton
					value="Cancelar" action="#{ relatorioAcaoExtensao.cancelar }"
					id="btCancelar" /></td>
			</tr>
		</tfoot>
	</table>

</h:form>

<br />
<br />
