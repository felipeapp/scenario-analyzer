<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

<%@include file="/portais/docente/menu_docente.jsp"%>
<h2><ufrn:subSistema /> > Plano de Trabalho Projeto </h2>

<h:form id="form">

<table class="formulario">
<caption> Finalizar Discente Projeto </caption>
<tbody>
	<tr>
		<th width="25%" class="rotulo"> Projeto: </th>
		<td colspan="2"> <h:outputText value="#{planoTrabalhoProjeto.obj.projeto.ano} - #{planoTrabalhoProjeto.obj.projeto.titulo}" /> </td>
	</tr>
	<tr>
		<th class="rotulo"> Coordenador(a): </th>
		<td colspan="2"><h:outputText value="#{planoTrabalhoProjeto.obj.projeto.coordenador.pessoa.nome}" /></td>
	</tr>

	<tr>
		<th class="rotulo"> Período do Plano: </th>
		<td colspan="2"><h:outputText value="#{planoTrabalhoProjeto.obj.dataInicio}" >
				              <f:convertDateTime type="date" dateStyle="medium"/>
				    </h:outputText>  a 
				    <h:outputText value="#{planoTrabalhoProjeto.obj.dataFim}" >
				              <f:convertDateTime type="date" dateStyle="medium"/>
				    </h:outputText></td>
	</tr>
	
	<c:if test="${not empty planoTrabalhoProjeto.obj.discenteProjeto.discenteProjetoAnterior}">
		<tr>
			<th class="rotulo"> Discente Anterior: </th>
			<td colspan="2">
				<h:outputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.discenteProjetoAnterior.discente.pessoa.nome}" rendered="#{planoTrabalhoProjeto.obj.discenteProjeto.discenteProjetoAnterior.discente != null} " />
				<h:outputText value="Não definido" rendered="#{planoTrabalhoProjeto.obj.discenteProjeto.discenteProjetoAnterior.discente == null} " />
			</td>
		</tr>
		
		<tr>
			<th class="rotulo"> Motivo da Substituição:</th>
			<td colspan="2"> 
				<h:outputText value="#{ planoTrabalhoProjeto.obj.discenteProjeto.discenteProjetoAnterior.motivoSubstituicao } " />
			</td>
		</tr>
	</c:if>
	
	
	<tr><td colspan="4" class="subFormulario" style="text-align: center"> Finalização </td></tr>
	
	
	<tr>
		<th class="rotulo"> Discente Atual: </th>
		<td colspan="2">
			<h:outputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.discente.nome}" rendered="#{planoTrabalhoProjeto.obj.discenteProjeto.discente != null }"  />
			<h:outputText value="Não definido" rendered="#{planoTrabalhoProjeto.obj.discenteProjeto.discente == null }"  />
		</td>
	</tr>
	
	
	<tr>
		<th class="rotulo"> Local de Trabalho: </th>
		<td colspan="2"><ufrn:format type="texto" name="planoTrabalhoProjeto" property="obj.localTrabalho"/></td>
	</tr>
	<tr>
		<th class="rotulo"> Tipo de Vínculo: </th>
		<td colspan="2"><h:outputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.tipoVinculo.descricao}" /></td>
	</tr>

	<tr>
		<th class="rotulo"> <b>Data da Início:</b> </th>
		<td colspan="2">
			<h:outputText value="#{planoTrabalhoProjeto.obj.discenteProjeto.dataInicio}">
				  <f:convertDateTime type="date" dateStyle="medium"/>
			</h:outputText>
		</td>
	</tr>

	
	<tr>
		<th class="required rotulo"> Data da Finalização: </th>
		<td colspan="2">
			<t:inputCalendar size="10" value="#{planoTrabalhoProjeto.obj.discenteProjeto.dataFim}" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="formataData(this,event)"  maxlength="10" id="fimDiscente"/>
		</td>
	</tr>
	
	<tr>
		<th class="required rotulo">Motivo da Finalização:</th>
		
		<td>
			<h:selectOneMenu value="#{planoTrabalhoProjeto.obj.discenteProjeto.motivoSubstituicao}" id="motivo">
				<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM TIPO --" />
				<f:selectItem itemValue="SAÚDE" itemLabel="SAÚDE" />
				<f:selectItem itemValue="VÍNCULO EMPREGATÍCIO" itemLabel="VÍNCULO EMPREGATÍCIO" />
				<f:selectItem itemValue="MUDANÇA DE PROJETO" itemLabel="MUDANÇA DE PROJETO" />
				<f:selectItem itemValue="CONCLUSÃO DA GRADUAÇÃO" itemLabel="CONCLUSÃO DA GRADUAÇÃO" />
				<f:selectItem itemValue="A PEDIDO DO ALUNO" itemLabel="A PEDIDO DO ALUNO" />
				<f:selectItem itemValue="FALECIMENTO" itemLabel="FALECIMENTO" />
				<f:selectItem itemValue="OUTROS" itemLabel="OUTROS" />
			</h:selectOneMenu>
		</td>
	</tr>
</tbody>
<tfoot>
	<tr>
		<td colspan="3">
			<h:commandButton value="Finalizar"		action="#{planoTrabalhoProjeto.finalizarDiscente}" id="btfinalizar"/>
			<h:commandButton value="<< Voltar" action="#{planoTrabalhoProjeto.planosCoordenador}" />
			<h:commandButton value="Cancelar" action="#{planoTrabalhoProjeto.cancelar}" id="btcancelar"/>
		</td>
	</tr>
</tfoot>
</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>