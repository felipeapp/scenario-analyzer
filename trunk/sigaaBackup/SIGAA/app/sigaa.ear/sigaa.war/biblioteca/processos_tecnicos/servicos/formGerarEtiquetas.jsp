<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf.GeraEtiquetaImpressaoMBean"%>


<style type="text/css">
		
	.colunaCabecalho{
		text-align:left;
	}
	
	.colunaTextGrande{
		text-align: left;
		width: 30%;
	} 
	
	.colunaTextPequeno{
		text-align: left;
		width: 20%;
	}
	
	.colunaIcone{
		text-align: center;
		width: 0.5%;
	}

	table.subFormulario{
		border: 1px solid #DEDFE3;
		border-top: 0px solid;
	}


	div.imitaSubFormulario{
		margin-left: auto; 
		margin-right: auto; 
		text-align: center;
		border-left: 1px solid #DEDFE3;
		border-right: 1px solid #DEDFE3;
		background-color:#F9FBFD;
	}

	div.imitaCaptionSubFormulario{
		background-color:#EDF1F8;
		border-color:-moz-use-text-color -moz-use-text-color #C8D5EC;
		border-style:none none solid;
		border-width:0 0 1px;
		border-bottom:none;
		color:#333366;
		font-variant:small-caps;
		font-weight:bold;
		letter-spacing:1px;
		margin:1px 0;
		padding:3px 0 3px 20px;
		text-align:left;
	}
	
	
	div.imitaFootTabela{
		background: #C8D5EC;
		text-align: center;
		padding: 3px;
	}

</style>



