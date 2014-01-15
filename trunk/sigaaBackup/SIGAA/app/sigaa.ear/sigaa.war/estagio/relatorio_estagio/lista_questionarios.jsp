<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<a4j:keepAlive beanName="relatorioEstagioMBean"/>
<a4j:keepAlive beanName="buscaEstagioMBean"/>
<h2> <ufrn:subSistema /> &gt; Relatórios Disponíveis</h2>		

<style>
	.footer{ text-align: center; }
</style>

<div class="descricaoOperacao">
	<p><b>Prezado Usuário,</b></p><br />
	<p>Selecione um dos Questionários listados abaixo para o preenchimento do mesmo.</p>
</div>

<h:form id="form">
	<h:dataTable value="#{relatorioEstagioMBean.questionarios}" var="item" style="width: 80%;"
		styleClass="listagem" id="listagemQuestionarios" rowClasses="linhaPar, linhaImpar">
		<f:facet name="caption">
			<h:outputText value="Questinários Diponíveis para Responder"/>
		</f:facet>
		<rich:column>
			<f:facet name="header"><f:verbatim>Título</f:verbatim></f:facet>			
			<h:outputText value="#{item.titulo}"/>																		
		</rich:column>
		<rich:column>
			<f:facet name="header"><f:verbatim>Tipo</f:verbatim></f:facet>						
			<h:outputText value="#{item.tipo.descricao}"/>																		
		</rich:column>					
		<rich:column>
			<h:commandLink action="#{relatorioEstagioMBean.selecionarQuestionario}" title="Responder Questionário" id="responderQuestionario">
				<h:graphicImage value="/img/seta.gif"/>
				<f:setPropertyActionListener value="#{item}" target="#{relatorioEstagioMBean.questionario}"/>
			</h:commandLink>						
		</rich:column>
		<f:facet name="footer">
	        <rich:columnGroup styleClass="footer">
	            <rich:column colspan="3">
					<h:commandButton value="<< Voltar" action="#{buscaEstagioMBean.telaBusca}" id="btVoltar"/>	            
	            </rich:column>
	        </rich:columnGroup>	
		</f:facet>
	</h:dataTable>	
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>