<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="hideSubsistema" value="true" />
<f:view>
<h2>Módulo de Pesquisa</h2>

<h:form id="menuPesquisaForm">
<input type="hidden" name="aba" id="aba"/>

<div id="operacoes-subsistema" class="reduzido">
	<ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.GESTOR_PESQUISA } %>">
		<div id="projetos" class="aba">
		    <%@include file="/WEB-INF/jsp/pesquisa/menu/projetos.jsp"%>
		</div>
		<div id="iniciacao" class="aba">
			<%@include file="/WEB-INF/jsp/pesquisa/menu/iniciacao.jsp"%>
		</div>
		<div id="consultores" class="aba">
			<%@include file="/WEB-INF/jsp/pesquisa/menu/consultores.jsp"%>
		</div>
		<div id="relatorios" class="aba">
			<%@include file="/WEB-INF/jsp/pesquisa/menu/relatorio.jsp"%>
		</div>
		<div id="prodocente" class="aba">
			<%@include file="/WEB-INF/jsp/pesquisa/menu/prodocente.jsp"%>
		</div>
		<div id="cadastros" class="aba">
			<%@include file="/WEB-INF/jsp/pesquisa/menu/auxiliares.jsp"%>
		</div>
	</ufrn:checkRole>

	<c:if test="${ acesso.nit }">
		<div id="inovacao" class="aba">
			<%@include file="/WEB-INF/jsp/pesquisa/menu/inovacao_empreendedorismo.jsp"%>
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
	        <ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.GESTOR_PESQUISA } %>">
		        abas.addTab('projetos', "Projetos");
				abas.addTab('iniciacao', "IC")
				abas.addTab('consultores', "Consultores/Comissão")
				abas.addTab('relatorios', "Relatórios")
				abas.addTab('prodocente', "Prod. Int.")
				abas.addTab('cadastros', "Cadastros")
				<c:if test="${empty sessionScope.aba}">
		        	abas.activate('projetos');
		   	 	</c:if>
		    	<c:if test="${sessionScope.aba != null}">
		    		abas.activate('${sessionScope.aba}');
		    	</c:if>
	    	</ufrn:checkRole>

			<c:if test="${ acesso.nit }">
				abas.addTab('inovacao', "Inovação")
				abas.activate('inovacao');
			</c:if>

			<c:if test="${ acesso.comissaoPesquisa }">
				abas.addTab('comissao', "Comissão de Pesquisa")
			</c:if>
	    }
    }
}();
YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
function setAba(aba) {
	document.getElementById('aba').value = aba;
}
</script>

</h:form>

<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>