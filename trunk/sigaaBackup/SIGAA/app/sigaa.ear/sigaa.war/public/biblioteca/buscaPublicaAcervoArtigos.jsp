<%@include file="/public/include/cabecalho.jsp" %>

<h2>
	 Sistema de Bibliotecas
</h2>


<div class="descricaoOperacao"> 
	<h4 style="text-align:left; ">Dicas de busca:</h4>
	<p>Preencha os campos conforme desejado. Usando mais de uma linha, a busca será mais específica.</p>
	<p>O sistema <b>não</b> diferencia caracteres maiúsculos e minúsculos, nem acentos. Por exemplo, o termo 
	<i>bibliográfico</i> recupera registros com as palavras: <i>bibliografico</i>, <i>Bibliografico</i>, <i>Bibliográfico</i>, <i>BIBLIOGRAFICO</i> e <i>BIBLIOGRÁFICO</i>.</p>
</div>


<script type="text/javascript">

	
	function ativaBotaoFalso() {
		$('formBuscaPublica:botaoPesquisar').hide();
		$('formBuscaPublica:fakeBotaoPesquisar').show();
		$('indicatorPesquisa').style.display = '';
	}

	ativaBotaoVerdadeiro();

	function ativaBotaoVerdadeiro() {
		$('formBuscaPublica:botaoPesquisar').show();
		$('formBuscaPublica:fakeBotaoPesquisar').hide();
		$('indicatorPesquisa').style.display = 'none';
	}
	
	
</script>

<script type="text/javascript" src="/sigaa/javascript/biblioteca/functions.js" ></script>

