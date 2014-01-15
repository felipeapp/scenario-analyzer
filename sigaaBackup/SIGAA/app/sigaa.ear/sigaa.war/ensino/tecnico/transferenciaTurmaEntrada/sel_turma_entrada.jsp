<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<script type="text/javascript" src="/shared/javascript/matricula.js"></script>

<script type="text/javascript">
	function marcarTodos() {
		var elements = document.getElementsByTagName('input');
		for (i = 0; i < elements.length; i++) {
			if (elements[i].type == 'checkbox') {
				elements[i].checked = true;
			}
		}
	}
</script>

<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Transferência Entre Turmas de Entrada </h2>
<br/>

<f:view>

<h:form id="form">
<table class="formulario" width="100%">
<caption>Informe os Dados para a Matrícula</caption>

	<tbody>
	<tr>
		<th class="obrigatorio"> Curso: </th>
		<td>
			 <h:selectOneMenu value="#{ transferenciaTurmaEntradaMBean.curso.id }" onchange="submit()" 
			 	valueChangeListener="#{ transferenciaTurmaEntradaMBean.carregarTurmasEntrada }">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				<f:selectItems value="#{ matriculaTecnico.cursos }"/>
			 </h:selectOneMenu> 
		</td>
	</tr>
	
		<tr>
			<th class="obrigatorio">Turma de Entrada: </th>
			<td> 
				<h:selectOneMenu value="#{ transferenciaTurmaEntradaMBean.turmaEntradaOrigem.id }" onchange="submit()" 
				valueChangeListener="#{ transferenciaTurmaEntradaMBean.carregarDiscentes }" >
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
					<f:selectItems value="#{ transferenciaTurmaEntradaMBean.comboTurmasEntrada }"/>
				</h:selectOneMenu> 
			</td>
		</tr>
	</tbody>

	<a4j:region rendered="#{ not empty transferenciaTurmaEntradaMBean.discentes }">  
		 <tr>
			<td colspan="2">
				<h:dataTable value="#{ transferenciaTurmaEntradaMBean.discentes }" var="discente" width="100%">
	
					<f:facet name="caption"><f:verbatim>Discentes Encontrados</f:verbatim></f:facet>
		
					<h:column>
						<f:facet name="header">
							<f:verbatim>
							<a href="#" onclick="marcarTodos();">Todos</a>
							</f:verbatim>
						</f:facet>
						<h:selectBooleanCheckbox value="#{ discente.matricular }"/>
					</h:column>
				
					<h:column>
						<f:facet name="header"><f:verbatim>Matrícula</f:verbatim></f:facet>
						<h:outputText value="#{ discente.matricula }"/>
					</h:column>
				
					<h:column>
						<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
						<h:outputText value="#{ discente.pessoa.nome }"/>
					</h:column>
				
				</h:dataTable>
		    </td>
	     </tr>
	</a4j:region>

	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Cancelar" action="#{ transferenciaTurmaEntradaMBean.cancelar }" onclick="#{confirm}" immediate="true" />
				<h:commandButton value="Avançar >>" action="#{ transferenciaTurmaEntradaMBean.submeterTurmaEntrada }" />
			</td>
	</tfoot>

  </table>
</h:form>

<br />
	<center>	
		<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
<br/>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>