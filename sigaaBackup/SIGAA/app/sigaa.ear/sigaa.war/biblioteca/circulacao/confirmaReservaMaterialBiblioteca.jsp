<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

	<a4j:keepAlive beanName="solicitarReservaMaterialBibliotecaMBean" />
	<a4j:keepAlive beanName="visualizarReservasMaterialBibliotecaMBean" />
	<a4j:keepAlive beanName="pesquisaInternaBibliotecaMBean" />
	
	<h2> <ufrn:subSistema /> &gt; Realizar Nova Reserva </h2>
		
	<div class="descricaoOperacao" style="width:85%;">
		
		<c:if test="${solicitarReservaMaterialBibliotecaMBean.solicitandoPropriaReserva}" >
			<p>Caro usu�rio,</p>
			<p>A listagem abaixo mostra todas as reservas para o t�tulo escolhido, bem como uma <strong>"previs�o"</strong> da data em que voc� poder� 
			realizar o empr�stimo do material. A previs�o � uma aproxima��o considerando que todas as reservas sejam realizadas e que o usu�rio fique com o material o maior tempo poss�vel. 
			</p>
			<br/>
			<p>O sistema enviar� um <i>e-mail</i> quando a sua reserva estiver dispon�vel, tamb�m � poss�vel visualiz�-la pela tela de acompanhamento das suas reservas.</p>
			<br/>
			<p><strong>IMPORTANTE:</strong> Lembre-se de manter o seu <i>e-mail</i> sempre atualizado no sistema. N�o nos responsabilizamos pelo n�o recebimento do <i>e-mail</i> por qualquer motivo t�cnico.</p>
			
		</c:if>
		
		<c:if test="${! solicitarReservaMaterialBibliotecaMBean.solicitandoPropriaReserva}" >
			<p>Caro operador,</p>
			<p>A listagem abaixo mostra todas as reservas para o t�tulo escolhido, bem como uma <strong>"previs�o"</strong> da data em que o usu�rio poder� 
			realizar o empr�stimo do material. A previs�o � uma aproxima��o considerando que todas as reservas sejam realizadas e que o usu�rio fique com o material o maior tempo poss�vel.</p>
			<p>O sistema enviar� um <i>e-mail</i> para o usu�rio quando a reserva dele estiver dispon�vel.</p>
		</c:if>
		
	</div>
		
	
	<h:form id="formularioSolicitarReserva">
	
		<%-- Exibe as informa��es do usu�rio. --%>
		<c:if test="${! solicitarReservaMaterialBibliotecaMBean.solicitandoPropriaReserva}">
			<c:set var="_infoUsuarioCirculacao" value="${solicitarReservaMaterialBibliotecaMBean.informacaoUsuario}" scope="request"/>
			<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>
		</c:if>
	
		<table class="visualizacao" style="margin-bottom:10px; width:90%;">
			<tr><td style="text-align: center;"> ${solicitarReservaMaterialBibliotecaMBean.tituloEmFormatoReferencia} </td></tr>
			<tr><td style="height: 20px;"></td></tr>
			<tr><td style="text-align: center; font-size: 14px; font-weight: bold;"> Previs�o para retirada do material: <ufrn:format type="dataHora" valor="${solicitarReservaMaterialBibliotecaMBean.dataPrevisaoEmprestimoMaterial}"/> </td></tr>
		</table>
	
	
		<table class="formulario" style="margin-bottom:10px;width:90%;">
			<caption>Reservas J� existendes do T�tulo ( ${fn:length(solicitarReservaMaterialBibliotecaMBean.reservasJaExistentes)} )</caption>
				
				<c:if test="${fn:length(solicitarReservaMaterialBibliotecaMBean.reservasJaExistentes) == 0}">
					<tbody>
						<tr>
							<td style="color: green; text-align: center;"> N�o existem reservas ativas para o material selecionado </td>
						</tr>
					</tbody>
				</c:if>
				<c:if test="${fn:length(solicitarReservaMaterialBibliotecaMBean.reservasJaExistentes) > 0}">
					<thead>
						<tr>
							<th style="width:15%">Data da Solicita��o</th>
							<th style="width:60%">Solicitador</th>
							<th style="width: 10%">Status</th>
							<th style="width: 15%;">Previs�o</th>
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
