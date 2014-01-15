<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>Módulo de Pesquisa</h2>
<div id="operacoes-subsistema" class="reduzido">
	<c:if test="${ acesso.pesquisa }">
		<div id="projetos" class="aba">
		    <%@include file="/WEB-INF/jsp/pesquisa/menu/projetos.jsp"%>
		</div>
		<div id="iniciacao" class="aba">
			<%@include file="/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp"%>
		</div>
		<div id="relatorios" class="aba">
			<%@include file="/WEB-INF/jsp/pesquisa/menu/relatorio.jsp"%>
		</div>
		<div id="prodocente" class="aba">
			<%@include file="/WEB-INF/jsp/pesquisa/menu/prodocente.jsp"%>
		</div>
	</c:if>

	<c:if test="${ acesso.comissaoPesquisa }">
		<div id="comissao" class="aba">
			<%@include file="/WEB-INF/jsp/pesquisa/menu/comissao.jsp"%>
		</div>
	</c:if>
</div>
<script>
var Abas = function() {
	return {
	    init : function(){
	        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');

	        <c:if test="${ acesso.pesquisa }">
	        abas.addTab('projetos', "Projetos");
			abas.addTab('iniciacao', "Iniciação Científica")
			abas.addTab('relatorios', "Relatórios")
			abas.addTab('prodocente', "Produção Docente")
			abas.activate('projetos');
			</c:if>

			<c:if test="${ acesso.comissaoPesquisa }">
			abas.addTab('comissao', "Comissão de Pesquisa")
			abas.activate('comissao');
			</c:if>
	    }
    }
}();
YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
</script>

<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>