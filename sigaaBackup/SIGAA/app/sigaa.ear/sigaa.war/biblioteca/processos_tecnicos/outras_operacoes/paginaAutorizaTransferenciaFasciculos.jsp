<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<h2>  <ufrn:subSistema /> &gt; Autorizar Transfer�ncia de Fasc�culos </h2>

<div class="descricaoOperacao"> 
   <p>P�gina para autorizar transfer�ncia de fasc�culos entre bibliotecas. </p>
   <p>Existem tr�s op��es que podem ser escolhidas:</p>
   <ul>
   		<li><strong>SIM</strong> - A transfer�ncia ser� autorizada e o fasc�culo mudar� de biblioteca.</li>
   		<li><strong>N�O</strong> - A transfer�ncia ser� cancelada e o fasc�culo voltar� a biblioteca origem.</li>
   		<li><strong>AGUARDAR</strong> - O fasc�culo permanece na listagem at� que a sua transfer�ncia seja autorizada ou cancelada.</li>
   </ul>
   
   <br/>
   <p>
	   Por padr�o, na transferencia os fasc�culos permanencem com o mesmo c�digo de barras. Assim fasc�culos da assinatura de c�digo "1234"
	   transferidos para a assintura de c�digo "5678", permanecem com o c�digo de barras 1234-1, 1234-2, 1234-3, apesar de pertencerem agora a assinatura
	   "5678". Contudo pode-se solicitar para que os c�digo de barras dos fasc�culos sejam alterados para ficarem com os c�digo de barras iguais aos c�digo 
	   de barras dos fasc�culos da assinatura destino, 5678-4, 5678-5 e 5678-6.
   </p>
   <br/>
   
   <p>Observa��o 1: Para cada fasc�culo cuja transfer�ncia n�o for autorizada, deve-se informar o motivo, esse motivo ser� enviado � caixa postal 
   do usu�rio que a solicitou.</p>
   <p>Observa��o 2: Caso a solicita��o tenha sido feita para uma biblioteca que n�o possu�a uma assinatura para os fasc�culos, ser� necess�rio primeiro criar essa assinatura.</p>
</div>

<style type="text/css">

	table.subFormulario tr.biblioteca td{
		background: #EEEEEE;
		font-weight: bold;
		padding-left: 20px;
	}
	
	table.listagem tr.biblioteca td{
		background: #EEEEEE;
		font-weight: bold;
		padding-left: 20px;
	}

</style>


<script type="text/javascript">

	function habilitarCampoMotivo(idLinha){
			
		if (/msie/.test( navigator.userAgent.toLowerCase() )){
			$('labelMotivo_'+idLinha).style.display = 'inline-block';
			$('textoMotivo_'+idLinha).style.display = 'inline-block';
			$('labelQtdCaracteres_'+idLinha).style.display = 'inline-block';
			$('qtdCaracteres_'+idLinha).style.display = 'inline-block';
			$('colunaCaracteresRestantes_'+idLinha).style.display = 'inline-block';
		}else{
			$('labelMotivo_'+idLinha).style.display = 'table-cell';
			$('textoMotivo_'+idLinha).style.display = 'table-cell';
			$('labelQtdCaracteres_'+idLinha).style.display = 'table-cell';
			$('qtdCaracteres_'+idLinha).style.display = 'table-cell';
			$('colunaCaracteresRestantes_'+idLinha).style.display = 'table-cell';
		}

	}

	function desHabilitarCampoMotivo(idLinha){
		$('labelMotivo_'+idLinha).style.display = 'none';
		$('textoMotivo_'+idLinha).style.display = 'none';
		$('labelQtdCaracteres_'+idLinha).style.display = 'none';
		$('qtdCaracteres_'+idLinha).style.display = 'none';
		$('colunaCaracteresRestantes_'+idLinha).style.display = 'none';

	}
	

	function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
		
		if (field.value.length > maxlimit){
			field.value = field.value.substring(0, maxlimit);
		}else{ 
			document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
		} 
	}
	
</script>

