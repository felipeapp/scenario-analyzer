<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="/tags/ufrn" prefix="ufrn"%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<%--    página incluída dentro da pagina de pesquisa no acervo de títulos  --%>




<table id="tableDadosPesquisa" class="formulario" style="width: 80%">

	<caption>Selecione os campos para a busca</caption>

	<tr>
		<td width="2%">
			<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.buscarNumeroSistema}" styleClass="noborder" id="checkNumeroSistema"/>
		</td>
		<th  width="30%" style="text-align:left"> Número do Sistema: </th>

		<td  width="70%" colspan="6">
			<h:inputText id="inputTextNumeroSistemaBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.numeroDoSistema}" size="10"  maxlength="9" 
						onkeyup="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkNumeroSistema'); return formatarInteiro(this);"> 
			</h:inputText> <%-- Aqui o  marcarCheckBox não pode ficar no onchange porque no IE a função formatarInteiro anula a execução --%>
		</td>
	</tr>

	<tr>
		<td width="2%">
			<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.buscarTitulo}" styleClass="noborder" id="checkTitulo"/>
		</td>
		<th  width="30%" style="text-align:left"> Título: </th>

		<td  width="70%" colspan="6">
			<h:inputText id="inputTextTituloBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.titulo}" size="60" maxlength="100"
				onchange="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkTitulo');"  /> 
		</td>
	</tr>

	<tr>
		<td width="2%">
			<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.buscarAutor}" styleClass="noborder" id="checkAutor"/>
		</td>
		<th  width="30%" style="text-align:left">Autor:</th>

		<td  width="70%" colspan="6">
			<h:inputText id="inputTextAutorBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.autor}" size="60" maxlength="100" 
				onchange="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkAutor');"/>
			<ufrn:help>Busca por autores e autores secundários</ufrn:help> 
		</td>
	</tr>
	
	
	<tr>
		<td width="2%">
			<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.buscarAssunto}" styleClass="noborder" id="checkAssunto"/>
		</td>
		<th  width="30%" style="text-align:left">Assunto:</th>

		<td  width="70%" colspan="6">
			<h:inputText id="inputTextAssuntoBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.assunto}" size="60" maxlength="100" 
				onchange="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkAssunto');"/> 
		</td>
	</tr>


	<tr>
		<td width="2%">
			<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.buscarLocalPublicacao}" styleClass="noborder" id="checkLocalPublicao"/>
		</td>
		<th  width="30%" style="text-align:left">Local de Publicação:</th>

		<td  width="70%" colspan="6">
			<h:inputText id="inputTextLocalPublicacaoBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.localPublicacao}" size="60" maxlength="100" 
				onchange="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkLocalPublicao');"/> 
		</td>
	</tr>

	<tr>
		<td width="2%">
			<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.buscarEditora}" styleClass="noborder" id="checkEditora"/>
		</td>
		<th  width="30%" style="text-align:left">Editora:</th>

		<td  width="70%" colspan="6">
			<h:inputText id="inputTextEditoraBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.editora}" size="60" maxlength="100" 
				onchange="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkEditora');"/>
		</td>
	</tr>

	<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao1}">
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.buscarClassificacao1}" styleClass="noborder" id="checkClassificacao1"/>
			</td>
			<th  width="30%" style="text-align:left">${classificacaoBibliograficaMBean.descricaoClassificacao1} :</th>
	
			<td  width="70%" colspan="6">
				<h:inputText id="inputTextClassificacao1BuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.classificacao1}" size="30" maxlength="50" 
					onchange="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkClassificacao1');"/>
			</td>
		</tr>
	</c:if>

	<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao2}">
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.buscarClassificacao2}" styleClass="noborder" id="checkClassificacao2"/>
			</td>
			<th  width="30%" style="text-align:left">${classificacaoBibliograficaMBean.descricaoClassificacao2} :</th>
	
			<td  width="70%" colspan="6">
				<h:inputText id="inputTextClassificacao2BuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.classificacao2}" size="30" maxlength="50" 
					onchange="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkClassificacao2');"/>
			</td>
		</tr>
	</c:if>
	
	<c:if test="${classificacaoBibliograficaMBean.sistemaUtilizandoClassificacao3}">
		<tr>
			<td width="2%">
				<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.buscarClassificacao3}" styleClass="noborder" id="checkClassificacao3"/>
			</td>
			<th  width="30%" style="text-align:left">${classificacaoBibliograficaMBean.descricaoClassificacao3} :</th>
	
			<td  width="70%" colspan="6">
				<h:inputText id="inputTextClassificacao3BuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.classificacao3}" size="30" maxlength="50" 
					onchange="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkClassificacao3');"/>
			</td>
		</tr>
	</c:if>

	<tr>
		<td width="2%">
			<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.buscarAno}" styleClass="noborder" id="checkAnoPublicacao"/>
		</td>
		<th colspan="1" style="text-align:left">Ano publicação de:</th>

		<td colspan="1" width="10%">
			<h:inputText id="inputTextAnoInicialBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.anoInicial}" size="7"  maxlength="4" 
					onkeyup="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkAnoPublicacao'); return formatarInteiro(this);"> </h:inputText>
					<%-- Aqui o  marcarCheckBox não pode ficar no onchange porque no IE a função formatarInteiro anula a execução --%>
		</td>

		<th colspan="1" width="10%">
			até:
		</th>
	  
		<td colspan="4">
			<h:inputText id="inputTextAnoFinalBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.anoFinal}" size="7" maxlength="4" 
					onkeyup="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkAnoPublicacao'); return formatarInteiro(this);"> </h:inputText>
					<%-- Aqui o  marcarCheckBox não pode ficar no onchange porque no IE a função formatarInteiro anula a execução --%>
		</td>
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
					
					<th colspan="1"  style="text-align:left">Biblioteca:</th>
					
					<td colspan="4">
						<h:selectOneMenu id="comboboxBibliotecasInternasBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.idBiblioteca}" 
								onchange="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkBiblioteca');">
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
					<td colspan="4">
						<h:selectOneMenu id="comboboxColecoesBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.idColecao}" 
								onchange="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkColecao');">
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
								onchange="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkTipoMaterial');">
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
					<td colspan="4">
						<h:selectOneMenu id="comboboxStatusBuscaTituloMulticampo" value="#{pesquisaTituloCatalograficoMBean.idStatus}" 
								onchange="marcarCheckBox(this, 'formBuscaTituloMulticampo:checkStatus');">
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
			<td colspan="9">
				
				<h:commandButton id="botaoPesquisar" value="Pesquisar" action="#{pesquisaTituloCatalograficoMBean.pesquisaMultiCampo}"  onclick="ativaBotaoFalso();">
					<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaMultiCampo" />
				</h:commandButton>
				<h:commandButton id="fakeBotaoPesquisar" value="Aguarde ..." style="display: none;" disabled="true" />
				<span id="indicatorPesquisaTitulosMultiCampo"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
				
				<h:commandButton id="butaoVoltarTelaInformacoesTombamentoMultiCampo"  value="<< Voltar" action="#{catalogacaoMBean.telaBuscaInformacoesSipacAPartirNumeroPatrimonio}" rendered="#{pesquisaTituloCatalograficoMBean.pesquisaTituloParaCatalogacaoComTombamento}">
					<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaMultiCampo" />
				</h:commandButton>
				
				<h:commandButton id="butaoVoltarTelaSelecionaTituloMultiCampo" value="<< Voltar " action="#{pesquisaTituloCatalograficoMBean.mbeanChamadoPesquisaTitulo.voltarBuscaAcervo}" rendered="#{pesquisaTituloCatalograficoMBean.mbeanChamadoPesquisaTitulo.utilizaVoltarBuscaAcervo}" >
						<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaMultiCampo" />
				</h:commandButton>
				
				<h:commandButton id="butaoVoltarTelaSelecionaVariosMateriaisMultiCampo" value="<< Voltar " action="#{pesquisaTituloCatalograficoMBean.mbeanChamadorSelecionaMaterais.voltarBuscaPesquisarAcervoMateriais}" rendered="#{pesquisaTituloCatalograficoMBean.mbeanChamadorSelecionaMaterais.utilizaBotaoVoltarBuscaPesquisarAcervoMateriais}" >
						<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaMultiCampo" />
				</h:commandButton>
				
				
				<h:commandButton value="Limpar" id="butaoLimpaBuscaMultiCampo" action="#{pesquisaTituloCatalograficoMBean.apagarDadosPesquisaMultiCampo}">
					<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaMultiCampo" />
				</h:commandButton>
				
				<h:commandButton id="butaoFormatoABNTBuscaMultiCampo" value="Gerar Formato da ABNT" actionListener="#{pesquisaTituloCatalograficoMBean.gerarResultadoPesquisaArquivoReferenciaFormatoABNT}"
						rendered="#{pesquisaTituloCatalograficoMBean.quantidadeTotalResultados > 0}"
						title="Gerar o resultado da pesquisa em um arquivo no formato de referência da ABNT">
					<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaMultiCampo" />
				</h:commandButton>
				
				<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{pesquisaTituloCatalograficoMBean.cancelar}"/>
				
			</td>
		</tr>
	</tfoot>


</table>
		


