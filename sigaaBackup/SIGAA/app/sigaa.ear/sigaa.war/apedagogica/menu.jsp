<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<% CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.GESTOR_PAP} ); %>

<c:set var="hideSubsistema" value="true" />

<f:view>

<h:form>

	<input type="hidden" name="aba" id="aba"/>
	<h2>Menu do Programa de Atualiza��o Pedag�gica</h2>

	<div id="operacoes-subsistema" class="reduzido" >
		<div id="geral" class="aba">
		 	<%@include file="geral.jsp"%>
		</div>
	</div>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
        <%-- Configura��o das abas --%>
        abas.addTab('geral', "Geral");
		<%-- Ativa��o das abas --%>
	    <c:if test="${sessionScope.aba != null}">
	    	abas.activate('${sessionScope.aba}');
	    </c:if>
	    <c:if test="${sessionScope.aba == null}">
	    	abas.activate('geral');
	    </c:if>
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
