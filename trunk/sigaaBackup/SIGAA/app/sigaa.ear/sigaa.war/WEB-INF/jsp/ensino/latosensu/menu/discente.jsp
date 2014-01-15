<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/tags/struts-html" prefix="html"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"  %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>

    <ul>

		<li>Aluno
            <ul>
                <li><h:commandLink id="cadastrarNovoAluno" action="#{discenteLato.iniciarCadastroDiscenteNovo}" value="Cadastrar Novo Aluno" onclick="setAba('aluno')"/></li>
                <li><h:commandLink id="alterarRemoverAluno" action="#{discenteLato.alterarRemover}" value="Alterar/Remover" onclick="setAba('aluno')"/></li>
 				<li><h:commandLink id="emitirHistorico"	action="#{ historico.buscarDiscente }"	value="Emitir Hist�rico" onclick="setAba('aluno')"/></li>
				<li><h:commandLink id="linkAlterarStatusDiscentes" action="#{alteracaoStatusDiscente.iniciar}" value="Alterar Status do Discente"  onclick="setAba('aluno')"/> </li>
               	<li><h:commandLink id="concluirPrograma" value="Concluir Programa"	action="#{movimentacaoAluno.iniciarConclusaoProgramaLato }" onclick="setAba('aluno')"/></li>
				<li><h:commandLink id="cancelarPrograma" action="#{movimentacaoAluno.iniciarCancelamentoPrograma}" value="Cancelar Programa" onclick="setAba('aluno')"/></li>
            </ul>
        </li>

        <li>Matr�cula
        	<ul>
        		<li><a href="${ ctx }/ensino/tecnico/matricula/tipoMatricula.jsf?aba=aluno">Efetuar Matr�cula em Turma</a></li>
        		<li><h:commandLink id="alterarStatusMatriculaTurma" action="#{alteracaoStatusMatricula.iniciar }" value="Alterar Status de Matr�culas em Turmas" onclick="setAba('aluno')"/></li>
 				<li><h:commandLink id="retificarConsolidacaoTurma"  action="#{retificacaoMatricula.iniciar}" value="Retificar Consolida��o de Turma" onclick="setAba('aluno')"/></li>
 				<li><h:commandLink id="consolidacaoIndividual" action="#{consolidacaoIndividual.iniciar}" value="Consolida��o Individual" onclick="setAba('aluno')"/> </li>
        	</ul>
        </li>
		
		<li>Movimenta��o de Aluno
            <ul>
				<li><h:commandLink id="conclusaoEfetivaPrograma" action="#{conclusaoCursoLatoMBean.iniciar}" value="Conclus�o Coletiva de Programa" onclick="setAba('aluno');"/></li>
            </ul>
		</li>

		<li>Trabalho Final
			<ul>
		        <li><h:commandLink id="cadastrarTrabalhoFinal" action="#{registroAtividade.iniciarValidacaoLatoSensu}" value="Cadastrar" onclick="setAba('aluno')"/></li>
				<li><h:commandLink id="alterarTrabalhoFinal" action="#{registroAtividade.alterarValidacaoLatoSensu}" value="Alterar" onclick="setAba('aluno')"/></li>
			</ul>
        </li>
        <li> Carteira de Estudante
			<ul>
				<li><a href="${ ctx }/arquivoSttu?nivel=L&log=true">Alunos com problema</a></li>
			</ul>
		</li>
    </ul>
