<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<f:view>
<h2> <ufrn:subSistema /> > Registro de Diplomas Antigo </h2>

<h:form enctype="multipart/form-data" id="form">

<div class="descricaoOperacao">
<p><b>Caro Usuário,</b></p>
<p>Preencha o formulário abaixo com muita atenção, informando os valores atualmentenete registrados no Livro de Registro de Diplomas.</p>
<p>Valores incorretos implicarão na busca por registros errados, bem como na impressão de diplomas com valores incorretos.</p>
</div>

<table class="formulario" >
	<caption>Dados do Registro</caption>
	<tbody>
		<tr>
			<th class="rotulo">Discente:</th>
			<td>
				<h:outputText value="#{registroDiplomaAntigoMBean.obj.discente.matriculaNome}"/>
			</td>
		</tr>
		<tr>
			<th class="rotulo"> Livro: </th>
			<td>
				<h:outputText value="#{registroDiplomaAntigoMBean.livro.titulo} - #{registroDiplomaAntigoMBean.obj.discente.curso}"/>
			</td>
		</tr>
		<tr>
		<th class="required"> Folha: </th>
			<td>
				<a4j:region>
					<h:selectOneMenu id="folhaCombo" value="#{registroDiplomaAntigoMBean.folha.id}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{registroDiplomaAntigoMBean.folhasCombo}"/>
						<a4j:support event="onchange" reRender="registroCombo" />
					</h:selectOneMenu>
					<a4j:status>
		                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>
		            </a4j:status>
				</a4j:region>
			</td>
		</tr>
		<tr>
			<th class="required"> Ordem do Registro na Folha: </th>
			<td>
				<h:selectOneMenu value="#{registroDiplomaAntigoMBean.obj.id}" id="registroCombo">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
					<f:selectItems value="#{registroDiplomaAntigoMBean.registroCombo}"/>
				</h:selectOneMenu>
			</td>
		</tr>
		<tr>
			<th class="required"> Nº do Registro: </th>
			<td><h:inputText value="#{registroDiplomaAntigoMBean.obj.numeroRegistro}" size="6" maxlength="6"  onkeyup="return formatarInteiro(this);" converter="#{ intConverter }" id="numeroRegistro"/> </td>
		</tr>
		<tr>
			<th> Número do Protocolo: </th>
			<td ><h:inputText value="#{registroDiplomaAntigoMBean.obj.processo}" onkeyup="formatarProtocolo(this, event);" size="22" maxlength="20" id="processo2" /><br>
				(ex.: 23077.001234/2003-98) Caso n&atilde;o saiba os digitos verificadores, informe 99
			</td>
		</tr>
		<tr>
			<th class="${ empty registroDiplomaAntigoMBean.obj.discente.dataColacaoGrau ? 'required' : 'rotulo' }">
				<c:choose>
				<c:when test="${registroDiplomaAntigoMBean.obj.discente.graduacao}">Data da Colação:</c:when>
				<c:otherwise>Data da Homologação:</c:otherwise>
				</c:choose>
			</th>
			<td>
				<c:if test="${ not empty registroDiplomaAntigoMBean.obj.discente.dataColacaoGrau }">
					<ufrn:format type="dia_mes_ano" valor="${registroDiplomaAntigoMBean.obj.dataColacao}" />
				</c:if>
				<t:inputCalendar value="#{registroDiplomaAntigoMBean.obj.dataColacao}" rendered="#{ empty registroDiplomaAntigoMBean.obj.discente.dataColacaoGrau }"
					size="10" maxlength="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" id="dataColacao" 
				 	renderAsPopup="true" renderPopupButtonAsImage="true">
				 		<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th class="required"> Data do Registro: </th>
			<td ><t:inputCalendar value="#{registroDiplomaAntigoMBean.obj.dataRegistro}" size="10" maxlength="10"
				 onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" id="dataRegistro" 
				 renderAsPopup="true" renderPopupButtonAsImage="true">
				 	<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th class="required"> Data de Expedição: </th>
			<td ><t:inputCalendar value="#{registroDiplomaAntigoMBean.obj.dataExpedicao}" size="10" maxlength="10"
				 onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" id="dataExpedicao" 
				 renderAsPopup="true" renderPopupButtonAsImage="true">
				 	<f:converter converterId="convertData"/>
				</t:inputCalendar>
			</td>
		</tr>
		<tr>
			<th valign="top">Observação: </th>
			<td>
				<h:inputTextarea value="#{registroDiplomaAntigoMBean.observacao.observacao}" id="observacao" rows="4" cols="60" 
				  onkeyup="if (this.value.length > 600) this.value = this.value.substring(0, 600); $('form:observacao_count').value = 600 - this.value.length;" 
				onkeydown="if (this.value.length > 600) this.value = this.value.substring(0, 600); $('form:observacao_count').value = 600 - this.value.length;">
				</h:inputTextarea>
				<br/>
				Você pode digitar <h:inputText id="observacao_count" size="3" value="#{600 - fn:length(registroDiplomaAntigoMBean.observacao.observacao)}" disabled="true" /> caracteres.
			</td>
		</tr>
		<tr>
			<th valign="top">Diploma Digitalizado: </th>
			<td>
				<t:inputFileUpload value="#{registroDiplomaAntigoMBean.diplomaDigitalizado}" styleClass="file" id="diplomaDigitalizado" />
				<ufrn:help>Caso tenha o diploma digitalizado do discente, você poderá incluí-lo no resgistro.</ufrn:help>
			</td>
		</tr>
	</tbody>
	<tfoot>
		<tr>
			<td colspan="2" align="center">
				<h:commandButton action="#{registroDiplomaAntigoMBean.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelar"/>
				<h:commandButton action="#{registroDiplomaAntigoMBean.submeterDadosGerais}" value="Próximo Passo >>" id="proximo"/>
			</td>
		</tr>
	</tfoot>
</table>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>