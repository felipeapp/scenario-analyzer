<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript">
//Verifica se a nota digitada é maior que 10. Se for, invalida.
function verificaNotaMaiorDez(element) {
	var valor = parseFloat(element.value.replace(',','.'));
	if (valor > 10.0) {
		alert('Nota inv\341lida. A nota deve estar entre 0 e 10.');
		element.value = '';
	}
}
</script>

<f:view>
<h:form id="avaliacao">
<h2><ufrn:subSistema /> &gt; <h:commandLink value="Cadastro de Notas" action="#{cadastrarAvaliacao.cancelar}" /> &gt; Cadastrar Avaliação</h2>

<h:outputText value="#{ cadastrarAvaliacao.configuracaoUnidade }"/>


<c:if test="${ param.unidade > 0 && param.unidade <= consolidarTurma.config.numeroUnidadesMaximoImplementadas && param.unidade <= cadastrarAvaliacao.numeroUnidades }" >
<div class="descricaoOperacao">
Digite as informações da avaliação. A abreviação será utilizada
para identificar a avaliação na tabela de notas. 

<c:set var="ponderada" value="avaliacoesMediaPonderada${ param.unidade }"/>
<c:set var="soma" value="avaliacoesSoma${ param.unidade }"/>
<c:set var="aritmetica" value="avaliacoesMediaAritmetica${ param.unidade }"/>

<c:if test="${ consolidarTurma.config[ ponderada ] }">
O peso será utilizado para calcular a nota da unidade através de <strong>média ponderada</strong>. 
</c:if>
<c:if test="${ consolidarTurma.config[ soma ] }">
A nota da unidade será calculada a partir da <strong>soma das suas avaliações</strong>. Será necessário para isso definir a nota
máxima da avaliação.  
</c:if>
<c:if test="${ consolidarTurma.config[ aritmetica ] }">
A nota da unidade será calculada através de <strong>média arimética</strong>. 
</c:if>
</div>

<table class="formulario" width="80%">
<caption>Dados da Avaliação</caption>
<tbody>
<tr>
	<th>Turma:</th>
	<td>
		<h:outputText id="turma" value="#{ cadastrarAvaliacao.turma.descricaoSemDocente }"/>
	</td>
</tr>
<tr>
	<th nowrap="nowrap">Unidade: <span class="required">&nbsp;</span></th>
	<td> 
		<h:selectOneMenu id="unidade" value="#{ cadastrarAvaliacao.unidade }" disabled="true">
			<f:selectItems value="#{ cadastrarAvaliacao.unidades }"/>
		</h:selectOneMenu> 
	</td>
</tr>
<c:if test="${ consolidarTurma.config[ ponderada ] }">
<tr>
	<th nowrap="nowrap">Peso: <span class="required">&nbsp;</span></th>
	<td><h:inputText id="peso" value="#{ cadastrarAvaliacao.obj.peso }" size="2" maxlength="2" onkeyup="return formatarInteiro(this);"/></td>
</tr>
</c:if>
<c:if test="${ consolidarTurma.config[ soma ] }">
<tr>
	<th nowrap="nowrap">Nota Máxima: <span class="required">&nbsp;</span></th>
	<td><h:inputText id="notaMaxima" value="#{ cadastrarAvaliacao.obj.notaMaxima }" size="4" maxlength="4" onkeydown="return(formataValor(this, event, 1))"  onblur="verificaNotaMaiorDez(this)">
		<f:converter converterId="convertNota"/>
	</h:inputText>
	</td>
</tr>
</c:if>
<tr>
	<th nowrap="nowrap">Descrição: <span class="required">&nbsp;</span></th>
	<td><h:inputText id="descricao" value="#{ cadastrarAvaliacao.obj.denominacao }" size="60" maxlength="60"/></td>
</tr>
<tr>
	<th nowrap="nowrap">Abreviação: <span class="required">&nbsp;</span></th>
	<td><h:inputText id="abreviacao" value="#{ cadastrarAvaliacao.obj.abreviacao }" size="4" maxlength="4"/></td>
</tr>
</tbody>

<tfoot>
<tr>
	<td colspan="2">
	<input type="hidden" value="${ param['unidade'] }" name="unidade"/>
	<h:commandButton id="caastrar" value="Cadastrar" action="#{ cadastrarAvaliacao.cadastrar }" onclick="return(mensagemNotas());"/>
	<h:commandButton id="cancelar" value="Cancelar" immediate="true" action="#{ cadastrarAvaliacao.cancelar }" onclick="return(confirm('Deseja realmente cancelar a operação?'));"/>
	</td>
</tr>
</tfoot>

</table>

<br><center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
</c:if>
<c:if test="${ param.unidade < 0 || param.unidade > consolidarTurma.config.numeroUnidadesMaximoImplementadas || param.unidade > cadastrarAvaliacao.numeroUnidades}">	
	<div style="line-height:200px;text-align:center;font-size:1.3em;font-weight:bold;color: #FF0000;">Unidade Inexistente.</div>
</c:if>

</h:form>

<script>
	
	function mensagemNotas(){
		var possuiNotas = '<h:outputText value="#{ cadastrarAvaliacao.unidadePossuiNota }" />' == "true"; 
		
		if (possuiNotas)	
			return(confirm("J\u00e1 foram cadastradas notas para esta unidade. Ao desmembrar uma unidade em avali\u00e7\u00f5es " +
					" as notas da unidade ser\u00e3o perdidas." +
	 				" Deseja continuar com a opera\u00e7\u00e3o?"));

	}
</script>

<br>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
