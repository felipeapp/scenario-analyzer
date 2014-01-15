<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

	<a4j:keepAlive beanName="solicitarReservaMaterialBibliotecaMBean" />
	<a4j:keepAlive beanName="visualizarReservasMaterialBibliotecaMBean" />
	<a4j:keepAlive beanName="pesquisaInternaBibliotecaMBean" />
	
	<h2> <ufrn:subSistema /> &gt; Realizar Nova Reserva </h2>
		
	<div class="descricaoOperacao" style="width:85%;">
		
		<c:if test="${solicitarReservaMaterialBibliotecaMBean.solicitandoPropriaReserva}" >
			<p>Caro usuário,</p>
			<p>A listagem abaixo mostra todas as reservas para o título escolhido, bem como uma <strong>"previsão"</strong> da data em que você poderá 
			realizar o empréstimo do material. A previsão é uma aproximação considerando que todas as reservas sejam realizadas e que o usuário fique com o material o maior tempo possível. 
			</p>
			<br/>
			<p>O sistema enviará um <i>e-mail</i> quando a sua reserva estiver disponível, também é possível visualizá-la pela tela de acompanhamento das suas reservas.</p>
			<br/>
			<p><strong>IMPORTANTE:</strong> Lembre-se de manter o seu <i>e-mail</i> sempre atualizado no sistema. Não nos responsabilizamos pelo não recebimento do <i>e-mail</i> por qualquer motivo técnico.</p>
			
		</c:if>
		
		<c:if test="${! solicitarReservaMaterialBibliotecaMBean.solicitandoPropriaReserva}" >
			<p>Caro operador,</p>
			<p>A listagem abaixo mostra todas as reservas para o título escolhido, bem como uma <strong>"previsão"</strong> da data em que o usuário poderá 
			realizar o empréstimo do material. A previsão é uma aproximação considerando que todas as reservas sejam realizadas e que o usuário fique com o material o maior tempo possível.</p>
			<p>O sistema enviará um <i>e-mail</i> para o usuário quando a reserva dele estiver disponível.</p>
		</c:if>
		
	</div>
		
	
	<h:form id="formularioSolicitarReserva">
	
		<%-- Exibe as informações do usuário. --%>
		<c:if test="${! solicitarReservaMaterialBibliotecaMBean.solicitandoPropriaReserva}">
			<c:set var="_infoUsuarioCirculacao" value="${solicitarReservaMaterialBibliotecaMBean.informacaoUsuario}" scope="request"/>
			<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>
		</c:if>
	
		<table class="visualizacao" style="margin-bottom:10px; width:90%;">
			<tr><td style="text-align: center;"> ${solicitarReservaMaterialBibliotecaMBean.tituloEmFormatoReferencia} </td></tr>
			<tr><td style="height: 20px;"></td></tr>
			<tr><td style="text-align: center; font-size: 14px; font-weight: bold;"> Previsão para retirada do material: <ufrn:format type="dataHora" valor="${solicitarReservaMaterialBibliotecaMBean.dataPrevisaoEmprestimoMaterial}"/> </td></tr>
		</table>
	
	
		<table class="formulario" style="margin-bottom:10px;width:90%;">
			<caption>Reservas Já existendes do Título ( ${fn:length(solicitarReservaMaterialBibliotecaMBean.reservasJaExistentes)} )</caption>
				
				<c:if test="${fn:length(solicitarReservaMaterialBibliotecaMBean.reservasJaExistentes) == 0}">
					<tbody>
						<tr>
							<td style="color: green; text-align: center;"> Não existem reservas ativas para o material selecionado </td>
						</tr>
					</tbody>
				</c:if>
				<c:if test="${fn:length(solicitarReservaMaterialBibliotecaMBean.reservasJaExistentes) > 0}">
					<thead>
						<tr>
							<th style="width:15%">Data da Solicitação</th>
							<th style="width:60%">Solicitador</th>
							<th style="width: 10%">Status</th>
							<th style="width: 15%;">Previsão</th>
						</tr>
					</thead>
					<tbody>
						<c:forEach var="reserva" items="#{solicitarReservaMaterialBibliotecaMBean.reservasJaExistentes}">
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
							</tr>
						</c:forEach>
					</tbody>
				</c:if>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton id="solicitarReservas" value="Confirmar Reserva" action="#{solicitarReservaMaterialBibliotecaMBean.solicitarReserva}" onclick="return confirm('Confirma reserva do material ? ');" />
						<h:commandButton id="cmdVoltar" value="<< Voltar" action="#{pesquisaInternaBibliotecaMBean.telaBuscaPublicaAcervo}" />
						<h:commandButton id="cmdCancelar" value="Cancelar" onclick="#{confirm}" action="#{solicitarReservaMaterialBibliotecaMBean.cancelar}" immediate="true"  />
					</td>
				</tr>
			</tfoot>	
		</table>
					
	
	</h:form>

</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
