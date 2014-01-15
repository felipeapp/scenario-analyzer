<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<link rel="stylesheet" media="all" href="/sigaa/css/biblioteca/estilo_botoes_pequenos.css" type="text/css"/>

<script type="text/javascript">

	//funcao para abrir a pagina dos dados MARC sempre na mesma janela do navegador
	var janela = null;
	
	function abreJanelaInformacoesCompletasAutoridade(idAutoridade){
		if (janela == null || janela.closed){
			janela = window.open('${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/detalhesAutoridade.jsf?idAutoridade='+idAutoridade+'&mostarPaginaDadosMarc=true','','width=1024,height=400,left=50,top=50,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janela.location = '${ctx}/biblioteca/processos_tecnicos/pesquisas_acervo/detalhesAutoridade.jsf?idAutoridade='+idAutoridade+'&mostarPaginaDadosMarc=true';
		}
	
		janela.focus();
	}
	
	
	function ativaBotaoFalsoMultiCampo() {
		$('formBuscaAutoridadesMultiCampo:botaoPesquisarBuscaMultiCampo').hide();
		$('formBuscaAutoridadesMultiCampo:fakeBotaoPesquisarMultiCampo').show();
		$('indicatorPesquisaMultiCampo').style.display = '';
	}

	ativaBotaoVerdadeiroMultiCampo();

	function ativaBotaoVerdadeiroMultiCampo() {
		$('formBuscaAutoridadesMultiCampo:botaoPesquisarBuscaMultiCampo').show();
		$('formBuscaAutoridadesMultiCampo:fakeBotaoPesquisarMultiCampo').hide();
		$('indicatorPesquisaMultiCampo').style.display = 'none';
	}
	
	
	function ativaBotaoFalsoSimples() {
		$('formBuscaAutoridadesSimples:botaoPesquisarBuscaSimples').hide();
		$('formBuscaAutoridadesSimples:fakeBotaoPesquisarBuscaSimples').show();
		$('indicatorPesquisaBuscaSimples').style.display = '';
	}

	ativaBotaoVerdadeiroSimples();

	function ativaBotaoVerdadeiroSimples() {
		$('formBuscaAutoridadesSimples:botaoPesquisarBuscaSimples').show();
		$('formBuscaAutoridadesSimples:fakeBotaoPesquisarBuscaSimples').hide();
		$('indicatorPesquisaBuscaSimples').style.display = 'none';
	}

</script>

<script type="text/javascript" src="/sigaa/javascript/biblioteca/functions.js" ></script>

<style type="text/css">

#tabelaInterna tbody{
	background-color: transparent;
}

</style>


<h2>  <ufrn:subSistema /> &gt; Pesquisa de Autoridades</h2>

<div class="descricaoOperacao"> 
	<h3>Dicas de busca:</h3>
	<p>Preencha os campos conforme desejado. Usando mais de uma linha, a busca será mais específica.</p>
	<p>O sistema <b>não</b> diferencia caracteres maiúsculos e minúsculos. Por exemplo, o termo <i>computador</i> recupera registros com as palavras <i>computador</i>, <i>Computador</i> e <i>COMPUTADOR</i>.</p>
	<p>Use o sinal de "?" para variações de grafia. Por exemplo, <i>su?ana</i> recupera as palavras <i>suzana</i> e <i>susana</i>.</p>
</div>

