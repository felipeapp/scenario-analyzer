<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<h2>  <ufrn:subSistema /> &gt; Material que Deseja para Realizar a Solicitação </h2>

<f:view>

<div class="descricaoOperacao"> 
   <p>Página para solicitação de material de outra biblioteca. </p>
   <p><strong>Somente materiais que não estejam emprestados podem ser solicitados. </strong></p>
</div>


	<a4j:keepAlive beanName="solicitacaoMaterialOutraBibliotecaMBean" />

	<h:form id="formPesquisaMaterialMovimentacao">	
		
		
		<c:if test="${fn:length( solicitacaoMaterialOutraBibliotecaMBean.materiais) > 0}">
			
			<table class="listagem" style="width:90%">
				<caption> Dados do Material Escolhido para Solicitar Envio para Outra Biblioteca </caption>	
				
				<c:forEach items="#{solicitacaoMaterialOutraBibliotecaMBean.materiais}" var="material" varStatus="status">
					<tr>
						<td style="text-align: right; font-weight:bold">Código de Barras:</td>
						<td style="text-align: left">${material.codigoBarras}</td>
					</tr>
					<tr>
						<td style="text-align: right; font-weight:bold">Biblioteca:</td>
						<td style="text-align: left">${material.biblioteca.descricao}</td>
					</tr>
					<tr>
						<td style="text-align: right; font-weight:bold">Status:</td>
						<td style="text-align: left">${material.status.descricao}</td>
					</tr>
					<tr>
						<td style="text-align: right; font-weight:bold">Situação:</td>
						<c:if test="${material.disponivel}"> 
							<td style="text-align: left; color:green"> ${material.situacao.descricao}
								<c:if test="${not empty material.prazoConcluirReserva}">  [Prazo Reserva:<ufrn:format type="data" valor="${material.prazoConcluirReserva}"/>] </c:if>
							</td>
						</c:if>
						<c:if test="${! material.disponivel && ! material.emprestado}"> 
							<td style="text-align: left"> ${material.situacao.descricao}</td>
						</c:if>
						<c:if test="${material.emprestado}"> 
							<td style="text-align: left; color:red"> ${material.situacao.descricao}
							[Prazo:<ufrn:format type="data" valor="${material.prazoEmprestimo}"/>]
							</td>
						</c:if>
					</tr>
				</c:forEach>		
				
				<tr>
					<td colspan="6">
						<table class="subFormulario" style="width: 90%">
							<tr>
								<th style="width: 30%">Solicitar envio do material para Biblioteca:</th>
								<td style="width: 60%">
									<h:selectOneMenu value="#{solicitacaoMaterialOutraBibliotecaMBean.bibliotecaDestino.id}">
										<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
										<f:selectItems value="#{solicitacaoMaterialOutraBibliotecaMBean.bibliotecasInternasAtivas}"/>
									</h:selectOneMenu>
								</td>
							</tr>
						</table>
					</td>
				</tr>
				
				<tfoot>
					<tr>
						<td colspan="6" style="text-align: center">
						
							<h:commandButton value="Solicitar" action="#{solicitacaoMaterialOutraBibliotecaMBean.solicitaMateriais}" 
										onclick="return confirm('Confirma solicitação de Material? ');"/>
							<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{solicitacaoMaterialOutraBibliotecaMBean.cancelar}" />
							
						</td>
					</tr>
				</tfoot>
				
			</table>
			
		</c:if>	
			
	</h:form>

</f:view>



<script type="text/javascript">

	checarRadioButton();    // executa quando a página é carregada

	function checarRadioButton(){
		document.getElementById('formPesquisaMaterialMovimentacao:inputTxtCodBarras').focus();
	}
	


	function selecionarTudo(chk){
	   for (i=0; i<document.resultadoPesquisaFasciculosTransferencia.elements.length; i++)
	      if(document.resultadoPesquisaFasciculosTransferencia.elements[i].type == "checkbox")
	         document.resultadoPesquisaFasciculosTransferencia.elements[i].checked= chk.checked;
	}

</script>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>