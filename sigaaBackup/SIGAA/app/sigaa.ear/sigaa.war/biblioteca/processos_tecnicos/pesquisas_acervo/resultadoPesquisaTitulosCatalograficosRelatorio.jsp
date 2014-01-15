<%@include file="/WEB-INF/jsp/include/cabecalho_impressao_paisagem.jsp"%>

<f:view>
	
	<div id="parametrosRelatorio" style="margin-bottom:10px;">
		<table>

			<c:if test="${sessionScope.abaPesquisa == 'buscaMultiCampo'}">
			
				<c:if test="${not empty pesquisaTituloCatalograficoMBean.numeroDoSistema && pesquisaTituloCatalograficoMBean.buscarNumeroSistema}">
					<tr>	<th style="white-space: nowrap;">Número do Sistema:</th>
					<td><h:outputText value="#{pesquisaTituloCatalograficoMBean.numeroDoSistema}" /></td></tr>
				</c:if>
				<c:if test="${not empty pesquisaTituloCatalograficoMBean.titulo && pesquisaTituloCatalograficoMBean.buscarTitulo}">
					<tr>	<th style="white-space: nowrap;">Título:</th>
					<td><h:outputText value="#{pesquisaTituloCatalograficoMBean.titulo}" /></td></tr>
				</c:if>
				
				<c:if test="${not empty pesquisaTituloCatalograficoMBean.assunto && pesquisaTituloCatalograficoMBean.buscarAssunto}">
					<tr>	<th style="white-space: nowrap;">Assunto: </th>
					<td><h:outputText value="#{pesquisaTituloCatalograficoMBean.assunto}" /></td></tr>
				</c:if>
				
				<c:if test="${not empty pesquisaTituloCatalograficoMBean.autor && pesquisaTituloCatalograficoMBean.buscarAutor}">
					<tr>	<th style="white-space: nowrap;">Autor:</th>
					<td><h:outputText value="#{pesquisaTituloCatalograficoMBean.autor}" /></td></tr>
				</c:if>
				
				<c:if test="${not empty pesquisaTituloCatalograficoMBean.localPublicacao && pesquisaTituloCatalograficoMBean.buscarLocalPublicacao}">
					<tr>	<th style="white-space: nowrap;">Local de Publicação: </th>
					<td><h:outputText value="#{pesquisaTituloCatalograficoMBean.localPublicacao}" /></td></tr>
				</c:if>
				
				<c:if test="${not empty pesquisaTituloCatalograficoMBean.editora && pesquisaTituloCatalograficoMBean.buscarEditora}">
					<tr>	<th style="white-space: nowrap;">Editora: </th>
					<td><h:outputText value="#{pesquisaTituloCatalograficoMBean.editora}" /></td></tr>
				</c:if>
				
				<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao1}">
					<c:if test="${not empty pesquisaTituloCatalograficoMBean.classificacao1 && pesquisaTituloCatalograficoMBean.buscarClassificacao1}">
						<tr> <th style="white-space: nowrap;">${classificacaoBibliograficaMBean.descricaoClassificacao1}: </th>
						<td> <h:outputText value="#{pesquisaTituloCatalograficoMBean.classificacao1}" /></td></tr>
					</c:if>
				</c:if>
				
				<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao2}">
					<c:if test="${not empty pesquisaTituloCatalograficoMBean.classificacao2 && pesquisaTituloCatalograficoMBean.buscarClassificacao2}">
						<tr> <th style="white-space: nowrap;">${classificacaoBibliograficaMBean.descricaoClassificacao2}: </th>
						<td> <h:outputText value="#{pesquisaTituloCatalograficoMBean.classificacao2}" /></td></tr>
					</c:if>
				</c:if>
				
				<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao3}">
					<c:if test="${not empty pesquisaTituloCatalograficoMBean.classificacao3 && pesquisaTituloCatalograficoMBean.buscarClassificacao3}">
						<tr> <th style="white-space: nowrap;">${classificacaoBibliograficaMBean.descricaoClassificacao3}: </th>
						<td> <h:outputText value="#{pesquisaTituloCatalograficoMBean.classificacao3}" /></td></tr>
					</c:if>
				</c:if>
				
				<c:if test="${ (not empty pesquisaTituloCatalograficoMBean.anoInicial ||  not empty pesquisaTituloCatalograficoMBean.anoFinal ) && pesquisaTituloCatalograficoMBean.buscarAno}">
					<tr>	<th style="white-space: nowrap;">Ano publicação de: </th>
					<td><h:outputText value="#{pesquisaTituloCatalograficoMBean.anoInicial}" /> a <h:outputText value="#{pesquisaTituloCatalograficoMBean.anoFinal}" /> </td></tr>
				</c:if>
				
				<c:if test="${not empty pesquisaTituloCatalograficoMBean.descricaoBibliotecaBuscaMultiCampo && pesquisaTituloCatalograficoMBean.buscarBiblioteca}">
					<tr>	<th style="white-space: nowrap;">Biblioteca: &nbsp&nbsp</th>
					<td><h:outputText value="#{pesquisaTituloCatalograficoMBean.descricaoBibliotecaBuscaMultiCampo}" /></td></tr>
				</c:if>
				
				<c:if test="${not empty pesquisaTituloCatalograficoMBean.descricaoColecaoBuscaMultiCampo && pesquisaTituloCatalograficoMBean.buscarColecao}">
					<tr>	<th style="white-space: nowrap;">Coleção: &nbsp&nbsp</th>
					<td><h:outputText value="#{pesquisaTituloCatalograficoMBean.descricaoColecaoBuscaMultiCampo}" /></td></tr>
				</c:if>
				
				<c:if test="${not empty pesquisaTituloCatalograficoMBean.descricaoTipoMaterialBuscaMultiCampo && pesquisaTituloCatalograficoMBean.buscarTipoMaterial}">
					<tr>	<th style="white-space: nowrap;">Tipo Material: &nbsp&nbsp</th>
					<td><h:outputText value="#{pesquisaTituloCatalograficoMBean.descricaoTipoMaterialBuscaMultiCampo}" /></td></tr>
				</c:if>
				
			</c:if>
			
			<c:if test="${sessionScope.abaPesquisa == 'buscaAvancada'}">
				
				<c:forEach items="#{ pesquisaTituloCatalograficoMBean.campos}" var="campoPesquisa" >
					<td>
						<c:if test="${not empty campoPesquisa}">
							${campoPesquisa}
						</c:if>
					</td>
				</c:forEach>
			
			</c:if>
			
			<c:if test="${sessionScope.abaPesquisa == 'buscaPorListas'}">
				<tr>	
					<th style="white-space: nowrap;"> ${ pesquisaTituloCatalograficoMBean.campoPesquisaPorLista.descricaoTipoCampoEscolhido} : </th>
					<td style="font-style: italic; "> <h:outputText value="#{pesquisaTituloCatalograficoMBean.campoPesquisaPorLista.valorCampo}" /> </td>
				</tr>
				
				<c:if test="${not empty pesquisaTituloCatalograficoMBean.descricaoBibliotecaBuscaMultiCampo && pesquisaTituloCatalograficoMBean.buscarBiblioteca}">
					<tr>	
						<th style="white-space: nowrap;">Biblioteca: &nbsp&nbsp</th>
						<td><h:outputText value="#{pesquisaTituloCatalograficoMBean.descricaoBibliotecaBuscaMultiCampo}" /></td>
					</tr>
				</c:if>
				
				<c:if test="${not empty pesquisaTituloCatalograficoMBean.descricaoColecaoBuscaMultiCampo && pesquisaTituloCatalograficoMBean.buscarColecao}">
					<tr>	
						<th style="white-space: nowrap;">Coleção: &nbsp&nbsp</th>
						<td>	<h:outputText value="#{pesquisaTituloCatalograficoMBean.descricaoColecaoBuscaMultiCampo}" /></td>
					</tr>
				</c:if>
				
				<c:if test="${not empty pesquisaTituloCatalograficoMBean.descricaoTipoMaterialBuscaMultiCampo && pesquisaTituloCatalograficoMBean.buscarTipoMaterial}">
					<tr>	
						<th style="white-space: nowrap;">Tipo Material: &nbsp&nbsp </th>
						<td><h:outputText value="#{pesquisaTituloCatalograficoMBean.descricaoTipoMaterialBuscaMultiCampo}" /></td>
					</tr>
				</c:if>
				
			</c:if>
			
		</table>
	</div>
	
	<c:if test="${pesquisaTituloCatalograficoMBean.quantidadeTotalResultados == 0}">
		<table class="listagem" style="width: 100%;">
			<tr>
				<th colspan="7" style="text-align: center;  background-color:#C0C0C0;">Títulos Encontrados (  <h:outputText value="#{pesquisaTituloCatalograficoMBean.quantidadeTotalResultados}"/> ) </th>
			</tr>
		</table>
	</c:if>
	
	<c:if test="${pesquisaTituloCatalograficoMBean.quantidadeTotalResultados > 0}">
	
		<table class="listagem" style="width: 100%;">
				
			<thead>
				<tr>
					
					<th colspan="7" style="text-align: center; background-color:#C0C0C0;">Títulos Encontrados (  <h:outputText value="#{pesquisaTituloCatalograficoMBean.quantidadeTotalResultados}"/> ) </th>
				
				</tr>
				<tr>
					<th style="width: 5%; text-align: right"> Nº Sistema </th>
					<c:if test="${sessionScope.abaPesquisa != 'buscaPorListas'}">
						<th style="width: 28%;"> Autor </th>
						<th style="width: 35%;"> Título </th>
						<th style="width: 6%; text-align: center"> Edição </th>
						<th style="width: 6%; padding-left: 5px;"> Ano </th>
						<th style="width: 17%;"> Nº Chamada </th>
					</c:if>
					
					<c:if test="${sessionScope.abaPesquisa == 'buscaPorListas'}">
						
						<th style="width: 92%;"> ${pesquisaTituloCatalograficoMBean.campoPesquisaPorLista.descricaoTipoCampoEscolhido} </th>
						
					</c:if>
					
					
					<th style="width: 1%; text-align: center">Qtd.  </th>
				</tr>
			</thead>
	
			<tbody>
	
				<c:forEach items="#{pesquisaTituloCatalograficoMBean.resultadosBuscados}" var="tituloCache" varStatus="status">
	
	
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
	
						<td style="${tituloCache.catalogado ? " " : "color:red"}; text-align: right" width="5%">
							${tituloCache.numeroDoSistema}
						</td>
						
						<c:if test="${sessionScope.abaPesquisa != 'buscaPorListas'}">
							<td style="${tituloCache.catalogado ? " " : "color:red"}" width="30%">
								${tituloCache.autor}
							</td>
							
							<td style="${tituloCache.catalogado ? " " : "color:red"}" width="30%">
								${tituloCache.titulo} ${tituloCache.subTitulo}
							</td>
							
							<td style="${tituloCache.catalogado ? " " : "color:red"}" width="7%">
								${tituloCache.edicao}
							</td>
														
							<td width="7%">
								<table width="100%">
									<tbody style="background-color: transparent;">
										<tr>
											<td style="${tituloCache.catalogado ? " " : "color:red"}">
												<c:forEach items="${tituloCache.anosFormatados}" var="ano">
													${ano}
												</c:forEach>
											</td>
										</tr>
									</tbody>
								</table>
							</td>
							
							<td style="${tituloCache.catalogado ? " " : "color:red"}" width="15%">
								${tituloCache.numeroChamada}
							</td>
						
						</c:if>
						
						<c:if test="${sessionScope.abaPesquisa == 'buscaPorListas'}">
								
							<td width="92%">
								<table width="100%">
									<tbody style="background-color: transparent;">
										<tr>
											<td style="${tituloCache.catalogado ? " " : "color:red"}">
												<c:forEach items="${tituloCache.campoMostrarUsuarioPesquisaLista}" var="campo">
													${campo}
												</c:forEach>
											</td>
										</tr>
									</tbody>
								</table>
							</td>
							
						</c:if>
						
						<td width="1%" style="text-align:right; ${tituloCache.catalogado ? " " : "color:red"}">
							${tituloCache.quantidadeMateriaisAtivosTitulo}
						</td>
						
					</tr>
	
				</c:forEach>
	
			</tbody>
				
		</table>
	
	</c:if>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>