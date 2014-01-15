<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %> 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %> 


<div class="menu">
    <div class="titulo">
        <h2>Ingresso e Afastamento</h2>
    </div>
    
    <ul>
        <li>Afastamento/Retorno
            <ul>
                <li><sipac:link action="/ensino/tecnico/cadastroCursoTecnico" param="dispatch=edit">Cadastrar</html:link></li>
                <li><sipac:link action="/ensino/tecnico/cadastroCursoTecnico" param="dispatch=list">Alterar/Remover</html:link></li>
            </ul>
		</li>
    </ul>
    <!-- ul>
        <li>Retornos
            <ul>
                <li><html:link action="/ensino/tecnico/verSelecaoAfastamento">Cadastrar</html:link></li>
                <li><html:link action="/ensino/tecnico/listarTecRetornos">Consultar</html:link></li>
            </ul>
		</li>
    </ul-->
    <ul>
        <li>Prorrogação de prazos
            <ul>
                <li>Cadastrar</li>
                <li>Consultar</li>
            </ul>
		</li>
    </ul>
    <ul>
        <li>Outros processos seletivos
            <ul>
                <li><html:link action="/ensino/tecnico/verTecProcessoSeletivoForm">Cadastrar</html:link></li>
                <li><html:link action="/ensino/tecnico/listarTecProcessosSeletivos">Consultar</html:link></li>
            </ul>
		</li>
    </ul>
</div>
