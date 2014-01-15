<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<%-- Pagina geral para pesquisa de FASCÍCULOS  --%>
<%-- A ideia é que sempre que existe pesquisa de Fascículos se use essa pagina para não ficar repetindo código. --%>

<h2>  <ufrn:subSistema /> &gt; Pesquisa de Fascículos</h2>

<div class="descricaoOperacao"> 
    <p>Nessa página é possível realizar uma busca direta dos fascículos no acervo do sistema de bibliotecas.</p>
    <p> <strong> Observação: Fascículos mostrados em <span style="color: red">vermelho</span> estão baixados do acervo.</strong> </p>
</div>

<script type="text/javascript">
<!--
	// função para abrir a pagina dos dados MARC sempre na mesma janela do navegador
	var janela = null;
	
	function abreJanelaInformacoesCompletasTitulo(idTitulo){
		if (janela == null || janela.closed){
			janela = window.open('${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/informacoesBibliograficasTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaDadosMarc=true','','width=1024,height=400,left=50,top=50,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janela.location = '${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/informacoesBibliograficasTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaDadosMarc=true';
		}
	
		janela.focus();
	}
-->
</script>

<script type="text/javascript" src="/sigaa/javascript/biblioteca/functions.js" ></script>

<style type="text/css">
<!--
table.listagem tr.biblioteca td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
-->
</style>


<f:view>

	<h:form id="formPesquisaFasciculo">

	<a4j:keepAlive beanName="pesquisarFasciculoMBean"></a4j:keepAlive>


	<%-- Usado no caso de uso de substituição de um fascículo por outro--%>
	<a4j:keepAlive beanName="substituirFasciculoMBean"></a4j:keepAlive>
	

	<table class="formulario" width="80%">
		
			<caption class="listagem">Entre com os Parâmetros da Busca</caption>

			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarCodigoBarras}" styleClass="noborder" id="checkCodigoBarras"/>
				</td>
				<th colspan="2" style="text-align:left; width: 25%">Código de Barras:</th>
				<td colspan="4">
					<h:inputText id="inputTextCodigoBarrasFasciculo" size="25" maxlength="20" value="#{pesquisarFasciculoMBean.obj.codigoBarras}" 
						onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkCodigoBarras');"/>
					<ufrn:help>Se não souber o código de barras completo do fascículo, pode-se digitar somente os primeiros caracteres do código de barras. </ufrn:help>
				</td>
			</tr>

			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarBiblioteca}" styleClass="noborder" id="checkBiblioteca"/>
				</td>
				<th colspan="2" style="text-align:left">Biblioteca:</th>
				<td colspan="4">
					<h:selectOneMenu id="comboBoxBibliotecaFasciculos" value="#{pesquisarFasciculoMBean.obj.biblioteca.id}" 
						onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkBiblioteca');">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						<f:selectItems value="#{pesquisarFasciculoMBean.bibliotecas}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarColecao}" styleClass="noborder" id="checkColecao"/>
				</td>
				<th colspan="2"  style="text-align:left">Coleção:</th>
				<td colspan="4">
					<h:selectOneMenu id="comboBoxColecaoFasciculos" value="#{pesquisarFasciculoMBean.obj.colecao.id}" 
						onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkColecao');">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						<f:selectItems value="#{pesquisarFasciculoMBean.colecoes}"/>
					</h:selectOneMenu>
				</td>

			</tr> 

			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarTipoMaterial}" styleClass="noborder" id="checkTipoMaterial"/>
				</td>
				<th colspan="2"  style="text-align:left">Tipo Material:</th>
				<td colspan="4">
					<h:selectOneMenu id="comboBoxTipoMaterialFasciculos" value="#{pesquisarFasciculoMBean.obj.tipoMaterial.id}" 
						onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkTipoMaterial');">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						<f:selectItems value="#{pesquisarFasciculoMBean.tiposMaterial}"/>
					</h:selectOneMenu>
				</td>

			</tr>
			
			<tr>
				<td width="2%" valign="top">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarFormaDocumento}" styleClass="noborder" id="checkFormaDocumento"/>
				</td>
				<th colspan="2"  style="text-align:left;padding-top:4px;"  valign="top">Forma de Documento:</th>
				<td colspan="4">
					<h:selectManyListbox  id="comBoxFormaDocumentoPesquisar" value="#{pesquisarFasciculoMBean.idsFormasDocumentoEscolhidos}" size="10" 
						onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkFormaDocumento');">
			           <f:selectItems value="#{formaDocumentoMBean.allCombo}"/>
			       </h:selectManyListbox>
			       <ufrn:help>Para selecionar mais de uma <strong>forma de documento</strong> mantenha pressionada a tecla "Ctrl", em seguida selecione os itens desejados. 
						Para retirar a seleção, também é preciso pressionar a tecla "Ctrl". </ufrn:help>
				</td>
			</tr>

			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarStatus}" styleClass="noborder" id="checkStatus"/>
				</td>
				<th colspan="2"  style="text-align:left">Status do Fascículo:</th>
				<td colspan="4">
					<h:selectOneMenu id="comboBoxStatusFasciculos" value="#{pesquisarFasciculoMBean.obj.status.id}" 
						onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkStatus');">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						<f:selectItems value="#{pesquisarFasciculoMBean.statusMateriais}"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarSituacao}"
							styleClass="noborder" id="checkSituacao"
							disabled="#{ pesquisarFasciculoMBean.operacaoDesfazerBaixa }" />
				</td>
				<th colspan="2"  style="text-align:left">Situação do Fascículo:</th>
				<td colspan="4">
					<h:selectOneMenu id="comboBoxSituacaoFasciculos" value="#{pesquisarFasciculoMBean.obj.situacao.id}" 
						onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkSituacao');">
						<c:if test="${ not pesquisarFasciculoMBean.operacaoDesfazerBaixa }">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						</c:if>
						<f:selectItems value="#{pesquisarFasciculoMBean.situacaoMateriais}"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarCodigoAssinatura}" styleClass="noborder" id="checkCodigoAssinante"/>
				</td>
				<th colspan="2" style="text-align:left">Código do Assinatura:</th>
				<td colspan="4">
					<h:inputText id="inputTextCodigoAssinaturaFasciculo" size="30" maxlength="40" value="#{pesquisarFasciculoMBean.obj.assinatura.codigo}"
					onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkCodigoAssinante');" />
				</td>
			</tr>
			

			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarTituloAssinatura}" styleClass="noborder" id="checkTituloAssinatura"/>
				</td>
				<th colspan="2" style="text-align:left">Título da Assinatura:</th>
				<td colspan="4">
					<h:inputText id="inputTextTituloAssinaturaFasciculo" size="80" maxlength="100" value="#{pesquisarFasciculoMBean.obj.assinatura.titulo}" 
						onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkTituloAssinatura');"/>
				</td>
			</tr>
			

			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarAnoCronologico}" styleClass="noborder" id="checkAnoCronologico"/>
				</td>
				<th colspan="2" style="text-align:left">Ano Cronológico:</th>
				<td colspan="4">
					<h:inputText id="inputTextAnoCronologico" size="6" maxlength="4" value="#{pesquisarFasciculoMBean.obj.anoCronologico}" 
						 onkeypress="return ApenasNumeros(event);"
						 onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkAnoCronologico');"/>
				</td>
			</tr>

			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarDiaMes}" styleClass="noborder" id="checkDiaMes"/>
				</td>
				<th colspan="2" style="text-align:left">Dia/Mês:</th>
				<td colspan="4">
					<h:inputText id="inputTextDiaMes" size="6" maxlength="4" value="#{pesquisarFasciculoMBean.obj.diaMes}" 
						 onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkDiaMes');"/>
				</td>
			</tr>

			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarAno}" styleClass="noborder" id="checkAno"/>
				</td>
				<th colspan="2" style="text-align:left">Ano:</th>
				<td colspan="4">
					<h:inputText id="inputTextAno" size="6" maxlength="4" value="#{pesquisarFasciculoMBean.obj.ano}" 
						 onkeypress="return ApenasNumeros(event);"
						 onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkAno');"/>
				</td>
			</tr>
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarVolume}" styleClass="noborder" id="checkVolume"/>
				</td>
				<th colspan="2" style="text-align:left">Volume:</th>
				<td colspan="4">
					<h:inputText id="inputTextVolumeFasciculo" size="7" maxlength="5" value="#{pesquisarFasciculoMBean.obj.volume}" 
						 onkeypress="return ApenasNumeros(event);" 
						 onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkVolume');"/>
				</td>
			</tr>
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarNumero}" styleClass="noborder" id="checkNumero"/>
				</td>
				<th colspan="2" style="text-align:left">Número:</th>
				<td colspan="4">
					<h:inputText id="inputTextNumeroFasciculo" size="20" maxlength="15" value="#{pesquisarFasciculoMBean.obj.numero}"
					 onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkNumero');" />
				</td>
			</tr>
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarEdicao}" styleClass="noborder" id="checkEdicao"/>
				</td>
				<th colspan="2" style="text-align:left">Edição:</th>
				<td colspan="4">
					<h:inputText id="inputTextEdicaoFasciculo" size="12" maxlength="10" value="#{pesquisarFasciculoMBean.obj.edicao}" 
						 onkeypress="return ApenasNumeros(event);"
						 onchange="marcarCheckBox(this, 'formPesquisaFasciculo:checkEdicao');" />
				</td>
			</tr>
			
			
			<tr>
				<td width="2%">
					<%-- Não funciona no t:inputCalendar porque se o usuário clicar no calendário não tem como capturar o evento java script  
					<h:selectBooleanCheckbox value="#{pesquisarFasciculoMBean.buscarData}" styleClass="noborder" id="checkDataCriacao"/>
					--%>
				</td>
				<th colspan="2"  style="text-align:left">Data de criação do fascículo:</th>
				<td style="width: 100px;">
					<table><tr><td>
						<t:inputCalendar value="#{pesquisarFasciculoMBean.dataCriacaoInicio}" id="DataDeCriacaoInicial" size="10" maxlength="10"
							onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true" title="Data de Criação Inicial">
								
						</t:inputCalendar>
						</td><td>
						a
						</td><td>
						<t:inputCalendar value="#{pesquisarFasciculoMBean.dataCriacaoFinal}" id="DataDeCriacaoFinal" size="10" maxlength="10"
							onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true" renderPopupButtonAsImage="true"  title="Data de Criação Final">
								
						</t:inputCalendar>
					</td></tr></table>
				 </td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="7">
						<h:commandButton value="Pesquisar" id="cmdButaoPesquisarFasciculos"  action="#{pesquisarFasciculoMBean.pesquisar}" />
						<h:commandButton value="Limpar" id="cmdLimpar"  action="#{pesquisarFasciculoMBean.limparDadosPesquisa}" />
						
						<c:if test="${substituirFasciculoMBean.obj != null}"> <%-- Se está no caso de uso de substituição e o usuário já escolheu o fascículo para ser substituído --%>
							<h:commandButton value="<< Voltar" id="cmdVoltar" action="#{substituirFasciculoMBean.voltaTelaConfirmaSubstituicao}" />
						</c:if>
						
						<h:commandButton value="Cancelar" id="cmdCancelar" onclick="#{confirm}" immediate="true" action="#{pesquisarFasciculoMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>


	</table>
	
	
	
