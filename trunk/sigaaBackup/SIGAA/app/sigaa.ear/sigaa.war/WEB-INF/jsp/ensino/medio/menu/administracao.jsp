<%@ taglib uri="/tags/struts-html" prefix="html"  %>

    <div class="secao administracaoMedio">
        <h3>Administração</h3>
    </div>
<%--     <ul>
        <li>Prorrogação de prazos
            <ul>
                <li>Cadastrar</li>
                <li>Alterar/Remover</li>
            </ul>
		</li>
    </ul>
 --%>
    <ul>
        <li>Bolsa
            <ul>
                <li><html:link action="/ensino/tecnico/verTecBolsaForm">Cadastrar</html:link></li>
                <li><html:link action="/ensino/tecnico/listarTecBolsas">Alterar/Remover</html:link></li>
            </ul>
        </li>
    </ul>
<%--
    <ul>
        <li>Outros processos seletivos
            <ul>
                <li><html:link action="/ensino/tecnico/verTecProcessoSeletivoForm">Cadastrar</html:link></li>
                <li><html:link action="/ensino/tecnico/listarTecProcessosSeletivos">Alterar/Remover</html:link></li>
            </ul>
		</li>
    </ul>
--%>