<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>


<style>
<!--
table.subFormulario th{
	font-weight: bold;
}
-->
</style>
<f:view>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:outputText value="#{consolidacaoIndividual.create}" />
	<h2 class="title"><ufrn:subSistema /> > Consolida��o Individual</h2>

<h:form id="formulario">
<div class="descricaoOperacao">
	Caso a m�dia final j� venha preenchido com algum valor diferente de zero � por que o professor lan�ou as notas
	das unidades mas n�o consolidou a turma.
</div>

	<h:messages showDetail="true"></h:messages>
	<table class="formulario" width="80%">
			<caption class="listagem">Matr�cula</caption>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Dados Consolidados (${consolidacaoIndividual.matricula.situacaoMatricula.descricao})</caption>
					<tr>
						<th>Aluno: </th>
						<td colspan="3">${consolidacaoIndividual.discente.matricula} - ${consolidacaoIndividual.discente.pessoa.nome}</td>
					</tr>
					<tr>
						<th>Disciplina: </th>
						<td colspan="3">${consolidacaoIndividual.matricula.componente}</td>
					</tr>
					<tr>
						<th>Ano.Per�odo: </th>
						<td colspan="3">${consolidacaoIndividual.matricula.anoPeriodo}</td>
					</tr>

				</table>
				</td>
			</tr>
			<tr>
				<td colspan="2">
				<table class="subFormulario" width="100%">
					<caption>Dados da Consolida��o Individual</caption>
					<tr>
							<th width="25%" class="obrigatorio">M�dia Final: </th>
							<td width="15%">
								
								<c:choose>
									<c:when test="${ consolidacaoIndividual.conceito }">
										<c:if test="${ !consolidacaoIndividual.matricula.consolidada }">
										<select name="conceito_${ consolidacaoIndividual.matricula.id }">
											<option value="-1">-</option>
											<c:forEach var="conceito" items="${ consolidacaoIndividual.conceitos }">
												<option value="${ conceito.valor }" ${ (consolidacaoIndividual.matricula.conceito == conceito.valor) ? 'selected="selected"' : '' }>${ conceito.conceito }</option>
											</c:forEach>
										</select>
										</c:if>
										<c:if test="${ consolidacaoIndividual.matricula.consolidada }">
										${ consolidacaoIndividual.matricula.conceito.conceito }
										</c:if>
									</c:when>
									<c:otherwise>
										<h:inputText value="#{consolidacaoIndividual.matricula.mediaFinal}" id="media" size="5" maxlength="5" onkeydown="return(formataValor(this, event, 1))">
										<f:converter converterId="convertNota"/>
										</h:inputText>
									</c:otherwise>
								</c:choose>
							
							</td>
						<th width="10%" class="obrigatorio">Faltas: </th>
						<td>
						<h:inputText value="#{consolidacaoIndividual.matricula.numeroFaltas}" size="5" maxlength="5" id="Faltas" onkeyup="return(formatarInteiro(this))"/>
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
		 <h:commandButton value="Consolidar Individual" action="#{consolidacaoIndividual.chamaModelo}" />
		 <h:commandButton value="<< Voltar" action="#{consolidacaoIndividual.selecionaDiscente}" />
		 <h:commandButton value="Cancelar" onclick="#{confirm}" action="#{consolidacaoIndividual.cancelar}" />
		<br>
	</center>

	<br/>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
	class="fontePequena"> Campos de preenchimento obrigat�rio. </span> <br>

	</h:form>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>

<c:if test="${ not consolidacaoIndividual.conceito }">
<script type="text/javascript">
if ( getEl('formulario:media').dom.value == '0,0' )
	getEl('formulario:media').dom.value = '';
getEl('formulario:media').focus();
</script>
</c:if>