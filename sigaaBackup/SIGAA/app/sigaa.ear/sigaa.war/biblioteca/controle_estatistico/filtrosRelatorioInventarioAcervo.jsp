<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<a4j:region>

	<a4j:keepAlive beanName="relatorioInventarioAcervoMBean" />
	
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

	<h2> <ufrn:subSistema/> &gt; Inventário do Acervo </h2>

	<div class="descricaoOperacao"> 
		<p>Neste relatório, é retornado uma listagem com os materiais <strong>não registrados</strong> no inventário do acervo das bibliotecas.</p>
		<p>O objetivo deste relatório é identificar os materiais do acervo que por algum motivo (falta de atenção, empréstimo, perda, furto, etc.) não foram levantados 
		quando do fechamento do inventário. Para tal, o sistema compara os materiais presentes no acervo da biblioteca com aqueles registrados no inventário do acervo, 
		a partir do que	os materias não presentes no inventário são exibidos no relatório.</p>
		<p>Por favor, selecione a biblioteca, o inventário de comparação desejado, os filtros a serem aplicados aos materiais e a forma de ordenação dos dados.</p>
		<br />
		<p><strong>Observações:</strong></p>
		<ul>
			<li>Só é possível consultar pelos inventários que se encontram fechados (concluídos).</li>
			<li>Para efeito de geração do relatório, só são considerados os materiais que tenham sido incorporados ao acervo até a data de conclusão do inventário 
			selecionado.</li>
			<li>Para desconsiderar da consulta os materiais emprestados (cujo motivo de não registro seria óbvio), basta selecionar todos os status de material com 
			exceção daquele.</li>
			<li>Caso a biblioteca consultada possua um acervo muito grande, <strong>o relatório pode levar alguns minutos</strong> para ser gerado.</li>
		</ul>
	</div>
	
	<div id="indicatorGeracaoRelatorio" style="display:none; width: 100%; font-weight: bold; text-align: center; margin-bottom: 20px; margin-top: 10px;"> 
		Gerando o relatório, por favor aguarde, isto pode levar alguns minutos ... <br/>
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
												<th class="obrigatorio">Inventário:</th>
												<td>
													<h:selectOneMenu id="outputInventarios" value="#{relatorioInventarioAcervoMBean.idInventario}" style="width:450px;">
														<a4j:support event="onchange" actionListener="#{relatorioInventarioAcervoMBean.selecionouInventario}" reRender="selectManyColecao"></a4j:support>
														<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
														<f:selectItems value="#{relatorioInventarioAcervoMBean.inventariosCombo}"/>
													</h:selectOneMenu>
												</td>
												<td style="vertical-align: middle; text-align: left;">
													<ufrn:help>
														Neste campo são listados os inventários que estão fechados para a biblioteca selecionada.
													</ufrn:help>
												</td>
											</tr>
										
										
											<tr>
												<th>Coleções:</th>
												<td>
													<t:selectManyListbox id="selectManyColecao" value="#{relatorioInventarioAcervoMBean.colecoes}" 
															readonly="#{relatorioInventarioAcervoMBean.inventarioEspecificoColecao}"
															size="7" style="width: 450px">
														<f:selectItems value="#{colecaoMBean.allCombo}" />
													</t:selectManyListbox>
												</td>
												<td style="vertical-align: middle; text-align: left;">
													<ufrn:help>
														Mantenha a tecla <em>Ctrl</em> pressionada para selecionar/deselecionar mais de uma coleção.<br/>
														Use a tecla <em>Shift</em> para selecionar um intervalo de coleções.
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
												<th>Situações de Material:</th>
												<td>
													<t:selectManyListbox id="situacaoesDosMaterial" value="#{relatorioInventarioAcervoMBean.situacoesMaterial}" size="7" style="width: 450px">
														<f:selectItems value="#{relatorioInventarioAcervoMBean.situacoesMateriaisCombo}" />
													</t:selectManyListbox>
												</td>
												<td style="vertical-align: middle; text-align: left;">
													<ufrn:help>
														Mantenha a tecla <em>Ctrl</em> pressionada para selecionar/deselecionar mais de uma situação.<br/>
														Use a tecla <em>Shift</em> para selecionar um intervalo de situações.
													</ufrn:help>
												</td>
											</tr>
										
											<tr>
												<th>Ordenação:</th>
												<td>
													<h:selectOneRadio id="ordenacao"
															value="#{relatorioInventarioAcervoMBean.ordenacao}" layout="lineDirection" >
														<f:selectItem itemLabel="Código de Barras" itemValue="#{relatorioInventarioAcervoMBean.ordenarPorCodigoBarras}" />
														<f:selectItem itemLabel="Título" itemValue="#{relatorioInventarioAcervoMBean.ordenarPorTitulo}" />
														<f:selectItem itemLabel="Localização" itemValue="#{relatorioInventarioAcervoMBean.ordenarPorLocalizacao}" />
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
						
							<%-- Oculto que chama a ação para visualizar o relatório via JavaScript --%>
							<h:commandButton id="cmdVisualizarRelatorioInventario" value="Visualizar Relatório" style="display:none;"
									actionListener="#{relatorioInventarioAcervoMBean.visualizarRelatorio}" 
									rendered="#{relatorioInventarioAcervoMBean.outputSteamDadosRelatorio != null}"/>
						
							<h:commandButton id="cmdGerarRelatorioInventario" value="Gerar Relatório" onclick="showIndicator();" action="#{relatorioInventarioAcervoMBean.gerarRelatorio}" />
							<h:commandButton id="fakeCmdGerarRelatorioInventario" value="Gerar Relatório" style="display: none;" disabled="true" />
							
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
	Chama o método que mostra o relatório para o usuário automaticamente, para o usuário não precisar clicar no botão.
	--%>
	if ( $('formRelatorioInventarioDoAcervo:cmdVisualizarRelatorioInventario') != null )
		visualizarRelatorio();

	<%--
	Depois que o relatório é gerado esta função chama o método que mostra o relatório para o usuário
	--%>
	function visualizarRelatorio(){
		$('formRelatorioInventarioDoAcervo:cmdVisualizarRelatorioInventario').click();
	}
	
	
</script>
