<%@taglib uri="http://myfaces.apache.org/tomahawk" prefix="t"%>
<ufrn:subSistema teste="portalCoordenadorLato">
<c:if test="${acesso.coordenadorCursoLato || acesso.secretarioLato}">
<h:form>
<div id="menu-dropdown">
	<input id="jscook_action" type="hidden" name="jscook_action"/>
<t:jscookMenu layout="hbr"
	theme="ThemeOffice" styleLocation="/css/jscookmenu">
	<t:navigationMenuItem itemLabel="Cadastro" icon="/img/graduacao/coordenador/cadastro.gif">
		<t:navigationMenuItem itemLabel="Processos Seletivos">
			<t:navigationMenuItem itemLabel="Gerenciar Processos Seletivos" action="#{processoSeletivo.listar}"/>
			<t:navigationMenuItem itemLabel="Questionários para Processos Seletivos" action="#{questionarioBean.gerenciarProcessosSeletivos}"/>
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Oferta de Vagas no Curso" action="#{cadastroOfertaVagasCurso.iniciarLato}"/>
		<t:navigationMenuItem itemLabel="Cadastrar Programa de Componente"  action="#{programaComponente.iniciar}" />
	</t:navigationMenuItem>
	<t:navigationMenuItem itemLabel="Matrículas" icon="/img/graduacao/coordenador/matricula.png">
		<t:navigationMenuItem itemLabel="Efetuar Matrícula em Turma" actionListener="#{menu.redirecionar}" itemValue="/ensino/tecnico/matricula/tipoMatricula.jsf" icon="/img/graduacao/coordenador/matricular.png"/>
		<t:navigationMenuItem itemLabel="Alterar Status de Matrículas em Turmas" action="#{alteracaoStatusMatricula.iniciar}" icon="/img/graduacao/coordenador/alterar_matricula.png" />
	</t:navigationMenuItem>
	<t:navigationMenuItem itemLabel="Curso" icon="/img/graduacao/coordenador/ativ_acam_especifica.png" split="true">
		<t:navigationMenuItem itemLabel="Visualizar Proposta" action="#{cursoLatoMBean.visualizar}" />
		<t:navigationMenuItem itemLabel="Identificar Secretário" action="#{secretariaUnidade.iniciarCoordenacaoLato}" rendered="#{acesso.coordenadorCursoLato}" />
		<t:navigationMenuItem itemLabel="Substituir Secretário" action="#{secretariaUnidade.iniciarSubstituicaoCoordenacaoLato}" rendered="#{acesso.coordenadorCursoLato}" />
		<t:navigationMenuItem itemLabel="Solicitar Prorrogação de Prazo" action="#{prorrogacaoLatoBean.iniciarSolicitacao}" />
		<t:navigationMenuItem itemLabel="Cadastrar Turmas de Entrada" action="#{turmaEntrada.preCadastrarTurmaEntrada}" />
		<t:navigationMenuItem itemLabel="Submeter Relatório Final" action="#{relatorioFinalLato.iniciar}" rendered="#{acesso.coordenadorCursoLato}"/>
	</t:navigationMenuItem>
	<t:navigationMenuItem itemLabel="Aluno" icon="/img/graduacao/coordenador/aluno.png" split="true">
		<t:navigationMenuItem itemLabel="Atualizar Dados Pessoais" action="#{discenteLato.alterarRemover}" />
		<t:navigationMenuItem itemLabel="Emitir Atestado de Matrícula" action="#{ atestadoMatricula.buscarDiscente }" icon="/img/graduacao/coordenador/documento.png"/>
		<t:navigationMenuItem itemLabel="Emitir Histórico" action="#{historico.buscarDiscente}" icon="/img/graduacao/coordenador/documento.png"/>
		<t:navigationMenuItem itemLabel="Notas" split="true">
			<t:navigationMenuItem itemLabel="Consolidar Notas de Turmas" actionListener="#{menu.redirecionar}" itemValue="/ensino/consolidacao/selecionaTurma.jsf?gestor=true" />
			<t:navigationMenuItem itemLabel="Retificar Consolidação de Turma" action="#{retificacaoMatricula.iniciar}" />
			<t:navigationMenuItem itemLabel="Consolidação Individual" action="#{consolidacaoIndividual.iniciar}" />
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Transferência de Alunos Entre Turmas" action="#{transferenciaTurma.iniciarLatoSensu}" itemDisabled="false" />
		<t:navigationMenuItem id="DiscenteNEE" itemLabel="Discente com NEE" icon="/img/acessibilidade.png">
			<t:navigationMenuItem id="cadastrarDiscenteNEE" itemLabel="Solicitar Apoio a CAENE" action="#{ solicitacaoApoioNee.preCadastrarDiscenteNee }" />
			<t:navigationMenuItem id="AlterarDiscenteNEE" itemLabel="Alterar Solicitação de Apoio" action="#{ solicitacaoApoioNee.listarSolicitacoesDiscente }" />
			<t:navigationMenuItem id="parecerNEE" itemLabel="Solicitações Enviadas para CAENE" action="#{ solicitacaoApoioNee.listaAllSolicitacoes }" />
		</t:navigationMenuItem>	
		<t:navigationMenuItem itemLabel="Cadastrar Trabalho Final de Curso" action="#{ registroAtividade.iniciarValidacaoLatoSensu }" />
		<t:navigationMenuItem itemLabel="Alterar Trabalho Final de Curso" action="#{ registroAtividade.alterarValidacaoLatoSensu }" icon="/img/graduacao/coordenador/atualizar.png" />
	</t:navigationMenuItem>
	<t:navigationMenuItem itemLabel="Turmas" icon="/img/graduacao/coordenador/turma.png" split="true"  itemDisabled="false">
		<t:navigationMenuItem itemLabel="Cadastrar Turma" action="#{ turmaLatoSensuBean.preCadastrar}" icon="/img/graduacao/coordenador/turma_sol.png" />
		<t:navigationMenuItem itemLabel="Consultar/Alterar/Remover Turma" action="#{buscaTurmaBean.popularBuscaGeral}" itemDisabled="false"/>
	</t:navigationMenuItem>
	<t:navigationMenuItem itemLabel="Relatórios" icon="/img/graduacao/coordenador/relatorios.png" split="true">
		<t:navigationMenuItem itemLabel="Ranking dos Alunos do Curso" action="#{ relatoriosLato.gerarRankingCursoCoordenador }" icon="/img/graduacao/coordenador/relatorio_item.png"/>
		<t:navigationMenuItem itemLabel="Orientadores de Trabalho Final de Curso" action="#{ relatoriosLato.gerarRelatorioOrientadoresTccCoordenador }" icon="/img/graduacao/coordenador/relatorio_item.png"/>
		<t:navigationMenuItem itemLabel="Pagamento de Mensalidades" action="#{ relatoriosLato.gerarRelatorioMensalidadesPagas }" icon="/img/graduacao/coordenador/relatorio_item.png"/>
	</t:navigationMenuItem>
	<t:navigationMenuItem itemLabel="Consultas" icon="/img/buscar.gif" split="true">
		<t:navigationMenuItem action="#{ componenteCurricular.popularBuscaGeral }" itemLabel="Componentes Curriculares"/>
		<t:navigationMenuItem action="#{buscaTurmaBean.popularBuscaGeral}" itemLabel="Turmas"/>
		<t:navigationMenuItem actionListener="#{menu.redirecionar}" itemValue="/graduacao/curso/lista.jsf" itemLabel="Cursos"/>
	</t:navigationMenuItem>
	
	<t:navigationMenuItem itemLabel="Página WEB" icon="/img/comprovante.png" split="true" rendered="#{acesso.coordenadorCursoLato || acesso.secretarioLato}">
		<t:navigationMenuItem itemLabel="Apresentação do Curso" action="#{detalhesSite.iniciarDetalhesCurso}"/>
		<t:navigationMenuItem itemLabel="Documentos/Arquivos">
			<t:navigationMenuItem itemLabel="Cadastrar" action="#{documentoSite.preCadastrarCurso}" />
			<t:navigationMenuItem itemLabel="Alterar / Remover"  action="#{documentoSite.listarCurso}"  />
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Notícias do Curso">
			<t:navigationMenuItem itemLabel="Cadastrar" action="#{noticiaSite.preCadastrarCurso}" />
			<t:navigationMenuItem itemLabel="Alterar / Remover"  action="#{noticiaSite.listarCurso}"  />
		</t:navigationMenuItem>
		<t:navigationMenuItem itemLabel="Outras Opções do Curso">
			<t:navigationMenuItem itemLabel="Cadastrar" action="#{secaoExtraSite.preCadastrarCurso}" />
			<t:navigationMenuItem itemLabel="Alterar / Remover"  action="#{documentoSite.listarCurso}"  />
		</t:navigationMenuItem>
	</t:navigationMenuItem>

	<t:navigationMenuItem itemLabel="Outros" split="true" icon="/img/outros.png">
		<t:navigationMenuItem action="#{ respostasAutoAvaliacaoMBean.iniciarPreenchimento }" itemLabel="Preencher a Auto Avaliação" id="calendarioAplicacaoAutoAvaliacaoMBean_iniciarPreenchimento"/>
		<t:navigationMenuItem id="notificarParticipantesCurso" action="#{ notificarParticipantesCurso.iniciar }" itemLabel="Notificar alunos e docentes do curso"/>
	</t:navigationMenuItem>
</t:jscookMenu>

	</h:form>
	</div>
</c:if>
</ufrn:subSistema>
