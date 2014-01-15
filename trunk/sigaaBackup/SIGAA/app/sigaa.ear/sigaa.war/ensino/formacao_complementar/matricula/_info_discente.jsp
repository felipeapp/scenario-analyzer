	<h:form>
	<table class="visualizacao">
	<tr>
		<th width="20%"> Discente: </th>
		<td> ${matriculaFormacaoComplementarMBean.discente}		
		</td>
	</tr>
	
	<tr>
		<th width="20%"> Curso: </th>
		<td> ${matriculaFormacaoComplementarMBean.discente.curso.nome} </td>
	</tr>

	<tr>
		<th> Currículo: </th>
		<td> ${matriculaFormacaoComplementarMBean.discente.estruturaCurricularTecnica.descricaoResumida} </td>
	</tr>
	</table>
	</h:form>
	<br />