<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/ext-1.1/adapter/jquery/jquery.js"></script>
<script type="text/javascript" charset="ISO-8859">
					var J = jQuery.noConflict();
</script>
<style>
	.colLeft{text-align: left; }
	.colCenter{text-align: center; }
	.colRight{text-align: right; }
</style>

<f:view>
<h2> <ufrn:subSistema /> > 
<h:outputText value="Estorno de Conclusão Coletiva" rendered="#{ !colacaoColetiva.apenasAlterarDataColacao }" />
<h:outputText value="Alterar Data de Colação Coletiva" rendered="#{ colacaoColetiva.apenasAlterarDataColacao }" /> 
</h2>

<h:form id="form">
<a4j:keepAlive beanName="colacaoColetiva"></a4j:keepAlive>
<table class="formulario" width="100%" >
	<caption>Dados da Turma de Conclusão</caption>
	<tr>
		<th width="25%" class="rotulo"> Curso: </th>
		<td> ${colacaoColetiva.curso.descricao} </td>
	</tr>
	<tr>
		<th class="rotulo"> Ano - Período: </th>
		<td>${ colacaoColetiva.ano}.${ colacaoColetiva.periodo}</td>
	</tr>
	<c:if test="${ !colacaoColetiva.apenasAlterarDataColacao }">
		<tr>
			<th class="required" style="font-weight: normal;">Status que os alunos devem ficar:&nbsp&nbsp</th>
			<td>											
				<h:selectOneMenu value="#{colacaoColetiva.statusRetorno}" id="status" style="width: 40%;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{discente.statusAtivosCombo}" />
					</h:selectOneMenu>
			</td>
		</tr>	
	</c:if>
	<c:if test="${ colacaoColetiva.apenasAlterarDataColacao }">
		<tr>
			<th class="obrigatorio" style="font-weight: normal;">Nova Data da Colação:</th>
			<td>
			<t:inputCalendar value="#{colacaoColetiva.dataColacao}" size="10" maxlength="10"
				onkeypress="return(formatarMascara(this,event,'##/##/####'))"
				title="Nova Data da Colação"
				popupDateFormat="dd/MM/yyyy" id="colacao" renderAsPopup="true" renderPopupButtonAsImage="true" >
				<f:converter converterId="convertData"/>
			</t:inputCalendar>
			</td>
		</tr>	
	</c:if>
</table>
<br/>
<table class="formulario" width="100%">
	<tbody>	
		<tr>
			<td class="subFormulario">Selecione os Alunos  
				<h:outputText value="que serão Estornados" rendered="#{ !colacaoColetiva.apenasAlterarDataColacao }" />
				<h:outputText value="que terão Data de Colação Alterada" rendered="#{ colacaoColetiva.apenasAlterarDataColacao }" />
			</td>
		</tr>
		<tr>
			<td>	
				<table width="90%" class="listagem">
					<thead>				
						<tr>
							<th align="left"><h:selectBooleanCheckbox id="selectAll" styleClass="selectAll"/></th>
							<th width="500px">Discente</th>
							<th width="120px" style="text-align: center;">Data Conclusão</th>
							<th width="350px" align="left">Matriz Curricular</th>
						</tr>
					</thead>
					
					<tbody>
						<c:forEach items="#{colacaoColetiva.concluintes}" var="concluintes" varStatus="loop">
							<tr class="${loop.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
								<td align="left"><h:selectBooleanCheckbox id="check" styleClass="check" value="#{concluintes.selecionado}" rendered="#{concluintes != null}"/></td>
								<td width="500px"><h:outputText value="#{concluintes.matriculaNome}"/></td>
								<td width="120px" align="center"><h:outputText value="#{concluintes.dataColacaoGrau}"/></td>
								<td align="left"><h:outputText value="#{concluintes.matrizCurricular.descricaoMin}"/></td>
							</tr>						
						</c:forEach>
					</tbody>
				</table>
			</td>
		</tr>
	</tbody>
	<tfoot>
	<tr>
		<td>
			<h:commandButton value="Estornar Alunos Selecionados" action="#{colacaoColetiva.confirmarEstorno}" id="estornarAlunosSelecionados" rendered="#{ !colacaoColetiva.apenasAlterarDataColacao }"/>
			<h:commandButton value="Alterar Data de Colação" action="#{colacaoColetiva.confirmarEstorno}" id="alterarDataColacaoSelecionados" rendered="#{ colacaoColetiva.apenasAlterarDataColacao }"/>
			<h:commandButton value="<< Escolher Outro Curso" action="#{colacaoColetiva.telaBuscaEstornoColetivo}" id="escolherOutroCurso"/>
			<h:commandButton value="Cancelar" action="#{colacaoColetiva.cancelar}" onclick="#{confirm}" id="cancelarOperacao"/>
		</td>
	</tr>	
	</tfoot>
</table>

	<c:set var="exibirApenasSenha" value="true" scope="request"/>
	<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>

<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>	
</h:form>
</f:view>

<SCRIPT LANGUAGE="JavaScript">
J(document).ready(function() {
	J(".selectAll").attr("checked","checked");
	
	J(".selectAll").click(function(){
		var check = J(this);
		J(".check").each(function(){
			J(this).attr("checked",check.attr("checked")?"checked":"");
		});
	});	
});
</script>



<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>