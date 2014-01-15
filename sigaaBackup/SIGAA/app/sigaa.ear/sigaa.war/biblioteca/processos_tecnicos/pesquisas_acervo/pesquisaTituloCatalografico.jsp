<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<c:set var="bibliotecario" value="false" />

<ufrn:checkRole papeis="<%= new int[] {
		SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO,
		SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO,SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO,
		SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_CHECKOUT, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO,
		SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF,
		SigaaPapeis.BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO,
		SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_LOCAL, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL,
		SigaaPapeis.BIBLIOTECA_SETOR_INTERCAMBIO, SigaaPapeis.BIBLIOTECA_SETOR_INTERCAMBIO_BIBLIOTECARIO } %>">
	<c:set var="bibliotecario" value="true" />
</ufrn:checkRole>

<%--                Página geral da pesquisa de títulos catalográficos                   --%>
<%--   Possui as várias páginas dos vários tipos de pesquisa e a página dos resultados    --%>

<link rel="stylesheet" media="all" href="/sigaa/css/biblioteca/estilo_botoes_pequenos.css" type="text/css"/>

<style type="text/css">
	
	<!--
	
	/* Sob escrevendo para essa página o estilo do caption, porque ele estava peguando o do 
	 * subFomulario e eu queria que fosse o do própio Formulário */
	
	
	
	#tableDadosPesquisa caption{
		background:transparent url(/sigaa/public/images/bg_caption.gif) repeat-x scroll center center;
		color:#FFFFFF;
		font-variant:small-caps;
		font-weight:bold;
		letter-spacing:1px;
		margin:0 auto;
		padding:3px 0;
		text-align:center;
	}
	
	#tableDadosPesquisaInterna caption{
		-moz-background-clip:border;
		-moz-background-inline-policy:continuous;
		-moz-background-origin:padding;
		background:#EDF1F8 none repeat scroll 0 0;
		border-color:-moz-use-text-color -moz-use-text-color #C8D5EC;
		border-style:none none solid;
		border-width:0 0 1px;
		color:#333366;
		font-variant:small-caps;
		font-weight:bold;
		letter-spacing:1px;
		margin:1px 0;
		padding:3px 0 3px 20px;
		text-align:left;
	}
	
	-->
	
</style>

