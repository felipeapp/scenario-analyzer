<%@ taglib uri="/tags/ufrn" prefix="ufrn"  %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

    <div class="secao discenteMedio" >
        <h3>Aluno</h3>
    </div>
    <ul>
        <li>Aluno
            <ul>
                <li><ufrn:link action="pessoa/wizard" param="dispatch=popular&nextView=dadosDiscente" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Cadastrar</ufrn:link></li>
                <li><ufrn:link action="ensino/discente/wizard" param="dispatch=list&page=0" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Alterar/Remover</ufrn:link></li>
             <%--
                <li>Lista de Alunos com hora complementar pendente</li>
            --%>
            </ul>
        </li>
        <li>Matrícula
            <ul>
                <li><ufrn:link action="ensino/matriculaDisciplina" param="dispatch=escolheTipo" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Efetuar Matrícula</ufrn:link></li>
        		<li><ufrn:link action="ensino/alterarMatriculaDisciplina" param="dispatch=discentes&tipo=cancelar" roles="<%=new int[] {SigaaPapeis.GESTOR_TECNICO} %>">Cancelar Matrícula em Turmas</ufrn:link></li>
<%--
        		<li><ufrn:link action="ensino/alterarMatriculaDisciplina" param="dispatch=discentes&tipo=trancar" roles="<%=new int[] {SigaaPapeis.GESTOR_TECNICO} %>">Trancar Matrícula em Turmas</ufrn:link></li>
        		<li>Efetuar Matrícula Compulsória</li>
        		<li>Trancar Matrícula</li>
        		<li>Retificar Trancamento de Matrícula</li>
--%>
		        <li><ufrn:link action="ensino/verAtestado" param="dispatch=checaUsuario&forward=Atestado" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Atestado de matrícula</ufrn:link></li>
        	</ul>
        </li>
        <li>Movimentação de Aluno
            <ul>
                <li><ufrn:link action="ensino/cadastroAfastamentoAluno" param="dispatch=edit" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Registro de Afastamento</ufrn:link></li>
                <li><ufrn:link action="ensino/cadastroAfastamentoAluno" param="dispatch=list&tipoLista=1" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Alterar/Desativar Afastamento</ufrn:link></li>
                <li><ufrn:link action="ensino/cadastroAfastamentoAluno" param="dispatch=list&tipoLista=2" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Registro de Retorno de Afastamento</ufrn:link></li>
            </ul>
		</li>
        <li>Expedição
        	<ul>
                <li><ufrn:link action="ensino/verHistorico" param="dispatch=checaUsuario" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Histórico</ufrn:link></li>
                <li><ufrn:link action="ensino/tecnico/verSelecaoDiscenteCertificado" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Certificado</ufrn:link></li>
<%--
                <li>Nota</li>
                <li>Diploma</li>
--%>
             </ul>
        </li>
        <li>Aproveitamento de Disciplina
            <ul>
                <li><ufrn:link action="ensino/cadastroAproveitamentoDisciplina" param="dispatch=edit" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Cadastrar</ufrn:link></li>
                <li><ufrn:link action="ensino/cadastroAproveitamentoDisciplina" param="dispatch=list" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Alterar/Desativar</ufrn:link></li>
            </ul>
        </li>
    </ul>