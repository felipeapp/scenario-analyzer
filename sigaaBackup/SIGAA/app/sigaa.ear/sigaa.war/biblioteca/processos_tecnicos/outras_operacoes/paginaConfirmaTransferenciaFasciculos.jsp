<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<%-- Criada apenas para visualizar os dados inconsistentes para migra��o --%>


<style>	
	table.listagem td.oculto { display: none; padding: 0;}
	
</style>

<script type="text/javascript" src="/sigaa/javascript/biblioteca/mostraDetalhes.js"> </script>




<h2>  <ufrn:subSistema /> &gt; Escolhe Unidade Destino dos Fasc�culos</h2>

<div class="descricaoOperacao"> 
   <p>Escolha para qual assinatura os fasc�culos devem ser transferidos.</p>
   <p><strong>Observa��o 1: </strong>Para um fasc�culo ser transferido para outra biblioteca � necess�rio que a biblioteca destino 
   possua uma assinatura criada para o mesmo T�tulo dos fasc�culos. Caso a biblioteca destino n�o possua essa assinatura, pode-se delegar a cria��o dela  
   para o bibliotec�rio que vai autorizar a transfer�ncia.</p>
   <p><strong>Observa��o 2:</strong> Caso a assinatura escolhida seja de outra biblioteca, os fasc�culos n�o ser�o transferidos de imediato, ficar�o pendentes esperando a verifica��o e autoriza��o de algum bibliotec�rio da biblioteca destino.</p>
</div>



