<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<c:set var="confirmVoltar" value="if (!confirm('Ao voltar para a página anterior os dados digitados nessa página serão perdidos. Continuar?')) return false" scope="request" />
<c:set var="confirmRemover" value="if (!confirm('Confirma a remoção desta informação?')) return false" scope="request" />



<script type="text/javascript" src="/sigaa/javascript/biblioteca/formulario_catalogacao.js"> </script>


<link rel="stylesheet" media="all" href="/sigaa/css/biblioteca/formulario_catalogacao.css" type="text/css"/>
<link rel="stylesheet" media="all" href="/sigaa/css/biblioteca/estilo_botoes_pequenos.css" type="text/css"/>



<f:view>
	
	
	<c:if test="${catalogacaoMBean.tipoCatalogacaoBibliografica}">
		<h2>  <ufrn:subSistema /> &gt; Catalogação &gt; Página Gerenciamento Títulos no formato MARC</h2>
	</c:if>
	
	<c:if test="${catalogacaoMBean.tipoCatalogacaoAutoridade}">
		<h2>  <ufrn:subSistema /> &gt; Catalogação &gt; Página Gerenciamento Autoridades no formato MARC</h2>
	</c:if>
	
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	
	<div class="descricaoOperacao"> 
	    <p>Página para edição dos campos MARC.</p>
	    <p>Ao adicionar campos de dados, eles são adicionados vazios, então digite a etiqueta do campo desejado no lugar apropriado 
	    para preenchê-los.</p>
	    <p>Os campos de controle são adicionados em suas próprias páginas, pois cada um segue regras diferentes. </p> 
	    <p>Se quiser alterar os dados de algum campo de controle, clique em cima da etiqueta dele.</p> 
	    <br/> 
	    <p><strong>Arquivo Digital</strong> é algum arquivo em formato PDF, caso o Título possua um, que permita ao usuário visualizar os dados do mesmo no formato em ele que foi impresso.</p>
	    <p>As alterações no Arquivo Digital serão confirmadas ao salvar ou atualizar o Título.</p>
	    <p>É possível também fazer uma referência a um arquivo digital que esteja disponível em outro sistema, colocando seu endereço
	    eletrônico no campo <strong>856$a</strong> ou <strong>856$u</strong>.</p>
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
			<h:graphicImage value="/img/associado_pequeno.png" style="overflow: visible;" />: Associar Catalogação à Defesa
			<h:graphicImage value="/img/associado_pequeno_excluir.png" style="overflow: visible;" />: Remover Associação entre Catalogação e Defesa
		</c:if>
		
		<br/>
		<h:graphicImage value="/img/biblioteca/campo_cima.png" style="overflow: visible;" />: Mover o Campo para Cima
		<h:graphicImage value="/img/biblioteca/campo_baixo.png" style="overflow: visible;" />: Mover o Campo para Baixo
		<h:graphicImage value="/img/biblioteca/subcampo_cima.png" style="overflow: visible;" />: Mover o  Sub Campo para Cima
		<h:graphicImage value="/img/biblioteca/subcampo_baixo.png" style="overflow: visible;" />: Mover o Sub Campo para Baixo
		
	</div> 
	
	
	
	<%-- Quando a página de pesquisa de autoridades chama a edição, caso o usuário clique em voltar é necessário guardar --%>
	<c:if test="${catalogacaoMBean.tipoCatalogacaoAutoridade}">
		<a4j:keepAlive beanName="catalogaAutoridadesMBean"></a4j:keepAlive>
	</c:if>
	
	<%-- Usado quando o usuário quer editar as informações de um título, se voltar para a tela de pesquisa necessica quardar essa informação --%>
	<a4j:keepAlive beanName="pesquisaTituloCatalograficoMBean"></a4j:keepAlive>
	
	<a4j:keepAlive beanName="catalogacaoMBean"></a4j:keepAlive>
	
	<%-- Para manter os dados quando é importado vários título ou autoridades --%>
	<a4j:keepAlive beanName="buscaCatalogacoesIncompletasMBean"></a4j:keepAlive>
	
	<%-- Para manter os dados da consulta de defesa, quando a catalogação é a partir de uma defesa de tese. --%>
	<a4j:keepAlive beanName="consultarDefesaMBean"></a4j:keepAlive>
	
	
	
	
	<h:form id="fromDadosCatalograficos" enctype="multipart/form-data">

	
		<a4j:status onstart="modelPanelAguarde.show();" onstop="modelPanelAguarde.hide();"></a4j:status>

	
		<%-- Manda uma requisiçao para salva o título automaticamente e não expirar a sessão do servidor enquanto o usuário estiver na página de catalogação 
		
		Retirando por enquanto, as comandlink estão parando de funcinar quando o poll realiza a operação
		<a4j:poll id="poll" interval="#{catalogacaoMBean.tempoSalvamentoCatalogacacao}" enabled="#{catalogacaoMBean.tempoSalvamentoCatalogacacao > 0}" reRender="fromDadosCatalograficos" 
			ajaxSingle="false" actionListener="#{catalogacaoMBean.salvaCatalogacaoAutomaticamente}"></a4j:poll> 
		--%>
	
	
		<input id="NumeroSistemaAlterando" type="hidden" value="${catalogacaoMBean.obj.numeroDoSistema}" name="NumeroSistemaAlterando" /> <%-- Para ficar fácil identificar qual a catalogação que o usuário está alterando no momento, caso ocorra algum erro --%>
	
	
	
	
		<t:div id="divGeralFormCatalogacao" style="width: 100%;">
		
		
		
		
			<%-- Div que contém um menu lateral com algumas opções da tela de catalogação. É o que aparece à esqueda da tela --%>
			
			<%@include file="/biblioteca/processos_tecnicos/catalogacao/painelLateralFormCatalogacao.jsp"%>
			
			
			<%-- Div que contém o painel de configuração da página acima da planilha de catalogação --%>
			<%@include file="/biblioteca/processos_tecnicos/catalogacao/painelConfiguracoesFormCatalogacao.jsp"%>
		
		
		
		
		
		
			<%-- Div onde o bibliotecário vai realizar a catalogação, contém o formulário com os campos e sub campos da página, é o div que fica a direita --%>
			
			<t:div id="divDadoCatalogacao"  style="#{catalogacaoMBean.exibirPainelLateral ?  'width: 80%;' : 'width: 100%;'}   clear:right; float: right;">
				
				<t:div id="divDadosCatalogacaoCompleta" rendered="#{catalogacaoMBean.usarTelaCatalogacaoCompleta}">
					<%@include file="/biblioteca/processos_tecnicos/catalogacao/telaCatalogacaoCompleta.jsp"%>
				</t:div>
				
				<t:div id="divDadosCatalogacaoSimplificada" rendered="#{! catalogacaoMBean.usarTelaCatalogacaoCompleta}">
					<%@include file="/biblioteca/processos_tecnicos/catalogacao/telaCatalogacaoSimplificada.jsp"%>	
				</t:div>
					
			</t:div> <%-- divDadoCatalogacao Div onde está contido a planilha com os campos marc é o div central  --%>




		</t:div>  <%-- divGeralFormCatalogacao --%>




		<%-- Observação: Tem que ficar fora do div geral para o ocultar/mostrar painel lateral funcionar --%>
		<%@include file="/biblioteca/processos_tecnicos/catalogacao/modelPanelsFormCatalogacao.jsp"%>


	</h:form>
	   	   
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

			
