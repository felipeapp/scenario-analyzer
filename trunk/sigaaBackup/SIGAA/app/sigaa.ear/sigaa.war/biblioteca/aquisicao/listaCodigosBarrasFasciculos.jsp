<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Reorganizar os Códigos de Barras dos Fascículos</h2>

<div class="descricaoOperacao">
	<p> Caro(a) usuário(a), </p>
	<p> Essa operação permite reorganizar os códigos de barras gerados para os fascículos.</p>
	<p> Os códigos de barras dos fascículos são reorganizados de acordo com a data de criação do fascículo no sistema.</p>
	<p> Os códigos de barras são gerados com a seguinte informação: <span style="font-variant: small-caps;">código da assinatura</span> + <span style="font-variant: small-caps;">número sequencial</span>. 
	Em alguns casos, quando um fascículo é removido ou transferido para outra assinatura, essa sequência pode ser quebrada. Essa operação pode ser utilizada para deixar novamente os códigos de barras em sequência.</p>
	<br/>
	<p><strong>IMPORTANTE: Utilizando essa operação será necessário reimprimir as etiquetas dos fascículos! </strong></p>
</div>


<f:view>


	<a4j:keepAlive beanName="gerenciarCodigosBarrasFasciculosMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="assinaturaPeriodicoMBean"></a4j:keepAlive>

	<h:form id="formReorganizaCodigosBarras">
	
		<table class="formulario" style="width: 70%; margin-bottom: 20px;">
			
			<caption>Dados da Assinatura </caption>
			
			<tbody>
				
				<c:if test="${gerenciarCodigosBarrasFasciculosMBean.assinaturaSelecionada != null}">
					
					<tr>
						<th style="font-weight:bold; ">
							Código:
						</th>
						<td colspan="3" style="width: 50%">${gerenciarCodigosBarrasFasciculosMBean.assinaturaSelecionada.codigo}</td>
					</tr>			
					<tr>
						<th style="font-weight:bold; ">
							Título:
						</th>
						<td colspan="3" style="width: 50%">${gerenciarCodigosBarrasFasciculosMBean.assinaturaSelecionada.titulo}</td>
					</tr>
					<tr>
						<th style="font-weight:bold; ">
							Modalidade de Aquisição:
						</th>
						<td colspan="3" style="width: 50%">
							<c:if test="${gerenciarCodigosBarrasFasciculosMBean.assinaturaSelecionada.assinaturaDeCompra}">
								COMPRA
							</c:if>
							<c:if test="${gerenciarCodigosBarrasFasciculosMBean.assinaturaSelecionada.assinaturaDeDoacao}">
								DOAÇÃO
							</c:if>
							<c:if test="${! gerenciarCodigosBarrasFasciculosMBean.assinaturaSelecionada.assinaturaDeCompra &&  ! gerenciarCodigosBarrasFasciculosMBean.assinaturaSelecionada.assinaturaDeDoacao  }">
								INDEFINIDO
							</c:if>
						</td>
					</tr>
					<tr>
						<th style="font-weight:bold; ">
							Unidade de Destino:
						</th>
						<td colspan="3" style="width: 50%">${gerenciarCodigosBarrasFasciculosMBean.assinaturaSelecionada.unidadeDestino.descricao}</td>
					</tr>
					
					<tr style="color: blue;">
						<th style="font-weight:bold; ">
							Número do Fascículo Atual:
						</th>
						<td colspan="3" style="width: 50%">${gerenciarCodigosBarrasFasciculosMBean.assinaturaSelecionada.numeroGeradorFasciculo}</td>
					</tr>
					
					<tr style="color: red;">
						<th style="font-weight:bold; ">
							Novo Número do Fascículo Atual:
						</th>
						<td colspan="3" style="width: 50%">${gerenciarCodigosBarrasFasciculosMBean.numeroSequencialFasciculos}</td>
					</tr>
					
					<tr>
						<td colspan="4">
							<%-- os fascículos  que já foram registrados   --%>
							<div style="max-height:300px; overflow-y:scroll; width: 100%; margin-left: auto; margin-right: auto; margin-top: 20px;">
								<table class="subFormulario" style="width: 100%">
									
									<caption>Fascículos (  ${gerenciarCodigosBarrasFasciculosMBean.quantidadeFasciculosAssinatura} )  </caption>
									
									<thead>
										<tr>
											<th style="text-align: left;">Códigos de Barras Atuais</th>
											<th style="text-align: left;">Novos Códigos Barras</th>
										</tr>
									</thead>
										
									<c:forEach var="fasciculo" items="#{gerenciarCodigosBarrasFasciculosMBean.fasciculosAssinatura}" varStatus="status">
										
										<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
											
											<td>${fasciculo.codigoBarras}</td>
											<td>${fasciculo.informacao} - <span style="color: red;">${fasciculo.informacao2} </span>  </td>
										</tr>
										
									</c:forEach>
									
								</table> 
							</div>	
						
						</td>	
					</tr>
					
					<tr>
						<td colspan="4">
							<%-- os fascículos  que já foram registrados   --%>
							<div style="max-height:300px; overflow-y:scroll; width: 100%; margin-left: auto; margin-right: auto; margin-top: 20px;">
								<table class="subFormulario" style="width: 100%">
									
									<caption>Fascículos com o mesmo código existentes em outras assinaturas ( ${gerenciarCodigosBarrasFasciculosMBean.quantidadeFasciculosEmOutrasAssinaturas} )</caption>
									
									<thead>
										<tr>
											<th style="text-align: left;">Códigos de Barras</th>
											<th style="text-align: left;">Assinatura</th>
										</tr>
									</thead>
										
									<c:forEach var="fasciculoOutraAssinatura" items="#{gerenciarCodigosBarrasFasciculosMBean.fasciculosEmOutrasAssinaturas}" varStatus="status">
										
										<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
											
											<td>${fasciculoOutraAssinatura.codigoBarras}</td>
											<td>${fasciculoOutraAssinatura.assinatura.codigoTitulo} </td>
										</tr>
										
									</c:forEach>
									
								</table> 	
							</div>	
						</td>
					</tr>
					
				</c:if>
				
			</tbody>
			
			<tfoot>
				<tr style="text-align: center">
					<td colspan="4">
						<h:commandButton id="cmdReorganizarCodigosBarras" value="Reorganizar Códigos de Barras" 
							action="#{gerenciarCodigosBarrasFasciculosMBean.reorganizaCodigoFasciculos}"
							rendered="#{gerenciarCodigosBarrasFasciculosMBean.podeReorganizarCodigos}" 
							onclick="return confirm('Deseja alterar os código de barras dos fascículos? Essa operação não pode ser desfeita.');"/>
						
						<h:commandButton id="fakeCmdReorganizarCodigosBarras" value="Fake" style="display:none;" 
							actionListener="#{gerenciarCodigosBarrasFasciculosMBean.visualizarRelatorioGerado}" />
						
						<h:commandButton id="cmdCancelar" value="Cancelar" action="#{assinaturaPeriodicoMBean.telaBuscaAssinaturas}" immediate="true"/>
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		
	</h:form>
	
</f:view>

<script type="text/javascript">

function mostrarRelatorio(){
	document.getElementById('formReorganizaCodigosBarras:fakeCmdReorganizarCodigosBarras').click();
}

<c:if test="${gerenciarCodigosBarrasFasciculosMBean.outputSteam != null}">
	mostrarRelatorio();
</c:if>


</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>