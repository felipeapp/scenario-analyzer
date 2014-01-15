<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<f:view>

		<a4j:keepAlive beanName="multasUsuarioBibliotecaMBean" />
		<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />

		<h2> <ufrn:subSistema /> &gt; Gerenciar Multas</h2>
		
		<div class="descricaoOperacao" style="width:85%;">
			<p>Caro usu�rio, utilize este formul�rio para gerenciar as multas de usu�rios da biblioteca.</p>
			
			<br/>
			<p><span style="font-weight: bold;">Observa��o:</span> O sistema possui uma rotina de confirma��o de pagamento automatizada. Ap�s o pagamento no sistema banc�rio, dentro de alguns dias a multa ser� quitada na biblioteca.
			Caso o usu�rio necessite realizar um novo empr�stimo antes da confirma��o autom�tica ser realizada, pode-ser utilizar essa opera��o para confirmar o pagamento manualmente mediante apresenta��o da GRU com o comprovante de pagamento.
			</p>
			<br/>
			<br/>
			<p>Caso deseje criar uma nova multa manualmente para o usu�rio, seleciona a op��o <i>"Criar Nova Multa"</i>.
			<p>Para estornar as multas indevidas do usu�rio clique em <i>"Estornar Multas"</i> (O motivo do estorno pode deve ser informado).</p>
			<br/>
			
			<div style="font-weight: bold; text-align: center;"> IMPORTANTE </div>
			<div> 
			<table style="border: 1px solid;">
			<tr>
				<td>
					<p>Para ser gerada, a GRU precisa que uma "Configura��o de GRU" esteja previamente criada no sistema.</p>
				</td>
			</tr>
			<tr>	
				<td>
					<p>Uma "Configura��o de GRU" possui informa��es como o c�digo de recolhimento, unidade de recebimento e a compet�ncia a serem impressas nas GRUs.</p>
				</td>
			</tr>
			<tr>
				<td>
					<p>Pode-se criar uma "Configura��o de GRU" para cada biblioteca do sistema, assim a unidade de recebimento vai ser a unidade da biblioteca onde foi realizado o empr�stimo.</p>
				</td>
			</tr>
			<tr>	
				<td>
					<p>Por padr�o caso nenhuma biblioteca setorial possua uma "Configura��o de GRU", o sistema utilizar� a "Configura��o de GRU" da biblioteca central, assim a unidade
			de recebimento de todas as multas ser� a unidade da biblioteca central.</p>
				</td>
			</tr>
			</table>
			</div>		
		</div>

		<%-- Exibe as informa��es do usu�rio --%>
		<c:set var="_infoUsuarioCirculacao" value="${multasUsuarioBibliotecaMBean.informacaoUsuario}" scope="request"/>
		<c:set var="_mostrarVinculo" value="false" scope="request" />
		<%@ include file="/biblioteca/circulacao/dadosUsuarioBiblioteca.jsp"%><br>


	<h:form id="formularioGerenciaMultas">
		
		<div class="infoAltRem" style="width:90%;">
			
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
				<h:graphicImage value="/img/adicionar.gif" /> <h:commandLink id="cmdCadastrarMulta" action="#{multasUsuarioBibliotecaMBean.preCadastrar}" value="Cadastrar Nova Multa" />
			</ufrn:checkRole>
			
			<c:if test="${ not empty multasUsuarioBibliotecaMBean.multasAtivasUsuario}">
					<h:graphicImage value="/img/pagamento.png" />: Confirmar Pagamento Manualmente
					<h:graphicImage value="/img/imprimir.gif" />: Imprimir/Reimprimir GRU
				</c:if>
			
			<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
			
				<c:if test="${ not empty multasUsuarioBibliotecaMBean.multasAtivasUsuario}">
					<h:graphicImage value="/img/biblioteca/estornar.gif" />: Estornar Multa
				</c:if>
				
			</ufrn:checkRole>
		</div>


		<table class="listagem" style="margin-bottom:10px;width:90%;">
			<caption>Multas do Usu�rio n�o Pagas ( ${fn:length(multasUsuarioBibliotecaMBean.multasAtivasUsuario)} )</caption>
			
				<c:if test="${not empty multasUsuarioBibliotecaMBean.multasAtivasUsuario}">
					<thead>
						<tr>
							<th style="text-align:left; width: 25%">Valor</th>
							<th style="width: 40%;">Tipo da Multa</th>
							<th style="width: 33%;"></th>
							<th style="width: 1%;"></th> <%-- icone confirmar pagamento --%>
							<th style="width: 1%;"></th> <%-- icone para imprimir a GRU --%>
							<th style="width: 1%;"></th>  <%-- icone estornar multa --%>
						</tr>
					</thead>
					
					<tbody>
						<c:forEach var="multa" items="#{multasUsuarioBibliotecaMBean.multasAtivasUsuario}" varStatus="status">
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								<td  style="text-align:left;">${multa.valorFormatado} </td>
								<td colspan="2"> ${! multa.manual ? 'MULTA GERADA PELO SISTEMA' : 'MULTA MANUAL'} </td>
								<td>
									<h:commandLink action="#{multasUsuarioBibliotecaMBean.preConfirmarPagamento}" title="Confirmar Pagamento Manualmente">
										<h:graphicImage value="/img/pagamento.png" />
										<f:param name="idMultaSelecionada" value="#{multa.id}" />
									</h:commandLink>
								</td>
								<td>
									<h:commandLink action="#{multasUsuarioBibliotecaMBean.emitirGRU}" title="Imprimir/Reimprimir GRU">
										<h:graphicImage value="/img/imprimir.gif" />
										<f:param name="idMultaSelecionada" value="#{multa.id}" />
									</h:commandLink>
								</td>
								<td>
									<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL}  %>">
										<h:commandLink action="#{multasUsuarioBibliotecaMBean.preEstornar}" title="Estornar Multa">
											<h:graphicImage value="/img/biblioteca/estornar.gif" />
											<f:param name="idMultaSelecionada" value="#{multa.id}" />
										</h:commandLink>
									</ufrn:checkRole>
								</td>
							</tr>
							
							<%-- Informa��es sobre o que gerou a multa --%>
							<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
								
								<c:if test="${! multa.manual}">
									<td colspan="1"> </td>
									<td> 
											Data do empr�stimo: <ufrn:format type="dataHora" valor="${multa.emprestimo.dataEmprestimo}" />  <br/>
											Prazo: <ufrn:format type="dataHora" valor="${multa.emprestimo.prazo}" />  <br/>
											Data da devolu��o: <ufrn:format type="dataHora" valor="${multa.emprestimo.dataDevolucao}" /> <br/>
											Material do empr�stimo: <h:outputText value="#{multa.emprestimo.material.codigoBarras}" /> <br/>
									</td>
								</c:if>
								
								<c:if test="${multa.manual}"> 
									<td colspan="1"> </td>
									<td> 
										Cadastrado por: <h:outputText value="#{multa.usuarioCadastro.pessoa.nome}" />  <br/>
										Data do Cadastro: <h:outputText value="#{multa.dataCadastro}" />   <br/>
										Motivo: <h:outputText value="#{multa.motivoCadastro}" />   <br/>
									</td>
								</c:if>
								
								<td colspan="4" style="text-align: center; font-style: italic;">
									<h:outputText value="GRU N�o Gerada" rendered="#{ multa.idGRUQuitacao == null }" style="color:red" />
									<h:outputText value="GRU J� Gerada"    rendered="#{ multa.idGRUQuitacao != null }"  style="color:green" />
								</td>
								
							</tr> 
							
							
						</c:forEach>	
						
						<tr>
							<td colspan="6" style="font-weight: bold; text-align: center; background-color: #DEDFE3;"> Valor Total das multas do usu�rio: ${multasUsuarioBibliotecaMBean.valorTotalMultasFormatado}</td>
						</tr>
					</tbody>
				</c:if>
			
				<tfoot>
					<tr>
						<td colspan="6" style="text-align: center;">
							<h:commandButton id="cmdEmitirGRUUnica"   value="Emitir GRU �nica para as Multas Abertas" 
								action="#{multasUsuarioBibliotecaMBean.preEmitirGRUUnicaParaMultasAbertas}" 
								rendered="#{multasUsuarioBibliotecaMBean.quantidadeGRUsNaoEmitidas > 0}" />
							
							<h:commandButton id="fakeCmdVisualizaGRUUnicaMultas" value="Fake" style="display:none;" 
							actionListener="#{multasUsuarioBibliotecaMBean.visualizarGRUUnicaGerada}" />
							
							<h:commandButton id="cmdVoltar" value="<< Voltar" action="#{buscaUsuarioBibliotecaMBean.telaBuscaUsuarioBiblioteca}"  />
							<h:commandButton id="cmdCancelar"  value="Cancelar" onclick="#{confirm}" action="#{multasUsuarioBibliotecaMBean.cancelar}" immediate="true" />
						</td>
					</tr>
				</tfoot>
			
				
				<c:if test="${empty multasUsuarioBibliotecaMBean.multasAtivasUsuario}">
					<tr><td style="text-align:center;font-weight:bold; color: green;">O usu�rio n�o possui multas no sistema</td></tr>
				</c:if>
				
		</table>

	</h:form>
	
</f:view>


<script type="text/javascript">

function mostrarGRU(){
	document.getElementById('formularioGerenciaMultas:fakeCmdVisualizaGRUUnicaMultas').click();
}

<c:if test="${multasUsuarioBibliotecaMBean.outputSteam != null}" >
	mostrarGRU();
</c:if>


</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>