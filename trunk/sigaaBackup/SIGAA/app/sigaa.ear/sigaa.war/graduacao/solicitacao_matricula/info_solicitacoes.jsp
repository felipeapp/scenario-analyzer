	<div class="descricaoOperacao">
		<p>
		Caro ${analiseSolicitacaoMatricula.portalCoordenadorGraduacao or analiseSolicitacaoMatricula.portalCoordenadorStricto ? 'Coordenador' : 'Orientador'},
		</p><br>
		<c:if test="${analiseSolicitacaoMatricula.discente.graduacao}">
			<p> Esta operação mostra a lista de componentes que o aluno irá se matricular.
			Você pode orientar o aluno para que ele desista da matrícula por algum motivo
			ou apenas marcar a matrícula como vista.
			</p>
			<p>
			Após a data final do período de matrícula, o discente será automaticamente matriculado "EM ESPERA"
			em todas as turmas escolhidas e exibidas abaixo. Até a data final desse período,
			é permitido ao discente escolher outras turmas ou remover as já selecionadas.
			</p>
			<p>
			<b>Os orientadores acadêmicos e coordenadores do  curso</b> poderão fazer observações sobre as turmas
			escolhidas até <ufrn:format type="data" valor="${calendarioAcademico.fimCoordenacaoAnaliseMatricula}"/>.
			É importante lembrar que essas observações têm o objetivo somente de orientar os discentes, nenhuma orientação
			é capaz de cancelar ou excluir uma matrícula escolhida pelo discente. Apenas o próprio discente pode excluir
			as matrículas nas turmas escolhidas até o prazo final da matrícula on-line.
			</p>
			<p>
			Através desta operação, a coordenação pode orientar o aluno para que ele faça a melhor escolha.
			</p>			
			<p>
			<b>O chefe de departamento</b> pode aceitar ou impedir que o aluno especial realize a matrícula nos componentes. Se o chefe aceitar, a solicitação do aluno participará do processamento de matrícula da graduação.
			</p>			
		</c:if>
		<c:if test="${!analiseSolicitacaoMatricula.discente.graduacao}">
			<c:if test="${!analiseSolicitacaoMatricula.analiseOutroPrograma}">
				<p> Esta operação mostra a lista de componentes que o aluno irá se matricular.
				Você pode aprovar ou negar a matricula na(s) disciplina(s) selecionada(s) pelo aluno.
				Caso a disciplina que o aluno deseja se matricular seja de outro programa após a sua aprovação 
				será necessário a aprovação do outro programa para que a matrícula seja efetivada. 
				</p>
			</c:if>
			
			<c:if test="${analiseSolicitacaoMatricula.analiseOutroPrograma}">
				<p> Esta operação mostra a lista de componentes do seu programa que o aluno selecionado deseja se matricular.
				Você pode aprovar ou negar a matricula na(s) disciplina(s) selecionada(s) pelo aluno.
				As matrícula do aluno nas disciplinas listadas já foram aprovadas por seu respectivo orientador.
				</p>
			</c:if>
			
		</c:if>
	</div>