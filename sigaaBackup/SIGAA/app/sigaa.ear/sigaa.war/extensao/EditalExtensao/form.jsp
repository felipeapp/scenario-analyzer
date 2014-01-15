<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>
<%@page import="br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao"%>


<f:view>
	<h2><ufrn:subSistema /> &gt; Edital de Extens�o</h2>
	<a4j:keepAlive beanName="editalExtensao" />
	<h:form id="form_edital"  enctype="multipart/form-data">

		<h:inputHidden value="#{editalExtensao.confirmButton}" />
		<h:inputHidden value="#{editalExtensao.obj.id}" />
		<h:inputHidden value="#{editalExtensao.obj.edital.idArquivo}" />

		<table class="formulario" width="100%" >
			<caption class="listagem">Cadastrar Edital</caption>
			
			<tr>
				<th width="20%" class="required"> Tipo de Edital: </th>
				<td>
					<b><h:outputText value="#{editalExtensao.obj.edital.tipoString}"/></b>
				</td>
			</tr>
			
			<tr>
				<th width="40%" class="required">Nome Edital:</th>
				<td>
					<h:inputText value="#{editalExtensao.obj.descricao}" readonly="#{editalExtensao.readOnly}" size="80" id="descricaoEdital"/>
				</td>
			</tr>

			<tr>
				<th class="required">N�mero:</th>
				<td>
					<h:inputText value="#{editalExtensao.obj.numeroEdital}"
					size="20" readonly="#{editalExtensao.readOnly}" maxlength="20" id="numeroEdital"/>
				</td>
			</tr>
			
			<tr>
				<th class="required">Ano/Semestre:</th>
				<td>
					<h:inputText value="#{editalExtensao.obj.ano}"	size="4" readonly="#{editalExtensao.readOnly}" maxlength="4" id="ano"/>/
					<h:inputText value="#{editalExtensao.obj.semestre}" size="1" readonly="#{editalExtensao.readOnly}" maxlength="1" id="semestre"/>
				</td>
			</tr>

			<tr>
				<th class="required">In�cio do per�odo de realiza��o dos projetos:</th>
				<td>
					<t:inputCalendar id="inicioPeriodoRealiProj" value="#{ editalExtensao.obj.edital.inicioRealizacao }" renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))" size="10" maxlength="10"> 
						<f:convertDateTime pattern="dd/MM/yyyy"/> 
					</t:inputCalendar>								
				</td>
			</tr>

			<tr>
				<th class="required">Fim do per�odo de realiza��o dos projetos:</th>
				<td>
					<t:inputCalendar id="fimPeriodoRealiProj" value="#{ editalExtensao.obj.edital.fimRealizacao }" renderAsPopup="true"  renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))" size="10" maxlength="10"> 
						<f:convertDateTime pattern="dd/MM/yyyy"/> 
					</t:inputCalendar>
				</td>
			</tr>
			
			
			<tr>
				<th class="required">Iniciar Recebimento Em:</th>
				<td>
					<t:inputCalendar size="10" value="#{editalExtensao.obj.inicioSubmissao}" renderAsPopup="true" renderPopupButtonAsImage="true" onkeypress="return(formataData(this,event))"  maxlength="10" id="inicioRecebimento" popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �" />
				</td>
			</tr>

			<tr>
				<th class="required">Receber Propostas at�:</th>
				<td>
					<t:inputCalendar size="10" value="#{editalExtensao.obj.fimSubmissao}" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="return(formataData(this,event))"  maxlength="10" id="finalRecebimento" popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �" />
				</td>
			</tr>
			
			<tr>
				<th class="required">Autoriza��es do Departamento at�:</th>
				<td>
					<t:inputCalendar size="10" value="#{editalExtensao.obj.dataFimAutorizacaoChefe}" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="return(formataData(this,event))"  maxlength="10" id="finalEnvioDepartamento" popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �" />
				</td>
			</tr>

			<tr>
				<th class="required">Data in�cio do limite para envio de reconsidera��o:</th>
				<td>
					<t:inputCalendar size="10" value="#{editalExtensao.obj.edital.dataInicioReconsideracao}" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="return(formataData(this,event))"  maxlength="10" id="dataInicioReconsideracao" popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �" />
				</td>
			</tr>

			<tr>
				<th class="required">Data fim do limite para envio de reconsidera��o:</th>
				<td>
					<t:inputCalendar size="10" value="#{editalExtensao.obj.edital.dataFimReconsideracao}" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="return(formataData(this,event))"  maxlength="10" id="dataFimReconsideracao" popupDateFormat="dd/MM/yyyy" popupTodayString="Hoje �" />
				</td>
			</tr>

			<tr>
				<th class="required">N�mero de Bolsas:</th>
				<td>
					<h:inputText value="#{editalExtensao.obj.numeroBolsas}"
					size="3" readonly="#{editalExtensao.readOnly}" maxlength="3" id="numeroBolsas" onkeyup="return formatarInteiro(this)"/>
				</td>
			</tr>

			<tr>
				<th class="required">Valor Total Financiamento (R$):</th>
				<td>
					<h:inputText id="valorFinanciamento" value="#{editalExtensao.obj.valorFinanciamento}" size="12" maxlength="10" onkeypress="return(formataValor(this, event, 2))" readonly="#{editalExtensao.readOnly}">
						<f:convertNumber pattern="#,##0.00"/>
					</h:inputText>
				</td>
			</tr>

			<tr>
			    <td class="subFormulario" colspan="2">Restri��es de coordena��o</td>
			</tr>
			
			<tr>
			    <th class="required">M�ximo de Coordena��es Ativas por Docente neste Edital:</th>
			    <td>
			        <h:inputText id="maxCoordenacoesAtivas" value="#{editalExtensao.obj.edital.restricaoCoordenacao.maxCoordenacoesAtivas}" size="10" maxlength="5" readonly="#{editalMBean.readOnly}" onkeyup="return formatarInteiro(this)" />
			    </td>
			</tr>
			
			<tr>
			    <th class="required">Permitir Docentes como Coordenadores de projetos:</th>
			    <td>
			        <h:selectOneRadio id="permitirCoordenadorDocente" value="#{editalExtensao.obj.edital.restricaoCoordenacao.permitirCoordenadorDocente}"  layout="lineDirection">
				        <f:selectItem itemLabel="SIM" itemValue="true"/>
					    <f:selectItem itemLabel="N�O" itemValue="false"/>           
			        </h:selectOneRadio>
			    </td>
			</tr>
			
			<tr>
			    <th class="required">Permitir T�cnicos Administrativos como Coordenadores de projetos:</th>
			    <td>
			        <h:selectOneRadio id="permitirCoordenadorTecnico" value="#{editalExtensao.obj.edital.restricaoCoordenacao.permitirCoordenadorTecnico}" layout="lineDirection">
			   	        <f:selectItem itemLabel="SIM" itemValue="true"/>
					    <f:selectItem itemLabel="N�O" itemValue="false"/> 
					    <a4j:support event="onclick" reRender="form_edital" />
			        </h:selectOneRadio>
			        
			    </td>
			</tr>
			
			
			<tr>
			    <th class="required">Somente Servidores do Quadro e em Efetivo Exerc�cio Podem Coordenar Projetos:</th>
			    <td>
			        <h:selectOneRadio id="apenasServidorAtivoCoordena" value="#{editalExtensao.obj.edital.restricaoCoordenacao.apenasServidorAtivoCoordena}">
			            <f:selectItem itemLabel="SIM" itemValue="true"/>
					    <f:selectItem itemLabel="N�O" itemValue="false"/>           
			        </h:selectOneRadio>        
			    </td>
			</tr>
			<a4j:region id="tecNivelSuperior">
			 <c:if test="${editalExtensao.obj.edital.restricaoCoordenacao.permitirCoordenadorTecnico}">
				<tr>
				    <th class="required">Somente T�cnicos Administrativos com N�vel Superior Podem Coordenar Projetos:</th>
				    <td>
				        <h:selectOneRadio id="apenasTecnicoSuperiorCoordena" value="#{editalExtensao.obj.edital.restricaoCoordenacao.apenasTecnicoSuperiorCoordena}" 
				        rendered="#{editalExtensao.obj.edital.restricaoCoordenacao.permitirCoordenadorTecnico}">
				            <f:selectItem itemLabel="SIM" itemValue="true"/>
						    <f:selectItem itemLabel="N�O" itemValue="false"/>           
				        </h:selectOneRadio>
				        
				    </td>
				</tr>
			 </c:if>
			</a4j:region>

            <tr>
                <td colspan="2">
					<table width="100%" class="subFormulario">
						<caption class="listagem">Restri��es para submiss�o de a��es neste edital</caption>
						<tr>
							<th width="40%" class="required">Tipo de A��o:</th>
							<td><h:selectOneMenu id="buscaTipoAcao"
								value="#{editalExtensao.novaRegra.tipoAtividadeExtensao.id}"
								onchange="javascript:atualizarRegras(this);">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="0" />
								<f:selectItems value="#{tipoAtividadeExtensao.allCombo}" />
							</h:selectOneMenu></td>
						</tr>
	
	                    <tr id="regraPrograma" style="display: ${editalExtensao.novaRegra.tipoAtividadeExtensao.programa ? '' : 'none' };">
                            <th class="required">Quantidade m�nima de a��es vinculadas ao Programa:</th>
                            <td>
                                <h:inputText value="#{editalExtensao.novaRegra.minAcoesVinculadas}"
                                size="5" readonly="#{editalExtensao.readOnly}" maxlength="5" id="minAcoesVinculadasTipo" onkeyup="return formatarInteiro(this)"/> 
                                <ufrn:help img="/img/ajuda.gif">Determina a quantidade m�nima necess�ria para que um Programa de Extens�o seja poss�vel.</ufrn:help>
                            </td>
                        </tr>
	
						<tr>
							<th class="required">Or�amento M�ximo Solicitado(R$):</th>
							<td><h:inputText id="orcamentoMaximo"
								value="#{editalExtensao.novaRegra.orcamentoMaximo}" size="12"
								maxlength="10" onkeypress="return(formataValor(this, event, 2))"
								readonly="#{editalExtensao.readOnly}">
								<f:convertNumber pattern="#,##0.00" />
							</h:inputText>
							<ufrn:help img="/img/ajuda.gif">Indica o financiamento m�ximo que uma a��o deste tipo pode solicitar para o edital.</ufrn:help>
							</td>
						</tr>
						
  					    <tr>
			                <th class="required">Per�odo M�nimo de Realiza��o da A��o:</th>
			                <td>
			                    <h:inputText value="#{editalExtensao.novaRegra.periodoMinimoExecucao}"
			                    size="5" readonly="#{editalExtensao.readOnly}" maxlength="5" id="periodoMinimoExecucao" onkeyup="return formatarInteiro(this)"/> meses 
			                    <ufrn:help img="/img/ajuda.gif">Argumento utilizado para proibir o cadastro de A��es de Extens�o com per�odo de execu��o muito curto.</ufrn:help>
			                </td>
			            </tr>

                        <tr>
                            <th class="required">M�ximo de Coordena��es Ativas por Docente neste Tipo de A��o:</th>
                            <td>
                                <h:inputText value="#{editalExtensao.novaRegra.maxCoordenacoesAtivas}"
                                size="5" readonly="#{editalExtensao.readOnly}" maxlength="5" id="maxCoordenacoesAtivasTipo" onkeyup="return formatarInteiro(this)"/> 
                                <ufrn:help img="/img/ajuda.gif">Limita a quantidade de coordena��es ativas para um Tipo de A��o espec�fico.</ufrn:help>
                            </td>
                        </tr>
	
						<tfoot>
							<tr>
								<td colspan="2"><h:commandButton value="Adicionar Regra"
									action="#{editalExtensao.adicionarRegra}" /></td>
							</tr>
						</tfoot>
					</table>
	               <br/>
					<h:dataTable id="regrasEdital" value="#{editalExtensao.obj.regrasAtivas}" binding="#{editalExtensao.dataTableRegras}"
					   var="regra" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
                          <f:facet name="caption">
                               <h:outputText value="Lista de restri��es cadastradas" />
                          </f:facet>
                             	                              	          
                          <t:column>
                              <f:facet name="header"><f:verbatim>Tipo de A��o de Extens�o</f:verbatim></f:facet>                                      
                              <h:outputText value="#{regra.tipoAtividadeExtensao.descricao}" id="descricaoTipo"/>                                     
                          </t:column>
                          
                          <t:column>
                                 <f:facet name="header"><f:verbatim>Or�amento M�ximo Solicitado(R$)</f:verbatim></f:facet>                                      
                                 <h:outputText value="#{regra.orcamentoMaximo}" id="orcamentoMaximo" rendered="#{regra.orcamentoMaximo > 0 }">
                                    <f:convertNumber pattern="#,##0.00" type="money" currencySymbol="R$"/>
                                 </h:outputText>
                                 <h:outputText value="-" rendered="#{regra.orcamentoMaximo <= 0 }"/>
                          </t:column>
                             
                          <t:column>
                                 <f:facet name="header"><f:verbatim>Per�odo M�nimo</f:verbatim></f:facet>                                      
                                 <h:outputText value="#{regra.periodoMinimoExecucao}" id="periodoMinimo" rendered="#{regra.periodoMinimoExecucao > 0 }">
                                         <f:convertNumber pattern="00"/>
                                 </h:outputText><f:verbatim rendered="#{regra.periodoMinimoExecucao > 0 }"> meses</f:verbatim>
                                 <h:outputText value="-" rendered="#{regra.periodoMinimoExecucao <= 0 }"/>
                          </t:column>                 	              

                          <t:column>
                                 <f:facet name="header"><f:verbatim>M�x. Coordena��es Ativas</f:verbatim></f:facet>                                      
                                 <h:outputText value="#{regra.maxCoordenacoesAtivas}" id="maxCoordenacoesTipo" rendered="#{regra.maxCoordenacoesAtivas > 0 }">
                                         <f:convertNumber pattern="00"/>
                                 </h:outputText>                      
                                 <h:outputText value="-" rendered="#{regra.maxCoordenacoesAtivas <= 0 }"/>
                          </t:column>                                 

                          <t:column>
                                 <f:facet name="header"><f:verbatim>M�n. A��es Vinculadas</f:verbatim></f:facet>                                      
                                 <h:outputText value="#{regra.minAcoesVinculadas}" id="minAcoesVinculadasTipo" rendered="#{regra.minAcoesVinculadas > 0 }">
                                         <f:convertNumber pattern="00"/>
                                 </h:outputText>                      
                                 <h:outputText value="-" rendered="#{regra.minAcoesVinculadas <= 0 }"/>
                          </t:column>                                 

                          
                          <t:column>
                            <h:commandLink action="#{editalExtensao.removerRegra}" title="Remover">
                                <h:graphicImage url="/img/delete.gif" />
                            </h:commandLink>
                          </t:column>                                     
		             </h:dataTable>                                            
                    <h:outputText value="<center><font color='red'>Lista de regras est� vazia</font></center>" rendered="#{empty editalExtensao.obj.regrasAtivas}" escape="false"/>                                                        
                </td>
            </tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="#{editalExtensao.confirmButton}" action="#{editalExtensao.cadastrar}"  rendered="#{editalExtensao.confirmButton != 'Remover'}" id="bt_cadastrar"/>
						<h:commandButton value="#{editalExtensao.confirmButton}" action="#{editalExtensao.remover}"  rendered="#{editalExtensao.confirmButton == 'Remover'}" id="bt_inativar"/>
						<h:commandButton value="Cancelar" action="#{editalExtensao.cancelar}" id="btcancelar" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>

		</table>
	</h:form>
</f:view>

<c:set var="_tipoPrograma" value="<%= String.valueOf(TipoAtividadeExtensao.PROGRAMA) %>" scope="session"/>
<script type="text/javascript">
<!--
	function atualizarRegras(obj){
		var regraPrograma = $('regraPrograma');
		
		if (obj.value == ${_tipoPrograma}){
		    regraPrograma.style.display = ""; 
		}else {
		    regraPrograma.style.display = "none"; 
		}
	}
//-->
</script>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>