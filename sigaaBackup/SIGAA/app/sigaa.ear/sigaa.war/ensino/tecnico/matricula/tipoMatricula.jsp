<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2> <ufrn:subSistema /> &gt; Matricular </h2>

<f:view>

<h:form>
<table align="center" width="100%" cellpadding="3" class="formulario">
<caption>Selecione uma modalidade de matrícula</caption>
<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
<tr align="center">
<td>
	<h3>
	<h:commandLink action="#{ matriculaTecnico.escolheTipo }" value="Matricular um Aluno em uma Turma">
		<f:param name="tipo" value="alunoTurma"/>
	</h:commandLink></h3><br/>
	Selecione esta opção para realizar a matrícula <br/>de apenas um aluno em uma única turma.
</td>
<ufrn:subSistema teste="infantil">
<td>
	<h3><h:commandLink action="#{ matriculaInfantil.verTurmas }" value="Matricular Alunos por Transferência de Turmas" />
	</h3><br/>
	Selecione esta opção para realizar a matrícula <br/>de alunos dos níveis infantis através de transferência de turmas.
</td>
</ufrn:subSistema>
<ufrn:subSistema teste="not infantil">
<ufrn:subSistema teste="not lato">
<ufrn:subSistema teste="not portalCoordenadorLato">
<td>
	<h3><h:commandLink action="#{ matriculaTecnico.escolheTipo }" value="Matricular um Aluno em um Módulo">
		<f:param name="tipo" value="alunoModulo"/>
	</h:commandLink></h3><br/>
	Selecione esta opção para realizar a matrícula <br/>de apenas um aluno nas disciplinas de um módulo.
</td>
</ufrn:subSistema>
</ufrn:subSistema>
</ufrn:subSistema>
</tr>
<ufrn:subSistema teste="not infantil">
<tr><td>&nbsp;</td><td>&nbsp;</td></tr>
<tr align="center">
<td>
	<h3><h:commandLink action="#{ matriculaTecnico.escolheTipo }" value="Matricular uma Turma de Entrada em uma Turma">
		<f:param name="tipo" value="turmaTurma"/>
	</h:commandLink></h3><br/>
	Selecione esta opção para realizar a matrícula de <br/> alunos de uma turma de entrada em uma única turma.
</td>

<ufrn:subSistema teste="not lato">
<ufrn:subSistema teste="not portalCoordenadorLato">
<td>
	<h3><h:commandLink action="#{ matriculaTecnico.escolheTipo }" value="Matricular uma Turma de Entrada em um Módulo">
		<f:param name="tipo" value="turmaModulo"/>
	</h:commandLink></h3><br/>
	Selecione esta opção para realizar a matrícula dos alunos <br/>de uma turma de entrada nas disciplinas de um módulo.
</td>
</ufrn:subSistema>
</ufrn:subSistema>

</tr>
</ufrn:subSistema>
<tr><td>&nbsp;</td><td>&nbsp;</td></tr>

<ufrn:subSistema teste="not portalCoordenadorLato">
<ufrn:subSistema teste="not lato">
<tr align="center">
<td colspan="2">
	<h3>
	<h:commandLink action="#{ matriculaTecnico.escolheTipo }" value="Matricular um Aluno em Turmas do Semestre">
		<f:param name="tipo" value="turmasSemestre"/>
	</h:commandLink></h3><br/>
	Selecione esta opção para realizar a matrícula <br/>de um aluno em algumas turmas do semestre atual.
</td>
</tr>
</ufrn:subSistema>
</ufrn:subSistema>

</table>
</h:form>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
