<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<h2>  <ufrn:subSistema /> &gt; Confirmar Associação </h2>

<div class="descricaoOperacao"> 
   <p>Confirme que a assinatura escolhida no passo anterior será a assinatura para onde os fascículos serão transferidos </p>
</div>

<f:view>

	<a4j:keepAlive beanName="autorizaTransferenciaFasciculosEntreAssinaturasMBean" />
	<a4j:keepAlive beanName="assinaturaPeriodicoMBean" />
	
	<h:form id="confirmaAssociacaoAssinaturaJaExistenteTransferenciaFasciculos">
	
		<table class="visualizacao" style="width: 80%;">
			<caption>Confirmação da Associação</caption>
			<tr>
				<th>Código:</th>
				<td>${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.codigo}</td>
				<th>Título:</th>
				<td  width="30%">${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.titulo}</td>
			</tr>
			<tr>	
				<th>Data de Início da Assinatura:</th>
				<td style="text-align: center"> <ufrn:format type="data" valor="${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.dataInicioAssinatura}">  </ufrn:format> </td>
				<th>Data Término da Assinatura:</th>
				<td style="text-align: center"> <ufrn:format type="data" valor="${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.dataTerminoAssinatura}">  </ufrn:format> </td>
			</tr>
			<tr>	
				<th>Modalidade de Aquisição:</th>
				<td style="width:10%">
					<c:if test="${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.assinaturaDeCompra}">
						COMPRA
					</c:if>
					<c:if test="${autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.assinaturaDeDoacao}">
						DOAÇÃO
					</c:if>
					<c:if test="${! autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.assinaturaDeDoacao && ! autorizaTransferenciaFasciculosEntreAssinaturasMBean.assinaturaJaExistenteSelecionada.assinaturaDeCompra}">
						INDEFINIDO
					</c:if>
				</td>
				<th>Internacionalização: </th>
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
						<caption>Dados da Transferência</caption>
						<tr>
							<th>Título Original do Fascículo:</th>
							<td> ${autorizaTransferenciaFasciculosEntreAssinaturasMBean.dadoTransferenciaSelecinado.descricaoTitulo}</td>
						</tr>
						
						<tr>
							<th>Usuário Solicitou a Transferência:</th>
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