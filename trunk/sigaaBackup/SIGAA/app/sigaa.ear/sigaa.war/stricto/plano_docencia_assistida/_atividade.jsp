<a4j:region>
<a4j:outputPanel id="formAtividades">
<table style="width: 100%" name="listaatividades" class="subFormulario">
	<tr>
		<td class="obrigatorio" style="text-align: right !important;">Atividade: &nbsp; </td>
		<td>
			<h:selectOneMenu value="#{planoDocenciaAssistidaMBean.atividade.formaAtuacao.id}" onchange="informarAtividade(this);" style="width: 70%" id="atividadeCombo">
				<f:selectItem itemValue="-1" itemLabel="-- Selecione uma Atividade --"/>
				<f:selectItem itemValue="0" itemLabel="OUTRA"/>
				<f:selectItems value="#{planoDocenciaAssistidaMBean.formasAtuacaoCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>
	<tr id="outraAtividade">
		<th class="obrigatorio" style="text-align: right;">Outra Atividade:</th>
		<td> 
			<h:inputText id="descricaoOutraAtividade" size="60" maxlength="100" value="#{planoDocenciaAssistidaMBean.atividade.outraAtividade}" />
			<ufrn:help>Caso não exista a atividade na lista acima, informe aqui a atividade desejada.</ufrn:help>	
		</td>
	</tr>
	<tr>
		<th class="obrigatorio" style="text-align: right;">Data de Início: </th>
		<td>
			<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy" renderAsPopup="true" renderPopupButtonAsImage="true" size="10"
					maxlength="10" onkeypress="return formataData(this,event)" value="#{planoDocenciaAssistidaMBean.atividade.dataInicio}" id="datainicio" />
			Data de Fim: <span class="obrigatorio">&nbsp;</span>
			<t:inputCalendar popupTodayString="Hoje é" popupDateFormat="dd/MM/yyyy"
					renderAsPopup="true" renderPopupButtonAsImage="true" size="10" maxlength="10" onkeypress="return formataData(this,event)"
					value="#{planoDocenciaAssistidaMBean.atividade.dataFim}" id="datafim" />														 
		</td>
	</tr>				
	<tr>
		<th class="obrigatorio" style="text-align: right;">Frequência da atividade:</th>
		<td>
			<h:selectOneMenu value="#{planoDocenciaAssistidaMBean.atividade.frequenciaAtividade.id}" id="tipoFrequencia">
				<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
				<f:selectItems value="#{planoDocenciaAssistidaMBean.allFrequenciaAtividadeCombo}"/>
			</h:selectOneMenu>
		</td>
	</tr>	
	<tr>
		<th class="obrigatorio" style="text-align: right;">Carga Horária: </th>
		<td>
			<h:inputText value="#{planoDocenciaAssistidaMBean.atividade.ch}" id="cargaHoraria" size="5" maxlength="5" 
			onkeyup="return formatarInteiro(this);" style="text-align: right;"/>  (h)
		</td>
	</tr>		
	<c:if test="${planoDocenciaAssistidaMBean.relatorioSemestral}">	
		<tr>
			<th class="obrigatorio" style="text-align: right;">Realização (%): </th>
			<td>
				<h:selectOneMenu value="#{planoDocenciaAssistidaMBean.atividade.percentualRealizado}" id="percentualAtividadeCombo">
					<f:selectItems value="#{planoDocenciaAssistidaMBean.percentuaisCombo}"/>
				</h:selectOneMenu>
			</td>				
		</tr>
	</c:if>			
	<tr>
		<td colspan="2">
			<table width="80%" align="center">				
				<tr>
					<td colspan="2">Metodologias da Atividade:<span class="obrigatorio">&nbsp;</span> </td>
				</tr>
				<tr>	
					<td colspan="2">
						<h:inputTextarea cols="100" rows="3" id="comoOrganizar" value="#{ planoDocenciaAssistidaMBean.atividade.comoOrganizar }"/> 
					</td>
				</tr>				
			</table>
		</td>
	</tr>				
	<tr>
		<td colspan="2">
			<table width="80%" align="center">				
				<tr>
					<td colspan="2">Como Avaliar a Atividade e Metodologias Empregadas:<span class="obrigatorio">&nbsp;</span> </td>
				</tr>
				<tr>	
					<td colspan="2">
						<h:inputTextarea cols="100" rows="3" id="procedimentos" value="#{ planoDocenciaAssistidaMBean.atividade.procedimentos }"/><ufrn:help>Análises ou Sínteses qualitativas e/ou quantitativas.</ufrn:help>
					</td>
				</tr>				
			</table>
		</td>
	</tr>
	<c:if test="${planoDocenciaAssistidaMBean.relatorioSemestral}">	
		<tr>
			<td colspan="2">
				<table width="80%" align="center">				
					<tr>
						<td class="subFormulario" colspan="2">Resultados Obtidos:<span class="obrigatorio">&nbsp;</span> </td>
					</tr>
					<tr>	
						<td colspan="2">
							<h:inputTextarea cols="100" rows="3" id="resultadosObtidosAtividade" value="#{ planoDocenciaAssistidaMBean.atividade.resultadosObtidos }"/>
						</td>
					</tr>				
				</table>
			</td>
		</tr>	
		<tr>
			<td colspan="2">
				<table width="80%" align="center">				
					<tr>
						<td class="subFormulario" colspan="2">Dificuldades Encontradas:<span class="obrigatorio">&nbsp;</span> </td>
					</tr>
					<tr>	
						<td colspan="2">
							<h:inputTextarea cols="100" rows="3" id="dificuldadesAtividade" value="#{ planoDocenciaAssistidaMBean.atividade.dificuldades }"/>
						</td>
					</tr>				
				</table>
			</td>
		</tr>			
	</c:if>
	<tfoot>
		<tr>
			<td colspan="2">
				<a4j:commandButton value="Adicionar Atividade" actionListener="#{planoDocenciaAssistidaMBean.addAtividade}" oncomplete="carregarAtividade()" 
				 reRender="listaAtividades, atividadeCombo, formAtividades" id="btAddAtividade"/>
				<rich:spacer width="10"/>
	            <a4j:status>
	                <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
	            </a4j:status>	
			</td>
		</tr>
	</tfoot>					
