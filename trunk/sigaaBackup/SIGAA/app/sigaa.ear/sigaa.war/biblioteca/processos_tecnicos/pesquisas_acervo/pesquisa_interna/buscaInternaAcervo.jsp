<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<style>	
	table.listagem td.informacoesFormatoReferencia { display: none; padding: 0;}
	
	.campoBuscaSimples{
		box-shadow: 0 2px 2px #8F8F8F inset; color: #313131;
		border-radius: 3px 3px 3px 3px;
		height: 20px;
	}
	
	#tabelaInterna tbody{
		background-color: transparent;
	}
	
</style>


<script type="text/javascript" >

    //funcao para abrir a pagina dos endereços eletrônicos de um título
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
			janela = window.open('${ctx}/public/biblioteca/informacoesMarcTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaDadosMarc=true','','width=1024,height=400,left=50,top=50,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janela.location = '${ctx}/public/biblioteca/informacoesMarcTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaDadosMarc=true';
		}
	
		janela.focus();
	}

	//funcao para abrir a pagina dos dados MARC sempre na mesma janela do navegador
	var janela3 = null;
	
	function abreJanelaInformacoesBibliograficasTitulo(idTitulo){
		if (janela3 == null || janela3.closed){
			janela3 = window.open('${ctx}/public/biblioteca/paginaPublicaVisualizaFormatosBibliograficoTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaReferencias=true&exibirPaginaFichaCatalografica=true','','width=1024,height=500,left=50,top=50,dependent=yes,scrollbars=yes,status=yes,resizable=no');
		}else{
			janela3.location = '${ctx}/public/biblioteca/paginaPublicaVisualizaFormatosBibliograficoTitulo.jsf?idTitulo='+idTitulo+'&exibirPaginaReferencias=true&exibirPaginaFichaCatalografica=true';
		}
	
		janela3.focus();
	}	
	
	//funcao para abrir a pagina dos dados MARC sempre na mesma janela do navegador
	var janela4 = null;
	
	function abreJanelaInformacoesCompletasAutoridade(idAutoridade){
		if (janela4 == null || janela4.closed){
			janela4 = window.open('${ctx}/public/biblioteca/informacoesMarcAutoridade.jsf?idAutoridade='+idAutoridade+'&mostarPaginaDadosMarc=true','','width=1024,height=400,left=50,top=50,dependent=yes,scrollbars=yes,status=yes');
		}else{
			janela4.location = '${ctx}/public/biblioteca/informacoesMarcAutoridade.jsf?idAutoridade='+idAutoridade+'&mostarPaginaDadosMarc=true';
		}
	
		janela4.focus();
	}

	function ativaBotaoFalsoBuscaSimples() {
		$('formBuscaInternaSimples:cmdPesquisaInternaSimples').hide();
		$('formBuscaInternaSimples:fakeCmdPesquisaInternaSimples').show();
		$('indicatorPesquisaInternaSimples').style.display = '';
	}

	function ativaBotaoFalsoBuscaMultiCampo() {
		$('formBuscaInternaMultiCampo:cmdPesquisaInternaMultiCampo').hide();
		$('formBuscaInternaMultiCampo:fakeCmdPesquisaInternaMultiCampo').show();
		$('indicatorPesquisaInternaMultiCampo').style.display = '';
	}
		
		
	function ativaBotaoFalsoBuscaAvancada() {
		$('formBuscaInternaAvancada:cmdPesquisaInternaAvancada').hide();
		$('formBuscaInternaAvancada:fakeCmdPesquisaInternaAvancada').show();
		$('indicatorPesquisaInternaAvancada').style.display = '';
	}
	
	
	function ativaBotaoFalsoBuscaSimplesAutoridades() {
		$('formBuscaInternaSimplesAutoridades:cmdPesquisaInternaSimplesAutoridades').hide();
		$('formBuscaInternaSimplesAutoridades:fakeCmdPesquisaInternaSimplesAutoridade').show();
		$('indicatorPesquisaInternaSimplesAutoridades').style.display = '';
	}
	
	
	ativaBotaoVerdadeiroBuscaSimples();
	ativaBotaoVerdadeiroBuscaMultiCampo();
	ativaBotaoVerdadeiroBuscaAvancada();
	ativaBotaoVerdadeiroBuscaSimplesAutoridade();
	
	
	function ativaBotaoVerdadeiroBuscaSimples() {
		$('formBuscaInternaSimples:cmdPesquisaInternaSimples').show();
		$('formBuscaInternaSimples:fakeCmdPesquisaInternaSimples').hide();
		$('indicatorPesquisaInternaSimples').style.display = 'none';
	}
	
	function ativaBotaoVerdadeiroBuscaMultiCampo() {
		$('formBuscaInternaMultiCampo:cmdPesquisaInternaMultiCampo').show();
		$('formBuscaInternaMultiCampo:fakeCmdPesquisaInternaMultiCampo').hide();
		$('indicatorPesquisaInternaMultiCampo').style.display = 'none';
	}
	
	function ativaBotaoVerdadeiroBuscaAvancada() {
		$('formBuscaInternaAvancada:cmdPesquisaInternaAvancada').show();
		$('formBuscaInternaAvancada:fakeCmdPesquisaInternaAvancada').hide();
		$('indicatorPesquisaInternaAvancada').style.display = 'none';
	}
	
	function ativaBotaoVerdadeiroBuscaSimplesAutoridade() {
		$('formBuscaInternaSimplesAutoridades:cmdPesquisaInternaSimplesAutoridades').show();
		$('formBuscaInternaSimplesAutoridades:fakeCmdPesquisaInternaSimplesAutoridade').hide();
		$('indicatorPesquisaInternaSimplesAutoridades').style.display = 'none';
	}


</script>


<script type="text/javascript" src="/sigaa/javascript/biblioteca/functions.js" ></script>


<h2> <ufrn:subSistema /> &gt; Pesquisa no Acervo </h2> 


<div class="descricaoOperacao"> 
	<h4 style="text-align:left; ">Dicas de busca:</h4>
	<p>Preencha os campos conforme desejado. Usando mais de uma linha, a busca será mais específica.</p>
	<p>O sistema <b>não</b> diferencia caracteres maiúsculos e minúsculos, nem acentos. Por exemplo, o termo 
	<i>bibliográfico</i> recupera registros com as palavras: <i>bibliografico</i>, <i>Bibliografico</i>, <i>Bibliográfico</i>, <i>BIBLIOGRAFICO</i> e <i>BIBLIOGRÁFICO</i>.</p>
