<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script type="text/javascript" src="/shared/javascript/consulta_cep.js"></script>
<script type="text/javascript" src="/shared/javascript/formatador.js"></script>

<c:set var="confirmBloqueio" value="if (!confirm('Confirma o bloqueio do material?')) return false" scope="request" />

<f:view>
	<h2><ufrn:subSistema /> > Incluir Nota de Circulação </h2>
	
	<div class="descricaoOperacao" style="width:80%;">
		<p>Caro usuário,</p>
		<p>utilize este formulário para modificar o conteúdo da nota de circulação e suas características.</p>
	</div>
	
	<a4j:keepAlive beanName="notasCirculacaoMBean" />
	
	<%-- Quando o caso de uso é chamada da edição de materiais a partir da pesquisa de Títulos no acervo. --%>
	<a4j:keepAlive beanName="detalhesMateriaisDeUmTituloMBean"></a4j:keepAlive>	
	
	<%-- Quando o caso de uso é chamada da edição dos exemplares, volta para a página de listagem dos exemplares --%>
	<a4j:keepAlive beanName="pesquisarExemplarMBean"></a4j:keepAlive>
	
	<%-- Quando o caso de uso é chamada da edição dos fascículos, volta para a página de listagem dos fascículos--%>
	<a4j:keepAlive beanName="pesquisarFasciculoMBean"></a4j:keepAlive>
	
	<%-- Quando o caso de uso é chamada da página inclusão de materiais, volta para a página da busca no acervo --%>
	<a4j:keepAlive beanName="materialInformacionalMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>
	
	
	
	
	
	<h:form id="formIncluirNotaCirculacao">

		<table class="formulario"  style="width: 100%; border-bottom: none;">
			<caption>Nota de Circulação </caption>

			<thead>
				<th style="text-align: left; width: 50%;" colspan="3"> Material</th>
				<th style="text-align: left; width: 20%;"> Prazo do Empréstimo</th>
				<th style="text-align: left; width: 15%;"> Pode Renovar?</th>
				<th style="text-align: left; width: 15%;"> Último Dia Renovação?</th>
			</thead>

			<tr class="linhaImpar">
				<td colspan="3">
					${notasCirculacaoMBean.notaCirculacaoEdicao.material.informacao}
				</td>
				<td style="font-style: italic;">
					<ufrn:format type="dataHora" valor="${notasCirculacaoMBean.notaCirculacaoEdicao.material.prazoEmprestimo}" /> 
				</td>
				<td>
					<c:if test="${notasCirculacaoMBean.notaCirculacaoEdicao.material.podeRenovar }"> 
						<span style="font-weight: bold; color:red;"> SIM </span> 
					</c:if> 
					<c:if test="${! notasCirculacaoMBean.notaCirculacaoEdicao.material.podeRenovar}"> 
						<span style="color: green; font-weight: bold;"> NÃO </span>  
					</c:if>
				</td>
				<td>
					<c:if test="${notasCirculacaoMBean.notaCirculacaoEdicao.material.ultimoDiaRenovacao }"> 
						<span style="color: red; font-weight: bold;"> SIM </span> 
					</c:if> 
					<c:if test="${! notasCirculacaoMBean.notaCirculacaoEdicao.material.ultimoDiaRenovacao}"> 
						<span style="color: green; font-weight: bold;"> NÃO </span>  
					</c:if>
				</td>
			</tr>
	
			<tr style="text-align: center;">
				<td colspan="6">
					<h:selectOneRadio id="radioEscolheTipoNota" value="#{notasCirculacaoMBean.notaCirculacaoEdicao.bloquearMaterial}" style=" width:50%; margin-left: 25%; margin-right: 25%;">  
	        			<f:selectItem itemLabel="Nota Bloqueante" itemValue="true" />
	        			<f:selectItem itemLabel="Nota não Bloqueante" itemValue="false" />
	        			<a4j:support event="onclick" reRender="linhaInformaOcorrenciaNota"></a4j:support>
 	    			</h:selectOneRadio>
				</td>
			</tr>
		</table>
	
		<%--   Região redenrizada por ajax    --%>
		
		<a4j:outputPanel id="linhaInformaOcorrenciaNota" ajaxRendered="true" >
			<t:div rendered="#{! notasCirculacaoMBean.notaCirculacaoEdicao.bloquearMaterial}">
				<table class="formulario" style="width: 100%; border-top: none; border-bottom: none;">
					<tr>
						<th style="width: 50%">Mostrar Nota no Próximo Empréstimo:</th>
						<td>
							<h:selectBooleanCheckbox id="checkMostrarEmprestio" value="#{notasCirculacaoMBean.notaCirculacaoEdicao.mostrarEmprestimo}" />
						</td>
					</tr>
					<tr>
						<th style="width: 50%">Mostrar Nota na Próxima Renovação:</th>
						<td>
							<h:selectBooleanCheckbox id="checkMostrarRenovacao" value="#{notasCirculacaoMBean.notaCirculacaoEdicao.mostrarRenovacao}"  /> 
						</td>
					</tr>
					<tr>
						<th style="width: 50%">Mostrar Nota na Próxima Devolução:</th>
						<td>
							<h:selectBooleanCheckbox id="checkMostrarDevolucao" value="#{notasCirculacaoMBean.notaCirculacaoEdicao.mostrarDevolucao}"   />
						</td>
					</tr>
				</table>
			</t:div>
		</a4j:outputPanel>

			
		<table class="formulario" style="width: 100%; border-top: none;">
				<tr>
					<th class="obrigatorio" style="width:20%;">Nota:</th>
					<td colspan="3"><h:inputTextarea id="inputAreaNotaCirculacao" value="#{notasCirculacaoMBean.notaCirculacaoEdicao.nota}" rows="5" cols="75" onkeyup="textCounter(this, 'quantidadeCaracteresDigitados', 200);" /></td>
				</tr>
	
				<tr>
					<th colspan="2" style="width: 40% " >Caracteres Restantes:</th>
					<td colspan="2">
						<span id="quantidadeCaracteresDigitados">${200 - fn:length(notasCirculacaoMBean.notaCirculacaoEdicao.nota)}</span>/200
					</td>
				</tr>
				
	
				<tfoot>
					<tr>
						<td colspan="6" align="center">
							
							<ufrn:checkRole  papeis="<%= new int[] { SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO, SigaaPapeis.BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO
										, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO} %>">
								<h:commandButton id="cmdButtonIncluirNota" value="Editar Nota" action="#{notasCirculacaoMBean.editarNota}" />
							</ufrn:checkRole>
							
							<h:commandButton id="cmdButtonVoltarIncluirNota" value="Cancelar " action="#{notasCirculacaoMBean.voltarTelaBusca}" onclick="#{confirm}" immediate="true" />

						</td>
					</tr>
				</tfoot>
			</table>

		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp" %>
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