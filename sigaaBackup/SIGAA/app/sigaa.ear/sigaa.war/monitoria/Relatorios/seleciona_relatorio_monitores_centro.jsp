<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<f:view>
	
	<h2><ufrn:subSistema /> > Relatório de Monitores por Centro</h2>
	 	<h:form id="formBuscaProjeto">


	<table class="formulario" width="100%">
	<caption>Busca por Monitores</caption>
	<tbody>
	    
		 <tr>
			<td>
			<h:selectBooleanCheckbox value="#{comissaoMonitoria.checkBuscaCentro}" id="selectBuscaCentro"/>
			</td>
			
	    	<td> <label for="nomeProjeto"> Centro do Projeto: </label> </td>
	    	
	    	<td>
	    	<h:selectOneMenu id="selectUnidade" onfocus="javascript:$('formBuscaProjeto:selectBuscaCentro').checked = true;" 
	    		value="#{comissaoMonitoria.unidade.id}">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM CENTRO --"  />
				<f:selectItems value="#{unidade.centrosEspecificasEscolas}" />
			</h:selectOneMenu>
	    </tr>		    
	    
		 <tr>
			<td>
				<h:selectBooleanCheckbox value="#{comissaoMonitoria.checkBuscaAno}" id="selectBuscaAno"/>
			</td>
			
	    	<td><label for="anoProjeto"> Ano do Projeto: </label></td>
	    	
	    	<td>
	    	<h:inputText id="inputAno" value="#{comissaoMonitoria.ano}" onfocus="javascript:$('formBuscaProjeto:selectBuscaAno').checked = true;" size="6" maxlength="4" onkeyup="formatarInteiro(this)" />
	    </tr>		    
	
		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{comissaoMonitoria.checkBuscaTipoMonitor}"  id="selectBuscaTipoMonitor" />
			</td>
	    	<td> <label> Vínculo do Monitor: </label> </td>
	    	<td>

	    	 <h:selectOneMenu value="#{comissaoMonitoria.idTipoMonitor}" id="selecaoTipoMonitor" style="width: 200px"  onchange="javascript:$('formBuscaProjeto:selectBuscaTipoMonitor').checked = true;">
	    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM TIPO --"  />
				<f:selectItems value="#{discenteMonitoria.tipoMonitoriaCombo}"/>
 			 </h:selectOneMenu>

	    	 </td>
	    </tr>	

		 <tr>
			<td>
				<h:selectBooleanCheckbox value="#{comissaoMonitoria.checkBuscaTipoStatusDiscente}" id="selectStatusDisc"/>
			</td>
			
	    	<td><label for="statusDic"> Status Discente Monitoria: </label></td>
	    	
	    	<td>
	    	 <h:selectOneMenu value="#{comissaoMonitoria.idSituacaoDiscMonit}" id="selecaoStatusDisc" style="width: 200px" 
	    	 	onchange="javascript:$('formBuscaProjeto:selectStatusDisc').checked = true;">
	    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM TIPO --"  />
				<f:selectItems value="#{discenteMonitoria.allSituacaoDiscenteMonitoriaCombo}"/>
 			 </h:selectOneMenu>
			</td>
	    </tr>		    
	
		 <tr>
			<td>
				<h:selectBooleanCheckbox value="#{comissaoMonitoria.checkBuscaTipoStatusProj}" id="selectStatusProj"/>
			</td>
			
	    	<td><label for="StatusProj"> Status Projeto Monitoria: </label></td>
	    	
	    	<td>
	    	 <h:selectOneMenu value="#{comissaoMonitoria.idSituacaoProj}" id="selecaoStatusProj" style="width: 200px" 
	    	 	onchange="javascript:$('formBuscaProjeto:selectStatusProj').checked = true;">
	    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM TIPO --"  />
				<f:selectItems value="#{projetoMonitoria.tipoSituacaoProjetoCombo}" />
 			 </h:selectOneMenu>
			</td>
	    </tr>		    
	
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Gerar Relatório" action="#{ comissaoMonitoria.geraRelatorioMonitoresPorDepartamento }" />
				<h:commandButton value="Cancelar" action="#{ comissaoMonitoria.cancelar }" onclick="#{confirm}"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>	
	
	
	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
