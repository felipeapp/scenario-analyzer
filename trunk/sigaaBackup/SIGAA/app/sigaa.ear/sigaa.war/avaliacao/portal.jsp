<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<%
	CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.COMISSAO_AVALIACAO, SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL} );
%>

<c:set var="hideSubsistema" value="true" />
<f:view>
<h:form>
	<h2>Avaliação Institucional</h2>
	<input type="hidden" name="aba" id="aba"/>
	<div id="operacoes-subsistema" class="reduzido" >
		<ufrn:checkRole papel="<%=SigaaPapeis.COMISSAO_AVALIACAO%>">
			<div id="administracao" class="aba">
			 	<%@include file="/avaliacao/menus/administracao.jsp"%>
			</div>
			<div id="relatorios" class="aba">
				<%@include file="/avaliacao/menus/relatorios.jsp"%>
			</div>
		</ufrn:checkRole>
		<ufrn:checkRole papel="<%=SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL%>">
			<div id="bolsista" class="aba">
				<%@include file="/avaliacao/menus/bolsista.jsp"%>
			</div>
		</ufrn:checkRole>
	</div>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
        	<ufrn:checkRole papel="<%=SigaaPapeis.COMISSAO_AVALIACAO%>">
		        abas.addTab('administracao', "Administração");
	    	    abas.addTab('relatorios', "Relatórios e Consultas");
    	    </ufrn:checkRole>
    	    <ufrn:checkRole papel="<%=SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL%>">
    	    	abas.addTab('bolsista', "Avaliação Institucional");
    	    </ufrn:checkRole>
    	    <c:if test="${sessionScope.aba != null}">
	    		abas.activate('${sessionScope.aba}');
	    	</c:if>
	    	<c:if test="${sessionScope.aba == null}">
		    	abas.activate('administracao');
		    </c:if>
		    <ufrn:checkRole papel="<%=SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL%>">
		    	abas.activate('bolsista');
		    </ufrn:checkRole>
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