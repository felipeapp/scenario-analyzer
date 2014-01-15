

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<script type="text/javascript" >

	function ativaBotaoFalso() {
		$('formBuscaPublica:botaoPesquisarPublicaSimples').hide();
		$('formBuscaPublica:fakeBotaoPesquisarPublicaSimples').show();
		$('indicatorPesquisaPublicaSimples').style.display = '';
	}

	ativaBotaoVerdadeiro();

	function ativaBotaoVerdadeiro() {
		$('formBuscaPublica:botaoPesquisarPublicaSimples').show();
		$('formBuscaPublica:fakeBotaoPesquisarPublicaSimples').hide();
		$('indicatorPesquisaPublicaSimples').style.display = 'none';
	}
	
</script>

	<t:div rendered="#{pesquisaPublicaBibliotecaMBean.buscaSimples}">

	<%--       formulario com os dados da busca multi campo pública    --%>

		<table id="tableDadosPesquisa" class="formulario" width="80%">

			<caption>Selecione os campos para a busca</caption>
		
				<tr style="height: 20px;">
					<td colspan="8"></td>
				</tr>
				
				<tr>
					<td style="width: 2%;">
						<h:selectBooleanCheckbox value="#{pesquisaPublicaBibliotecaMBean.campoBuscaSimples.buscarCampo}" styleClass="noborder" id="checkDadosPesquisaSimples"/>
					</td>
					<th style="text-align:left; width: 18%;">
					
					</th>
			
					<td style="width: 80%;" colspan="6">
						<h:inputText value="#{pesquisaPublicaBibliotecaMBean.campoBuscaSimples.valorCampo}" size="90" maxlength="100" styleClass="campoBuscaSimples"
						onchange="marcarCheckBox(this, 'formBuscaPublica:checkDadosPesquisaSimples');"> </h:inputText>
					</td>
				</tr>
			
				<tr style="height: 20px;">
					<td colspan="8"></td>
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
									<h:selectBooleanCheckbox value="#{pesquisaPublicaBibliotecaMBean.buscarBiblioteca}" styleClass="noborder" id="checkBibliotecaBuscaSimples"/>
								</td>
								
								<th colspan="1"  style="text-align:left; width:18%;">Biblioteca:</th>
								
								<td colspan="4" style=" padding: 0px; width:80%;">
									<h:selectOneMenu value="#{pesquisaPublicaBibliotecaMBean.idBiblioteca}" style="width:95%" 
												 onchange="marcarCheckBox(this, 'formBuscaPublica:checkBibliotecaBuscaSimples');">
										<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
										<f:selectItems value="#{pesquisaPublicaBibliotecaMBean.bibliotecasInternasAtivas}"/>
									</h:selectOneMenu>
								</td>
							</tr>
							
							<tr>
								<td width="2%">
									<h:selectBooleanCheckbox value="#{pesquisaPublicaBibliotecaMBean.buscarColecao}" styleClass="noborder" id="checkColecaoBuscaSimples"/>
								</td>
								<th colspan="1"  style="text-align:left;">Coleção:</th>
								<td colspan="4" style=" padding: 0px; ">
									<h:selectOneMenu value="#{pesquisaPublicaBibliotecaMBean.idColecao}" 
											onchange="marcarCheckBox(this, 'formBuscaPublica:checkColecaoBuscaSimples');">
										<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
										<f:selectItems value="#{pesquisaPublicaBibliotecaMBean.colecoesAtivas}"/>
									</h:selectOneMenu>
								</td>
				
							</tr> 
				
							<tr>
								<td width="2%">
									<h:selectBooleanCheckbox value="#{pesquisaPublicaBibliotecaMBean.buscarTipoMaterial}" styleClass="noborder" id="checkTipoMaterialBuscaSimples"/>
								</td>
								<th colspan="1"  style="text-align:left">Tipo de Material:</th>
								<td colspan="4" style=" padding: 0px; ">
									<h:selectOneMenu value="#{pesquisaPublicaBibliotecaMBean.idTipoMaterial}" 
											onchange="marcarCheckBox(this, 'formBuscaPublica:checkTipoMaterialBuscaSimples');">
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
						<h:commandButton id="botaoPesquisarPublicaSimples" value="Pesquisar" action="#{pesquisaPublicaBibliotecaMBean.pesquisaSimplesAcervo}" style="margin-right: 5px" onclick="ativaBotaoFalso();"/>
						<h:commandButton id="fakeBotaoPesquisarPublicaSimples" value="Aguarde ..." style="display: none;" disabled="true" />
						<span id="indicatorPesquisaPublicaSimples"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
						
						<h:commandButton value="Limpar" action="#{pesquisaPublicaBibliotecaMBean.limparResultadosBuscaAcervo}" style="margin-right: 5px"/>
						<h:commandButton id="butaoFormatoABNTBuscaSimplesCampoPublica" value="Gerar Formato da ABNT" style="margin-right: 5px" 
							actionListener="#{pesquisaPublicaBibliotecaMBean.gerarResultadoPesquisaArquivoReferenciaFormatoABNT}"
							rendered="#{pesquisaPublicaBibliotecaMBean.quantidadeTotalResultados > 0}"
							title="Gerar o resultado da pesquisa em um arquivo no formato de referência da ABNT" />
							
						<h:commandButton id="cancelarBuscaSimples" value="Cancelar" action="#{pesquisaPublicaBibliotecaMBean.cancelarBuscaPublica}" immediate="true" onclick="#{confirm}" style="margin-right: 5px"/>	
					</td>
				</tr>
			</tfoot>
				


		</table>
		
		
	</t:div>	
		