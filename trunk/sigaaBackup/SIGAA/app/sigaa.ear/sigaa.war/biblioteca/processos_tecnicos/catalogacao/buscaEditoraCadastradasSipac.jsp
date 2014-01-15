<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>



<f:view>

	<h2>  <ufrn:subSistema /> &gt; Catalogação &gt; Busca editoras cadastradas no <h:outputText value="#{catalogacaoMBean.nomeSistemaBuscaEditoras}" /> </h2>
	
	
	<div class="descricaoOperacao"> 
	  <p>Página para buscar as editoras cadastradas no <h:outputText value="#{catalogacaoMBean.nomeSistemaBuscaEditoras}" /> </p>
	  <p>Entre com o nome da editora no local indicado, depois clique na opção <span style="font-family: monospace; font-size: 12px; "> "Selecionar Editora"</span> para preencher o campo MARC com o valor encontrado. </p>  
	</div>

	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
	<%-- Manter os dados da pesquisa se o usuário clicar no botão voltar --%>
	<a4j:keepAlive beanName="catalogaAutoridadesMBean"></a4j:keepAlive>
	
	<%-- Para mandar os dados quando é importado vários título ou autoridades --%>
	<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>

	
	<h:form id="formBuscaEditoras">

		<table class="formulario" style="width: 70%;">
			<caption>Entre como  nome da Editora</caption>
			
			<tr> 
				<th> <h:outputText value="Editora: " /> </th>
			
				<td>

					<h:panelGrid columns="3" style="width:100%; margin-left:auto; margin-right:auto; text-align:center; ">
							
				        <h:inputText value="#{catalogacaoMBean.nomeEditora}" id="inputTextEditora"  size="60" onkeypress="return submeteEditora(event)" />		
						
						<h:commandButton id="cmdLinkSelecionarEditora" value="Selecionar Editora" action="#{catalogacaoMBean.adicionarEditora}" />
						
						<rich:suggestionbox id="suggestionBoxId" for="inputTextEditora" tokens=",."
				                   suggestionAction="#{catalogacaoMBean.autocompleteEditora}" var="_result"
				                   fetchValue="#{_result.denominacao}"
				                   minChars="3"
				                   shadowOpacity="false"
				                   width="500" height="300"
				                   shadowDepth="false"
				                   cellpadding="3px"
				                   nothingLabel="EDITORA NÃO ENCONTRADA"
				                   usingSuggestObjects="true">
				                
				                	<%-- Elementos que vão ser rederizados no menu com os resulatados da busca --%>
				                   <h:column>
				                       <h:outputText value="#{_result.denominacao}" />
				                   </h:column>
				                   
				                                     
						</rich:suggestionbox>  
						
					
					
					</h:panelGrid>
					
				</td>
			</tr>
				
			<tfoot>
				<tr>
					<td colspan="2"   style="text-align: center;">
						<h:commandButton id="butaoVoltarPaginaCatalogacao" value="<< Voltar à Tela de Catalogação" action="#{catalogacaoMBean.telaDadosTituloCatalografico}" />
					</td>
				</tr>
			</tfoot>		
			
		</table>		

	</h:form>


</f:view>


<script type="text/javascript">

	focaCampoBusca();


	function focaCampoBusca(){
		document.getElementById('formBuscaEditoras:inputTextEditora').focus();
	}


	//retorna se a tecla pressionada foi um "ENTER"
	function submeteEditora(evento) {
		
		var tecla = "";
		if (isIe())
			tecla = evento.keyCode;
		else
			tecla = evento.which;
	
		if (tecla == 13){
			document.getElementById('formBuscaEditoras:cmdLinkSelecionarEditora').click();
			return false;
		}
		
		return true;
		
	}	
	
	function isIe() {
		return (typeof window.ActiveXObject != 'undefined');
	}	
	
</script>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>