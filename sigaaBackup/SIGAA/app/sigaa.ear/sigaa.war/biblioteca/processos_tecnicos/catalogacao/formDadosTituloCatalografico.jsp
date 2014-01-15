<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<c:set var="confirmVoltar" value="if (!confirm('Ao voltar para a p�gina anterior os dados digitados nessa p�gina ser�o perdidos. Continuar?')) return false" scope="request" />
<c:set var="confirmRemover" value="if (!confirm('Confirma a remo��o desta informa��o?')) return false" scope="request" />



<script type="text/javascript" src="/sigaa/javascript/biblioteca/formulario_catalogacao.js"> </script>


<link rel="stylesheet" media="all" href="/sigaa/css/biblioteca/formulario_catalogacao.css" type="text/css"/>
<link rel="stylesheet" media="all" href="/sigaa/css/biblioteca/estilo_botoes_pequenos.css" type="text/css"/>



<f:view>
	
	
	<c:if test="${catalogacaoMBean.tipoCatalogacaoBibliografica}">
		<h2>  <ufrn:subSistema /> &gt; Cataloga��o &gt; P�gina Gerenciamento T�tulos no formato MARC</h2>
	</c:if>
	
	<c:if test="${catalogacaoMBean.tipoCatalogacaoAutoridade}">
		<h2>  <ufrn:subSistema /> &gt; Cataloga��o &gt; P�gina Gerenciamento Autoridades no formato MARC</h2>
	</c:if>
	
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	
	<div class="descricaoOperacao"> 
	    <p>P�gina para edi��o dos campos MARC.</p>
	    <p>Ao adicionar campos de dados, eles s�o adicionados vazios, ent�o digite a etiqueta do campo desejado no lugar apropriado 
	    para preench�-los.</p>
	    <p>Os campos de controle s�o adicionados em suas pr�prias p�ginas, pois cada um segue regras diferentes. </p> 
	    <p>Se quiser alterar os dados de algum campo de controle, clique em cima da etiqueta dele.</p> 
	    <br/> 
	    <p><strong>Arquivo Digital</strong> � algum arquivo em formato PDF, caso o T�tulo possua um, que permita ao usu�rio visualizar os dados do mesmo no formato em ele que foi impresso.</p>
	    <p>As altera��es no Arquivo Digital ser�o confirmadas ao salvar ou atualizar o T�tulo.</p>
	    <p>� poss�vel tamb�m fazer uma refer�ncia a um arquivo digital que esteja dispon�vel em outro sistema, colocando seu endere�o
	    eletr�nico no campo <strong>856$a</strong> ou <strong>856$u</strong>.</p>
	</div>
	
	
	<%--   Legenda das imagens      --%>
	<div class="infoAltRem">
	
		<t:div rendered="#{catalogacaoMBean.tipoCatalogacaoBibliografica}">
			<h:graphicImage value="/img/delete_old.gif" style="overflow: visible;"/>: Apagar Arquivo Digital
			<h:graphicImage value="/img/buscar.gif" style="overflow: visible;" />: Buscar Entrada Autorizada
			<h:graphicImage value="/img/primeira.gif" style="overflow: visible;" />: Inserir Valor no Sub Campo
		</t:div>
		
		<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />: Adicionar / Duplicar Sub Campo
		<h:graphicImage value="/img/add2.png" style="overflow: visible;" />: Duplicar Campo
		<h:graphicImage value="/img/prova_mes.png" style="overflow: visible;" />: Ajuda do Campo
		
		<br/>
		
		<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Campo
		<h:graphicImage value="/img/biblioteca/estornar.gif" style="overflow: visible;" />: Remover Sub Campo
		
		<c:if test="${catalogacaoMBean.tipoCatalogacaoBibliografica}">
			<h:graphicImage value="/img/associado_pequeno.png" style="overflow: visible;" />: Associar Cataloga��o � Defesa
			<h:graphicImage value="/img/associado_pequeno_excluir.png" style="overflow: visible;" />: Remover Associa��o entre Cataloga��o e Defesa
		</c:if>
		
		<br/>
		<h:graphicImage value="/img/biblioteca/campo_cima.png" style="overflow: visible;" />: Mover o Campo para Cima
		<h:graphicImage value="/img/biblioteca/campo_baixo.png" style="overflow: visible;" />: Mover o Campo para Baixo
		<h:graphicImage value="/img/biblioteca/subcampo_cima.png" style="overflow: visible;" />: Mover o  Sub Campo para Cima
		<h:graphicImage value="/img/biblioteca/subcampo_baixo.png" style="overflow: visible;" />: Mover o Sub Campo para Baixo
		
	</div> 
	
	
	
	<%-- Quando a p�gina de pesquisa de autoridades chama a edi��o, caso o usu�rio clique em voltar � necess�rio guardar --%>
	<c:if test="${catalogacaoMBean.tipoCatalogacaoAutoridade}">
		<a4j:keepAlive beanName="catalogaAutoridadesMBean"></a4j:keepAlive>
	</c:if>
	
	<%-- Usado quando o usu�rio quer editar as informa��es de um t�tulo, se voltar para a tela de pesquisa necessica quardar essa informa��o --%>
	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	
	<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
	<%-- Para manter os dados quando � importado v�rios t�tulo ou autoridades --%>
	<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>
	
	<%-- Para manter os dados da consulta de defesa, quando a cataloga��o � a partir de uma defesa de tese. --%>
	<a4j:keepAlive beanName="consultarDefesaMBean"></a4j:keepAlive>
	
	
	
	
	<h:form id="fromDadosCatalograficos" enctype="multipart/form-data">

	
		<a4j:status onstart="modelPanelAguarde.show();" onstop="modelPanelAguarde.hide();"></a4j:status>

	
		<%-- Manda uma requisi�ao para salva o t�tulo automaticamente e n�o expirar a sess�o do servidor enquanto o usu�rio estiver na p�gina de cataloga��o 
		
		Retirando por enquanto, as comandlink est�o parando de funcinar quando o poll realiza a opera��o
		<a4j:poll id="poll" interval="#{catalogacaoMBean.tempoSalvamentoCatalogacacao}" enabled="#{catalogacaoMBean.tempoSalvamentoCatalogacacao > 0}" reRender="fromDadosCatalograficos" 
			ajaxSingle="false" actionListener="#{catalogacaoMBean.salvaCatalogacaoAutomaticamente}"></a4j:poll> 
		--%>
	
	
		<input id="NumeroSistemaAlterando" type="hidden" value="${catalogacaoMBean.obj.numeroDoSistema}" name="NumeroSistemaAlterando" /> <%-- Para ficar f�cil identificar qual a cataloga��o que o usu�rio est� alterando no momento, caso ocorra algum erro --%>
	
	
	
	
		<t:div id="divGeralFormCatalogacao" style="width: 100%;">
		
		
		
		
			<%-- Div que cont�m um menu lateral com algumas op��es da tela de cataloga��o. � o que aparece � esqueda da tela --%>
			
			<%@include file="/biblioteca/processos_tecnicos/catalogacao/painelLateralFormCatalogacao.jsp"%>
			
			
			<%-- Div que cont�m o painel de configura��o da p�gina acima da planilha de cataloga��o --%>
			<%@include file="/biblioteca/processos_tecnicos/catalogacao/painelConfiguracoesFormCatalogacao.jsp"%>
		
		
		
		
		
		
			<%-- Div onde o bibliotec�rio vai realizar a cataloga��o, cont�m o formul�rio com os campos e sub campos da p�gina, � o div que fica a direita --%>
			
			<t:div id="divDadoCatalogacao"  style="#{catalogacaoMBean.exibirPainelLateral ?  'width: 80%;' : 'width: 100%;'}   clear:right; float: right;">
				
				<t:div id="divDadosCatalogacaoCompleta" rendered="#{catalogacaoMBean.usarTelaCatalogacaoCompleta}">
					<%@include file="/biblioteca/processos_tecnicos/catalogacao/telaCatalogacaoCompleta.jsp"%>
				</t:div>
				
				<t:div id="divDadosCatalogacaoSimplificada" rendered="#{! catalogacaoMBean.usarTelaCatalogacaoCompleta}">
					<%@include file="/biblioteca/processos_tecnicos/catalogacao/telaCatalogacaoSimplificada.jsp"%>	
				</t:div>
					
			</t:div> <%-- divDadoCatalogacao Div onde est� contido a planilha com os campos marc � o div central  --%>




		</t:div>  <%-- divGeralFormCatalogacao --%>




		<%-- Observa��o: Tem que ficar fora do div geral para o ocultar/mostrar painel lateral funcionar --%>
		<%@include file="/biblioteca/processos_tecnicos/catalogacao/modelPanelsFormCatalogacao.jsp"%>


	</h:form>
	   	   
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

			
