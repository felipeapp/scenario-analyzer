<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	
	<c:if test="${!acesso.extensao}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	
	<h2><ufrn:subSistema /> > Consultar P�blico Estimado x P�blico Atendido</h2>

	<div class="descricaoOperacao">
		Caro Usu�rio, <br/>
		todas as buscas deste relat�rio s�o realizadas com base nas datas de in�cio e fim das A��es de Extens�o.<br/>
		O ano refer�ncia informado durante o cadastro da proposta n�o � considerado.
	</div>


 	<h:form id="form">
	<h:inputHidden value="#{relatoriosAtividades.buscaNomeSituacao}" id="nomeSituacao" />
	
	<table class="formulario" width="75%">
	<caption>P�blico Estimado x P�blico Atendido</caption>
	<tbody>


		<tr>
			<td><h:selectBooleanCheckbox value="#{relatoriosAtividades.checkBuscaPeriodo}" id="selectBuscaPeriodo" /></td>
			<td>Iniciam no per�odo: </td>
			<td>
				<t:inputCalendar size="10" value="#{relatoriosAtividades.dataInicio}" renderAsPopup="true" 
				renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" maxlength="10" id="inicio" 
				onfocus="javascript:$('form:selectBuscaPeriodo').checked = true;" popupTodayString="Hoje �"
				onkeypress="return(formatarMascara(this,event,'##/##/####'))" >
				<f:converter converterId="convertData"/>
				</t:inputCalendar>	
				a	
				<t:inputCalendar size="10" value="#{relatoriosAtividades.dataFim}" popupDateFormat="dd/MM/yyyy" 
				renderAsPopup="true" renderPopupButtonAsImage="true" maxlength="10" id="fim" popupTodayString="Hoje �"
				onfocus="javascript:$('form:selectBuscaPeriodo').checked = true;" onkeypress="return(formatarMascara(this,event,'##/##/####'))" >
				<f:converter converterId="convertData"/>
				</t:inputCalendar>
				<ufrn:help img="/img/ajuda.gif">Ao marcar esta op��o ser�o retornadas todas as A��es de Extens�o que iniciam no per�odo informado.</ufrn:help>
			</td>
		</tr>


		<tr>
			<td><h:selectBooleanCheckbox value="#{relatoriosAtividades.checkBuscaPeriodoConclusao}" id="selectBuscaPeriodoConclusao" /></td>
			<td>Concluem no per�odo: </td>
			<td>
				<t:inputCalendar size="10" value="#{relatoriosAtividades.dataInicioConclusao}" renderAsPopup="true" 
				renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" maxlength="10" id="inicioConclusao" 
				onfocus="javascript:$('form:selectBuscaPeriodoConclusao').checked = true;" popupTodayString="Hoje �"
				onkeypress="return(formatarMascara(this,event,'##/##/####'))" >
				<f:converter converterId="convertData"/>
				</t:inputCalendar>	
				a	
				<t:inputCalendar size="10" value="#{relatoriosAtividades.dataFimConclusao}" popupDateFormat="dd/MM/yyyy" 
				renderAsPopup="true" renderPopupButtonAsImage="true" maxlength="10" id="fimConclusao" popupTodayString="Hoje �"
				onfocus="javascript:$('form:selectBuscaPeriodoConclusao').checked = true;" onkeypress="return(formatarMascara(this,event,'##/##/####'))" >
				<f:converter converterId="convertData"/>
				</t:inputCalendar>
				<ufrn:help img="/img/ajuda.gif">Ao marcar esta op��o ser�o retornadas todas as A��es de Extens�o que concluem no per�odo informado.</ufrn:help>
			</td>
		</tr>

		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{relatoriosAtividades.checkBuscaSituacaoAtividade}"  id="selectBuscaSituacaoAtividade" />
			</td>
	    	<td> <label> Situa��o Atual da A��o: </label> </td>
	    	<td>
		    	 <h:selectOneMenu  id="buscaSituacao" value="#{relatoriosAtividades.situacaoAtividade.id}" 
		    	 onfocus="javascript:$('form:selectBuscaSituacaoAtividade').checked = true;"
		    	 onchange="javascript:if(this.selectedIndex > 0) $('form:nomeSituacao').value = this.options[this.selectedIndex].text;">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
		    	 	<f:selectItems value="#{tipoSituacaoProjeto.situacoesAcaoExtensaoValidas}" />
	 			 </h:selectOneMenu>

	    	 </td>
	    </tr>

	    <tr>
			<td>
			<h:selectBooleanCheckbox value="#{relatoriosAtividades.checkGerarRelatorio}"  id="selectGerarRelatorio" />
			</td>
	    	<td colspan="2"> <label> <b>Gerar Relat�rio</b></label> </td>
	    </tr>    
	    
	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
			<h:commandButton value="Buscar" action="#{ relatoriosAtividades.relatorioPublicoEstimadoAtendido }"/>
			<h:commandButton value="Cancelar" action="#{ relatoriosAtividades.cancelar }" onclick="#{confirm}"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>

	</h:form>

	<br/>

	<c:set var="result" value="${relatoriosAtividades.resultadoQuantitativo}"/>

	<c:if test="${empty result}">
	<center><i> Nenhuma a��o de extens�o localizada </i></center>
	</c:if>


	<c:if test="${not empty result}">

		 <table class="listagem">
		    <caption> A��ES LOCALIZADAS <c:if test="${(relatoriosAtividades.checkBuscaSituacaoAtividade)}">COM SITUA��O '${relatoriosAtividades.situacaoAtividade.descricao}'</c:if></caption>
	
		      <thead>
		      	<tr>
					<th style="text-align:center">Ano</th>
		        	<th style="text-align:center">M�s de Conclus�o</th>
		        	<th>Tipo de A��o</th>
		        	<th>Situa��o</th>
					<th style="text-align:right">N� de A��es</th>
		        	<th style="text-align:right">P�blico Estimado</th>
		        	<th style="text-align:right">P�blico Atendido</th>
		        </tr>
		 	</thead>
		 	<tbody>
		 	<c:set value="0" var="total_publico_estimado"/>
		 	<c:set value="0" var="total_publico_atendido"/>
		 	<c:set value="0" var="total_acoes"/>
	       	<c:forEach items="${result}" var="r" varStatus="status">
	       		
	       		<c:set value="${total_acoes + r[4]}" var="total_acoes"/>
	       		<c:set value="${total_publico_estimado + r[5]}" var="total_publico_estimado"/>
	       		<c:set value="${total_publico_atendido + r[6]}" var="total_publico_atendido"/>
	       		
	               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	                    <td style="text-align:center"> ${r[1]}</td>
	                    <td style="text-align:center"> ${r[2]}</td>
	                    <td> ${r[0]} </td>
	                    <td> ${r[3]}</td>                    
	                    <td style="text-align:right" width="12%"><fmt:formatNumber value="${r[4]}"/></td>
	                    <td style="text-align:right" width="8%"><fmt:formatNumber value="${r[5]}"/></td>
	                    <td style="text-align:right" width="8%"><fmt:formatNumber value="${r[6]}"/></td>
	              </tr>
	          </c:forEach>
	              <tr class="linhaPar">
	              		<td><b>Total</b> </td>
	                    <td></td>
	                    <td></td>
	                    <td></td>
	                    <td style="text-align:right"><b><fmt:formatNumber value="${total_acoes}"/></b></td>
	                    <td style="text-align:right"><b><fmt:formatNumber value="${total_publico_estimado}"/></b></td>
	                    <td style="text-align:right"><b><fmt:formatNumber value="${total_publico_atendido}"/></b></td>	                    	                 
	              </tr>
		 	</tbody>
		 </table>
	
	</c:if>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>