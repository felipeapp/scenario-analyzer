<%@ taglib uri="/tags/ufrn" prefix="ufrn"  %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
    <ul>
        <li>Aluno
            <ul>
                <li> <h:commandLink id="cadastrarDiscente"  action="#{discenteMedio.iniciarNovoDiscente}" value="Cadastrar" onclick="setAba('aluno')"/></li>
                <li> <h:commandLink id="alterarRemoverAluno" action="#{discenteMedio.atualizar}" value="Listar/Alterar" onclick="setAba('aluno')"/></li>
 				<li> <h:commandLink id="atualizarDadosPessoais" action="#{alteracaoDadosDiscente.iniciar}" value="Atualizar Dados Pessoais" onclick="setAba('aluno')"/> </li>
				<li> <h:commandLink id="editarObservacoesDiscente" action="#{observacaoDiscente.iniciar}" value="Editar Observações do Discente" onclick="setAba('aluno')"/> </li>
				<li> <h:commandLink id="editarObservacoesDiscenteSerie" action="#{observacaoDiscenteSerieMBean.iniciar}" value="Editar Observações em Série do Discente" onclick="setAba('aluno')"/> </li>
                <li> <h:commandLink id="cadastrarDiscenteAntigo" action="#{discenteMedio.iniciarDiscenteAntigo}" value="Cadastrar Aluno Antigo" onclick="setAba('aluno')"/> </li>
                <li> <h:commandLink id="implantarHistoricoAlunoMedio" action="#{implantarHistoricoMedioMBean.iniciar}" value="Implantar Histórico do Aluno" onclick="setAba('aluno')"/> </li>
             </ul>
        </li>
        <li>Matrícula
            <ul>
            	<li> <h:commandLink id="matricularAluno" action="#{matriculaMedio.iniciarMatriculaDiscente}" value="Matricular Aluno em Série" onclick="setAba('aluno')"/> </li>
            	<li> <h:commandLink id="matricularAlunoDependencia" action="#{matriculaMedio.iniciarMatriculaDependencia}" value="Matricular Aluno em Dependência" onclick="setAba('aluno')"/> </li>
                <li> <h:commandLink id="alterarStatusMatricula" action="#{alteracaoStatusMatriculaMedioMBean.iniciar}" value="Alterar Status de Matrículas em Série" onclick="setAba('aluno')"/> </li>
        	    <li> <h:commandLink id="alterarStatusMatriculaDisciplina" action="#{alteracaoStatusMatriculaMedioMBean.iniciarAlteracaoStatusDisciplina}" value="Alterar Status de Matrículas em Disciplina" onclick="setAba('aluno')"/> </li>
        	</ul>
        </li>
        <li>Movimentação de Aluno
            <ul>
            	<li> <h:commandLink id="concluirProgramaMedio"     action="#{afastamentoDiscenteMedioMBean.iniciarConclusaoProgramaMedio}" value="Concluir Programa" onclick="setAba('aluno')"/></li>
            	<li> <h:commandLink id="cadastrarAfastamentoMedio" action="#{afastamentoDiscenteMedioMBean.iniciarAfastamento}" value="Cadastrar Afastamento" onclick="setAba('aluno')"/></li>
				<li> <h:commandLink id="estornarAfastamentoMedio" action="#{afastamentoDiscenteMedioMBean.iniciarEstorno}" value="Estornar Afastamento" onclick="setAba('aluno')"/></li>
		    </ul>
		</li>
        <li>Documentos
        	<ul>
        		<li> <h:commandLink id="emitirAtestadoMatriculaMedio" action="#{ atestadoMatriculaMedio.buscarDiscente }" value="Emitir Atestado de Matrícula" onclick="setAba('aluno')"/> </li>
                <li> <h:commandLink id="emitirBoletim"	action="#{boletimMedioMBean.iniciar}" value="Emitir Boletim" onclick="setAba('aluno')"/></li>
                <li> <h:commandLink id="emitirHistorico" action="#{historicoMedio.buscarDiscente}" value="Emitir Histórico" onclick="setAba('aluno')"/></li>
             </ul>
        </li>
         <li>Notas/Retificações
		<ul>
			<li><h:commandLink id="retificarAprovConsTurmaTecnico" action="#{retificacaoMatricula.iniciar}" value="Retificar Aproveitamento e Consolidação de Turma" onclick="setAba('aluno')"/></li>
			<li><h:commandLink id="consolidacaoIndividual" action="#{consolidacaoIndividualMedio.iniciar}" value="Consolidação Individual" onclick="setAba('aluno')"/></li>
		</ul>
		</li>
        <li> Transferência de Aluno entre Turmas
			<ul>
				<li> <h:commandLink action="#{transferenciaTurmaMedioMBean.iniciarAutomatica}" value="Transferência Automática" onclick="setAba('aluno')"/> </li>
				<li> <h:commandLink action="#{transferenciaTurmaMedioMBean.iniciarManual}" value="Transferência Manual" onclick="setAba('aluno')"/> </li>
			</ul>
		</li>
		<li> Operações Administrativas
			<ul>
                <li> <h:commandLink action="#{alteracaoStatusDiscente.iniciar}" value="Alterar Status de Aluno"  onclick="setAba('aluno')"/> </li>
			</ul>
		</li>	
    </ul>