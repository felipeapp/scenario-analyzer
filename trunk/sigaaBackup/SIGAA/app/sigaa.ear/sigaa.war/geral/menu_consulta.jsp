<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<% CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.CONSULTADOR_ACADEMICO} ); %>

<c:set var="hideSubsistema" value="true" />
<f:view>

<h:form>
	<input type="hidden" name="aba" id="aba"/>
	<h2>Menu de Consultas</h2>

	<div id="operacoes-subsistema" class="reduzido" >
		<div id="consultas" class="aba">
			<%@include file="/graduacao/menus/consultas.jsp"%>
			<ul>
				<li> <a href="${ctx}/administracao/cadastro/Servidor/lista.jsf?aba=consultas">Consulta de Servidores</a></li>
			</ul>
		</div>
	</div>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
	    abas.addTab('consultas', "Consultas");
	    abas.activate('consultas');
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
