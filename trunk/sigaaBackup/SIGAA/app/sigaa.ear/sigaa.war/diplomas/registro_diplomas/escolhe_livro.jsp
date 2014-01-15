<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:form id="cadastroCurso" >

<h2 class="title"><ufrn:subSistema /> > Registro de Diploma Coletivo</h2>

<table class="formulario" >
	<caption class="formulario">Escolha o Livro Onde Será Registrada a Turma</caption>
	<tbody>
		<tr>
			<th width="15%" class="required">Livro:</th>
			<td>
				<h:selectOneMenu value="#{registroDiplomaColetivo.obj.livroRegistroDiploma.id}" id="livro">
					<f:selectItem itemValue="0" itemLabel="---> SELECIONE <---"/>
					<f:selectItems value="#{livroRegistroDiplomas.livrosSemCurso}"/>
				</h:selectOneMenu>
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton id="voltar" value="<< Voltar" action="#{registroDiplomaColetivo.formBuscaCurso}" />
				<h:commandButton id="avancar" value="Avançar >> " action="#{registroDiplomaColetivo.criaRegistroAutomatico}" />
				<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}" action="#{livroRegistroDiplomas.cancelar}" immediate="true" />
			</td>
		</tr>
	</tfoot>
</table>

	<br>
	<center><html:img page="/img/required.gif"
		style="vertical-align: top;" /> <span class="fontePequena">
	Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
