<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	
	<h2> <ufrn:subSistema /> &gt; Solicitação de Matrícula no Módulo Avançado </h2>

	<c:set var="discente" value="#{matriculaModuloAvancadoMBean.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>


	<h:form id="form">
	
	<table class="formulario" width="100%">
		<caption>Dados da Matrícula</caption>
		<tr>
			<td colspan="3" class="subFormulario"> Escolha da Ênfase </td>
		</tr>
		<tr>
			<td colspan="3">
				<div class="descricaoOperacao">
					<p>Informe as ênfases de sua preferência para cursar o Módulo Avançado em ordem de prioridade. </p>
					<p>O sistema tentará matricular você automaticamente de acordo com a sua classificação final no Módulo Básico e o número de vagas disponível na ênfase escolhida por ordem.</p>
					<p>Caso não haja mais vagas na sua 1ª Opção de Ênfase, o sistema tentará matriculá-lo na 2ª Opção, e assim sucessivamente. </p>
				</div> 
			</td>
		</tr>
		<tr>
			<th width="20%">1ª Opção:</th>
			<td width="30%">
				<h:selectOneMenu id="enfasePreferencial" value="#{matriculaModuloAvancadoMBean.idEnfase[0]}" disabled="#{matriculaModuloAvancadoMBean.idEnfase[0] > 0}" 
						valueChangeListener="#{matriculaModuloAvancadoMBean.selecionarPrimeiraOpcao}">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
						<f:selectItems value="#{matriculaModuloAvancadoMBean.modulosCombo}"/>
						<a4j:support event="onchange" reRender="form"/>
				</h:selectOneMenu>
			</td>
			<td>
				<h:outputText id="labelEnfasePrimaria" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[0].modulo.descricao}"/>
			</td>
		</tr>
		
		<a4j:region rendered="#{ matriculaModuloAvancadoMBean.segundaOpcao }">
			<tr>
				<th>2ª Opção:</th>
				<td>
					<h:selectOneMenu id="enfaseSecundaria" value="#{matriculaModuloAvancadoMBean.idEnfase[1]}" valueChangeListener="#{matriculaModuloAvancadoMBean.selecionarSegundaOpcao}" 
						disabled="#{matriculaModuloAvancadoMBean.idEnfase[0] == 0 || matriculaModuloAvancadoMBean.idEnfase[1] > 0}" >
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{matriculaModuloAvancadoMBean.modulosCombo}"/>
							<a4j:support event="onchange" reRender="form"/>
					</h:selectOneMenu>
				</td>
				<td>
					<h:outputText id="labelEnfaseSecundaria" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[1].modulo.descricao}"/>
				</td>
			</tr>
		</a4j:region>
		
		<a4j:region rendered="#{ matriculaModuloAvancadoMBean.terceiraOpcao }">
			<tr>
				<th>3ª Opção:</th>
				<td>
					<h:selectOneMenu id="enfaseTerciaria" value="#{matriculaModuloAvancadoMBean.idEnfase[2]}" valueChangeListener="#{matriculaModuloAvancadoMBean.selecionarTerceiraOpcao}" 
						disabled="#{matriculaModuloAvancadoMBean.idEnfase[0] == 0 || matriculaModuloAvancadoMBean.idEnfase[1] == 0 || matriculaModuloAvancadoMBean.idEnfase[2] > 0}">
							<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
							<f:selectItems value="#{matriculaModuloAvancadoMBean.modulosCombo}"/>
							<a4j:support event="onchange" reRender="form"/>
					</h:selectOneMenu>
				</td>
				<td>
					<h:outputText id="labelEnfaseTerciaria" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[2].modulo.descricao}"/>
				</td>
			</tr>
		</a4j:region>
		
		<a4j:region rendered="#{ matriculaModuloAvancadoMBean.quartaOpcao }">
			<tr>
				<th>4ª Opção:</th>
				<td>
				</td>
				<td>
					<h:outputText id="labelEnfaseQuaternaria" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[3].modulo.descricao}"/>
				</td>
			</tr>
		</a4j:region>
		
		<tr>
			<tr>
				<td colspan="3" class="subFormulario"> 1ª Opção de Ênfase: <h:outputText id="labelEnfPref" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[0].modulo.descricao}"/> </td>
			</tr>
			<td colspan="3">
				<div class="descricaoOperacao">
					<p>
						Informe suas preferências de dia e horário no qual deseja cursar o Módulo Avançado dentre as opções disponíveis para sua 1ª Opção de Ênfase.  
					</p>
					<ul>
						<li>As opções de horário devem ser selecionadas por ordem de prioridade.</li>
						<li>Você deve adicionar <strong>TODAS</strong> as opções de horário para concluir a solicitação de matrícula.</li>
					</ul>
				</div> 
				<center>
					<div class="infoAltRem">
					    <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: Adicionar
					    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover 
					</div>
				</center>
			</td>
		</tr>
		<tr>
			<td colspan="3" align="center">
				Opção de horário:
				<h:selectOneMenu id="opcaoPref" value="#{matriculaModuloAvancadoMBean.opcaoEnfase[0]}" style="width: 30%">
					<f:selectItems value="#{matriculaModuloAvancadoMBean.opcoesPreferencialCombo}"/>
				</h:selectOneMenu>
				
				<a4j:commandLink id="linkAddOpPref" action="#{matriculaModuloAvancadoMBean.adicionarOpcaoHorario}" reRender="opcaoPref, listaPref">
					<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Adicionar"/>
					<f:param name="tipo" value="0"/>
				</a4j:commandLink>
				<br/>
				<br/>
				 
				<rich:separator height="2" lineType="dashed"/>
				
				<br/>
				<h:dataTable id="listaPref" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[0].opcoesHorario}"
					binding="#{matriculaModuloAvancadoMBean.itens[0]}" 
					rowClasses="linhaPar, linhaImpar"  var="item"  width="50%" >
						<f:facet name="header">
							<rich:columnGroup  >
								<rich:column width="20%">
									 <f:facet name="header">Prioridade</f:facet>
								</rich:column>
								<rich:column width="75%">
									<f:facet name="header">Horário</f:facet>
								</rich:column> 
								<rich:column width="5%">
									<rich:spacer />
								</rich:column>
							</rich:columnGroup>
						</f:facet>
						
						<rich:column >
							<h:outputText value="#{item.ordem}" />
						</rich:column>
						<rich:column >
							<h:outputText value="#{item.descricao}" />
						</rich:column>
						
						<rich:column >
							<a4j:commandButton id="btnRemOpPref" action="#{matriculaModuloAvancadoMBean.removerOpcaoHorarioEnfasePrimaria}" 
								title="Remover" image="/img/delete.gif" reRender="opcaoPref, listaPref" />
						</rich:column>
				</h:dataTable>			   
		   </td>
		</tr>
		
		<c:if test="${ matriculaModuloAvancadoMBean.segundaOpcao }">
			<tr>
				<td colspan="3" class="subFormulario"> 2ª Opção de Ênfase: <h:outputText id="labelEnfSec" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[1].modulo.descricao}"/> </td>
			</tr>
			<tr>
				<td colspan="3">
					<div class="descricaoOperacao">
						<p>
							Caso você não se classifique para cursar o Módulo Avançado na 1ª Opção de Ênfase escolhida, o sistema automaticamente tentará colocá-lo
							na 2ª Opção de Ênfase. Dessa forma, informe suas preferências de dia e horário dentre as opções disponíveis para sua 2ª Opção de Ênfase.
						</p>
						<ul>
							<li>As opções de horário devem ser selecionadas por ordem de prioridade.</li>
							<li>Você deve adicionar <strong>TODAS</strong> as opções de horário para concluir a solicitação de matrícula.</li>
						</ul>
					</div>
					<center>
						<div class="infoAltRem">
						    <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: Adicionar
						    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover 
						</div>
					</center>
				</td>
			</tr>
			<tr>
				<td colspan="3" align="center">
					Opção de horário:
					<h:selectOneMenu id="opcaoSec" value="#{matriculaModuloAvancadoMBean.opcaoEnfase[1]}" style="width: 30%">
						<f:selectItems value="#{matriculaModuloAvancadoMBean.opcoesSecundariaCombo}"/>
					</h:selectOneMenu>
					
					<a4j:commandLink id="linkAddOpSec" action="#{matriculaModuloAvancadoMBean.adicionarOpcaoHorario}" reRender="opcaoSec, listaSec">
						<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Adicionar"/>
						<f:param name="tipo" value="1"/>
					</a4j:commandLink>
					<br/>
					<br/>
					 
					<rich:separator height="2" lineType="dashed"/>
					
					<br/>
					<h:dataTable id="listaSec" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[1].opcoesHorario}" 
						binding="#{matriculaModuloAvancadoMBean.itens[1]}"
						rowClasses="linhaPar, linhaImpar"  var="item"  width="50%" >
							<f:facet name="header">
								<rich:columnGroup  >
									<rich:column width="20%">
										 <f:facet name="header">Prioridade</f:facet>
									</rich:column>
									<rich:column width="75%">
										<f:facet name="header">Horário</f:facet>
									</rich:column> 
									<rich:column width="5%">
										<rich:spacer />
									</rich:column>
								</rich:columnGroup>
							</f:facet>
							
							<rich:column >
								<h:outputText value="#{item.ordem}" />
							</rich:column>
							<rich:column >
								<h:outputText value="#{item.descricao}" />
							</rich:column>
							
							<rich:column >
								<a4j:commandButton id="btnRemOpSec" action="#{matriculaModuloAvancadoMBean.removerOpcaoHorarioEnfaseSecundaria}" 
									title="Remover" image="/img/delete.gif" reRender="opcaoSec, listaSec" />
							</rich:column>
					</h:dataTable>			   
			   </td>
			</tr>
		</c:if>
		
		<c:if test="${ matriculaModuloAvancadoMBean.terceiraOpcao }">
			<tr>
				<td colspan="3" class="subFormulario"> 3ª Opção de Ênfase: <h:outputText id="labelEnfTer" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[2].modulo.descricao}"/> </td>
			</tr>
			<tr>
				<td colspan="3">
					<div class="descricaoOperacao">
						<p>
							Caso você não se classifique para cursar o Módulo Avançado na 1ª e 2ª Opções de Ênfases escolhidas, o sistema automaticamente tentará colocá-lo
							na 3ª Opção de Ênfase. Dessa forma, informe suas preferências de dia e horário dentre as opções disponíveis para sua 3ª Opção de Ênfase.
						</p>
						<ul>
							<li>As opções de horário devem ser selecionadas por ordem de prioridade.</li>
							<li>Você deve adicionar <strong>TODAS</strong> as opções de horário para concluir a solicitação de matrícula.</li>
						</ul>
					</div>
					<center>
						<div class="infoAltRem">
						    <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: Adicionar
						    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover 
						</div>
					</center>
				</td>
			</tr>
			<tr>
				<td colspan="3" align="center">
					Opção de horário:
					<h:selectOneMenu id="opcaoTer" value="#{matriculaModuloAvancadoMBean.opcaoEnfase[2]}" style="width: 30%">
						<f:selectItems value="#{matriculaModuloAvancadoMBean.opcoesTerciariaCombo}"/>
					</h:selectOneMenu>
					
					<a4j:commandLink id="linkAddOpTer" action="#{matriculaModuloAvancadoMBean.adicionarOpcaoHorario}" reRender="opcaoTer, listaTer">
						<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Adicionar"/>
						<f:param name="tipo" value="2"/>
					</a4j:commandLink>
					<br/>
					<br/>
					 
					<rich:separator height="2" lineType="dashed"/>
					
					<br/>
					<h:dataTable id="listaTer" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[2].opcoesHorario}" 
						binding="#{matriculaModuloAvancadoMBean.itens[2]}"
						rowClasses="linhaPar, linhaImpar"  var="item"  width="50%" >
							<f:facet name="header">
								<rich:columnGroup  >
									<rich:column width="20%">
										 <f:facet name="header">Prioridade</f:facet>
									</rich:column>
									<rich:column width="75%">
										<f:facet name="header">Horário</f:facet>
									</rich:column> 
									<rich:column width="5%">
										<rich:spacer />
									</rich:column>
								</rich:columnGroup>
							</f:facet>
							
							<rich:column >
								<h:outputText value="#{item.ordem}" />
							</rich:column>
							<rich:column >
								<h:outputText value="#{item.descricao}" />
							</rich:column>
							
							<rich:column >
								<a4j:commandButton id="btnRemOpTer" action="#{matriculaModuloAvancadoMBean.removerOpcaoHorarioEnfaseTerciaria}" 
									title="Remover" image="/img/delete.gif" reRender="opcaoTer, listaTer" />
							</rich:column>
					</h:dataTable>			   
			   </td>
			</tr>
		</c:if>
		
		<c:if test="${ matriculaModuloAvancadoMBean.quartaOpcao }">
			<tr>
				<td colspan="3" class="subFormulario"> 4ª Opção de Ênfase: <h:outputText id="labelEnfQua" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[3].modulo.descricao}"/> </td>
			</tr>
			<tr>
				<td colspan="3">
					<div class="descricaoOperacao">
						<p>
							Caso você não se classifique para cursar o Módulo Avançado na 1ª, 2ª ou 3ª Opções de Ênfases escolhidas, o sistema automaticamente tentará colocá-lo
							na 4ª Opção de Ênfase. Dessa forma, informe suas preferências de dia e horário dentre as opções disponíveis para sua 4ª Opção de Ênfase.
						</p>
						<ul>
							<li>As opções de horário devem ser selecionadas por ordem de prioridade.</li>
							<li>Você deve adicionar <strong>TODAS</strong> as opções de horário para concluir a solicitação de matrícula.</li>
						</ul>
					</div>
					<center>
						<div class="infoAltRem">
						    <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: Adicionar
						    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover 
						</div>
					</center>
				</td>
			</tr>
			<tr>
				<td colspan="3" align="center">
					Opção de horário:
					<h:selectOneMenu id="opcaoQua" value="#{matriculaModuloAvancadoMBean.opcaoEnfase[3]}" style="width: 30%">
						<f:selectItems value="#{matriculaModuloAvancadoMBean.opcoesQuaternariaCombo}"/>
					</h:selectOneMenu>
					
					<a4j:commandLink id="linkAddOpQua" action="#{matriculaModuloAvancadoMBean.adicionarOpcaoHorario}" reRender="opcaoQua, listaQua">
						<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Adicionar"/>
						<f:param name="tipo" value="3"/>
					</a4j:commandLink>
					<br/>
					<br/>
					 
					<rich:separator height="2" lineType="dashed"/>
					
					<br/>
					<h:dataTable id="listaQua" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[3].opcoesHorario}" 
						binding="#{matriculaModuloAvancadoMBean.itens[3]}"
						rowClasses="linhaPar, linhaImpar"  var="item"  width="50%" >
							<f:facet name="header">
								<rich:columnGroup  >
									<rich:column width="20%">
										 <f:facet name="header">Prioridade</f:facet>
									</rich:column>
									<rich:column width="75%">
										<f:facet name="header">Horário</f:facet>
									</rich:column> 
									<rich:column width="5%">
										<rich:spacer />
									</rich:column>
								</rich:columnGroup>
							</f:facet>
							
							<rich:column >
								<h:outputText value="#{item.ordem}" />
							</rich:column>
							<rich:column >
								<h:outputText value="#{item.descricao}" />
							</rich:column>
							
							<rich:column >
								<a4j:commandButton id="btnRemOpQua" action="#{matriculaModuloAvancadoMBean.removerOpcaoHorarioEnfaseQuaternaria}" 
									title="Remover" image="/img/delete.gif" reRender="opcaoQua, listaQua" />
							</rich:column>
					</h:dataTable>			   
			   </td>
			</tr>
		</c:if>

		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="Confirmar Solicitação de Matrícula" action="#{matriculaModuloAvancadoMBean.cadastrar}" id="cadastrar" />
					<h:commandButton value="<< Selecionar Outro Discente" action="#{matriculaModuloAvancadoMBean.iniciarCoordenacao}" id="selecionarOutroDiscente" rendered="#{not matriculaModuloAvancadoMBean.discenteLogado}" />
					<h:commandButton value="Cancelar" action="#{matriculaModuloAvancadoMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	
	</table>

	</h:form>

	<br />
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>