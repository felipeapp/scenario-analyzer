<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2>Escolas Especializadas</h2>

<c:set var="hideSubsistema" value="true" />

<h:form id="menuTecnicoForm">
<input type="hidden" name="aba" id="aba"/>

<div id="operacoes-subsistema" class="reduzido">
	<c:if test="${ acesso.formacaoComplementar }">
		<div id="curso" class="aba">
		    <%@include file="/ensino/formacao_complementar/menus/curso.jsp"%>
		</div>
		<div id="aluno" class="aba">
			<%@include file="/ensino/formacao_complementar/menus/discente.jsp"%>
		</div>
		<div id="turma" class="aba">
			<%@include file="/ensino/formacao_complementar/menus/turma.jsp"%>
		</div>
		<%--
		<div id="relatorios" class="aba">
			<%@include file="/ensino/formacao_complementar/menus/relatorios.jsp"%>
		</div>
		--%>
	</c:if>
	<%--
	<c:if test="${ acesso.coordenadorCursoTecnico }">
		<div id="coordenacao" class="aba">
			<%@include file="/ensino/formacao_complementar/menus/coordenacao.jsp"%>
		</div>
	</c:if>
	--%>
</div>

<script><!--
var Abas = function() {
	return {
	    init : function(){
	        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
	        <c:if test="${ acesso.formacaoComplementar }">
		       	abas.addTab('curso', "Curso");
				abas.addTab('aluno', "Aluno")
				abas.addTab('turma', "Turma")
				
			   	<c:if test="${empty sessionScope.aba}">
		        	abas.activate('aluno');
		   	 	</c:if>
			   	<c:if test="${empty sessionScope.aba}">
		        	abas.activate('curso');
		   	 	</c:if>
		    	<c:if test="${sessionScope.aba != null}">
		    		abas.activate('${sessionScope.aba}');
		    	</c:if>
			</c:if>
	    }
    }
}();
YAHOO.ext.EventManager.onDocumentReady(Abas.init, Abas, true);
function setAba(aba) {
	document.getElementById('aba').value = aba;
}
--></script>

</h:form>

<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
