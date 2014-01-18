<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript">
<!--
	function limitText(limitField, limitCount, limitNum) {
	    if (limitField.value.length > limitNum) {
	        limitField.value = limitField.value.substring(0, limitNum);
	    } else {
	        $(limitCount).value = limitNum - limitField.value.length;
	    }
	}
	
//-->
</script>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>

<f:verbatim>
	<script type="text/javascript">
		function participou(obj, i) {

			var just = document.getElementById("txaJustificativaSid"+ i) ;

			if (obj.checked == false){
				just.style.display = "";
			}else{
				just.style.display = "none";
			}
			
		}
		
	</script>
</f:verbatim>

	<h:outputText value="#{resumoSid.create}" />		
		
	<h:messages showDetail="true" showSummary="true"/>

	<h2><ufrn:subSistema /> > Cadastro de Resumo das Atividades do Projeto no Semin�rio de Inicia��o � Doc�ncia (SID) </h2>
	<div class="descricaoOperacao">
		<center>Discentes que n�o participaram do Resumo poder�o ser desligados do projeto pela PROGRAD.</center>
	</div>
	<h:form id="formCadastroResumoSid">	
	
		<table class="formulario" width="100%">
		<caption class="listagem"> Cadastramento de Resumo (SID) </caption>
	
		<tr>
			<th width="20%">
			 	<b>Projeto:</b>
			</th>	
			<td>
				<h:outputText value="#{resumoSid.obj.projetoEnsino.titulo}" />
			</td>
		</tr>
	
	
		<tr>
			<th class="required">
				<b>Resumo: </b>
			</th>
			<td>
					<h:inputTextarea id="txaResumo"  value="#{resumoSid.obj.resumo}" rows="10" 
					style="width: 98%" readonly="#{resumoSid.readOnly}" onkeydown="limitText(this, countResumo, 1500);" onkeyup="limitText(this, countResumo, 1500);"/>
					<center>
					     Voc� pode digitar <input readonly type="text" id="countResumo" size="3" value="${1500 - fn:length(resumoSid.obj.resumo) < 0 ? 0 : 1500 - fn:length(resumoSid.obj.resumo)}"> caracteres.
					</center> 
			</td>
		</tr>
	
	
		<tr>
			<th class="required">
				<b>Palavras Chave:</b>
			</th>
			<td>
				<h:inputText id="txtPalavrasChave" value="#{resumoSid.obj.palavrasChave}"  readonly="#{resumoSid.readOnly}" 
				 maxlength="200" style="width: 98%"/>
			    
			</td>
		</tr>
		
		
		<tr>
			<th class="required">
				<b>Ano do Semin�rio:</b>
			</th>
			<td>	
				<h:inputText id="txtAnoSeminario" value="#{resumoSid.obj.anoSid}" readonly="#{resumoSid.readOnly}"  
				maxlength="4" size="5"/>
			</td>
		</tr>
		

		<tr>
			<td colspan="2">
	
				<table class="listagem" width="100%">	
				<caption><font color='red'>Desmarque</font> os Discentes que <font color='red'>N�O</font> Enviaram Resumos para o Semin�rio de Inicia��o � Doc�ncia (SID):</caption>
				
					<tr>
						<td>
						
										<t:dataTable value="#{resumoSid.obj.participacoesSid}" var="partSid" rowClasses="linhaPar,linhaImpar" width="100%" id="psid">
											
											<t:column>
														<h:selectBooleanCheckbox id="chkParticipouSid" value="#{partSid.participou}" styleClass="noborder" onclick="javascript:participou(this, #{partSid.discenteMonitoria.discente.matricula});" disabled="#{resumoSid.readOnly}"/>
											</t:column>
											<t:column>
													<h:outputText value="<b>#{partSid.discenteMonitoria.discente.matriculaNome}</b>"  escape="false"/>

													<h:outputText value="<div id='txaJustificativaSid#{partSid.discenteMonitoria.discente.matricula}'style='display:#{partSid.participou ? \"none\":\"\" }' >"  escape="false" />
														<label ><i>Justificar motivo do n�o envio do Resumo SID :</i></label><h:graphicImage url="/img/required.gif"/>	
														<h:inputTextarea value="#{partSid.justificativa}" rows="3" style="width: 98%" readonly="#{resumoSid.readOnly}"/>
														
													<f:verbatim></div></f:verbatim>
											</t:column>
											
										</t:dataTable>
											
						</td>
					</tr>
	
				</table>		
	 		   </td>
		 	</tr>

		<br/>
		<br/>
		
			<tfoot>
				<tr>
					<td colspan="2">
		
						<h:commandButton id="btCadastrar" value="#{resumoSid.confirmButton}" action="#{resumoSid.cadastrar}"/>
						<h:commandButton id="btCancelar" value="Cancelar" action="#{resumoSid.cancelar}"/>
						
					</td>
				</tr>
			</tfoot>
	</h:form>
	
</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>