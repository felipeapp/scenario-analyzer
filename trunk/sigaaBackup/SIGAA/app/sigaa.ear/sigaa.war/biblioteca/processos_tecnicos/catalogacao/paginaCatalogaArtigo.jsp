<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<style>	
	table.subFormulario td.oculto { display: none; padding: 0;}
	
	table.tabelaFundoTransparente tbody{
		background-color: transparent
	}
	
</style>

<link rel="stylesheet" media="all" href="/sigaa/css/biblioteca/estilo_botoes_pequenos.css" type="text/css"/>


<script type="text/javascript">

	function habilitarDetalhesArtigo(idLinha) {
		var linha = 'linha_'+ idLinha;           // o id da linha da tabela
		var linhaImagem = 'imagem_' + idLinha;  // o id da imagem usada no link que abilita os detalhes
		
		if ( $(linha).style.display != 'table-cell' && $(linha).style.display != 'inline-block' ) {       //$() == getElementById()

			if (/msie/.test( navigator.userAgent.toLowerCase() )){
				$(linha).style.display = 'inline-block';
			}else{
				$(linha).style.display = 'table-cell';
			}
			
			$(linhaImagem).src= '/sigaa/img/biblioteca/cima.gif';
		} else {
			$(linha).style.display = 'none';
			$(linhaImagem).src= '/sigaa/img/biblioteca/baixo.gif';
		}
	}


	//funcao para abrir a pagina com o hit�rico de altera��es de um t�tulo. 
	var janelaHistoricoArtigos = null;
	
	function abreJanelaHistoricoAlteracoes(idArtigoVisualizarHistorico){
		
		if (janelaHistoricoArtigos == null || janelaHistoricoArtigos.closed){
			janelaHistoricoArtigos = window.open('${ctx}/biblioteca/processos_tecnicos/catalogacao/paginaHistoricoAlteracoesCatalogacaoArtigo.jsf?idArtigoVisualizarHistorico='+idArtigoVisualizarHistorico,'','width=1024,height=600,left=50,top=100,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janelaHistoricoArtigos.location = '${ctx}/biblioteca/processos_tecnicos/catalogacao/paginaHistoricoAlteracoesCatalogacaoArtigo.jsf?idArtigoVisualizarHistorico='+idArtigoVisualizarHistorico;
		}
	
		janelaHistoricoArtigos.focus();
	}
	

</script>



<h2>  <ufrn:subSistema /> &gt; Cataloga��o &gt; Artigos de Peri�dicos</h2>


<div class="descricaoOperacao"> 
    <p>P�gina para cataloga��o de artigos de peri�dicos.</p>
    <p>O usu�rio deve digitar os dados dos artigos nesse formul�rio simplificado, esses dados ser�o salvos no formato MARC.</p>
    <p> <strong> Observa��o: N�o � preciso informar quais s�o os campos MARC correspondentes, o sistema ir� ger�-los automaticamente. </strong> </p>
</div>

<f:view>

	<h:form>
	
		<%-- Parte onde o usu�rio visualiza o hist�rico de altera��es do t�tulo ou autoridade --%>
		<c:if test="${catalogacaoArtigosMBean.obj.id > 0}">
			<table style="width: 100%;">
					
				<tr>
					<td>					
						<ul class="listaOpcoes">
						
							<li id="btnHistoricoTitulo">
								
								<a class="noborder" style="font-weight: bold;" title="Visualizar Hist�rico de Altera��es" onclick="abreJanelaHistoricoAlteracoes( ${catalogacaoArtigosMBean.obj.id} );">
									Hist�rico de Altera��es
								</a>
								
							</li>
							
						</ul>
					</td>
				</tr>
						
			</table>
		</c:if>
	
	
		<%-- Para quando voltar para a tela de pesquisa, vindo da cataloga��o de um novo artigo. --%>
		<a4j:keepAlive beanName="pesquisarFasciculoMBean"></a4j:keepAlive>
		
	
	
		<%-- Quando essa p�gina � chamada a partir da tela de detalhes dos materiais � preciso guarda os dados para voltar para l� --%>
		
		<a4j:keepAlive beanName="detalhesMateriaisDeUmTituloMBean"></a4j:keepAlive>
		
		
		<a4j:keepAlive beanName="catalogacaoArtigosMBean"></a4j:keepAlive>
	
	
		<%-- Quando ele chama essa p�gina a partir da p�gina de pesquisa --%>
	
		<a4j:keepAlive beanName="pesquisarArtigoMBean"></a4j:keepAlive>
			
	
		<%-- Quando ele chama essa p�gina a partir da p�gina de pesquisa --%>
	
		<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	

	
	
		<%-- Visualiza��o dos artigos que o peri�dico j� possui  --%>
		
		<div class="infoAltRem" style="margin-top: 10px">
		
			
			<h:graphicImage value="/img/biblioteca/baixo.gif" style="overflow: visible;" />: Mostrar Informa��es Completas do Artigo

			<h:graphicImage value="/img/biblioteca/cima.gif" style="overflow: visible;" />: Ocultar Informa��es Completas do Artigo
			
			<br>
			
			<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />: Adicionar Palavra-Chave
			
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Palavra-Chave
			
		</div>
		
		
		<table class="formulario" style="width: 100%;">
			
			<caption> Incluir Artigo </caption>
			
			<tr>
				<td colspan="14">
					<c:set var="_assinatura" value="${catalogacaoArtigosMBean.fasciculoDoArtigo.assinatura}" scope="request"/>
					<%@include file="/biblioteca/info_assinatura.jsp"%>
				</td>
			</tr>
			
			<tr>
				<td colspan="14">
					<table class="subFormulario" width="100%">
						<caption>Dados do Fasc�culo</caption>
						<tr>
							<th style="width: 15%;"> C�digo de Barras: </th>
							<td>${catalogacaoArtigosMBean.fasciculoDoArtigo.codigoBarras}</td>
							<th style="width: 15%;"> Ano Cronol�gico: </th>
							<td>${catalogacaoArtigosMBean.fasciculoDoArtigo.anoCronologico}</td>
							<th> Dia/M�s: </th>
							<td>${catalogacaoArtigosMBean.fasciculoDoArtigo.diaMes}</td>
							<th> Ano: </th>
							<td>${catalogacaoArtigosMBean.fasciculoDoArtigo.ano}</td>
							<th > Volume: </th>
							<td>${catalogacaoArtigosMBean.fasciculoDoArtigo.volume}</td>
							<th> N�mero: </th>
							<td>${catalogacaoArtigosMBean.fasciculoDoArtigo.numero}</td>
							<th> Edi��o: </th>
							<td style="width: 5%;">${catalogacaoArtigosMBean.fasciculoDoArtigo.edicao}</td>
						</tr>
					</table>
				</td>
			</tr>
			
			<c:if test="${not empty catalogacaoArtigosMBean.artigosDoFasciculo}">
			
				<tr>
					<td colspan="14" style="width: 100%">
						
						<rich:panel header="Dados dos Artigos do Fasc�culo">
						
					    		<table class="subFormulario" style="width: 100%">
				
								<c:forEach items="${catalogacaoArtigosMBean.artigosDoFasciculo}" var="artigoCache" varStatus="status">
					
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								
										<td width="2%" style="text-align:center">
											<a href="javascript: void(0);" onclick="habilitarDetalhesArtigo(${artigoCache.idArtigoDePeriodico});">
												<img id="imagem_${artigoCache.idArtigoDePeriodico}" src="${ctx}/img/biblioteca/baixo.gif"/>
											</a>
										</td> 
									
										<th width="20%" colspan="1">
											T�tulo:
										</th>
										
										<td width="28%" colspan="1">
											${artigoCache.titulo}
										</td>
					
										<th width="20%" colspan="1">
											Autor:
										</th>
					
										<td width="28%" colspan="1">
											${artigoCache.autor}
										</td>
										
									</tr>
										
									
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
									
										<td id="linha_${artigoCache.idArtigoDePeriodico}" colspan="5" class="oculto">
											<table width="100%" class="tabelaFundoTransparente">
												<c:if test="${not empty artigoCache.autoresSecundarios}">
													<th width="23%" style="vertical-align: top;">Autores Secund�rios:</th>
													<td>
														<c:forEach var="autor" items="#{artigoCache.autoresSecundariosFormatados}">
															${autor} <br/>
														</c:forEach>
													</td>
												</c:if>
												<tr>
													<th width="23%">Intervalos de P�ginas:</th>
													
													<td>
														${artigoCache.intervaloPaginas}
													</td>
												</tr>
												<tr> 
												 	<th width="23%" style="vertical-align: top;">Palavras-Chave:</th>
												 	
												 	<td>
														<table width="100%" id="tabelaInterna">
															<c:forEach items="${artigoCache.assuntosFormatados}" var="assunto">
															<tr>
																<td>
																	${assunto}
																</td>
															</tr>
															</c:forEach>
														</table>
													</td>
										  		</tr>
										  		
										  		<tr>
													<th width="20%">Local de Publica��o:</th>
													<td width="80%">
														<table width="100%" id="tabelaInterna">
															<c:forEach items="${artigoCache.locaisPublicacaoFormatados}" var="local">
																<tr>
																	<td>
																		${local}
																	</td>
																</tr>
															</c:forEach>
														</table>
													</td>	
												</tr>
												
												<tr>
													<th width="20%">Editora:</th>
													<td width="80%">
														<table width="100%" id="tabelaInterna">
															<c:forEach items="${artigoCache.editorasFormatadas}" var="editora">
																<tr>
																	<td>
																		${editora}
																	</td>
																</tr>
															</c:forEach>
														</table>
													</td>	
												</tr>
												
												<tr>
													<th width="20%">Ano:</th>
													<td width="80%">
														<table width="100%" id="tabelaInterna">
															<c:forEach items="${artigoCache.anosFormatados}" var="ano">
																<tr>
																	<td>
																		${ano}
																	</td>
																</tr>
															</c:forEach>
														</table>
													</td>	
												</tr>
										  		
										  		<tr>
													<th style="vertical-align: top" width="23%">Resumo:</th>
													<td>
														${artigoCache.resumo}
													</td>	
												</tr>
												
										  	</table>
										  	
									  	</td>
									</tr>
							
								</c:forEach>
					
							</table>
					    
					    </rich:panel>
						
					</td>
				</tr>
			
			</c:if>
			
			<tr>
				<td colspan="14" style="width: 100%">
				
					
					<%--   O formul�rio para entrada dos dados do novo artigo de peri�dico--%>
				
					<table class="subFormulario" style="width: 100%">
						
						<caption>Informa��es do novo Artigo </caption>

						<tr>
							<th style="font-weight: normal;">T�tulo:<span class="obrigatorio">&nbsp;</span> </th>
							<td  colspan="2" style="padding-left: 6px;">
								<h:inputText value="#{catalogacaoArtigosMBean.titulo}" size="100" maxlength="400"/>
							</td>
						</tr>
			
						<tr>
							<th style="font-weight: normal;">Autor:<span class="obrigatorio">&nbsp;</span></th>
							<td colspan="2" style="padding-left: 6px;">
								<h:inputText value="#{catalogacaoArtigosMBean.autor}" size="100" maxlength="400"/>
							</td>
						</tr>
						
						<tr>
							<th style="font-weight: normal; vertical-align: top;">Autores secund�rios: &nbsp;&nbsp;&nbsp;</th>
							
							<td>	
								<a4j:outputPanel ajaxRendered="true">
									<t:dataTable var="autorSecundario" rowIndexVar="i"
											id="dtTblAutoresSecundarios" value="#{catalogacaoArtigosMBean.autoresSecundariosDataModel}">
										<h:column>
											<h:inputText value="#{catalogacaoArtigosMBean.autoresSecundarios[i]}" size="100" maxlength="400" />
										</h:column>
										<h:column rendered="#{catalogacaoArtigosMBean.autoresSecundariosDataModel.rowCount > 1}" >
											<a4j:commandLink
													actionListener="#{catalogacaoArtigosMBean.removerAutorSecundario}"
													reRender="dtTblAutoresSecundarios">
												<h:graphicImage url="/img/delete.gif" style="border: none;"
														title="Clique aqui para remover este autor secund�rio." />
											</a4j:commandLink>
										</h:column>
									</t:dataTable>
								</a4j:outputPanel>
							</td>
							
							<td style="vertical-align: top;" >
								<a4j:commandLink
										actionListener="#{catalogacaoArtigosMBean.adicionarAutorSecundario}"
										reRender="dtTblAutoresSecundarios">
									<h:graphicImage url="/img/adicionar.gif" style="border:none"
											title="Clique aqui para adicionar um autor secund�rio." />
								</a4j:commandLink>
							</td>
						</tr>
						<tr>
							<th style="font-weight: normal;">Intervalo de P�ginas:<span class="obrigatorio">&nbsp;</span></th>
							<td colspan="2" style="padding-left: 6px;">
								<h:inputText value="#{catalogacaoArtigosMBean.intervaloPaginas}" size="30" maxlength="100"/>
								<ufrn:help> Exemplos de preenchimento: p10-p20, p�gina 10 � p�gina 20 </ufrn:help>
							</td>
						</tr>
						
						<tr>
							<th style="vertical-align: top; font-weight: normal;">Palavras-Chave:<span class="obrigatorio">&nbsp;</span> </th>
							<td style="width: 40%">
								<a4j:outputPanel ajaxRendered="true">
									<t:dataTable id="dtTblPalavrasChaves" value="#{catalogacaoArtigosMBean.palavrasChaveDataModel}" var="palavraChave" rowIndexVar="linha" style="width:100%; padding-left: 0px; ">
										<h:column>
											<h:inputText value="#{catalogacaoArtigosMBean.palavrasChaves[linha]}" size="40" maxlength="400"/>
										</h:column>
										<h:column rendered="#{catalogacaoArtigosMBean.palavrasChaveDataModel.rowCount > 1}">
											<a4j:commandLink reRender="dtTblPalavrasChaves" actionListener="#{catalogacaoArtigosMBean.removerPalavraChave}">
												<h:graphicImage url="/img/delete.gif" style="border:none" title="Clique aqui para remover essa palavra-chave" />
											</a4j:commandLink>
										</h:column>
									</t:dataTable>	
								</a4j:outputPanel>
								
							</td>
							<td style="vertical-align: top; font-weight: normal;">
								<a4j:commandLink reRender="dtTblPalavrasChaves" actionListener="#{catalogacaoArtigosMBean.adicionarPalavraChave}">
									<h:graphicImage url="/img/adicionar.gif" style="border:none" title="Clique aqui para adicionar uma palavra-chave" />
								</a4j:commandLink>
							</td>
						</tr>
						
						<tr>
							<th style="font-weight: normal;">Local de Publica��o: &nbsp;&nbsp;&nbsp;</th>
							<td  colspan="2" style="padding-left: 6px;">
								<h:inputText value="#{catalogacaoArtigosMBean.localPublicacao}" size="50" maxlength="100"/>
							</td>
						</tr>
						
						<tr>
							<th style="font-weight: normal;">Editora: &nbsp;&nbsp;&nbsp;</th>
							<td  colspan="2" style="padding-left: 6px;">
								<h:inputText value="#{catalogacaoArtigosMBean.editora}" size="40" maxlength="100"/>
							</td>
						</tr>
						
						<tr>
							<th style="font-weight: normal;">Ano: &nbsp;&nbsp;&nbsp;</th>
							<td  colspan="2" style="padding-left: 6px;">
								<h:inputText value="#{catalogacaoArtigosMBean.ano}" size="10" maxlength="50"/>
							</td>
						</tr>
						
						<tr>
							<th style="font-weight: normal; vertical-align: top; ">Resumo: &nbsp;&nbsp;&nbsp;</th>
							<td  colspan="2" style="padding-left: 6px;">
								<h:inputTextarea value="#{catalogacaoArtigosMBean.resumo}" cols="70" rows="20" />
							</td>
						</tr>
							
					</table>
					
				</td>
			</tr>
			 
		</table>
			
	
		<table class="formulario" width="100%">
			<tfoot>
				<tr>
			    	<td colspan="3"> 
			    	
			    		<c:if test="${catalogacaoArtigosMBean.operacaoCatalogaArtigo}">
			    			<h:commandButton value="Salvar" action="#{catalogacaoArtigosMBean.salvarArtigo}">
			    				<f:setPropertyActionListener target="#{catalogacaoArtigosMBean.finalizarCatalogacao}" value="false" />
			    			</h:commandButton>
			    			
			    			<h:commandButton value="Finalizar" action="#{catalogacaoArtigosMBean.salvarArtigo}">
			    				<f:setPropertyActionListener target="#{catalogacaoArtigosMBean.finalizarCatalogacao}" value="true" />
			    			</h:commandButton>
			    			
			    		</c:if>
			    		
			    		<c:if test="${catalogacaoArtigosMBean.operacaoAtualizaArtigo}">
			    			<h:commandButton value="Atualizar" action="#{catalogacaoArtigosMBean.atualizarArtigo}" >
			    				<f:setPropertyActionListener target="#{catalogacaoArtigosMBean.finalizarAtualizacao}" value="false" />
			    			</h:commandButton>
			    			
			    			<%-- Tamb�m salva o Artigo, mas volta para a p�gina de onde foi chamado --%>
							<h:commandButton value="Finalizar Atualiza��o" action="#{catalogacaoArtigosMBean.atualizarArtigo}">
								<f:setPropertyActionListener target="#{catalogacaoArtigosMBean.finalizarAtualizacao}" value="true" />
							</h:commandButton>
			    			
			    		</c:if>
			    		
			    		<h:commandButton value="<< Voltar" action="#{catalogacaoArtigosMBean.voltarPagina}" /> 
			    		
			    	 	<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{catalogacaoArtigosMBean.cancelar}" /> 
			    	</td>
				</tr>
			</tfoot> 
		</table>
	
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
		
	
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>