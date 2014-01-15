<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<f:view>

		<a4j:keepAlive beanName="emitirGRUPagamentoMultasBibliotecaMBean" />

		<h2> <ufrn:subSistema /> &gt; Imprimir GRU para Pagamento das Multas na biblioteca</h2>
		
		<div class="descricaoOperacao" style="width:85%;">
			<p>Caro usu�rio, </p>
			<p>Utilize este formul�rio para imprimir as GRUs e realizar o pagamento das multas devidas na biblioteca. </p>
			<br/>
			<p><span style="font-weight: bold;">Observa��o:</span> O sistema possui uma rotina de confirma��o de pagamento automatizada. Ap�s o pagamento no sistema banc�rio, dentro de alguns dias a multa ser� quitada na biblioteca.
			Caso n�o queira esperar a confirma��o autom�tica do pagamento, compare�a na biblioteca com a GRU paga e realize a quita��o na pr�pria biblioteca.
			</p>
			<br/>
			<br/>
			<p><strong>N�o ser�o quitas as multas caso n�o se possua a GRU comprovadamente paga.</strong></p>
			<p>Voc� receber� uma mensagem de confirma��o quando o pagamento da multa for confirmado no sistema.</p>
		</div>
		
		
		<%-- Exibe as informa��es do usu�rio --%>
		<c:set var="_infoUsuarioCirculacao" value="${emitirGRUPagamentoMultasBibliotecaMBean.informacaoUsuario}" scope="request"/>
		<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>
		
		
		<h:form id="formularioEmiteGRU">
		
			<div class="infoAltRem" style="width:90%;">
				<c:if test="${ not empty emitirGRUPagamentoMultasBibliotecaMBean.multasAtivasUsuario}">
					<h:graphicImage value="/img/imprimir.gif" />: Imprimir/Reimprimir GRU
				</c:if>
			</div>
		
			<table class="listagem" style="margin-bottom:10px;width:90%;">
				
				<caption>Multas sem confirma��o de pagamento (${fn:length(emitirGRUPagamentoMultasBibliotecaMBean.multasAtivasUsuario)})</caption>
		
				<c:if test="${empty emitirGRUPagamentoMultasBibliotecaMBean.multasAtivasUsuario}">
					<tr><td style="text-align:center;font-weight:bold;color: green;">O usu�rio n�o possui multas no sistema</td></tr>
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
							
							<%-- Informa��es sobre o que gerou a multa --%>
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								
								<c:if test="${! multa.manual}">
									<td>Empr�stimo que gerou a multa: </td>
									<td> 
											Material do empr�stimo: <h:outputText value="#{multa.emprestimo.material.codigoBarras}" /> <br/>
											Data do empr�stimo: <ufrn:format type="dataHora" valor="${multa.emprestimo.dataEmprestimo}" />  <br/>
											Prazo: <ufrn:format type="dataHora" valor="${multa.emprestimo.prazo}" />  <br/>
											Data da devolu��o: <ufrn:format type="dataHora" valor="${multa.emprestimo.dataDevolucao}" /> <br/>
									</td>
								</c:if>
								
								<c:if test="${multa.manual}">
									<td> Usu�rio que cadastrou a multa: </td>
									<td> 
										<h:outputText value="#{multa.usuarioCadastro.pessoa.nome}" />  <br/>
										<h:outputText value="#{multa.dataCadastro}" />   <br/>
										<h:outputText value="#{multa.motivoCadastro}" />   <br/>
									</td>
								</c:if>
								
								<td colspan="2" style="text-align: center; font-style: italic;">
									<h:outputText value="GRU N�o Gerada" rendered="#{ multa.idGRUQuitacao == null }" style="color:red" />
									<h:outputText value="GRU J� Gerada"    rendered="#{ multa.idGRUQuitacao != null }"  style="color:green" />
								</td>
								
							</tr>
							
							
						</c:forEach>
						
						
						
					</tbody>
					
					<tfoot>
						<tr>
							<td colspan="4" style="text-align: center;">
								<h:commandButton id="cmdEmitirGRUUnica"   value="Emitir GRU �nica para as Multas Abertas" 
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