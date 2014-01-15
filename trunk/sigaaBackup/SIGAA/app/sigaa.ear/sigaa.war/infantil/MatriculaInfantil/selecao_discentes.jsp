<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h:form id="form">
	
			<h2> <ufrn:subSistema /> &gt; Matrícula &gt; Seleção dos Alunos </h2>
			
			<div class="descricaoOperacao">
				<p>
					Neste passo, selecione os alunos que deseja matricular na turma selecionada no passo anterior.
				</p>
			</div>
			
			<table class="visualizacao" >
				<tr>
					<th width="20%"> Turma: </th>
					<td colspan="3"> ${matriculaInfantilMBean.turma.descricaoTurmaInfantil} </td>
				</tr>
				<tr>
					<th> Matriculados: </th>
					<td colspan="3"> ${matriculaInfantilMBean.turma.qtdMatriculados} aluno(s) </td>
				</tr>
				<tr>
					<th> Capacidade da Turma: </th>
					<td> ${matriculaInfantilMBean.turma.capacidadeAluno} aluno(s) </td>
				</tr>
			</table>
			<br />
		
			<table class="formulario" width="100%">
			<caption>Lista de Alunos</caption>
			<tr>
			<td>
			<rich:dataTable id="dtTableAlunos" value="#{matriculaInfantilMBean.discentes}" var="d" width="100%">
				<f:facet name="header">
		            <rich:columnGroup>
		                <rich:column width="3%">
		                	<f:facet name="verbatim"><input type="checkbox" onclick="selecionarTudo(this);" /></f:facet>
		                </rich:column>
		                <rich:column style="text-align: center;">
		                	<h:outputText value="Matrícula" />
		                </rich:column>
		                <rich:column>
		                	<h:outputText value="Nome" />
		                </rich:column>
		          	</rich:columnGroup>
		        </f:facet>
		        <rich:column>
		        	<h:selectBooleanCheckbox id="chkBoxSelecionado" value="#{d.selecionado}"/>
		        </rich:column>
		        <rich:column style="text-align: center;">
		        	<h:outputText value="#{d.matricula}" />
		        </rich:column>
		        <rich:column>
		        	<h:outputText value="#{d.pessoa.nome}" />
		        </rich:column>
		    </rich:dataTable>
		    </td>
		    </tr>
		    <tfoot>
			    <tr>
			    	<td>
			    		<h:commandButton value="Confirmar Matrículas" action="#{matriculaInfantilMBean.confirmar}"/>
			    		<h:commandButton value="Cancelar" action="#{matriculaInfantilMBean.cancelar}" onclick="#{confirm}" immediate="true"/>
			    		<h:commandButton value="<< Voltar" action="#{matriculaInfantilMBean.telaSelecaoTurma}"/>
			    	</td>
			    </tr>
		    </tfoot>
		    </table>
	</h:form>

</f:view>

<script type="text/javascript">
	function selecionarTudo(chk){
	   for (i=0; i<document.form.elements.length; i++)
	      if(document.form.elements[i].type == "checkbox")
	         document.form.elements[i].checked= chk.checked;
	}
</script>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>