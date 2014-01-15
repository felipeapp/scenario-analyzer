<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

	<a4j:keepAlive beanName="visualizarReservasMaterialBibliotecaMBean" />
	<a4j:keepAlive beanName="pesquisaInternaBibliotecaMBean" />
	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	
	<h2> <ufrn:subSistema /> &gt; Visualizar Minhas Reservas </h2>

	<div class="descricaoOperacao" style="width:85%;">
		<p>Caro, usuário,</p>
		<p>A listagem abaixo mostra todas as suas reservas ativas para os títulos do acervo do sistema </p>
	</div>	
	
	<h:form id="formularioListaReservasDoUsuario">
	
		<%-- Exibe as informações do usuário. --%>
		<c:set var="_infoUsuarioCirculacao" value="${visualizarReservasMaterialBibliotecaMBean.informacaoUsuario}" scope="request"/>
		<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>
	
	
		<div class="infoAltRem" style="margin-top: 10px; width:90%;">
			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" /> 
			<h:commandLink id="cmdSolicitarReserva" value="Solicitar Nova Reserva" 
				action="#{solicitarReservaMaterialBibliotecaMBean.iniciarReservaPeloUsuarioDaPaginaVisualizacao}" 
				rendered="#{visualizarReservasMaterialBibliotecaMBean.visualizacaoProprioUsuario}" />
			
			<h:commandLink id="cmdSolicitarReservaParaUsuarioSelecionada" value="Solicitar Nova Reserva Para o Usuario Selecionado" 
				action="#{solicitarReservaMaterialBibliotecaMBean.iniciarReservaParaUsuarioSelecionado}" 
				rendered="#{! visualizarReservasMaterialBibliotecaMBean.visualizacaoProprioUsuario}" >
				<f:setPropertyActionListener target="#{solicitarReservaMaterialBibliotecaMBean.usuarioSolicitador}" value="#{visualizarReservasMaterialBibliotecaMBean.usuarioDasReservas}" />
			</h:commandLink>	
				
			<h:graphicImage value="/img/delete_old.gif" style="overflow: visible;" />: Cancelar reserva
		</div>
		
		<table class="listagem" style="margin-bottom:10px; width:90%;">	
			<caption>Reservas do Usuário ( ${fn:length(visualizarReservasMaterialBibliotecaMBean.reservasAtivas)} )</caption>
			<thead>
				<tr>
					<th style="width:15%">Data da Solicitação</th>
					<th style="width:60%">Título Reservado</th>
					<th style="width: 10%">Status</th>
					<th style="width: 14%;">Previsão</th>
					<th style="width: 1%;"></th>
				</tr>
			</thead>
			
			<c:forEach var="reserva" items="#{visualizarReservasMaterialBibliotecaMBean.reservasAtivas}">
				<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td>
						<ufrn:format type="dataHora" valor="${reserva.dataSolicitacao}" /> 
					</td>
					<td>
						${reserva.infoTitulo}
					</td>
					<td>
						${reserva.status.descricao}
					</td>
					<c:if test="${empty reserva.prazoRetiradaMaterial}">
						<td>
							<ufrn:format type="dataHora" valor="${reserva.dataPrevisaoRetiradaMaterial}" />
						</td>
					</c:if>
					<c:if test="${not empty reserva.prazoRetiradaMaterial}">
						<td>
							<ufrn:format type="data" valor="${reserva.prazoRetiradaMaterial}" />
						</td>
					</c:if>
					<td>
						<c:if test="${visualizarReservasMaterialBibliotecaMBean.visualizacaoProprioUsuario}">
							<h:commandLink id="cmdLinkCancelarPropriaReserva" action="#{visualizarReservasMaterialBibliotecaMBean.cancelarReserva}" onclick="return confirm('Confirma cancelamento da reserva selecionada ? ');">
								<h:graphicImage url="/img/delete_old.gif" style="border:none" title="Cancelar a reserva selecionada" />
								<f:param name="idReservaCancelamento" value="#{reserva.id}"/>
							</h:commandLink>
						</c:if>
						<c:if test="${! visualizarReservasMaterialBibliotecaMBean.visualizacaoProprioUsuario}">
							<h:commandLink id="cmdLinkCancelarReserva" action="#{visualizarReservasMaterialBibliotecaMBean.preCancelarReserva}">
								<h:graphicImage url="/img/delete_old.gif" style="border:none" title="Cancelar a reserva selecionada" />
								<f:param name="idReservaCancelamento" value="#{reserva.id}"/>
							</h:commandLink>
						</c:if>
					</td>
				</tr>
			</c:forEach>
			
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center;">
						<h:commandButton id="cancelarTelaBuscaUsuario" value="<< Voltar" action="#{visualizarReservasMaterialBibliotecaMBean.voltaTelaBusca}" />
						<h:commandButton id="cancelar" value="Cancelar" action="#{visualizarReservasMaterialBibliotecaMBean.cancelar}"  immediate="true"  onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
		
	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>