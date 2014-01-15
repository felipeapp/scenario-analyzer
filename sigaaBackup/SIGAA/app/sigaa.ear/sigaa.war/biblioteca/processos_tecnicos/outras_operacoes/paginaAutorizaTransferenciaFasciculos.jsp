<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<h2>  <ufrn:subSistema /> &gt; Autorizar Transferência de Fascículos </h2>

<div class="descricaoOperacao"> 
   <p>Página para autorizar transferência de fascículos entre bibliotecas. </p>
   <p>Existem três opções que podem ser escolhidas:</p>
   <ul>
   		<li><strong>SIM</strong> - A transferência será autorizada e o fascículo mudará de biblioteca.</li>
   		<li><strong>NÃO</strong> - A transferência será cancelada e o fascículo voltará a biblioteca origem.</li>
   		<li><strong>AGUARDAR</strong> - O fascículo permanece na listagem até que a sua transferência seja autorizada ou cancelada.</li>
   </ul>
   
   <br/>
   <p>
	   Por padrão, na transferencia os fascículos permanencem com o mesmo código de barras. Assim fascículos da assinatura de código "1234"
	   transferidos para a assintura de código "5678", permanecem com o código de barras 1234-1, 1234-2, 1234-3, apesar de pertencerem agora a assinatura
	   "5678". Contudo pode-se solicitar para que os código de barras dos fascículos sejam alterados para ficarem com os código de barras iguais aos código 
	   de barras dos fascículos da assinatura destino, 5678-4, 5678-5 e 5678-6.
   </p>
   <br/>
   
   <p>Observação 1: Para cada fascículo cuja transferência não for autorizada, deve-se informar o motivo, esse motivo será enviado à caixa postal 
   do usuário que a solicitou.</p>
   <p>Observação 2: Caso a solicitação tenha sido feita para uma biblioteca que não possuía uma assinatura para os fascículos, será necessário primeiro criar essa assinatura.</p>
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
		
			<caption>Selecione a Biblioteca Destino dos Fascículos </caption>
			
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
			
		
		<%--   Não existe nada pendente de autorização    --%>	
		<c:if test="${ autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturasComFasciculosPendentes == null
			&& autorizaTransferenciaFasciculosEntreAssinaturasMBean.dadosMovimentacaoSemAssinatura == null 
			&& autorizaTransferenciaFasciculosEntreAssinaturasMBean.obj.unidadeDestino.id != -1}">
				
			<table class="listagem" width="100%">
				<caption> Assinaturas Destino dos Fascículos </caption>
				
				<tr>
					<td style="color: red; text-align: center; "> Nenhum fascículo pendente de autorização para a biblioteca escolhida</td>
				</tr>
				
			</table>
		
		</c:if>	
			
			
		
		
			
		<%--  Existem fascículos pendentes de autorização com assinaturas     --%>	
			
		<c:if test="${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturasComFasciculosPendentes != null }">

			
				
			<c:if test="${ fn:length(autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturasComFasciculosPendentes) > 0 }">
				
				<div class="infoAltRem" style="margin-top: 10px">
					<h:graphicImage value="/img/biblioteca/baixo.gif" style="overflow: visible;" />: 
						Exibir fascículos pendentes de autorização da assinatura
	    			<h:graphicImage value="/img/biblioteca/cima.gif" style="overflow: visible;" />: 
						Ocultar fascículos pendentes de autorização da assinatura
				</div>
				
				
				<table class="listagem" width="100%">
					<caption> Assinaturas Destino dos Fascículos </caption>
					
					<thead>
						<tr>
							<th style="text-align: left">Código</th>
							<th style="text-align: left">Título</th>
							<th style="text-align: left">Unidade Destino</th>
							<th style="text-align: center">Internacional?</th>
							<th style="text-align: center">Modalidade de Aquisição</th>
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
									NÃO
								</c:if>
							</td>
							<td style="text-align: center">
								<c:if test="${assinatura.assinaturaDeCompra}">
									COMPRA
								</c:if>
								<c:if test="${assinatura.assinaturaDeDoacao}">
									DOAÇÃO
								</c:if>
								<c:if test="${! assinatura.assinaturaDeCompra &&  ! assinatura.assinaturaDeDoacao  }">
									INDEFINIDO
								</c:if>
							</td>
							<td>
								<h:commandLink id="cmdLinkVisualizaFasciculosSolicitados" action="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.visualizarFasciculosPendentesAutorizacao}">
									<h:graphicImage id="imagem_${assinatura.id}" url="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaSelecionada.id == assinatura.id ? '/img/biblioteca/cima.gif' : '/img/biblioteca/baixo.gif'}" 
									style="border:none" title="Visualizar fascículos pendentes de autorização da assinatura " />
									<f:param name="idAssinaturaSelecionada" value="#{assinatura.id}"/>	
								</h:commandLink>
							</td>
						</tr>
						
					</c:forEach>
					
					<tr>	
						<td colspan="6" style="height: 20px;">
						</td>
					</tr>
					
					
					
					
					
					<%-- Fascículos pendentes de autorização --%>	
					<c:if test="${ fn:length(autorizaTransferenciaFasciculosEntreAssinaturasMBean.fasciculosPendente) > 0}">
						
						<tr>	
							<td colspan="6">
							
								<table class="subFormulario" style="width: 100%">
									<caption> Fascículos pendentes de Autorização para a Transferência </caption>
									<thead>
										<tr>
											<th style="text-align: left; font-weight: bold;">Código de Barras</th>
											<th style="text-align: right; font-weight: bold;">Ano Cron.</th>
											<th style="text-align: right; font-weight: bold;">Ano</th>
											<th style="text-align: right; font-weight: bold;">Volume</th>
											<th style="text-align: right; font-weight: bold;">Número</th>
											<th style="text-align: right; font-weight: bold; padding-right: 10px;">Edição</th>
											<%-- <th style="text-align: left; font-weight: bold; width: 20%; ">Assinatura Origem dos Fascículos</th> --%>
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
												<input type="radio" name="${fasciculo.id}" value="NAO" onclick="habilitarCampoMotivo( ${fasciculo.id} );" ${ fasciculo.opcaoSelecao == autorizaTransferenciaFasciculosEntreAssinaturasMBean.NAO ? " checked=\"checked\" ": " "} > NÃO
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
								<span style="font-weight: bold;">Alterar os Código de Barras dos Fasículos:</span>
								<h:selectOneMenu id="comboboxAlterarCodigoBarrasFasciculos" value="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.codigoDeBarrasAcompanhaCodigoNovaAssinatura}">
									<f:selectItem itemLabel="NÃO" itemValue="false" />
									<f:selectItem itemLabel="SIM" itemValue="true" />
								</h:selectOneMenu>
								<ufrn:help> Informa se o código de barras dos fascículos transferidos serão alterados para ficarem iguais aos códigos de barras dos fascículos da assinatura para onde ele estão sendo transferidos. </ufrn:help>
							</td>
							
						</tr>
						
						<tfoot>
							<tr>				
								<td colspan="6" style="text-align: center;">
									<h:commandButton value="Confirmar"  action="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.realizarTransferenciaFasciculos}" onclick="return confirm('Confirma a transferência dos fascículos ? ');"/>
									<h:commandButton value="Cancelar"  action="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
								</td>
							</tr>
						</tfoot>
					
						
					</c:if>
					
					
					
				</table>
				
			</c:if>	
		
			
		
			
			
		
				
		</c:if>	
			
			
			
			
			
		<%-- Fascículos transferidos para a biblioteca escolhida, mas a biblioteca não tem assinatura para eles --%>
		<%-- Nesse caso o bibliotecário vai ter que primeiro criar a assinatura para depois autorizar   --%>        
		
		
		<c:if test="${autorizaTransferenciaFasciculosEntreAssinaturasMBean.dadosMovimentacaoSemAssinatura != null }">
			
				<div class="infoAltRem" style="margin-top: 10px">
	    			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
						Criar uma assinatura na biblioteca para poder transferir os fascículos
					
					<h:graphicImage value="/img/delete_old.gif" style="overflow: visible;" />: 
						Cancelar a transferência
						
				</div>
				
				
				<table class="listagem" width="100%">
					<caption> Transferências Solicitadas para as quais a biblioteca não possui assinatura </caption>
					
					<thead>
						<tr>
							<th style="text-align: left">Código de Barras</th>
							<th style="text-align: left">Ano Cronológico</th>
							<th style="text-align: left">Ano</th>
							<th style="text-align: left">Volume</th>
							<th style="text-align: left">Número</th>
							<th style="text-align: left">Edição</th>
							<th style="width: 1%"></th>
							<th style="width: 1%"></th>
						</tr>
					</thead>
					
					<c:set var="idFiltroTituloFasciculos" value="-1" scope="request"/>
					
					<c:forEach items="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.dadosMovimentacaoSemAssinatura}" var="dados" varStatus="loop">
						<c:if test="${ idFiltroTituloFasciculos != dados.idTitulo}">
							<c:set var="idFiltroTituloFasciculos" value="${ dados.idTitulo}" scope="request" />
							<tr class="biblioteca">
								<td colspan="6">Título <i>(Catalogação)</i> Original do Fascículo: ${dados.descricaoTitulo}</td>
								
								<td colspan="1">
									<h:commandLink action="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.chamarCasoDeUsoCriacaoAssinaturaParaTransferencia}">
										<h:graphicImage url="/img/seta.gif" style="border:none" title="Criar uma assinatura na biblioteca para poder transferir os fascículos" />
										<f:param name="idRegistroMovimentacaoSelecionado" value="#{dados.idRegistroMovimentacao}"/>	
									</h:commandLink>
								</td>
							
								<td colspan="1">
									<h:commandLink action="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.cancelarTransferenciaFasiculosSemAssinatura}" onclick="return confirm('Confirma o cancelamento da transferência ? ');">
										<h:graphicImage url="/img/delete_old.gif" style="border:none" title="Cancelar a transferência" />
										<f:param name="idRegistroMovimentacaoSelecionado" value="#{dados.idRegistroMovimentacao}"/>	
									</h:commandLink>
								</td>
							</tr>
							<tr class="biblioteca">
								<td colspan="8">Usuário Solicitou a Transferência: ${dados.nomeUsuarioSolicitouTranferencia}</td>
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