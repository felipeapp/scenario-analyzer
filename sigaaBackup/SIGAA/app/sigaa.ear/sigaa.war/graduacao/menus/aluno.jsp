<ul>
	<li> Dados do Discente
		<ul>
		
		<%-- ESTAS DUAS OP��ES NAO DEVE IR PARA PRODU��O, ESTOU COMITANDO APENAS PARA A EQUIPE DE TESTES 
		<li> <h:commandLink action="#{questionario.iniciarCadastro}" value="Cadastrar Question�rio" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{questionario.listar}" value="Listar Question�rio" onclick="setAba('discente')"/> </li>
		<!-- ESTAS DUAS OP��ES NAO DEVE IR PARA PRODU��O, ESTOU COMITANDO APENAS PARA A EQUIPE DE TESTES -->
		<br/>
		--%>
		
		<li> <h:commandLink action="#{ alteracaoDadosDiscente.iniciar}" value="Atualizar Dados Pessoais" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{historicoDiscente.iniciar}" value="Consultar Dados do Aluno" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{ observacaoDiscente.iniciar}" value="Editar Observa��es do Discente" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{ alterarFormaIngresso.iniciar}" value="Alterar Dados de Ingresso" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{ indiceAcademicoMBean.buscarIndicesDiscente }" onclick="setAba('discente')" value="�ndices Acad�micos do Aluno"/> </li>
		</ul>
	</li>
	<li> Cadastrar Discente
		<ul>
		<li> <h:commandLink action="#{discenteGraduacao.iniciarCadastroDiscenteNovo}" value="Cadastrar Discente" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{discenteGraduacao.iniciarCadastroDiscenteAntigo}" value="Cadastrar Discente Antigo" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{cadastramentoDiscenteConvocadoMBean.iniciarCadastramento}" value="Cadastramento de Discentes Aprovados em Processos Seletivos" onclick="setAba('discente')" id="convocacaoVestibular_iniciarCadastramento"/></li>
		</ul>
	</li>
	<li> Documentos
		<ul>
		<li> <h:commandLink action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matr�cula" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink	action="#{ historico.buscarDiscente }"	value="Emitir Hist�rico" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink	action="#{ declaracaoVinculo.buscarDiscente }"	value="Emitir Declara��o de V�nculo/Cadastro" onclick="setAba('discente')"/> </li>
		</ul>
	</li>
	<li> Aproveitamento de Estudos
		<ul>
		<li> <h:commandLink action="#{aproveitamento.iniciarAproveitamento}" value="Aproveitar Componente Curricular" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{aproveitamentoAutomatico.iniciar}" value="Aproveitamento Autom�tico" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{aproveitamento.iniciarCancelamento}" value="Excluir Aproveitamento" onclick="setAba('discente')"/> </li>
		</ul>
	</li>
	<li> Transfer�ncia de Aluno entre Turmas
		<ul>
		<li> <h:commandLink action="#{transferenciaTurma.iniciarAutomatica}" value="Transfer�ncia Autom�tica" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{transferenciaTurma.iniciarManual}" value="Transfer�ncia Manual" onclick="setAba('discente')"/> </li>
		</ul>
	</li>
	<li>
		Notas/Retifica��es
		<ul>
			<li> <h:commandLink	action="#{retificacaoMatricula.iniciar}" value="Retificar Aproveitamento e Consolida��o de Turma" onclick="setAba('discente')"/> </li>
			<li> <h:commandLink action="#{consolidacaoIndividual.iniciar}" value="Consolida��o Individual" onclick="setAba('discente')"/> </li>
			<li> <h:commandLink action="#{implantarHistorico.iniciar}" value="Implantar Hist�rico do Aluno" onclick="setAba('discente')"/> </li>
		</ul>
	</li>
	<li> Outras Opera��es
		<ul>
		<li> <h:commandLink action="#{mudancaCurricular.iniciarMudancaMatriz}" value="Mudan�a de Curso/Matriz Curricular" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{mudancaCurricular.iniciarMudancaCurriculo}" value="Mudan�a de Estrutura Curricular" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{atenderTrancamentoMatricula.iniciarAtendimentoSolicitacao}" value="Orientar Trancamentos de Matr�cula" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{ alterarPoloDiscente.iniciarEad }" value="Transferir Discente EAD Entre P�los" onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{ alterarPoloDiscente.iniciarProbasica }" value="Transferir Discente PROB�SICA Entre P�los" onclick="setAba('discente')"/> </li>
		<li> <a href="${ctx}/ead/pessoa/lista.jsf">Alterar Dados Pessoais </a> </li>
		<li> <h:commandLink action="#{calculosDiscente.iniciar}" value="C�lculos de Discente"  onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{excluirDiscente.iniciar}" value="Excluir Aluno"  onclick="setAba('discente')"/> </li>
		<li> <h:commandLink action="#{etiquetasDiscentesBean.iniciar}" value="Gerar Etiquetas de Identifica��o"  onclick="setAba('discente')"/> </li>
		<li> <a href="${ctx}/graduacao/geracao_enade.jsf">Gera��o do Arquivo de Upload do ENADE </a> </li>
		</ul>
	</li>
	<li> Carteira de Estudante
		<ul>
			<li><a href="${ ctx }/arquivoSttu?nivel=G&log=true">Alunos com problema</a></li>
		</ul>
	</li>
	<li> Usu�rios de Discentes
		<ul>
			<li><h:commandLink value="Recuperar Senha" action="#{ recuperarSenhaDiscenteMBean.iniciar }"/></li>
		</ul>
	</li>
	<li> ENADE
		<ul>
			<li> <h:commandLink action="#{ calendarioEnadeMBean.listar}" value="Calend�rio de Cursos" onclick="setAba('discente')" id="calendarioEnadeMBean_preCadastrar"/> </li>
			<li> <h:commandLink action="#{ participacaoEnade.listar}" value="Listar/Cadastrar Participa��es no ENADE" onclick="setAba('discente')" id="participacaoEnade_listar"/> </li>
			<li> <h:commandLink action="#{ participacaoDiscenteEnade.iniciar}" value="Editar Participa��o do Discente no ENADE" onclick="setAba('discente')" id="participacaoEnadeparticipacaoEnade_iniciar"/> </li>
			<li> <h:commandLink action="#{ participacaoDiscenteEnade.iniciarLote}" value="Editar Participa��o do Discente no ENADE em Lote" onclick="setAba('discente')" id="participacaoEnadeparticipacaoEnade_iniciarLote"/> </li>
			<li> <h:commandLink action="#{ participacaoDiscenteEnade.iniciarLoteCurso}" value="Editar Participa��o do Discente no ENADE em Lote de Cursos" onclick="setAba('discente')" id="participacaoEnadeparticipacaoEnade_iniciarLoteCurso"/> </li>
		</ul>
	</li>
</ul>