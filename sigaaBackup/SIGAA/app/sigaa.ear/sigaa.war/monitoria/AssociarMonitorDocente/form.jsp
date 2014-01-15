<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<%@include file="/portais/docente/menu_docente.jsp" %>

	<h:messages/>
	<h2><ufrn:subSistema /> >  Lista de Candidatos à Monitoria (Resultado da Prova Seletiva) </h2>
	<br>
	
	<c:if test="${ empty associarMonitorDocente.obj.compCurricular.provasSelecao}">
		<f:verbatim><center><font color="red">Lista com o resultado da prova seletiva ainda não foi cadastrada!</font></center></f:verbatim>
	</c:if>

	<h:form rendered="#{ not empty associarMonitorDocente.obj.compCurricular.provasSelecao}">

		<t:dataTable styleClass="listagem" var="prova" value="#{associarMonitorDocente.obj.compCurricular.provasSelecao}" width="100%">
	
			<t:column rendered="#{ prova.ativa }">
			
					<f:verbatim><b>Projeto: </b></f:verbatim>
					<h:outputText value="#{associarMonitorDocente.obj.compCurricular.projetoEnsino.titulo}"/>
					<f:verbatim><br/><b>Comp. Curricular: </b></f:verbatim>
					<h:outputText value="#{associarMonitorDocente.obj.compCurricular.disciplina.nome}"/>
					<f:verbatim><b><br/>Orientador: </b></f:verbatim>
					<h:outputText value="#{associarMonitorDocente.obj.orientador.servidor.pessoa.nome}"/>
					<f:verbatim><b><br/>Bolsas Solicitadas: </b></f:verbatim>
					<h:outputText value="#{associarMonitorDocente.obj.compCurricular.bolsasSolicitadas}"/>
					<f:verbatim><b><br/>Data da Prova: </b></f:verbatim>
					<h:outputText value="#{prova.data}"/>					
										
					<f:verbatim><br/><br/><br/></f:verbatim>
				
				<t:dataTable styleClass="listagem" var="selecao" value="#{prova.selecao}" width="100%">
					

					<t:column>
						<f:facet name="header"><f:verbatim>Monitor</f:verbatim></f:facet>
						<h:selectBooleanCheckbox id="statusSelecao"  value="#{selecao.selecionado}" styleClass="noborder"/>
					</t:column>


					<t:column>
						<f:facet name="header"><f:verbatim>Tipo Monitor</f:verbatim></f:facet>
						<h:selectOneMenu value="#{selecao.bolsista}" style="width: 100px">
							<f:selectItem itemLabel="Bolsista" 		itemValue="true" />
							<f:selectItem itemLabel="Não Bolsista" 	itemValue="false" />
						</h:selectOneMenu>
					</t:column>


					<t:column>
						<f:facet name="header"><f:verbatim>Discente</f:verbatim></f:facet>
						<h:outputText value="#{selecao.discente.pessoa.nome}"/>
					</t:column>
			
					<t:column>
						<f:facet name="header"><f:verbatim>Classif.</f:verbatim></f:facet>
						<h:outputText value="#{selecao.classificacao}"/>
					</t:column>
			
					<t:column>
						<f:facet name="header"><f:verbatim>Nota</f:verbatim></f:facet>
						<h:outputText value="#{selecao.nota}"/>
					</t:column>
					
					<t:column>
						<f:facet name="header"><f:verbatim>Aprovado</f:verbatim></f:facet>
						<h:outputText value="#{(selecao.aprovado == true ? 'Sim': 'Não')}"/>
					</t:column>
					
				</t:dataTable>		
			</t:column>
			
		</t:dataTable>
	
		<br/>
	
		<p align="center">
		<h:commandButton action="#{ associarMonitorDocente.associarMonitor }" value="Confirmar"/> 
		<h:commandButton action="#{ associarMonitorDocente.cancelar }" value="Cancelar"/>
		</p>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>