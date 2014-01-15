<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<h2 class="tituloPagina">
	<html:link action="/ensino/criarTurma?dispatch=cancelar">
	<ufrn:subSistema semLink="true"/>
	</html:link>
	&gt; Turma &gt; Confirmação
</h2>
<style>
<!--
table.listagem th {
	font-weight: bold;
}
-->
</style>

<html:form action="/ensino/criarTurma" method="post">

	<table class="listagem" width="80%">
	<caption class="listagem"> Resumo dos Dados da Turma </caption>

	<tr>
		<th>
		<ufrn:subSistema teste="not infantil">Disciplina:</ufrn:subSistema>
		<ufrn:subSistema teste="infantil">Nível Infantil:</ufrn:subSistema>
		</th>
		<td colspan="5"> ${turmaForm.obj.disciplina.nome} </td>
	</tr>
	<tr>
		<th width="20%"> Código: </th>
		<td width=""> ${turmaForm.obj.codigo} </td>
		<ufrn:subSistema teste="not infantil">
		<ufrn:subSistema teste="not lato">
		<th>Ano - Período: 	</th>
		<td>${turmaForm.obj.ano} - ${turmaForm.obj.periodo}</td>
		</ufrn:subSistema>
		</ufrn:subSistema>
		<ufrn:subSistema teste="infantil">
		<th>Ano: 	</th>
		<td>${turmaForm.obj.ano}</td>
		</ufrn:subSistema>
		<th> Capacidade: </th>
		<td> ${turmaForm.obj.capacidadeAluno} Alunos </td>
	</tr>
	<tr>
		<th> Observação: </th>
		<td colspan="5"> ${turmaForm.obj.observacao}</td>
	</tr>
	<tr>
	<ufrn:subSistema teste="tecnico">
		<tr>
			<th>Especialidade da turma de Entrada: </th>
			<td colspan="2">${turmaForm.obj.especializacao.descricao }</td>
		</tr>
	</ufrn:subSistema>
	</tr>
	<tr>
		<th> Local: </th>
		<td> ${turmaForm.obj.local} </td>
		<ufrn:subSistema teste="not infantil">
		<th> Carga Horária: </th>
		<td colspan="2"> ${turmaForm.obj.disciplina.chAula} - ${turmaForm.obj.disciplina.chLaboratorio} - ${turmaForm.obj.disciplina.chEstagio} </td>
		</ufrn:subSistema>
	</tr>
	<ufrn:subSistema teste="not infantil">
	<tr>
		<th> Data Início:  </th>
		<td> <ufrn:format name="turmaForm" property="obj.dataInicio" type="data"/> </td>
		<th> Data Fim:  </th>
		<td colspan="3"> <ufrn:format name="turmaForm" property="obj.dataFim" type="data"/> </td>
	</tr>
	</ufrn:subSistema>
	<tr>
		<th>Docente(s):</th>
		<td colspan="5">
			<table width="100%" style="border-collapse: collapse;">
				<c:forEach items="${turmaForm.obj.docentesTurmas}" var="docenteTurma">
				<tr>
				<td>${docenteTurma.docenteDescricao} (${docenteTurma.chDedicadaPeriodo} hrs)</td>
				</tr>
				</c:forEach>
			</table>
		</td>
	</tr>
	<ufrn:subSistema teste="not infantil">
	<tr>
		<th>Horários:</th>
		<td colspan="5">
			${turmaForm.obj.descricaoHorario}
		</td>
	</tr>
	</ufrn:subSistema>
	<tfoot>
		<tr>
			<td colspan="6" align="center">

    		<c:if test="${param.dispatch != 'view'}">
    		<html:button dispatch="chamaModelo" value="Confirmar"/>&nbsp;&nbsp;
			</c:if>
    		<input type="button" value="Voltar" onclick="history.go(-1);"/>
    		<c:if test="${param.dispatch != 'remove' and param.dispatch != 'view'}">
    		<br><br>
    		<html:button view="horario" value="<< Horários"/>&nbsp;&nbsp;
    		<html:button view="docentes"><< Alterar Docentes</html:button>&nbsp;&nbsp;
    		<html:button view="dadosGerais" value="<< Dados Gerais"/>&nbsp;&nbsp;
    		</c:if>
    		</td>
    	</tr>
    </tfoot>
</table>

</html:form>

<br><br>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
