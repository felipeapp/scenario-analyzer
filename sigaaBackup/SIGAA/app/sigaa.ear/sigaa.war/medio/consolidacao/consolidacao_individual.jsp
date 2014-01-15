<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
<!--
table.subFormulario th{
	font-weight: bold;
}
-->
</style>
<f:view>
	<a4j:keepAlive beanName="consolidacaoIndividualMedio"/>

	<h:outputText value="#{consolidacaoIndividualMedio.create}" />
	<h2 class="title"><ufrn:subSistema /> > Consolidação Individual</h2>

<h:form id="formulario">
<div class="descricaoOperacao">
	Caso a média final já venha preenchido com algum valor diferente de zero é por que o professor lançou as notas
	das unidades mas não consolidou a turma.
</div>

	<table class="formulario" width="80%">
			<caption class="listagem">Matrícula</caption>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Dados Consolidados (${consolidacaoIndividualMedio.matricula.situacaoMatricula.descricao})</caption>
					<tr>
						<th>Aluno: </th>
						<td colspan="3">${consolidacaoIndividualMedio.discente.matricula} - ${consolidacaoIndividualMedio.discente.pessoa.nome}</td>
					</tr>
					<tr>
						<th>Disciplina: </th>
						<td colspan="3">${consolidacaoIndividualMedio.matricula.componenteNome}</td>
					</tr>
					<tr>
						<th>Ano: </th>
						<td colspan="3">${consolidacaoIndividualMedio.matricula.anoPeriodo}</td>
					</tr>

				</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Dados da Consolidação Individual</caption>
					<tr>
						<th width="25%" class="obrigatorio">Média Final: </th>
						<td width="15%">
							<h:inputText value="#{consolidacaoIndividualMedio.matricula.mediaFinal}" id="media" size="5" maxlength="5" onkeydown="return(formataValor(this, event, 1))">
							<f:converter converterId="convertNota"/>
							</h:inputText>
						</td>
						<th width="10%" class="obrigatorio">Faltas: </th>
						<td>
							<h:inputText value="#{consolidacaoIndividualMedio.matricula.numeroFaltas}" size="5" maxlength="5" id="Faltas" onkeyup="return(formatarInteiro(this))"/>
						</td>
					</tr>
				</table>
				</td>
			</tr>
	</table>
	<br>
	
	<c:set var="exibirApenasSenha" value="true" scope="request"/>
	<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
	
	<center>
		 <h:commandButton value="Consolidar Individual" action="#{consolidacaoIndividualMedio.chamaModelo}" id="consolidar"/>
		 <h:commandButton value="<< Voltar" action="#{consolidacaoIndividualMedio.selecionaDiscente}" id="voltar" />
		 <h:commandButton value="Cancelar" onclick="#{confirm}" action="#{consolidacaoIndividualMedio.cancelar}" id="cancelar" />
		<br>
	</center>

	<br/>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>	

	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<c:if test="${ not consolidacaoIndividualMedio.conceito }">
<script type="text/javascript">
	if ( getEl('formulario:media').dom.value == '0,0' )
		getEl('formulario:media').dom.value = '';
	getEl('formulario:media').focus();
</script>
</c:if>