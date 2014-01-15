<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Transfer�ncia de Solicita��o de Orienta��o de Normaliza��o.</h2>
	
	<div class="descricaoOperacao"> 
	   <p>Abaixo est�o listadas todas as Biblioteca que prestam o servido de Orienta��o de Normaliza��o. </p>
	   <p>Selecione a biblioteca para onde a solicita��o vai ser enviada.</p>
	</div>


	<a4j:keepAlive beanName="solicitacaoOrientacaoMBean"></a4j:keepAlive>

	<h:form id="formTransfereSolicitacao">
	
		<table class="formulario" width="80%">
			<caption>Transfer�ncia de Solicita��o de Orienta��o de Normaliza��o </caption>
			<tbody>
				<tr>
					<th>Biblioteca Atual da Solicita��o:</th>
					<td>
						${solicitacaoOrientacaoMBean.obj.biblioteca.descricao}
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Biblioteca Destino da Solicita��o:</th>
					<td>
						<h:selectOneMenu id="comboBoxBibliotecasCatalogacaoENormalizacao" value="#{solicitacaoOrientacaoMBean.bibliotecaDestino.id}">
							<f:selectItem itemLabel=" -- SELECIONE -- " itemValue="0" />
							<f:selectItems value="#{solicitacaoOrientacaoMBean.bibliotecasServicoCombo}" />
						</h:selectOneMenu>
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton value="Transferir Solicita��o" action="#{solicitacaoOrientacaoMBean.transferirSolicitacao}" disabled="#{empty(empty(solicitacaoOrientacaoMBean.bibliotecasServico))}" />
						<h:commandButton value="Cancelar" action="#{solicitacaoOrientacaoMBean.telaListaSolicitacoes}" onclick="#{confirm}" immediate="true"  />
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>