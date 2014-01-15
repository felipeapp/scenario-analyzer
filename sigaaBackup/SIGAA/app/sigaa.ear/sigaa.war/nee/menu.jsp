<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<c:set var="hideSubsistema" value="true" />
<f:view>
	<h:form>
		<input type="hidden" name="aba" id="aba"/>
		<h2><ufrn:subSistema /> > Menu do NEE</h2>
	
		<div id="operacoes-subsistema" class="reduzido" >
			<div id="alunos" class="aba">
				<%@include file="/nee/aluno.jsp"%>
			</div>
			
			<div id="consultas" class="aba">
				<%@include file="/nee/consultas.jsp"%>
			</div>
			
			<div id="administracao" class="aba">
				<%@include file="/nee/administracao.jsp"%>
			</div>				
		</div>


	<script>
	var Abas = {
	    init : function(){
	        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
		    abas.addTab('alunos', "Aluno");
		    abas.addTab('consultas', "Relatórios / Consultas");
		    abas.addTab('administracao', "Administração");
		    <c:if test="${empty sessionScope.aba}">
	    	 	abas.activate('alunos');
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

<div class="linkRodape">
	<html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>