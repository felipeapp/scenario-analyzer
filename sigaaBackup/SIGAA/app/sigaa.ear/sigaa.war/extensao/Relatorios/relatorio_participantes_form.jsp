<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<script>
function setarLabel(select,id_hidden){
	var indice = select.selectedIndex;
	if (indice > 0)	
		$('form:'+id_hidden).value = select.options[indice].text;
}
</script>

<f:view>
	
	<c:if test="${!acesso.extensao}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	
	<h2><ufrn:subSistema /> &gt; Totais de Participantes Cadastrados em Ações de Extensão</h2>

 	<h:form id="form">
 	
 	<h:inputHidden value="#{relatoriosAtividades.buscaTipoAcao}" id="tipoAcao" />
	<h:inputHidden value="#{relatoriosAtividades.buscaTipoParticipante}" id="tipoParticipante" />
	<h:inputHidden value="#{relatoriosAtividades.buscaNomeUnidade}" id="nomeUnidade" />

	<table class="formulario" width="80%">
	<caption>Informe os Critérios de Busca</caption>
	<tbody>
		
		<tr>
			<td><h:selectBooleanCheckbox value="#{relatoriosAtividades.checkBuscaAno}" id="selectBuscaAno" /></td>
	    	<td width="20%"> Ano da Ação: </td>
	    	<td> <h:inputText value="#{relatoriosAtividades.ano}" maxlength="4" size="4" onfocus="javascript:$('form:selectBuscaAno').checked = true;" onkeyup="return formatarInteiro(this)" id="AnoDaAcao"/></td>
	    </tr>
	     
		<tr>
			<td>
				<h:selectBooleanCheckbox value="#{relatoriosAtividades.checkBuscaTipoAtividade}"  id="selectBuscaTipoAtividade"/>
			</td>
	    	<td width="20%"> Tipo da Ação: </td>
	    	<td>
		    	 <h:selectOneMenu  id="buscaTipo" value="#{relatoriosAtividades.tipoAtividadeExtensao.id}"  onfocus="javascript:$('form:selectBuscaTipoAtividade').checked = true;"
		    	 onchange="javascript:setarLabel(this,'tipoAcao');">					
		    	 	<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
		    	 	<f:selectItems value="#{relatoriosAtividades.tipoAtividadeCombo}" />		    	 	
	 			 </h:selectOneMenu>
	    	 </td>
	    </tr>
	    
	    <tr>
			<td>
				<h:selectBooleanCheckbox value="#{relatoriosAtividades.checkBuscaTipoParticipante}"  id="selectBuscaTipoParticipante"/>
			</td>
	    	<td width="20%">  Tipo de Participante:  </td>
	    	<td>
		    	 <h:selectOneMenu  id="buscaParticipante" value="#{relatoriosAtividades.participante.tipoParticipacao.id}"  onfocus="javascript:$('form:selectBuscaTipoParticipante').checked = true;"
		    	 onchange="javascript:setarLabel(this,'tipoParticipante');">					
		    	 	<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
		    	 	<f:selectItems value="#{relatoriosAtividades.tipoParticipanteCombo}" />		    	 	
	 			 </h:selectOneMenu>
	    	 </td>
	    </tr>
	    
	    <tr>
			<td>
				<h:selectBooleanCheckbox value="#{relatoriosAtividades.checkBuscaUnidade}"  id="selectBuscaUnidade"/>
			</td>
	    	<td width="20%">  Unidade:  </td>
	    	<td>
		    	 <h:selectOneMenu  id="buscaUnidade" value="#{relatoriosAtividades.unidade.id}"  onfocus="javascript:$('form:selectBuscaUnidade').checked = true;" style="width:90%;"
		    	 onchange="javascript:setarLabel(this,'nomeUnidade');">					
		    	 	<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
		    	 	<f:selectItems value="#{unidade.allCombo}" />		    	 	
	 			 </h:selectOneMenu>
	    	 </td>
	    </tr>
	    
	    <tr>
			<td><h:selectBooleanCheckbox value="#{relatoriosAtividades.checkBuscaPeriodo}" id="selectBuscaPeriodo" /></td>
			<td>Período: </td>
			<td>
				<t:inputCalendar size="10" value="#{relatoriosAtividades.dataInicio}" popupDateFormat="dd/MM/yyyy" id="dataInicio" 
				renderAsPopup="true" onkeypress="return(formatarMascara(this,event,'##/##/####'))" maxlength="10" 
				id="inicio" onfocus="javascript:$('form:selectBuscaPeriodo').checked = true;" renderPopupButtonAsImage="true"
				popupTodayString="Hoje é"><f:converter converterId="convertData"/>
				</t:inputCalendar>	
				a
				<t:inputCalendar size="10" value="#{relatoriosAtividades.dataFim}" popupDateFormat="dd/MM/yyyy" id="dataFim"
				renderAsPopup="true" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10"
				renderPopupButtonAsImage="true" id="fim" onfocus="javascript:$('form:selectBuscaPeriodo').checked = true;"
				popupTodayString="Hoje é"><f:converter converterId="convertData"/>
				</t:inputCalendar>
				
			</td>
		</tr>

	    <tr>
			<td><h:selectBooleanCheckbox value="#{relatoriosAtividades.checkGerarRelatorio}"  id="selectGerarRelatorio" /></td>
	    	<td width="20%">  <b>Gerar Relatório</b> </td>
	    	<td></td>	    		    	
	    </tr>    

	    
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ relatoriosAtividades.relatorioTotaisParticipantes }"/>
			<h:commandButton value="Cancelar" action="#{ relatoriosAtividades.cancelar }" onclick="#{confirm}"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

	</h:form>

	<br/>

	<c:set var="result" value="${relatoriosAtividades.resultadoQuantitativo}"/>

	<c:if test="${empty result}">
	<center><i> Nenhuma Ação de Extensão localizada </i></center>
	</c:if>


	<c:if test="${not empty result}">

		 <table class="listagem">
		    <caption> LISTA DE AÇÕES <c:if test="${(relatoriosAtividades.checkBuscaSituacaoAtividade)}">COM SITUAÇÃO '${relatoriosAtividades.situacaoAtividade.descricao}'</c:if></caption>
	
		      <thead>
		      	<tr>
		      		<th style="text-align:center">Ano</th>
					<th>Tipo Ação</th>
		        					
					<th>Unidade</th>
					<th>Tipo Participante</th>
					<th style="text-align:right">Quantidade</th>		        	
		        </tr>
		 	</thead>
		 	<tbody>
		 	<c:set value="0" var="total_publico"/>
	       	<c:forEach items="${result}" var="r" varStatus="status">	       		
	       		
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	                    <td style="text-align:center"> ${r[4]}</td>
	                    <td> ${r[0]}</td>
	                    	                    
	                    <td> ${r[3]} </td>
	                    <td> ${r[1]} </td>
	                    <td style="text-align:right"> ${r[2]} </td>
	                    <c:set value="${total_publico + r[2]}" var="total_publico"/>                    
	              </tr>
	          </c:forEach>
	          <tr class="linhaPar">
	              		<td></td>
	                    <td></td>
	                    <td></td>
	                    <td><b>Total</b> </td>
	                    <td style="text-align:right"><b><fmt:formatNumber value="${total_publico}"/></b></td>                    	                    	                 
	              </tr>	              
		 	</tbody>
		 </table>
	
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>