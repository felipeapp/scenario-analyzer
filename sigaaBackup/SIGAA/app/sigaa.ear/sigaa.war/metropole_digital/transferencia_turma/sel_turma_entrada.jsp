<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<script type="text/javascript" src="/shared/javascript/matricula.js"></script>


<script type="text/javascript">

	var checkflag = "false";

	function selectAllCheckBox() {
	    var div = document.getElementById('form');
	    var e = div.getElementsByTagName("input");
	   
	    var i;
	
	    if (checkflag == "false") {
	            for ( i = 0; i < e.length ; i++) {
	                    if (e[i].type == "checkbox"){ e[i].checked = true; }
	            }
	            checkflag = "true";
	    } else {
	            for ( i = 0; i < e.length ; i++) {
	                    if (e[i].type == "checkbox"){ e[i].checked = false; }
	            }
	            checkflag = "false";
	    }
	}

</script>

<h2> <ufrn:subSistema></ufrn:subSistema> &gt; Transfer�ncia Entre Turmas </h2>
<br/>

<f:view>

<h:form id="form">
<table class="formulario" width="100%">

<div class="descricaoOperacao">
	<p>Selecione a turma que o aluno est� vinculado e clique no bot�o avan�ar.</p>
	
	<p>Em seguida selecione o(s) aluno(s) que deseja mudar de turma e clique novamente no bot�o avan�ar.</p>


</div>
<caption>Transfer�ncia Entre Turmas</caption>

	<tbody>
	<tr>
		<th class="obrigatorio"> Curso: </th>
		<td>
			 <h:selectOneMenu value="#{ transferenciaTurmaIMD.curso.id }" onchange="submit()" 
			 	valueChangeListener="#{ transferenciaTurmaIMD.carregarTurmasEntrada }">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				<f:selectItems value="#{transferenciaTurmaIMD.cursosCombo }"/>
			 </h:selectOneMenu> 
		</td>
	</tr>
	
		<tr>
			<th class="obrigatorio">Turma: </th>
			<td> 
				<h:selectOneMenu value="#{ transferenciaTurmaIMD.turmaEntradaOrigem.id }" onchange="submit()" 
				valueChangeListener="#{ transferenciaTurmaIMD.carregarDiscentes }" >
					<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
					<f:selectItems value="#{ transferenciaTurmaIMD.comboTurmasEntrada }"/>
				</h:selectOneMenu> 
			</td>
		</tr>
	</tbody>

	<a4j:region rendered="#{ not empty transferenciaTurmaIMD.discentes }">  
		 <tr>
			<td colspan="2">
				<rich:dataTable value="#{ transferenciaTurmaIMD.discentes }" var="discente" width="100%" rowKeyVar="c">
	
					<f:facet name="caption"><f:verbatim>Discentes Encontrados</f:verbatim></f:facet>
		
					<rich:column styleClass="#{c % 2 == 0 ? 'linhaPar': 'linhaImpar' }"  width="20">
						<f:facet name="header">
							<f:verbatim>
							<a href="#" onclick="selectAllCheckBox();">Todos</a>
							</f:verbatim>
						</f:facet>
						<h:selectBooleanCheckbox value="#{ discente.matricular }"/>
					</rich:column>
				
					<rich:column styleClass="#{c % 2 == 0 ? 'linhaPar': 'linhaImpar'}">
						<f:facet name="header"><f:verbatim>Matr�cula</f:verbatim></f:facet>
						<h:outputText value="#{ discente.matricula }"/>
					</rich:column>
				
					<rich:column styleClass="#{c % 2 == 0 ? 'linhaPar': 'linhaImpar'}">
						<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
						<h:outputText value="#{ discente.pessoa.nome }"/>
					</rich:column>
				
				</rich:dataTable>
		    </td>
	     </tr>
	</a4j:region>

	<tfoot>
		<tr>
			<td colspan="2">
				<h:commandButton value="Cancelar" action="#{ transferenciaTurmaIMD.cancelar }" onclick="#{confirm}" immediate="true" />
				<h:commandButton value="Avan�ar >>" action="#{ transferenciaTurmaIMD.submeterTurmaEntrada }" />
			</td>
	</tfoot>

  </table>
</h:form>

<br />
	<center>	
		<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
	</center>
<br/>

</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>