<%@include file="/biblioteca/cabecalho_popup.jsp"%>

<script type="text/javascript">

function habilitarVisualizarDadosMarc(idTitulo) {
	var linha = 'linha_'+ idTitulo; // o id da linha da tabela
	
	if ( $(linha).style.display != 'table-cell' && $(linha).style.display != 'inline-block' ) {       //$() == getElementById()
		if ( !Element.hasClassName(linha, 'populado') ) {
			
			
			new Ajax.Request("/sigaa/biblioteca/processos_tecnicos/pesquisas_acervo/infoMarc.jsf?idTitulo=" + idTitulo, {
				onComplete: function(transport) {
					$(linha).innerHTML = transport.responseText;
					Element.addClassName(linha, 'populado');
				}
			});
			
		}
		
		if (/msie/.test( navigator.userAgent.toLowerCase() )){
			$(linha).style.display = 'inline-block';
		}else{
			$(linha).style.display = 'table-cell';
		}
		
		
	} else {
		$(linha).style.display = 'none';
	}
}


function ativaBotaoFalso() {
	$('pesquisaTitulosPopUp:butaoPesquisaMultiCampoPopUp').hide();
	$('pesquisaTitulosPopUp:fakeBotaoPesquisar').show();
	$('indicatorPesquisaTitulosPopUp').style.display = '';
	
}

ativaBotaoVerdadeiro();

function ativaBotaoVerdadeiro() {
	$('pesquisaTitulosPopUp:butaoPesquisaMultiCampoPopUp').show();
	$('pesquisaTitulosPopUp:fakeBotaoPesquisar').hide();
	$('indicatorPesquisaTitulosPopUp').style.display = 'none';
}

</script>

<script type="text/javascript" src="/sigaa/javascript/biblioteca/functions.js" ></script>


<h2> Biblioteca &gt;  Pesquisar Catalogações no Acervo </h2>


