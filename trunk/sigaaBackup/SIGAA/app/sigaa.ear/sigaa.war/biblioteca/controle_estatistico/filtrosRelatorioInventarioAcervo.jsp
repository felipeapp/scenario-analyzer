<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<a4j:region>

	<a4j:keepAlive beanName="relatorioInventarioAcervoMBean" />
	
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

	<h2> <ufrn:subSistema/> &gt; Invent�rio do Acervo </h2>

	<div class="descricaoOperacao"> 
		<p>Neste relat�rio, � retornado uma listagem com os materiais <strong>n�o registrados</strong> no invent�rio do acervo das bibliotecas.</p>
		<p>O objetivo deste relat�rio � identificar os materiais do acervo que por algum motivo (falta de aten��o, empr�stimo, perda, furto, etc.) n�o foram levantados 
		quando do fechamento do invent�rio. Para tal, o sistema compara os materiais presentes no acervo da biblioteca com aqueles registrados no invent�rio do acervo, 
		a partir do que	os materias n�o presentes no invent�rio s�o exibidos no relat�rio.</p>
		<p>Por favor, selecione a biblioteca, o invent�rio de compara��o desejado, os filtros a serem aplicados aos materiais e a forma de ordena��o dos dados.</p>
		<br />
		<p><strong>Observa��es:</strong></p>
		<ul>
			<li>S� � poss�vel consultar pelos invent�rios que se encontram fechados (conclu�dos).</li>
			<li>Para efeito de gera��o do relat�rio, s� s�o considerados os materiais que tenham sido incorporados ao acervo at� a data de conclus�o do invent�rio 
			selecionado.</li>
			<li>Para desconsiderar da consulta os materiais emprestados (cujo motivo de n�o registro seria �bvio), basta selecionar todos os status de material com 
			exce��o daquele.</li>
			<li>Caso a biblioteca consultada possua um acervo muito grande, <strong>o relat�rio pode levar alguns minutos</strong> para ser gerado.</li>
		</ul>
	</div>
	
	<div id="indicatorGeracaoRelatorio" style="display:none; width: 100%; font-weight: bold; text-align: center; margin-bottom: 20px; margin-top: 10px;"> 
		Gerando o relat�rio, por favor aguarde, isto pode levar alguns minutos ... <br/>
		<img src="/sigaa/img/indicator_bar.gif"  alt="Aguarde..." title="Aguarde..."/>
	</div>
	
	<h:form id="formRelatorioInventarioDoAcervo">
			
			<table class="formulario">
				<caption class="formulario">Dados da Busca</caption>
	
				<tbody>
					<tr>
						<td>
							<table>
								<tr>
									<td style="width: 10px;"></td>
									<td>
										<table>
								
											<tr>
												<th class="obrigatorio">Biblioteca:</th>
												<td>
													<h:selectOneMenu id="comboboxBibliotecas" value="#{relatorioInventarioAcervoMBean.idBiblioteca}" style="width:450px;">
														<a4j:support event="onchange" action="#{relatorioInventarioAcervoMBean.buscarInventarios}" reRender="outputInventarios, selectManyColecao"></a4j:support>
														<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
														<f:selectItems value="#{relatorioInventarioAcervoMBean.bibliotecasCombo}" />
													</h:selectOneMenu>
												</td>
											</tr>
													
											<tr>
												<th class="obrigatorio">Invent�rio:</th>
												<td>
													<h:selectOneMenu id="outputInventarios" value="#{relatorioInventarioAcervoMBean.idInventario}" style="width:450px;">
														<a4j:support event="onchange" actionListener="#{relatorioInventarioAcervoMBean.selecionouInventario}" reRender="selectManyColecao"></a4j:support>
														<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
														<f:selectItems value="#{relatorioInventarioAcervoMBean.inventariosCombo}"/>
													</h:selectOneMenu>
												</td>
												<td style="vertical-align: middle; text-align: left;">
													<ufrn:help>
														Neste campo s�o listados os invent�rios que est�o fechados para a biblioteca selecionada.
													</ufrn:help>
												</td>
											</tr>
										
										
											<tr>
												<th>Cole��es:</th>
												<td>
													<t:selectManyListbox id="selectManyColecao" value="#{relatorioInventarioAcervoMBean.colecoes}" 
															readonly="#{relatorioInventarioAcervoMBean.inventarioEspecificoColecao}"
															size="7" style="width: 450px">
														<f:selectItems value="#{colecaoMBean.allCombo}" />
													</t:selectManyListbox>
												</td>
												<td style="vertical-align: middle; text-align: left;">
													<ufrn:help>
														Mantenha a tecla <em>Ctrl</em> pressionada para selecionar/deselecionar mais de uma cole��o.<br/>
														Use a tecla <em>Shift</em> para selecionar um intervalo de cole��es.
													</ufrn:help>
												</td>
											</tr>
										
						
											<tr>
												<th>Tipos de Material:</th>
												<td>
													<t:selectManyListbox id="tipoDeMaterial" value="#{relatorioInventarioAcervoMBean.tiposMaterial}" size="7" style="width: 450px">
														<f:selectItems value="#{tipoMaterialMBean.allCombo}" />
													</t:selectManyListbox>
												</td>
												<td style="vertical-align: middle; text-align: left;">
													<ufrn:help>
														Mantenha a tecla <em>Ctrl</em> pressionada para selecionar/deselecionar mais de um tipo de materiais.<br/>
														Use a tecla <em>Shift</em> para selecionar um intervalo de tipos de materiais.
													</ufrn:help>
												</td>
											</tr>

											<tr>
												<th>Situa��es de Material:</th>
												<td>
													<t:selectManyListbox id="situacaoesDosMaterial" value="#{relatorioInventarioAcervoMBean.situacoesMaterial}" size="7" style="width: 450px">
														<f:selectItems value="#{relatorioInventarioAcervoMBean.situacoesMateriaisCombo}" />
													</t:selectManyListbox>
												</td>
												<td style="vertical-align: middle; text-align: left;">
													<ufrn:help>
														Mantenha a tecla <em>Ctrl</em> pressionada para selecionar/deselecionar mais de uma situa��o.<br/>
														Use a tecla <em>Shift</em> para selecionar um intervalo de situa��es.
													</ufrn:help>
												</td>
											</tr>
										
											<tr>
												<th>Ordena��o:</th>
												<td>
													<h:selectOneRadio id="ordenacao"
															value="#{relatorioInventarioAcervoMBean.ordenacao}" layout="lineDirection" >
														<f:selectItem itemLabel="C�digo de Barras" itemValue="#{relatorioInventarioAcervoMBean.ordenarPorCodigoBarras}" />
														<f:selectItem itemLabel="T�tulo" itemValue="#{relatorioInventarioAcervoMBean.ordenarPorTitulo}" />
														<f:selectItem itemLabel="Localiza��o" itemValue="#{relatorioInventarioAcervoMBean.ordenarPorLocalizacao}" />
													</h:selectOneRadio>
												</td>
											</tr>
											
										</table>
									</td>
									<td style="width: 10px;"></td>
								</tr>
							</table>
						</td>
					</tr>
				</tbody>
				
				<tfoot>
					<tr>
						<td colspan="2" align="center">
						
							<%-- Oculto que chama a a��o para visualizar o relat�rio via JavaScript --%>
							<h:commandButton id="cmdVisualizarRelatorioInventario" value="Visualizar Relat�rio" style="display:none;"
									actionListener="#{relatorioInventarioAcervoMBean.visualizarRelatorio}" 
									rendered="#{relatorioInventarioAcervoMBean.outputSteamDadosRelatorio != null}"/>
						
							<h:commandButton id="cmdGerarRelatorioInventario" value="Gerar Relat�rio" onclick="showIndicator();" action="#{relatorioInventarioAcervoMBean.gerarRelatorio}" />
							<h:commandButton id="fakeCmdGerarRelatorioInventario" value="Gerar Relat�rio" style="display: none;" disabled="true" />
							
							<h:commandButton value="Cancelar"  action="#{relatorioInventarioAcervoMBean.cancelar}" immediate="true" onclick="#{confirm}" />
							
						</td>
					</tr>
				</tfoot>
				
			</table>
		
			<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
			
	</h:form>

	</a4j:region>