<f:view>


	<c:set var="ETIQUETA_CODIGO_BARRAS" value="<%= GeraEtiquetaImpressaoMBean.ETIQUETA_CODIGO_BARRAS %>" scope="request" />
	<c:set var="ETIQUETA_LOMBADA" value="<%= GeraEtiquetaImpressaoMBean.ETIQUETA_LOMBADA %>" scope="request" />
	<c:set var="AMBAS_ETIQUETAS" value="<%= GeraEtiquetaImpressaoMBean.AMBAS_ETIQUETAS %>" scope="request" />

	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

	<h2> <ufrn:subSistema /> &gt; Gerar Etiquetas para Materiais Informacionais</h2>

	<div class="descricaoOperacao">
		<p> Caro(a) usuário(a), </p>
		<p> Essa operação permite imprimir as etiquetas de <strong>Código de Barras</strong> e <strong>Lombada</strong> para a etiquetagem dos materiais.</p>
		<p> É mostrado por padrão a listagem de materiais incluídos no acervo cujas etiquetas ainda não foram impressas. 
		É permitido ainda utilizar a busca presente nessa página para incluir materiais avulsos na impressão.</p>
		<br/>
		<p>O sistema gera um arquivo <span style="color: red;">PDF</span> que pode ser salvo, compartilhado e impresso a partir de qualquer computador.</p>
		<br/><br/>
		<p>Para a impressão lembre-se de imprimir em formato A4, não marcar nenhum opção para reduzir a página e não usar modo econômico de impressão. </p>
		<p>Caso o documento não seja impresso da maneira que ele é visualizado digitalmente, verifique as configurações da sua impressora.</p>
	</div>


	<a4j:keepAlive beanName="geraEtiquetaImpressao"></a4j:keepAlive>

	<h:form id="formGerarEtiquetas">	
	
			<h:inputHidden id="hiddenTipoBusca" value="#{geraEtiquetaImpressao.tipoBusca}"></h:inputHidden>
	
			<table class=formulario style="margin-bottom:30px; width: 70%;">
				
				<caption class="listagem">Adicionar Material</caption>
				
				<tr>
					<td>
						<input id="radio1" type="radio" name="radio" onclick="getEl('formGerarEtiquetas:hiddenTipoBusca').dom.value = 1;">
						<h:outputText value="Código de Barras:"></h:outputText>						
   					</td>

					<td colspan="2">
						<h:inputText id="inputTxtCodBarras"  value="#{geraEtiquetaImpressao.codigoBarras}" maxlength="20" onkeypress="return getCodigoTecla(event)" 
							onfocus="getEl('radio1').dom.checked = true; getEl('formGerarEtiquetas:hiddenTipoBusca').dom.value = 1; "/>
					</td>
					
				</tr>
				
				<tr>
					<td>
						<input id="radio2" type="radio" name="radio" onclick="getEl('formGerarEtiquetas:hiddenTipoBusca').dom.value = 2;">
						<h:outputText value="Faixa de Códigos Barras:"></h:outputText>
   					</td>
				
					<td>	
						<h:inputText value="#{geraEtiquetaImpressao.codigoBarrasInicial}" id="inputTxtCodBarrasInicial" maxlength="15" 
							onfocus="getEl('radio2').dom.checked = true; getEl('formGerarEtiquetas:hiddenTipoBusca').dom.value = 2; "/>
					</td>
					
					<th>a</th>
					
					<td>
						<h:inputText value="#{geraEtiquetaImpressao.codigoBarrasFinal}" id="inputTxtCodBarrasFinal" maxlength="15" 
								onfocus="getEl('radio2').dom.checked = true; getEl('formGerarEtiquetas:hiddenTipoBusca').dom.value = 2; "/>
					</td>
					
					<td>
						<ufrn:help>Adicionar materiais que estejam entre o intervalo de códigos de barras informado.</ufrn:help>
					</td>
					
				</tr>
							
				<tfoot>
					<tr>
						<td colspan="5" align="center">
							<h:commandButton value="Adicionar" actionListener="#{geraEtiquetaImpressao.adicionarCodigoBarras}" id="botaoAdicionarMaterial"/>
							<h:commandButton value="Cancelar" action="#{geraEtiquetaImpressao.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
						</td>
					</tr>
				</tfoot>
				
			</table>
			
		
	
	
	
	
	
	
	
		<%--   parte da página onde  mostra os materiais escolhidos para exportação, sendo salvos pelo sistema ou que o usuário adicionou   --%>
			
		<%-- <a4j:outputPanel ajaxRendered="true">		--%>		
				 
				<t:div styleClass="infoAltRem" rendered="#{ geraEtiquetaImpressao.qtdMateriaisGeracaoEtiqueta  > 0 }">
					<h:graphicImage value="/img/delete.gif" />: Remover Material da Lista de Geração de Etiquetas
					<h:graphicImage value="/img/adicionar.gif" />: Adicionar Informação à Impressão da Etiqueta de Lombada
					<br/>
					<h:graphicImage value="/img/biblioteca/estornar.gif" />: Remover Informação da Impressão da Etiqueta de Lombada
				</t:div>
				
				<%-- <t:div  rendered="#{ geraEtiquetaImpressao.qtdMateriaisGeracaoEtiqueta  > 0 }"> --%>
					<table style="width:98%" class="formulario" >
						<caption>Materiais para Geração de Etiquetas</caption>
							<tr style="text-align:center;">
					
								<th colspan="1" class="obrigatorio" style="width: 50%">Apenas Meus Materiais ? </th>
								
								<td colspan="6" style="margin-bottom:20px; text-align:left;">
									
									<h:selectOneRadio value="#{geraEtiquetaImpressao.apenasMeusMateriasPedentes}" onclick="submit();"
											valueChangeListener="#{geraEtiquetaImpressao.atualizaMateriaisPendentes}">  
										<f:selectItem itemLabel="SIM" itemValue="true" />
										<f:selectItem itemLabel="NÃO" itemValue="false" />
	    							</h:selectOneRadio>
									
								</td>
							</tr>
							<tr>
								<th style="font-weight: bold;">
									Quantidade de materiais selecionados:
								</th>
								<td>
									<span id="quantidadeMateriaisSelecionados"></span>
								</td>
							</tr>
					</table>
				<%-- </t:div> --%>
	
				<t:dataTable var="material" value="#{geraEtiquetaImpressao.dataModelMateriais}" 
						rendered="#{ geraEtiquetaImpressao.qtdMateriaisGeracaoEtiqueta  > 0 }"
						id="tableListagemMaterial"
						style="width: 98%; margin-right:auto; margin-left:auto; border: 1px solid #DEDFE3; background-color:#F9FBFD;"
						rowIndexVar="posicao" 
						columnClasses="colunaIcone, colunaTextPequeno, colunaTextGrande, colunaTextPequeno, colunaTextPequeno, colunaIcone "
						headerClass="colunaCabecalho">
				
					<h:column>
						<f:facet name="header">
							<h:selectBooleanCheckbox id="checkBoxGeral" value="true" onclick="selecionarTudo(this); quantidadeMateriaisSelecionados();"> </h:selectBooleanCheckbox>
						</f:facet>
						
						<h:selectBooleanCheckbox  value="#{material.selecionado}" onclick="quantidadeMateriaisSelecionados();"></h:selectBooleanCheckbox>
						
					</h:column>
				
					<h:column>
						<f:facet name="header">
							<h:outputText value="Cod. Barras" />
						</f:facet>
						<h:outputText value="#{material.codigoBarras}"></h:outputText>
					</h:column>
				
					<h:column>
						<f:facet name="header">
							<h:outputText value="Biblioteca" />
						</f:facet>
						<h:outputText value="#{material.biblioteca.descricao}"></h:outputText>
					</h:column>
					
					<h:column>
						<f:facet name="header">
							<h:outputText value="Status" />
						</f:facet>
						<h:outputText value="#{material.status.descricao}"></h:outputText>
					</h:column>
					
					<h:column>
						<f:facet name="header">
							<h:outputText value="Tipo Material" />
						</f:facet>
						<h:outputText value="#{material.tipoMaterial.descricao}"></h:outputText>
					</h:column>
					 
					<h:column>
						<h:commandLink action="#{geraEtiquetaImpressao.removerMaterialGeracaoEtiquetas}" onclick="#{confirmDelete}">
							<h:graphicImage url="/img/delete.gif" style="border:none" title="Clique aqui para remover da lista de geração de etiquetas o material " />				
						</h:commandLink>
					</h:column>
				
				</t:dataTable>	
				
				
				
					
				<t:div styleClass="imitaSubFormulario" style="width: 98%" rendered="#{ geraEtiquetaImpressao.qtdMateriaisGeracaoEtiqueta  > 0 }">
				
					<t:div styleClass="imitaCaptionSubFormulario" rendered="#{ geraEtiquetaImpressao.qtdMateriaisGeracaoEtiqueta  > 0 }">Parâmetros da Geração</t:div>
				
					<t:div style="margin-left: auto; margin-right: auto; padding: 3px;" rendered="#{ geraEtiquetaImpressao.qtdMateriaisGeracaoEtiqueta  > 0 }">
						<table width="100%">
							<tr>
								<th width="300px"><t:htmlTag styleClass="required" value="span"> <h:outputText> Tipo de Etiqueta:</h:outputText></t:htmlTag></th>
								<td align="left">
									<h:selectOneMenu id="selectOneMenuTiposEtiquetas" value="#{geraEtiquetaImpressao.valorEtiquetasEscolhidas}">	
										<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --"/>
										<f:selectItem itemValue="1" itemLabel="Código de Barras"/>
										<f:selectItem itemValue="2" itemLabel="Lombada"/>
										<f:selectItem itemValue="3" itemLabel="Código de Barras e Lombada"/>
										<a4j:support event="onchange" actionListener="#{geraEtiquetaImpressao.atualizaValorEtiquetaEscolhido}" reRender="formGerarEtiquetas"></a4j:support>
									</h:selectOneMenu>																
								</td>
							</tr>
						</table> 											
					
					
						<a4j:outputPanel id="outputPanelInformacoesLombada"  ajaxRendered="true">
							<c:if test="${geraEtiquetaImpressao.valorEtiquetasEscolhidas == ETIQUETA_LOMBADA || geraEtiquetaImpressao.valorEtiquetasEscolhidas == AMBAS_ETIQUETAS}"> 
								<table id="dtTblInformacoesLombada" style="width: 80%; margin-left: auto; margin-right: auto; margin-top: 20px; margin-bottom: 20px; border: 1px solid #DEDFE3;">
									<caption style="font-weight: bold; height: 20px; background-color: #EDF1F8;"> Informações a serem impressas na etiqueta de lombada </caption>
									<tr>
										<th width="300px"><t:htmlTag styleClass="required" value="span"> <h:outputText> Informação da Etiqueta de Lombada:</h:outputText></t:htmlTag></th>
										<td align="left">
											<h:selectOneMenu id="comboBoxInformacaoEtiquetaLombada" value="#{geraEtiquetaImpressao.valorCampoEtiquetaLombadaSelecionado}" >
												<f:selectItems value="#{geraEtiquetaImpressao.allInformacoesEtiquetaLombadaComboBox}"/>
											</h:selectOneMenu>
										</td>
										<td style="vertical-align: top; font-weight: normal;">
											<a4j:commandLink reRender="dtTblInformacoesLombada" actionListener="#{geraEtiquetaImpressao.adicionarInformacaoEtiquetaLombada}">
												<h:graphicImage url="/img/adicionar.gif" style="border:none" title="Adicionar Informação à Impressão da Etiqueta de Lombada" />
											</a4j:commandLink>
											<ufrn:help>A informação adicionada pode não aparecer caso ultrapasse o tamanho da etiqueta.</ufrn:help>
										</td>
									</tr>
									
									
									<tr>
										<th class="required" style="vertical-align: top; font-weight: normal;">Informações que serão impressas: </th>
										<td style="width: 40%">
											
												<t:dataTable id="dtTblInformacoesImpressas" value="#{geraEtiquetaImpressao.camposEtiquetaLombadaDataModel}" var="campo" rowIndexVar="linha" style="width:100%; padding-left: 0px; ">
													<h:column>
														<h:outputText value="#{geraEtiquetaImpressao.camposEtiquetaLombada[linha]}" />
													</h:column>
													<h:column rendered="#{geraEtiquetaImpressao.camposEtiquetaLombadaDataModel.rowCount > 1}">
														<a4j:commandLink reRender="dtTblInformacoesImpressas" actionListener="#{geraEtiquetaImpressao.removerInformacaoEtiquetaLombada}">
															<f:param name="posicaoInformacao" value="#{linha}" ></f:param>
															<h:graphicImage url="/img/biblioteca/estornar.gif" style="border:none" title="Remover Informação da Impressão da Etiqueta de Lombada" />
														</a4j:commandLink>
													</h:column>
												</t:dataTable>	
											
										</td>
										
									</tr>
									
								</table> 	
							</c:if> 
						</a4j:outputPanel>
						
						
						
						
						
					
						<table width="100%">
							<tr>
								<th width="300px"><t:htmlTag styleClass="required" value="span"> <h:outputText>Formato da Página:</h:outputText></t:htmlTag></th>
								<td align="left">
									<h:selectOneMenu id="comboBoxFormatosPagina" value="#{geraEtiquetaImpressao.numeroFormatoPagina}">
											<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --"/>
											<f:selectItems  value="#{geraEtiquetaImpressao.formatosPaginasComboBox}" />
									</h:selectOneMenu> 
								</td>
							</tr>
						</table>										
					</t:div>
				
				
				
					<t:div style="margin-left: auto; margin-right: auto; padding: 3px;" rendered="#{ geraEtiquetaImpressao.qtdMateriaisGeracaoEtiqueta  > 0 }">
						<table width="100%">
						    <tr>
								<th width="300px"><t:htmlTag styleClass="required" value="span" style="margin-left: -155px;"> <h:outputText>Posição Inicial:</h:outputText> </t:htmlTag></th>
								<td align="left">
									<h:inputText value="#{geraEtiquetaImpressao.posicaoInicial}" size="2" maxlength="2"  onkeypress="return ApenasNumeros(event);" />		
								</td>							
						    </tr>
						</table>
					</t:div>
					
					<t:div style="margin-left: auto; margin-right: auto; padding: 3px;">
						<table width="100%">
						    <tr>										
								<th width="300px"><t:htmlTag styleClass="required" value="span" style="margin-left: -155px;"> <h:outputText>Quantidade de Código Barras:</h:outputText> </t:htmlTag></th>
								<td align="left">
									<h:inputText value="#{geraEtiquetaImpressao.copiasCodigoBarras}" size="2" maxlength="2" onkeypress="return ApenasNumeros(event);" />		
									<ufrn:help>A quantidade de vezes que cada etiqueta de código de barras será impressa.</ufrn:help>
								</td>							
						    </tr>
						</table>											
					</t:div>
					
					<t:div style="margin-left: auto; margin-right: auto; padding: 3px;">
						<table width="100%">
						    <tr>										
								<th width="300px"><t:htmlTag styleClass="required" value="span" style="margin-left: -155px;"> <h:outputText>Quantidade Lombada:</h:outputText> </t:htmlTag></th>
								<td align="left">
									<h:inputText value="#{geraEtiquetaImpressao.copiasLombada}" size="2" maxlength="2" onkeypress="return ApenasNumeros(event);" />		
									<ufrn:help>A quantidade de vezes que cada etiqueta de lombada será impressa.</ufrn:help>
								</td>							
						    </tr>
						</table>																
					</t:div>
				
				</t:div>
				
				
					
				<t:div styleClass="imitaFootTabela" style="width: 98%; margin-left: auto; margin-right: auto; text-align: center;" rendered="#{ geraEtiquetaImpressao.qtdMateriaisGeracaoEtiqueta  > 0 }">
					<h:commandButton value="Gerar Etiquetas" action="#{geraEtiquetaImpressao.gerar}"/>
					<h:commandButton value="Remover Selecionados" action="#{geraEtiquetaImpressao.removerMateriaisSelecionados}" onclick="#{confirmDelete}"/>
				</t:div>
			
		<%-- </a4j:outputPanel>	--%>
		
	
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
	
	</h:form>
	
