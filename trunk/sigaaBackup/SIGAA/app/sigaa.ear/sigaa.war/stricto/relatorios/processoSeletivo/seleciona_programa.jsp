<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema/> > Relatório de Processos Seletivos (Demandas x Vagas)</h2>

<h:form id="form">
<table align="center" class="formulario" width="70%">
	<caption class="listagem">Dados do Relatório</caption>
	<c:if test="${!processoSeletivo.portalPpg}">	
		<tr>
			<th class="rotulo" width="20%">Programa: </th>
			<td><h:outputText value="#{processoSeletivo.unidade.sigla}"/> - <h:outputText value="#{processoSeletivo.unidade.nome}"/></td>
		</tr>
	</c:if>		
	
	<c:if test="${processoSeletivo.portalPpg}">	
		<tr>
			<th width="20%">Programa:</th>
			<td><h:selectOneMenu value="#{processoSeletivo.unidade.id}" id="programa">
				<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
				<f:selectItems value="#{unidade.allDeptosProgramasEscolasCombo}" />
			</h:selectOneMenu></td>
		</tr>
	</c:if>	
	
	<tr>
		<th> Nível de Ensino: </th>
		<td> 
			<h:selectOneMenu id="nivelEnsino" value="#{processoSeletivo.nivel}">
				<f:selectItem itemValue="0" itemLabel="-- TODOS --" />
				<f:selectItems value="#{nivelEnsino.niveisSuperiorCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>	
	
	<tr>
		<th> Data de Início: </th>		
		<td>
			<t:inputCalendar 
				value="#{processoSeletivo.dataInicio}" 
				maxlength="10" size="10" 
				onkeypress="return(formataData(this,  event))"
				renderAsPopup="true" renderPopupButtonAsImage="true" id="dtInicio"
				popupDateFormat="dd/MM/yyyy" title="Data Início">
				
				<f:converter converterId="convertData"/>
				
			</t:inputCalendar>
			
			<h:outputText value="Data de Fim:"/>
			<t:inputCalendar 
				value="#{processoSeletivo.dataFim}" 
				maxlength="10" 
				size="10" 
				onkeypress="return(formataData(this,  event))"
				renderAsPopup="true" renderPopupButtonAsImage="true" id="dtFim"
				popupDateFormat="dd/MM/yyyy" title="Data Fim">
				
				<f:converter converterId="convertData"/>
				
			</t:inputCalendar>			
		</td>
	</tr>
	<tfoot>
		<tr>
			<td colspan="3" align="center">
				<h:commandButton id="gerarRelatorio" value="Gerar Relatório" action="#{processoSeletivo.listaDemandaVagas}"/> 
				<h:commandButton value="Cancelar" action="#{processoSeletivo.cancelar}" id="cancelar" onclick="#{confirm}" />
			</td>
		</tr>
	</tfoot>
</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>