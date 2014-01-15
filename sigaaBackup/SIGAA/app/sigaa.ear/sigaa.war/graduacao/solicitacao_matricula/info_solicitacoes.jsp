	<div class="descricaoOperacao">
		<p>
		Caro ${analiseSolicitacaoMatricula.portalCoordenadorGraduacao or analiseSolicitacaoMatricula.portalCoordenadorStricto ? 'Coordenador' : 'Orientador'},
		</p><br>
		<c:if test="${analiseSolicitacaoMatricula.discente.graduacao}">
			<p> Esta opera��o mostra a lista de componentes que o aluno ir� se matricular.
			Voc� pode orientar o aluno para que ele desista da matr�cula por algum motivo
			ou apenas marcar a matr�cula como vista.
			</p>
			<p>
			Ap�s a data final do per�odo de matr�cula, o discente ser� automaticamente matriculado "EM ESPERA"
			em todas as turmas escolhidas e exibidas abaixo. At� a data final desse per�odo,
			� permitido ao discente escolher outras turmas ou remover as j� selecionadas.
			</p>
			<p>
			<b>Os orientadores acad�micos e coordenadores do  curso</b> poder�o fazer observa��es sobre as turmas
			escolhidas at� <ufrn:format type="data" valor="${calendarioAcademico.fimCoordenacaoAnaliseMatricula}"/>.
			� importante lembrar que essas observa��es t�m o objetivo somente de orientar os discentes, nenhuma orienta��o
			� capaz de cancelar ou excluir uma matr�cula escolhida pelo discente. Apenas o pr�prio discente pode excluir
			as matr�culas nas turmas escolhidas at� o prazo final da matr�cula on-line.
			</p>
			<p>
			Atrav�s desta opera��o, a coordena��o pode orientar o aluno para que ele fa�a a melhor escolha.
			</p>			
			<p>
			<b>O chefe de departamento</b> pode aceitar ou impedir que o aluno especial realize a matr�cula nos componentes. Se o chefe aceitar, a solicita��o do aluno participar� do processamento de matr�cula da gradua��o.
			</p>			
		</c:if>
		<c:if test="${!analiseSolicitacaoMatricula.discente.graduacao}">
			<c:if test="${!analiseSolicitacaoMatricula.analiseOutroPrograma}">
				<p> Esta opera��o mostra a lista de componentes que o aluno ir� se matricular.
				Voc� pode aprovar ou negar a matricula na(s) disciplina(s) selecionada(s) pelo aluno.
				Caso a disciplina que o aluno deseja se matricular seja de outro programa ap�s a sua aprova��o 
				ser� necess�rio a aprova��o do outro programa para que a matr�cula seja efetivada. 
				</p>
			</c:if>
			
			<c:if test="${analiseSolicitacaoMatricula.analiseOutroPrograma}">
				<p> Esta opera��o mostra a lista de componentes do seu programa que o aluno selecionado deseja se matricular.
				Voc� pode aprovar ou negar a matricula na(s) disciplina(s) selecionada(s) pelo aluno.
				As matr�cula do aluno nas disciplinas listadas j� foram aprovadas por seu respectivo orientador.
				</p>
			</c:if>
			
		</c:if>
	</div>