<f:view>

	<a4j:keepAlive beanName="autorizaTransferenciaFasciculosEntreAssinaturasMBean" />
	<a4j:keepAlive beanName="assinaturaPeriodicoMBean" />
	
	<h:form id="pesquisaAutorizaTransferenciaFasciculos">
	
		
		
		<table class="formulario" width="80%" style="margin-bottom: 20px">
		
			<caption>Selecione a Biblioteca Destino dos Fasc�culos </caption>
			
			<tr>
				<th style="width: 30% " >Biblioteca:</th>
				<td>
					<h:selectOneMenu id="comboboxBibliotecasDestinoTransferencia" value="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.obj.unidadeDestino.id}"
							valueChangeListener="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.verificaAssinaturaPendentesDaBibliotecaEscolhida}" 
							onchange="submit();">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						<f:selectItems value="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.bibliotecasInternasDoUsuario}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2" style="text-align: center;">
						<h:commandButton value="Cancelar"  action="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
			
		</table>
			
		
		<%--   N�o existe nada pendente de autoriza��o    --%>	
		<c:if test="${ autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturasComFasciculosPendentes == null
			&& autorizaTransferenciaFasciculosEntreAssinaturasMBean.dadosMovimentacaoSemAssinatura == null 
			&& autorizaTransferenciaFasciculosEntreAssinaturasMBean.obj.unidadeDestino.id != -1}">
				
			<table class="listagem" width="100%">
				<caption> Assinaturas Destino dos Fasc�culos </caption>
				
				<tr>
					<td style="color: red; text-align: center; "> Nenhum fasc�culo pendente de autoriza��o para a biblioteca escolhida</td>
				</tr>
				
			</table>
		
		</c:if>	
			
			
		
		
			
		<%--  Existem fasc�culos pendentes de autoriza��o com assinaturas     --%>	
			
		<c:if test="${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturasComFasciculosPendentes != null }">

			
				
			<c:if test="${ fn:length(autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturasComFasciculosPendentes) > 0 }">
				
				<div class="infoAltRem" style="margin-top: 10px">
					<h:graphicImage value="/img/biblioteca/baixo.gif" style="overflow: visible;" />: 
						Exibir fasc�culos pendentes de autoriza��o da assinatura
	    			<h:graphicImage value="/img/biblioteca/cima.gif" style="overflow: visible;" />: 
						Ocultar fasc�culos pendentes de autoriza��o da assinatura
				</div>
				
				
				<table class="listagem" width="100%">
					<caption> Assinaturas Destino dos Fasc�culos </caption>
					
					<thead>
						<tr>
							<th style="text-align: left">C�digo</th>
							<th style="text-align: left">T�tulo</th>
							<th style="text-align: left">Unidade Destino</th>
							<th style="text-align: center">Internacional?</th>
							<th style="text-align: center">Modalidade de Aquisi��o</th>
							<th style="width: 1%"> </th>
						</tr>
					</thead>
					
					<c:forEach items="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturasComFasciculosPendentes}" var="assinatura" varStatus="loop">
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
							<td>
								<h:commandLink id="cmdLinkVisualizaFasciculosSolicitados" action="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.visualizarFasciculosPendentesAutorizacao}">
									<h:graphicImage id="imagem_${assinatura.id}" url="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaSelecionada.id == assinatura.id ? '/img/biblioteca/cima.gif' : '/img/biblioteca/baixo.gif'}" 
									style="border:none" title="Visualizar fasc�culos pendentes de autoriza��o da assinatura " />
									<f:param name="idAssinaturaSelecionada" value="#{assinatura.id}"/>	
								</h:commandLink>
							</td>
						</tr>
						
					</c:forEach>
					
					<tr>	
						<td colspan="6" style="height: 20px;">
						</td>
					</tr>
					
					
					
					
					
					<%-- Fasc�culos pendentes de autoriza��o --%>	
					<c:if test="${ fn:length(autorizaTransferenciaFasciculosEntreAssinaturasMBean.fasciculosPendente) > 0}">
						
						<tr>	
							<td colspan="6">
							
								<table class="subFormulario" style="width: 100%">
									<caption> Fasc�culos pendentes de Autoriza��o para a Transfer�ncia </caption>
									<thead>
										<tr>
											<th style="text-align: left; font-weight: bold;">C�digo de Barras</th>
											<th style="text-align: right; font-weight: bold;">Ano Cron.</th>
											<th style="text-align: right; font-weight: bold;">Ano</th>
											<th style="text-align: right; font-weight: bold;">Volume</th>
											<th style="text-align: right; font-weight: bold;">N�mero</th>
											<th style="text-align: right; font-weight: bold; padding-right: 10px;">Edi��o</th>
											<%-- <th style="text-align: left; font-weight: bold; width: 20%; ">Assinatura Origem dos Fasc�culos</th> --%>
											<th style="text-align: left; font-weight: bold;  width: 15%;">Solicitada Por</th>
											<th style="text-align: left; font-weight: bold; width: 25%;" colspan="2">Autorizar?</th>
										</tr>
									</thead>
									
									<c:set var="idFiltroBibliotecaOrigem" value="-1" scope="request"/>
									<c:set var="idFiltroAssinaturaOrigem" value="-1" scope="request"/>
									
									<c:forEach items="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.fasciculosPendente}" var="fasciculo" varStatus="loop">
										
										<c:if test="${ idFiltroBibliotecaOrigem != fasciculo.biblioteca.id}">
											<c:set var="idFiltroBibliotecaOrigem" value="${fasciculo.biblioteca.id}" scope="request" />
											<tr class="biblioteca">
												<td colspan="9">Biblioteca de Origem: ${fasciculo.biblioteca.descricao}</td>
											</tr>
										</c:if>
										
										<c:if test="${ idFiltroAssinaturaOrigem != fasciculo.assinatura.id}">
											<c:set var="idFiltroAssinaturaOrigem" value="${fasciculo.assinatura.id}" scope="request" />
											<tr class="biblioteca">
												<td colspan="9">Assinatura de Origem: ${fasciculo.assinatura.codigo} - ${fasciculo.assinatura.titulo} </td>
											</tr>
										</c:if>
										
										<tr  class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
											<td> ${fasciculo.codigoBarras} </td>
											<td style="text-align: right"> ${fasciculo.anoCronologico} </td>
											<td style="text-align: right"> ${fasciculo.ano} </td>
											<td style="text-align: right"> ${fasciculo.volume} </td>
											<td style="text-align: right"> ${fasciculo.numero} </td>
											<td style="text-align: right; padding-right: 10px;"> ${fasciculo.edicao} </td>
											<%--  <td> ${fasciculo.assinatura.codigo} - ${fasciculo.assinatura.titulo} </td> --%>
											
											<td> ${fasciculo.nomeUsuario}</td> 
											<td> 
												<input type="radio" name="${fasciculo.id}" value="SIM" onclick="desHabilitarCampoMotivo( ${fasciculo.id} );" ${ fasciculo.opcaoSelecao == autorizaTransferenciaFasciculosEntreAssinaturasMBean.SIM ? " checked=\"checked\" ": " "}  > SIM
												<input type="radio" name="${fasciculo.id}" value="NAO" onclick="habilitarCampoMotivo( ${fasciculo.id} );" ${ fasciculo.opcaoSelecao == autorizaTransferenciaFasciculosEntreAssinaturasMBean.NAO ? " checked=\"checked\" ": " "} > N�O
												<input type="radio" name="${fasciculo.id}" value="AGUARDAR" onclick="desHabilitarCampoMotivo( ${fasciculo.id} );" ${ fasciculo.opcaoSelecao == autorizaTransferenciaFasciculosEntreAssinaturasMBean.AGUARDAR ? " checked=\"checked\" ": " "} > AGUARDAR 
	    									</td>
										</tr>
										
										<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
											<th id="labelMotivo_${fasciculo.id}" ${ fasciculo.opcaoSelecao == autorizaTransferenciaFasciculosEntreAssinaturasMBean.AGUARDAR ? " style=\"display: none;\" ": "  "} class="obrigatorio">Motivo:</th>                        
											
											<td colspan="5" id="textoMotivo_${fasciculo.id}" ${fasciculo.opcaoSelecao == autorizaTransferenciaFasciculosEntreAssinaturasMBean.AGUARDAR ? " style=\"display: none;\" ": "  "} >            			
													<h:inputTextarea id="textAreaMotivNaoTransferencia"  value="#{fasciculo.informacao}" cols="60" rows="2" onkeyup="textCounter(this, 'quantidadeCaracteresDigitados_#{fasciculo.id}', 100);"
														rendered="#{fasciculo != null}" />
											</td>
											
											<td colspan="4" id="colunaCaracteresRestantes_${fasciculo.id}"  ${ fasciculo.opcaoSelecao == autorizaTransferenciaFasciculosEntreAssinaturasMBean.AGUARDAR ? " style=\"display: none;\" ": "  "}>
												<table style="width: 70%">
													<tbody style="background-color: transparent;">
														<tr>
															<td id="labelQtdCaracteres_${fasciculo.id}" ${ fasciculo.opcaoSelecao == autorizaTransferenciaFasciculosEntreAssinaturasMBean.AGUARDAR ? " style=\"display: none; \" ": " style=\"width:30%;\" "}>Caracteres Restantes:</td>
															<td id="qtdCaracteres_${fasciculo.id}" ${ fasciculo.opcaoSelecao == autorizaTransferenciaFasciculosEntreAssinaturasMBean.AGUARDAR ? " style=\"display: none;\" ": "  "}>
																<span id="quantidadeCaracteresDigitados_${fasciculo.id}">100</span>/100
															</td>
														</tr>
													</tbody>
												</table>
											</td>
										</tr>
										
									</c:forEach>
									
								</table>
							</td>
						</tr>
						
						<tr>
							<td colspan="6" style="text-align: center;">
								<span style="font-weight: bold;">Alterar os C�digo de Barras dos Fas�culos:</span>
								<h:selectOneMenu id="comboboxAlterarCodigoBarrasFasciculos" value="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.codigoDeBarrasAcompanhaCodigoNovaAssinatura}">
									<f:selectItem itemLabel="N�O" itemValue="false" />
									<f:selectItem itemLabel="SIM" itemValue="true" />
								</h:selectOneMenu>
								<ufrn:help> Informa se o c�digo de barras dos fasc�culos transferidos ser�o alterados para ficarem iguais aos c�digos de barras dos fasc�culos da assinatura para onde ele est�o sendo transferidos. </ufrn:help>
							</td>
							
						</tr>
						
						<tfoot>
							<tr>				
								<td colspan="6" style="text-align: center;">
									<h:commandButton value="Confirmar"  action="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.realizarTransferenciaFasciculos}" onclick="return confirm('Confirma a transfer�ncia dos fasc�culos ? ');"/>
									<h:commandButton value="Cancelar"  action="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
								</td>
							</tr>
						</tfoot>
					
						
					</c:if>
					
					
					
				</table>
				
			</c:if>	
		
			
		
			
			
		
				
		</c:if>	
			
			
			
			
			
		<%-- Fasc�culos transferidos para a biblioteca escolhida, mas a biblioteca n�o tem assinatura para eles --%>
		<%-- Nesse caso o bibliotec�rio vai ter que primeiro criar a assinatura para depois autorizar   --%>        
		
		
		<c:if test="${autorizaTransferenciaFasciculosEntreAssinaturasMBean.dadosMovimentacaoSemAssinatura != null }">
			
				<div class="infoAltRem" style="margin-top: 10px">
	    			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
						Criar uma assinatura na biblioteca para poder transferir os fasc�culos
					
					<h:graphicImage value="/img/delete_old.gif" style="overflow: visible;" />: 
						Cancelar a transfer�ncia
						
				</div>
				
				
				<table class="listagem" width="100%">
					<caption> Transfer�ncias Solicitadas para as quais a biblioteca n�o possui assinatura </caption>
					
					<thead>
						<tr>
							<th style="text-align: left">C�digo de Barras</th>
							<th style="text-align: left">Ano Cronol�gico</th>
							<th style="text-align: left">Ano</th>
							<th style="text-align: left">Volume</th>
							<th style="text-align: left">N�mero</th>
							<th style="text-align: left">Edi��o</th>
							<th style="width: 1%"></th>
							<th style="width: 1%"></th>
						</tr>
					</thead>
					
					<c:set var="idFiltroTituloFasciculos" value="-1" scope="request"/>
					
					<c:forEach items="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.dadosMovimentacaoSemAssinatura}" var="dados" varStatus="loop">
						<c:if test="${ idFiltroTituloFasciculos != dados.idTitulo}">
							<c:set var="idFiltroTituloFasciculos" value="${ dados.idTitulo}" scope="request" />
							<tr class="biblioteca">
								<td colspan="6">T�tulo <i>(Cataloga��o)</i> Original do Fasc�culo: ${dados.descricaoTitulo}</td>
								
								<td colspan="1">
									<h:commandLink action="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.chamarCasoDeUsoCriacaoAssinaturaParaTransferencia}">
										<h:graphicImage url="/img/seta.gif" style="border:none" title="Criar uma assinatura na biblioteca para poder transferir os fasc�culos" />
										<f:param name="idRegistroMovimentacaoSelecionado" value="#{dados.idRegistroMovimentacao}"/>	
									</h:commandLink>
								</td>
							
								<td colspan="1">
									<h:commandLink action="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.cancelarTransferenciaFasiculosSemAssinatura}" onclick="return confirm('Confirma o cancelamento da transfer�ncia ? ');">
										<h:graphicImage url="/img/delete_old.gif" style="border:none" title="Cancelar a transfer�ncia" />
										<f:param name="idRegistroMovimentacaoSelecionado" value="#{dados.idRegistroMovimentacao}"/>	
									</h:commandLink>
								</td>
							</tr>
							<tr class="biblioteca">
								<td colspan="8">Usu�rio Solicitou a Transfer�ncia: ${dados.nomeUsuarioSolicitouTranferencia}</td>
							</tr>
						</c:if>
					
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td> ${dados.codigoBarras}  </td>
							<td> ${dados.anoCronologico} </td>
							<td> ${dados.ano} </td>
							<td> ${dados.volume}  </td>
							<td> ${dados.numero} </td>
							<td> ${dados.edicao} </td>
							<td> </td>
							<td> </td>
						</tr>  
					
					</c:forEach> 
					
				</table>
							
		</c:if>  

	
	
	
	
	</h:form>

	<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>

</f:view>
	
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>	