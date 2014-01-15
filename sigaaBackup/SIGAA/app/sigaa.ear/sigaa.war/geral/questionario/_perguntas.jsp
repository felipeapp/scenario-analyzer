<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<rich:dataTable var="pergunta" value="#{questionarioBean.modelPerguntas}" width="100%" styleClass="listagem" 
	id="dataTableQuestionario" rowKeyVar="row" >

	 <f:facet name="header">
               <rich:columnGroup>
                   <rich:column colspan="2">
                       <h:outputText value="Perguntas do Questionário" />
                   </rich:column>
               </rich:columnGroup>
              </f:facet>

	<rich:column rendered="#{pergunta.ativo}">
	
		<h:outputText value="#{row + 1}. #{pergunta.pergunta}" styleClass="pergunta" /> 
		<h:outputText styleClass="obrigatorio" rendered="#{resposta.pergunta.obrigatoria}"/>
		
		<h:panelGroup rendered="#{pergunta.vf}">
			<rich:panel>
				<h:outputText value="#{pergunta.gabaritoVf == null ? 'Verdadeiro / Falso' : (pergunta.gabaritoVf ? 'Verdadeiro' : 'Falso?')}"/>
			</rich:panel>
		</h:panelGroup>
		
		<h:panelGroup rendered="#{pergunta.dissertativa}">
			<rich:panel>
				<h:outputText value="#{empty pergunta.gabaritoDissertativa ? 'Resposta Dissertativa' : pergunta.gabaritoDissertativa}"/>
			</rich:panel>
		</h:panelGroup>
		
		<h:panelGroup rendered="#{pergunta.numerica}">
			<rich:panel>
				<h:outputText value="#{empty pergunta.gabaritoNumerica ? 'Resposta Numérica' : pergunta.gabaritoNumerica}"/>
			</rich:panel>
		</h:panelGroup>
		
		<h:panelGroup rendered="#{pergunta.arquivo}">
			<rich:panel>
				<h:outputText value="Arquivo Anexado"/>
			</rich:panel>
		</h:panelGroup>
	
	</rich:column>
	
	<rich:subTable var="alternativa" value="#{pergunta.alternativas}" rendered="#{pergunta.unicaEscolha || pergunta.unicaEscolhaAlternativaPeso}" rowClasses="alternativa" >
		<rich:column styleClass="alternativa" rendered="#{alternativa.ativo}">
			<span class="radio <h:outputText value="#{alternativa.gabarito ? 'marcado' : ''}"/>">
			<h:outputText value="#{alternativa.letraAlternativa} #{alternativa}"/>
			</span>
		</rich:column>
	</rich:subTable>


	<rich:subTable var="alternativa" value="#{pergunta.alternativas}" rendered="#{pergunta.multiplaEscolha}"  rowClasses="alternativa">
		<rich:column styleClass="alternativa" rendered="#{alternativa.ativo}">
			<span class="checkbox <h:outputText value="#{alternativa.gabarito ? 'marcado' : ''}"/>">
				<h:outputText value="#{alternativa.letraAlternativa} #{alternativa}"/>
			</span>
		</rich:column>
	</rich:subTable>

</rich:dataTable>
		