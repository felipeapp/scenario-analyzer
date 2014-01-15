<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

	<a4j:keepAlive beanName="visualizarReservasMaterialBibliotecaMBean" />
	<a4j:keepAlive beanName="pesquisaInternaBibliotecaMBean" />
	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	
	
	<%-- Quando é chamado a partir do caso de uso de comunição de material para cancelar a reserva dos materiais perdidos --%>
	<a4j:keepAlive beanName="comunicarMaterialPerdidoMBean" />
	
	
	<h2> <ufrn:subSistema /> &gt; Visualizar Reservas de um Título </h2>
		
	<div class="descricaoOperacao" style="width:85%;">
		<p>Caro operador,</p>
		<p>A listagem abaixo mostra todas as reservas ativas para o Título selecionado. </p>
	</div>	
	
	<h:form id="formularioListaReservasDoTitulo">
	
		<div class="infoAltRem" style="margin-top: 10px; width:90%;">
			<h:graphicImage value="//img/delete_old.gif" style="overflow: visible;" />: Cancelar reserva
		</div>
		
		<div style="margin-bottom: 20px; text-align: center; margin-left: auto; margin-right: auto; width: 90%">
			<table class="visualizacao">
				<tr>
					<td> ${visualizarReservasMaterialBibliotecaMBean.formatoReferenciaTitulo} </td>
				</tr>
			</table>
		</div>
		
		<table class="listagem" style="margin-bottom:10px; width:90%;">	
			<caption>Reservas Encontradas ( ${fn:length(visualizarReservasMaterialBibliotecaMBean.reservasAtivas)} )</caption>
			<thead>
				<tr>
					<th style="width:15%">Data da Solicitação</th>
					<th style="width:60%">Usuário Solicitante</th>
					<th style="width: 10%">Status</th>
					<th style="width: 14%;">Previsão</th>
					<th style="width: 1%;"></th>
				</tr>
			</thead>
			
			<c:forEach var="reserva" items="#{visualizarReservasMaterialBibliotecaMBean.reservasAtivas}" varStatus="loop">
				<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td>
						<ufrn:format type="dataHora" valor="${reserva.dataSolicitacao}" /> 
					</td>
					<td>
						${reserva.usuarioReserva.pessoa.nome}
					</td>
					<td>
						${reserva.status.descricao}
					</td>
					<c:if test="${empty reserva.prazoRetiradaMaterial}">
						<td>
							<ufrn:format type="data" valor="${reserva.dataPrevisaoRetiradaMaterial}" />
						</td>
					</c:if>
					<c:if test="${not empty reserva.prazoRetiradaMaterial}">
						<td>
							<ufrn:format type="data" valor="${reserva.prazoRetiradaMaterial}" />
						</td>
					</c:if>
					<td>
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
						<h:commandButton id="cmdButtonCancelarTodasReservasTitulo" value="Cancelar Todas as Reservas" 
							action="#{visualizarReservasMaterialBibliotecaMBean.preCancelarTodasReservasTitulo}"
							rendered="#{! visualizarReservasMaterialBibliotecaMBean.visualizacaoProprioUsuario && visualizarReservasMaterialBibliotecaMBean.quantidadeReservas > 0}"/>
					</td>
				</tr>
			</tfoot>
			
		</table>
		
	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>