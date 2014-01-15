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
	<%@include file="../include_painel.jsp"%>
	<f:subview id="menu">
		<%@include file="/portais/docente/menu_docente.jsp"%>
	</f:subview>	
	
	<h2><ufrn:subSistema /> > Auto-Valida��o de Produ��es Intectuais</h2>
	<h:messages showDetail="true" />
	
	<div class="descricaoOperacao">
	   <h3>Instru��es:</h3>
	   	<ol class="instrucoes">
	   		<li>Certifique-se de que as informa��es das produ��es abaixo est�o corretas;</li>
	   		<li>Selecione as produ��es que deseja validar;</li>
	   		<li>Leia a declara��o no final desta p�gina;</li>
	   		<li>Registre sua concord�ncia com o termo da declara��o; e</li>
	   		<li>Clique no bot�o <em>Confirmar</em> no final desta p�gina para realizar a valida��o das produ��es selecionadas.</li>
	   	</ol>
	</div>
	
	<h:form>
	
	<div class="infoAltRem" style="width: 100%">
		<h:graphicImage value="/img/alterar.gif"style="overflow: visible;"/>: Alterar Produ��o
	</div>
	
	<table class="listagem">
		<caption> Produ��es pendentes de valida��o </caption>
		<c:set var="tipo" />
		<c:forEach var="item" items="#{autoValidacaoProducaoBean.mapaProducoes}">
			<%-- Servidor --%>
			<tr>
				<td class="subFormulario" colspan="4"> ${item.key.nome} </td>
			</tr>
			
			<c:forEach var="prod" items="#{item.value}" varStatus="loop"> 
				<%-- Tipo da Produ��o --%>
				<c:if test="${tipo != prod.tipoProducao.id}" >
					<c:set var="tipo" value="${prod.tipoProducao.id}" />
					<tr class="tipoProducao">
						<td colspan="4"> ${prod.tipoProducao}</td>
					</tr>
				</c:if>
				<%-- Produ��o --%>
				<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td width="3%">
						<input type="checkbox" name="selecaoProducao" value="${prod.id}"
								id="${prod.id}" class="noborder"/>
					</td>
					<td class="ano"> ${prod.anoReferencia} </td>
					<td class="producao">
						<a4j:commandLink id="showItem" action="#{carregarDadosProducoesMBean.carregar}"
		               		oncomplete="Richfaces.showModalPanel('view')" immediate="true" reRender="view">  
		               			<h:outputText value="#{prod.titulo}" escape="false" />
		               		<f:param name="id" value="#{prod.id}"/>
						</a4j:commandLink>  
					</td>
					<td>
						<h:commandLink action="#{autoValidacaoProducaoBean.atualizar}" >
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" title="Alterar Produ��o"/>
							<f:param name="id" value="#{prod.id}"/>
						</h:commandLink>
					</td>						
				</tr>
			</c:forEach>
			
		</c:forEach>
	</table>
	<div class="descricaoOperacao" style="width: 75%">
		<p style="line-height: 1.5em; margin: 1em 0.5em; font-size: 1.1em;">
			<h:selectBooleanCheckbox value="#{autoValidacaoProducaoBean.concordancia}" id="concordancia" />
			<h:outputLabel for="concordancia">
			O solicitante declara formalmente que est� de acordo com o Termo de
			Ades�o e Compromisso da Plataforma SIGAA e que responde pela veracidade de todas as
			informa��es contidas na sua produ��o acad�mica implantada no banco de dados
			institucional da ${ configSistema['siglaInstituicao'] }
			</h:outputLabel>
		</p>
		<p style="text-align: right;">
			<em>(Declara��o feita em observ�ncia aos artigos 297-299 do C�digo Penal Brasileiro).</em>
		</p>
		<div align="center" style="margin: 10px;">
			<h:commandButton value="Confirmar" action="#{autoValidacaoProducaoBean.confirmar}" style="margin: 2px;"/>	
			<h:commandButton value="Voltar" action="#{autoValidacaoProducaoBean.cancelar}" style="margin: 2px;"/>	
		</div>
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