<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.academico.dominio.NivelEnsino"%>

<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<link rel="stylesheet" type="text/css" href="/sigaa/css/primefaces_skin.css"/>

<script type="text/javascript">
	var ultimoFocus = null;
	
	function setUltimoFocus(elemento) {
		ultimoFocus = elemento.id;
	}
	
	function setFocus() {
		document.getElementById(ultimoFocus).focus();
	}
</script>

<f:view>
<h2> <ufrn:subSistema/> &gt; Solicitação de Catalogação na Fonte &gt;  
	<c:choose>
		<c:when test="${solicitacaoCatalogacaoMBean.atender || solicitacaoCatalogacaoMBean.cancelar}">
			<h:outputText value="#{solicitacaoCatalogacaoMBean.confirmButton}"/>
		</c:when>
		<c:otherwise>
			Visualizar
		</c:otherwise>
	</c:choose>
 </h2>


<c:if test="${solicitacaoCatalogacaoMBean.atender}">
	<div class="descricaoOperacao">
		<p>
			Defina se a forma de geração da ficha será manual ou por arquivo:
		</p>
		<ul>
			<li><strong>Manual:</strong> informe todos os dados manualmente. Uma prévia da ficha é exibida no topo do formulário.</li>
			<li><strong>Arquivo:</strong> selecione um arquivo no formato <b>pdf,doc,docx ou odt</b> contendo a ficha catalográfica já gerada.</li>
		</ul>
		<p>
			Após a edição da ficha, há duas opções:
		</p>
		<ul>
			<li><strong>Salvar:</strong> salva a ficha em caráter temporário, permitindo que se faça alterações futuras. O usuário solicitante ainda não poderá visualizar a ficha.</li>
			<li><strong>Atender:</strong> finaliza o atendimento e a ficha catalográfica é consolidada (não pode mais ser alterada). A ficha se tornará disponível para o usuário solicitante.</li>
		</ul>
	</div>
</c:if>

<a4j:keepAlive beanName="solicitacaoCatalogacaoMBean"></a4j:keepAlive>
<a4j:keepAlive beanName="solicitacaoServicoDocumentoMBean"></a4j:keepAlive>

<p:resources />

