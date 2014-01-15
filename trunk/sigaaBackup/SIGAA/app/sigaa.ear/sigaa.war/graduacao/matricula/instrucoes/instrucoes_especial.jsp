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
			O per�odo de matr�cula on-line estende-se de
			<span class="periodo">
				<ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioMatriculaAlunoEspecial}"/>
		 		a <b><ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimMatriculaAlunoEspecial}"/></b>
		 	</span>
		 	<c:if test="${matriculaGraduacao.calendarioParaMatricula.periodoReMatricula}">
			 	(com a re-matr�cula de
			 	<span class="periodo">
			 	<b><ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.inicioReMatricula}"/>
		 		a <ufrn:format type="data" valor="${matriculaGraduacao.calendarioParaMatricula.fimReMatricula}"/></b>).
		 		</span>
		 	</c:if>
		 	. Durante esse per�odo voc� poder� efetuar a matr�cula nos componentes curriculares desejados, de acordo com a
		 	oferta de turmas.
		</p>
		<p>
			Lembramos que a escolha das turmas est� sujeita �s regras do
			<a href="${linkPublico.urlDownloadPublico}/regulamento_dos_cursos_de_graduacao.pdf" target="_blank">	Regulamento dos Cursos de Gradua��o	</a>.
			Vale ressaltar ainda que essa opera��o permite somente a matr�cula
			em componentes curriculares do tipo DISCIPLINA ou M�DULO, que possuem turmas abertas no per�odo
			de ${calendarioAcademico.anoPeriodo}.
		</p>
		<p>
			Os chefes de departamento (ou coordenadores de curso, no caso de alunos em mobilidade estudantil ou em complementa��o de estudos)
			analisar�o suas solicita��es de matr�cula e poder�o aprovar ou indefer�-las, de acordo com seu plano de estudos.
		</p>		
		<p>
			Caso aprovadas, ap�s a data final desse per�odo voc� ser� automaticamente matriculado "EM ESPERA"
			em todas as turmas escolhidas e submetidas. <b style="color: #922;">At� a data final do per�odo de matr�cula on-line,
			� permitida a altera��o das turmas selecionadas, adicionando outras turmas ou removendo aquelas previamente escolhidas.</b>
			O deferimento das suas matr�culas est� sujeito ao processamento de matr�cula, que classificar�
			os alunos em cada turma com base nos crit�rios definidos no	regulamento dos cursos de gradua��o da ${ configSistema['siglaInstituicao'] }.
		</p>
		<c:if test="${matriculaGraduacao.parametroConcordanciaRegulamento}">
	 		<br>
			<p>
				<b>Antes de iniciar a sele��o das turmas para a sua matr�cula, voc� DEVE obter uma c�pia eletr�nica e declarar que est� ciente das regras
				introduzidas pelo novo regulamento dos cursos de gradua��o, aprovado na Resolu��o N� 227/2009-CONSEPE, de 3 de dezembro de 2009.</b>
			</p>
			<p style="line-height: 1.5em; margin: 1em 0.5em; font-size: 1.2em; text-align: center;">
				<a href="${linkPublico.urlDownloadPublico}/regulamento_dos_cursos_de_graduacao.pdf" onclick="showConcordancia();" target="_blank">	Clique aqui para baixar o NOVO REGULAMENTO DOS CURSOS DE GRADUA��O	</a>
			</p>
			<h:panelGroup id="grupoConcordancia" layout="block" style="text-align: center;">
					<h:selectBooleanCheckbox value="#{matriculaGraduacao.concordancia}" id="checkConcordancia" rendered="#{matriculaGraduacao.exibirConcordancia}" />
					<h:outputLabel for="checkConcordancia" id="labelConcordancia" rendered="#{matriculaGraduacao.exibirConcordancia}" style="line-height: 1.5em; margin: 1em 0.5em; font-size: 1.1em; text-align: center; font-weight: bold;">
					Declaro que recebi uma c�pia eletr�nica do Regulamento dos Cursos de Gradua��o da ${ configSistema['siglaInstituicao'] } e estou ciente 
					das altera��es introduzidas, particularmente a caracteriza��o de abandono de curso por falta de aumento no percentual 
					de integraliza��o curricular em um per�odo letivo, definida no inciso II do artigo 313 do referido regulamento.
					</h:outputLabel>
			</h:panelGroup>
		</c:if>
	</div>

	<center>
		<h:commandButton value="Iniciar Sele��o de Turmas >>" action="#{matriculaGraduacao.iniciarSolicitacaoMatricula}" id="btnIniciarSolicit"/>
	</center>
	</h:form>
	<script>
		<c:if test="${matriculaGraduacao.parametroConcordanciaRegulamento}">
			$('form:grupoConcordancia').style.display = "none";
		</c:if>
	</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
