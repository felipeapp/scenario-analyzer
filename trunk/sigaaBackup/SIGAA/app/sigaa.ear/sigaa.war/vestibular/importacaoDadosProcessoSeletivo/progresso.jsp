<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
.rich-progress-bar-width { width: 800px;}
.rich-progress-bar-uploaded-dig {font-size: 16px;}
.rich-progress-bar-shell-dig {font-size: 16px;}
</style> 
<f:view>
	<h2><ufrn:subSistema /> > Importação de dados dos candidatos do Vestibular</h2>
	
	<h:form prependId="false" id="form">
	
	<center>
		<br/> <br/>
		<h3><h:outputText value="#{processoImportacaDadosProcessoSeletivo.iniciaProcessamento }" rendered="#{ processoImportacaDadosProcessoSeletivo.percentualProcessado < 100}" id="avisoProcesso"/></h3>
		<br/> <br/> 
		
		<rich:progressBar interval="1000" id="progressBar" minValue="0" maxValue="100"
			value="#{ processoImportacaDadosProcessoSeletivo.percentualProcessado }"
			label ="#{ processoImportacaDadosProcessoSeletivo.mensagemProgresso }"
			reRenderAfterComplete="form" ignoreDupResponses="true">
			<f:facet name="complete">
	        	<br/>
	        	<h:outputText>
	        		<h3>
		        		<c:if test="${processoImportacaDadosProcessoSeletivo.erro != null}">
		        			Ocorreu um problema no processamento das notas:<br/>
		        			${processoImportacaDadosProcessoSeletivo.erro.message}
		        		</c:if>
		        		<c:if test="${processoImportacaDadosProcessoSeletivo.erro == null}">Processamento Concluído.</c:if>
	        		</h3>
	        		<br/><br/>
	            	<h:commandLink action="#{processoImportacaDadosProcessoSeletivo.cancelar}" value="<< Voltar ao Módulo Vestibular"/>
	            </h:outputText>
	        </f:facet>
		</rich:progressBar>
	</center>
		        		
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>