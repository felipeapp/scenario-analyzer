<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>



<f:view>

	<h2>  <ufrn:subSistema /> &gt; Cataloga��o &gt; Busca c�digo da tabela <strong>Cutter</strong> </h2>
	
	
	<div class="descricaoOperacao">
	  <p> Selecione quais das op��es abaixo deve ser o c�digo <strong>Cutter</strong> usado na cataloga��o. � mostrado em destaque o c�digo que o sistema considera o correto. </p>  
	  <p> Selecione o c�digo desejado, ou apenas pressione  <i>Enter</i> para confirmar o c�digo calculado pelo sistema. </p>
	</div>

	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
	<%-- Mander os dados da pesquisa se o usu�rio clicar no bot�o voltar --%>
	<a4j:keepAlive beanName="catalogaAutoridadesMBean"></a4j:keepAlive>
	
	<%-- Para mandar os dados quando � importado v�rios t�tulo ou autoridades --%>
	<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>

	
	<h:form id="formEscolherCutter">
		
		<div class="infoAltRem" style="width: 60%">
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />:Selecionar Cutter Desejado
		</div>


		<table class="formulario" style="width: 70%;">
			<caption>Sugest�es de C�digos Cutter ( ${fn:length(catalogacaoMBean.suguestoesTabelaCuter)} ) </caption>
			
			<thead>
				<tr>
					<th></th>
					<th> Sobre nome</th>
					<th> Cutter</th>
					<th> </th>
				</tr>
			</thead>
			
			<%-- Para otimizar, sen�o ele vai ficar realizando uma novo consulta para cada formato encontrado --%>
			<c:set var="sugestoes" value="${catalogacaoMBean.suguestoesTabelaCuter}" scope="request" />
			
			<c:if test="${ fn:length(catalogacaoMBean.suguestoesTabelaCuter) == 0}">
				<td colspan="4" style="color: red;"> N�o existem sugest�es na Tabela Cutter</td>
			</c:if>
			
			<c:if test="${ fn:length(catalogacaoMBean.suguestoesTabelaCuter) > 0}">
				<c:forEach items="#{sugestoes}" var="sugestao" varStatus="status">
					<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}" style=" ${sugestao.codigoCalculado ? 'background-color: #FFFFE4; font-weight: bold; font-style: italic;' : ''}"> 
						<td></td>
						<td style="width: 50%;">${sugestao.sobreNomeAutor}</td>
						<td style="width: 10%">${sugestao.caracterInicialSobrenome} ${sugestao.codigoCutter} ${sugestao.caracterInicialTitulo}</td>
						<td style="width: 1%">
							<h:commandLink styleClass="noborder" title="Selecionar o cutter para a cataloga��o" action="#{catalogacaoMBean.confirmaCutter}">
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
						<h:commandButton id="butaoVoltarPaginaCatalogacao" value="<< Voltar � Tela de Cataloga��o" action="#{catalogacaoMBean.telaDadosTituloCatalografico}" />
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