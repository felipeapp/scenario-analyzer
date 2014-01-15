<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<a4j:keepAlive beanName="registroParticipacaoAP"></a4j:keepAlive>
	<h2 class="title"><ufrn:subSistema /> > Cadastro de Participação em Atividades</h2>

	<div class="descricaoOperacao">
		<p>Caro usuário,</p>
		<p>o formulário abaixo permite o cadastro das antigas participações em atividades de atualização pedagógica, ou seja, as atividades onde a inscrição não tenha sido realizada pelo sistema.</p>
	</div>

	<h:form id="formRegistroParticipacaoAP">

		<table class="formulario" width="90%">
			<caption class="formulario">Dados do Registro</caption>

			<tr>
				<th class="${!registroParticipacaoAP.readOnly?'required':''}">Título:</th>
				<td>
					<h:inputText id="titulo" size="80" maxlength="255"
						 rendered="#{!registroParticipacaoAP.readOnly}" 
						value="#{registroParticipacaoAP.obj.titulo}"></h:inputText>
					<h:outputText value="#{registroParticipacaoAP.obj.titulo}" rendered="#{registroParticipacaoAP.readOnly}"></h:outputText>	
				</td>
			</tr>
			
			<tr>
				<th class="${!registroParticipacaoAP.readOnly?'required':''}">Período:</th>
				<td>
					<t:inputCalendar id="dataInicio" title="Data Inicial" rendered="#{!registroParticipacaoAP.readOnly}" 
						value="#{registroParticipacaoAP.obj.dataInicio}" renderAsPopup="true"
						renderPopupButtonAsImage="true" size="10" popupDateFormat="dd/MM/yyyy"
						onkeypress="return formataData(this,event)" maxlength="10" />
					<h:outputText value="#{registroParticipacaoAP.obj.dataInicio}" rendered="#{registroParticipacaoAP.readOnly}"></h:outputText>	
						 a  
					<t:inputCalendar rendered="#{!registroParticipacaoAP.readOnly}" 
						id="dataFim" value="#{registroParticipacaoAP.obj.dataFim}"  title="Data Final"
						renderAsPopup="true" renderPopupButtonAsImage="true" size="10" popupDateFormat="dd/MM/yyyy"
						onkeypress="return formataData(this,event)" maxlength="10" />
					<h:outputText value="#{registroParticipacaoAP.obj.dataFim}" rendered="#{registroParticipacaoAP.readOnly}"></h:outputText>
				</td>
			</tr>
			
			<tr>
				<th >Carga Horária:</th>
				<td>
					<h:inputText id="cargaHoraria" size="3" maxlength="3"
						onkeyup="return formatarInteiro(this);"
						 rendered="#{!registroParticipacaoAP.readOnly}" 
						value="#{registroParticipacaoAP.obj.cargaHoraria}"></h:inputText>
					<h:outputText value="#{registroParticipacaoAP.obj.cargaHoraria}" rendered="#{registroParticipacaoAP.readOnly}"></h:outputText>	
				</td>
			</tr>
			
			
			<tr>
				<th class="${!registroParticipacaoAP.readOnly?'required':''}" width="20%">Descrição:</th>
				<td>
					<h:inputTextarea id="descricaoAtividade" rows="10" cols="80" rendered="#{!registroParticipacaoAP.readOnly}"
						value="#{registroParticipacaoAP.obj.descricaoAtividade}"></h:inputTextarea>
					<h:outputText value="#{registroParticipacaoAP.obj.descricaoAtividade}" rendered="#{registroParticipacaoAP.readOnly}"></h:outputText>	
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{registroParticipacaoAP.confirmButton}" rendered="#{registroParticipacaoAP.readOnly}" 
							id="btnremover" action="#{registroParticipacaoAP.remover}"/>
						<h:commandButton value="#{registroParticipacaoAP.confirmButton}" rendered="#{!registroParticipacaoAP.readOnly}" 
							action="#{registroParticipacaoAP.cadastrar}" id="btncadastrar"/> 
						<h:commandButton value="<< Voltar" action="#{registroParticipacaoAP.listar}" rendered="#{registroParticipacaoAP.obj.id>0}" 
							  immediate="true" id="btnvoltar"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{registroParticipacaoAP.cancelar}" 
							  immediate="true" id="btncancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
		
	</h:form>
	
	<br>
	
	<c:if test="${!registroParticipacaoAP.readOnly}">
	<center>
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
	</center>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
