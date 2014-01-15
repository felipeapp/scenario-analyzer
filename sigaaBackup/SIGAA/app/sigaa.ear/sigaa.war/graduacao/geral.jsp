<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<% CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.SECRETARIA_CENTRO, SigaaPapeis.GESTOR_PROBASICA} ); %>

<c:set var="hideSubsistema" value="true" />
<f:view>

<h:form>

	<input type="hidden" name="aba" id="aba"/>
	<h2>Menu de Graduação</h2>

	<div id="operacoes-subsistema" class="reduzido" >
		<c:if test="${acesso.dae}">
			<div id="discente" class="aba">
			 	<%@include file="/graduacao/menus/aluno.jsp"%>
			</div>
			<div id="dae-programa" class="aba">
				<%@include file="/graduacao/menus/programa.jsp"%>
			</div>
		</c:if>
		<c:if test="${acesso.administradorDAE}">
			<div id="administracao" class="aba">
				<%@include file="/graduacao/menus/administracao.jsp"%>
			</div>
		</c:if>
		<c:if test="${acesso.dae or acesso.cdp}">
			<div id="consultas" class="aba">
				<%@include file="/graduacao/menus/consultas.jsp"%>
			</div>
			<div id="relatorios" class="aba">
				<%@include file="/graduacao/menus/relatorios_dae.jsp"%>
			</div>
		</c:if>
		<c:if test="${acesso.cdp}">
			<div id="cdp-cadastros" class="aba">
				<%@include file="/graduacao/menus/cdp.jsp"%>
			</div>
			<div id="cdp-relatorios" class="aba">
				<%@include file="/graduacao/menus/relatorios_cdp.jsp"%>
			</div>
		</c:if>
		<c:if test="${acesso.secretarioCentro || acesso.coordenacaoProbasica}">
			<div id="coordenacao" class="aba">
				<%@include file="/graduacao/menus/coordenacao.jsp"%>
			</div>
		</c:if>
	</div>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
        <%-- Configuração das abas --%>
        <c:if test="${acesso.dae}">
	        abas.addTab('discente', "Alunos");
    	    abas.addTab('dae-programa', "Matrículas e Programas");
        </c:if>
        <c:if test="${acesso.administradorDAE}">
        	abas.addTab('administracao', "Administração");
        </c:if>
        <c:if test="${acesso.dae or acesso.cdp}">
			abas.addTab('consultas', "Consultas");
			abas.addTab('relatorios', "Relatórios - ${ configSistema['siglaUnidadeGestoraGraduacao'] }");
		</c:if>
		<c:if test="${acesso.cdp}">
			abas.addTab('cdp-cadastros', "${ configSistema['siglaCDP'] }");
			abas.addTab('cdp-relatorios', "Relatórios - ${ configSistema['siglaCDP'] }");
		</c:if>
		<c:if test="${acesso.secretarioCentro || acesso.coordenacaoProbasica}">
			abas.addTab('coordenacao', "Coordenação Única");
		</c:if>
		<%-- Ativação das abas --%>
        <c:if test="${acesso.administradorDAE}">
        	abas.activate('administracao');
	    </c:if>		
		<c:if test="${acesso.secretarioCentro || acesso.coordenacaoProbasica}">
			abas.activate('coordenacao');
		</c:if>
        <c:if test="${empty sessionScope.aba and not acesso.secretarioCentro}">
	        abas.activate('discente');
	    </c:if>
	    <c:if test="${sessionScope.aba != null}">
	    	abas.activate('${sessionScope.aba}');
	    </c:if>
	    <c:if test="${sessionScope.aba == null and acesso.cdp}">
	    	abas.activate('cdp-cadastros');
	    </c:if>
	    <c:if test="${sessionScope.aba == null and acesso.dae}">
	    	abas.activate('discente');
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
