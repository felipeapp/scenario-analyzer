<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<f:view>
	<h:messages/>
	<h2><ufrn:subSistema /> > Relatórios de Atividade dos Monitores</h2>
	<br/>

	<h:outputText value="#{atividadeMonitor.create}"/>

	<h:form id="formBuscaAtividadeMonitor">
			<table class="formulario" width="90%">
				<caption>Busca por Relatório de Atividades dos Monitores</caption>
			<tbody>
		
			
			
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{atividadeMonitor.checkBuscaAnoAtividade}" id="selectBuscaAnoAtividade"/>
					</td>
				
					<td><label>Ano do Relatório: </label></td>
					<td>
						<h:inputText value="#{atividadeMonitor.anoAtividade}"
									 id="anoAtividade" size="10"
									 onchange="javascript:$('formBuscaAtividadeMonitor:selectBuscaAnoAtividade').checked = true;"
									 converter="#{ intConverter }"
									 onkeyup="return formatarInteiro(this)"
									 onkeypress="javascript:$('formBuscaAtividadeMonitor:selectBuscaAnoAtividade').checked = true;"/>			
					</td>
				</tr>
				
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{atividadeMonitor.checkBuscaAnoProjeto}" id="selectBuscaAnoProjeto"/>
					</td>
				
					<td><label>Ano do Projeto: </label></td>
					<td>
						<h:inputText value="#{atividadeMonitor.anoProjeto}"
								     id="anoProjeto"
								     size="10"
								     onchange="javascript:$('formBuscaAtividadeMonitor:selectBuscaAnoProjeto').checked = true;"
								     converter="#{ intConverter }"
								     onkeyup="return formatarInteiro(this)"
								     onkeypress="javascript:$('formBuscaAtividadeMonitor:selectBuscaAnoProjeto').checked = true;"/>			
					</td>
				</tr>		
			
			
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{atividadeMonitor.checkBuscaValidacaoFrequencia}" id="selectBuscaValidacaoFrequencia"/>
					</td>
					
					<td>Período de Análise:</td>
					
					<td>
						<t:inputCalendar id="data_inicio_validacao"
							onchange="javascript:$('formBuscaAtividadeMonitor:selectBuscaValidacaoFrequencia').checked = true;"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"
							value="#{atividadeMonitor.inicioValidacaoFrequencia}" 
							popupDateFormat="dd/MM/yyyy"
							popupTodayString="Hoje é"
							size="10" 
							maxlength="10"
							onkeypress="return(formataData(this,event))">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						a
						<t:inputCalendar id="data_fim_validacao"
							onchange="javascript:$('formBuscaAtividadeMonitor:selectBuscaValidacaoFrequencia').checked = true;"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"
							value="#{atividadeMonitor.fimValidacaoFrequencia}" 
							popupDateFormat="dd/MM/yyyy"
							popupTodayString="Hoje é"
							size="10"
							maxlength="10"
							onkeypress="return(formataData(this,event))">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						<ufrn:help img="/img/ajuda.gif">Período em que o Coordenador/Orientador analisou o relatório de atividades (Frequência) </ufrn:help>										
					</td>
				</tr>
				
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{atividadeMonitor.checkBuscaDataCadastro}" id="selectBuscaDataCadastro"/>
					</td>
					
					<td>Período de Cadastro:</td>
					
					<td>
						<t:inputCalendar id="data_inicio_cadastro"
							onchange="javascript:$('formBuscaAtividadeMonitor:selectBuscaDataCadastro').checked = true;"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"
							value="#{atividadeMonitor.inicioDataCadastro}" 
							popupDateFormat="dd/MM/yyyy"
							popupTodayString="Hoje é"
							size="10" 
							maxlength="10"
							onkeypress="return(formataData(this,event))">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						a
						<t:inputCalendar id="data_fim_cadastro"
							onchange="javascript:$('formBuscaAtividadeMonitor:selectselectBuscaDataCadastro').checked = true;"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"
							value="#{atividadeMonitor.fimDataCadastro}" 
							popupDateFormat="dd/MM/yyyy"
							popupTodayString="Hoje é"
							size="10"
							maxlength="10"
							onkeypress="return(formataData(this,event))">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						<ufrn:help img="/img/ajuda.gif">Período em que foi realizado o cadastro de atividades (Frequência) </ufrn:help>										
					</td>
				</tr>
			
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{atividadeMonitor.checkBuscaMes}" id="selectBuscaMes"/>
					</td>
					
					<td><label> Mês: </label></td>
					<td>
						<h:selectOneMenu value="#{atividadeMonitor.mes}" id="mes" style="width: 200px"  onchange="javascript:$('formBuscaAtividadeMonitor:selectBuscaMes').checked = true;">
							<f:selectItem itemValue="0" itemLabel=" -- SELECIONE -- "  />
							<f:selectItems value="#{atividadeMonitor.meses}"/>
						</h:selectOneMenu>					
					</td>
				</tr>
				
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{atividadeMonitor.checkBuscaFrequencia}" id="selectBuscaFrequencia"/>
					</td>
					
					<td><label> Frequências Validadas: </label></td>
					<td>
						<h:selectOneMenu value="#{atividadeMonitor.frequencia}" id="frequencia_validada" style="width: 200px"  onchange="javascript:$('formBuscaAtividadeMonitor:selectBuscaFrequencia').checked = true;">
							<f:selectItem itemValue="-1" itemLabel="-- SELECIONE --"/>
							<f:selectItem itemValue="100" itemLabel="SIM"  />
							<f:selectItem itemValue="0" itemLabel="NÃO"  />
						</h:selectOneMenu>					
					</td>
				</tr>
				

				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{atividadeMonitor.checkBuscaDiscente}" id="selectBuscaDiscente"/>
					</td>

					<td><label> Discente: </label></td>
					<td>
						
						 <h:inputHidden id="idDiscente" value="#{ atividadeMonitor.discente.id}"/>
						 <h:inputText id="nomeDiscente" value="#{ atividadeMonitor.discente.pessoa.nome }" size="60"  onchange="javascript:$('formBuscaAtividadeMonitor:selectBuscaDiscente').checked = true;" onkeypress="javascript:$('formBuscaAtividadeMonitor:selectBuscaDiscente').checked = true;"/>
				
						<ajax:autocomplete source="formBuscaAtividadeMonitor:nomeDiscente" target="formBuscaAtividadeMonitor:idDiscente"
							baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
							indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=ufrn"
							parser="new ResponseXmlToHtmlListParser()" />
				
						<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
											
					</td>
				</tr>					
				
				<tr>
					<td>
						<h:selectBooleanCheckbox value="#{atividadeMonitor.checkBuscaProjeto}" id="selectBuscaProjeto"/>
					</td>				
					<td><label> Projeto: </label></td>
					<td>
						 <h:inputText id="tituloProjeto" value="#{ atividadeMonitor.tituloProjeto }" size="60"  onchange="javascript:$('formBuscaAtividadeMonitor:selectBuscaProjeto').checked = true;" onkeypress="javascript:$('formBuscaAtividadeMonitor:selectBuscaProjeto').checked = true;"/>
					</td>
				</tr>		
				
				
				<tr>
		   			<td>
					<h:selectBooleanCheckbox value="#{atividadeMonitor.checkBuscaIdCentro}"  id="selectBuscaIdCentro" styleClass="noborder" />
					</td>
					<td> <label for="centro">Centro do Projeto:</label></td>
					<td>
						<h:selectOneMenu id="centro" value="#{atividadeMonitor.centro.id}" style="width: 300px" onchange="javascript:$('formBuscaAtividadeMonitor:selectBuscaIdCentro').checked = true;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"  />
							<f:selectItems value="#{unidade.centrosEspecificasEscolas}"/>
						</h:selectOneMenu>
					</td>
				</tr>
				
				
				<tr>
					<td>
					<h:selectBooleanCheckbox value="#{atividadeMonitor.checkBuscaSituacao}"  id="selectBuscaSituacao" />
					</td>
			    	<td> <label> Situação do Monitor:</label> </td>
			    	<td>
		
			    	 <h:selectOneMenu value="#{atividadeMonitor.situacaoMonitor.id}" style="width: 200px"  onchange="javascript:$('formBuscaAtividadeMonitor:selectBuscaSituacao').checked = true;">
			    	 	<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{consultarMonitor.situacaoDiscenteMonitoriaCombo}"/>
		 			 </h:selectOneMenu>
		
			    	 </td>
			    </tr>
		
			    
				<tr>
					<td><h:selectBooleanCheckbox value="#{atividadeMonitor.checkBuscaTipoVinculo}"  id="selectBuscaTipoVinculo" /></td>
			    	<td><label> Tipo de Vínculo:</label></td>
			    	<td>
		
			    	 <h:selectOneMenu value="#{atividadeMonitor.tipoVinculo.id}" style="width: 200px"  onchange="javascript:$('formBuscaAtividadeMonitor:selectBuscaTipoVinculo').checked = true;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{discenteMonitoria.tipoMonitoriaCombo}"/>
		 			 </h:selectOneMenu>
		
			    	 </td>
			    </tr>	
				
				
				
								
			
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="3">
					<h:commandButton id="BtnBuscar" value="Buscar" action="#{atividadeMonitor.buscar}" />
					<h:commandButton id="BtnCancelar" value="Cancelar" action="#{ atividadeMonitor.cancelar }"/>
			    	</td>
			    </tr>
			</tfoot>			
			
			</table>
	</h:form>

