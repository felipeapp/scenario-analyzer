<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<style media="screen">
<!--
  .esconder {
     display : none;
  }	
-->
</style>

<style media="print">
<!--
  .esconder {
     display : inline;
     font-family: "courier new";
  }	
-->
</style>					


<link href="/sigaa/css/ensino/questionarios.css" rel="stylesheet" type="text/css" />

<rich:dataTable var="resposta" value="#{questionarioRespostasBean.respostasModel}" width="100%" id="dataTableQuestionario" rowKeyVar="row"
	rowClasses="linhaPar, linhaImpar" >

	<rich:column>
		<h:outputText value="#{row + 1}. #{resposta.pergunta.pergunta}" styleClass="pergunta" /> 
		
		<h:panelGroup rendered="#{not resposta.pergunta.multiplaOuUnicaEscolha && not resposta.pergunta.arquivo}">
			<rich:panel>
				<h:outputText value="#{resposta.respostaVf ? 'Verdadeiro' : 'Falso'}" rendered="#{resposta.pergunta.vf}"/>
				
				<h:outputText value="#{resposta.respostaDissertativa}" rendered="#{resposta.pergunta.dissertativa}"/>
				
				<h:outputText value="#{resposta.respostaNumerica}" rendered="#{resposta.pergunta.numerica}"/>
			</rich:panel>
		</h:panelGroup>
		
		<h:panelGroup rendered="#{resposta.pergunta.arquivo}">
			<rich:panel>
				<h:outputText escape="false" value="<a href='/sigaa/verProducao?idProducao=#{resposta.respostaArquivo}&key=#{ sf:generateArquivoKey(resposta.respostaArquivo) }' target='_blank'>"/>
				<h:outputText escape="false" value="<img src='/shared/img/icones/download.png' alt='Baixar Arquivo' title='Baixar Arquivo'/> Baixar Arquivo"/>
				<h:outputText escape="false" value="</a>"/>
			</rich:panel>
		</h:panelGroup>

	</rich:column>

	<rich:subTable var="alternativa" value="#{resposta.pergunta.alternativas}" rendered="#{resposta.pergunta.unicaEscolha || resposta.pergunta.unicaEscolhaAlternativaPeso}" rowClasses="alternativa" >
		<rich:column styleClass="alternativa">
			<span class="radio <h:outputText value="#{alternativa.gabarito ? 'marcado' : ''}"/>">
				<span class="esconder"> 
		       		<b><h:outputText value="#{alternativa.gabarito ? '(X)' : '( )'}"/></b>
		    	</span>			 
			 
				<h:outputText value="#{alternativa.letraAlternativa} #{alternativa}"/>
			</span>
		</rich:column>
    </rich:subTable>

	<rich:subTable var="alternativa" value="#{resposta.pergunta.alternativas}" rendered="#{resposta.pergunta.multiplaEscolha}"  rowClasses="alternativa">
		<rich:column styleClass="alternativa">
			<span class="checkbox <h:outputText value="#{alternativa.gabarito ? 'marcado' : ''}"/>">
				<span class="esconder"> 
		       		<b><h:outputText value="#{alternativa.gabarito ? '[X]' : '[ ]'}"/></b>
		    	</span>			
				<h:outputText value="#{alternativa.letraAlternativa} #{alternativa}"/>
			</span>
		</rich:column>
	</rich:subTable>

</rich:dataTable>