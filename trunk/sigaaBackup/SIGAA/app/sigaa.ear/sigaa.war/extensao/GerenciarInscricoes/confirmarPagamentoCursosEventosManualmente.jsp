<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

		<a4j:keepAlive beanName="gerenciarInscricoesCursosEventosExtensaoMBean" />
		<a4j:keepAlive beanName="gerenciarInscritosCursosEEventosExtensaoMBean" />

		<h2> <ufrn:subSistema /> &gt; Confirma��o Manual de Pagamento da Inscri��o do Curso e Evento</h2>
		
		<div class="descricaoOperacao" style="width:85%;">
			<p> Caro usu�rio, por essa op��o � poss�vel confirmar no sistema o pagamento das GRUs emitidas para os Cursos ou Eventos de Extens�o.</p>
			<p> Os pagamentos s�o automaticamente confirmados dentro de poucos dias ap�s o pagamento da GRU no sistema banc�rio. Utilize essa op��o caso a 
			confirma��o do pagamento demore a ser realizada.</p>
		</div>
		
		<br/>
		
		<div class="descricaoOperacao" style="width:85%;">
			<br/>
			<br/>
			<p> 
				<strong>
				Declaro para os devidos fins que estou confirmando o pagamento das inscri��es selecionadas abaixo mediante a apresenta��o
				dos comprovantes de pagamento das GRUs.
				<br/>
				<br/>
				</strong>
			</p>
			<br/>
			<br/>
		</div>
		
		
		<h:form id="formConfirmaPagamentoCursoEvento">
		
		<%-- Dados para facilitar a auditoria caso alguem alegue que confirmou o pagamento mas ele nao foi realizado --%>
		<input type="hidden" value="USUARIO ESTA NA PAGINA QUE CONFIRMA O PAGAMENTO" name="MENSAGEM_LOG" />
		<input type="hidden" value="${usuario.id}" name="ID_USUARIO_CONFIRMOU_PAGAMENTO" />
		<input type="hidden" value="${usuario.nome}" name="NOME_USUARIO_CONFIRMOU_PAGAMENTO" />
		
		<table class="listagem" style="margin-bottom:10px;width:90%;">
			
			<caption>Dados das Inscri��es Selecionadas </caption>
			
				<thead>
					<tr>
						<th style="width: 10%"> CPF </th>
						<th style="width: 10%"> Passaporte </th>
						<th style="width: 40%"> Nome</th>
						<th style="width: 10%"> Valor</th>
						<th style="width: 10%">Data da Emiss�o</th>
						<th style="width: 10%"> Vencimento</th>
						<th style="width: 10%"> N� Refer�ncia</th>
					</tr>
				</thead>
				
				<tbody>
					
					<c:forEach var="inscricaoParaConfirmarPagamento" items="#{gerenciarInscritosCursosEEventosExtensaoMBean.inscricoesSelecionadas}">
						<tr>
							<td> ${inscricaoParaConfirmarPagamento.cadastroParticipante.cpf} </td>
							<td> ${inscricaoParaConfirmarPagamento.cadastroParticipante.passaporte} </td>
							<td> ${inscricaoParaConfirmarPagamento.cadastroParticipante.nome} </td>
							
							<c:if test="${not empty inscricaoParaConfirmarPagamento.gru}">
								<td> ${inscricaoParaConfirmarPagamento.gru.valorTotal} </td>
								<td> <ufrn:format type="data" valor="${inscricaoParaConfirmarPagamento.gru.dataDocumento}" />  </td>
								<td> <ufrn:format type="data" valor="${inscricaoParaConfirmarPagamento.gru.vencimento}" /> </td>
								<td style="font-weight: bold;"> ${inscricaoParaConfirmarPagamento.gru.numeroReferenciaNossoNumero} </td>
							</c:if>
							<c:if test="${empty inscricaoParaConfirmarPagamento.gru}">
								<td colspan="4" style="color: red;font-weight: bold; text-align: center;"> GRU n�o gerada pelo usu�rio </td>
							</c:if>
						</tr>
					
					</c:forEach>
					
				</tbody>
				
			
				<tfoot>
					<tr>
						<td colspan="7" style="text-align: center;">
							<h:commandButton id="cmdConfirmaPagamentoCursoEvento"  value="Confirmar Pagamento" action="#{gerenciarInscritosCursosEEventosExtensaoMBean.confirmarPagamentoManualmente}"/>
							<h:commandButton id="cmdCancelar" value="Cancelar" action="#{gerenciarInscritosCursosEEventosExtensaoMBean.telaListaInscritosCursosEventosExtensao}" immediate="true" />
						</td>
					</tr>
				</tfoot>
			
				
		</table>
		
		</h:form>
		
</f:view>		


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>		