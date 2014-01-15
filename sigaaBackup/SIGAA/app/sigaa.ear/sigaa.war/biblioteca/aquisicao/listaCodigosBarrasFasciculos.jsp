<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Reorganizar os C�digos de Barras dos Fasc�culos</h2>

<div class="descricaoOperacao">
	<p> Caro(a) usu�rio(a), </p>
	<p> Essa opera��o permite reorganizar os c�digos de barras gerados para os fasc�culos.</p>
	<p> Os c�digos de barras dos fasc�culos s�o reorganizados de acordo com a data de cria��o do fasc�culo no sistema.</p>
	<p> Os c�digos de barras s�o gerados com a seguinte informa��o: <span style="font-variant: small-caps;">c�digo da assinatura</span> + <span style="font-variant: small-caps;">n�mero sequencial</span>. 
	Em alguns casos, quando um fasc�culo � removido ou transferido para outra assinatura, essa sequ�ncia pode ser quebrada. Essa opera��o pode ser utilizada para deixar novamente os c�digos de barras em sequ�ncia.</p>
	<br/>
	<p><strong>IMPORTANTE: Utilizando essa opera��o ser� necess�rio reimprimir as etiquetas dos fasc�culos! </strong></p>
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
							C�digo:
						</th>
						<td colspan="3" style="width: 50%">${gerenciarCodigosBarrasFasciculosMBean.assinaturaSelecionada.codigo}</td>
					</tr>			
					<tr>
						<th style="font-weight:bold; ">
							T�tulo:
						</th>
						<td colspan="3" style="width: 50%">${gerenciarCodigosBarrasFasciculosMBean.assinaturaSelecionada.titulo}</td>
					</tr>
					<tr>
						<th style="font-weight:bold; ">
							Modalidade de Aquisi��o:
						</th>
						<td colspan="3" style="width: 50%">
							<c:if test="${gerenciarCodigosBarrasFasciculosMBean.assinaturaSelecionada.assinaturaDeCompra}">
								COMPRA
							</c:if>
							<c:if test="${gerenciarCodigosBarrasFasciculosMBean.assinaturaSelecionada.assinaturaDeDoacao}">
								DOA��O
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
							N�mero do Fasc�culo Atual:
						</th>
						<td colspan="3" style="width: 50%">${gerenciarCodigosBarrasFasciculosMBean.assinaturaSelecionada.numeroGeradorFasciculo}</td>
					</tr>
					
					<tr style="color: red;">
						<th style="font-weight:bold; ">
							Novo N�mero do Fasc�culo Atual:
						</th>
						<td colspan="3" style="width: 50%">${gerenciarCodigosBarrasFasciculosMBean.numeroSequencialFasciculos}</td>
					</tr>
					
					<tr>
						<td colspan="4">
							<%-- os fasc�culos  que j� foram registrados   --%>
							<div style="max-height:300px; overflow-y:scroll; width: 100%; margin-left: auto; margin-right: auto; margin-top: 20px;">
								<table class="subFormulario" style="width: 100%">
									
									<caption>Fasc�culos (  ${gerenciarCodigosBarrasFasciculosMBean.quantidadeFasciculosAssinatura} )  </caption>
									
									<thead>
										<tr>
											<th style="text-align: left;">C�digos de Barras Atuais</th>
											<th style="text-align: left;">Novos C�digos Barras</th>
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
							<%-- os fasc�culos  que j� foram registrados   --%>
							<div style="max-height:300px; overflow-y:scroll; width: 100%; margin-left: auto; margin-right: auto; margin-top: 20px;">
								<table class="subFormulario" style="width: 100%">
									
									<caption>Fasc�culos com o mesmo c�digo existentes em outras assinaturas ( ${gerenciarCodigosBarrasFasciculosMBean.quantidadeFasciculosEmOutrasAssinaturas} )</caption>
									
									<thead>
										<tr>
											<th style="text-align: left;">C�digos de Barras</th>
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
						<h:commandButton id="cmdReorganizarCodigosBarras" value="Reorganizar C�digos de Barras" 
							action="#{gerenciarCodigosBarrasFasciculosMBean.reorganizaCodigoFasciculos}"
							rendered="#{gerenciarCodigosBarrasFasciculosMBean.podeReorganizarCodigos}" 
							onclick="return confirm('Deseja alterar os c�digo de barras dos fasc�culos? Essa opera��o n�o pode ser desfeita.');"/>
						
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