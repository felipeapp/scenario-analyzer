<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

	<a4j:keepAlive beanName="visualizarReservasMaterialBibliotecaMBean" />
	<a4j:keepAlive beanName="pesquisaInternaBibliotecaMBean" />
	<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />
	
	<%-- Quando é chamado a partir do caso de uso de comunição de material para cancelar a reserva dos materiais perdidos --%>
	<a4j:keepAlive beanName="comunicarMaterialPerdidoMBean" />
	
	<h2> <ufrn:subSistema /> &gt; Visualizar Reservas de um Usuário </h2>
		
	<div class="descricaoOperacao" style="width:85%;">
		<p>Confirme o cancelamento da reserva do usuário.</p>
		<p>O usuário será avisado automaticamente do cancelamento da reserva dele.</p>
	</div>
	
		<h:form id="formularioListaReservasDoUsuario">
	
			<c:if test="${visualizarReservasMaterialBibliotecaMBean.visualizacaoReservaPorUsuario}">
				<c:set var="_infoUsuarioCirculacao" value="${visualizarReservasMaterialBibliotecaMBean.informacaoUsuario}" scope="request"/>
				<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>
			</c:if>
	
			<c:if test="${! visualizarReservasMaterialBibliotecaMBean.visualizacaoReservaPorUsuario}">
				<table class="visualizacao" style="margin-bottom:10px; width:90%;">
					<tr><td style="text-align: center;"> ${visualizarReservasMaterialBibliotecaMBean.formatoReferenciaTitulo} </td></tr>
		
				</table>
			</c:if>
	
			<table class="formulario" style="margin-bottom:10px; width:90%;">	
			
				<caption> Informe o motivo do cancelamento das reservas abaixo ( ${visualizarReservasMaterialBibliotecaMBean.quantidadeReservas} ) </caption>
			
				<c:forEach var="reservaCancelada" items="#{visualizarReservasMaterialBibliotecaMBean.reservasCanceladas}" varStatus="status">
			
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<th>Data da Solicitação:</th>
						<td>
							<ufrn:format type="dataHora" valor="${reservaCancelada.dataSolicitacao}" /> 
						</td>
						
					</tr>
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">	
						<th>Status:</th>
						<td colspan="5">
							${reservaCancelada.status.descricao}
						</td>
					</tr>
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						<th>Previsão:</th>
						<td colspan="5">
							<ufrn:format type="dataHora" valor="${reservaCancelada.dataPrevisaoRetiradaMaterial}" />
						</td>
					</tr>
				
					<c:if test="${visualizarReservasMaterialBibliotecaMBean.visualizacaoReservaPorUsuario}">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<th>Título:</th>
							<td colspan="5">
								${reservaCancelada.infoTitulo}
							</td>
						</tr>
					</c:if>
				
					<c:if test="${! visualizarReservasMaterialBibliotecaMBean.visualizacaoReservaPorUsuario}">
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<th>Usuário Solicitante:</th>
							<td colspan="5">
								${reservaCancelada.usuarioReserva.pessoa.nome}
							</td>
						</tr>
					</c:if>
					
				</c:forEach>
				
				<tr>
					<th class="obrigatorio" style="vertical-align: center;">Motivo Cancelamento:</th>
					<td colspan="5" style="text-align: left;">
						<h:inputTextarea value="#{visualizarReservasMaterialBibliotecaMBean.motivoCancelamento}" rows="4" cols="70" />
					</td>
				</tr>
			
				<tfoot>
					<tr>
						<td colspan="6">
							<h:commandButton id="cmdCancelarReservaDoUsuario" value="Cancelar Reservas" action="#{visualizarReservasMaterialBibliotecaMBean.cancelarReserva}" />
							<h:commandButton id="cmdVoltarTelaReservasUsuario" value="<< Voltar" action="#{visualizarReservasMaterialBibliotecaMBean.telaVisualizaReservasDeUmUsuario}" rendered="#{visualizarReservasMaterialBibliotecaMBean.visualizacaoReservaPorUsuario}" />
							<h:commandButton id="cmdVoltarTelaReservasTitulo" value="<< Voltar" action="#{visualizarReservasMaterialBibliotecaMBean.telaVisualizaReservasDeUmTitulo}" rendered="#{! visualizarReservasMaterialBibliotecaMBean.visualizacaoReservaPorUsuario}" />
						</td>
					</tr>
				</tfoot>
			
		</table>
	
		</h:form>


		<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>