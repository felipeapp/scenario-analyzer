<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Transferência de Solicitação de Orientação de Normalização.</h2>
	
	<div class="descricaoOperacao"> 
	   <p>Abaixo estão listadas todas as Biblioteca que prestam o servido de Orientação de Normalização. </p>
	   <p>Selecione a biblioteca para onde a solicitação vai ser enviada.</p>
	</div>


	<a4j:keepAlive beanName="solicitacaoOrientacaoMBean"></a4j:keepAlive>

	<h:form id="formTransfereSolicitacao">
	
		<table class="formulario" width="80%">
			<caption>Transferência de Solicitação de Orientação de Normalização </caption>
			<tbody>
				<tr>
					<th>Biblioteca Atual da Solicitação:</th>
					<td>
						${solicitacaoOrientacaoMBean.obj.biblioteca.descricao}
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Biblioteca Destino da Solicitação:</th>
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
						<h:commandButton value="Transferir Solicitação" action="#{solicitacaoOrientacaoMBean.transferirSolicitacao}" disabled="#{empty(empty(solicitacaoOrientacaoMBean.bibliotecasServico))}" />
						<h:commandButton value="Cancelar" action="#{solicitacaoOrientacaoMBean.telaListaSolicitacoes}" onclick="#{confirm}" immediate="true"  />
					</td>
				</tr>
			</tfoot>
			
		</table>
	</h:form>
	
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>