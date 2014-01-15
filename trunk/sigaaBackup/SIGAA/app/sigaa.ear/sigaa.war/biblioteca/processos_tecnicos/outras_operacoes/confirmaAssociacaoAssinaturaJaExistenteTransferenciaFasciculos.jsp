<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<h2>  <ufrn:subSistema /> &gt; Confirmar Associa��o </h2>

<div class="descricaoOperacao"> 
   <p>Confirme que a assinatura escolhida no passo anterior ser� a assinatura para onde os fasc�culos ser�o transferidos </p>
</div>

<f:view>

	<a4j:keepAlive beanName="autorizaTransferenciaFasciculosEntreAssinaturasMBean" />
	<a4j:keepAlive beanName="assinaturaPeriodicoMBean" />
	
	<h:form id="confirmaAssociacaoAssinaturaJaExistenteTransferenciaFasciculos">
	
		<table class="visualizacao" style="width: 80%;">
			<caption>Confirma��o da Associa��o</caption>
			<tr>
				<th>C�digo:</th>
				<td>${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.codigo}</td>
				<th>T�tulo:</th>
				<td  width="30%">${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.titulo}</td>
			</tr>
			<tr>	
				<th>Data de In�cio da Assinatura:</th>
				<td style="text-align: center"> <ufrn:format type="data" valor="${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.dataInicioAssinatura}">  </ufrn:format> </td>
				<th>Data T�rmino da Assinatura:</th>
				<td style="text-align: center"> <ufrn:format type="data" valor="${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.dataTerminoAssinatura}">  </ufrn:format> </td>
			</tr>
			<tr>	
				<th>Modalidade de Aquisi��o:</th>
				<td style="width:10%">
					<c:if test="${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.assinaturaDeCompra}">
						COMPRA
					</c:if>
					<c:if test="${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.assinaturaDeDoacao}">
						DOA��O
					</c:if>
					<c:if test="${! autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.assinaturaDeDoacao && ! autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.assinaturaDeCompra}">
						INDEFINIDO
					</c:if>
				</td>
				<th>Internacionaliza��o: </th>
				<td>
					<c:if test="${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.internacional}">
						Internacional
					</c:if>
					<c:if test="${ ! autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.internacional}">
						Nacional
					</c:if>
				</td>
			</tr>
			
			<tr>
				<th>Periodicidade:</th>
				<td style="width:10%">${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.frequenciaPeriodicos.descricao}</td>
				<th>Biblioteca:</th>
				<td  width="30%">${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.unidadeDestino.descricao}</td>
			</tr>
			
			
			<tr>
				<td colspan="4">
					<table class="subFormulario" style="width: 100%;">
						<caption>Dados da Transfer�ncia</caption>
						<tr>
							<th>T�tulo Original do Fasc�culo:</th>
							<td> ${autorizaTransferenciaFasciculosEntreAssinaturasMBean.dadoTransferenciaSelecinado.descricaoTitulo}</td>
						</tr>
						
						<tr>
							<th>Usu�rio Solicitou a Transfer�ncia:</th>
							<td>${autorizaTransferenciaFasciculosEntreAssinaturasMBean.dadoTransferenciaSelecinado.nomeUsuarioSolicitouTranferencia}</td>
						</tr>
					</table>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton id="cmdLinkConfirmar" value="Confirmar" action="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.confirmaAssociacaoAssinaturaJaExistenteTransferencia}" />
		
						<h:commandButton id="cmdLinkVoltar" value="<< Voltar" action="#{assinaturaPeriodicoMBean.telaBuscaAssinaturas}" />
		
						<h:commandButton id="cmdLinkCancelar" value="Cancelar" action="#{autorizaTransferenciaFasciculosEntreAssinaturasMBean.telaAutorizaTransferenciaFasciculos}" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
	
	</h:form>

	<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>

</f:view>
	
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>	