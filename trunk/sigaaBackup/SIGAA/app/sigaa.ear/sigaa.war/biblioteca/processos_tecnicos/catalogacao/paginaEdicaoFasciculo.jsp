<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<h2>  <ufrn:subSistema /> &gt; Edição de Fascículos </h2>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
%>


<c:set var="confirmBaixa" value="if (!confirm('Confirma a baixa do fascículo?')) return false" scope="request" />
<c:set var="confirmRemocao" value="if (!confirm('Confirma remoção do fascículo do acervo ?')) return false" scope="request" />


<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">


<f:view>

	<h:form>
	
		<a4j:keepAlive beanName="detalhesMateriaisDeUmTituloMBean"></a4j:keepAlive>  <%-- se a edição vem da página de detalhes--%>
	
		<a4j:keepAlive beanName="editaMaterialInformacionalMBean"></a4j:keepAlive>
	
	
		<%-- Quando vai editar, precisa guardar se está na pesquisa de catalogação ou não, se o usuário usar o botão voltar --%>
		<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	
	
		<%-- Quando o usuário começa a catalogação e decide alterar as informações dos materiais do título, se ele voltar as informações da catalogação devem permanecer --%>
		<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
		<%-- Quando edita um fascículo a partir da página de pesquisa --%>
		<a4j:keepAlive beanName="pesquisarFasciculoMBean"></a4j:keepAlive>
	
		
		<c:if test="${ editaMaterialInformacionalMBean.notaCirculacao != null }">
			<div class="infoAltRem" style="margin-top: 10px">
				<h:graphicImage value="/img/delete.gif"  />: Remover Nota de Circulação 
				<%-- <h:graphicImage value="/img/email_go.png"  />: Enviar para validação  --%>
			</div>
		</c:if>
	
		<table class="formulario" width="100%" style="maring-top:20px">
					
			<tbody>
				
				<caption> Editar Fascículo </caption>
	
				<tr>
					<td colspan="2">
						<c:set var="_titulo" value="${editaMaterialInformacionalMBean.titulo}" scope="request"/>
						<%@include file="/public/biblioteca/informacoes_padrao_titulo.jsp"%>
					</td>
				</tr>
			
				<tr>
					<td colspan="2">
						<c:set var="_assinatura" value="${editaMaterialInformacionalMBean.fasciculoEdicao.assinatura}" scope="request"/>
						<%@include file="/biblioteca/info_assinatura.jsp"%>
					</td>
				</tr>
				
				<tr>
					<td colspan="2">
						<table class="subFormulario" style="width: 100%">
							<caption>Informações do Fascículo</caption>
							<tr>
								<th>Código de Barras: </th> 
								<td>
									<h:inputText id="inputTextCodigoBarras"  value="#{editaMaterialInformacionalMBean.fasciculoEdicao.codigoBarras}" disabled="true"/>
								</td>
							</tr>
							
							<tr>
								<th width="25%">Ano Cronológico: </th> 
								<td>
									<h:inputText id="inputTextAnoCronologico" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.anoCronologico}" maxlength="20"  size="10" />
									<ufrn:help>Este campo pode conter letras para fascículos que englobam mais de um ano cronológico. Exemplo: 2009-2010</ufrn:help>
								</td>
							</tr>
							
							<tr>
								<th>Dia/Mês:</th>
								<td>
									<h:inputText id="inputTextDiaMes" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.diaMes}" maxlength="20"  size="10"  />
									<ufrn:help> Este campo pode conter letras para fascículos que englobam mais de um dia/mês. Exemplo: 10-20, jan-fev, 10jan-15fev</ufrn:help>
								</td>
							</tr>
							
							<tr>
								<th>Ano:</th>
								<td>
									<h:inputText id="inputTextAno" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.ano}" maxlength="20"  size="10"   />
									<ufrn:help>Número que faz referência ao ano de criação do fascículo. Esse campo pode conter letras para fascículos que englobam mais de um ano. Exemplo: 10-20</ufrn:help>
								</td>
							</tr>
							
							<tr>
								<th>Volume:</th>
								<td>
									<h:inputText id="inputTextVolume" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.volume}" maxlength="20"  size="10" />
									<ufrn:help>Este campo pode conter letras para fascículos que englobam mais de um volume. Exemplo: 10-20</ufrn:help>
								</td>
							</tr>
							
							<tr>
								<th>Número:</th>
								<td>
									<h:inputText id="inputTextNumero" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.numero}" maxlength="10"  size="10"/>
									<ufrn:help>Este campo pode conter letras para fascículos que englobam mais de um número. Exemplo: 10-20</ufrn:help>
								</td>
							</tr>
							
							<tr>
								<th>Edição:</th>
								<td>
									<h:inputText id="inputTextEdicao" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.edicao}" maxlength="20" size="10" />
									<ufrn:help>Este campo pode conter letras para fascículos que englobam mais de uma edição. Exemplo: 10-20</ufrn:help>
								</td>
							</tr>
							
							
							<tr>
								<th class="required">Número de Chamada (localização)</th>
								<td colspan="3">
									<h:inputText id="inputTextNumeroChamada" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.numeroChamada }" size="50" maxlength="200" />
								</td>
							</tr>
							
							<tr>
								<th class="required">Segunda Localização</th>
								<td colspan="3">
									<h:inputText id="inputTextSegundaLocalizacao" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.segundaLocalizacao }" size="50" maxlength="200" />
								</td>
							</tr>
							
							<tr>
								<th class="required">Biblioteca:</th>
								<td colspan="3"> 
									<h:selectOneMenu id="comboboxBiblioteca" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.biblioteca.id}" disabled="#{true}" > 
										<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
										<f:selectItems value="#{editaMaterialInformacionalMBean.bibliotecasInternas}"/>
									</h:selectOneMenu>
									<ufrn:help> Para alterar a biblioteca do fascículo, utilize a opção "Transferir Fascículos entre Bibliotecas" no menu principal do sistema.</ufrn:help>
								</td>
							</tr>
							
							
							<tr>
								<th  class="required">Coleção:</th>
								<td colspan="3">
									<h:selectOneMenu id="comboBoxColecao" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.colecao.id}" >
										<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
										<f:selectItems value="#{editaMaterialInformacionalMBean.colecoes}"/>
									</h:selectOneMenu>
								</td>
							</tr>
							
							<tr>
							
								<th class="required">Situação:</th>
								<td>
									
									<%-- 2 verificações para não permitir de forma alguma que o usuário mude a situação do material manualmente estando ele emprestado --%>
									
									<c:if test="${! editaMaterialInformacionalMBean.fasciculoEdicao.situacao.situacaoEmprestado}">
										<h:selectOneMenu id="comboBoxSituacao" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.situacao.id}" rendered="#{! editaMaterialInformacionalMBean.fasciculoEdicao.situacao.situacaoEmprestado}">	
											<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
											<f:selectItems value="#{editaMaterialInformacionalMBean.situacoes}"/>
										</h:selectOneMenu>
									</c:if>
									
									<c:if test="${ editaMaterialInformacionalMBean.fasciculoEdicao.situacao.situacaoEmprestado}">
										<h:selectOneMenu id="comboSituacaoEmpretado" disabled="true" rendered="#{editaMaterialInformacionalMBean.fasciculoEdicao.situacao.situacaoEmprestado}">
											<f:selectItem itemLabel=" Emprestado " itemValue="#{editaMaterialInformacionalMBean.idSituacaoEmprestado}"/>
										</h:selectOneMenu>
									</c:if>
									
								</td>
							</tr>
							
							<tr>
								<th class="required">Status:</th>
								<td>
									<h:selectOneMenu id="comboStatus" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.status.id}" >
										<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
										<f:selectItems value="#{editaMaterialInformacionalMBean.statusAtivos}"/>
									</h:selectOneMenu>  		
								</td>
							</tr>
							
							<tr>
								<th class="required">Tipo do Material:</th>
								<td colspan="3">
									<h:selectOneMenu id="comboTipoMaterial" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.tipoMaterial.id}" >
										<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
										<f:selectItems  value="#{editaMaterialInformacionalMBean.tiposMaterial}" />
									</h:selectOneMenu>
					
								</td>
					
							</tr>
							
							<tr>
								<th valign="top">Formas do Documento:</th>
								<td colspan="3">
									<h:selectManyListbox  id="comBoxFormaDocumento" value="#{editaMaterialInformacionalMBean.idsFormasDocumentoEscolhidos}" size="10" style="width: 40%;">
						           		<f:selectItems value="#{formaDocumentoMBean.allCombo}"/>
						       		</h:selectManyListbox>
						       		<ufrn:help>Para selecionar mais de uma <strong>forma de documento</strong> mantenha pressionada a tecla "Ctrl", em seguida selecione os itens desejados. 
									Para retirar a seleção, também é preciso pressionar a tecla "Ctrl". </ufrn:help>
								</td>
							</tr>
							
							<tr>
								<th>Nota Geral:</th>
								<td colspan="3">
									<h:inputTextarea id="inputAreaNotaGeral" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.notaGeral }" cols="57" rows="2" />
								</td>
					
							</tr>
							
							<tr>
								<th>Nota ao Usuário:</th>
								<td colspan="3">
									<h:inputTextarea id="inputAreaNotaUsuario" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.notaUsuario }" cols="57" rows="2"  />
									<ufrn:help> Informações que vão aparecer para o usuário nas consultas públicas do sistema</ufrn:help>
								</td>
					
							</tr>
							
							
							<tr>
								<th>Suplemento que acompanha o fascículo:</th>
								<td colspan="3">
									<h:inputTextarea id="inputAreaSuplemento" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.descricaoSuplemento }" cols="57" rows="1" />
								</td>
							</tr>
		
							<c:if test="${ editaMaterialInformacionalMBean.notaCirculacao != null }">
								<tr style="height: 20px"></tr>
								<tr style="background-color: #CCCCCC;">
									<td colspan="3">
										<div style="height: 20px; font-weight: bold; text-align: justify; padding-left: 15%; padding-right: 15%;">
											<div style="float: left">
												Nota de Circulação:
												<h:outputText id="outputNotaCirculacao" value="#{ editaMaterialInformacionalMBean.notaCirculacao.nota }" />
											</div>
											<div style="float: right">							
												<h:commandLink id="cmdLinkRemoverNotaCirculacao" action="#{editaMaterialInformacionalMBean.removerNotaCirculacaoFasciculo}" onclick="#{confirmDelete}">
													<h:graphicImage url="/img/delete.gif" style="border:none;" title="Clique aqui para remover a nota de circulação" />				
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
										<h:inputTextarea id="inputAreaMotivoBaixa" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.motivoBaixa }" cols="57" rows="2"  />
									</td>
						
								</tr>
							</c:if> --%>
						</table>
					</td>
				</tr>
				
				<tfoot>
					<tr>
						<td colspan="4" align="center">
							
							
							<h:commandButton id="botaoAtualizarFasciculo" value="Atualizar Fascículo" action="#{editaMaterialInformacionalMBean.atualizarFasciculo}">
								<f:setPropertyActionListener target="#{editaMaterialInformacionalMBean.finalizarAtualizacao}" value="false" />
							</h:commandButton>
							
							<%-- Também salva o Fascículo, mas volta para a página de onde foi chamado --%>
							<h:commandButton id="botaoFinalizarAtualizacao"  value="Finalizar Atualização" action="#{editaMaterialInformacionalMBean.atualizarFasciculo}">
								<f:setPropertyActionListener target="#{editaMaterialInformacionalMBean.finalizarAtualizacao}" value="true" />
							</h:commandButton>
							
							<h:commandButton id="botaoIncluirNotaCirculacao" value="Incluir Nota de Circulação" action="#{editaMaterialInformacionalMBean.incluirNotaCirculacaoFasciculo}" 
								rendered="#{! editaMaterialInformacionalMBean.retornaCatalogacao}"
								onclick="return confirm('Deseja iniciar a inclusão de uma nota de circulação ? Os dados não salvos nesta tela serão perdidos.') " />
							
							<%--
							<c:if test="${! editaMaterialInformacionalMBean.dandoBaixaMaterial && ! editaMaterialInformacionalMBean.removendoMaterial}">
							</c:if>
							 --%>
							
							<%-- 
							<c:if test="${editaMaterialInformacionalMBean.dandoBaixaMaterial}">
								<h:commandButton id="botaoDarBaixaMaterial"  value="Dar Baixa Fascículo " 
												action="#{editaMaterialInformacionalMBean.darBaixaMaterial}" 
												rendered="#{editaMaterialInformacionalMBean.dandoBaixaMaterial}" onclick="#{confirmBaixa}">
								</h:commandButton>
							</c:if>
							
							<c:if test="${editaMaterialInformacionalMBean.removendoMaterial}">
								<h:commandButton id="botaoRemoverMaterial"  value="Remover Fascículo " 
										rendered="#{editaMaterialInformacionalMBean.removendoMaterial}"
										action="#{editaMaterialInformacionalMBean.apagarMaterial}" onclick="#{confirmRemocao}">
								</h:commandButton>
							</c:if>
							--%>
							
							<h:commandButton value="<< Voltar" action="#{editaMaterialInformacionalMBean.voltarPaginaFasciculo}" rendered="true" id="botaoVoltar" />			
						</td>
					</tr>
				</tfoot>
				
		</table>

	</h:form>
	
</f:view>
		
</ufrn:checkRole>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>