</h:form>



<%-- por padrão as informações dos detalhes do item não são mostradas --%>
<style>
	table.listagem td.detalhesItem {
		display: none;
		padding: 0;
	}
</style>


<h:form id="formResultadosPesquisaFasciculo">

		<p:resources />

		<link rel="stylesheet" type="text/css" href="/sigaa/css/primefaces_skin.css" />


		<%-- Modal panel com os detalhes de cada material selecionado pelo usuário --%>
	
		<a4j:outputPanel ajaxRendered="true" id="painelInfoCompletaMaterial">
			<c:set var="_material_selecionado" value="${detalhesMateriaisDeUmTituloMBean.materialSelecionado}" scope="request" />
			<c:set var="_artigos_do_fasciculo_selecionado" value="${detalhesMateriaisDeUmTituloMBean.artigosDoFasciculoSelecionado}" scope="request" />
			<c:set var="_reservas_do_material_selecionado" value="${detalhesMateriaisDeUmTituloMBean.reservasDoMaterial}" scope="request" />
			<c:set var="_qtd_emprestimos_materail_selecionado" value="${detalhesMateriaisDeUmTituloMBean.qtdEmprestimosMaterialSelecionado}" scope="request" />
			<c:set var="_is_fasciculo" value="${detalhesMateriaisDeUmTituloMBean.periodico}" scope="request" />
			<c:set var="_is_mostrar_informacoes_titulo" value="${detalhesMateriaisDeUmTituloMBean.mostrarInformacaoTituloDetalhesMaterial}" scope="request" />
			<c:set var="_titulo_do_material" value="${detalhesMateriaisDeUmTituloMBean.tituloDoMaterial}" scope="request" />
			<c:set var="_assinatura_do_material" value="${detalhesMateriaisDeUmTituloMBean.asssinaturaDoMaterial}" scope="request" />
			<%@include file="/public/biblioteca/paginaPadraoDetalhesMaterial.jsp"%>
		</a4j:outputPanel>
	
	
		<c:if test="${pesquisarFasciculoMBean.qtdFasciculos > 0}">
	
			<div class="infoAltRem" style="margin-top: 10px">
			
				<c:if test="${pesquisarFasciculoMBean.operacaoPesquisar}">
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
					Alterar as Informações do Fascículo
				</c:if>
				
				<br/>
				
				<c:if test="${pesquisarFasciculoMBean.operacaoCatalogacaoArtigos}">
					<%-- <h:graphicImage value="/img/view.gif" style="overflow: visible;" />: 
					Visualizar Informações dos Artigos dos Fascículos --%>
				
					<h:graphicImage value="/img/view2.gif" style="overflow: visible;" />: 
					Catalogar um Artigo para esse Fascículo
				</c:if>
				
				<c:if test="${pesquisarFasciculoMBean.operacaoSubstituicao}">
					<h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;" />: 
					Substituir fascículo por outro similar
				</c:if> 
				
				<c:if test="${pesquisarFasciculoMBean.operacaoProcuraFasciculoParaSubstituicao}">
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
					Selecionar fascículo para substituir o anterior
				</c:if> 
				
				<c:if test="${pesquisarFasciculoMBean.operacaoBaixar}">
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
					Selecionar fascículo para realizar a baixa no acervo
				</c:if> 
				
				<c:if test="${pesquisarFasciculoMBean.operacaoDesfazerBaixa}">
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
					Selecionar fascículo para desfazer a baixa no acervo
				</c:if> 
				
				<c:if test="${pesquisarFasciculoMBean.operacaoRemover}">
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
					Selecionar fascículo para realizar a remoção do acervo
				</c:if> 
				
				<c:if test="${pesquisarFasciculoMBean.operacaoBloquearMaterial}">
					<h:graphicImage value="/img/biblioteca/estornar.gif" style="overflow: visible;" />: 
					Selecione o material a ser bloqueado
				</c:if>
				
			</div> 

			<t:div id="divResultados" rendered="#{pesquisarFasciculoMBean.qtdFasciculos > 0}">
	
				<table class="listagem">
					
					<caption>Fascículos no acervo ( ${pesquisarFasciculoMBean.qtdFasciculos} )</caption>
					
					<thead>
						<tr align="center">
							<th width="7%" style="text-align: left;">Código de Barras</th>
							<th width="40%" style="padding-left: 20px;">Assinatura</th>
							<th width="10%" style="text-align: center;">Ano Cronológico</th>
							<th width="5%" style="text-align: center;">Dia/Mês</th>
							<th width="5%" style="text-align: center;">Ano</th>
							<th width="10%" style="text-align: right;">Volume</th>
							<th width="10%" style="text-align: right;">Número</th>
							<th width="10%" style="text-align: right; padding-right: 10px;">Edição</th>
							<th width="1%" style="text-align: right;">Qtd.</th>
							<th width="1%" style="text-align: center;"> </th>
							
							<c:if test="${pesquisarFasciculoMBean.operacaoPesquisar}">
								<th width="1%" style="text-align: center;"> </th>
							</c:if>
						
							<c:if test="${pesquisarFasciculoMBean.operacaoCatalogacaoArtigos}">
								<th width="1%" style="text-align: center;"> </th>
							</c:if>
							
							<c:if test="${pesquisarFasciculoMBean.operacaoSubstituicao}">
								<th width="2%" style="text-align: center;"> </th>
							</c:if>
							
							<c:if test="${pesquisarFasciculoMBean.operacaoProcuraFasciculoParaSubstituicao}">
								<th width="2%" style="text-align: center;"> </th>
							</c:if> 
							
							<c:if test="${pesquisarFasciculoMBean.operacaoBaixar ||
									pesquisarFasciculoMBean.operacaoRemover ||
									pesquisarFasciculoMBean.operacaoDesfazerBaixa}">
								<th width="2%" style="text-align: center;"> </th>
							</c:if>
							
							<c:if test="${pesquisarFasciculoMBean.operacaoBloquearMaterial}">
								<th width="2%" style="text-align: center;"> </th>
							</c:if>
							
						</tr>
					</thead>		
					<c:set var="idFiltro" value="-1" scope="request"/>
					
					<c:forEach items="#{pesquisarFasciculoMBean.fasciculos}" var="fasciculo" varStatus="status">

						<c:if test="${ idFiltro != fasciculo.biblioteca.id}">
							<c:set var="idFiltro" value="${fasciculo.biblioteca.id}" scope="request" />
							<tr class="biblioteca">
								<td colspan="11">${fasciculo.biblioteca.descricao}</td>
							</tr>
						</c:if>

						<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">

							<td style="text-align: left; ${ fasciculo.dadoBaixa ?  'color: red' : '' } ">
								${fasciculo.codigoBarras} <c:if test="${fasciculo.suplemento}"> <span style="font-style: italic;">(suplemento)</span> </c:if>
							</td>

							<td style="padding-left: 20px; ${ fasciculo.dadoBaixa ?  'color: red' : ''} ">
								${fasciculo.assinatura.titulo}
							</td>

							<td style="text-align: center; ${ fasciculo.dadoBaixa ?  'color: red' : '' } ">
								${fasciculo.anoCronologico}
							</td>

							<td style="text-align: center; ${ fasciculo.dadoBaixa ?  'color: red' : '' } ">
								${fasciculo.diaMes}
							</td>

							<td style="text-align: center; ${ fasciculo.dadoBaixa ? 'color: red' : '' } ">
								${fasciculo.ano}
							</td>

							<td style="text-align: right; ${ fasciculo.dadoBaixa ?  'color: red' : '' } ">
								${fasciculo.volume}
							</td>
							
							<td  style="text-align: right; ${ fasciculo.dadoBaixa ?  'color: red' : '' } ">
								${fasciculo.numero}
							</td>
							
							<td style="text-align: right;  padding-right: 10px; ${ fasciculo.dadoBaixa ?  'color: red' : '' } ">
								${fasciculo.edicao}
							</td>
							
							<td width="2%" style="text-align:right">
								${fasciculo.quantidadeArtigos}
							</td>
									
							
							<c:if test="${pesquisarFasciculoMBean.operacaoPesquisar && ! fasciculo.dadoBaixa}">
								<td>	
									<h:commandLink id="cmdLinkEditarFasciculo" action="#{editaMaterialInformacionalMBean.iniciarParaEdicaoFasciculos}">
										<h:graphicImage url="/img/alterar.gif" style="border:none"
											title="Alterar as Informações do Fascículo" />
	
										<f:param name="idFasciculoParaEdicao" value="#{fasciculo.id}"/>					
									</h:commandLink>
								</td> 
							</c:if>
							
							<c:if test="${pesquisarFasciculoMBean.operacaoCatalogacaoArtigos && ! fasciculo.dadoBaixa}">
								<td>	
									<h:commandLink id="cmdLinkCatalogarArtigosFasciculo" action="#{catalogacaoArtigosMBean.iniciarCatalogacao}">
										<h:graphicImage url="/img/view2.gif" style="border:none"
											title="Catalogar um artigo para esse fascículo" />
	
										<f:param name="idFasciculoDoArtigo" value="#{fasciculo.id}"/>					
									</h:commandLink>
								</td>
							</c:if>
							
							
							<c:if test="${pesquisarFasciculoMBean.operacaoSubstituicao}">  <%-- fasciculos baixados podem ser substituídos por outros --%>
								<td style="text-align: center;">
									<h:commandLink id="cmdLinkSubstituirFasciculo" action="#{substituirFasciculoMBean.iniciar}">
										<h:graphicImage url="/img/alterar_old.gif" style="border:none" title="Substituir fascículo por outro similar" />
										<f:param name="idFasciculoSubstituicao" value="#{fasciculo.id}"/>					
									</h:commandLink>
								</td>
							</c:if>

							<c:if test="${pesquisarFasciculoMBean.operacaoProcuraFasciculoParaSubstituicao && ! fasciculo.dadoBaixa}">
								<td style="text-align: center;">
									<h:commandLink id="cmdLinkSelecionarFasciculoSubstituidor" action="#{substituirFasciculoMBean.confirmarSubstituicao}">
										<h:graphicImage url="/img/seta.gif" style="border:none" title="Selecionar fascículo para substituir o anterior" />
										<f:param name="idFasciculoSubstituidor" value="#{fasciculo.id}"/>					
									</h:commandLink>
								</td>
							</c:if>  
							
							
							<c:if test="${pesquisarFasciculoMBean.operacaoBaixar && ! fasciculo.dadoBaixa}">
								<td style="text-align: center;">
									<h:commandLink id="cmdLinkDarBaixaFasciculo" action="#{editaMaterialInformacionalMBean.iniciaParaBaixaFasciculo}">
										<h:graphicImage url="/img/seta.gif" style="border:none" title="Selecionar fascículo para realizar a baixa no acervo" />
										<f:param name="idFasciculoBaixa" value="#{fasciculo.id}"/>
									</h:commandLink>
								</td>
							</c:if>
							
							<c:if test="${pesquisarFasciculoMBean.operacaoDesfazerBaixa && fasciculo.dadoBaixa}">
								<td style="text-align: center;">
									<h:commandLink id="cmdLinkDesfazerBaixaFasciculo" action="#{editaMaterialInformacionalMBean.iniciaParaDesfazerBaixaFasciculo}">
										<h:graphicImage url="/img/seta.gif" style="border:none" title="Selecionar fascículo para desfazer a baixa no acervo" />
										<f:param name="idFasciculoBaixa" value="#{fasciculo.id}"/>
									</h:commandLink>
								</td>
							</c:if>
							
							<c:if test="${pesquisarFasciculoMBean.operacaoRemover && ! fasciculo.dadoBaixa}">
								<td style="text-align: center;">
									<h:commandLink id="cmdLinkRemoverFasciculo" action="#{editaMaterialInformacionalMBean.iniciaParaRemocaoFasciculo}">
										<h:graphicImage url="/img/seta.gif" style="border:none" title="Selecionar Fascículo para realizar a remoção do acervo" />
										<f:param name="idFasciculoRemocao" value="#{fasciculo.id}"/>					
									</h:commandLink>
								</td>
							</c:if>
							
							<c:if test="${pesquisarFasciculoMBean.operacaoBloquearMaterial && ! fasciculo.dadoBaixa}">
								<td style="text-align: center;">
									<h:commandLink id="cmdLinkBloqueiarExemplar" action="#{bloquearMaterialInformacionalMBean.preBloquear}">
										<h:graphicImage url="/img/biblioteca/estornar.gif" style="border:none" title="Clique aqui para informar o motivo e bloquear esse material." />
										<f:param name="idMaterial" value="#{fasciculo.id}"/>					
									</h:commandLink>
								</td>
							</c:if>
							
							
							<%-- Só para completar os espaços na tela --%>
							<c:if test="${(fasciculo.dadoBaixa || ( ( pesquisarFasciculoMBean.operacaoRemover || pesquisarFasciculoMBean.operacaoBaixar || pesquisarFasciculoMBean.operacaoDesfazerBaixa)
									&& exemplar.fasciculo)) && not pesquisarExemplarMBean.operacaoDesfazerBaixa}">
								<td style="text-align: center;">
								</td>
							</c:if>
							
		
						</tr>
			
						<%-- A linha da tabela que mostra os detalhes do fascículo e é habilitado usando JavaScript 
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}"> 
							<td colspan="11" id="linha_${fasciculo.id}" class="detalhesItem" ></td>		
						</tr> --%>
						
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							 <td colspan="11" style="text-align: center;">
							 
							 	
							 	<a4j:commandLink  value="Mostrar Detalhes "  actionListener="#{detalhesMateriaisDeUmTituloMBean.carregarDetalhesMaterialSelecionado}"
							 				ajaxSingle="true" oncomplete="modelPanelDetalhes.show();" style="font-weight: normal; font-style: italic; " 
							 				reRender="formResultadosPesquisaFasciculo">
							 		<f:param name="isPeriodicoVisualzarDetalhes"  value="true" />
							 		<f:param name="isMostrarInformacaoTituloDetalhesMaterial" value="true" />			
						 			<f:param name="idMaterialMostrarDetalhes" value="#{fasciculo.id}"/>
							 	</a4j:commandLink>
							 	
							 	
						    </td>
						    
						</tr>
							

					</c:forEach>
				
					<tfoot>
						<tr>
							<td colspan="12" style="text-align: center; font-weight: bold;">
								<h:outputText id="outTxtQtdItens" 
									value="Quantidade de Fascículos Encontrados: #{pesquisarFasciculoMBean.qtdFasciculos}" 
										rendered="#{pesquisarFasciculoMBean.qtdFasciculos > 0}" />		
							</td>
						</tr>
					</tfoot>
					
				</table>

			</t:div> 
	
		</c:if>	



</h:form>
	
	
</f:view>



<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>