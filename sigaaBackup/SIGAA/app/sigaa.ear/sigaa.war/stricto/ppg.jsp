<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<% CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.PPG, SigaaPapeis.MEMBRO_APOIO_DOCENCIA_ASSISTIDA} ); %>

<c:set var="hideSubsistema" value="true" />
<f:view>

<h:form prependId="false">
	<input type="hidden" name="aba" id="aba"/>
	<h2>Pró-Reitoria de Pós-Graduação</h2>

	<div id="operacoes-subsistema" class="reduzido" >
		<c:if test="${acesso.ppg}">
			<div id="discente" class="aba">
			 	<%@include file="/stricto/menus/discente.jsp"%>
			</div>
			<div id="matricula" class="aba">
			 	<%@include file="/stricto/menus/matricula.jsp"%>
			</div>
			<div id="cadastro" class="aba">
			 	<%@include file="/stricto/menus/cadastro.jsp"%>
			</div>
			<div id="permissao" class="aba">
			 	<%@include file="/stricto/menus/permissao.jsp"%>
			</div>
			<div id="relatorios" class="aba">
			 	<%@include file="/stricto/menus/relatorios.jsp"%>
			</div>
		</c:if>
		
		<c:if test="${acesso.ppg || acesso.membroApoioDocenciaAssistida}">
			<div id="reuni" class="aba">
			 	<%@include file="/stricto/menus/bolsas_reuni.jsp"%>
			</div>
		</c:if>
	</div>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
        <c:if test="${acesso.ppg}">
	        abas.addTab('discente', "Alunos");
	        abas.addTab('matricula', "Vínculo / Matrículas");
    	    abas.addTab('cadastro', "Cadastros");
    	    abas.addTab('permissao', "Permissões");
    	    abas.addTab('relatorios', "Relatórios e Consultas");
        </c:if>

	    abas.addTab('reuni', "Bolsas Docência Assistida");

	    <c:if test="${sessionScope.aba == null and acesso.membroApoioDocenciaAssistida}">
	    	abas.activate('reuni');
    	</c:if>	    
	    
        <c:if test="${empty sessionScope.aba}">
	        abas.activate('discente');
	    </c:if>
	    <c:if test="${sessionScope.aba != null}">
	    	abas.activate('${sessionScope.aba}');
	    </c:if>
	    <c:if test="${sessionScope.aba == null and acesso.ppg}">
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
