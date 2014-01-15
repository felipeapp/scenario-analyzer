<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<a4j:keepAlive beanName="consultarMonitor" />

<h:form id="frmBuscarMonitor">

	<table class="formulario" width="90%">
	<caption>Busca por Monitores</caption>
	<tbody>
	    
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{consultarMonitor.checkBuscaProjeto}" id="selectBuscaProjeto"/>
			</td>		
			<td><label> Projeto: </label></td>
			<td>
				<h:inputText id="tituloProjeto" value="#{consultarMonitor.tituloProjeto }" size="60"  onfocus="javascript:$('frmBuscarMonitor:selectBuscaProjeto').checked = true;"/>
			</td>
		</tr>		
		
		<tr>
			<td><h:selectBooleanCheckbox value="#{consultarMonitor.checkBuscaAno}" id="selectBuscaAno" /></td>
	    	<td> <label for="anoProjeto"> Ano do Projeto: </label> </td>
	    	<td> <h:inputText label="Ano" value="#{consultarMonitor.anoReferencia}" size="4" maxlength="4" 
	    	      onfocus="javascript:$('frmBuscarMonitor:selectBuscaAno').checked = true;" 
	    	      onkeyup="return formatarInteiro(this);" />
	    	</td>
	    </tr>
		    
	    <tr>
			<td>
			<h:selectBooleanCheckbox value="#{consultarMonitor.checkBuscaOrientador}" id="selectBuscaOrientador"/>
			</td>
	    	<td> <label> Orientador: </label> </td>
	    	<td> 
	    	
				<h:inputText value="#{consultarMonitor.servidor.pessoa.nome}" id="nome" size="60" onfocus="javascript:$('frmBuscarMonitor:selectBuscaOrientador').checked = true;"/>
				<h:inputHidden value="#{consultarMonitor.servidor.id}" id="idServidor"/>
	
				<ajax:autocomplete source="frmBuscarMonitor:nome" target="frmBuscarMonitor:idServidor"
					baseUrl="/sigaa/ajaxServidor" className="autocomplete"
					indicator="indicator" minimumCharacters="3" parameters="tipo=todos,inativos=true"
					parser="new ResponseXmlToHtmlListParser()" />
	
				<span id="indicator" style="display:none; "> <img src="/sigaa/img/indicator.gif" /> </span>	    	
	    	
	    	</td>
	    </tr>
	    
		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{consultarMonitor.checkBuscaDiscente}"  id="selectBuscaDiscente" />
			</td>
	    	<td> <label> Discente: </label> </td>
			<td>
		
			 <h:inputText id="nomeDiscente" value="#{ consultarMonitor.discente.pessoa.nome }" size="60" onfocus="javascript:$('frmBuscarMonitor:selectBuscaDiscente').checked = true;"/>
			 <h:inputHidden id="idDiscente" value="#{ consultarMonitor.discente.id }"/>
		
			<ajax:autocomplete source="frmBuscarMonitor:nomeDiscente" target="frmBuscarMonitor:idDiscente"
				baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
				indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=G"
				parser="new ResponseXmlToHtmlListParser()" />
		
			<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
			</td>
		</tr>

		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{consultarMonitor.checkBuscaSituacao}"  id="selectBuscaSituacao" />
			</td>
	    	<td> <label> Situação do Monitor: </label> </td>
	    	<td>

	    	 <h:selectOneMenu value="#{consultarMonitor.situacaoMonitor.id}"  onfocus="javascript:$('frmBuscarMonitor:selectBuscaSituacao').checked = true;">
	    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"  />
				<f:selectItems value="#{consultarMonitor.situacaoDiscenteMonitoriaCombo}"/>
 			 </h:selectOneMenu>

	    	 </td>
	    </tr>
	    
	    <tr>
			<td> <h:selectBooleanCheckbox value="#{consultarMonitor.checkBuscaPeriodoEntrada}" id="selectBuscaPeriodoEntrada" styleClass="noborder"/> </td>
	    	<td> <label for="periodo"> Assumiu Monitoria: </label> </td>
	    	<td>	    	
	    	  de <t:inputCalendar 
	    	  		id="dataInicio"
	    	  		value="#{consultarMonitor.buscaDataInicioEntrada}" 
					renderAsPopup="true"
					renderPopupButtonAsImage="true"
					size="10"
					onchange="javascript:$('frmBuscarMonitor:selectBuscaPeriodoEntrada').checked = true;"	
					maxlength="10"
					popupDateFormat="dd/MM/yyyy"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))">
					<f:converter converterId="convertData"/>
				</t:inputCalendar>
				
			até <t:inputCalendar
					id="dataFim"
					value="#{consultarMonitor.buscaDataFimEntrada}" 
					renderAsPopup="true"
					renderPopupButtonAsImage="true"
					size="10"
					onkeypress="return(formatarMascara(this,event,'##/##/####'))"
					maxlength="10"
					popupDateFormat="dd/MM/yyyy">
					<f:converter converterId="convertData"/>
				</t:inputCalendar>	    	
	    	</td>
	    </tr>

		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{consultarMonitor.checkBuscaCurso}"  id="selectBuscaCurso" />
			</td>
	    	<td> <label> Curso do Monitor: </label> </td>
	    	<td>

	    	 <h:selectOneMenu value="#{consultarMonitor.curso.id}"  onfocus="javascript:$('frmBuscarMonitor:selectBuscaCurso').checked = true;">
	    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"  />
				<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
 			 </h:selectOneMenu>

	    	 </td>
	    </tr>

		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{consultarMonitor.checkBuscaTipoVinculo}"  id="selectBuscaTipoVinculo" />
			</td>
	    	<td> <label> Tipo de Vínculo do Monitor: </label> </td>
	    	<td>

	    	 <h:selectOneMenu value="#{consultarMonitor.tipoVinculo}"   onfocus="javascript:$('frmBuscarMonitor:selectBuscaTipoVinculo').checked = true;">
	    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"  />
				<f:selectItems value="#{discenteMonitoria.tipoMonitoriaCombo}"/>
 			 </h:selectOneMenu>

	    	 </td>
	    </tr>	
	    
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ consultarMonitor.localizar }"/>
			<h:commandButton value="Cancelar" action="#{ consultarMonitor.cancelar }" onclick="#{confirm}"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

</h:form>

<c:set var="monitores" value="#{consultarMonitor.monitores}"/>

<br/>