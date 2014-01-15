<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<h2>  <ufrn:subSistema /> &gt; Pesquisa de Exemplares</h2>

<div class="descricaoOperacao"> 
    <p>Nesta página é possível realizar uma busca direta dos exemplares no acervo do sistema de bibliotecas.</p>
    <p> <strong> Observação: Exemplares mostrados em <span style="color: red">vermelho</span> estão baixados do acervo.</strong> </p>
</div>

<script type="text/javascript">
<!--
	// função para abrir a página dos dados MARC sempre na mesma janela do navegador
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


<f:view>

	<h:form id="formPesquisaExemplar">


	<a4j:keepAlive beanName="pesquisarExemplarMBean"></a4j:keepAlive>

	<%-- Usado no caso de uso de substituição de um exemplar por outro--%>
	<c:if test="${pesquisarExemplarMBean.operacaoSubstituicao || pesquisarExemplarMBean.operacaoProcuraExemplarParaSubstituicao}" >
		<a4j:keepAlive beanName="substituirExemplarMBean"></a4j:keepAlive>
	</c:if>
	

	<table class="formulario" width="80%">
		
			<caption>Entre com os Parâmetros da Busca</caption>

			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarExemplarMBean.buscarCodigoBarras}" styleClass="noborder" id="checkCodigoBarras"/>
				</td>
				<th colspan="2"  style="text-align:left" width="25%">Código de Barras:</th>
				<td colspan="4">
					<h:inputText id="inputTextCodigoBarrasPesquisar" size="25" maxlength="20" value="#{pesquisarExemplarMBean.obj.codigoBarras}"
					onchange="marcarCheckBox(this, 'formPesquisaExemplar:checkCodigoBarras');" />
					<ufrn:help>Caso não se saiba o código de barras completo do exemplar, pode-se digitar somente os primeiros caracteres. </ufrn:help>
				</td>
			</tr>

			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarExemplarMBean.buscarBiblioteca}" styleClass="noborder" id="checkBiblioteca"/>
				</td>
				<th colspan="2"  style="text-align:left">Biblioteca:</th>
				<td colspan="4">
					<h:selectOneMenu id="comBoxBibliotecaPesquisar" value="#{pesquisarExemplarMBean.obj.biblioteca.id}" 
							onchange="marcarCheckBox(this, 'formPesquisaExemplar:checkBiblioteca');">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						<f:selectItems value="#{pesquisarExemplarMBean.bibliotecas}"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarExemplarMBean.buscarColecao}" styleClass="noborder" id="checkColecao"/>
				</td>
				<th colspan="2"  style="text-align:left">Coleção:</th>
				<td colspan="4">
					<h:selectOneMenu id="comBoxColecaoPesquisar" value="#{pesquisarExemplarMBean.obj.colecao.id}" 
							onchange="marcarCheckBox(this, 'formPesquisaExemplar:checkColecao');">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						<f:selectItems value="#{pesquisarExemplarMBean.colecoes}"/>
					</h:selectOneMenu>
				</td>

			</tr> 

			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarExemplarMBean.buscarTipoMaterial}" styleClass="noborder" id="checkTipoMaterial"/>
				</td>
				<th colspan="2"  style="text-align:left">Tipo Material:</th>
				<td colspan="4">
					<h:selectOneMenu id="comBoxTipoMaterialPesquisar" value="#{pesquisarExemplarMBean.obj.tipoMaterial.id}" 
								onchange="marcarCheckBox(this, 'formPesquisaExemplar:checkTipoMaterial');">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						<f:selectItems value="#{pesquisarExemplarMBean.tiposMaterial}"/>
					</h:selectOneMenu>
				</td>

			</tr>
			
			<tr>
				<td width="2%" valign="top">
					<h:selectBooleanCheckbox value="#{pesquisarExemplarMBean.buscarFormaDocumento}" styleClass="noborder" id="checkFormaDocumento"/>
				</td>
				<th colspan="2"  style="text-align:left;padding-top:4px;"  valign="top">Forma de Documento:</th>
				<td colspan="4">
					<h:selectManyListbox  id="comBoxFormaDocumentoPesquisar" value="#{pesquisarExemplarMBean.idsFormasDocumentoEscolhidos}" size="10"
						onchange="marcarCheckBox(this, 'formPesquisaExemplar:checkFormaDocumento');">
			           <f:selectItems value="#{formaDocumentoMBean.allCombo}"/>
			       </h:selectManyListbox>
			       <ufrn:help>Para selecionar mais de uma <strong>forma de documento</strong> mantenha pressionada a tecla "Ctrl", em seguida selecione os itens desejados. 
						Para retirar a seleção, também é preciso pressionar a tecla "Ctrl". </ufrn:help>
				</td>
			</tr>
			
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarExemplarMBean.buscarStatus}" styleClass="noborder" id="checkStatus"/>
				</td>
				<th colspan="2"  style="text-align:left">Status do Exemplar:</th>
				<td colspan="4">
					<h:selectOneMenu id="comBoxStatusPesquisar" value="#{pesquisarExemplarMBean.obj.status.id}" 
							onchange="marcarCheckBox(this, 'formPesquisaExemplar:checkStatus');">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						<f:selectItems value="#{pesquisarExemplarMBean.statusMateriais}"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{pesquisarExemplarMBean.buscarSituacao}"
							styleClass="noborder" id="checkSituacao"
							disabled="#{ pesquisarExemplarMBean.operacaoDesfazerBaixa }"/>
				</td>
				<th colspan="2"  style="text-align:left">Situação do Exemplar:</th>
				<td colspan="4">
					<h:selectOneMenu id="comBoxSituacaoPesquisar" value="#{pesquisarExemplarMBean.obj.situacao.id}" 
							onchange="marcarCheckBox(this, 'formPesquisaExemplar:checkSituacao');">
						<c:if test="${ not pesquisarExemplarMBean.operacaoDesfazerBaixa }">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
						</c:if>
						<f:selectItems value="#{pesquisarExemplarMBean.situacaoMateriais}"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<td width="2%">
					<%-- Não funciona no t:inputCalendar porque se o usuário clicar no calendário não tem como capturar o evento java script
					 <h:selectBooleanCheckbox value="#{pesquisarExemplarMBean.buscarDataCriacao}" styleClass="noborder" id="checkData"/>
					--%> 
				</td>
				<th colspan="2"  style="text-align:left">Data de criação do exemplar:</th>
				
				<td style="width: 100px;">
					<table>
					<tr><td>
					<t:inputCalendar value="#{pesquisarExemplarMBean.dataCriacaoInicio}" id="DataDeCriacaoInicial" size="10" maxlength="10"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy"
						renderAsPopup="true" renderPopupButtonAsImage="true" 
						popupTodayString="Hoje:" popupWeekString="Semana"
						onblur="getEl('formPesquisaExemplar:checkData').dom.checked = true;">
					</t:inputCalendar>
					</td><td>
					a
					</td><td>
					<t:inputCalendar value="#{pesquisarExemplarMBean.dataCriacaoFinal}" id="DataDeCriacaoFinal" size="10" maxlength="10"
						onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy"
						renderAsPopup="true" renderPopupButtonAsImage="true" 
						onblur="getEl('formPesquisaExemplar:checkData').dom.checked = true;">
					</t:inputCalendar>
					</td></tr>
					</table>
					
				 </td>
				 
			</tr>

			


			<tfoot>
				<tr>
					<td colspan="7">
						<h:commandButton id="cmdButtonPesquisarExemplar" value="Pesquisar" action="#{pesquisarExemplarMBean.pesquisar}"/>
						<h:commandButton id="cmdButtonLimpaResultadoPesquisaExemplar" value="Limpar" action="#{pesquisarExemplarMBean.limparDadosPesquisa}" />
						<h:commandButton id="cmdButtonCancelarPesquisarExemplar" value="Cancelar" onclick="#{confirm}" immediate="true" action="#{pesquisarExemplarMBean.cancelar}" />
					</td>
				</tr>
			</tfoot>


	</table>
	
	
	
