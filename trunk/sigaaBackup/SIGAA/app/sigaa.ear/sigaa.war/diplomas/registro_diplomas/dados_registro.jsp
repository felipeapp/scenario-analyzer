<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style>
.direita {
	text-align: right;
}

.esquerda {
	text-align: left;
}
</style>

<f:view>
<h2> <ufrn:subSistema /> > Registro Coletivo de ${registroDiplomaColetivo.obj.livroRegistroDiploma.tipoRegistroDescricao } </h2>

<h:form>

<div class="descricaoOperacao">Para registrar os  ${registroDiplomaColetivo.obj.livroRegistroDiploma.tipoRegistroDescricao } desta turma, é
		necessário informar o número do processo e as datas.<br />
		<b>Verifique</b> com cuidado se todas informações estão corretas antes
		de concluir a operação.</div>

<table class="formulario" >
	<caption>Dados do Registro Coletivo</caption>
	<tbody>
		<tr>
			<th width="20%" class="rotulo"> Curso: </th>
			<td > ${registroDiplomaColetivo.obj.curso.descricao} </td>
		</tr>
		<tr>
			<th class="rotulo"> Livro de Registro: </th>
			<td> 
				${registroDiplomaColetivo.obj.livroRegistroDiploma.titulo}
				(${fn:length(registroDiplomaColetivo.obj.livroRegistroDiploma.folhas)} folhas de ${registroDiplomaColetivo.obj.livroRegistroDiploma.numeroSugeridoFolhas} folhas sugeridas)
			</td>
		</tr>
		<tr>
			<th class="obrigatorio">
			<c:choose>
				<c:when test="${acesso.graduacao}">Data da Colação:</c:when>
				<c:otherwise>Data de Conclusão:</c:otherwise> 
			</c:choose>
			</th>
			<td>
				<t:inputCalendar value="#{registroDiplomaColetivo.obj.dataColacao}" size="10" maxlength="10"
					 onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" id="dataColacao" 
					 renderAsPopup="true" renderPopupButtonAsImage="true" >
				 	<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th class="required"> Número do Processo: </th>
			<td ><h:inputText value="#{registroDiplomaColetivo.obj.processo}" onkeyup="formatarProtocolo(this, event);" size="20" maxlength="20" id="processo"/><br>
				(ex.: 23077.001234/2003-98) Caso não saiba os dígitos verificadores, informe 99
			</td>
		</tr>
		<tr>
			<th class="required"> Data do Registro: </th>
			<td ><t:inputCalendar value="#{registroDiplomaColetivo.obj.dataRegistro}" size="10" maxlength="10"
				 onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" id="dataRegistro" 
				 renderAsPopup="true" renderPopupButtonAsImage="true" >
				 	<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th class="required"> Data de Expedição: </th>
			<td ><t:inputCalendar value="#{registroDiplomaColetivo.obj.dataExpedicao}" size="10" maxlength="10"
				 onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" id="dataExpedicao" 
				 renderAsPopup="true" renderPopupButtonAsImage="true" >
				 	<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr><td colspan="2">&nbsp;</td></tr>
		<tr>
			<td class="subFormulario" colspan="2">
				Discentes: (${fn:length(registroDiplomaColetivo.registrar) } Graduando(s) Encontrado(s))
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<t:dataTable value="#{registroDiplomaColetivo.registrar}" var="item" rowClasses="linhaImpar,linhaPar" style="width:100%; ">
					<t:column width="30%">
						<f:facet name="header"><f:verbatim>Discente</f:verbatim></f:facet>
						<t:outputText value="#{item.matriculaNome}"/>
					</t:column>
				</t:dataTable>
			</td>
		</tr>
		<c:if test="${not empty registroDiplomaColetivo.discentesRegistrados}">
			<tr><td colspan="2">&nbsp;</td></tr>
			<tr><td colspan="2">&nbsp;</td></tr>
			<tr>
				<td class="subFormulario" colspan="2">
					Discentes com ${registroDiplomaColetivo.obj.livroRegistroDiploma.tipoRegistroDescricao } já Registrados: (${fn:length(registroDiplomaColetivo.discentesRegistrados) })
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<t:dataTable value="#{registroDiplomaColetivo.discentesRegistrados}" var="registro" rowClasses="linhaImpar,linhaPar" style="width:100%; "
					columnClasses="esquerda, esquerda, direita, direita, direita" >
						<t:column width="30%">
							<f:facet name="header"><f:verbatim>Discente</f:verbatim></f:facet>
							<t:outputText value="#{registro.discente.matriculaNome}"/>
						</t:column>
						<t:column width="15%">
							<f:facet name="header"><f:verbatim>Livro</f:verbatim></f:facet>
							<t:outputText value="#{registro.livroRegistroDiploma.titulo}"/>
						</t:column>
						<t:column width="15%">
							<f:facet name="header"><f:verbatim>Folha</f:verbatim></f:facet>
							<t:outputText value="#{registro.folha.numeroFolha}"/>
						</t:column>
						<t:column width="15%">
							<f:facet name="header"><f:verbatim>Nº do<br>Registro</f:verbatim></f:facet>
							<t:outputText value="#{registro.numeroRegistro}"/>
						</t:column>
					</t:dataTable>
				</td>
			</tr>
		</c:if>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2">
			<h:commandButton value="Registrar #{registroDiplomaColetivo.obj.livroRegistroDiploma.tipoRegistroDescricao }" action="#{registroDiplomaColetivo.cadastrar}" id="registrar"/>
			<h:commandButton value="<< Escolher Outro Curso" action="#{registroDiplomaColetivo.formBuscaCurso}" id="outroCurso"/>
			<h:commandButton value="Cancelar" action="#{registroDiplomaColetivo.cancelar}" onclick="#{confirm}" id="cancelar"/>
			</td>
		</tr>
	</tfoot>
</table>
<br>
<center>
	<html:img page="/img/required.gif" style="vertical-align: top;" /> 
	<span class="fontePequena">Campos de preenchimento obrigatório. </span>
</center>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>