<%@ taglib uri="/tags/ufrn" prefix="ufrn"  %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

    <ul>
   		<li> Turma
   			<ul>
				<li><h:commandLink action="#{turmaTecnicoBean.preCadastrar}" value="Cadastrar" onclick="setAba('turma')" />	</li>
				<%--  <li><h:commandLink action="#{ buscaTurmaBean.popularBuscaTecnico }" value="Consultar, Alterar ou Remover" onclick="setAba('turma')"/> </li> --%>
				<li><a href="${ctx}/ensino/consolidacao/selecionaTurma.jsf?gestor=true&aba=turma">Consolidar Turma</a></li>
				<li><h:commandLink action="#{buscaTurmaBean.popularBuscaGeral}" value="Consulta Geral de Turmas" onclick="setAba('turma')"></h:commandLink></li>
   			</ul>
   		</li>
   		<li> Docentes Externos
   			<ul>
	   			<li><h:commandLink action="#{docenteExterno.popular}" value="Cadastrar"  onclick="setAba('turma')"/></li>
	   			<li><a href="${ctx}/administracao/docente_externo/lista.jsf?aba=turma">Alterar/Remover</a></li>
   			</ul>
   		</li>
   		<%-- 
   		<c:if test="${ !acesso.secretarioTecnico }">
			<li>Manuten��o de Coordenadores
	         	<ul>
					<li><h:commandLink action="#{coordenacaoCurso.iniciar}" value="Identificar Coordenador" onclick="setAba('turma')" /></li>				
					<li><h:commandLink action="#{coordenacaoCurso.iniciarBuscaCoordenadorPorCurso}" value="Substituir/Cancelar Coordenador" onclick="setAba('turma')" /></li>
					<li> <a href="${ctx}/ensino/coordenacao_curso/coordenadores_tecnico.jsf?aba=turma" id="listarCoordenacoes"> Listar Coordenadores </a></li>
				</ul>
	        </li>
	        <li>Manuten��o de Secret�rios
	         	<ul>
					<li><h:commandLink action="#{secretariaUnidade.iniciarCoordenacaoTecnico}" value="Identificar Secret�rio" onclick="setAba('turma')" /></li>				
					<li><h:commandLink action="#{secretariaUnidade.iniciarSubstituicaoCoordenacaoTecnico}" value="Substituir/Cancelar Secret�rio" onclick="setAba('turma')" /></li>
					<li> <a href="${ctx}/ensino/secretaria_unidade/secretarios_tecnico.jsf?aba=turma" id="listarSecretarios"> Listar Secret�rios </a></li>
				</ul>
	        </li>
        </c:if>
        --%>
    </ul>