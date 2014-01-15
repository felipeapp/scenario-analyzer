<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<a4j:region>

	<a4j:keepAlive beanName="relatorioListagemGeralAcervoMBean" />

	<h2> <ufrn:subSistema/> &gt; Listagem Geral do Acervo </h2>

	<div class="descricaoOperacao"> 
		<p>Relat�rio utilizado para consultar o acervo completo das bibliotecas. Neste relat�rio � retornada uma listagem com <strong>todos os materiais ativos</strong> do acervo das bibliotecas.</p>
		<br />
		<p>Os materiais s�o mostrados juntamente com seus respectivos T�tulos. T�tulo sem materiais n�o s�o contados nesse relat�rio.</p>
		<br />
		<p> <span style="font-weight: bold;"> Observa��o 1: </span> Todos os filtros desse relat�rio filtram informa��es sobre os materiais. Por exemplo, o filtro "N�mero de Chamada" filtram 
		materiais que come�am com esse n�mero de chamada, n�o necessariamente implica que esse n�mero de chamada ser� a classifica��o utilizada no T�tulo. Se o T�tulo possuir materiais em bibliotecas 
		diferentes, a classifica��o do T�tulo � a classifica��o da biblioteca que catalogou o T�tulo.  </p>
		<br />
		<p><span style="font-weight: bold;"> Observa��o 2:</span> Caso a biblioteca consultada possua um acervo muito grande, o relat�rio pode levar alguns minutos para ser gerado.  </p>
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
												<th class="obrigatorio">Bibliotecas:</th>
												<td>
													<h:selectManyListbox id="bibliotecas" value="#{relatorioListagemGeralAcervoMBean.bibliotecas}"
															style="width:450px;" size="10" title="V�rias bibliotecas">
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
												<th>Cole��es:</th>
												<td>
													<t:selectManyListbox id="colecao" value="#{relatorioListagemGeralAcervoMBean.colecoes}" size="7" style="width: 450px">
														<f:selectItems value="#{colecaoMBean.allCombo}" />
													</t:selectManyListbox>
												</td>
												<td style="vertical-align: middle; text-align: left;">
													<ufrn:help>
														Mantenha a tecla <em>Ctrl</em> pressionada para selecionar mais de uma cole��o.<br/>
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
												<th>Situa��es de Material:</th>
												<td>
													<t:selectManyListbox id="situacaoesDosMaterial" value="#{relatorioListagemGeralAcervoMBean.situacoesMaterial}" size="7" style="width: 450px">
														<f:selectItems value="#{relatorioListagemGeralAcervoMBean.situacoesMateriaisCombo}" />
													</t:selectManyListbox>
												</td>
												<td style="vertical-align: middle; text-align: left;">
													<ufrn:help>
														Mantenha a tecla <em>Ctrl</em> pressionada para selecionar mais de uma situa��o.<br/>
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
														<f:selectItem itemLabel="Fasc�culos" itemValue="#{ relatorioListagemGeralAcervoMBean.ctgMatFasciculos }" />
														<f:selectItem itemLabel="Ambos" itemValue="#{ relatorioListagemGeralAcervoMBean.ctgMatAmbos }" />
													</h:selectOneRadio>
												</td>
											</tr>
										
											<tr>
												<th>N�mero de Chamada:</th>
												<td>
													<h:inputText id="classificacaoCdu" value="#{relatorioListagemGeralAcervoMBean.numeroChamada}" size="7" maxlength="5"> </h:inputText>
												</td>
											</tr>
											
											
											<tr>
												<th>Ordena��o:</th>
												<td>
													<h:selectOneRadio id="ordenacao"
															value="#{relatorioListagemGeralAcervoMBean.ordenacao}" layout="lineDirection" >
														<f:selectItem itemLabel="C�digo de Barras" itemValue="#{relatorioListagemGeralAcervoMBean.ordenarPorCodigoBarras}" />
														<f:selectItem itemLabel="T�tulo" itemValue="#{relatorioListagemGeralAcervoMBean.ordenarPorTitulo}" />
														<f:selectItem itemLabel="Localiza��o" itemValue="#{relatorioListagemGeralAcervoMBean.ordenarPorLocalizacao}" />
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
						
							<%-- Oculto que chama a a��o para visualizar o relat�rio via JavaScript --%>
							<h:commandButton id="cmdVisualizarRelatorioInventario" value="Visualizar Relat�rio" style="display:none;"
									actionListener="#{relatorioListagemGeralAcervoMBean.visualizarRelatorio}" 
									rendered="#{relatorioListagemGeralAcervoMBean.outputSteamDadosRelatorio != null}"/>
						
							<h:commandButton id="cmdGerarRelatorioInventario" value="Gerar Relat�rio" onclick="showIndicator();" action="#{relatorioListagemGeralAcervoMBean.gerarRelatorio}" />
							<h:commandButton id="fakeCmdGerarRelatorioInventario" value="Gerar Relat�rio" style="display: none;" disabled="true" />
							
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
