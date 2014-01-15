
<ul>
	<li> Alunos
		<ul>
		<li> <h:commandLink action="#{ buscaAvancadaDiscenteMBean.iniciar}" value="Consulta Geral de Discentes"/></li>		
		<li> <h:commandLink action="#{ historicoDiscente.iniciar}" value="Consultar Dados do Aluno" onclick="setAba('coordenacao')"/> </li>
		<li> <h:commandLink action="#{ alteracaoDadosDiscente.iniciar}" value="Atualizar Dados Pessoais" onclick="setAba('coordenacao')"/> </li>		
		<li> <h:commandLink action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matrícula" onclick="setAba('coordenacao')"/> </li>
		<li> <h:commandLink	action="#{ historico.buscarDiscente }"	value="Emitir Histórico" onclick="setAba('coordenacao')"/> </li>
		<li> <h:commandLink	action="#{ declaracaoVinculo.buscarDiscente }"	value="Emitir Declaração de Vínculo" onclick="setAba('coordenacao')"/> </li>
    	<li><h:commandLink action="#{ matriculaGraduacao.iniciarMatriculaFerias}" value="Matricular Aluno em Turma de Férias" onclick="setAba('coordenacao')"/></li>
    	<li><h:commandLink action="#{ matriculaGraduacao.iniciarMatriculaRecemCadastrado}" value="Matricular Aluno Ingressante" onclick="setAba('coordenacao')"/></li>
    	<li><h:commandLink action="#{ matriculaGraduacao.iniciarMatriculaTurmasNaoMatriculaveis}" value="Matricular Aluno em Turmas Restritas" onclick="setAba('coordenacao')"/></li>
		</ul>
	</li>
	<li> Consultas Gerais
		<ul>
		<li> <h:commandLink action="#{ componenteCurricular.popularBuscaGeral }" value="Componentes Curriculares" onclick="setAba('coordenacao')"/> </li>
		<li> <a href="${ctx}/graduacao/curso/lista.jsf?aba=coordenacao">Cursos</a></li>
		<li> <a href="${ctx}/graduacao/curriculo/lista.jsf?aba=coordenacao">Estruturas Curriculares </a></li>
		<li> <a	href="${ctx}/administracao/cadastro/GrauAcademico/lista.jsf?aba=coordenacao">Graus Acadêmicos </a></li>
		<li> <a href="${ctx}/graduacao/habilitacao/lista.jsf?aba=coordenacao"> Habilitações</a> </li>
		<li> <a href="${ctx}/graduacao/matriz_curricular/lista.jsf?aba=coordenacao">Matrizes Curriculars</a> </li>
		<li> <a	href="${ctx}/administracao/cadastro/ModalidadeEducacao/lista.jsf?aba=coordenacao">Modalidades de Educação</a></li>
		<li> <a	href="${ctx}/administracao/cadastro/Municipio/lista.jsf?aba=coordenacao">Municípios	</a></li>
		<li> <a href="${ctx}/graduacao/reconhecimento/lista.jsf?aba=coordenacao">Reconhecimentos</a></li>
		<li> <h:commandLink action="#{ buscaTurmaBean.popularBuscaGeral }" value="Turmas" onclick="setAba('coordenacao')"/> </li>
		<li> <a href="${ctx}/administracao/cadastro/Turno/lista.jsf?aba=coordenacao">Turnos</a></li>
		</ul>
	</li>

	<li>Relatórios
		<ul>
		<li>
			<h:commandLink action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('coordenacao')" value="Alunos Ingressantes">
				<f:param value="seleciona_ingressantes.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Alunos Ativos por curso" action="#{relatorioPorCurso.iniciar}" onclick="setAba('coordenacao')" >
				<f:param name="relatorio" value="AlunosAtivosPorCurso"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink value="Alunos Concluintes" action="#{relatorioPorCurso.iniciar}" onclick="setAba('coordenacao')" >
				<f:param name="relatorio" value="AlunosConcluintes"/>
			</h:commandLink>
		</li>		
		<li>
		<h:commandLink action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('coordenacao')" value="Alunos Matriculados em uma Atividade">
			<f:param value="seleciona_matriculado_atividade.jsf" name="relatorio"/>
		</h:commandLink>
		</li>
		<li>
			<h:commandLink action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('coordenacao')" value="Lista de Insucessos em disciplinas por semestre">
				<f:param value="seleciona_insucessos.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li><h:commandLink action="#{relatorioTurma.iniciarRelatorioListaTurmasOfertadasCurso}" value="Relatório de Turmas Ofertadas ao Curso" onclick="setAba('coordenacao')"/></li>
		<li><h:commandLink action="#{relatoriosCoordenador.relatorioTrancamentos}" value="Relatório de Trancamentos no Semestre" onclick="setAba('coordenacao')"/></li>
		<li><h:commandLink action="#{relatoriosCoordenador.relatorioTurmasConsolidadas}" value="Relatório de Turmas Consolidadas" onclick="setAba('coordenacao')"/></li>
		<li><h:commandLink action="#{relatoriosCoordenador.relatorioReprovacoesDisciplinas}" value="Relatório de Disciplinas com mais Reprovações" onclick="setAba('coordenacao')"/></li>
		<li>
			<h:commandLink action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('coordenacao')" value="Lista para eleição">
				<f:param value="seleciona_eleicao.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		<li>
			<h:commandLink action="#{relatorioDiscente.carregarSelecaoRelatorio}"  onclick="setAba('coordenacao')" value="Por Tipo de saída ">
				<f:param value="seleciona_tipo_saida.jsf" name="relatorio"/>
			</h:commandLink>
		</li>
		</ul>
	</li>
</ul>