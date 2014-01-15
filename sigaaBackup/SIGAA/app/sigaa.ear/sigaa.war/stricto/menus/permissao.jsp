<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html"%>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
<ul>
	<li>Manuten��o de Coordenadores
	<ul>
		<li><h:commandLink action="#{coordenacaoCurso.iniciar}" value="Identificar Coordenador" onclick="setAba('permissao')" id="identificacaoDeCoord"/></li>
		<li><h:commandLink action="#{coordenacaoCurso.iniciarSubstituicao}" value="Alterar/Substituir/Cancelar Coordenador" onclick="setAba('permissao')" id="SubstituirCoord"/></li>
		<li><h:commandLink action="#{coordenacaoCurso.listar}" value="Listar Coordenadores" onclick="setAba('permissao')" id="listarCoordenacoes"/></li>
	</ul>
	</li>
	<li>Manuten��o de Secret�rios
	<ul>
		<li><h:commandLink action="#{secretariaUnidade.iniciarProgramaPos}" value="Identificar Secret�rio" onclick="setAba('permissao')"  id="linkiniciarProgramaPos"/></li>
		<li> <h:commandLink action="#{secretariaUnidade.iniciarSubstituicaoProgramaPos}" value="Substituir Secret�rio" onclick="setAba('permissao')" id="iniciarSubstituicaoProgramaPos"/> </li>
		<li> <a href="${ctx}/ensino/secretaria_unidade/secretarios_programa.jsf?aba=permissao" id="listarsecretarios"> Listar Secret�rios </a></li>
	</ul>
	</li>
	<li>Docentes
	<ul>
		<li><h:commandLink action="#{docenteExterno.popular}" onclick="setAba('permissao')" id="cadastrarDocentesExternos">Cadastrar Docente Externo </h:commandLink></li>
		<li><a href="${ctx}/administracao/docente_externo/lista.jsf" onclick="setAba('permissao')" id="listardocentesExternos">Consultar Docentes Externos </a></li>
	</ul>
	</li>
	<li>Opera��es Administrativas
	<ul>
		<li> <h:commandLink action="#{calendario.iniciarPPG}" value="Calend�rio Universit�rio" onclick="setAba('permissao')" id="linkCalendarioUniversitarioPPG"/> </li>
		
		<c:if test="${acesso.administradorStricto}">
			<li> <h:commandLink action="#{userBean.iniciarLogarComo}" value="Logar como Outro Usu�rio" onclick="setAba('permissao')" rendered="#{acesso.administradorStricto}" id="logandoComo"/> </li>
			<li> <h:commandLink action="#{parametros.iniciarStrictoSensu}" value="Par�metros do Sistema" onclick="setAba('permissao')"/> </li>
		</c:if>
	</ul>
	</li>
</ul>