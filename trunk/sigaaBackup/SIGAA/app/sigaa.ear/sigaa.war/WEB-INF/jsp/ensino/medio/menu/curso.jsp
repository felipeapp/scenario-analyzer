<%@ taglib uri="/tags/ufrn" prefix="ufrn"  %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

    <div class="secao cursoMedio" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">
        <h3>Curso</h3>
    </div>
    <ul>
        <li>Curso
            <ul>
                <li><ufrn:link action="ensino/tecnico/cadastroCursoTecnico" param="dispatch=edit" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Cadastrar</ufrn:link></li>
                <li><ufrn:link action="ensino/tecnico/cadastroCursoTecnico" param="dispatch=list" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Alterar/Remover</ufrn:link></li>
		        <li><ufrn:link action="ensino/cadastroDocumentoLegal" param="dispatch=edit" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Cadastrar Documentos Legais</ufrn:link></li>
		        <li><ufrn:link action="ensino/cadastroDocumentoLegal" param="dispatch=list" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Consultar Documentos Legais</ufrn:link></li>
                <li><ufrn:link action="ensino/ead/cadastroVeiculacoesCurso" param="dispatch=list" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Veiculações de Ensino à Distância</ufrn:link></li>
            </ul>
        </li>
        <li>Série
            <ul>
                <li><ufrn:link action="ensino/tecnico/modulo/wizard" param="dispatch=popular" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Cadastrar</ufrn:link></li>
                <li><ufrn:link action="ensino/tecnico/modulo/wizard" param="dispatch=list" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Alterar/Remover</ufrn:link></li>
			</ul>
        </li>
        <li>Estrutura Curricular
            <ul>
                <li><ufrn:link action="ensino/tecnico/estruturaCurricular/wizard" param="dispatch=popular" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Cadastrar</ufrn:link></li>
                <li><ufrn:link action="ensino/tecnico/estruturaCurricular/wizard" param="dispatch=list" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Alterar/Remover</ufrn:link></li>
			</ul>
        </li>
    	<li>Turma de Entrada
    		<ul>
    			<li><ufrn:link action="ensino/tecnico/cadastroTurmaEntradaTecnico" param="dispatch=edit" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Cadastrar</ufrn:link></li>
    			<li><ufrn:link action="ensino/tecnico/cadastroTurmaEntradaTecnico" param="dispatch=list" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Alterar/Remover</ufrn:link></li>
    		</ul>
    	</li>
        <li>Coordenação de Curso
            <ul>
                <li><ufrn:link action="ensino/cadastroCoordenacaoCurso" param="dispatch=edit" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Cadastrar</ufrn:link></li>
                <li><ufrn:link action="ensino/cadastroCoordenacaoCurso" param="dispatch=list" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Alterar/Remover</ufrn:link></li>
            </ul>
        </li>
    </ul>
<%--
        <li>Mudanças
            <ul>
                <li>Currículo</li>
                <li>Habilitação</li>
            </ul>
            <ul>
                <li>Sede</li>
                <li>Modalidade</li>
            </ul>
        </li>
    </ul>
 --%>