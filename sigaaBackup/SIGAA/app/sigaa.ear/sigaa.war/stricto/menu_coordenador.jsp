<%@page import="br.ufrn.sigaa.pessoa.dominio.Discente"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="t" uri="http://myfaces.apache.org/tomahawk" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>

<ufrn:subSistema teste="portalCoordenadorStricto">
<c:if test="${acesso.coordenadorCursoStricto || acesso.secretariaPosGraduacao }">
<h:form>
<input type="hidden" id="jscook_action" name="jscook_action" />
<div id="menu-dropdown">
<t:jscookMenu layout="hbr" theme="ThemeOffice" styleLocation="/css/jscookmenu">

	<t:navigationMenuItem itemLabel="Cadastros" icon="/img/graduacao/coordenador/cadastro.gif">
	
		<t:navigationMenuItem itemLabel="Corpo Docente do Programa">
			<t:navigationMenuItem itemLabel="Equipe de Docentes do Programa"  action="#{equipePrograma.listar}" id="EquipedeDocentesdoPrograma"/>
			<t:navigationMenuItem itemLabel="Limites de Orientandos por Docente" action="#{equipePrograma.iniciarLimitesOrientandos}" id="LimitesdeOrientandosporDocente"/>
			<t:navigationMenuItem itemLabel="Docente Externo à #{ configSistema['siglaInstituicao'] }">
				<t:navigationMenuItem itemLabel="Cadastrar" action="#{docenteExterno.preCadastrar}" id="PreCadastroDocentExterno"/>
				<t:navigationMenuItem itemLabel="Alterar / Remover" action="#{docenteExterno.iniciarAlterar}" id="iniciarAltDocenteExterno"/>				
				<t:navigationMenuItem itemLabel="Cadastrar Usuário para Docente Externo à #{ configSistema['siglaInstituicao'] }"  action="#{docenteExterno.iniciarAlterar}" id="cadastrouserDocenteExterno"/>
			</t:navigationMenuItem>
		</t:navigationMenuItem>
		
		<t:navigationMenuItem itemLabel="Ementas e Referências de Componentes Curriculares" action="#{ementaComponenteCurricularMBean.iniciarAtualizacaoEmenta}" id="ementasRefDeCompCurricular"/>
			
		<t:navigationMenuItem itemLabel="Processos Seletivos" split="true">
			<t:navigationMenuItem itemLabel="Gerenciar Processos Seletivos" action="#{processoSeletivo.listar}" id="GerenciarProcessosSeletivoss"/>
			<t:navigationMenuItem itemLabel="Questionários para Processos Seletivos" action="#{questionarioBean.gerenciarProcessosSeletivos}" id="QuestionarioProcessosSeletiv"/>
		</t:navigationMenuItem>

		<t:navigationMenuItem itemLabel="Oferta de Vagas nos Cursos" action="#{cadastroOfertaVagasCurso.iniciarStricto}" id="ofertVagasCurso"/>
		<t:navigationMenuItem itemLabel="Projetos de pesquisa vinculados ao programa" action="#{programaProjetoBean.iniciar}" id="ProjPesqVinculadosAoProg"/>

		<t:navigationMenuItem itemLabel="Solicitar Cadastro de Componente Curricular" action="#{componenteCurricular.preCadastrarStricto}" id="solicitarCompoenenteCurricular"/>

		<t:navigationMenuItem itemLabel="Componente Curricular" split="true" rendered="#{componenteCurricular.coordenadorOpcaoAlterarComponenteCurricular or componenteCurricular.coordenadorOpcaoCadastrarComponenteCurricular}">
			<t:navigationMenuItem itemLabel="Cadastrar"   action="#{componenteCurricular.preCadastrar}"    id="cadastrarCompoenenteCurricular" rendered="#{componenteCurricular.coordenadorOpcaoCadastrarComponenteCurricular}"/>
			<t:navigationMenuItem itemLabel="Alterar"   action="#{componenteCurricular.listar}"    id="alterarComponeneteCurricular" rendered="#{componenteCurricular.coordenadorOpcaoAlterarComponenteCurricular}"/>			
		</t:navigationMenuItem>
							
		<t:navigationMenuItem itemLabel="Estrutura Curricular"   action="#{curriculo.listarEstruturasCoordenador}"    id="alterarEstruturaCurricular"/>						
		
		<t:navigationMenuItem itemLabel="Calendário do Programa" action="#{calendario.iniciarProgramasPos}" split="true" id="calDeProg"/>
		<t:navigationMenuItem itemLabel="Parâmetros do Programa" action="#{parametrosProgramaPosBean.iniciar}" id="ParamDoPrograma"/>
		
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Aluno" icon="/img/graduacao/coordenador/aluno.png" split="true">
		<t:navigationMenuItem itemLabel="Consulta Avançada" action="#{ buscaAvancadaDiscenteMBean.iniciarCoordStricto }" id="consultaAvancadaDeAluno"/>
		<t:navigationMenuItem itemLabel="Gerenciar Orientações" action="#{ orientacaoAcademica.iniciar }" icon="/img/graduacao/coordenador/documento.png" id="gerenciaOrient"/>
		<t:navigationMenuItem itemLabel="Cadastro" split="true">
			<t:navigationMenuItem itemLabel="Cadastrar Novo Discente" action="#{ discenteStricto.iniciarCadastroDiscenteNovo}" id="CadastroDeNovoDiscent"/>
			<t:navigationMenuItem itemLabel="Atualizar Discente" action="#{ discenteStricto.atualizar}" id="atualizacaoDeDiscentes"/>
			<t:navigationMenuItem itemLabel="Atualizar Dados Pessoais" action="#{ alteracaoDadosDiscente.iniciar}" icon="/img/graduacao/coordenador/atualizar.png" id="altdadosDEdiscente"/>
			<t:navigationMenuItem itemLabel="Editar Observações do Discente" action="#{ observacaoDiscente.iniciar}" id="edicaoDeObsNoDiscente"/>
			
			<t:navigationMenuItem itemLabel="Cadastrar Discente Antigo" action="#{discenteStricto.iniciarCadastroDiscenteAntigo}" id="linkCadastrarDiscenteAntigo" rendered="#{portalCoordenacaoStrictoBean.programaCadastraDiscenteAntigo}"/>
			<t:navigationMenuItem itemLabel="Implantar Histórico" action="#{implantarHistorico.iniciar}" id="linkImplantarhistorico" rendered="#{portalCoordenacaoStrictoBean.programaImplantaHistorico}"/>
			
			
			
		</t:navigationMenuItem>
			
		<t:navigationMenuItem itemLabel="Documentos">
			<t:navigationMenuItem itemLabel="Emitir Atestado de Matrícula" action="#{ atestadoMatricula.buscarDiscente }" icon="/img/graduacao/coordenador/documento.png" id="atestadoMatriculaEmissao"/>
			<t:navigationMenuItem itemLabel="Emitir Histórico" action="#{ historico.buscarDiscente }" icon="/img/graduacao/coordenador/documento.png" id="emitHistoricDiscente"/>
			<t:navigationMenuItem itemLabel="Termo de Autorização para Publicação de Teses e Dissertações - TEDE" action="#{ termoPublicacaoTD.buscarDiscente }" icon="/img/graduacao/coordenador/documento.png" id="autorizacaoPublicTeseDissert"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Aproveitamentos">
			<t:navigationMenuItem itemLabel="Aproveitar Disciplina" action="#{aproveitamento.iniciarAproveitamento}" id="aproveitamentoDeDIsciplina"/>
			<t:navigationMenuItem itemLabel="Excluir Aproveitamento de Componente" action="#{aproveitamento.iniciarCancelamento}" id="retirarAproveitDeComponente"/>
			<t:navigationMenuItem itemLabel="Retificar Aproveitamento e Consolidação de Turma" action="#{retificacaoMatricula.iniciar}" id="retificarAproveitConsolidacaoTurm"/>
			<t:navigationMenuItem itemLabel="Aproveitamento de Crédito" action="#{aproveitamentoCredito.iniciar}"  split="true" id="aproveitCreditoss"/>
			<t:navigationMenuItem itemLabel="Excluir Aproveitamento de Crédito" action="#{aproveitamentoCredito.iniciarRemover}" id="exclusaoDeAproveitamentoDeCreditos"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Notas">
			<t:navigationMenuItem itemLabel="Consolidação Individual" action="#{consolidacaoIndividual.iniciar}" id="consolidacaoindividual"/>
			<t:navigationMenuItem itemLabel="Retificar Aproveitamento e Consolidação de Turma" action="#{retificacaoMatricula.iniciar}" id="retificacaoAproveitamentoConsolidTurma"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Vínculo">
			<t:navigationMenuItem itemLabel="Prorrogar Prazo de Conclusão" action="#{prorrogacao.buscarDiscente}" id="prorrogPrazoDeConclusao"/>
			<t:navigationMenuItem itemLabel="Cancelar Prorrogação de Prazo de Conclusão" action="#{prorrogacao.buscarDiscenteCancelarProrrogacao}" id="cancelProrrogPrazoConc"/>
			<t:navigationMenuItem itemLabel="Trancar Vínculo" action="#{movimentacaoAluno.iniciarTrancamentoPrograma }" id="trancDeVinculo"/>
			<t:navigationMenuItem itemLabel="Cancelar Vínculo" action="#{movimentacaoAluno.iniciarCancelamentoPrograma }" id="cancelDeVinculos"/>
			<t:navigationMenuItem itemLabel="Retornar Aluno Trancado" action="#{movimentacaoAluno.iniciarRetorno }" id="voltarAlunoTrancado"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Conclusão">
			<t:navigationMenuItem itemLabel="Cadastrar Bancas" >
				<t:navigationMenuItem itemLabel="Banca de Qualificação" action="#{bancaPos.iniciarQualificacao}" id="cadastrarBancaDequalificacao"/>
				<t:navigationMenuItem itemLabel="Banca de Defesa" action="#{bancaPos.iniciarDefesa}" id="cadastroBDefesa"/>
				<t:navigationMenuItem itemLabel="Alterar Bancas" action="#{bancaPos.listar}" id="alteracaoDeBancas"/>
				<t:navigationMenuItem itemLabel="Inserir Ata" action="#{ataBancaMBean.iniciar}" id="itemMenuInserirAta"/>
				<t:navigationMenuItem itemLabel="Declaração de Participação em Banca" action="#{ certificadoBancaPos.iniciar }" id="emitirDecParticipEmBanca"/>
				<t:navigationMenuItem itemLabel="Cadastrar Defesa de Aluno Concluído" action="#{bancaPos.iniciarDefesaAlunoConcluido}" split="true" id="cadastroDefesaAlunoConcluido"/>
				<t:navigationMenuItem itemLabel="Validar Bancas Pendentes" action="#{bancaPos.listarBancasPendentesAprovacao}" id="validaBancasPendentes"/>
			</t:navigationMenuItem>
			
			<t:navigationMenuItem itemLabel="Homologação de Diploma" split="true">
				<t:navigationMenuItem itemLabel="Solicitar Homologação de Diploma" action="#{homologacaoTrabalhoFinal.iniciarHomologacao}" id="solicitHomologacaoDeDiploMa"/>
				<t:navigationMenuItem itemLabel="Gerar Documentos da Solicitação" action="#{homologacaoTrabalhoFinal.iniciarGerarDocumentos}" />
			</t:navigationMenuItem>
			<t:navigationMenuItem itemLabel="Comprovante de Solicitação Homologação" action="#{relatorioHomologacaoStricto.iniciar}" id="comprovanteSolicitHomologacaoDiploma"/>
			<t:navigationMenuItem itemLabel="Visualizar Solicitações de Homologação de Diploma pendentes" action="#{homologacaoTrabalhoFinal.listar}" id="visualizarSolicitacoesHOmologacaoDiploma"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="DiscenteNEE" itemLabel="Discente com NEE" icon="/img/acessibilidade.png">
			<t:navigationMenuItem id="cadastrarDiscenteNEE" itemLabel="Solicitar Apoio a CAENE" action="#{ solicitacaoApoioNee.preCadastrarDiscenteNee }" />
			<t:navigationMenuItem id="AlterarDiscenteNEE" itemLabel="Alterar Solicitação de Apoio" action="#{ solicitacaoApoioNee.listarSolicitacoesDiscente }" />
			<t:navigationMenuItem id="parecerNEE" itemLabel="Solicitações Enviadas para CAENE" action="#{ solicitacaoApoioNee.listaAllSolicitacoes }" />
			<t:navigationMenuItem id="manualNEE"   itemLabel="Manual de Solicitação de Apoio"  action="javascript:redirectManual()" icon="/img/report.png"/>
		</t:navigationMenuItem>		
		<t:navigationMenuItem itemLabel="Cadastro dos Bolsistas CNPq" action="#{ bolsasCnpqStrictoMBean.listar }" />
		<t:navigationMenuItem itemLabel="Bolsas Docência Assistida">
			<t:navigationMenuItem itemLabel="Submeter Proposta" action="#{ solicitacaoBolsasReuniBean.iniciarCadastro }" id="submissaoPropostas"/>
			<t:navigationMenuItem itemLabel="Consultar Propostas Cadastradas" action="#{ solicitacaoBolsasReuniBean.listBuscar }" id="consultarPropostasCadastradas"/>
			<t:navigationMenuItem itemLabel="Gerenciar Planos de Docência Assistida" action="#{ planoDocenciaAssistidaMBean.iniciarBuscaGeral}" split="true" id="planDocenciaAssistidGerenciar"/>
			<t:navigationMenuItem itemLabel="Planos de Docência Assistida Sem Indicação" action="#{ planoDocenciaAssistidaMBean.listarSemIndicao}" id="planDocenciaAssistidSemIndic"/>
			<t:navigationMenuItem itemLabel="Editais Publicados" split="true" rendered="false" id="editaisPublicadoss"/>
		</t:navigationMenuItem>		
		<t:navigationMenuItem itemLabel="Fórum de Cursos" action="#{ forum.listarForunsCurso }" split="true"/>
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Turmas" icon="/img/graduacao/coordenador/turma.png" split="true">
			<t:navigationMenuItem itemLabel="Criar Turma" action="#{ turmaStrictoSensuBean.preCadastrar}" icon="/img/graduacao/coordenador/turma_sol.png" id="criarTurmas"/>
			<t:navigationMenuItem itemLabel="Consultar Turma" action="#{buscaTurmaBean.popularBuscaGeral}" itemDisabled="false" id="consultaDeturmas"/>
			<t:navigationMenuItem itemLabel="Solicitar Turma de Outro Programa" action="#{ solicitacaoTurma.iniciar }" icon="/img/graduacao/coordenador/turma_sol.png" rendered="false" id="solicitacaoTurmaOutroPeriodo"/>
			<t:navigationMenuItem itemLabel="Visualizar Solicitações Enviadas" action="#{ solicitacaoTurma.listar }" icon="/img/graduacao/coordenador/listar.png" rendered="false" id="visualizacaoSolicitacEnviadas"/>
	</t:navigationMenuItem>
		
	<t:navigationMenuItem itemLabel="Matrículas" icon="/img/graduacao/coordenador/matricula.png"   split="true">
		<t:navigationMenuItem itemLabel="Analisar Matriculas de Alunos do Seu Programa" action="#{analiseSolicitacaoMatricula.iniciar}" rendered="#{acesso.coordenadorCursoStricto}" id="anal"/>
		<t:navigationMenuItem itemLabel="Analisar Matriculas de Alunos de Outros Programas" action="#{analiseSolicitacaoMatricula.iniciarDiscentesOutrosProgramas}" rendered="#{acesso.coordenadorCursoStricto}" id="analiseAlunosPorprog"/>
		<t:navigationMenuItem itemLabel="Matricular Aluno Especial" action="#{ matriculaGraduacao.iniciarMatriculasEspecial}" rendered="#{matriculaGraduacao.permiteCoordenadorMatricularDiscenteStrictoEspecial}" icon="/img/graduacao/coordenador/matricular.png" id="matricularAlunoEspecial"/>
		<t:navigationMenuItem itemLabel="Matricular Aluno Regular"  action="#{ matriculaGraduacao.iniciarMatriculasRegulares}" rendered="#{matriculaGraduacao.permiteCoordenadorMatricularDiscenteStrictoRegular}" icon="/img/graduacao/coordenador/matricular.png" id="matricularAlunoRegulares"/>
		<t:navigationMenuItem itemLabel="Atividades" icon="/img/graduacao/coordenador/ativ_acam_especifica.png" split="true" id="atividades">
			<t:navigationMenuItem itemLabel="Matricular" action="#{ registroAtividade.iniciarMatricula}" icon="/img/graduacao/coordenador/matricular.png" id="matricularrDiscente"/>
			<t:navigationMenuItem itemLabel="Renovar Matrícula" action="#{ registroAtividade.iniciarRenovacao}" icon="/img/graduacao/coordenador/matricular.png" id="renovarMatricuulas"/>
			<t:navigationMenuItem itemLabel="Consolidar" action="#{ registroAtividade.iniciarConsolidacao}" icon="/img/graduacao/coordenador/consolidar.png" id="consolidacaoMatriculaEmComp"/>
			<t:navigationMenuItem itemLabel="Validar" action="#{ registroAtividade.iniciarValidacao}" icon="/img/graduacao/coordenador/validar.png" id="validacaoMatriculaEmComp"/>
			<t:navigationMenuItem itemLabel="Excluir" action="#{ registroAtividade.iniciarExclusao}" icon="/img/graduacao/coordenador/excluir.png" id="excluirMatriculas"/>
		</t:navigationMenuItem>
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Consultas" icon="/img/buscar.gif" split="true">
		<t:navigationMenuItem actionListener="#{menu.redirecionar}" itemValue="/graduacao/curso/lista.jsf" itemLabel="Cursos de Pós-Graduação" id="cursosDePosConsulta"/>
		<t:navigationMenuItem action="#{ componenteCurricular.popularBuscaGeral }" itemLabel="Disciplinas" id="consultaDiscipliinas"/>
		<t:navigationMenuItem actionListener="#{menu.redirecionar}" itemValue="/stricto/curriculo/lista.jsf" itemLabel="Estruturas Curriculares" id="consultaDeEstCurricular"/>
		<t:navigationMenuItem action="#{buscaTurmaBean.popularBuscaGeral}" itemLabel="Turmas" id="consultarTurmass"/>
		<t:navigationMenuItem action="#{unidade.popularUnidadeAcademica}" itemLabel="Consultar Unidade Acadêmica" id="consultaundAcademica"/>
		<t:navigationMenuItem itemLabel="Projetos de Pesquisa" actionListener="#{menuDiscente.redirecionar}" itemValue="/pesquisa/projetoPesquisa/buscarProjetos.do?dispatch=consulta&popular=true" id="projsDePesquisa"/>
		<t:navigationMenuItem action="#{consultarDefesaMBean.iniciar}" itemLabel="Consultar Bancas" split="true" id="buscaEmBancas"/>
		<t:navigationMenuItem action="#{calendario.iniciarBusca}" itemLabel="Consulta de Calendários Acadêmicos" id="consultaCalAcademico"/>
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Relatórios" icon="/img/graduacao/coordenador/relatorios.png" itemDisabled="#{acesso.secretarioCentro}" >
		<t:navigationMenuItem id="turmas" itemLabel="Turmas">
			<t:navigationMenuItem itemLabel="Relatório de Turmas" action="#{buscaTurmaBean.iniciarRelatorio}"  icon="/img/graduacao/coordenador/relatorio_item.png" id="relDeTUrmas"/>
			<t:navigationMenuItem itemLabel="Relatório de Turmas por Departamento"  action="#{relatoriosDepartamentoCpdi.iniciarTurmasDepartamentoDocente}" icon="/img/graduacao/coordenador/relatorio_item.png" id="relTurmasPorDepartamento"/>
			<t:navigationMenuItem itemLabel="Relatório de Ocupação de Vagas de Turmas" action="#{relatorioTurma.iniciarRelatorioOcupacaoVagas}" itemValue="/graduacao/relatorios/discente/seleciona_trancamentos_componente.jsf" icon="/img/graduacao/coordenador/relatorio_item.png" id="relatorioDeOcupacaoDeVagasDeTurmas"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="alunos" itemLabel="Alunos">
			<t:navigationMenuItem itemLabel="Alunos Matriculados em Atividades" action="#{relatorioDiscente.carregarRelatorioMatriculadosAtividadeStricto}" icon="/img/graduacao/coordenador/relatorio_item.png" id="relatorioMatriculadosAtividadeStricto"/>
 			<t:navigationMenuItem itemLabel="Alunos Matriculados em Atividades Não Renovadas" action="#{ relatorioDiscente.carregarRelatorioAlunosMatriculadosEmAtividadesNaoRenovadas}" icon="/img/graduacao/coordenador/relatorio_item.png" id="relMatriculadosAtividadesNaoRenovadas"/>
			<t:navigationMenuItem itemLabel="Alunos com Trancamento em Componentes do Programa" actionListener="#{menuDocente.redirecionar}" itemValue="/graduacao/relatorios/discente/seleciona_trancamentos_componente.jsf" icon="/img/graduacao/coordenador/relatorio_item.png" id="alunosTrancCompPrograma"/>
			<t:navigationMenuItem itemLabel="Alunos e Respectivos Orientadores" action="#{relatorioDiscente.iniciarRelatorioAlunosRespecOrientadores}" icon="/img/graduacao/coordenador/relatorio_item.png" id="alunosRespecOrientadores"/>
			<t:navigationMenuItem itemLabel="Declaração de Qualificação/Defesa do Aluno" action="#{declaracaoDefesaMBean.iniciar}" icon="/img/graduacao/coordenador/relatorio_item.png" id="declaracaoQualifDefesaAluno"/>
			<t:navigationMenuItem itemLabel="Discentes por Linha de Pesquisa" action="#{ relatorioDiscentesLinhaPesquisa.gerarRelatorio }" icon="/img/graduacao/coordenador/relatorio_item.png" id="discentLinhaDePesq"/>
			<t:navigationMenuItem itemLabel="Lista de Alunos para Eleição" action="#{ relatorioDiscente.carregarListaAlunosEleicao }" icon="/img/graduacao/coordenador/relatorio_item.png" id="alunosParaEleicao"/>
			<t:navigationMenuItem itemLabel="Lista de Contatos de Alunos" action="#{ relatorioDiscente.carregarListaContatoAlunos }" icon="/img/graduacao/coordenador/relatorio_item.png" id="listaContatosAlunnos"/>
			<t:navigationMenuItem itemLabel="Lista de Alunos Reprovados" action="#{ relatorioDiscente.iniciaRelatorioAlunosReprovados }" icon="/img/graduacao/coordenador/relatorio_item.png" id="relAlunosRep"/>
			<%-- <t:navigationMenuItem itemLabel="Lista de Alunos Não Matriculados On-Line" action="#{ relatoriosStricto.gerarRelatorioAlunosNaoMatriculadosOnLine }" icon="/img/graduacao/coordenador/relatorio_item.png" id="relAlunosNaoMatriculadosOnLine"/> --%>
			<t:navigationMenuItem itemLabel="Quantitativo de Alunos Ativos" action="#{relatorioQuantitativoAlunosPrograma.iniciarQuantitativoAtivo}" itemValue="/graduacao/relatorios/discente/seleciona_trancamentos_componente.jsf" icon="/img/graduacao/coordenador/relatorio_item.png" id="quantitativoAlunosAtivos"/>
			<t:navigationMenuItem itemLabel="Quantitativo de Alunos Ativos / Matriculados" action="#{relatorioQuantitativoAlunosPrograma.iniciarQuantitativoMatriculado}" itemValue="/graduacao/relatorios/discente/seleciona_trancamentos_componente.jsf" icon="/img/graduacao/coordenador/relatorio_item.png" id="quantitativoAlunosativosmatricula"/>
			<t:navigationMenuItem itemLabel="Relatório de Alunos Ativos Não Matriculados" action="#{ relatorioDiscente.iniciaRelatorioAlunosAtivosNaoMatriculadosBolsa }" icon="/img/graduacao/coordenador/relatorio_item.png" id="relAlunosAtivosNaoMatriculados"/>
			<t:navigationMenuItem itemLabel="Relatório de Alunos Especiais e Disciplinas" action="#{ relatorioDiscente.iniciarRelatorioDiscentesEspeciais }" icon="/img/graduacao/coordenador/relatorio_item.png" id="relDiscentesEspeciais"/>			
			<t:navigationMenuItem itemLabel="Relatório de Bolsistas por Período" action="#{ relatorioDiscente.iniciaRelatorioPrazoMaximoBolsaAlunos}" icon="/img/graduacao/coordenador/relatorio_item.png" id="relPrazoMaxBolsaAlunos"/>
			<t:navigationMenuItem itemLabel="Relatório de Discentes Ativos e Prazo Máximo para Conclusão" action="#{ relatorioPrazoMaximoPosBean.iniciar }" icon="/img/graduacao/coordenador/relatorio_item.png" id="relDiscentesAtivPrazoMaxConclusao"/>
			<t:navigationMenuItem itemLabel="Relatório de Alunos e Matrículas" action="#{ relatorioAlunosMatriculasPosBean.iniciar }" icon="/img/graduacao/coordenador/relatorio_item.png" id="relAlunosEMatriculas"/>
			<t:navigationMenuItem itemLabel="Relatório de Bolsistas cadastrados no SIPAC" action="#{ relatorioBolsasStrictoBean.iniciar }" icon="/img/graduacao/coordenador/relatorio_item.png" id="relBolsistasCadastradosSipac"/>
			<t:navigationMenuItem itemLabel="Relatório de Tempo Médio de Titulação por Discente" action="#{ relatorioTempoMedioTitulacaoMBean.iniciarTempoMedioTitulacaoPorDiscente }" icon="/img/graduacao/coordenador/relatorio_item.png" id="relTempoMedioTitulacao"/>
			<t:navigationMenuItem itemLabel="Relatório de Tempo Médio de Titulação por Orientador" action="#{ relatorioTempoMedioTitulacaoMBean.iniciarTempoMedioTitulacaoPorOrientador }" icon="/img/graduacao/coordenador/relatorio_item.png" id="relTempoMedioTitulacaoPorOrientador"/>
			<t:navigationMenuItem itemLabel="Relatório de Taxa de Sucesso" action="#{relatorioTaxaSucessoStricto.iniciarTaxaSucesso}" icon="/img/graduacao/coordenador/relatorio_item.png" id="iniciarTaxaSucesso"/>			
			<t:navigationMenuItem itemLabel="Relatório de Créditos Integralizados" action="#{relatorioCreditosIntegralizadosMBean.iniciar}" icon="/img/graduacao/coordenador/relatorio_item.png" id="iniciarCreditosIntegralizados"/>			
		</t:navigationMenuItem>
		<t:navigationMenuItem id="docentes" itemLabel="Docentes">
			<t:navigationMenuItem itemLabel="Declaração de Participação em Banca" action="#{ certificadoBancaPos.iniciar }" icon="/img/graduacao/coordenador/relatorio_item.png" id="decparticemBanca"/>
			<t:navigationMenuItem itemLabel="Docentes por Turma" action="#{ relatorioDocentesTurmaBean.iniciar }" icon="/img/graduacao/coordenador/relatorio_item.png" id="docentesPorturma"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem id="outros" itemLabel="Outros">
			<t:navigationMenuItem itemLabel="Orientações" action="#{relatorioOrientacoes.iniciar}" icon="/img/graduacao/coordenador/relatorio_item.png" id="orientacoess"/>
			<t:navigationMenuItem itemLabel="Participantes das Bancas" action="#{ participantesDasBancas.iniciar }" icon="/img/graduacao/coordenador/relatorio_item.png" id="relpartDasBancas"/>
			<t:navigationMenuItem itemLabel="Relatório de Processos Seletivos (Demandas x Vagas)" action="#{ processoSeletivo.iniciaRelatorioDemandaVagas }" icon="/img/graduacao/coordenador/relatorio_item.png" id="relDeProcessosSeletiv"/>
		</t:navigationMenuItem>				
	</t:navigationMenuItem>
	
	<t:navigationMenuItem id="biblioteca" itemLabel="Biblioteca" icon="/img/icones/biblioteca_menu.gif" split="true">
		<t:navigationMenuItem id="autorizarRequisicoesLivros" itemLabel="Autorizar Requisições de Livros" actionListener="#{ menuDocente.redirecionar }" itemValue="/entrarSistema.do?sistema=sipac&url=autorizarReqLivros" />
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Página WEB" icon="/img/comprovante.png" split="true" rendered="#{acesso.coordenadorCursoStricto || acesso.secretariaPosGraduacao}">
		<t:navigationMenuItem itemLabel="Apresentação do Programa" action="#{detalhesSite.iniciarDetalhesPrograma}" id="apresentacaoPrograma"/>
		<t:navigationMenuItem itemLabel="Configurar Cores do Programa" action="#{detalhesSite.iniciarTemplateSite}" id="confCoresPrograma"/>
		
		<t:navigationMenuItem itemLabel="Documentos/Arquivos do Programa" split="true">
			<t:navigationMenuItem itemLabel="Cadastrar" action="#{documentoSite.preCadastrarPrograma}" id="cadastroDocArqPrograma"/>
			<t:navigationMenuItem itemLabel="Alterar / Remover"  action="#{documentoSite.listarPrograma}"  id="removerAlterarDocArqPrograma"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Notícias do Portal Público do Programa">
			<t:navigationMenuItem itemLabel="Cadastrar" action="#{noticiaSite.preCadastrarPrograma}" id="cadastroNoticiaProgram"/>
			<t:navigationMenuItem itemLabel="Alterar / Remover"  action="#{noticiaSite.listarPrograma}"  id="remAlterarNoticiaProg"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Notícias do Portal dos Discentes do Programa">
			<t:navigationMenuItem itemLabel="Cadastrar" action="#{noticiaPortalDiscente.iniciarNovaNoticia}" id="btaoCadastrarNovaNoticia"/>
			<t:navigationMenuItem itemLabel="Alterar / Remover"  action="#{noticiaPortalDiscente.listarNoticias}" id="alterarRemNoticias"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Outras Opções do Programa">
			<t:navigationMenuItem itemLabel="Cadastrar" action="#{secaoExtraSite.preCadastrarPrograma}" id="precadastrarProgrma"/>
			<t:navigationMenuItem itemLabel="Alterar / Remover"  action="#{secaoExtraSite.listarPrograma}"  id="altRemoverPrograma"/>
		</t:navigationMenuItem>		
	</t:navigationMenuItem>
	
	<t:navigationMenuItem itemLabel="Outros" split="true" icon="/img/contato.png">
		<t:navigationMenuItem action="#{ coordenacaoCurso.iniciarAlterarContatos }" itemLabel="Alterar dados de contato da coordenação" id="coordenacaoCurso_iniciarAlterarContatos"/>
		<t:navigationMenuItem action="#{ respostasAutoAvaliacaoMBean.iniciarPreenchimento }" itemLabel="Preencher a Auto Avaliação" id="calendarioAplicacaoAutoAvaliacaoMBean_iniciarPreenchimento"/>
	</t:navigationMenuItem>


</t:jscookMenu>

	</div>
	</h:form>
	</c:if>
</ufrn:subSistema>
<script>
function redirectManual(){
	return window.open("${linkPublico.urlDownload}/manual_nee_solicitacao_apoio.pdf","_blank");
}
</script>