<f:view>

	<a4j:keepAlive beanName="pesquisaPublicaArtigosBibliotecaMBean"></a4j:keepAlive>

	<h:form id="formBuscaPublica">



		<%--     Formulário com os dados da busca     --%>

		<table id="tableDadosPesquisa" class="formulario" width="55%" style="margin-bottom: 20px">

			<caption>Selecione os campos para a busca</caption>
		
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{pesquisaPublicaArtigosBibliotecaMBean.buscarTitulo}" styleClass="noborder" id="checkTitulo"/>
					</td>
					<th style="text-align:left">
						Título:
					</th>
			
					<td colspan="6">
						<h:inputText id="inputTextTituloArtigo" value="#{pesquisaPublicaArtigosBibliotecaMBean.titulo}" size="60" maxlength="300"
						onchange="marcarCheckBox(this, 'formBuscaPublica:checkTitulo');"> </h:inputText>
					</td>
				</tr>
	
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{pesquisaPublicaArtigosBibliotecaMBean.buscarAutor}" styleClass="noborder" id="checkAutor"/>
					</td>
					<th style="text-align:left">
						Autor:
					</th>
			
					<td colspan="6">
						<h:inputText id="inputTextAutorArtigo" value="#{pesquisaPublicaArtigosBibliotecaMBean.autor}" size="60" maxlength="100"
						onchange="marcarCheckBox(this, 'formBuscaPublica:checkAutor');"> </h:inputText>
					</td>
				</tr>
	
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{pesquisaPublicaArtigosBibliotecaMBean.buscarPalavrasChaves}" styleClass="noborder" id="checkAssunto"/>
					</td>
					<th style="text-align:left">
						Palavra-Chave:
					</th>
			
					<td colspan="6">
						<h:inputText id="inputTextPalavrasChaves" value="#{pesquisaPublicaArtigosBibliotecaMBean.palavrasChaves}" size="60" maxlength="100"
						onchange="marcarCheckBox(this, 'formBuscaPublica:checkAssunto');"> </h:inputText>
					</td>
				</tr>		
					
					
				<tr>
					<td></td>
					<th style="text-align:left">Ordenação:</th>
					<td colspan="2">
						<h:selectOneMenu value="#{pesquisaPublicaArtigosBibliotecaMBean.valorCampoOrdenacao}">
							<f:selectItems value="#{pesquisaPublicaArtigosBibliotecaMBean.campoOrdenacaoResultadosComboBox}"/>
						</h:selectOneMenu>
					</td>
				</tr>
	
				<tr>
					<td></td>
					<th style="text-align:left">Registros por página:</th>
					<td colspan="2">
						<h:selectOneMenu value="#{pesquisaPublicaArtigosBibliotecaMBean.quantideResultadosPorPagina}">
							<f:selectItems value="#{pesquisaPublicaArtigosBibliotecaMBean.qtdResultadosPorPaginaComboBox}"/>
						</h:selectOneMenu>
					</td>
				</tr>	
					
				<tfoot>
					<tr>
						<td colspan="8">
							<h:commandButton id="botaoPesquisar" value="Pesquisar" action="#{pesquisaPublicaArtigosBibliotecaMBean.pesquisarAcervoArtigos}" onclick="ativaBotaoFalso();"/>
							<h:commandButton id="fakeBotaoPesquisar" value="Aguarde ..." style="display: none;" disabled="true" />
							<span id="indicatorPesquisa"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
							<h:commandButton id="cmdButtonLimpar" value="Limpar" action="#{pesquisaPublicaArtigosBibliotecaMBean.limparResultadosBuscaAcervoArtigos}" />
						</td>
					</tr>
				</tfoot>

		</table>


		<%--    Mostra os resultados da busca    --%>

        
		<t:div id="divResultadoPesquisaArtigos" rendered="#{pesquisaPublicaArtigosBibliotecaMBean.quantidadeTotalResultados > 0}">
		

			<%-- Modal panel com os detalhes de cada material selecionado pelo usuário --%>

			<a4j:outputPanel ajaxRendered="true" id="painelInfoCompletaArtigo">
				<c:set var="_artigo_selecionado" value="${detalhesArtigoMBean.obj}" scope="request" />
				<c:set var="_assinatura_artigo_selecionado" value="${detalhesArtigoMBean.assinatura}" scope="request" />
				<c:set var="_fasciculo_artigo_selecionado" value="${detalhesArtigoMBean.fasciculo}" scope="request" />
				<%@include file="/public/biblioteca/paginaPadraoDetalhesArtigo.jsp"%>
			</a4j:outputPanel>
	
	
			<%-- Inclue os links de páginação para percorrer os resultados das pesquisas no acervo --%>
			
			<c:set var="pesquisarAcervoPaginadoBiblioteca" value="${pesquisaPublicaArtigosBibliotecaMBean}" scope="request" />
			<%@ include file="/public/biblioteca/pesquisas_acervo/paginaPaginacaoConsultaAcervo.jsp"%>


			<table class="listagem" width="100%"  style="margin-bottom: 20px">
				<caption>  Artigos Encontrados ( <h:outputText value="#{pesquisaPublicaArtigosBibliotecaMBean.descricaoPaginacao}"/> )</caption>

				<thead>
					<tr>
						<th style="width: 30%;"> Autor </th>
						<th style="width: 38%;"> Título </th>
						
						<th style="width: 15%;"> Palavras-chave </th>
						
						<th style="width: 1%; text-align: center">  </th> <%-- Visualizar o artigo de periódico--%>
						
					</tr>
				</thead>
				
				<c:forEach items="#{pesquisaPublicaArtigosBibliotecaMBean.resultadosPaginadosEmMemoria}" var="artigo" varStatus="status">
				
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
					
						<td width="30%">
							${artigo.autor}
						</td>
						
					
						<td width="38%">
							${artigo.titulo}
						</td>
	
						
						<td width="15%">
							<c:forEach items="${artigo.assuntosFormatados}" var="assunto">
								<span style="display: block; white-space: nowrap;">${assunto}</span>
							</c:forEach>
						</td>
						
					</tr>
				
					<%-- Linha estra para visualizar os detalhes dos fascículos --%>
						
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

				<tfoot>
					<tr>
						<td colspan="10" style="text-align: center; font-weight: bold;">
						<h:outputText value="#{pesquisaPublicaArtigosBibliotecaMBean.descricaoPaginacao}"/> artigos(s).
						</td>
					</tr>
				</tfoot>

			</table>

		</t:div>

		<%@include file="/public/include/voltar.jsp"%>

	</h:form>

</f:view>
						

<%@include file="/public/include/rodape.jsp" %>