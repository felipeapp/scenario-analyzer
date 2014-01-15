<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	
	<c:if test="${!acesso.extensao}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	
	<h2><ufrn:subSistema /> > Consultar Público Estimado x Público Atendido</h2>

	<div class="descricaoOperacao">
		Caro Usuário, <br/>
		todas as buscas deste relatório são realizadas com base nas datas de início e fim das Ações de Extensão.<br/>
		O ano referência informado durante o cadastro da proposta não é considerado.
	</div>


 	<h:form id="form">
	<h:inputHidden value="#{relatoriosAtividades.buscaNomeSituacao}" id="nomeSituacao" />
	
	<table class="formulario" width="75%">
	<caption>Público Estimado x Público Atendido</caption>
	<tbody>


		<tr>
			<td><h:selectBooleanCheckbox value="#{relatoriosAtividades.checkBuscaPeriodo}" id="selectBuscaPeriodo" /></td>
			<td>Iniciam no período: </td>
			<td>
				<t:inputCalendar size="10" value="#{relatoriosAtividades.dataInicio}" renderAsPopup="true" 
				renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" maxlength="10" id="inicio" 
				onfocus="javascript:$('form:selectBuscaPeriodo').checked = true;" popupTodayString="Hoje é"
				onkeypress="return(formatarMascara(this,event,'##/##/####'))" >
				<f:converter converterId="convertData"/>
				</t:inputCalendar>	
				a	
				<t:inputCalendar size="10" value="#{relatoriosAtividades.dataFim}" popupDateFormat="dd/MM/yyyy" 
				renderAsPopup="true" renderPopupButtonAsImage="true" maxlength="10" id="fim" popupTodayString="Hoje é"
				onfocus="javascript:$('form:selectBuscaPeriodo').checked = true;" onkeypress="return(formatarMascara(this,event,'##/##/####'))" >
				<f:converter converterId="convertData"/>
				</t:inputCalendar>
				<ufrn:help img="/img/ajuda.gif">Ao marcar esta opção serão retornadas todas as Ações de Extensão que iniciam no período informado.</ufrn:help>
			</td>
		</tr>


		<tr>
			<td><h:selectBooleanCheckbox value="#{relatoriosAtividades.checkBuscaPeriodoConclusao}" id="selectBuscaPeriodoConclusao" /></td>
			<td>Concluem no período: </td>
			<td>
				<t:inputCalendar size="10" value="#{relatoriosAtividades.dataInicioConclusao}" renderAsPopup="true" 
				renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" maxlength="10" id="inicioConclusao" 
				onfocus="javascript:$('form:selectBuscaPeriodoConclusao').checked = true;" popupTodayString="Hoje é"
				onkeypress="return(formatarMascara(this,event,'##/##/####'))" >
				<f:converter converterId="convertData"/>
				</t:inputCalendar>	
				a	
				<t:inputCalendar size="10" value="#{relatoriosAtividades.dataFimConclusao}" popupDateFormat="dd/MM/yyyy" 
				renderAsPopup="true" renderPopupButtonAsImage="true" maxlength="10" id="fimConclusao" popupTodayString="Hoje é"
				onfocus="javascript:$('form:selectBuscaPeriodoConclusao').checked = true;" onkeypress="return(formatarMascara(this,event,'##/##/####'))" >
				<f:converter converterId="convertData"/>
				</t:inputCalendar>
				<ufrn:help img="/img/ajuda.gif">Ao marcar esta opção serão retornadas todas as Ações de Extensão que concluem no período informado.</ufrn:help>
			</td>
		</tr>

		<tr>
			<td>
			<h:selectBooleanCheckbox value="#{relatoriosAtividades.checkBuscaSituacaoAtividade}"  id="selectBuscaSituacaoAtividade" />
			</td>
	    	<td> <label> Situação Atual da Ação: </label> </td>
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
	    	<td colspan="2"> <label> <b>Gerar Relatório</b></label> </td>
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
	<center><i> Nenhuma ação de extensão localizada </i></center>
	</c:if>


	<c:if test="${not empty result}">

		 <table class="listagem">
		    <caption> AÇÕES LOCALIZADAS <c:if test="${(relatoriosAtividades.checkBuscaSituacaoAtividade)}">COM SITUAÇÃO '${relatoriosAtividades.situacaoAtividade.descricao}'</c:if></caption>
	
		      <thead>
		      	<tr>
					<th style="text-align:center">Ano</th>
		        	<th style="text-align:center">Mês de Conclusão</th>
		        	<th>Tipo de Ação</th>
		        	<th>Situação</th>
					<th style="text-align:right">Nº de Ações</th>
		        	<th style="text-align:right">Público Estimado</th>
		        	<th style="text-align:right">Público Atendido</th>
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