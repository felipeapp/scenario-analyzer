<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>

<%@ taglib uri="/tags/sigaaFunctions" prefix="sf" %>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<style>
	.dr-table {
		border: 0;
	}

	.dr-pnl {
		margin: 3px 10px;
		border: 0;
		background: transparent;
	}

	.dr-pnl-b {
		padding: 3px 10px;
	}
	
	.dr-table-cell {
		border: 0;
	}
	
	.pergunta {
		font-weight: bold;
	}	
</style>

<rich:dataTable var="resposta" value="#{questionarioRespostasBean.respostasModel}" width="100%" id="dataTableQuestionario" rowKeyVar="row"
	rowClasses="linhaPar, linhaImpar" >

	<rich:column rendered="#{resposta.pergunta.ativo}">
		<b><h:outputText value="#{row + 1}. #{resposta.pergunta.pergunta}"  styleClass="required" style="padding-bottom: 9px; display: inline;" rendered="#{resposta.pergunta.obrigatoria || resposta.pergunta.questionario.respostasObrigatorias}"/></b>
		<b><h:outputText value="#{row + 1}. #{resposta.pergunta.pergunta}"  rendered="#{!(resposta.pergunta.obrigatoria || resposta.pergunta.questionario.respostasObrigatorias)}"/></b>
		<h:outputText rendered="#{ resposta.pergunta.exibeMaxCaracteres }" value="#{ resposta.pergunta.mensagemExibicao }"/>
		 		
		<h:panelGroup>
			<rich:panel>
				<h:selectOneRadio value="#{resposta.respostaVf}" disabled="#{ questionarioRespostasBean.readOnly }" 
					rendered="#{resposta.pergunta.vf}" id="respostaVf">
					<f:selectItem itemValue="true" itemLabel="Verdadeiro"/>
					<f:selectItem itemValue="false" itemLabel="Falso"/>
				</h:selectOneRadio>
				
				<h:inputTextarea value="#{resposta.respostaDissertativa}" rows="4" style="width: 98%;" 
					rendered="#{resposta.pergunta.dissertativa}" id="respostaDissertativa" disabled="#{ questionarioRespostasBean.readOnly }"/>

				<h:inputText value="#{resposta.respostaNumerica}" rendered="#{resposta.pergunta.numerica}" disabled="#{ questionarioRespostasBean.readOnly }"
						size="10" maxlength="12" id="respostaNumerica" onkeyup="return formatarInteiro(this);" style="text-align: right;">
				</h:inputText>	
				
				<h:selectOneRadio value="#{resposta.alternativa}" layout="pageDirection"  disabled="#{ questionarioRespostasBean.readOnly }"
					converter="convertAlternativa" rendered="#{resposta.pergunta.unicaEscolha || resposta.pergunta.unicaEscolhaAlternativaPeso}" id="unicaEscolha"> 
					<t:selectItems value="#{resposta.pergunta.alternativasValidas}" var="a" itemLabel="#{a.alternativa}" itemValue="#{a}"/>
				</h:selectOneRadio>
				
				<h:selectManyCheckbox value="#{resposta.alternativas}" layout="pageDirection"  disabled="#{ questionarioRespostasBean.readOnly }"
 					converter="convertAlternativa" rendered="#{resposta.pergunta.multiplaEscolha}" id="multiplaEscolha">
					<t:selectItems value="#{resposta.pergunta.alternativasValidas}" var="a" itemLabel="#{a.alternativa}" itemValue="#{a}"/>
				</h:selectManyCheckbox>
				
				<t:inputFileUpload value="#{resposta.arquivo}" rendered="#{resposta.pergunta.arquivo}"  disabled="#{ questionarioRespostasBean.readOnly }"/>
				<h:panelGroup rendered="#{not empty resposta.respostaArquivo}" >
					<a href="${ctx}/verProducao?idProducao=<h:outputText value="#{ resposta.respostaArquivo }"/>&key=<h:outputText value="#{ sf:generateArquivoKey(resposta.respostaArquivo) }"/>" 
						target="_blank">
						Baixar Arquivo Atual
					</a>
				</h:panelGroup>
				<%--
				<c:if test="${resposta.pergunta.arquivo}">
					:help img="/img/ajuda.gif" over="true">
						Somente arquivos no formato PDF são permitidos. 
						Caso seu processador de texto não exporte no formato PDF, recomendamos instalar o
						<a href="${questionarioRespostasBean.enderecoExportarPDF}" target="_blank">PDFCreator</a> 
						(${questionarioRespostasBean.enderecoExportarPDF}). 
					</ufrn:help> 	
				</c:if>	 
				--%>			
				
			</rich:panel>
		</h:panelGroup>

	</rich:column>

</rich:dataTable>
		