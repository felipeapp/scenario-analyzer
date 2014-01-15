<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<%@ taglib uri="/tags/ufrn" prefix="ufrn"%>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<%--    página incluída dentro da pagina de pesquisa no acervo de títulos  --%>
 
 
<div class="infoAltRem" style="margin-top: 10px">	
	
	<h:commandLink id="cmdLinkEditarExemplar" action="#{pesquisaTituloCatalograficoMBean.adicionaNovoCampoPesquisa}">
		<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />: 
			Adicionar Novo Campo de Pesquisa
	</h:commandLink>

	<h:graphicImage value="/img/biblioteca/estornar.gif" style="overflow: visible;" />: 
	Remover o Campo de Pesquisa
</div> 

<a4j:outputPanel ajaxRendered="true">


<table class="formulario" id="tableDadosPesquisa" style="width: 80%">

	<caption>Selecione os campos para a busca</caption>

	<tbody>

	<c:forEach items="#{pesquisaTituloCatalograficoMBean.campos}" var="campoPesquisa" varStatus="status">
	
		<c:if test="${campoPesquisa.posicaoCampo != 0 }" >  <%-- Para o primeiro campo é sempre 'E' --%>
			<tr>
				<td colspan="8" style="text-align: center; height: 60px;">
					<h:selectOneRadio value="#{campoPesquisa.conexao}" style="text-align: center; width: 30%; margin-left: 35%; margin-right: 35%; ">
						<f:selectItem itemLabel="E" itemValue="E" />
						<f:selectItem itemLabel="OU" itemValue="O"/>
						<f:selectItem itemLabel="NÃO" itemValue="N"/>
					</h:selectOneRadio>
				</td>
			</tr>
		</c:if>
	
		<tr style="width: 100%;">
			<td width="2%">
				<h:selectBooleanCheckbox value="#{campoPesquisa.buscarCampo}" styleClass="noborder" id="checkCampo_${status.index}"/>
			</td>
			
			<td width="18%">
				<h:selectOneMenu id="comboBoxTipoCampo" value="#{campoPesquisa.valorTipoCampo}"
							valueChangeListener="#{pesquisaTituloCatalograficoMBean.verificaExibicaoTipoCampo}">	
						
						<a4j:support event="onchange" reRender="formBuscaTituloAvancada:tableDadosPesquisa" />
						
						<f:selectItems value="#{campoPesquisa.camposPesquisaAvancadaComboBox}" />
						
						<f:attribute name="campoSelecionado" value="#{campoPesquisa.posicaoCampo}" />
						
				 </h:selectOneMenu>
			</td>
			
			<td colspan="5" style="width: 40%; text-align: left;">
			
				<h:inputText id="inputTextCampo" style="width:100%" value="#{campoPesquisa.valorCampo}" size="80" maxlength="100"
					rendered="#{! campoPesquisa.renderizaComboxBibliotecaCampo
						&& ! campoPesquisa.renderizaComboxColecaoCampo
						&& ! campoPesquisa.renderizaComboxTipoMaterialCampo
						&& ! campoPesquisa.renderizaComboxStatusCampo}"
					onchange="marcarCheckBox(this, 'formBuscaTituloAvancada:checkCampo_#{campoPesquisa.posicaoCampo}');"/>
				
				<h:selectOneMenu id="comboBoxBibliotecaCampo" value="#{campoPesquisa.valorCampo}" 
						rendered="#{campoPesquisa.renderizaComboxBibliotecaCampo}" 
						onfocus="getEl('formBuscaTituloAvancada:checkCampo_#{campoPesquisa.posicaoCampo}').dom.checked = true;">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
					<f:selectItems value="#{pesquisaTituloCatalograficoMBean.bibliotecasInternasAtivas}"/>
				</h:selectOneMenu>
				
				
				<h:selectOneMenu id="comboBoxColecaoCampo" value="#{campoPesquisa.valorCampo}" 
					rendered="#{campoPesquisa.renderizaComboxColecaoCampo}" 
					onchange="marcarCheckBox(this, 'formBuscaTituloAvancada:checkCampo_#{campoPesquisa.posicaoCampo}');">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
					<f:selectItems value="#{pesquisaTituloCatalograficoMBean.colecoesAtivas}"/>
				</h:selectOneMenu>
				
				<h:selectOneMenu id="comboBoxTipoMaterialCampo" value="#{campoPesquisa.valorCampo}" 
					rendered="#{campoPesquisa.renderizaComboxTipoMaterialCampo}" 
					onchange="marcarCheckBox(this, 'formBuscaTituloAvancada:checkCampo_#{campoPesquisa.posicaoCampo}');">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
					<f:selectItems value="#{pesquisaTituloCatalograficoMBean.tiposMateriaisAtivos}"/>
				</h:selectOneMenu>
				
				<h:selectOneMenu id="comboBoxStatusCampo" value="#{campoPesquisa.valorCampo}" 
					rendered="#{campoPesquisa.renderizaComboxStatusCampo}" 
					onchange="marcarCheckBox(this, 'formBuscaTituloAvancada:checkCampo_#{campoPesquisa.posicaoCampo}');">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
					<f:selectItems value="#{pesquisaTituloCatalograficoMBean.statusAtivos}"/>
				</h:selectOneMenu>
				
			</td>
			
			<td style="text-align: right; width: 2%">
				<h:commandLink id="cmdRemoverCampo" action="#{pesquisaTituloCatalograficoMBean.removerCampo}">
					<h:graphicImage url="/img/biblioteca/estornar.gif" style="border:none" title="Clique aqui para remover esse campo" />	
					<f:param name="campoSelecionado" value="#{campoPesquisa.posicaoCampo}"/>		
				</h:commandLink>
				
			</td>
				
		</tr>
		
	
	</c:forEach> 

	<tr>
		<td width="2%">
			<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.utilizarBuscaRemissiva}" styleClass="noborder" id="checkExecutarBuscaRemissiva"/>
		</td>
		<th colspan="7" style="text-align:left;">Executar a busca remissiva na base de autoridades</th>
	</tr>	

	<tr>
		<td style="width: 2%;" >
			<h:selectBooleanCheckbox value="#{pesquisaTituloCatalograficoMBean.exitirDadosFormatoRelatorio}" styleClass="noborder" id="checkExibirFormatoRelatorioBuscaAvancaa"/>
		</td>
		<th colspan="7" style="text-align:left; padding-left: 5px;">Exibir Dados no Formato de Relatório</th>
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

	</tbody>

	<tfoot>
		<tr>
			<td colspan="8" style="min-width: 500px;">
			
					<h:commandButton id="botaoPesquisar" value="Pesquisar" action="#{pesquisaTituloCatalograficoMBean.pesquisaAvancada}"  onclick="ativaBotaoFalso();">
						<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaAvancada" />
					</h:commandButton>
					
					<h:commandButton id="fakeBotaoPesquisar" value="Aguarde ..." style="display: none;" disabled="true" />
					<span id="indicatorPesquisaTitulosAvancada"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
					
					<h:commandButton id="butaoVoltarTelaInformacoesTombamentoAvancada" value="<< Voltar" action="#{catalogacaoMBean.telaBuscaInformacoesSipacAPartirNumeroPatrimonio}" rendered="#{pesquisaTituloCatalograficoMBean.pesquisaTituloParaCatalogacaoComTombamento}">
						<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaAvancada" />
					</h:commandButton>
					
					<h:commandButton id="butaoVoltarTelaSelecionaTituloAvancada" value="<< Voltar " action="#{pesquisaTituloCatalograficoMBean.mbeanChamadoPesquisaTitulo.voltarBuscaAcervo}" rendered="#{pesquisaTituloCatalograficoMBean.mbeanChamadoPesquisaTitulo.utilizaVoltarBuscaAcervo}" >
						<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaAvancada" />
					</h:commandButton>
					
					<h:commandButton id="butaoVoltarTelaSelecionaVariosMateriaisAvancada" value="<< Voltar " action="#{pesquisaTituloCatalograficoMBean.mbeanChamadorSelecionaMaterais.voltarBuscaPesquisarAcervoMateriais}" rendered="#{pesquisaTituloCatalograficoMBean.mbeanChamadorSelecionaMaterais.utilizaBotaoVoltarBuscaPesquisarAcervoMateriais}" >
						<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaAvancada" />
					</h:commandButton>
						
						
					<h:commandButton id="butaoLimpaBuscaAvancada" value="Limpar" action="#{pesquisaTituloCatalograficoMBean.apagarDadosPesquisaAvancada}">
						<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaAvancada" />
					</h:commandButton>
					
					<h:commandButton id="butaoFormatoABNTBuscaAvancada" value="Gerar Formato da ABNT" actionListener="#{pesquisaTituloCatalograficoMBean.gerarResultadoPesquisaArquivoReferenciaFormatoABNT}"
							rendered="#{pesquisaTituloCatalograficoMBean.quantidadeTotalResultados > 0}"
							title="Gerar o resultado da pesquisa em um arquivo no formato de referência da ABNT">
						<f:setPropertyActionListener target="#{pesquisaTituloCatalograficoMBean.valorAbaPesquisa}" value="buscaAvancada" />
					</h:commandButton>
					
					<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{pesquisaTituloCatalograficoMBean.cancelar}" />
			</td>
		</tr>
	</tfoot>

</table>

</a4j:outputPanel>

