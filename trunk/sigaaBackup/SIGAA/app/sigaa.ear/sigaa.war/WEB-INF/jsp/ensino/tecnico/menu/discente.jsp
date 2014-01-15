<%@ taglib uri="/tags/ufrn" prefix="ufrn"  %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
    <ul>
        <li>Aluno
            <ul>
                <li> <h:commandLink id="cadastrarDiscenteTecnico"  action="#{ discenteTecnico.iniciarCadastroDiscenteNovo}"  value="Cadastrar" onclick="setAba('aluno')"/></li>
                <li> <h:commandLink id="alterarRemoverAluno" action="#{discenteTecnico.atualizar}" value="Listar/Alterar" onclick="setAba('aluno')"/></li>
 				<li> <h:commandLink action="#{ alteracaoDadosDiscente.iniciar}" value="Atualizar Dados Pessoais" onclick="setAba('aluno')"/> </li>
				<li> <h:commandLink id="editarObservacoesDiscenteTecnico" action="#{ observacaoDiscente.iniciar}" value="Editar Observa��es do Discente" onclick="setAba('aluno')"/> </li>
                <li> <h:commandLink id="cadastrarDiscenteAntigoTecnico" action="#{ discenteTecnico.iniciarCadastroDiscenteAntigo}" value="Cadastrar Discente Antigo" onclick="setAba('aluno')"/> </li>
             </ul>
        </li>
        <li>Matr�cula
            <ul>
                <li><a href="${ ctx }/ensino/tecnico/matricula/tipoMatricula.jsf?aba=aluno">Efetuar Matr�cula</a></li>
				<li>
						<h:commandLink id="alterarStatusMatriculaTurmaTecnico"	action="#{alteracaoStatusMatricula.iniciar }"
							value="Alterar Status de Matr�culas em Turmas" onclick="setAba('aluno')">
						</h:commandLink>
				</li>
				<li>
						<h:commandLink id="trancarMatriculaTurmaTecnico" action="#{alteracaoStatusMatricula.iniciarTrancamentoMatricula }"
							value="Trancar Matr�culas em Turmas" onclick="setAba('aluno')">
						</h:commandLink>
				</li>
		        <li><h:commandLink id="analisarSolicitacaoMatriculaTecnico" action="#{analiseSolicitacaoMatricula.iniciar}" value="Analisar Solicita��es de Matr�cula" onclick="setAba('aluno')"/></li>
		        <li><h:commandLink id="consultarSolcitacoesMatriculaTecnico" action="#{consultaSolicitacoes.iniciar}" value="Consultar Solicita��es de Matr�cula" onclick="setAba('aluno')"/> </li>
        	</ul>
        </li>
        <li> Consultas Gerais
			<ul>
				<li> <h:commandLink action="#{ buscaAvancadaDiscenteMBean.iniciarTecnico }" value="Consulta Geral de Discentes" onclick="setAba('aluno')"/></li>
			</ul>
		</li>		
        <li> Registro de Atividades
			<ul>
				<li> <h:commandLink id="registroAtividadeiniciarMatricula" action="#{registroAtividade.iniciarMatricula}" value="Matricular" onclick="setAba('aluno')" /> </li>
				<li> <h:commandLink id="registroAtividadeiniciarConsolidacao" action="#{registroAtividade.iniciarConsolidacao}" value="Consolidar" onclick="setAba('aluno')"/> </li>
				<li> <h:commandLink id="registroAtividadeiniciarValidacao" action="#{registroAtividade.iniciarValidacao}" value="Validar" onclick="setAba('aluno')"/> </li>
				<li> <h:commandLink id="registroAtividadeiniciarExclusao" action="#{registroAtividade.iniciarExclusao}" value="Excluir" onclick="setAba('aluno')"/> </li>
			</ul>
		</li>
        <li>Movimenta��o de Aluno
            <ul>
            	<li><h:commandLink id="prorrogacaobuscarDiscente" action="#{prorrogacao.buscarDiscente}" value="Prorrogar Prazo de Conclus�o" onclick="setAba('discente')"/> </li>
            	<li><h:commandLink id="cadastrarAfastamentoTecnico" action="#{movimentacaoAluno.iniciarAfastamento}" value="Cadastrar Afastamento" onclick="setAba('aluno')"/></li>
				<li><h:commandLink id="concluirProgramaTecnico" action="#{movimentacaoAluno.iniciarConclusaoProgramaTecnico }" value="Concluir Programa"  onclick="setAba('aluno')"/></li>
				<li><h:commandLink id="cancelarProgramaTecnico" action="#{movimentacaoAluno.iniciarCancelamentoPrograma}" value="Cancelar Programa" onclick="setAba('aluno')"/></li>
				<li><h:commandLink id="retornoManualDiscenteTecnico" action="#{movimentacaoAluno.iniciarRetorno }" value="Retorno Manual de Discente" onclick="setAba('aluno')"/> </li>
				<li><h:commandLink id="retornarAlunosTrancadosTecnico" action="#{retornoTrancamento.iniciarTecnico }" value="Retornar Alunos de Trancamento" onclick="setAba('aluno')"/> </li>
				<li><h:commandLink id="estornarAfastamentoTecnico" action="#{movimentacaoAluno.iniciarEstorno }" value="Estornar Afastamento" onclick="setAba('aluno')"/></li>				
				<li><h:commandLink id="cancelarAlunoAbondonoTecnicoRef" action="#{jubilamentoMBean.iniciar}" value="Cancelar Aluno"  onclick="setAba('aluno')"/> </li>
				<li><h:commandLink id="cancelarAlunoReprovacoes" action="#{cancelamentoReprovacoesTecnicoMBean.iniciar}" value="Cancelar Aluno por Reprova��es"  onclick="setAba('aluno')"/> </li>
            </ul>
		</li>
        <li>Documentos
        	<ul>
        		<li> <h:commandLink id="emitirAtestadoMatriculaTecnico" action="#{ atestadoMatricula.buscarDiscente }" value="Emitir Atestado de Matr�cula" onclick="setAba('aluno')"/> </li>
                <li> <h:commandLink id="emitirHistoricoTecnico"	action="#{ historico.buscarDiscente }"	value="Emitir Hist�rico" onclick="setAba('aluno')"/></li>
             </ul>
        </li>
        <li>Orienta��o de Aluno
           <ul>
           		<li><h:commandLink id="cadastrarOrientacaoAcademicaTecnico" action="#{ orientacaoAcademica.iniciar }" value="Cadastrar Orienta��o Acad�mica" onclick="setAba('aluno')"/></li>
               	<li><h:commandLink id="gerenciarOrientacaoAcademicaTecnico" action="#{ orientacaoAcademica.gerenciarOrientacoes }" value="Gerenciar Orienta��es Acad�micas" onclick="setAba('aluno')"/></li>
            </ul>
        </li>
        <li>Notas/Retifica��es
		<ul>
			<li><h:commandLink id="retificarAprovConsTurmaTecnico" action="#{retificacaoMatricula.iniciar}" value="Retificar Aproveitamento e Consolida��o de Turma" onclick="setAba('aluno')"/></li>
	        <li><h:commandLink id="implantarHistoricoAlunoTecnico" action="#{implantarHistorico.iniciar}" value="Implantar Hist�rico do Aluno" onclick="setAba('aluno')"/> </li>
		</ul>
		</li>
		<li>Aproveitamento de Disciplina
		<ul>
			<li><h:commandLink
				action="#{aproveitamento.iniciarAproveitamento}" id="aproveitarDisciplinaTecnico"
				value="Aproveitar Disciplina" onclick="setAba('aluno')"/></li>
			<li><h:commandLink
				action="#{aproveitamento.iniciarCancelamento}" id="cancelarAproveitamentoDisciplinaTecnico"
				value="Cancelar Aproveitamento" onclick="setAba('aluno')"/></li>
		</ul>
		</li>
		<li> Transfer�ncia de Aluno entre Turmas
			<ul>
				<li> <h:commandLink id="transferenciaAutomaticaTecnico" action="#{transferenciaTurma.iniciarTecnicoAutomatica}" value="Transfer�ncia Autom�tica" onclick="setAba('aluno')"/> </li>
				<li> <h:commandLink id="transferenciaManualTecnico" action="#{transferenciaTurma.iniciarTecnicoManual}" value="Transfer�ncia Manual" onclick="setAba('aluno')"/> </li>
				<li> <h:commandLink id="transferenciaIndividualTecnico" action="#{transferenciaTurma.buscarDiscente}" value="Transfer�ncia Individual" onclick="setAba('aluno')"/> </li>
				<li> <h:commandLink id="transferenciaTurmaEntrada" action="#{ transferenciaTurmaEntradaMBean.iniciarTransfTurmaEntrada }" value="Transfer�ncia pela Turma de Entrada" onclick="setAba('aluno')"/> </li>
			</ul>
		</li>
		<li> Carteira de Estudante
			<ul>
				<li><a href="${ ctx }/arquivoSttu?nivel=T&unidade=${usuario.unidade.id}" id="listaAlunosSTTU">Lista de alunos para STTU</a></li>
				<li><a href="${ ctx }/arquivoSttu?nivel=T&log=true&unidade=${usuario.unidade.id}" id="listaAlunosProblema">Lista de alunos com problema</a></li>
			</ul>
		</li>
    </ul>
