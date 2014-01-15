<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<%@ taglib uri="/tags/a4j" prefix="a4j" %>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h2><ufrn:subSistema /> > Seleção do Projeto de Monitoria</h2>
	
	<div class="descricaoOperacao">
		<font color="red">Atenção:</font><br/>
		Somente projetos de monitoria ativos podem cadastrar e alterar uma prova seletiva.		
	</div>
	
	<h:form>
	

		<h:outputText value="#{provaSelecao.create}"/>		
		<h:inputHidden value="#{provaSelecao.obj.id}"/>
		<h:inputHidden value="#{provaSelecao.obj.projetoEnsino.id}" />

		<table class="formulario" width="100%">
		<caption class="listagem"> Seleção de Monitoria </caption>

		   	<tr>
			   	<th width="30%" class="rotulo">Ano Projeto:</th>
			   	<td>${provaSelecao.obj.projetoEnsino.ano}</td>
		   	</tr>

		   	<tr>
			   	<th class="rotulo">Título do Projeto:</th>
			   	<td>${provaSelecao.obj.projetoEnsino.titulo}</td>
		   	</tr>

		   	<tr>
			   	<th class="rotulo">Situação do Projeto:</th>
			   	<td>${provaSelecao.obj.projetoEnsino.situacaoProjeto.descricao}</td>
		   	</tr>

		   	<tr>
			   	<th class="rotulo">Situação da Prova Seletiva:</th>
			   	<td>
			   	     <ufrn:checkNotRole papeis="<%= new int[] { SigaaPapeis.GESTOR_MONITORIA }  %>">
			   	         ${provaSelecao.obj.situacaoProva.descricao}
			   	     </ufrn:checkNotRole>
			   	     <ufrn:checkRole papeis="<%= new int[] { SigaaPapeis.GESTOR_MONITORIA }  %>">
			   	            <h:selectOneMenu id="situacaoProva" value="#{provaSelecao.obj.situacaoProva.id}">
			   	                <f:selectItem itemValue="0" itemLabel="-- SELECIONE --"  />
                                <f:selectItems value="#{provaSelecao.allSituacaoProvaCombo}"/>                                
                            </h:selectOneMenu>
			   	     </ufrn:checkRole>
			   	</td>
		   	</tr>

		   	<tr>
			   	<th class="rotulo">Vagas Concedidas:</th>
			   	<td>
			   		${provaSelecao.obj.projetoEnsino.bolsasConcedidas} Remuneradas e  
			   		${provaSelecao.obj.projetoEnsino.bolsasNaoRemuneradas} Não Remuneradas.
			   	</td>
		   	</tr>

		   	<tr>
			   	<th class="rotulo">Vagas Disponíveis para Reserva:</th>
			   	<td>
			   		${provaSelecao.obj.totalBolsasRemuneradasDisponiveisReserva} Remuneradas e  
			   		${provaSelecao.obj.totalBolsasNaoRemuneradasDisponiveisReserva} Não Remuneradas.
			   	</td>
		   	</tr>

			<tr>
				<th class="required">Título da Prova: </th>
				<td>
					<h:inputText id="titulo_prova" value="#{provaSelecao.obj.titulo}" style="width: 90%" readonly="#{provaSelecao.readOnly}"/>
					<ufrn:help img="/img/ajuda.gif">Identificador da prova seletiva. Ex.: Seleção para Monitoria das Disciplinas de Cálculo I e II</ufrn:help>
				</td>
			</tr>

			<tr>
				<th class="required">Data da Prova:</th>
				<td>
					<t:inputCalendar
						renderAsPopup="true"
						renderPopupButtonAsImage="true"
						value="#{provaSelecao.obj.dataProva}" 
						popupDateFormat="dd/MM/yyyy"
						popupTodayString="Hoje é"
						size="10"
						maxlength="10"
						onkeypress="return(formataData(this,event))"
						readonly="#{provaSelecao.readOnly}" id="data_prova">
						<f:converter converterId="convertData"/>
					</t:inputCalendar>					
				</td>
			</tr>

			<tr>
				<th class="required">Inscrições Até:</th>
				<td>
				
				<t:inputCalendar
						renderAsPopup="true"
						renderPopupButtonAsImage="true"
						value="#{provaSelecao.obj.dataLimiteIncricao}" 
						popupDateFormat="dd/MM/yyyy"
						popupTodayString="Hoje é"
						size="10"
						maxlength="10"
						onkeypress="return(formataData(this,event))"
						readonly="#{provaSelecao.readOnly}" id="data_max_inscricoes" >
						<f:converter converterId="convertData"/>
					</t:inputCalendar>
					<ufrn:help img="/img/ajuda.gif">Data limite para inscrição dos alunos na prova pelo SIGAA.</ufrn:help>
				</td>
			</tr>

	
			<tr>
				<th>Complemento: </th>
				<td>
					 Informações complementares (Local de Realização, Horário das provas, etc..)<br>
					<h:inputTextarea id="info_complementares" value="#{provaSelecao.obj.informacaoSelecao}" rows="5" style="width: 98%" readonly="#{provaSelecao.readOnly}"/>
				</td>
			</tr>

			<tr>
				<td colspan="2"> <br/></td>
			</tr>

			<tr>
				<td colspan="2" class="subFormulario">
					Perfil da Vaga: Selecione quais componentes curriculares serão considerados na seleção de monitores
					<span class="required"></span>
				</td>
			</tr>

			<tr>
				<th>Vagas Reservadas: </th>
				<td>
					<b>Remuneradas:</b>
					<h:selectOneMenu
						value="#{provaSelecao.obj.vagasRemuneradas}" id="reservaRemunerada"
						disabled="#{provaSelecao.readOnly}">
						<f:selectItems value="#{provaSelecao.bolsasRemuneradasDisponiveisReservaCombo}" />
					</h:selectOneMenu>
					<ufrn:help img="/img/ajuda.gif">Número de bolsas remuneradas do projeto reservadas para esta prova seletiva.</ufrn:help>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;					
					<b>Não remuneradas:</b>
					<h:selectOneMenu
						value="#{provaSelecao.obj.vagasNaoRemuneradas}" id="reservaNaoRemunerada"
						disabled="#{provaSelecao.readOnly}">
						<f:selectItems value="#{provaSelecao.bolsasNaoRemuneradasDisponiveisReservaCombo}" />
					</h:selectOneMenu>
					<ufrn:help img="/img/ajuda.gif">Número de vagas para voluntários do projeto reservadas para esta prova seletiva.</ufrn:help>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<div class="infoAltRem" style="width: 100%">
						<img src="/shared/img/adicionar.gif"style="overflow: visible;"/>: Adicionar 
					    <img src="/shared/img/delete.gif" style="overflow: visible;"/>: Remover
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2">

		            <rich:simpleTogglePanel switchType="client" label="Lista de componentes do projeto." bodyClass="body2" 
		            id="componentesProjeto">						
								<t:dataTable id="dtCompProjeto" 
									value="#{provaSelecao.allComponentesCurriculares}" var="comp" align="center" width="100%" 
									styleClass="listagem" rowClasses="linhaPar, linhaImpar">
										<t:column>
											<h:outputText value="#{comp.disciplina.descricao}" />
										</t:column>
										
										<t:column styleClass="centerAlign" rendered="#{!provaSelecao.readOnly}">
											<a4j:commandLink action="#{provaSelecao.adicionarComponenteCurricular}" 	
												title="Adicionar" 
												reRender="componentesProva, componentesProjeto, mensagemComponente">
												<f:param value="#{comp.id}" name="idComponente"/>
												<h:graphicImage url="/img/adicionar.gif" />
											</a4j:commandLink>
										</t:column>
								</t:dataTable>				
					</rich:simpleTogglePanel>
					
					 <br/>
			            <a4j:status>
			                <f:facet name="start"><h:graphicImage  value="/img/indicator.gif"/></f:facet>			                			                				            
			            </a4j:status>
					 <rich:separator height="2" lineType="dashed"/>
					 
					 <h:panelGrid id="mensagemComponente">
					 	<h:outputText value="#{provaSelecao.mensagemAdicionarComponente}" style="color: red"/>
					 </h:panelGrid>
					 <br/>
					 
					<rich:simpleTogglePanel switchType="client" label="Lista de componentes vinculados à prova  (Selecione quais são obrigatórios para a seleção)" 
						bodyClass="body2" id="componentesProva">
					
								<h:outputText value="<center><font color='red'><i> Nenhum componente vinculado </i></font></center>" escape="false" rendered="#{empty provaSelecao.obj.componentesObrigatorios}"/>
									
								<t:dataTable id="dtComponentesProva" value="#{provaSelecao.obj.componentesObrigatorios}" var="compProva" 
										align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" 
										rendered="#{not empty provaSelecao.obj.componentesObrigatorios}">
					
					
										<t:column><h:selectBooleanCheckbox id="comp_obrigatorio" value="#{compProva.obrigatorio}" styleClass="noborder" rendered="#{!provaSelecao.readOnly && !compProva.componenteCurricularMonitoria.componenteNovo}"/></t:column>
										<t:column><h:outputText value="#{compProva.componenteCurricularMonitoria.disciplina.descricao}" /></t:column>			
					
										<t:column width="5%" styleClass="centerAlign" rendered="#{!provaSelecao.readOnly}">
											<a4j:commandLink action="#{provaSelecao.removeComponenteCurricular}"
												title="Remover" 
												reRender="componentesProva, componentesProjeto">
												<f:param value="#{compProva.id}" name="idProvaComponente"/>
												<f:param value="#{compProva.componenteCurricularMonitoria.id}" name="idComponente"/>
												<h:graphicImage url="/img/delete.gif" />
											</a4j:commandLink>
											
										</t:column>
					
								</t:dataTable>
				
					</rich:simpleTogglePanel>

			</td>
		</tr>

		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="#{provaSelecao.confirmButton}" action="#{provaSelecao.cadastrar}"/>
					<h:commandButton value="Cancelar" action="#{provaSelecao.cancelar}" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>

	</h:form>

	</table>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>