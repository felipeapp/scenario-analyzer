<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

<ul>
	<li> Indicadores da Situação do Departamento
		<ul>
			<li> <h:commandLink value="Situação Docente Atual" action="#{relatoriosDepartamentoCpdi.iniciarSituacaoDocente}" onclick="setAba('departamento')"/> </li>
			<li> <h:commandLink value="Indicadores de Ensino" action="#{relatoriosDepartamentoCpdi.iniciarIndicadoresEnsino}" onclick="setAba('departamento')"/>
			<li> <h:commandLink value="Distribuição de Turmas" action="#{relatoriosDepartamentoCpdi.iniciarDistribuicaoTurmas}" onclick="setAba('departamento')"/> </li>
			<li> <h:commandLink value="Pesquisa" action="#{relatoriosDepartamentoCpdi.iniciarIndicadoresPesquisa}" onclick="setAba('departamento')"/> </li>
			<li> <h:commandLink value="Pesquisa (Agrupado por Docente)" action="#{relatoriosDepartamentoCpdi.iniciarIndicadoresPesquisaAgrupadoDocente}" onclick="setAba('departamento')"/> </li>
			<li> <h:commandLink value="Extensão" action="#{relatoriosDepartamentoCpdi.iniciarIndicadoresExtensao}" onclick="setAba('departamento')"/> </li>
			<li> <h:commandLink value="Relatório Sintético" action="#{relatoriosDepartamentoCpdi.iniciarRelatorioSintetico}" onclick="setAba('departamento')"/> </li>
			<li> <h:commandLink action="#{ relatoriosDepartamentoCpdi.iniciarTurmasDepartamentoDocente }" value="Relatório de Turmas por Departamento" onclick="setAba('departamento')"/> </li>
			<li> <h:commandLink action="#{relatorioTotalTurmasHorarioMBean.iniciarTotalTurmasPorHorarioDepartamento}" value="Relatório Total de Turmas por Horários de Aula" onclick="setAba('departamento')"/> </li>
		</ul>
	</li>
	<li> Consultas Gerais
		<ul>
			<li> <h:commandLink action="#{historicoDiscente.iniciar}" value="Alunos" onclick="setAba('departamento')"/> </li>
			<li> <h:commandLink action="#{consultaPidMBean.iniciar}" value="Planos Individuais Docente"  id="ensinoPid_Consultar"/>	</li>
			<li> <h:commandLink action="#{ componenteCurricular.popularBuscaGeral }" value="Componentes Curriculares" onclick="setAba('departamento')"/> </li>
			<li> <a href="${ctx}/graduacao/curso/lista.jsf?aba=departamento">Cursos</a></li>
			<li> <a href="${ctx}/graduacao/curriculo/lista.jsf?aba=departamento">Estruturas Curriculares</a></li>
			<li> <a	href="${ctx}/administracao/cadastro/GrauAcademico/lista.jsf?aba=departamento">Graus Acadêmicos</a></li>
			<li> <a href="${ctx}/graduacao/habilitacao/lista.jsf?aba=departamento">Habilitações</a> </li>
			<li> <a href="${ctx}/graduacao/matriz_curricular/lista.jsf?aba=departamento">Matrizes Curriculares</a> </li>
			<li> <a	href="${ctx}/administracao/cadastro/ModalidadeEducacao/lista.jsf?aba=departamento">Modalidades de Educação</a></li>
			<li> <a	href="${ctx}/administracao/cadastro/Municipio/lista.jsf?aba=departamento">Municípios</a></li>
			<li> <h:commandLink action="#{ reconhecimento.listar }" value="Reconhecimentos" onclick="setAba('departamento')"/><%-- <a href="${ctx}/graduacao/reconhecimento/lista.jsf?aba=departamento"></a> --%></</li>
			<li> <h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Turmas" onclick="setAba('departamento')"/> </li>
			<li> <a href="${ctx}/administracao/cadastro/Turno/lista.jsf?aba=departamento">Turnos</a></li>
		</ul>
	</li>
	<li> Plano Individual Docente
		<ul>
			<li> <h:commandLink action="#{ consultaPidMBean.buscarPIDAnoPeriodo}" value="Exportação de Dados do PID" onclick="setAba('departamento')"/> </li>
		</ul>
	</li>
	<%-- Comentado temporáriamente	
	<li> Professor Substituto
		<ul>
			<li><h:commandLink action="#{solicitacaoProfessorSubstituto.listar}" value="Gerenciar Solicitações" onclick="setAba('departamento')"/></li>
		</ul>
	</li>
	--%>
</ul>