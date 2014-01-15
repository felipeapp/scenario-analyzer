<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 


<div class="menu">
    <div class="titulo">
        <h2>Matr�cula</h2>
    </div>
    
    <ul>
    	<li><html:link action="/ensino/matriculaDisciplina?dispatch=escolheTipo">Matricular</html:link>
        </li>
        <li>Outras opera��es:
        	<ul>
        		<li><html:link action="/ensino/tecnico/verSelecaoDiscente">Excluir Matr�cula</html:link></li>
        	</ul>
        	<ul>
        		<li>Efetuar Matr�cula Compuls�ria</li>
        	</ul>
        	<ul>
        		<li>Trancamento de Matr�cula</li>
        	</ul>
        	<ul>
        		<li>Retifica��o de Trancamento</li>        		
        	</ul>
        	<ul>
        		<li>Lista de Indeferimentos</li>
        	</ul>
        	<ul>
        		<li>Processar Matr�cula</li>
        	</ul>
        	<ul>
        		<li>Processar Re-matr�cula</li>
        	</ul>
        </li>
    </ul>
</div>
