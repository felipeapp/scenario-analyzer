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
	<h2><ufrn:subSistema /> &gt; <h:outputText value="#{inscricaoAtividade.texto}" /> Inscrição</h2>
	<h:form id="form1">

		<table class="formulario" width="90%">
			<caption>Dados do Período de Inscrição</caption>
			<tbody>
				<tr>
					<th><b>Código:</b></th>
					<td><h:outputText value="#{inscricaoAtividade.obj.atividade.codigo}" /></td>
				</tr>
				<tr>
					<th><b>Título:</b></th>
					<td><h:outputText value="#{inscricaoAtividade.obj.atividade.titulo}" /></td>
				</tr>
				<tr>
					<th><b>Período de Realização:</b></th>
					<td><h:outputText value="#{inscricaoAtividade.obj.atividade.dataInicio}" /> até <h:outputText value="#{inscricaoAtividade.obj.atividade.dataFim}" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Quantidade de Vagas:</th>
					<td><h:inputText value="#{inscricaoAtividade.obj.quantidadeVagas}" size="6" id="quantidadeVagas"
								maxlength="6" onkeyup="formatarInteiro(this);" />
					</td>
				</tr>
				<tr>
					<th class="obrigatorio">Período de Inscrição:</th>
					<td>
						<t:inputCalendar id="periodoInicio" value="#{inscricaoAtividade.obj.periodoInicio}" 
								renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
								size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
								maxlength="10" popupTodayString="Hoje é">
							<f:converter converterId="convertData" />
						</t:inputCalendar>
						<i>até</i>
						<t:inputCalendar id="periodoFim" value="#{inscricaoAtividade.obj.periodoFim}" 
								renderAsPopup="true" renderPopupButtonAsImage="true" popupDateFormat="dd/MM/yyyy" 
								size="10" onkeypress="return(formatarMascara(this,event,'##/##/####'))" 
								maxlength="10" popupTodayString="Hoje é">
							<f:converter converterId="convertData" />
						</t:inputCalendar>
					</td>
				</tr>
				
				<c:if test="${not empty inscricaoAtividade.possiveisQuestionarios}">				
				<tr>
					<th>Questionário:</th>
					<td>
						<h:selectOneMenu value="#{inscricaoAtividade.obj.questionario.id}" id="questionarioEspecifico" style="width:95%;">
						<f:selectItems value="#{inscricaoAtividade.possiveisQuestionarios}"/>
					</h:selectOneMenu>
					</td>				
				</tr>
				
				</c:if>
				
				<tr>
					<th class="obrigatorio">Observações:</th>
					<td><h:inputTextarea value="#{inscricaoAtividade.obj.observacoes}" rows="4" id="observacoes" style="width:90%;" /></td>
				</tr>
				<tr>
					<td colspan="2" />
				</tr>
				<tr>
					<th class="obrigatorio">Instruções para Inscrição:</th>
					<td><h:inputTextarea value="#{inscricaoAtividade.obj.instrucoesInscricao}" rows="4" id="instrucoes" style="width:90%;"/></td>
				</tr>
				<tr>
					<th class="obrigatorio">Envio de Arquivo Obrigatório:
						<ufrn:help>Torna obrigatório o envio de arquivo no ato da inscrição do participante.</ufrn:help>
					</th>
					<td>
						<h:selectOneRadio value="#{inscricaoAtividade.obj.envioArquivoObrigatorio}" id="envioArquivoObrigatorio">
							<f:selectItem itemValue="true" itemLabel="Sim"/>
							<f:selectItem itemValue="false" itemLabel="Não"/>
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
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>