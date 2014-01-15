<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<f:view>

		<a4j:keepAlive beanName="emitirGRUPagamentoMultasBibliotecaMBean" />

		<h2> <ufrn:subSistema /> &gt; Imprimir GRU para Pagamento das Multas na biblioteca</h2>
		
		<div class="descricaoOperacao" style="width:85%;">
			<p>Caro usuário, </p>
			<p>Utilize este formulário para imprimir as GRUs e realizar o pagamento das multas devidas na biblioteca. </p>
			<br/>
			<p><span style="font-weight: bold;">Observação:</span> O sistema possui uma rotina de confirmação de pagamento automatizada. Após o pagamento no sistema bancário, dentro de alguns dias a multa será quitada na biblioteca.
			Caso não queira esperar a confirmação automática do pagamento, compareça na biblioteca com a GRU paga e realize a quitação na própria biblioteca.
			</p>
			<br/>
			<br/>
			<p><strong>Não serão quitas as multas caso não se possua a GRU comprovadamente paga.</strong></p>
			<p>Você receberá uma mensagem de confirmação quando o pagamento da multa for confirmado no sistema.</p>
		</div>
		
		
		<%-- Exibe as informações do usuário --%>
		<c:set var="_infoUsuarioCirculacao" value="${emitirGRUPagamentoMultasBibliotecaMBean.informacaoUsuario}" scope="request"/>
		<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>
		
		
		<h:form id="formularioEmiteGRU">
		
			<div class="infoAltRem" style="width:90%;">
				<c:if test="${ not empty emitirGRUPagamentoMultasBibliotecaMBean.multasAtivasUsuario}">
					<h:graphicImage value="/img/imprimir.gif" />: Imprimir/Reimprimir GRU
				</c:if>
			</div>
		
			<table class="listagem" style="margin-bottom:10px;width:90%;">
				
				<caption>Multas sem confirmação de pagamento (${fn:length(emitirGRUPagamentoMultasBibliotecaMBean.multasAtivasUsuario)})</caption>
		
				<c:if test="${empty emitirGRUPagamentoMultasBibliotecaMBean.multasAtivasUsuario}">
					<tr><td style="text-align:center;font-weight:bold;color: green;">O usuário não possui multas no sistema</td></tr>
				</c:if>
		
				<c:if test="${not empty emitirGRUPagamentoMultasBibliotecaMBean.multasAtivasUsuario}">
					
					<thead>
						<tr>
							<th style="text-align:left;width: 30%">Valor</th>
							<th style="width: 50%;"></th>
							<th style="width: 10%;"></th>
							<th style="width: 10%;"></th> <%-- icone confirmar pagamento --%>
						</tr>
					</thead>
					
					<tbody>
						<c:forEach var="multa" items="#{emitirGRUPagamentoMultasBibliotecaMBean.multasAtivasUsuario}" varStatus="status">
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td style="text-align:left;">${multa.valorFormatado} </td>
								<td></td>
								<td></td>
								<td style="text-align: center;">
									<h:commandLink action="#{emitirGRUPagamentoMultasBibliotecaMBean.emitirGRU}" title="Imprimir GRU">
										<h:graphicImage value="/img/imprimir.gif" />
										<f:param name="idMultaSelecionada" value="#{multa.id}" />
									</h:commandLink>
								</td>
							</tr>
							
							<%-- Informações sobre o que gerou a multa --%>
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								
								<c:if test="${! multa.manual}">
									<td>Empréstimo que gerou a multa: </td>
									<td> 
											Material do empréstimo: <h:outputText value="#{multa.emprestimo.material.codigoBarras}" /> <br/>
											Data do empréstimo: <ufrn:format type="dataHora" valor="${multa.emprestimo.dataEmprestimo}" />  <br/>
											Prazo: <ufrn:format type="dataHora" valor="${multa.emprestimo.prazo}" />  <br/>
											Data da devolução: <ufrn:format type="dataHora" valor="${multa.emprestimo.dataDevolucao}" /> <br/>
									</td>
								</c:if>
								
								<c:if test="${multa.manual}">
									<td> Usuário que cadastrou a multa: </td>
									<td> 
										<h:outputText value="#{multa.usuarioCadastro.pessoa.nome}" />  <br/>
										<h:outputText value="#{multa.dataCadastro}" />   <br/>
										<h:outputText value="#{multa.motivoCadastro}" />   <br/>
									</td>
								</c:if>
								
								<td colspan="2" style="text-align: center; font-style: italic;">
									<h:outputText value="GRU Não Gerada" rendered="#{ multa.idGRUQuitacao == null }" style="color:red" />
									<h:outputText value="GRU Já Gerada"    rendered="#{ multa.idGRUQuitacao != null }"  style="color:green" />
								</td>
								
							</tr>
							
							
						</c:forEach>
						
						
						
					</tbody>
					
					<tfoot>
						<tr>
							<td colspan="4" style="text-align: center;">
								<h:commandButton id="cmdEmitirGRUUnica"   value="Emitir GRU Única para as Multas Abertas" 
								action="#{emitirGRUPagamentoMultasBibliotecaMBean.preEmitirGRUUnicaParaMultasAbertas}"
								rendered="#{emitirGRUPagamentoMultasBibliotecaMBean.quantidadeGRUsNaoEmitidas > 0}" />
							
								<h:commandButton id="fakeCmdVisualizaGRUUnicaMultas" value="Fake" style="display:none;" 
									actionListener="#{emitirGRUPagamentoMultasBibliotecaMBean.visualizarGRUUnicaGerada}" />
							
								<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{emitirGRUPagamentoMultasBibliotecaMBean.cancelar}" immediate="true" id="cancelar" />
							</td>
						</tr>
					</tfoot>
					
				</c:if>
		
			</table>
		
		
		
		</h:form>
		
</f:view>


<script type="text/javascript">

function mostrarGRU(){
	document.getElementById('formularioEmiteGRU:fakeCmdVisualizaGRUUnicaMultas').click();
}

<c:if test="${emitirGRUPagamentoMultasBibliotecaMBean.outputSteam != null}" >
	mostrarGRU();
</c:if>


</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>