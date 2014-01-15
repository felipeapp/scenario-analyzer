<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@include file="/portais/discente/menu_discente.jsp" %>
<h2 class="title">Solicita��o de Turma de F�rias > Confirma��o </h2>
<h:outputText value="#{confirmacaoMatriculaFeriasBean.create}"/>

<c:set var="discente" value="#{confirmacaoMatriculaFeriasBean.obj.discente}"/>
<%@include file="/graduacao/info_discente.jsp"%>

<h:form id="confirmacaoMatriculaFerias">

	<table class="formulario" width="100%">
		<caption>TERMO DE MATR�CULA EM PER�ODO LETIVO ESPECIAL DE F�RIAS</caption>
		<tr>
			<td colspan="2">
			
			<div class="descricaoOperacao" style="line-height: 1.5em; margin: 1em 0.5em; font-size: 1.2em; text-indent: 2em; text-align: justify; width: 90%" >
		     	Eu, ${confirmacaoMatriculaFeriasBean.obj.discente.nome}, matr�cula n� ${confirmacaoMatriculaFeriasBean.obj.discente.matricula}, 
		     	aluno do Curso de ${confirmacaoMatriculaFeriasBean.obj.discente.curso.descricao}, 
		     	${confirmacaoMatriculaFeriasBean.obj.discente.matrizCurricular.grauAcademico.descricao}, 
		     	Turno ${confirmacaoMatriculaFeriasBean.obj.discente.matrizCurricular.turno.sigla},
		     	<c:if test="${not empty confirmacaoMatriculaFeriasBean.obj.discente.matrizCurricular.habilitacao}">
		     	habilita��o ${confirmacaoMatriculaFeriasBean.obj.discente.matrizCurricular.habilitacao},</c:if> 
		     	<c:if test="${not empty confirmacaoMatriculaFeriasBean.obj.discente.matrizCurricular.enfase}">
		     	�nfase ${confirmacaoMatriculaFeriasBean.obj.discente.matrizCurricular.enfase.nome},</c:if> 
		     	venho por meio deste, nos termos do Regulamento dos Cursos Regulares de Gradua��o 
		     	(Resolu��o N� 227/2009-CONSEPE, de 3 de dezembro de 2009), 
		     	autorizar minha matr�cula na disciplina ${confirmacaoMatriculaFeriasBean.obj.turma.disciplina.codigo}, 
		     	${confirmacaoMatriculaFeriasBean.obj.turma.disciplina.nome}, Turma ${confirmacaoMatriculaFeriasBean.obj.turma.codigo}, 
		     	oferecida no per�odo letivo especial de f�rias ${confirmacaoMatriculaFeriasBean.obj.turma.anoPeriodo}, 
		     	estando ciente de que me � vetado o trancamento da matr�cula na referida turma, 
		     	de acordo com o artigo 247 da referida resolu��o.
			</div>	
		
			</td>
		</tr>
	
	
		<tr>
			<th width="50%"><b>Aceita os termos descritos acima?</b></th>
			<td> 
				<h:selectOneRadio value="#{confirmacaoMatriculaFeriasBean.obj.confirmou}">
					<f:selectItem itemValue="true" itemLabel="Sim"/>
					<f:selectItem itemValue="false" itemLabel="N�o"/>
				</h:selectOneRadio> 
			</td>
		</tr>
		
		<tr>
			<td colspan="2">
				<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
			</td>
		</tr>	

		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Confirmar Matr�cula" action="#{confirmacaoMatriculaFeriasBean.confirmarMatricula}" />
					<h:commandButton value="<< Voltar" action="#{confirmacaoMatriculaFeriasBean.iniciar}" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{confirmacaoMatriculaFeriasBean.cancelar}" />
				</td>
			</tr>
		</tfoot>
	</table>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>