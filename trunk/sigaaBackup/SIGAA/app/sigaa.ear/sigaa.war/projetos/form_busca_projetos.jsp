<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<script>
function setarLabel(select, id_hidden){
	var indice = select.selectedIndex;
	if (indice > 0)	
		$("form:"+id_hidden).value = select.options[indice].text;
	
}
</script>

	<h:form id="form">

	<table class="formulario" width="90%">
	<caption>Busca por Ações Acadêmicas Integradas</caption>  
	<tbody>
		<tr>
			<td> <h:selectBooleanCheckbox value="#{buscaAcaoAssociada.checkBuscaTitulo}" id="selectBuscaTitulo" styleClass="noborder" /> </td>
	    	<td> <label for="titulo"> Título do Projeto: </label> </td>
	    	<td> <h:inputText id="buscaTitulo" value="#{buscaAcaoAssociada.buscaTitulo}" size="50" 
	    		onchange="javascript:$('form:selectBuscaTitulo').checked = true;" />
	    	</td>
	    </tr>

		<tr>
			<td> <h:selectBooleanCheckbox value="#{buscaAcaoAssociada.checkBuscaAno}" id="selectBuscaAno" styleClass="noborder" /> </td>
	    	<td> <label for="ano"> Ano: </label> </td>
	    	<td> <h:inputText id="buscaAno" value="#{buscaAcaoAssociada.buscaAno}" maxlength="4" size="4" 
	    		onchange="return formatarInteiro(this); javascript:$('form:selectBuscaAno').checked = true;" />
	    	</td>
	    </tr>
	    
		<tr>
			<td> <h:selectBooleanCheckbox value="#{buscaAcaoAssociada.checkBuscaPeriodo}" id="selectBuscaPeriodo" styleClass="noborder" /> </td>
	    	<td> <label for="periodo"> Período: </label> </td>
	    	<td>
	    	
	    	<t:inputCalendar value="#{buscaAcaoAssociada.buscaInicio}"
				renderAsPopup="true" renderPopupButtonAsImage="true"
				popupDateFormat="dd/MM/yyyy" size="10" id="dataInicio"
				onkeypress="return(formatarMascara(this,event,'##/##/####'))"
				onchange="$('form:selectBuscaPeriodo').checked = true;"
				maxlength="10" popupTodayString="Hoje é">
				<f:converter converterId="convertData" />
			</t:inputCalendar>
			a 
			<t:inputCalendar value="#{buscaAcaoAssociada.buscaFim}"
				renderAsPopup="true" renderPopupButtonAsImage="true"
				popupDateFormat="dd/MM/yyyy" size="10" id="dataFim"
				onkeypress="return(formatarMascara(this,event,'##/##/####'))"
				onchange="$('form:selectBuscaPeriodo').checked = true;"					
				maxlength="10" popupTodayString="Hoje é">
				<f:converter converterId="convertData" />
			</t:inputCalendar>	
	    	</td>
	    </tr>

		<tr>
			<td> <h:selectBooleanCheckbox value="#{buscaAcaoAssociada.checkBuscaEdital}" id="selectBuscaEdital" styleClass="noborder"/> </td>
	    	<td> <label for="edital"> Edital: </label> </td>
	    	<td>	    	
	    	 	<h:selectOneMenu id="buscaEdital" value="#{buscaAcaoAssociada.buscaEdital}" 
	    	 		onchange="javascript:$('form:selectBuscaEdital').checked = true; 
	    	 			javascript:setarLabel(this,'nomeEdital');">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
	    	 		<f:selectItems value="#{editalMBean.allCombo}" />
	    	 	</h:selectOneMenu>	    	 
	    	 </td>
	    </tr>

		<tr>
			<td><h:selectBooleanCheckbox value="#{buscaAcaoAssociada.checkBuscaAreaCNPq}"	id="selectBuscaAreaCNPq" styleClass="noborder"/> </td>
	    	<td> <label for="areaCNPQ"> Área do CNPq: </label> </td>
	    	<td>
		    	<h:selectOneMenu id="buscaAreaCNPq" value="#{buscaAcaoAssociada.buscaAreaCNPq}" 
		    		onchange="javascript:$('form:selectBuscaAreaCNPq').checked = true; 
		    			javascript:setarLabel(this,'nomeAreaCnpq');">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{area.allGrandesAreasCombo}"/>
				</h:selectOneMenu>
	    	 </td>
	    </tr>

		<tr>
			<td><h:selectBooleanCheckbox value="#{buscaAcaoAssociada.checkBuscaUnidade}" id="selectBuscaUnidade" styleClass="noborder"/> </td>
	    	<td> <label for="unidade"> Unidade Proponente: </label> </td>
	    	<td>
				<h:selectOneMenu id="buscaUnidade" value="#{buscaAcaoAssociada.buscaUnidade}" style="width: 90%" 
					onchange="javascript:$('form:selectBuscaUnidade').checked = true; 
						javascript:setarLabel(this,'nomeUnidade');">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />					
					<f:selectItems value="#{unidade.unidadesProponentesProjetosCombo}"/>
				</h:selectOneMenu>
	    	 </td>
	    </tr>

		<tr>
			<td><h:selectBooleanCheckbox value="#{buscaAcaoAssociada.checkBuscaServidor}" id="selectBuscaServidor" styleClass="noborder" />
			</td>
			<td><label>Coordenador(a):</label></td>
			<td>
			<h:inputHidden id="buscaServidor" value="#{buscaAcaoAssociada.membroProjeto.servidor.id}" />
			<h:inputText id="buscaNome"	value="#{buscaAcaoAssociada.membroProjeto.servidor.pessoa.nome}" size="70" onchange="javascript:$('form:selectBuscaServidor').checked = true;" /> 
				<ajax:autocomplete
					source="form:buscaNome" target="form:buscaServidor"
					baseUrl="/sigaa/ajaxServidor" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
					parser="new ResponseXmlToHtmlListParser()" /> 
				<span id="indicator" style="display:none;"> <img src="/sigaa/img/indicator.gif" /> </span>
			</td>
		</tr>

		<tr>
			<td><h:selectBooleanCheckbox value="#{buscaAcaoAssociada.checkBuscaSituacao}" id="selectBuscaSituacao" styleClass="noborder" />  
			</td>
	    	<td> <label for="situacaoProjeto"> Situação: </label> </td>
	    	<td>
	    		<h:selectOneMenu id="buscaSituacao" value="#{buscaAcaoAssociada.buscaSituacao}" 
	    			onchange="javascript:$('form:selectBuscaSituacao').checked = true; 
	    				javascript:setarLabel(this,'nomeSituacao');">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
	    	 		<f:selectItems value="#{tipoSituacaoProjeto.situacoesAcoesAssociadasValidas}" />
 			 	</h:selectOneMenu>
	    	 </td>
		</tr>

			<tr>
				<td><h:selectBooleanCheckbox value="#{buscaAcaoAssociada.checkBuscaAcaoSolicitacaoRenovacao}"	id="selectBuscaAcaoSolicitacaoRenovacao" styleClass="noborder"/>  
				</td>
		    	<td> <label for="projetoRenovado"> Solicitação de Renovação: </label> </td>
		    	<td>
					<h:selectOneMenu id="buscaAcaoSolicitacaoRenovacao" value="#{buscaAcaoAssociada.buscaAcaoSolicitacaoRenovacao}" 
		    			onchange="javascript:$('form:selectBuscaAcaoSolicitacaoRenovacao').checked = true;
		    				javascript:setarLabel(this,'nometipoReg');">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItem itemLabel="SOLICITAÇÃO DE RENOVAÇÃO" itemValue="1" />
						<f:selectItem itemLabel="PROJETO NOVO" itemValue="2" />
	 				</h:selectOneMenu>
		    	 </td>
		    </tr>

		    <tr>
				<td> <h:selectBooleanCheckbox value="#{buscaAcaoAssociada.checkBuscaFinanciamentoConvenio}" id="selectBuscaFinanciamentoConvenio" styleClass="noborder"/>  </td>
		    	<td> <label for="financiamentoConvenio"> Financiamentos & Convênios: </label> </td>
		    	<td>
			    	<h:selectBooleanCheckbox value="#{buscaAcaoAssociada.buscaFinanciamentoInterno}" styleClass="noborder" 
			    		onchange="javascript:$('form:selectBuscaFinanciamentoConvenio').checked = true;" /> Financiamento Interno
					<br />
			    	<h:selectBooleanCheckbox value="#{buscaAcaoAssociada.buscaFinanciamentoExterno}"	styleClass="noborder" 
			    		onchange="javascript:$('form:selectBuscaFinanciamentoConvenio').checked = true;" /> Financiamento Externo
					<br />
			    	<h:selectBooleanCheckbox value="#{buscaAcaoAssociada.buscaAutoFinanciamento}" styleClass="noborder" 
			    		onchange="javascript:$('form:selectBuscaFinanciamentoConvenio').checked = true;" /> Auto Financiamento
		    	 </td>
		    </tr>
		    
		    <tr>
				<td> <h:selectBooleanCheckbox value="#{buscaAcaoAssociada.checkBuscaDimensaoAcademica}" id="selectBuscaDimensaoAcademica" styleClass="noborder"/>  </td>
		    	<td> <label for="dimensaoAcademica"> Dimensão Acadêmica: </label> </td>
		    	<td>
			    	<h:selectBooleanCheckbox value="#{buscaAcaoAssociada.buscaEnsino}" styleClass="noborder" 
			    		onchange="javascript:$('form:selectBuscaDimensaoAcademica').checked = true;" /> Ensino
					<br />
			    	<h:selectBooleanCheckbox value="#{buscaAcaoAssociada.buscaExtensao}"	styleClass="noborder" 
			    		onchange="javascript:$('form:selectBuscaDimensaoAcademica').checked = true;" /> Extensão
					<br />
			    	<h:selectBooleanCheckbox value="#{buscaAcaoAssociada.buscaPesquisa}" styleClass="noborder" 
			    		onchange="javascript:$('form:selectBuscaDimensaoAcademica').checked = true;" /> Pesquisa
		    	 </td>
		    </tr>	    

	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Buscar" action="#{ buscaAcaoAssociada.localizar }" id="btBuscar" />
				<h:commandButton value="Cancelar" action="#{ buscaAcaoAssociada.cancelar }" onclick="#{confirm}" id="btCancelar" />
	    	</td>
	    </tr>
	</tfoot>
	</table>
	
	</h:form>

	<br/>
	<br/>
	