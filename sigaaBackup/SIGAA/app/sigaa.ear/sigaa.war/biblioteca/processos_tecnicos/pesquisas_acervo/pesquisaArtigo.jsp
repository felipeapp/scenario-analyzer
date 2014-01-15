<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

<h2>  <ufrn:subSistema /> &gt; Pesquisa de Artigos de Periódicos</h2>

<div class="descricaoOperacao"> 
    Nessa página é possível realizar uma busca direta dos artigos de periódicos no acervo.
</div>


<style>	
	/* Oculta a linha que visualiza as informações completas de um artigo.*/
	table.listagem td.oculto { display: none; padding: 0;}
		
</style>


<script type="text/javascript">

	//funcao para abrir a página dos dados MARC
	var janela = null;
	
	function abreJanelaInformacoesMARCArtigo(idArtigo){
		if (janela == null || janela.closed){
			janela = window.open('${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/detalhesMARCArtigo.jsf?idArtigo='+idArtigo+'&mostarPaginaDadosMarc=true','','width=1024,height=400,left=50,top=50,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janela.location = '${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/detalhesMARCArtigo.jsf?idArtigo='+idArtigo+'&mostarPaginaDadosMarc=true';
		}
	
		janela.focus();
	}
	
	
	function ativaBotaoFalso() {
		$('formPesquisaArtigo:botaoPesquisar').hide();
		$('formPesquisaArtigo:fakeBotaoPesquisar').show();
		$('indicatorPesquisa').style.display = '';
	}

	ativaBotaoVerdadeiro();

	function ativaBotaoVerdadeiro() {
		$('formPesquisaArtigo:botaoPesquisar').show();
		$('formPesquisaArtigo:fakeBotaoPesquisar').hide();
		$('indicatorPesquisa').style.display = 'none';
	}
	
	
</script>



<script type="text/javascript" src="/sigaa/javascript/biblioteca/functions.js" ></script>

	

	<h:form id="formPesquisaArtigo">

		<%--  Menu exibido com as opções de cada material, para poupar espaço e ícones na tela --%>
	
		<rich:contextMenu attached="false" id="menuOpcoesArtigos" hideDelay="300" >
						     
	           <rich:menuItem value="Alterar" icon="/img/alterar.gif" action="#{catalogacaoArtigosMBean.iniciarParaEdicaoArtigo}">
	           		<f:param name="idArtigoParaEdicao" value="{_id_artigo_context_menu}"/>
	           </rich:menuItem>
	           
	           <rich:menuItem value="Visualizar dados MARC do Artigo" icon="/img/biblioteca/visualizarMarc.png" 
	           			onclick="abreJanelaInformacoesMARCArtigo( {_id_artigo_context_menu} ); return false;">
	           </rich:menuItem>
	           
	           <rich:menuItem value="Remover" icon="/img/delete.gif" 
	           		action="#{removerEntidadeDoAcervoMBean.telaConfirmaRemocaoVindoPaginaPesquisaArtigos}">
	           		<f:param name="idArtigoRemocao" value="{_id_artigo_context_menu}"/>	
	           </rich:menuItem>
	              
	    </rich:contextMenu>












		<a4j:keepAlive beanName="pesquisarArtigoMBean"></a4j:keepAlive>
		
		<table class=formulario  style="margin-bottom:20px; width:70%;">
		
			<caption class="listagem">Entre com os dados do Artigo</caption>
		
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarArtigoMBean.buscarNumeroSistema}" styleClass="noborder" id="checkNumeroSistema"/>
				</td>
				<th  width="30%" style="text-align:left"> Número do Sistema: </th>
		
				<td  width="70%" colspan="6">
					<h:inputText value="#{pesquisarArtigoMBean.numeroDoSistema}" size="10"  maxlength="9" 
								onchange="marcarCheckBox(this, 'formPesquisaArtigo:checkNumeroSistema');"
								onkeyup="return formatarInteiro(this);" > </h:inputText>
				</td>
			</tr>
			
		
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarArtigoMBean.buscarTitulo}" styleClass="noborder" id="checkTitulo" />
				</td>
				<th colspan="1" style="text-align:left">Título:</th>
				<td colspan="2">
					<h:inputText size="50" maxlength="300" value="#{pesquisarArtigoMBean.tituloArtigo}" onchange="marcarCheckBox(this, 'formPesquisaArtigo:checkTitulo');"/>
				</td>
			</tr>
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarArtigoMBean.buscarAutor}" styleClass="noborder" id="checkAutor"/>
				</td>
				<th colspan="1" style="text-align:left">Autor:</th>
				<td colspan="2">
					<h:inputText size="50" maxlength="100" value="#{pesquisarArtigoMBean.autorArtigo}" onchange="marcarCheckBox(this, 'formPesquisaArtigo:checkAutor');"/>
				</td>
			</tr>
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarArtigoMBean.buscarPalavraChave}" styleClass="noborder" id="checkPalavraChave"/>
				</td>
				<th colspan="1" style="text-align:left">Palavra-Chave:</th>
				<td colspan="2">
					<h:inputText size="50" maxlength="100" value="#{pesquisarArtigoMBean.palavraChave}" onchange="marcarCheckBox(this, 'formPesquisaArtigo:checkPalavraChave');"/>
				</td>
			</tr>
			
			<tr>
				<td></td>
				<th style="text-align:left">Ordenação:</th>
				<td colspan="2">
					<h:selectOneMenu value="#{pesquisarArtigoMBean.valorCampoOrdenacao}">
						<f:selectItems value="#{pesquisarArtigoMBean.campoOrdenacaoResultadosComboBox}"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<td></td>
				<th style="text-align:left">Registros por página:</th>
				<td colspan="2">
					<h:selectOneMenu value="#{pesquisarArtigoMBean.quantideResultadosPorPagina}">
						<f:selectItems value="#{pesquisarArtigoMBean.qtdResultadosPorPaginaComboBox}"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan=4>
						<h:commandButton id="botaoPesquisar" value="Pesquisar" action="#{pesquisarArtigoMBean.pesquisar}"  onclick="ativaBotaoFalso();"/>
						<h:commandButton id="fakeBotaoPesquisar" value="Aguarde ..." style="display: none;" disabled="true" />
						<span id="indicatorPesquisa"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
						<h:commandButton value="Limpar" action="#{pesquisarArtigoMBean.limparDadosPesquisa}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{pesquisarArtigoMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>


		</table>

	</h:form>


	



	<t:div id="divResultadoPesquisaArtigos" rendered="#{pesquisarArtigoMBean.quantidadeTotalResultados > 0}">
		

		<h:form id="formResultadosArtigos">
		
		
				<%-- Modal panel com os detalhes de cada material selecionado pelo usuário --%>
	
				<a4j:outputPanel ajaxRendered="true" id="painelInfoCompletaArtigo">
					<c:set var="_artigo_selecionado" value="${detalhesArtigoMBean.obj}" scope="request" />
					<c:set var="_assinatura_artigo_selecionado" value="${detalhesArtigoMBean.assinatura}" scope="request" />
					<c:set var="_fasciculo_artigo_selecionado" value="${detalhesArtigoMBean.fasciculo}" scope="request" />
					<%@include file="/public/biblioteca/paginaPadraoDetalhesArtigo.jsp"%>
				</a4j:outputPanel>
		
		
				<%-- Inclue os links de páginação para percorrer os resultados das pesquisas no acervo --%>
				
				<c:set var="pesquisarAcervoPaginadoBiblioteca" value="${pesquisarArtigoMBean}" scope="request" />
				<%@ include file="/public/biblioteca/pesquisas_acervo/paginaPaginacaoConsultaAcervo.jsp"%>
				
				
				
				
						
				<t:div styleClass="infoAltRem" style="margin-top: 10px">
					
					<h:graphicImage value="/img/submenu.png" style="overflow: visible;" />: Opções
					
				</t:div>
							
				
				<table class="listagem" style="width:98%">
							
					<caption>Artigos no acervo ( <h:outputText value="#{pesquisarArtigoMBean.quantidadeResultadosMostrados}"/>  )</caption>
		
					<thead>
						<tr align="center">
							<th width="15%">Número do Sistema</th>
							
							<th width="20%">Autor</th>
							<th width="35%">Título</th>
							<th width="22%">Palavras-chave</th>
						
							<th style="width: 1%; text-align: center">  </th>
						
						</tr>
						
					</thead>
					
					<tbody>
		
						<c:forEach items="#{pesquisarArtigoMBean.resultadosPaginadosEmMemoria}" var="artigoCache" varStatus="status">
						
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
						
								<td width="5%">
									<h:outputText value="#{artigoCache.numeroDoSistema}" />
								</td>
								
							
								<td width="30%">
									<h:outputText value="#{artigoCache.autor}" />
								</td>
							
								<td width="30%">
									<h:outputText value="#{artigoCache.titulo}" />
								</td>
			
							
									
								<td width="30%">
									<c:forEach items="#{artigoCache.assuntosFormatados}" var="assunto">
										<span style="display: block; white-space: nowrap;">${assunto}</span>
									</c:forEach>
								</td>
								
								<td width="1%">
									<h:graphicImage value="/img/submenu.png">
										<rich:componentControl event="onmouseover" for="menuOpcoesArtigos" operation="show">
									        <f:param value="#{artigoCache.idArtigoDePeriodico}" name="_id_artigo_context_menu"/>
									    </rich:componentControl>
									</h:graphicImage>	
								</td>

							</tr>
							
							
							<%-- Linha estra para visualizar os detalhes dos fascículos --%>
						
							<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td colspan="5" style="text-align: center;">
								 
								 	<a4j:commandLink   value="Mostrar Detalhes "  actionListener="#{detalhesArtigoMBean.carregarDetalhesArtigoSelecionado}"
								 				ajaxSingle="true" oncomplete="modelPanelDetalhesArtigo.show();" style="font-weight: normal; font-style: italic; " 
								 				reRender="formResultadosArtigos">
							 			<f:param name="idArtigoMostrarDetalhes" value="#{artigoCache.idArtigoDePeriodico}"/>
								 	</a4j:commandLink>
								 	
							    </td>
							</tr>
							
										
						</c:forEach>
						
					</tbody>
					
					<tfoot>
						<tr>
							<td colspan="12" style="text-align: center; font-weight: bold;">
								<h:outputText value="#{pesquisarArtigoMBean.quantidadeResultadosMostrados}"/> artigo(s) encontrado(s).
							</td>
						</tr>
					</tfoot>
					
				</table>
			
			
			</h:form>
	
	</t:div>




	
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>