<h:form enctype="multipart/form-data">


	<a4j:status onstart="modelPanelAguarde.show();" onstop="modelPanelAguarde.hide();"></a4j:status>


	<p:dialog id="modelAguarde" header="Por favor, aguarde." widgetVar="modelPanelAguarde" modal="true" width="250" height="100">
		<h:outputText value="Processando ..."></h:outputText>
	    <h:graphicImage value="/img/indicator.gif"></h:graphicImage>
	</p:dialog>


	<%--                     Dados da solicitação        --%>
	<table class="visualizacao" width="90%">
			
			<caption>Solicitação de Catalogação na Fonte número ${solicitacaoCatalogacaoMBean.obj.numeroSolicitacao} </caption>
			
			<c:if test="${not empty solicitacaoCatalogacaoMBean.obj.discente}">
				<tr>
					<th width="40%">Solicitante:</th>
					<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.discente}"/></td>
				</tr>
				
				<th>Categoria:</th>
				
				<c:if test="${  ( solicitacaoCatalogacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						|| solicitacaoCatalogacaoMBean.obj.discente.lato 
						|| solicitacaoCatalogacaoMBean.obj.discente.mestrado 
						|| solicitacaoCatalogacaoMBean.obj.discente.doutorado}">
					
					<td>Aluno de Pós-Graduação</td>
				</c:if>
				<c:if test="${ ! ( solicitacaoCatalogacaoMBean.obj.discente.nivel == NivelEnsino.STRICTO)
						&& !solicitacaoCatalogacaoMBean.obj.discente.lato 
						&& !solicitacaoCatalogacaoMBean.obj.discente.mestrado 
						&& !solicitacaoCatalogacaoMBean.obj.discente.doutorado}">
					<td>Aluno de Graduação</td>
				</c:if>
				
				<tr>
					<th>Curso:</th>
					<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.discente.curso}"/></td>
				</tr>
				
			</c:if>
			
			<c:if test="${not empty solicitacaoCatalogacaoMBean.obj.servidor}">
				<tr>
					<th>Solicitante:</th>
					<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.servidor}"/></td>
				</tr>
				<tr>
					<th>Categoria:</th>
					<td>
						<c:if test="${solicitacaoCatalogacaoMBean.obj.servidor.categoria.docente}" >
							Docente
						</c:if>
						<c:if test="${solicitacaoCatalogacaoMBean.obj.servidor.categoria.tecnico}" >
							Técnico Administrativo
						</c:if>
					</td>
				</tr>
				<tr>
					<th>Lotação:</th>
					<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.servidor.unidade}"/></td>
				</tr>
			</c:if>
			
			<tr>
				<th>Telefone:</th>
				<td> ${solicitacaoCatalogacaoMBean.obj.pessoa.telefone} </td>
			</tr>
			
			<tr>
				<th>Celular:</th>
				<td> ${solicitacaoCatalogacaoMBean.obj.pessoa.celular} </td>
			</tr>
			
			<tr>
				<th>Email:</th>
				<td> ${solicitacaoCatalogacaoMBean.obj.pessoa.email} </td>
			</tr>
			
			<tr>
				<th>Data da Solicitação:</th>
				<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.dataCadastro}"/></td>
			</tr>
			
			<tr>
				<th>Situação da Solicitação:</th>
				<td>
					<c:if test="${solicitacaoCatalogacaoMBean.obj.solicitado || solicitacaoCatalogacaoMBean.obj.reenviado}">
						<h:outputText value="#{solicitacaoCatalogacaoMBean.obj.situacao.descricao}"/>
					</c:if>
					<c:if test="${solicitacaoCatalogacaoMBean.obj.atendido || solicitacaoCatalogacaoMBean.obj.atendidoFinalizado}">
						<h:outputText style="color:green;"  value="#{solicitacaoCatalogacaoMBean.obj.situacao.descricao}"/>
					</c:if>
					<c:if test="${solicitacaoCatalogacaoMBean.obj.cancelado}">
						<h:outputText style="color:red;"  value="#{solicitacaoCatalogacaoMBean.obj.situacao.descricao}"/>
					</c:if>
				</td>
			</tr>
			
			<tr>
				<th>Biblioteca:</th>
				<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.biblioteca.descricao}"/></td>
			</tr>
			
			<tr>
				<th>Tipo do Documento:</th>
				<td>
					<h:outputText value="#{solicitacaoCatalogacaoMBean.obj.tipoDocumento.denominacao}"/>
					<c:if test="${solicitacaoCatalogacaoMBean.obj.tipoDocumento.tipoDocumentoOutro}">
								(  ${solicitacaoCatalogacaoMBean.obj.outroTipoDocumento}  )
					</c:if>	
				</td>
			</tr>
			
			<tr>
				<th>Documento Enviado pelo Usuário:</th>
				<td>
					<a target="_blank" href="${ configSistema['linkSigaa'] }/sigaa/verProducao?idProducao=${solicitacaoCatalogacaoMBean.obj.idTrabalhoDigitalizado}&key=${ sf:generateArquivoKey(solicitacaoCatalogacaoMBean.obj.idTrabalhoDigitalizado) }">
						<h:graphicImage url="/img/porta_arquivos/icones/doc.png" style="border:none" title="Clique aqui para visualizar o documento em formato digital." /> Visualizar
					</a>
				</td>
			</tr>
			
			<c:if test="${(not empty solicitacaoCatalogacaoMBean.obj.fichaGerada && solicitacaoCatalogacaoMBean.obj.fichaGerada.id != 0) || not empty solicitacaoCatalogacaoMBean.obj.idFichaDigitalizada}">
				<tr>
					<th>
						<c:if test="${solicitacaoCatalogacaoMBean.obj.atendido && solicitacaoCatalogacaoMBean.obj.atendidoFinalizado}">Ficha Catalográfica:</c:if>
						<c:if test="${!solicitacaoCatalogacaoMBean.obj.atendido && !solicitacaoCatalogacaoMBean.obj.atendidoFinalizado}">Ficha Catalográfica (Não finalizada):</c:if>
					</th>
					<td>
						<c:if test="${not empty solicitacaoCatalogacaoMBean.obj.fichaGerada && solicitacaoCatalogacaoMBean.obj.fichaGerada.id != 0}">
							<h:commandLink action="#{solicitacaoCatalogacaoMBean.imprimirFichaCatalograficaPDF}" target="_blank">
								<f:param name="id" value="#{solicitacaoCatalogacaoMBean.obj.id}" />
								<h:graphicImage url="/img/porta_arquivos/icones/desconhecido.png" style="border:none" title="Clique aqui para visualizar a ficha catalográfica." /> Visualizar
							</h:commandLink>
						</c:if>
						<c:if test="${not empty solicitacaoCatalogacaoMBean.obj.idFichaDigitalizada}">
							<a target="_blank" href="${ configSistema['linkSigaa'] }/sigaa/verProducao?idProducao=${solicitacaoCatalogacaoMBean.obj.idFichaDigitalizada}&key=${ sf:generateArquivoKey(solicitacaoCatalogacaoMBean.obj.idFichaDigitalizada) }">
								<h:graphicImage url="/img/porta_arquivos/icones/desconhecido.png" style="border:none" title="Clique aqui para visualizar a ficha catalográfica." /> Visualizar
							</a>
						</c:if>
					</td>
				</tr>
			</c:if>
			
			<tr>
				<th>Número de folhas</th>
				<td><h:outputText value="#{solicitacaoCatalogacaoMBean.obj.numeroPaginas}"/></td>
			</tr>
			
			<tr>
				<th>Palavras-chave:</th>
				<td>
					<h:outputText id="palavrasChaves"
						value="#{solicitacaoCatalogacaoMBean.obj.palavrasChaveString}"/>
				</td>
			</tr>
			
			<c:if test="${solicitacaoCatalogacaoMBean.obj.atendido || solicitacaoCatalogacaoMBean.obj.atendidoFinalizado}">
				<tr style="margin-top: 20px;">
					<th style="padding-top: 20px;">Atendida por:</th>
					<td style="padding-top: 20px;">
						${solicitacaoCatalogacaoMBean.obj.atendente}
					</td>
				</tr>
				<tr>
					<th>Data do Atendimento:</th>
					<td>
						<ufrn:format type="dataHora" valor="${solicitacaoCatalogacaoMBean.obj.dataAtendimento}"/>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${solicitacaoCatalogacaoMBean.obj.reenviado}">
				<tr style="margin-top: 20px; color: red;">
					<th style="padding-top: 20px;">Problema Reportado pelo Solicitante:</th>
					<td style="padding-top: 20px;">
						${solicitacaoCatalogacaoMBean.obj.motivoReenvio}
					</td>
				</tr>
			</c:if>
			
			
			
			
			
			<%-- Se o usuário solicitou a catalogação na fonte, para atender uma solicitação o bibliotecário enviar aqui a ficha catalográfica --%>
			
			<c:if test="${solicitacaoCatalogacaoMBean.atender}">
				<tr>
					<td colspan="2">
							<table class="subFormulario" style="width: 100%">
								<caption>Ficha Catalográfica</caption>
								<tr>
									<th style="font-weight: normal; padding-right: 13px; width: 25%">Forma de Geração da Ficha:</th>
									<td>
										<h:selectOneRadio id="sorTipoFicha" value="#{solicitacaoCatalogacaoMBean.tipoFicha}" style="margin: 0px;">
											<f:selectItem itemLabel="Manual" itemValue="#{solicitacaoCatalogacaoMBean.tipoFichaManual}" />
											<f:selectItem itemLabel="Arquivo" itemValue="#{solicitacaoCatalogacaoMBean.tipoFichaArquivo}" />
											<a4j:support event="onclick" reRender="tabelaFicha"></a4j:support>
										</h:selectOneRadio>
									</td>
								</tr>
								<tr>
									<td colspan="2">
										<a4j:outputPanel ajaxRendered="true">
											<t:div rendered="#{solicitacaoCatalogacaoMBean.tipoFicha == 'MANUAL'}">
												<t:div id="previaFicha" style="text-align: center; margin: 10px 0px 20px 0px;">
													<div>
														<p><strong>PRÉVIA DA FICHA</strong></p>
													</div>
													<div>
														<span style="display: block;">${solicitacaoCatalogacaoMBean.tituloFicha}</span>
														<span style="display: block;">Catalogação de Publicação na Fonte. ${solicitacaoCatalogacaoMBean.siglaInstituicao} - ${solicitacaoCatalogacaoMBean.obj.biblioteca.descricao}</span>
													</div>
													<div style="margin: 0px auto; text-align: left; width: 500px; border-width: 1px; border-color: black; border-style: solid; padding: 10px 10px 10px 10px">
														${solicitacaoCatalogacaoMBean.fichaCatalografica}
													</div>
												</t:div>
												
												<%-- gera um arquivo para o usuário utilizar o que foi feito no sistema como modelo e poder aproveitar --%>
												<t:div id="divGerarArquivoEditavel" style="text-align: center; margin: 10px 0px 20px 0px;">
													<h:commandLink value="Gerar Arquivo Editável" actionListener="#{solicitacaoCatalogacaoMBean.gerarFichaEmArquivo}"></h:commandLink>
												</t:div>
												
											</t:div>
										</a4j:outputPanel>
										
										
										
										
										<a4j:outputPanel ajaxRendered="true" keepTransient="false">
											<table id="tabelaFicha" class="subFormulario" style="width: 100%">
											
											<%--                     
										      -- O campo para submeter o arquivo
										      --%>
											
											<c:if test="${solicitacaoCatalogacaoMBean.tipoFicha == 'ARQUIVO'}">
												<tr>
													<th class="obrigatorio" style="font-weight: normal; padding-right: 13px; width: 25%">Arquivo:</th>
													<td>
														<t:inputFileUpload id="ifuArquivoFicha" size="70" value="#{solicitacaoCatalogacaoMBean.arquivo}" />
													</td>
												</tr>
											</c:if>
											
											<%--                     
										      -- O formulário onde o usuáro preenche os dados para o sistema gerar a ficha. 
										      --%>
											
											<c:if test="${solicitacaoCatalogacaoMBean.tipoFicha == 'MANUAL'}">
												<tr>
													<td colspan="2">
														<div style="text-align: center;">
															<p><strong>DADOS</strong></p>
														</div>
													</td>
												</tr>
												<tr>
													<th class="obrigatorio" style="font-weight: normal; padding-right: 13px; width: 25%">Título:</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.titulo}" size="70" maxlength="300" id="txtTitulo" onfocus="setUltimoFocus(this);" requiredMessage="O campo 'Título' é obrigatório.">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th class="obrigatorio" style="font-weight: normal; padding-right: 13px;">Autor:</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.autor}" size="40" maxlength="150" id="txtAutor" onfocus="setUltimoFocus(this);" requiredMessage="O campo 'Autor' é obrigatório.">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; padding-right: 13px;">Responsabilidade:</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.responsabilidade}" size="40" maxlength="150" id="txtResponsabilidade" onfocus="setUltimoFocus(this);">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; padding-right: 13px;">Edição:</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.edicao}" size="10" maxlength="20" id="txtEdicao" onfocus="setUltimoFocus(this);">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; padding-right: 13px;">Local:</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.localPublicacao}" size="20" maxlength="50" id="txtLocalPublicacao" onfocus="setUltimoFocus(this);">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; padding-right: 13px;">Editora:</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.editora}" size="20" maxlength="50" id="txtEditora" onfocus="setUltimoFocus(this);">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th class="obrigatorio" style="font-weight: normal; padding-right: 13px;">Ano:</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.ano}" size="10" maxlength="4" id="txtAno" onfocus="setUltimoFocus(this);" requiredMessage="O campo 'Ano' é obrigatório.">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th class="obrigatorio" style="font-weight: normal; padding-right: 13px;">Descrição Física (campo 300a):</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.descricaoFisica}" size="20" maxlength="50" id="txtDescricaoFisica" onfocus="setUltimoFocus(this);" requiredMessage="O campo 'Descrição Física (campo 300a)' é obrigatório.">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; padding-right: 13px;">Detalhes (campo 300b):</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.descricaoFisicaDetalhes}" size="20" maxlength="50" id="txtDescricaoFisicaDetalhes" onfocus="setUltimoFocus(this);">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; padding-right: 13px;">Dimensão (campo 300c):</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.descricaoFisicaDimensao}" size="20" maxlength="50" id="txtDescricaoFisicaDimensao" onfocus="setUltimoFocus(this);">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; padding-right: 13px;">Material (campo 300e):</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.descricaoFisicaMaterialAcompanha}" size="35" maxlength="100" id="txtDescricaoFisicaMaterial" onfocus="setUltimoFocus(this);">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; padding-right: 13px;">Série:</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.serie}" size="10" maxlength="20" id="txtSerie" onfocus="setUltimoFocus(this);">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; padding-right: 13px;">ISBN:</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.isbn}" size="20" maxlength="30" id="txtISBN" onfocus="setUltimoFocus(this);">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; padding-right: 13px;">ISSN:</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.issn}" size="20" maxlength="30" id="txtISSN" onfocus="setUltimoFocus(this);">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th class="obrigatorio" style="font-weight: normal; padding-right: 13px;">Biblioteca:</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.biblioteca}" size="40" maxlength="200" id="txtBiblioteca" onfocus="setUltimoFocus(this);" requiredMessage="O campo 'Biblioteca' é obrigatório.">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th class="obrigatorio" style="font-weight: normal; padding-right: 13px;"> <h:outputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.descricaoClassificacao}" /> :</th>
													<td>
														<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.classificacao}" size="10" maxlength="15" id="txtClassificacao" onfocus="setUltimoFocus(this);" requiredMessage="O campo  #{solicitacaoCatalogacaoMBean.obj.fichaGerada.descricaoClassificacao} é obrigatório.">
															<a4j:support event="onchange" reRender="previaFicha" oncomplete="setFocus();"></a4j:support>
														</h:inputText>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; vertical-align: top;">Notas Gerais: &nbsp;&nbsp;&nbsp;</th>
													
													<td>	
														<a4j:outputPanel ajaxRendered="true" style="float: left;">
															<t:dataTable var="notaGeral" rowIndexVar="i"
																	id="dtTblNotasGerais" value="#{solicitacaoCatalogacaoMBean.notasGeraisDataModel}">
																<h:column>
																	<h:outputText value="#{i + 1}." />
																</h:column>
																<h:column>
																	<h:inputText value="#{solicitacaoCatalogacaoMBean.obj.fichaGerada.notasGerais[i]}" size="70" maxlength="200">
																		<a4j:support event="onchange" reRender="previaFicha"></a4j:support>
																	</h:inputText>
																</h:column>
																<h:column rendered="#{solicitacaoCatalogacaoMBean.notasGeraisDataModel.rowCount > 0}" >
																	<a4j:commandLink
																			actionListener="#{solicitacaoCatalogacaoMBean.removerNotaGeral}"
																			reRender="dtTblNotasGerais">
																		<h:graphicImage url="/img/delete.gif" style="border: none;"
																				title="Clique aqui para remover esta nota geral." />
																	</a4j:commandLink>
																</h:column>
															</t:dataTable>
														</a4j:outputPanel>
													
														<span style="float: left; margin-top: 4px;" >
															<a4j:commandLink
																	actionListener="#{solicitacaoCatalogacaoMBean.adicionarNotaGeral}"
																	reRender="dtTblNotasGerais">
																<h:graphicImage url="/img/adicionar.gif" style="border:none"
																		title="Clique aqui para adicionar uma nota geral." />
															</a4j:commandLink>
														</span>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; vertical-align: top;">Notas Teses: &nbsp;&nbsp;&nbsp;</th>
													
													<td>	
														<a4j:outputPanel ajaxRendered="true" style="float: left;">
															<t:dataTable var="notaTese" rowIndexVar="i"
																	id="dtTblNotasTeses" value="#{solicitacaoCatalogacaoMBean.notasTesesDataModel}">
																<h:column>
																	<h:outputText value="#{i + 1}." />
																</h:column>
																<h:column>
																	<h:inputText value="#{solicitacaoCatalogacaoMBean.notasTeses[i]}" size="70" maxlength="200">
																		<a4j:support event="onchange" reRender="previaFicha"></a4j:support>
																	</h:inputText>
																</h:column>
																<h:column rendered="#{solicitacaoCatalogacaoMBean.notasTesesDataModel.rowCount > 0}" >
																	<a4j:commandLink
																			actionListener="#{solicitacaoCatalogacaoMBean.removerNotaTese}"
																			reRender="dtTblNotasTeses">
																		<h:graphicImage url="/img/delete.gif" style="border: none;"
																				title="Clique aqui para remover esta nota tese." />
																	</a4j:commandLink>
																</h:column>
															</t:dataTable>
														</a4j:outputPanel>
													
														<span style="float: left; margin-top: 4px;" >
															<a4j:commandLink
																	actionListener="#{solicitacaoCatalogacaoMBean.adicionarNotaTese}"
																	reRender="dtTblNotasTeses">
																<h:graphicImage url="/img/adicionar.gif" style="border:none"
																		title="Clique aqui para adicionar uma nota tese." />
															</a4j:commandLink>
														</span>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; vertical-align: top;">Notas Bibliográficas: &nbsp;&nbsp;&nbsp;</th>
													
													<td>	
														<a4j:outputPanel ajaxRendered="true" style="float: left;">
															<t:dataTable var="notaBibliografica" rowIndexVar="i"
																	id="dtTblNotasBibliograficas" value="#{solicitacaoCatalogacaoMBean.notasBibliograficasDataModel}">
																<h:column>
																	<h:outputText value="#{i + 1}." />
																</h:column>
																<h:column>
																	<h:inputText value="#{solicitacaoCatalogacaoMBean.notasBibliograficas[i]}" size="70" maxlength="200">
																		<a4j:support event="onchange" reRender="previaFicha"></a4j:support>
																	</h:inputText>
																</h:column>
																<h:column rendered="#{solicitacaoCatalogacaoMBean.notasBibliograficasDataModel.rowCount > 0}" >
																	<a4j:commandLink
																			actionListener="#{solicitacaoCatalogacaoMBean.removerNotaBibliografica}"
																			reRender="dtTblNotasBibliograficas">
																		<h:graphicImage url="/img/delete.gif" style="border: none;"
																				title="Clique aqui para remover esta nota bibliográfica." />
																	</a4j:commandLink>
																</h:column>
															</t:dataTable>
														</a4j:outputPanel>
													
														<span style="float: left; margin-top: 4px;" >
															<a4j:commandLink
																	actionListener="#{solicitacaoCatalogacaoMBean.adicionarNotaBibliografica}"
																	reRender="dtTblNotasBibliograficas">
																<h:graphicImage url="/img/adicionar.gif" style="border:none"
																		title="Clique aqui para adicionar uma nota bibliográfica." />
															</a4j:commandLink>
														</span>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; vertical-align: top;">Notas Conteúdos: &nbsp;&nbsp;&nbsp;</th>
													
													<td>	
														<a4j:outputPanel ajaxRendered="true" style="float: left;">
															<t:dataTable var="notaConteudo" rowIndexVar="i"
																	id="dtTblNotasConteudos" value="#{solicitacaoCatalogacaoMBean.notasConteudosDataModel}">
																<h:column>
																	<h:outputText value="#{i + 1}." />
																</h:column>
																<h:column>
																	<h:inputText value="#{solicitacaoCatalogacaoMBean.notasConteudos[i]}" size="70" maxlength="200">
																		<a4j:support event="onchange" reRender="previaFicha"></a4j:support>
																	</h:inputText>
																</h:column>
																<h:column rendered="#{solicitacaoCatalogacaoMBean.notasConteudosDataModel.rowCount > 0}" >
																	<a4j:commandLink
																			actionListener="#{solicitacaoCatalogacaoMBean.removerNotaConteudo}"
																			reRender="dtTblNotasConteudos">
																		<h:graphicImage url="/img/delete.gif" style="border: none;"
																				title="Clique aqui para remover esta nota conteúdo." />
																	</a4j:commandLink>
																</h:column>
															</t:dataTable>
														</a4j:outputPanel>
													
														<span style="float: left; margin-top: 4px;" >
															<a4j:commandLink
																	actionListener="#{solicitacaoCatalogacaoMBean.adicionarNotaConteudo}"
																	reRender="dtTblNotasConteudos">
																<h:graphicImage url="/img/adicionar.gif" style="border:none"
																		title="Clique aqui para adicionar uma nota conteúdo." />
															</a4j:commandLink>
														</span>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; vertical-align: top;">Assuntos Pessoais: &nbsp;&nbsp;&nbsp;</th>
													
													<td>	
														<a4j:outputPanel ajaxRendered="true" style="float: left;">
															<t:dataTable var="assuntoPessoal" rowIndexVar="i"
																	id="dtTblAssuntosPessoais" value="#{solicitacaoCatalogacaoMBean.assuntosPessoaisDataModel}">
																<h:column>
																	<h:outputText value="#{i + 1}." />
																</h:column>
																<h:column>
																	<h:inputText value="#{solicitacaoCatalogacaoMBean.assuntosPessoais[i]}" size="70" maxlength="200">
																		<a4j:support event="onchange" reRender="previaFicha"></a4j:support>
																	</h:inputText>
																</h:column>
																<h:column rendered="#{solicitacaoCatalogacaoMBean.assuntosPessoaisDataModel.rowCount > 0}" >
																	<a4j:commandLink
																			actionListener="#{solicitacaoCatalogacaoMBean.removerAssuntoPessoal}"
																			reRender="dtTblAssuntosPessoais">
																		<h:graphicImage url="/img/delete.gif" style="border: none;"
																				title="Clique aqui para remover este assunto pessoal." />
																	</a4j:commandLink>
																</h:column>
															</t:dataTable>
														</a4j:outputPanel>
													
														<span style="float: left; margin-top: 4px;" >
															<a4j:commandLink
																	actionListener="#{solicitacaoCatalogacaoMBean.adicionarAssuntoPessoal}"
																	reRender="dtTblAssuntosPessoais">
																<h:graphicImage url="/img/adicionar.gif" style="border:none"
																		title="Clique aqui para adicionar um assunto pessoal." />
															</a4j:commandLink>
														</span>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; vertical-align: top;">Assuntos: &nbsp;&nbsp;&nbsp;</th>
													
													<td>	
														<a4j:outputPanel ajaxRendered="true" style="float: left;">
															<t:dataTable var="assunto" rowIndexVar="i"
																	id="dtTblAssuntos" value="#{solicitacaoCatalogacaoMBean.assuntosDataModel}">
																<h:column>
																	<h:outputText value="#{i + 1}." />
																</h:column>
																<h:column>
																	<h:inputText value="#{solicitacaoCatalogacaoMBean.assuntos[i]}" size="70" maxlength="200">
																		<a4j:support event="onchange" reRender="previaFicha"></a4j:support>
																	</h:inputText>
																</h:column>
																<h:column rendered="#{solicitacaoCatalogacaoMBean.assuntosDataModel.rowCount > 0}" >
																	<a4j:commandLink
																			actionListener="#{solicitacaoCatalogacaoMBean.removerAssunto}"
																			reRender="dtTblAssuntos">
																		<h:graphicImage url="/img/delete.gif" style="border: none;"
																				title="Clique aqui para remover este assunto." />
																	</a4j:commandLink>
																</h:column>
															</t:dataTable>
														</a4j:outputPanel>
													
														<span style="float: left; margin-top: 4px;" >
															<a4j:commandLink
																	actionListener="#{solicitacaoCatalogacaoMBean.adicionarAssunto}"
																	reRender="dtTblAssuntos">
																<h:graphicImage url="/img/adicionar.gif" style="border:none"
																		title="Clique aqui para adicionar um assunto." />
															</a4j:commandLink>
														</span>
													</td>
												</tr>
												<tr>
													<th style="font-weight: normal; vertical-align: top;">Autores Secundários: &nbsp;&nbsp;&nbsp;</th>
													
													<td>	
														<a4j:outputPanel ajaxRendered="true" style="float: left;">
															<t:dataTable var="autorSecundario" rowIndexVar="i"
																	id="dtTblAutoresSecundarios" value="#{solicitacaoCatalogacaoMBean.autoresSecundariosDataModel}">
																<h:column>
																	<h:outputText value="#{i + 1}." />
																</h:column>
																<h:column>
																	<h:inputText value="#{solicitacaoCatalogacaoMBean.autoresSecundarios[i]}" size="70" maxlength="200">
																		<a4j:support event="onchange" reRender="previaFicha"></a4j:support>
																	</h:inputText>
																</h:column>
																<h:column rendered="#{solicitacaoCatalogacaoMBean.autoresSecundariosDataModel.rowCount > 0}" >
																	<a4j:commandLink
																			actionListener="#{solicitacaoCatalogacaoMBean.removerAutorSecundario}"
																			reRender="dtTblAutoresSecundarios">
																		<h:graphicImage url="/img/delete.gif" style="border: none;"
																				title="Clique aqui para remover este ator secundário." />
																	</a4j:commandLink>
																</h:column>
															</t:dataTable>
														</a4j:outputPanel>
													
														<span style="float: left; margin-top: 4px;" >
															<a4j:commandLink
																	actionListener="#{solicitacaoCatalogacaoMBean.adicionarAutorSecundario}"
																	reRender="dtTblAutorSecundario">
																<h:graphicImage url="/img/adicionar.gif" style="border:none"
																		title="Clique aqui para adicionar um autor secundário." />
															</a4j:commandLink>
														</span>
													</td>
												</tr>
											</c:if>
										</table>
									</a4j:outputPanel>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</c:if>
			
			<c:if test="${solicitacaoCatalogacaoMBean.cancelar}">
				<tr>
					<td colspan="2">
						<table class="subFormulario" style="width: 100%">
							<caption>Motivo do Cancelamento</caption>
							<tr>
								<th class="obrigatorio" style="font-weight: normal; padding-right: 13px;">Motivo:</th>
								<td>
									<h:inputTextarea
											id="txtAreaMotivoCancelamento"
											value="#{solicitacaoCatalogacaoMBean.motivoCancelamento}" 
											cols="80" rows="6"
											onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 100);" />
								</td>
							</tr>
							<tr>
								<th style="font-weight: normal;">Caracteres Restantes:</th>
								<td>
									<span id="quantidadeCaracteresDigitados">100</span>/100
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</c:if>
			
			<tfoot>
				<tr>
					<td colspan="2" align="center">
