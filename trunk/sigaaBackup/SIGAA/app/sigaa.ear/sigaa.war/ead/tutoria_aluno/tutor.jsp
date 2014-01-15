<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h2> <ufrn:subSistema /> > Associar Alunos a Tutor > Selecionar Tutor </h2>
	<h:messages showDetail="true"></h:messages>

	<h:form id="form">
	<table class="formulario">
		<caption> Selecione o Tutor </caption>
		<tbody>
			<tr>
				<th style="vertical-align: middle;">Curso: <h:graphicImage url="/img/required.gif" /></th>
				<td>
					<h:selectOneMenu value="#{tutoriaAluno.cursoReferencia.id}" id="curso" onchange="submit()" valueChangeListener="#{tutoriaAluno.carregarTutoresCurso }">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM CURSO --" />
						<f:selectItems value="#{tutoriaAluno.cursosComPolo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th>Pólo:</th>
				<td>
					<h:selectOneMenu value="#{tutoriaAluno.polo.id}" id="polo" onchange="submit()"  valueChangeListener="#{ tutoriaAluno.carregarTutoresPolo }">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM PÓLO --" />
						<f:selectItems value="#{tutoriaAluno.polos}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th style="vertical-align: middle;">Tutor: <h:graphicImage url="/img/required.gif" /></th>
				<td> 
					<h:selectOneMenu value="#{tutoriaAluno.tutor.id}" id="tutor">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM TUTOR --" />
						<f:selectItems value="#{tutoriaAluno.possiveisTutores}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
		</tbody>
		<tfoot>
			<tr><td colspan="2">
				<h:commandButton value="Cancelar" action="#{ tutoriaAluno.cancelar }" onclick="#{confirm }"/>
				<h:commandButton value="Avançar >> " action="#{ tutoriaAluno.selecionarOrientador }"/> 
			</td></tr>
		</tfoot>
	</table>

	</h:form>
	<br>
	<center>
	<h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
	<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>