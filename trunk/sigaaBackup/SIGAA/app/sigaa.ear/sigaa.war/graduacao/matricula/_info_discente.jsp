	<h:form>
	<table class="visualizacao">
	<tr>
		<th width="20%"> Discente: </th>
		<td> ${matriculaGraduacao.discente}
			<c:if test="${!(matriculaGraduacao.discenteLogado && matriculaGraduacao.modoOtimizado)}">
				<small><i>( 
				<h:commandLink action="#{historico.selecionaDiscenteForm}" value="Ver histórico ">
					<f:param name="id" value="#{matriculaGraduacao.discente.id}"/>
				</h:commandLink>
				)</i></small>
			</c:if>
		</td>
	</tr>
	
	<c:if test="${matriculaGraduacao.discente.graduacao and not empty matriculaGraduacao.discente.curso}">
		<tr>
			<th width="20%"> Matriz Curricular: </th>
			<td> ${matriculaGraduacao.discente.matrizCurricular.descricao} </td>
		</tr>
	</c:if>		
	
	<c:if test="${not matriculaGraduacao.discente.graduacao && !matriculaStrictoBean.discente.discente.especial}">
		<tr>
			<th width="20%"> Curso: </th>
			<td> ${matriculaGraduacao.discente.curso.nomeCursoStricto} </td>
		</tr>
	</c:if>
	<c:if test="${matriculaStrictoBean.discente.discente.especial}">
		<tr>
			<th width="20%"> Programa: </th>
			<td> ${matriculaStrictoBean.discente.gestoraAcademica.nome} </td>
		</tr>		
	</c:if>
	<c:if test="${not empty matriculaGraduacao.discente.curso}">
		<tr>
			<th> Currículo: </th>
			<c:if test="${!matriculaGraduacao.discente.tecnico}">
			<td> ${matriculaGraduacao.discente.curriculo.codigo} </td>
			</c:if>
			<c:if test="${matriculaGraduacao.discente.tecnico}">
			<td> ${matriculaGraduacao.discente.estruturaCurricularTecnica.descricaoResumida} </td>
			</c:if>
		</tr>
	</c:if>
	</table>
	</h:form>
	<br />