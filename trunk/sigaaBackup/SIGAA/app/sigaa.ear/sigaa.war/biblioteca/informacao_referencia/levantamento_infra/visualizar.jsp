<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<f:view>

	<a4j:keepAlive beanName="levantamentoInfraMBean" />
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Levantamento de Infra-Estrutura</h2>
	
	<h:messages showDetail="true" />
	
	<c:set var="obj" value="#{ levantamentoInfraMBean.obj }" />

	<table class="visualizacao" style="width: 700px;" >
		
		<caption>Dados do Levantamento</caption>
		
		<tbody>
		
			<tr>
				<th>Número:</th>
				<td><h:outputText id="numero" value="#{ obj.numeroLevantamentoInfra }" /></td>
			</tr>
		
			<tr>
				<th>Situação:</th>
				<td><h:outputText id="situacao" value="#{ obj.descricaoSituacao }" /></td>
			</tr>
			
			<tr>
				<th>Data de Solicitação:</th>
				<td><h:outputText id="dataSolicitacao" value="#{ obj.dataSolicitacao }" /></td>
			</tr>
			
			<tr>
				<th>Biblioteca:</th>
				<td><h:outputText id="biblioteca" value="#{ obj.biblioteca.descricao }" /></td>
			</tr>
			
			<tr>
				<th>Usuário:</th>
				<td><h:outputText id="usuario" value="#{ obj.solicitante.nome }" /></td>
			</tr>
			
			<tr>
				<th style="white-space: nowrap;">Texto da solicitação:</th>
				<td>
					<h:outputText id="textoSolicitacao"
							value="#{ obj.textoSolicitacao }" />
				</td>
			</tr>
			
			<c:if test="${ obj.situacao == obj.concluido }" >
				<tr>
					<th>Resposta ao usuário:</th>
					<td>
						<h:outputText id="resposta" value="#{ obj.textoBibliotecario }" />
					</td>
				</tr>
				
				<tr>
					<th>Arquivos:</th>
					<td>
						<c:if test="${ empty obj.arquivos }">
							<em>Nenhum arquivo</em>
						</c:if>
						
						<c:if test="${ not empty obj.arquivos }">
							<h:form id="formularioBaixarArquivos">
								<table>
								<c:forEach var="arquivo" items="#{ levantamentoInfraMBean.obj.arquivos }">
									<tr><td>
									<h:commandLink action="#{ levantamentoInfraMBean.baixarArquivo }" >
										<h:outputText value="#{ arquivo.nome }" />
										<f:param name="id" value="#{ arquivo.idArquivo }" />
									</h:commandLink>
									</td></tr>
								</c:forEach>
								</table>
							</h:form>
						</c:if>
					</td>
				</tr>
				
				<tr>
					<th>Realizado por:</th>
					<td><h:outputText value="#{ obj.registroEntradaConclusao.usuario.nome }" /></td>
				</tr>
				<tr>
					<th>Data da realização:</th>
					<td><h:outputText value="#{ obj.dataConclusao }" /></td>
				</tr>

			</c:if>
			
			<c:if test="${ obj.situacao == obj.cancelado }" >
				<tr>
					<th>Motivo do cancelamento:</th>
					<td><h:outputText value="#{ obj.motivoCancelamento }" /></td>
				</tr>
				<tr>
					<th>Cancelado por:</th>
					<td><h:outputText value="#{ obj.registroEntradaCancelamento.usuario.nome }" /></td>
				</tr>
				<tr>
					<th>Data do cancelamento:</th>
					<td><h:outputText value="#{ obj.dataCancelamento }" /></td>
				</tr>
			</c:if>
			
			<c:if test="${ not empty obj.dataAtualizacao }">
				<tr>
					<th>Última atualização:</th>
					<td>
						<h:outputText value="#{ obj.dataAtualizacao }" /> por
						<h:outputText value="#{ obj.registroEntradaAtualizacao.usuario.nome }" />
					</td>
				</tr>
			</c:if>
			
		</tbody>
		
		<h:form id="formularioAcoes">
		<tfoot>
			<tr>
				<td colspan="2">
					<c:choose>
						<c:when test="${ levantamentoInfraMBean.bibliotecario }" >
							<c:if test="${ obj.situacao == obj.solicitado }" >
								<h:commandButton id="cancelarSolicitacaoInfra1" value="Cancelar solicitação" action="#{ levantamentoInfraMBean.iniciarCancelarSolicitacaoParaUsuario }" />
							</c:if>
							
							<h:commandButton id="voltar1" value="<< Voltar" action="#{ levantamentoInfraMBean.listarParaBibliotecario }" immediate="true"/>
						</c:when>
						<c:otherwise>
							<c:if test="${ obj.situacao == obj.solicitado && obj.usuarioPodeCancelar}" >
								<h:commandButton id="cancelarSolicitacaoInfra2" value="Cancelar solicitação" action="#{ levantamentoInfraMBean.cancelarSolicitacaoUsuario }" onclick="if (!confirm('Confirma o cancelamento da sua solicitação ?')) return false"/>
							</c:if>
						
							<h:commandButton id="voltar2" value="<< Voltar" action="#{ levantamentoInfraMBean.listarParaUsuario }" immediate="true"/>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>
		</tfoot>
		</h:form>
		
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>