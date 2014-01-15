<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<h2>  <ufrn:subSistema /> &gt; Edi��o de Exemplar </h2>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
%>

<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">


<c:set var="confirmBaixa" value="if (!confirm('Confirma a baixa do exemplar?')) return false" scope="request" />
<c:set var="confirmRemocao" value="if (!confirm('Confirma remo��o do exemplar do acervo ?')) return false" scope="request" />


<f:view>

	<h:form>
	
		<%-- se a edi��o vem da p�gina de detalhes--%>
		<a4j:keepAlive beanName="detalhesMateriaisDeUmTituloMBean"></a4j:keepAlive>	
	
	
		<a4j:keepAlive beanName="editaMaterialInformacionalMBean"></a4j:keepAlive>
	
		<%-- Quando vai editar, precisa guardar se est� na pesquisa de cataloga��o ou n�o, se o usu�rio usar o bot�o voltar --%>
		<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	
		<%-- Quando o usu�rio come�a a cataloga��o e decide alterar as informa��es dos materiais do t�tulo, se ele voltar as informa��es da cataloga��o devem permanecer --%>
		<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
		<%-- Quando edita um exemplar a partir da p�gina de pesquisa --%>
		<a4j:keepAlive beanName="pesquisarExemplarMBean"></a4j:keepAlive>
	
	
		<%-- Quando a p�gina de inclus�o de exemplares chama a p�gina de edi��o --%>
		<a4j:keepAlive beanName="materialInformacionalMBean"></a4j:keepAlive>
	
		<c:if test="${ editaMaterialInformacionalMBean.notaCirculacao != null }">
			<div class="infoAltRem" style="margin-top: 10px">
				<h:graphicImage value="/img/delete.gif"  />: Remover Nota de Circula��o 
				<%-- <h:graphicImage value="/img/email_go.png"  />: Enviar para valida��o  --%>
			</div>
		</c:if>
    
		<table class="formulario" width="100%" style="maring-top:20px">
					
			<tbody>
				
				<caption> Editar Exemplar </caption>
	
				<tr>
					<td colspan="2">
						<c:set var="_titulo" value="${editaMaterialInformacionalMBean.titulo}"/>
						<%@include file="/public/biblioteca/informacoes_padrao_titulo.jsp"%>
					</td>
				</tr>
				
				<tr>
					<td colspan="2">
						<table class="subformulario" width="100%" style="maring-top:20px">
									
							<tbody>
								
								<caption> Dados do Exemplar <c:if test="${editaMaterialInformacionalMBean.exemplarEdicao.anexo}"> <span style="font-style: italic;">(anexo)</span> </c:if> </caption>
								
								<tr>
									<th>C�digo de Barras: </th> 
									<td>
									
										<%-- Se n�o tem permiss�o apenas mostra o c�digo de barras  --%>
										<ufrn:checkNotRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO }  %>">
											<h:outputText value="#{editaMaterialInformacionalMBean.exemplarEdicao.codigoBarras}"/>
										</ufrn:checkNotRole>
										
										<%-- Se � administrados geral ou bibliotec�rio e o mateiral n�o � tombado, pode editar o c�digo de barras, o sitema verifica apenas se n�o existe outro --%>
										<ufrn:checkRole papeis="<%= new int[] {  SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO }  %>">
											<c:if test="${editaMaterialInformacionalMBean.exemplarEdicao.numeroPatrimonio == null && !editaMaterialInformacionalMBean.exemplarEdicao.anexo }">
												<h:inputText id="inputTextCodigoBarrasExemplar" value="#{editaMaterialInformacionalMBean.exemplarEdicao.codigoBarras}" onkeyup="CAPS(this)" />
											</c:if>
											
											<c:if test="${editaMaterialInformacionalMBean.exemplarEdicao.numeroPatrimonio != null || editaMaterialInformacionalMBean.exemplarEdicao.anexo}">
												<h:outputText value="#{editaMaterialInformacionalMBean.exemplarEdicao.codigoBarras}"/>
											</c:if>
											
										</ufrn:checkRole>
									</td>
								</tr>
								
								<tr>
									<th class="required">N�mero de Chamada:</th>
									<td>
										<h:inputText id="inputTextNumeroChamada" value="#{editaMaterialInformacionalMBean.exemplarEdicao.numeroChamada}"  size="50" maxlength="200"/>
									</td>
								</tr>
								
								<tr>
									<th>Segunda Localiza��o:</th>
									<td colspan="3">
										<h:inputText  id="inputTextSegundaLocalizacao" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.segundaLocalizacao }" size="50" maxlength="200" />
										<ufrn:help> <p>Alguns exemplares possuem uma segunda localiza��o, por exemplo MAPAS, para indicar a gaveta onde est�o localizados. Essa informa��o � atribu�da nesse campo.</ufrn:help>
									</td>
								</tr>
								
								<tr>
									<th class="required">Biblioteca:</th>
									<td colspan="3">
										<h:selectOneMenu id="comboboxBiblioteca" value="#{editaMaterialInformacionalMBean.exemplarEdicao.biblioteca.id}"
												valueChangeListener="#{editaMaterialInformacionalMBean.verificaAlteracaoBiblioteca}" onchange="submit();"
												disabled="#{true}">
											<f:selectItems value="#{editaMaterialInformacionalMBean.bibliotecasInternas}"/>
										</h:selectOneMenu>
										<ufrn:help> Para alterar a biblioteca do exemplar, utilize a op��o "Transferir Exemplares entre Bibliotecas" no menu principal do sistema</ufrn:help>
									</td>
								</tr>
								
								<tr>
									<th  class="required">Cole��o:</th>
									<td colspan="3">
										<h:selectOneMenu  id="comboBoxColecao" value="#{editaMaterialInformacionalMBean.exemplarEdicao.colecao.id}">
											<f:selectItems value="#{editaMaterialInformacionalMBean.colecoes}"/>
										</h:selectOneMenu>
									</td>
								</tr>
								
								<tr>
									
									<th class="required">Situa��o:</th>
									<td>
									
										<%-- 2 verifica��es para n�o permitir de forma alguma que o usu�rio mude a situa��o do material manualmente estando ele emprestado --%>
									
										<c:if test="${! editaMaterialInformacionalMBean.exemplarEdicao.situacao.situacaoEmprestado}">
											<h:selectOneMenu id="comboBoxSituacao" value="#{editaMaterialInformacionalMBean.exemplarEdicao.situacao.id}" rendered="#{! editaMaterialInformacionalMBean.exemplarEdicao.situacao.situacaoEmprestado}">
												<f:selectItems value="#{editaMaterialInformacionalMBean.situacoes}"/>
											</h:selectOneMenu>
										</c:if>
										
										<c:if test="${ editaMaterialInformacionalMBean.exemplarEdicao.situacao.situacaoEmprestado}">
											<h:selectOneMenu id="comboSituacaoEmpretado" disabled="true" rendered="#{editaMaterialInformacionalMBean.exemplarEdicao.situacao.situacaoEmprestado}" >
												<f:selectItem itemLabel=" Emprestado " itemValue="#{editaMaterialInformacionalMBean.idSituacaoEmprestado}"/>
											</h:selectOneMenu>
										</c:if>
										
									</td>
								</tr>
								
								<tr>
									<th class="required">Status:</th>
									<td>
										<h:selectOneMenu id="comboStatus" value="#{editaMaterialInformacionalMBean.exemplarEdicao.status.id}" >
											<f:selectItems value="#{editaMaterialInformacionalMBean.statusAtivos}"/>
										</h:selectOneMenu>
						
									</td>
								</tr>
								
								<tr>
									<th class="required">Tipo do Material:</th>
									<td colspan="3">
										<h:selectOneMenu id="comboTipoMaterial" value="#{editaMaterialInformacionalMBean.exemplarEdicao.tipoMaterial.id}" >
											<f:selectItems  value="#{editaMaterialInformacionalMBean.tiposMaterial}" />
										</h:selectOneMenu>
						
									</td>
						
								</tr>
								
								<tr>
									<th valign="top">Formas do Documento:</th>
									<td colspan="3">
										<h:selectManyListbox  id="comBoxFormaDocumento" value="#{editaMaterialInformacionalMBean.idsFormasDocumentoEscolhidos}" size="10"  style="width: 40%;">
							           		<f:selectItems value="#{formaDocumentoMBean.allCombo}"/>
							       		</h:selectManyListbox>
							       		<ufrn:help>Para selecionar mais de uma <strong>forma de documento</strong> mantenha pressionada a tecla "Ctrl", em seguida selecione os itens desejados. 
										Para retirar a sele��o, tamb�m � preciso pressionar a tecla "Ctrl". </ufrn:help>
									</td>
								</tr>
								
								<tr>
									<th>N�mero do Volume:</th>
									<td>
										<h:inputText id="inputTextNumeroVolume" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.numeroVolume}" 
												size="7" maxlength="6" onkeyup="return formatarInteiro(this);" />
									</td>
								</tr>
								
								<tr>
									<th>Tomo:</th>
									<td>
										<h:inputText id="inputTextTomo" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.tomo}" 
												size="12" maxlength="10" />
									</td>
								</tr>
								
								
								<tr>
									<th>Nota de Tese e Disserta��o:</th>
									<td colspan="3">
										<h:inputTextarea id="inputAreaNotaTeseDissertacao" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.notaTeseDissertacao }" 
										  cols="57" rows="2" />
									</td>
						
								</tr>
						
								<tr>
									<th>Nota de Conte�do:</th>
									<td colspan="3">
										<h:inputTextarea id="inputAreaNotaConteudo" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.notaConteudo }" 
										  cols="57" rows="2" />
									</td>
						
								</tr>
								
								<tr>
									<th>Nota Geral:</th>
									<td colspan="3">
										<h:inputTextarea id="inputAreaNotaGeral" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.notaGeral }" 
										  cols="57" rows="2"  />
									</td>
						
								</tr>
						
								<tr>
									<th>Nota ao Usu�rio:</th>
									<td colspan="3">
										<h:inputTextarea id="inputAreaNotaUsuario" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.notaUsuario }" 
											  cols="57" rows="2"  />
										<ufrn:help> Informa��es que v�o aparecer para o usu�rio nas consultas p�blicas do sistema</ufrn:help>
									</td>
						
								</tr>
						
								<c:if test="${ editaMaterialInformacionalMBean.notaCirculacao != null }">
									<tr style="height: 20px"></tr>
									<tr style="background-color: #CCCCCC;">
										<td colspan="3">
											<div style="height: 20px; font-weight: bold; text-align: justify; padding-left: 15%; padding-right: 15%;">
												<div style="float: left">
													Nota de Circula��o:
													<h:outputText id="outputNotaCirculacao" value="#{ editaMaterialInformacionalMBean.notaCirculacao.nota }" />
												</div>
												<div style="float: right">							
													<h:commandLink id="cmdLinkRemoverNotaCirculacao" action="#{editaMaterialInformacionalMBean.removerNotaCirculacaoExemplar}" onclick="#{confirmDelete}">
														<h:graphicImage url="/img/delete.gif" style="border:none;" title="Clique aqui para remover a nota de circula��o" />				
													</h:commandLink>
												</div>
											</div>
										</td>
									</tr>
									<tr style="height: 20px"></tr>
								</c:if>
								
								<%-- 
								<c:if test="${editaMaterialInformacionalMBean.dandoBaixaMaterial}">
									<tr>
										<th class="obrigatorio">Motivo da Baixa:</th>
										<td colspan="3">
											<h:inputTextarea id="inputAreaMotivoBaixa" value="#{ editaMaterialInformacionalMBean.exemplarEdicao.motivoBaixa }" cols="57" rows="2"  />
										</td>
							
									</tr>
								</c:if>
								--%>
								
								<tfoot>
									<tr>
										<td colspan="4" align="center">	
											
											<h:commandButton id="botaoAtualizarAnexo" value="Atualizar Anexo" action="#{editaMaterialInformacionalMBean.atualizarExemplar}"
													rendered="#{editaMaterialInformacionalMBean.exemplarEdicao.anexo}">
												<f:setPropertyActionListener target="#{editaMaterialInformacionalMBean.finalizarAtualizacao}" value="false" />
											</h:commandButton>
											
											
											<h:commandButton id="botaoAtualizarExemplar" value="Atualizar Exemplar" action="#{editaMaterialInformacionalMBean.atualizarExemplar}"
												rendered="#{! editaMaterialInformacionalMBean.exemplarEdicao.anexo}">
												<f:setPropertyActionListener target="#{editaMaterialInformacionalMBean.finalizarAtualizacao}" value="false" />
											</h:commandButton>
											
										
											<%-- Tamb�m salva o Exemplar, mas volta para a p�gina de onde foi chamado --%>
											<h:commandButton id="botaoFinalizarAtualizacao" value="Finalizar Atualiza��o" action="#{editaMaterialInformacionalMBean.atualizarExemplar}">
												<f:setPropertyActionListener target="#{editaMaterialInformacionalMBean.finalizarAtualizacao}" value="true" />
											</h:commandButton>
											
											
											<h:commandButton id="botaoIncluirNotaCirculacao" value="Incluir Nota de Circula��o" action="#{editaMaterialInformacionalMBean.incluirNotaCirculacaoExemplar}"
												rendered="#{! editaMaterialInformacionalMBean.retornaCatalogacao}"
												onclick="return confirm('Deseja iniciar a inclus�o de uma nota de circula��o ? Os dados n�o salvos nesta tela ser�o perdidos.') " />
											
											<%-- 
											<c:if test="${editaMaterialInformacionalMBean.dandoBaixaMaterial}">
												<h:commandButton id="botaoDarBaixaMaterial" value="Dar Baixa Exemplar" action="#{editaMaterialInformacionalMBean.darBaixaMaterial}" 
															rendered="#{editaMaterialInformacionalMBean.dandoBaixaMaterial}"	onclick="#{confirmBaixa}">
												</h:commandButton>
											</c:if>
										
											<c:if test="${editaMaterialInformacionalMBean.removendoMaterial}">
												<h:commandButton id="botaoRemoverMaterial" value="Remover Exemplar" action="#{editaMaterialInformacionalMBean.apagarMaterial}" 
														rendered="#{editaMaterialInformacionalMBean.removendoMaterial}" onclick="#{confirmRemocao}">
												</h:commandButton>
											</c:if> --%>
											
											<h:commandButton id="botaoVoltar" value="<< Voltar" action="#{editaMaterialInformacionalMBean.voltarPaginaExemplar}" rendered="true" />			
										</td>
									</tr>
								</tfoot>
								
								
						</table>
					</td>
				</tr>
			</table>

	</h:form>
	
</f:view>
		
</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>