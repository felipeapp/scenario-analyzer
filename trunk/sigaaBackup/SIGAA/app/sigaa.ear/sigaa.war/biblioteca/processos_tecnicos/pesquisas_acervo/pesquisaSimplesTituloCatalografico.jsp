<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="/tags/ufrn" prefix="ufrn"%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<%--    página incluída dentro da pagina de pesquisa no acervo de títulos  --%>




<table id="tableDadosPesquisa" class="formulario" style="width: 80%">

	<caption>Selecione os campos para a busca</caption>

	<tr style="height: 20px;">
		<td colspan="8"></td>
	</tr>

	<tr>
		<td style="width: 2%;">
			<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.campoBuscaSimples.buscarCampo}" styleClass="noborder" id="checkDadosPesquisa"/>
		</td>
		<th style="text-align:left; width: 18%;">
		
		</th>

		<td style="width: 80%;" colspan="6">
			<h:inputText value="#{pesquisaTituloCatalograficoMBean.campoBuscaSimples.valorCampo}" size="90" maxlength="100" styleClass="campoBuscaSimples"
			onchange="marcarCheckBox(this, 'formBuscaTituloSimples:checkDadosPesquisa');"> </h:inputText>
			
		</td>
	</tr>

	<tr style="height: 20px;">
		<td colspan="8"></td>
	</tr>

	
	
	<tr>
		<td width="2%">
			<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.utilizarBuscaRemissiva}" styleClass="noborder" id="checkExecutarBuscaRemissiva"/>
		</td>
		<th colspan="7" style="text-align:left;">Executar a busca remissiva na base de autoridades</th>
	</tr>	
	
	<tr>
		<td width="2%">
			<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.exitirDadosFormatoRelatorio}" styleClass="noborder" id="checkExibirFormatoRelatorioBuscaMultiCampo"/>
		</td>
		<th colspan="7" style="text-align:left;">Exibir Dados no Formato de Relatório</th>
	</tr>	
	
	<tr>
		<td></td>
		<th style="text-align:left">Ordenação:</th>
		<td colspan="6">
			<h:selectOneMenu value="#{pesquisaTituloCatalograficoMBean.valorCampoOrdenacao}">
				<f:selectItems value="#{pesquisaTituloCatalograficoMBean.campoOrdenacaoResultadosComboBox}"/>
			</h:selectOneMenu>
		</td>
	</tr>

	<tr>
		<td></td>
		<th style="text-align:left">Registros por página:</th>
		<td colspan="6">
			<h:selectOneMenu value="#{pesquisaTituloCatalograficoMBean.quantideResultadosPorPagina}">
				<f:selectItems value="#{pesquisaTituloCatalograficoMBean.qtdResultadosPorPaginaComboBox}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	
	<tr>
	
		<td colspan="8">
			<table id="tableDadosPesquisaInterna" class="subFormulario">
				<caption>Filtros sobre os Materiais dos Títulos</caption>
				
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.buscarBiblioteca}" styleClass="noborder" id="checkBiblioteca"/>
					</td>
					
					<th colspan="1"  style="text-align:left; width: 18%;">Biblioteca:</th>
					
					<td colspan="6">
						<h:selectOneMenu id="comboboxBibliotecasInternasBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.idBiblioteca}" 
								onchange="marcarCheckBox(this, 'formBuscaTituloSimples:checkBiblioteca');">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
							<f:selectItems value="#{pesquisaTituloCatalograficoMBean.bibliotecasInternasAtivas}"/>
						</h:selectOneMenu>
					</td>
				</tr>
				
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.buscarColecao}" styleClass="noborder" id="checkColecao"/>
					</td>
					<th colspan="1"  style="text-align:left">Coleção:</th>
					<td colspan="6">
						<h:selectOneMenu id="comboboxColecoesBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.idColecao}" 
								onchange="marcarCheckBox(this, 'formBuscaTituloSimples:checkColecao');">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
							<f:selectItems value="#{pesquisaTituloCatalograficoMBean.colecoesAtivas}"/>
						</h:selectOneMenu>
					</td>
	
				</tr> 
	
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.buscarTipoMaterial}" styleClass="noborder" id="checkTipoMaterial"/>
					</td>
					<th colspan="1"  style="text-align:left">Tipo de Material:</th>
					<td colspan="6">
						<h:selectOneMenu id="comboboxTipoMaterialBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.idTipoMaterial}" 
								onchange="marcarCheckBox(this, 'formBuscaTituloSimples:checkTipoMaterial');">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
							<f:selectItems value="#{pesquisaTituloCatalograficoMBean.tiposMateriaisAtivos}"/>
						</h:selectOneMenu>
					</td>

				</tr>
				
				<tr>
					<td width="2%">
						<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.buscarStatus}" styleClass="noborder" id="checkStatus"/>
					</td>
					<th colspan="1"  style="text-align:left">Status:</th>
					<td colspan="6">
						<h:selectOneMenu id="comboboxStatusBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.idStatus}" 
								onchange="marcarCheckBox(this, 'formBuscaTituloSimples:checkStatus');">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
							<f:selectItems value="#{pesquisaTituloCatalograficoMBean.statusAtivos}"/>
						</h:selectOneMenu>
					</td>

				</tr>
				
			</table>
		</td>
			
	<tr>


	<tfoot>
		<tr>
			<td colspan="8">
				
				<h:commandButton id="botaoPesquisar" value="Pesquisar" action="#{pesquisaTituloCatalograficoMBean.pesquisaSimplesAcervo}"  onclick="ativaBotaoFalso();">
					<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaSimples" />
				</h:commandButton>
				<h:commandButton id="fakeBotaoPesquisar" value="Aguarde ..." style="display: none;" disabled="true" />
				<span id="indicatorPesquisaTitulosSimples"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
				
				<h:commandButton id="butaoVoltarTelaInformacoesTombamentoMultiCampo"  value="<< Voltar" action="#{catalogacaoMBean.telaBuscaInformacoesSipacAPartirNumeroPatrimonio}" rendered="#{pesquisaTituloCatalograficoMBean.pesquisaTituloParaCatalogacaoComTombamento}">
					<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaSimples" />
				</h:commandButton>
				
				<h:commandButton id="butaoVoltarTelaSelecionaTituloMultiCampo" value="<< Voltar " action="#{pesquisaTituloCatalograficoMBean.mbeanChamadoPesquisaTitulo.voltarBuscaAcervo}" rendered="#{pesquisaTituloCatalograficoMBean.mbeanChamadoPesquisaTitulo.utilizaVoltarBuscaAcervo}" >
						<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaSimples" />
				</h:commandButton>
				
				<h:commandButton id="butaoVoltarTelaSelecionaVariosMateriaisMultiCampo" value="<< Voltar " action="#{pesquisaTituloCatalograficoMBean.mbeanChamadorSelecionaMaterais.voltarBuscaPesquisarAcervoMateriais}" rendered="#{pesquisaTituloCatalograficoMBean.mbeanChamadorSelecionaMaterais.utilizaBotaoVoltarBuscaPesquisarAcervoMateriais}" >
						<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaSimples" />
				</h:commandButton>
				
				
				<h:commandButton value="Limpar" id="butaoLimpaBuscaMultiCampo" action="#{pesquisaTituloCatalograficoMBean.apagarDadosPesquisaSimples}">
					<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaSimples" />
				</h:commandButton>
				
				<h:commandButton id="butaoFormatoABNTBuscaMultiCampo" value="Gerar Formato da ABNT" actionListener="#{pesquisaTituloCatalograficoMBean.gerarResultadoPesquisaArquivoReferenciaFormatoABNT}"
						rendered="#{pesquisaTituloCatalograficoMBean.quantidadeTotalResultados > 0}"
						title="Gerar o resultado da pesquisa em um arquivo no formato de referência da ABNT">
					<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaSimples" />
				</h:commandButton>
				
				<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{pesquisaTituloCatalograficoMBean.cancelar}"/>
				
			</td>
		</tr>
	</tfoot>


</table>
		