</div>

<f:view>

	<a4j:keepAlive beanName="pesquisaInternaBibliotecaMBean" />
	
	
	
	<%-- Se algum bean chamar a busca interna da biblitoeca e desejar manter suas informações salvas, declare o aqui --%>
	
	<a4j:keepAlive beanName="planoCurso" />
	<a4j:keepAlive beanName="solicitarReservaMaterialBibliotecaMBean" />
	<a4j:keepAlive beanName="visualizarReservasMaterialBibliotecaMBean" />
	
	<%--  Fim da lista de MBeans cujos valores devem ser mantidos  --%>
	


	
	<div id="abas-tipos-de-pesquisa-interna" style="width: 90%; margin-left: auto; margin-right: auto;">
	
	
	
	
		<t:div id="buscaSimples" styleClass="aba">
		
			<h:form id="formBuscaInternaSimples"> <%-- Um form para cada aba senão os botões não funcionam direito --%>
		
				<table id="tableDadosPesquisaSimples" class="formulario" style="width: 100%">
				
					<caption>Selecione os campos para a busca</caption>
				
					<tr style="height: 20px;">
						<td colspan="8"></td>
					</tr>
				
					<tr>
						<td style="width: 2%;">
							<h:selectBooleanCheckbox value="#{pesquisaInternaBibliotecaMBean.campoBuscaSimples.buscarCampo}" styleClass="noborder" id="checkDadosPesquisa"/>
						</td>
						<th style="text-align:left; width: 18%;">
						
						</th>
				
						<td style="width: 80%;" colspan="6">
							<h:inputText value="#{pesquisaInternaBibliotecaMBean.campoBuscaSimples.valorCampo}" size="90" maxlength="100" styleClass="campoBuscaSimples"
							onchange="marcarCheckBox(this, 'formBuscaInternaSimples:checkDadosPesquisa');"> </h:inputText>
							
						</td>
					</tr>
				
					<tr style="height: 20px;">
						<td colspan="8"></td>
					</tr>
				
					<tr>
						<td></td>
						<th style="text-align:left;">Ordenação:</th>
						<td colspan="6">
							<h:selectOneMenu value="#{pesquisaInternaBibliotecaMBean.valorCampoOrdenacao}">
								<f:selectItems value="#{pesquisaInternaBibliotecaMBean.campoOrdenacaoResultadosComboBox}"/>
							</h:selectOneMenu>
						</td>
					</tr>
				
					<tr>
						<td></td>
						<th style="text-align:left">Registros por página:</th>
						<td colspan="6">
							<h:selectOneMenu value="#{pesquisaInternaBibliotecaMBean.quantideResultadosPorPagina}">
								<f:selectItems value="#{pesquisaInternaBibliotecaMBean.qtdResultadosPorPaginaComboBox}"/>
							</h:selectOneMenu>
						</td>
					</tr>
				
				
					<tr>
						<td colspan="8" style=" padding: 0px; ">
							<table id="tableDadosPesquisaInterna" class="subFormulario" style="width: 100%;">
								<tr>
									<td width="2%">
										<h:selectBooleanCheckbox value="#{pesquisaInternaBibliotecaMBean.buscarBiblioteca}" styleClass="noborder" id="checkBiblioteca"/>
									</td>
									
									<th colspan="1"  style="text-align:left; width:18%;">Biblioteca:</th>
									<td colspan="4" style=" padding: 0px; ">
										<h:selectOneMenu value="#{pesquisaInternaBibliotecaMBean.idBiblioteca}" 
												onchange="marcarCheckBox(this, 'formBuscaInternaSimples:checkBiblioteca');">
											<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
											<f:selectItems value="#{pesquisaInternaBibliotecaMBean.bibliotecasInternasAtivas}"/>
										</h:selectOneMenu>
									</td>
								</tr>
								
								<tr>
									<td width="2%">
										<h:selectBooleanCheckbox value="#{pesquisaInternaBibliotecaMBean.buscarColecao}" styleClass="noborder" id="checkColecao"/>
									</td>
									<th colspan="1"  style="text-align:left;">Coleção:</th>
									<td colspan="4" style=" padding: 0px; ">
										<h:selectOneMenu value="#{pesquisaInternaBibliotecaMBean.idColecao}" 
												onchange="marcarCheckBox(this, 'formBuscaInternaSimples:checkColecao');">
											<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
											<f:selectItems value="#{pesquisaInternaBibliotecaMBean.colecoesAtivas}"/>
										</h:selectOneMenu>
									</td>
								</tr> 
					
								<tr>
									<td width="2%">
										<h:selectBooleanCheckbox value="#{pesquisaInternaBibliotecaMBean.buscarTipoMaterial}" styleClass="noborder" id="checkTipoMaterial"/>
									</td>
									<th colspan="1"  style="text-align:left">Tipo de Material:</th>
									<td colspan="4" style=" padding: 0px; ">
										<h:selectOneMenu value="#{pesquisaInternaBibliotecaMBean.idTipoMaterial}" 
												onchange="marcarCheckBox(this, 'formBuscaInternaSimples:checkTipoMaterial');">
											<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
											<f:selectItems value="#{pesquisaInternaBibliotecaMBean.tiposMateriaisAtivos}"/>
										</h:selectOneMenu>
									</td>
								</tr>
							</table>
						</td>
							
					<tr>
						
					<tfoot>
						<tr>
							<td colspan="8">
								<h:commandButton id="cmdPesquisaInternaSimples" value="Pesquisar" action="#{pesquisaInternaBibliotecaMBean.pesquisaSimplesAcervo}" style="margin-right: 5px" onclick="ativaBotaoFalsoBuscaSimples();">
									<f:setPropertyActionListener target="#{pesquisaInternaBibliotecaMBean.valorAbaPesquisa}" value="buscaSimples" />
								</h:commandButton>
								
								<h:commandButton id="fakeCmdPesquisaInternaSimples" value="Aguarde ..." style="display: none;" disabled="true" />
								<span id="indicatorPesquisaInternaSimples"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
								
								<h:commandButton id="cmdLimpaPesquisaInternaSimples" value="Limpar" action="#{pesquisaInternaBibliotecaMBean.limparResultadosBuscaAcervo}" >
									<f:setPropertyActionListener target="#{pesquisaInternaBibliotecaMBean.valorAbaPesquisa}" value="buscaSimples" />
								</h:commandButton>
								
								<h:commandButton id="cmdFormataABNTPesquisaInternaSimples" value="Gerar Formato da ABNT" actionListener="#{pesquisaInternaBibliotecaMBean.gerarResultadoPesquisaArquivoReferenciaFormatoABNT}"
									rendered="#{pesquisaInternaBibliotecaMBean.quantidadeTotalResultados > 0 && pesquisaInternaBibliotecaMBean.valorAbaPesquisa != 'buscaSimplesAutoridades'}"
									title="Gerar o resultado da pesquisa em um arquivo no formato de referência da ABNT" >
									<f:setPropertyActionListener target="#{pesquisaInternaBibliotecaMBean.valorAbaPesquisa}" value="buscaSimples" />
								</h:commandButton>
								
								<c:if test="${ pesquisaInternaBibliotecaMBean.buscaSelecionarTitulo}">
									<h:commandButton id="cmdVoltaPesquisaInternaSimples" value="<< Voltar" action="#{pesquisaInternaBibliotecaMBean.voltarBusca}" 
												rendered="#{pesquisaInternaBibliotecaMBean.mostrarBotaoVoltarBusca}" 	style="margin-right: 5px">
										<f:setPropertyActionListener target="#{pesquisaInternaBibliotecaMBean.valorAbaPesquisa}" value="buscaSimples" />
									</h:commandButton>
								</c:if>
								
								<%-- Como nesse caso não tem nenhum vinculação com quem chamou o caso de uso, utiliza-se apenas o voltar no navegador 
								<c:if test="${pesquisaInternaBibliotecaMBean.buscaSimples}">
									<input type="button" onclick="javascript:history.back();" value="<< Voltar" />
								</c:if> --%>
								
								<h:commandButton id="cmdCancelaPesquisaInternaSimples" value="Cancelar" onclick="#{confirm}" immediate="true" action="#{pesquisaInternaBibliotecaMBean.cancelar}"/>
							
							</td>
						</tr>
				
					</tfoot>
				
				</table>
		
			</h:form>
		
		</t:div>
		
		
		
		
		
		
		
		
		
		<t:div id="buscaMultiCampo" styleClass="aba">
		
			<h:form id="formBuscaInternaMultiCampo"> <%-- Um form para cada aba senão os botões não funcionam direito --%>

			<table id="tableDadosPesquisa" class="formulario" style="width: 100%">
	
				<caption>Selecione os campos para a busca</caption>
			
					<tr>
						<td width="2%">
							<h:selectBooleanCheckbox value="#{pesquisaInternaBibliotecaMBean.buscarTitulo}" styleClass="noborder" id="checkTitulo"/>
						</td>
						<th  width="28%" style="text-align:left">
							Título:
						</th>
				
						<td  width="70%" colspan="6">
							<h:inputText value="#{pesquisaInternaBibliotecaMBean.titulo}" size="60" maxlength="100"
							onchange="marcarCheckBox(this, 'formBuscaInternaMultiCampo:checkTitulo');"> </h:inputText>
						</td>
					</tr>
		
					<tr>
						<td width="2%">
							<h:selectBooleanCheckbox value="#{pesquisaInternaBibliotecaMBean.buscarAutor}" styleClass="noborder" id="checkAutor"/>
						</td>
						<th  width="28%" style="text-align:left">
							Autor:
						</th>
				
						<td  width="70%" colspan="6">
							<h:inputText value="#{pesquisaInternaBibliotecaMBean.autor}" size="60" maxlength="100"
							onchange="marcarCheckBox(this, 'formBuscaInternaMultiCampo:checkAutor');"> </h:inputText>
						</td>
					</tr>
		
					<tr>
						<td width="2%">
							<h:selectBooleanCheckbox value="#{pesquisaInternaBibliotecaMBean.buscarAssunto}" styleClass="noborder" id="checkAssunto"/>
						</td>
						<th  width="28%" style="text-align:left">
							Assunto:
						</th>
				
						<td  width="70%" colspan="6">
							<h:inputText value="#{pesquisaInternaBibliotecaMBean.assunto}" size="60" maxlength="100"
							onchange="marcarCheckBox(this, 'formBuscaInternaMultiCampo:checkAssunto');"> </h:inputText>
						</td>
					</tr>			
		
					<tr>
						<td width="2%">
							<h:selectBooleanCheckbox value="#{pesquisaInternaBibliotecaMBean.buscarLocalPublicacao}" styleClass="noborder" id="checkLocalPublicao"/>
						</td>
						<th  width="28%" style="text-align:left">
							Local de Publicação:
						</th>
				
						<td  width="70%" colspan="6">
							<h:inputText value="#{pesquisaInternaBibliotecaMBean.localPublicacao}" size="60" maxlength="100"
							onchange="marcarCheckBox(this, 'formBuscaInternaMultiCampo:checkLocalPublicao');"> </h:inputText>
						</td>
					</tr>
		
					<tr>
						<td width="2%">
							<h:selectBooleanCheckbox value="#{pesquisaInternaBibliotecaMBean.buscarEditora}" styleClass="noborder" id="checkEditora"/>
						</td>
						<th  width="28%" style="text-align:left">
							Editora:
						</th>
				
						<td  width="70%" colspan="6">
							<h:inputText value="#{pesquisaInternaBibliotecaMBean.editora}" size="60" maxlength="100"
							onchange="marcarCheckBox(this, 'formBuscaInternaMultiCampo:checkEditora');"> </h:inputText>
						</td>
					</tr>
		
					<tr>
						<td width="2%">
							<h:selectBooleanCheckbox value="#{pesquisaInternaBibliotecaMBean.buscarAno}" styleClass="noborder" id="checkAnoPublicacao"/>
						</td>
						<th colspan="1" style="text-align:left">
							Ano de Publicação de:
						</th>
				
						<td colspan="1" width="10%">
							<h:inputText id="AnoPublicacaoInicio" value="#{pesquisaInternaBibliotecaMBean.anoInicial}" size="7"  maxlength="4" 
								onkeyup="marcarCheckBox(this, 'formBuscaInternaMultiCampo:checkAnoPublicacao'); return formatarInteiro(this)"> </h:inputText>
						</td>
		
						<th colspan="1" width="10%">
							até:
						</th>
		  	  
						<td colspan="4">
							<h:inputText id="AnoPublicacaoFinal" value="#{pesquisaInternaBibliotecaMBean.anoFinal}" size="7" maxlength="4" 
							onkeyup="marcarCheckBox(this, 'formBuscaInternaMultiCampo:checkAnoPublicacao'); return formatarInteiro(this)"> </h:inputText>
						</td>
					</tr>
			
					<tr>
						<td></td>
						<th style="text-align:left">Ordenação:</th>
						<td colspan="6">
							<h:selectOneMenu value="#{pesquisaInternaBibliotecaMBean.valorCampoOrdenacao}">
								<f:selectItems value="#{pesquisaInternaBibliotecaMBean.campoOrdenacaoResultadosComboBox}"/>
							</h:selectOneMenu>
						</td>
					</tr>
				
					<tr>
						<td></td>
						<th style="text-align:left">Registros por página:</th>
						<td colspan="6">
							<h:selectOneMenu value="#{pesquisaInternaBibliotecaMBean.quantideResultadosPorPagina}">
								<f:selectItems value="#{pesquisaInternaBibliotecaMBean.qtdResultadosPorPaginaComboBox}"/>
							</h:selectOneMenu>
						</td>
					</tr>
			
					<tr>
						<td colspan="8" style=" padding: 0px; ">
							<table id="tableDadosPesquisaInterna" class="subFormulario">
								<tr>
									<td width="2%">
										<h:selectBooleanCheckbox value="#{pesquisaInternaBibliotecaMBean.buscarBiblioteca}" styleClass="noborder" id="checkBiblioteca"/>
									</td>
									
									<th colspan="1"  style="text-align:left; width:28%;">Biblioteca:</th>
									<td colspan="4" style=" padding: 0px; ">
										<h:selectOneMenu value="#{pesquisaInternaBibliotecaMBean.idBiblioteca}" 
												onchange="marcarCheckBox(this, 'formBuscaInternaMultiCampo:checkBiblioteca');">
											<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
											<f:selectItems value="#{pesquisaInternaBibliotecaMBean.bibliotecasInternasAtivas}"/>
										</h:selectOneMenu>
									</td>
								</tr>
								
								<tr>
									<td width="2%">
										<h:selectBooleanCheckbox value="#{pesquisaInternaBibliotecaMBean.buscarColecao}" styleClass="noborder" id="checkColecao"/>
									</td>
									<th colspan="1"  style="text-align:left; width:28%;">Coleção:</th>
									<td colspan="4" style=" padding: 0px; ">
										<h:selectOneMenu value="#{pesquisaInternaBibliotecaMBean.idColecao}" 
												onchange="marcarCheckBox(this, 'formBuscaInternaMultiCampo:checkColecao');">
											<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
											<f:selectItems value="#{pesquisaInternaBibliotecaMBean.colecoesAtivas}"/>
										</h:selectOneMenu>
									</td>
								</tr> 
					
								<tr>
									<td width="2%">
										<h:selectBooleanCheckbox value="#{pesquisaInternaBibliotecaMBean.buscarTipoMaterial}" styleClass="noborder" id="checkTipoMaterial"/>
									</td>
									<th colspan="1"  style="text-align:left">Tipo de Material:</th>
									<td colspan="4" style=" padding: 0px; ">
										<h:selectOneMenu value="#{pesquisaInternaBibliotecaMBean.idTipoMaterial}" 
												onchange="marcarCheckBox(this, 'formBuscaInternaMultiCampo:checkTipoMaterial');">
											<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
											<f:selectItems value="#{pesquisaInternaBibliotecaMBean.tiposMateriaisAtivos}"/>
										</h:selectOneMenu>
									</td>
								</tr>
							</table>
						</td>
							
					<tr>
					
					<tfoot>
					<tr>
						<td colspan="8">
							<h:commandButton id="cmdPesquisaInternaMultiCampo" value="Pesquisar" action="#{pesquisaInternaBibliotecaMBean.pesquisaMultiCampoAcervo}" style="margin-right: 5px" onclick="ativaBotaoFalsoBuscaMultiCampo();">
								<f:setPropertyActionListener target="#{pesquisaInternaBibliotecaMBean.valorAbaPesquisa}" value="buscaMultiCampo" />
							</h:commandButton>
							
							<h:commandButton id="fakeCmdPesquisaInternaMultiCampo" value="Aguarde ..." style="display: none;" disabled="true" />
							<span id="indicatorPesquisaInternaMultiCampo"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
							
							<h:commandButton id="cmdLimpaPesquisaInternaMultiCampo" value="Limpar" action="#{pesquisaInternaBibliotecaMBean.limparResultadosBuscaAcervo}" >
								<f:setPropertyActionListener target="#{pesquisaInternaBibliotecaMBean.valorAbaPesquisa}" value="buscaMultiCampo" />
							</h:commandButton>
							
							<h:commandButton id="cmdFormataABNTPesquisaInternaMultiCampo" value="Gerar Formato da ABNT" actionListener="#{pesquisaInternaBibliotecaMBean.gerarResultadoPesquisaArquivoReferenciaFormatoABNT}"
								rendered="#{pesquisaInternaBibliotecaMBean.quantidadeTotalResultados > 0 &&  pesquisaInternaBibliotecaMBean.valorAbaPesquisa != 'buscaSimplesAutoridades'}"
								title="Gerar o resultado da pesquisa em um arquivo no formato de referência da ABNT" >
								<f:setPropertyActionListener target="#{pesquisaInternaBibliotecaMBean.valorAbaPesquisa}" value="buscaMultiCampo" />
							</h:commandButton>
							
							<c:if test="${pesquisaInternaBibliotecaMBean.buscaSelecionarTitulo}">
								<h:commandButton id="cmdVoltaPesquisaInternaMultiCampo" value="<< Voltar" action="#{pesquisaInternaBibliotecaMBean.voltarBusca}" 
											rendered="#{pesquisaInternaBibliotecaMBean.mostrarBotaoVoltarBusca}" 	style="margin-right: 5px">
									<f:setPropertyActionListener target="#{pesquisaInternaBibliotecaMBean.valorAbaPesquisa}" value="buscaMultiCampo" />
								</h:commandButton>
							</c:if>
							
							<%-- Como nesse caso não tem nenhum vinculação com quem chamou o caso de uso, utiliza-se apenas o voltar no navegador 
							<c:if test="${pesquisaInternaBibliotecaMBean.buscaSimples}">
								<input type="button" onclick="javascript:history.back();" value="<< Voltar" />
							</c:if> --%>
							
							<h:commandButton id="cmdCancelaPesquisaInternaMultiCampo" value="Cancelar" onclick="#{confirm}" immediate="true" action="#{pesquisaInternaBibliotecaMBean.cancelar}"/>
						
						</td>
					</tr>
				
				</tfoot>
				
			</table>
			
			</h:form>	
			
		</t:div>  <%-- Fecha a div buscas multi campo --%>
		
		
	
		
		
	
		
		<t:div id="buscaAvancada" styleClass="aba">
	
				<h:form id="formBuscaInternaAvancada">  <%-- Um form para cada aba senão os botões não funcionam direito --%>
					
				<table class="formulario" id="tableDadosPesquisaAvancada" style="width: 100%">
				
					<caption>Selecione os campos para a busca</caption>
				
					<c:forEach items="#{pesquisaInternaBibliotecaMBean.campos}" var="campoPesquisa" varStatus="status">
					
						<c:if test="${campoPesquisa.posicaoCampo != 0 }" >  <%-- Para o primeiro campo é sempre 'E'--%>
							<tr>
								<td colspan="8" style="text-align: center; height: 60px; ">	
									<h:selectOneRadio value="#{campoPesquisa.conexao}"  style="text-align: center; width: 30%; margin-left: 35%; margin-right: 35%; ">
										<f:selectItem itemLabel="E" itemValue="E" />
										<f:selectItem itemLabel="OU" itemValue="O"/>
										<f:selectItem itemLabel="NÃO" itemValue="N"/>
									</h:selectOneRadio>
								</td>
							</tr>
						</c:if>
					
						<tr>
							<td width="2%">
								<h:selectBooleanCheckbox value="#{campoPesquisa.buscarCampo}" styleClass="noborder" id="checkCampo_${status.index}"/>
							</td>
							
							<td  width="18%">
								<h:selectOneMenu id="comboBoxTipoCampo" value="#{campoPesquisa.valorTipoCampo}"
											valueChangeListener="#{pesquisaInternaBibliotecaMBean.verificaExibicaoTipoCampo}">	
										
										<a4j:support event="onchange" reRender="buscaAvancada" />
										
										<f:selectItems value="#{campoPesquisa.camposPesquisaAvancadaComboBox}" />
										
										<f:attribute name="campoSelecionado" value="#{campoPesquisa.posicaoCampo}" />
										
								 </h:selectOneMenu>
							</td>
							
							<td style="width: 40%; text-align: left;" colspan="6">
							
								<h:inputText id="inputTextCampo" style="width:90%" value="#{campoPesquisa.valorCampo}" size="80" maxlength="100"
									rendered="#{! campoPesquisa.renderizaComboxBibliotecaCampo
										&& ! campoPesquisa.renderizaComboxColecaoCampo
										&& ! campoPesquisa.renderizaComboxTipoMaterialCampo}"
									onchange="marcarCheckBox(this, 'formBuscaInternaAvancada:checkCampo_#{campoPesquisa.posicaoCampo}');"/>
								
								<h:selectOneMenu id="comboBoxBibliotecaCampo" value="#{campoPesquisa.valorCampo}"
										rendered="#{campoPesquisa.renderizaComboxBibliotecaCampo}" 
										onchange="marcarCheckBox(this, 'formBuscaInternaAvancada:checkCampo_#{campoPesquisa.posicaoCampo}');">
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
									<f:selectItems value="#{pesquisaInternaBibliotecaMBean.bibliotecasInternasAtivas}"/>
								</h:selectOneMenu>
								
								
								<h:selectOneMenu id="comboBoxColecaoCampo" value="#{campoPesquisa.valorCampo}" 
									rendered="#{campoPesquisa.renderizaComboxColecaoCampo}" 
									onchange="marcarCheckBox(this, 'formBuscaInternaAvancada:checkCampo_#{campoPesquisa.posicaoCampo}');">
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
									<f:selectItems value="#{pesquisaInternaBibliotecaMBean.colecoesAtivas}"/>
								</h:selectOneMenu>
								
								<h:selectOneMenu id="comboBoxTipoMaterialCampo" value="#{campoPesquisa.valorCampo}" 
									rendered="#{campoPesquisa.renderizaComboxTipoMaterialCampo}" 
									onchange="marcarCheckBox(this, 'formBuscaInternaAvancada:checkCampo_#{campoPesquisa.posicaoCampo}');">
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
									<f:selectItems value="#{pesquisaInternaBibliotecaMBean.tiposMateriaisAtivos}"/>
								</h:selectOneMenu>
								
							</td>
								
						</tr>
						
					
				</c:forEach>
			
				<tr>
					<td></td>
					<th style="text-align:left">Ordenação:</th>
					<td colspan="6">
						<h:selectOneMenu value="#{pesquisaInternaBibliotecaMBean.valorCampoOrdenacao}">
							<f:selectItems value="#{pesquisaInternaBibliotecaMBean.campoOrdenacaoResultadosComboBox}"/>
						</h:selectOneMenu>
					</td>
				</tr>
			
				<tr>
					<td></td>
					<th style="text-align:left">Registros por página:</th>
					<td colspan="6">
						<h:selectOneMenu value="#{pesquisaInternaBibliotecaMBean.quantideResultadosPorPagina}">
							<f:selectItems value="#{pesquisaInternaBibliotecaMBean.qtdResultadosPorPaginaComboBox}"/>
						</h:selectOneMenu>
					</td>
				</tr>
			
				<tfoot>
					<tr>
						<td colspan="8">
							
							<h:commandButton id="cmdPesquisaInternaAvancada" value="Pesquisar"  action="#{pesquisaInternaBibliotecaMBean.pesquisaAvancadaAcervo}" style="margin-right: 5px" onclick="ativaBotaoFalsoBuscaAvancada();">
								<f:setPropertyActionListener target="#{pesquisaInternaBibliotecaMBean.valorAbaPesquisa}" value="buscaAvancada" />
							</h:commandButton>
							
							<h:commandButton id="fakeCmdPesquisaInternaAvancada" value="Aguarde ..." style="display: none;" disabled="true" />
							<span id="indicatorPesquisaInternaAvancada"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
							
							<h:commandButton id="cmdLimpaPesquisaInternaAvancada" value="Limpar" action="#{pesquisaInternaBibliotecaMBean.limparResultadosBuscaAcervo}" >
								<f:setPropertyActionListener target="#{pesquisaInternaBibliotecaMBean.valorAbaPesquisa}" value="buscaAvancada" />
							</h:commandButton>
							
							<h:commandButton id="cmdFormataABNTPesquisaInternaAvancada" value="Gerar Formato da ABNT" actionListener="#{pesquisaInternaBibliotecaMBean.gerarResultadoPesquisaArquivoReferenciaFormatoABNT}"
								rendered="#{pesquisaInternaBibliotecaMBean.quantidadeTotalResultados > 0 &&  pesquisaInternaBibliotecaMBean.valorAbaPesquisa != 'buscaSimplesAutoridades'}"
								title="Gerar o resultado da pesquisa em um arquivo no formato de referência da ABNT" >
								<f:setPropertyActionListener target="#{pesquisaInternaBibliotecaMBean.valorAbaPesquisa}" value="buscaAvancada" />
							</h:commandButton>
							
							<c:if test="${pesquisaInternaBibliotecaMBean.buscaSelecionarTitulo}">
								<h:commandButton id="cmdVoltaPesquisaInternaAvancada" value="<< Voltar" action="#{pesquisaInternaBibliotecaMBean.voltarBusca}" style="margin-right: 5px">
									<f:setPropertyActionListener target="#{pesquisaInternaBibliotecaMBean.valorAbaPesquisa}" value="buscaAvancada" />
								</h:commandButton>
							</c:if>
							
							<%-- Como nesse caso não tem nenhum vinculação com quem chamou o caso de uso, utiliza-se apenas o voltar no navegador 
							<c:if test="${pesquisaInternaBibliotecaMBean.buscaSimples}">
								<input type="button" onclick="javascript:history.back();" value="<< Voltar" />
							</c:if> --%>
							
							<h:commandButton id="cmdCancelaPesquisaInternaAvancada" value="Cancelar" onclick="#{confirm}" immediate="true" action="#{pesquisaInternaBibliotecaMBean.cancelar}"/>
						</td>
					</tr>
				</tfoot>
			
			</table>
			
			</h:form>		
				
		</t:div>  <%-- Fecha a div buscas avançada --%>
		
	
		
		
		
		
		
		<t:div id="buscaSimplesAutoridades" styleClass="aba" rendered="#{! pesquisaInternaBibliotecaMBean.buscaSelecionarTitulo}">
		
			<h:form id="formBuscaInternaSimplesAutoridades">  <%-- Um form para cada aba senão os botões não funcionam direito --%>
			
				<table id="tableDadosPesquisaAutoridades" class="formulario" width="100%">
		
					<caption>Selecione os campos para a busca</caption>
			
						<tr>
							<td>
								 <input type="radio" name="radioCampoAutoridades" value="buscarAutor" id="radioAutorAutorizado"   ${pesquisaInternaBibliotecaMBean.buscarAutorAutorizado ? 'checked="checked"' : '' }  /> 
							</td>
							<th style="text-align:left">
								Autor:
							</th>
					
							<td colspan="6">
								<h:inputText value="#{pesquisaInternaBibliotecaMBean.autorAutorizado}" size="60" maxlength="100"
									onchange="marcarCheckBox(this, 'radioAutorAutorizado');" > </h:inputText>
							</td>
						</tr>
			
						<tr>
							<td>
								 <input type="radio" name="radioCampoAutoridades" value="buscarAssunto" id="radioAssuntoAutorizado"  ${pesquisaInternaBibliotecaMBean.buscarAssuntoAutorizado ? 'checked="checked"' : '' } />
							</td>
							<th style="text-align:left">
								Assunto:
							</th>
					
							<td colspan="6">
								<h:inputText value="#{pesquisaInternaBibliotecaMBean.assuntoAutorizado}" size="60" maxlength="100"
									onchange="marcarCheckBox(this, 'radioAssuntoAutorizado');"> </h:inputText>
							</td>
						</tr>
		
				
						<tr>
							<td></td>
							<th style="text-align:left">Ordenação:</th>
							<td colspan="6">
								<h:selectOneMenu value="#{pesquisaInternaBibliotecaMBean.valorCampoOrdenacao}">
									<f:selectItems value="#{pesquisaInternaBibliotecaMBean.campoOrdenacaoResultadosAutoridadesComboBox}" />
								</h:selectOneMenu>
							</td>
						</tr>
					
						<tr>
							<td></td>
							<th style="text-align:left">Registros por página:</th>
							<td colspan="6">
								<h:selectOneMenu value="#{pesquisaInternaBibliotecaMBean.quantideResultadosPorPagina}">
									<f:selectItems value="#{pesquisaInternaBibliotecaMBean.qtdResultadosPorPaginaComboBox}"/>
								</h:selectOneMenu>
							</td>
						</tr>
				
						<tr style="height: 30px;">
						</tr>
				
						
						<tfoot>
							<tr>
								<td colspan="8">
									<h:commandButton id="cmdPesquisaInternaSimplesAutoridades" value="Pesquisar" action="#{pesquisaInternaBibliotecaMBean.pesquisaSimplesAcervoAutoridades}" style="margin-right: 5px" onclick="ativaBotaoFalsoBuscaSimplesAutoridades();">
										<f:setPropertyActionListener target="#{pesquisaInternaBibliotecaMBean.valorAbaPesquisa}" value="buscaSimplesAutoridades" />
									</h:commandButton>
									
									<h:commandButton id="fakeCmdPesquisaInternaSimplesAutoridade" value="Aguarde ..." style="display: none;" disabled="true" />
									<span id="indicatorPesquisaInternaSimplesAutoridades"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
									
									<h:commandButton id="cmdLimpaPesquisaInternaSimplesAutoridade" value="Limpar" action="#{pesquisaInternaBibliotecaMBean.limparResultadosBuscaAcervo}" style="margin-right: 5px" >
										<f:setPropertyActionListener target="#{pesquisaInternaBibliotecaMBean.valorAbaPesquisa}" value="buscaSimplesAutoridades" />
									</h:commandButton>
									
									<h:commandButton id="cmdCancelaPesquisaSimplesAutoridade" value="Cancelar" onclick="#{confirm}" immediate="true" action="#{pesquisaInternaBibliotecaMBean.cancelar}" />
										
								</td>
							</tr>
						</tfoot>

				</table>
				
			</h:form>	
					
		</t:div>			
		
	
	</div> <%-- Fecha a div geral das abas --%>
		














	

	<h:form id="formBuscaInternaAcervoBibliotecaResultado">


		<t:div style="text-align: center; margin-top: 20px;  margin-bottom: 20px;  " rendered="#{! pesquisaInternaBibliotecaMBean.valorAbaPesquisa == 'buscaSimplesAutoridades'}">
			<h:commandLink action="#{configuraPerfilInteresseUsuarioBibliotecaMBean.iniciar}" value="Não encontrou o que estava procurando?  Cadastre-se para receber avisos quando novos materiais forem incluídos no acervo." />
		</t:div>


		<%-- Inclue os links de páginação para percorrer os resultados das pesquisas no acervo --%>
				
		<c:set var="pesquisarAcervoPaginadoBiblioteca" value="${pesquisaInternaBibliotecaMBean}" scope="request" />
		<%@ include file="/public/biblioteca/pesquisas_acervo/paginaPaginacaoConsultaAcervo.jsp"%>


		<%--    mostra os resultados da busca    --%>

		<t:div rendered="#{pesquisaInternaBibliotecaMBean.quantidadeResultadosMostrados > 0 &&  pesquisaInternaBibliotecaMBean.valorAbaPesquisa != 'buscaSimplesAutoridades'}">
		
			
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
								
				<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Informações dos Materiais Informacionais
				&nbsp &nbsp &nbsp &nbsp &nbsp
				<c:if test="${! pesquisaInternaBibliotecaMBean.buscaSelecionarTitulo}">
					<h:graphicImage value="/img/submenu.png" style="overflow: visible;" />: Opções	
				</c:if>
				<c:if test="${pesquisaInternaBibliotecaMBean.buscaSelecionarTitulo}">
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Selecionar	
				</c:if>
			</div> 

			<table class="listagem tablesorter" width="100%"  style="margin-bottom: 20px" id="listagem">
				<caption>  Títulos Encontrados (  <h:outputText value="#{pesquisaInternaBibliotecaMBean.descricaoPaginacao}" /> )</caption>

				<thead>
					<tr>
						<th style="width: 38%"> Autor </th>
						<th style="width: 36%;"> Título </th>
						<th style="width: 8%; "> Edição </th>
						<th style="width: 8%; "> Ano </th>
						<th style="width: 1%; text-align: right"> Qtd.</th>
						<th style="width: 1%; text-align: center">  </th> <%--  Visualizar Materiais --%>
						<th style="width: 1%; text-align: center">  </th> <%--  Outras opções --%>
						<th style="width: 1%; text-align: center">  </th> <%--  Selecionar quando o caso de uso é chamado por outro--%>
					</tr>
				</thead>
				
				<c:forEach items="#{pesquisaInternaBibliotecaMBean.resultadosPaginadosEmMemoria}" var="titulo" varStatus="status">
				
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
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
						
						<td width="20" style="text-align: right">	 
							<h:outputText value="#{titulo.quantidadeMateriaisAtivosTitulo}" />
						</td>
						
						<td width="20">	
							<h:commandLink id="ClinkView" action="#{detalhesMateriaisDeUmTituloInternoMBean.visualizarMateriaisTitulo}" >
								<h:graphicImage id="ImageView" url="/img/view.gif" style="border:none" title="Visualizar Informações dos Materiais Informacionais " />
								<f:param name="idTitulo" value="#{titulo.idTituloCatalografico}"/>
								<f:param name="idsBibliotecasAcervoPublicoFormatados" value="#{pesquisaInternaBibliotecaMBean.idsBibliotecasAcervoPublicoFormatados}"/>						
								<f:param name="apenasSituacaoVisivelUsuarioFinal" value="true"/>
							</h:commandLink>
						
						</td>
						
						<td width="1%" style="text-align:center">
							<c:if test="${! pesquisaInternaBibliotecaMBean.buscaSelecionarTitulo}">
								<h:graphicImage value="/img/submenu.png" title="Opções">
									<rich:componentControl event="onmouseover" for="menuOpcoesTitulosEncontrados" operation="show">
								        <f:param value="#{titulo.idTituloCatalografico}" name="_id_titulo_context_menu"/>
								    </rich:componentControl>
								</h:graphicImage>
							</c:if>
						</td>
						
						<%--Se for uma busca chamada de outro caso de uso do sistema, habilita a opção de o usuário selecionar o título --%>
						<td width="20">	
							<c:if test="${pesquisaInternaBibliotecaMBean.buscaSelecionarTitulo}">
								<h:commandLink action="#{pesquisaInternaBibliotecaMBean.selecionouTitulo}">
									<h:graphicImage value="/img/seta.gif" style="border:none" title="Selecionar" />
									<f:param name="idTitulo" value="#{titulo.idTituloCatalografico}"/>
								</h:commandLink>
							</c:if>
						</td>
					</tr>
					
					<%-- Linha estra para visualizar as inforamções eletrônicos do Título --%>
						
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td colspan="8" style="font-size: xx-small; font-style: italic; font-weight: normal; text-align: center;">
							
							<c:if test="${titulo.idObraDigitalizada != null}">
								<a target="_blank" href="${ configSistema['linkSigaa'] }/sigaa/verProducao?idProducao=${titulo.idObraDigitalizada}&key=${ sf:generateArquivoKey(titulo.idObraDigitalizada) }">
									<h:graphicImage url="/img/pdf.png" style="border:none" title="Arquivo Digital" />
									Arquivo Digital
								</a>
							</c:if>
							
							<c:if test="${titulo.possuiEnderecoEletronico}">	
								<a href="#buscarEnderecos" onClick="abreJanelaEnderecosEletronicos(${titulo.id})" style="padding-left: 40px;">
									<img src="${ctx}/img/biblioteca/enderecoEletronico.png" title="Endereços Eletrônicos"/>
									 Endereços Eletrônicos
								</a>
							</c:if>
							
						</td>	
					</tr>
				
				</c:forEach>

				<c:if test="${pesquisaInternaBibliotecaMBean.quantidadeResultadosMostrados > 50}"> <%-- Só mostra no final se a listagem ficar muito grande --%>
					<tfoot>
						<tr>
							<td colspan="8" style="text-align: center; font-weight: bold;">
							<h:outputText value="#{pesquisaInternaBibliotecaMBean.descricaoPaginacao}"/> título(s) encontrado(s)
							</td>
						</tr>
					</tfoot>
				</c:if>
				
			</table>
			
		</t:div>

		
		<t:div id="divResultadoPesquisaAutoridade" rendered="#{pesquisaInternaBibliotecaMBean.quantidadeTotalResultados > 0 && pesquisaInternaBibliotecaMBean.valorAbaPesquisa == 'buscaSimplesAutoridades'}">
			
			
			<%--  Menu exibido com as opções de cada material, para poupar espaço e ícones na tela --%>
	
	
			<rich:contextMenu attached="false" id="menuOpcoesAutoridades" hideDelay="300" >
					
				  <rich:menuItem value="Visualizar Dados MARC da Autoridade" icon="/img/biblioteca/visualizarMarc.png" 
		           			onclick="abreJanelaInformacoesCompletasAutoridade( {_id_autoridade_context_menu} ); return false;">
		          </rich:menuItem>	
		          
		          <rich:menuItem value="Exportar Autoridade" icon="/img/biblioteca/exportar.png" action="#{cooperacaoTecnicaExportacaoMBean.exportarArquivoAutoridadesPublico}" >
		           				<f:param name="idAutoridadeParaExportacao" value="{_id_autoridade_context_menu}"/>
		          </rich:menuItem>  
		              
		    </rich:contextMenu>
				
	
			<div class="infoAltRem" style="margin-top: 10px">

				<h:graphicImage value="/img/submenu.png" style="overflow: visible;" />: Opções

			</div> 
	
	
	
			<%-- Tabela para mostrar o cabeçalho já que os dados estão sendo mostrados em uma rich datable eu não --%>
			<%-- E é mais difícil tentar formatar a rich dataTable para ter um estido igual do das tabelas padroes usadas --%>
	
			<table class="listagem">
				<caption>  Autoridades Encontradas ( <h:outputText value="#{pesquisaInternaBibliotecaMBean.quantidadeResultadosMostrados}"/>  )</caption>
				<thead>
					<tr>
						<th style="width: 45%; padding-left: 20px;"> Entrada Autorizada </th>
						<th style="width: 54%;"> Entradas Remissivas </th>
						
					
						<th style="width: 1%; text-align: center">  </th>
						
						
					</tr>
				</thead>

				

				<c:forEach items="#{pesquisaInternaBibliotecaMBean.resultadosPaginadosEmMemoria}" var="autoridade" varStatus="status">

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
							<h:outputText value="#{pesquisaInternaBibliotecaMBean.quantidadeResultadosMostrados}"/>  autoridade(s).
						</td>
					</tr>
				</tfoot>

				
				
			</table>
			
		</t:div>
		


		<!-- Listagem de artigos -->
		<c:if test="${ not empty pesquisaInternaBibliotecaMBean.artigos }">
			
			<%-- Modal panel com os detalhes de cada material selecionado pelo usuário --%>
	
			<a4j:outputPanel ajaxRendered="true" id="painelInfoCompletaArtigo">
				<c:set var="_artigo_selecionado" value="${detalhesArtigoMBean.obj}" scope="request" />
				<c:set var="_assinatura_artigo_selecionado" value="${detalhesArtigoMBean.assinatura}" scope="request" />
				<c:set var="_fasciculo_artigo_selecionado" value="${detalhesArtigoMBean.fasciculo}" scope="request" />
				<%@include file="/public/biblioteca/paginaPadraoDetalhesArtigo.jsp"%>
			</a4j:outputPanel>
			
			
			<div class="infoAltRem" style="margin-top: 10px">
				
				
			</div> 
			
			<table class="listagem" style="margin:bottom: 20px; width: 100%;">
				<caption>Artigos Encontrados ( ${fn:length( pesquisaInternaBibliotecaMBean.artigos)} )</caption>
				
				<thead>
					<tr>
						<th style="width: 30%">Autor</th>
						<th style="width: 40%">Título</th>
						<th style="width: 30%;">Palavras-chave</th>
					</tr>
				</thead>
				
				<tbody>
					<c:forEach var="artigo" items="#{ pesquisaInternaBibliotecaMBean.artigos }" varStatus="status">
						<tr class="${ status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">
							
							<td><h:outputText value="#{ artigo.autor }" /></td>
							
							<td><h:outputText value="#{ artigo.titulo }" /></td>
							
							<td>
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
				</tbody>
				
				<tfoot>
					<tr>
						<td style="font-weight: bold; text-align: center;" colspan="11">
							<h:outputText value="#{fn:length( pesquisaInternaBibliotecaMBean.artigos)}" /> artigo(s)
						</td>
					</tr>
				</tfoot>
			</table>
		</c:if> 
		
		
		
		<%--   Importa a página com as ações extras que o caso de uso de pesquisa no acervo da biblioteca pode ter   --%>
		<c:if test="${pesquisaInternaBibliotecaMBean.paginaAcoesExtras != null}">
			<c:import url="${pesquisaInternaBibliotecaMBean.paginaAcoesExtras}" />
		</c:if>
		
	</h:form>
	
	

	

