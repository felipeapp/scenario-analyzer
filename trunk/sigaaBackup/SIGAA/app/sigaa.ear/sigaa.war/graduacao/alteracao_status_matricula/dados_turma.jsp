<table class="visualizacao" style="width: 80%;">
	<caption>Turma Selecionada</caption>
	<tr>
		<th>Turma:</th>
		<td>${turma.disciplina.descricaoResumida} <small>(${turma.disciplina.nivelDesc})</small></td>
	</tr>
	<tr>
		<th>Ano Período:</th>
		<td>${turma.ano}.${turma.periodo}</td>
	</tr>		
	<tr>
		<th>Docente(s):</th>
		<td>${turma.docentesNomesCh}</td>
	</tr>
	<tr>
		<th>Tipo:</th>
		<td>${turma.tipoString}</td>
	</tr>
	<tr>
		<th>Situação:</th>
		<td>${turma.situacaoTurma.descricao}</td>
	</tr>		
	<tr>
		<th>Horário:</th>
		<td>${turma.descricaoHorario}</td>
	</tr>			
	<tr>
		<th>Local:</th>
		<td>${turma.local}</td>
	</tr>									
</table>