<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="consultaPidMBean"></a4j:keepAlive>

<h2><ufrn:subSistema/> > Consultar Planos Individuais de Docentes </h2>

	<div class="descricaoOperacao">
		<p>
			Esta consulta destina-se a disponibilizar os planos individuais dos docentes que já os submeteram à homologação.
		</p>
	</div>

	<h:form id="form">
	${consultaPidMBean.unidadeIntegracao}
	<table class="formulario" style="width: 90%">
		<caption>Informe os critérios de busca</caption>
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox value="#{consultaPidMBean.opcaoAnoPeriodo}" styleClass="noborder" id="opcaoAnoPeriodo" />
			</td>
			<td width="15%"><h:outputLabel for="opcaoAnoPeriodo">Ano-Período:</h:outputLabel></td>
			<td>
				<h:inputText id="ano" 
					value="#{consultaPidMBean.ano}"
					onkeyup="return formatarInteiro(this);" 
					onfocus="$('form:opcaoAnoPeriodo').checked = true;" size="4" maxlength="4" /> . 
				
				<h:inputText id="periodo" 
					value="#{consultaPidMBean.periodo}" 
					onkeyup="return formatarInteiro(this);" 
					onfocus="$('form:opcaoAnoPeriodo').checked = true;" size="1" maxlength="1" />
			</td>
		</tr>
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox value="#{consultaPidMBean.opcaoUnidade}" styleClass="noborder" id="opcaoUnidade" />
			</td>
			<td><h:outputLabel for="opcaoUnidade">Unidade de lotação:</h:outputLabel></td>
			<td>
				<h:selectOneMenu id="unidade" style="width: 600px"
					value="#{consultaPidMBean.unidade.id}"
					onfocus="$('form:opcaoUnidade').checked = true;">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{unidade.allDepartamentoUnidAcademicaCombo}" />
				</h:selectOneMenu>	
			</td>
		</tr>
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox value="#{consultaPidMBean.opcaoDocente}" styleClass="noborder" id="opcaoDocente" />
			</td>
			<td><h:outputLabel for="opcaoDocente">Docente:</h:outputLabel></td>
			<td>
				<a4j:outputPanel id="inputDocente">
					<h:inputText value="#{consultaPidMBean.docente.pessoa.nome}" id="nomeDocente" onfocus="$('form:opcaoDocente').checked = true;" style="width: 600px;"/>
					<rich:suggestionbox id="idNomeDocente" width="600" height="100" for="nomeDocente" 
						minChars="5" frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
						suggestionAction="#{servidor.autocompleteDocente}" var="_servidor" fetchValue="#{_servidor.nome}">
						<h:column>
							<h:outputText value="#{_servidor.siape}"/>
						</h:column>
						<h:column>
							<h:outputText value="#{_servidor.nome}"/>
						</h:column>
						<a4j:support event="onselect" actionListener="#{consultaPidMBean.selecionarDocente}" reRender="inputDocente">
							<f:attribute name="docente" value="#{_servidor}"/>
						</a4j:support>
					</rich:suggestionbox>
					
					<rich:spacer width="10"/>
		            <a4j:status>
		                <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
	            </a4j:outputPanel>					
			</td>
		</tr>	
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{consultaPidMBean.buscar}" value="Buscar" />
					<h:commandButton action="#{consultaPidMBean.cancelar}" value="Cancelar" onclick="#{confirm}" immediate="true" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	
	<c:if test="${not empty consultaPidMBean.resultadosBusca}">
		<br />
		<center>
			<div class="infoAltRem"> 
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar PID
			</div>
		</center>
		
		<h:form id="formResultado">
		<table class="listagem">
			<caption> Resultado da busca (${fn:length(consultaPidMBean.resultadosBusca)}) </caption>
			<thead>
				<tr>
					<th> Docente </th>
					<th> Regime Trabalho </th>
					<th> Unidade </th>
					<th> Situação </th>
					<th> </th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{consultaPidMBean.resultadosBusca}" var="_pid" varStatus="status">
				<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td> ${_pid.servidor.nome} </td>
					<td> ${_pid.servidor.descricaoRegimeTrabalho} </td>
					<td> ${_pid.servidor.unidade.sigla} </td>
					<td> ${_pid.descricaoStatus}</td>
					<td> 
						<h:commandButton image="/img/view.gif" title="Visualizar PID" 
							action="#{consultaPidMBean.visualizar}" styleClass="noborder">
							<f:setPropertyActionListener value="#{_pid}" target="#{consultaPidMBean.obj}"/>
						</h:commandButton>					
					</td>
				</tr>
				</c:forEach>
			</tbody>
		</table>
		</h:form>
	</c:if>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>