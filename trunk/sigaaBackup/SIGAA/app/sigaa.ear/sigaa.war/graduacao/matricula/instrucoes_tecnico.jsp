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
			O período de matrícula on-line extende-se de
			<span class="periodo">
				<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioMatriculaOnline}"/>
		 		a <b><ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimMatriculaOnline}"/></b>
		 	</span>.
		 	Durante este período você poderá efetuar a matrícula nas disciplinas desejadas, de acordo com a
		 	oferta de turmas.
		</p>
		<p>
			Lembramos que a escolha das turmas está sujeita às regras do regulamento dos cursos técnicos da escola de Música,
			tais como a verificação de pré-requisitos. Vale ressaltar ainda que essa operação permite somente a matrícula
			em disciplinas que possuem turmas abertas no ano-semestre de ${calendarioAcademico.anoPeriodo}.
		</p>
		<p>
			Para realizar a consulta e seleção das turmas disponíveis você poderá acessar a opção:
			<ul>
				<li><i>Turmas da Estr. Curricular:</i> aqui serão listadas todas as turmas abertas para disciplinas do seu currículo;</li>
			</ul>
		</p>
		<p>
			É importante lembrar que a efetivação de todas as matrículas de todos os alunos da escolha está sujeita à aprovação
			da coordenação do seu curso.
			A coordenação tem até o dia <span class="periodo"><ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimCoordenacaoAnaliseMatricula}"/></span>
			para analisar todas as matrículas, e tem o poder de não permitir a matrícula em turmas de acordo com seu julgamento.
		</p>
		<p>
			Dessa forma, somente após o período de análise das matrículas por parte da coordenação será possível saber se você
			realmente conseguiu se matricular nas turmas.
		<br>
		<p>
			<b>Para iniciar a seleção das turmas para a sua matricula clique no botão abaixo.</b>
		</p>
	</div>

	<center>
	<h:form>
		<h:commandButton value="Iniciar Seleção de Turmas >>" action="#{matriculaGraduacao.iniciarSolicitacaoMatricula}" id="iniciarSolicitTurmas"/>
	</h:form>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
