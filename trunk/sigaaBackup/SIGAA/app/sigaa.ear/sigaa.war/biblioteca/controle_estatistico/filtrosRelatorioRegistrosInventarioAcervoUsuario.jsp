<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<a4j:keepAlive beanName="relatorioRegistrosInventarioAcervoUsuarioMBean" />
	


	<h2> <ufrn:subSistema/> &gt; Registros Realizados por um Usu�rio em um Invent�rio </h2>

	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>

	<div class="descricaoOperacao"> 
		<p>Neste relat�rio � poss�vel visualizar os registros realizados por um determinado usu�rio para um Invent�rio do Acervo. </p>
		<p>O intuito desse relat�rio � o usu�rio poder verificar quais materiais ele j� registrou em um certo invent�rio.</p>
		
		<p>� poss�vel visualizar os registros feitos no invent�rio que est� sendo realizado no momento para a biblioteca escolhida.</p>
	</div>
	
	<div id="indicatorGeracaoRelatorio" style="display:none; width: 100%; font-weight: bold; text-align: center; margin-bottom: 20px; margin-top: 10px;"> 
		Gerando o relat�rio, por favor aguarde, isto pode levar alguns minutos ... <br/>
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
						<th class="obrigatorio">Invent�rio:</th>
						<td>
							<h:selectOneMenu id="outputInventarios" value="#{relatorioRegistrosInventarioAcervoUsuarioMBean.idInventario}" style="width:450px;">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1" />
								<f:selectItems value="#{relatorioRegistrosInventarioAcervoUsuarioMBean.inventariosCombo}"/>
							</h:selectOneMenu>
							<ufrn:help>
								Neste campo s�o listados os invent�rios de acordo com a biblioteca selecionada.
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
								Entre com o nome ou CPF do Usu�rio.
							</ufrn:help>
							
							<span id="indicadorCarregamentoPessoas" style="display:none;"> 
								<img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> 
							</span>
							
						</td>
					</tr>
				
					<tr>
						<th>Ordena��o:</th>
						<td>
							<h:selectOneRadio id="ordenacao"
									value="#{relatorioRegistrosInventarioAcervoUsuarioMBean.ordenacao}" layout="lineDirection" >
								<f:selectItem itemLabel="C�digo de Barras" itemValue="#{relatorioRegistrosInventarioAcervoUsuarioMBean.ordenarPorCodigoBarras}" />
								<f:selectItem itemLabel="T�tulo" itemValue="#{relatorioRegistrosInventarioAcervoUsuarioMBean.ordenarPorTitulo}" />
								<f:selectItem itemLabel="Localiza��o" itemValue="#{relatorioRegistrosInventarioAcervoUsuarioMBean.ordenarPorLocalizacao}" />
							</h:selectOneRadio>
						</td>
					</tr>
								
				</tbody>
				
				<tfoot>
					<tr>
						<td colspan="2" align="center">
						
							<%-- Oculto que chama a a��o para visualizar o relat�rio via JavaScript --%>
							<h:commandButton id="cmdVisualizarRelatorioInventario" value="Visualizar Relat�rio" style="display:none;"
									actionListener="#{relatorioRegistrosInventarioAcervoUsuarioMBean.visualizarRelatorio}" 
									rendered="#{relatorioRegistrosInventarioAcervoUsuarioMBean.outputSteamDadosRelatorio != null}"/>
						
							<h:commandButton id="cmdGerarRelatorioInventario" value="Gerar Relat�rio" onclick="showIndicator();" action="#{relatorioRegistrosInventarioAcervoUsuarioMBean.gerarRelatorio}" />
							<h:commandButton id="fakeCmdGerarRelatorioInventario" value="Gerar Relat�rio" style="display: none;" disabled="true" />
							
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



	<%-- Chama o m�todo que mostra o relat�rio para o usu�rio automaticamente, para o usu�rio n�o precisar clicar no bot�o. --%>
	
	if ( $('formRelatorioRegistrosUsuarioInventarioDoAcervo:cmdVisualizarRelatorioInventario') != null )
		visualizarRelatorio();

	
	<%-- Depois que o relat�rio � gerado esta fun��o chama o m�todo que mostra o relat�rio para o usu�rio --%>
	
	function visualizarRelatorio(){
		$('formRelatorioRegistrosUsuarioInventarioDoAcervo:cmdVisualizarRelatorioInventario').click();
	}
	
	
</script>
