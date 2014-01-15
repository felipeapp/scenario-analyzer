<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>
<script type="text/javascript" src="/shared/javascript/formatador.js"></script>

<c:set var="confirmBloqueio" value="if (!confirm('Confirma o bloqueio do material?')) return false" scope="request" />

<f:view>
	<h2><ufrn:subSistema /> > Incluir Nota de Circula��o </h2>
	
	<div class="descricaoOperacao" style="width:80%;">
		<p>   <strong>Observa��es:</strong> 
			<ul>
			<li>N�o ser� poss�vel bloquear o material no �ltimo dia para a sua renova��o, para n�o causar transtornos ao usu�rio.</li>
			<li>Caso um material com empr�stimo que ainda pode ser renovado seja bloqueado, ser� enviado um email ao usu�rio que est� com o material, 
			informando que o empr�stimo n�o poder� mais ser renovado.</li>
			</ul>
		</p>
	</div>
	
	<a4j:keepAlive beanName="notasCirculacaoMBean" />
	
	<%-- Quando o caso de uso � chamada da edi��o de materiais a partir da pesquisa de T�tulos no acervo. --%>
	<a4j:keepAlive beanName="detalhesMateriaisDeUmTituloMBean"></a4j:keepAlive>	
	
	<%-- Quando o caso de uso � chamada da edi��o dos exemplares, volta para a p�gina de listagem dos exemplares --%>
	<a4j:keepAlive beanName="pesquisarExemplarMBean"></a4j:keepAlive>
	
	<%-- Quando o caso de uso � chamada da edi��o dos fasc�culos, volta para a p�gina de listagem dos fasc�culos--%>
	<a4j:keepAlive beanName="pesquisarFasciculoMBean"></a4j:keepAlive>
	
	<%-- Quando o caso de uso � chamada da p�gina inclus�o de materiais, volta para a p�gina da busca no acervo --%>
	<a4j:keepAlive beanName="materialInformacionalMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>
	
	
	
	
	
	<h:form id="formIncluirNotaCirculacao">

		<table class="formulario"  style="width: 100%; border-bottom: none;">
			<caption>Nota de Circula��o </caption>

			<thead>
				<th style="text-align: left; width: 50%;" colspan="3"> Material</th>
				<th style="text-align: left; width: 20%;"> Prazo do Empr�stimo</th>
				<th style="text-align: left; width: 15%;"> Pode Renovar?</th>
				<th style="text-align: left; width: 15%;"> �ltimo Dia Renova��o?</th>
			</thead>

			<c:forEach items="#{notasCirculacaoMBean.materiaisParaInclusaoNota}" var="material" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td colspan="3">
						${material.informacao}
					</td>
					<td style="font-style: italic;">
						<ufrn:format type="dataHora" valor="${material.prazoEmprestimo}" /> 
					</td>
					<td>
						<c:if test="${material.podeRenovar }"> 
							<span style="font-weight: bold; color:red;"> SIM </span> 
						</c:if> 
						<c:if test="${! material.podeRenovar}"> 
							<span style="color: green; font-weight: bold;"> N�O </span>  
						</c:if>
					</td>
					<td>
						<c:if test="${material.ultimoDiaRenovacao }"> 
							<span style="color: red; font-weight: bold;"> SIM </span> 
						</c:if> 
						<c:if test="${! material.ultimoDiaRenovacao}"> 
							<span style="color: green; font-weight: bold;"> N�O </span>  
						</c:if>
					</td>
				</tr>
			</c:forEach>
	
			<tr style="text-align: center;">
				<td colspan="6">
					<h:selectOneRadio id="radioEscolheTipoNota" value="#{notasCirculacaoMBean.notaBloqueante}" style=" width:50%; margin-left: 25%; margin-right: 25%;">  
	        			<f:selectItem itemLabel="Incluir Nota Bloqueante" itemValue="true" />
	        			<f:selectItem itemLabel="Incluir Nota n�o Bloqueante" itemValue="false" />
	        			<a4j:support event="onclick" reRender="linhaInformaOcorrenciaNota"></a4j:support>
	    			</h:selectOneRadio>
				</td>
			</tr>
		</table>
	
		<%--   Regi�o redenrizada por ajax    --%>
		
		<a4j:outputPanel id="linhaInformaOcorrenciaNota" ajaxRendered="true" >
			<t:div rendered="#{! notasCirculacaoMBean.notaBloqueante}">
				<table class="formulario" style="width: 100%; border-top: none; border-bottom: none;">
					<tr>
						<th style="width: 50%">Mostrar Nota no Pr�ximo Empr�stimo:</th>
						<td>
							<h:selectBooleanCheckbox id="checkMostrarEmprestio" value="#{notasCirculacaoMBean.mostrarProximoEmprestimo}" />
						</td>
					</tr>
					<tr>
						<th style="width: 50%">Mostrar Nota na Pr�xima Renova��o:</th>
						<td>
							<h:selectBooleanCheckbox id="checkMostrarRenovacao" value="#{notasCirculacaoMBean.mostrarProximaRenovacao}"  /> 
						</td>
					</tr>
					<tr>
						<th style="width: 50%">Mostrar Nota na Pr�xima Devolu��o:</th>
						<td>
							<h:selectBooleanCheckbox id="checkMostrarDevolucao" value="#{notasCirculacaoMBean.mostrarProximaDevolucao}"   />
						</td>
					</tr>
				</table>
			</t:div>
		</a4j:outputPanel>

			
		<table class="formulario" style="width: 100%; border-top: none;">
				<tr>
					<th class="obrigatorio" style="width:20%;">Nota:</th>
					<td colspan="3"><h:inputTextarea id="inputAreaNotaCirculacao" value="#{notasCirculacaoMBean.nota}" rows="5" cols="75" onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 200);" /></td>
				</tr>
	
				<tr>
					<th colspan="2" style="width: 40% " >Caracteres Restantes:</th>
					<td colspan="2">
						<span id="quantidadeCaracteresDigitados">200</span>/200
					</td>
				</tr>
				
	
				<tfoot>
					<tr>
						<td colspan="6" align="center">
							
							<ufrn:checkRole  papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
										, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO} %>">
								<h:commandButton id="cmdButtonIncluirNota" value="Incluir Nota" action="#{notasCirculacaoMBean.incluirNota}" />
							</ufrn:checkRole>
							
							<h:commandButton id="cmdButtonVoltarIncluirNota" value="Cancelar " action="#{notasCirculacaoMBean.voltarTelaBusca}" onclick="#{confirm}" immediate="true"  />
							
						</td>
					</tr>
				</tfoot>
			</table>
			
		<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
		
	</h:form>

</f:view>


<script type="text/javascript">


	function textCounter(field, idMostraQuantidadeUsuario, maxlimit) {
		
		if (field.value.length > maxlimit){
			field.value = field.value.substring(0, maxlimit);
		}else{ 
			document.getElementById(idMostraQuantidadeUsuario).innerHTML = maxlimit - field.value.length ;
		} 
	}

	
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>