

<ul class="form">
	
	<li>
		<label>Código </label>
		<div class="campo">${ planoEnsino.object.turma.disciplina.codigo }</div>
	</li>
	<li>
		<label>Disciplina </label>
		<div class="campo">${ planoEnsino.object.turma.disciplina.detalhes.nome }</div>
	</li>
	<li>
		<label>Turma </label>
		<div class="campo">${ planoEnsino.object.turma.codigo }</div>
	</li>
	<li>
		<label>Período </label>
		<div class="campo">${ planoEnsino.object.turma.anoPeriodo }</div>
	</li>
	<li>
		<label>Horário </label>
		<div class="campo">${ planoEnsino.object.turma.descricaoHorario }</div>
	</li>
	<%--><li>
		<label>Professor(es) </label>
		<div class="campo">
			<c:forEach var="docente" items="${ planoEnsino.object.turma.docentes }">
				${ docente.pessoa.nome }<br/>
			</c:forEach>
		</div>
	</li>--%>
	<li>
		<label>CH Total </label>
		<div class="campo">${ planoEnsino.object.turma.disciplina.detalhes.chTotal } horas</div>
	</li>
	<li>
		<label>CH de Aulas </label>
		<div class="campo">${ planoEnsino.object.turma.disciplina.detalhes.chAula } horas</div>
	</li>
	<li>
		<label>CH de Laboratório </label>
		<div class="campo">${ planoEnsino.object.turma.disciplina.detalhes.chLaboratorio } horas</div>
	</li>
	<li>
		<label>CH de Estágio </label>
		<div class="campo">${ planoEnsino.object.turma.disciplina.detalhes.chEstagio } horas</div>
	</li>
	
	<li>
		<label class="required"> Objetivos <span class="required"/> </label>
		<h:inputTextarea value="#{planoEnsino.object.objetivos}"/>
	</li>
	
	<li>
		<label class="required">Ementa <span class="required"/> </label>
		<c:if test="${ not empty planoEnsino.object.turma.disciplina.detalhes.ementa }">
		${ planoEnsino.object.turma.disciplina.detalhes.ementa }
		</c:if>
		<c:if test="${ empty planoEnsino.object.turma.disciplina.detalhes.ementa }">
		Não há ementa cadastrada para este componente curricular
		</c:if>
	</li>
	
	<li>
		<label class="required">Metodologia <span class="required"/> </label>
		<h:inputTextarea value="#{planoEnsino.object.metodologia}"/>
	</li>
	
	<li>
		<label class="required">Bibliografia <span class="required"/> </label>
		<h:inputTextarea value="#{planoEnsino.object.bibliografia}"/>
	</li>

</ul>