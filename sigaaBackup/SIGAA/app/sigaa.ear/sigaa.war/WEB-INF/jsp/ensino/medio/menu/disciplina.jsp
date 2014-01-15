<%@ taglib uri="/tags/ufrn" prefix="ufrn"  %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

    <div class="secao disciplinaMedio">
        <h3>Disciplina</h3>
    </div>
    <ul style="list-style-image: none; list-style: none;">
    <li>
    <ul>
        <li><ufrn:link action="ensino/cadastroDisciplina" param="dispatch=edit" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Cadastrar</ufrn:link></li>
    	<li><ufrn:link action="ensino/cadastroDisciplina" param="dispatch=list" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Alterar/Remover</ufrn:link></li>
    </ul>
    </li>
    </ul>
<%--     <ul>
    	<li>Disciplinas Equivalentes</li>
    </ul>
    <ul>
		<li>Disciplinas por Departamento</li>
	</ul>
	<ul>
		<li>Aproveitamento de Disciplina</li>
	</ul>
 --%>
