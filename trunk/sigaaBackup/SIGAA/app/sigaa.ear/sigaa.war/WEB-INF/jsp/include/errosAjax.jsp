
<%-- Mostra mensagens que ocorrem no processamento ajax, porque da maneira normal n�o s�o mostradas --%>
<%-- Tem que ser inclu�do dentro de uma <f:view>      --%>	
	
	<a4j:outputPanel id="painelErrosAjax" ajaxRendered="true">
		
		<%@include file="/WEB-INF/jsp/include/erros.jsp"%>
		
	</a4j:outputPanel>