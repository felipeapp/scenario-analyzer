<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<f:view>

	<a4j:keepAlive beanName="levantamentoInfraMBean" />
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Levantamento de Infra-Estrutura</h2>
	
	<h:messages showDetail="true" />
	
	<div class="descricaoOperacao">
		<p>Caro usuário,</p> 
		<p>Nesta página estão listadas as suas solicitações de Levantamento de Infra-Estrutura.</p>
		<p>Para solicitar um novo levantamento, clique em <em>Solicitar um Levantamento de
		Infra-Estrutura</em>.</p>
	</div>
	
	<h:form id="form">
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/adicionar.gif" />
		<h:commandLink value="Solicitar um Levantamento de Infra-Estrutura" action="#{levantamentoInfraMBean.iniciarSolicitacao}" 
					id="solicitarLevantamentoInfra"/>
		<h:graphicImage value="/img/view.gif"/> : Visualizar Levantamento
	</div>
	
		
		<table class="listagem">
		
			<caption>Suas Solicitações de Levantamento de Infra-estrutura</caption>
		
			<thead>
				<tr>
					<th style="text-align: right;">Número</th>
					<th style="text-align: center;">Data da Solicitação</th>
					<th style="text-align: left;">Biblioteca</th>
					<th style="text-align: center;">Situação</th>
					<th style="text-align: left;">Ultima alteração</th>
					<th style="text-align: center;">Visualizar</th>
				</tr>
			</thead>
		
			<tbody>
			
				<c:choose>
					
					<c:when test="${ empty levantamentoInfraMBean.lista }"><tr><td colspan="4" style="text-align: center;">
					
						<em>Você ainda não fez nenhuma solicitação de levantamento de infra-estrutura.</em>
					
					</td></tr></c:when>
					
					<c:otherwise>
						<c:forEach var="lv" items="#{ levantamentoInfraMBean.lista }">
							<tr>
								<td style="text-align: right;"><h:outputText value="#{ lv.numeroLevantamentoInfra }" /> </td>
								<td style="text-align: center;"><h:outputText value="#{ lv.dataSolicitacao }" /> </td>
								<td style="text-align: left;"><h:outputText value="#{ lv.biblioteca.descricao }" /> </td>
								<td style="text-align: center;"><h:outputText value="#{ lv.descricaoSituacao }" /> </td>
								<td style="text-align: left;">
									<c:choose>
										<c:when test="${ not empty lv.dataAtualizacao }">
											<h:outputText value="#{ lv.dataAtualizacao }" />
											por
											<h:outputText value="#{ lv.registroEntradaAtualizacao.usuario.nome }" />
										</c:when>
										<c:otherwise>
											-
										</c:otherwise>
									</c:choose>
								</td>
								<td style="text-align: center;">
									<h:commandLink action="#{ levantamentoInfraMBean.visualizarParaUsuario }" >
										<f:param name="idLevantamentoInfra" value="#{ lv.id }" />
										<h:graphicImage value="/img/view.gif" alt="Visualizar" title="Visualizar Levantamento" />
									</h:commandLink>
								</td>
							</tr>
						</c:forEach>
					</c:otherwise>
				</c:choose>
			
			</tbody>
			
			<tfoot>
				
				<tr>
					<td colspan="6" style="text-align: center;">
						<h:commandButton id="cmdVoltarListaSolicitacoesUsuario" value="<< Voltar" action="#{levantamentoInfraMBean.cancelar}"></h:commandButton>
					</td>
				</tr>
				
			</tfoot>
		
		</table>
	
	</h:form>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>