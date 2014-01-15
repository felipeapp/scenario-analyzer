<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>Portal do Gestor Lato Sensu</h2>

<c:set var="hideSubsistema" value="true" />

<h:form id="menuLatoForm">
<input type="hidden" name="aba" id="aba"/>

<div id="operacoes-subsistema" class="reduzido">
	<c:if test="${ acesso.lato }">
		<div id="curso" class="aba">
		    <%@include file="/WEB-INF/jsp/ensino/latosensu/menu/curso.jsp"%>
		</div>
		<div id="aluno" class="aba">
			<%@include file="/WEB-INF/jsp/ensino/latosensu/menu/discente.jsp"%>
		</div>
		<div id="turma" class="aba">
			<%@include file="/WEB-INF/jsp/ensino/latosensu/menu/turma.jsp"%>
		</div>
		<div id="relatorios" class="aba">
			<%@include file="/WEB-INF/jsp/ensino/latosensu/menu/relatorios.jsp"%>
		</div>
		<div id="administracao" class="aba">
			<%@include file="/WEB-INF/jsp/ensino/latosensu/menu/administracao.jsp"%>
		</div>
	</c:if>
</div>

<script>
var Abas = function() {
	return {
	    init : function(){
	        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
	        <c:if test="${ acesso.lato }">
		        abas.addTab('curso', "Curso");
				abas.addTab('aluno', "Aluno")
				abas.addTab('turma', "Turma")
				abas.addTab('relatorios', "Relatórios")
				abas.addTab('administracao', "Administração")
				<c:if test="${empty sessionScope.aba}">
		        	abas.activate('curso');
		   	 	</c:if>
		    	<c:if test="${sessionScope.aba != null}">
		    		abas.activate('${sessionScope.aba}');
		    	</c:if>
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
