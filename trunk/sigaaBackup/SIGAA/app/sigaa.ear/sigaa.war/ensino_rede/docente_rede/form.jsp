<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2> <ufrn:subSistema /> > 
	<h:outputText value="Cadastro de Docente" rendered="#{!docenteRedeMBean.acaoAlterar}"/>
	<h:outputText value="Alterar Docente" rendered="#{docenteRedeMBean.acaoAlterar}"/>
	</h2>
	
	
	<h:form id="formDiscenteStricto">
	<table class="formulario" style="width: 70%">
		<caption>Dados do Docente</caption>
		<tbody>
			<tr>
				<th style="width: 20%;" class="rotulo"> Nome: </th>
				<td>${docenteRedeMBean.obj.pessoa.nome}</td>
			</tr>
			<tr>
				<th style="width: 20%;" class="rotulo"> Instituição: </th>
				<td>${docenteRedeMBean.obj.dadosCurso.campus.instituicao.sigla} - ${docenteRedeMBean.obj.dadosCurso.campus.sigla}</td>
			</tr>		
			<tr>
				<th class="obrigatorio">Tipo: </th>
				<td>
					<h:selectOneMenu value="#{docenteRedeMBean.obj.tipo.id}" id="status" style="width: 40%;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{tipoDocenteMBean.allCombo}" id="tipoDocenteCombo"/>
					</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="obrigatorio">Situação: </th>
				<td>
					<h:selectOneMenu value="#{docenteRedeMBean.obj.situacao.id}" id="situacao" style="width: 40%;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"  />
						<f:selectItems value="#{situacaoDocenteRedeMBean.allCombo}" id="situacaoDocenteCombo"/>
					</h:selectOneMenu>
				</td>
			</tr>			
		</tbody>
		<tfoot>
			<tr>	
				<td colspan="2">
					<h:commandButton value="#{docenteRedeMBean.confirmButton }" id="Cadastrar" action="#{ docenteRedeMBean.cadastrar }"/>
					<h:commandButton value="<< Dados Pessoais" id="DadosPessoais" immediate="true" rendered="#{docenteRedeMBean.obj.id==0}" action="#{docenteRedeMBean.telaDadosPessoais}"/>
					<h:commandButton value="Cancelar" id="Cancelar" immediate="true" action="#{ docenteRedeMBean.cancelar }" onclick="#{confirm}" />
				</td>
			</tr>
		</tfoot>
	</table>

	</h:form>
</f:view>

	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	</center>


<script>
function exibirCurriculo(sel){
	var val = sel.options[sel.selectedIndex].value;
		if (val != "1") {
			$('trCurriculo').style.display='none';
			$('spanOrientador').style.display='none';
			$('spanInicio').style.display='none';
		} else {
			$('trCurriculo').style.display='table-row';
			$('spanOrientador').style.display='inline';
			$('spanInicio').style.display='inline';
		}
}
/*
function escolherInstituicao(rede) {
	if (rede.checked) {
		$('instituicao').show();
	} else {
		$('instituicao').hide();
	}
	$('formDiscenteStricto:checkAlunoOutraInstituicaoe').focus();
}
escolherInstituicao($('formDiscenteStricto:checkAlunoOutraInstituicao'));
*/

</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>