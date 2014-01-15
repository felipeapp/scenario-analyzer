<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<f:view>

		<a4j:keepAlive beanName="multasUsuarioBibliotecaMBean" />
		<a4j:keepAlive beanName="buscaUsuarioBibliotecaMBean" />

		<h2> <ufrn:subSistema /> &gt; Imprimir GRU �nica para V�rias Multas</h2>

		<div class="descricaoOperacao" style="width:85%;">
			<p>Essa opera��o permite simplificar o processo de pagamento de GRUs emitindo uma �nica GRU para o pagamento de v�rias multas do usu�rio.</p>
			<p>S� � poss�vel emitir a GRU para as multas cuja GRU n�o foi gerada ainda e as multas selecionadas devem possuir a mesma unidade de recolhimento.</p>
			<p>A unidade de recolhimento � a unidade que via receber o pagamento e vai ser impressa na GRU.</p>
		</div>	


	   

		<h:form id="formImprimirGRUUnica">

			<table class="formulario" style="width: 100%;">
			
			<caption>Multas do Usu�rio n�o Pagas ( ${fn:length(multasUsuarioBibliotecaMBean.multasAtivasUsuario)} )</caption>
			
				<c:forEach var="multa" items="#{multasUsuarioBibliotecaMBean.multasAtivasUsuario}" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						<td  style="text-align:left;" >${multa.valorFormatado} </td>
						
						<td colspan="2"> ${! multa.manual ? 'MULTA GERADA PELO SISTEMA' : 'MULTA MANUAL'} </td>
						
						<td colspan="1" style="width: 5%;">
							<h:selectBooleanCheckbox id="ckeckSelecionaMulta" value="#{multa.selecionado}" />
						</td>
						
					</tr>
					
					<%-- Informa��es sobre o que gerou a multa --%>
					<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
						
						<td colspan="1"> </td>
						
						<c:if test="${! multa.manual}">
							<td> 
								Data do empr�stimo: <ufrn:format type="dataHora" valor="${multa.emprestimo.dataEmprestimo}" />  <br/>
								Prazo: <ufrn:format type="dataHora" valor="${multa.emprestimo.prazo}" />  <br/>
								Data da devolu��o: <ufrn:format type="dataHora" valor="${multa.emprestimo.dataDevolucao}" /> <br/>
								Material do empr�stimo: <h:outputText value="#{multa.emprestimo.material.codigoBarras}" /> <br/>
								<span style="font-weight: bold;"> Unidade de Recolhimento: ${multa.emprestimo.material.biblioteca.descricao} </span> <br/>
							</td>
							
						</c:if>
						
						<c:if test="${multa.manual}"> 
							<td> 
								Cadastrado por: <h:outputText value="#{multa.usuarioCadastro.pessoa.nome}" />  <br/>
								Data do Cadastro:  <ufrn:format type="dataHora" valor="${multa.dataCadastro}" />  <br/>
								Motivo: <h:outputText value="#{multa.motivoCadastro}" />   <br/>
								<span style="font-weight: bold;"> Unidade de Recolhimento: ${multa.bibliotecaRecolhimento.descricao} </span> <br/>
							</td>
						</c:if>
						
						<td style="text-align: center; font-style: italic;">
							<h:outputText value="GRU N�o Gerada" rendered="#{ multa.idGRUQuitacao == null }" style="color:red" />
							<h:outputText value="GRU J� Gerada"    rendered="#{ multa.idGRUQuitacao != null }"  style="color:green" />
						</td>
						
						<td colspan="1"> </td>
						
					</tr> 
					
					
			</c:forEach>	
	
				<tfoot>
					<tr>
						<td colspan="6" style="text-align: center;">
							<h:commandButton id="cmdEmitirGRUUnica"   value="Confirmar Emiss�o para as Multas Selecionadas" action="#{multasUsuarioBibliotecaMBean.emitirGRUUnicaParaMultasAbertas}" />
							<h:commandButton id="cmdCancelar"  value="Cancelar" action="#{multasUsuarioBibliotecaMBean.telaListaMultasUsuario}" immediate="true" />
						</td>
					</tr>
				</tfoot>
	
			</table>

		</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>