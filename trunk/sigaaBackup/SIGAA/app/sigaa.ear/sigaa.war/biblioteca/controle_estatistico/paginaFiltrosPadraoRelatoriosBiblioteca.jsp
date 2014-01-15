<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	
	<a4j:keepAlive  beanName="_abstractRelatorioBiblioteca" />

	<h2 class="tituloPagina"> <ufrn:subSistema/> &gt; <h:outputText value="#{_abstractRelatorioBiblioteca.titulo}" /> </h2>

	<h:form id="form">
		<c:if test="${_abstractRelatorioBiblioteca.descricao != null}">
			<div class="descricaoOperacao">
				<p>${_abstractRelatorioBiblioteca.descricao}</p> <%-- <h:output> n�o � usado porque precisamos usar markup aqui. --%>
			</div>
		</c:if>

		<table class="formulario" style="width: 70%;">
			<caption class="formulario">Filtros do Relat�rio</caption>

			<tbody>
				<tr>
					<td>
						<table style="width: 100%;">
							<tr>
								<td style="width: 10px;"></td>
								<td>
									<table>
									
									<%-- F I L T R O     D E      B I B L I O T E C A S  --%>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorVariasBibliotecas}" >
										<tr>
											<th class="${_abstractRelatorioBiblioteca.campoBibliotecaObrigatorio? 'obrigatorio': ''}"> ${_abstractRelatorioBiblioteca.descricaoFiltro1Bibliotecas}:</th>
											<td style="width: 450px;">
												<table>
												<tr>
													<td>
														<h:selectManyListbox value="#{_abstractRelatorioBiblioteca.variasBibliotecas}" style="width:450px;"
																id="variasBibliotecas" size="10" title="V�rias bibliotecas">
															<f:selectItems value="#{_abstractRelatorioBiblioteca.bibliotecasCombo}"/>
														</h:selectManyListbox>
													</td>
													<td style="vertical-align: middle; text-align: left; width: 100%;">
														<ufrn:help>
															Mantenha a tecla <em>Ctrl</em> pressionada para selecionar ou desselecionar uma Biblioteca.<br/>
															Use a tecla <em>Shift</em> para selecionar um intervalo de Bibliotecas.
														</ufrn:help>
													</td>
												</tr>
												</table>
											</td>
										</tr>
									</c:if>
					
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorSegundaOpcaoVariasBiblioteca}" >
										<tr>
											<th class="${_abstractRelatorioBiblioteca.campo2BibliotecaObrigatorio? 'obrigatorio': ''}">${_abstractRelatorioBiblioteca.descricaoFiltro2Bibliotecas}:</th>
											<td style="width: 450px;">
												<table>
												<tr>
													<td>
														<h:selectManyListbox value="#{_abstractRelatorioBiblioteca.variasBibliotecas2}" style="width:450px;"
																id="variasBibliotecas2" size="10" title="V�rias bibliotecas">
															<f:selectItems value="#{_abstractRelatorioBiblioteca.bibliotecasCombo}"/>
														</h:selectManyListbox>
													</td>
													<td style="vertical-align: middle; text-align: left; width: 100%;">
														<ufrn:help>
															Mantenha a tecla <em>Ctrl</em> pressionada para selecionar ou desselecionar uma Biblioteca.<br/>
															Use a tecla <em>Shift</em> para selecionar um intervalo de Bibliotecas.
														</ufrn:help>
													</td>
												</tr>
												</table>
											</td>
										</tr>
									</c:if>
					
					
					
					
					
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorServidor}" >
										<tr>
											<th class="obrigatorio">Servidor:</th>
											<td>
											
												<h:inputHidden id="idServidor"   value="#{_abstractRelatorioBiblioteca.servidor}"/>
												<h:inputText   id="nomeServidor" value="#{_abstractRelatorioBiblioteca.nomeServidor}"
														size="70" onkeyup="CAPS(this);"/>
												<rich:suggestionbox id="suggestionNomeServidor"
														for="nomeServidor"
														var="_servidor" fetchValue="#{ _servidor.pessoa.nome }"
														suggestionAction="#{servidorAutoCompleteMBean.autocompleteNomeServidor}"
														width="450" height="100" minChars="5">
													
													<h:column>
														<h:outputText value="#{ _servidor.pessoa.nome }"/>
														( <h:outputText value="#{_servidor.siape }" /> )
													</h:column>
													
													<a4j:support event="onselect" reRender="idServidor">
														<f:setPropertyActionListener
															value="#{ _servidor.id }" target="#{_abstractRelatorioBiblioteca.servidor}"/>
													</a4j:support>
													
												</rich:suggestionbox>
												
												<span id="indicador" style="display:none; "> 
													<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
												</span>
											</td>
										</tr>
									</c:if>
					
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorPessoa}" >
										<tr>
											<th class="obrigatorio">Usu�rio:</th>
											<td>
												<h:inputHidden id="idPessoa"   value="#{_abstractRelatorioBiblioteca.pessoa}"/>
												<h:inputText   id="nomePessoa" value="#{_abstractRelatorioBiblioteca.nomePessoa}"
														size="64" onkeyup="CAPS(this);" />
												<rich:suggestionbox id="suggestionNomePessoa"
														for="nomePessoa"
														var="_pessoa" fetchValue="#{ _pessoa.nome }"
														suggestionAction="#{_abstractRelatorioBiblioteca.autocompleteNomePessoa}"
														width="400" height="100" minChars="5">
													
													<h:column>
														<h:outputText value="#{ _pessoa.nome }"/>
														( <h:outputText value="#{_pessoa.cpf_cnpj }" /> )
													</h:column>
													
													<a4j:support event="onselect" reRender="idPessoa">
														<f:setPropertyActionListener
															value="#{ _pessoa.id }" target="#{_abstractRelatorioBiblioteca.pessoa}"/>
													</a4j:support>
													
												</rich:suggestionbox>
												
												<span id="indicador" style="display:none; "> 
													<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
												</span>
											</td>
										</tr>
									</c:if>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorVariasPessoas}" >
										<tr>
											<th class="${_abstractRelatorioBiblioteca.campoPessoaObrigatorio? 'obrigatorio': ''}">Usu�rios:</th>
											<td style="width: 450px;">
												<div>
													<h:inputHidden id="idPessoa"   value="#{_abstractRelatorioBiblioteca.pessoa}"/>
													<h:inputText   id="nomePessoa" value="#{_abstractRelatorioBiblioteca.nomePessoa}"
															size="60" onkeyup="CAPS(this);" />
															
													<rich:suggestionbox id="suggestionNomePessoa"
														for="nomePessoa"
														var="_pessoa" fetchValue="#{ _pessoa.nome }"
														suggestionAction="#{_abstractRelatorioBiblioteca.autocompleteNomePessoa}"
														width="400" height="300" minChars="5">																																							
														<h:column>
															<h:outputText value="#{ _pessoa.nome }"/>
															( <h:outputText value="#{_pessoa.cpf_cnpj }" /> )
														</h:column>
														
														<a4j:support event="onselect" reRender="idPessoa" oncomplete="clickLinkAdicionarPessoa();">
															<f:setPropertyActionListener
																value="#{ _pessoa.id }" target="#{_abstractRelatorioBiblioteca.pessoa}"/>
														</a4j:support>
													</rich:suggestionbox>
													
													<span id="indicador" style="display:none; "> 
														<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
													</span>
											
													<span style="top: 20px; vertical-align: bottom">
														<a4j:commandLink styleClass="botaoAdicionarPessoa"
																actionListener="#{_abstractRelatorioBiblioteca.adicionarPessoa}"
																reRender="dtTblPessoas" oncomplete="limparCampoPessoa();" immediate="true" >
														</a4j:commandLink>
													</span>
													
												</div>
																														
												<a4j:outputPanel ajaxRendered="true" style="float: left;">
													<t:dataTable var="pessoa" rowIndexVar="i"
															id="dtTblPessoas" value="#{_abstractRelatorioBiblioteca.nomeVariasPessoasDataModel}">
														<h:column>
															<h:outputText value="#{i + 1}." />
														</h:column>
														<h:column>
															<h:outputText value="#{_abstractRelatorioBiblioteca.nomeVariasPessoas[i]}" style="width: 359px; display: block;" />
														</h:column>
														<h:column rendered="#{_abstractRelatorioBiblioteca.nomeVariasPessoasDataModel.rowCount > 0}" >
															<a4j:commandLink
																	actionListener="#{_abstractRelatorioBiblioteca.removerPessoa}"
																	reRender="dtTblPessoas">
																<h:graphicImage url="/img/delete.gif" style="border: none;"
																		title="Clique aqui para remover esta pessoa da lista." />
															</a4j:commandLink>
														</h:column>
													</t:dataTable>
												</a4j:outputPanel>
											</td>
										</tr>
									</c:if>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorCategoriaDeUsuario}" >
										<tr>
											<th>Categoria do Usu�rio:</th>
											<td>
												<h:selectOneMenu id="categoriaDeUsuario" 
														value="#{_abstractRelatorioBiblioteca.valorVinculoDoUsuarioSelecionado}"
														valueChangeListener="#{_abstractRelatorioBiblioteca.selectCategoriaDeUsuario}"
														onchange="submit();">
													<f:selectItem itemValue="-1" itemLabel="Todas" />
													<f:selectItems value="#{_abstractRelatorioBiblioteca.vinculosDeUsuarioCombo}" />
												</h:selectOneMenu>
											</td>
										</tr>
									</c:if>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorVariasCategoriasDeUsuario}" >
										<tr>
											<th class="obrigatorio">Categorias de Usu�rio:</th>
											<td>
												<h:selectBooleanCheckbox id="ctgUsuarioTodas" style="margin-left: 6px;" onclick="selecionarTodasCategorias(); submit();" />
												<label for="ctgUsuarioTodas">TODAS</label>
												<h:selectManyCheckbox
														id="variasCategoriasDeUsuario" title="As categorias de usu�rio escolhidas"
														value="#{_abstractRelatorioBiblioteca.variasCategoriasDeUsuario}"
														valueChangeListener="#{_abstractRelatorioBiblioteca.selectCategoriaDeUsuario}"
														onclick="submit();"
														layout="pageDirection" styleClass="categoriaUsuario" style="margin-left: 3px;">
													<f:selectItems value="#{_abstractRelatorioBiblioteca.vinculosDeUsuarioCombo}" />
												</h:selectManyCheckbox>
											</td>
										</tr>
									</c:if>
									
									<%-- <c:if test="${_abstractRelatorioBiblioteca.filtradoPorSituacaoDeServidor}" >
										<tr>
											<th>Situa��o de Servidor:</th>
											<td>
												<table>
												<tr>
													<td>
														<h:selectOneMenu id="categoriaDeUsuario" 
																value="#{_abstractRelatorioBiblioteca.valorSituacaoDoServidorSelecionado}"
																disabled="#{_abstractRelatorioBiblioteca.desabilitaFiltroSituacaoDeServidor}">
															<f:selectItem itemValue="-1" itemLabel="Todas" />
															<f:selectItems value="#{_abstractRelatorioBiblioteca.situacoesDeServidorCombo}" />
														</h:selectOneMenu>
													</td>
													<td style="vertical-align: middle; text-align: left; width: 100%;">
														<ufrn:help>
															Op��o v�lida apenas para as categorias de usu�rio 'Servidor T�cnico-Administrativo' e 'Docente'.
														</ufrn:help>
													</td>
												</tr>
												</table>
											</td>
										</tr>
									</c:if> --%>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorVariasSituacoesDeServidor && !_abstractRelatorioBiblioteca.desabilitaFiltroSituacaoDeServidor}" >
										<tr>
											<th>Situa��es do Servidor:</th>
											<td>
												<table>
												<tr>
													<td>
														<h:selectManyListbox value="#{_abstractRelatorioBiblioteca.variasSituacoesDeServidor}" style="width:144px;"
																id="variasSituacoesDeServidor" size="3" title="As situa��es de servidor escolhidas">
															<f:selectItems value="#{_abstractRelatorioBiblioteca.situacoesDeServidorCombo}" />
														</h:selectManyListbox>
													</td>
													<td style="vertical-align: middle; text-align: left; width: 100%;">
														<ufrn:help>
															Mantenha a tecla <em>Ctrl</em> pressionada para selecionar ou desselecionar uma Unidade Administrativa.<br/>
															Use a tecla <em>Shift</em> para selecionar um intervalo de Unidades Administrativas.
														</ufrn:help>
													</td>
												</tr>
												</table>
											</td>
										</tr>
									</c:if>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorVariasUnidades && !_abstractRelatorioBiblioteca.desabilitaFiltroSituacaoDeServidor}" >
										<tr>
											<th>Unidades Administrativas do Servidor:</th>
											<td style="width: 450px;">
												<table>
												<tr>
													<td>
														<h:selectManyListbox value="#{_abstractRelatorioBiblioteca.variasUnidades}" style="width:450px;"
																id="variasUnidades" size="7" title="V�rias unidades">
															<f:selectItems value="#{unidade.allDeptoCombo}"/>
														</h:selectManyListbox>
													</td>
													<td style="vertical-align: middle; text-align: left; width: 100%;">
														<ufrn:help>
															Mantenha a tecla <em>Ctrl</em> pressionada para selecionar ou desselecionar uma Unidade Administrativa.<br/>
															Use a tecla <em>Shift</em> para selecionar um intervalo de Unidades Administrativas.
														</ufrn:help>
													</td>
												</tr>
												</table>
											</td>
										</tr>
									</c:if>
					
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorCurso}" >
										<tr>
											<th>Curso:</th>
											<td>
												<h:selectOneMenu id="curso" value="#{_abstractRelatorioBiblioteca.curso}">
													<f:selectItem itemValue="0" itemLabel="Todos" />
													<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
												</h:selectOneMenu>
											</td>
										</tr>
									</c:if>						
					
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorDepartamento}" >
										<tr>
											<th>Departamento:</th>
											<td>
												<h:selectOneMenu id="departamento" value="#{_abstractRelatorioBiblioteca.departamento}">
													<f:selectItem itemValue="0" itemLabel="Todos" />
													<f:selectItems value="#{unidade.allDeptoCombo}" />
												</h:selectOneMenu>
											</td>
										</tr>
									</c:if>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorCodigoDeBarras}">
										<tr>
											<th width="130" class="obrigatorio">C�digo de Barras:</th>
											<td>
												<table>
												<tr>
													<td>
														<h:inputText value="#{_abstractRelatorioBiblioteca.codigoDeBarras}" id="codigoDeBarras" maxlength="20" />
													</td>
													<td style="vertical-align: middle; text-align: left; width: 100%;">
														<ufrn:help>
															Digite o in�cio da faixa de c�digos de barras que se deseja pesquisar. Ex.: '2011' traz todos os materiais cujos c�digos de barra iniciam com 2011 (2011XXXXX), 'P0' traz todos os materiais cujos c�digos de barras iniciam com P0 (P0XXXXXXX), e assim por diante.
														</ufrn:help>
													</td>
												</tr>
												</table>
											</td>
										</tr>
									</c:if>

									<c:if test="${_abstractRelatorioBiblioteca.escolherEntrePeriodoEAno}" >
										<tr>
											<th>Agrupamento:</th>
											<td>
												<h:selectOneRadio id="anoOuPeriodo"
														value="#{_abstractRelatorioBiblioteca.filtrarTempoPorAno}"
														onclick="mostrarAnoOuPeriodo()">
													<f:selectItem itemLabel="Meses de um Ano" itemValue="#{true}" />
													<f:selectItem itemLabel="Per�odo" itemValue="#{false}" />
												</h:selectOneRadio>
											</td>
										</tr>
									</c:if>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorAno}" >
										<tr id="filtroAno">
											<th>Ano:</th>
											<td>
												<h:selectOneMenu id="ano" value="#{_abstractRelatorioBiblioteca.ano}">
													<f:selectItems value="#{_abstractRelatorioBiblioteca.anos}" />
												</h:selectOneMenu>
											</td>
										</tr>
									</c:if>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorMes}" >
										<tr>
											<th>M�s:</th>
											<td>
												<h:selectOneMenu id="mes" value="#{_abstractRelatorioBiblioteca.mes}">
													<f:selectItems value="#{_abstractRelatorioBiblioteca.meses}" />
												</h:selectOneMenu>
											</td>
										</tr>
									</c:if>
					
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorColecoes}" >
										<tr>
											<th>Cole��es:</th>
											<td style="width: 450px;">
												<table>
												<tr>
													<td>
														<h:selectManyListbox id="colecoes" value="#{_abstractRelatorioBiblioteca.colecoes}"
																size="7" title="Cole��es" style="width: 400px;">
															<f:selectItems value="#{colecaoMBean.allCombo}" />
														</h:selectManyListbox>
													</td>
													<td style="vertical-align: middle; text-align: left; width: 100%;">
														<ufrn:help>
															Mantenha a tecla <em>Ctrl</em> pressionada para selecionar ou desselecionar uma Cole��o.<br/>
															Use a tecla <em>Shift</em> para selecionar um intervalo de Cole��es.
														</ufrn:help>
													</td>
												</tr>
												</table>
											</td>
										</tr>
									</c:if>
					
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorColecao}" >
										<tr>
											<th>Cole��o:</th>
											<td>
												<h:selectOneMenu id="colecao" value="#{_abstractRelatorioBiblioteca.colecao}">
													<f:selectItem itemLabel="Todas" itemValue="-" />
													<f:selectItems value="#{colecaoMBean.allCombo}" />
												</h:selectOneMenu>
											</td>
										</tr>
									</c:if>
					
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorTiposDeMaterial}" >
										<tr>
											<th>Tipos de Material:</th>
											<td style="width: 450px;">
												<table>
												<tr>
													<td>
														<h:selectManyListbox id="tiposDeMaterial" value="#{_abstractRelatorioBiblioteca.tiposDeMaterial}"
																size="7" title="Tipos de Materiais" style="width: 400px;">
															<f:selectItems value="#{tipoMaterialMBean.allCombo}" />
														</h:selectManyListbox>
													</td>
													<td style="vertical-align: middle; text-align: left; width: 100%;">
														<ufrn:help>
															Mantenha a tecla <em>Ctrl</em> pressionada para selecionar ou desselecionar um Tipo de Material.<br/>
															Use a tecla <em>Shift</em> para selecionar um intervalo de Tipos.
														</ufrn:help>
													</td>
												</tr>
												</table>
											</td>
										</tr>
									</c:if>
					
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorTipoDeMaterial}" >
										<tr>
											<th>Tipo de Material:</th>
											<td>
												<h:selectOneMenu id="tipoDeMaterial" value="#{_abstractRelatorioBiblioteca.tipoDeMaterial}">
													<f:selectItem itemLabel="Todos" itemValue="0" />
													<f:selectItems value="#{tipoMaterialMBean.allCombo}" />
												</h:selectOneMenu>
											</td>
										</tr>
									</c:if>
									
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorSituacoesDoMaterial}" >
										<tr>
											<th>Situa��es do Material:</th>
											<td style="width: 450px;">
												<table>
												<tr>
													<td>
														<h:selectManyListbox id="situacaoesDosMaterial" value="#{_abstractRelatorioBiblioteca.situacoesMaterial}"
																size="7" title="Tipos de Materiais" style="width: 400px;">
															<f:selectItems value="#{_abstractRelatorioBiblioteca.situacoesMateriaisCombo}" />
														</h:selectManyListbox>
													</td>
													<td style="vertical-align: middle; text-align: left; width: 100%;">
														<ufrn:help>
															Mantenha a tecla <em>Ctrl</em> pressionada para selecionar ou desselecionar uma Situa��o do Material.<br/>
															Use a tecla <em>Shift</em> para selecionar um intervalo de Situa��es.
														</ufrn:help>
													</td>
												</tr>
												</table>
											</td>
										</tr>
									</c:if>
									
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorTipoDeAcervo}" >
										<tr>
											<th>Tipo de Acervo:</th>
											<td style="width: 450px;">
												<h:selectOneMenu id="tipoDeAcervo" value="#{_abstractRelatorioBiblioteca.tipoDeAcervo}">
													<f:selectItem itemLabel="Todos" itemValue="#{ _abstractRelatorioBiblioteca.tipoAcervoTodos }" />
													<f:selectItem itemLabel="T�tulos" itemValue="#{ _abstractRelatorioBiblioteca.tipoAcervoTitulos }" />
													<f:selectItem itemLabel="Materiais" itemValue="#{ _abstractRelatorioBiblioteca.tipoAcervoMateriais }" />
												</h:selectOneMenu>
											</td>
										</tr>
									</c:if>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorFormasDocumento}" >
										<tr>
											<th>Formas do Documento:</th>
											<td>
												<table>
												<tr>
													<td>
														<h:selectManyListbox id="formasDoDocumento" value="#{_abstractRelatorioBiblioteca.formasDocumento}"
																size="7" title="Formas do Documento" style="width: 400px;" >
															<f:selectItems value="#{formaDocumentoMBean.allCombo}"/>
														</h:selectManyListbox>
													</td>
													<td style="vertical-align: middle; text-align: left; width: 100%;">
														<ufrn:help>
															Mantenha a tecla <em>Ctrl</em> pressionada para selecionar ou desselecionar uma Forma do Documento.<br/>
															Use a tecla <em>Shift</em> para selecionar um intervalo de Formas do Documento.
														</ufrn:help>
													</td>
												</tr>
												</table>
											</td>
										</tr>
									</c:if>
									
									
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorTipoDeEmprestimo}" >
										<tr>
											<th>Tipo de Empr�stimo:</th>
											<td>
												<h:selectOneMenu id="tipoDeEmprestimo" value="#{_abstractRelatorioBiblioteca.tipoDeEmprestimo}">
													<f:selectItem itemLabel="Todos" itemValue="0" />
													<f:selectItems value="#{_abstractRelatorioBiblioteca.tiposDeEmprestimoCombo}" />
												</h:selectOneMenu>
											</td>
										</tr>
									</c:if>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorPeriodo}" >
										<tr id="filtroPeriodo">
											<th class="${_abstractRelatorioBiblioteca.campoPeriodoObrigatorio? 'obrigatorio': ''}">Per�odo:</th>
											<td>
											<table>
												<tr>
													<td>
													<t:inputCalendar id="inicio" value="#{_abstractRelatorioBiblioteca.inicioPeriodo}" renderAsPopup="true"
															popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)"
															renderPopupButtonAsImage="true" size="10" maxlength="10" title="Data de in�cio do Per�odo" />
													</td>
													<td>a</td>
													<td>
													<t:inputCalendar id="fim" value="#{_abstractRelatorioBiblioteca.fimPeriodo}" renderAsPopup="true"
															popupDateFormat="dd/MM/yyyy" onkeypress="return formataData(this,event)"
															renderPopupButtonAsImage="true" size="10" maxlength="10" title="Data de fim do Per�odo" />
													</td>
												</tr>
											</table>
										</tr>
									</c:if>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorSituacaoUsuario}">
										<tr>
											<th>Situa��o do Usu�rio:</th>
											<td>
												<h:selectOneMenu id="situacaoUsuarioBiblioteca" title="Situa��o do Usu�rio"
														value="#{_abstractRelatorioBiblioteca.situacaoUsuarioBiblioteca}">
													<f:selectItems value="#{_abstractRelatorioBiblioteca.situacoesDoUsuarioCombo}" />
												</h:selectOneMenu>
											</td>
										</tr>
									</c:if>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorBooleano}" >
										<tr>
											<th>${_abstractRelatorioBiblioteca.descricaoDadoBooleano}:</th>
											<td>
												<h:selectBooleanCheckbox id="checkBooleano" value="#{_abstractRelatorioBiblioteca.dadoBooleano}" />
											</td>
										</tr>
									</c:if>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorTipoClassificacao}">
										<tr>
											<th>Tipo de Classifica��o:</th>
											<td>
												<h:selectOneRadio id="classe" layout="lineDirection"
														value="#{_abstractRelatorioBiblioteca.tipoclassificacaoEscolhida}" title="Tipo de Classifica��o">
													<f:selectItems value="#{_abstractRelatorioBiblioteca.classificacoesBibliograficasComboBox}"/>
												</h:selectOneRadio>
											</td>
										</tr>
									</c:if>
									
									<c:if test="${_abstractRelatorioBiblioteca.filtradoPorIntervaloClassificacao}">
										<tr>
											<td colspan="2">
												<table>
														<tr>
															<th width="130" align="right" class="${_abstractRelatorioBiblioteca.campoIntervaloClassificacaoObrigatorio? 'obrigatorio': ''}">Classe Inicial:</th>
															<td>
																<h:inputText value="#{_abstractRelatorioBiblioteca.classeInicial}" id="classeInicial"/>
															</td>
															<th width="130" align="right" class="${_abstractRelatorioBiblioteca.campoIntervaloClassificacaoObrigatorio? 'obrigatorio': ''}">Classe Final:</th>
															<td>
																<h:inputText value="#{_abstractRelatorioBiblioteca.classeFinal}" id="classeFinal" />
															</td>
														</tr>
												</table>
											</td>
										</tr>		
										
									</c:if>
									
									
									<c:if test="${ _abstractRelatorioBiblioteca.filtradoPorCtgMaterial }" >
										<tr>
											<th>Materiais Mostrados:</th>
											<td>
												<h:selectOneRadio id="categoriaDeMaterial" layout="lineDirection"
														value="#{ _abstractRelatorioBiblioteca.ctgMaterial }">
													<f:selectItem itemLabel="Exemplares" itemValue="#{ _abstractRelatorioBiblioteca.ctgMatExemplares }" />
													<f:selectItem itemLabel="Fasc�culos" itemValue="#{ _abstractRelatorioBiblioteca.ctgMatFasciculos }" />
													<c:if test="${ _abstractRelatorioBiblioteca.permitirDigitalCtgMaterial }" >
														<f:selectItem itemLabel="Digital" itemValue="#{ _abstractRelatorioBiblioteca.ctgMatDigitais }" />
													</c:if>
													<c:if test="${ _abstractRelatorioBiblioteca.permitirTodasCtgMaterial }" >
														<f:selectItem itemLabel="Todos" itemValue="#{ _abstractRelatorioBiblioteca.ctgMatTodos }" />
													</c:if>
												</h:selectOneRadio>
											</td>
										</tr>
									</c:if>
									
									
									<c:if test="${ _abstractRelatorioBiblioteca.filtradoPorTurno }" >
										<tr>
											<th class="obrigatorio">Turno:</th>
											<td>
												<h:selectOneRadio id="turno" layout="lineDirection"
														value="#{ _abstractRelatorioBiblioteca.turno }">
													<f:selectItem itemLabel="Todos" itemValue="#{ _abstractRelatorioBiblioteca.turnoTodos }" />
													<f:selectItem itemLabel="Manh�" itemValue="#{ _abstractRelatorioBiblioteca.turnoManha }" />
													<f:selectItem itemLabel="Tarde" itemValue="#{ _abstractRelatorioBiblioteca.turnoTarde }" />
													<f:selectItem itemLabel="Noite" itemValue="#{ _abstractRelatorioBiblioteca.turnoNoite }" />
												</h:selectOneRadio>
											</td>
										</tr>
									</c:if>
									
									
									
									
									
									<%--      T I P O            D  E     T O M B A M E N T O  --%>
									
									<c:if test="${ _abstractRelatorioBiblioteca.filtradoPorTipoDeTombamento }" >
										<tr>
											<th>Tipo de Tombamento:</th>
											<td>
												<h:selectOneRadio id="tipoDeTombamento" layout="lineDirection"
														value="#{ _abstractRelatorioBiblioteca.tipoDeTombamento }">
													<f:selectItem itemLabel="Compra" itemValue="#{ _abstractRelatorioBiblioteca.tombamentoCompra }" />
													<f:selectItem itemLabel="Doa��o" itemValue="#{ _abstractRelatorioBiblioteca.tombamentoDoacao }" />
													<c:if test="${ _abstractRelatorioBiblioteca.permitirTodosTiposDeTombamento }">
														<f:selectItem itemLabel="Todos" itemValue="#{ _abstractRelatorioBiblioteca.tombamentoTodos }" />
													</c:if>
												</h:selectOneRadio>
											</td>
										</tr>
									</c:if>
									
									
									
									
									<%--      M O D A L I D A D E    D  E     A Q U I S I � � O   --%>
									<%--     Modalidade de aquisi��o diz respeito as informa��es da assinatura, o filtro tipo de tombamento acima � com rela��o aos bens   --%>
									
									<c:if test="${ _abstractRelatorioBiblioteca.filtradoPorModalidadeAquisicao }" >
										<tr>
											<th>Modalidade de Aquisi��o:</th>
											<td>
												<h:selectOneRadio id="tipoDeTombamento" layout="lineDirection"
														value="#{ _abstractRelatorioBiblioteca.tipoModalidadeEscolhida }">
													<f:selectItem itemLabel="Todas" itemValue="#{ _abstractRelatorioBiblioteca.modalidadeAquisicaoTodas }" />
													<f:selectItem itemLabel="Compra" itemValue="#{ _abstractRelatorioBiblioteca.modalidadeAquisicaoCompra }" />
													<f:selectItem itemLabel="Doa��o" itemValue="#{ _abstractRelatorioBiblioteca.modalidadeAquisicaoDoacao }" />
													<f:selectItem itemLabel="Substitui��o" itemValue="#{ _abstractRelatorioBiblioteca.modalidadeAquisicaoSubstituicao }" />
													<f:selectItem itemLabel="Indefinida" itemValue="#{ _abstractRelatorioBiblioteca.modalidadeAquisicaoIndefinida }" />
												</h:selectOneRadio>
											</td>
										</tr>
									</c:if>
									
									
									
									<%--         F O R M A        D  O    R E L A T O R I O       --%>
									<c:if test="${ _abstractRelatorioBiblioteca.filtradoPorFormaRelatorio }" >
										<tr>
											<th width="130" align="right">Formato do Relat�rio:</th>
											<td>
												<h:selectOneRadio id="formatoDoRelatorio" value="#{_abstractRelatorioBiblioteca.formatoRelatorio}">
													<f:selectItem itemLabel="Sint�tico" itemValue="1" />
													<f:selectItem itemLabel="Anal�tico" itemValue="2" />
												</h:selectOneRadio>
											</td>
										</tr>
									</c:if>
									
									
									
									
									
									<%--         A G R U P A M E N T O S       --%>
									
									<c:if test="${ _abstractRelatorioBiblioteca.filtradoPorAgrupamento1 }" >
										<tr id="linhaAgrupamento1">
											<th class="${_abstractRelatorioBiblioteca.agrupamento1Obrigatorio? 'obrigatorio' : ''}">${ _abstractRelatorioBiblioteca.filtradoPorAgrupamento2 ? "1&ordm;" : "" } Agrupamento:</th>
											<td>
												<h:selectOneMenu id="agrupamento1" value="#{_abstractRelatorioBiblioteca.valorAgrupamento1}">
													<f:selectItems value="#{_abstractRelatorioBiblioteca.agrupamentos1ComboBox}" />
												</h:selectOneMenu>
											</td>
										</tr>
									</c:if>
									
									<c:if test="${ _abstractRelatorioBiblioteca.filtradoPorAgrupamento1 && _abstractRelatorioBiblioteca.filtradoPorAgrupamento2 }" >	
										<tr id="linhaAgrupamento2">
											<th class="${_abstractRelatorioBiblioteca.agrupamento2Obrigatorio? 'obrigatorio': ''}">2&ordm; Agrupamento:</th>
											<td>
												<h:selectOneMenu id="agrupamento2" value="#{_abstractRelatorioBiblioteca.valorAgrupamento2}">
													<f:selectItems value="#{_abstractRelatorioBiblioteca.agrupamentos2ComboBox}" />
												</h:selectOneMenu>
											</td>
										</tr>
									</c:if>
									
									
									<%--         O R D E N A � � O      --%>
									
									<c:if test="${ _abstractRelatorioBiblioteca.filtradoPorOrdenacao }" >
										<tr>
											<th>Ordena��o:</th>
											<td>
												<h:selectOneRadio id="ordenacao" value="#{_abstractRelatorioBiblioteca.ordenacao}" layout="lineDirection" >
													<f:selectItems value="#{_abstractRelatorioBiblioteca.ordenacaoComboBox}" />
												</h:selectOneRadio>
											</td>
										</tr>
									</c:if>
									
									
									
									
									<%--         N I V E L     D E     D E T A L H E      --%>
									
									<c:if test="${ _abstractRelatorioBiblioteca.filtradoPorBasicoVsDetalhado }" >
										<tr>
											<th>N�vel de detalhe:</th>
											<td>
												<h:selectOneRadio id="nivelDeDetalhe" layout="lineDirection"
														value="#{ _abstractRelatorioBiblioteca.mostrarDetalhado }">
													<f:selectItem itemLabel="B�sico" itemValue="#{false}"/>
													<f:selectItem itemLabel="Detalhado" itemValue="#{true}"/>
												</h:selectOneRadio>
											</td>
										</tr>
									</c:if>
									
									</table>
								</td>
								<td style="width: 10px;"></td>
							</tr>
						</table>
					</td>
				</tr>
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
						<h:commandButton id="cmdButtonGerarRelatoriosBiblioteca" value="Gerar Relat�rio" action="#{_abstractRelatorioBiblioteca.gerar}" onclick="ativaBotaoFalso();"/>
						
						<%-- Botao falso que � mostrado ao usu�rio desabilitado, porque n�o d� para desabilitar o bot�o geral, sen�o a a��o n�o � executada --%>
						<h:commandButton id="fakecmdButtonGerarRelatoriosBiblioteca" value="Aguarde ..." style="display: none;" disabled="true" />
						<span id="indicatorGeracaoRelatorio"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
						
						<h:commandButton id="cmdButtonCancelar" value="Cancelar" action="#{_abstractRelatorioBiblioteca.cancelar}" immediate="true" onclick="#{confirm}" />
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		<c:if test="${ _abstractRelatorioBiblioteca.possuiFiltrosObrigatorios }" >
			<div style="width: 100%; margin-top: 10px;" class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
		</c:if>
		
	</h:form>

