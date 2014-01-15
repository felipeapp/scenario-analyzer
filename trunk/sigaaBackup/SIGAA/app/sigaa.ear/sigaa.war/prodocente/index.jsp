<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<c:set var="hideSubsistema" value="true" />
<f:view>
  <h:form>
	
	<h2>Produção Intelectual</h2>
<input type="hidden" name="aba" id="aba"/>
	<div id="operacoes-subsistema" class="reduzido" >
			<div id="prograd" class="aba">
			 	<%@include file="/prodocente/abas/proGrad.jsp"%>
			</div>
			<div id="prh" class="aba">
			 	<%@include file="/prodocente/abas/prh.jsp"%>
			</div>
			<div id="proex" class="aba">
			 	<%@include file="/prodocente/abas/proex.jsp"%>
			</div>
			<div id="pos" class="aba">
			 	<%@include file="/prodocente/abas/pos_graduacao.jsp"%>
			</div>
			<div id="propesq" class="aba">
			 	<%@include file="/prodocente/abas/propesq.jsp"%>
			</div>
			<div id="relatorios" class="aba">
			 	<%@include file="/prodocente/abas/relatorios.jsp"%>
			</div>
			<div id="administracao" class="aba">
			 	<%@include file="/prodocente/abas/administracao.jsp"%>
			</div>
	</div>
<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
        abas.addTab('prograd', "ProGrad");
        abas.addTab('prh', "PRH");
        abas.addTab('proex', "PROEX");
        abas.addTab('pos', "Pós-Graduação");
        abas.addTab('propesq', "PROPESQ");
        abas.addTab('relatorios', "Relatórios");
        abas.addTab('administracao', "Administração");
        abas.activate('prh');
       	abas.activate('proex');
       	abas.activate('pos');
       	abas.activate('propesq');
       	abas.activate('relatorios');
       	abas.activate('administracao');
       	abas.activate('prograd');
   		abas.activate('${sessionScope.aba}');
    }
};
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