<%-- 						<h:form id="form"> --%>
							<c:if test="${solicitacaoCatalogacaoMBean.atender}">
								<h:commandButton
										id="confirmButton"
										onclick="return confirm('Ao finalizar o atendimento, a ficha catalográfica NÃO poderá mais ser alterada. Deseja continuar?');"
										value="#{solicitacaoCatalogacaoMBean.confirmButton}"
										action="#{solicitacaoCatalogacaoMBean.confirmar}" />
							</c:if>
							<c:if test="${solicitacaoCatalogacaoMBean.cancelar}">
								<h:commandButton
										id="confirmButton"
										value="#{solicitacaoCatalogacaoMBean.confirmButton}"
										action="#{solicitacaoCatalogacaoMBean.confirmar}" />
							</c:if>
							<c:if test="${solicitacaoCatalogacaoMBean.atender}">
								<h:commandButton
										value="Salvar"
										action="#{solicitacaoCatalogacaoMBean.salvar}" />
							</c:if>
							<c:if test="${solicitacaoCatalogacaoMBean.atender || solicitacaoCatalogacaoMBean.cancelar}">
								<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{solicitacaoCatalogacaoMBean.verSolicitacoes}" immediate="true" id="cancelar" />
							</c:if>
							<c:if test="${!solicitacaoCatalogacaoMBean.atender && !solicitacaoCatalogacaoMBean.cancelar}">
								<h:commandButton value="Voltar" action="#{solicitacaoCatalogacaoMBean.verSolicitacoes}" immediate="true" id="Voltar" />
							</c:if>
<%-- 						</h:form> --%>
					</td>
				</tr>
			</tfoot>
	</table>

	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>

</h:form>

</f:view>

<script language="javascript">
<!--
function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
	
	if (field.value.length > maxlimit){
		field.value = field.value.substring(0, maxlimit);
	}else{ 
		document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
	} 
}
-->
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>