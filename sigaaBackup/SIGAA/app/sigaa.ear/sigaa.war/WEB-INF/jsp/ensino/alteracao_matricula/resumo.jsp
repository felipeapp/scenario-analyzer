<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.ensino.struts.AlteracaoMatriculaAction"%>
<h2 class="tituloPagina">
<ufrn:subSistema /> &gt;
${tipoAlteracao }
</h2>

<html:form action="/ensino/alterarMatriculaDisciplina" >
	<html:hidden property="obj.id" />
	<html:hidden property="obj.usuario.id" value="${sessionScope.usuario.id}"/>
	<html:hidden property="obj.matricula.situacaoMatricula.id" />
	<html:hidden property="obj.matricula.id" />
	<html:hidden property="obj.matricula.discente.id" />
	<html:hidden property="obj.matricula.turma.id" />
    <table class="formulario" width="90%">
       <caption>Dados da Alteração da Matrícula em uma Disciplina</caption>
       <tbody>
        <tr>
            <th>Aluno</th>
            <td><b>${formAlteracaoMatricula.obj.matricula.discente.matricula } - ${formAlteracaoMatricula.obj.matricula.discente.pessoa.nome}</b>
            </td>
        </tr>
		<tr>
			<th>Disciplina</th>
			<td><b>${formAlteracaoMatricula.obj.matricula.turma.disciplina.codigo} - ${formAlteracaoMatricula.obj.matricula.turma.disciplina.nome}</b>
			</td>
		</tr>
		<tr>
			<th>Turma</th>
			<td><b>Cod. ${formAlteracaoMatricula.obj.matricula.turma.codigo}</b>
			</td>
		</tr>
		<tr>
			<th>Novo Status:</th>
			<td style="font-weight: bold;">
			Matrícula da Turma
				<%=((AlteracaoMatriculaAction.CANCELAMENTO.equals(session.getAttribute("tipoAlteracao")))?
						"Cancelada":"Trancada")%>
			</td>
		</tr>
		<tfoot>
   		<tr>
   			<td colspan="2">
                <html:button value="Confirmar" dispatch="persist" />
                <html:button value="Cancelar" dispatch="cancel" />
   			</td>
   		</tr>
   		</tfoot>
   	</table>
</html:form>
<br><br>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>