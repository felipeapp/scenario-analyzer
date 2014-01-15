<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2 class="title"><ufrn:subSistema /> > Cadastro do Formulário de Evolução</h2>
	
 <div class="infoAltRem">
    <h:graphicImage value="/img/adicionar.gif" 	style="overflow: visible;"/>: Adicionar			    	    
    <h:graphicImage value="/img/garbage.png" 	style="overflow: visible;"/>: Remover	
    <html:img page="/img/prodocente/cima.gif" style="overflow: visible;"/>: Mover Para Cima
    <html:img page="/img/prodocente/baixo.gif" style="overflow: visible;"/>: Mover Para Baixo  
</div>
	
 <h:form>
 	<h:outputText value="#{cadastroFormularioEvolucaoMBean.create}" />
		<table class="formulario" width="700px">
			<caption class="formulario">Formulário de Evolução</caption>
			
			<tr>
				<td colspan="2" align="left">
					<table class="subFormulario" align="left" width="100%">
						<caption class="subFormulario">Turma Infantil</caption>
						<tr>
							<th class="required">Turma:</th>
							<td>
								<h:selectOneMenu id="componenteCurricular" value="#{cadastroFormularioEvolucaoMBean.obj.nivelInfantil.id}">
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
									<f:selectItems value="#{cadastroFormularioEvolucaoMBean.componenteCurricular}"/>
								</h:selectOneMenu>
							</td>
						</tr>
					</table>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" align="left">
					<table class="subFormulario" align="left" width="100%">
						<caption class="subFormulario">Inserir Nova Área</caption>
						<tr>
							<th class="required">Descrição: </th>
							<td><h:inputText id="nomeBloco" size="70" maxlength="200" onkeyup="CAPS(this)" value="#{cadastroFormularioEvolucaoMBean.nomeBloco}"  /></td>
						</tr>
						<tr>
							<th>Forma de Avaliação:</th>
							<td>
								<h:selectOneMenu id="formaAvaliacaoBloco" value="#{cadastroFormularioEvolucaoMBean.formaAvaliacao}">
									<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
									<f:selectItem itemLabel="S: Sim N: Não AV: Às Vezes" itemValue="0"/>
									<f:selectItem itemLabel="SA: Sem Ajuda CA: Com Ajuda DIF: Teve Dificuldade" itemValue="1"/>
								</h:selectOneMenu>
							</td>
						</tr>
						<tr>
							<th>Rótulo: </th>
							<td><h:inputText id="rotuloBloco" size="5" maxlength="200" onkeyup="CAPS(this)" value="#{cadastroFormularioEvolucaoMBean.rotuloBloco}"  /></td>
							<td><h:commandButton id="addBloco" image="/img/adicionar.gif" styleClass="noborder" title="Adicionar Nova Área" actionListener="#{cadastroFormularioEvolucaoMBean.adicionarBloco}" /></td>
						</tr>
					</table>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" align="left">
					<table width="100%" cellspacing="0"><caption>Áreas:</caption>
						<c:if test="${empty cadastroFormularioEvolucaoMBean.blocos}">
							<tr><td colspan="2">Nenhuma Área Adicionada</td></tr>
						</c:if>
						<tr><td align="center">
								<t:dataTable align="left" width="100%" styleClass="listagem" rowClasses="linhaPar,linhaImpar"
								value="#{cadastroFormularioEvolucaoMBean.modelBlocos}" var="bloco" id="tabelaBlocos">
									<t:column>
										<b><h:outputText styleClass="subFormulario" value="#{bloco.descricao }" /></b>
										<f:verbatim> &nbsp; </f:verbatim>
										
										<f:verbatim> &nbsp; </f:verbatim>
										<h:commandButton id="remg" image="/img/garbage.png" title="Remover Área" actionListener="#{cadastroFormularioEvolucaoMBean.removerBloco}"  styleClass="noborder"/>
										<f:verbatim> <br> </f:verbatim>
										
										<table class="subFormulario" align="left" width="100%">
											<caption class="subFormulario">Inserir Nova Sub-Área</caption>
											<tr>
												<th class="required">Descrição: </th>
												<td><h:inputText id="nomeArea" size="70" maxlength="200" onkeyup="CAPS(this)" value="#{cadastroFormularioEvolucaoMBean.nomeArea}"  /></td>
											</tr>
											<tr>
												<th class="required">Forma de Avaliação:</th>
												<td>
													<h:selectOneMenu id="formaAvaliacaoArea" value="#{cadastroFormularioEvolucaoMBean.formaAvaliacao}">
														<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
														<f:selectItem itemLabel="S: Sim N: Não AV: Às Vezes" itemValue="0"/>
														<f:selectItem itemLabel="SA: Sem Ajuda CA: Com Ajuda DIF: Teve Dificuldade" itemValue="1"/>
													</h:selectOneMenu>
												</td>
											</tr>
											<tr>
												<th>Rótulo: </th>
												<td><h:inputText id="rotuloArea" size="5" maxlength="200" onkeyup="CAPS(this)" value="#{cadastroFormularioEvolucaoMBean.rotuloArea}"  /></td>
												<td><h:commandButton id="addArea" image="/img/adicionar.gif" styleClass="noborder" title="Adicionar Nova Sub-Área"	actionListener="#{cadastroFormularioEvolucaoMBean.adicionarArea}" /></td>
											</tr>
											<tr>
												<td colspan="3">
													<t:dataTable align="left" width="100%" styleClass="listagem" rowClasses="linhaPar,linhaImpar"
													value="#{cadastroFormularioEvolucaoMBean.modelArea}" var="areas" id="tabelaAreas"> 
														<t:column>
															<f:facet name="header">
																<h:outputText value="#" />
															</f:facet>
															<h:outputText value="#{areas.ordem}" />
														</t:column>
														<t:column >
															<f:facet name="header">
																<h:outputText value="Descrição" />
															</f:facet>
															<h:outputText value="#{areas.descricao}"/>
														</t:column>
														<t:column >
															<f:facet name="header">
																<h:outputText value="Forma de Avaliação" />
															</f:facet>
															<h:outputText value="#{areas.legendaFormaAvaliacao}"/>
														</t:column>
														<t:column>
															<a4j:commandButton id="cimaArea" image="/img/prodocente/cima.gif" 
																title="Mover para cima" actionListener="#{cadastroFormularioEvolucaoMBean.moveAreaCima}" 
																reRender="tabelaBlocos" styleClass="noborder"/>
															<a4j:commandButton id="baixoArea" image="/img/prodocente/baixo.gif" title="Mover para baixo" 
																actionListener="#{cadastroFormularioEvolucaoMBean.moveAreaBaixo}" 
																reRender="tabelaBlocos"  styleClass="noborder"/>
															<a4j:commandButton id="removeArea" image="/img/garbage.png" title="Remover Sub-Área" 
																actionListener="#{cadastroFormularioEvolucaoMBean.removerArea}" 
																reRender="tabelaBlocos" styleClass="noborder"/>
														</t:column>
													</t:dataTable>
												</td>
											</tr>
										</table>
									</t:column>
								</t:dataTable>
							</td></tr>
					</table>
			</td></tr>
			
			
			
			<tr>
				<td colspan="2" align="left">
					<table class="subFormulario" align="left" width="100%">
						<caption>Inserir Novo Conteúdo</caption>
						<c:if test="${not empty cadastroFormularioEvolucaoMBean.blocos}">
							<tr>
								<th class="required">Área: </th>
								<td>
									<h:selectOneMenu id="bloco" value="#{cadastroFormularioEvolucaoMBean.indexBloco}">
										<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
										<f:selectItems value="#{cadastroFormularioEvolucaoMBean.blocosCombo}"/>
										<a4j:support event="onblur" reRender="areas" action="#{cadastroFormularioEvolucaoMBean.blocoSelecionado}"/>
									</h:selectOneMenu>
								</td>
							</tr>
							<tr>
								<th class="required">Sub-Área: </th>
								<td>
									<h:selectOneMenu id="areas" value="#{cadastroFormularioEvolucaoMBean.indexArea}">
										<f:selectItem itemLabel="-- SELECIONE --" itemValue="-1"/>
										<f:selectItems value="#{cadastroFormularioEvolucaoMBean.areasCombo}"/>
									</h:selectOneMenu>
								</td>
							</tr>
							<tr>
								<th class="required">Descrição: </th>
								<td><h:inputText id="nomeConteudo" size="70" maxlength="200" onkeyup="CAPS(this)" value="#{cadastroFormularioEvolucaoMBean.nomeConteudo}"  /></td>
							</tr>
							<tr>
								<th>Rótulo: </th>
								<td><h:inputText id="rotuloConteudo" size="5" maxlength="200" onkeyup="CAPS(this)" value="#{cadastroFormularioEvolucaoMBean.rotuloConteudo}"  /></td>
								<td><h:commandButton id="addConteudo" image="/img/adicionar.gif" styleClass="noborder" title="Adicionar Novo Conteúdo" actionListener="#{cadastroFormularioEvolucaoMBean.adicionarConteudo}" /></td>
							</tr>
						</c:if>
					</table>
				</td>
			</tr>
			
			<tr>
				<td colspan="2" align="left">
					<table width="100%" cellspacing="0"><caption>Conteúdos:</caption>
					<c:if test="${empty cadastroFormularioEvolucaoMBean.conteudos}">
						<tr><td colspan="2">Nenhum Conteúdo Adicionado</td></tr>
					</c:if>
					<tr><td>
							<t:dataTable align="center" width="100%" styleClass="listagem" rowClasses="linhaPar,linhaImpar"
							value="#{cadastroFormularioEvolucaoMBean.modelConteudos}" var="conteudo" id="tabelaConteudos">
							<t:column>
								<b><h:outputText styleClass="subFormulario" value="#{conteudo.descricao }" /></b>
								<f:verbatim> > </f:verbatim>
								<b><h:outputText styleClass="subFormulario" value="#{conteudo.area.descricao }" /></b>
								
								<f:verbatim> &nbsp; </f:verbatim>
								<h:commandButton id="remCont" image="/img/garbage.png" title="Remover Conteúdo" actionListener="#{cadastroFormularioEvolucaoMBean.removerConteudo}"  styleClass="noborder"/>
								<f:verbatim> <br> </f:verbatim>
								
								<table class="subFormulario" align="left" width="100%">
									<caption class="subFormulario">Inserir Novo Objetivo</caption>
									<tr>
										<th class="required">Descrição: </th>
										<td><h:inputText id="nomeObjetivo" size="70" maxlength="200" onkeyup="CAPS(this)" value="#{cadastroFormularioEvolucaoMBean.nomeObjetivo}"  /></td>
										<td><h:commandButton id="addObjetivo" image="/img/adicionar.gif" styleClass="noborder" title="Adicionar Novo Objetivo"	actionListener="#{cadastroFormularioEvolucaoMBean.adicionarObjetivo}" /></td>
									</tr>
									<tr>
										<td colspan="3">
											<t:dataTable align="left" width="100%" styleClass="listagem" rowClasses="linhaPar,linhaImpar"
											value="#{cadastroFormularioEvolucaoMBean.modelObjetivo}" var="objetivo" id="tabelaObjetivos"> 
												<t:column>
													<f:facet name="header">
														<h:outputText value="#" />
													</f:facet>
													<h:outputText value="#{objetivo.ordem}" />
												</t:column>
												<t:column >
													<f:facet name="header">
														<h:outputText value="Descrição" />
													</f:facet>
													<h:outputText value="#{objetivo.descricao}"/>
												</t:column>
												<t:column style="right">
													<a4j:commandButton id="cimaObjetivo" image="/img/prodocente/cima.gif" 
														title="Mover para cima" actionListener="#{cadastroFormularioEvolucaoMBean.moveObjetivoCima}" 
														reRender="tabelaConteudos" styleClass="noborder"/>
													<a4j:commandButton id="baixoObjetivo" image="/img/prodocente/baixo.gif" title="Mover para baixo" 
														actionListener="#{cadastroFormularioEvolucaoMBean.moveObjetivoBaixo}" 
														reRender="tabelaConteudos"  styleClass="noborder"/>
													<a4j:commandButton id="removeObjetivo" image="/img/garbage.png" title="Remover Objetivo" 
														actionListener="#{cadastroFormularioEvolucaoMBean.removerObjetivo}" 
														reRender="tabelaConteudos" styleClass="noborder"/>
												</t:column>
											</t:dataTable>
										</td>
									</tr>
								</table>
								
								
							</t:column>
							</t:dataTable>
						</td></tr>
				</table>
			</td></tr>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden value="#{cadastroFormularioEvolucaoMBean.confirmButton}" />
						<h:commandButton value="#{cadastroFormularioEvolucaoMBean.confirmButton}" action="#{cadastroFormularioEvolucaoMBean.cadastrar}" />
					 	<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{cadastroFormularioEvolucaoMBean.cancelar}" />
					 </td>

				</tr>
			</tfoot>
		</table>

</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