<f:view>

	<a4j:keepAlive beanName="solicitaTransferenciaFasciculosEntreAssinaturasMBean" />
	
	
	<h:form id="formConfirmaTransferenciaFasciculo">

		<table class="formulario" style="width:90%; margin-bottom: 10px;">
					
			<caption> Buscar a assinatura destino dos fasc�culos </caption>
				
			<tbody>
			
				<tr>
					<th colspan="1">Biblioteca de Destino dos Fasc�culos:</th>
					<td colspan="3" >
						<h:selectOneMenu id="comboBoxBuscaBibliotecasDestinoTranferencia" value="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.bibliotecaDestino.id}"
								valueChangeListener="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.buscaAllAssinaturasDaBibliotecaSelecionadaQuePossuamMesmoTitulo}"
								onchange="submit();">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
							<f:selectItems value="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.bibliotecasInternas}"/>
						</h:selectOneMenu>
					</td>
				</tr>
				
				<%-- 
				 --    N�o existem assinaturas na biblioteca destino, ent�o o usu�rio que est� fazendo 
				 --    a transfer�ncia pode solicitar que essa assinatura seja criada pelo bibliotec�rio que vai autorizar a transfer�ncia 
				--%>
				<c:if test="${solicitaTransferenciaFasciculosEntreAssinaturasMBean.bibliotecaDestino.id > 0 && fn:length(solicitaTransferenciaFasciculosEntreAssinaturasMBean.assinaturasDestino) == 0}">
				
					<tr>
						<td colspan="4">
							<table style="width: 100%">
								<tr>
									<td style="width: 50%; text-align: right;">
										Solicitar cria��o da assinatura
									</td>
									<td style="width: 50%; text-align: left;"> 
										<h:selectBooleanCheckbox id="checkBoxCriacaoAssinaturaNoMomentoVerificacao" value="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.solicitarCriacaoAssinatura}" onclick="deselecionaOsOutrosCheckBox(this);"> </h:selectBooleanCheckbox>
										<ufrn:help>Marque essa op��o para deixar a cria��o da assinatura dos fasc�culos pendente. Ela dever� ser feita pelo bibliotec�rio da biblioteca destino, no momento da autoriza��o da transfer�ncia. </ufrn:help> 
									</td>
								</tr>
								
							</table>
						</td>
						
					</tr>
				
				</c:if>
				
				
				<c:if test="${fn:length(solicitaTransferenciaFasciculosEntreAssinaturasMBean.assinaturasDestino) > 0}">
					
					<tr>
						<td colspan="4">
						
							<table class="listagem" width="100%">
								<caption> Assinaturas ( ${fn:length(solicitaTransferenciaFasciculosEntreAssinaturasMBean.assinaturasDestino)} ) </caption>	
								
								<thead>
									<tr>
										<th style="text-align: left; width: 5%; font-weight: bold;">C�digo</th>
										<th style="text-align: left; width: 20%; font-weight: bold;">T�tulo</th>
										<th style="text-align: left; width: 20%; font-weight: bold;">Unidade Destino</th>
										<th style="text-align: center; width: 5%; font-weight: bold;">Internacional?</th>
										<th style="text-align: center; width: 10%; font-weight: bold;">Modalidade Aquisi��o</th>
										<th style="text-align: left; 29%; font-weight: bold;">Criada por</th>
										<th style="width: 1%"> </th>
									</tr>
								</thead>
								
								<c:forEach items="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.assinaturasDestino}" var="assinatura" varStatus="loop">
									<tr  class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<td> ${assinatura.codigo} </td>
										<td> ${assinatura.titulo}</td>
										<td> ${assinatura.unidadeDestino.descricao}</td>
										<td style="text-align: center">
											<c:if test="${assinatura.internacional}">
												SIM
											</c:if>
											<c:if test="${! assinatura.internacional}">
												N�O
											</c:if>
										</td>
										<td style="text-align: center">
											<c:if test="${assinatura.assinaturaDeCompra}">
												COMPRA
											</c:if>
											<c:if test="${assinatura.assinaturaDeDoacao}">
												DOA��O
											</c:if>
											<c:if test="${! assinatura.assinaturaDeCompra &&  ! assinatura.assinaturaDeDoacao  }">
												INDEFINIDO
											</c:if>
										</td>
										<td> ${assinatura.nomeCriador}</td>
										<td> 
											<h:selectBooleanCheckbox id="checkBoxSelecionaAssinatura" value="#{assinatura.selecionada}" rendered="#{assinatura != null}"  onclick="deselecionaOsOutrosCheckBox(this);"> </h:selectBooleanCheckbox>
										</td>
									</tr>
								</c:forEach>
						
							</table>
				
						</td>
						
					</tr>
				
				</c:if>
			
			</tbody>
			
			<tfoot>
				
				<tr>
					<td colspan="10" style="text-align: center; font-weight: bold;">
						<h:commandButton id="botaoConfirmarTransferencia" value="Transferir" action="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.transferirFasciculos}" onclick="return confirm('Confirma a transfer�ncia dos fasc�culos ? ');" />
						<h:commandButton id="botaoVoltar" value="<< Voltar" action="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.telaSelecionaFasciculosTransferencia}" />
						<h:commandButton value="Cancelar" action="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
					</td>
				</tr>
				
			</tfoot>
			
		</table>


		<table class="listagem" style="width:50%;" >
			<caption> Fasc�culos selecionados para a transfer�ncia ( ${fn:length(solicitaTransferenciaFasciculosEntreAssinaturasMBean.fasciculosTransferencia) } ) </caption>
			
			<thead>
				<tr>
					<th style="text-align: left;">C�digo de Barras</th>
					<th style="text-align: left;">Ano Cronol�gico</th>
					<th style="text-align: left;">Ano</th>
					<th style="text-align: left;">Volume</th>
					<th style="text-align: left;">N�mero</th>
					<th style="text-align: left;">Edi��o</th>
				</tr>
			</thead>
			
			<c:forEach items="#{solicitaTransferenciaFasciculosEntreAssinaturasMBean.fasciculosTransferencia}" var="fasciculo" varStatus="loop">
				<tr  class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td> ${fasciculo.codigoBarras} </td>
					<td> ${fasciculo.anoCronologico} </td>
					<td> ${fasciculo.ano} </td>
					<td> ${fasciculo.volume} </td>
					<td> ${fasciculo.numero} </td>
					<td> ${fasciculo.edicao} </td>
				</tr>
			</c:forEach>
			
		</table>


	</h:form>


</f:view>

<script type="text/javascript">

	function deselecionaOsOutrosCheckBox(chk){
		for (i=0; i<document.formConfirmaTransferenciaFasciculo.elements.length; i++){
	       if(document.formConfirmaTransferenciaFasciculo.elements[i].type == "checkbox"){
				if(document.formConfirmaTransferenciaFasciculo.elements[i] != chk){
		  			document.formConfirmaTransferenciaFasciculo.elements[i].checked= false;
				}
	       }
		}
	}


</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>