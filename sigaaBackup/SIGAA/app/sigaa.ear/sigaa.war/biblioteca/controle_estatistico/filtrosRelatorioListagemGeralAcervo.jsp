<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<a4j:region>

	<a4j:keepAlive beanName="relatorioListagemGeralAcervoMBean" />

	<h2> <ufrn:subSistema/> &gt; Listagem Geral do Acervo </h2>

	<div class="descricaoOperacao"> 
		<p>Relatório utilizado para consultar o acervo completo das bibliotecas. Neste relatório é retornada uma listagem com <strong>todos os materiais ativos</strong> do acervo das bibliotecas.</p>
		<br />
		<p>Os materiais são mostrados juntamente com seus respectivos Títulos. Título sem materiais não são contados nesse relatório.</p>
		<br />
		<p> <span style="font-weight: bold;"> Observação 1: </span> Todos os filtros desse relatório filtram informações sobre os materiais. Por exemplo, o filtro "Número de Chamada" filtram 
		materiais que começam com esse número de chamada, não necessariamente implica que esse número de chamada será a classificação utilizada no Título. Se o Título possuir materiais em bibliotecas 
		diferentes, a classificação do Título é a classificação da biblioteca que catalogou o Título.  </p>
		<br />
		<p><span style="font-weight: bold;"> Observação 2:</span> Caso a biblioteca consultada possua um acervo muito grande, o relatório pode levar alguns minutos para ser gerado.  </p>
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
												<th class="obrigatorio">Bibliotecas:</th>
												<td>
													<h:selectManyListbox id="bibliotecas" value="#{relatorioListagemGeralAcervoMBean.bibliotecas}"
															style="width:450px;" size="10" title="Várias bibliotecas">
														<f:selectItems value="#{relatorioListagemGeralAcervoMBean.bibliotecasCombo}" />
													</h:selectManyListbox>
												</td>
												<td style="vertical-align: middle; text-align: left;">
													<ufrn:help>
														Mantenha a tecla <em>Ctrl</em> pressionada para selecionar mais de uma biblioteca.<br/>
														Use a tecla <em>Shift</em> para selecionar um intervalo de bibliotecas.
													</ufrn:help>
												</td>
											</tr>
										
										
											<tr>
												<th>Coleções:</th>
												<td>
													<t:selectManyListbox id="colecao" value="#{relatorioListagemGeralAcervoMBean.colecoes}" size="7" style="width: 450px">
														<f:selectItems value="#{colecaoMBean.allCombo}" />
													</t:selectManyListbox>
												</td>
												<td style="vertical-align: middle; text-align: left;">
													<ufrn:help>
														Mantenha a tecla <em>Ctrl</em> pressionada para selecionar mais de uma coleção.<br/>
														Use a tecla <em>Shift</em> para selecionar um intervalo de bibliotecas.
													</ufrn:help>
												</td>
											</tr>
										
						
											<tr>
												<th>Tipos de Material:</th>
												<td>
													<t:selectManyListbox id="tipoDeMaterial" value="#{relatorioListagemGeralAcervoMBean.tiposMaterial}" size="7" style="width: 450px">
														<f:selectItems value="#{tipoMaterialMBean.allCombo}" />
													</t:selectManyListbox>
												</td>
												<td style="vertical-align: middle; text-align: left;">
													<ufrn:help>
														Mantenha a tecla <em>Ctrl</em> pressionada para selecionar mais de um tipo.<br/>
														Use a tecla <em>Shift</em> para selecionar um intervalo de bibliotecas.
													</ufrn:help>
												</td>
											</tr>

											<tr>
												<th>Situações de Material:</th>
												<td>
													<t:selectManyListbox id="situacaoesDosMaterial" value="#{relatorioListagemGeralAcervoMBean.situacoesMaterial}" size="7" style="width: 450px">
														<f:selectItems value="#{relatorioListagemGeralAcervoMBean.situacoesMateriaisCombo}" />
													</t:selectManyListbox>
												</td>
												<td style="vertical-align: middle; text-align: left;">
													<ufrn:help>
														Mantenha a tecla <em>Ctrl</em> pressionada para selecionar mais de uma situação.<br/>
														Use a tecla <em>Shift</em> para selecionar um intervalo de bibliotecas.
													</ufrn:help>
												</td>
											</tr>
										
											<tr>
												<th>Materiais Mostrados:</th>
												<td>
													<h:selectOneRadio id="categoriaDeMaterial" layout="lineDirection"
															value="#{ relatorioListagemGeralAcervoMBean.ctgMaterial }">
														<f:selectItem itemLabel="Exemplares" itemValue="#{ relatorioListagemGeralAcervoMBean.ctgMatExemplares }" />
														<f:selectItem itemLabel="Fascículos" itemValue="#{ relatorioListagemGeralAcervoMBean.ctgMatFasciculos }" />
														<f:selectItem itemLabel="Ambos" itemValue="#{ relatorioListagemGeralAcervoMBean.ctgMatAmbos }" />
													</h:selectOneRadio>
												</td>
											</tr>
										
											<tr>
												<th>Número de Chamada:</th>
												<td>
													<h:inputText id="classificacaoCdu" value="#{relatorioListagemGeralAcervoMBean.numeroChamada}" size="7" maxlength="5"> </h:inputText>
												</td>
											</tr>
											
											
											<tr>
												<th>Ordenação:</th>
												<td>
													<h:selectOneRadio id="ordenacao"
															value="#{relatorioListagemGeralAcervoMBean.ordenacao}" layout="lineDirection" >
														<f:selectItem itemLabel="Código de Barras" itemValue="#{relatorioListagemGeralAcervoMBean.ordenarPorCodigoBarras}" />
														<f:selectItem itemLabel="Título" itemValue="#{relatorioListagemGeralAcervoMBean.ordenarPorTitulo}" />
														<f:selectItem itemLabel="Localização" itemValue="#{relatorioListagemGeralAcervoMBean.ordenarPorLocalizacao}" />
													</h:selectOneRadio>
												</td>
											</tr>
											
											<tr>
												<th>Apenas Materiais Emprestados ?</th>
												<td>
													<h:selectBooleanCheckbox id="checkApenasEmprestados" value="#{relatorioListagemGeralAcervoMBean.somenteEmprestados}"  />
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
									actionListener="#{relatorioListagemGeralAcervoMBean.visualizarRelatorio}" 
									rendered="#{relatorioListagemGeralAcervoMBean.outputSteamDadosRelatorio != null}"/>
						
							<h:commandButton id="cmdGerarRelatorioInventario" value="Gerar Relatório" onclick="showIndicator();" action="#{relatorioListagemGeralAcervoMBean.gerarRelatorio}" />
							<h:commandButton id="fakeCmdGerarRelatorioInventario" value="Gerar Relatório" style="display: none;" disabled="true" />
							
							<h:commandButton value="Cancelar"  action="#{relatorioListagemGeralAcervoMBean.cancelar}" immediate="true" onclick="#{confirm}" />
							
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
