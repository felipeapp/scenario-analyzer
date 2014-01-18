<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" media="all" href="/sigaa/css/tecnico/coordenacao.css" type="text/css" />
<f:view>
<h:form id="menuTecnicoForm">
<h2>
	Ensino Técnico 
	<c:if test="${ acesso.coordenadorCursoTecnico && fn:length(curso.allCursosCoordenacaoNivelCombo) > 0 }">
		<h:selectOneMenu styleClass="comboCursos" value="#{ curso.cursoAtualCoordenacao.id }" 
			valueChangeListener="#{curso.trocarCurso}" 
			onchange="submit()">
	 		<f:selectItems value="#{curso.allCoordenadorCombo}"/>
		</h:selectOneMenu>
	</c:if>
</h2>

<c:set var="hideSubsistema" value="true" />


<input type="hidden" name="aba" id="aba"/>


<div id="operacoes-subsistema" class="reduzido">
	<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.GESTOR_TECNICO } %>">
		<c:if test="${ !acesso.secretarioTecnico }">
			<div id="curso" class="aba">
			    <%@include file="/WEB-INF/jsp/ensino/tecnico/menu/curso.jsp"%>
			</div>
		</c:if>
		<div id="aluno" class="aba">
			<%@include file="/WEB-INF/jsp/ensino/tecnico/menu/discente.jsp"%>
		</div>
		<div id="turma" class="aba">
			<%@include file="/WEB-INF/jsp/ensino/tecnico/menu/turma.jsp"%>
		</div>
		<div id="relatorios" class="aba">
			<%@include file="/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp"%>
		</div>
	</ufrn:checkRole>
	<ufrn:checkRole papeis="<%=new int[] { SigaaPapeis.COORDENADOR_TECNICO }%>">
		<ufrn:checkNotRole papeis="<%=new int[] { SigaaPapeis.GESTOR_TECNICO}%>">
		<div id="coordenacao" class="aba">
			<%@include file="/WEB-INF/jsp/ensino/tecnico/menu/coordenacao.jsp"%>
		</div>
		
		</ufrn:checkNotRole>
		
	</ufrn:checkRole>

	<ufrn:checkRole
		papeis="<%=new int[] { SigaaPapeis.PEDAGOGICO_TECNICO }%>">
		<div id="pedagogico" class="aba"><%@include file="/WEB-INF/jsp/ensino/tecnico/menu/pedagogico.jsp"%></div>
		<div id="relatorios" class="aba"><%@include file="/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp"%></div>
	</ufrn:checkRole>

	<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO } %>">
		<div id="importacao" class="aba">
			<%@include file="/WEB-INF/jsp/ensino/tecnico/menu/importacao.jsp"%>
		</div>
	</ufrn:checkRole>
	
</div>

<script>
var Abas = function() {
	return {
	    init : function(){
	        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
	        <ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.GESTOR_TECNICO } %>">
	        	<c:if test="${ !acesso.secretarioTecnico }">
		       		abas.addTab('curso', "Curso");
		        </c:if>
				abas.addTab('aluno', "Aluno")
				abas.addTab('turma', "Turma")
				abas.addTab('relatorios', "Relatórios")
				<c:if test="${empty sessionScope.aba}">
		        	abas.activate('aluno');
		   	 	</c:if>
			   	 <c:if test="${empty sessionScope.aba}">
		        	abas.activate('curso');
		   	 	</c:if>
		    	<c:if test="${sessionScope.aba != null}">
		    		abas.activate('${sessionScope.aba}');
		    	</c:if>
			</ufrn:checkRole>
			
			<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.COORDENADOR_TECNICO } %>">
				<ufrn:checkNotRole papeis="<%=new int[] { SigaaPapeis.GESTOR_TECNICO}%>">
					abas.addTab('coordenacao', "Coordenação");
					<c:if test="${empty sessionScope.aba}">
						abas.activate('coordenacao');
		   	 		</c:if>
		    		<c:if test="${sessionScope.aba != null}">
			    		abas.activate('${sessionScope.aba}');
			    	</c:if>
		    	</ufrn:checkNotRole>
		    </ufrn:checkRole>
		    
		    <ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.PEDAGOGICO_TECNICO } %>">
			    abas.addTab('pedagogico', "Acompanhamento Pedagógico")
				abas.addTab('relatorios', "Relatórios")
				abas.activate('pedagogico');
		    </ufrn:checkRole>
			
			<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO } %>">
				abas.addTab('importacao', "Cadastros IMD");
				<c:if test="${empty sessionScope.aba}">
					abas.activate('importacao');
		   	 	</c:if>
	   	 	</ufrn:checkRole>
	    	<c:if test="${sessionScope.aba != null}">
	    		abas.activate('${sessionScope.aba}');
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

<div class="linkRodape">
    <html:link action="/verMenuPrincipal">Menu Principal</html:link>
</div>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
