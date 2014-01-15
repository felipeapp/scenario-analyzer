<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<link rel="stylesheet" type="text/css" href="/sigaa/css/ensino/detalhes_discente.css"/>
<script type="text/javascript" src="/sigaa/javascript/graduacao/busca_discente.js"> </script>

<f:view>
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Consulta de Defesas</h2>
	
	<h:form id="formulario">
	
	<a4j:keepAlive beanName="consultarDefesaMBean"/>
	
	<c:if test="${consultarDefesaMBean.ehCatalogacao || consultarDefesaMBean.associacaoComCatalogacao}">
		<a4j:keepAlive beanName="catalogacaoMBean"/>
		<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>
	</c:if>
	
	<table class="formulario" width=80%>
		<caption> Informe os critérios de busca</caption>
		
		<tbody>
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{consultarDefesaMBean.checkPrograma}" styleClass="noborder" id="checkPrograma"/>
				</td>
				
				<td> Programa:</td>
				<td colspan="2">
					<h:selectOneMenu binding="#{consultarDefesaMBean.selectUnidadeGestora}" id="programa" onchange="$('formulario:checkPrograma').checked = true;">
						<f:selectItems value="#{consultarDefesaMBean.unidades}" id="itensUnidadeGestora"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{consultarDefesaMBean.checkTipoBanca}" styleClass="noborder" id="checkTipoBanca"/>
				</td>
				
				<td> Tipo da Banca: </td>
				
				<td colspan="3"> 
					<h:selectOneRadio id="tipoBanca" value="#{consultarDefesaMBean.tipoBanca}" onchange="$('formulario:checkTipoBanca').checked = true;">
							<f:selectItem itemLabel="Qualificação" itemValue="1" />
							<f:selectItem itemLabel="Defesa" itemValue="2" />
					</h:selectOneRadio>
				</td>
			</tr>
			
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{consultarDefesaMBean.checkNivelEnsino}" styleClass="noborder" id="checkNivelEnsino"/>
				</td>
				
				<td> Nível de Ensino: </td>
				
				<td colspan="3"> 
					<h:selectOneRadio id="nivelEnsino" value="#{consultarDefesaMBean.nivelBusca}" onchange="$('formulario:checkNivelEnsino').checked = true;">
							<f:selectItems value="#{nivelEnsino.strictoCombo}"/>
					</h:selectOneRadio>
				</td>
			</tr>
			
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{consultarDefesaMBean.checkDataInicio}" styleClass="noborder" id="checDataInicio"/>
				</td>
				
				<td> Data Início: </td>
				
				<td colspan="3">
					<t:inputCalendar 
						value="#{consultarDefesaMBean.dataInicio}" 
						maxlength="10" size="10" 
						onkeypress="return(formataData(this,  event))"
						renderAsPopup="true" renderPopupButtonAsImage="true" onfocus="$('formulario:checDataInicio').checked = true;" 
						id="dtInicio"
						onchange="$('formulario:checDataInicio').checked = true;" 
						popupDateFormat="dd/MM/yyyy" title="Data Início">
						
						<f:converter converterId="convertData"/>
						
					</t:inputCalendar>
				</td>
			</tr>
			
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{consultarDefesaMBean.checkDataFim}" styleClass="noborder" id="checDataFim"/>
				</td>
				
				<td> Data Fim: </td>
				
				<td colspan="3"> 
					<t:inputCalendar 
						value="#{consultarDefesaMBean.dataFim}" 
						maxlength="10" 
						size="10" 
						onkeypress="return(formataData(this,  event))"
						renderAsPopup="true" renderPopupButtonAsImage="true" 
						onchange="$('formulario:checDataInicio').checked = true;" 
						id="dtFim"
						popupDateFormat="dd/MM/yyyy" title="Data Fim">
						
						<f:converter converterId="convertData"/>
						
					</t:inputCalendar>
				</td>
			</tr>
					
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{consultarDefesaMBean.checkDocente}" styleClass="noborder" id="checkDocente"/>
				</td>
				
				<td nowrap="nowrap"> Docente: </td>
				
				<td colspan="3">
					<h:inputText value="#{consultarDefesaMBean.docente}" id="docente" maxlength="60" size="60" 
					onchange = "$('formulario:checkDocente').checked = true;" title="Docente" alt="Docente"/>	
				</td>				
			</tr>
			
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{consultarDefesaMBean.checkDiscente}" styleClass="noborder" id="checkDiscente"/>
				</td>
				
				<td nowrap="nowrap"> Discente: </td>
				
				<td colspan="3">
					<h:inputText value="#{consultarDefesaMBean.discente}" id="discente" maxlength="60" size="60" 
					onchange="$('formulario:checkDiscente').checked = true;" title="Discente" alt="Discente" />	
				</td>				
			</tr>
			
			<tr>
				<td>
					<h:selectBooleanCheckbox value="#{consultarDefesaMBean.checkTituloTrabalho}" styleClass="noborder" id="checkTituloTrabalho"/>
				</td>
				
				<td nowrap="nowrap"> Título do Trabalho: </td>
				
				<td colspan="3">
					<h:inputText value="#{consultarDefesaMBean.tituloTrabalho}" id="tituloTrabalho" maxlength="60" size="60" 
					onchange="$('formulario:checkTituloTrabalho').checked = true;" title="Título do Trabalho" alt="Título do Trabalho"></h:inputText>	
				</td>				
			</tr>									
			
			<tr>
				<td></td>
				<td nowrap="nowrap" alt="Ordenar por"> Ordenar por: </td>
				<td colspan="3"> 
					<h:selectOneRadio id="ordenacao" value="#{consultarDefesaMBean.tipoOrdenacao}" title="Ordenar por">
							<f:selectItem itemLabel="Discente" itemValue="1" />
							<f:selectItem itemLabel="Data da Banca" itemValue="2" />
							<f:selectItem itemLabel="Ano-Período de Ingresso" itemValue="3" />
					</h:selectOneRadio>
				</td>	
			</tr>
			
		</tbody>
		
		<tfoot>
			<tr>
				<td colspan="5">
					<h:commandButton action="#{consultarDefesaMBean.buscar}" value="Buscar" id="buscar"/>
					<h:commandButton id="cancelar" action="#{consultarDefesaMBean.cancelar}" onclick="#{confirm}" value="Cancelar" />
				</td>
			</tr>
		</tfoot>
	</table>

	
	<c:if test="${not empty consultarDefesaMBean.listaBancaPos}">
		<br />
		<center>
			<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif" title="Visualizar" />: Visualizar
				
				<%-- Usado no caso de uso de catalogação de defesa no Módulo Biblioteca. --%>
				<c:if test="${consultarDefesaMBean.ehCatalogacao}">
					<h:graphicImage value="/img/seta.gif" title="Visualizar" />: Catalogar Defesa
				</c:if>
				<%-- Usado no caso de uso de catalogação de defesa no Módulo Biblioteca. --%>
				<c:if test="${consultarDefesaMBean.associacaoComCatalogacao}">
					<h:graphicImage value="/img/seta.gif" title="Visualizar" />: Associar Defesa à Catalogação
				</c:if>
			</div>
		</center>
		
		<c:if test="${consultarDefesaMBean.ordenadoPorDiscente}">
			<%@include file="./resultado_ordem_discente.jsp"%>
		</c:if>
		
		<c:if test="${consultarDefesaMBean.ordenadoPorData}">
			<%@include file="./resultado_ordem_data.jsp"%>
		</c:if>
		
		<c:if test="${consultarDefesaMBean.ordenadoPorAnoPeriodoIngresso}">
			<%@include file="./resultado_ano_periodo_ingresso.jsp"%>
		</c:if>
	</c:if>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>