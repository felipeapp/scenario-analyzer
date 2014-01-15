<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<f:view>

	<a4j:keepAlive beanName="levantamentoInfraMBean" />
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Levantamento de Infra-Estrutura</h2>
	
	<h:messages showDetail="true" />
	
	<div class="descricaoOperacao">
		<p>Nesta página você pode preencher os resultados do levantamento de infra-estrutura. As seguintes
		ações são disponibilizadas:</p>
		<ul>
			<li><strong>Salvar:</strong> somente salva as informações do levantamento. Não envia nada para o usuário.</li>
			<li><strong>Finalizar e Enviar Resultados:</strong> Finaliza o atendimento da solicitação. Salva os dados informados, os disponibiliza
			para o usuário e envia um email de aviso para ele.</li>
			<li><strong>Invalidar Levantamento:</strong> indica que o levantamento não será feito.</li>
		</ul>
	</div>
	
	<c:set var="mbean" value="#{ levantamentoInfraMBean }" />
	<c:set var="obj" value="#{ levantamentoInfraMBean.obj }" />
	<c:set var="situacao" value="#{ mbean.obj.situacao }" />
	
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
				<th>Texto da solicitação:</th>
				<td>
					<h:outputText id="textoSolicitacao" value="#{ obj.textoSolicitacao }" />
				</td>
			</tr>
		</tbody>
	
	</table>
	
	<%-- Se o levantamento ainda não foi atendido ou cancelado: --%>
	<c:if test="${ obj.situacao == obj.solicitado }" >
	
		<h:form id="form" enctype="multipart/form-data" >
		
			<table class="formulario" style="width: 700px; margin-top: 10px;">
				
				<caption>Levantamento de Infra-Estrutura</caption>
				
				<tbody>
				
					<tr>
						<th class="obrigatorio">Resposta ao usuário:</th>
						<td><h:inputTextarea id="resposta" cols="70" rows="6" value="#{ levantamentoInfraMBean.obj.textoBibliotecario }" /></td>
					</tr>
					
					<tr>
						<th>Arquivos:</th>
						<td>
							<table>
								
								<c:if test="${ empty levantamentoInfraMBean.obj.arquivos and empty levantamentoInfraMBean.arquivosEmMemoria }" >
									<tr><td colspan="2">
										<em>Nenhum arquivo foi adicionado ainda. <br/>
										Utilize o botão abaixo para adicionar arquivos.</em>
									</td></tr>
								</c:if>
								
								<%-- Arquivos já salvos na base de arquivos. --%>
								<c:forEach var="arquivo" items="#{ levantamentoInfraMBean.obj.arquivos }" >
									<tr>
										<td><h:outputText value="#{arquivo.nome}" /></td>
										<td>
											<h:commandLink action="#{ levantamentoInfraMBean.removerArquivoDaBase }"
													onclick="return confirm('Tem certeza que quer remover o arquivo?');">
												<h:graphicImage value="/img/garbage.png" title="Remover arquivo" />
												<f:param name="id" value="#{ arquivo.id }" />
											</h:commandLink>
										</td>
									</tr>
								</c:forEach>
								
								<%-- Arquivos que ainda estão somente em memória. --%>
								<c:forEach var="arquivo" items="#{ levantamentoInfraMBean.arquivosEmMemoria }" >
									<tr>
										<td><h:outputText value="#{ arquivo }" /></td>
										<td>
											<h:commandLink action="#{ levantamentoInfraMBean.removerArquivoEmMemoria }"
													onclick="return confirm('Tem certeza que quer remover o arquivo?');">
												<h:graphicImage value="/img/garbage.png" title="Remover arquivo" />
												<f:param name="arquivo" value="#{ arquivo }" />
											</h:commandLink>
										</td>
									</tr>
								</c:forEach>
								
								<tr><td colspan="2">
									<%-- Input para o usuário adicionar arquivos, um por vez. --%>
									<t:inputFileUpload id="arquivo"  size="20" valueChangeListener="#{levantamentoInfraMBean.adicionarArquivo}" onchange="submit();" />
								</td></tr>
							</table>
							
						</td>
					</tr>
					
				</tbody>
				
				<tfoot><tr><td colspan="2">
				
					<h:commandButton value="Salvar" id="salvar" action="#{ levantamentoInfraMBean.salvar }" />
					<h:commandButton value="Finalizar e Enviar Resultados" id="salvarEEnviarResultados" action="#{ levantamentoInfraMBean.atender }" onclick="if (!confirm('Confirma a finalização da solicitação ?')) return false" />
					<h:commandButton value="Invalidar levantamento" id="invalidar" action="#{ levantamentoInfraMBean.iniciarCancelarSolicitacaoParaBibliotecario }" />
					<h:commandButton value="<< Voltar" id="voltar" action="#{ levantamentoInfraMBean.listarParaBibliotecario }" immediate="true"/>
					<h:commandButton value="Cancelar" id="cancelar" action="#{ levantamentoInfraMBean.cancelar }" onclick="#{confirm}" immediate="true" />
					
				</td></tr></tfoot>
				
			</table>
		
		</h:form>
	
	</c:if>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>