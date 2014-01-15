<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="relatorioRegistrosInventarioAcervoUsuarioMBean" />
	


	<h2> <ufrn:subSistema/> &gt; Registros Realizados por um Usuário em um Inventário </h2>

	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

	<div class="descricaoOperacao"> 
		<p>Neste relatório é possível visualizar os registros realizados por um determinado usuário para um Inventário do Acervo. </p>
		<p>O intuito desse relatório é o usuário poder verificar quais materiais ele já registrou em um certo inventário.</p>
		
		<p>É possível visualizar os registros feitos no inventário que está sendo realizado no momento para a biblioteca escolhida.</p>
	</div>
	
	<div id="indicatorGeracaoRelatorio" style="display:none; width: 100%; font-weight: bold; text-align: center; margin-bottom: 20px; margin-top: 10px;"> 
		Gerando o relatório, por favor aguarde, isto pode levar alguns minutos ... <br/>
		<img src="/sigaa/img/indicator_bar.gif"  alt="Aguarde..." title="Aguarde..."/>
	</div>
	
	<h:form id="formRelatorioRegistrosUsuarioInventarioDoAcervo">
			
			<table class="formulario">
				
				<caption class="formulario">Dados da Busca</caption>
	
				<tbody>
					
								
					<tr>
						<th class="obrigatorio">Biblioteca:</th>
						<td>
							<h:selectOneMenu id="comboboxBibliotecas" value="#{relatorioRegistrosInventarioAcervoUsuarioMBean.idBiblioteca}" style="width:450px;">
								<a4j:support event="onchange" actionListener="#{relatorioRegistrosInventarioAcervoUsuarioMBean.buscarInventarios}" reRender="outputInventarios"></a4j:support>
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
								<f:selectItems value="#{relatorioRegistrosInventarioAcervoUsuarioMBean.bibliotecasCombo}" />
							</h:selectOneMenu>
						</td>
					</tr>
							
					<tr>
						<th class="obrigatorio">Inventário:</th>
						<td>
							<h:selectOneMenu id="outputInventarios" value="#{relatorioRegistrosInventarioAcervoUsuarioMBean.idInventario}" style="width:450px;">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
								<f:selectItems value="#{relatorioRegistrosInventarioAcervoUsuarioMBean.inventariosCombo}"/>
							</h:selectOneMenu>
							<ufrn:help>
								Neste campo são listados os inventários de acordo com a biblioteca selecionada.
							</ufrn:help>
						</td>
					</tr>
					
					<tr>
						<th class="obrigatorio">Usuario:</th>
						<td>
							<h:inputHidden id="inputHiddenIdPessoa"  value="#{relatorioRegistrosInventarioAcervoUsuarioMBean.idPessoa}"/>
							<h:inputText   id="nomePessoa"  value="#{relatorioRegistrosInventarioAcervoUsuarioMBean.nomePessoa}" size="64" onkeyup="CAPS(this);"/>
							
							<rich:suggestionbox id="suggestionFiltroPessoa"
									for="nomePessoa"
									var="_pessoaRetornada" fetchValue="#{_pessoaRetornada.nome}"
									suggestionAction="#{relatorioRegistrosInventarioAcervoUsuarioMBean.autocompleteNomePessoa}"
									width="400" height="100" minChars="5">
									
									<h:column>
										<h:outputText value="#{_pessoaRetornada.nome}"/>
										( <h:outputText value="#{_pessoaRetornada.cpf_cnpj}" /> )
									</h:column>
									
									<a4j:support event="onselect" reRender="inputHiddenIdPessoa">
										<f:setPropertyActionListener
											value="#{_pessoaRetornada.id}" target="#{relatorioRegistrosInventarioAcervoUsuarioMBean.idPessoa}"/>
									</a4j:support>
									
							</rich:suggestionbox>
							
							
							<ufrn:help>
								Entre com o nome ou CPF do Usuário.
							</ufrn:help>
							
							<span id="indicadorCarregamentoPessoas" style="display:none;"> 
								<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
							</span>
							
						</td>
					</tr>
				
					<tr>
						<th>Ordenação:</th>
						<td>
							<h:selectOneRadio id="ordenacao"
									value="#{relatorioRegistrosInventarioAcervoUsuarioMBean.ordenacao}" layout="lineDirection" >
								<f:selectItem itemLabel="Código de Barras" itemValue="#{relatorioRegistrosInventarioAcervoUsuarioMBean.ordenarPorCodigoBarras}" />
								<f:selectItem itemLabel="Título" itemValue="#{relatorioRegistrosInventarioAcervoUsuarioMBean.ordenarPorTitulo}" />
								<f:selectItem itemLabel="Localização" itemValue="#{relatorioRegistrosInventarioAcervoUsuarioMBean.ordenarPorLocalizacao}" />
							</h:selectOneRadio>
						</td>
					</tr>
								
				</tbody>
				
				<tfoot>
					<tr>
						<td colspan="2" align="center">
						
							<%-- Oculto que chama a ação para visualizar o relatório via JavaScript --%>
							<h:commandButton id="cmdVisualizarRelatorioInventario" value="Visualizar Relatório" style="display:none;"
									actionListener="#{relatorioRegistrosInventarioAcervoUsuarioMBean.visualizarRelatorio}" 
									rendered="#{relatorioRegistrosInventarioAcervoUsuarioMBean.outputSteamDadosRelatorio != null}"/>
						
							<h:commandButton id="cmdGerarRelatorioInventario" value="Gerar Relatório" onclick="showIndicator();" action="#{relatorioRegistrosInventarioAcervoUsuarioMBean.gerarRelatorio}" />
							<h:commandButton id="fakeCmdGerarRelatorioInventario" value="Gerar Relatório" style="display: none;" disabled="true" />
							
							<h:commandButton value="Cancelar"  action="#{relatorioRegistrosInventarioAcervoUsuarioMBean.cancelar}" immediate="true" onclick="#{confirm}" />
							
						</td>
					</tr>
				</tfoot>
				
			</table>
		
			<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
			
	</h:form>

</f:view>



<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>



<script type="text/javascript">


	function showIndicator() {
		$('indicatorGeracaoRelatorio').show();
		$('formRelatorioRegistrosUsuarioInventarioDoAcervo:cmdGerarRelatorioInventario').hide();
		$('formRelatorioRegistrosUsuarioInventarioDoAcervo:fakeCmdGerarRelatorioInventario').show();
	}

	hideIndicator();
	
	function hideIndicator() {
		$('formRelatorioRegistrosUsuarioInventarioDoAcervo:cmdGerarRelatorioInventario').enable();
		$('formRelatorioRegistrosUsuarioInventarioDoAcervo:cmdGerarRelatorioInventario').show();
		$('formRelatorioRegistrosUsuarioInventarioDoAcervo:fakeCmdGerarRelatorioInventario').hide();
	}



	<%-- Chama o método que mostra o relatório para o usuário automaticamente, para o usuário não precisar clicar no botão. --%>
	
	if ( $('formRelatorioRegistrosUsuarioInventarioDoAcervo:cmdVisualizarRelatorioInventario') != null )
		visualizarRelatorio();

	
	<%-- Depois que o relatório é gerado esta função chama o método que mostra o relatório para o usuário --%>
	
	function visualizarRelatorio(){
		$('formRelatorioRegistrosUsuarioInventarioDoAcervo:cmdVisualizarRelatorioInventario').click();
	}
	
	
</script>
