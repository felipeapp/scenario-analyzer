<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script>
	JAWR.loader.script('/javascript/jquery/jquery.js');
</script>
<script>
	// Muda o nome do jQuery para J, evitando conflitos com a Prototype.
	var jQuery = jQuery.noConflict();
</script>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Cadastro de Tutores para Pólos > Cadastrar</h2>

	<h:messages showDetail="true"></h:messages>
	<br>
	<h:form id="formulario">
		<c:if test="${tutorOrientador.confirmButton == 'Remover' }">
		<table class="formulario" width="90%">
			<h:outputText value="#{tutorOrientador.create}" />
			<caption class="listagem">Cadastro de Tutores</caption>
			<tr>
				<th style="font-weight:bold;">Tutor:</th>
				<td><h:outputText value="#{tutorOrientador.obj.pessoa.nome }" /></td>
			</tr>
			<tr>
				<th>Vínculo:</th>
				<td><h:outputText value="#{tutorOrientador.obj.vinculo.nome }" /></td>
			</tr>
			<tr>
				<th>Curso:</th>
				<td>${tutorOrientador.obj.poloCurso.curso.descricao }</td>
			</tr>
			<tr>
				<th>Pólo:</th>
				<td>${tutorOrientador.obj.poloCurso.polo.cidade.nome}</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="4">
					<input type="hidden" name="id" value="${ tutorOrientador.obj.id }"/>
					<h:commandButton value="#{tutorOrientador.confirmButton}"
						action="#{tutorOrientador.remover}" /> <h:commandButton value="Cancelar"
						onclick="#{confirm}" action="#{tutorOrientador.cancelar}" /></td>
				</tr>
			</tfoot>
		</table>
		</c:if>
	
		<c:if test="${tutorOrientador.confirmButton != 'Remover' }">

		<table class="formulario" width="90%">
			<h:outputText value="#{tutorOrientador.create}" />
			<caption class="listagem">Cadastro de Tutores</caption>
			<tr>
				<th style="font-weight:bold;">Tutor:</th>
				<td><h:outputText value="#{tutorOrientador.obj.pessoa.nome }" /></td>
			</tr>
			<tr>
				<th class="required">Vínculo:</th>
				<td>
					<h:selectOneMenu value="#{tutorOrientador.obj.vinculo.id }">
						<f:selectItems value="#{ tutorOrientador.allVinculos }"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Pólo:</th>
				<td>
					<h:selectOneMenu value="#{tutorOrientador.polo.id}" valueChangeListener="#{tutorOrientador.carregarCursosCombo}" id="polo">
						<f:selectItem itemValue="0" itemLabel="Escolha um pólo" />
						<f:selectItems value="#{tutorOrientador.polos}" />
						<a4j:support event="onchange" onsubmit="true" oncomplete="marcarTodos(false)" reRender="panelCursos" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<a4j:outputPanel id="panelCursos">
						<a4j:outputPanel rendered="#{ not empty tutorOrientador.cursosCombo }">
							<table style="margin-left:17.8%;">
								<th class="required">Cursos:</th>
								<td>
									<label id="marcarTodos" onclick="marcarTodos()"><img src="/sigaa/img/check.png"> MARCAR TODOS</label>
									<h:selectManyCheckbox id="associarEm" value="#{ tutorOrientador.associarEm }" layout="pageDirection">
										<f:selectItems value="#{ tutorOrientador.cursosCombo }" />
									</h:selectManyCheckbox>
								</td>
							</table>
						</a4j:outputPanel>
					</a4j:outputPanel>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="4">
						<h:commandButton value="#{tutorOrientador.confirmButton}" action="#{tutorOrientador.cadastrar}" />
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{tutorOrientador.cancelar}" />
					</td>
				</tr>
			</tfoot>
		</table>
		</c:if>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

<script type="text/javascript">
function marcarTodos(marcarTodos) {

	if (marcarTodos!=null)
		marcar = marcarTodos;
		
	jQuery("#formulario\\:associarEm").find("input").each(
			function(index,item) {
				if (!marcar)
					item.checked = true;
				else
					item.checked = false;
			}
	);

	if (marcar) {
		marcar = false;
		jQuery("#marcarTodos").html('<img src="/sigaa/img/check.png"> MARCAR TODOS</label>');
	}	
	else {
		marcar = true;
		jQuery("#marcarTodos").html('<img src="/sigaa/img/check_cinza.png"> DESMARCAR TODOS</label>');
	}	
	
}
var marcar = false;
</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
