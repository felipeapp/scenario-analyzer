<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

		<a4j:keepAlive beanName="gerenciarInscricoesCursosEventosExtensaoMBean" />
		<a4j:keepAlive beanName="gerenciarInscritosCursosEEventosExtensaoMBean" />

		<h2> <ufrn:subSistema /> &gt; Confirmação de Estorno do Pagamento da Inscrição do Curso e Evento</h2>
		
		<div class="descricaoOperacao" style="width:85%;">
			<p> Caro coordenador(a), </p>
			<p> Por essa opção é possível confirmar no sistema o estorno do pagamento das GRUs emitidas para os Cursos ou Eventos de Extensão.</p>
			<br/><br/>
			<p> <strong>IMPORTANTE:</strong> Essa operação de estorno do pagamento consiste apenas em registrar no sistema 
			que o dinheiro referente ao pagamento não deve ser contabilizado. <strong>Não</strong> é gerada nenhuma movimentação 
			financeira que implique no estorno da GRU ou retirada de dinheiro da conta da ${ configSistema['siglaInstituicao'] }.
			Essas operações ainda não são possíveis de serem realizadas.
			</p>
			<br/>
			<p style="color: red; text-align: center;"> <strong>Essa operação não poderá ser desfeita!</strong></p>
			
		</div>
		
		<br/>
		
		<h:form id="formConfirmaPagamentoCursoEvento">
		
		<%-- Dados para facilitar a auditoria caso alguem alegue que confirmou o pagamento mas ele nao foi realizado --%>
		<input type="hidden" value="USUARIO ESTA NA PAGINA QUE CONFIRMA O ESTORNO DO PAGAMENTO" name="MENSAGEM_LOG" />
		<input type="hidden" value="${usuario.id}" name="ID_USUARIO_CONFIRMOU_ESTORNO_PAGAMENTO" />
		<input type="hidden" value="${usuario.nome}" name="NOME_USUARIO_CONFIRMOU_ESTORNO_PAGAMENTO" />
		
		<table class="listagem" style="margin-bottom:10px;width:90%;">
			
			<caption>Dados das Inscrições Selecionadas </caption>
			
				<thead>
					<tr>
						<th style="width: 10%"> CPF </th>
						<th style="width: 10%"> Passaporte </th>
						<th style="width: 40%"> Nome</th>
						<th style="width: 10%"> Valor Pago</th>
						<th style="width: 30%"> Nº Referência GRU</th>
					</tr>
				</thead>
				
				<tbody>
					
					<c:forEach var="inscricaoParaConfirmarPagamento" items="#{gerenciarInscritosCursosEEventosExtensaoMBean.inscricoesSelecionadas}">
						<tr>
							<td> ${inscricaoParaConfirmarPagamento.cadastroParticipante.cpf} </td>
							<td> ${inscricaoParaConfirmarPagamento.cadastroParticipante.passaporte} </td>
							<td> ${inscricaoParaConfirmarPagamento.cadastroParticipante.nome} </td>
							<td> ${inscricaoParaConfirmarPagamento.valorTaxaMatricula} </td>

							<c:if test="${not empty inscricaoParaConfirmarPagamento.gru}">
								<td style="font-weight: bold;"> ${inscricaoParaConfirmarPagamento.gru.numeroReferenciaNossoNumero} </td>
							</c:if>
							<c:if test="${empty inscricaoParaConfirmarPagamento.gru}">
								<td colspan="4" style="color: red;font-weight: bold; text-align: center;"> GRU não gerada pelo usuário </td>
							</c:if>
							
						</tr>
					
					</c:forEach>
					
				</tbody>
				
			
				<tfoot>
					<tr>
						<td colspan="7" style="text-align: center;">
							<h:commandButton id="cmdConfirmaPagamentoCursoEvento"  value="Confirmar Estorno do Pagamento" action="#{gerenciarInscritosCursosEEventosExtensaoMBean.confirmarEstornoPagamento}" 
										onclick="return confirm('Confirma o estorno do pagamento ? Essa operação não poderá ser desfeita!');"/>
							<h:commandButton id="cmdCancelar" value="Cancelar" action="#{gerenciarInscritosCursosEEventosExtensaoMBean.telaListaInscritosCursosEventosExtensao}" immediate="true" />
						</td>
					</tr>
				</tfoot>
			
				
		</table>
		
		</h:form>
		
</f:view>		


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>		