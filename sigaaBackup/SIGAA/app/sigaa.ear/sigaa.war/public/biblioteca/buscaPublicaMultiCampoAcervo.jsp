

<%-- Tags --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<script type="text/javascript" >

	function ativaBotaoFalso() {
		$('formBuscaPublica:botaoPesquisarPublicaMulti').hide();
		$('formBuscaPublica:fakeBotaoPesquisarPublicaMulti').show();
		$('indicatorPesquisaPublicaMulti').style.display = '';
	}

	ativaBotaoVerdadeiro();

	function ativaBotaoVerdadeiro() {
		$('formBuscaPublica:botaoPesquisarPublicaMulti').show();
		$('formBuscaPublica:fakeBotaoPesquisarPublicaMulti').hide();
		$('indicatorPesquisaPublicaMulti').style.display = 'none';
	}
	
</script>

		<%--       formulario com os dados da busca multi campo pública    --%>
	
		<t:div rendered="#{pesquisaPublicaBibliotecaMBean.buscaMultiCampo}">

			<table id="tableDadosPesquisa" class="formulario" width="80%">
	
				<caption>Selecione os campos para a busca</caption>
			
					<tr>
						<td width="2%">
							<h:selectBooleanCheckbox value="#{pesquisaPublicaBibliotecaMBean.buscarTitulo}" styleClass="noborder" id="checkTitulo"/>
						</td>
						<th  width="22%" style="text-align:left">
							Título:
						</th>
				
						<td  width="76%" colspan="6">
							<h:inputText value="#{pesquisaPublicaBibliotecaMBean.titulo}" size="60" maxlength="100"
							onchange="marcarCheckBox(this, 'formBuscaPublica:checkTitulo');"> </h:inputText>
						</td>
					</tr>
		
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{pesquisaPublicaBibliotecaMBean.buscarAutor}" styleClass="noborder" id="checkAutor"/>
						</td>
						<th style="text-align:left">
							Autor:
						</th>
				
						<td colspan="6">
							<h:inputText value="#{pesquisaPublicaBibliotecaMBean.autor}" size="60" maxlength="100"
							onchange="marcarCheckBox(this, 'formBuscaPublica:checkAutor');"> </h:inputText>
						</td>
					</tr>
		
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{pesquisaPublicaBibliotecaMBean.buscarAssunto}" styleClass="noborder" id="checkAssunto"/>
						</td>
						<th style="text-align:left">
							Assunto:
						</th>
				
						<td colspan="6">
							<h:inputText value="#{pesquisaPublicaBibliotecaMBean.assunto}" size="60" maxlength="100"
							onchange="marcarCheckBox(this, 'formBuscaPublica:checkAssunto');"> </h:inputText>
						</td>
					</tr>			
		
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{pesquisaPublicaBibliotecaMBean.buscarLocalPublicacao}" styleClass="noborder" id="checkLocalPublicao"/>
						</td>
						<th style="text-align:left">
							Local de Publicação:
						</th>
				
						<td colspan="6">
							<h:inputText value="#{pesquisaPublicaBibliotecaMBean.localPublicacao}" size="60" maxlength="100"
							onchange="marcarCheckBox(this, 'formBuscaPublica:checkLocalPublicao');"> </h:inputText>
						</td>
					</tr>
		
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{pesquisaPublicaBibliotecaMBean.buscarEditora}" styleClass="noborder" id="checkEditora"/>
						</td>
						<th  style="text-align:left">
							Editora:
						</th>
				
						<td colspan="6">
							<h:inputText value="#{pesquisaPublicaBibliotecaMBean.editora}" size="60" maxlength="100"
							onchange="marcarCheckBox(this, 'formBuscaPublica:checkEditora');"> </h:inputText>
						</td>
					</tr>
		
					<tr>
						<td>
							<h:selectBooleanCheckbox value="#{pesquisaPublicaBibliotecaMBean.buscarAno}" styleClass="noborder" id="checkAnoPublicacao"/>
						</td>
						<th colspan="1" style="text-align:left">
							Ano de Publicação de:
						</th>
				
						<td colspan="1" width="10%">
							<h:inputText value="#{pesquisaPublicaBibliotecaMBean.anoInicial}" size="7"  maxlength="4" 
								onkeypress="return ApenasNumeros(event);"
								onchange="marcarCheckBox(this, 'formBuscaPublica:checkAnoPublicacao');"> </h:inputText>
						</td>
		
						<th colspan="1" width="10%">
							até:
						</th>
		  	  
						<td colspan="4">
							<h:inputText value="#{pesquisaPublicaBibliotecaMBean.anoFinal}" size="7" maxlength="4" 
							onkeypress="return ApenasNumeros(event);"
							onchange="marcarCheckBox(this, 'formBuscaPublica:checkAnoPublicacao');"> </h:inputText>
						</td>
					</tr>
	
			
					<tr>
						<td></td>
						<th style="text-align:left">Ordenação:</th>
						<td colspan="6">
							<h:selectOneMenu value="#{pesquisaPublicaBibliotecaMBean.valorCampoOrdenacao}">
								<f:selectItems value="#{pesquisaPublicaBibliotecaMBean.campoOrdenacaoResultadosComboBox}"/>
							</h:selectOneMenu>
						</td>
					</tr>
				
					<tr>
						<td></td>
						<th style="text-align:left">Registros por página:</th>
						<td colspan="6">
							<h:selectOneMenu value="#{pesquisaPublicaBibliotecaMBean.quantideResultadosPorPagina}">
								<f:selectItems value="#{pesquisaPublicaBibliotecaMBean.qtdResultadosPorPaginaComboBox}"/>
							</h:selectOneMenu>
						</td>
					</tr>
			
					<tr style="height: 30px;">
					</tr>
			
					<tr>
		
						<td colspan="8" style=" padding: 0px; ">
							<table id="tableDadosPesquisaInterna" class="subFormulario" align="left">
							
								<tr>
									<td width="2%">
										<h:selectBooleanCheckbox value="#{pesquisaPublicaBibliotecaMBean.buscarBiblioteca}" styleClass="noborder" id="checkBiblioteca"/>
									</td>
									
									<th colspan="1"  style="text-align:left; width:23%;">Biblioteca:</th>
									
									<td colspan="4" style=" padding: 0px; ">
										<h:selectOneMenu value="#{pesquisaPublicaBibliotecaMBean.idBiblioteca}" style="width:95%" 
													 onchange="marcarCheckBox(this, 'formBuscaPublica:checkBiblioteca');">
											<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
											<f:selectItems value="#{pesquisaPublicaBibliotecaMBean.bibliotecasInternasAtivas}"/>
										</h:selectOneMenu>
									</td>
								</tr>
								
								<tr>
									<td width="2%">
										<h:selectBooleanCheckbox value="#{pesquisaPublicaBibliotecaMBean.buscarColecao}" styleClass="noborder" id="checkColecao"/>
									</td>
									<th colspan="1"  style="text-align:left;">Coleção:</th>
									<td colspan="4" style=" padding: 0px; ">
										<h:selectOneMenu value="#{pesquisaPublicaBibliotecaMBean.idColecao}" 
												onchange="marcarCheckBox(this, 'formBuscaPublica:checkColecao');">
											<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
											<f:selectItems value="#{pesquisaPublicaBibliotecaMBean.colecoesAtivas}"/>
										</h:selectOneMenu>
									</td>
					
								</tr> 
					
								<tr>
									<td width="2%">
										<h:selectBooleanCheckbox value="#{pesquisaPublicaBibliotecaMBean.buscarTipoMaterial}" styleClass="noborder" id="checkTipoMaterial"/>
									</td>
									<th colspan="1"  style="text-align:left">Tipo de Material:</th>
									<td colspan="4" style=" padding: 0px; ">
										<h:selectOneMenu value="#{pesquisaPublicaBibliotecaMBean.idTipoMaterial}" 
												onchange="marcarCheckBox(this, 'formBuscaPublica:checkTipoMaterial');">
											<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
											<f:selectItems value="#{pesquisaPublicaBibliotecaMBean.tiposMateriaisAtivos}"/>
										</h:selectOneMenu>
									</td>
				
								</tr>
								
							</table>
						</td>
							
					<tr>
					
				<tfoot>
					<tr>
						<td colspan="8">
							<h:commandButton id="botaoPesquisarPublicaMulti" value="Pesquisar" action="#{pesquisaPublicaBibliotecaMBean.pesquisaMultiCampoAcervo}" style="margin-right: 5px" onclick="ativaBotaoFalso();"/>
							<h:commandButton id="fakeBotaoPesquisarPublicaMulti" value="Aguarde ..." style="display: none;" disabled="true" />
							<span id="indicatorPesquisaPublicaMulti"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
							
							<h:commandButton value="Limpar" action="#{pesquisaPublicaBibliotecaMBean.limparResultadosBuscaAcervo}" style="margin-right: 5px"/>
							<h:commandButton id="butaoFormatoABNTBuscaMultiCampoPublica" value="Gerar Formato da ABNT" style="margin-right: 5px"
								actionListener="#{pesquisaPublicaBibliotecaMBean.gerarResultadoPesquisaArquivoReferenciaFormatoABNT}"
								rendered="#{pesquisaPublicaBibliotecaMBean.quantidadeTotalResultados > 0}"
								title="Gerar o resultado da pesquisa em um arquivo no formato de referência da ABNT" />
								
							<h:commandButton id="cancelarBuscaMultiCampo"  value="Cancelar" action="#{pesquisaPublicaBibliotecaMBean.cancelarBuscaPublica}" immediate="true" onclick="#{confirm}" style="margin-right: 5px"/>
								
						</td>
					</tr>
				</tfoot>
					
	
	
			</table>
		
		
		</t:div>