<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/WEB-INF/jsp/include/errosAjax.jsp"%>
	
	<h2> <ufrn:subSistema /> &gt; Solicita��o de Matr�cula no M�dulo Avan�ado </h2>

	<c:set var="discente" value="#{matriculaModuloAvancadoMBean.obj.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>


	<h:form id="form">
	
	<table class="formulario" width="100%">
		<caption>Dados da Matr�cula</caption>
		<tr>
			<td colspan="3" class="subFormulario"> Escolha da �nfase </td>
		</tr>
		<tr>
			<td colspan="3">
				<div class="descricaoOperacao">
					<p>Informe as �nfases de sua prefer�ncia para cursar o M�dulo Avan�ado em ordem de prioridade. </p>
					<p>O sistema tentar� matricular voc� automaticamente de acordo com a sua classifica��o final no M�dulo B�sico e o n�mero de vagas dispon�vel na �nfase escolhida por ordem.</p>
					<p>Caso n�o haja mais vagas na sua 1� Op��o de �nfase, o sistema tentar� matricul�-lo na 2� Op��o, e assim sucessivamente. </p>
				</div> 
			</td>
		</tr>
		<tr>
			<th width="20%">1� Op��o:</th>
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
				<th>2� Op��o:</th>
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
				<th>3� Op��o:</th>
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
				<th>4� Op��o:</th>
				<td>
				</td>
				<td>
					<h:outputText id="labelEnfaseQuaternaria" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[3].modulo.descricao}"/>
				</td>
			</tr>
		</a4j:region>
		
		<tr>
			<tr>
				<td colspan="3" class="subFormulario"> 1� Op��o de �nfase: <h:outputText id="labelEnfPref" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[0].modulo.descricao}"/> </td>
			</tr>
			<td colspan="3">
				<div class="descricaoOperacao">
					<p>
						Informe suas prefer�ncias de dia e hor�rio no qual deseja cursar o M�dulo Avan�ado dentre as op��es dispon�veis para sua 1� Op��o de �nfase.  
					</p>
					<ul>
						<li>As op��es de hor�rio devem ser selecionadas por ordem de prioridade.</li>
						<li>Voc� deve adicionar <strong>TODAS</strong> as op��es de hor�rio para concluir a solicita��o de matr�cula.</li>
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
				Op��o de hor�rio:
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
									<f:facet name="header">Hor�rio</f:facet>
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
				<td colspan="3" class="subFormulario"> 2� Op��o de �nfase: <h:outputText id="labelEnfSec" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[1].modulo.descricao}"/> </td>
			</tr>
			<tr>
				<td colspan="3">
					<div class="descricaoOperacao">
						<p>
							Caso voc� n�o se classifique para cursar o M�dulo Avan�ado na 1� Op��o de �nfase escolhida, o sistema automaticamente tentar� coloc�-lo
							na 2� Op��o de �nfase. Dessa forma, informe suas prefer�ncias de dia e hor�rio dentre as op��es dispon�veis para sua 2� Op��o de �nfase.
						</p>
						<ul>
							<li>As op��es de hor�rio devem ser selecionadas por ordem de prioridade.</li>
							<li>Voc� deve adicionar <strong>TODAS</strong> as op��es de hor�rio para concluir a solicita��o de matr�cula.</li>
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
					Op��o de hor�rio:
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
										<f:facet name="header">Hor�rio</f:facet>
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
				<td colspan="3" class="subFormulario"> 3� Op��o de �nfase: <h:outputText id="labelEnfTer" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[2].modulo.descricao}"/> </td>
			</tr>
			<tr>
				<td colspan="3">
					<div class="descricaoOperacao">
						<p>
							Caso voc� n�o se classifique para cursar o M�dulo Avan�ado na 1� e 2� Op��es de �nfases escolhidas, o sistema automaticamente tentar� coloc�-lo
							na 3� Op��o de �nfase. Dessa forma, informe suas prefer�ncias de dia e hor�rio dentre as op��es dispon�veis para sua 3� Op��o de �nfase.
						</p>
						<ul>
							<li>As op��es de hor�rio devem ser selecionadas por ordem de prioridade.</li>
							<li>Voc� deve adicionar <strong>TODAS</strong> as op��es de hor�rio para concluir a solicita��o de matr�cula.</li>
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
					Op��o de hor�rio:
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
										<f:facet name="header">Hor�rio</f:facet>
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
				<td colspan="3" class="subFormulario"> 4� Op��o de �nfase: <h:outputText id="labelEnfQua" value="#{matriculaModuloAvancadoMBean.obj.opcoesModulo[3].modulo.descricao}"/> </td>
			</tr>
			<tr>
				<td colspan="3">
					<div class="descricaoOperacao">
						<p>
							Caso voc� n�o se classifique para cursar o M�dulo Avan�ado na 1�, 2� ou 3� Op��es de �nfases escolhidas, o sistema automaticamente tentar� coloc�-lo
							na 4� Op��o de �nfase. Dessa forma, informe suas prefer�ncias de dia e hor�rio dentre as op��es dispon�veis para sua 4� Op��o de �nfase.
						</p>
						<ul>
							<li>As op��es de hor�rio devem ser selecionadas por ordem de prioridade.</li>
							<li>Voc� deve adicionar <strong>TODAS</strong> as op��es de hor�rio para concluir a solicita��o de matr�cula.</li>
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
					Op��o de hor�rio:
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
										<f:facet name="header">Hor�rio</f:facet>
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
					<h:commandButton value="Confirmar Solicita��o de Matr�cula" action="#{matriculaModuloAvancadoMBean.cadastrar}" id="cadastrar" />
					<h:commandButton value="<< Selecionar Outro Discente" action="#{matriculaModuloAvancadoMBean.iniciarCoordenacao}" id="selecionarOutroDiscente" rendered="#{not matriculaModuloAvancadoMBean.discenteLogado}" />
					<h:commandButton value="Cancelar" action="#{matriculaModuloAvancadoMBean.cancelar}" onclick="#{confirm}" immediate="true" id="cancelar" />
				</td>
			</tr>
		</tfoot>
	
	</table>

	</h:form>

	<br />
	<div class="obrigatorio">Campos de preenchimento obrigat�rio.</div>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>