</table>
</a4j:outputPanel>

<table style="width: 100%">
	<tr>
		<td>
			<a4j:outputPanel id="listaAtividades">
			<c:if test="${not empty planoDocenciaAssistidaMBean.obj.atividadeDocenciaAssistida}">						
				<div class="infoAltRem">
					<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Atividade							
					<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: 
					Remover Atividade
				</div>
			</c:if>
			
			<style>
				.data {
					text-align: center !important;
				}
				.atividade {
				   text-align: left !important;
				}
				.ch {
				   text-align: right !important;
				}							
			</style>
									
			<t:dataTable value="#{planoDocenciaAssistidaMBean.obj.atividadeDocenciaAssistida}" var="_atividade" style="width: 100%;" 
				styleClass="listagem" id="listagemAtividades" rowClasses="linhaPar, linhaImpar" rowIndexVar="row" rendered="#{not empty planoDocenciaAssistidaMBean.obj.atividadeDocenciaAssistida}">
				<t:column headerstyleClass="atividade" styleClass="atividade">
					<f:facet name="header"><f:verbatim>Atividade</f:verbatim></f:facet>
					<h:outputText value="#{_atividade.outraAtividade}"/>										
					<h:outputText value="#{_atividade.formaAtuacao.descricao}"/>																			
				</t:column>
				<t:column headerstyleClass="data" styleClass="data">
						<f:facet name="header"><f:verbatim>Data de Início</f:verbatim></f:facet>
						<h:outputText value="#{_atividade.dataInicio}"/>
				</t:column>
				<t:column headerstyleClass="data" styleClass="data">
						<f:facet name="header"><f:verbatim>Data de Fim</f:verbatim></f:facet>
						<h:outputText value="#{_atividade.dataFim}"/>						
				</t:column>
				<t:column headerstyleClass="atividade" styleClass="atividade">
						<f:facet name="header"><f:verbatim>Frequência</f:verbatim></f:facet>
						<h:outputText value="#{_atividade.frequenciaAtividade.descricao}"/>									
				</t:column>
				<t:column headerstyleClass="ch" styleClass="ch">
						<f:facet name="header"><f:verbatim>Carga Horária (h)</f:verbatim></f:facet>
						<h:outputText value="#{_atividade.ch}"/>									
				</t:column>
				<t:column>
					<a4j:commandLink actionListener="#{planoDocenciaAssistidaMBean.alterarAtividade}" 
					oncomplete="carregarAtividade()" 
					reRender="formAtividades, listagemAtividades" title="Alterar Atividade" id="linkAlterarAtv"
					 rendered="#{!planoDocenciaAssistidaMBean.relatorioSemestral || !_atividade.prevista}">
							<h:graphicImage value="/img/alterar.gif"/>
							<f:param name="indice" value="#{row}"/>
							<f:param name="id" value="#{_atividade.id}"/>
					</a4j:commandLink>								
				</t:column>											
				<t:column>
					<a4j:commandLink actionListener="#{planoDocenciaAssistidaMBean.removerAtividade}" 
					reRender="listaAtividades" title="Remover Atividade" id="linkRemoveAtv" onclick="if (!confirm('Confirma a remoção desta informação?')) return false;"
					rendered="#{(!planoDocenciaAssistidaMBean.relatorioSemestral || !_atividade.prevista) && !_atividade.alteracao}">
							<h:graphicImage value="/img/delete.gif"/>
							<f:param name="indice" value="#{row}"/>
					</a4j:commandLink>								
				</t:column>	
			</t:dataTable>
		</a4j:outputPanel>								
		</td>
	</tr>
</table>	
</a4j:region>
<script>
function informarAtividade(obj){
	if (obj.value == '0') {
		$('outraAtividade').show();
	} else {
		$('outraAtividade').hide();
	}	
}

function carregarAtividade(){
	informarAtividade(document.getElementById("form:atividadeCombo"));	
}

carregarAtividade();

</script>