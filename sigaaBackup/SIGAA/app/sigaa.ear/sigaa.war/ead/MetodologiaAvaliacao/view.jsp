<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Metodologia de Avalia��o</h2>

	<c:set var="nomeOperacaoVisualizacao" value="Visualizar Metodologia de Avalia��o"/>

	<%@include file="/ead/MetodologiaAvaliacao/detalhesMetodologia.jsp"%>
	
	<br />
	<h:form>
		<center>
			<h:commandLink id="voltar" value="<< Voltar" action="#{metodologiaAvaliacaoEad.voltar}">
				<f:setPropertyActionListener target="#{metodologiaAvaliacaoEad.operacaoVoltar}" value="4" /> 
			</h:commandLink>
		</center>
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>