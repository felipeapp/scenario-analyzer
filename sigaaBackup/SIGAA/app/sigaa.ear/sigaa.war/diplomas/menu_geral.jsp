<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<% CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO, SigaaPapeis.GESTOR_DIPLOMAS_LATO, SigaaPapeis.GESTOR_DIPLOMAS_STRICTO} ); %>

<c:set var="hideSubsistema" value="true" />

<f:view>

<h:form>

	<input type="hidden" name="aba" id="aba"/>
	<h2>Menu de Diplomas</h2>

	<div id="operacoes-subsistema" class="reduzido" >
			<div id="registro" class="aba">
			 	<%@include file="/diplomas/menus/registro.jsp"%>
			</div>
			<%-- <ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO} %>">
				<div id="revalidacao" class="aba">
					<%@include file="/diplomas/menus/revalidacao.jsp"%>
				</div>
			</ufrn:checkRole> --%>
	</div>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
        <%-- Configuração das abas --%>
        abas.addTab('registro', "Registro/Impressão");
        <%-- <ufrn:checkRole papeis="<%= new int[] {	SigaaPapeis.GESTOR_DIPLOMAS_GRADUACAO} %>">
   	    	abas.addTab('revalidacao', "Revalidação");
  	    </ufrn:checkRole> --%>
		<%-- Ativação das abas --%>
	    <c:if test="${sessionScope.aba != null}">
	    	abas.activate('${sessionScope.aba}');
	    </c:if>
	    <c:if test="${sessionScope.aba == null}">
	    	abas.activate('registro');
	    </c:if>
    }
};

YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
</script>

	</h:form>
</f:view>

<div class="linkRodape">
	<html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
