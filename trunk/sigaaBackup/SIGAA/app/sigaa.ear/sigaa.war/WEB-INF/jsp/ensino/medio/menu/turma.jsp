<%@ taglib uri="/tags/ufrn" prefix="ufrn"  %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

    <div class="secao turmaMedio" >
        <h3>Turma</h3>
    </div>

    <ul>
		<li> Horário
			<ul>
				 <li><ufrn:link action="ensino/cadastroHorario" param="dispatch=edit" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Cadastrar</ufrn:link></li>
       			 <li><ufrn:link action="ensino/cadastroHorario" param="dispatch=list" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Alterar/Remover</ufrn:link></li>
			</ul>
		</li>
		<li> Turma
			<ul>
	   		<li><ufrn:link action="ensino/criarTurma" param="dispatch=popular" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Cadastrar </ufrn:link></li>
			<li><ufrn:link action="ensino/criarTurma" param="dispatch=list&page=0" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Alterar/Remover </ufrn:link></li>
			<li><a href="${ctx}/ensino/consolidacao/selecionaTurma.jsf" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Consolidar Turma</a></li>
			</ul>
		</li>
		<li> Avaliação de Turma
			<ul>
				 <li><ufrn:link action="ensino/cadastroAvaliacao" param="dispatch=popular" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Cadastrar</ufrn:link></li>
       			 <li><ufrn:link action="ensino/removerAvaliacao" param="dispatch=popular" roles="<%=new int[] {SigaaPapeis.GESTOR_MEDIO} %>">Remover</ufrn:link></li>
			</ul>
		</li>

<%--
    		<li>Turmas Oferecidas a um Curso</li>
	        <li>Definir Docente/Local</li>
       		<li>Transferência de Discente</li>
 --%>





    </ul>
