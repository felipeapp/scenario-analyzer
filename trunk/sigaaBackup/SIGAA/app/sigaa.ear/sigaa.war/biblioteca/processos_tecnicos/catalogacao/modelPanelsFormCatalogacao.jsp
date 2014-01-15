	
	<%-- Models panels utilizados na página de catalogação --%>
	
	<%-- 
	     Re Definição das tag utilizadas, apenas para não ficar dando erro no momento do desenvolvimento
	     Já que as tag vão estão na página onde essa é incluída   --%>
	     
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
	
	<p:resources />

	<link rel="stylesheet" type="text/css" href="/sigaa/css/primefaces_skin.css" />

	<style type="text/css">
		
		/* Os estilos para a model panel do campo 090$d */
		
		.valorCampo090d{
			width: 99%;
		}
		
		.selecionarCampo090d{
			width: 1%;
		}
		
		.fecharModelPanel{
			font-weight: bold; 
			color: #003390; 
			cursor: pointer;
			text-align: center;
			background: none repeat scroll 0 0 #C8D5EC;
		}
		
		.footerModelPanel{
			text-align: center;
			background-color: #C4D2EB;
		}
		
	</style>


	<%-- Painel que bloqueia a tela a cada requisição ajax para forçar o usuário a esperar a requisição acabar e evitar erros  --%>
	<p:dialog id="modelAguarde" header="Por favor, aguarde." widgetVar="modelPanelAguarde" modal="true" width="250" height="100">
		<h:outputText value="Processando ..."></h:outputText>
       	<h:graphicImage value="/img/indicator.gif"></h:graphicImage>
	</p:dialog>



	<%-- Model panel para o usuário configurar suas preferência na tela de catalogação  --%>
	<p:dialog header="Configurações da Tela de Catalogação" widgetVar="modelPanelConfiguracoesTela" modal="true" width="500" height="150"> 
		
		<h:panelGrid style="width: 100%;" columns="2" footerClass="footerModelPanel">
			<f:facet name="header">
			   <h:outputText value=" Configurações do Usuário:  #{usuario.nome}"/>
			</f:facet>
			
		
			<h:outputLabel for="checkExibirPainelLateral" value="Exibir Painel Lateral:" />
	  		<h:selectBooleanCheckbox id="checkExibirPainelLateral" value="#{catalogacaoMBean.configuracoesTela.exibirPainelLateral}" />
	 		<h:outputLabel for="checkUsarTelaCompleta" value="Usar Tela Catalogação Completa:" />
	 		<h:selectBooleanCheckbox id="checkUsarTelaCompleta" value="#{catalogacaoMBean.configuracoesTela.usarTelaCatalogacaoCompleta}" />
			
			
			<f:facet name="footer">
		  		
		  		 <a4j:commandButton id="cmdButtonSalvarConfiguracoesTela" value="Salvar Configurações" 
		  		 				actionListener="#{catalogacaoMBean.salvarConfiguracoesTelasCatalogacao}" 
	    					reRender="divGeralFormCatalogacao"
							onclick="modelPanelConfiguracoesTela.hide();"
							oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
				</a4j:commandButton>
				
		  	</f:facet>
				
		</h:panelGrid>		
				
	</p:dialog>



	<%-- Painel para adiconar um campo de dados já escolhendo os sub campos que o campo de dados vai ter --%>
	
	<p:dialog id="painelBuscaCamposDadosESubCampo" header="Buscar Campos de Dados" widgetVar="modelPanelBuscarCamposDados" modal="true" width="750" height="600" >  
     		
	       
	        <rich:dataTable id="tableMensagens" var="mensagem" value="#{catalogacaoMBean.mensagensModelPanel}"
	        		style="width: 50%; margin-left: auto; margin-right: auto; margin-bottom: 30px;">
	        	<rich:column>
	        		<h:outputText value="#{mensagem}" style="color red; font-weight:bold;"/>
	        	</rich:column>
	        </rich:dataTable>
	     
	     	<h:panelGrid id="camposEntradaEtiquetaBuscaCampoDadosCompleto" columns="3" style="width: 50%; margin-left: auto; margin-right: auto; margin-bottom: 30px; ">
			        <h:outputLabel value="Campo: " /> 
			        
			        <h:inputText id="inputTxtTagEtiquetaBuscaCampoDadosCompleto" value="#{catalogacaoMBean.tagEtiquetaCampoDeDadosCompleto}" size="4" maxlength="3"
			        	onkeypress="return realizaAcaoTeclaEnter(this, event, 'fromDadosCatalograficos:cmdBuscaCampoDadosCompleto');" >
			        </h:inputText>	 
			        
			        <a4j:commandButton id="cmdBuscaCampoDadosCompleto" value="Buscar" actionListener="#{catalogacaoMBean.buscarCampoDeDadosCompleto}" 
			        	reRender="gridInformacaoCampo, tableEscolheSubCampos, tableMensagens, divDescricaoCampoDados" oncomplete="document.getElementById('fromDadosCatalograficos:inputTxtTagEtiquetaBuscaCampoDadosCompleto').focus();">
			        </a4j:commandButton>
	     	</h:panelGrid>	
	        
	        <t:div id="divDescricaoCampoDados" style="background-color: #C8D5EC;">
		        <h:panelGrid id="gridInformacaoCampo" columns="2" style="width: 50%; margin-left: auto; margin-right: auto; font-weight: bold;">
			        <h:outputText value="#{catalogacaoMBean.campoDadosBuscaCompleta.etiqueta.tag}"/> 
			        <h:outputText escape="false" value="#{catalogacaoMBean.campoDadosBuscaCompleta.etiqueta.descricao}"/> 
	      		</h:panelGrid>
	        </t:div>
	        
	        <div style="overflow: scroll; height: 400px;">
		        <rich:dataTable id="tableEscolheSubCampos" var="subCampo" value="#{catalogacaoMBean.campoDadosBuscaCompleta.subCampos}"
		        				style="width: 100%;" columnClasses="textCentralizado, textAlinhadoEsquerda, iconeRemoveSubCampoModelPanel">
		        	<rich:column>
		        		<h:outputText value="#{subCampo.codigo}"/>
		        	</rich:column>
		        	<rich:column>
		        		<h:outputText value="#{subCampo.descricaoSubCampo}"/>
		        	</rich:column>
		        	<rich:column>
		        		 <a4j:commandLink id="comdLinkRemoverSubCamo" reRender="tableEscolheSubCampos" actionListener="#{catalogacaoMBean.removeSubCampoDadosCompleto}">
		        		 	<f:attribute name="codigoSubCampoCompleto" value="#{subCampo.codigo}"></f:attribute>
		        		 	<h:graphicImage value="/img/biblioteca/estornar.gif" style="overflow: visible;" /> :Remover
		        		 </a4j:commandLink>
		        	</rich:column>
		        </rich:dataTable>
	         </div>
	         
	         
	         <%-- Aqui é para redenrizar o form principal para adicionar os campos  --%>
	        <t:div id="divBotaoAdicionarCampoDados" style="background-color: #C8D5EC; width:100%; text-align:center;">
	        	
	        	<a4j:commandButton id="cmdButtonAdicionarCampoDadosComSubCamposAjax" value="Adicionar" 
	        		actionListener="#{catalogacaoMBean.adicionaNovoCampoDadosCompleto}" oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}"
	        		onclick="modelPanelBuscarCamposDados.hide();" reRender="dtTblCamposDadosCatalograficos, painelBuscaCamposDadosESubCampo">
	        	</a4j:commandButton>	
	        
	      	</t:div>
	      
	       
		</p:dialog>  
	

	<t:div id="divAdicionadoCamposPlanilha">
		<p:dialog header="Adicionar Campo" widgetVar="modelPanelBuscarCamposDadosPlanilha" modal="true" width="700" height="500" > 
		
			<c:if test="${catalogacaoMBean.tituloTemp != null }">
				<table>
					<c:forEach var="campoDados" items="#{catalogacaoMBean.tituloTemp.camposDadosNaoReservados}">
						
						<tr>
							<c:if test="${campoDados.grupoEtiquetaVisivel}">
								<td colspan="2" style="width: 100%;" class="estiloCaptionSubFormulario">
									${campoDados.etiqueta.grupo.descricao}
								</td>
							</c:if>
						</tr>
						
						<tr onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
							
							<td style="width: 98%; text-align: left;">
								${campoDados.etiqueta.descricao} ${campoDados.etiqueta.descricao} &nbsp&nbsp&nbsp
							</td>
							
							<td width="2%" style="text-align: right;">
								<h:commandLink styleClass="noborder" title="Selecionar o campo" actionListener="#{catalogacaoMBean.adicionarCampoPlanilha}">
									<f:param name="identificadorCampoDadosTemp" value="#{campoDados.identificadorTemp}"/>
									<h:graphicImage value="/img/seta.gif"/>
								</h:commandLink>
							</td>
			
						</tr>						
			
			
					</c:forEach>
				</table>
			</c:if>
		
		</p:dialog>
	</t:div>

	
	<c:if test="${catalogacaoMBean.tipoCatalogacaoBibliografica}">


		<p:dialog header="Valores do Sub Campo 090$d" widgetVar="modelPanelValoresCampo090D" modal="true" width="500" height="200"> 
				
				<h:dataTable var="valor" value="#{catalogacaoMBean.dadosCampo090D}" style="width: 100%;" rowClasses="linhaPar, linhaImpar" columnClasses="valorCampo090d, selecionarCampo090d" footerClass="fecharModelPanel">
					<h:column>
		        		<h:outputText value="#{valor}"/>
		        	</h:column>
		        	<h:column>
		        		<a4j:commandLink id="cmdLinkBuscaDadosCampo090d" actionListener="#{catalogacaoMBean.atribuiValorCampo090D}" 
		    					reRender="dtTblCamposDadosCatalograficos, dtTblCamposDadosSimplificados"
								onclick="modelPanelValoresCampo090D.hide();"
								oncomplete="#{catalogacaoMBean.exibirPainelLateral ?  'resize_AllTextArea(false);' : 'resize_AllTextArea(true);'}">
							<f:param name="valorCampo090D" value="#{valor}"/>
							<h:graphicImage value="/img/seta.gif"/>					
						</a4j:commandLink>
		        	</h:column>
		        	
		        	<f:facet name="footer">
		        		<h:column>
		        			<a onclick="modelPanelValoresCampo090D.hide();"> 
								Fechar
							 </a>
		        		</h:column>
		        	</f:facet>
		        	
				</h:dataTable>
				
		</p:dialog>



		<p:dialog header="Campos de Controle" widgetVar="modelPanelControle" modal="true" width="550" height="250"> 
				
				<table class="formulario" width="100%" style="border: 0;">
					
					<%-- Estah fixo aqui porque como as etiquetas de controle fazem parte de um padrao --%>
					<%-- Niguem pode mudar isso, entao nao precisa ficar buscando no banco.  --%>
					
					<tr class="linhaPar">
						<td style="text-align:left">LDR - LÍDER (NR)</td>
						<td width="1%">
							<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" actionListener="#{catalogacaoMBean.adicionarNovoCampoControle}">
								<f:param name="tagEscolhidaAdicaoCampoControle" value="LDR"/>
								<h:graphicImage value="/img/seta.gif"/>
							</h:commandLink>
						</td>
					</tr>
					<tr class="linhaImpar">
						<td style="text-align:left">001 - NÚMERO DE CONTROLE (NR) </td>
						<td>
							<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" actionListener="#{catalogacaoMBean.adicionarNovoCampoControle}">
								<f:param name="tagEscolhidaAdicaoCampoControle" value="001"/>
								<h:graphicImage value="/img/seta.gif"/>
							</h:commandLink>
						</td>
					</tr>
					<tr class="linhaPar">
						<td style="text-align:left">003 - IDENTIFICADOR DO NÚMERO DE CONTROLE (NR) </td>
						<td>
							<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" actionListener="#{catalogacaoMBean.adicionarNovoCampoControle}">
								<f:param name="tagEscolhidaAdicaoCampoControle" value="003"/>
								<h:graphicImage value="/img/seta.gif"/>
							</h:commandLink>
						</td>
					</tr>
					<tr class="linhaImpar">
						<td style="text-align:left">005 - DATA E HORA DA ÚLTIMA INTERVENÇÃO (NR) </td>
						<td>
							<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" actionListener="#{catalogacaoMBean.adicionarNovoCampoControle}">
								<f:param name="tagEscolhidaAdicaoCampoControle" value="005"/>
								<h:graphicImage value="/img/seta.gif"/>
							</h:commandLink>
						</td>
					</tr>
					<tr class="linhaPar">
						<td style="text-align:left">006 - CAMPOS DE TAMANHO FIXO (R)</td>
						<td>
							<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" actionListener="#{catalogacaoMBean.adicionarNovoCampoControle}">
								<f:param name="tagEscolhidaAdicaoCampoControle" value="006"/>
								<h:graphicImage value="/img/seta.gif"/>
							</h:commandLink>
						</td>
					</tr>
					<tr class="linhaImpar">
						<td style="text-align:left">007 - CAMPOS FIXOS DE DESCRIÇÃO FÍSICA (R)</td>
						<td>
							<a onclick="modelPanelControle.hide(); modelPanelCampoControle007.show();" style="font-weight: bold; color: #003390; cursor: pointer;"> 
								<h:graphicImage value="/img/seta.gif" style="cursor:pointer"/>
							 </a>
						
						</td>
					</tr>
					<tr class="linhaPar">
						<td style="text-align:left">008 - CAMPOS FIXOS DE DADOS (NR)</td>
						<td>
							<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" actionListener="#{catalogacaoMBean.adicionarNovoCampoControle}">
								<f:param name="tagEscolhidaAdicaoCampoControle" value="008"/>
								<h:graphicImage value="/img/seta.gif"/>
							</h:commandLink>
						</td>
					</tr>
					
					<tfoot>
						<tr>
							<td colspan="2">
							 <h:panelGroup>
								 <a onclick="modelPanelControle.hide();" style="font-weight: bold; color: #003390; cursor: pointer;"> 
									Fechar
								 </a>
	            			</h:panelGroup>
							</td>
						</tr>
					</tfoot>
					
				</table>
		
		</p:dialog>
        
		
	
	
	
	
	
		<%-- ModalPanel que eh mostrado para o usuario escolher a Categoria do Material                                --%>
		<%-- Soh nos casos que o usuario escolher o campo 007 cujos dados dependem da Categoria de Material  --%>
		
		<p:dialog header="Categoria do Material" widgetVar="modelPanelCampoControle007" modal="true" width="400" height="450" > 
			<table class="formulario" width="100%" style="border: 0;">
					
					<c:forEach var="categoriaMaterial" items="#{catalogacaoMBean.allCategoriasMateriaisAtivas}" varStatus="loop"> 
						
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							
							<td style="text-align:left">${categoriaMaterial.codigo } - ${categoriaMaterial.descricao} </td>
							
							<td width="1%">
							   
								<h:commandLink styleClass="noborder" title="Selecionar a categoria do material" 
														actionListener="#{catalogacaoMBean.adicionarNovoCampoControle}">
									<h:graphicImage value="/img/seta.gif"  style="border:none"/>
									
									<f:param name="codigoCategoriaMaterial" value="#{categoriaMaterial.codigo}"/>
									
								</h:commandLink>
							</td>
							
						</tr>
						
					</c:forEach>
					
					<tfoot>
						<tr>
							<td colspan="2">
							 	<h:panelGroup>
							 		 <a onclick="modelPanelCampoControle007.hide();" style="font-weight: bold; color: #003390; cursor: pointer;"> 
										Fechar
									 </a>
	            				</h:panelGroup>
							</td>
						</tr> 
					</tfoot>
				
				</table>
		</p:dialog>
		
		
	
	</c:if>
	
	
	
	
	<%-- ModalPanel que eh mostrado para o usuario escolher o campo de controle que quer acrescentar   --%>
	<%-- Para autoridades os campo de controle sao outros, não existe o 006 e 007                      --%>
		
	<c:if test="${catalogacaoMBean.tipoCatalogacaoAutoridade}">
	
		<p:dialog header="Campos de Controle" widgetVar="modelPanelControle" modal="true" width="550" height="200" > 
				<table class="formulario" width="100%" style="border: 0;">
					
					<%-- Esta fixo aqui porque como as etiquetas de controle fazem parte de um padrão --%>
					<%-- ninguém poderá mudar isto, não havendo a necessidade de nova busca no banco.  --%>
					
					<tr class="linhaPar">
						<td style="text-align:left">LDR - LÍDER (NR)</td>
						<td width="1%">
							<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" actionListener="#{catalogacaoMBean.adicionarNovoCampoControle}">
								<f:param name="tagEscolhidaAdicaoCampoControle" value="LDR"/>
								<h:graphicImage value="/img/seta.gif"/>
							</h:commandLink>
						</td>
					</tr>
					<tr class="linhaImpar">
						<td style="text-align:left">001 - NÚMERO DE CONTROLE (NR) </td>
						<td>
							<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" actionListener="#{catalogacaoMBean.adicionarNovoCampoControle}">
								<f:param name="tagEscolhidaAdicaoCampoControle" value="001"/>
								<h:graphicImage value="/img/seta.gif"/>
							</h:commandLink>
						</td>
					</tr>
					<tr class="linhaPar">
						<td style="text-align:left">003 - IDENTIFICADOR DO NÚMERO DE CONTROLE (NR) </td>
						<td>
							<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" actionListener="#{catalogacaoMBean.adicionarNovoCampoControle}">
								<f:param name="tagEscolhidaAdicaoCampoControle" value="003"/>
								<h:graphicImage value="/img/seta.gif"/>
							</h:commandLink>
						</td>
					</tr>
					<tr class="linhaImpar">
						<td style="text-align:left">005 - DATA E HORA DA ÚLTIMA INTERVENÇÃO (NR) </td>
						<td>
							<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" actionListener="#{catalogacaoMBean.adicionarNovoCampoControle}">
								<f:param name="tagEscolhidaAdicaoCampoControle" value="005"/>
								<h:graphicImage value="/img/seta.gif"/>
							</h:commandLink>
						</td>
					</tr>
					<tr class="linhaPar">
						<td style="text-align:left">008 - CAMPOS FIXOS DE DADOS (NR)</td>
						<td>
							<h:commandLink styleClass="noborder" title="Selecionar o campo de controle do título" actionListener="#{catalogacaoMBean.adicionarNovoCampoControle}">
								<f:param name="tagEscolhidaAdicaoCampoControle" value="008"/>
								<h:graphicImage value="/img/seta.gif"/>
							</h:commandLink>
						</td>
					</tr>
					
					<tfoot>
						<tr>
							<td colspan="2">
							 <h:panelGroup>
							  <a onclick="modelPanelControle.hide();" style="font-weight: bold; color: #003390; cursor: pointer;"> 
									Fechar
								</a>
	            			</h:panelGroup>
							</td>
						</tr>
					</tfoot>
					
				</table>
		</p:dialog>
	
	</c:if>
	


	
		
			
	
    <%-- O painel pop-up com a juda do campo--%>
	<a4j:outputPanel ajaxRendered="true">
		<p:dialog id="dialogAjudaCampoMarc" header="Ajuda"  
				widgetVar="modelPanelAjudaCampoMarc" width="800" height="700">
			<h:outputText escape="false" value="#{catalogacaoMBean.ajudaCampo}" />
		</p:dialog>
	</a4j:outputPanel>
	
			
		

	