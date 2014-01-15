<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<%@page import="br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria"%>

	<c:if test="${!acesso.monitoria}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	
	<h2><ufrn:subSistema /> > Consultar Monitores</h2>

	<h:outputText value="#{consultarMonitor.create}"/>


 	<h:form id="formBuscaMonitor">

	<table class="formulario" width="90%">
	<caption>Busca por Monitores</caption>
	<tbody>
	    
		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{consultarMonitor.checkBuscaDiscente}"  id="selectBuscaDiscente" />
			</td>
	    	<td> <label> Discente </label> </td>
			<td>
		
			 <h:inputText id="nomeDiscente" value="#{ consultarMonitor.discente.pessoa.nome }" size="60" onchange="javascript:$('formBuscaMonitor:selectBuscaDiscente').checked = true;"/>
			 <h:inputHidden id="idDiscente" value="#{ consultarMonitor.discente.id }"/>
		
			<ajax:autocomplete source="formBuscaMonitor:nomeDiscente" target="formBuscaMonitor:idDiscente"
				baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
				indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=ufrn"
				parser="new ResponseXmlToHtmlListParser()" />
		
			<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
			</td>
		</tr>

		<tr>
			<td><h:selectBooleanCheckbox value="#{consultarMonitor.checkBuscaAno}" id="selectBuscaAno" /></td>
	    	<td> <label for="anoProjeto"> Ano do Projeto </label> </td>
	    	<td> <h:inputText value="#{consultarMonitor.anoReferencia}" size="10" onchange="javascript:$('formBuscaMonitor:selectBuscaAno').checked = true;"/></td>
	    </tr>

		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{consultarMonitor.checkBuscaTipoVinculo}"  id="selectBuscaTipoVinculo" />
			</td>
	    	<td> <label> Tipo de Vínculo do Monitor </label> </td>
	    	<td>
	    	 <h:selectOneMenu value="#{consultarMonitor.tipoVinculo}" style="width: 200px"  onchange="javascript:$('formBuscaMonitor:selectBuscaTipoVinculo').checked = true;">
				<f:selectItems value="#{discenteMonitoria.tipoMonitoriaCombo}"/>
 			 </h:selectOneMenu>
	    	</td>
	    </tr>	


		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{consultarMonitor.checkBuscaMonitorAtivo}"  id="selectBuscaMonitorAtivo" />
			</td>
	    	<td> <label> Situação do Monitor </label> </td>
	    	<td>
	    	 <h:selectOneMenu value="#{consultarMonitor.monitorAtivo}" onchange="javascript:$('formBuscaMonitor:selectBuscaMonitorAtivo').checked = true;">
					<f:selectItem itemValue="" itemLabel="TODOS"  />	    	 
					<f:selectItem itemValue="true" itemLabel="ATIVO"  />
					<f:selectItem itemValue="false" itemLabel="INATIVO"  />					
 			 </h:selectOneMenu>
	    	</td>
	    </tr>	
	    
	    <tr>
			<td>
			<h:selectBooleanCheckbox value="#{consultarMonitor.checkGerarRelatorio}"  id="selectGerarRelatorio" />
			</td>
	    	<td colspan="2"> <label> <b>Gerar Relatório</b></label> </td>
	    </tr>	     
	    

	    
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ consultarMonitor.localizarRelatorioDadosBancarios }"/>
			<h:commandButton value="Cancelar" action="#{ consultarMonitor.cancelar }"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

	</h:form>

	<br/>

	<c:set var="monitores" value="${consultarMonitor.monitores}"/>

	<c:if test="${empty monitores}">
	<center><i> Nenhum monitor localizado </i></center>
	</c:if>


	<c:if test="${not empty monitores}">

		 <table class="listagem">
		    <caption>Monitores Encontrados (${ fn:length(monitores) })</caption>
	
		      <thead>
		      	<tr>
		        	<th>Discente</th>
		        	<th>Banco</th>
		        	<th>Agência</th>
		        	<th>Conta</th>
		        	<th>Operação</th>	        	
		        	<th>Vínculo </th>	        		        	
		        	<th>Situação</th>
		        	
		        </tr>
		 	</thead>
		 	<tbody>
		 	<c:set var="ASSUMIU_MONITORIA" value="<%= String.valueOf(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA) %>" scope="application"/>	 		 	
	       	<c:forEach items="${monitores}" var="monitor" varStatus="status">
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	
	                    <td> ${monitor.discente.matriculaNome} </td>
	                    <td> ${monitor.banco.denominacao}</td>
	                    <td> ${monitor.agencia}</td>                    
	                    <td> ${monitor.conta}</td> 
	                    <td> ${monitor.operacao}</td>                      
	                    <td> ${monitor.tipoVinculo.descricao} </td>
						<td>${(monitor.situacaoDiscenteMonitoria.id == ASSUMIU_MONITORIA) ? '<font color=blue>ATIVO</font>':'<font color=red>INATIVO</font>'} </td>
	              </tr>
	          </c:forEach>
		 	</tbody>
		 </table>

	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>