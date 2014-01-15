<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<h2>Listagem dos Alunos</h2>
	<div id="parametrosRelatorio">
		<table>
			<tr>
				<th width="18%"> Turma: </th>
				<td style="text-align: left;"> ${turmaInfantilMBean.obj.disciplina.descricaoResumida} - ${turmaInfantilMBean.obj.codigo} </td>
			</tr>
			<tr>
				<th> Docente(s): </th>
				<td style="text-align: left;"> ${turmaInfantilMBean.obj.docentesNomes} </td>
			</tr>
			<tr>
				<th> Horário: </th>
				<td style="text-align: left;"> ${turmaInfantilMBean.obj.descricaoHorario} </td>
			</tr>
		</table>
	</div><br/>
	
	<h3 class="tituloTabelaRelatorio">Dados dos Alunos</h3>
	<table class="tabelaRelatorio" width="100%">
		<thead>
			<tr>
				<th style="text-align: center;">Matrícula</th>
				<th style="text-align: left;">Aluno</th>
				<th style="text-align: center;">Telefone</th>
			</tr>
		</thead>
		<c:forEach items="#{turmaInfantilMBean.matriculados}" var="aluno" varStatus="status">
			<tr class="relatorioBody">
				<td style="text-align: center;"> ${ aluno.discente.matricula }</td>
				<td style="text-align: left;"> ${ aluno.discente.pessoa } </td>
				<td style="text-align: center;"> ${ aluno.discente.pessoa.telefone } </td>
			</tr>
		</c:forEach>
	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>