</f:view>





<script type="text/javascript">
var AbasPesquisa = {
    init : function(){
        var abasPesquisa = new YAHOO.ext.TabPanel('abas-tipos-de-pesquisa-interna');

        abasPesquisa.addTab('buscaSimples', "Busca Simples");
        abasPesquisa.addTab('buscaMultiCampo', "Busca Multi-Campo");
        abasPesquisa.addTab('buscaAvancada', "Busca Avançada");
       
        
        <c:if test="${! pesquisaInternaBibliotecaMBean.buscaSelecionarTitulo}">
    		abasPesquisa.addTab('buscaSimplesAutoridades', "Busca de Autoridades");
    	</c:if>
        
        <c:if test="${sessionScope.abaPesquisaInterna == null || sessionScope.abaPesquisaInterna == ''}">
			abasPesquisa.activate('buscaMultiCampo');
    	</c:if>


    	<c:if test="${sessionScope.abaPesquisaInterna != null && sessionScope.abaPesquisaInterna != ''}">

		    <c:if test="${sessionScope.abaPesquisaInterna == 'buscaSimples'}">
				abasPesquisa.activate('buscaSimples');
			</c:if>
			<c:if test="${sessionScope.abaPesquisaInterna == 'buscaMultiCampo'}">
				abasPesquisa.activate('buscaMultiCampo');
			</c:if>
			<c:if test="${sessionScope.abaPesquisaInterna == 'buscaAvancada'}">
				abasPesquisa.activate('buscaAvancada');
			</c:if>
			<c:if test="${sessionScope.abaPesquisaInterna == 'buscaSimplesAutoridades'}">
				abasPesquisa.activate('buscaSimplesAutoridades');
			</c:if>
			
    	</c:if>
    	
	}
};

YAHOO.ext.EventManager.onDocumentReady(AbasPesquisa.init, AbasPesquisa, true);



</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>