<br/>



<c:if test="${ not empty atividadeMonitor.atividades }">
	
	<div class="infoAltRem">
	    <h:graphicImage value="/img/arrow_undo.png" style="overflow: visible;"/>: Desvalidar Relatório
	    <h:graphicImage value="/img/view.gif" 	style="overflow: visible;"/>: Visualizar Relatório			    
	    <h:graphicImage value="/img/monitoria/user1_view.png" style="overflow: visible;"/>: Visualizar Dados do Monitor
	</div>
<br/>

	<h:form>
	<table class="listagem tablesorter" id="listagem" width="100">
	<caption>Lista de Relatórios de Atividades Encontrados (${fn:length(atividadeMonitor.atividades)})</caption>
	<thead>
		<tr>
			<th>Matrícula</th>
			<th>Discente</th>
			<th>Freq.</th>
			<th>Validado</th>
			<th>Análisado em</th>
			<th>Vínculo</th>
			<th>Situação</th>			
			<th></th>			
			<th></th>						
			<th></th>
			<th></th>

		</tr>
	</thead>
		<c:forEach items="#{atividadeMonitor.atividades}" var="atividade" varStatus="status">
               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		
					<td width="11%"> ${atividade.discenteMonitoria.discente.matricula} </td>
					<td> ${atividade.discenteMonitoria.discente.pessoa.nome} </td>
					<td> <fmt:formatNumber value="${atividade.mes}" pattern="00"/>/${ atividade.ano } </td>
					<td>
						<c:if test="${(atividade.dataValidacaoOrientador != null)}">
							<c:set var="cor" value="${(atividade.frequencia < 100) ? 'red' : 'black'}"/>
							<font color="${cor}">${atividade.frequencia}%</font>
	 					</c:if>
					</td>
										
					<td width="10%"> <fmt:formatDate pattern="dd/MM/yyyy" value="${atividade.dataValidacaoOrientador}"/> </td>
					<td> ${atividade.discenteMonitoria.tipoVinculo.descricao} </td>			

					<td>
						<c:set var="cor" value="${(atividade.discenteMonitoria.vigente) ? 'blue' : 'red'}"/>
						<font color="${cor}">${(atividade.discenteMonitoria.vigente) ? 'ATIVO':'INATIVO'}</font>
					</td>

					<td width="1%">
							<h:commandLink id="btnDesvalidar" title="Desvalidar Relatório de Atividade" 
							rendered="#{(atividade.discenteMonitoria.ativo) && (not empty atividade.dataValidacaoOrientador)}"
								action="#{atividadeMonitor.iniciarDesvalidarAtividadeMonitor}" style="border: 0;">
								   	<f:param name="id" value="#{atividade.id}"/>				    	
									<h:graphicImage url="/img/arrow_undo.png"    />
							</h:commandLink>
					</td>
					<td width="1%">		
							<h:commandLink id="btnVisualizar" title="Relatório Atividade" action="#{atividadeMonitor.visualizarRelatorioMonitor}" style="border: 0;">
								   	<f:param name="id" value="#{atividade.id}"/>				    	
									<h:graphicImage url="/img/view.gif"    />
							</h:commandLink>
					</td>
					<td width="1%">		
							<h:commandLink title="Visualizar Monitor" action="#{ consultarMonitor.view }">
								   	<f:param name="id" value="#{atividade.discenteMonitoria.id}"/>				    	
									<h:graphicImage url="/img/monitoria/user1_view.png"    />
							</h:commandLink>
					</td>
					
				</tr>			
		</c:forEach>		
	
	</table>
	<rich:jQuery selector="#listagem" query="tablesorter( {dateFormat: 'uk', headers: { 3: { sorter: false }, 4: { sorter: 'shortDate'  }, 5: { sorter: false }, 7: { sorter: false }, 8: { sorter: false }, 9: { sorter: false } } });" timing="onload" />
	
	
</h:form>
</c:if>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>