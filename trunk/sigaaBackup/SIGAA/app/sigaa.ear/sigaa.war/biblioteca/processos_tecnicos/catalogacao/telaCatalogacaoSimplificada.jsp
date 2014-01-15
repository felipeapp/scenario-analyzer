

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


						
		<%--      Cabeçalho da tabela da página     --%>
					
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
		
		
		<table style="width: 100%;">
			<tr>
				<th style="font-weight: bold; text-align: right; width: 40%; ">Tipo de Catalogação:</th>
				<td style="text-align: left; width: 60%;">
					<h:selectOneMenu id="planilhasCatalogacao" value="#{catalogacaoMBean.idPlanilhaCatalogacaoSimplificada}">
							
							<a4j:support event="onchange" actionListener="#{catalogacaoMBean.alterouPlanilhaCatalogacao}" 
									reRender="divDadosCatalogacaoSimplificada, pnlOperacoes" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}" />
								
						<f:selectItem itemValue="-1" itemLabel="--- SELECIONE ---"/>
						<f:selectItems value="#{catalogacaoMBean.planilhasCatalogacaoSimplificadaComboBox}" />
					</h:selectOneMenu>
				</td>
			</tr>
		</table>				



		<%-- Campo da dados contidos na planilha escolhida que pode ser editados pelos catalogador --%>
		                 
		<rich:dataTable id="dtTblCamposDadosSimplificados"  var="campo" value="#{catalogacaoMBean.dataModelCampos}"
				rendered="#{catalogacaoMBean.idPlanilhaCatalogacaoSimplificada > 0}"
				style="width: 100%;" rowKeyVar="linha" columnsWidth="2%, 2%, 2%, 20%, 10%, 48%, 2%, 2%, 2%, 2%, 2%, 2%, 2%, 2%">
		
			
		
			<rich:column colspan="14" rendered="#{campo.grupoEtiquetaVisivel}" styleClass="estiloCaptionSubFormulario">
				<h:outputText id="outputGrupoEtiqueta" escape="false" value="#{campo.etiqueta.grupo.descricao}"/>
			</rich:column>
				
			
			<rich:subTable  id="dtTblSubCamposDadosSimplificados" value="#{campo.dataModelSubCampos}" var="subcampo" rowKeyVar="linhaInterna">
				
				<%--  Botões para mover os campos de dados    --%>
				<h:column>
					<a4j:commandLink reRender="dtTblCamposDadosSimplificados" id="comdLinkMoverCampoCimaSimplificado" rendered="#{linhaInterna == 0}"
						actionListener="#{catalogacaoMBean.moverCampoDadosCima}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
							<h:graphicImage id="gphicimgMoverCampoCimaSimplificado" url="/img/biblioteca/campo_cima.png" style="border:none"
							title="Clique aqui para mover este campo para cima" />
					</a4j:commandLink>
				</h:column>
				
				<h:column>
					<a4j:commandLink reRender="dtTblCamposDadosSimplificados" id="comdLinkmoverCampoBaixoSimplificado" rendered="#{linhaInterna == 0}"
						actionListener="#{catalogacaoMBean.moverCampoDadosBaixo}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
							<h:graphicImage id="gphicimgMoverCampoBaixoSimplificado" url="/img/biblioteca/campo_baixo.png" style="border:none"
							title="Clique aqui para mover este campo para baixo" />
					</a4j:commandLink>
				</h:column>
				
				
				<%--  A Ajuda da calalogação simplificada    --%>
				<h:column>
	    			<a4j:commandLink id="linkVisualizarAjudaCampoSimplificada" actionListener="#{catalogacaoMBean.montaDadosAjudaDados}" rendered="#{linhaInterna == 0}"
	    					reRender="dialogAjudaCampoMarc" oncomplete="modelPanelAjudaCampoMarc.show(); #{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
						<h:graphicImage id="gphicimgVisualizarAjudaCampoSimplificada" url="/img/prova_mes.png" style="border:none"
							title="Clique aqui para visualizar a ajuda de preenchimento do campo" />					
					</a4j:commandLink>
				</h:column>	
				
				
				
				<rich:column>
					<h:outputText id="outPutDescricaoCampo" style="font-weight:bold; color:gray;" escape="false" value="#{campo.etiqueta.descricao}" rendered="#{linhaInterna == 0}"/>
				</rich:column>			
					
							 	
				<rich:column rendered="#{subcampo != null}">
					<h:outputText id="outPutDescricaoSubCampo" style="color:gray;" escape="false" value="#{subcampo.descricaoSubCampo}"/>		 	
				</rich:column>	
				
				<rich:column rendered="#{subcampo != null}">
					
					<%--
					  --
					  -- A text área onde é digitado os dados dos subcampos 
					  --
					  --%>
					
					<h:inputTextarea value="#{subcampo.dado}"  styleClass="classeTxtAreaDadosSubCampo" id="inputTxtAreaDadosSubCampo"
							disabled="#{subcampo.subCampoAutoridade != null}"
							cols="#{catalogacaoMBean.exibirPainelLateral ?  80 : 110}" rows="1" 
							onkeyup="#{catalogacaoMBean.exibirPainelLateral ?  'resize_HTMLTextArea(this, false);' : 'resize_HTMLTextArea(this, true);'}" 
							onclick="#{catalogacaoMBean.exibirPainelLateral ?  'resize_HTMLTextArea(this, false);' : 'resize_HTMLTextArea(this, true);'}">
						
						<f:attribute name="permiteEspacos" value="true"/>

						<%-- Atualiza de forma automática, sempre que o usuário alterar alguma coisa dentro dos campos 080 e 084  --%>
						    <a4j:support event="onblur" actionListener="#{ catalogacaoMBean.configurarClassificacoes}" 
							reRender="pnlClassificacoes" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_HTMLTextArea(this, false);' : 'resize_HTMLTextArea(this, true);'}"  
							rendered="#{ campo.campoArmazenamentoClassificacaoBibliografica && subcampo.subCampoArmazenamentoClassificacaoBibliografica }"
							/> 
							
					</h:inputTextarea>
							
				</rich:column>	
				
				 
				<%--  Comandos para completar as informações dos campos de dados   --%>
							
				<h:column  rendered="#{subcampo != null}">
					<c:if test="${catalogacaoMBean.tipoCatalogacaoBibliografica}">
				
						<a4j:outputPanel ajaxRendered="true">
			    			
			    			<h:commandLink action="#{catalogacaoMBean.telaBuscaTabelaCutter}" id="cmdLinkBuscaCutterSimplificada"  rendered="#{ campo.etiqueta.tag == '090' && subcampo.subCampoB  }">
			    				<h:graphicImage url="/img/primeira.gif" style="border:none" title="Clique aqui para popular o campo com o valor da tabela cutter" />
			    			</h:commandLink>
			    			
			    			<a4j:commandLink id="cmdLinkBuscaDadosCampo090dSimplificada" actionListener="#{catalogacaoMBean.selecionaCampo090D}" 
			    					rendered="#{ campo.etiqueta.tag == '090' && subcampo.subCampoD  }" reRender="dtTblCamposDadosSimplificados"
									oncomplete="modelPanelValoresCampo090D.show(); #{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
								<h:graphicImage id="gphicimgPopulaCampo" url="/img/primeira.gif" style="border:none" title="Clique aqui para popular o campo" />					
							</a4j:commandLink>
			    			
			    			<% if (Sistema.isSipacAtivo()) { %>
				    			<h:commandLink action="#{catalogacaoMBean.telaBuscaEditorasCadastradasSipac}"  id="cmdLinkBuscaEditorasSimplificada"  rendered="#{ campo.etiqueta.tag == '260' && subcampo.subCampoB }">			        			
				        			<h:graphicImage url="/img/primeira.gif" style="border:none" title="Clique aqui para buscar as editoras cadastradas no #{catalogacaoMBean.nomeSistemaBuscaEditoras}" />
				    			</h:commandLink>
			    			<% } %>
			    			
						</a4j:outputPanel>
					</c:if>
				</h:column>
							
				<%--  ordenar sub campo  --%>
				
				<h:column  rendered="#{subcampo != null}">
					<a4j:commandLink reRender="dtTblCamposDadosSimplificados" id="comdLinkMoverSubCampoCimaSimplificada"
						actionListener="#{catalogacaoMBean.moverSubCampoCima}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
					<h:graphicImage id="gphicimgMoverSubCampoCima" url="/img/biblioteca/subcampo_cima.png" style="border:none"
						title="Clique aqui para mover este sub campo para cima" />
					</a4j:commandLink>
				</h:column>
				
				<h:column  rendered="#{subcampo != null}">
					<a4j:commandLink reRender="dtTblCamposDadosSimplificados" id="comdLinkmoverSubCampoBaixoSimplificada"
						actionListener="#{catalogacaoMBean.moverSubCampoBaixo}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
					<h:graphicImage id="gphicimgMoverSubCampoBaixo" url="/img/biblioteca/subcampo_baixo.png" style="border:none"
						title="Clique aqui para mover este sub campo para baixo" />
					</a4j:commandLink>
				</h:column>
				
				<%--  remover sub campo  --%>
				
				<h:column  rendered="#{subcampo != null}">
					<a4j:commandLink reRender="dtTblCamposDadosSimplificados" id="comdLinkRemoverSubCampoSimplificada"
						actionListener="#{catalogacaoMBean.removeSubCampoDados}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
					<h:graphicImage id="gphicimgAdicionarSubCampo" url="/img/biblioteca/estornar.gif" style="border:none"
						title="Clique aqui para remover este sub campo" />
					</a4j:commandLink>
				</h:column> 
				 
				 
				  
				<%-- Busca na base de autoridades  --%>
				<h:column>
					<h:commandLink id="busaAutorAutorizadoSimplificada" action="#{catalogacaoMBean.buscarAutoridadesPorNomeAutorizadoAutor}" 
								rendered="#{campo.campoPrenchidoComAutoridadesAutores && catalogacaoMBean.tipoCatalogacaoBibliografica && linhaInterna == 0 }">
	    				<h:graphicImage url="/img/buscar.gif" style="border:none" title="Clique aqui para buscar o autor autorizado da autoridade" />
	    			</h:commandLink>
				    			
	    			<h:commandLink id="busaAssuntoAutorizadoSimplificada" action="#{catalogacaoMBean.buscarAutoridadesPorNomeAutorizadoAssunto}" 
	    						rendered="#{campo.campoPrenchidoComAutoridadesAssuntos && catalogacaoMBean.tipoCatalogacaoBibliografica}">
	    				<h:graphicImage url="/img/buscar.gif" style="border:none" title="Clique aqui para buscar o assunto autorizado da autoridade" />
	    			</h:commandLink>
				</h:column>
						
				<%-- duplicar sub campo --%>		
				<h:column>
					<a4j:commandLink reRender="dtTblCamposDadosSimplificados" id="comdLinkDuplicarSubCampoSimplificada"
								actionListener="#{catalogacaoMBean.duplicarSubCampo}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
						<h:graphicImage id="gphicimgDuplicaSubCampoSimplfificada" url="/img/adicionar.gif" style="border:none" 
						title="Clique aqui para duplicar esse sub campo" />
					</a4j:commandLink>
				</h:column>
				
				<%-- duplicar campo dados --%>		
				<h:column>
					<a4j:commandLink reRender="dtTblCamposDadosSimplificados" id="comdLinkDuplicarCampoSimplificada"
								actionListener="#{catalogacaoMBean.duplicarCampo}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}"
								rendered="#{linhaInterna == 0}">
						<h:graphicImage id="gphicimgDuplicaCampoSimplfificada" url="/img/add2.png" style="border:none" 
						title="Clique aqui para duplicar esse campo" />
					</a4j:commandLink>
				</h:column>	
					
				<%-- Apaga este campo de dados --%>
				<h:column>
					<a4j:commandLink reRender="dtTblCamposDadosSimplificados, pnlClassificacoes" styleClass="colunaApagaCampo" id="comdLinkRemoverCampoSimplificada"
								actionListener="#{catalogacaoMBean.removerCampoDados}" 
								rendered="#{linhaInterna == 0}"
								onclick="#{confirmRemover}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
							<h:graphicImage id="gphicimgRemoverCampoSimplificada" url="/img/delete.gif"style="border:none"
								title="Clique aqui para remover esse campo de dados" />
					</a4j:commandLink>
				</h:column>			
				
			</rich:subTable>
			
		</rich:dataTable>



		<%-- A tabela com os campos de dados reservados do sistema os quais o usuário não pode editar --%>
				
		<rich:dataTable  id="dtTblCamposDadosReservadosSimplificada"  styleClass="tabelaCamposDadosCatalogacao" rendered="#{catalogacaoMBean.idPlanilhaCatalogacaoSimplificada > 0}"
			value="#{catalogacaoMBean.camposReservados}" var="campoReservado" columnsWidth="2%, 2%, 2%, 20%, 10%, 48%, 2%, 2%, 2%, 2%, 2%, 2%, 2%, 2%">
	
			<%--  Botões para mover os campos de dados    --%>
			<h:column> </h:column>
			<h:column> </h:column>
			<%-- Ajuda --%>
			<h:column> </h:column>		
					
			<h:column>
				<h:outputText styleClass="outTxtDescricaoEtiquetaSimplificada" escape="false" id="outTxtDescricaoEtiquetaCampoReservado" value="#{campoReservado.etiqueta.descricao}"/>
			</h:column>	
			
			<h:column>
				<rich:dataTable  id="dtTblSubCamposDadosReservador" styleClass="tabelaSubCampos" 
				 	value="#{campoReservado.subCampos}" var="subcampo"  rowKeyVar="numeroLinhaSubCampo" columnClasses="nada, nada, nada, nada, nada, textAlinhadoDireita" columnsWidth="5%, 90%, 2%, 1%, 1% 1%">
				 
				 	<h:column>
						<h:outputText styleClass="inputTxtCodigoSubCampo" id="outPutTxtCodigoSubCampo" value="#{subcampo.descricaoSubCampo}" />
					</h:column>
					
					<h:column>
						<h:outputText value="#{subcampo.dado}"  styleClass="classeTxtAreaDadosSubCampo" id="inputTxtAreaDadosSubCampoReservados" />
					</h:column>
				 
				 </rich:dataTable>	
			</h:column>
	
			<h:column></h:column>
			<h:column></h:column>
			<h:column></h:column>
			<h:column></h:column>
			<h:column></h:column>
			<h:column> </h:column>
			<h:column> </h:column>
			<h:column> </h:column>
	
		</rich:dataTable>

	
		<%-- Tabela que forma o final do formulário que tambem não é dinâmico  --%>
					
		<table id="tlbBotoesDeComandoSimplificada" style="width: 100%; border; 0px;" >
		
			<%-- Adiciona um novo campo --%>
			<tr>
				<td width="100%" align="right">
					
					<a4j:commandLink style="color: #003390;"
								actionListener="#{catalogacaoMBean.carregaCamposPlanilhaSimplificada}" reRender="dtTblCamposDadosCatalograficos, divAdicionadoCamposPlanilha"
								rendered="#{! catalogacaoMBean.usarTelaCatalogacaoCompleta && catalogacaoMBean.idPlanilhaCatalogacaoSimplificada > 0}"
								oncomplete="abreModelPanelBuscarCampoDadosPlanilha(); #{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">				
						<h:outputText value="Adicionar Campo" /> 
					</a4j:commandLink>
					
				</td>
			</tr>
			
		</table>



		<%--    Parte de adição de arquivos digitalizados para o título   --%>
				
		<a4j:outputPanel ajaxRendered="true" >
			
			<t:div style="width:100%; margin-left:auto; margin-right:auto; margin-bottom:10px; margin-top:0px; border: 0px solid; background-color:#F9FBFD; text-align:center;" 
					rendered="#{catalogacaoMBean.tipoCatalogacaoBibliografica && catalogacaoMBean.idPlanilhaCatalogacaoSimplificada > 0}">
					
					<t:div style="width:55%; margin-left:auto; margin-right:auto; margin-bottom: 5px; background-color:#F9FBFD;" >
						<h:outputText escape="false" style="font-weight: bold;" value=" Arquivo Atual: &nbsp;" rendered="#{catalogacaoMBean.nomeArquivoTemporarioDoTitulo != null}"/>
					
						<h:outputText id="outTxtArquivoSubmetidoCatalogacaoSimplificada" value="#{catalogacaoMBean.nomeArquivoTemporarioDoTitulo}" rendered="#{catalogacaoMBean.nomeArquivoTemporarioDoTitulo != null}"/>
						
						
						<h:commandLink actionListener="#{catalogacaoMBean.apagarArquivo}" rendered="#{catalogacaoMBean.nomeArquivoTemporarioDoTitulo != null}" onclick="#{confirmRemover}">	
								<h:graphicImage url="/img/delete_old.gif" title="Apagar Arquivo Digital"/>
								<f:param name="apagarArquivoSumetido" value="#{true}" ></f:param>
						</h:commandLink>
					</t:div>
					
					<t:div style="width:55%; margin-left:auto; margin-right:auto; background-color:#F9FBFD;">
						<h:outputText value="Arquivo Digital:"/>
						<t:inputFileUpload id="arquivoCatalogacaoSimplificada" size="30" valueChangeListener="#{catalogacaoMBean.validaFormatoArquivo}" onchange="submit();" />
					</t:div>
					
			</t:div>
			
		</a4j:outputPanel>
		
		
		
		
		<%-- Os botões de ação no final da tela --%>
		
		<table id="tlbRodapeCamposCatalograficos" width="100%" class="formulario" >
					
			<tfoot>
				<tr>
					<td align="center">
					
						<%-- BOTÕES PARA CRIAÇÃO DE TÍTULOS E AUTORIADDES--%>
					
						<%-- Sempre fica na mesma página --%>
						<h:commandButton id="botaoSalvarCatalogacaoSimplificada" value="Salvar" action="#{catalogacaoMBean.submeterDadosTituloCatalografico}" 
								rendered="#{!catalogacaoMBean.editando && catalogacaoMBean.idPlanilhaCatalogacaoSimplificada > 0}">
							  <f:setPropertyActionListener target="#{catalogacaoMBean.adicionarMaterialInformacional}" value="false" />
							  <f:setPropertyActionListener target="#{catalogacaoMBean.finalizarCatalogacao}" value="false" />
						</h:commandButton>
						
						<%-- RETORNA PARA A PÁGINA INICIAL DO SISTEMA, para Títulos só deve aparecer se não for possível adicionar materiais --%>
						<h:commandButton id="botaoSalvarCatalogarCatalogacaoSimplificada" value="Salvar e Catalogar" action="#{catalogacaoMBean.submeterDadosTituloCatalografico}" 
										rendered="#{!catalogacaoMBean.editando && catalogacaoMBean.idPlanilhaCatalogacaoSimplificada > 0
										&& ( catalogacaoMBean.tipoCatalogacaoAutoridade || (catalogacaoMBean.tipoCatalogacaoBibliografica && ! catalogacaoMBean.catalogacaoComTombamento && ! catalogacaoMBean.catalogacaoMateriaisSemTombamento ) ) }">
							
							<f:setPropertyActionListener target="#{catalogacaoMBean.adicionarMaterialInformacional}" value="false" />			
							<f:setPropertyActionListener target="#{catalogacaoMBean.finalizarCatalogacao}" value="true" />
							<f:setPropertyActionListener target="#{catalogacaoMBean.redirecionaProximaAutoriadeIncompleta}" value="false" />
						</h:commandButton>
						
						<h:commandButton id="botaoCatalogarAdicionarMateriaisCatalogacaoSimplificada" value="Catalogar e Adicionar Materiais" action="#{catalogacaoMBean.submeterDadosTituloCatalografico}" 
										rendered="#{!catalogacaoMBean.editando && catalogacaoMBean.tipoCatalogacaoBibliografica 
												&& catalogacaoMBean.idPlanilhaCatalogacaoSimplificada > 0
												&& (catalogacaoMBean.catalogacaoComTombamento || catalogacaoMBean.catalogacaoMateriaisSemTombamento ) }">
							
							<f:setPropertyActionListener target="#{catalogacaoMBean.adicionarMaterialInformacional}" value="true" />
							<f:setPropertyActionListener target="#{catalogacaoMBean.finalizarCatalogacao}" value="true" />
						</h:commandButton>
					
					
						<%-- No caso de catalogação de Títulos, o botão fica na página de inclusão de materiais (exemplares e fascículos)  --%>
						<%--  <h:commandButton value="Finalizar e Catalogar Próximo Título não Finalizada"></h:commandButton>--%>
						
						<%-- Para o caso da catalogação de autoridades com autoridades não finalizadas  --%>
						<h:commandButton id="botaoSalvarCatalogarEIniciarProximaCatalogacaoSimplificada" value="Salvar, Catalogar e Iniciar Próxima Catalogação" action="#{catalogacaoMBean.submeterDadosTituloCatalografico}" 
										rendered="#{!catalogacaoMBean.editando 
										&& catalogacaoMBean.idPlanilhaCatalogacaoSimplificada > 0
										&& catalogacaoMBean.tipoCatalogacaoAutoridade && catalogacaoMBean.possuiEntiadesNaoFinalizados}" title="Finaliza a catalogação dessa autoridade e volta para a página de autoridades não catalogadas para escolher a próxima">
							
							<f:setPropertyActionListener target="#{catalogacaoMBean.adicionarMaterialInformacional}" value="false" />			
							<f:setPropertyActionListener target="#{catalogacaoMBean.finalizarCatalogacao}" value="true" />
							<f:setPropertyActionListener target="#{catalogacaoMBean.redirecionaProximaAutoriadeIncompleta}" value="true" />
						</h:commandButton>
					
					
						<%-- BOTÕES PARA EDIÇÃO DE TÍTULOS E AUTORIADDES  --%>
					
						<%-- Sempre fica na mesma página --%>
						<h:commandButton id="botaoAtualizarCatalogacaoSimplificada" value="Atualizar" action="#{catalogacaoMBean.submeterDadosTituloCatalografico}" 
								rendered="#{catalogacaoMBean.editando && catalogacaoMBean.idPlanilhaCatalogacaoSimplificada > 0}" >
							<f:setPropertyActionListener target="#{catalogacaoMBean.adicionarMaterialInformacional}" value="false" />
							<f:setPropertyActionListener target="#{catalogacaoMBean.finalizarCatalogacao}" value="false" />
						</h:commandButton>
					
						<%-- RETORNA PARA A PÁGINA INICIAL DO SISTEMA, para Títulos só deve aparecer se não for possível adicionar materiais --%>
						<h:commandButton id="botaoFinalizarAtualizacaoCatalogacaoSimplificada" value="Finalizar Atualização" action="#{catalogacaoMBean.submeterDadosTituloCatalografico}" 
									rendered="#{catalogacaoMBean.editando
											&& catalogacaoMBean.idPlanilhaCatalogacaoSimplificada > 0
											&& ( catalogacaoMBean.tipoCatalogacaoAutoridade || (catalogacaoMBean.tipoCatalogacaoBibliografica && ! catalogacaoMBean.catalogacaoComTombamento && ! catalogacaoMBean.catalogacaoMateriaisSemTombamento ) ) }" >
							<f:setPropertyActionListener target="#{catalogacaoMBean.adicionarMaterialInformacional}" value="false" />
							<f:setPropertyActionListener target="#{catalogacaoMBean.finalizarCatalogacao}" value="true" />
							<f:setPropertyActionListener target="#{catalogacaoMBean.redirecionaProximaAutoriadeIncompleta}" value="false" />
						</h:commandButton>
					
						<h:commandButton id="botaoFinalizarAtualizacaoEAdicionarMateriaisCatalogacaoSimplificada" value="Finalizar Atualização e Adicionar Materiais" action="#{catalogacaoMBean.submeterDadosTituloCatalografico}" 
							rendered="#{catalogacaoMBean.editando && catalogacaoMBean.tipoCatalogacaoBibliografica 
												&& catalogacaoMBean.idPlanilhaCatalogacaoSimplificada > 0
												&& ( catalogacaoMBean.catalogacaoComTombamento || catalogacaoMBean.catalogacaoMateriaisSemTombamento) }" >
												
							<f:setPropertyActionListener target="#{catalogacaoMBean.finalizarCatalogacao}" value="true" />
							<f:setPropertyActionListener target="#{catalogacaoMBean.adicionarMaterialInformacional}" value="true" />
						</h:commandButton>
						
					</td>
				</tr>
				
				<tr>
				
					<td align="center">
						
						<a4j:commandButton id="butaoRemoveCamposVaziosCatalogacaoSimplificada" value="Remover Campos Vazios" 
							rendered="#{catalogacaoMBean.idPlanilhaCatalogacaoSimplificada > 0}"
							actionListener="#{catalogacaoMBean.removerCamposVazios}" 
							reRender="divDadoCatalogacao, dtTblCamposDadosCatalograficos" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}" />
						
						<h:commandButton id="butaoVoltarPagina008CatalogacaoSimplificada" value="<< Voltar" action="#{catalogacaoMBean.voltarPagina008}" rendered="#{catalogacaoMBean.catalogacaoDoZero}" />
						
						<h:commandButton id="butaoVoltarPaginaCatalogacaoCatalogacaoSimplificada" value="<< Voltar" action="#{catalogacaoMBean.voltarDaPaginaCatalogacao}" rendered="#{! catalogacaoMBean.catalogacaoDoZero && ! catalogacaoMBean.catalogacaoDeDefesa }" onclick="#{confirmVoltar}" />
						
						<h:commandButton id="butaoVoltarParaConsultarDefesaCatalogacaoSimplificada" value="<< Voltar" action="#{consultarDefesaMBean.telaConsulta}" rendered="#{ catalogacaoMBean.catalogacaoDeDefesa }" onclick="#{confirmVoltar}"  />
						
						<h:commandButton id="butaoCancelarCatalogacaoSimplificada" value="Cancelar" onclick="#{confirm}" immediate="true" action="#{catalogacaoMBean.cancelar}" />
						
					</td>
				</tr>
				
			</tfoot>
	
		</table>
		
		

</div> <%-- divPainelPrimeFaces   div que simula um painel no prime faces para ficar igual aos paineis do painel lateral --%>