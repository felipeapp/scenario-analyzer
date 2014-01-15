<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<%
	CheckRoleUtil.checkRole(request, response,
			new int[] { SigaaPapeis.VESTIBULAR });
%>
<c:set var="hideSubsistema" value="true" />

<f:view>

	<h:form>
		<input type="hidden" name="aba" id="aba" />
		<h2>Vestibular</h2>

		<div id="operacoes-subsistema" class="reduzido">
			<div id="candidatos" class="aba"><%@include file="/vestibular/menus/candidatos.jsp"%></div>
			<div id="cadastros" class="aba"><%@include file="/vestibular/menus/cadastros.jsp"%></div>
			<div id="fiscais" class="aba"><%@include file="/vestibular/menus/fiscal.jsp"%></div>
			<ufrn:checkRole papel="<%=SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS%>">
				<div id="importacao" class="aba"><%@include file="/vestibular/menus/importacao.jsp"%></div>
			</ufrn:checkRole>
		</div>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
        <c:if test="${acesso.vestibular}">
        	abas.addTab('candidatos', "Candidatos");
	        abas.addTab('cadastros', "Cadastros");
	        abas.addTab('fiscais', "Fiscais");
	        <ufrn:checkRole papel="<%=SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS%>">
	        	abas.addTab('importacao', "Importação/Convocação");
        	</ufrn:checkRole>
        </c:if>
        <c:if test="${empty sessionScope.aba}">
	        abas.activate('candidatos');
	    </c:if>
	    <c:if test="${sessionScope.aba != null}">
	    	abas.activate('${sessionScope.aba}');
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

<div class="linkRodape"><html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
