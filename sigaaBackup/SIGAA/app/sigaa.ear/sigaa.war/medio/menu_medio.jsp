<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<c:set var="hideSubsistema" value="true" />

<f:view>
<h:form id="menuMedioForm">
	<input type="hidden" name="aba" id="aba"/>
	<h2><ufrn:subSistema /></h2>

	<div id="operacoes-subsistema" class="reduzido">
		
		<c:if test="${ acesso.medio && !acesso.pedagogico }">
			<c:if test="${ !acesso.secretarioMedio }">
				<div id="curso" class="aba">
				    <%@include file="/medio/menus/curso.jsp"%>
				</div>
			</c:if>
			<div id="aluno" class="aba">
				<%@include file="/medio/menus/discente.jsp"%>
			</div>
			<div id="turma" class="aba">
				<%@include file="/medio/menus/turma.jsp"%>
			</div>
			<div id="relatorios" class="aba">
				<%@include file="/medio/menus/relatorios.jsp"%>
			</div>
		</c:if>
		<c:if test="${acesso.pedagogico }">
			<div id="pedagogico" class="aba">
				<%@include file="/medio/menus/pedagogico.jsp"%>
			</div>
			<div id="relatorios" class="aba">
				<%@include file="/medio/menus/relatorios.jsp"%>
			</div>
		</c:if>
	</div>

	<script>
		var Abas = function() {
			return {
			    init : function(){
			        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
			        <c:if test="${ acesso.medio && !acesso.pedagogico}">
			       		abas.addTab('aluno', "Aluno")
			        	abas.addTab('turma', "Turma")	
		        		<c:if test="${ !acesso.secretarioMedio }">
				       		abas.addTab('curso', "Curso");
				        </c:if>
						abas.addTab('relatorios', "Relatórios")
						
					   	<c:if test="${empty sessionScope.aba}">
				        	abas.activate('aluno');
				   	 	</c:if>
					    <c:if test="${sessionScope.aba != null}">
				    		abas.activate('${sessionScope.aba}');
				    	</c:if>
					</c:if>
					<c:if test="${acesso.pedagogico}">
						abas.addTab('pedagogico', "Acompanhamento Pedagógico")
						abas.addTab('relatorios', "Relatórios")
						abas.activate('pedagogico');
					</c:if>
		    	}	
	    	}
		}();
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