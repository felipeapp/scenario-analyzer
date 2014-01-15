
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"%>
<%@ taglib uri="/tags/ufrn" prefix="ufrn"%>
<%@ taglib uri="/tags/ajax" prefix="ajax"%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk"%>

<%@taglib uri="/tags/rich" prefix="rich"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<%@taglib uri="/tags/jawr" prefix="jwr"%>   
	
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<%@page import="br.ufrn.comum.dominio.Sistema"%>

<div class="divPainelPrimeFaces">
					
						
					<%--      Cabe�alho da tabela da p�gina     --%>
					
					<div class="divCabecalhoPainelPrimeFaces">
						${catalogacaoMBean.captionFormularioCatalogacao} 
						(
						<h:commandLink value="#{catalogacaoMBean.obj.formatoMaterial.descricaoCompleta}" rendered="#{catalogacaoMBean.tipoCatalogacaoBibliografica}"
						title="Editar Formato do Material"
						style="color: white; font-style: italic; text-decoration: underline;  "
						action="#{catalogacaoMBean.alterarFormatoMaterialCatalogacao}">
						</h:commandLink> 
						)
					</div>
				
				
				
				
					<%-- Mostra as informa��es do campos de controle, eles n�o podem ser editados nessa pagina --%>
				
					<t:dataTable id="dtTblCamposControle"  value="#{catalogacaoMBean.camposControleOrdenados}" var="campoControle" rowIndexVar="linha" 
							columnClasses="colunaIcones, colunaIcones, descicaoEtiquetaControle, tagEtiquetaControle, dadosControle, colunaIcones" style="width:100%">
						
						<%-- Colunas para mover os campos de dados, no caso dos campos de controle n�o tem, ent�o fica vazio apenas para ficar alinhado --%>
						<h:column></h:column>
						<h:column></h:column>
						
						<h:column>
							<h:outputText value="#{campoControle.etiqueta.descricao}"/>
						</h:column>
						<h:column>
							<h:commandLink value="#{campoControle.etiqueta.tag}" action="#{catalogacaoMBean.editarCampoControle}">
								<%-- eh necess�rio saber a posicao pois o campo pode repetir  --%>
								<%-- eh o caso do 006 e 007, pode haver varios, nao da para pegar so pela tag --%>
								<f:param name="posicaoCampoControleSelecionado" value="#{linha}" />
								<f:param name="conteudoCampoControleAlteracao" value="#{campoControle.dado}"/>
							</h:commandLink>
						</h:column>
						<h:column>
							<h:outputText value="#{campoControle.dadoParaExibicao}"/>
						</h:column>
						<h:column>
							<a4j:commandLink reRender="dtTblCamposControle" actionListener="#{catalogacaoMBean.removerCampoControle}" 
											onclick="#{confirmRemover}" rendered="#{campoControle.etiqueta.tag != 'LDR' && campoControle.etiqueta.tag != '008'}">
							
									<h:graphicImage id="gphicimgRemoverCampo" url="/img/delete.gif"style="border:none" title="Clique aqui para remover esse campo de controle" />
									<f:param name="posicaoCampoControleRemover" value="#{linha}" ></f:param>
									 
							</a4j:commandLink>
						</h:column>
					</t:dataTable>   
				
					
					
				
					<%-- A tabela dos campos de dados do t�tulo --%>
					
					<rich:dataTable  id="dtTblCamposDadosCatalograficos" width="100%" 
							 styleClass="tabelaCamposDadosCatalogacao"
							 value="#{catalogacaoMBean.dataModelCampos}" var="campo" 
							 columnClasses="nada, nada, descricaoEtiquetaDados, rich-table-cell" 
							 columnsWidth="2%, 2%, 30%, 2%, 4%, 2%, 2%, 50%, 2%, 2%, 2% "> 
							 
						<%-- A ordem das colunas �: bot�o mover cima, bot�o mover baixo, descri�ao etiqueata, ajuda, tag etiqueta, indicador1, indicador2, tabela sub campo, bot�o pesquisar base autoridades, bot�o adicionar subcampo, bot�o remover campo. --%>
						
						
						<%--  Bot�es para mover os campos de dados    --%>
						<h:column>
							<a4j:commandLink reRender="dtTblCamposDadosCatalograficos" id="comdLinkMoverCampoCima"
								actionListener="#{catalogacaoMBean.moverCampoDadosCima}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
							<h:graphicImage id="gphicimgMoverCampoCima" url="/img/biblioteca/campo_cima.png" style="border:none"
								title="Clique aqui para mover este campo para cima" />
							</a4j:commandLink>
						</h:column>
						
						<h:column>
							<a4j:commandLink reRender="dtTblCamposDadosCatalograficos" id="comdLinkmoverCampoBaixo"
								actionListener="#{catalogacaoMBean.moverCampoDadosBaixo}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
							<h:graphicImage id="gphicimgMoverCampoBaixo" url="/img/biblioteca/campo_baixo.png" style="border:none"
								title="Clique aqui para mover este campo para baixo" />
							</a4j:commandLink>
						</h:column>
						
						<%-- Fim dos bot�es que movem os campos --%>
						
						
						
						
						
						<h:column>
							<h:outputText styleClass="outTxtDescricaoEtiqueta" escape="false" id="outTxtDescricaoEtiqueta" value="#{campo.etiqueta.descricao}"/>
						</h:column>	
						
						<h:column>
			    			<a4j:commandLink id="linkVisualizarAjudaCampo" actionListener="#{catalogacaoMBean.montaDadosAjudaDados}" 
			    							reRender="dialogAjudaCampoMarc" oncomplete="modelPanelAjudaCampoMarc.show(); #{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
								<h:graphicImage id="gphicimgVisualizarAjudaCampo" url="/img/prova_mes.png" style="border:none"
									title="Clique aqui para visualizar a ajuda de preenchimento do campo" />					
							</a4j:commandLink>
						</h:column>	
						
						<h:column>
							<h:inputText styleClass="inputTxtTagEtiqueta" id="idInputTxtTagEtiqueta" value="#{campo.etiqueta.tag}" size="4" maxlength="3" autocomplete="off">
								<a4j:support event="onblur" 
									actionListener="#{catalogacaoMBean.buscaEtiqueta}" 
									reRender="dtTblCamposDadosCatalograficos, pnlClassificacoes" 
									oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}"/>
							</h:inputText>
						</h:column>
							
						<h:column>
							<h:inputText styleClass="inputTxtIndicadores" id="inputTxtIndicador1" value="#{campo.indicador1}" size="1" maxlength="1" autocomplete="off" onclick="selecionaValorCampo(this);">
								<f:attribute name="permiteEspacos" value="true"/>
							</h:inputText>
						</h:column>
						
						<h:column>	
							<h:inputText styleClass="inputTxtIndicadores" id="inputTxtIndicador2" value="#{campo.indicador2}" size="1" maxlength="1" autocomplete="off" onclick="selecionaValorCampo(this);">
								<f:attribute name="permiteEspacos" value="true"/>
							</h:inputText>
						</h:column>
					
					
						
						<h:column>
							
							<%-- A tabela interna dos sub campos  --%>
							
							<rich:dataTable  id="dtTblSubCamposDadosCatalograficos" styleClass="tabelaSubCampos" 
							 	value="#{campo.dataModelSubCampos}" var="subcampo"  rowKeyVar="numeroLinhaSubCampo" columnClasses="nada, nada, nada, nada, nada, textAlinhadoDireita" columnsWidth="5%, 90%, 2%, 1%, 1% 1%">
								
								<h:column rendered="#{subcampo != null}">
									<h:inputText styleClass="inputTxtCodigoSubCampo" id="inputTxtCodigoSubCampo" value="#{subcampo.codigo}" size="1" maxlength="1" autocomplete="off" onclick="selecionaValorCampo(this);">
										<a4j:support event="onkeyup" actionListener="#{catalogacaoMBean.mudarSubCampo}" onsubmit="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}"/>
									</h:inputText>
								</h:column>
								
								<h:column  rendered="#{subcampo != null}">
								
									<a4j:outputPanel ajaxRendered="true" style="width: 100%; ">
									
										<%--
										  --
										  -- A text �rea onde � digitado os dados dos subcampos 
										  --
										  --%>
										
										<h:inputTextarea value="#{subcampo.dado}"  styleClass="classeTxtAreaDadosSubCampo" id="inputTxtAreaDadosSubCampo"
												disabled="#{subcampo.subCampoAutoridade != null}"
												cols="#{catalogacaoMBean.exibirPainelLateral ?  80 : 110}" rows="1" 
												onkeyup="#{catalogacaoMBean.exibirPainelLateral ?  'resize_HTMLTextArea(this, false);' : 'resize_HTMLTextArea(this, true);'}" 
												onclick="#{catalogacaoMBean.exibirPainelLateral ?  'resize_HTMLTextArea(this, false);' : 'resize_HTMLTextArea(this, true);'}">
											
											<f:attribute name="permiteEspacos" value="true"/>
	
											<%-- Atualiza de forma autom�tica, sempre que o usu�rio alterar alguma coisa dentro dos campos 080 e 084  --%>
											    <a4j:support event="onblur" actionListener="#{ catalogacaoMBean.configurarClassificacoes}" 
												reRender="pnlClassificacoes" oncomplete="resize_HTMLTextArea(this);"  
												rendered="#{ campo.campoArmazenamentoClassificacaoBibliografica && subcampo.subCampoArmazenamentoClassificacaoBibliografica }"
												/> 
												
										</h:inputTextarea>
										
									</a4j:outputPanel>
									
								</h:column>
								
								
								<%--  Comandos para completar as informa��es dos campos de dados   --%>
							
								<h:column  rendered="#{subcampo != null}">
									<c:if test="${catalogacaoMBean.tipoCatalogacaoBibliografica}">
								
										<a4j:outputPanel ajaxRendered="true">
							    			
							    			<%-- Atualiza as classifica��es semente se o usu�rio clicar no bot�o ao lado, sempre que o usu�rio alterar alguma coisa dentro dos campos 080 e 084 
							    			<a4j:commandLink actionListener="#{catalogacaoMBean.configurarClassificacoes}" reRender="pnlClassificacoes"
							    				rendered="#{ ( campo.etiqueta.tag == '080' || campo.etiqueta.tag == '084' ) && subcampo.subCampoA  }">
							    				<h:graphicImage url="/img/mudar.png" style="border:none" title="Clique aqui para calcular as classifica��es da cataloga��o" />
							    			</a4j:commandLink> --%>
							    			
							    			<h:commandLink action="#{catalogacaoMBean.telaBuscaTabelaCutter}" id="cmdLinkBuscaCutter"  rendered="#{ campo.etiqueta.tag == '090' && subcampo.subCampoB  }">
							    				<h:graphicImage url="/img/primeira.gif" style="border:none" title="Clique aqui para popular o campo com o valor da tabela cutter" />
							    			</h:commandLink>
							    			
							    			<a4j:commandLink id="cmdLinkBuscaDadosCampo090d" actionListener="#{catalogacaoMBean.selecionaCampo090D}" 
							    					rendered="#{ campo.etiqueta.tag == '090' && subcampo.subCampoD  }" reRender="dtTblCamposDadosCatalograficos"
													oncomplete="modelPanelValoresCampo090D.show(); #{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
												<h:graphicImage id="gphicimgPopulaCampo" url="/img/primeira.gif" style="border:none" title="Clique aqui para popular o campo" />					
											</a4j:commandLink>
							    			
							    			<% if (Sistema.isSipacAtivo()) { %>
								    			<h:commandLink action="#{catalogacaoMBean.telaBuscaEditorasCadastradasSipac}"  id="cmdLinkBuscaEditoras"  rendered="#{ campo.etiqueta.tag == '260' && subcampo.subCampoB }">			        			
								        			<h:graphicImage url="/img/primeira.gif" style="border:none" title="Clique aqui para buscar as editoras cadastradas no #{catalogacaoMBean.nomeSistemaBuscaEditoras}" />
								    			</h:commandLink>
							    			<% } %>
							    			
										</a4j:outputPanel>
									</c:if>
								</h:column>
							
								<%--  ordenar sub campo  --%>
								
								<h:column  rendered="#{subcampo != null}">
									<a4j:commandLink reRender="dtTblCamposDadosCatalograficos" id="comdLinkMoverSubCampoCima"
										actionListener="#{catalogacaoMBean.moverSubCampoCima}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
									<h:graphicImage id="gphicimgMoverSubCampoCima" url="/img/biblioteca/subcampo_cima.png" style="border:none"
										title="Clique aqui para mover este sub campo para cima" />
									</a4j:commandLink>
								</h:column>
								
								<h:column  rendered="#{subcampo != null}">
									<a4j:commandLink reRender="dtTblCamposDadosCatalograficos" id="comdLinkmoverSubCampoBaixo"
										actionListener="#{catalogacaoMBean.moverSubCampoBaixo}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
									<h:graphicImage id="gphicimgMoverSubCampoBaixo" url="/img/biblioteca/subcampo_baixo.png" style="border:none"
										title="Clique aqui para mover este sub campo para baixo" />
									</a4j:commandLink>
								</h:column>
								
								<%--  remover sub campo  --%>
								
								<h:column  rendered="#{subcampo != null}">
									<a4j:commandLink reRender="dtTblCamposDadosCatalograficos" id="comdLinkRemoverSubCampo"
										actionListener="#{catalogacaoMBean.removeSubCampoDados}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
									<h:graphicImage id="gphicimgAdicionarSubCampo" url="/img/biblioteca/estornar.gif" style="border:none"
										title="Clique aqui para remover este sub campo" />
									</a4j:commandLink>
								</h:column>
					
							</rich:dataTable>
							
						</h:column>
					
					
					
						<%-- Mais colunas com bot�es de controle  --%>
						<h:column>
							<c:if test="${catalogacaoMBean.tipoCatalogacaoBibliografica}">
								<h:commandLink action="#{catalogacaoMBean.buscarAutoridadesPorNomeAutorizadoAutor}" rendered="#{campo.campoPrenchidoComAutoridadesAutores}">
				    				<h:graphicImage url="/img/buscar.gif" style="border:none" title="Clique aqui para buscar o autor autorizado da autoridade" />
				    			</h:commandLink>
							    			
				    			<h:commandLink action="#{catalogacaoMBean.buscarAutoridadesPorNomeAutorizadoAssunto}" rendered="#{campo.campoPrenchidoComAutoridadesAssuntos}">
				    				<h:graphicImage url="/img/buscar.gif" style="border:none" title="Clique aqui para buscar o assunto autorizado da autoridade" />
				    			</h:commandLink>
							</c:if>
						</h:column>
						
						
						<h:column>
							<a4j:commandLink reRender="dtTblCamposDadosCatalograficos" id="comdLinkAdicionarSubCampo"
										actionListener="#{catalogacaoMBean.adicionarNovoSubCampo}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
								<h:graphicImage id="gphicimgAdicionarSubCampo" url="/img/adicionar.gif" style="border:none" 
								title="Clique aqui para adicionar um sub campo a esse campo de dados" />
							</a4j:commandLink>
						</h:column>
					
					
						<%-- Apaga este campo de dados --%>
						<h:column>
							<a4j:commandLink reRender="dtTblCamposDadosCatalograficos, pnlClassificacoes" styleClass="colunaApagaCampo" id="comdLinkRemoverCampo"
										actionListener="#{catalogacaoMBean.removerCampoDados}" onclick="#{confirmRemover}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
									<h:graphicImage id="gphicimgRemoverCampo" url="/img/delete.gif"style="border:none"
										title="Clique aqui para remover esse campo de dados" />
							</a4j:commandLink>
						</h:column>	
	
					</rich:dataTable>
				
				
				
				
				
				
				
				
				
					<%-- A tabela com os campos de dados reservados do sistema os quais o usu�rio n�o pode editar --%>
				
					<rich:dataTable  id="dtTblCamposDadosReservados"  styleClass="tabelaCamposDadosCatalogacao"
						value="#{catalogacaoMBean.camposReservados}" var="campoReservado" columnsWidth="2%, 2%, 30%, 2%, 4%, 2%, 2%, 50%, 2%, 2%, 2%  ">
				
						<%--  Bot�es para mover os campos de dados    --%>
						<h:column>
						
						</h:column>
						
						<h:column>
				
						</h:column>
								
						<h:column>
							<h:outputText styleClass="outTxtDescricaoEtiqueta" escape="false" id="outTxtDescricaoEtiquetaCampoReservado" value="#{campoReservado.etiqueta.descricao}"/>
						</h:column>	
						
						<%-- Ajuda --%>
						<h:column>
				
						</h:column>
				
						
						<h:column>
							<h:outputText styleClass="inputTxtTagEtiqueta" id="outputTxtTagEtiquetaCampoReservado" value="#{campoReservado.etiqueta.tag}" />
						</h:column>
							
						<h:column>
							<h:outputText styleClass="inputTxtIndicadores" id="outputTxtIndicador1CampoReservado" value="#{campoReservado.indicador1}" />
						</h:column>
						
						<h:column>	
							<h:outputText styleClass="inputTxtIndicadores" id="outputTxtIndicador2CampoReservado" value="#{campoReservado.indicador2}" />
						</h:column>
						
						
						<h:column>
							<rich:dataTable  id="dtTblSubCamposDadosReservador" styleClass="tabelaSubCampos" 
							 	value="#{campoReservado.subCampos}" var="subcampo"  rowKeyVar="numeroLinhaSubCampo" columnClasses="nada, nada, nada, nada, nada, textAlinhadoDireita" columnsWidth="5%, 90%, 2%, 1%, 1% 1%">
							 
							 	<h:column>
									<h:outputText styleClass="inputTxtCodigoSubCampo" id="outPutTxtCodigoSubCampo" value="#{subcampo.codigo}" />
								</h:column>
								
								<h:column>
									<h:outputText value="#{subcampo.dado}"  styleClass="classeTxtAreaDadosSubCampo" id="inputTxtAreaDadosSubCampoReservados" />
								</h:column>
							 
							 </rich:dataTable>	
						</h:column>
				
						<h:column>
						</h:column>
				
						<h:column>
						</h:column>
						
						<h:column>
						</h:column>
						
						<h:column>
						</h:column>
				
						<%-- Mais colunas com botoes de controle  --%>
						<h:column>
						
						</h:column>
					
					
						<%-- Apaga este campo de dados --%>
						<h:column>
						
						</h:column>	
				
					</rich:dataTable>
				
				
				
				
					<%-- Tabela que forma o final do formul�rio que tambem n�o � din�mico  --%>
					
					<table id="tlbBotoesDeComando" width="100%" class="formulario" >
					
						<%-- Adiciona um novo campo de dados --%>
						<tr>
							<td width="100%" align="right">
								<a4j:commandLink id="adicionaNovoCampoDados" value="Adicionar Campo de Dados" reRender="dtTblCamposDadosCatalograficos"
										actionListener="#{catalogacaoMBean.adicionarNovoCampoDados}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}" styleClass="botaoAdicionaCampoDados">
									<h:graphicImage id="gphicimgAdicionarCampoDados" url="/img/add2.png" style="border:none"
										title="Clique aqui para adicionar um novo campo de dados" />					
								</a4j:commandLink>
							</td>
						</tr>
					
						<%-- Adiciona um novo campo de controle, esse � em uma nova pagina porque os campos de controle seguem regras diferentes --%>
						<tr>
							<td width="100%" align="right">
							
								<a onclick="modelPanelControle.show();" style="font-weight: bold; color: #003390; cursor: pointer;"> 
									Adicionar Campo de Controle
									<h:graphicImage url="/img/add2.png" style="border:none" title="Clique aqui para adicionar um novo campo de controle"/>
								 </a>
							</td>
						</tr>
						
					</table>
						
						
						
						
						
					<%--    Parte de adi��o de arquivos digitalizados para o t�tulo   --%>
				
					<a4j:outputPanel ajaxRendered="true" >
						
						<t:div style="width:100%; margin-left:auto; margin-right:auto; margin-bottom:10px; margin-top:0px; border: 0px solid; background-color:#F9FBFD;" 
								rendered="#{catalogacaoMBean.tipoCatalogacaoBibliografica}">
								
								<t:div style="width:55%; margin-left:auto; margin-right:auto; margin-bottom: 5px; background-color:#F9FBFD;" >
									<h:outputText escape="false" style="font-weight: bold;" value=" Arquivo Atual: &nbsp;" rendered="#{catalogacaoMBean.nomeArquivoTemporarioDoTitulo != null}"/>
								
									<h:outputText id="outTxtArquivoSubmetido" value="#{catalogacaoMBean.nomeArquivoTemporarioDoTitulo}" rendered="#{catalogacaoMBean.nomeArquivoTemporarioDoTitulo != null}"/>
									
									
									<h:commandLink actionListener="#{catalogacaoMBean.apagarArquivo}" rendered="#{catalogacaoMBean.nomeArquivoTemporarioDoTitulo != null}" onclick="#{confirmRemover}">	
											<h:graphicImage url="/img/delete_old.gif" title="Apagar Arquivo Digital"/>
											<f:param name="apagarArquivoSumetido" value="#{true}" ></f:param>
									</h:commandLink>
								</t:div>
								
								<t:div style="width:55%; margin-left:auto; margin-right:auto; background-color:#F9FBFD;">
									<h:outputText value="Arquivo Digital:"/>
									<t:inputFileUpload id="arquivo" size="30" valueChangeListener="#{catalogacaoMBean.validaFormatoArquivo}" onchange="submit();" />
								</t:div>
								
						</t:div>
						
					</a4j:outputPanel>
					
					
						
						
					<table id="tlbRodapeCamposCatalograficos" width="100%" class="formulario" >
					
						<tfoot>
							<tr>
								<td align="center">
								
									<%-- BOT�ES PARA CRIA��O DE T�TULOS E AUTORIADDES--%>
								
								
									<%-- Sempre fica na mesma p�gina --%>
									<h:commandButton id="botaoSalvar" value="Salvar" action="#{catalogacaoMBean.submeterDadosTituloCatalografico}" rendered="#{!catalogacaoMBean.editando}">
										  <f:setPropertyActionListener target="#{catalogacaoMBean.adicionarMaterialInformacional}" value="false" />
										  <f:setPropertyActionListener target="#{catalogacaoMBean.finalizarCatalogacao}" value="false" />
									</h:commandButton>
									
									<%-- RETORNA PARA A P�GINA INICIAL DO SISTEMA, para T�tulos s� deve aparecer se n�o for poss�vel adicionar materiais --%>
									<h:commandButton id="botaoSalvarCatalogar" value="Salvar e Catalogar" action="#{catalogacaoMBean.submeterDadosTituloCatalografico}" 
													rendered="#{!catalogacaoMBean.editando 
													&& ( catalogacaoMBean.tipoCatalogacaoAutoridade || (catalogacaoMBean.tipoCatalogacaoBibliografica && ! catalogacaoMBean.catalogacaoComTombamento && ! catalogacaoMBean.catalogacaoMateriaisSemTombamento ) ) }">
										
										<f:setPropertyActionListener target="#{catalogacaoMBean.adicionarMaterialInformacional}" value="false" />			
										<f:setPropertyActionListener target="#{catalogacaoMBean.finalizarCatalogacao}" value="true" />
										<f:setPropertyActionListener target="#{catalogacaoMBean.redirecionaProximaAutoriadeIncompleta}" value="false" />
									</h:commandButton>
									
									<h:commandButton id="botaoCatalogarAdicionarMateriais" value="Catalogar e Adicionar Materiais" action="#{catalogacaoMBean.submeterDadosTituloCatalografico}" 
													rendered="#{!catalogacaoMBean.editando && catalogacaoMBean.tipoCatalogacaoBibliografica 
															&& (catalogacaoMBean.catalogacaoComTombamento || catalogacaoMBean.catalogacaoMateriaisSemTombamento ) }">
										
										<f:setPropertyActionListener target="#{catalogacaoMBean.adicionarMaterialInformacional}" value="true" />
										<f:setPropertyActionListener target="#{catalogacaoMBean.finalizarCatalogacao}" value="true" />
									</h:commandButton>
								
								
									<%-- No caso de cataloga��o de T�tulos, o bot�o fica na p�gina de inclus�o de materiais (exemplares e fasc�culos)  --%>
									<%--  <h:commandButton value="Finalizar e Catalogar Pr�ximo T�tulo n�o Finalizada"></h:commandButton>--%>
									
									<%-- Para o caso da cataloga��o de autoridades com autoridades n�o finalizadas  --%>
									<h:commandButton id="botaoSalvarCatalogarEIniciarProxima" value="Salvar, Catalogar e Iniciar Pr�xima Cataloga��o" action="#{catalogacaoMBean.submeterDadosTituloCatalografico}" 
													rendered="#{!catalogacaoMBean.editando && catalogacaoMBean.tipoCatalogacaoAutoridade && catalogacaoMBean.possuiEntiadesNaoFinalizados}" title="Finaliza a cataloga��o dessa autoridade e volta para a p�gina de autoridades n�o catalogadas para escolher a pr�xima">
										
										<f:setPropertyActionListener target="#{catalogacaoMBean.adicionarMaterialInformacional}" value="false" />			
										<f:setPropertyActionListener target="#{catalogacaoMBean.finalizarCatalogacao}" value="true" />
										<f:setPropertyActionListener target="#{catalogacaoMBean.redirecionaProximaAutoriadeIncompleta}" value="true" />
									</h:commandButton>
								
								
									<%-- BOT�ES PARA EDI��O DE T�TULOS E AUTORIADDES  --%>
								
									<%-- Sempre fica na mesma p�gina --%>
									<h:commandButton id="botaoAtualizar" value="Atualizar" action="#{catalogacaoMBean.submeterDadosTituloCatalografico}" rendered="#{catalogacaoMBean.editando}" >
										<f:setPropertyActionListener target="#{catalogacaoMBean.adicionarMaterialInformacional}" value="false" />
										<f:setPropertyActionListener target="#{catalogacaoMBean.finalizarCatalogacao}" value="false" />
									</h:commandButton>
								
									<%-- RETORNA PARA A P�GINA INICIAL DO SISTEMA, para T�tulos s� deve aparecer se n�o for poss�vel adicionar materiais --%>
									<h:commandButton id="botaoFinalizarAtualizacao" value="Finalizar Atualiza��o" action="#{catalogacaoMBean.submeterDadosTituloCatalografico}" 
												rendered="#{catalogacaoMBean.editando
														&& ( catalogacaoMBean.tipoCatalogacaoAutoridade || (catalogacaoMBean.tipoCatalogacaoBibliografica && ! catalogacaoMBean.catalogacaoComTombamento && ! catalogacaoMBean.catalogacaoMateriaisSemTombamento ) ) }" >
										<f:setPropertyActionListener target="#{catalogacaoMBean.adicionarMaterialInformacional}" value="false" />
										<f:setPropertyActionListener target="#{catalogacaoMBean.finalizarCatalogacao}" value="true" />
										<f:setPropertyActionListener target="#{catalogacaoMBean.redirecionaProximaAutoriadeIncompleta}" value="false" />
									</h:commandButton>
								
									<h:commandButton id="botaoFinalizarAtualizacaoEAdicionarMateriais" value="Finalizar Atualiza��o e Adicionar Materiais" action="#{catalogacaoMBean.submeterDadosTituloCatalografico}" 
										rendered="#{catalogacaoMBean.editando && catalogacaoMBean.tipoCatalogacaoBibliografica 
															&& ( catalogacaoMBean.catalogacaoComTombamento || catalogacaoMBean.catalogacaoMateriaisSemTombamento) }" >
															
										<f:setPropertyActionListener target="#{catalogacaoMBean.finalizarCatalogacao}" value="true" />
										<f:setPropertyActionListener target="#{catalogacaoMBean.adicionarMaterialInformacional}" value="true" />
									</h:commandButton>
									
								</td>
							</tr>
							
							<tr>
							
								<td align="center">
								
									<a4j:commandButton id="butaoOrdenaCampos" value="Ordenar Campos" actionListener="#{catalogacaoMBean.ordenaCamposDados}" 
										reRender="divDadoCatalogacao, dtTblCamposDadosCatalograficos" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}" />
									
									<a4j:commandButton id="butaoValidaCampos" value="Validar Campos" actionListener="#{catalogacaoMBean.validaInformacoesMARCCampos}"
										oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}" />
									
									<a4j:commandButton id="butaoRemoveCamposVazios" value="Remover Campos Vazios" actionListener="#{catalogacaoMBean.removerCamposVazios}" 
										reRender="divDadoCatalogacao, dtTblCamposDadosCatalograficos" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}" />
									
									<h:commandButton id="butaoVoltarPagina008" value="<< Voltar" action="#{catalogacaoMBean.voltarPagina008}" rendered="#{catalogacaoMBean.catalogacaoDoZero}" />
									
									<h:commandButton id="butaoVoltarPaginaCatalogacao" value="<< Voltar" action="#{catalogacaoMBean.voltarDaPaginaCatalogacao}" rendered="#{! catalogacaoMBean.catalogacaoDoZero && ! catalogacaoMBean.catalogacaoDeDefesa }" onclick="#{confirmVoltar}" />
									
									<h:commandButton id="butaoVoltarParaConsultarDefesa" value="<< Voltar" action="#{consultarDefesaMBean.telaConsulta}" rendered="#{ catalogacaoMBean.catalogacaoDeDefesa }" onclick="#{confirmVoltar}"  />
									
									<h:commandButton id="butaoCancelar" value="Cancelar" onclick="#{confirm}" immediate="true" action="#{catalogacaoMBean.cancelar}" />
									
								</td>
							</tr>
							
						</tfoot>
				
					</table>
				
				
</div> <%-- divPainelPrimeFaces   div que simula um painel no prime faces para ficar igual aos paineis do painel lateral --%>
				
				