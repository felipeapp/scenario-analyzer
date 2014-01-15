<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp"%>

	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Avaliar Proposta de A��o de Extens�o</h2>


	<h:form id="formListaAvaliacaoParereristas">


	<div class="infoAltRem">
	    <h:graphicImage value="/img/view.gif"style="overflow: visible;"/>: Visualizar A��o de Extens�o
    	<h:graphicImage value="/img/extensao/document_chart.png"style="overflow: visible;"/>: Visualizar Avalia��o	    
	    <h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Avaliar A��o de Extens�o	    	
	</div>
	    
	<br/>

		<h:dataTable id="datatableAtividades" value="#{avaliacaoAtividade.avaliacoesPendentesParecerista}" var="avaliacao" 
			width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
		
			<f:facet name="caption">
				<h:outputText value="Selecione uma A��o para realizar a avalia��o" />
			</f:facet>


			<t:column>
				<f:facet name="header">
					<f:verbatim>Ano</f:verbatim>
				</f:facet>
				<h:outputText value="#{avaliacao.atividade.ano}" />
			</t:column>

			<t:column>
				<f:facet name="header">
					<f:verbatim>T�tulo</f:verbatim>
				</f:facet>
				<h:outputText value="#{avaliacao.atividade.titulo}" />
			</t:column>
			
			<t:column  styleClass="centerAlign">
				<f:facet name="header">
					<f:verbatim>Tipo</f:verbatim>
				</f:facet>
				<h:outputText value="#{avaliacao.atividade.tipoAtividadeExtensao.descricao}" />
			</t:column>			

			<t:column  styleClass="centerAlign">
				<f:facet name="header">
					<f:verbatim>Nota</f:verbatim>
				</f:facet>
				<h:outputText value="#{avaliacao.nota}" rendered="#{avaliacao.nota != null}" />
				<h:outputText value="-" rendered="#{avaliacao.nota == null}" />
			</t:column>			

			<t:column  styleClass="centerAlign">
				<f:facet name="header">
					<f:verbatim>Situa��o</f:verbatim>
				</f:facet>
				<h:outputText value="#{avaliacao.statusAvaliacao.descricao}" />
			</t:column>			

			<t:column width="2%">
				<h:commandLink title="Visualizar A��o de Extens�o" action="#{ atividadeExtensao.view }" immediate="true">
				        <f:param name="id" value="#{avaliacao.atividade.id}"/>
		        		<h:graphicImage url="/img/view.gif" />
				</h:commandLink>
			</t:column>


			<t:column width="2%">
				<h:commandLink title="Visualiza Avalia��o Completa" action="#{ avaliacaoAtividade.view }" 
					immediate="true" rendered="#{not empty avaliacao.dataAvaliacao}">
				        <f:param name="idAvaliacao" value="#{avaliacao.id}"/>
		        		<h:graphicImage url="/img/extensao/document_chart.png" />
				</h:commandLink>
			</t:column>

										
			<t:column width="2%">
				<h:commandLink title="Avaliar A��o de Extens�o" action="#{ avaliacaoAtividade.iniciarAvaliacaoParecerista }" immediate="true" 
					rendered="#{avaliacao.atividade.permitidoAdHocAvaliar}">
				        <f:param name="idAvaliacao" value="#{avaliacao.id}"/>
		        		<h:graphicImage url="/img/seta.gif" />
				</h:commandLink>
			</t:column>
			
		</h:dataTable>


		<c:if test="${empty avaliacaoAtividade.avaliacoesPendentesParecerista}">
			<center><font color="red">N�o h� avalia��es pendentes</font> </center>
		</c:if>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>