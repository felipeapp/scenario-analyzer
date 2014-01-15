<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>
<ufrn:subSistema teste="portalCoordenadorGrad">
<c:if test="${acesso.coordenadorCursoGrad || acesso.secretarioGraduacao || acesso.coordenacaoProbasica}">
<h:form id="menu_coordenador">
<div id="menu-dropdown">
<input type="hidden" id="jscook_action" name="jscook_action" />
<t:jscookMenu layout="hbr" theme="ThemeOffice" styleLocation="/css/jscookmenu">
	<t:navigationMenuItem id="matriculas" itemLabel="Matr�culas" icon="/img/graduacao/coordenador/matricula.png">
		<t:navigationMenuItem id="analisarSolicitacoesMatricula" itemLabel="Analisar Solicita��es de Matr�cula"  icon="/img/graduacao/coordenador/analisar_solicitacoes.png" rendered="#{!acesso.coordenacaoProbasica && !portalCoordenadorGrad.cursoAtualCoordenacao.probasica}">
			<t:navigationMenuItem id="analisarSolicitacoesPendentes" itemLabel="Analisar Solicita��es Pendentes" action="#{ analiseSolicitacaoMatricula.iniciar}" />
			<t:navigationMenuItem id="consultarSolicitacoesDiscente" itemLabel="Consultar Solicita��es de um Discente" action="#{ analiseSolicitacaoMatricula.iniciarBuscaDiscente}"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem id="matricularAlunoAtivo" itemLabel="Matricular Aluno Ativo" action="#{ matriculaGraduacao.iniciarMatriculasRegulares}" icon="/img/graduacao/coordenador/matricular.png" rendered="#{!acesso.coordenacaoProbasica and !portalCoordenadorGrad.cursoAtualCoordenacao.probasica && portalCoordenadorGrad.cursoAtualCoordenacao.podeMatricular}"/>
		<t:navigationMenuItem id="matricularTurmasRestritas" itemLabel="Matricular Em Turmas Restritas" action="#{ matriculaGraduacao.iniciarMatriculaTurmasNaoMatriculaveis}" icon="/img/graduacao/coordenador/matricular.png" rendered="#{!acesso.cursoEad && !acesso.coordenacaoProbasica && !portalCoordenadorGrad.cursoAtualCoordenacao.probasica}"/>
		<t:navigationMenuItem id="matricularAlunoEAD" itemLabel="Matricular Aluno EAD" action="#{ matriculaGraduacao.iniciarMatriculaEAD}" icon="/img/graduacao/coordenador/matricular.png" rendered="#{acesso.cursoEad and !portalTutor.alunoEadFazMatriculaOnline }"/>
		<t:navigationMenuItem id="cancelarMatriculaAlunoEAD" itemLabel="Cancelar Matr�cula de Aluno EAD" action="#{ alteracaoStatusMatricula.iniciarCancelamentoMatricula}" icon="/img/graduacao/coordenador/excluir.png" rendered="#{acesso.cursoEad and !portalTutor.alunoEadFazMatriculaOnline }"/>
		<t:navigationMenuItem id="matricularAlunoProbasica" itemLabel="Matricular Aluno PROBASICA" action="#{ matriculaGraduacao.iniciarMatriculaConvenio}" icon="/img/graduacao/coordenador/matricular.png" rendered="#{acesso.coordenacaoProbasica or  portalCoordenadorGrad.cursoAtualCoordenacao.probasica}"/>
		<t:navigationMenuItem id="matricularAlunoTurmaFerias" itemLabel="Matricular Aluno Em Turma de F�rias" action="#{ matriculaGraduacao.iniciarMatriculaFerias}" icon="/img/graduacao/coordenador/matricular.png"  rendered="#{!acesso.coordenacaoProbasica && !portalCoordenadorGrad.cursoAtualCoordenacao.probasica && portalCoordenadorGrad.acessoMatriculaTurmaFerias}" />
		<t:navigationMenuItem id="orientarTrancamentoMatriculas" itemLabel="Orientar Trancamentos de Matr�cula" action="#{ atenderTrancamentoMatricula.iniciarAtendimentoSolicitacaoGraduacao }" icon="/img/graduacao/coordenador/confirmar_trancamento.png"  rendered="#{!acesso.coordenacaoProbasica && !portalCoordenadorGrad.cursoAtualCoordenacao.probasica && !acesso.cursoEad }" />
		<t:navigationMenuItem id="confirmarTrancamentosMatricula" itemLabel="Confirmar Trancamentos de Matr�cula" action="#{ atenderTrancamentoMatricula.iniciarAtendimentoSolicitacaoEad }" icon="/img/graduacao/coordenador/confirmar_trancamento.png"  rendered="#{acesso.cursoEad}" />
		<t:navigationMenuItem id="PermitirExtrapolarCreditosMinimoMaximo" itemLabel="Permitir Extrapolar Cr�ditos M�nimo e M�ximo" action="#{ extrapolarCredito.iniciar }" icon="/img/graduacao/coordenador/matricular.png" />
		<t:navigationMenuItem id="ingressantes" itemLabel="Alunos Ingressantes" icon="/img/icones/graduacao.gif">
			<t:navigationMenuItem id="matricularAlunoIngressante" itemLabel="Matricular Aluno Ingressante" action="#{ matriculaGraduacao.iniciarMatriculaRecemCadastrado}" icon="/img/graduacao/coordenador/matricular.png" rendered="#{!acesso.cursoEad && !acesso.coordenacaoProbasica && !portalCoordenadorGrad.cursoAtualCoordenacao.probasica}"/>
			<t:navigationMenuItem id="validacaoVinculoIngressante" itemLabel="Valida��o de V�nculos de Ingressante" action="#{validacaoVinculo.iniciar}" icon="/img/graduacao/coordenador/matricular.png" rendered="#{!acesso.cursoEad && !acesso.coordenacaoProbasica && !portalCoordenadorGrad.cursoAtualCoordenacao.probasica}"/>
			<t:navigationMenuItem id="planoMatricula" itemLabel="Plano de Matr�cula" split="true" icon="/img/report.png">
				<t:navigationMenuItem id="planoMatriculaTurmasIngressantes" itemLabel="Gerenciar Planos de Matr�culas" action="#{ planoMatriculaIngressantesMBean.listar }" rendered="#{!acesso.cursoEad}" icon="/img/requisicoes.png"/>
				<t:navigationMenuItem id="consultarPlanosMatricula" itemLabel="Consultar Planos de Matr�cula por Discente" action="#{ planoMatriculaBean.buscarDiscente }" icon="/img/buscar.gif"/>
			</t:navigationMenuItem>
		</t:navigationMenuItem>		
	</t:navigationMenuItem>
	<t:navigationMenuItem id="atividades" itemLabel="Atividades" icon="/img/graduacao/coordenador/ativ_acam_especifica.png">
		<t:navigationMenuItem id="orientacoesAtividades" itemLabel="Orienta��es de Atividades" action="#{orientacaoAtividade.iniciarBusca}" icon="/img/report.png" />
		<t:navigationMenuItem id="matricular" itemLabel="Matricular" action="#{ registroAtividade.iniciarMatricula}" icon="/img/graduacao/coordenador/matricular.png" split="true"/>
		<t:navigationMenuItem id="consolidarMatriculas" itemLabel="Consolidar Matr�culas" action="#{ registroAtividade.iniciarConsolidarMatriculas}" icon="/img/graduacao/coordenador/consolidar.png"/>
		<t:navigationMenuItem id="validar" itemLabel="Validar" action="#{ registroAtividade.iniciarValidacao}" icon="/img/graduacao/coordenador/validar.png" />
		<t:navigationMenuItem id="excluir" itemLabel="Excluir" action="#{ registroAtividade.iniciarExclusao}" icon="/img/graduacao/coordenador/excluir.png" />
		<t:navigationMenuItem id="trabalhoFimCurso" itemLabel="Trabalho de Fim de Curso"  split="true">
			<t:navigationMenuItem id="alterarTrabalhoFimCurso" itemLabel="Alterar Trabalho de Fim de Curso" action="#{ registroAtividade.iniciarAlterarTrabalhoEstagio }" />
			<t:navigationMenuItem id="cadastrarBanca" itemLabel="Cadastrar Banca" action="#{ bancaDefesaMBean.iniciarCadastro }" split="true"/>
			<t:navigationMenuItem id="consultarBancas" itemLabel="Consultar Bancas" action="#{ buscaBancaDefesaMBean.iniciar }" />
			<t:navigationMenuItem id="declaracaoParticipacaoBancas" itemLabel="Declara��o de Participa��o de Bancas" action="#{ declaracaoParticipacaoBanca.iniciar }" split="true"/>
		</t:navigationMenuItem>		
		<t:navigationMenuItem id="alterarEstagio" itemLabel="Alterar Est�gio ou Trabalho de Conclus�o de Curso" action="#{ registroAtividade.iniciarAlterarTrabalhoEstagio}"/>
		<t:navigationMenuItem id="solicitarCadastroAtividade" itemLabel="Solicitar Cadastro de Atividade" action="#{componenteCurricular.preCadastrar}" icon="/img/graduacao/coordenador/solicitar.png" split="true"/>
		<t:navigationMenuItem id="minhasSolicitacoesCadastroAtividade" itemLabel="Minhas Solicita��es de Cadastro de Atividade" action="#{autorizacaoComponente.verMinhasSolicitacoes}" icon="/img/graduacao/coordenador/listar.png"/>
	</t:navigationMenuItem>
	<t:navigationMenuItem id="aluno" itemLabel="Aluno" icon="/img/graduacao/coordenador/aluno.png" >
		<t:navigationMenuItem id="consultaAvancada" itemLabel="Consulta Avan�ada" action="#{ buscaAvancadaDiscenteMBean.iniciarCoordGraduacao }"/>
		<t:navigationMenuItem id="atualizarDadosPessoais" itemLabel="Atualizar Dados Pessoais" action="#{ alteracaoDadosDiscente.iniciar}" icon="/img/graduacao/coordenador/atualizar.png"/>
		<t:navigationMenuItem id="atualizarDadosPessoaisIngressantes" itemLabel="Atualizar Dados Pessoais de Ingressantes" action="#{ alteracaoDadosDiscente.iniciarIngressantes}" icon="/img/graduacao/coordenador/atualizar.png"/>
		<t:navigationMenuItem id="DiscenteNEE" itemLabel="Discente com NEE" icon="/img/acessibilidade.png">
			<t:navigationMenuItem id="cadastrarDiscenteNEE" itemLabel="Solicitar Apoio a CAENE" action="#{ solicitacaoApoioNee.preCadastrarDiscenteNee }" />
			<t:navigationMenuItem id="AlterarDiscenteNEE" itemLabel="Alterar Solicita��o de Apoio" action="#{ solicitacaoApoioNee.listarSolicitacoesDiscente }" />
			<t:navigationMenuItem id="parecerNEE" itemLabel="Solicita��es Enviadas para CAENE" action="#{ solicitacaoApoioNee.listaAllSolicitacoes }" />
			<t:navigationMenuItem id="manualNEE"   itemLabel="Manual de Solicita��o de Apoio"  action="" icon="/img/report.png"/>           
		</t:navigationMenuItem>			
		<t:navigationMenuItem id="exportarPlanilhaNotasSemestre" itemLabel="Exportar Planilha de Notas do Semestre" action="#{ exportarNotasDiscente.iniciar}" icon="/img/report.png"/>		
		<t:navigationMenuItem id="emitirAtestadoMatricula" itemLabel="Emitir Atestado de Matr�cula" action="#{ atestadoMatricula.buscarDiscente }" icon="/img/graduacao/coordenador/documento.png" split="true"/>
		<t:navigationMenuItem id="emitirHistorico" itemLabel="Emitir Hist�rico" action="#{ historico.buscarDiscente }" icon="/img/graduacao/coordenador/documento.png"/>
		<t:navigationMenuItem id="emitirDeclaracaoVinculo" itemLabel="Emitir Declara��o de V�nculo" action="#{ declaracaoVinculo.buscarDiscente }" icon="/img/graduacao/coordenador/documento.png"/>
		<t:navigationMenuItem id="relatorioIndices" itemLabel="Relat�rio dos �ndices Acad�micos do Aluno" action="#{ indiceAcademicoMBean.buscarIndicesDiscente }" icon="/img/graduacao/coordenador/documento.png"/>		
		<t:navigationMenuItem id="cadastrarOrientacaoAcademica" itemLabel="Cadastrar Orienta��o Acad�mica" action="#{ orientacaoAcademica.iniciar }" split="true"/>
		<t:navigationMenuItem id="gerenciarOrientacoesAcademicas" itemLabel="Gerenciar Orienta��es Acad�micas" action="#{ orientacaoAcademica.gerenciarOrientacoes }"/>
		<t:navigationMenuItem id="forumCursos" split="true" itemLabel="F�rum de Cursos" action="#{ forum.listarForunsCurso }"/>
		<t:navigationMenuItem id="trabalhoFinalCurso" itemLabel="Trabalho Final de Curso"  action="#{trabalhoFimCurso.listar }"/>
	</t:navigationMenuItem>
	
	<t:navigationMenuItem id="turmas" itemLabel="Turmas" icon="/img/graduacao/coordenador/turma.png">
		<t:navigationMenuItem id="solicitarAberturaTurmas" itemLabel="Solicitar Abertura de Turmas Regulares" action="#{ solicitacaoTurma.iniciarRegular }" icon="/img/graduacao/coordenador/turma_sol.png" rendered="#{!acesso.cursoEad}"/>
		<t:navigationMenuItem id="solicitarAberturaTurmasFerias" itemLabel="Solicitar Abertura de Turmas de F�rias" icon="/img/graduacao/coordenador/turma_sol.png" rendered="#{!acesso.cursoEad && !acesso.coordenacaoProbasica && !portalCoordenadorGrad.cursoAtualCoordenacao.probasica}">
			<t:navigationMenuItem id="visualizarSolicitacoesAlunos" itemLabel="Visualizar Solicita��es dos Alunos" action="#{ solicitacaoTurma.visualizarSolicitacaoFerias }" icon="/img/graduacao/coordenador/turma_sol.png" rendered="#{!acesso.cursoEad && !acesso.coordenacaoProbasica && !portalCoordenadorGrad.cursoAtualCoordenacao.probasica}"/>
			<t:navigationMenuItem id="SolicitarTurmaFerias" itemLabel="Solicitar Turma de F�rias" action="#{ solicitacaoTurma.iniciarSolicitacaoFeriasSemSolicitacao }" icon="/img/graduacao/coordenador/turma_sol.png" rendered="#{!acesso.cursoEad && !acesso.coordenacaoProbasica && !portalCoordenadorGrad.cursoAtualCoordenacao.probasica && solicitacaoTurma.permiteSolicitarTurmaSemSolicitacao}"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="solicitacoesEnsinoIndividual" itemLabel="Solicitar Abertura de Turmas de Ensino Individual" icon="/img/graduacao/coordenador/turma_sol.png" rendered="#{!acesso.cursoEad && !acesso.coordenacaoProbasica && !portalCoordenadorGrad.cursoAtualCoordenacao.probasica}">
			<t:navigationMenuItem id="solicitarTurmaEnsinoIndividual" itemLabel="Solicitar Turma de Ensino Individual" action="#{ solicitacaoTurma.iniciarSolicitacaoEnsinoIndividual }" icon="/img/graduacao/coordenador/turma_sol.png"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="visualizarSolicitacoesEnviadas" itemLabel="Visualizar Solicita��es de Abertura de Turmas Enviadas" action="#{ solicitacaoTurma.listar }" icon="/img/graduacao/coordenador/listar.png" rendered="#{!acesso.cursoEad }"/>
		<t:navigationMenuItem id="consultarTurmas" itemLabel="Consultar Turmas" actionListener="#{menuDocente.consultaTurma}" itemDisabled="false"/>
		<t:navigationMenuItem id="cadastrarTurmaPROBASICA" itemLabel="Cadastrar Turma PROB�SICA" action="#{turmaGraduacaoBean.iniciarProbasica}" itemDisabled="false" rendered="#{acesso.coordenacaoProbasica or portalCoordenadorGrad.cursoAtualCoordenacao.probasica}"/>
		<t:navigationMenuItem id="cadastrarTurma" itemLabel="Cadastrar Turma" action="#{turmaGraduacao.preCadastrar}" itemDisabled="false" rendered="false"/>
		<t:navigationMenuItem id="alterarOuRemoverTurma" itemLabel="Alterar/Remover Turma PROB�SICA" rendered="#{acesso.coordenacaoProbasica || portalCoordenadorGrad.cursoAtualCoordenacao.probasica}" action="#{buscaTurmaBean.popularBuscaGeral}" itemDisabled="false"/>		
		<t:navigationMenuItem id="chefTur_AltRemTurma" itemLabel="Alterar/Remover Turma" action="#{buscaTurmaBean.popularBuscaGeral}" itemDisabled="false" split="true" />
		<t:navigationMenuItem id="chefTur_CriarTurma" itemLabel="Criar Turma Sem Solicita��o" action="#{turmaGraduacaoBean.iniciarTurmaSemSolicitacao}" itemDisabled="false"/>
		<t:navigationMenuItem id="chefTur_Transferir" itemLabel="Transferir Alunos entre turmas" action="#{transferenciaTurma.iniciarAutomatica}" itemDisabled="false"/>
		<t:navigationMenuItem id="chefTur_Gerenciar" itemLabel="Gerenciar Solicita��es de Turmas">
		<t:navigationMenuItem id="chefTurGer_Regular" itemLabel="Turmas Regulares" action="#{analiseSolicitacaoTurma.gerenciarSolicitacoesRegulares}" itemDisabled="false"/>
			<t:navigationMenuItem id="chefTurGer_Ferias" itemLabel="Turmas de F�rias" action="#{analiseSolicitacaoTurma.gerenciarSolicitacoesFerias}" itemDisabled="false"/>
			<t:navigationMenuItem id="chefTurGer_EnsIndividual" itemLabel="Turmas de Ensino Individual" action="#{analiseSolicitacaoTurma.gerenciarSolicitacoesEnsinoIndividual}" itemDisabled="false"/>
			<t:navigationMenuItem id="chefTurGer_Todas" itemLabel="Ver Todas" action="#{analiseSolicitacaoTurma.gerenciarSolicitacoesTodas}" itemDisabled="false"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="chefCompCur_CadastrarProg" itemLabel="Cadastrar Programa de Componente" action="#{programaComponente.iniciar}" itemDisabled="false" split="true"/>
		<t:navigationMenuItem id="chefCompCur_ConsultarComp" itemLabel="Consultar Componentes com Programas Cadastrados" action="#{relatorioPorDepartamento.iniciarRelatorioComponentesComPrograma}" itemDisabled="false"/>
		
	</t:navigationMenuItem>
	<t:navigationMenuItem id="relatorios" itemLabel="Relat�rios" icon="/img/graduacao/coordenador/relatorios.png">
		<t:navigationMenuItem id="discentes" itemLabel="Discentes">
			<t:navigationMenuItem id="alunosAtivosCurso" itemLabel="Alunos Ativos no Curso" action="#{ relatoriosCoordenador.relatorioAlunosAtivosCurso }" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="alunosPendentesMatricula" itemLabel="Alunos Pendentes de Matr�cula" action="#{ relatoriosCoordenador.relatorioAlunosPendenteMatricula }" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="alunosPendentesComponenteCurricular" itemLabel="Alunos Pendentes de Componente Curricular" action="#{ relatorioAlunosPendentesDeComponente.iniciar }" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="alunosAptosACursarDeterminadoComponenteCurricular" itemLabel="Alunos Aptos a Cursar Determinado Componente Curricular" action="#{ relatorioAlunosPendentesDeComponente.iniciarAptos }" icon="/img/graduacao/coordenador/relatorio_item.png"/>			
			<t:navigationMenuItem id="alunosGraduandos" itemLabel="Alunos Graduandos" action="#{relatoriosCoordenador.relatorioAlunosConcluintes}" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="alunosFormandos" itemLabel="Alunos Formandos" action="#{relatoriosCoordenador.relatorioAlunosFormandos}" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="listaAlunosEleicao" itemLabel="Lista de Alunos para Elei��o" actionListener="#{menuDocente.redirecionar}" itemValue="/graduacao/relatorios/discente/seleciona_eleicao.jsf" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="listaAlunosTipoSaida" itemLabel="Lista de Alunos por Tipo de Sa�da" actionListener="#{menuDocente.redirecionar}" itemValue="/graduacao/relatorios/discente/seleciona_tipo_saida.jsf" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="listaInsucessos" itemLabel="Lista de Insucessos" actionListener="#{menuDocente.redirecionar}" itemValue="/graduacao/relatorios/discente/seleciona_insucessos.jsf" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="listaIngressantes" itemLabel="Lista de Ingressantes" actionListener="#{menuDocente.redirecionar}" itemValue="/graduacao/relatorios/discente/seleciona_ingressantes.jsf" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="listaEmailAlunosVinculo" itemLabel="Lista de Email dos Alunos com V�nculo" action="#{relatorioDiscente.relatorioEmailDosAlunos}" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="listaEmailAlunosConcluidos" itemLabel="Lista de Email dos Alunos Conclu�dos" action="#{relatorioDiscente.iniciarRelatorioEmailAlunoConcluidos}" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="alunosAtividadesExtensaoMonitoriaPesquisa" itemLabel="Alunos em Atividades de Extens�o, Monitoria e Pesquisa" action="#{relatorioAlunosExtensaoMonitoriaPesquisaBean.iniciarCoordenador}" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="alunosPercentualCHCumprida" itemLabel="Alunos com Percentual de CH Cumprida" action="#{ relatoriosCoordenador.relatorioPercentualCHAluno }" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="discentesComDigitalCadastrada" itemLabel="Discentes Com Digital Cadastrada" action="#{relatoriosDigitalMBean.gerarRelatorioDiscentesComDigitalCadastrada}" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="discentesSemDigitalCadastrada" itemLabel="Discentes Sem Digital Cadastrada" action="#{relatoriosDigitalMBean.gerarRelatorioDiscentesSemDigitalCadastrada}" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="discentesSemOrientacaoAcademica" itemLabel="Discentes Sem Orienta��o Acad�mica" action="#{orientacaoAcademica.gerarRelatorioSemOrientacaoAcademica}" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="bolsistasContemplados" itemLabel="Bolsistas Deferidos/Contemplados" action="#{relatoriosSaeMBean.iniciarRelatorioContempladosDeferidos}" icon="/img/graduacao/coordenador/relatorio_item.png"/> 			
		</t:navigationMenuItem>
		<t:navigationMenuItem id="docentes" itemLabel="Docentes">
			<t:navigationMenuItem id="listaDocentesEleicao" itemLabel="Lista de Docentes para Elei��o" action="#{relatorioEleicaoCoordenadoMBean.iniciarListaDocenteEleicaoCoordenador}" itemValue="/graduacao/relatorios/docente/seleciona_eleicao.jsf" icon="/img/graduacao/coordenador/relatorio_item.png"/>
		</t:navigationMenuItem>	
		<t:navigationMenuItem id="matriculasETrancamentos" itemLabel="Matr�culas e Trancamentos">
			<t:navigationMenuItem id="alunosTrancamentoDeterminadoComponente" itemLabel="Alunos com Trancamento em Determinado Componente" actionListener="#{menuDocente.redirecionar}" itemValue="/graduacao/relatorios/discente/seleciona_trancamentos_componente.jsf" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="trancamentosNoSemestre" itemLabel="Trancamentos no Semestre" action="#{ relatoriosCoordenador.relatorioTrancamentos }" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="trancamentodeMatriculasPorMotivo" itemLabel="Trancamento de Matr�culas por Motivo" action="#{relatorioTrancamentoTurma.iniciarRelatorio}" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="matriculasOnlineNaoOrientadas" itemLabel="Matr�culas Online n�o Orientadas" action="#{ relatoriosCoordenador.relatorioMatriculasNaoAtendidas }" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="matriculadosEmDeterminadoPeriodo" itemLabel="Matriculados em Determinado Per�odo" actionListener="#{menuDocente.redirecionar}" itemValue="/graduacao/relatorios/discente/seleciona_matriculados.jsf" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="matriculadosEmAtividades" itemLabel="Matriculados em Atividades" actionListener="#{menuDocente.redirecionar}" itemValue="/graduacao/relatorios/discente/seleciona_matriculado_atividade.jsf" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="consultarIndeferimentos" itemLabel="Consultar Indeferimentos" action="#{listaIndeferimentos.iniciar}" icon="/img/graduacao/coordenador/relatorio_item.png"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="turmasEComponentes" itemLabel="Turmas e Componentes Curriculares">
			<t:navigationMenuItem id="turmasConsolidadas" itemLabel="Turmas Consolidadas" action="#{ relatoriosCoordenador.relatorioTurmasConsolidadas }" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="turmasOfertadasAoCurso" itemLabel="Turmas Ofertadas ao Curso" action="#{ relatorioTurma.iniciarRelatorioListaTurmasOfertadasCurso }" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="disciplinasComMaisReprovacoes" itemLabel="Disciplinas com mais Reprova��es" action="#{ relatoriosCoordenador.relatorioReprovacoesDisciplinas }" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="relatorioOcupa��oVagasTurmas" itemLabel="Relat�rio de Ocupa��o de Vagas de Turmas" action="#{relatorioTurma.iniciarRelatorioOcupacaoVagas}" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="elatorioDisciplinasEmentas" itemLabel="Relat�rio de Disciplinas com Ementas" action="#{ relatoriosCoordenador.relatorioEmentaDisciplinasCurso }" icon="/img/graduacao/coordenador/relatorio_item.png"/>
			<t:navigationMenuItem id="relatorioEquivalencia" itemLabel="Relat�rio de Equival�ncias" action="#{ relatoriosCoordenador.relatorioEquivalencias }" icon="/img/graduacao/coordenador/relatorio_item.png"/>
		</t:navigationMenuItem>
	</t:navigationMenuItem>
	<t:navigationMenuItem id="consultas" itemLabel="Consultas" icon="/img/buscar.gif">
		<t:navigationMenuItem id="consultas_relatoriosTurmas" actionListener="#{menu.redirecionar}" itemValue="/graduacao/relatorios/turma/seleciona_turma.jsf?aba=relatorios" itemLabel="Relat�rio de Turmas"/>
		<t:navigationMenuItem id="consultas_componentesCurriculares" action="#{ componenteCurricular.popularBuscaGeral }" itemLabel="Componentes Curriculares"/>
		<t:navigationMenuItem id="consultas_turmas" action="#{buscaTurmaBean.popularBuscaGeral}" itemLabel="Turmas"/>
		<t:navigationMenuItem id="consultas_estruturasCurriculares" actionListener="#{menu.redirecionar}" itemValue="/graduacao/curriculo/lista.jsf?aba=consultas" itemLabel="Estruturas Curriculares"/>
		<t:navigationMenuItem id="consultas_habilitacoes" actionListener="#{menu.redirecionar}" itemValue="/graduacao/habilitacao/lista.jsf?aba=consultas" itemLabel="Habilita��es"/>
		<t:navigationMenuItem id="consultas_matrizesCurriculares" actionListener="#{menu.redirecionar}" itemValue="/graduacao/matriz_curricular/lista.jsf?aba=consultas" itemLabel="Matrizes Curriculares"/>
		<t:navigationMenuItem id="consultas_cursos" actionListener="#{menu.redirecionar}" itemValue="/graduacao/curso/lista.jsf" itemLabel="Cursos"/>
		<t:navigationMenuItem id="consultas_calendariosAcademicos" action="#{calendario.iniciarBusca}" itemLabel="Consulta de Calend�rios Acad�micos"/>
	</t:navigationMenuItem>
	
	<t:navigationMenuItem id="estagio" itemLabel="Est�gio" icon="/img/estagio/estagio_menu.png">
		<t:navigationMenuItem id="convenios" itemLabel="Conv�nio de Est�gio" >
			<t:navigationMenuItem id="cadConvenioEstagio" itemLabel="Solicitar Conv�nio de Est�gio" action="#{ convenioEstagioMBean.iniciar }"/>
			<t:navigationMenuItem id="consultarConvenioEstagio" itemLabel="Consultar Conv�nio de Est�gio" action="#{ convenioEstagioMBean.iniciarConsulta }"/>		
		</t:navigationMenuItem>
		<t:navigationMenuItem id="ofertaEstagio" itemLabel="Oferta de Est�gio" >
			<t:navigationMenuItem id="cadOfertaEstagio" itemLabel="Cadastrar Oferta de Est�gio" action="#{ ofertaEstagioMBean.iniciar }"/>
			<t:navigationMenuItem id="consultarOfertaEstagio" itemLabel="Consultar Oferta de Est�gio" action="#{ ofertaEstagioMBean.iniciarConsulta }"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="gerenciarEstagios" itemLabel="Gerenciar Estagi�rios #{coordenacaoCurso.qtdConveniosPendentesAnalise}" action="#{buscaEstagioMBean.iniciar}" split="true"/>		
		<t:navigationMenuItem id="cadastrarEstagios" itemLabel="Cadastrar Estagi�rios Avulso" action="#{estagioMBean.iniciarCadastroAvulso}"/>				
	</t:navigationMenuItem>	
	
	<t:navigationMenuItem id="biblioteca" itemLabel="Biblioteca" icon="/img/icones/biblioteca_menu.gif" rendered="#{acesso.coordenadorCursoGrad}">
		<%--
		<t:navigationMenuItem itemLabel="Solicita��es de Levantamento Bibliogr�fico e Infra-estrutura" action="#{levantamentoBibliograficoInfraMBean.listarSolicitacoesLevantIndividualPorUsuario}" />
		--%>
		<t:navigationMenuItem itemLabel="Autorizar Requisi��es de Livros" actionListener="#{ menuDocente.redirecionar }" itemValue="/entrarSistema.do?sistema=sipac&url=autorizarReqLivros" rendered="#{acesso.coordenadorCursoGrad}"/>
	</t:navigationMenuItem>
	
	<t:navigationMenuItem id="paginaWEB" itemLabel="P�gina WEB" icon="/img/comprovante.png" rendered="#{acesso.secretarioGraduacao || acesso.coordenadorCursoGrad}">
		<t:navigationMenuItem id="paginaWEB_apresentacaoCurso" itemLabel="Apresenta��o do Curso" action="#{detalhesSite.iniciarDetalhesCurso}"/>
		<t:navigationMenuItem id="paginaWEB_documentos" itemLabel="Documentos/Arquivos do Curso">
			<t:navigationMenuItem id="paginaWEB_cadastrar" itemLabel="Cadastrar" action="#{documentoSite.preCadastrarCurso}" />
			<t:navigationMenuItem id="paginaWEB_alterarOuRemover" itemLabel="Alterar / Remover"  action="#{documentoSite.listarCurso}"  />
		</t:navigationMenuItem>
		<t:navigationMenuItem id="noticiasCurso" itemLabel="Not�cias do Portal P�blico do Curso">
			<t:navigationMenuItem id="noticiasCurso_cadastrar" itemLabel="Cadastrar" action="#{noticiaSite.preCadastrarCurso}" />
			<t:navigationMenuItem id="noticiasCurso_alterarOuRemover" itemLabel="Alterar / Remover"  action="#{noticiaSite.listarCurso}"  />
		</t:navigationMenuItem>
		<t:navigationMenuItem id="not�ciasCurso" itemLabel="Not�cias do Portal dos Discentes do Curso">
			<t:navigationMenuItem id="outros_cadastrar" itemLabel="Cadastrar" action="#{noticiaPortalDiscente.iniciarNovaNoticia}" />
			<t:navigationMenuItem id="outros_alterarOuRemover" itemLabel="Alterar / Remover"  action="#{noticiaPortalDiscente.listarNoticias}" />
		</t:navigationMenuItem>
		<t:navigationMenuItem id="outrasOpcoes" itemLabel="Outras Op��es do Curso">
			<t:navigationMenuItem id="outrasOpcoes_cadastrar" itemLabel="Cadastrar" action="#{secaoExtraSite.preCadastrarCurso}" />
			<t:navigationMenuItem id="outrasOpcoes_alterarOuRemover" itemLabel="Alterar / Remover"  action="#{secaoExtraSite.listarCurso}"  />
		</t:navigationMenuItem>
	</t:navigationMenuItem>
	
	<t:navigationMenuItem id="outros" itemLabel="Outros" icon="/img/contato.png">
		<t:navigationMenuItem id="logarComoCoordenadorPolo" actionListener="#{ menu.redirecionar }" itemLabel="Logar como Coordenador de P�lo" itemValue="/ead/CoordenacaoPolo/logarComo.jsf" rendered="#{ portalCoordenadorGrad.cursoAtualCoordenacao.modalidadeEducacao.ADistancia }"/>
		<t:navigationMenuItem id="alterarDadosContatoCoordenacao" action="#{ coordenacaoCurso.iniciarAlterarContatos }" itemLabel="Alterar dados de contato da coordena��o"/>
		<t:navigationMenuItem id="notificarParticipantesCurso" action="#{ notificarParticipantesCurso.iniciar }" itemLabel="Notificar alunos e docentes do curso"/>		
		<t:navigationMenuItem id="listarGestorForumCurso" action="#{ gestorForumCursoBean.listar }" itemLabel="Gestor de Forum de Curso"/>
	</t:navigationMenuItem>
</t:jscookMenu>

</h:form>
</div>
</c:if>
</ufrn:subSistema>