</f:view>


<script type="text/javascript">

	function checarRadioButton(){
		document.getElementById('formGerarEtiquetas:inputTxtCodBarras').focus();
	}
	
	checarRadioButton();    // executa quando a página é carregada



	function selecionarTudo(chk){
	   for (i=0; i<document.formGerarEtiquetas.elements.length; i++)
	      if(document.formGerarEtiquetas.elements[i].type == "checkbox")
	         document.formGerarEtiquetas.elements[i].checked= chk.checked;
	}


	quantidadeMateriaisSelecionados(); // executa quando a página é carregada
	

	function quantidadeMateriaisSelecionados(){
		quantidade = 0;
		
		 for (i=0; i<document.formGerarEtiquetas.elements.length; i++){
		 	if(document.formGerarEtiquetas.elements[i].type == "checkbox"){
			 	if( document.formGerarEtiquetas.elements[i].checked && document.formGerarEtiquetas.elements[i].id != 'formGerarEtiquetas:tableListagemMaterial:checkBoxGeral' )
		    		quantidade++;
		 	}	
		 }
		 
		document.getElementById('quantidadeMateriaisSelecionados').innerHTML = quantidade ;   	  
	}

	

	//retorna se a tecla pressionada foi um "ENTER"
	function getCodigoTecla(evento) {
		var tecla = "";
		if (isIe())
			tecla = evento.keyCode;
		else
			tecla = evento.which;
	
		if (tecla == 13){
			document.getElementById('formGerarEtiquetas:botaoAdicionarMaterial').click();
			return false;
		}
		
		return true;
		
	}	
	
	function isIe() {
		return (typeof window.ActiveXObject != 'undefined');
	}	
	
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>