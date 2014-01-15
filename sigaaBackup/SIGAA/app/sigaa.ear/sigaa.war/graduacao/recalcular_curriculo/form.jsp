<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<%@ include file="/WEB-INF/jsp/include/errosAjax.jsp"%>


	<a4j:keepAlive beanName="recalculosMBean" />

	<h2><ufrn:subSistema/> > Rec�lculo de Curr�culo</h2>

	<div id="ajuda" class="descricaoOperacao">
		<p>
			<strong>Rec�lculo de Curr�culo</strong>
		</p>
		<br/>
		<p>
			Use a busca para selecionar o(s) curr�culo(s) que deseja recalcular os totais.
		</p>
		<p>
			Se a op��o <i>Recalcular Discentes Associados</i> for marcada, isto ir� for�ar que os discentes com v�nculo ativo tamb�m sejam inclu�dos no rec�lculo.
		</p>
	</div>

	<h:form id="form">
		<table class="formulario" width="90%">
		<caption>Lista de Curr�culos</caption>
		<tbody>
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">
						<tbody>
							<tr>
								<th class="required" width="20%">Curso: </th>
								<td>
									<h:selectOneMenu id="curso" value="#{recalculosMBean.curso.id}" style="width: 90%">
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
										<f:selectItems value="#{curso.allCursoGraduacaoCombo}" />
										<a4j:support reRender="matriz" event="onchange" />
									</h:selectOneMenu>
								</td>
							</tr>
							<tr>
								<th class="required">Matriz: </th>
								<td>
									<h:selectOneMenu id="matriz" value="#{recalculosMBean.matriz.id}" style="width: 90%">
										<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
										<f:selectItems value="#{recalculosMBean.allMatrizCombo}" />
									</h:selectOneMenu>	
									&nbsp;&nbsp;&nbsp;	
									<a4j:status>
										<f:facet name="start">
											<h:graphicImage value="/img/indicator.gif"/>
										</f:facet>
									</a4j:status>
								</td>
							</tr>
							<tr>
								<th>
									<h:selectBooleanCheckbox id="ckeckRecalcularDiscente" value="#{recalculosMBean.ckeckRecalcularDiscente}" />
								</th>
								<td>
									<h:outputLabel value="Recalcular discentes associados" for="ckeckRecalcularDiscente" />
								</td>
							</tr>
						</tbody>
						<tfoot>
							<tr>
								<td colspan="2" align="center">
									<a4j:commandButton id="buscar" value="Buscar" actionListener="#{recalculosMBean.buscar}" reRender="curriculosEncontradosPanel, curriculosAdicionadosPanel" />				
								</td>
							</tr>
						</tfoot>									
					</table>
				</td>						
			</tr>
			
			<tr>
				<td>
					<table width="100%">
						<tr>
							<td valign="top" width="50%">
								<a4j:outputPanel id="curriculosEncontradosPanel">
									<t:div styleClass="infoAltRem" style="margin-top:15px" rendered="#{recalculosMBean.modelCurriculosEncontrados.rowCount > 0 || recalculosMBean.modelCurriculosAdicionados.rowCount > 0 }">
										<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />: Adicionar Curr�culo						
									</t:div>					
									<rich:dataTable id="curriculosEncontrados" value="#{recalculosMBean.modelCurriculosEncontrados}" var="c"
											rowKeyVar="row" width="98%" styleClass="listagem" headerClass="linhaCinza" rowClasses="linhaPar, linhaImpar" rendered="#{recalculosMBean.modelCurriculosEncontrados.rowCount > 0 || recalculosMBean.modelCurriculosAdicionados.rowCount > 0 }">
											
										<f:facet name="caption">
											<h:outputText value="Curr�culos Encontrados (#{recalculosMBean.modelCurriculosEncontrados.rowCount})" />
										</f:facet>							
				
										<rich:column>
											<f:facet name="header">
												<h:outputText value="Descri��o" />
											</f:facet>
											<h:outputText id="descricao" value="#{c.descricaoCursoCurriculo}"/>
											<h:outputText escape="false"  value="<br /><i>#{c.qtdAlunos} Alunos Ativos</i>"/>
										</rich:column>
												
										<rich:column width="5%" style="white-space:nowrap;">
											<a4j:commandButton actionListener="#{recalculosMBean.adicionarCurriculo}" image="/img/adicionar.gif" reRender="curriculosAdicionadosPanel, curriculosEncontradosPanel" />
										</rich:column>   
				
									</rich:dataTable>
								</a4j:outputPanel>
							</td>
							<td width="50%" valign="top">
								<a4j:outputPanel id="curriculosAdicionadosPanel">
									<t:div styleClass="infoAltRem" style="margin-top:15px" rendered="#{recalculosMBean.modelCurriculosEncontrados.rowCount > 0 || recalculosMBean.modelCurriculosAdicionados.rowCount > 0 }">
										<h:graphicImage value="/img/biblioteca/estornar.gif" style="overflow: visible;" />: Remover Curr�culo						
									</t:div>					
								
									<rich:dataTable id="curriculosAdicionados" value="#{recalculosMBean.modelCurriculosAdicionados}" var="c" 
											rowKeyVar="row" width="98%" styleClass="listagem" headerClass="linhaCinza" rowClasses="linhaPar, linhaImpar" rendered="#{recalculosMBean.modelCurriculosEncontrados.rowCount > 0 || recalculosMBean.modelCurriculosAdicionados.rowCount > 0 }">
											
										<f:facet name="caption">
											<h:outputText id="valorCurriculosAdicionados" value="Curr�culos Adicionados (#{recalculosMBean.modelCurriculosAdicionados.rowCount > 0 ? recalculosMBean.modelCurriculosAdicionados.rowCount : '0'})" />
										</f:facet>							
				
										<rich:column>
											<f:facet name="header">
												<h:outputText value="Descri��o" />
											</f:facet>
											<h:outputText id="descricao" value="#{c.descricaoCursoCurriculo}"/>
											<h:outputText escape="false"  value="<br /><i>#{c.qtdAlunos} Alunos Ativos</i>"/>
										</rich:column>
												
										<rich:column width="5%" style="white-space:nowrap;">
											<a4j:commandButton id="btnRemover" actionListener="#{recalculosMBean.removerCurriculo}" image="/img/biblioteca/estornar.gif" reRender="curriculosAdicionadosPanel, curriculosEncontradosPanel" />
										</rich:column>   
				
									</rich:dataTable>
								</a4j:outputPanel>
							</td>										
						</tr>
					</table>
				</td>
			</tr>			
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2" align="center">
					<h:commandButton value="Calcular" action="#{recalculosMBean.recalcularCurriculosDAE}" id="recalcular" />				
					<h:commandButton value="Cancelar" action="#{recalculosMBean.cancelar}" immediate="true" onclick="#{confirm}" id="cancelar" />
				</td>
			</tr>
		</tfoot>		
		</table>
	</h:form>

	<br/>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
	</center>

</f:view>
<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>