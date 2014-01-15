<%@include file="/public/include/cabecalho.jsp" %>
<%@page import="br.ufrn.arq.seguranca.SigaaSubsistemas"%>

<script type="text/javascript" >


    	//funcao para abrir a pagina dos endere�os eletr�nicos de um t�tulo
	var janela2 = null;
	
	function abreJanelaEnderecosEletronicos(idTituloCache){
		if (janela2 == null || janela2.closed){
			janela2 = window.open('${ctx}/public/biblioteca/enderecosEletronicosTitulo.jsf?idTituloCache='+idTituloCache,'','width=1000,height=200,left=100,top=100,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janela2.location = '${ctx}/public/biblioteca/enderecosEletronicosTitulo.jsf?idTituloCache='+idTituloCache;
		}
	
		janela2.focus();
	}

	//funcao para abrir a pagina dos dados MARC sempre na mesma janela do navegador
	var janela = null;
	
	function abreJanelaInformacoesMARCTitulo(idTitulo){
		if (janela == null || janela.closed){
			janela = window.open('${ctx}/public/biblioteca/informacoesMarcTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaDadosMarc=true','janela','width=1024,height=400,left=50,top=50,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janela.location = '${ctx}/public/biblioteca/informacoesMarcTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaDadosMarc=true';
		}
	
		janela.focus();
	}

	//funcao para abrir a pagina dos dados MARC sempre na mesma janela do navegador
	var janela4 = null;
	
	function abreJanelaInformacoesCompletasAutoridade(idAutoridade){
		if (janela4 == null || janela4.closed){
			janela4 = window.open('${ctx}/public/biblioteca/informacoesMarcAutoridade.jsf?idAutoridade='+idAutoridade+'&mostarPaginaDadosMarc=true','janela4','width=1024,height=400,left=50,top=50,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janela4.location = '${ctx}/public/biblioteca/informacoesMarcAutoridade.jsf?idAutoridade='+idAutoridade+'&mostarPaginaDadosMarc=true';
		}
	
		janela4.focus();
	}
	
	//funcao para abrir a pagina dos dados MARC sempre na mesma janela do navegador
	var janela3 = null;
	
	function abreJanelaInformacoesBibliograficasTitulo(idTitulo){
		if (janela3 == null || janela3.closed){
			janela3 = window.open('${ctx}/public/biblioteca/paginaPublicaVisualizaFormatosBibliograficoTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaReferencias=true&exibirPaginaFichaCatalografica=true','janela3','width=1024,height=500,left=50,top=50,dependent=yes,scrollbars=yes,status=yes,resizable=no');
		}else{
			janela3.location = '${ctx}/public/biblioteca/paginaPublicaVisualizaFormatosBibliograficoTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaReferencias=true&exibirPaginaFichaCatalografica=true';
		}
	
		janela3.focus();
	}	

</script>

<script type="text/javascript" src="/sigaa/javascript/biblioteca/functions.js" ></script>


<style>	
	
	#tabelaInterna tbody{
		background-color: transparent;
	}
	
	.campoBuscaSimples{
		box-shadow: 0 2px 2px #8F8F8F inset; color: #313131;
		border-radius: 3px 3px 3px 3px;
		height: 20px;
	}
	
</style>

<h2>
	 Sistema de Bibliotecas
</h2>


<div class="descricaoOperacao"> 
	<h4 style="text-align:left; ">Dicas de busca:</h4>
	<p>Preencha os campos conforme desejado. Usando mais de uma linha, a busca ser� mais espec�fica.</p>
	<p>O sistema <b>n�o</b> diferencia caracteres mai�sculos e min�sculos, nem acentos. Por exemplo, o termo 
	<i>bibliogr�fico</i> recupera registros com as palavras: <i>bibliografico</i>, <i>Bibliografico</i>, <i>Bibliogr�fico</i>, <i>BIBLIOGRAFICO</i> e <i>BIBLIOGR�FICO</i>.</p>
</div>




<f:view>

	<a4j:keepAlive beanName="pesquisaPublicaBibliotecaMBean"></a4j:keepAlive>


	<h:form id="formBuscaPublica">

		
		<div style="text-align:center; font-weight: bold; color: #666666; margin-bottom: 20px;  margin-top: 20px; margin-right: auto; margin-left: auto;">		
			
			<c:if test="${pesquisaPublicaBibliotecaMBean.buscaSimples}">
				<span style="padding-right: 40px;" > Busca Simples</span>
				<h:commandLink id="buscaMultiCampo" style="padding-right: 40px;"  action="#{pesquisaPublicaBibliotecaMBean.configuraBuscaMultiCampo}"> Busca Multi-Campo >> </h:commandLink>
				<h:commandLink id="buscaAvancada" style="padding-right: 40px;" action="#{pesquisaPublicaBibliotecaMBean.configuraBuscaAvancada}"> Busca Avan�ada >> </h:commandLink>
				<h:commandLink id="buscaSimplesAutoridades" style="padding-right: 40px;" action="#{pesquisaPublicaBibliotecaMBean.configuraBuscaAutoridadesSimples}"> Busca de Autoridades >> </h:commandLink>
			</c:if>
			
			<c:if test="${pesquisaPublicaBibliotecaMBean.buscaMultiCampo}">
				<h:commandLink id="buscaSimples" style="padding-right: 40px;" action="#{pesquisaPublicaBibliotecaMBean.configuraBuscaSimples}"> << Busca Simples </h:commandLink>
				<span style="padding-right: 40px;" > Busca Multi Campo</span>
				<h:commandLink id="buscaAvancada" style="padding-right: 40px;" action="#{pesquisaPublicaBibliotecaMBean.configuraBuscaAvancada}"> Busca Avan�ada >> </h:commandLink>
				<h:commandLink id="buscaSimplesAutoridades" style="padding-right: 40px;" action="#{pesquisaPublicaBibliotecaMBean.configuraBuscaAutoridadesSimples}"> Busca de Autoridades >> </h:commandLink>
			</c:if>
			
			<c:if test="${pesquisaPublicaBibliotecaMBean.buscaAvancada}">
				<h:commandLink id="buscaSimples" style="padding-right: 40px;" action="#{pesquisaPublicaBibliotecaMBean.configuraBuscaSimples}"> << Busca Simples </h:commandLink>
				<h:commandLink id="buscaMultiCampo" style="padding-right: 40px;" action="#{pesquisaPublicaBibliotecaMBean.configuraBuscaMultiCampo}"> << Busca Multi-Campo </h:commandLink>
				<span style="padding-right: 40px;" > Busca Avan�ada</span>
				<h:commandLink id="buscaSimplesAutoridades" style="padding-right: 40px;" action="#{pesquisaPublicaBibliotecaMBean.configuraBuscaAutoridadesSimples}"> Busca de Autoridades >> </h:commandLink>
			</c:if>
			
			<c:if test="${pesquisaPublicaBibliotecaMBean.buscaAutoridadesSimples}">
				<h:commandLink id="buscaSimples" style="padding-right: 40px;" action="#{pesquisaPublicaBibliotecaMBean.configuraBuscaSimples}"> << Busca Simples </h:commandLink>
				<h:commandLink id="buscaMultiCampo" style="padding-right: 40px;" action="#{pesquisaPublicaBibliotecaMBean.configuraBuscaMultiCampo}"> << Busca Multi-Campo </h:commandLink>
				<h:commandLink id="buscaAvancada" style="padding-right: 40px;"  action="#{pesquisaPublicaBibliotecaMBean.configuraBuscaAvancada}"> << Busca Avan�ada </h:commandLink>
				<span style="padding-right: 40px;" > Busca de Autoridades</span>
			</c:if>
			
		</div> 



		<%-- Usando p�ginas diferentes para a pesquisa multicampo e avan�ada   --%>
		
		<c:if test="${ pesquisaPublicaBibliotecaMBean.buscaSimples}">
			<%@include file="/public/biblioteca/buscaPublicaSimplesAcervo.jsp"%>
		</c:if>
		
		<c:if test="${ pesquisaPublicaBibliotecaMBean.buscaMultiCampo}">
			<%@include file="/public/biblioteca/buscaPublicaMultiCampoAcervo.jsp"%>
		</c:if>

		<c:if test="${ pesquisaPublicaBibliotecaMBean.buscaAvancada}">
			<%@include file="/public/biblioteca/buscaPublicaAvancadaAcervo.jsp"%>
		</c:if>
		
		<c:if test="${ pesquisaPublicaBibliotecaMBean.buscaAutoridadesSimples}">
			<%@include file="/public/biblioteca/buscaPublicaSimplesAutoridades.jsp"%>
		</c:if>
		
		
		<%--    mostra os resultados da busca    --%>

		<t:div style="text-align: center; margin-top: 20px;  margin-bottom: 20px;  color: #404E82; " rendered="#{! pesquisaPublicaBibliotecaMBean.buscaAutoridadesSimples}">
			
			<c:if test="${usuario == null}" >
				<a style="color: #404E82; font-weight: bold;" 
				href="/sigaa/verTelaLogin.do?urlRedirect=/sigaa/biblioteca/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsf&subsistemaRedirect=<%=SigaaSubsistemas.BIBLIOTECA.getId()%>">
				N�o encontrou o que estava procurando?  Cadastre-se para receber avisos quando novos materiais forem inclu�dos no acervo. 
				</a>
				<br/>
				(requer autentica��o)
			</c:if>
			
			<c:if test="${usuario != null}" >
				<a style="color: #404E82; font-weight: bold;" 
					href="/sigaa/biblioteca/disseminacao_seletiva_informacao/listaMeuPerfilDeInteresse.jsf">
					N�o encontrou o que estava procurando?  Cadastre-se para receber avisos quando novos materiais forem inclu�dos no acervo. 
				</a>
				<br/>
				(usu�rio j� autenticado)
			</c:if>
			
			
		</t:div>


		<%-- Inclue os links de p�gina��o para percorrer os resultados das pesquisas no acervo --%>
				
		<c:set var="pesquisarAcervoPaginadoBiblioteca" value="${pesquisaPublicaBibliotecaMBean}" scope="request" />
		<%@ include file="/public/biblioteca/pesquisas_acervo/paginaPaginacaoConsultaAcervo.jsp"%>
	

		<t:div id="divResultadoPesquisaTitulos" rendered="#{pesquisaPublicaBibliotecaMBean.quantidadeTotalResultados > 0 && ! pesquisaPublicaBibliotecaMBean.buscaAutoridadesSimples}">
		
		
			<rich:contextMenu attached="false" id="menuOpcoesTitulosEncontrados" hideDelay="300" >
				
				 <%-- Referencia e Ficha  --%>
	             	
	            <rich:menuItem value="Formatos Bibliograficos" icon="/ava/img/book_open.png" 
	            	onclick="abreJanelaInformacoesBibliograficasTitulo({_id_titulo_context_menu}); return false;">
	            </rich:menuItem>
				
				
				 <%-- MARC  --%>
	             	
	            <rich:menuItem value="MARC" icon="/img/biblioteca/visualizarMarc.png" 
	            	onclick="abreJanelaInformacoesMARCTitulo({_id_titulo_context_menu}); return false;">
	            </rich:menuItem>
				
				
				<%-- EXPORTAR  --%>
	             	
	            <rich:menuItem value="Exportar para MARC" icon="/img/biblioteca/exportar.png"  action="#{cooperacaoTecnicaExportacaoMBean.exportarArquivoPublico}">
	            	<f:param name="idTituloParaExportacao" value="{_id_titulo_context_menu}"/>
	            </rich:menuItem>
				
	    	</rich:contextMenu>
		


			<div class="infoAltRem" style="margin-top: 10px">
								
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Informa��es dos Materiais Informacionais
				&nbsp &nbsp &nbsp &nbsp &nbsp
				<h:graphicImage value="/img/submenu.png" style="overflow: visible;" />: Op��es	
			</div> 


			<table class="listagem" width="100%"  style="margin-bottom: 20px" id="listagem">
				<caption>  T�tulos Encontrados ( <h:outputText value="#{pesquisaPublicaBibliotecaMBean.descricaoPaginacao}" /> )</caption>

				<thead>
					<tr>
						<th style="width: 28%;"> Autor </th>
						<th style="width: 36%;"> T�tulo </th>
						<th style="width: 8%; "> Edi��o </th>
						<th style="width: 8%; "> Ano </th>
						<th style="width: 1%; text-align: right"> Qtd.</th>
						<th style="width: 1%; text-align: center">  </th> <%-- Visualizar Materiais --%>
						<th style="width: 1%; text-align: center">  </th> <%-- Outras op��es --%>
					</tr>
				</thead>
				
				<c:forEach items="#{pesquisaPublicaBibliotecaMBean.resultadosPaginadosEmMemoria}" var="titulo" varStatus="status">
				
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
						
						<td>
							${titulo.autor}
						</td>
						
						<td>
							${titulo.titulo} ${titulo.meioPublicacao} ${titulo.subTitulo}
						</td>
						
						<td>
							<c:forEach items="${titulo.edicoesFormatadas}" var="edicao">
								${edicao}
							</c:forEach>
						</td>	
												
						<td>
							<table width="100%">
								<tbody style="background-color: transparent;">
									<tr>
										<td>
											<c:forEach items="${titulo.anosFormatados}" var="ano">
												${ano}
											</c:forEach>
										</td>
									</tr>
								</tbody>
							</table>
						</td>
							
						<td width="1%" style="text-align: right">	 
							<h:outputText value="#{titulo.quantidadeMateriaisAtivosTitulo}" />
						</td>
						
						
						<td width="1%">	
							<h:commandLink id="ClinkView" action="#{detalhesMateriaisDeUmTituloPublicoMBean.visualizarMateriaisTitulo}" >
								<h:graphicImage id="ImageView" url="/img/view.gif" style="border:none" title="Visualizar Informa��es dos Materiais Informacionais" />
								<f:param name="idTitulo" value="#{titulo.idTituloCatalografico}"/>
								<f:param name="idsBibliotecasAcervoPublicoFormatados" value="#{pesquisaPublicaBibliotecaMBean.idsBibliotecasAcervoPublicoFormatados}"/>				
								<f:param name="apenasSituacaoVisivelUsuarioFinal" value="true"/>
							</h:commandLink>
						
						</td>
						
						<td width="1%" style="text-align:center">
							<h:graphicImage value="/img/submenu.png" title="Op��es">
								<rich:componentControl event="onmouseover" for="menuOpcoesTitulosEncontrados" operation="show">
							        <f:param value="#{titulo.idTituloCatalografico}" name="_id_titulo_context_menu"/>
							    </rich:componentControl>
							</h:graphicImage>
						</td>
						
					</tr>
					
					<%-- Linha estra para visualizar as inforam��es eletr�nicos do T�tulo --%>
						
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td colspan="7" style="font-size: xx-small; font-style: italic; font-weight: normal; text-align: center;">
							
							<c:if test="${titulo.idObraDigitalizada != null}">
								<a target="_blank" href="${ configSistema['linkSigaa'] }/sigaa/verProducao?idProducao=${titulo.idObraDigitalizada}&key=${ sf:generateArquivoKey(titulo.idObraDigitalizada) }">
									<h:graphicImage url="/img/pdf.png" style="border:none" title="Arquivo Digital" />
									Arquivo Digital
								</a>
							</c:if>
							
							<c:if test="${titulo.possuiEnderecoEletronico}">	
								<a href="#buscarEnderecos" onClick="abreJanelaEnderecosEletronicos(${titulo.id})" style="padding-left: 40px;">
									<img src="${ctx}/img/biblioteca/enderecoEletronico.png" title="Endere�os Eletr�nicos"/>
									 Endere�os Eletr�nicos
								</a>
							</c:if>
							
						</td>	
					</tr>
				
				</c:forEach>

				<tfoot>
					<tr>
						<td colspan="7" style="text-align: center; font-weight: bold;">
						<h:outputText value="#{pesquisaPublicaBibliotecaMBean.descricaoPaginacao}"/> t�tulo(s) encontrado(s)
						</td>
					</tr>
				</tfoot>

			</table>
			
		</t:div>



		<t:div id="divResultadoPesquisaAutoridade" rendered="#{pesquisaPublicaBibliotecaMBean.quantidadeTotalResultados > 0 && pesquisaPublicaBibliotecaMBean.buscaAutoridadesSimples}">
			
			
			<%--  Menu exibido com as op��es de cada material, para poupar espa�o e �cones na tela --%>
	
	
			<rich:contextMenu attached="false" id="menuOpcoesAutoridades" hideDelay="300" >
					
				  <rich:menuItem value="Visualizar Dados MARC da Autoridade" icon="/img/biblioteca/visualizarMarc.png" id="viewDadosMarcAuto"
		           			onclick="abreJanelaInformacoesCompletasAutoridade( {_id_autoridade_context_menu} ); return false;">
		          </rich:menuItem>	
		          
		          <rich:menuItem value="Exportar Autoridade" icon="/img/biblioteca/exportar.png" 
		          	action="#{cooperacaoTecnicaExportacaoMBean.exportarArquivoAutoridadesPublico}" id="ExportAuto">
           				<f:param name="idAutoridadeParaExportacao" value="{_id_autoridade_context_menu}"/>
		          </rich:menuItem>  
		              
		    </rich:contextMenu>
				
	
			<div class="infoAltRem" style="margin-top: 10px">

				<h:graphicImage value="/img/submenu.png" style="overflow: visible;" />: Op��es

			</div> 
	
	
	
			<%-- Tabela para mostrar o cabe�alho j� que os dados est�o sendo mostrados em uma rich datable eu n�o --%>
			<%-- E � mais dif�cil tentar formatar a rich dataTable para ter um estido igual do das tabelas padroes usadas --%>
	
			<table class="listagem">
				<caption>  Autoridades Encontradas ( <h:outputText value="#{pesquisaPublicaBibliotecaMBean.quantidadeResultadosMostrados}"/>  )</caption>
				<thead>
					<tr>
						<th style="width: 45%; padding-left: 20px;"> Entrada Autorizada </th>
						<th style="width: 54%;"> Entradas Remissivas </th>
						
					
						<th style="width: 1%; text-align: center">  </th>
						
						
					</tr>
				</thead>

				

				<c:forEach items="#{pesquisaPublicaBibliotecaMBean.resultadosPaginadosEmMemoria}" var="autoridade" varStatus="status">

					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">

						<c:if test="${autoridade.entradaAutorizadaAutor != null }">

							<td style="vertical-align:top; padding-left: 20px; width:45%; " >
								${autoridade.entradaAutorizadaAutorComIndicacaoCampo}
							</td>
							
							<td style="text-align:center; width: 44%;">
							
								<table width="100%" id="tabelaInterna">
									<c:forEach items="${autoridade.nomesRemissivosAutorFormatados}" var="nomeRemissivo">
									<tr>
										<td>
											${nomeRemissivo}
										</td>
									</tr>
									</c:forEach>
								</table>
							</td>
						
						</c:if>

						<c:if test="${autoridade.entradaAutorizadaAutor == null }">	
							

							<td style="vertical-align:top; padding-left: 20px;  width:45%;">
								${autoridade.entradaAutorizadaAssuntoComIndicacaoCampo}
							</td>
							
							<td style="text-align:center; width:44%;">
							
								<table width="100%" id="tabelaInterna">
									<c:forEach items="${autoridade.nomesRemissivosAssuntoFormatados}" var="nomeRemissivo">
									<tr>
										<td>
											${nomeRemissivo}
										</td>
									</tr>
									</c:forEach>
								</table>
							</td>
							
						</c:if>	
						
						
						<td style="width:1%;">
							<h:graphicImage value="/img/submenu.png">
								<rich:componentControl event="onmouseover" for="menuOpcoesAutoridades" operation="show">
							        <f:param value="#{autoridade.idAutoridade}" name="_id_autoridade_context_menu"/>
							    </rich:componentControl>
							</h:graphicImage>	
						</td>


					</tr>
				
				
				</c:forEach>

				<tfoot>
					<tr>
						<td colspan="11" style="text-align: center; font-weight: bold;">
							<h:outputText value="#{pesquisaPublicaBibliotecaMBean.quantidadeResultadosMostrados}"/>  autoridade(s).
						</td>
					</tr>
				</tfoot>

				
				
			</table>
			
		</t:div>






		<c:if test="${ not empty pesquisaPublicaBibliotecaMBean.artigos }">
			
			<%-- Modal panel com os detalhes de cada material selecionado pelo usu�rio --%>
	
			<a4j:outputPanel ajaxRendered="true" id="painelInfoCompletaArtigo">
				<c:set var="_artigo_selecionado" value="${detalhesArtigoMBean.obj}" scope="request" />
				<c:set var="_assinatura_artigo_selecionado" value="${detalhesArtigoMBean.assinatura}" scope="request" />
				<c:set var="_fasciculo_artigo_selecionado" value="${detalhesArtigoMBean.fasciculo}" scope="request" />
				<%@include file="/public/biblioteca/paginaPadraoDetalhesArtigo.jsp"%>
			</a4j:outputPanel>
			
			<table class="listagem" style="margin:bottom: 20px; width: 100%;">
				<caption>Artigos Encontrados ( ${fn:length( pesquisaPublicaBibliotecaMBean.artigos)} )</caption>
				
				<thead>
					<tr>
						<th style="width: 30%">Autor</th>
						<th style="width: 40%">T�tulo</th>
						<th style="width: 30%;">Palavras-chave</th>
					</tr>
				</thead>
				
				<tbody>
					<c:forEach var="artigo" items="#{ pesquisaPublicaBibliotecaMBean.artigos }" varStatus="status">
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
							
							<td><h:outputText value="#{ artigo.autor }" /></td>
							
							<td><h:outputText value="#{ artigo.titulo }" /></td>
							
							<td>
								<c:forEach items="${artigo.assuntosFormatados}" var="assunto">
									<span style="display: block; white-space: nowrap;">${assunto}</span>
								</c:forEach>
							</td>
							
						</tr>
						
						<%-- Linha estra para visualizar os detalhes dos fasc�culos --%>
						
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
							<td colspan="3" style="text-align: center;">
							 
							 	<a4j:commandLink   value="Mostrar Detalhes "  actionListener="#{detalhesArtigoMBean.carregarDetalhesArtigoSelecionado}"
							 				ajaxSingle="true" oncomplete="modelPanelDetalhesArtigo.show();" style="font-weight: normal; font-style: italic; " 
							 				reRender="formResultadosArtigos">
						 			<f:param name="idArtigoMostrarDetalhes" value="#{artigo.idArtigoDePeriodico}"/>
							 	</a4j:commandLink>
							 	
						    </td>
						</tr>
						
					</c:forEach>
				</tbody>
				
				<tfoot>
					<tr>
						<td style="font-weight: bold; text-align: center;" colspan="11">
							${fn:length( pesquisaPublicaBibliotecaMBean.artigos)} artigo(s)
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if> 

		<%@include file="/public/include/voltar.jsp"%>

	</h:form>

</f:view>

<%@include file="/public/include/rodape.jsp" %>