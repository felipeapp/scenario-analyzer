<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<link rel="stylesheet" media="all" href="/sigaa/css/tecnico/coordenacao.css" type="text/css" />
<f:view>
<h:form id="menuTecnicoForm">
<h2>
	<ufrn:subSistema />
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
	<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.GESTOR_METROPOLE_DIGITAL} %>">
		<c:if test="${ !acesso.secretarioTecnico }">
			<div id="curso" class="aba">
			    <%@include file="curso.jsp"%>
			</div>
		</c:if>
		<div id="aluno" class="aba">
			<%@include file="discente.jsp"%>
		</div>
		<div id="turma" class="aba">
			<%@include file="turma.jsp"%>
		</div>
		<div id="relatorios" class="aba">
			<%@include file="relatorios.jsp"%>
		</div>
	</ufrn:checkRole>
	
	<ufrn:checkRole papeis="<%=new int[] { SigaaPapeis.GESTOR_METROPOLE_DIGITAL }%>">
		<ufrn:checkNotRole papeis="<%=new int[] { SigaaPapeis.GESTOR_METROPOLE_DIGITAL}%>">
		<div id="coordenacao" class="aba">
			<%@include file="coordenacao.jsp"%>
		</div>
		
		<div id="aluno" class="aba">
			<ul>
				<li>Registro de Atividades
					<ul>
						<li><h:commandLink id="registroAtividadeiniciarMatricula"
								action="#{registroAtividade.iniciarMatricula}"
								value="Matricular" onclick="setAba('aluno')" /></li>
						<li><h:commandLink
								id="registroAtividadeiniciarConsolidacao"
								action="#{registroAtividade.iniciarConsolidacao}"
								value="Consolidar" onclick="setAba('aluno')" /></li>
						<li><h:commandLink id="registroAtividadeiniciarValidacao"
								action="#{registroAtividade.iniciarValidacao}"
								value="Validar" onclick="setAba('aluno')" /></li>
						<li><h:commandLink id="registroAtividadeiniciarExclusao"
								action="#{registroAtividade.iniciarExclusao}" value="Excluir"
								onclick="setAba('aluno')" /></li>
					</ul>
				</li>
			</ul>
		</div>
		</ufrn:checkNotRole>	
	</ufrn:checkRole>


 	<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_IMD, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_IMD, SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO} %>">
 		<div id="importacao" class="aba">
 			<%@include file="importacao.jsp"%> 
		</div> 
 	</ufrn:checkRole> 
 	 	
 	<ufrn:checkRole papeis="<%=new int[] { SigaaPapeis.ASSISTENTE_SOCIAL_IMD }%>">
 		<div id="ass_social" class="aba">
 			<%@include file="ass_social.jsp"%> 
		</div> 
 	</ufrn:checkRole> 
 	
 	<ufrn:checkRole papeis="<%=new int[] { SigaaPapeis.COORDENADOR_POLO_IMD }%>">
 		<div id="coordenador_polo" class="aba">
 			<%@include file="coordenador_polo.jsp"%> 
		</div> 
 	</ufrn:checkRole> 
 	
 	<ufrn:checkRole papeis="<%=new int[] { SigaaPapeis.COORDENADOR_TUTOR_IMD }%>">
 		<div id="coordenador_tutor" class="aba">
 			<%@include file="coordenador_tutor.jsp"%> 
		</div> 
 	</ufrn:checkRole> 
	
</div>

<script>
var Abas = function() {
	return {
	    init : function(){
	        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
	        <ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.GESTOR_METROPOLE_DIGITAL } %>">
	        	<c:if test="${ !acesso.secretarioTecnico }">
		       		abas.addTab('curso', "Curso");
		        </c:if>
				abas.addTab('aluno', "Aluno")
				abas.addTab('turma', "Turma")
				abas.addTab('relatorios', "Relat�rios")
			
				
				<c:if test="${empty sessionScope.aba}">
		        	abas.activate('aluno');
		   	 	</c:if>
			   	 <c:if test="${empty sessionScope.aba}">
		        	abas.activate('curso');
		   	 	</c:if>
			   	 <c:if test="${empty sessionScope.aba}">
		        	abas.activate('curso');
		   	 	</c:if>
	    	
		    	<c:if test="${sessionScope.aba != null}">
		    		abas.activate('${sessionScope.aba}');
		    	</c:if>
			</ufrn:checkRole>
			
			<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.ASSISTENTE_SOCIAL_IMD } %>">
				abas.addTab('ass_social', "Assist�ncia Social");
				abas.activate('ass_social');
			</ufrn:checkRole>
			
			<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.COORDENADOR_POLO_IMD } %>">
				abas.addTab('coordenador_polo', "Coordena��o de P�lo");
				abas.activate('coordenador_polo');
			</ufrn:checkRole>
			
			<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.COORDENADOR_TUTOR_IMD } %>">
				abas.addTab('coordenador_tutor', "Coordena��o de Tutores");
				abas.activate('coordenador_tutor');
			</ufrn:checkRole>
		
			<c:if test="${sessionScope.aba != null}">
	    		abas.activate('${sessionScope.aba}');
	    	</c:if>
			
	
			<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.GESTOR_METROPOLE_DIGITAL } %>">
				<ufrn:checkNotRole papeis="<%=new int[] { SigaaPapeis.GESTOR_METROPOLE_DIGITAL}%>">
					abas.addTab('coordenacao', "Coordena��o");
					abas.addTab('aluno', "Aluno");
					<c:if test="${empty sessionScope.aba}">
						abas.activate('aluno');
						abas.activate('coordenacao');
		   	 		</c:if>
		    		
		    	</ufrn:checkNotRole>
		    </ufrn:checkRole>
		    
		    <ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.PEDAGOGICO_TECNICO } %>">
				abas.addTab('consultas', "Consultas");
				abas.activate('consultas');
		    </ufrn:checkRole>
			

			<ufrn:checkRole	papeis="<%= new int[] { SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_IMD, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_IMD, SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO } %>">
				abas.addTab('importacao', "Cadastramento");
				
				<c:if test="${sessionScope.aba == null}">
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