<f:view>

	<%-- usado quando a página de exportação chama a página de consulta de autoridades --%>
	<%-- mantém os dados do outro bean--%>
	<c:if test="${catalogaAutoridadesMBean.pesquisaExportacao}">
		
		<a4j:keepAlive beanName="cooperacaoTecnicaExportacaoMBean"></a4j:keepAlive>
	
	</c:if>

	<h:form id="formPesquisaAutoridades">
	
		<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
		<a4j:keepAlive beanName="catalogaAutoridadesMBean"></a4j:keepAlive>
	
		<c:if test="${catalogaAutoridadesMBean.pesquisaCatalogacao}"> 
	
			<table  width="100%">
			
				<tr>
					<td>					
						<ul class="listaOpcoes">
						
							<li id="btnAdicionarNovoTitulo">
								<h:commandLink styleClass="noborder" title="Adicionar Novo Título" action="#{catalogacaoMBean.iniciarAutoridades}" >
									Digitar Manualmente
								</h:commandLink>
							</li>
						
							<li id="btnAdicionarNovoTituloPlanilha">
								<h:commandLink styleClass="noborder" title="Adicionar Novo Título Usando Planilha" action="#{catalogacaoMBean.telaEscolhePlanilhaAutoridades}">
									Usar Planilha
								</h:commandLink>
							</li>
							
							<li id="btnImportarTitulo">
								<h:commandLink styleClass="noborder" title=" Importar Título" action="#{cooperacaoTecnicaImportacaoMBean.iniciarImportacaoAutoridades}">
									 Importar Dados de Autoridade
								</h:commandLink>
							</li>
							
						</ul>
					</td>
				</tr>
				
			</table>  
	
		</c:if>
	
	
	
		<%--  Menu exibido com as opções de cada material, para poupar espaço e ícones na tela --%>
	
	
		<rich:contextMenu attached="false" id="menuOpcoesAutoridades" hideDelay="300" >
				
				<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO} %>">		     
	           		
	           		<rich:menuItem value="Alterar Autoridade" icon="/img/alterar.gif" action="#{catalogaAutoridadesMBean.editarAutoridade}" 
	           				rendered="#{!catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAutor && !catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAssunto}">
	           				<f:param name="idAutoridadeParaEdicao" value="{_id_autoridade_context_menu}"/>
	           		</rich:menuItem>
	           		
	           		<rich:menuItem value="Remover Autoridade" icon="/img/delete.gif" action="#{removerEntidadeDoAcervoMBean.telaConfirmaRemocaoVindoPaginaPesquisaAutoridades}" 
	           				rendered="#{!catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAutor && !catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAssunto}">
	           				<f:param name="idAutoridadeRemocao" value="{_id_autoridade_context_menu}"/>
	           		</rich:menuItem>
	           		
	           	</ufrn:checkRole>
	           
	           <rich:menuItem value="Selecionar Autoridade" icon="/img/seta.gif" action="#{catalogaAutoridadesMBean.selecionarAutoridade}" 
	           				rendered="#{catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAutor || catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAssunto}">
	           				<f:param name="idAutoridadeCacheSelecionada" value="{_id_autoridade_context_menu}"/>
	           </rich:menuItem>
	          
	          <rich:menuItem value="Exportar Autoridade" icon="/img/biblioteca/exportar.png" action="#{catalogaAutoridadesMBean.exportarAutoridade}" 
	           				rendered="#{catalogaAutoridadesMBean.pesquisaExportacao}">
	           				<f:param name="idAutoridadeParaExportacao" value="{_id_autoridade_context_menu}"/>
	           </rich:menuItem>
	          
	           <rich:menuItem value="Duplicar Autoridade" icon="/img/biblioteca/duplicar.png" action="#{catalogaAutoridadesMBean.duplicarAutoridade}" 
	           				rendered="#{catalogaAutoridadesMBean.pesquisaCatalogacao}">
	           				<f:param name="idAutoridadeParaDuplicacao" value="{_id_autoridade_context_menu}"/>
	           </rich:menuItem>
	         
	           
	           <rich:menuItem value="Visualizar Dados MARC da Autoridade" icon="/img/biblioteca/visualizarMarc.png" 
	           			onclick="abreJanelaInformacoesCompletasAutoridade( {_id_autoridade_context_menu} ); return false;">
	           </rich:menuItem>
	              
	    </rich:contextMenu>
	
	</h:form>
	
	
	<div id="abas-tipos-de-pesquisa-autoridade" style="width: 90%; margin-left: auto; margin-right: auto;">
		
		<t:div id="buscaSimples" styleClass="aba">
			
			<h:form id="formBuscaAutoridadesSimples"> <%-- Um form para cada aba senão os botões não funcionam direito --%>
			
				<table id="tableDadosPesquisa" class="formulario" width="100%" style="margin-bottom: 10px">
	
					<caption>Selecione os campos para a busca</caption>	
				
					<tr>
						<td width="2%">
							<h:selectBooleanCheckbox value="#{catalogaAutoridadesMBean.buscarNumeroSistema}" styleClass="noborder" id="checkNumeroSistemaBuscaSimples"/>
						</td>
						<th  width="30%" style="text-align:left"> Número do Sistema: </th>
				
						<td  width="70%" colspan="6">
							<h:inputText id="inputTxtNumeroSistemaBuscaSimples" value="#{catalogaAutoridadesMBean.numeroDoSistema}" size="10"  maxlength="9" 
										onchange="marcarCheckBox(this, 'formBuscaAutoridadesSimples:checkNumeroSistemaBuscaSimples');"
										onkeyup="return formatarInteiro(this);" > </h:inputText>
						</td>
					</tr>
				
					<tr>
						<a4j:region rendered="#{! catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAssunto}">
							<td width="2%">
								<input type="radio" name="radioCampoAutoridades" value="buscarAutor" id="radioAutorAutorizado"   ${catalogaAutoridadesMBean.buscarAutor ? 'checked="checked"' : '' }  />
								<%-- <h:selectBooleanCheckbox value="#{catalogaAutoridadesMBean.buscarAutor}" styleClass="noborder" id="checkAutorizadoAutorBuscaSimples"/> --%>
							</td>
							<th style="text-align:left">
								Autor:
							</th>
					
							<td colspan="6">
								<h:inputText id="inputTxtEntraAutorizadaAutorBuscaSimples" value="#{catalogaAutoridadesMBean.autor}" 
									size="80" maxlength="300" 
									onchange="marcarCheckBox(this, 'radioAutorAutorizado');"> </h:inputText>			   
							</td>
						</a4j:region>	
					</tr>
				
					<tr>
						<a4j:region rendered="#{!catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAutor}">
							<td width="2%">
								<input type="radio" name="radioCampoAutoridades" value="buscarAssunto" id="radioAssuntoAutorizado"  ${catalogaAutoridadesMBean.buscarAssunto ? 'checked="checked"' : '' } />
								<%-- <h:selectBooleanCheckbox value="#{catalogaAutoridadesMBean.buscarAssunto}" styleClass="noborder" id="checkAutorizadoAssuntoBuscaSimples"/> --%>
							</td>
							<th style="text-align:left">
								Assunto:
							</th>
					
							<td colspan="6">
								<h:inputText id="inputTxtEntraAutorizadaAssuntoBuscaSimples" value="#{catalogaAutoridadesMBean.assunto}" 
									size="80" maxlength="300" 
									onchange="marcarCheckBox(this, 'radioAssuntoAutorizado');"> </h:inputText>	   
							</td>
							
						 </a4j:region>
					</tr>
					
					<tr>
						<td></td>
						<th style="text-align:left">Ordenação:</th>
						<td colspan="2">
							<h:selectOneMenu value="#{catalogaAutoridadesMBean.valorCampoOrdenacao}">
								<f:selectItems value="#{catalogaAutoridadesMBean.campoOrdenacaoResultadosComboBox}"/>
							</h:selectOneMenu>
						</td>
					</tr>
		
					<tr>
						<td></td>
						<th style="text-align:left">Registros por página:</th>
						<td colspan="2">
							<h:selectOneMenu value="#{catalogaAutoridadesMBean.quantideResultadosPorPagina}">
								<f:selectItems value="#{catalogaAutoridadesMBean.qtdResultadosPorPaginaComboBox}"/>
							</h:selectOneMenu>
						</td>
					</tr>
					
					<tfoot>
						<tr>
							<td colspan="8">
							
								<h:commandButton id="botaoPesquisarBuscaSimples" value="Pesquisar" action="#{catalogaAutoridadesMBean.pesquisarAutoridadeSimples}" onclick="ativaBotaoFalsoSimples();" >
									<f:setPropertyActionListener target="#{catalogaAutoridadesMBean.valorAbaPesquisa}" value="buscaSimples" />
								</h:commandButton>
							
								<h:commandButton id="fakeBotaoPesquisarBuscaSimples" value="Aguarde ..." style="display: none;" disabled="true" />
								<span id="indicatorPesquisaBuscaSimples"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
							
								<c:if test="${catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAutor || catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAssunto}">
									<h:commandButton value="<< Voltar " action="#{catalogaAutoridadesMBean.selecionarAutoridade}"  >
										<f:setPropertyActionListener target="#{catalogaAutoridadesMBean.valorAbaPesquisa}" value="buscaSimples" />
									</h:commandButton>
								</c:if>
								<c:if test="${catalogaAutoridadesMBean.pesquisaExportacao}">
									<h:commandButton value="<< Voltar " action="#{cooperacaoTecnicaExportacaoMBean.telaExportarTituloAutoridade}"  >
										<f:setPropertyActionListener target="#{catalogaAutoridadesMBean.valorAbaPesquisa}" value="buscaSimples" />
									</h:commandButton>
									
								</c:if>
								
								<h:commandButton value="Limpar" action="#{catalogaAutoridadesMBean.apagarDadosPesquisa}" >
									<f:setPropertyActionListener target="#{catalogaAutoridadesMBean.valorAbaPesquisa}" value="buscaSimples" />
								</h:commandButton>
								
								<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{catalogaAutoridadesMBean.cancelar}" />
							</td>
						</tr>
					</tfoot>
				
				
				</table>
			
			</h:form>
		
		</t:div>
		
		<t:div id="buscaMultiCampo" styleClass="aba">
			
			<h:form id="formBuscaAutoridadesMultiCampo"> <%-- Um form para cada aba senão os botões não funcionam direito --%>
			
				<table id="tableDadosPesquisa" class="formulario" width="100%" style="margin-bottom: 10px">
	
					<caption>Selecione os campos para a busca</caption>	
				
					<tr>
						<td width="2%">
							<h:selectBooleanCheckbox value="#{catalogaAutoridadesMBean.buscarNumeroSistema}" styleClass="noborder" id="checkNumeroSistema"/>
						</td>
						<th  width="30%" style="text-align:left"> Número do Sistema: </th>
				
						<td  width="70%" colspan="6">
							<h:inputText value="#{catalogaAutoridadesMBean.numeroDoSistema}" size="10"  maxlength="9" 
										onchange="marcarCheckBox(this, 'formBuscaAutoridadesMultiCampo:checkNumeroSistema');"
										onkeyup="return formatarInteiro(this);" > </h:inputText>
						</td>
					</tr>
				
					<tr>
						<a4j:region rendered="#{! catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAssunto}">
							<td width="2%">
								<h:selectBooleanCheckbox value="#{catalogaAutoridadesMBean.buscarAutorizadoAutor}" styleClass="noborder" id="checkAutorizadoAutor"/>
							</td>
							<th style="text-align:left">
								Entrada Autorizada Autor:
							</th>
					
							<td colspan="6">
								<h:inputText id="inputTxtEntraAutorizadaAutor" value="#{catalogaAutoridadesMBean.entradaAutorizadaAutor}" 
									size="80" maxlength="300" 
									onchange="marcarCheckBox(this, 'formBuscaAutoridadesMultiCampo:checkAutorizadoAutor');"> </h:inputText>			   
							</td>
						</a4j:region>	
					</tr>
				
					<tr>
						<a4j:region rendered="#{!catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAutor}">
							<td width="2%">
								<h:selectBooleanCheckbox value="#{catalogaAutoridadesMBean.buscarAutorizadoAssunto}" styleClass="noborder" id="checkAutorizadoAssunto"/>
							</td>
							<th style="text-align:left">
								Entrada Autorizada Assunto:
							</th>
					
							<td colspan="6">
								<h:inputText id="inputTxtEntraAutorizadaAssunto" value="#{catalogaAutoridadesMBean.entradaAutorizadaAssunto}" 
									size="80" maxlength="300" 
									onchange="marcarCheckBox(this, 'formBuscaAutoridadesMultiCampo:checkAutorizadoAssunto');"> </h:inputText>	   
							</td>
							
						 </a4j:region>
					</tr>
			
			
					<tr>
						<a4j:region rendered="#{!catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAssunto}">
							<td width="2%">
								<h:selectBooleanCheckbox value="#{catalogaAutoridadesMBean.buscarRemissivoAutor}" styleClass="noborder" id="checkRemissivoAutor"/>
							</td>
							<th style="text-align:left">
								Entradas Remissivas Autor:
							</th>
					
							<td  colspan="6">
								<h:inputText value="#{catalogaAutoridadesMBean.nomeRemissivoAutor}" 
									size="80" maxlength="300" 
									onchange="marcarCheckBox(this, 'formBuscaAutoridadesMultiCampo:checkRemissivoAutor');"> </h:inputText>
							</td>
						</a4j:region>
					</tr>
					
					<tr>
						<a4j:region rendered="#{!catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAutor}">
							<td width="2%">
								<h:selectBooleanCheckbox value="#{catalogaAutoridadesMBean.buscarRemissivoAssunto}" styleClass="noborder" id="checkRemissivoAssunto"/>
							</td>
							<th style="text-align:left">
								Entradas Remissivas Assunto:
							</th>
					
							<td  colspan="6">
								<h:inputText value="#{catalogaAutoridadesMBean.nomeRemissivoAssunto}" 
									size="80"  maxlength="300" 
									onchange="marcarCheckBox(this,  'formBuscaAutoridadesMultiCampo:checkRemissivoAssunto');"> </h:inputText>
							</td>
						</a4j:region>
					</tr>
					
					<tr>
						<td></td>
						<th style="text-align:left">Ordenação:</th>
						<td colspan="2">
							<h:selectOneMenu value="#{catalogaAutoridadesMBean.valorCampoOrdenacao}">
								<f:selectItems value="#{catalogaAutoridadesMBean.campoOrdenacaoResultadosComboBox}"/>
							</h:selectOneMenu>
						</td>
					</tr>
		
					<tr>
						<td></td>
						<th style="text-align:left">Registros por página:</th>
						<td colspan="2">
							<h:selectOneMenu value="#{catalogaAutoridadesMBean.quantideResultadosPorPagina}">
								<f:selectItems value="#{catalogaAutoridadesMBean.qtdResultadosPorPaginaComboBox}"/>
							</h:selectOneMenu>
						</td>
					</tr>
					
					<tfoot>
						<tr>
							<td colspan="8">
							
								<h:commandButton id="botaoPesquisarBuscaMultiCampo" value="Pesquisar" action="#{catalogaAutoridadesMBean.pesquisarAutoridadeMultiCampo}" onclick="ativaBotaoFalsoMultiCampo();" >
									<f:setPropertyActionListener target="#{catalogaAutoridadesMBean.valorAbaPesquisa}" value="buscaMultiCampo" />
								</h:commandButton>
							
								<h:commandButton id="fakeBotaoPesquisarMultiCampo" value="Aguarde ..." style="display: none;" disabled="true" />
								<span id="indicatorPesquisaMultiCampo"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
							
								<c:if test="${catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAutor || catalogaAutoridadesMBean.pesquisaSelecaoAutoridadeAssunto}">
									<h:commandButton value="<< Voltar " action="#{catalogaAutoridadesMBean.selecionarAutoridade}"  >
										<f:setPropertyActionListener target="#{catalogaAutoridadesMBean.valorAbaPesquisa}" value="buscaMultiCampo" />
									</h:commandButton>
									
								</c:if>
								<c:if test="${catalogaAutoridadesMBean.pesquisaExportacao}">
									<h:commandButton value="<< Voltar " action="#{cooperacaoTecnicaExportacaoMBean.telaExportarTituloAutoridade}" >
										<f:setPropertyActionListener target="#{catalogaAutoridadesMBean.valorAbaPesquisa}" value="buscaMultiCampo" />
									</h:commandButton>
								</c:if>
								
								<h:commandButton value="Limpar" action="#{catalogaAutoridadesMBean.apagarDadosPesquisa}" >
									<f:setPropertyActionListener target="#{catalogaAutoridadesMBean.valorAbaPesquisa}" value="buscaMultiCampo" />
								</h:commandButton>
								
								<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{catalogaAutoridadesMBean.cancelar}" />
							</td>
						</tr>
					</tfoot>
				
				
				</table>
			
			</h:form>
		
		</t:div>
		
	</div>
	
	
		
		
	<%--       Resultados da pesquisa     --%>	
		
	
	<h:form id="formResultadosPesquisaAutoridades">
		
		
			
		<t:div id="divResultadoPesquisaAutoridade" rendered="#{catalogaAutoridadesMBean.quantidadeTotalResultados > 0}">
	
			<%-- Inclue os links de páginação para percorrer os resultados das pesquisas no acervo --%>
				
			<c:set var="pesquisarAcervoPaginadoBiblioteca" value="${catalogaAutoridadesMBean}" scope="request" />
			<%@ include file="/public/biblioteca/pesquisas_acervo/paginaPaginacaoConsultaAcervo.jsp"%>
				
	
			<div class="infoAltRem" style="margin-top: 10px">

				<h:graphicImage value="/img/submenu.png" style="overflow: visible;" />: Opções

			</div> 
	
	
	
			<%-- Tabela para mostrar o cabeçalho já que os dados estão sendo mostrados em uma rich datable eu não --%>
			<%-- E é mais difícil tentar formatar a rich dataTable para ter um estido igual do das tabelas padroes usadas --%>
	
			<table class="listagem">
				<caption>  Autoridades Encontradas ( <h:outputText value="#{catalogaAutoridadesMBean.quantidadeResultadosMostrados}"/>  )</caption>
				<thead>
					<tr>
						<th style="width: 10%; text-align: right;""> Nº do Sistema </th>
						<th style="width: 45%; padding-left: 20px;"> Entrada Autorizada </th>
						<th style="width: 44%;"> Entradas Remissivas </th>
						
					
						<th style="width: 1%; text-align: center">  </th>
						
						
					</tr>
				</thead>

				

				<c:forEach items="#{catalogaAutoridadesMBean.resultadosPaginadosEmMemoria}" var="autoridade" varStatus="status">

					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">

						<c:if test="${autoridade.entradaAutorizadaAutor != null }">

							<td style="${autoridade.catalogado ? " " : "color:red"}; vertical-align:top; text-align: right; width:10%;">
								${autoridade.numeroDoSistema}
							</td>

							<td style="${autoridade.catalogado ? " " : "color:red"}; vertical-align:top; padding-left: 20px; width:45%; " >
								${autoridade.entradaAutorizadaAutorComIndicacaoCampo}
							</td>
							
							<td style="text-align:center; width: 44%;">
							
								<table width="100%" id="tabelaInterna">
									<c:forEach items="${autoridade.nomesRemissivosAutorFormatados}" var="nomeRemissivo">
									<tr>
										<td style="${autoridade.catalogado ? " " : "color:red"}">
											${nomeRemissivo}
										</td>
									</tr>
									</c:forEach>
								</table>
							</td>
						
						</c:if>

						<c:if test="${autoridade.entradaAutorizadaAutor == null }">	
							<td style="${autoridade.catalogado ? " " : "color:red"}; vertical-align:top; text-align: right; width:10%;">
								${autoridade.numeroDoSistema}
							</td>

							<td style="${autoridade.catalogado ? " " : "color:red"}; vertical-align:top; padding-left: 20px;  width:45%;">
								${autoridade.entradaAutorizadaAssuntoComIndicacaoCampo}
							</td>
							
							<td style="text-align:center; width:44%;">
							
								<table width="100%" id="tabelaInterna">
									<c:forEach items="${autoridade.nomesRemissivosAssuntoFormatados}" var="nomeRemissivo">
									<tr>
										<td style="${autoridade.catalogado ? " " : "color:red"}">
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
							<h:outputText value="#{catalogaAutoridadesMBean.quantidadeResultadosMostrados}"/>  autoridade(s).
						</td>
					</tr>
				</tfoot>

				
				
			</table>	
		
		</t:div>
		
	</h:form>

</f:view>


<script type="text/javascript">
var AbasPesquisa = {
    init : function(){
        var abasPesquisa = new YAHOO.ext.TabPanel('abas-tipos-de-pesquisa-autoridade');

        abasPesquisa.addTab('buscaSimples', "Busca Simples");
        abasPesquisa.addTab('buscaMultiCampo', "Busca Multi-Campo");
        
        <c:if test="${sessionScope.abaPesquisaAutoridades == null || sessionScope.abaPesquisaAutoridades == ''}">
			abasPesquisa.activate('buscaSimples');
    	</c:if>


    	<c:if test="${sessionScope.abaPesquisaAutoridades != null && sessionScope.abaPesquisaAutoridades != ''}">

		    <c:if test="${sessionScope.abaPesquisaAutoridades == 'buscaSimples'}">
				abasPesquisa.activate('buscaSimples');
			</c:if>
			<c:if test="${sessionScope.abaPesquisaAutoridades == 'buscaMultiCampo'}">
				abasPesquisa.activate('buscaMultiCampo');
			</c:if>
			
    	</c:if>
    	
	}
};

YAHOO.ext.EventManager.onDocumentReady(AbasPesquisa.init, AbasPesquisa, true);



</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>