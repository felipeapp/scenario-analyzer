<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

<h2>  <ufrn:subSistema /> &gt; Edi��o de Fasc�culos </h2>

<%
	CheckRoleUtil.checkRole(request, response, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
%>


<c:set var="confirmBaixa" value="if (!confirm('Confirma a baixa do fasc�culo?')) return false" scope="request" />
<c:set var="confirmRemocao" value="if (!confirm('Confirma remo��o do fasc�culo do acervo ?')) return false" scope="request" />


<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL }  %>">


<f:view>

	<h:form>
	
		<a4j:keepAlive beanName="detalhesMateriaisDeUmTituloMBean"></a4j:keepAlive>  <%-- se a edi��o vem da p�gina de detalhes--%>
	
		<a4j:keepAlive beanName="editaMaterialInformacionalMBean"></a4j:keepAlive>
	
	
		<%-- Quando vai editar, precisa guardar se est� na pesquisa de cataloga��o ou n�o, se o usu�rio usar o bot�o voltar --%>
		<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	
	
		<%-- Quando o usu�rio come�a a cataloga��o e decide alterar as informa��es dos materiais do t�tulo, se ele voltar as informa��es da cataloga��o devem permanecer --%>
		<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
		<%-- Quando edita um fasc�culo a partir da p�gina de pesquisa --%>
		<a4j:keepAlive beanName="pesquisarFasciculoMBean"></a4j:keepAlive>
	
		
		<c:if test="${ editaMaterialInformacionalMBean.notaCirculacao != null }">
			<div class="infoAltRem" style="margin-top: 10px">
				<h:graphicImage value="/img/delete.gif"  />: Remover Nota de Circula��o 
				<%-- <h:graphicImage value="/img/email_go.png"  />: Enviar para valida��o  --%>
			</div>
		</c:if>
	
		<table class="formulario" width="100%" style="maring-top:20px">
					
			<tbody>
				
				<caption> Editar Fasc�culo </caption>
	
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
							<caption>Informa��es do Fasc�culo</caption>
							<tr>
								<th>C�digo de Barras: </th> 
								<td>
									<h:inputText id="inputTextCodigoBarras"  value="#{editaMaterialInformacionalMBean.fasciculoEdicao.codigoBarras}" disabled="true"/>
								</td>
							</tr>
							
							<tr>
								<th width="25%">Ano Cronol�gico: </th> 
								<td>
									<h:inputText id="inputTextAnoCronologico" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.anoCronologico}" maxlength="20"  size="10" />
									<ufrn:help>Este campo pode conter letras para fasc�culos que englobam mais de um ano cronol�gico. Exemplo: 2009-2010</ufrn:help>
								</td>
							</tr>
							
							<tr>
								<th>Dia/M�s:</th>
								<td>
									<h:inputText id="inputTextDiaMes" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.diaMes}" maxlength="20"  size="10"  />
									<ufrn:help> Este campo pode conter letras para fasc�culos que englobam mais de um dia/m�s. Exemplo: 10-20, jan-fev, 10jan-15fev</ufrn:help>
								</td>
							</tr>
							
							<tr>
								<th>Ano:</th>
								<td>
									<h:inputText id="inputTextAno" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.ano}" maxlength="20"  size="10"   />
									<ufrn:help>N�mero que faz refer�ncia ao ano de cria��o do fasc�culo. Esse campo pode conter letras para fasc�culos que englobam mais de um ano. Exemplo: 10-20</ufrn:help>
								</td>
							</tr>
							
							<tr>
								<th>Volume:</th>
								<td>
									<h:inputText id="inputTextVolume" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.volume}" maxlength="20"  size="10" />
									<ufrn:help>Este campo pode conter letras para fasc�culos que englobam mais de um volume. Exemplo: 10-20</ufrn:help>
								</td>
							</tr>
							
							<tr>
								<th>N�mero:</th>
								<td>
									<h:inputText id="inputTextNumero" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.numero}" maxlength="10"  size="10"/>
									<ufrn:help>Este campo pode conter letras para fasc�culos que englobam mais de um n�mero. Exemplo: 10-20</ufrn:help>
								</td>
							</tr>
							
							<tr>
								<th>Edi��o:</th>
								<td>
									<h:inputText id="inputTextEdicao" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.edicao}" maxlength="20" size="10" />
									<ufrn:help>Este campo pode conter letras para fasc�culos que englobam mais de uma edi��o. Exemplo: 10-20</ufrn:help>
								</td>
							</tr>
							
							
							<tr>
								<th class="required">N�mero de Chamada (localiza��o)</th>
								<td colspan="3">
									<h:inputText id="inputTextNumeroChamada" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.numeroChamada }" size="50" maxlength="200" />
								</td>
							</tr>
							
							<tr>
								<th class="required">Segunda Localiza��o</th>
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
									<ufrn:help> Para alterar a biblioteca do fasc�culo, utilize a op��o "Transferir Fasc�culos entre Bibliotecas" no menu principal do sistema.</ufrn:help>
								</td>
							</tr>
							
							
							<tr>
								<th  class="required">Cole��o:</th>
								<td colspan="3">
									<h:selectOneMenu id="comboBoxColecao" value="#{editaMaterialInformacionalMBean.fasciculoEdicao.colecao.id}" >
										<f:selectItem itemLabel="-- Selecione --" itemValue="-1" />
										<f:selectItems value="#{editaMaterialInformacionalMBean.colecoes}"/>
									</h:selectOneMenu>
								</td>
							</tr>
							
							<tr>
							
								<th class="required">Situa��o:</th>
								<td>
									
									<%-- 2 verifica��es para n�o permitir de forma alguma que o usu�rio mude a situa��o do material manualmente estando ele emprestado --%>
									
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
									Para retirar a sele��o, tamb�m � preciso pressionar a tecla "Ctrl". </ufrn:help>
								</td>
							</tr>
							
							<tr>
								<th>Nota Geral:</th>
								<td colspan="3">
									<h:inputTextarea id="inputAreaNotaGeral" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.notaGeral }" cols="57" rows="2" />
								</td>
					
							</tr>
							
							<tr>
								<th>Nota ao Usu�rio:</th>
								<td colspan="3">
									<h:inputTextarea id="inputAreaNotaUsuario" value="#{ editaMaterialInformacionalMBean.fasciculoEdicao.notaUsuario }" cols="57" rows="2"  />
									<ufrn:help> Informa��es que v�o aparecer para o usu�rio nas consultas p�blicas do sistema</ufrn:help>
								</td>
					
							</tr>
							
							
							<tr>
								<th>Suplemento que acompanha o fasc�culo:</th>
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
												Nota de Circula��o:
												<h:outputText id="outputNotaCirculacao" value="#{ editaMaterialInformacionalMBean.notaCirculacao.nota }" />
											</div>
											<div style="float: right">							
												<h:commandLink id="cmdLinkRemoverNotaCirculacao" action="#{editaMaterialInformacionalMBean.removerNotaCirculacaoFasciculo}" onclick="#{confirmDelete}">
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
							
							
							<h:commandButton id="botaoAtualizarFasciculo" value="Atualizar Fasc�culo" action="#{editaMaterialInformacionalMBean.atualizarFasciculo}">
								<f:setPropertyActionListener target="#{editaMaterialInformacionalMBean.finalizarAtualizacao}" value="false" />
							</h:commandButton>
							
							<%-- Tamb�m salva o Fasc�culo, mas volta para a p�gina de onde foi chamado --%>
							<h:commandButton id="botaoFinalizarAtualizacao"  value="Finalizar Atualiza��o" action="#{editaMaterialInformacionalMBean.atualizarFasciculo}">
								<f:setPropertyActionListener target="#{editaMaterialInformacionalMBean.finalizarAtualizacao}" value="true" />
							</h:commandButton>
							
							<h:commandButton id="botaoIncluirNotaCirculacao" value="Incluir Nota de Circula��o" action="#{editaMaterialInformacionalMBean.incluirNotaCirculacaoFasciculo}" 
								rendered="#{! editaMaterialInformacionalMBean.retornaCatalogacao}"
								onclick="return confirm('Deseja iniciar a inclus�o de uma nota de circula��o ? Os dados n�o salvos nesta tela ser�o perdidos.') " />
							
							<%--
							<c:if test="${! editaMaterialInformacionalMBean.dandoBaixaMaterial && ! editaMaterialInformacionalMBean.removendoMaterial}">
							</c:if>
							 --%>
							
							<%-- 
							<c:if test="${editaMaterialInformacionalMBean.dandoBaixaMaterial}">
								<h:commandButton id="botaoDarBaixaMaterial"  value="Dar Baixa Fasc�culo " 
												action="#{editaMaterialInformacionalMBean.darBaixaMaterial}" 
												rendered="#{editaMaterialInformacionalMBean.dandoBaixaMaterial}" onclick="#{confirmBaixa}">
								</h:commandButton>
							</c:if>
							
							<c:if test="${editaMaterialInformacionalMBean.removendoMaterial}">
								<h:commandButton id="botaoRemoverMaterial"  value="Remover Fasc�culo " 
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