</f:view>

<c:if test="${_abstractRelatorioBiblioteca.escolherEntrePeriodoEAno}" >
	<script type="text/javascript">
	
		function mostrarAnoOuPeriodo() {
			if ( $('form:anoOuPeriodo:0').checked ) {
				$('filtroAno').show();
				$('filtroPeriodo').hide();
			} else {
				$('filtroAno').hide();
				$('filtroPeriodo').show();
			}
		}
	
		Event.observe(window, 'load', function() {
			mostrarAnoOuPeriodo();
		});
	
	</script>
</c:if>

<c:if test="${ _abstractRelatorioBiblioteca.filtradoPorVariasCategoriasDeUsuario }">
	<script type="text/javascript">
	
		function selecionarTodasCategorias() {
			$('form:variasCategoriasDeUsuario').select('[type=checkbox]').each( function(elem) {
				elem.checked = $('form:ctgUsuarioTodas').checked;
			});
		}
	
	</script>
</c:if>

<c:if test="${ _abstractRelatorioBiblioteca.filtradoPorVariasPessoas }">
	<script type="text/javascript">
	
		function limparCampoPessoa() {
			//alert($('form:idPessoa').value);
			//$('form:idPessoa').value = '';
			$('form:nomePessoa').value = '';
		}
	
		// Executa a fun��o do commadlink adicionar assunto selecioda automaticamente para o usu�rio.
		function clickLinkAdicionarPessoa(){ 
			J(".botaoAdicionarPessoa").trigger('click');
			return false;
		}
		
		var J = jQuery.noConflict(); 
		
	</script>
</c:if>


<script type="text/javascript">

	function ativaBotaoFalso() {
		$('form:cmdButtonGerarRelatoriosBiblioteca').hide();
		$('form:fakecmdButtonGerarRelatoriosBiblioteca').show();
		$('indicatorGeracaoRelatorio').style.display = '';
		
	}

	ativaBotaoVerdadeiro();
	
	function ativaBotaoVerdadeiro() {
		$('form:cmdButtonGerarRelatoriosBiblioteca').show();
		$('form:fakecmdButtonGerarRelatoriosBiblioteca').hide();
		$('indicatorGeracaoRelatorio').style.display = 'none';
	}
	
</script>	


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
