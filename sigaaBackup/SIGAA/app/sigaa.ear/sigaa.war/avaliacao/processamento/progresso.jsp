<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<html>
	<head>
		<title>SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas</title>
		
		<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
		<script type="text/javascript">
			JAWR.loader.style('/bundles/css/sigaa_base.css');
		</script>                
	</head>
	
	<body>
		<f:view>
		<%@include file="/WEB-INF/jsp/include/erros.jsp"%>
		
		<h:form prependId="false">
		
		<center>
			<br/> <br/>
			<h3>Por favor, aguarde enquanto processamos as notas da Avaliação Institucional...</h3>
			<br/> <br/> 
			
			<a4j:outputPanel>
			<rich:progressBar interval="1000" id="progressBar" minValue="0" maxValue="100"
					enabled="#{ processamentoAvaliacaoInstitucional.processando }"
					value="#{ processamentoAvaliacaoInstitucional.percentualProcessado }"
					label ="#{ processamentoAvaliacaoInstitucional.tempoDecorrido } (#{ processamentoAvaliacaoInstitucional.tempoRestante } restante)">
			        <f:facet name="complete">
			        	<br/>
			        	<h:outputText>
			        		<c:if test="${processamentoAvaliacaoInstitucional.erroProcessamento}">Ocorreu um problema no processamento das notas.</c:if>
			        		<c:if test="${not processamentoAvaliacaoInstitucional.erroProcessamento}">Processamento Concluído.</c:if>
			        		<br/><br/>
			            	<a4j:commandLink action="#{processamentoAvaliacaoInstitucional.retornaMenuPrincipal}" value="<< Voltar ao Portal da Avaliação Institucional"/>
			            </h:outputText>
			        </f:facet>
				</rich:progressBar>
			</a4j:outputPanel>
		</center>
			        		
		</h:form>
		</f:view>
	</body>
</html>