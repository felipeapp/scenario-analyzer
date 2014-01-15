<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<f:view>

	<a4j:keepAlive beanName="levantamentoInfraMBean" />
	
	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; Levantamento de Infra-Estrutura</h2>
	
	<h:messages showDetail="true" />
	
	<div class="descricaoOperacao">
		<p>Nesta p�gina voc� pode preencher os resultados do levantamento de infra-estrutura. As seguintes
		a��es s�o disponibilizadas:</p>
		<ul>
			<li><strong>Salvar:</strong> somente salva as informa��es do levantamento. N�o envia nada para o usu�rio.</li>
			<li><strong>Finalizar e Enviar Resultados:</strong> Finaliza o atendimento da solicita��o. Salva os dados informados, os disponibiliza
			para o usu�rio e envia um email de aviso para ele.</li>
			<li><strong>Invalidar Levantamento:</strong> indica que o levantamento n�o ser� feito.</li>
		</ul>
	</div>
	
	<c:set var="mbean" value="#{ levantamentoInfraMBean }" />
	<c:set var="obj" value="#{ levantamentoInfraMBean.obj }" />
	<c:set var="situacao" value="#{ mbean.obj.situacao }" />
	
	<table class="visualizacao" style="width: 700px;" >
		
		<caption>Dados do Levantamento</caption>
		
		<tbody>
		
			<tr>
				<th>N�mero:</th>
				<td><h:outputText id="numero" value="#{ obj.numeroLevantamentoInfra }" /></td>
			</tr>
		
			<tr>
				<th>Situa��o:</th>
				<td><h:outputText id="situacao" value="#{ obj.descricaoSituacao }" /></td>
			</tr>
			
			<tr>
				<th>Data de Solicita��o:</th>
				<td><h:outputText id="dataSolicitacao" value="#{ obj.dataSolicitacao }" /></td>
			</tr>
			
			<tr>
				<th>Biblioteca:</th>
				<td><h:outputText id="biblioteca" value="#{ obj.biblioteca.descricao }" /></td>
			</tr>
			
			<tr>
				<th>Usu�rio:</th>
				<td><h:outputText id="usuario" value="#{ obj.solicitante.nome }" /></td>
			</tr>
			
			<tr>
				<th>Texto da solicita��o:</th>
				<td>
					<h:outputText id="textoSolicitacao" value="#{ obj.textoSolicitacao }" />
				</td>
			</tr>
		</tbody>
	
	</table>
	
	<%-- Se o levantamento ainda n�o foi atendido ou cancelado: --%>
	<c:if test="${ obj.situacao == obj.solicitado }" >
	
		<h:form id="form" enctype="multipart/form-data" >
		
			<table class="formulario" style="width: 700px; margin-top: 10px;">
				
				<caption>Levantamento de Infra-Estrutura</caption>
				
				<tbody>
				
					<tr>
						<th class="obrigatorio">Resposta ao usu�rio:</th>
						<td><h:inputTextarea id="resposta" cols="70" rows="6" value="#{ levantamentoInfraMBean.obj.textoBibliotecario }" /></td>
					</tr>
					
					<tr>
						<th>Arquivos:</th>
						<td>
							<table>
								
								<c:if test="${ empty levantamentoInfraMBean.obj.arquivos and empty levantamentoInfraMBean.arquivosEmMemoria }" >
									<tr><td colspan="2">
										<em>Nenhum arquivo foi adicionado ainda. <br/>
										Utilize o bot�o abaixo para adicionar arquivos.</em>
									</td></tr>
								</c:if>
								
								<%-- Arquivos j� salvos na base de arquivos. --%>
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
								
								<%-- Arquivos que ainda est�o somente em mem�ria. --%>
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
									<%-- Input para o usu�rio adicionar arquivos, um por vez. --%>
									<t:inputFileUpload id="arquivo"  size="20" valueChangeListener="#{levantamentoInfraMBean.adicionarArquivo}" onchange="submit();" />
								</td></tr>
							</table>
							
						</td>
					</tr>
					
				</tbody>
				
				<tfoot><tr><td colspan="2">
				
					<h:commandButton value="Salvar" id="salvar" action="#{ levantamentoInfraMBean.salvar }" />
					<h:commandButton value="Finalizar e Enviar Resultados" id="salvarEEnviarResultados" action="#{ levantamentoInfraMBean.atender }" onclick="if (!confirm('Confirma a finaliza��o da solicita��o ?')) return false" />
					<h:commandButton value="Invalidar levantamento" id="invalidar" action="#{ levantamentoInfraMBean.iniciarCancelarSolicitacaoParaBibliotecario }" />
					<h:commandButton value="<< Voltar" id="voltar" action="#{ levantamentoInfraMBean.listarParaBibliotecario }" immediate="true"/>
					<h:commandButton value="Cancelar" id="cancelar" action="#{ levantamentoInfraMBean.cancelar }" onclick="#{confirm}" immediate="true" />
					
				</td></tr></tfoot>
				
			</table>
		
		</h:form>
	
	</c:if>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>