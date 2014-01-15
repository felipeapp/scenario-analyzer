<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 


<div class="menu">
    <div class="titulo">
        <h2>Matrícula</h2>
    </div>
    
    <ul>
    	<li><html:link action="/ensino/matriculaDisciplina?dispatch=escolheTipo">Matricular</html:link>
        </li>
        <li>Outras operações:
        	<ul>
        		<li><html:link action="/ensino/tecnico/verSelecaoDiscente">Excluir Matrícula</html:link></li>
        	</ul>
        	<ul>
        		<li>Efetuar Matrícula Compulsória</li>
        	</ul>
        	<ul>
        		<li>Trancamento de Matrícula</li>
        	</ul>
        	<ul>
        		<li>Retificação de Trancamento</li>        		
        	</ul>
        	<ul>
        		<li>Lista de Indeferimentos</li>
        	</ul>
        	<ul>
        		<li>Processar Matrícula</li>
        	</ul>
        	<ul>
        		<li>Processar Re-matrícula</li>
        	</ul>
        </li>
    </ul>
</div>
