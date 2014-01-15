<%@ taglib uri="/tags/ufrn" prefix="ufrn"  %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

    <ul>
   		<li> Turma
   			<ul>
				<li><h:commandLink action="#{turmaSerie.preCadastrar}" value="Cadastrar" onclick="setAba('turma')" />	</li>
				<li><h:commandLink action="#{turmaSerieDependencia.preCadastrar}" value="Cadastrar Turma de Dependência" onclick="setAba('turma')" />	</li>
				<li><h:commandLink action="#{turmaSerie.listar}" value="Listar/Alterar" onclick="setAba('turma')"/> </li>
				<li><h:commandLink action="#{consolidarDisciplinaMBean.iniciar}" value="Consolidar Turma" onclick="setAba('turma')"/> </li>
				<!--<li><h:commandLink action="#" value="Consulta Geral de Turmas" onclick="setAba('turma')"/> </li>-->
   			</ul>
   		</li>
   		<li> Transferência de Aluno entre Turmas
			<ul>
				<li> <h:commandLink action="#{transferenciaTurmaMedioMBean.iniciarAutomatica}" value="Transferência Automática" onclick="setAba('turma')"/> </li>
				<li> <h:commandLink action="#{transferenciaTurmaMedioMBean.iniciarManual}" value="Transferência Manual" onclick="setAba('turma')"/> </li>
			</ul>
		</li>
		<li> Processamento de Série
			<ul>
				<li> <h:commandLink id="consolidarMatriculaSerie" action="#{processamentoMatriculaDiscenteSerie.iniciar}" value="Consolidação de Discentes na Série" onclick="setAba('turma')"/> </li>
				<li> <h:commandLink id="resultadoProcessamentoMatriculaSerie" action="#{processamentoMatriculaDiscenteSerie.iniciarResultado}" value="Resultado de Consolidação de Discentes em Série" onclick="setAba('turma')"/> </li>
				<li> <h:commandLink id="sinalizarAlunosDependencia" action="#{matriculaDiscenteSerieMBean.iniciarPreReprovados}" value="Sinalizar Alunos para Dependência" onclick="setAba('turma')"/> </li>
			</ul>
		</li>
		
		
		
    </ul>
