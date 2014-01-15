<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>

    <ul>
    	<li>Relat�rios Sint�ticos
    		<ul>
    			<li><h:commandLink id="relEntradaAno" value="Relat�rio de Entradas por Ano" action="#{relatoriosLato.popularEntradasAnoSintetico}" onclick="setAba('relatorios')" /></li>
				<li><h:commandLink id="relAlunosCurso" value="Relat�rio de Alunos por Curso" action="#{relatoriosLato.iniciarBuscaAlunosCursoSintetico}" onclick="setAba('relatorios')" /></li>
				<li><h:commandLink id="relCursosCentro" value="Relat�rio de Cursos por Centro" action="#{relatoriosLato.iniciarBuscaCursoCentro}" onclick="setAba('relatorios')" /></li>
    		</ul>
    	</li>
    	<li>Alunos
    		<ul>
    			<li>
			        <h:commandLink id="relListaConcluintes"
						action="#{relatoriosLato.gerarRelatorioConcluintes}"
						value="Lista de Alunos Concluintes" onclick="setAba('relatorios')">
					</h:commandLink>
		        </li>
		        <li>
		        	<a href="${ctx}/lato/relatorios/seleciona_ranking_curso.jsf?aba=relatorios">Ranking de Alunos por Curso</a>
		        </li>
		    	<li>
					<h:commandLink id="relListaEleicao" action="#{relatorioDiscente.carregarSelecaoRelatorio}" onclick="setAba('relatorios')" value="Lista para Elei��o">
						<f:param value="seleciona_eleicao.jsf" name="relatorio"/>
					</h:commandLink>
				</li>
<!-- 				<li> -->
<%-- 		        	<a href="${ctx}/lato/relatorios/seleciona_curso_orientadores.jsf?aba=relatorios"> Orientadores de Trabalho Final por Curso </a> --%>
<!-- 		        </li> -->
		        <li> <h:commandLink id="relOrientacoes" action="#{ relatoriosLato.gerarRelatorioOrientacoes }" value="Orientadores de Trabalho Final de Curso" onclick="setAba('relatorios')"/> </li>
		        <li> <h:commandLink id="relDiscenteSemOrientacao" action="#{ relatoriosLato.gerarRelatorioDiscenteSemOrientacao }" value="Discentes sem Orientador" onclick="setAba('relatorios')"/> </li>
    		</ul>
    	</li>
    	<li>Quantitativos
    		<ul>
		        <li><a href="${ctx}/lato/relatorios/seleciona_matriculados_concluintes.jsf?aba=relatorios">Relat�rio Quantitativo dos Alunos Matriculados e Conclu�dos por Centro</a></li>
    		</ul>
    	</li>
    	<li>Turmas
    		<ul>
		        <li> <h:commandLink id="relBuscaGeralTurmas" action="#{ buscaTurmaBean.popularBuscaGeral }" value="Busca Geral de Turmas" onclick="setAba('relatorios')"/> </li>
    		</ul>
    	</li>
    	<li>Curso
    		<ul>
		        <li> <h:commandLink id="relAndamentoCurso" action="#{ relatoriosLato.inicioAndamentoDosCursos }" value="Andamento dos Cursos" onclick="setAba('relatorios')"/> </li>
		        <li> <h:commandLink id="relSituacaoProposta" action="#{ relatoriosLato.gerarRelatorioStatusProposta }" value="Situa��o das Propostas" onclick="setAba('relatorios')"/> </li>
		        <li> <h:commandLink id="relOrientacoesDepartamento" action="#{ relatoriosLato.gerarRelatorioOrientacoesDepartamento }" value="Orienta��es dos Cursos Departamento" onclick="setAba('relatorios')"/> </li>
    		</ul>
    	</li>
    </ul>