<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>


<script>
function setarLabel(select, id_hidden){
	var indice = select.selectedIndex;
	if (indice > 0)	
		$("formBuscaAtividade:"+id_hidden).value = select.options[indice].text;
	
}
</script>

	<h:form id="formBuscaAtividade">

	<h:outputText value="#{atividadeExtensao.create}"/>
	<h:outputText value="#{editalExtensao.create}"/>
	
	<h:inputHidden value="#{atividadeExtensao.nomeEdital}" id="nomeEdital" />
	<h:inputHidden value="#{atividadeExtensao.nomeTipoAcao}" id="nomeTipoAcao" />
	<h:inputHidden value="#{atividadeExtensao.nomeAreaCnpq}" id="nomeAreaCnpq" />
	<h:inputHidden value="#{atividadeExtensao.nomeUnidade}" id="nomeUnidade" />
	<h:inputHidden value="#{atividadeExtensao.nomeCentro}" id="nomeCentro" />
	<h:inputHidden value="#{atividadeExtensao.nomeArea}" id="nomeArea" />
	<h:inputHidden value="#{atividadeExtensao.nomeSituacao}" id="nomeSituacao" />
	<h:inputHidden value="#{atividadeExtensao.nomeTipoRegistro}" id="nometipoReg" />
	
	<table class="formulario" width="90%">
	<caption>Busca por A��es de Extens�o</caption>  
	<tbody>
		<tr>
			<td> <h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaTitulo}" id="selectBuscaTitulo" styleClass="noborder" /> </td>
	    	<td> <label for="titulo" > T�tulo da A��o: </label> </td>
	    	<td> <h:inputText id="buscaTitulo" value="#{atividadeExtensao.buscaNomeAtividade}" style="width:90%"
	    		onchange="javascript:$('formBuscaAtividade:selectBuscaTitulo').checked = true;" />
	    	</td>
	    </tr>

		<tr>
			<td> <h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaAno}" id="selectBuscaAno" styleClass="noborder" /> </td>
	    	<td> <label for="ano"> Ano: </label> </td>
	    	<td> <h:inputText id="buscaAno" value="#{atividadeExtensao.buscaAno}" maxlength="4" size="4" 
	    		onkeyup="return formatarInteiro(this)" onchange="javascript:$('formBuscaAtividade:selectBuscaAno').checked = true;" />
	    	</td>
	    </tr>
	    
	    <tr>
			<td> <h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaCodigo}" id="selectBuscaCodigo" styleClass="noborder" /> </td>
	    	<td> <label for="ano"> C�digo: </label> </td>
	    	<td>
	    	     <h:inputText id="buscaCodigo" value="#{atividadeExtensao.buscaCodigo}" size="10" alt="" 
	    	     	onchange="javascript:$('formBuscaAtividade:selectBuscaCodigo').checked = true;" />
	    	     <ufrn:help img="/img/ajuda.gif">
	    	     	O c�digo de uma a��o de extens�o � composto por no m�nimo dez caracteres.
	    	     	Os dois primeiros d�gitos identificam o tipo da a��o. Os n�meros  seguintes e que
	    	     	antecedem o caractere '-' representam o n�mero 'sequ�ncia' da a��o. Por fim, os
	    	     	�ltimos quatro d�gitos representam o ano da a��o. Por exemplo, o c�digo da a��o
	    	     	PD002-2009 informa que a a��o � um produto(PD) e que foi a segunda a��o do tipo produto(002)
	    	     	criada no ano de 2009. Existem c�digos ainda onde a sequ�ncia � representada tr�s
	    	     	caracteres: xxx. Por exemplo, PJxxx-2008. Neste caso, o c�digo da a��o n�o foi
	    	     	gerado completamente e ele poder� n�o ser �nico. Uma busca para este c�digo retornaria
	    	     	todos os projetos(PJ) de 2008.
	    	     </ufrn:help>
	    	</td>
	    	
	    </tr>

		<tr>
			<td> <h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaPeriodo}" id="selectBuscaPeriodo" styleClass="noborder" /> </td>
	    	<td> <label for="periodo"> Per�odo de execu��o: </label> </td>
	    	<td>
	    	
	    	<t:inputCalendar value="#{atividadeExtensao.buscaInicio}"
				renderAsPopup="true" renderPopupButtonAsImage="true"
				popupDateFormat="dd/MM/yyyy" size="10" id="dataInicio"
				onchange="javascript:$('formBuscaAtividade:selectBuscaPeriodo').checked = true;" 
				onkeypress="return(formatarMascara(this,event,'##/##/####'));"
				maxlength="10" popupTodayString="Hoje �">
				<f:converter converterId="convertData" />
			</t:inputCalendar>
			a 
			<t:inputCalendar value="#{atividadeExtensao.buscaFim}"
				renderAsPopup="true" renderPopupButtonAsImage="true"
				popupDateFormat="dd/MM/yyyy" size="10" id="dataFim"
				onchange="javascript:$('formBuscaAtividade:selectBuscaPeriodo').checked = true;"
				onkeypress="return(formatarMascara(this,event,'##/##/####'));"
				maxlength="10" popupTodayString="Hoje �">
				<f:converter converterId="convertData" />
			</t:inputCalendar>	
	    	</td>
	    </tr>

		<tr>
			<td> <h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaPeriodoConclusao}" id="selectBuscaPeriodoConclusao" styleClass="noborder" /> </td>
	    	<td> <label for="periodoConclusao">Per�odo de conclus�o: </label> </td>
	    	<td>
	    	
	    	<t:inputCalendar value="#{atividadeExtensao.buscaInicioConclusao}"
				renderAsPopup="true" renderPopupButtonAsImage="true"
				popupDateFormat="dd/MM/yyyy" size="10" id="dataInicioConclusao"
				onchange="javascript:$('formBuscaAtividade:selectBuscaPeriodoConclusao').checked = true;"
				onkeypress="return(formatarMascara(this,event,'##/##/####'))"
				maxlength="10" popupTodayString="Hoje �">
				<f:converter converterId="convertData" />
			</t:inputCalendar>
			a 
			<t:inputCalendar value="#{atividadeExtensao.buscaFimConclusao}"
				renderAsPopup="true" renderPopupButtonAsImage="true"
				popupDateFormat="dd/MM/yyyy" size="10" id="dataFimConclusao"
				onchange="javascript:$('formBuscaAtividade:selectBuscaPeriodoConclusao').checked = true;"
				onkeypress="return(formatarMascara(this,event,'##/##/####'))"
				maxlength="10" popupTodayString="Hoje �">
				<f:converter converterId="convertData" />
			</t:inputCalendar>	
	    	</td>
	    </tr>
	    
	    <tr>
            <td> <h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaPeriodoInicio}" id="selectBuscaPeriodoInicio" styleClass="noborder" /> </td>
            <td> <label for="periodoInicio">Per�odo de in�cio: </label> </td>
            <td>            
	            <t:inputCalendar value="#{atividadeExtensao.buscaInicioExecucao}"
	                renderAsPopup="true" renderPopupButtonAsImage="true"
	                popupDateFormat="dd/MM/yyyy" size="10" id="dataInicioExecucao"
	                onchange="javascript:$('formBuscaAtividade:selectBuscaPeriodoInicio').checked = true;"
	                onkeypress="return(formatarMascara(this,event,'##/##/####'))"
	                maxlength="10" popupTodayString="Hoje �">
	                <f:converter converterId="convertData" />
	            </t:inputCalendar>
	            a 
	            <t:inputCalendar value="#{atividadeExtensao.buscaFimExecucao}"
	                renderAsPopup="true" renderPopupButtonAsImage="true"
	                popupDateFormat="dd/MM/yyyy" size="10" id="dataFimExecucao"
	                onchange="javascript:$('formBuscaAtividade:selectBuscaPeriodoInicio').checked = true;"
	                onkeypress="return(formatarMascara(this,event,'##/##/####'))"
	                maxlength="10" popupTodayString="Hoje �">
	                <f:converter converterId="convertData" />
	            </t:inputCalendar>  
            </td>
        </tr>

		<tr>
			<td> <h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaEdital}" id="selectBuscaEdital" styleClass="noborder"/> </td>
	    	<td> <label for="edital"> Edital: </label> </td>
	    	<td>	    	
	    	 	<h:selectOneMenu id="buscaEdital" value="#{atividadeExtensao.buscaEdital}" 
	    	 		onchange="javascript:$('formBuscaAtividade:selectBuscaEdital').checked = true;">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
	    	 		<f:selectItems value="#{editalExtensao.allCombo}" />
	    	 	</h:selectOneMenu>	    	 
	    	 </td>
	    </tr>

		<tr>
			<td><h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaTipoAtividade}" 
					id="selectBuscaTipoAtividade" styleClass="noborder"/> </td>
	    	<td> <label for="tipoAcao"> Tipo da A��o: </label> </td>
	    	<td>
	    	 	<h:selectManyListbox id="buscaTipoAcao" value="#{atividadeExtensao.buscaTipoAtividade}" size="4"
	    	 		onchange="javascript:$('formBuscaAtividade:selectBuscaTipoAtividade').checked = true;">
	    	 		<f:selectItems value="#{tipoAtividadeExtensao.allCombo}" />
	    	 	</h:selectManyListbox>
	    	 	<ufrn:help img="/img/ajuda.gif">
	    	 		Poder� ser marcada mais de uma op��o, basta 
	    	 		apenas segurar a tecla "Ctrl" e clicar nas op��es.
	    	 		Role para baixo para mais op��es.
	    	     </ufrn:help>   	 
	    	 </td>
	    </tr>

		<tr>
			<td><h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaAreaCNPq}" 
					id="selectBuscaAreaCNPq" styleClass="noborder"/> </td>
	    	<td> <label for="areaCNPQ"> �rea do CNPq: </label> </td>
	    	<td>
		    	<h:selectOneMenu id="buscaAreaCNPq" value="#{atividadeExtensao.buscaAreaCNPq}" 
		    		onchange="javascript:$('formBuscaAtividade:selectBuscaAreaCNPq').checked = true;"> 
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{area.allGrandesAreasCombo}"/>
				</h:selectOneMenu>
	    	 </td>
	    </tr>

		<tr>
			<td><h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaUnidadeProponente}" 
					id="selectBuscaUnidadeProponente" styleClass="noborder"/> </td>
	    	<td> <label for="unidade"> Unidade Proponente: </label> </td>
	    	<td>
				<h:selectOneMenu id="buscaUnidade"
					value="#{atividadeExtensao.buscaUnidade}"
					onchange="javascript:$('formBuscaAtividade:selectBuscaUnidadeProponente').checked = true;"
					readonly="#{atividadeExtensao.readOnly}" style="width: 90%">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{unidade.unidadesProponentesProjetosCombo}" />
				</h:selectOneMenu></td>
	    </tr>

   		<tr>
   			<td>
				<h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaCentro}" id="selectBuscaCentro" styleClass="noborder" />
			</td>
			<td> <label for="centro">Centro da A��o:</label></td>
			<td>
				<h:selectOneMenu id="buscaCentro" value="#{atividadeExtensao.buscaCentro}" style="width: 300px" 
					onchange="javascript:$('formBuscaAtividade:selectBuscaCentro').checked = true;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"  />
					<f:selectItems value="#{unidade.centrosEspecificasEscolas}" />
				</h:selectOneMenu>
			</td>
		</tr>

		<tr>
			<td><h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaAreaTematicaPrincipal}" 
					id="selectBuscaAreaTematicaPrincipal" styleClass="noborder"/>  
			</td>
	    	<td> <label for="unidade"> �rea Tem�tica: </label> </td>
	    	<td>
				<h:selectOneMenu id="buscaAreaTematica" value="#{atividadeExtensao.buscaAreaTematicaPrincipal}" 
					onchange="javascript:$('formBuscaAtividade:selectBuscaAreaTematicaPrincipal').checked = true;">
					<f:selectItem itemValue="0" itemLabel=" -- SELECIONE --" />
					<f:selectItems value="#{areaTematica.allCombo}" />
				</h:selectOneMenu>
	    	</td>
	    </tr>
	    
	    
	    
	    <tr>
			<td>
				<h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaProjetoAssociado}" 
					id="selectBuscaAssociado" styleClass="noborder" />
			</td>
	    	<td> 
	    		Dimens�o Acad�mica: 
	    	</td>
	    	<td>
	    		<h:selectOneMenu value="#{atividadeExtensao.buscaProjetoAssociado}" id="associado"
	    			onchange="javascript:$('formBuscaAtividade:selectBuscaAssociado').checked = true;">
	    			<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItem itemValue="1" itemLabel="PROJETO ASSOCIADO" />
					<f:selectItem itemValue="2" itemLabel="PROJETO ISOLADO" />
 			 	</h:selectOneMenu>
	    	 </td>
	    </tr>
	    

		<tr>
			<td><h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaServidor}" 
					id="selectBuscaServidor" styleClass="noborder" />
			</td>
			<td><label onclick="javascript:$('formBuscaAtividade:selectBuscaServidor').checked = !$('formBuscaAtividade:selectBuscaServidor').checked;">Servidor:</label></td>
			
			<td>
				<h:inputText value="#{atividadeExtensao.membroEquipe.servidor.pessoa.nome}" id="nomeServidor" size="59"
				 onchange="javascript:$('formBuscaAtividade:selectBuscaServidor').checked = true;"/>
				<rich:suggestionbox for="nomeServidor" width="450" height="100" minChars="3" id="suggestionNomeServ" 
						suggestionAction="#{servidorAutoCompleteMBean.autocompleteNomeServidor}" var="_serv" 
						fetchValue="#{_serv.pessoa.nome}">
						 
					<h:column>
						<h:outputText value="#{_serv.pessoa.nome}" />
					</h:column>
				 
			        <f:param name="apenasAtivos" value="false" />
			        <a4j:support event="onselect" >
				        <f:param name="apenasAtivos" value="false" />
						<f:setPropertyActionListener value="#{_serv.id}" target="#{atividadeExtensao.membroEquipe.servidor.id}" />
				    </a4j:support>
				</rich:suggestionbox>
			</td>
		</tr>

		<c:if test="${acesso.extensao}">
			<tr>
				<td><h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaSituacaoAtividade}" 
						id="selectBuscaSituacaoAtividade" styleClass="noborder" />  
				</td>
		    	<td> <label for="situacaoProjeto"> Situa��o da A��o: </label> </td>
		    	<td>
		    		<h:selectManyListbox id="buscaSituacao" value="#{atividadeExtensao.buscaSituacaoAtividade}" size="4" 
		    			onchange="javascript:$('formBuscaAtividade:selectBuscaSituacaoAtividade').checked = true;">
		    	 		<f:selectItems value="#{atividadeExtensao.tipoSituacaoAtividadeCombo}" />
	 			 	</h:selectManyListbox>
	 			 	<ufrn:help img="/img/ajuda.gif">
						Poder� ser marcada mais de uma op��o, basta	apenas
						segurar a tecla "Ctrl" e clicar nas op��es.
		    	 		Role para baixo para mais op��es.
	    	     	</ufrn:help> 
		    	 </td>
		    </tr>

			<tr>
				<td><h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaRegistroSimplificado}" 
						id="selectBuscaRegistroSimplificado" styleClass="noborder"/>  
				</td>
		    	<td> <label for="tipoRegistro"> Tipo de Registro: </label> </td>
		    	<td>
					<h:selectOneMenu id="buscaTipoRegistro" value="#{atividadeExtensao.buscaRegistroSimplificado}" 
		    			onchange="javascript:$('formBuscaAtividade:selectBuscaRegistroSimplificado').checked = true;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItem itemLabel="REGISTRO SIMPLIFICADO DE A��O ANTERIOR" itemValue="1" />
						<f:selectItem itemLabel="PROPOSTA COMPLETA DE A��O DE EXTENS�O" itemValue="2" />
	 				</h:selectOneMenu>
		    	 </td>
		    </tr>
		    
		    
		    <tr>
				<td><h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaAcaoSolicitacaoRenovacao}"	id="selectBuscaAcaoSolicitacaoRenovacao" styleClass="noborder"/>  
				</td>
		    	<td> <label for="projetoRenovado"> Solicita��o de Renova��o: </label> </td>
		    	<td>
					<h:selectOneMenu id="buscaAcaoSolicitacaoRenovacao" value="#{atividadeExtensao.buscaAcaoSolicitacaoRenovacao}" 
		    			onchange="javascript:$('formBuscaAtividade:selectBuscaAcaoSolicitacaoRenovacao').checked = true;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
						<f:selectItem itemLabel="SOLICITA��O DE RENOVA��O" itemValue="1" />
						<f:selectItem itemLabel="PROJETO NOVO" itemValue="2" />
	 				</h:selectOneMenu>
		    	 </td>
		    </tr>
		    
		    
		    

		    <tr>
				<td> <h:selectBooleanCheckbox value="#{atividadeExtensao.checkBuscaFinanciamentoConvenio}" id="selectBuscaFinanciamentoConvenio" styleClass="noborder"/>  </td>
		    	<td> <label for="financiamentoConvenio"> Financiamentos & Conv�nios: </label> </td>
		    	<td>
			    	<h:selectBooleanCheckbox value="#{atividadeExtensao.buscaFinanciamentoInterno}" styleClass="noborder" 
			    		onfocus="javascript:$('formBuscaAtividade:selectBuscaFinanciamentoConvenio').checked = true;" /> Solicitou Financiamento Interno
					<br />
			    	<h:selectBooleanCheckbox value="#{atividadeExtensao.buscaFinanciamentoExterno}"	styleClass="noborder" 
			    		onfocus="javascript:$('formBuscaAtividade:selectBuscaFinanciamentoConvenio').checked = true;" /> Solicitou Financiamento Externo
					<br />
			    	<h:selectBooleanCheckbox value="#{atividadeExtensao.buscaAutoFinanciamento}" styleClass="noborder" 
			    		onfocus="javascript:$('formBuscaAtividade:selectBuscaFinanciamentoConvenio').checked = true;" /> Auto Financiamento
					<br />
			    	<h:selectBooleanCheckbox value="#{atividadeExtensao.buscaConvenioFunpec}" styleClass="noborder" 
			    		onfocus="javascript:$('formBuscaAtividade:selectBuscaFinanciamentoConvenio').checked = true;" /> Conv�nio Funpec
			    	<br />
			    	<h:selectBooleanCheckbox value="#{atividadeExtensao.buscaRecebeuFinanciamentoInterno}" styleClass="noborder" 
			    		onfocus="javascript:$('formBuscaAtividade:selectBuscaFinanciamentoConvenio').checked = true;" /> Recebeu Financiamento Interno
			    	<ufrn:help img="/img/ajuda.gif">A��es que solicitaram e receberam finaciamento interno ap�s aprova��o do presidente do comit�,
			    		ser�o listadas</ufrn:help>
		    	 </td>
		    </tr>	    
		</c:if>

	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<!-- EXIBE PAGINA DE RELATORIO COM DADOS PARA CONTATO COM COORDENADOR -->
				<c:if test="${atividadeExtensao.botaoRelatorioContatoCoordenador == true}">
					<h:commandButton value="Gerar relat�rio" action="#{ atividadeExtensao.localizar }" id="btBuscar" />
				</c:if>
				<!-- EXIBE LISTAGEM NORMAL COMO RESULTADO DA BUSCA -->
				<c:if test="${ atividadeExtensao.botaoRelatorioContatoCoordenador == false}">
					<h:commandButton value="Buscar" action="#{ atividadeExtensao.localizar }" id="btBuscar" />
				</c:if>
				
				<h:commandButton value="Cancelar" action="#{ atividadeExtensao.cancelar }" onclick="#{confirm}" id="btCancelar" />
	    	</td>
	    </tr>
	</tfoot>
	</table>
	
	</h:form>

	<br/>
	<br/>
	