<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Plano de Trabalho Selecionado</h2>

<h:form id="form">

<table class="formulario">
<caption> Finalizar Discente de Extens�o </caption>
<tbody>
	<tr>
		<th width="25%" class="rotulo"> A��o de Extens�o: </th>
		<td colspan="2"> ${planoTrabalhoExtensao.obj.atividade.anoTitulo} </td>
	</tr>
	<tr>
		<th class="rotulo"> Coordenador(a): </th>
		<td colspan="2">${planoTrabalhoExtensao.obj.orientador.pessoa.nome }</td>
	</tr>

	<tr>
		<th class="rotulo"> Per�odo do Plano: </th>
		<td colspan="2"><fmt:formatDate value="${planoTrabalhoExtensao.obj.dataInicio}"/> at� <fmt:formatDate value="${planoTrabalhoExtensao.obj.dataFim}"/></td>
	</tr>
	
	<c:if test="${not empty planoTrabalhoExtensao.obj.discenteExtensao.discenteExtensaoAnterior}">
		<tr>
			<th class="rotulo"> Discente Anterior: </th>
			<td colspan="2">
				<c:choose>
					<c:when test="${not empty planoTrabalhoExtensao.obj.discenteExtensao.discenteExtensaoAnterior.discente}">
						${planoTrabalhoExtensao.obj.discenteExtensao.discenteExtensaoAnterior.discente.pessoa.nome}
					</c:when>
					<c:otherwise>
						<i> N�o definido </i>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
		
		<tr>
			<th class="rotulo"> Motivo da Substitui��o:</th>
			<td colspan="2"> ${ planoTrabalhoExtensao.obj.discenteExtensao.discenteExtensaoAnterior.motivoSubstituicao } </td>
		</tr>
	</c:if>
	
	
	<tr><td colspan="4" class="subFormulario" style="text-align: center"> Finaliza��o </td></tr>
	
	
	<tr>
		<th class="rotulo"> Discente Atual: </th>
		<td colspan="2">
			<c:choose>
				<c:when test="${not empty planoTrabalhoExtensao.obj.discenteExtensao.discente}">
					${planoTrabalhoExtensao.obj.discenteExtensao.discente.pessoa.nome}
				</c:when>
				<c:otherwise>
					<i> N�o definido </i>
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
	
	
	<tr>
		<th class="rotulo"> Local de Trabalho: </th>
		<td colspan="2"><ufrn:format type="texto" name="planoTrabalhoExtensao" property="obj.localTrabalho"/></td>
	</tr>
	<tr>
		<th class="rotulo"> Tipo de V�nculo: </th>
		<td colspan="2"><h:outputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.tipoVinculo.descricao}" /></td>
	</tr>

	<tr>
		<th class="rotulo"> <b>Data da In�cio:</b> </th>
		<td colspan="2">
			<fmt:formatDate value="${planoTrabalhoExtensao.obj.discenteExtensao.dataInicio}" pattern="dd/MM/yyyy" />
		</td>
	</tr>

	
	<tr>
		<th class="required rotulo"> Data da Finaliza��o: </th>
		<td colspan="2">
			<t:inputCalendar size="10" value="#{planoTrabalhoExtensao.obj.discenteExtensao.dataFim}" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="formataData(this,event)"  maxlength="10" id="fimDiscente"/>
		</td>
	</tr>
	
	<tr>
		<th class="required rotulo">Motivo da Finaliza��o:</th>
		<td>
			<select onchange="javascript: $('form:motivo').value = this.value;">
				<option value=""> -- SELECIONE O MOTIVO -- </option>
				<option value="SA�DE"> SA�DE </option>
				<option value="V�NCULO EMPREGAT�CIO"> V�NCULO EMPREGAT�CIO </option>
				<option value="MUDAN�A DE PROJETO"> MUDAN�A DE PROJETO </option>
				<option value="CONCLUS�O DA GRADUA��O"> CONCLUS�O DA GRADUA��O </option>
				<option value="A PEDIDO DO ALUNO"> A PEDIDO DO ALUNO </option>
				<option value="FALECIMENTO"> FALECIMENTO </option>
				<option value="OUTROS"> OUTROS </option>
			</select>
		</td>
		<td>
			<h:inputText value="#{planoTrabalhoExtensao.obj.discenteExtensao.motivoSubstituicao}" size="45" id="motivo"/>
		</td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="3">
			<h:commandButton value="Finalizar"	action="#{planoTrabalhoExtensao.finalizarDiscente}" id="btfinalizar"/>
			<h:commandButton value="Cancelar" action="#{planoTrabalhoExtensao.cancelar}" id="btcancelar"/>
		</td>
	</tr>
</tfoot>
</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>