<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<%-- Pagina que mostra os detalhes de todos os artigos de um fascículo 


<h2>  <ufrn:subSistema /> &gt; Artigos do Fascículo </h2>

<style type="text/css">
	
	table.dadosArtigo{
		width: 90%;
		margin-left: auto;
		margin-right: auto;
		border-left: 1px solid #DEDFE3;
		border-right: 1px solid silver;
	}

</style>


<link rel="stylesheet" media="all" href="/shared/css/ufrn_relatorio.css" type="text/css"/>


<f:view>

	<h:form>

		<%-- Usado quando vai volta para tela dos detalhes dos fascículos de um Título 
		<a4j:keepAlive beanName="detalhesMateriaisDeUmTituloMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
		
		
		<%-- Usado quando vai volta para tela  de pesquisa dos fascículos 
		<a4j:keepAlive beanName="pesquisarFasciculoMBean"></a4j:keepAlive>
		
	
		<div class="infoAltRem" style="margin-top: 10px; width: 98%">
			
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Apagar a Arquivo
			
		</div>

		<table class="formulario" style="width: 98%;">
				<caption>Artigos do Fascículo</caption>
				
				<tr>
					<td>
						<c:set var="_assinatura" value="${detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.assinatura}" scope="request"/>
						<%@include file="/biblioteca/info_assinatura.jsp"%>
					</td>
				</tr>
				
				<tr>
					<td>
			    		<table class="subFormulario" style="width: 100%">
							<caption>Detalhes do Fascículo</caption>
							<tr>
								<th width="20%">Ano Cronológico:</th>
								<td width="15%">
								
									<c:if test="${not empty detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.anoCronologico}">
										${detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.anoCronologico}
									</c:if>
							
									<c:if test="${empty detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.anoCronologico}">
										--
									</c:if>
								
								</td>
								
								<th>Dia/Mês:</th>
								<td width="15%">
								
									<c:if test="${not empty detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.diaMes}">
											${detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.diaMes}
									</c:if>
								
									<c:if test="${empty detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.diaMes}">
										--
									</c:if>
								
								</td>
								
								
								<th>Ano:</th>
								<td width="15%">
								
									<c:if test="${not empty detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.ano}">
											${detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.ano}
									</c:if>
								
									<c:if test="${empty detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.ano}">
										--
									</c:if>
								
								</td>
								
								<th>Volume:</th>
								<td width="15%">
								
									<c:if test="${not empty detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.volume}">
											${detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.volume}
									</c:if>
								
									<c:if test="${empty detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.volume}">
										--
									</c:if>
									
								</td>
								
								<th>Numero:</th>
								<td width="15%">
									<c:if test="${not empty detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.numero}">
											${detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.numero}
									</c:if>
								
									<c:if test="${empty detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.numero}">
										--
									</c:if>
								</td>
								
								<th>Edição:</th>
								<td width="15%">
								
									<c:if test="${not empty detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.edicao}">
											${detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.edicao}
									</c:if>
								
									<c:if test="${empty detalhesMateriaisDeUmTituloMBean.fasciculoSelecionado.edicao}">
										--
									</c:if>
									
								</td>
							
							</tr>
						</table>
					</td>
					
				</tr>
		</table>

		<table class="dadosArtigo" style="width: 98%;"> 
			
			<tbody>
			
				<c:forEach var="artigoDePeriodico" items="#{detalhesMateriaisDeUmTituloMBean.listaArtigosDoFasciculo}" varStatus="status">
					
					<tr>
						<td>
					
						<table class="subFormulario" style="width: 100%">
						
							<caption>Artigo	${status.index+1} de  ${fn:length( detalhesMateriaisDeUmTituloMBean.listaArtigosDoFasciculo)}
							
								<h:commandLink style=" margin-left: 90%" 
									action="#{removerEntidadeDoAcervoMBean.telaConfirmaRemocaoVindoPaginaVisualizacaoArtigosDoFasciculo}">
									<h:graphicImage url="/img/delete.gif" style="border:none"
										title="Clique aqui para remover o artigo do sistema" />
		
									<f:param name="idArtigoRemocao" value="#{artigoDePeriodico.idArtigoDePeriodico}"/>					
								</h:commandLink>
								
							</caption>
							
							<tr>
								<td style="border:none;">
									<strong>Título:</strong>
									<c:if test="${not empty artigoDePeriodico.titulo}">
											${artigoDePeriodico.titulo}
									</c:if>
								
									<c:if test="${empty artigoDePeriodico.titulo}">
										-- NÃO INFORMADO --
									</c:if>
								</td>
								<td style="border:none;">
									<strong>Autor:</strong>
									<c:if test="${not empty artigoDePeriodico.autor}">
											${artigoDePeriodico.autor}
									</c:if>
								
									<c:if test="${empty artigoDePeriodico.autor}">
										-- NÃO INFORMADO --
									</c:if>
								</td>
							</tr>
							<tr>
								<td style="border:none;">
									<strong>Autores Secundários:</strong>
									<c:choose>
										<c:when test="${not empty artigoDePeriodico.autoresSecundarios}">
											<c:forEach var="autor" items="#{artigoDePeriodico.autoresSecundariosFormatados}">
												<br/> &nbsp;&nbsp;&nbsp; ${autor}
											</c:forEach>
										</c:when>
										<c:otherwise>
											-- NÃO INFORMADO --
										</c:otherwise>
									</c:choose>
								</td>
								<td style="border:none;">
									<strong>Intervalo de Páginas:</strong>
									
									<c:if test="${not empty artigoDePeriodico.intervaloPaginas}">
											${artigoDePeriodico.intervaloPaginas}
									</c:if>
								
									<c:if test="${empty artigoDePeriodico.intervaloPaginas}">
										-- NÃO INFORMADO --
									</c:if>
									
								</td>
							</tr>
							<tr>
								<td style="border:none;">
									<strong>Palavras-Chave:</strong>
									
									<c:if test="${  fn:length(artigoDePeriodico.assuntosFormatados) > 0}">
										<c:forEach items="${artigoDePeriodico.assuntosFormatados}" var="assunto" varStatus="statusPalavaChave">
												<c:if test="${statusPalavaChave.index != 0}">
												,
												</c:if>
												${assunto}
										</c:forEach>		
									</c:if>
									<c:if test="${ fn:length(artigoDePeriodico.assuntosFormatados) == 0 }">
										-- NÃO INFORMADO --
									</c:if>
										
								</td>
								
								<td style="border:none;">
									<strong>Local da Publicação:</strong> 
										
										<c:if test="${  fn:length(artigoDePeriodico.locaisPublicacaoFormatados) > 0}">
											<c:forEach items="${artigoDePeriodico.locaisPublicacaoFormatados}" var="local" varStatus="statusLocal">
												<c:if test="${statusLocal.index != 0}">
												,
												</c:if>
												${local}
											</c:forEach>
										</c:if>
										<c:if test="${ fn:length(artigoDePeriodico.locaisPublicacaoFormatados) == 0 }">
											-- NÃO INFORMADO --
										</c:if>
								</td>
							</tr>
							<tr>
								
							</tr>
							<tr>
								<td style="border:none;">
									<strong>Editora:</strong>
										<c:if test="${  fn:length(artigoDePeriodico.editorasFormatadas) > 0}">
											<c:forEach items="${artigoDePeriodico.editorasFormatadas}" var="editora" varStatus="statusEditora">
												<c:if test="${statusEditora.index != 0}">
												,
												</c:if>
												${editora}
											</c:forEach>
										</c:if>
										<c:if test="${ fn:length(artigoDePeriodico.editorasFormatadas) == 0 }">
											-- NÃO INFORMADO --
										</c:if>
								</td>
								
								<td style="border:none;">
									<strong>Ano:</strong> 
									
									<c:if test="${  fn:length(artigoDePeriodico.anosFormatados) > 0}">
										<c:forEach items="${artigoDePeriodico.anosFormatados}" var="ano" varStatus="statusAno">
											<c:if test="${statusAno.index != 0}">
											,
											</c:if>
											${ano}
										</c:forEach>
									</c:if>
									
									<c:if test="${ fn:length(artigoDePeriodico.anosFormatados) == 0 }">
										-- NÃO INFORMADO --
									</c:if>
									
								</td>
							</tr>
							<tr>
								<th colspan="2" style="border:none; text-align: left"><strong>Resumo:</strong></th>
							</tr>
							<tr>
								<td colspan="2" style="border:none;">
								
									<c:if test="${not empty artigoDePeriodico.resumo}">
											${artigoDePeriodico.resumo}
									</c:if>
								
									<c:if test="${empty artigoDePeriodico.resumo}">
										-- NÃO INFORMADO --
									</c:if>
								
								</td>
							</tr>
						</table> 
						
					
						</td>
					</tr>
					
				</c:forEach>

			</tbody>

		</table>
	
	
	
		<c:if test="${pesquisarFasciculoMBean.operacaoPesquisar || pesquisarFasciculoMBean.operacaoCatalogacaoArtigos || pesquisarFasciculoMBean.operacaoAtribuiFasciculoAoArtigo }">
			<table class="visualizacao" style="width: 98%; text-align: center;">
				<tfoot>
					<tr>
						<td>
							<h:commandButton action="#{pesquisarFasciculoMBean.pesquisar}" value="<< Voltar"></h:commandButton>
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if>
	
		<c:if test="${!pesquisarFasciculoMBean.operacaoPesquisar && ! pesquisarFasciculoMBean.operacaoCatalogacaoArtigos && ! pesquisarFasciculoMBean.operacaoAtribuiFasciculoAoArtigo}">
			<table class="visualizacao" style="width: 98%; text-align: center;">
				<tfoot>
					<tr>
						<td>
							<h:commandButton action="#{detalhesMateriaisDeUmTituloMBean.telaInformacoesMateriais}" value=" << Voltar "></h:commandButton>
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%> --%>