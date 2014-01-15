<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<style>
	span.periodo {
		color: #292;
		font-weight: bold;
	}

	descricaoOperacao p{
		line-height: 1.25em;
		margin: 8px 10px;
	}
</style>

	<%@include file="/portais/discente/menu_discente.jsp" %>
	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>
	<div class="descricaoOperacao">
		<h4>Caro(a) Aluno(a),</h4> <br />
		<p>
			O per�odo de matr�cula on-line extende-se de
			<span class="periodo">
				<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioMatriculaOnline}"/>
		 		a <b><ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimMatriculaOnline}"/></b>
		 	</span>.
		 	Durante este per�odo voc� poder� efetuar a matr�cula nas disciplinas desejadas, de acordo com a
		 	oferta de turmas.
		</p>
		<p>
			Lembramos que a escolha das turmas est� sujeita �s regras do regulamento dos cursos t�cnicos da escola de M�sica,
			tais como a verifica��o de pr�-requisitos. Vale ressaltar ainda que essa opera��o permite somente a matr�cula
			em disciplinas que possuem turmas abertas no ano-semestre de ${calendarioAcademico.anoPeriodo}.
		</p>
		<p>
			Para realizar a consulta e sele��o das turmas dispon�veis voc� poder� acessar a op��o:
			<ul>
				<li><i>Turmas da Estr. Curricular:</i> aqui ser�o listadas todas as turmas abertas para disciplinas do seu curr�culo;</li>
			</ul>
		</p>
		<p>
			� importante lembrar que a efetiva��o de todas as matr�culas de todos os alunos da escolha est� sujeita � aprova��o
			da coordena��o do seu curso.
			A coordena��o tem at� o dia <span class="periodo"><ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimCoordenacaoAnaliseMatricula}"/></span>
			para analisar todas as matr�culas, e tem o poder de n�o permitir a matr�cula em turmas de acordo com seu julgamento.
		</p>
		<p>
			Dessa forma, somente ap�s o per�odo de an�lise das matr�culas por parte da coordena��o ser� poss�vel saber se voc�
			realmente conseguiu se matricular nas turmas.
		<br>
		<p>
			<b>Para iniciar a sele��o das turmas para a sua matricula clique no bot�o abaixo.</b>
		</p>
	</div>

	<center>
	<h:form>
		<h:commandButton value="Iniciar Sele��o de Turmas >>" action="#{matriculaGraduacao.iniciarSolicitacaoMatricula}" id="iniciarSolicitTurmas"/>
	</h:form>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
