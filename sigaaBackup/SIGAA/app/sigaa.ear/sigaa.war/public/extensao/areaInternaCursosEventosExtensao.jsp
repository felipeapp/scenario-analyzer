<%@include file="/public/include/cabecalho.jsp"%>


	<f:view>

		<%@include file="/public/extensao/cabecalhoAreaInternaCursosEventosExtensao.jsp"%>

		
		<%--  tem que manter os keep alive aqui fora para funcionar --%>
		<a4j:keepAlive beanName="cadastroParticipanteAtividadeExtensaoMBean" />
		<a4j:keepAlive beanName="inscricaoParticipanteAtividadeMBean" />
		<a4j:keepAlive beanName="inscricaoParticipanteMiniAtividadeMBean" />
		<a4j:keepAlive beanName="gerenciaMeusCursosEventosExtensaoMBean" />
		
		
		<%--  OBRIGAT�RIO A VERIFICA��O SE O USU�RIO T� LOGADO, TODAS AS P�GINAS INTERNAS FICA AQUI DENTRO. --%>
		
		<c:if test="${sessionScope.participanteCursosEventosExtensaoLogado != null}">
	
			<t:div id="painelLateralAreaInterna" style=" clear: left; float: left; height: 100%; width: 20%;">
				<%@include file="/public/extensao/painelLateralAreaInternaCursosEventosExtensao.jsp"%>
			</t:div>
	
	
			<%-- Exibe a p�gina interna dinamicamente de acordo com a opera��o ativa no momento --%>
			
			
			<t:div id="painelCentralAreaInterna1" style=" clear: right; " >
					
					<c:import url="${gerenciaAreaInternaCursosEventoExtensaoMBean.paginaAreaInternaParticipante}" />
					
			</t:div>
	
		
				
	
	
			<br />
			<div style="margin: 0pt auto; width: 100%; text-align: center; float: left; color: ">
				<h:form>
					<h:commandLink action="#{gerenciaAreaInternaCursosEventoExtensaoMBean.forwardPaginaInternaPadrao}" immediate="true">  P�gina Inicial </h:commandLink>
				</h:form>
			</div>
			<br />
	
		</c:if>
	
	</f:view>

<%@include file="/public/include/rodape.jsp" %>