<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:outputText value="#{exclusaoMatricula.create}"/>

	<h2> Exclusão de Matrícula > Confirmação </h2>

	<c:set var="discente" value="#{exclusaoMatricula.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	<h:form>
	<table class="formulario" style="width: 80%">
		<caption> Confira os dados para efetuar a exclusão da matrícula </caption>
		<tbody>
			<tr>
				<th> Disciplina Selecionada: </th>
				<td> ${exclusaoMatricula.obj.turma.disciplina.codigoNome}</td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="Confirmar" action="#{exclusaoMatricula.efetuarExclusao}" id="efetuarExclusao"/>
					<h:commandButton value="Selecionar outra disciplina" action="#{exclusaoMatricula.telaSelecaoDisciplina}" id="selecionarOutraDisciplina"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{exclusaoMatricula.cancelar}" id="cancelarOpeacao"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>