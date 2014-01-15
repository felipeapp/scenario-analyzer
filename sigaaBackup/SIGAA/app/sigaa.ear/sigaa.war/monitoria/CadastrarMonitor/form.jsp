<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<h:messages showDetail="true" showSummary="true"/>
<h:outputText value="#{cadMonitor.create}" />

<h2><ufrn:subSistema /> >  Cadastrar Monitor </h2>
<h:form id="form_cadastro_monitor" >

	<table class="formulario" width="100%">
		<caption>Cadastrar Novo Monitor</caption>
	
		<tr>
				<th><label> Título do Projeto: </label></th>
				<td><b> <h:outputText value="#{cadMonitor.projeto.anoTitulo}"/></b> </td>
		</tr>		    

        <tr>
                <th><label> Início do projeto: </label></th>
                <td><b> <h:outputText value="#{cadMonitor.projeto.projeto.dataInicio}"/></b> </td>
        </tr>           

        <tr>
                <th><label> Fim do projeto: </label></th>
                <td><b> <h:outputText value="#{cadMonitor.projeto.projeto.dataFim}"/></b> </td>
        </tr>           

	
		<tr>
			<th class="required"> Discente: </th>
			<td>
		
			 <h:inputHidden id="id_discente" value="#{ cadMonitor.idDiscente }"/>
			 <h:inputText id="nome_discente" value="#{ cadMonitor.nomeDiscente }" size="80" />
		
			<ajax:autocomplete source="form_cadastro_monitor:nome_discente" target="form_cadastro_monitor:id_discente"
				baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
				indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=G"
				parser="new ResponseXmlToHtmlListParser()" />
		
			<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
			</td>
		</tr>

		<tr>
			<th class="required"> Tipo de Vínculo: </th>
			<td>
				<h:selectOneMenu value="#{cadMonitor.tipoVinculo}" id="tipo_vinculo">
					<f:selectItem itemLabel="-- OPÇÕES --" itemValue="-1"/>
					<f:selectItems value="#{discenteMonitoria.tiposAtivosMonitoriaCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		
		
		<tr>
			<th class="required"> Situação do Monitor: </th>
			<td>
  				<h:selectOneMenu value="#{cadMonitor.obj.situacaoDiscenteMonitoria.id}" id="situacao_monitor" >
					<f:selectItem itemLabel="-- OPÇÕES --" itemValue="-1"/>
					<f:selectItems value="#{discenteMonitoria.allSituacaoDiscenteMonitoriaCombo}"/>
				</h:selectOneMenu>
  			</td>
  		</tr>
  		
  		
  		<tr>
			<th class="required">Data Início:</th>
			<td>
				<t:inputCalendar id="data_inicio_monitor_" value="#{cadMonitor.obj.dataInicio}" 
					renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
					size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
					<f:converter converterId="convertData" />
				</t:inputCalendar>
			</td>
		</tr>

  		<tr>
			<th class="required">Data Fim:</th>
			<td>
				<t:inputCalendar id="data_fim_monitor_" value="#{cadMonitor.obj.dataFim}" 
					renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
					size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" popupTodayString="Hoje é">
					<f:converter converterId="convertData" />
				</t:inputCalendar>
			</td>
		</tr>

		
		<tr>
			<th> Observação: </th>
			<td>
				<h:inputTextarea value="#{cadMonitor.obj.observacao}" rows="4" style="width:98%" id="observacao"/>
			</td>
		</tr>
	
	<c:set var="membros" value="${cadMonitor.docentes}"/> 
	<c:if test="${empty membros}">
		<tr>
			<th> Orientadores: </th>
			<td>
				<font color="red">Não há docentes ativos cadastrados no projeto selecionado</font>		
			</td>
		</tr>   
   	</c:if>	

	
	<c:if test="${not empty membros}">
		<tr>
			<th class="required"> Orientadores: </th>
			<td>
				<t:dataTable value="#{cadMonitor.docentes}" var="docente" width="94%">
		
					<t:column width="5%" styleClass="centerAlign">
						<h:selectBooleanCheckbox value="#{docente.selecionado}" id="docente_selecionado_"/>
					</t:column>
				
					<t:column styleClass="centerAlign">
						<f:facet name="header"><f:verbatim>Orientadores</f:verbatim></f:facet>
						<h:outputText value="#{docente.servidor.siapeNome}"/>
					</t:column>

					<t:column>
						<f:facet name="header">
							<f:verbatim>Início</f:verbatim>
						</f:facet>
						<t:inputCalendar
							size="10"
							maxlength="10"
							value="#{docente.dataInicioOrientacao}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							onkeypress="return(formataData(this,event))"
							renderPopupButtonAsImage="true">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>																				
					</t:column>

					<t:column>
						<f:facet name="header">
							<f:verbatim>Fim</f:verbatim>
						</f:facet>
						<t:inputCalendar 
							size="10"
							maxlength="10"
							value="#{docente.dataFimOrientacao}"
							popupDateFormat="dd/MM/yyyy"
							onkeypress="return(formataData(this,event))"
							renderAsPopup="true"
							renderPopupButtonAsImage="true">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>										
					</t:column>
					
				</t:dataTable>			
			</td>
		</tr>	
	</c:if>

	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton action="#{cadMonitor.cadastrar}" value="Cadastrar" id="botao_cadastrar"/>
				<h:commandButton action="#{cadMonitor.cancelar}" value="Cancelar" id="botao_cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
</h:form>
<center>   <h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>