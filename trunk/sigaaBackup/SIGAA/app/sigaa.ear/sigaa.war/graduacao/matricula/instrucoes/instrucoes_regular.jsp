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
<script>

 function showConcordancia() {
	 
	var elem = $('form:grupoConcordancia');
	var dpy = elem.style.display;
	if (dpy == "none" || dpy == "") {
		elem.style.display = "block";
	} 
 }

</script>

	<%@include file="/portais/discente/menu_discente.jsp" %>
	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>
	<h:form id="form">
	<div class="descricaoOperacao">
		<h4>Caro(a) Aluno(a),</h4> <br />
		<p>
			O período de matrícula on-line estende-se de
			<span class="periodo">
				<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioMatriculaOnline}"/>
		 		a <b><ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimMatriculaOnline}"/></b>${matriculaGraduacao.calendarioParaMatricula.periodoReMatricula ? '' : '.'}
		 	</span>
		 	<c:if test="${matriculaGraduacao.calendarioParaMatricula.periodoReMatricula}">
			 	(com a re-matrícula de
			 	<span class="periodo">
			 	<b><ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioReMatricula}"/>
		 		a <ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimReMatricula}"/></b>).
		 		</span>
		 	</c:if>
		 	Durante esse período você poderá efetuar a matrícula nos componentes curriculares desejados, de acordo com a
		 	oferta de turmas.
		</p>
		<p>
			Lembramos que a escolha das turmas está sujeita às regras do
			<a href="${linkPublico.urlDownloadPublico}/regulamento_dos_cursos_de_graduacao.pdf" target="_blank">	Regulamento dos Cursos de Graduação	</a>
			tais como a verificação de pré-requisitos e co-requisitos, as matrículas em componentes
			equivalentes, entre outras. Vale ressaltar ainda que essa operação permite somente a matrícula
			em componentes curriculares do tipo DISCIPLINA ou MÓDULO, que possuem turmas abertas no ano-semestre
			de ${calendarioAcademico.anoPeriodo}.
		</p>
		<p>
			Para realizar a consulta e seleção das turmas disponíveis você terá duas opções:
			<ul>
				<li><i>Turmas da Estr. Curricular:</i> aqui serão listadas todas as turmas abertas para componentes do seu currículo;</li>
				<li><i>Buscar Outras Turmas:</i> aqui você poderá buscar e selecionar qualquer turma aberta.</li>
			</ul>
		</p>
		<p>
			Após a data final desse período você será automaticamente matriculado "EM ESPERA"
			em todas as turmas escolhidas e submetidas. <b style="color: #922;">Até a data final do período de matrícula on-line,
			é permitida a alteração das turmas selecionadas, adicionando outras turmas ou removendo aquelas previamente escolhidas.</b>
			O deferimento das suas matrículas está sujeito ao processamento de matrícula, que classificará
			os alunos em cada turma com base nos critérios definidos no	regulamento dos cursos de graduação da ${ configSistema['siglaInstituicao'] }.
		</p>
		<p>
			Os orientadores acadêmicos e coordenadores do seu curso poderão fazer observações sobre as turmas
			escolhidas até o dia
			<span class="periodo"><ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimCoordenacaoAnaliseMatricula}"/>.</span>
			É importante lembrar que essas observações têm o objetivo somente de orientar os alunos. Nenhuma orientação
			é capaz de cancelar ou excluir uma matrícula escolhida por você, sendo de sua responsabilidade
			excluir, caso desejado,	as matrículas nas turmas escolhidas até o prazo final da matrícula on-line.
		</p>
		<c:if test="${matriculaGraduacao.parametroConcordanciaRegulamento}">
	 		<br>
			<p>
				<b>Antes de iniciar a seleção das turmas para a sua matrícula, você DEVE obter uma cópia eletrônica e declarar que está ciente das regras
				introduzidas pelo novo regulamento dos cursos de graduação, aprovado na Resolução Nº 227/2009-CONSEPE, de 3 de dezembro de 2009.</b>
			</p>
			<p style="line-height: 1.5em; margin: 1em 0.5em; font-size: 1.2em; text-align: center;">
				<a href="${linkPublico.urlDownloadPublico}/regulamento_dos_cursos_de_graduacao.pdf" onclick="showConcordancia();" target="_blank">	Clique aqui para baixar o NOVO REGULAMENTO DOS CURSOS DE GRADUAÇÃO	</a>
			</p>
			<h:panelGroup id="grupoConcordancia" layout="block" style="text-align: center;">
					<h:selectBooleanCheckbox value="#{matriculaGraduacao.concordancia}" id="checkConcordancia" rendered="#{matriculaGraduacao.exibirConcordancia}" />
					<h:outputLabel for="checkConcordancia" id="labelConcordancia" rendered="#{matriculaGraduacao.exibirConcordancia}" style="line-height: 1.5em; margin: 1em 0.5em; font-size: 1.1em; text-align: center; font-weight: bold;">
					Declaro que recebi uma cópia eletrônica do Regulamento dos Cursos de Graduação da ${ configSistema['siglaInstituicao'] } e estou ciente 
					das alterações introduzidas, particularmente a caracterização de abandono de curso por falta de aumento no percentual 
					de integralização curricular em um período letivo, definida no inciso II do artigo 313 do referido regulamento.
					</h:outputLabel>
			</h:panelGroup>
		</c:if>
	</div>

	<center>
		<h:commandButton value="Iniciar Seleção de Turmas >>" action="#{matriculaGraduacao.iniciarSolicitacaoMatricula}" id="btnIniciarSolicit"/>
	</center>
	</h:form>
	<script>
		<c:if test="${matriculaGraduacao.parametroConcordanciaRegulamento}">
			$('form:grupoConcordancia').style.display = "none";
		</c:if>
	</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
