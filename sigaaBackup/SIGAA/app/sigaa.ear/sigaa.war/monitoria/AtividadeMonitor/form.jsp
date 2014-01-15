<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
    <ufrn:checkNotRole papel="<%= SigaaPapeis.GESTOR_MONITORIA %>">
	   <%@include file="/portais/discente/menu_discente.jsp"%>
	</ufrn:checkNotRole>

	<a4j:keepAlive beanName="consultarMonitor" />
	<h2><ufrn:subSistema /> > Relat�rio de Atividades do Monitor</h2>

	<h:messages showDetail="true" />
		
		<h:form id="form" prependId="false">
		
			<h:messages/>
		
			<h:inputHidden value="#{atividadeMonitor.obj.id}"  id="idAtividadeMonitor" />
			<h:inputHidden value="#{atividadeMonitor.obj.discenteMonitoria.id}"  id="idDiscenteMonitoria" />
			<h:inputHidden value="#{atividadeMonitor.obj.discenteMonitoria.projetoEnsino.id}"  id="idProjetoEnsino"/>
			

			<table class="formulario" width="100%">
			<caption class="listagem">Relat�rio de Atividades do Monitor </caption>
		

            <tr>
                <th width="20%"><b>Discente:</b></th>
                <td><h:outputText value="#{atividadeMonitor.obj.discenteMonitoria.discente.matriculaNome}" id="txtNomeDiscente"/></td>
            </tr>

            <tr>
                <th width="20%"><b>Per�odo da Monitoria:</b></th>
                <td><h:outputText value="#{atividadeMonitor.obj.discenteMonitoria.dataInicio}" id="txtDataIncio"/> at� <h:outputText value="#{atividadeMonitor.obj.discenteMonitoria.dataFim}" id="txtDataFim"/></td>
            </tr>

            <tr>
                <th width="20%"><b>V�nculo:</b></th>
                <td><h:outputText value="#{atividadeMonitor.obj.discenteMonitoria.tipoVinculo.descricao}" id="txtTipoVinculo"/></td>
            </tr>

			<tr>
				<th width="20%"><b>Projeto:</b></th>
				<td><h:outputText value="#{atividadeMonitor.obj.discenteMonitoria.projetoEnsino.anoTitulo}" id="txtTituloProjeto"/></td>
			</tr>

            <tr>
                <th width="20%"><b>Coordena��o:</b></th>
                <td><h:outputText value="#{atividadeMonitor.obj.discenteMonitoria.projetoEnsino.coordenacao.pessoa.nome}" id="txtCoordenacaoProjeto"/></td>
            </tr>


			
			<tr>	
				<c:if test="${ atividadeMonitor.confirmButton != 'Remover' }">
					<th class="${!acesso.monitoria ? 'rotulo' : ' '}">M�s/Ano:</th>
					<td>
					    <h:outputText value="#{ atividadeMonitor.mesAtualString } / " id="txtMes" rendered="#{!acesso.monitoria}"/><h:outputText value="#{ atividadeMonitor.anoAtual }" id="txtAno" rendered="#{!acesso.monitoria}"/>
					    
					    <h:panelGroup rendered="#{acesso.monitoria}">
						    <h:selectOneMenu value="#{ atividadeMonitor.obj.mes }" id="selectMes">
						            <f:selectItem itemLabel="Janeiro"   itemValue="1"/>                             
		                            <f:selectItem itemLabel="Fevereiro" itemValue="2"/>                                                             
		                            <f:selectItem itemLabel="Mar�o"     itemValue="3"/>
		                            <f:selectItem itemLabel="Abril"     itemValue="4"/>
		                            <f:selectItem itemLabel="Maio"      itemValue="5"/>
		                            <f:selectItem itemLabel="Junho"     itemValue="6"/>
		                            <f:selectItem itemLabel="Julho"     itemValue="7"/>
		                            <f:selectItem itemLabel="Agosto"    itemValue="8"/>
		                            <f:selectItem itemLabel="Setembro"  itemValue="9"/>
		                            <f:selectItem itemLabel="Outubro"   itemValue="10"/>
		                            <f:selectItem itemLabel="Novembro"  itemValue="11"/>
		                            <f:selectItem itemLabel="Dezembro"  itemValue="12"/>				        
						    </h:selectOneMenu>/
						    <h:selectOneMenu value="#{ atividadeMonitor.obj.ano }" id="selectAno">
						        <f:selectItems value="#{atividadeMonitor.anos}"/>
						    </h:selectOneMenu>
					    </h:panelGroup>
					</td>
				</c:if>
				<c:if test="${ atividadeMonitor.confirmButton == 'Remover' }">
					<th><b>M�s/Ano:</b></th>
					<td> ${atividadeMonitor.obj.mes}/${atividadeMonitor.obj.ano} <td>
				</c:if>
			</tr>
		
		    <c:if test="${acesso.monitoria}">
		      <tr>
		      	<c:if test="${ atividadeMonitor.confirmButton == 'Remover' }">
		      		<th><b>Freq��ncia:</b></th>
		      		<td>${atividadeMonitor.obj.frequencia}%</td>
		      	</c:if>
		      
		      	<c:if test="${ atividadeMonitor.confirmButton != 'Remover' }">
	                <th>Freq��ncia:</th> 
	                <td>        
	                   <h:selectOneMenu rendered="#{acesso.monitoria}"  
	                          value="#{ atividadeMonitor.obj.frequencia }" id="txtFrequenciaGestorMonitoria">
	                          <f:selectItem itemLabel="0%" itemValue="0"/>                                
	                          <f:selectItem itemLabel="100%" itemValue="100"/>
	                   </h:selectOneMenu>
	                   <ufrn:help img="/img/ajuda.gif">Nova resolu��o limitou a valida��o de freq��ncias para 0% ou 100%</ufrn:help>
	                </td>
                </c:if>
              </tr>            
		    </c:if>
		
		
			<tr>
				 <c:if test="${ atividadeMonitor.confirmButton == 'Remover' }">
					<th><b>Atividades Desenvolvidas:<b/></th>
				 	<td>${atividadeMonitor.obj.atividades}</td>
				 </c:if>
				 <c:if test="${ atividadeMonitor.confirmButton != 'Remover'}">
				 	<th class="required" >Atividades Desenvolvidas:</th>
                    <td><h:inputTextarea value="#{ atividadeMonitor.obj.atividades }" rows="10" style="width: 98%" id="txaAtividades" readonly="#{atividadeMonitor.readOnly}"/></td>
                 </c:if>
				
			</tr>
		
			<tr>
				<td colspan="2"><br/></td>
			</tr>
			
            <c:if test="${acesso.monitoria}">
                <tr>
                    
                    <c:if test="${ atividadeMonitor.confirmButton == 'Remover' }">
                    	<th nowrap="nowrap"><b>Observa��es (Motivo do cadastro):<b/></th>
                    	<td>${ atividadeMonitor.obj.observacaoPrograd }</td>
                    </c:if>
                    
                    <c:if test="${ atividadeMonitor.confirmButton != 'Remover'}">
                        <th nowrap="nowrap" class="required">Observa��es (Motivo do cadastro):</th>
                    	<td><h:inputTextarea value="#{ atividadeMonitor.obj.observacaoPrograd }" rows="5" style="width: 98%" id="txaObsGestorMonitoria" readonly="#{atividadeMonitor.readOnly}" rendered="#{acesso.monitoria}"/></td>
                    </c:if>
                    
                </tr> 
            </c:if>
		
			<tfoot>
				<tr>
					<td colspan="2">
							<c:if test="${ atividadeMonitor.confirmButton != 'Remover' }">
								<h:commandButton value="#{atividadeMonitor.confirmButton}" action="#{atividadeMonitor.cadastrarAtividade}" id="btCadastrar"/>
							</c:if>
							
							<c:if test="${ atividadeMonitor.confirmButton == 'Remover' }">
								<h:commandButton value="#{atividadeMonitor.confirmButton}" onclick="return confirm('Deseja realmente remover este relat�rio de atividade do monitor?')" action="#{atividadeMonitor.cadastrarAtividade}" id="btCadastrar"/>
							</c:if>
							
							<h:commandButton value="Cancelar" action="#{atividadeMonitor.listarMeusProjetos}" id="btVoltarLista" rendered="#{!acesso.monitoria}" onclick="#{confirm}"/>							
							<h:commandButton value="Cancelar" action="#{atividadeMonitor.getListPage}" id="btVoltarGestorLista" rendered="#{acesso.monitoria && atividadeMonitor.confirmButton == 'Alterar'}" onclick="#{confirm}"/>
							<h:commandButton value="Cancelar" action="#{comissaoMonitoria.novoRelatorioAtividades}" id="btVoltarGestorBusca" rendered="#{acesso.monitoria && atividadeMonitor.confirmButton != 'Alterar'}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
			</table>
			<c:if test="${ atividadeMonitor.confirmButton != 'Remover'}">
				<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>
			</c:if>
		</h:form>
	

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>