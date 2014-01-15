<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<a4j:keepAlive beanName="avaliadorProjetoMbean" />
<f:view>
	<h2><ufrn:subSistema /> &gt; Avaliadores de Projetos</h2>
	
 <h:form id="form">
	<center>
		<div class="infoAltRem">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
				<h:commandLink action="#{avaliadorProjetoMbean.preCadastrar}" value="Cadastrar" id="lnkCadastrar"/>
				<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar
				<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Remover
		</div>
	</center>
	
  	<h:dataTable id="dtAvaliadores"  value="#{avaliadorProjetoMbean.resultadosBusca}" 
 		var="avaliador" binding="#{avaliadorProjetoMbean.avaliadores}" 
 		width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar"> 
 		
		<f:facet name="caption">
			<f:verbatim>Lista de Avaliadores Cadastrados (${fn:length(avaliadorProjetoMbean.resultadosBusca)})</f:verbatim>			
		</f:facet>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Nome</f:verbatim>
			</f:facet>
			<h:outputText value="#{avaliador.usuario.nome}" />
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Área Conhecimento</f:verbatim>
			</f:facet>
			<h:outputText value="#{avaliador.areaConhecimento1.nome}" />
		</t:column>
		<t:column>
			<f:facet name="header">
				<f:verbatim>Início</f:verbatim>
			</f:facet>
			<h:outputText value="#{avaliador.dataInicio}">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</h:outputText>
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Fim</f:verbatim>
			</f:facet>
			<h:outputText value="#{avaliador.dataFim}">
				<f:convertDateTime pattern="dd/MM/yyyy"/>
			</h:outputText>
		</t:column>
		
		<t:column>
			<h:commandButton action="#{avaliadorProjetoMbean.alterar}" image="/img/alterar.gif" value="Alterar" id="btAlterar" title="Alterar"/>
		</t:column>
		
		<t:column>
			<h:commandLink title="Remover" id="btRemover" action="#{avaliadorProjetoMbean.inativar}" onclick="return confirm('Tem certeza que deseja Remover este Avaliador?');">
				<f:param name="id" value="#{avaliador.id}"/>
				<h:graphicImage url="/img/delete.gif" />
			</h:commandLink>
		</t:column>
		
 	</h:dataTable>
	<center><h:outputText  value="Não há avaliadores cadastrados." rendered="#{empty avaliadorProjetoMbean.allAtivos}"/></center>
	
 </h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>