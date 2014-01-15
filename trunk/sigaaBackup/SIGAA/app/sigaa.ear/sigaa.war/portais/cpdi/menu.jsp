<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<c:set var="hideSubsistema" value="true" />
<f:view>
	<h:form>
	<input type="hidden" name="aba" id="aba"/>

	<h2>Portal CPDI</h2>

	<div id="operacoes-subsistema" class="reduzido">
		<div id="departamento" class="aba">
			<%@include file="/portais/cpdi/abas/sitdepartamento.jsp"%>
		</div>
	</div>

	<script>
	var Abas = function() {
		return {
		    init : function(){
		        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
		        abas.addTab('departamento', "Departamento");
		    	abas.activate('departamento');
		    }
	    }
	}();
	YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
	function setAba(aba) {
		document.getElementById('aba').value = aba;
	}
	</script>

	</h:form>
</f:view>

<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>