</f:view>



<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>



<script type="text/javascript">

	function showIndicator() {
		$('indicatorGeracaoRelatorio').show();
		$('formRelatorioInventarioDoAcervo:cmdGerarRelatorioInventario').hide();
		$('formRelatorioInventarioDoAcervo:fakeCmdGerarRelatorioInventario').show();
	}

	hideIndicator();
	
	function hideIndicator() {
		$('formRelatorioInventarioDoAcervo:cmdGerarRelatorioInventario').enable();
		$('formRelatorioInventarioDoAcervo:cmdGerarRelatorioInventario').show();
		$('formRelatorioInventarioDoAcervo:fakeCmdGerarRelatorioInventario').hide();
	}


	<%--
	Chama o m�todo que mostra o relat�rio para o usu�rio automaticamente, para o usu�rio n�o precisar clicar no bot�o.
	--%>
	if ( $('formRelatorioInventarioDoAcervo:cmdVisualizarRelatorioInventario') != null )
		visualizarRelatorio();

	<%--
	Depois que o relat�rio � gerado esta fun��o chama o m�todo que mostra o relat�rio para o usu�rio
	--%>
	function visualizarRelatorio(){
		$('formRelatorioInventarioDoAcervo:cmdVisualizarRelatorioInventario').click();
	}
	
	
</script>
