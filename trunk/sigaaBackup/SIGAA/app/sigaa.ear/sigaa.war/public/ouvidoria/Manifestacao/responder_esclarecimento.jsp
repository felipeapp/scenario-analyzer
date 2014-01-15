<%@ include file="../../include/cabecalho.jsp" %>
<a4j:keepAlive beanName="esclarecimentoOuvidoria" />

<f:view>
	<h:form id="form">
	<h2>
		Ouvidoria > Responder Esclarecimento
	</h2>

		<div class="descricaoOperacao">
			<b>Caro Usuáio,</b> 
			<br/><br/>
			Aqui você poderá responder um pedido de esclarecimento. Sua resposta será enviada para a ouvidoria e gravada no histórico da manifestação.
		</div>				

		<table class="formulario" width="100%">
			<caption>Responder Esclarecimento</caption>
			<tbody>
				<tr>
					<td>
						<table class="subFormulario" width="100%">
							<tbody>
								<tr>
									<th valign="top" class="required" style="font-weight: bold;" width="15%">Solicitação de Esclarecimento: </th>
									<td>
										<h:outputText value="#{esclarecimentoOuvidoria.ultimaSolicitacaoEsclarecimento }" style="width: 95%;"/>
									</td>
								</tr>
								<tr>
									<th valign="top" class="required" style="font-weight: bold;" width="15%">Resposta: </th>
									<td>
										<h:inputTextarea value="#{esclarecimentoOuvidoria.historico.resposta }" style="width: 95%;" rows="5" disabled="#{esclarecimentoOuvidoria.obj.respondida }" />
									</td>
								</tr>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
			<tfoot>
				<tr><td colspan="2">
					<h:commandButton value="Responder Solicitação" action="#{esclarecimentoOuvidoria.responderEsclarecimentoPublico }" />
				</td></tr>
			</tfoot>
		</table>
	<br/>	
	<table class="formulario" width="100%">
	<caption>Detalhes da Manifestação</caption>
	<tbody>
	<tr><td>
				
		<c:set var="manifestacao" value="#{esclarecimentoOuvidoria.obj }" scope="page" />
		<c:set var="historicos" value="#{esclarecimentoOuvidoria.historicos }" scope="page" />
		<c:set var="copias" value="#{esclarecimentoOuvidoria.copias }" scope="page" />
		<c:set var="delegacoes" value="#{esclarecimentoOuvidoria.delegacoes }" scope="page" />
		<%@ include file="/ouvidoria/Manifestacao/include/dados_detalhes_manifestacao.jsp" %>
		
		</td></tr>
		</tbody>
	</table>
	</h:form>
	<%@include file="/public/include/voltar.jsp"%>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>