<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<%@page import="br.ufrn.sigaa.arq.util.CheckRoleUtil"%>

<% CheckRoleUtil.checkRole(request, response, new int[] {SigaaPapeis.SECRETARIA_DEPARTAMENTO} ); %>

<c:set var="hideSubsistema" value="true" />
<f:view>

<h:form>
	<input type="hidden" name="aba" id="aba"/>
	<h2><ufrn:subSistema /> > Menu do Departamento</h2>

	<div id="operacoes-subsistema" class="reduzido" >
		<div id="departamento" class="aba">
			<ul>
				<li> Componentes Curriculares
					<ul>
					<li> <h:commandLink action="#{componenteCurricular.preCadastrar}" value="Solicitar Cadastro" onclick="setAba('departamento')"/> </li>
					<li> <h:commandLink action="#{autorizacaoComponente.telaComponentes}" value="Listar Solicitações"  onclick="setAba('departamento')"/> </li>
					<li> <h:commandLink action="#{componenteCurricular.popularBuscaGeral}" value="Consultar Componentes"  onclick="setAba('departamento')"/> </li>
					<li> <h:commandLink action="#{programaComponente.iniciar}" value="Cadastrar Programa de Componente"  onclick="setAba('departamento')"/> </li>
					</ul>
				</li>
				<li> Turmas
					<ul>
					<li> <h:commandLink action="#{buscaTurmaBean.popularBuscaGeral}" value="Alterar/Remover Turma" onclick="setAba('departamento')"/> </li>
					<li> <h:commandLink action="#{analiseSolicitacaoTurma.gerenciarSolicitacoesTodas}" value="Gerenciar Solicitações" onclick="setAba('departamento')"/> </li>
					</ul>
				</li>
				<li> Discentes
					<ul>
					<li> <h:commandLink action="#{ buscaAvancadaDiscenteMBean.iniciarCoordGraduacao }" value="Buscar" onclick="setAba('departamento')"/> </li>
					<li> <h:commandLink action="#{matriculaGraduacao.iniciarEspecial}" value="Matricular Aluno Especial" onclick="setAba('departamento')"/> </li>
					<li> <h:commandLink action="#{transferenciaTurma.iniciarAutomatica}" value="Transferir Aluno entre turmas" onclick="setAba('departamento')"/> </li>
					</ul>
				</li>
				<c:if test="${acesso.secretarioDepartamento || acesso.chefeDepartamento}">
				<li>Página WEB
		    	
		    		<ul>
		    			<li><h:commandLink action="#{detalhesSite.iniciarDetalhesDepartamento}" value="Apresentação" onclick="setAba('departamento')"/></li>
		    			<li><h:commandLink action="#{documentoSite.preCadastrarDepartamento}" value="Cadastrar Documentos" onclick="setAba('departamento')"/></li>
		    			<li><h:commandLink action="#{documentoSite.listarDepartamento}" value="Alterar / Remover Documentos" onclick="setAba('departamento')"/></li>
		    			<li><h:commandLink action="#{noticiaSite.preCadastrarDepartamento}" value="Cadastrar Notícias" onclick="setAba('departamento')"/></li>
		    			<li><h:commandLink action="#{noticiaSite.listarDepartamento}" value="Alterar / Remover Notícias" onclick="setAba('departamento')"/></li>
		    			<li><h:commandLink action="#{secaoExtraSite.preCadastrarDepartamento}" value="Cadastrar Seções Extras" onclick="setAba('departamento')"/></li>
		    			<li><h:commandLink action="#{secaoExtraSite.listarDepartamento}" value="Alterar / Remover Seções Extras" onclick="setAba('departamento')"/></li>
		    		</ul>
		    	</li>
		    	</c:if>
				<li> Relatórios
					<ul>
					<li> <h:commandLink action="#{relatorioDocente.gerarRelatorioLista}" value="Relatório de Docentes" onclick="setAba('departamento')"/> </li>
					<li> <h:commandLink action="#{buscaTurmaBean.iniciarTurmasSituacao}" value="Turmas por Situação" onclick="setAba('departamento')"/> </li>
					<li> <h:commandLink action="#{relatoriosCoordenador.relatorioTurmasConsolidadasDepartamento}" value="Turmas Consolidadas" onclick="setAba('departamento')"/> </li>
					<li><h:commandLink action="#{relatorioTurma.iniciarRelatorioOcupacaoVagas}" value="Relatório de Ocupação de Vagas de Turmas" onclick="setAba('departamento')"/></li>
					<li><h:commandLink action="#{relatorioAlunosEmDisciplinas.iniciarRelatorio}" value="Relatório de Alunos Matriculados por Disciplina" onclick="setAba('departamento')"/></li>
					</ul>
				</li>
				
				<li> Declarações
					<ul>
					<li> <h:commandLink action="#{declaracaoDisciplinasMinistradas.iniciarDocenteUFRN}" value="Declaração de Disciplinas Ministradas Por Docentes UFRN" onclick="setAba('departamento')"/> </li>
					<li> <h:commandLink action="#{declaracaoDisciplinasMinistradas.iniciarDocenteExterno}" value="Declaração de Disciplinas Ministradas Por Docentes Externos" onclick="setAba('departamento')"/> </li>
					</ul>
				</li>
				
		    	<li> Docentes Externo
		    		<ul>
			    		<li> <ufrn:link action="administracao/docente_externo/lista.jsf" roles="<%=new int[] {SigaaPapeis.SECRETARIA_DEPARTAMENTO} %>"> Cadastrar Usuário Para Docente Externo</ufrn:link> </li>
		    		</ul>
		    	</li>
		    	<li>Consultas
		    		<ul>
						<li><a href="${ctx}/graduacao/relatorios/turma/seleciona_turma.jsf?aba=relatorios">Relatório de Turmas</a></li>		    		
						<li> <h:commandLink action="#{componenteCurricular.popularBuscaGeral}" value="Componentes Curriculares" onclick="setAba('departamento')"/> </li>
						<li> <h:commandLink action="#{buscaTurmaBean.popularBuscaGeral}" value="Turmas" onclick="setAba('departamento')"/> </li>
						
						<li><a href="/sigaa/graduacao/curriculo/lista.jsf" onclick="setAba('departamento')">Estruturas Curriculares</a></li>
						<li><a href="/sigaa/graduacao/habilitacao/lista.jsf" onclick="setAba('departamento')">Habilitações</a></li>
						<li><a href="/sigaa/graduacao/matriz_curricular/lista.jsf" onclick="setAba('departamento')">Matrizes Curriculares</a></li>
						<li><a href="/sigaa/graduacao/curso/lista.jsf" onclick="setAba('departamento')">Cursos</a></li>
		    		</ul>
		    	</li>
		    	
		    	
			</ul>
		</div>
	</div>

<script>
var Abas = {
    init : function(){
        var abas = new YAHOO.ext.TabPanel('operacoes-subsistema');
	    abas.addTab('departamento', "Secretaria de Departamento");
	    abas.activate('departamento');
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
