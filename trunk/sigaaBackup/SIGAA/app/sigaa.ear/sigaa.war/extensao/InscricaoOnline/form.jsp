<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script src="/shared/javascript/tiny_mce/tiny_mce.js" type="text/javascript"></script>
<script type="text/javascript">
  tinyMCE.init({
        mode : "textareas",
        theme : "simple",
        width : "650",
        height : "200"
 });
</script>
<f:view>
	<h:messages/>
	<h2><ufrn:subSistema /> &gt; <h:outputText value="#{inscricaoAtividade.texto}" /> Inscri��o</h2>
	<h:form id="form1">

		<table class="formulario" width="90%">
			<caption>Dados do Per�odo de Inscri��o</caption>
			<tbody>
				<tr>
					<th><b>C�digo:</b></th>
					<td><h:outputText value="#{inscricaoAtividade.obj.atividade.codigo}" /></td>
				</tr>
				<tr>
					<th><b>T�tulo:</b></th>
					<td><h:outputText value="#{inscricaoAtividade.obj.atividade.titulo}" /></td>
				</tr>
				<tr>
					<th><b>Per�odo de Realiza��o:</b></th>
					<td><h:outputText value="#{inscricaoAtividade.obj.atividade.dataInicio}" /> at� <h:outputText value="#{inscricaoAtividade.obj.atividade.dataFim}" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Quantidade de Vagas:</th>
					<td><h:inputText value="#{inscricaoAtividade.obj.quantidadeVagas}" size="6" id="quantidadeVagas"
								maxlength="6" onkeyup="formatarInteiro(this);" />
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Per�odo de Inscri��o:</th>
					<td>
						<t:inputCalendar id="periodoInicio" value="#{inscricaoAtividade.obj.periodoInicio}" 
								renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
								size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
								maxlength="10" popupTodayString="Hoje �">
							<f:converter converterId="convertData" />
						</t:inputCalendar>
						<i>at�</i>
						<t:inputCalendar id="periodoFim" value="#{inscricaoAtividade.obj.periodoFim}" 
								renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
								size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
								maxlength="10" popupTodayString="Hoje �">
							<f:converter converterId="convertData" />
						</t:inputCalendar>
					</td>
				</tr>
				
				<c:if test="${not empty inscricaoAtividade.possiveisQuestionarios}">				
				<tr>
					<th>Question�rio:</th>
					<td>
						<h:selectOneMenu value="#{inscricaoAtividade.obj.questionario.id}" id="questionarioEspecifico" style="width:95%;">
						<f:selectItems value="#{inscricaoAtividade.possiveisQuestionarios}"/>
					</h:selectOneMenu>
					</td>				
				</tr>
				
				</c:if>
				
				<tr>
					<th class="obrigatorio">Observa��es:</th>
					<td><h:inputTextarea value="#{inscricaoAtividade.obj.observacoes}" rows="4" id="observacoes" style="width:90%;" /></td>
				</tr>
				<tr>
					<td colspan="2" />
				</tr>
				<tr>
					<th class="obrigatorio">Instru��es para Inscri��o:</th>
					<td><h:inputTextarea value="#{inscricaoAtividade.obj.instrucoesInscricao}" rows="4" id="instrucoes" style="width:90%;"/></td>
				</tr>
				<tr>
					<th class="obrigatorio">Envio de Arquivo Obrigat�rio:
						<ufrn:help>Torna obrigat�rio o envio de arquivo no ato da inscri��o do participante.</ufrn:help>
					</th>
					<td>
						<h:selectOneRadio value="#{inscricaoAtividade.obj.envioArquivoObrigatorio}" id="envioArquivoObrigatorio">
							<f:selectItem itemValue="true" itemLabel="Sim"/>
							<f:selectItem itemValue="false" itemLabel="N�o"/>
						</h:selectOneRadio>
					</td>
				</tr>
				
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{inscricaoAtividade.confirmButton}" action="#{inscricaoAtividade.cadastrar}" id="cadastrar" />
						<h:commandButton value="Cancelar" action="#{inscricaoAtividade.voltaTelaGerenciaInscricoes}" id="voltar"   onclick="#{confirm}"  immediate="true"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<div class="obrigatorio"> Campos de preenchimento obrigat�rio. </div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>