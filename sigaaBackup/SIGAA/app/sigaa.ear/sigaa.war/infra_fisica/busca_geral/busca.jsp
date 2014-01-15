<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>

	<a4j:keepAlive beanName = "espacoFisicoBean"/>
	<a4j:keepAlive beanName = "buscaEspacoFisico"/>

	<h:panelGroup id="ajaxErros">
		<h:dataTable  value="#{buscaEspacoFisico.avisosAjax}" var="msg" rendered="#{not empty buscaEspacoFisico.avisosAjax}" width="100%">
			<t:column>
				<h:outputText value="<div id='painel-erros' style='position: relative; padding-bottom: 10px;'><ul class='erros'><li>#{msg.mensagem}</li></ul></div>" escape="false"/>
			</t:column>
		</h:dataTable>
	</h:panelGroup>

	<h2><ufrn:subSistema/> > <h:outputText value="#{buscaEspacoFisico.requisitor.descricao}" /> </h2>
	
	<a4j:form id="busca">
		<table class="formulario" width="90%">
			<caption>Buscar por Espaço Físico</caption>
			<tbody>
				<tr>
					<td width="5%"><h:selectBooleanCheckbox id="codigoCheck" value="#{ buscaEspacoFisico.restricoes.buscaCodigo }"/></td>
					<td width="15%">Código:</td>
					<td><h:inputText id="codigo" value="#{ buscaEspacoFisico.parametros.codigo }" style="width: 30%" onkeyup="CAPS(this);" size="50" onfocus="$('busca:codigoCheck').checked = true" /> <ufrn:help>O Código é único e identifica um espaço físico</ufrn:help></td>
				</tr>						
				<tr>
					<td><h:selectBooleanCheckbox id="capacidadeCheck" value="#{ buscaEspacoFisico.restricoes.buscaCapacidade }"/></td>
					<td>Capacidade:</td>
					<td><h:inputText id="capacidadeInicio" value="#{ buscaEspacoFisico.parametros.capacidadeInicio }" style="width: 10%" onfocus="Field.check('busca:capacidadeCheck')"/> e <h:inputText id="capacidadeFim" value="#{ buscaEspacoFisico.parametros.capacidadeFim }" style="width: 10%" onfocus="$('busca:capacidadeCheck').checked = true"/> <ufrn:help>O valor informado deve estar dentro de um intervalo. O valor inicial deve ser maior que 0.</ufrn:help></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="areaCheck" value="#{ buscaEspacoFisico.restricoes.buscaArea }"/></td>
					<td>Área:</td>
					<td><h:inputText id="areaInicio" value="#{ buscaEspacoFisico.parametros.areaInicio }" style="width: 10%" onfocus="Field.check('busca:areaCheck')"/> e <h:inputText id="areaFim" value="#{ buscaEspacoFisico.parametros.areaFim }" style="width: 10%" onfocus="$('busca:areaCheck').checked = true"/> <ufrn:help>O valor informado deve estar dentro de um intervalo. O valor inicial deve ser maior que 0.</ufrn:help></td>
				</tr>
				<tr>
					<td><h:selectBooleanCheckbox id="recursoCheck" value="#{buscaEspacoFisico.restricoes.buscaTipoRecurso}"/></td>
					<td>Recurso:</td>
					<td>
						<h:selectOneMenu value="#{buscaEspacoFisico.parametros.tipoRecurso.id}" id="tipoRecurso" style="width: 30%" onfocus="$('busca:recursoCheck').checked = true">
							<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--"/>
							<f:selectItems value="#{espacoFisicoBean.tipoRecursosCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>				
				<tr>
					<td><h:selectBooleanCheckbox id="localizacaoCheck" value="#{ buscaEspacoFisico.restricoes.buscaLocalizacao }"/></td>
					<td>Localização:</td>
					<td>
						<h:selectOneMenu value="#{buscaEspacoFisico.parametros.localizacao.id}" style="width: 95%" onfocus="$('busca:localizacaoCheck').checked = true">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--"/>
							<f:selectItems value="#{unidade.allCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>																				
				<tr>
					<td><h:selectBooleanCheckbox id="reservaPrioritariaCheck" value="#{ buscaEspacoFisico.restricoes.buscaReservaPrioritaria }"/></td>
					<td>Reservado Prioritáriamente:</td>
					<td>
						<h:selectOneMenu value="#{buscaEspacoFisico.parametros.reservaPrioritaria.id}" style="width: 95%" onfocus="$('busca:reservaPrioritariaCheck').checked = true">
						<f:selectItem itemValue="0" itemLabel="--> SELECIONE <--"/>
							<f:selectItems value="#{unidade.allCombo}"/>
						</h:selectOneMenu>
					</td>
				</tr>																									
				<tr valign="top">
					<td><h:selectBooleanCheckbox id="descricaoCheck" value="#{ buscaEspacoFisico.restricoes.buscaDescricao }"/></td>
					<td>Descrição:</td>
					<td><h:inputTextarea id="descricao" value="#{ buscaEspacoFisico.parametros.descricao }" style="width: 95%" onfocus="$('busca:descricaoCheck').checked = true"/></td>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="3">
					<a4j:commandButton value="Buscar" actionListener="#{ buscaEspacoFisico.buscar }" reRender="outPutPanelResultado, ajaxErros" type="submit" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ buscaEspacoFisico.cancelar }" immediate="true" />
				</td></tr>
			</tfoot>			
		</table>
	</a4j:form>

			
	<center>
		<a4j:status>
			<f:facet name="start">
				<h:graphicImage value="/img/ajax-loader.gif"/>
			</f:facet>
		</a4j:status>
	</center>
	
		
	<br />
	<div class="infoAltRem">
	   	<h:graphicImage value="/img/seta.gif" style="overflow: visible;"/>: Selecionar Espaço Físico
	   	<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Espaço Físico
	   	<br/>
	</div>
	<br />
	<a4j:outputPanel id="outPutPanelResultado">
	<h:form id="resultado">
		<rich:dataTable id="dataTableResultado" value="#{buscaEspacoFisico.modelResultadoBusca}" var="ef" rendered="#{buscaEspacoFisico.modelResultadoBusca.rowCount > 0}"
				rowKeyVar="row" width="100%" styleClass="listagem" headerClass="linhaCinza" rowClasses="linhaPar, linhaImpar">
	
			<f:facet name="caption">
				<h:outputText value="Espaços Físicos Encontrados (#{buscaEspacoFisico.modelResultadoBusca.rowCount})" />
			</f:facet>
	
			<rich:column>
				<f:facet name="header">
					<h:outputText value="Código" />
				</f:facet>
				<h:outputText id="codigo" value="#{ef.codigo}"/>
			</rich:column>

			<rich:column>
				<f:facet name="header">
					<h:outputText value="Descrição" />
				</f:facet>
				<h:outputText id="descricao" value="#{ef.descricao}"/>
			</rich:column>

			<rich:column>
				<f:facet name="header">
					<h:outputText value="Capacidade" />
				</f:facet>
				<h:outputText id="capacidade" value="#{ef.capacidade}"/>
			</rich:column>		
			
			<rich:column>
				<f:facet name="header">
					<h:outputText value="Área" />
				</f:facet>
				<h:outputText id="area" value="#{ef.area}"/>
			</rich:column>
					
			<rich:column width="5%" style="white-space:nowrap;">
				<f:subview id="idTeste">
					<c:import url="${buscaEspacoFisico.url}"></c:import>
				</f:subview>
			</rich:column>                 														
				
		</rich:dataTable>
	</h:form>
	</a4j:outputPanel>
</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>