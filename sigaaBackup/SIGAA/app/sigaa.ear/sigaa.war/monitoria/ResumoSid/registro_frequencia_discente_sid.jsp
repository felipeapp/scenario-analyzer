<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>


<f:view>
<h:form>
	<h2><ufrn:subSistema /> > Registro de Participação dos Discentes na Apresentação do Resumo no SID  </h2>

		<table width="100%" class="subFormulario" id="aviso">
			<tr>
			<td width="40"><html:img page="/img/help.png"/> </td>
			<td valign="top" style="text-align: justify">
			<font color="red" size="2">Atenção:</font> Esta operação efetua o registro de freqüência dos discentes que apresentaram trabalhos no SID.<br>
			Somente os alunos que Apresentaram e Participaram da elaboração dos Resumo tem direito ao certificado de participação do Seminário de Iniciação à Docência.<br/>
			<br/>
			<br/>			
			</td>
			</tr>
		</table>



		<table class="formulario" width="100%">
		<caption class="listagem"> Dados do Resumo das Atividades do Projeto (SID) </caption>
	
		<tr>
			<th width="15%">
			 	<b>Projeto:</b>
			</th>	
			<td>
				<h:outputText value="#{resumoSid.buscaProjetoEnsino.titulo}" />
			</td>
		</tr>
	

		<tr>
			<th>
			 	<b>Coordenador(a):</b>
			</th>	
			<td>
				<h:outputText value="#{resumoSid.buscaProjetoEnsino.coordenacao.siapeNome}" />
			</td>
		</tr>

		<tr>
			<td colspan="2"><br/></td>
		</tr>

	
		<tr>
			<td colspan="2">
	
				<table class="listagem" width="100%">	
				<caption>Discentes do Projeto de Ensino:</caption>
				
					<tr>
						<td>
						
										<t:dataTable value="#{resumoSid.participacoes}" var="partSid" rowClasses="linhaPar,linhaImpar" width="100%" id="psid">


											<t:column>
														<f:facet name="header"><f:verbatim>ANO SID</f:verbatim></f:facet>
														<h:outputText id="anoSid" value="#{partSid.resumoSid.anoSid}" styleClass="noborder"/>
											</t:column>

											<t:column>
														<f:facet name="header"><f:verbatim>&nbsp;PARTICIPOU&nbsp;</f:verbatim></f:facet>
														<h:outputText id="participouSid" value="#{partSid.participou ? 'SIM':'NÃO'}" styleClass="noborder"/>
											</t:column>
											
											<t:column>
														<f:facet name="header"><f:verbatim>&nbsp;APRESENTOU&nbsp;</f:verbatim></f:facet>
														<h:selectBooleanCheckbox id="chkApresentouSid" value="#{partSid.apresentou}" styleClass="noborder" />
											</t:column>
											
											<t:column width="70%">
													<f:facet name="header"><f:verbatim>DISCENTE</f:verbatim></f:facet>
													<h:outputText value="<b>#{partSid.discenteMonitoria.discente.matriculaNome}</b>"  escape="false"/>

													<h:outputText value="<div id='txaJustificativaSid#{partSid.discenteMonitoria.discente.matricula}' style='display: #{partSid.participou ? 'none':'' }'>"  escape="false"/>
														<f:verbatim><label><i>Justificar motivo da não participação no Resumo SID :</i></label><br/></f:verbatim>													
														<h:outputText value="#{partSid.justificativa}"/>
														
													<f:verbatim></div></f:verbatim>
											</t:column>
											
										</t:dataTable>
											
						</td>
					</tr>
	
				</table>		
	 		   </td>
		 	</tr>


			<tfoot>
				<tr>
					<td colspan="3">
					<h:commandButton value="<< Voltar" action="#{ resumoSid.consultarFrequenciaSID }"/>
					<h:commandButton value="Confirmar Registro" action="#{ resumoSid.registrarFrequencia }"/>
					<h:commandButton id="btCancelar" value="Cancelar" action="#{resumoSid.cancelar}"/>					
			    	</td>
			    </tr>
			</tfoot>

		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>