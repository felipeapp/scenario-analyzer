<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>

    <ul>
    	<li>Turma
    		<ul>
    			<li> <h:commandLink action="#{turmaLatoSensuBean.preCadastrar}" value="Criar Turma" onclick="setAba('turma')"/> </li>
				<li> <h:commandLink action="#{buscaTurmaBean.popularBuscaGeral}" value="Consultar/Alterar/Remover Turma" onclick="setAba('turma')"/> </li>
    			<li><a href="${ctx}/ensino/consolidacao/selecionaTurma.jsf?gestor=true&aba=turma">Consolidar Turma</a></li>
    			<li><a href="${ctx}/ensino/turma/busca_turma.jsf?gestor=true">Reabrir Turma</a></li>
    		</ul>
    	</li>
    </ul>

