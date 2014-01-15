<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

<h2>  <ufrn:subSistema /> &gt; <h:outputText value="#{pesquisaMateriaisInformacionaisMBean.tituloOperacao}" /> </h2>

<div class="descricaoOperacao"> 
     <h:outputText value="#{pesquisaMateriaisInformacionaisMBean.descricaoOperacao}" escape="false" />
</div>


	<a4j:keepAlive beanName="pesquisaMateriaisInformacionaisMBean" />	

	<%--   Listar todos os Mbean que utilizam essa pesquisa  --%>
	<a4j:keepAlive beanName="notasCirculacaoMBean" />
	<a4j:keepAlive beanName="alteraDadosVariosMateriaisMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="modificarNotaCirculacaoVariosMateriaisMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="alterarMotivoBaixaVariosMateriaisMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="inventarioAcervoBibliotecaMBean"></a4j:keepAlive>

	<h:form id="formPesquisaPadraoMateriaisInformacionais">	

		<c:if test="${pesquisaMateriaisInformacionaisMBean.permitirUtilizarPesquisa}">
			<div class="infoAltRem" style="margin-top: 10px">		
			    <h:graphicImage value="/img/biblioteca/pesquisar_titulo.png" />
				<h:commandLink action="#{pesquisaMateriaisInformacionaisMBean.buscarExemplaresPorTitulo}" value="Pesquisar Materiais por Título" />
			</div>
		</c:if>
	
		<h:inputHidden id="hiddenTipoBusca" value="#{pesquisaMateriaisInformacionaisMBean.tipoBusca}"></h:inputHidden>
		
	
		<table class="formulario" style="margin-bottom:30px; width: 65%;">		
			
			<caption class="listagem">Buscar Material Informacional</caption>
			
			<tr>
				<td>
					<input id="radio1" type="radio" name="radio" onclick="getEl('formPesquisaPadraoMateriaisInformacionais:hiddenTipoBusca').dom.value = 1;">
					<h:outputText value="Código de Barras:"></h:outputText>						
  				</td>
  					
				<td style="text-align: center;">
						<h:inputText id="inputTxtCodBarras"  value="#{pesquisaMateriaisInformacionaisMBean.codigoBarras}" maxlength="20" onkeypress="return getCodigoTecla(event)" 
							onfocus="getEl('radio1').dom.checked = true; getEl('formPesquisaPadraoMateriaisInformacionais:hiddenTipoBusca').dom.value = 1; "/>
				</td>
				
				<td colspan="2"></td>
			</tr>
			
		
			<tr>
				<td>
					<input id="radio2" type="radio" name="radio" onclick="getEl('formPesquisaPadraoMateriaisInformacionais:hiddenTipoBusca').dom.value = 2;">
					<h:outputText value="Faixa de Códigos de Barras:"></h:outputText>
  				</td>
			
				<td style="text-align: center">	
					<h:inputText value="#{pesquisaMateriaisInformacionaisMBean.codigoBarrasInicial}" id="inputTxtCodBarrasInicial" maxlength="15" 
						onfocus="getEl('radio2').dom.checked = true; getEl('formPesquisaPadraoMateriaisInformacionais:hiddenTipoBusca').dom.value = 2; "/>
				</td>
				
				<th style="text-align: center">a</th>
				
				<td style="text-align: center">
					<h:inputText value="#{pesquisaMateriaisInformacionaisMBean.codigoBarrasFinal}" id="inputTxtCodBarrasFinal" maxlength="15" 
							onfocus="getEl('radio2').dom.checked = true; getEl('formPesquisaPadraoMateriaisInformacionais:hiddenTipoBusca').dom.value = 2; "/>
					<ufrn:help>Adicionar exemplares<br/> que estejam entre o<br/> intervalo de códigos<br/> de barras informado.</ufrn:help>
				</td>
				
			</tr>
		
						
			<tfoot>
				<tr>
					<td colspan="5" align="center">
						<h:commandButton value="Buscar" action="#{pesquisaMateriaisInformacionaisMBean.pesquisarMaterial}" id="botaoBuscarMaterial"/>
						<h:commandButton value="<< Voltar" action="#{pesquisaMateriaisInformacionaisMBean.mbeanChamador.voltarBuscaPesquisaPadraoMateriais}" />
						<h:commandButton value="Cancelar" action="#{pesquisaMateriaisInformacionaisMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
					</td>
				</tr>
			</tfoot>
			
		</table>
		
		
		<c:if test="${fn:length( pesquisaMateriaisInformacionaisMBean.materiais) > 0}">
			
			<div class="infoAltRem" style="margin-top: 10px">		
			    <h:graphicImage value="/img/delete.gif" />: Remover Material da Lista
			</div>
			
			<table class="listagem">
				<caption> Materiais escolhidos ( ${fn:length( pesquisaMateriaisInformacionaisMBean.materiais)} )</caption>	
				
				<thead>
					<th style="text-align: left"> Código de Barras</th>
					<th style="text-align: left"> Biblioteca</th>
					<th style="text-align: left"> Status</th>
					<th style="text-align: left"> Situação</th>
					<th style="text-align: left"> Tipo Material</th>
					<th> </th>
				</thead>
				
				<c:forEach items="#{pesquisaMateriaisInformacionaisMBean.materiais}" var="material" varStatus="status">
					<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
						<td>${material.codigoBarras}</td>
						<td>${material.biblioteca.descricao}</td>
						<td>${material.status.descricao}</td>
						
						<c:if test="${material.disponivel}"> 
							<td style="color:green"> ${material.situacao.descricao} </td>
						</c:if>
						<c:if test="${! material.disponivel && ! material.emprestado}"> 
							<td> ${material.situacao.descricao} </td>
						</c:if>
						<c:if test="${material.emprestado}"> 
							<td style="color:red"> ${material.situacao.descricao}</td>
						</c:if>
	
	
						<td>${material.tipoMaterial.descricao}</td>
						<td>
							<h:commandLink action="#{pesquisaMateriaisInformacionaisMBean.removerMaterialSelecionadoDaLista}" onclick="#{confirmDelete}">
								<h:graphicImage url="/img/delete.gif" style="border:none" title="Remover Material da Lista" />
								<f:param name="idMaterialRemocao" value="#{material.id}"/>				
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
				
				
				<tfoot>
					<tr>
						<td colspan="6" style="text-align: center">
						
							<h:commandButton value="#{pesquisaMateriaisInformacionaisMBean.confirmButton}" action="#{pesquisaMateriaisInformacionaisMBean.realizarAcao}"/>
						
							<h:commandButton value="Limpar Lista" action="#{pesquisaMateriaisInformacionaisMBean.removerTodosMateriaisDaLista}" />
						
							<h:commandButton value="<< Voltar" action="#{pesquisaMateriaisInformacionaisMBean.mbeanChamador.voltarBuscaPesquisaPadraoMateriais}" />
						
							<h:commandButton value="Cancelar" onclick="#{confirm}" immediate="true" action="#{pesquisaMateriaisInformacionaisMBean.cancelar}" />
							
						</td>
					</tr>
				</tfoot>
				
			</table>
			
		</c:if>	
			
	</h:form>

</f:view>



<script type="text/javascript">

	checarRadioButton();    // executa quando a página é carregada

	function checarRadioButton(){
		document.getElementById('formPesquisaPadraoMateriaisInformacionais:inputTxtCodBarras').focus();
	}

</script>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>