<f:view>

	<a4j:keepAlive beanName="pesquisaPopUpTituloCatalograficoMBean" />

	<h:form id="pesquisaTitulosPopUp">


	<table id="tableDadosPesquisa" class="formulario" style="width: 80%">
	
		<caption>Selecione os campos para a busca</caption>
	
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.buscarNumeroSistema}" styleClass="noborder" id="checkNumeroSistema"/>
			</td>
			<th  width="30%" style="text-align:left"> Número do Sistema: </th>
	
			<td  width="70%" colspan="6">
				<h:inputText id="inputTextNumeroSistemaBuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.numeroDoSistema}" size="10"  maxlength="9" 
							onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkNumeroSistema');"
							onkeyup="return formatarInteiro(this);" > </h:inputText>
			</td>
		</tr>
	
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.buscarTitulo}" styleClass="noborder" id="checkTitulo"/>
			</td>
			<th  width="30%" style="text-align:left"> Título: </th>
	
			<td  width="70%" colspan="6">
				<h:inputText id="inputTextTituloBuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.titulo}" size="60" maxlength="100"
					onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkTitulo');"  /> 
			</td>
		</tr>
	
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.buscarAutor}" styleClass="noborder" id="checkAutor"/>
			</td>
			<th  width="30%" style="text-align:left">Autor:</th>
	
			<td  width="70%" colspan="6">
				<h:inputText id="inputTextAutorBuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.autor}" size="60" maxlength="100" 
					onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkAutor');"/>
				<ufrn:help>Busca por autores e autores secundários</ufrn:help> 
			</td>
		</tr>
		
		
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.buscarAssunto}" styleClass="noborder" id="checkAssunto"/>
			</td>
			<th  width="30%" style="text-align:left">Assunto:</th>
	
			<td  width="70%" colspan="6">
				<h:inputText id="inputTextAssuntoBuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.assunto}" size="60" maxlength="100" 
					onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkAssunto');"/> 
			</td>
		</tr>
	
	
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.buscarLocalPublicacao}" styleClass="noborder" id="checkLocalPublicao"/>
			</td>
			<th  width="30%" style="text-align:left">Local de Publicação:</th>
	
			<td  width="70%" colspan="6">
				<h:inputText id="inputTextLocalPublicacaoBuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.localPublicacao}" size="60" maxlength="100" 
					onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkLocalPublicao');"/> 
			</td>
		</tr>
	
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.buscarEditora}" styleClass="noborder" id="checkEditora"/>
			</td>
			<th  width="30%" style="text-align:left">Editora:</th>
	
			<td  width="70%" colspan="6">
				<h:inputText id="inputTextEditoraBuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.editora}" size="60" maxlength="100" 
					onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkEditora');"/>
			</td>
		</tr>
	
		<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao1}">
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.buscarClassificacao1}" styleClass="noborder" id="checkClassificacao1"/>
				</td>
				<th  width="30%" style="text-align:left">${classificacaoBibliograficaMBean.descricaoClassificacao1} :</th>
		
				<td  width="70%" colspan="6">
					<h:inputText id="inputTextClassificacao1BuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.classificacao1}" size="30" maxlength="50" 
						onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkClassificacao1');"/>
				</td>
			</tr>
		</c:if>
	
		<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao2}">
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.buscarClassificacao2}" styleClass="noborder" id="checkClassificacao2"/>
				</td>
				<th  width="30%" style="text-align:left">${classificacaoBibliograficaMBean.descricaoClassificacao2} :</th>
		
				<td  width="70%" colspan="6">
					<h:inputText id="inputTextClassificacao2BuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.classificacao2}" size="30" maxlength="50" 
						onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkClassificacao2');"/>
				</td>
			</tr>
		</c:if>
		
		<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao3}">
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.buscarClassificacao3}" styleClass="noborder" id="checkClassificacao3"/>
				</td>
				<th  width="30%" style="text-align:left">${classificacaoBibliograficaMBean.descricaoClassificacao3} :</th>
		
				<td  width="70%" colspan="6">
					<h:inputText id="inputTextClassificacao3BuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.classificacao3}" size="30" maxlength="50" 
						onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkClassificacao3');"/>
				</td>
			</tr>
		</c:if>
	
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.buscarAno}" styleClass="noborder" id="checkAnoPublicacao"/>
			</td>
			<th colspan="1" style="text-align:left">Ano publicação de:</th>
	
			<td colspan="1" width="10%">
				<h:inputText id="inputTextAnoInicialBuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.anoInicial}" size="7"  maxlength="4" 
						onkeyup="return formatarInteiro(this);"
						onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkAnoPublicacao');"> </h:inputText>
			</td>
	
			<th colspan="1" width="10%">
				até:
			</th>
		  
			<td colspan="4">
				<h:inputText id="inputTextAnoFinalBuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.anoFinal}" size="7" maxlength="4" 
						onkeyup="return formatarInteiro(this);"
						onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkAnoPublicacao');"> </h:inputText>
			</td>
		</tr>
		
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.utilizarBuscaRemissiva}" styleClass="noborder" id="checkExecutarBuscaRemissiva"/>
			</td>
			<th colspan="7" style="text-align:left;">Executar a busca remissiva na base de autoridades</th>
		</tr>
		
		<tr>
			<td></td>
			<th style="text-align:left">Ordenação:</th>
			<td colspan="6">
				<h:selectOneMenu value="#{pesquisaPopUpTituloCatalograficoMBean.valorCampoOrdenacao}">
					<f:selectItems value="#{pesquisaPopUpTituloCatalograficoMBean.campoOrdenacaoResultadosComboBox}"/>
				</h:selectOneMenu>
			</td>
		</tr>
	
		<tr>
			<td></td>
			<th style="text-align:left">Registros por página:</th>
			<td colspan="6">
				<h:selectOneMenu value="#{pesquisaPopUpTituloCatalograficoMBean.quantideResultadosPorPagina}">
					<f:selectItems value="#{pesquisaPopUpTituloCatalograficoMBean.qtdResultadosPorPaginaComboBox}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		
		<tr>
		
			<td colspan="8">
				<table id="tableDadosPesquisaInterna" class="subFormulario">
					<caption>Filtros sobre os Materiais dos Títulos</caption>
					
					<tr>
						<td width="2%">
							<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.buscarBiblioteca}" styleClass="noborder" id="checkBiblioteca"/>
						</td>
						
						<th colspan="1"  style="text-align:left">Biblioteca:</th>
						
						<td colspan="4">
							<h:selectOneMenu id="comboboxBibliotecasInternasBuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.idBiblioteca}" 
									onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkBiblioteca');">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
								<f:selectItems value="#{pesquisaPopUpTituloCatalograficoMBean.bibliotecasInternasAtivas}"/>
							</h:selectOneMenu>
						</td>
					</tr>
					
					<tr>
						<td width="2%">
							<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.buscarColecao}" styleClass="noborder" id="checkColecao"/>
						</td>
						<th colspan="1"  style="text-align:left">Coleção:</th>
						<td colspan="4">
							<h:selectOneMenu id="comboboxColecoesBuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.idColecao}" 
									onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkColecao');">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
								<f:selectItems value="#{pesquisaPopUpTituloCatalograficoMBean.colecoesAtivas}"/>
							</h:selectOneMenu>
						</td>
		
					</tr> 
		
					<tr>
						<td width="2%">
							<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.buscarTipoMaterial}" styleClass="noborder" id="checkTipoMaterial"/>
						</td>
						<th colspan="1"  style="text-align:left">Tipo de Material:</th>
						<td colspan="4">
							<h:selectOneMenu id="comboboxTipoMaterialBuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.idTipoMaterial}" 
									onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkTipoMaterial');">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
								<f:selectItems value="#{pesquisaPopUpTituloCatalograficoMBean.tiposMateriaisAtivos}"/>
							</h:selectOneMenu>
						</td>
	
					</tr>
					
					<tr>
						<td width="2%">
							<h:selectBooleanCheckbox value="#{pesquisaPopUpTituloCatalograficoMBean.buscarStatus}" styleClass="noborder" id="checkStatus"/>
						</td>
						<th colspan="1"  style="text-align:left">Status:</th>
						<td colspan="4">
							<h:selectOneMenu id="comboboxStatusBuscaTituloMulticampo" value="#{pesquisaPopUpTituloCatalograficoMBean.idStatus}" 
									onchange="marcarCheckBox(this, 'pesquisaTitulosPopUp:checkStatus');">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
								<f:selectItems value="#{pesquisaPopUpTituloCatalograficoMBean.statusAtivos}"/>
							</h:selectOneMenu>
						</td>
	
					</tr>
					
				</table>
			</td>
				
		<tr>
	
	
		<tfoot>
			<tr>
				<td colspan="9">
					
					<h:commandButton id="butaoPesquisaMultiCampoPopUp" value="Pesquisar" action="#{pesquisaPopUpTituloCatalograficoMBean.pesquisaMultiCampo}" onclick="ativaBotaoFalso();"/>
					
					<h:commandButton id="fakeBotaoPesquisar" value="Aguarde ..." style="display: none;" disabled="true" />
					<span id="indicatorPesquisaTitulosPopUp"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
					
					<h:commandButton value="Limpar" id="butaoLimpaBuscaAcervoPopup" action="#{pesquisaPopUpTituloCatalograficoMBean.apagarDadosPesquisaMultiCampo}" />
					
					
					<h:commandButton value="Fechar" id="botaoFecharBuscaAcervoPopUp" onclick="javascript:window.close();" immediate="true"/>
					
				</td>
			</tr>
			
		</tfoot>
	
	
	</table>
		
	
	<t:div id="divResultadoPesquisaAutoridade" rendered="#{pesquisaPopUpTituloCatalograficoMBean.quantidadeTotalResultados > 0}">

		<%-- Inclue os links de páginação para percorrer os resultados das pesquisas no acervo --%>
				
		<c:set var="pesquisarAcervoPaginadoBiblioteca" value="${pesquisaPopUpTituloCatalograficoMBean}" scope="request" />
		<%@ include file="/public/biblioteca/pesquisas_acervo/paginaPaginacaoConsultaAcervo.jsp"%>

		<div class="infoAltRem" style="margin-top: 10px">

			<h:graphicImage value="/img/biblioteca/visualizarMarc.png" style="overflow: visible;" />: 
			Visualizar dados MARC 
		</div>	
	
		
	
	<table class="listagem tablesorter" id="listagem" style="margin-top: 10px;">
			<caption>  Títulos Encontrados (  <h:outputText value="#{pesquisaPopUpTituloCatalograficoMBean.descricaoPaginacao}"/> )</caption>
			<thead>
				<tr>
					<th style="width: 1%; text-align: left;"> </th>
					<th style="width: 3%; text-align: left;"> <span style="white-space: nowrap; margin-right:15px;">Nº Sistema</span></th>
					<th style="width: 20%;"> Autor </th>
					<th style="width: 30%;"> Título </th>
					<th style="width: 5%; text-align: center"> <span style="white-space: nowrap; margin-right:15px;">Edição</span> </th>
					<th style="width: 5%; padding-left: 5px;"> <span style="white-space: nowrap; margin-right:15px;">Ano</span> </th>
					<th style="width: 25%;"> Assunto </th>
					<th style="width: 10%;"> <span style="white-space: nowrap; margin-right:15px;">Nº Chamada</span> </th>
					<th style="width: 1%; text-align: left;"> </th>
				</tr>	
					
			</thead>

			<tbody>

				<c:forEach items="#{pesquisaPopUpTituloCatalograficoMBean.resultadosPaginadosEmMemoria}" var="tituloCache" varStatus="status">


					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">

							<td width="1%" style="text-align:center">
								<a href="#buscarTitulo" onclick="habilitarVisualizarDadosMarc(${tituloCache.idTituloCatalografico})">
										<img src="${ctx}/img/biblioteca/visualizarMarc.png" title="Visualizar as Informações MARC do Título"/>
								</a>
							</td>
							

							<td style="${tituloCache.catalogado ? " " : "color:red"}; text-align: right" width="5%">
								${tituloCache.numeroDoSistema}
							</td>
							
	
						
							<td style="${tituloCache.catalogado ? " " : "color:red"}" width="30%">
								${tituloCache.autor}
							</td>
							
							<td style="${tituloCache.catalogado ? " " : "color:red"}" width="30%">
								${tituloCache.titulo} ${tituloCache.subTitulo}
							</td>
							
							<td style="${tituloCache.catalogado ? " " : "color:red"}" width="7%">
								<c:forEach items="${tituloCache.edicoesFormatadas}" var="edicao">
									${edicao}
								</c:forEach>
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
								<c:forEach items="${tituloCache.assuntosFormatados}" var="assunto">
									<span style="display: block;">${assunto}</span>
								</c:forEach>
							</td>
							
							<td style="${tituloCache.catalogado ? " " : "color:red"}" width="15%">
								${tituloCache.numeroChamada}
							</td>
							
							<td width="1%" style="text-align:right; ${tituloCache.catalogado ? " " : "color:red"}">
								${tituloCache.quantidadeMateriaisAtivosTitulo}
							</td>
							
						</tr>

						<%-- A linha da tabela que mostra os detalhes do exemplar e é habilitado usando JavaScript. --%>
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"> 
							<td colspan="9" id="linha_${tituloCache.idTituloCatalografico}" style=" display: none;" ></td>
						</tr>

				</c:forEach>

			</tbody>

			<tfoot>
				<tr>
					<td colspan="15" style="text-align: center; font-weight: bold;">
						<h:outputText value="#{pesquisaPopUpTituloCatalograficoMBean.descricaoPaginacao}"/> título(s).
					</td>
				</tr>
			</tfoot>
			
		</table>
		
	</t:div>
	
	</h:form>
	
</f:view>






<%@include file="/biblioteca/rodape_popup.jsp"%>