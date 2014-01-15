<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>



<f:view>

	<h2>  <ufrn:subSistema /> &gt; Catalogação &gt; Busca código da tabela <strong>Cutter</strong> </h2>
	
	
	<div class="descricaoOperacao">
	  <p> Selecione quais das opções abaixo deve ser o código <strong>Cutter</strong> usado na catalogação. É mostrado em destaque o código que o sistema considera o correto. </p>  
	  <p> Selecione o código desejado, ou apenas pressione  <i>Enter</i> para confirmar o código calculado pelo sistema. </p>
	</div>

	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
	<%-- Mander os dados da pesquisa se o usuário clicar no botão voltar --%>
	<a4j:keepAlive beanName="catalogaAutoridadesMBean"></a4j:keepAlive>
	
	<%-- Para mandar os dados quando é importado vários título ou autoridades --%>
	<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>

	
	<h:form id="formEscolherCutter">
		
		<div class="infoAltRem" style="width: 60%">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:Selecionar Cutter Desejado
		</div>


		<table class="formulario" style="width: 70%;">
			<caption>Sugestões de Códigos Cutter ( ${fn:length(catalogacaoMBean.suguestoesTabelaCuter)} ) </caption>
			
			<thead>
				<tr>
					<th></th>
					<th> Sobre nome</th>
					<th> Cutter</th>
					<th> </th>
				</tr>
			</thead>
			
			<%-- Para otimizar, senão ele vai ficar realizando uma novo consulta para cada formato encontrado --%>
			<c:set var="sugestoes" value="${catalogacaoMBean.suguestoesTabelaCuter}" scope="request" />
			
			<c:if test="${ fn:length(catalogacaoMBean.suguestoesTabelaCuter) == 0}">
				<td colspan="4" style="color: red;"> Não existem sugestões na Tabela Cutter</td>
			</c:if>
			
			<c:if test="${ fn:length(catalogacaoMBean.suguestoesTabelaCuter) > 0}">
				<c:forEach items="#{sugestoes}" var="sugestao" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style=" ${sugestao.codigoCalculado ? 'background-color: #FFFFE4; font-weight: bold; font-style: italic;' : ''}"> 
						<td></td>
						<td style="width: 50%;">${sugestao.sobreNomeAutor}</td>
						<td style="width: 10%">${sugestao.caracterInicialSobrenome} ${sugestao.codigoCutter} ${sugestao.caracterInicialTitulo}</td>
						<td style="width: 1%">
							<h:commandLink styleClass="noborder" title="Selecionar o cutter para a catalogação" action="#{catalogacaoMBean.confirmaCutter}">
								<f:param name="idTabelaCutterEscolhida" value="#{sugestao.idTabelaCutter}"/>
								<h:graphicImage value="/img/seta.gif"/>
							</h:commandLink>
						</td>
					</tr>
				</c:forEach>
			</c:if>	
				
			<tfoot>
				<tr>
					<td colspan="4"   style="text-align: center;">
						<h:commandButton id="butaoVoltarPaginaCatalogacao" value="<< Voltar à Tela de Catalogação" action="#{catalogacaoMBean.telaDadosTituloCatalografico}" />
						<h:commandButton id="butaoConfirmarCutter" value="Confirmar Cutter Calculado pelo Sistema >>" action="#{catalogacaoMBean.confirmaCutter}" />
					</td>
				</tr>
			</tfoot>		
			
		</table>		

	</h:form>

</f:view>
	

<script type="text/javascript">

	focaBotaoConfirmacao();


	function focaBotaoConfirmacao(){
		document.getElementById('formEscolherCutter:butaoConfirmarCutter').focus();
	}

</script>




<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>