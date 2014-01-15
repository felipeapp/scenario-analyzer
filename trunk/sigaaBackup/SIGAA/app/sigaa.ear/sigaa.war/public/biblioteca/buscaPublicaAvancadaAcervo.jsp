
<%-- Tags --%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>
<%@taglib uri="/tags/a4j" prefix="a4j"%>



<%--    pagina incluída dentro da pagina de pesquisa de títulos a área publica do sistema  --%>



<script type="text/javascript" >

	function ativaBotaoFalso() {
		$('formBuscaPublica:botaoPesquisarPublicaAvancada').hide();
		$('formBuscaPublica:fakeBotaoPesquisarPublicaAvancada').show();
		$('indicatorPesquisaPublicaAvancada').style.display = '';
	}

	ativaBotaoVerdadeiro();

	function ativaBotaoVerdadeiro() {
		$('formBuscaPublica:botaoPesquisarPublicaAvancada').show();
		$('formBuscaPublica:fakeBotaoPesquisarPublicaAvancada').hide();
		$('indicatorPesquisaPublicaAvancada').style.display = 'none';
	}
	
</script>


<style>
	td.radioButtom input { border: 0px solid;}
</style>

<t:div rendered="#{pesquisaPublicaBibliotecaMBean.buscaAvancada}">

<a4j:outputPanel ajaxRendered="true" style="width: 100%; ">

<table class="formulario" id="tableDadosPesquisa" style="width: 80%">

	<caption>Selecione os campos para a busca</caption>

	<c:forEach items="#{pesquisaPublicaBibliotecaMBean.campos}" var="campoPesquisa" varStatus="status">
	
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
							valueChangeListener="#{pesquisaPublicaBibliotecaMBean.verificaExibicaoTipoCampo}">	
						
						<a4j:support event="onchange" reRender="formBuscaTituloAvancada:tableDadosPesquisa" />
						
						<f:selectItems value="#{campoPesquisa.camposPesquisaAvancadaComboBox}" />
										
						<f:attribute name="campoSelecionado" value="#{campoPesquisa.posicaoCampo}" />
						
				 </h:selectOneMenu>
			</td>
			
			<td style="width: 40%; text-align: left;" colspan="6">
			
				<h:inputText id="inputTextCampo" style="width:90%" value="#{campoPesquisa.valorCampo}" size="80" maxlength="100"
					rendered="#{! campoPesquisa.renderizaComboxBibliotecaCampo
						&& ! campoPesquisa.renderizaComboxColecaoCampo
						&& ! campoPesquisa.renderizaComboxTipoMaterialCampo}"
					onchange="marcarCheckBox(this, 'formBuscaPublica:checkCampo_#{campoPesquisa.posicaoCampo}');"/>
				
				<h:selectOneMenu id="comboBoxBibliotecaCampo" value="#{campoPesquisa.valorCampo}"
						rendered="#{campoPesquisa.renderizaComboxBibliotecaCampo}" 
						onchange="marcarCheckBox(this, 'formBuscaPublica:checkCampo_#{campoPesquisa.posicaoCampo}');">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
					<f:selectItems value="#{pesquisaPublicaBibliotecaMBean.bibliotecasInternasAtivas}"/>
				</h:selectOneMenu>
				
				
				<h:selectOneMenu id="comboBoxColecaoCampo" value="#{campoPesquisa.valorCampo}" 
					rendered="#{campoPesquisa.renderizaComboxColecaoCampo}" 
					onchange="marcarCheckBox(this, 'formBuscaPublica:checkCampo_#{campoPesquisa.posicaoCampo}');">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
					<f:selectItems value="#{pesquisaPublicaBibliotecaMBean.colecoesAtivas}"/>
				</h:selectOneMenu>
				
				<h:selectOneMenu id="comboBoxTipoMaterialCampo" value="#{campoPesquisa.valorCampo}" 
					rendered="#{campoPesquisa.renderizaComboxTipoMaterialCampo}" 
					onchange="marcarCheckBox(this, 'formBuscaPublica:checkCampo_#{campoPesquisa.posicaoCampo}');">
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
					<f:selectItems value="#{pesquisaPublicaBibliotecaMBean.tiposMateriaisAtivos}"/>
				</h:selectOneMenu>
				
			</td>
				
		</tr>
		
	
	</c:forEach>

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

	<tfoot>
		<tr>
			<td colspan="8">
					<h:commandButton id="botaoPesquisarPublicaAvancada" value="Pesquisar" action="#{pesquisaPublicaBibliotecaMBean.pesquisaAvancadaAcervo}" style="margin-right: 5px" onclick="ativaBotaoFalso();"/>
					<h:commandButton id="fakeBotaoPesquisarPublicaAvancada" value="Aguarde ..." style="display: none;" disabled="true" />
					<span id="indicatorPesquisaPublicaAvancada"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
					
					<h:commandButton value="Limpar" action="#{pesquisaPublicaBibliotecaMBean.limparResultadosBuscaAcervo}" style="margin-right: 5px"/>
					<h:commandButton id="butaoFormatoABNTBuscaAvancadaPublica" value="Gerar Formato da ABNT" style="margin-right: 5px" 
						actionListener="#{pesquisaPublicaBibliotecaMBean.gerarResultadoPesquisaArquivoReferenciaFormatoABNT}"
						rendered="#{pesquisaPublicaBibliotecaMBean.quantidadeTotalResultados > 0}"
						title="Gerar o resultado da pesquisa em um arquivo no formato de referência da ABNT" />
						
					<h:commandButton id="cancelarBuscaAvancada"  value="Cancelar" action="#{pesquisaPublicaBibliotecaMBean.cancelarBuscaPublica}" immediate="true" onclick="#{confirm}" style="margin-right: 5px"/>	
			</td>
		</tr>
	</tfoot>

</table>

</a4j:outputPanel>

</t:div>

