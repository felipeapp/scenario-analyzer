<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>
<%-- Página que mostra as informações dos exemplares ou fascículos de um título na busca pública do sistema  --%>

<style type="text/css">
	#tabelaInterna tbody{
		background-color: transparent;
	}
	
	table.subFormulario tr.biblioteca td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	
	table.subFormulario tr.ano td{
		background: #828282;
		font-weight: bold;
		color: white;
		padding-left: 40px;
	}
	
	/** classe para o botão de paginação quando não está selecionado  **/
	.button_pagination {
	    background: -moz-linear-gradient(center top , #FFFFFF, #EFEFEF) repeat scroll 0 0 #F6F6F6;
	    border: 1px solid #CCCCCC;
	    border-radius: 3px 3px 3px 3px;
	    height: 2.0833em;
	    overflow: visible;
	    padding: 0 0.5em;
	    vertical-align: middle;
	    white-space: nowrap;
	    font-weight:  bolder;
	    font-size: 12px; 
	}
	
</style>

<h2>  <ufrn:subSistema /> &gt; Materiais Informacionais de um Título</h2>




<f:view>

	

	<a4j:keepAlive beanName="detalhesMateriaisDeUmTituloInternoMBean"></a4j:keepAlive>
	
	
	<%-- Se algum bean chamar a busca interna da biblitoeca e desejar manter suas informações salvas, declare o aqui --%>
	<a4j:keepAlive beanName="pesquisaInternaBibliotecaMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="planoCurso" />
	<a4j:keepAlive beanName="solicitarReservaMaterialBibliotecaMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="visualizarReservasMaterialBibliotecaMBean"></a4j:keepAlive>
	<%--  Fim da lista de MBeans cujos valores devem ser mantidos  --%>


	<h:form id="formDetalhesMateriaisInterno">


		<p:resources />

		<link rel="stylesheet" type="text/css" href="/sigaa/css/primefaces_skin.css" />

		<%-- Modal panel com os detalhes de cada material selecionado pelo usuário --%>
	
		<a4j:outputPanel ajaxRendered="true" id="painelInfoCompletaMaterial">
			<c:set var="_material_selecionado" value="${detalhesMateriaisDeUmTituloInternoMBean.materialSelecionado}" scope="request" />
			<c:set var="_artigos_do_fasciculo_selecionado" value="${detalhesMateriaisDeUmTituloInternoMBean.artigosDoFasciculoSelecionado}" scope="request" />
			<c:set var="_reservas_do_material_selecionado" value="${detalhesMateriaisDeUmTituloInternoMBean.reservasDoMaterial}" scope="request" />
			<c:set var="_qtd_emprestimos_materail_selecionado" value="${detalhesMateriaisDeUmTituloInternoMBean.qtdEmprestimosMaterialSelecionado}" scope="request" />
			<c:set var="_is_fasciculo" value="${detalhesMateriaisDeUmTituloInternoMBean.periodico}" scope="request" />
			<c:set var="_assinatura_do_material" value="${detalhesMateriaisDeUmTituloInternoMBean.asssinaturaDoMaterial}" scope="request" />
			<%@include file="/public/biblioteca/paginaPadraoDetalhesMaterial.jsp"%>
		</a4j:outputPanel>


		<div> 
	
	
	
			<c:if test="${ detalhesMateriaisDeUmTituloInternoMBean.obj.id == 0 }">
				
				<table class="visualizacao"> 
					<caption> Não foi possível visualizar as informações dos Materiais do Título</caption>
					<tr>
						<th style="text-align: center;">Volte à tela de pesquisa usando o botão do sistema, não utilize o botão do navegador, e realize uma nova busca.</th>
					</tr>
				</table>
				
			</c:if>
	
	
	
	
			<c:if test="${ detalhesMateriaisDeUmTituloInternoMBean.obj.id > 0 }">


				<%-- botões de navegação no resultado da pesquisa  --%>
					
					
				<div style="width: 100%; text-align: center; margin-top: 10px; margin-bottom: 20px;">
				
					<h:commandLink style="padding-right: 15px;" 
							value=" << Primeiro Registro" styleClass="button_pagination"
							rendered="#{! detalhesMateriaisDeUmTituloInternoMBean.podeVoltarResultadosPesquisa}" disabled="true"> 
					</h:commandLink> &nbsp;&nbsp;&nbsp;
					
					<h:commandLink action="#{detalhesMateriaisDeUmTituloInternoMBean.irPrimeiraPosicao}"
							value=" << Primeiro Registro" styleClass="button_pagination"
							rendered="#{detalhesMateriaisDeUmTituloInternoMBean.podeVoltarResultadosPesquisa}" style="padding-right: 15px;" > 
					</h:commandLink> &nbsp;&nbsp;&nbsp;
					
					<h:commandLink action="#{detalhesMateriaisDeUmTituloInternoMBean.irResultadoAnterior}" 
							value=" < Registro Anterior" styleClass="button_pagination"
							rendered="#{detalhesMateriaisDeUmTituloInternoMBean.podeVoltarResultadosPesquisa}"
							style="padding-right: 15px;"> 
					</h:commandLink> &nbsp;&nbsp;&nbsp;
					
					<h:commandLink style="padding-right: 15px;" disabled="true"
							value=" < Registro Anterior" styleClass="button_pagination"
							rendered="#{! detalhesMateriaisDeUmTituloInternoMBean.podeVoltarResultadosPesquisa}"> 
					</h:commandLink> &nbsp;&nbsp;&nbsp;
					
					
					
					<h:commandLink action="#{detalhesMateriaisDeUmTituloInternoMBean.irProximoResultado}" 
							value="Próximo Registro > " styleClass="button_pagination"
							rendered="#{detalhesMateriaisDeUmTituloInternoMBean.podeAvancarResultadosPesquisa}" style="padding-right: 15px;"> 
					</h:commandLink> &nbsp;&nbsp;&nbsp;
					
					<h:commandLink 
						value="Próximo Registro > " styleClass="button_pagination"
						rendered="#{! detalhesMateriaisDeUmTituloInternoMBean.podeAvancarResultadosPesquisa}" disabled="true" style="padding-right: 15px;"> 
					</h:commandLink> &nbsp;&nbsp;&nbsp;
					
					<h:commandLink action="#{detalhesMateriaisDeUmTituloInternoMBean.irUtimaPosicao}"
							value="Último Registro >> " styleClass="button_pagination"
							rendered="#{detalhesMateriaisDeUmTituloInternoMBean.podeAvancarResultadosPesquisa}"> 
					</h:commandLink> &nbsp;&nbsp;&nbsp;
					
					<h:commandLink 
							value="Último Registro >> " styleClass="button_pagination"
							rendered="#{! detalhesMateriaisDeUmTituloInternoMBean.podeAvancarResultadosPesquisa}" disabled="true"> 
					</h:commandLink> &nbsp;&nbsp;&nbsp;
					
				</div>
				

				<c:set var="_titulo" value="${detalhesMateriaisDeUmTituloInternoMBean.tituloCache}"  scope="request"/>
				<%@include file="/public/biblioteca/informacoes_padrao_titulo.jsp"%>
				
				<c:if test="${detalhesMateriaisDeUmTituloInternoMBean.periodico}">
					<c:set var="_assinaturas" value="${detalhesMateriaisDeUmTituloInternoMBean.assinaturasTitulo}" scope="request"/>
					<%@include file="/public/biblioteca/informacoes_padrao_assinaturas.jsp"%>
				</c:if>
					
				
				
				<%-- Navegação entre os materiais retornados na consulta  --%>
    
				<div style="width: 100%; text-align: center; margin-top: 10px; margin-bottom: 20px;">
						
					<h:commandLink value="<<" actionListener="#{detalhesMateriaisDeUmTituloInternoMBean.atualizaDadosPagina}" 
							styleClass="button_pagination" 
							rendered="#{detalhesMateriaisDeUmTituloInternoMBean.quantidadePaginas > 1}"  disabled="#{detalhesMateriaisDeUmTituloInternoMBean.paginaAtual == 1}">
							<f:param name="_numero_pagina_atual" value="1"/>
					</h:commandLink> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
						
					<h:commandLink value="<" actionListener="#{detalhesMateriaisDeUmTituloInternoMBean.atualizaDadosPagina}" 
							styleClass="button_pagination" 
							rendered="#{detalhesMateriaisDeUmTituloInternoMBean.quantidadePaginas > 1}" disabled="#{detalhesMateriaisDeUmTituloInternoMBean.paginaAtual == 1}">
							<f:param name="_numero_pagina_atual" value="#{detalhesMateriaisDeUmTituloInternoMBean.paginaAtual -1}"/>
					</h:commandLink>	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				
					<c:forEach var="pagina" items="#{detalhesMateriaisDeUmTituloInternoMBean.paginas}" >
						<h:commandLink value="#{pagina}" actionListener="#{detalhesMateriaisDeUmTituloInternoMBean.atualizaDadosPagina}" 
							styleClass="button_pagination" 
							disabled="#{detalhesMateriaisDeUmTituloInternoMBean.paginaAtual == pagina}">
							<f:param name="_numero_pagina_atual" value="#{pagina}"/>
						</h:commandLink>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					</c:forEach>
					
					<h:commandLink value=">" actionListener="#{detalhesMateriaisDeUmTituloInternoMBean.atualizaDadosPagina}"  
							styleClass="button_pagination" 
							rendered="#{detalhesMateriaisDeUmTituloInternoMBean.quantidadePaginas > 1}" disabled="#{detalhesMateriaisDeUmTituloInternoMBean.paginaAtual == detalhesMateriaisDeUmTituloInternoMBean.quantidadePaginas}">
							<f:param name="_numero_pagina_atual" value="#{detalhesMateriaisDeUmTituloInternoMBean.paginaAtual +1}"/>
					</h:commandLink> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					
					<h:commandLink value=">>" actionListener="#{detalhesMateriaisDeUmTituloInternoMBean.atualizaDadosPagina}" 
							styleClass="button_pagination" 
							rendered="#{detalhesMateriaisDeUmTituloInternoMBean.quantidadePaginas > 1}" disabled="#{detalhesMateriaisDeUmTituloInternoMBean.paginaAtual == detalhesMateriaisDeUmTituloInternoMBean.quantidadePaginas}">
							<f:param name="_numero_pagina_atual" value="#{detalhesMateriaisDeUmTituloInternoMBean.quantidadePaginas}" />
					</h:commandLink>	
					
				</div>
				
					
				<table class="visualizacao">
					<c:if test="${detalhesMateriaisDeUmTituloInternoMBean.periodico}"> 
						
						<tr>
							<td colspan="2" style="height: 10px;background-color: #FFFFFF"></td>
						</tr>
					 
						<tr>
							<th style="text-align: right; background-color: white;"> Escolha a Biblioteca dos Fascículos:</th>
							<td style="background-color: transparent;" >
								<h:selectOneMenu id="comboBoxBibliotecasDosFasciculos" value="#{detalhesMateriaisDeUmTituloInternoMBean.idBibliotecaMateriais}"
										valueChangeListener="#{detalhesMateriaisDeUmTituloInternoMBean.verificaAlteracaoFiltroBiblioteca}" onchange="submit();">
									<f:selectItem itemLabel="-- TODAS --" itemValue="-2" />
									<f:selectItems value="#{detalhesMateriaisDeUmTituloInternoMBean.bibliotecasInternas}"/>
								</h:selectOneMenu>
							</td>
						</tr>
					
						<tr>
							<th style="text-align: right; background-color: white;"> Ano:</th>
							<td colspan="3">
								<h:selectOneMenu id="comboBoxAnoCronologicoDosFasciculos" value="#{detalhesMateriaisDeUmTituloInternoMBean.anoPesquisaFasciculos}"
										valueChangeListener="#{detalhesMateriaisDeUmTituloInternoMBean.verificaAlteracaoFiltroAno}" onchange="submit();">
									<f:selectItems value="#{detalhesMateriaisDeUmTituloInternoMBean.anosPesquisaFasciculos}"/>
								</h:selectOneMenu>
							</td>
						</tr>
					
					</c:if>
					
					
					
					
							
					<%-- informações de exemplares --%>	
						
					<c:if test="${! detalhesMateriaisDeUmTituloInternoMBean.periodico}"> 
					
						<tr>
							<td colspan="4">
								
									<table class="subFormulario" width="100%"> 
									
										<caption style="text-align: center;"> 
										
										Exemplar(es) <h:outputText value="#{detalhesMateriaisDeUmTituloInternoMBean.primeiroResultadoPagina}"/>  
										a <h:outputText value="#{detalhesMateriaisDeUmTituloInternoMBean.ultimoResultadoPagina}"/> 
										de <h:outputText value="#{detalhesMateriaisDeUmTituloInternoMBean.quantidadeTotalResultados}"/>
										
										</caption>
										
										<thead>
											<tr>
											<th colspan="3" style="text-align: right; background-color: transparent;"> Escolha a Biblioteca:</th>
											<td colspan="4">
													<h:selectOneMenu id="comboBoxBibliotecasExemplares" value="#{detalhesMateriaisDeUmTituloInternoMBean.idBibliotecaMateriais}"
															valueChangeListener="#{detalhesMateriaisDeUmTituloInternoMBean.verificaAlteracaoFiltroBiblioteca}" onchange="submit();">
														<f:selectItem itemLabel="-- TODAS --" itemValue="-2" />
														<f:selectItems value="#{detalhesMateriaisDeUmTituloInternoMBean.bibliotecasInternas}"/>
													</h:selectOneMenu>
												</td>
											</tr>
										
										</thead>
										
										
										<thead>
											<tr align="center">
												<th style="text-align: left; width: 15%;"> Código de Barras</th>
												<th style="text-align: left; width: 15%;"> Tipo de Material</th>
												<th style="text-align: left; width: 15%;"> Coleção</th>
												<th style="text-align: left; width: 15%;"> Status</th>				
												<th style="text-align: left; width: 40%;"> Situação</th>
											</tr>
										</thead>
											
										<tbody>
										
											<c:set var="idFiltroBibliotecaPublico" value="-1" />
											<c:forEach var="exemplar" items="#{detalhesMateriaisDeUmTituloInternoMBean.exemplaresPaginados}" varStatus="status">
												
												<c:if test="${ idFiltroBibliotecaPublico != exemplar.biblioteca.id}">
													<c:set var="idFiltroBibliotecaPublico" value="${exemplar.biblioteca.id}" />
													<tr class="biblioteca">
														<td colspan="8">${exemplar.biblioteca.descricao}</td>
													</tr>
												</c:if>
												
												<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
													
													<td> ${exemplar.codigoBarras} <c:if test="${exemplar.anexo}"> <span style="font-style: italic;">(anexo)</span> </c:if> </td>
													
													<td> ${exemplar.tipoMaterial.descricao}</td>
													
													<td> ${exemplar.colecao.descricao}</td>
													
													<td> ${exemplar.status.descricao} </td>
													
													<c:if test="${exemplar.disponivel}"> 
														<td style="color:green"> ${exemplar.situacao.descricao}
															<c:if test="${not empty exemplar.prazoConcluirReserva}"> 
																&nbsp&nbsp&nbsp [Reservado, previsão conclusão: <ufrn:format type="data" valor="${exemplar.prazoConcluirReserva}"/> ]
															</c:if>
														</td>
													</c:if>
													<c:if test="${! exemplar.disponivel && ! exemplar.emprestado}"> 
														<td> ${exemplar.situacao.descricao}</td>
													</c:if>
													<c:if test="${exemplar.emprestado}"> 
														<td style="color:red"> 
														     ${exemplar.situacao.descricao}	 &nbsp&nbsp&nbsp [ Prazo de Devolução: <ufrn:format type="dataHora" valor="${exemplar.prazoEmprestimo}"/> ]										    													
														 </td>
													</c:if>
												</tr>
												
												
												<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
													<td colspan="1" style="font-weight: bold; color: #CD853F;"> Localização: </td>
													<td colspan="1" style="color: #CD853F;">  ${exemplar.numeroChamada} </td>
													<td colspan="3" style="font-style: italic; color: #CD853F;"> 
													    <c:if test="${ not empty exemplar.segundaLocalizacao}">  &nbsp&nbsp&nbsp ${exemplar.segundaLocalizacao} </c:if>
													 </td>
												</tr>
												
												
												<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
													<td colspan="7" style="text-align: center;">
															<a4j:commandLink   value="Mostrar Detalhes "  actionListener="#{detalhesMateriaisDeUmTituloInternoMBean.carregarDetalhesMaterialSelecionado}"
														 				ajaxSingle="true" oncomplete="modelPanelDetalhes.show();" style="font-weight: normal; font-style: italic; " 
														 				reRender="formDetalhesMateriaisInterno">
													 			<f:param name="idMaterialMostrarDetalhes" value="#{exemplar.id}"/>
														 	</a4j:commandLink>
													</td>
												</tr>
												
											</c:forEach>
							
										</tbody>
							
									</table>
							</td>
						</tr>
					
					</c:if>  <%-- Fim dos dados dos exemplares --%>
						
						
						
						
						
						
					<%-- Dados dos fascículos  --%>
					
					<c:if test="${detalhesMateriaisDeUmTituloInternoMBean.periodico}">  
					
						<tr>
							<td colspan="2">
								
								<table class="subFormulario" style="width: 100%; " > 
									<caption style="text-align: center;"> 
									
									Fascículo(s) <h:outputText value="#{detalhesMateriaisDeUmTituloInternoMBean.primeiroResultadoPagina}"/>  
									a <h:outputText value="#{detalhesMateriaisDeUmTituloInternoMBean.ultimoResultadoPagina}"/> 
									de <h:outputText value="#{detalhesMateriaisDeUmTituloInternoMBean.quantidadeTotalResultados}"/>				
									
									</caption>

									<c:if test="${fn:length( detalhesMateriaisDeUmTituloInternoMBean.fasciculosPaginados) > 0}">
									
										<thead>
											<tr align="center">
												<th style="text-align: left; width: 10%;">Código de Barras</th>
												<th style="text-align: right; width: 10%;"> Ano </th>
												<th style="text-align: right; width: 10%;"> Volume </th>
												<th style="text-align: right; width: 10%;"> Número </th>
												<th style="text-align: right; width: 10%;"> Edição </th>		
												<th style="text-align: left; width: 15%;">Tipo de Material</th>
												<th style="text-align: left; width: 15%;">Coleção</th>
												<th style="text-align: left; width: 10%;">Status</th>
												<th style="text-align: left; width: 10%;">Situação</th>
											</tr>
										</thead>
										
										<tbody>
							   
							   				<c:set var="idFiltroBiblioteca" value="-1" />
				   							<c:set var="idFiltroAno" value="-1" />
											<c:forEach var="fasciculo" items="#{detalhesMateriaisDeUmTituloInternoMBean.fasciculosPaginados}" varStatus="status">
							
												<c:if test="${ idFiltroBiblioteca != fasciculo.biblioteca.id}">
													<c:set var="idFiltroBiblioteca" value="${fasciculo.biblioteca.id}" />
													<tr class="biblioteca">
														<td colspan="12">${fasciculo.biblioteca.descricao}</td>
													</tr>
												</c:if>
												
												<c:if test="${ idFiltroAno != fasciculo.anoCronologico}">
													<c:set var="idFiltroAno" value="${fasciculo.anoCronologico}" />
													<tr class="ano">
														<td colspan="12">Ano: ${fasciculo.anoCronologico}</td>
													</tr>
												</c:if>
							
												<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
													<td> ${fasciculo.codigoBarras} <c:if test="${fasciculo.suplemento}"> <span style="font-style: italic;">(suplemento)</span> </c:if> </td>
													
													<td style="text-align: right;"> ${fasciculo.ano} </td>
													
													<td style="text-align: right;"> ${fasciculo.volume} </td>
													
													<td style="text-align: right;"> ${fasciculo.numero} </td>
													
													<td style="text-align: right;"> ${fasciculo.edicao} </td>
													
													<td> ${fasciculo.tipoMaterial.descricao}</td>
													
													<td> ${fasciculo.colecao.descricao}</td>
													
													<td> ${fasciculo.status.descricao}</td>
													
													<c:if test="${fasciculo.disponivel}"> 
														<td style="color:green"> ${fasciculo.situacao.descricao}</td>
													</c:if>
													<c:if test="${! fasciculo.disponivel && ! fasciculo.emprestado}"> 
														<td> ${fasciculo.situacao.descricao}</td>
													</c:if>
													<c:if test="${fasciculo.emprestado}"> 
														<td style="color:red"> ${fasciculo.situacao.descricao}</td>
													</c:if>
													
												</tr>
												
												<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
													<td colspan="1" style="color:#CD853F; font-weight: bold;"> Localização: </td>
													<td colspan="1" style="color:#CD853F;">  ${fasciculo.numeroChamada}</td>
													<td colspan="7" style="font-style: italic; color:#CD853F;"> 
													 	&nbsp;&nbsp;&nbsp; ${fasciculo.segundaLocalizacao}						    													
													 </td>
												</tr>
												
												<c:if test="${fasciculo.disponivel && not empty fasciculo.prazoConcluirReserva}"> 
													<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
														<td colspan="5"> </td>
														<td colspan="4" style="text-align: right; color: green;"> 
														    [Reservado, previsão conclusão: <ufrn:format type="data" valor="${fasciculo.prazoConcluirReserva}"/> ]								    													
														 </td>
													</tr>
												</c:if>
												
												
												<c:if test="${fasciculo.emprestado}"> 
													<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
														<td colspan="5"> </td>
														<td colspan="4" style="text-align: right; color: red;"> 
														    [Prazo de Devolução: <ufrn:format type="dataHora" valor="${fasciculo.prazoEmprestimo}"/>]											    													
														 </td>
													</tr>
												</c:if>
												
												
												<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
													<td colspan="9" style="text-align: center;">
															<a4j:commandLink   value="Mostrar Detalhes "  actionListener="#{detalhesMateriaisDeUmTituloInternoMBean.carregarDetalhesMaterialSelecionado}"
														 				ajaxSingle="true" oncomplete="modelPanelDetalhes.show();" style="font-weight: normal; font-style: italic; " 
														 				reRender="formDetalhesMateriaisInterno">
													 			<f:param name="idMaterialMostrarDetalhes" value="#{fasciculo.id}"/>
														 	</a4j:commandLink>
													</td>
												</tr>
												
											</c:forEach>
							
										</tbody>
									
									</c:if> <%-- Se quantidade Fascículos > 0--%>
						
								</table>
					
							</td>
							
						</tr>
					
					</c:if>   <%-- Fim dos dados dos fascículos  --%>	
						
			</table>
					
				
	
			</c:if>  <%-- Fim do titulo.id > 0 --%>
	
		</div>
	
	
		<div style="width: 100%; text-align:center; margin-top:20px">
				<%-- History.go não vai funcionar por causa que o usuário pode navegar entre as catalogações
				  <input type="button" id="linkVotarTelaAnterior" value="<< Voltar à Tela de Busca" onclick="javascript:history.go(-1)" /> --%>
				<h:commandButton id="cmdVotarTelaAnterior" value=" << Voltar à Tela de Busca"  action="#{detalhesMateriaisDeUmTituloInternoMBean.voltar}" />
		</div>

	</h:form>
	
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>