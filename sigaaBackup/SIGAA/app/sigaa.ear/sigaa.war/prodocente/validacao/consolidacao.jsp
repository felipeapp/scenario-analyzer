<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/paineis/painel_detalhar_pd.js"></script>

<style>
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
	</f:subview>	
	
	<h2><ufrn:subSistema /> &gt; Consolidação da Auto-Validação de Produções Intelectuais</h2>
	<h:messages showDetail="true" />
	
	<div class="descricaoOperacao">
		<p> 
			Verifique as produções validadas pelo docente em questão e confirme a consolidação no final da página.
		</p>
	</div>
	
	<h:form>
	<table class="listagem">
		<caption> Produções a consolidar </caption>
		<c:set var="tipo" />
		<c:forEach var="item" items="#{consolidacaoProducaoBean.mapaProducoes}">
			<%-- Servidor --%>
			<tr>
				<td class="subFormulario" colspan="2"> ${item.key.nome} </td>
			</tr>
			
			<c:forEach var="prod" items="#{item.value}" varStatus="loop"> 
				<%-- Tipo da Produção --%>
				<c:if test="${tipo != prod.tipoProducao.id}" >
					<c:set var="tipo" value="${prod.tipoProducao.id}" />
					<tr class="tipoProducao">
						<td colspan="2"> ${prod.tipoProducao}</td>
					</tr>
				</c:if>
				<%-- Produção --%>
				<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar' }">
					<td class="ano"> ${prod.anoReferencia} </td>
					<td class="producao">
						<a href="#" onclick="exibirPainel(${prod.id})">
							${prod.titulo}
						</a> 
					</td>						
				</tr>
			</c:forEach>
			<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="Consolidar" action="#{consolidacaoProducaoBean.confirmar}" style="margin: 2px;"/>	
					<h:commandButton value="Voltar" action="#{consolidacaoProducaoBean.listarPendentes}" style="margin: 2px;"/>	
				</td>
			</tr>
			</tfoot>
		</c:forEach>
	</table>
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