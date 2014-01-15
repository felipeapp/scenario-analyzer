<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/painel_detalhar_pd.js"></script>

<style>
	ol.instrucoes li{
		line-height: 1.5em;
	}


	table.listagem tr.tipoProducao td {
		font-weight: bold;
		background: #F5F5F5;
		border-bottom: 1px solid #DDD;
		padding-left: 1em;
	}
	
	table.listagem tr td.ano {
		padding-left: 2em;
		vertical-align: top;
	}
	
</style>

<f:view>
	<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp"%>
		<%@include file="/prodocente/include_painel.jsp"%>
	</f:subview>	
	
	<h2><ufrn:subSistema /> &gt; Validação de Bolsas de Produtividade</h2>
	<h:messages showDetail="true" />
	
	<div class="descricaoOperacao">
	   <h3>Instruções:</h3>
	   	<ol class="instrucoes">
	   		<li>Selecione o ano de referência desejado;</li>
	   		<li>Aguarde o carregamento das bolsas de produtividade pendentes de validação;</li>
	   		<li>Selecione as bolsas de produtividade que deseja validar; e</li>
	   		<li>Clique no botão <em>Confirmar</em> no final desta página para realizar a validação das bolsas de produtividade selecionadas.</li>
	   	</ol>
	</div>
	
	<h:form id="form">
	
	<div align="center" style="margin: 10px;">
		<a4j:region>
			Ano de Referência:
			<h:selectOneMenu id="ano" valueChangeListener="#{validacaoBolsaProdutividadeBean.listarPendentes}">
				<f:selectItem itemValue="0" itemLabel="--> SELECIONE UM ANO <--" />
				<f:selectItems value="#{validacaoBolsaProdutividadeBean.anos}"/>
				<a4j:support reRender="listaBolsas" event="onchange"/>
			</h:selectOneMenu> &nbsp;
			<a4j:status>
				<f:facet name="start">
					<h:graphicImage value="/img/ajax-loader.gif"/>
				</f:facet>
			</a4j:status>
		</a4j:region> 
	</div>
	
	<h:panelGroup id="listaBolsas">
	<table class="listagem">
		<caption> Bolsas de produtividade pendentes de validação </caption>
		
			<c:if test="${not empty validacaoBolsaProdutividadeBean.mapaProducoes}">
				<c:set var="tipo" />
				<c:forEach var="item" items="#{validacaoBolsaProdutividadeBean.mapaProducoes}">
					<%-- Servidor --%>
					<tr>
						<td class="subFormulario" colspan="3"> ${item.key.nome} </td>
					</tr>
					
					<c:forEach var="prod" items="#{item.value}" varStatus="loop"> 
						<%-- Tipo da Produção --%>
						<c:if test="${tipo != prod.tipoProducao.id}" >
							<c:set var="tipo" value="${prod.tipoProducao.id}" />
							<tr class="tipoProducao">
								<td colspan="3"> ${prod.tipoProducao}</td>
							</tr>
						</c:if>
						<%-- Produção --%>
						<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
							<td width="3%">
								<input type="checkbox" name="selecaoProducao" value="${prod.id}"
										id="${prod.id}" class="noborder"/>
							</td>
							<td class="ano"> ${prod.anoReferencia} </td>
							<td class="producao">
								<a4j:commandLink id="showItem" action="#{carregarDadosProducoesMBean.carregar}"
               						oncomplete="Richfaces.showModalPanel('view')" immediate="true" reRender="view">  
               						${prod.titulo}
               						<f:param name="id" value="#{prod.id}"/>
				    			</a4j:commandLink>  
							</td>						
						</tr>
					</c:forEach>
				</c:forEach>
			</c:if>
			<c:if test="${empty validacaoBolsaProdutividadeBean.mapaProducoes}">
				<tr>
				<td align="center">Não há bolsas de produtividade pendentes de validação para o ano selecionado.</td>
				</tr>
			</c:if>
	</table>
	</h:panelGroup>
		<div align="center" style="margin: 10px;">
			<h:commandButton value="Confirmar" action="#{validacaoBolsaProdutividadeBean.confirmar}" style="margin: 2px;"/>	
			<h:commandButton value="Cancelar" action="#{validacaoBolsaProdutividadeBean.cancelar}" style="margin: 2px;" onclick="#{confirm}"/>	
		</div>
	</h:form>
	
</f:view>

<script>
	function exibirPainel(id, idArquivo) {
		var getURL = function(t) {
			var url = t.responseText;
			PainelDetalhar.show(url, false, 600, 395);
		}
		// buscar url
		var ajax = new Ajax.Request('/sigaa/ajaxPreValidacaoProDoc',
		{
			method: 'get',
			parameters: 'id=' + id,
			onSuccess: getURL
		});
	}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>