<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>



<f:view>
	<h2>  <ufrn:subSistema /> > Modalidade de Participante</h2>
	<br>
	
	<div class="descricaoOperacao"> 
    	<c:if test="${modalidadeParticipanteMBean.obj.id == 0}">
    		<p> Entre com as informa��es da nova modalidade de participantes dos cursos e eventos de extens�o. 
    		A partir do cadatro os coordenadores j� poder�o informar novos valores de taxas de inscri��o para os participantes dessa modalidade.</p>
    	</c:if>
    	<c:if test="${modalidadeParticipanteMBean.obj.id > 0}">
    		<p> Edite as informa��es da modalidade selecionada.</p>
    	</c:if>
    </div>	
	
	<h:form id="formCadatroModalidadesParticipantes">
	
		<a4j:keepAlive beanName="modalidadeParticipanteMBean" />


		<table class="formulario" style="width: 60%;">
			<caption>Modalidade de Participante</caption>
			
			<tr>
				<th class="obrigatorio">Nome:</th>
				<td colspan="2"><h:inputText value="#{modalidadeParticipanteMBean.obj.nome}" maxlength="30" size="50" onkeyup="CAPS(this);"/></td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan="3" align="center">
						<h:commandButton id="acao" value="#{modalidadeParticipanteMBean.confirmButton}"  action="#{modalidadeParticipanteMBean.cadastrar}" />
												
						<h:commandButton id="cancelar" value="Cancelar" onclick="#{confirm}"  action="#{modalidadeParticipanteMBean.telaListar}"  immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>
		
		<%@include file="/WEB-INF/jsp/include/_campos_obrigatorios.jsp"%>
		
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>