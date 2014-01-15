<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

	
<%-- Pagina que mostra os detalhes de todos os materiais de um título sendo ele de periódico ou não --%>


<h2>  <ufrn:subSistema /> &gt; Materiais Informacionais de um Título</h2>
<style>
	.dadosMaterial { width: 100% ; background: transparent ;}
	
	.dadosMaterial th {font-weight: bold; text-align: left; background: transparent ; background: none}
	
	.dadosMaterial td {text-align: left; background: transparent ; background: none}
	
	.dadosMaterial tbody { background : transparent; }
	
	table.dadosMaterial tbody {
		background-color:transparent;
	}

	.listagem th {font-weight: bold; text-align: left;}

	table.listagem tr.biblioteca td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
	
	table.listagem tr.ano td{
		background: #828282;
		font-weight: bold;
		color: white;
		padding-left: 40px;
	}
	
	div.menu-botoes li.fasciculo-substituidor a p {
		background-image: url('/sigaa/img/table_refresh.png');
	}
	
	
	/** classe para o botão de paginação quando não está selecionado  **/
	.button_pagination {
	    background: -moz-linear-gradient(center top , #FFFFFF, #EFEFEF) repeat scroll 0 0 #F6F6F6;
	    border: 1px solid #CCCCCC;
	    border-radius: 3px 3px 3px 3px;
	    height: 2.0833em;
	    overflow: visible;
	    padding: 0 0.5em;
	    vertical-align: middle;
	    white-space: nowrap;
	    font-weight:  bolder;
	    font-size: 12px;
	}
	
</style>


<script type="text/javascript">

	function selecionarTudo(chk){
	   for (i=0; i<document.formDetalhesMateriaisTitulo.elements.length; i++)
	      if(document.formDetalhesMateriaisTitulo.elements[i].type == "checkbox")
	         document.formDetalhesMateriaisTitulo.elements[i].checked= chk.checked;
	}

</script>


<script type="text/javascript">
	//funcao para abrir a pagina com o hitórico de emprestimos. 
	var janelaHistorico = null;
	
	function abreJanelaHistoricoEmprestimos(idExemplar){
		if (janelaHistorico == null || janelaHistorico.closed){
			janelaHistorico = window.open('${ctx}/biblioteca/controle_estatistico/formHistoricoEmprestimo.jsf?idExemplar='+idExemplar,'','width=1024,height=400,left=50,top=100,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janelaHistorico.location = '${ctx}/biblioteca/controle_estatistico/formHistoricoEmprestimo.jsf?idExemplar='+idExemplar;
		}
		
		janelaHistorico.focus();
	}
</script>



<f:view>

	<a4j:keepAlive beanName="detalhesMateriaisDeUmTituloMBean"></a4j:keepAlive>
	
	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	
	<%-- A pesquisa de exemplar pode chamar a página para visualizar as informações dos outros exemplares do título.--%>
	<a4j:keepAlive beanName="pesquisarExemplarMBean"></a4j:keepAlive>
	
	<%-- Quando o usuário começa a catalogação e decide visualizar as informações dos materiais de título, se ele voltar as informações da catalogação devem permanecer --%>
	<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
	
	<%-- Mantém as informações dos beans que realizam pesquisa para selecionar materiais do acervo  --%>
	<c:if test="${detalhesMateriaisDeUmTituloMBean.mbeanChamador != null}">	
		<a4j:keepAlive beanName="pesquisaMateriaisInformacionaisMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="transfereExemplaresEntreTitulosMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="notasCirculacaoMBean" />
		<a4j:keepAlive beanName="transfereExemplaresEntreTitulosMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="transfereExemplaresEntreBibliotecasMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="alteraDadosVariosMateriaisMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="modificarNotaCirculacaoVariosMateriaisMBean"></a4j:keepAlive>
		<a4j:keepAlive beanName="inventarioAcervoBibliotecaMBean"></a4j:keepAlive> 
	</c:if>
	
	

	<h:form id="formDetalhesMateriaisTitulo">
	
	
	<p:resources />

	<link rel="stylesheet" type="text/css" href="/sigaa/css/primefaces_skin.css" />
	
	<%-- Modal panel com os detalhes de cada material selecionado pelo usuário --%>
	
	<a4j:outputPanel ajaxRendered="true" id="painelInfoCompletaMaterial">
		<c:set var="_material_selecionado" value="${detalhesMateriaisDeUmTituloMBean.materialSelecionado}" scope="request" />
		<c:set var="_artigos_do_fasciculo_selecionado" value="${detalhesMateriaisDeUmTituloMBean.artigosDoFasciculoSelecionado}" scope="request" />
		<c:set var="_reservas_do_material_selecionado" value="${detalhesMateriaisDeUmTituloMBean.reservasDoMaterial}" scope="request" />
		<c:set var="_qtd_emprestimos_materail_selecionado" value="${detalhesMateriaisDeUmTituloMBean.qtdEmprestimosMaterialSelecionado}" scope="request" />
		<c:set var="_is_fasciculo" value="${detalhesMateriaisDeUmTituloMBean.periodico}" scope="request" />
		<c:set var="_assinatura_do_material" value="${detalhesMateriaisDeUmTituloMBean.asssinaturaDoMaterial}" scope="request" />
		<%@include file="/public/biblioteca/paginaPadraoDetalhesMaterial.jsp"%>
	</a4j:outputPanel>
		
	
	
	
	<%--  Menu exibido com as opções de cada material, para poupar espaço e ícones na tela --%>
	
	<rich:contextMenu attached="false" id="menuOpcoesMaterial" hideDelay="300" >
						
			
			<%-- Operações sobre exemplares --%>	
					     
           <rich:menuItem value="Alterar" icon="/img/alterar.gif" 
           		action="#{editaMaterialInformacionalMBean.iniciarParaEdicaoExemplares}" rendered="#{! detalhesMateriaisDeUmTituloMBean.periodico}">
           		<f:param name="idExemplarParaEdicao" value="{_id_material_context_menu}"/>	
				<f:param name="retornaPaginaDetalhesExemplar" value="true"/>
           </rich:menuItem>
           
           <rich:menuItem value="Adicionar Anexo" icon="/img/biblioteca/anexo.png" iconDisabled="/img/biblioteca/anexo.png" disabled="#{exemplar.anexo}"
           			action="#{materialInformacionalMBean.preparaIncluirAnexoApartirDaPesquisa}" rendered="#{! detalhesMateriaisDeUmTituloMBean.periodico}">
           			<f:param name="idExemplarPrincipal" value="{_id_material_context_menu}"/>
           </rich:menuItem>
           
           <%-- Operações sobre fascículos --%>
           
           <rich:menuItem value="Alterar" icon="/img/alterar.gif" 
           		action="#{editaMaterialInformacionalMBean.iniciarParaEdicaoFasciculos}" rendered="#{detalhesMateriaisDeUmTituloMBean.periodico}">
           		<f:param name="idFasciculoParaEdicao" value="{_id_material_context_menu}"/>	
				<f:param name="retornaPaginaDetalhesFasciculo" value="true"/>
           </rich:menuItem>
													
		    <rich:menuItem value="Catalogar um Artigo" icon="/img/view2.gif" 
           		action="#{catalogacaoArtigosMBean.iniciarCatalogacaoApartirTelaDetalhesMateriais}" rendered="#{detalhesMateriaisDeUmTituloMBean.periodico}">
           		<f:param name="idFasciculoDoArtigo" value="{_id_material_context_menu}"/>	
           </rich:menuItem>
												
			<%-- Operações sobre ambos --%>							
           
           <rich:menuItem value="Historico de Emprestimos" icon="/img/listar.gif" 
           			onclick="abreJanelaHistoricoEmprestimos( {_id_material_context_menu} ); return false;">
           </rich:menuItem>
              
    </rich:contextMenu>
    
    
    
	

	<%--  Navegação entre os títulos retornados na consulta  --%>
	
	<div style="width: 100%; text-align: center; margin-top: 10px; margin-bottom: 20px;">
	
		<h:commandLink style="padding-right: 15px;" 
				value=" << Primeiro Registro" styleClass="button_pagination"
				rendered="#{! detalhesMateriaisDeUmTituloMBean.podeVoltarResultadosPesquisa}" disabled="true"> 
		</h:commandLink> &nbsp;&nbsp;&nbsp;
		
		<h:commandLink action="#{detalhesMateriaisDeUmTituloMBean.irPrimeiraPosicao}"  
				value=" << Primeiro Registro" styleClass="button_pagination"
				rendered="#{detalhesMateriaisDeUmTituloMBean.podeVoltarResultadosPesquisa}" style="padding-right: 15px;" > 
		</h:commandLink> &nbsp;&nbsp;&nbsp;
		
		<h:commandLink action="#{detalhesMateriaisDeUmTituloMBean.irResultadoAnterior}" 
				value=" < Registro Anterior" styleClass="button_pagination"
				rendered="#{detalhesMateriaisDeUmTituloMBean.podeVoltarResultadosPesquisa}"
				style="padding-right: 15px;"> 
		</h:commandLink> &nbsp;&nbsp;&nbsp;
		
		<h:commandLink style="padding-right: 15px;" 
				value=" < Registro Anterior" styleClass="button_pagination"
				rendered="#{! detalhesMateriaisDeUmTituloMBean.podeVoltarResultadosPesquisa}" disabled="true" > 
		</h:commandLink> &nbsp;&nbsp;&nbsp;
		
		
		
		<h:commandLink action="#{detalhesMateriaisDeUmTituloMBean.irProximoResultado}"  
				value="Próximo Registro > " styleClass="button_pagination"
				rendered="#{detalhesMateriaisDeUmTituloMBean.podeAvancarResultadosPesquisa}" style="padding-right: 15px;"> 
		</h:commandLink> &nbsp;&nbsp;&nbsp;
		
		<h:commandLink rendered="#{! detalhesMateriaisDeUmTituloMBean.podeAvancarResultadosPesquisa}" 
			value="Próximo Registro > " styleClass="button_pagination"
			disabled="true" style="padding-right: 15px;"> 
		</h:commandLink> &nbsp;&nbsp;&nbsp;
		
		<h:commandLink action="#{detalhesMateriaisDeUmTituloMBean.irUtimaPosicao}" 
				value="Último Registro >> " styleClass="button_pagination"
				rendered="#{detalhesMateriaisDeUmTituloMBean.podeAvancarResultadosPesquisa}"> 
		</h:commandLink> &nbsp;&nbsp;&nbsp;
		
		<h:commandLink rendered="#{! detalhesMateriaisDeUmTituloMBean.podeAvancarResultadosPesquisa}" 
				value="Último Registro >>" styleClass="button_pagination"
				disabled="true"> 
		</h:commandLink> &nbsp;&nbsp;&nbsp;
		
	</div>
	
	
	<c:set var="_titulo" value="${detalhesMateriaisDeUmTituloMBean.tituloCache}"  scope="request"/>
	<%@include file="/public/biblioteca/informacoes_padrao_titulo.jsp"%>


	<c:if test="${detalhesMateriaisDeUmTituloMBean.periodico}">
		<c:set var="_assinaturas" value="${detalhesMateriaisDeUmTituloMBean.assinaturasTitulo}" scope="request"/>
		<%@include file="/public/biblioteca/informacoes_padrao_assinaturas.jsp"%>
	</c:if>

	

    <%-- Navegação entre os materiais retornados na consulta  --%>
    
	<div style="width: 100%; text-align: center; margin-top: 10px; margin-bottom: 20px;">
			
		<h:commandLink value="<<" actionListener="#{detalhesMateriaisDeUmTituloMBean.atualizaDadosPagina}" 
				styleClass="button_pagination" 
				rendered="#{detalhesMateriaisDeUmTituloMBean.quantidadePaginas > 1}" disabled="#{detalhesMateriaisDeUmTituloMBean.paginaAtual == 1}">
				<f:param name="_numero_pagina_atual" value="1"/>
		</h:commandLink> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			
		<h:commandLink value="<" actionListener="#{detalhesMateriaisDeUmTituloMBean.atualizaDadosPagina}" 
				styleClass="button_pagination" 
				rendered="#{detalhesMateriaisDeUmTituloMBean.quantidadePaginas > 1}" disabled="#{detalhesMateriaisDeUmTituloMBean.paginaAtual == 1}">
				<f:param name="_numero_pagina_atual" value="#{detalhesMateriaisDeUmTituloMBean.paginaAtual -1}"/>
		</h:commandLink>	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	
		<c:forEach var="pagina" items="#{detalhesMateriaisDeUmTituloMBean.paginas}" >
			<h:commandLink value="#{pagina}" actionListener="#{detalhesMateriaisDeUmTituloMBean.atualizaDadosPagina}" 
				styleClass="button_pagination"	disabled="#{detalhesMateriaisDeUmTituloMBean.paginaAtual == pagina}">
				<f:param name="_numero_pagina_atual" value="#{pagina}"/>
			</h:commandLink>  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		</c:forEach>
		
		<h:commandLink value=">" actionListener="#{detalhesMateriaisDeUmTituloMBean.atualizaDadosPagina}"  
				styleClass="button_pagination" 
				rendered="#{detalhesMateriaisDeUmTituloMBean.quantidadePaginas > 1}"  disabled="#{detalhesMateriaisDeUmTituloMBean.paginaAtual == detalhesMateriaisDeUmTituloMBean.quantidadePaginas}">
				<f:param name="_numero_pagina_atual" value="#{detalhesMateriaisDeUmTituloMBean.paginaAtual +1}"/>
		</h:commandLink> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		
		<h:commandLink value=">>" actionListener="#{detalhesMateriaisDeUmTituloMBean.atualizaDadosPagina}" 
				styleClass="button_pagination" 
				rendered="#{detalhesMateriaisDeUmTituloMBean.quantidadePaginas > 1}"  disabled="#{detalhesMateriaisDeUmTituloMBean.paginaAtual == detalhesMateriaisDeUmTituloMBean.quantidadePaginas}">
				<f:param name="_numero_pagina_atual" value="#{detalhesMateriaisDeUmTituloMBean.quantidadePaginas}" />
		</h:commandLink>	
		
	</div>




	
	
	<div> 

		
		
		<%--   EXEMPLARES  --%>
		<c:if test="${! detalhesMateriaisDeUmTituloMBean.periodico}">

			<c:if test="${detalhesMateriaisDeUmTituloMBean.mbeanChamador == null}">	
				<div class="infoAltRem" style="margin-top: 10px">
					<h:graphicImage value="/img/submenu.png" style="overflow: visible;" />: Opções
				</div>
			</c:if>
			
			
  							


			<table class="listagem" id="tabelaExemplares"> 
			
				<caption> 
									
				Exemplar(es) <h:outputText value="#{detalhesMateriaisDeUmTituloMBean.primeiroResultadoPagina}"/>  
				a <h:outputText value="#{detalhesMateriaisDeUmTituloMBean.ultimoResultadoPagina}"/> 
				de <h:outputText value="#{detalhesMateriaisDeUmTituloMBean.quantidadeTotalResultados}"/>
				
				</caption>
				
				<thead>
					<tr>
					<th colspan="3" style="text-align: right; background-color: transparent;"> Escolha a Biblioteca:</th>
					<td colspan="4">
							<h:selectOneMenu id="comboBoxBibliotecasExemplares" value="#{detalhesMateriaisDeUmTituloMBean.idBibliotecaMateriais}"
									valueChangeListener="#{detalhesMateriaisDeUmTituloMBean.verificaAlteracaoFiltroBiblioteca}" onchange="submit();">
								<f:selectItem itemLabel="-- TODAS --" itemValue="-2" />
								<f:selectItems value="#{detalhesMateriaisDeUmTituloMBean.bibliotecasInternas}"/>
							</h:selectOneMenu>
						</td>
					</tr>
				
				</thead>
				
				
				<tbody>
					<tr align="center">
						<th style="width: 15%;"> Cód. Barras</th>
						<th style="width: 15%;"> Tipo de Material </th>
						<th style="width: 15%;"> Coleção </th>
						<th style="width: 15%;"> Status </th>
						<th style="width: 40%;"> Situação </th>
							
						<c:if test="${detalhesMateriaisDeUmTituloMBean.mbeanChamador == null}">	
							<th></th>
						</c:if>
							
						<c:if test="${detalhesMateriaisDeUmTituloMBean.mbeanChamador != null}">	
							<th> <h:selectBooleanCheckbox id="checkBoxGeralExemplares" onclick="selecionarTudo(this);"> </h:selectBooleanCheckbox> </th>
						</c:if>
					</tr>
					
					
				
					<c:set var="idFiltro" value="-1" />
					<c:forEach var="exemplar" items="#{detalhesMateriaisDeUmTituloMBean.exemplaresPaginados}" varStatus="status">
					
						<c:if test="${ idFiltro != exemplar.biblioteca.id}">
							<c:set var="idFiltro" value="${exemplar.biblioteca.id}" />
							<tr class="biblioteca">
								<td colspan="8">${exemplar.biblioteca.descricao}</td>
							</tr>
						</c:if>

						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
									
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">

										<td> ${exemplar.codigoBarras} <c:if test="${exemplar.anexo}"> <span style="font-style: italic;">(anexo)</span> </c:if> </td>
										<td> ${exemplar.tipoMaterial.descricao} </td>
										<td> ${exemplar.colecao.descricao} </td>
										<td> ${exemplar.status.descricao} </td>
										
	
										<c:if test="${exemplar.disponivel}"> 
											<td style="color:green"> ${exemplar.situacao.descricao}
											<c:if test="${not empty exemplar.prazoConcluirReserva}">  [Prazo Reserva:<ufrn:format type="data" valor="${exemplar.prazoConcluirReserva}"/>] </c:if>
											</td>
										</c:if>
										<c:if test="${! exemplar.disponivel && ! exemplar.emprestado}"> 
											<td> ${exemplar.situacao.descricao}</td>
										</c:if>
										<c:if test="${exemplar.emprestado}"> 
											<td style="color:red"> ${exemplar.situacao.descricao}
												[Prazo:<ufrn:format type="data" valor="${exemplar.prazoEmprestimo}"/>]
											</td>
										</c:if>
										
										<%--  
										-- Mostras as opções para cada exemplar mostrado
										--  Caso  o mBeanChamador == null é proque é o caso de de exibição normal onde todas as opções devem ser mostradas.
										--  Caso o mBeanChamador != null é porque esse caso se uso está sendo chamdo de outro caso de uso, então apenas a 
										--  opção de selecionar os exemplares deve está habilitada. O que vai ser feito com os exemplares selecionados 
										-- vai depender do caso de uso que chamou. 
										--%>
										
										<c:if test="${detalhesMateriaisDeUmTituloMBean.mbeanChamador == null}">	
										
											<td>
											 	<h:graphicImage value="/img/submenu.png" title="Opções">
													<rich:componentControl event="onmouseover" for="menuOpcoesMaterial" operation="show">
												        <f:param value="#{exemplar.id}" name="_id_material_context_menu"/>
												    </rich:componentControl>
												</h:graphicImage>	
											</td>
										
										</c:if>
											
										<c:if test="${detalhesMateriaisDeUmTituloMBean.mbeanChamador != null}">	
											<td colspan="3">
												<h:selectBooleanCheckbox id="checkBoxSelecionaExemplar" value="#{exemplar.selecionado}"></h:selectBooleanCheckbox>
											</td>
										</c:if> 
							
									</tr>
					
					
									<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<td colspan="1" style="font-weight: bold; color: #CD853F;"> Localização: </td>
										<td colspan="1" style="color: #CD853F;">  ${exemplar.numeroChamada} </td>
										<td colspan="6" style="font-style: italic; color: #CD853F;"> 
										    <c:if test="${ not empty exemplar.segundaLocalizacao}">  &nbsp&nbsp&nbsp ${exemplar.segundaLocalizacao} </c:if>
										 </td>
									</tr>
								
								
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
										 <td colspan="10" style="text-align: center;">
										 
										 	<a4j:commandLink   value="Mostrar Detalhes "  actionListener="#{detalhesMateriaisDeUmTituloMBean.carregarDetalhesMaterialSelecionado}"
										 				ajaxSingle="true" oncomplete="modelPanelDetalhes.show();" style="font-weight: normal; font-style: italic; " 
										 				reRender="formDetalhesMateriaisTitulo">
									 			<f:param name="idMaterialMostrarDetalhes" value="#{exemplar.id}"/>
										 	</a4j:commandLink>
										 	
									    </td> 
									       
									</tr>
						
					</c:forEach>
					
				</tbody>
	
			</table>
			
		</c:if>
		
		
		
		
		
		
		
		
		
		<%--   FASCÍCULOS  --%>
		<c:if test="${detalhesMateriaisDeUmTituloMBean.periodico}">

			<c:if test="${detalhesMateriaisDeUmTituloMBean.mbeanChamador == null}">	

				<c:if test="${detalhesMateriaisDeUmTituloMBean.mbeanChamador == null}">	
					<div class="infoAltRem" style="margin-top: 10px">
						<h:graphicImage value="/img/submenu.png" style="overflow: visible;" />: Opções
					</div>
				</c:if>
				
			</c:if>


			<table class="listagem" id="tabelaListagemFasciculos"> 
				
				<caption>
					Fascículo(s) <h:outputText value="#{detalhesMateriaisDeUmTituloMBean.primeiroResultadoPagina}"/>  
					a <h:outputText value="#{detalhesMateriaisDeUmTituloMBean.ultimoResultadoPagina}"/> 
					de <h:outputText value="#{detalhesMateriaisDeUmTituloMBean.quantidadeTotalResultados}"/>				
				</caption>
				
				<thead>
				
					<tr>
						<th style="text-align: right"> Escolha a Biblioteca dos Fascículos:</th>
						<td colspan="3">
							<h:selectOneMenu id="comboBoxBibliotecasDosFasciculos" value="#{detalhesMateriaisDeUmTituloMBean.idBibliotecaMateriais}"
									valueChangeListener="#{detalhesMateriaisDeUmTituloMBean.verificaAlteracaoFiltroBiblioteca}" onchange="submit();">
								<f:selectItem itemLabel="-- TODAS --" itemValue="-2" />
								<f:selectItems value="#{detalhesMateriaisDeUmTituloMBean.bibliotecasInternas}"/>
							</h:selectOneMenu>
						</td>
					</tr>
					
					<tr>
						<th style="text-align: right"> Escolha o Ano dos Fascículos:</th>
						<td colspan="3">
							<h:selectOneMenu id="comboBoxAnosCronologicosDosFasciculos" value="#{detalhesMateriaisDeUmTituloMBean.anoPesquisaFasciculos}"
									valueChangeListener="#{detalhesMateriaisDeUmTituloMBean.verificaAlteracaoFiltroAno}" onchange="submit();">
								<f:selectItems value="#{detalhesMateriaisDeUmTituloMBean.anosPesquisaFasciculos}"/>
							</h:selectOneMenu>
						</td>
					</tr>
				
					<tr>
						<th colspan="4" style="text-align: right; padding-right: 15px;"> 
							<c:if test="${detalhesMateriaisDeUmTituloMBean.mbeanChamador != null}">	
							<h:selectBooleanCheckbox id="checkBoxGeralFasciculos" onclick="selecionarTudo(this);"> </h:selectBooleanCheckbox>
							</c:if> 
						</th>
					</tr>
					
				</thead>
				
				
				<tbody>
				
					
	   				
	   				<c:set var="idFiltroBiblioteca" value="-1" scope="request"/>
	   				<c:set var="idFiltroAno" value="-1" scope="request" />
					<c:forEach var="fasciculo" items="#{detalhesMateriaisDeUmTituloMBean.fasciculosPaginados}" varStatus="status">
						
						<c:if test="${ idFiltroBiblioteca != fasciculo.biblioteca.id}">
							<c:set var="idFiltroBiblioteca" value="${fasciculo.biblioteca.id}" scope="request"/>
							<tr class="biblioteca">
								<td colspan="9">${fasciculo.biblioteca.descricao}</td>
							</tr>
						</c:if>
						
						
						<c:if test="${ idFiltroAno != fasciculo.anoCronologico}">
							<c:set var="idFiltroAno" value="${fasciculo.anoCronologico}" scope="request"/>
							<tr class="ano">
								<td colspan="9">Ano: ${fasciculo.anoCronologico}</td>
							</tr>
						</c:if>
						
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							
							<td colspan="7">
								<table width="100%" class="dadosMaterial">
									
									<thead>
										<tr align="center">
											<th style="width: 15%;"> Cód. Barras</th>
											<th style="width: 15%;"> Tipo de Material </th>
											<th style="width: 15%;"> Coleção </th>
											<th style="width: 15%;"> Status </th>
											<th colspan="3" style="width: 40%;"> Situação </th>
											
											<c:if test="${detalhesMateriaisDeUmTituloMBean.mbeanChamador == null}">	
												<th></th>
											</c:if>
											
											<c:if test="${detalhesMateriaisDeUmTituloMBean.mbeanChamador != null}">	
												<th> </th>
											</c:if>
										</tr>
									</thead>
									
									 
									<tr>
										<td> ${fasciculo.codigoBarras} <c:if test="${fasciculo.suplemento}"> <span style="font-style: italic;">(suplemento)</span> </c:if>  </td>
										<td> ${fasciculo.tipoMaterial.descricao} </td>
										<td> ${fasciculo.colecao.descricao} </td>
										<td> ${fasciculo.status.descricao} </td>
										
										<c:if test="${fasciculo.disponivel}"> 
											<td colspan="3" style="color:green"> ${fasciculo.situacao.descricao}
											<c:if test="${not empty fasciculo.prazoConcluirReserva}">  [Prazo Reserva:<ufrn:format type="data" valor="${fasciculo.prazoConcluirReserva}"/>] </c:if>
											</td>
										</c:if>
										
										<c:if test="${! fasciculo.disponivel && ! fasciculo.emprestado}"> 
											<td colspan="3"> ${fasciculo.situacao.descricao}</td>
										</c:if>
										
										<c:if test="${fasciculo.emprestado}">
											<td colspan="3" style="color:red"> 
													${fasciculo.situacao.descricao}
													[Prazo:<ufrn:format type="data" valor="${fasciculo.prazoEmprestimo}"/>]
											</td>
										</c:if>
										
										<td style="text-align: center;">
											
											<c:if test="${detalhesMateriaisDeUmTituloMBean.mbeanChamador == null}">	
												<h:graphicImage value="/img/submenu.png">
													<rich:componentControl event="onmouseover" for="menuOpcoesMaterial" operation="show">
												        <f:param value="#{fasciculo.id}" name="_id_material_context_menu"/>
												    </rich:componentControl>
												</h:graphicImage>
											</c:if>
											
											<c:if test="${detalhesMateriaisDeUmTituloMBean.mbeanChamador != null}">	
												
												<h:selectBooleanCheckbox id="checkBoxSelecionaFasciculo" value="#{fasciculo.selecionado}" rendered="#{fasciculo.selecionado != null}" />
												
											</c:if>
											
										</td>
										
									</tr>
									
									<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
										<td colspan="1" style="font-weight: bold; color: #CD853F;"> Localização: </td>
										<td colspan="1" style="color: #CD853F;">  ${fasciculo.numeroChamada} </td>
										<td colspan="6" style="font-style: italic; color: #CD853F;"> 
										    <c:if test="${ not empty fasciculo.segundaLocalizacao}">  &nbsp&nbsp&nbsp ${fasciculo.segundaLocalizacao} </c:if>
										 </td>
									</tr>
									
									<tr>
										<th>Ano:</th>   <td>${fasciculo.ano}</td>
										<th>Volume:</th> <td>${fasciculo.volume}</td>
										<th>Número:</th> <td>${fasciculo.numero}</td>
										<th>Edição:</th> <td>${fasciculo.edicao}</td>
									</tr>	
									
									<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
										 <td colspan="9" style="text-align: center;">
										 
										 	
										 	<a4j:commandLink  value="Mostrar Detalhes "  actionListener="#{detalhesMateriaisDeUmTituloMBean.carregarDetalhesMaterialSelecionado}"
										 				ajaxSingle="true" oncomplete="modelPanelDetalhes.show();" style="font-weight: normal; font-style: italic; " 
										 				reRender="formDetalhesMateriaisTitulo">
									 			<f:param name="idMaterialMostrarDetalhes" value="#{fasciculo.id}"/>
										 	</a4j:commandLink>
										 	
										 	
									    </td>
									    
									</tr>
									
								</table>
							</td>
						</tr>
						
					</c:forEach>
	
				</tbody>
	
			</table>
		
		</c:if>

	</div>


	<div style="width: 100%; text-align:center;">
		
			<table class="formulario" width="100%">
				<tfoot>
					<tr>
						<td>
						
							
							<c:if test="${detalhesMateriaisDeUmTituloMBean.mbeanChamador != null}">	
								<h:commandButton value="#{detalhesMateriaisDeUmTituloMBean.mbeanChamador.descricaoOperacaoUtilizandoBuscaAcervoMateriais}" 
										action="#{detalhesMateriaisDeUmTituloMBean.realizarAcaoMateriasSelecionado}" />
							</c:if>
							
							
							<h:commandButton value=" << Voltar " action="#{detalhesMateriaisDeUmTituloMBean.voltarTela}" />
							
							
							<h:commandButton value="Cancelar" action="#{detalhesMateriaisDeUmTituloMBean.cancelar}" immediate="true" id="cancelar"  onclick="#{confirm}"/>			
						</td>
					</tr>
					
				</tfoot>
			</table>
		
	</div>

	</h:form>
	

</f:view>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>