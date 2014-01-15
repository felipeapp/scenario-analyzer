<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript">
function marcarTodos() {
	var elements = document.getElementsByTagName('input');
	for (i = 0; i < elements.length; i++) {
		if (elements[i].type == 'checkbox') {
			elements[i].checked = true;
		}
	}
}

function desmarcarTodos() {
	var elements = document.getElementsByTagName('input');
	for (i = 0; i < elements.length; i++) {
		if (elements[i].type == 'checkbox') {
			elements[i].checked = false;
		}
	}
}
</script>

<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Matricular Alunos em Lote </h2>
<br/>

<f:view>
	<h:form id="matricula">
		<h:dataTable value="#{ matriculaResidenciaMedica.discentes }" var="_discente" width="100%" styleClass="listagem" rendered="#{ not empty matriculaResidenciaMedica.discentes }">
			<f:facet name="caption"><f:verbatim>Discentes Encontrados</f:verbatim></f:facet>
			<h:column>
				<f:facet name="header">
					<f:verbatim>
					<a href="#" onclick="marcarTodos();">Todos</a>
					</f:verbatim>
				</f:facet>
				<h:selectBooleanCheckbox value="#{ _discente.selecionado }" rendered="#{ not _discente.matricular }"/>
			</h:column>
			<h:column>
				<f:facet name="header"><f:verbatim>Matrícula</f:verbatim></f:facet>
				<h:outputText value="#{ _discente.matricula }"/>
			</h:column>
			<h:column>
				<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
				<h:outputText value="#{ _discente.pessoa.nome }"/>
			</h:column>
			<h:column>
				<f:facet name="header"><f:verbatim>Curso</f:verbatim></f:facet>
				<h:outputText value="#{_discente.curso.descricao}"/>
			</h:column>
		</h:dataTable>
		
		<a4j:region rendered="#{ not empty matriculaResidenciaMedica.discentes }">
			<table class="formulario" width="100%">
				<tfoot>
					<tr>
						<td colspan="4">
							<div style="float: left;">
								<a href="#" onclick="desmarcarTodos();">Limpar Seleção</a>
							</div>
							<h:commandButton value="<< Voltar" action="#{ matriculaResidenciaMedica.forwardSelecionarComponente }" />
							<h:commandButton value="Matricular Discentes" action="#{ matriculaResidenciaMedica.selecionarDiscentes }" />
						<h:commandButton value="Cancelar" action="#{matriculaResidenciaMedica.cancelar}" id="cancelar" onclick="#{confirm}"/>
						</td>
					</tr>
				</tfoot>
			</table>
		</a4j:region>
	</h:form>
	<br/>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>
