<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h2 class="title"><ufrn:subSistema /> &gt; 
		<c:if test="${!turmaRedeMBean.alterar}">	
			Criar Turma
		</c:if>
		<c:if test="${turmaRedeMBean.alterar}">	
			Alterar Turma
		</c:if>
	</h2>

	<div class="descricaoOperacao" style="width:90%">
		<p>Caro Usuário,</p>
		<p>Nesta tela você deve entrar com os dados gerais da turma.
	</div>

	<h:form id="cadastroTurma">
	
	<br/>
	<table class="formulario" style="width: 90%">
		<caption class="formulario">Dados da Turma </caption>
		<tbody>
		<tr>
			<td colspan="2" class="subFormulario">Dados do Componente Curricular</td>
		</tr>
		<tr>
			<th width="30%"><b>Componente Curricular:</b></th>
			<td>
				<h:outputText value="#{turmaRedeMBean.obj.componente.descricaoResumida}" /> 
			</td>
		</tr>
		<tr>
			<th width="30%"><b>Campus:</b></th>
			<td>
				<h:outputText value="#{turmaRedeMBean.obj.dadosCurso.campus.descricao}" /> 
			</td>
		</tr>
		<tr>
			<td colspan="2" class="subFormulario">Dados Gerais da Turma</td>
		</tr>
		<tr>
			<th class="obrigatorio" >Ano-Período:</th>
			<td>
				<h:inputText value="#{turmaRedeMBean.obj.ano}" maxlength="4" size="4" id="ano" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
					-
				<h:inputText value="#{turmaRedeMBean.obj.periodo}" maxlength="1" size="1" id="periodo" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Início:</th>
			<td>
				<t:inputCalendar value="#{turmaRedeMBean.obj.dataInicio}" readonly="false" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data Início" id="dataInicio">
					<f:convertDateTime pattern="dd/MM/yyyy"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">Fim:<br/>
			</th>
			<td>
				<t:inputCalendar value="#{turmaRedeMBean.obj.dataFim}" readonly="false" renderAsPopup="true" renderPopupButtonAsImage="true" size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))"  maxlength="10" title="Data Final" id="dataFinal">
					<f:convertDateTime pattern="dd/MM/yyyy"/>
				</t:inputCalendar>
			</td>
		</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="<< Selecionar Campus" action="#{turmaRedeMBean.telaSelecaoCampus}" rendered="#{!turmaRedeMBean.alterar}" id="voltarTelaCampus"/>
					<h:commandButton value="<< Selecionar Componente" action="#{turmaRedeMBean.telaSelecaoComponentes}" id="outra" rendered="#{!turmaRedeMBean.alterar}"/>
					<h:commandButton value="<< Selecionar Turma" action="#{turmaRedeMBean.telaBuscarTurmas}" id="turmas" rendered="#{turmaRedeMBean.alterar}"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{ turmaRedeMBean.cancelar }" id="cancelar"/>
					<h:commandButton value="Próximo Passo >>" action="#{ turmaRedeMBean.submeterDadosGerais }" id="proximoPasso"/>
				</td>
			</tr>
		</tfoot>
		</table>
	</h:form>
	
	<br>
	<center><h:graphicImage url="/img/required.gif" style="vertical-align: top;" /><span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>