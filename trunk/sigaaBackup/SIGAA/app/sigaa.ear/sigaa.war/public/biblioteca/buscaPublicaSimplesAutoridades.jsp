
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>
<%@taglib uri="/tags/a4j" prefix="a4j"%>

<script type="text/javascript" >

	function ativaBotaoFalso() {
		$('formBuscaPublica:botaoPesquisarPublicaAutoridades').hide();
		$('formBuscaPublica:fakeBotaoPesquisarPublicaAutoridades').show();
		$('indicatorPesquisaPublicaAutoridades').style.display = '';
	}

	ativaBotaoVerdadeiro();

	function ativaBotaoVerdadeiro() {
		$('formBuscaPublica:botaoPesquisarPublicaAutoridades').show();
		$('formBuscaPublica:fakeBotaoPesquisarPublicaAutoridades').hide();
		$('indicatorPesquisaPublicaAutoridades').style.display = 'none';
	}
	
</script>

		<%--       formulario com os dados da busca multi campo pública    --%>

		<t:div rendered="#{pesquisaPublicaBibliotecaMBean.buscaAutoridadesSimples}">

			<table id="tableDadosPesquisaAutoridades" class="formulario" width="80%">
	
				<caption>Selecione os campos para a busca</caption>
		
					<tr>
						<td>
							 <input type="radio" name="radioCampoAutoridades" value="buscarAutor" id="radioAutorAutorizado"   ${pesquisaPublicaBibliotecaMBean.buscarAutorAutorizado ? 'checked="checked"' : '' }  /> 
						</td>
						<th style="text-align:left">
							Autor:
						</th>
				
						<td colspan="6">
							<h:inputText value="#{pesquisaPublicaBibliotecaMBean.autorAutorizado}" size="60" maxlength="100"
								onchange="marcarCheckBox(this, 'radioAutorAutorizado');" > </h:inputText>
						</td>
					</tr>
		
					<tr>
						<td>
							 <input type="radio" name="radioCampoAutoridades" value="buscarAssunto" id="radioAssuntoAutorizado"  ${pesquisaPublicaBibliotecaMBean.buscarAssuntoAutorizado ? 'checked="checked"' : '' } />
						</td>
						<th style="text-align:left">
							Assunto:
						</th>
				
						<td colspan="6">
							<h:inputText value="#{pesquisaPublicaBibliotecaMBean.assuntoAutorizado}" size="60" maxlength="100"
								onchange="marcarCheckBox(this, 'radioAssuntoAutorizado');"> </h:inputText>
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
			
					
					<tfoot>
						<tr>
							<td colspan="8">
								<h:commandButton id="botaoPesquisarPublicaAutoridades" value="Pesquisar" action="#{pesquisaPublicaBibliotecaMBean.pesquisaSimplesAcervoAutoridades}" style="margin-right: 5px" onclick="ativaBotaoFalso();"/>
								<h:commandButton id="fakeBotaoPesquisarPublicaAutoridades" value="Aguarde ..." style="display: none;" disabled="true" />
								<span id="indicatorPesquisaPublicaAutoridades"  style="display: none;"> <h:graphicImage value="/img/indicator.gif" /> </span>
								
								<h:commandButton value="Limpar" action="#{pesquisaPublicaBibliotecaMBean.limparResultadosBuscaAcervo}" style="margin-right: 5px"/>
								<h:commandButton id="butaoFormatoABNTBuscaAutoridadesPublica" value="Gerar Formato da ABNT" style="margin-right: 5px" 
									actionListener="#{pesquisaPublicaBibliotecaMBean.gerarResultadoPesquisaArquivoReferenciaFormatoABNT}"
									rendered="#{pesquisaPublicaBibliotecaMBean.quantidadeTotalResultados > 0}"
									title="Gerar o resultado da pesquisa em um arquivo no formato de referência da ABNT" />
									
								<h:commandButton id="cancelarBuscaSimplesAutoridade"  value="Cancelar" action="#{pesquisaPublicaBibliotecaMBean.cancelarBuscaPublica}" immediate="true" onclick="#{confirm}" style="margin-right: 5px"/>
								
							</td>
						</tr>
					</tfoot>

			</table>
		
		</t:div>
		