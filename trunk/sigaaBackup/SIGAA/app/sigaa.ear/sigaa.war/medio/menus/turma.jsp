<%@ taglib uri="/tags/ufrn" prefix="ufrn"  %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>

    <ul>
   		<li> Turma
   			<ul>
				<li><h:commandLink action="#{turmaSerie.preCadastrar}" value="Cadastrar" onclick="setAba('turma')" />	</li>
				<li><h:commandLink action="#{turmaSerieDependencia.preCadastrar}" value="Cadastrar Turma de Depend�ncia" onclick="setAba('turma')" />	</li>
				<li><h:commandLink action="#{turmaSerie.listar}" value="Listar/Alterar" onclick="setAba('turma')"/> </li>
				<li><h:commandLink action="#{consolidarDisciplinaMBean.iniciar}" value="Consolidar Turma" onclick="setAba('turma')"/> </li>
				<!--<li><h:commandLink action="#" value="Consulta Geral de Turmas" onclick="setAba('turma')"/> </li>-->
   			</ul>
   		</li>
   		<li> Transfer�ncia de Aluno entre Turmas
			<ul>
				<li> <h:commandLink action="#{transferenciaTurmaMedioMBean.iniciarAutomatica}" value="Transfer�ncia Autom�tica" onclick="setAba('turma')"/> </li>
				<li> <h:commandLink action="#{transferenciaTurmaMedioMBean.iniciarManual}" value="Transfer�ncia Manual" onclick="setAba('turma')"/> </li>
			</ul>
		</li>
		<li> Processamento de S�rie
			<ul>
				<li> <h:commandLink id="consolidarMatriculaSerie" action="#{processamentoMatriculaDiscenteSerie.iniciar}" value="Consolida��o de Discentes na S�rie" onclick="setAba('turma')"/> </li>
				<li> <h:commandLink id="resultadoProcessamentoMatriculaSerie" action="#{processamentoMatriculaDiscenteSerie.iniciarResultado}" value="Resultado de Consolida��o de Discentes em S�rie" onclick="setAba('turma')"/> </li>
				<li> <h:commandLink id="sinalizarAlunosDependencia" action="#{matriculaDiscenteSerieMBean.iniciarPreReprovados}" value="Sinalizar Alunos para Depend�ncia" onclick="setAba('turma')"/> </li>
			</ul>
		</li>
		
		
		
    </ul>
