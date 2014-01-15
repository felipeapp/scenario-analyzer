<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Consolidar Avalia��es</h2>
	
 <h:form id="form">
	<center>
		<div class="infoAltRem">
			<h:graphicImage value="/img/seta.gif"style="overflow: visible;"/>: Consolidar Avalia��es
		</div>
	</center>
	
  	<h:dataTable id="dtDistribuicoes"  value="#{distribuicaoProjetoMbean.distribuicoesNaoConsolidadas}" 
 		var="distribuicao" binding="#{distribuicaoProjetoMbean.distribuicoesCadastradas}" 
 		width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
 		
		<f:facet name="caption">
			<h:outputText value="Lista das Distribui��es com Avalia��es N�o Consolidadas" />
		</f:facet>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Modelo de avalia��o</f:verbatim>
			</f:facet>
			<h:outputText value="#{distribuicao.modeloAvaliacao.descricao}" />
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim>M�todo de distribui��o</f:verbatim>
			</f:facet>
			<h:outputText value="#{distribuicao.metodo == 'M'? 'MANUAL' : 'AUTOM�TICA'}" />
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim>Tipo de avaliador</f:verbatim>
			</f:facet>
			<h:outputText value="#{distribuicao.tipoAvaliador.descricao}" />
		</t:column>

		<t:column>
			<f:facet name="header">
				<f:verbatim><center>Avalia��es consolidadas</center></f:verbatim>
			</f:facet>
			<f:verbatim><center><h:outputText value="#{distribuicao.avaliacaoConsolidada ? 'SIM' :'N�O'}" /></center></f:verbatim>
		</t:column>
		
		<t:column>
			<h:commandButton action="#{distribuicaoProjetoMbean.consolidarAvaliacoes}" image="/img/seta.gif" 
				value="Consolidar Avalia��es" id="btConsolidarAvalia��es" title="Consolidar Avalia��es" immediate="true" rendered="#{!distribuicao.avaliacaoConsolidada}"/>
		</t:column>
		
 	</h:dataTable>
	<center><h:outputText  value="N�o h� avalia��es em andamento." rendered="#{empty distribuicaoProjetoMbean.distribuicoesNaoConsolidadas}"/></center>
	
 </h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>