<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h:form id="formEvolucao">
	
			<h2> <ufrn:subSistema /> &gt; Lista de Formul�rios de Evolu��o</h2>
			<h:outputText value="#{cadastroFormularioEvolucaoMBean.create}"/>
		
			<div class="infoAltRem">
				<h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar Informa��es do Formul�rio de Evolu��o
				<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Formul�rio de Evolu��o
			    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Excluir Formul�rio de Evolu��o
			</div>
			<br/>
			
			<t:dataTable id="datatableFormularios" value="#{cadastroFormularioEvolucaoMBean.formulariosEvolucaoCrianca}" var="formEv" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
				<f:facet name="caption">
					<h:outputText value="Lista das Formul�rios de Evolu��o" />
				</f:facet>
		
				<t:column>
					<f:facet name="header">
						<f:verbatim>Turma</f:verbatim>
					</f:facet>
					<h:outputText value="#{formEv.nivelInfantil.codigo}" />
				</t:column>
				
				<t:column>
					<f:facet name="header"><f:verbatim><center>Data</center></f:verbatim></f:facet>
					<div style="text-align:center">
					<h:outputText value="#{formEv.dataCadastro}">
						<f:convertDateTime pattern="dd/MM/yyyy"/>
					</h:outputText>
					</div>
				</t:column>
		
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Visualizar Informa��es do Formul�rio de Evolu��o" action="#{ cadastroFormularioEvolucaoMBean.view }" immediate="true">
				        <f:param name="idFormulario" value="#{formEv.id}"/>
			    		<h:graphicImage url="/img/view.gif" />
					</h:commandLink>
				</t:column>
		
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Alterar Formul�rio de Evolu��o" action="#{ cadastroFormularioEvolucaoMBean.alterar }" immediate="true">
				        <f:param name="idFormulario" value="#{formEv.id}"/>
			    		<h:graphicImage url="/img/alterar.gif" />
					</h:commandLink>
				</t:column>
		
				<t:column width="5%" styleClass="centerAlign">
					<h:commandLink title="Excluir Formul�rio de Evolu��o" action="#{ cadastroFormularioEvolucaoMBean.preRemover }" immediate="true">
				        <f:param name="idFormulario" value="#{formEv.id}"/>
			    		<h:graphicImage url="/img/delete.gif" />
					</h:commandLink>
				</t:column>
		
			</t:dataTable>
			
		<c:if test="${(empty cadastroFormularioEvolucaoMBean.resultadosBusca)}">
			<center><font color='red'>N�o h� Formul�rios de Evolu��o cadastrados na base de dados.</font></center>
		</c:if>
			
</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>