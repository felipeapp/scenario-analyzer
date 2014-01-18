<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<c:set var="hideSubsistema" value="true" />

<f:view>
<h:form id="menuMedioForm">
	<input type="hidden" name="aba" id="aba"/>
	<h2><ufrn:subSistema /></h2>

	<div id="operacoes-subsistema" class="reduzido">
		
		<div id="aluno" class="aba">
			<ul>
				<li>Documentos
		        	<ul>
		        		<li> <h:commandLink id="emitirAtestadoMatriculaMedio" action="#{ atestadoMatriculaMedio.buscarDiscente }" value="Emitir Atestado de Matrícula" onclick="setAba('aluno')"/> </li>
		                <li> <h:commandLink id="emitirBoletim"	action="#{boletimMedioMBean.iniciar}" value="Emitir Boletim" onclick="setAba('aluno')"/></li>
		                <li> <h:commandLink id="emitirHistorico" action="#{historicoMedio.buscarDiscente}" value="Emitir Histórico" onclick="setAba('aluno')"/></li>
		             </ul>
		        </li>
		        <li> Transferência de Aluno entre Turmas
					<ul>
						<li> <h:commandLink action="#{transferenciaTurmaMedioMBean.iniciarAutomatica}" value="Transferência Automática" onclick="setAba('aluno')"/> </li>
						<li> <h:commandLink action="#{transferenciaTurmaMedioMBean.iniciarManual}" value="Transferência Manual" onclick="setAba('aluno')"/> </li>
					</ul>
				</li>
				<li>Matrícula
		           <ul>
		           	<li> <h:commandLink id="matricularAluno" action="#{matriculaMedio.iniciarMatriculaDiscente}" value="Matricular Aluno em Série" onclick="setAba('aluno')"/> </li>
		           	<li> <h:commandLink id="matriculaCompulsoria" action="#{matriculaCompulsoriaMedio.iniciarMatriculaCompulsoria}" value="Matrícula Compulsória" onclick="setAba('aluno')"/> </li>
		           	<li> <h:commandLink id="matricularAlunoDependencia" action="#{matriculaMedio.iniciarMatriculaDependencia}" value="Matricular Aluno em Dependência" onclick="setAba('aluno')"/> </li>
		               <li> <h:commandLink id="alterarStatusMatricula" action="#{alteracaoStatusMatriculaMedioMBean.iniciar}" value="Alterar Status de Matrículas em Série" onclick="setAba('aluno')"/> </li>
		       	    <li> <h:commandLink id="alterarStatusMatriculaDisciplina" action="#{alteracaoStatusMatriculaMedioMBean.iniciarAlteracaoStatusDisciplina}" value="Alterar Status de Matrículas em Disciplina" onclick="setAba('aluno')"/> </li>
		       	</ul>
		       </li>
			</ul>
		</div>
		<div id="turma" class="aba">
			<%@include file="/medio/menus/turma.jsp"%>
		</div>
		<div id="relatorios" class="aba">
			<%@include file="/medio/menus/relatorios.jsp"%>
		</div>
		<div id="consultas" class="aba">
			<ul>
				<li> Consultas Gerais
					<ul>
					<li> <h:commandLink action="#{ buscaAvancadaDiscenteMBean.iniciarMedio}" value="Consulta Geral de Discentes" onclick="setAba('consultas')" /></li>
					<li> <h:commandLink action="#{turmaSerie.listar}" value="Turma" onclick="setAba('consultas')" /> </li>
					<li> <h:commandLink action="#{ cursoGrad.listar }" value="Cursos" onclick="setAba('consultas')"/> </li>		
					<li> <h:commandLink action="#{disciplinaMedioMBean.listar}" value="Disciplina"  onclick="setAba('consultas')"/></li>
	  				<li> <h:commandLink action="#{curriculoMedio.listar}" value="Estrutura Curricular"  onclick="setAba('consultas')" /></li>
					</ul>
				</li>
			</ul>
		</div>
	</div>

	<script>
		var Abas = function() {
			return {
			    init : function(){
			        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
			        <c:if test="${ acesso.medio && !acesso.pedagogico}">
			       		abas.addTab('aluno', "Aluno")
			        	abas.addTab('turma', "Turma")	
						abas.addTab('relatorios', "Relatórios")
						abas.addTab('consultas', "Consultas")
						
					   	<c:if test="${empty sessionScope.aba}">
				        	abas.activate('aluno');
				   	 	</c:if>
					    <c:if test="${sessionScope.aba != null}">
				    		abas.activate('${sessionScope.aba}');
				    	</c:if>
					</c:if>
					<c:if test="${acesso.pedagogico}">
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