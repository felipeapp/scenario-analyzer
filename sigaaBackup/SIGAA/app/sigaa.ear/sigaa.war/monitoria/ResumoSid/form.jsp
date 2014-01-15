<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

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

	<h2><ufrn:subSistema /> > Cadastro de Resumo das Atividades do Projeto no Seminário de Iniciação à Docência (SID) </h2>

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
					style="width: 98%" readonly="#{resumoSid.readOnly}" /> 
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
				<b>Ano do Seminário:</b>
			</th>
			<td>	
				<h:inputText id="txtAnoSeminario" value="#{resumoSid.obj.anoSid}" readonly="#{resumoSid.readOnly}"  
				maxlength="4" size="5"/>
			</td>
		</tr>
		

		<tr>
			<td colspan="2">
	
				<table class="listagem" width="100%">	
				<caption><font color='red'>Desmarque</font> os Discentes que <font color='red'>NÃO</font> Enviaram Resumos para o Seminário de Iniciação à Docência (SID):</caption>
				
					<tr>
						<td>
						
										<t:dataTable value="#{resumoSid.obj.participacoesSid}" var="partSid" rowClasses="linhaPar,linhaImpar" width="100%" id="psid">
											
											<t:column>
														<h:selectBooleanCheckbox id="chkParticipouSid" value="#{partSid.participou}" styleClass="noborder" onclick="javascript:participou(this, #{partSid.discenteMonitoria.discente.matricula});" disabled="#{resumoSid.readOnly}"/>
											</t:column>
											<t:column>
													<h:outputText value="<b>#{partSid.discenteMonitoria.discente.matriculaNome}</b>"  escape="false"/>

													<h:outputText value="<div id='txaJustificativaSid#{partSid.discenteMonitoria.discente.matricula}' style='display: #{partSid.participou ? 'none':'' }'>"  escape="false"/>
														<f:verbatim><label><i>Justificar motivo do não envio do Resumo SID :</i></label><ufrn:help img="/img/ajuda.gif">Discentes que não participaram do Resumo poderão ser desligados do projeto pela PROGRAD.</ufrn:help><br/></f:verbatim>													
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