</h:form>




<script type="text/javascript" src="/sigaa/javascript/biblioteca/mostraDetalhes.js"> </script>

<%-- Por padrão as informações dos detalhes do item não são mostradas. --%>
<style>	
	table.listagem td.detalhesExemplar { display: none; padding: 0;}

	
</style>

<h:form id="formResultadosPesquisaExemplar">

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


		<c:if test="${pesquisarExemplarMBean.qtdExemplares > 0}">
	
			<div class="infoAltRem" style="margin-top: 10px">
			
				<c:if test="${pesquisarExemplarMBean.operacao == pesquisarExemplarMBean.operacaoPesquisar }">
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: 
					Alterar as informações do Exemplar
				</c:if>
				
				<c:if test="${pesquisarExemplarMBean.operacaoSubstituicao}">
					<h:graphicImage value="/img/alterar_old.gif" style="overflow: visible;" />: 
					Substituir Exemplar por outro Similar
				</c:if>
				
				<c:if test="${pesquisarExemplarMBean.operacaoProcuraExemplarParaSubstituicao}">
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
					Selecionar Exemplar para Substituir o Anterior
				</c:if>		
				
				<c:if test="${pesquisarExemplarMBean.operacao == pesquisarExemplarMBean.operacaoBloquearMaterial}">
					<h:graphicImage value="/img/biblioteca/estornar.gif" style="overflow: visible;" />: 
					Selecione o material a ser bloqueado.
				</c:if>
				
				<c:if test="${pesquisarExemplarMBean.operacaoBaixar}">
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
					Selecionar Exemplar para realizar a baixa no acervo
				</c:if> 
				
				<c:if test="${pesquisarExemplarMBean.operacaoDesfazerBaixa}">
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
					Selecionar Exemplar para desfazer a baixa no acervo
				</c:if> 
				
				<c:if test="${pesquisarExemplarMBean.operacaoRemover}">
					<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
					Selecionar Exemplar para realizar a remoção do acervo
				</c:if> 
				
			</div> 

				<table class="listagem">
					<caption>Exemplares no acervo ( ${pesquisarExemplarMBean.qtdExemplares} )</caption>
					<thead>
						<tr align="center">
							<th width="15%">Cód. de Barras</th>
							<th width="34%">Biblioteca</th>
							<th width="40%">Localização</th>
							
							<c:if test="${pesquisarExemplarMBean.operacao == pesquisarExemplarMBean.operacaoPesquisar }">
								<th width="2%" style="text-align: center;"> </th>
							</c:if>
							
							<c:if test="${pesquisarExemplarMBean.operacaoSubstituicao}">
								<th width="2%" style="text-align: center;"> </th>
							</c:if>
							
							<c:if test="${pesquisarExemplarMBean.operacaoProcuraExemplarParaSubstituicao}">
								<th width="2%" style="text-align: center;"> </th>
							</c:if>
						
							<c:if test="${pesquisarExemplarMBean.operacao == pesquisarExemplarMBean.operacaoBloquearMaterial}">
								<th width="2%" style="text-align: center;"> </th>
							</c:if>
							
							<c:if test="${pesquisarExemplarMBean.operacaoBaixar ||
									pesquisarExemplarMBean.operacaoRemover ||
									pesquisarExemplarMBean.operacaoDesfazerBaixa }">
								<th width="2%" style="text-align: center;"> </th>
							</c:if>
							
						</tr>
					</thead>
					
					
					<c:forEach items="#{pesquisarExemplarMBean.exemplares}" var="exemplar" varStatus="status">
						<tr  class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" onMouseOver="javascript:this.style.backgroundColor='#C4D2EB'" onMouseOut="javascript:this.style.backgroundColor=''">

							<td style=" ${ exemplar.dadoBaixa ? 'color: red' : '' } ">
								${exemplar.codigoBarras} <c:if test="${exemplar.anexo}"> <span style="font-style: italic;">(anexo)</span> </c:if>
							</td>

							<td style=" ${ exemplar.dadoBaixa ? 'color: red' : '' } ">
								${exemplar.biblioteca.descricao}
							</td>

							<td style="${ exemplar.dadoBaixa ? 'color: red' : '' }">
								${exemplar.numeroChamada} <br/> <span style="font-style: italic;"> ${exemplar.segundaLocalizacao}</span>
							</td>					



							<%-- Coluna com os ícones --%>
							
							<td style="text-align: center;">
									
								<h:commandLink id="cmdLinkEditarExemplar" action="#{editaMaterialInformacionalMBean.iniciarParaEdicaoExemplares}"
										rendered="#{pesquisarExemplarMBean.operacao == pesquisarExemplarMBean.operacaoPesquisar && ! exemplar.dadoBaixa }">
									<h:graphicImage url="/img/alterar.gif" style="border:none"
										title="Editar as informações desse Exemplar" />

									<f:param name="idExemplarParaEdicao" value="#{exemplar.id}"/>					
								</h:commandLink>
								
								<h:commandLink id="cmdLinkSubstituirExemplar" action="#{substituirExemplarMBean.selecionaExemplarSubstituicao}"
										rendered="#{pesquisarExemplarMBean.operacaoSubstituicao}">
									<h:graphicImage url="/img/alterar_old.gif" style="border:none" title="Substituir esse exemplar" />
									<f:param name="idExemplarSubstituicao" value="#{exemplar.id}"/>
								</h:commandLink>
								
								
								<h:commandLink id="cmdLinkSelecionaExemplarSubstituidor" action="#{substituirExemplarMBean.selecionaExemplarSubstituidor}"
										rendered="#{pesquisarExemplarMBean.operacaoProcuraExemplarParaSubstituicao && ! exemplar.dadoBaixa}">
									<h:graphicImage url="/img/seta.gif" style="border:none" title="Selecionar esse exemplar" />
									<f:param name="idExemplarSubstituidor" value="#{exemplar.id}"/>
								</h:commandLink>
								
								
								<h:commandLink id="cmdLinkBloqueiarExemplar" action="#{bloquearMaterialInformacionalMBean.preBloquear}"
									rendered="#{pesquisarExemplarMBean.operacao == pesquisarExemplarMBean.operacaoBloquearMaterial && ! exemplar.dadoBaixa}">
									<h:graphicImage url="/img/biblioteca/estornar.gif" style="border:none" title="Clique aqui para informar o motivo e bloquear esse material." />
									<f:param name="idMaterial" value="#{exemplar.id}"/>					
								</h:commandLink>
								
								<h:commandLink id="cmdLinkDarBaixaExemplar" action="#{editaMaterialInformacionalMBean.iniciaParaBaixaExemplar}"
										rendered="#{pesquisarExemplarMBean.operacaoBaixar && ! exemplar.dadoBaixa && ! exemplar.emprestado}">
									<h:graphicImage url="/img/seta.gif" style="border:none" title="Realizar a baixa do exemplar " />
									<f:param name="idExemplarBaixa" value="#{exemplar.id}"/>
								</h:commandLink>
								
								<h:commandLink id="cmdLinkDesfazerBaixaExemplar" action="#{editaMaterialInformacionalMBean.iniciaParaDesfazerBaixaExemplar}"
										rendered="#{pesquisarExemplarMBean.operacaoDesfazerBaixa && exemplar.dadoBaixa}">
									<h:graphicImage url="/img/seta.gif" style="border:none" title="Selecionar o exemplar para desfazer a baixa no acervo" />
									<f:param name="idExemplarBaixa" value="#{exemplar.id}"/>					
								</h:commandLink>
								
								<h:commandLink id="cmdLinkRemoverExemplar" action="#{editaMaterialInformacionalMBean.iniciaParaRemocaoExemplar}"
										rendered="#{pesquisarExemplarMBean.operacaoRemover && ! exemplar.dadoBaixa && ! exemplar.emprestado}">
									<h:graphicImage url="/img/seta.gif" style="border:none" title="Clique aqui para remover o exemplar do acervo" />
									<f:param name="idExemplarRemocao" value="#{exemplar.id}"/>					
								</h:commandLink>
								
							</td> 
							
		
						</tr>	
			
						
						<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							 <td colspan="4" style="text-align: center;">
							 
							 	
							 	<a4j:commandLink  value="Mostrar Detalhes "  actionListener="#{detalhesMateriaisDeUmTituloMBean.carregarDetalhesMaterialSelecionado}"
							 				ajaxSingle="true" oncomplete="modelPanelDetalhes.show();" style="font-weight: normal; font-style: italic; " 
							 				reRender="formResultadosPesquisaFasciculo">
							 		<f:param name="isPeriodicoVisualzarDetalhes"  value="false" />
							 		<f:param name="isMostrarInformacaoTituloDetalhesMaterial" value="true" />			
						 			<f:param name="idMaterialMostrarDetalhes" value="#{exemplar.id}"/>
							 	</a4j:commandLink>
							 	
							 	
						    </td>
						    
						</tr>

					</c:forEach>
				
					<tfoot>
						<tr>
							<td colspan="4" style="text-align: center; font-weight: bold;">
								<h:outputText id="outTxtQtdItens" value="Quantidade de Exemplares Encontrados: 
									#{pesquisarExemplarMBean.qtdExemplares}" rendered="#{pesquisarExemplarMBean.qtdExemplares > 0}" />		
							</td>
							
						</tr>
					</tfoot>
					
				</table>
	
		</c:if>
			
</h:form>
	
			
	
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>