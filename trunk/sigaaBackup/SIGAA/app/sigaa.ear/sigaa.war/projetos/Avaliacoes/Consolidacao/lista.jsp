<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Consolidar Avaliações</h2>
	
 <h:form id="form">
	<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Consolidar Avaliações
		</div>
	</center>
	
  	<h:dataTable id="dtDistribuicoes"  value="#{distribuicaoProjetoMbean.distribuicoesNaoConsolidadas}" 
 		var="distribuicao" binding="#{distribuicaoProjetoMbean.distribuicoesCadastradas}" 
 		width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
 		
		<f:facet name="caption">
			<h:outputText value="Lista das Distribuições com Avaliações Não Consolidadas" />
		</f:facet>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Modelo de avaliação</f:verbatim>
			</f:facet>
			<h:outputText value="#{distribuicao.modeloAvaliacao.descricao}" />
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Método de distribuição</f:verbatim>
			</f:facet>
			<h:outputText value="#{distribuicao.metodo == 'M'? 'MANUAL' : 'AUTOMÁTICA'}" />
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Tipo de avaliador</f:verbatim>
			</f:facet>
			<h:outputText value="#{distribuicao.tipoAvaliador.descricao}" />
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim><center>Avaliações consolidadas</center></f:verbatim>
			</f:facet>
			<f:verbatim><center><h:outputText value="#{distribuicao.avaliacaoConsolidada ? 'SIM' :'NÃO'}" /></center></f:verbatim>
		</t:column>
		
		<t:column>
			<h:commandButton action="#{distribuicaoProjetoMbean.consolidarAvaliacoes}" image="/img/seta.gif" 
				value="Consolidar Avaliações" id="btConsolidarAvaliações" title="Consolidar Avaliações" immediate="true" rendered="#{!distribuicao.avaliacaoConsolidada}"/>
		</t:column>
		
 	</h:dataTable>
	<center><h:outputText  value="Não há avaliações em andamento." rendered="#{empty distribuicaoProjetoMbean.distribuicoesNaoConsolidadas}"/></center>
	
 </h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>