<style>	
	table.listagem td.oculto { display: none; padding: 0;}
	
	/* O stilo do campo único da busca simples */
	.campoBuscaSimples{
		box-shadow: 0 2px 2px #8F8F8F inset; color: #313131;
		border-radius: 3px 3px 3px 3px;
		height: 20px;
	}
	
	
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
	
	
	//função para abrir a página dos dados MARC sempre na mesma janela do navegador
	var janela2 = null;
	
	function abreJanelaInformacoesMARCTitulo(idTitulo){
		if (janela2 == null || janela2.closed){
			janela2 = window.open('${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/informacoesMARCTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaDadosMarc=true','','width=1024,height=600,left=50,top=50,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janela2.location = '${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/informacoesMARCTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaDadosMarc=true';
		}
	
		janela2.focus();
	}

	//funcao para abrir a pagina dos endereços eletrônicos de um título
	var janela3 = null;
	
	function abreJanelaEnderecosEletronicos(idTituloCache){
		if (janela3 == null || janela3.closed){
			janela3 = window.open('${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/enderecosEletronicosTitulo.jsf?idTituloCache='+idTituloCache,'','width=1000,height=200,left=100,top=100,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janela3.location = '${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/enderecosEletronicosTitulo.jsf?idTituloCache='+idTituloCache;
		}
	
		janela3.focus();
	}
	
	//função para abrir a página dos dados MARC sempre na mesma janela do navegador
	var janela4 = null;
	
	function abreJanelaInformacoesBibliograficasTitulo(idTitulo){
		if (janela4 == null || janela4.closed){
			janela4 = window.open('${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/informacoesBibliograficasTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaReferencias=true','','width=1024,height=600,left=50,top=50,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janela4.location = '${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/informacoesBibliograficasTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaReferencias=true';
		}
	
		janela4.focus();
	}


	function ativaBotaoFalso() {
		
		$('formBuscaTituloSimples:botaoPesquisar').hide();
		$('formBuscaTituloSimples:fakeBotaoPesquisar').show();
		$('formBuscaTituloMulticampo:botaoPesquisar').hide();
		$('formBuscaTituloMulticampo:fakeBotaoPesquisar').show();
		$('formBuscaTituloAvancada:botaoPesquisar').hide();
		$('formBuscaTituloAvancada:fakeBotaoPesquisar').show();
		$('formBuscaTituloPorListas:botaoPesquisar').hide();
		$('formBuscaTituloPorListas:fakeBotaoPesquisar').show();
		$('indicatorPesquisaTitulosSimples').style.display = '';
		$('indicatorPesquisaTitulosMultiCampo').style.display = '';
		$('indicatorPesquisaTitulosAvancada').style.display = '';
		$('indicatorPesquisaTitulosPorListas').style.display = '';
	}

	ativaBotaoVerdadeiro();

	function ativaBotaoVerdadeiro() {
		$('formBuscaTituloSimples:botaoPesquisar').show();
		$('formBuscaTituloSimples:fakeBotaoPesquisar').hide();
		$('formBuscaTituloMulticampo:botaoPesquisar').show();
		$('formBuscaTituloMulticampo:fakeBotaoPesquisar').hide();
		$('formBuscaTituloAvancada:botaoPesquisar').show();
		$('formBuscaTituloAvancada:fakeBotaoPesquisar').hide();
		$('formBuscaTituloPorListas:botaoPesquisar').show();
		$('formBuscaTituloPorListas:fakeBotaoPesquisar').hide();
		$('indicatorPesquisaTitulosSimples').style.display = 'none';
		$('indicatorPesquisaTitulosMultiCampo').style.display = 'none';
		$('indicatorPesquisaTitulosAvancada').style.display = 'none';
		$('indicatorPesquisaTitulosPorListas').style.display = 'none';
	}

	
</script>

<script type="text/javascript" src="/sigaa/javascript/biblioteca/functions.js" ></script>

<h2>  <ufrn:subSistema /> &gt; Pesquisa de Títulos</h2>

<div class="descricaoOperacao"> 
	<h3>Dicas de busca:</h3>
	<p>Preencha os campos conforme desejado. Usando mais de uma linha, a busca será mais específica.</p>
	<p>O sistema <b>não</b> diferencia caracteres maiúsculos e minúsculos. Por exemplo, o termo <i>computador</i> recupera registros com as palavras <i>computador</i>, <i>Computador</i> e <i>COMPUTADOR</i>.</p>
	
</div>


<f:view>

	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>

	<%-- Usado na pesquisa do caso de uso de incluir notas de circulação --%>
	<a4j:keepAlive beanName="notasCirculacaoMBean"></a4j:keepAlive>

	<%-- Guarda os dados do Mbean da  catalogação na pesquisa --%>
	<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	

	<%-- quando da página de visualização dos materiais inclui-se um anexo e substitui o título do anexo --%>
	<a4j:keepAlive beanName="pesquisarExemplarMBean"></a4j:keepAlive>
	
	
	
	<%-- Tem que guardar os dados desse bean para o caso que a tela de pesquisa é utilizada para tranferência de exemplares entre Títulos --%>
	<a4j:keepAlive beanName="transfereExemplaresEntreTitulosMBean"></a4j:keepAlive>
	
	<%-- Tem que guardar os dados desse bean para o caso que a tela de pesquisa é utilizada para tranferência de exemplares entre Bibliotecas --%>
	<a4j:keepAlive beanName="transfereExemplaresEntreBibliotecasMBean"></a4j:keepAlive>
	

	<%-- usado quando a página de exportação chama a página de consulta de títulos --%>
	<%-- mantém os dados do outro bean--%>
	<a4j:keepAlive beanName="cooperacaoTecnicaExportacaoMBean"></a4j:keepAlive>
		 

	<a4j:keepAlive beanName="associaAssinaturaATituloMBean"></a4j:keepAlive> 
	
	<a4j:keepAlive beanName="pesquisaMateriaisInformacionaisMBean"></a4j:keepAlive>
	
	<a4j:keepAlive beanName="alteraDadosVariosMateriaisMBean"></a4j:keepAlive>
	
	<a4j:keepAlive beanName="modificarNotaCirculacaoVariosMateriaisMBean"></a4j:keepAlive>
	
	<%-- Usado quando na inclusão de um anexo se deseja busca um título para substituir a título do anexo --%>
	<a4j:keepAlive beanName="materialInformacionalMBean"></a4j:keepAlive>

	<%-- Usado quando na inclusão de um anexo se deseja busca um título para substituir a título do anexo --%>
	<a4j:keepAlive beanName="detalhesMateriaisDeUmTituloMBean"></a4j:keepAlive>
	
	<%-- Usado quando a pesquisa de título é chamada para escolher o título da assinatura --%>
	<a4j:keepAlive beanName="assinaturaPeriodicoMBean"></a4j:keepAlive>		
	
	<%-- Usado para pesquisar materiais registrados para um inventário --%>
	<a4j:keepAlive beanName="inventarioAcervoBibliotecaMBean"></a4j:keepAlive> 
	


	<h:form id="formBuscaTitulo">
	
	
		<h:inputHidden id="abaPesquisa" value="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}"/>
		
		<c:if test="${ ( pesquisaTituloCatalograficoMBean.pesquisaTituloParaCatalogacao || pesquisaTituloCatalograficoMBean.pesquisaTituloParaCatalogacaoComTombamento ) 
				&& ! pesquisaTituloCatalograficoMBean.pesquisaAcervoParaSelecao }"> 
	
			<table  width="100%">
			
				<tr>
					<td>
						<ul class="listaOpcoes" style="height: 67px; width: 465px;">
						
							<li id="btnAdicionarNovoTitulo">
								<h:commandLink styleClass="noborder" title="Adicionar Novo Título"
										action="#{catalogacaoMBean.iniciar}" id="linkAdicionarNovoTitulo">
									Adicionar Novo Título
								</h:commandLink>
							</li>
						
							<li id="btnAdicionarNovoTituloPlanilha">
								<h:commandLink styleClass="noborder" title="Adicionar Novo Título Usando Planilha"
										action="#{catalogacaoMBean.telaEscolhePlanilhaBibliografica}" id="linkAdicionarNovoTituloUsandoPlanilha">
									Adicionar Título Usando Planilha
								</h:commandLink>
							</li>
							
							<li id="btnImportarTitulo">
								<h:commandLink styleClass="noborder" title=" Importar Título"
										action="#{ cooperacaoTecnicaImportacaoMBean.iniciarImportacaoBibliografica}" id="linkImportarTitulo">
									Importar Novo Título
								</h:commandLink>
							</li>
							
							<li id="btnIncluirTituloDefesa">
								<h:commandLink styleClass="noborder" title="Catalogar Informações de Defesas da UFRN"
										action="#{consultarDefesaMBean.iniciar}" id="linkCatalogarDefesasDaUFRN">
									Catalogar Defesa
									<f:param name="catalogar" value="true"/>
								</h:commandLink>
							</li>
							
						</ul>
					</td>
				</tr>
				
			</table>
	
		</c:if>
		
		</h:form>
		
		
		<table class="formulario" width="100%">

			<tr>
				<td>
					<div id="abas-tipos-de-pesquisa">
					
						<div id="buscaPorListas" class="aba">
							<h:form id="formBuscaTituloPorListas">
							<%@include file="/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp"%>
							</h:form>
						</div>
					
						<div id="buscaAvancada" class="aba">
							<h:form id="formBuscaTituloAvancada">
							<%@include file="/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp"%>
							</h:form>
						</div>
						
						<div id="buscaMultiCampo" class="aba">
							<h:form id="formBuscaTituloMulticampo">
							<%@include file="/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp"%>
							</h:form>
						</div>
						
						<div id="buscaSimples" class="aba">
							<h:form id="formBuscaTituloSimples">
							<%@include file="/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaSimplesTituloCatalografico.jsp"%>
							</h:form>
						</div>
						
					</div>
				</td>
			</tr>
		
		
		</table>
		
	
	<%-- estilo e funcões utilizadas para ordenar os resultados via java script --%>

	<script type="text/javascript" src="/shared/javascript/jquery.tablesorter.min.js"></script>
	<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />






	<%-- A página na qual são mostrados os resultados da consulta dos TÍTULOS --%>
	<h:form>
	
	
	<t:div id="divResultadoPesquisaTitulose" rendered="#{pesquisaTituloCatalograficoMBean.quantidadeTotalResultados > 0}">

		<%-- Inclue os links de páginação para percorrer os resultados das pesquisas no acervo --%>
				
		<c:set var="pesquisarAcervoPaginadoBiblioteca" value="${pesquisaTituloCatalograficoMBean}" scope="request" />
		<%@ include file="/public/biblioteca/pesquisas_acervo/paginaPaginacaoConsultaAcervo.jsp"%>



		<%--  Menu com as ações que podem ser realizadas sobre cada Título do acervo  --%>
	
		<rich:contextMenu attached="false" id="menuOpcoesTitulosEncontrados" hideDelay="300" >
				
				<%-- Visualizar os Materiais  --%>
							
				<rich:menuItem value="Visualizar os Materiais" icon="/img/view.gif" action="#{detalhesMateriaisDeUmTituloMBean.visualizarMateriaisTitulo}">	
					<f:param name="idTitulo" value="{_id_titulo_context_menu}"/>		
	            </rich:menuItem>
				
				 <%-- Referencia e Ficha  --%>
	             	
	            <rich:menuItem value="Formatos Bibliograficos" icon="/ava/img/book_open.png" 
	            	onclick="abreJanelaInformacoesBibliograficasTitulo({_id_titulo_context_menu}); return false;">
	            </rich:menuItem>
				
				
				 <%-- MARC  --%>
	             	
	            <rich:menuItem value="MARC" icon="/img/biblioteca/visualizarMarc.png" 
	            	onclick="abreJanelaInformacoesMARCTitulo({_id_titulo_context_menu}); return false;">
	            </rich:menuItem>
				
				
				<%-- Editar o Título --%>		
			
				<rich:menuItem value="Editar" icon="/img/alterar.gif" action="#{pesquisaTituloCatalograficoMBean.editarTitulo}">	
					<f:param name="idTituloParaEdicao" value="{_id_titulo_context_menu}"/>		
	            </rich:menuItem>
				
				<%-- Adicionar Materiais ao Título --%>		
				
				<rich:menuItem value="Adicionar Materiais" icon="/img/biblioteca/add_materiais.png"
						rendered="#{(pesquisaTituloCatalograficoMBean.pesquisaTituloParaCatalogacao || pesquisaTituloCatalograficoMBean.pesquisaTituloParaCatalogacaoComTombamento ) && ! pesquisaTituloCatalograficoMBean.pesquisaTituloApenasCatalogacao}" 
						action="#{pesquisaTituloCatalograficoMBean.inserirMateriaisTituloSelecionado}">	
					<f:param name="idTituloParaInclusaoMaterial" value="{_id_titulo_context_menu}"/>		
	            </rich:menuItem>
				
				<%-- Duplicar  --%>		
				<rich:menuItem value="Duplicar" icon="/img/biblioteca/duplicar.png" 
						rendered="#{pesquisaTituloCatalograficoMBean.pesquisaTituloParaCatalogacao || pesquisaTituloCatalograficoMBean.pesquisaTituloParaCatalogacaoComTombamento}"
						action="#{pesquisaTituloCatalograficoMBean.duplicarTituloCatalografico}">	
					<f:param name="idTituloParaDuplicacao" value="{_id_titulo_context_menu}"/>		
	            </rich:menuItem>
	            
	            <%-- Remover  --%>		
	          
           		<rich:menuItem value="Remover" icon="/img/delete.gif" action="#{removerEntidadeDoAcervoMBean.telaConfirmaRemocaoVindoPaginaPesquisaTitulo}">	
					<f:param name="idTituloRemocao" value="{_id_titulo_context_menu}"/>		
            	</rich:menuItem>

				<rich:menuItem value="Historico de Alteracoes" icon="/img/clock.png" action="#{emiteRelatorioHistoricosMBean.iniciarConsultaAlteracaoTitulo}">	
					<f:param name="idTitulo" value="{_id_titulo_context_menu}"/>		
            	</rich:menuItem>
	              
	    </rich:contextMenu>



		<div class="infoAltRem" style="margin-top: 10px">

			<c:if test="${! pesquisaTituloCatalograficoMBean.pesquisaAcervoParaSelecao}">
				<h:graphicImage value="/img/submenu.png" style="overflow: visible;" />: Opções
			</c:if>
	
			<c:if test="${pesquisaTituloCatalograficoMBean.pesquisaAcervoParaSelecao}">
				<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar
			</c:if>

		</div> 

		


		<%-- Tabela para mostrar o cabeçalho já que os dados estão sendo mostrados em uma rich datable eu não --%>
		<%-- E é mais difícil tentar formatar a rich dataTable para ter um estido igual do das tabelas padroes usadas --%>

		<table class="listagem tablesorter" id="listagem">
			<caption>  Títulos Encontrados ( <h:outputText value="#{pesquisaTituloCatalograficoMBean.descricaoPaginacao}"/> )</caption>
			<thead>
				<tr>
					
					<th style="width: 5%; text-align: left;"> <span style="white-space: nowrap; margin-right:15px;">Nº Sistema</span></th>
					
					<c:if test="${sessionScope.abaPesquisa != 'buscaPorListas'}">
						<th style="width: 25%;"> Autor </th>
						<th style="width: 30%;"> Título </th>
						<th style="width: 6%; text-align: center"> <span style="white-space: nowrap; margin-right:15px;">Edição</span> </th>
						<th style="width: 6%; padding-left: 5px;"> <span style="white-space: nowrap; margin-right:15px;">Ano</span> </th>
						<th style="width: 27%;"> Assunto </th>
						<th style="width: 27%;"> Nº Chamada </th>
					</c:if>
					
					<c:if test="${sessionScope.abaPesquisa == 'buscaPorListas'}">
						<th style="width: 92%;"> ${pesquisaTituloCatalograficoMBean.campoPesquisaPorLista.descricaoTipoCampoEscolhido} </th>
					</c:if>
					
					
					<th style="width: 1%; text-align: center"> <span style="white-space: nowrap; margin-right:15px;">Qtd.</span>  </th>
					<th style="width: 1%; text-align: center"> </th>  <%--  Opções --%>
					
				</tr>
			</thead>

			<tbody>

				<c:forEach items="#{pesquisaTituloCatalograficoMBean.resultadosPaginadosEmMemoria}" var="tituloCache" varStatus="status">


					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">

							<td style="${tituloCache.catalogado ? " " : "color:red"}; text-align: right" width="5%">
								${tituloCache.numeroDoSistema}
							</td>
							
							<c:if test="${sessionScope.abaPesquisa != 'buscaPorListas'}">
							
								<td style="${tituloCache.catalogado ? " " : "color:red"}" width="30%">
									${tituloCache.autor}
								</td>
								
								<td style="${tituloCache.catalogado ? " " : "color:red"}" width="30%">
									${tituloCache.titulo} ${tituloCache.meioPublicacao} ${tituloCache.subTitulo}
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
								
							</c:if>
							
							<c:if test="${sessionScope.abaPesquisa == 'buscaPorListas'}">
								
								<td width="92%">
									<table width="100%">
										<tbody style="background-color: transparent;">
											<tr>
												<td style="${tituloCache.catalogado ? " " : "color:red"}">
													<c:forEach items="${tituloCache.campoMostrarUsuarioPesquisaLista}" var="campo">
														${campo}
													</c:forEach>
												</td>
											</tr>
										</tbody>
									</table>
								</td>
								
							</c:if>
							
							
							<td width="1%" style="text-align:right; ${tituloCache.catalogado ? " " : "color:red"}">
								${tituloCache.quantidadeMateriaisAtivosTitulo}
							</td>
							
							<c:if test="${pesquisaTituloCatalograficoMBean.pesquisaAcervoParaSelecao}">
								<td width="1%" style="text-align:center">
									<h:commandLink action="#{pesquisaTituloCatalograficoMBean.selecionouTitulo}" 
											rendered="#{pesquisaTituloCatalograficoMBean.pesquisaAcervoParaSelecao}"> 
										<h:graphicImage url="/img/seta.gif" style="border:none" title="Selecionar Título" />
										<f:param name="idTitulo" value="#{tituloCache.idTituloCatalografico}"/>
									</h:commandLink>
								</td>
							</c:if>
							
							<c:if test="${! pesquisaTituloCatalograficoMBean.pesquisaAcervoParaSelecao}">
								<td width="1%"> <%-- Opções a serem realizadas sobre o título selecionado dependendo do caso de uso que está utilizando a busca --%>	
									<h:graphicImage value="/img/submenu.png" rendered="#{! pesquisaTituloCatalograficoMBean.pesquisaAcervoParaSelecao}" title="Opções">
										<rich:componentControl event="onmouseover" for="menuOpcoesTitulosEncontrados" operation="show"
												rendered="#{! pesquisaTituloCatalograficoMBean.pesquisaAcervoParaSelecao}">
									        <f:param value="#{tituloCache.idTituloCatalografico}" name="_id_titulo_context_menu"/>
									   		<f:param value="#{tituloCache.quantidadeMateriaisAtivosTitulo}" name="_qtd_materiais_titulo_context_menu"/>
									    </rich:componentControl>
									</h:graphicImage>
								</td>
							</c:if>

						</tr>



						<%-- Linha estra para visualizar as inforamções eletrônicos do Título --%>
						
						<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
							<td colspan="9" style="font-size: xx-small; font-style: italic; font-weight: normal; text-align: center;">
								<c:if test="${tituloCache.idObraDigitalizada != null}">
									<a target="_blank" href="${ configSistema['linkSigaa'] }/sigaa/verProducao?idProducao=${tituloCache.idObraDigitalizada}&key=${ sf:generateArquivoKey(tituloCache.idObraDigitalizada) }">
										<h:graphicImage url="/img/pdf.png" style="border:none" title="Arquivo Digital" />
										Arquivo Digital
									</a>
								</c:if>
								
								<c:if test="${tituloCache.possuiEnderecoEletronico}">	
									<a href="#buscarEnderecos" onClick="abreJanelaEnderecosEletronicos(${tituloCache.id})" style="padding-left: 40px;">
										<img src="${ctx}/img/biblioteca/enderecoEletronico.png" title="Endereços Eletrônicos"/>
										 Endereços Eletrônicos
									</a>
								</c:if>
								
							</td>	
						</tr>

				</c:forEach>

			</tbody>

			<tfoot>
				<tr>
					<td colspan="15" style="text-align: center; font-weight: bold;">
						<h:outputText value="#{pesquisaTituloCatalograficoMBean.descricaoPaginacao}"/> título(s).
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		<%--  Não funciona com a adição de uma linha extra para o pfd e endereço eletronicos, mas é possível agora ordenar direto da pesquisa. 
		  A parte que permite o usuário ordenar a pesquisa usando java script para os resultados normais da pesquisa 
		<c:if test="${sessionScope.abaPesquisa != 'buscaPorListas'}">
			<rich:jQuery selector="#listagem" query="tablesorter( {headers: {7: { sorter: false } } });" timing="onload" /> 
		</c:if>
		
		<%-- A parte que permite o usuário ordenar a pesquisa usando java script para os resultados da busca por listas 
		<c:if test="${sessionScope.abaPesquisa == 'buscaPorListas'}">
			<rich:jQuery selector="#listagem" query="tablesorter( {headers: {3: { sorter: false }, 4: { sorter: false }, 5: { sorter: false }, 6: { sorter: false }, 7: { sorter: false },7: { sorter: false }, 8: { sorter: false }, 9: { sorter: false }, 10: { sorter: false }, 11: { sorter: false }, 12: { sorter: false }  } });" timing="onload" /> 
		</c:if> --%>
		
	</t:div>
	
</h:form>
	
	
	

	<%-- Mostra nessa página também o resulatado da pesquia na base de artigos do acervo  --%>
	<h:form id="formResultadosArtigos">
	
		<t:div style="margin-top: 50px;" 
			rendered="#{pesquisaTituloCatalograficoMBean.quantidadeArtigosEncontados > 0 
							&& ! pesquisaTituloCatalograficoMBean.pesquisaAcervoParaSelecao}">  <%--  Nos casos de uso para selecionar um Título, não mostrar os artigos --%>
							
	
			<%-- Modal panel com os detalhes de cada material selecionado pelo usuário --%>
	
			<a4j:outputPanel ajaxRendered="true" id="painelInfoCompletaArtigo">
				<c:set var="_artigo_selecionado" value="${detalhesArtigoMBean.obj}" scope="request" />
				<c:set var="_assinatura_artigo_selecionado" value="${detalhesArtigoMBean.assinatura}" scope="request" />
				<c:set var="_fasciculo_artigo_selecionado" value="${detalhesArtigoMBean.fasciculo}" scope="request" />
				<%@include file="/public/biblioteca/paginaPadraoDetalhesArtigo.jsp"%>
			</a4j:outputPanel>
	
	
			
			<%--  Menu com as ações que podem ser realizadas sobre os artigos   --%>
	
			<rich:contextMenu attached="false" id="menuOpcoesArtigosEncontrados" hideDelay="300" >
			
					 <%-- MARC  --%>
		             	
		            <rich:menuItem value="MARC" icon="/img/biblioteca/visualizarMarc.png" 
		            	onclick="abreJanelaInformacoesMARCArtigo({_id_artigo_context_menu}); return false;">
		            </rich:menuItem>
					
					
					<%-- Editar o Artigo --%>		
				
					<rich:menuItem value="Editar" icon="/img/alterar.gif" action="#{catalogacaoArtigosMBean.iniciarParaEdicaoArtigo}">	
							<f:param name="idArtigoParaEdicao" value="{_id_artigo_context_menu}"/>
							<f:param name="voltarParaRequest" value="true"/>
		            </rich:menuItem>
					
					
		            
		            <%-- Remover  --%>		
		          
	           		<rich:menuItem value="Remover" icon="/img/delete.gif" action="#{removerEntidadeDoAcervoMBean.telaConfirmaRemocaoVindoPaginaPesquisaArtigos}">	
						<f:param name="idArtigoRemocao" value="{_id_artigo_context_menu}"/>
						<f:param name="voltarParaRequest" value="true"/>		
	            	</rich:menuItem>
		              
		    </rich:contextMenu>
	
	
			<div class="infoAltRem" style="margin-top: 10px">
			
				<h:graphicImage value="/img/submenu.png" style="overflow: visible;" />: Opções
				
			</div>
					
		
			<table class="listagem" style="width:100%">
						
				<caption>Artigos Encontrados ( ${fn:length( pesquisaTituloCatalograficoMBean.artigos)} )</caption>
	
				<thead>
					<tr align="center">
						
						<th width="5%">Nº do Sistema</th>
						
						<th  width="30%">Autor</th>
						<th  width="34%">Título</th>
						<th  width="30%">Palavras-chave</th>
						
					
						<th style="width: 1%; text-align: center">  </th>
					
					</tr>
					
				</thead>
				
				<tbody>
	
					<c:forEach items="#{pesquisaTituloCatalograficoMBean.artigos}" var="artigoCache" varStatus="status">
					
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
					
							<td width="5%">
								${artigoCache.numeroDoSistema}
							</td>
							
							<td>
								${artigoCache.autor}
							</td>
							
							<td>
								${artigoCache.titulo}
							</td>
							
							<td>
								<c:forEach items="${artigoCache.assuntosFormatados}" var="assunto">
									<span style="display: block; white-space: nowrap;">${assunto}</span>
								</c:forEach>
							</td>
							
							<td>
								<h:graphicImage value="/img/submenu.png" rendered="#{! pesquisaTituloCatalograficoMBean.pesquisaAcervoParaSelecao}" title="Opções">
									<rich:componentControl event="onmouseover" for="menuOpcoesArtigosEncontrados" operation="show"
												rendered="#{! pesquisaTituloCatalograficoMBean.pesquisaAcervoParaSelecao}">
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
							<h:outputText value="#{pesquisaTituloCatalograficoMBean.quantidadeArtigosEncontados}" /> artigo(s) encontrado(s).
						</td>
					</tr>
				</tfoot>
				
			</table>
				
		</t:div>
	
	</h:form>



</f:view>

<script>
var AbasPesquisa = {
    init : function(){
        var abasPesquisa = new YAHOO.ext.TabPanel('abas-tipos-de-pesquisa');

        abasPesquisa.addTab('buscaSimples', "Busca Simples");
        abasPesquisa.addTab('buscaMultiCampo', "Busca Multi-Campo");
        abasPesquisa.addTab('buscaAvancada', "Busca Avançada");
        abasPesquisa.addTab('buscaPorListas', "Busca por Listas");
        
        <c:if test="${sessionScope.abaPesquisa == null || sessionScope.abaPesquisa == ''}">
			abasPesquisa.activate('buscaMultiCampo');
    	</c:if>


    	<c:if test="${sessionScope.abaPesquisa != null && sessionScope.abaPesquisa != ''}">

		    <c:if test="${sessionScope.abaPesquisa == 'buscaSimples'}">
				abasPesquisa.activate('buscaSimples');
			</c:if>
			<c:if test="${sessionScope.abaPesquisa == 'buscaMultiCampo'}">
				abasPesquisa.activate('buscaMultiCampo');
			</c:if>
			<c:if test="${sessionScope.abaPesquisa == 'buscaAvancada'}">
				abasPesquisa.activate('buscaAvancada');
			</c:if>
			<c:if test="${sessionScope.abaPesquisa == 'buscaPorListas'}">
				abasPesquisa.activate('buscaPorListas');
			</c:if>
    	</c:if>
    	
	}
};

YAHOO.ext.EventManager.onDocumentReady(AbasPesquisa.init, AbasPesquisa, true);


</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>